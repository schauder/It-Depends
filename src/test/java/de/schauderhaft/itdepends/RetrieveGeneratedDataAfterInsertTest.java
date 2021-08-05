package de.schauderhaft.itdepends;

import de.schauderhaft.itdepends.fixture.FailureAssertion;
import de.schauderhaft.itdepends.fixture.Fixture;
import de.schauderhaft.itdepends.support.DbTest;
import de.schauderhaft.itdepends.support.TableName;
import org.junit.jupiter.api.DisplayName;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.math.BigInteger;
import java.util.List;

import static de.schauderhaft.itdepends.fixture.ValueChange.*;
import static java.util.Arrays.*;
import static java.util.Collections.*;
import static org.assertj.core.api.Assertions.*;

public class RetrieveGeneratedDataAfterInsertTest {

	@DbTest
	@DisplayName("Can a single generated value be retrieved from the database.")
	void retrieveAfterInsertWithDefault(IdGenerationFixture fixture, TableName table) {

		NamedParameterJdbcTemplate jdbc = fixture.template();
		jdbc.getJdbcOperations()
				.execute(String.format("CREATE TABLE %s (VALUE %s)", table, fixture.typeDeclaration));

		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbc.update(String.format("INSERT INTO %s(VALUE) VALUES (DEFAULT)", table), new MapSqlParameterSource(emptyMap()), keyHolder);

		Number generatedKey = keyHolder.getKey();

		if (fixture.fails())
			fixture.failureAssertion.assertFailure(1, generatedKey);
		else
			assertThat(generatedKey).isEqualTo(1);
	}

	static List<IdGenerationFixture> retrieveAfterInsertWithDefault() {
		return asList(
				f("h2", "INTEGER IDENTITY"),
				f("hsql", "INTEGER IDENTITY", changesValue((act) -> 0)),
				f("postgres", "SERIAL"),
				f("mysql", "SERIAL", changesValue(act -> BigInteger.valueOf(act.longValue())))
		);
	}

	static IdGenerationFixture f(String database, String typeDeclaration, FailureAssertion<Number> failureAssertion) {
		return new IdGenerationFixture(database, typeDeclaration, failureAssertion);
	}

	static IdGenerationFixture f(String database, String typeDeclaration) {
		return new IdGenerationFixture(database, typeDeclaration, null);
	}

	static class IdGenerationFixture extends Fixture<Number> {
		final String typeDeclaration;

		IdGenerationFixture(String database, String typeDeclaration, FailureAssertion<Number> failureAssertion) {
			super(database, failureAssertion);
			this.typeDeclaration = typeDeclaration;
		}
	}

}
