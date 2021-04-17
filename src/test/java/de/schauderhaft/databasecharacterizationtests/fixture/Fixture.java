/*
 * Copyright 2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.schauderhaft.databasecharacterizationtests.fixture;

import de.schauderhaft.databasecharacterizationtests.datasource.DataSources;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

/**
 * A basic fixture for a test, specifying the database to run with and the expected result of the operation under test.
 * @param <T> the type of the expected result.
 */

public class Fixture<T> {

	final String database;
	public final FailureAssertion<T> failureAssertion;

	public static <T> Fixture<T> f(String database) {
		return new Fixture<>(database, null);
	}

	public static <T> Fixture<T> f(String database, FailureAssertion<T> failureAssertion) {
		return new Fixture<>(database, failureAssertion);
	}

	public Fixture(String database, FailureAssertion<T> failureAssertion) {
		this.database = database;
		this.failureAssertion = failureAssertion;
	}

	public NamedParameterJdbcTemplate template() {
		return new NamedParameterJdbcTemplate(DataSources.get(database));
	}

	public boolean fails() {
		return failureAssertion != null;
	}

	@Override
	public String toString() {
		return "Fixture{" +
				"database='" + database + '\'' +
				", failureAssertion=" + failureAssertion +
				'}';
	}
}
