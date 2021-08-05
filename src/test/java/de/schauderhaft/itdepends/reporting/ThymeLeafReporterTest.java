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
package de.schauderhaft.itdepends.reporting;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;

public class ThymeLeafReporterTest {

	@ParameterizedTest
	@MethodSource
	void test(Fixture f) {
		assertThat(ThymeLeafReporter.canonicalize(f.in)).isEqualTo(f.out);
	}

	static List<Fixture> test() {
		return asList(
				f("abCDe", "abCDe"),
				f("ab/De", "ab/De"),
				f("ab3CDe", "abCDe"),
				f("ab@CDe", "abCDe"),
				f("ab CDe", "abCDe")
		);
	}

	private static Fixture f(String in, String out) {
		return new Fixture(in, out);
	}


	private static class Fixture {
		private final String in;
		private final String out;

		public Fixture(String in, String out) {
			this.in = in;
			this.out = out;
		}

		@Override
		public String toString() {
			return "Fixture{" +
					"in='" + in + '\'' +
					", out='" + out + '\'' +
					'}';
		}
	}
}
