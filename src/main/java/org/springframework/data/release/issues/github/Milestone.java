/*
 * Copyright 2020-2022 the original author or authors.
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
package org.springframework.data.release.issues.github;

import lombok.Value;

import org.springframework.data.release.model.ModuleIteration;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Oliver Gierke
 * @author Mark Paluch
 */
@Value
class Milestone {

	Long number;
	String title;
	String description;
	String state;

	public static Milestone of(String title, String description) {
		return new Milestone(null, title, description, null);
	}

	public boolean matches(ModuleIteration moduleIteration) {

		return moduleIteration.getSupportedProject().getProject().isUseShortVersionMilestones()
				? title.equals(moduleIteration.getReleaseVersionString())
				: title.contains(moduleIteration.getShortVersionString());
	}

	@JsonIgnore
	public boolean isOpen() {
		return "open".equals(state);
	}

	public Milestone markReleased() {
		return new Milestone(number, null, null, "closed");
	}
}
