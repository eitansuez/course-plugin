package com.eitan;

import org.asciidoctor.gradle.AsciidoctorBackend;
import org.asciidoctor.gradle.AsciidoctorExtension;
import org.asciidoctor.gradle.AsciidoctorTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.util.GFileUtils;

import java.io.File;
import java.io.IOException;
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

    AsciidoctorTask asciidoctor =
        (AsciidoctorTask) project.getTasksByName("asciidoctor", false).iterator().next();
    configureDefaultBackend(asciidoctor);
    configureExtensions(asciidoctor);
  }

  private void configureDefaultBackend(AsciidoctorTask asciidoctor) {
    asciidoctor.backends(AsciidoctorBackend.HTML5.getId());
  }

  private void configureExtensions(AsciidoctorTask asciidoctor) {
    asciidoctor.extensions(
        fileResource("/extensions/attributes.groovy"),
        fileResource("/extensions/alternatives.groovy")
    );
  }

  private File fileResource(String path) {
    String tempDir = System.getProperty("java.io.tmpdir");
    String filename = path.substring(path.lastIndexOf("/") + 1);
    File file = new File(tempDir, filename);

    GFileUtils.copyURLToFile(getClass().getResource(path), file);
    file.deleteOnExit();
    return file;
  }

}
