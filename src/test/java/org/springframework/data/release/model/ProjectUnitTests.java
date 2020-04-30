/*
 * Copyright 2015-2020 the original author or authors.
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
package org.springframework.data.release.model;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * @author Oliver Gierke
 */
class ProjectUnitTests {

	@Test
	void testname() {

		List<Project> projects = new ArrayList<>(Projects.PROJECTS);
		// Collections.reverse(projects);
		// Collections.sort(projects);

		projects.stream().map(Project::getName).forEach(System.out::println);

		assertThat(projects.get(0)).isEqualTo(Projects.BUILD);
		assertThat(projects.get(1)).isEqualTo(Projects.COMMONS);
	}

	@Test // #28
	void findsProjectByKey() {
		assertThat(Projects.requiredByName("DATACMNS")).isEqualTo(Projects.COMMONS);
	}

	@Test
	void returnsCustomFullNameIfSet() {

		assertThat(Projects.BUILD.getFullName()).isEqualTo("Spring Data Build");
		assertThat(Projects.CASSANDRA.getFullName()).isEqualTo("Spring Data for Apache Cassandra");
	}

	@Test
	void returnsAllDependencies() {

		assertThat(Projects.REDIS.getDependencies())//
				.contains(Projects.COMMONS, Projects.KEY_VALUE, Projects.BUILD);
	}
}
