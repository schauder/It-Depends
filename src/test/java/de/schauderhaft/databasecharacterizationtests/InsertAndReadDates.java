package de.schauderhaft.databasecharacterizationtests;

import de.schauderhaft.databasecharacterizationtests.datasource.DataSources;
import org.h2.api.TimestampWithTimeZone;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.Timestamp;
import java.sql.Types;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static de.schauderhaft.databasecharacterizationtests.InsertAndReadDates.GetObjectFixture.*;
import static java.util.Arrays.*;
import static java.util.Collections.*;
import static org.assertj.core.api.Assertions.*;

class InsertAndReadDates {

	@ParameterizedTest
	@MethodSource("getObjectSource")
	void getObject(GetObjectFixture fixture) {

		NamedParameterJdbcTemplate jdbc = fixture.template();
		jdbc.getJdbcOperations()
				.execute("CREATE TABLE DUMMY1 (VALUE TIMESTAMP(9) WITH TIME ZONE)");

		OffsetDateTime value = OffsetDateTime.of(2005, 5, 5, 5, 5, 5, 123456789, ZoneOffset.ofHours(5));


		MapSqlParameterSource parameters = new MapSqlParameterSource("value", value);
		parameters.registerSqlType("value", Types.TIMESTAMP_WITH_TIMEZONE);
		jdbc.update("INSERT INTO DUMMY1 VALUES (:value)", parameters);

		Object reloaded = jdbc.queryForObject("SELECT VALUE FROM DUMMY1", emptyMap(), (rs, i) -> rs.getObject(1));

		if (fixture.fails())
			fixture.failureAssertion.accept(value, reloaded);
		else
			assertThat(reloaded).isEqualTo(value);
	}

	static List<GetObjectFixture> getObjectSource() {
		return asList(
				f("h2", "H2 returns a non standard type", (__,v) -> assertThat(v).isInstanceOf(TimestampWithTimeZone.class)),
				f("hsql"),
				f("postgres", "Postgres returns a Timestamp, looses precision on the way and the timezone (in a weird way)",
						(exp, v) -> assertThat(v).isEqualTo(Timestamp.from(
						exp
						.withNano(((int)Math.round(exp.getNano()/ 1_000.0))*1_000) // only millisecond precision
						.toInstant())
				)));
	}

	static class GetObjectFixture {

		final String database;
		final String comment;
		final BiConsumer<OffsetDateTime, Object> failureAssertion;

		static GetObjectFixture f(String database) {
			return new GetObjectFixture(database);
		}

		static GetObjectFixture f(String database, String comment, BiConsumer<OffsetDateTime, Object> failureAssertion) {
			return new GetObjectFixture(database, comment, failureAssertion);
		}

		public GetObjectFixture(String database) {
			this.database = database;
			this.comment = null;
			this.failureAssertion = null;
		}

		public GetObjectFixture(String database, String comment, BiConsumer<OffsetDateTime, Object> failureAssertion) {
			this.database = database;
			this.comment = comment;
			this.failureAssertion = failureAssertion;
		}

		private NamedParameterJdbcTemplate template() {
			return new NamedParameterJdbcTemplate(DataSources.get(database));
		}

		public boolean fails() {
			return failureAssertion != null;
		}
	}

	@ParameterizedTest
	@MethodSource("getObjectOffsetDateTimeSource")
	void getObjectOffsetDateTime(String dataBaseName) {

		NamedParameterJdbcTemplate jdbc = new NamedParameterJdbcTemplate(DataSources.get(dataBaseName));
		jdbc.getJdbcOperations()
				.execute("CREATE TABLE DUMMY2 (VALUE TIMESTAMP(9) WITH TIME ZONE)");

		OffsetDateTime value = OffsetDateTime.of(2005, 5, 5, 5, 5, 5, 123456789, ZoneOffset.ofHours(5));


		MapSqlParameterSource parameters = new MapSqlParameterSource("value", value);
		parameters.registerSqlType("value", Types.TIMESTAMP_WITH_TIMEZONE);
		jdbc.update("INSERT INTO DUMMY2 VALUES (:value)", parameters);

		Object reloaded = jdbc.queryForObject("SELECT VALUE FROM DUMMY2", emptyMap(), (rs, i) -> rs.getObject(1, OffsetDateTime.class));

		assertThat(reloaded).isEqualTo(value);
	}

	static List<String> getObjectOffsetDateTimeSource() {
		return asList("h2", "hsql", "postgres");
	}


}
