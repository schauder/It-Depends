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
package de.schauderhaft.itdepends.fixture;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.util.Throwables.*;

public class ExceptionAssertion<T extends Exception> implements FailureAssertion<T> {

	private final Class<? extends Exception> rootCauseType;
	private final String message;

	public ExceptionAssertion(
			Class<? extends Exception> rootCauseType,
			String message
	) {
		this.rootCauseType = rootCauseType;
		this.message = message;
	}

	@Override
	public String description(Exception value) {
		return "An exception with root cause of type " + rootCauseType + " and root message containing '" + message + "`.";
	}

	@SuppressWarnings("unchecked")
	@Override
	public void assertFailure(Exception __, Object actual) {

		assertThat(actual).isInstanceOf(Exception.class);
		Exception exception = (Exception) actual;
		assertThat(exception).hasRootCauseInstanceOf(rootCauseType);

		Exception rootCause = (Exception) getRootCause(exception);
		assertThat(rootCause).hasMessageContaining(message);
	}
}
