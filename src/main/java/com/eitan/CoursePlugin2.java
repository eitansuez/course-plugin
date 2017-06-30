package com.eitan;

import org.asciidoctor.gradle.AsciidoctorBackend;
import org.asciidoctor.gradle.AsciidoctorExtension;
import org.asciidoctor.gradle.AsciidoctorTask;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.CopySpec;

import java.io.File;
import java.util.Collections;

public class CoursePlugin2 implements Plugin<Project> {

  @Override
  public void apply(Project project) {
    setAsciidoctorAsDefaultTask(project);
    applyAsciidoctorPlugin(project);

    extractArtifactsToBuildDir(project);

    configureAsciidoctor(project);
  }

  private void extractArtifactsToBuildDir(Project project) {
    String jarPath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();

    File coursePluginDir = new File(project.getBuildDir(), "courseplugin");
    coursePluginDir.mkdirs();

//    task testTask(type: Copy) {
//      from(zipTree('courseplugin.jar')) {
//        include 'web/**/*'
//        include 'doc/*'
//        include 'extensions/*'
//      }
//      into {
//        "build/courseplugin"
//      }
//    }
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
    asciidoctor.extensions( "build/courseplugin/extensions/attributes.groovy",
      "build/courseplugin/extensions/alternatives.groovy" );
  }

}
