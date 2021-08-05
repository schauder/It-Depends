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
package de.schauderhaft.itdepends.datasource;

import org.postgresql.ds.PGSimpleDataSource;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;

public class PostgreSqlDataSourceFactory extends DataSourceFactory {

	private static PostgreSQLContainer<?> POSTGRESQL_CONTAINER;

	@Override
	DataSource createDataSource() {

		if (POSTGRESQL_CONTAINER == null) {

			PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:13.2");
			container.start();

			POSTGRESQL_CONTAINER = container;
		}

		PGSimpleDataSource dataSource = new PGSimpleDataSource();
		dataSource.setUrl(POSTGRESQL_CONTAINER.getJdbcUrl());
		dataSource.setUser(POSTGRESQL_CONTAINER.getUsername());
		dataSource.setPassword(POSTGRESQL_CONTAINER.getPassword());

		return dataSource;
	}
}
