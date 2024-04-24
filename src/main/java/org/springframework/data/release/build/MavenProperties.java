/*
 * Copyright 2015-2022 the original author or authors.
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
package org.springframework.data.release.build;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Maven configuration properties.
 *
 * @author Oliver Gierke
 * @author Mark Paluch
 */
@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "maven")
public class MavenProperties {

	private File mavenHome;
	private File localRepository;
	private Map<String, String> plugins;
	private boolean consoleLogger = true;
	private boolean parallelize;

	public MavenProperties() {

		String maven_home = System.getenv("MAVEN_HOME");

		if (StringUtils.hasText(maven_home)) {
			mavenHome = new File(maven_home);
		}
	}

	/**
	 * Configures the local Maven repository location to use. In case the given folder does not already exists it's
	 * created.
	 *
	 * @param localRepository must not be {@literal null} or empty.
	 */
	public void setLocalRepository(String localRepository) {

		Assert.hasText(localRepository, "Local repository must not be null!");

		log.info("Using {} as local Maven repository!", localRepository);

		this.localRepository = new File(localRepository.replace("~", FileUtils.getUserDirectoryPath()));

		if (!this.localRepository.exists()) {
			this.localRepository.mkdirs();
		}
	}

	/**
	 * Returns the fully-qualified plugin goal for the given local one.
	 *
	 * @param goal must not be {@literal null} or empty.
	 * @return
	 */
	public String getFullyQualifiedPlugin(String goal) {

		Assert.hasText(goal, "Goal must not be null or empty!");

		if (goal.startsWith("-")) {
			return goal;
		}

		String[] parts = goal.split(":");

		if (parts.length != 2 || !plugins.containsKey(parts[0])) {
			return goal;
		}

		return plugins.get(parts[0]).concat(":").concat(parts[1]);
	}
}
