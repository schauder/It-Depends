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

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class TestIdTest {

	@Test
	void create() {

		final TestId testId = TestId.createFromUniqueId("[engine:junit-jupiter]/[class:de.schauderhaft.itdepends.InsertAndIntervalCalculationTest]/[test-template:insertDateAfterCalculation(de.schauderhaft.itdepends.InsertAndIntervalCalculationTest$DateFixture, de.schauderhaft.itdepends.support.TableName)]");

		assertThat(testId).isNotNull();

		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(testId.className).isEqualTo("InsertAndIntervalCalculationTest");
			softly.assertThat(testId.packageName).isEqualTo("de.schauderhaft.itdepends");
			softly.assertThat(testId.name).isEqualTo("insertDateAfterCalculation");
		});
	}
}
