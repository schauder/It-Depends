package de.schauderhaft.databasecharacterizationtests;

import de.schauderhaft.databasecharacterizationtests.fixture.DescriptiveAssertion;
import de.schauderhaft.databasecharacterizationtests.fixture.Fixture;
import org.h2.api.TimestampWithTimeZone;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.Timestamp;
import java.sql.Types;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.util.List;

import static de.schauderhaft.databasecharacterizationtests.fixture.Fixture.*;
import static de.schauderhaft.databasecharacterizationtests.fixture.ValueChange.*;
import static java.util.Arrays.*;
import static java.util.Collections.*;
import static org.assertj.core.api.Assertions.*;

class InsertAndReadDates {

	@ParameterizedTest
	@MethodSource
	void getObject(Fixture<OffsetDateTime> fixture) {

		NamedParameterJdbcTemplate jdbc = fixture.template();
		jdbc.getJdbcOperations()
				.execute("CREATE TABLE DUMMY1 (VALUE TIMESTAMP(9) WITH TIME ZONE)");

		OffsetDateTime value = OffsetDateTime.of(2005, 5, 5, 5, 5, 5, 123456789, ZoneOffset.ofHours(5));


		MapSqlParameterSource parameters = new MapSqlParameterSource("value", value);
		parameters.registerSqlType("value", Types.TIMESTAMP_WITH_TIMEZONE);
		jdbc.update("INSERT INTO DUMMY1 VALUES (:value)", parameters);

		Object reloaded = jdbc.queryForObject("SELECT VALUE FROM DUMMY1", emptyMap(), (rs, i) -> rs.getObject(1));

		if (fixture.fails())
			fixture.failureAssertion.assertFailure(value, reloaded);
		else
			assertThat(reloaded).isEqualTo(value);
	}

	static List<Fixture<OffsetDateTime>> getObject() {
		return asList(
				f("h2", new DescriptiveAssertion<>("H2 returns a non standard type", (__, v) -> assertThat(v).isInstanceOf(TimestampWithTimeZone.class))),
				f("hsql"),
				f("postgres", changesValue(
						exp -> Timestamp.from(
								exp.withNano(((int) Math.round(exp.getNano() / 1_000.0)) * 1_000)
										.toInstant())
				)));
	}

	@ParameterizedTest
	@MethodSource
	void getObjectOffsetDateTime(Fixture<OffsetDateTime> fixture) {

		NamedParameterJdbcTemplate jdbc = fixture.template();
		jdbc.getJdbcOperations()
				.execute("CREATE TABLE DUMMY2 (VALUE TIMESTAMP(9) WITH TIME ZONE)");

		OffsetDateTime value = OffsetDateTime.of(2005, 5, 5, 5, 5, 5, 123456789, ZoneOffset.ofHours(5));


		MapSqlParameterSource parameters = new MapSqlParameterSource("value", value);
		parameters.registerSqlType("value", Types.TIMESTAMP_WITH_TIMEZONE);
		jdbc.update("INSERT INTO DUMMY2 VALUES (:value)", parameters);

		Object reloaded = jdbc.queryForObject("SELECT VALUE FROM DUMMY2", emptyMap(), (rs, i) -> rs.getObject(1, OffsetDateTime.class));

		if (fixture.fails()) {
			fixture.failureAssertion.assertFailure(value, reloaded);
		} else {
			assertThat(reloaded).isEqualTo(value);
		}
	}

	static List<Fixture<OffsetDateTime>> getObjectOffsetDateTime() {

		return asList(
				f("h2"),
				f("hsql"),
				f("postgres", changesValue(
						exp -> OffsetDateTime.of(
								exp.withNano(((int) Math.round(exp.getNano() / 1_000.0)) * 1_000)
										.toLocalDateTime().withHour(exp.getHour() - exp.getOffset().get(ChronoField.OFFSET_SECONDS) / 3600),
								ZoneOffset.UTC
						))
				)
		);
	}
}
