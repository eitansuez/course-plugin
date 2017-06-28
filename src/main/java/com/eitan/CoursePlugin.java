package com.eitan;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.repositories.ArtifactRepository;

import java.util.Collections;

public class CoursePlugin implements Plugin<Project> {

  @Override
  public void apply(Project project) {
    setAsciidoctorAsDefaultTask(project);
    applyAsciidoctorPlugin(project);
    forceResolutionStrategyForDsl(project);
  }

  private void forceResolutionStrategyForDsl(Project project) {
    Configuration asciidoctorConfig = project.getConfigurations().getByName("asciidoctor");
    asciidoctorConfig
        .getResolutionStrategy()
        .force("org.asciidoctor:asciidoctorj-groovy-dsl:1.0.0.Alpha2");
  }

  private void setAsciidoctorAsDefaultTask(Project project) {
    project.setDefaultTasks(Collections.singletonList("asciidoctor"));
  }

  private void applyAsciidoctorPlugin(Project project) {
    project.getPluginManager().apply("org.asciidoctor.convert");
  }

}
