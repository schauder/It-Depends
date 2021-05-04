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
package de.schauderhaft.databasecharacterizationtests.datasource;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.testcontainers.containers.MySQLContainer;

import javax.sql.DataSource;

public class MySqlDataSourceFactory extends DataSourceFactory {

	private static MySQLContainer<?> MYSQL_CONTAINER;

	@Override
	DataSource createDataSource() {

		if (MYSQL_CONTAINER == null) {

			MySQLContainer<?> container = new MySQLContainer<>("mysql:8.0.24");
			container.start();

			MYSQL_CONTAINER = container;
		}

		MysqlDataSource dataSource = new MysqlDataSource();
		dataSource.setUrl(MYSQL_CONTAINER.getJdbcUrl());
		dataSource.setUser(MYSQL_CONTAINER.getUsername());
		dataSource.setPassword(MYSQL_CONTAINER.getPassword());

		return dataSource;
	}
}
