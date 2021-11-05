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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class TestId {
	final String packageName;
	final String className;
	final String name;

	TestId(String packageName, String className, String name) {
		this.packageName = packageName;
		this.className = className;
		this.name = name;
	}

	static TestId createFromUniqueId(String testId) {
		try {

			Pattern pattern = Pattern.compile("\\[class:(.*)\\]/\\[test-template:(.*)\\(.*\\)\\]");
			Matcher matcher = pattern.matcher(testId);
			if (!matcher.find()) {
				return null;
			}
			final String fullClassName = matcher.group(1);
			final String simpleName = Class.forName(fullClassName).getSimpleName();
			final String packageName = fullClassName.substring(0, fullClassName.length() - simpleName.length()-1);
			return new TestId(packageName, simpleName, matcher.group(2));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
