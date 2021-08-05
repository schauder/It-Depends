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

import java.util.Objects;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.*;

/**
 * A {@link FailureAssertion} that can be described as a transformation of the normal expected value.
 * @param <S> type of the normal expected result.
 * @param <T> type of the actual result.
 */
public class ValueChange<S,T> implements FailureAssertion<S>{

	Function<S, T> transformation;

	public static <S,T> ValueChange<S,T> changesValue(Function<S, T> transformation) {
		return new ValueChange<>(transformation);
	}

	public ValueChange(Function<S, T> transformation) {
		this.transformation = transformation;
	}


	@Override
	public String description(S value) {
		return Objects.toString(transformation.apply(value));
	}

	@Override
	public void assertFailure(S expected, Object actual) {
		assertThat(actual).isEqualTo(transformation.apply(expected));
	}
}
