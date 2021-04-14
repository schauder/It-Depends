package de.schauderhaft.databasecharacterizationtests;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

@SpringBootApplication
public class DatabaseCharacterizationTestsApplication {

	public static void main(String[] args) {
		SpringApplication.run(DatabaseCharacterizationTestsApplication.class, args);
	}

	@Configuration
	class H2Configuration {
		@Bean
		NamedParameterJdbcTemplate h2() {

			return new NamedParameterJdbcTemplate(new EmbeddedDatabaseBuilder()
					.setType(EmbeddedDatabaseType.H2)
					.build());
		}
	}
	@Configuration
	class hsqldbConfiguration {
		@Bean
		NamedParameterJdbcTemplate hsqldb() {

			return new NamedParameterJdbcTemplate(new EmbeddedDatabaseBuilder()
					.setType(EmbeddedDatabaseType.HSQL)
					.build());
		}
	}

}
