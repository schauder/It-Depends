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
package de.schauderhaft.databasecharacterizationtests.reporting;

import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.TestTag;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ThymeLeafReporter implements TestExecutionListener {

	private TemplateEngine templateEngine;

	public ThymeLeafReporter() {

		ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
		templateResolver.setPrefix("/templates/");
		templateResolver.setSuffix(".xhtml");
		templateResolver.setCacheable(true);

		templateEngine = new TemplateEngine();
		templateEngine.setTemplateResolver(templateResolver);
	}

	@Override
	public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {

		if (testIdentifier.isTest()) {
			return;
		}
		if (!testIdentifier.getTags().contains(TestTag.create("reporting"))) {
			return;
		}

		String uniqueId = testIdentifier.getUniqueId();

		Pattern pattern = Pattern.compile("\\[class:(.*)\\]/\\[test-template:(.*)\\(.*\\)\\]");
		Matcher matcher = pattern.matcher(uniqueId);
		if (!matcher.find()) {
			return;
		}
		String className = matcher.group(1);
		String methodName = matcher.group(2);

		try {
			String simpleName = Class.forName(className).getSimpleName();
			String fileName = simpleName + "/" + methodName;


			Context context = new Context();

			String displayName = testIdentifier.getDisplayName();
			context.setVariable("testName", displayName);


			templateEngine.process("execution", context, createWriter(fileName));

		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}

	}

	private Writer createWriter(String name) throws IOException {

		name = canonicalize(name);

		File file = new File("target/it-depends-reports/" + name + ".html");
		file.getParentFile().mkdirs();
		return new BufferedWriter(new FileWriter(file));
	}

	static String canonicalize(String name) {
		return name.replaceAll("[^a-zA-Z/]", "");
	}
}
