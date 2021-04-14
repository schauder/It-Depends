package de.schauderhaft.databasecharacterizationtests;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Map;

import static java.util.Collections.*;

@SpringBootTest
class InsertAndReadDates {

	@Nested
	class H2 {

		@Autowired
		@Qualifier("h2")
		NamedParameterJdbcTemplate jdbc;

		@Test
		void contextLoads() {
			jdbc.getJdbcOperations()
					.execute("CREATE TABLE DUMMY (TIMESTAMP TIMESTAMP)");

		}
	}

	@Nested
	class HsqlDb {

		@Autowired
		@Qualifier("hsqldb")
		NamedParameterJdbcTemplate jdbc;

		@Test
		void contextLoads() {
			jdbc.getJdbcOperations()
					.execute("CREATE TABLE DUMMY (TIMESTAMP TIMESTAMP)");

			Instant pointInTime = LocalDateTime.of(2005, 5, 5, 5, 5, 5, 123456789).toInstant(ZoneOffset.UTC);
			Timestamp pitTs = Timestamp.from(pointInTime);
			System.out.println(pitTs);

			MapSqlParameterSource parameters = new MapSqlParameterSource("pit", pitTs);
			parameters.registerSqlType("pit", Types.TIMESTAMP);
			jdbc.update("insert into dummy values (:pit)", parameters);

			Object reloaded = jdbc.queryForObject("select timestamp from dummy", emptyMap(), Object.class);

			System.out.println(reloaded);
			System.out.println(reloaded.getClass());
		}

		@Test
		void timestamps() {
			Instant now = Instant.now();
			Timestamp from = Timestamp.from(now);
			Timestamp fromMillis = new Timestamp(now.toEpochMilli());


			System.out.println("now    " + now);
			System.out.println("from   " + from);
			System.out.println("fromMi " + fromMillis);

			System.out.println("now    ms " + now.toEpochMilli());
			System.out.println("from   ms " + from.getTime());
			System.out.println("fromMi ms " + fromMillis.getTime());


		}
	}


}
