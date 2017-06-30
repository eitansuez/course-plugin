package com.eitan

import org.asciidoctor.gradle.AsciidoctorBackend
import org.asciidoctor.gradle.AsciidoctorExtension
import org.asciidoctor.gradle.AsciidoctorTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy

class CoursePlugin implements Plugin<Project> {

  void apply(Project project) {
    project.pluginManager.apply 'org.asciidoctor.convert'
    project.defaultTasks = ['asciidoctor']
    setupToExtractArtifactsBeforeAsciidoctorRuns(project)
    configureAsciidoctor(project)
  }

  private void setupToExtractArtifactsBeforeAsciidoctorRuns(Project project) {
    def jarPath = getClass().protectionDomain.codeSource.location.path
    println jarPath

    File coursePluginDir = new File(project.buildDir, "courseplugin")
    coursePluginDir.mkdirs()

    def prepTask = project.tasks.create(name: 'extractArtifacts', type: Copy) {
      from(project.zipTree(jarPath)) {
        include 'web/**/*'
        include 'doc/*'
        include 'extensions/*'
      }
      into "${project.buildDir}/courseplugin"
    }
    def task = project.getTasksByName('asciidoctor', false).iterator().next()
    task.dependsOn prepTask
  }

  private void configureAsciidoctor(Project project) {
    AsciidoctorExtension asciidoctorjExtension =
        (AsciidoctorExtension) project.getExtensions().getByName("asciidoctorj")
    asciidoctorjExtension.setVersion("1.5.4.1")
    asciidoctorjExtension.setGroovyDslVersion("1.0.0.Alpha2")

    AsciidoctorTask asciidoctor =
        (AsciidoctorTask) project.getTasksByName("asciidoctor", false).iterator().next()
    configureDefaultBackend(asciidoctor)
    configureExtensions(asciidoctor)
  }

  private void configureDefaultBackend(AsciidoctorTask asciidoctor) {
    asciidoctor.backends(AsciidoctorBackend.HTML5.getId())
  }

  private void configureExtensions(AsciidoctorTask asciidoctor) {
    asciidoctor.extensions( "build/courseplugin/extensions/attributes.groovy",
        "build/courseplugin/extensions/alternatives.groovy" )
  }

}
