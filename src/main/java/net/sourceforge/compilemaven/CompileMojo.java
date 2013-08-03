package net.sourceforge.compilemaven;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.InstantiationStrategy;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

@Mojo(name = "compile", executionStrategy = "always", inheritByDefault = true, instantiationStrategy = InstantiationStrategy.SINGLETON, defaultPhase = LifecyclePhase.NONE, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME, requiresDependencyCollection = ResolutionScope.COMPILE_PLUS_RUNTIME, requiresDirectInvocation = true, requiresOnline = false, requiresProject = true, requiresReports = false, threadSafe = false)
@Execute(goal = "compile")
public class CompileMojo extends AbstractMojo {

	@Parameter(required = false, alias = "outputDirectory", property = "outputDirectory")
	private File outputDirectory;

	@Parameter(required = true, alias = "classes", property = "classes")
	private String classes;

	@Component
	private MavenProject project;

	public void execute() throws MojoExecutionException {
		try {
			Runtime.getRuntime().exec("javac " + getOutputDirectoryOption() + getClassPathOptions() + classes);
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}

	}

	private String getClassPathOptions() {
		StringBuffer sb = new StringBuffer();
		if (StringUtils.isNotBlank(getProjectClassPath())) {
			sb.append("-cp ").append(getProjectClassPath()).append(" ");
		}
		return sb.toString();
	}

	private String getOutputDirectoryOption() {
		StringBuffer sb = new StringBuffer();
		if (outputDirectory != null) {
			sb.append("-d ").append(outputDirectory.getAbsolutePath()).append(" ");
		}
		return sb.toString();
	}

	private String getProjectClassPath() {
		Set<Artifact> artifacts = project.getArtifacts();
		List<String> paths = new ArrayList<String>();
		for (Artifact artifact : artifacts) {
			if (artifact != null && artifact.getFile() != null) {
				paths.add(artifact.getFile().getAbsolutePath());
			}
		}
		paths.add(project.getBuild().getSourceDirectory());
		return StringUtils.join(paths, ';');

	}

}
