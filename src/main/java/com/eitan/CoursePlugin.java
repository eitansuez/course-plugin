package com.eitan;

import org.asciidoctor.gradle.AsciidoctorBackend;
import org.asciidoctor.gradle.AsciidoctorExtension;
import org.asciidoctor.gradle.AsciidoctorTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.repositories.ArtifactRepository;

import java.util.Collections;

public class CoursePlugin implements Plugin<Project> {

  @Override
  public void apply(Project project) {
    setAsciidoctorAsDefaultTask(project);
    applyAsciidoctorPlugin(project);
    configureAsciidoctor(project);
  }

  private void setAsciidoctorAsDefaultTask(Project project) {
    project.setDefaultTasks(Collections.singletonList("asciidoctor"));
  }

  private void applyAsciidoctorPlugin(Project project) {
    project.getPluginManager().apply("org.asciidoctor.convert");
  }

  private void configureAsciidoctor(Project project) {
    AsciidoctorExtension asciidoctorjExtension =
        (AsciidoctorExtension) project.getExtensions().getByName("asciidoctorj");
    asciidoctorjExtension.setVersion("1.5.4.1");
    asciidoctorjExtension.setGroovyDslVersion("1.0.0.Alpha2");
    configureDefaultBackend(project);
  }

  private void configureDefaultBackend(Project project) {
    AsciidoctorTask asciidoctor =
        (AsciidoctorTask) project.getTasksByName("asciidoctor", false).iterator().next();
    asciidoctor.backends(AsciidoctorBackend.HTML5.getId());
  }

}
