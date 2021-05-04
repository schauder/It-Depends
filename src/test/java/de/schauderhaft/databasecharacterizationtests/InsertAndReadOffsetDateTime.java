package de.schauderhaft.databasecharacterizationtests;

import de.schauderhaft.databasecharacterizationtests.fixture.DescriptiveAssertion;
import de.schauderhaft.databasecharacterizationtests.fixture.FailureAssertion;
import de.schauderhaft.databasecharacterizationtests.fixture.Fixture;
import de.schauderhaft.databasecharacterizationtests.support.DbTest;
import de.schauderhaft.databasecharacterizationtests.support.TableName;
import org.h2.api.TimestampWithTimeZone;
import org.junit.jupiter.api.DisplayName;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.util.List;

import static de.schauderhaft.databasecharacterizationtests.fixture.ValueChange.*;
import static java.util.Arrays.*;
import static java.util.Collections.*;
import static org.assertj.core.api.Assertions.*;

public class InsertAndReadOffsetDateTime {

	@DbTest
	@DisplayName("Write a `OffsetDateTime` \n" +
			"and read it back using `Resultset.getObject(int)`")
	void getObject(OffsetDateTimeFixture fixture, TableName table) {

		NamedParameterJdbcTemplate jdbc = fixture.template();
		jdbc.getJdbcOperations()
				.execute(String.format("CREATE TABLE %s (VALUE %s)", table, fixture.columnType));

		OffsetDateTime value = OffsetDateTime.of(2005, 5, 5, 5, 5, 5, 123456789, ZoneOffset.ofHours(5));


		MapSqlParameterSource parameters = new MapSqlParameterSource("value", value);
//		parameters.registerSqlType("value", Types.TIMESTAMP_WITH_TIMEZONE);
		jdbc.update(String.format("INSERT INTO %s VALUES (:value)", table), parameters);

		Object reloaded = jdbc.queryForObject("SELECT VALUE FROM " + table, emptyMap(), (rs, i) -> rs.getObject(1));

		if (fixture.fails())
			fixture.failureAssertion.assertFailure(value, reloaded);
		else
			assertThat(reloaded).isEqualTo(value);
	}

	static List<OffsetDateTimeFixture> getObject() {
		return asList(
				f("h2", new DescriptiveAssertion<>("H2 returns a non standard type", (__, v) -> assertThat(v).isInstanceOf(TimestampWithTimeZone.class))),
				f("hsql"),
				f("postgres", changesValue(
						exp -> Timestamp.from(
								exp.withNano(((int) Math.round(exp.getNano() / 1_000.0)) * 1_000)
										.toInstant())
				)),
				f("mysql",
						changesValue(
								exp -> Timestamp.from(
										exp.withNano(((int) Math.round(exp.getNano() / 1_000.0)) * 1_000)
												.toInstant()
								)
						),
						"TIMESTAMP(6)")
		);
	}


	@DbTest
	@DisplayName("Write a `OffsetDateTime` \n" +
			"and read it back using `Resultset.getObject(int, OffsetDateTime.class)`")
	void getObjectOffsetDateTime(OffsetDateTimeFixture fixture, TableName table) {

		NamedParameterJdbcTemplate jdbc = fixture.template();
		jdbc.getJdbcOperations()
				.execute(String.format("CREATE TABLE %s (VALUE %s)", table, fixture.columnType));

		OffsetDateTime value = OffsetDateTime.of(2005, 5, 5, 5, 5, 5, 123456789, ZoneOffset.ofHours(5));


		MapSqlParameterSource parameters = new MapSqlParameterSource("value", value);
//		parameters.registerSqlType("value", Types.TIMESTAMP_WITH_TIMEZONE);
		jdbc.update(String.format("INSERT INTO %s VALUES (:value)", table), parameters);

		Object reloaded = jdbc.queryForObject("SELECT VALUE FROM " + table, emptyMap(), (rs, i) -> rs.getObject(1, OffsetDateTime.class));

		if (fixture.fails()) {
			fixture.failureAssertion.assertFailure(value, reloaded);
		} else {
			assertThat(reloaded).isEqualTo(value);
		}
	}

	static List<OffsetDateTimeFixture> getObjectOffsetDateTime() {

		return asList(
				f("h2"),
				f("hsql"),
				f("postgres", changesValue(
						exp -> OffsetDateTime.of(
								exp.withNano(((int) Math.round(exp.getNano() / 1_000.0)) * 1_000)
										.toLocalDateTime().withHour(exp.getHour() - exp.getOffset().get(ChronoField.OFFSET_SECONDS) / 3600),
								ZoneOffset.UTC
						))
				),
				f("mysql",
						changesValue(
								exp -> exp.withOffsetSameInstant(OffsetDateTime.now().getOffset())
										.withNano(((int) Math.round(exp.getNano() / 1_000.0)) * 1_000)
						),
						"TIMESTAMP(6)")
		);
	}

	static OffsetDateTimeFixture f(String database) {
		return new OffsetDateTimeFixture(database, null, "TIMESTAMP(9) WITH TIME ZONE");
	}

	static OffsetDateTimeFixture f(String database, String columnType) {
		return new OffsetDateTimeFixture(database, null, columnType);
	}

	static OffsetDateTimeFixture f(String database, FailureAssertion<OffsetDateTime> failureAssertion) {
		return new OffsetDateTimeFixture(database, failureAssertion, "TIMESTAMP(9) WITH TIME ZONE");
	}

	static OffsetDateTimeFixture f(String database, FailureAssertion<OffsetDateTime> failureAssertion, String columnType) {
		return new OffsetDateTimeFixture(database, failureAssertion, columnType);
	}

	private static class OffsetDateTimeFixture extends Fixture<OffsetDateTime> {
		final String columnType;

		private OffsetDateTimeFixture(String database, FailureAssertion<OffsetDateTime> failureAssertion, String columnType) {
			super(database, failureAssertion);
			this.columnType = columnType;
		}
	}
}
