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

import java.util.function.BiConsumer;

/**
 * A {@link de.schauderhaft.databasecharacterizationtests.fixture.FailureAssertion} with explicitly provided description and {@link FailureAssertion}.
 *
 * @param <T> type of the normally expected result.
 */
public class DescriptiveAssertion<T> implements FailureAssertion<T> {

	private final String description;
	private final BiConsumer<T, Object> assertion;

	public DescriptiveAssertion(String description, BiConsumer<T, Object> assertion) {
		this.description = description;
		this.assertion = assertion;
	}

	@Override
	public String description(T value) {
		return description;
	}

	@Override
	public void assertFailure(T exp, Object actual) {
		assertion.accept(exp, actual);
	}

	@Override
	public String toString() {
		return "DescriptiveAssertion{" +
				"description='" + description + '\'' +
				'}';
	}
}
