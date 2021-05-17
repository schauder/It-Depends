package de.schauderhaft.databasecharacterizationtests;

import de.schauderhaft.databasecharacterizationtests.fixture.ExceptionAssertion;
import de.schauderhaft.databasecharacterizationtests.fixture.FailureAssertion;
import de.schauderhaft.databasecharacterizationtests.fixture.Fixture;
import de.schauderhaft.databasecharacterizationtests.support.DbTest;
import de.schauderhaft.databasecharacterizationtests.support.TableName;
import org.postgresql.util.PSQLException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static java.util.Arrays.*;
import static java.util.Collections.*;
import static org.assertj.core.api.Assertions.*;

public class InsertAndIntervalCalculation {

	@DbTest
	void insertDateAfterCalculation(DateFixture fixture, TableName table) {

		NamedParameterJdbcTemplate jdbc = fixture.template();
		jdbc.getJdbcOperations()
				.execute(String.format("CREATE TABLE %s (VALUE TIMESTAMP)", table));

		OffsetDateTime value = OffsetDateTime.of(2005, 5, 5, 5, 5, 5, 123456789, ZoneOffset.ofHours(5));


		MapSqlParameterSource parameters = new MapSqlParameterSource("value", value);
		if (fixture.jdbcTypeValue != null) {
			parameters.registerSqlType("value", fixture.jdbcTypeValue);
		}
		if (fixture.fails()) {
			try {
				jdbc.update(String.format("INSERT INTO %s VALUES (:value - interval '30 minutes')", table), parameters);
				failBecauseExceptionWasNotThrown(RuntimeException.class);
			} catch (RuntimeException exception) {
				fixture.failureAssertion.assertFailure(null, exception);
				return;
			}
		}
		jdbc.update(String.format("INSERT INTO %s VALUES (:value - interval '30 minutes')", table), parameters);
		Object reloaded = jdbc.queryForObject("SELECT VALUE FROM " + table, emptyMap(), (rs, i) -> rs.getObject(1));

		assertThat(reloaded).isNotNull();
	}

	static List<DateFixture> insertDateAfterCalculation() {
		return asList(
//				f("h2", new DescriptiveAssertion<>("H2 returns a non standard type", (__, v) -> assertThat(v).isInstanceOf(TimestampWithTimeZone.class))),
//				f("hsql"),
				f("postgres", new ExceptionAssertion<>(NumberFormatException.class, "Trailing junk on timestamp"), LocalDateTime.now(), Types.TIMESTAMP),
				f("postgres", new ExceptionAssertion<>(PSQLException.class, "ERROR: column \"value\" is of type timestamp without time zone but expression is of type interval"), LocalDateTime.now(), Types.OTHER),
				f("postgres", new ExceptionAssertion<>(PSQLException.class, "Unsupported Types value: 0"), LocalDateTime.now(), Types.NULL),
				f("postgres", LocalDateTime.now(), null),
				f("postgres", new ExceptionAssertion<>(NumberFormatException.class, "Trailing junk on timestamp"), Timestamp.from(Instant.now()), Types.TIMESTAMP),
				f("postgres", new ExceptionAssertion<>(PSQLException.class, "ERROR: column \"value\" is of type timestamp without time zone but expression is of type interval"), Timestamp.from(Instant.now()), Types.OTHER),
				f("postgres", new ExceptionAssertion<>(PSQLException.class, "Unsupported Types value: 0"), Timestamp.from(Instant.now()), Types.NULL),
				f("postgres", Timestamp.from(Instant.now()), null)
//				f("mysql",
//						changesValue(
//								exp -> Timestamp.from(
//										exp.withNano(((int) Math.round(exp.getNano() / 1_000.0)) * 1_000)
//												.toInstant()
//								)
//						),
//						"TIMESTAMP(6)")
		);
	}


	static DateFixture f(String database, Object dateLikeValue, Integer jdbcTypeValue) {
		return new DateFixture(database, null, dateLikeValue, jdbcTypeValue);
	}

	static DateFixture f(String database, FailureAssertion<RuntimeException> failureAssertion, Object dateLikeValue, Integer jdbcTypeValue) {
		return new DateFixture(database, failureAssertion, dateLikeValue, jdbcTypeValue);
	}

	private static class DateFixture extends Fixture<RuntimeException> {

		final Object dateLikeValue;
		final Integer jdbcTypeValue;

		private DateFixture(String database, FailureAssertion<RuntimeException> failureAssertion, Object dateLikeValue, Integer jdbcTypeValue) {

			super(database, failureAssertion);

			this.dateLikeValue = dateLikeValue;
			this.jdbcTypeValue = jdbcTypeValue;
		}

		@Override
		public String toString() {
			return "DateFixture{" +
					"database=" + database +
					", dateLikeValue=" + dateLikeValue +
					", jdbcTypeValue=" + jdbcTypeValue +
					'}';
		}
	}
}
