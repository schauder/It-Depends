package de.schauderhaft.databasecharacterizationtests;

import de.schauderhaft.databasecharacterizationtests.datasource.DataSources;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static java.util.Collections.*;
import static org.assertj.core.api.Assertions.*;

class InsertAndReadDates {

	@ParameterizedTest
	@ValueSource(strings = {"h2", "hsql", "postgres"})
	void contextLoads(String dataBaseName) {

		NamedParameterJdbcTemplate jdbc = new NamedParameterJdbcTemplate(DataSources.get(dataBaseName));
		jdbc.getJdbcOperations()
				.execute("CREATE TABLE DUMMY (VALUE TIMESTAMP(9) WITH TIME ZONE)");

		OffsetDateTime value = OffsetDateTime.of(2005, 5, 5, 5, 5, 5, 123456789, ZoneOffset.ofHours(5));


		MapSqlParameterSource parameters = new MapSqlParameterSource("value", value);
		parameters.registerSqlType("pit", Types.TIMESTAMP_WITH_TIMEZONE);
		jdbc.update("INSERT INTO DUMMY VALUES (:value)", parameters);

		Object reloaded = jdbc.queryForObject("SELECT VALUE FROM DUMMY", emptyMap(), OffsetDateTime.class);

		assertThat(reloaded).isEqualTo(value);
	}
}
