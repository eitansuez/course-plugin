package com.eitan

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy

class CoursePlugin implements Plugin<Project> {

  void apply(Project project) {
    project.pluginManager.apply 'org.asciidoctor.convert'
    project.defaultTasks = ['asciidoctor']
    extractArtifactsBeforeAsciidoctorRuns(project)
    configureAsciidoctor(project)
  }

  def extractArtifactsBeforeAsciidoctorRuns(Project project) {
    def jarPath = getClass().protectionDomain.codeSource.location.path

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
    project.asciidoctor.dependsOn prepTask
  }

  def configureAsciidoctor(Project project) {
    project.extensions.asciidoctorj.with {
      version = "1.5.4.1"
      groovyDslVersion = "1.0.0.Alpha2"
    }

    project.asciidoctor {
      backends 'html5'

      sourceDir project.file('docs')
      sources {
        include '**/*.adoc'
      }
      resources {
        from('resources') {
          include '**/*'
        }
        from("${project.buildDir}/courseplugin/web") {
          include '**/*'
        }
      }
      outputDir project.buildDir
      attributes docinfodir: "${project.buildDir}/courseplugin/doc",
          toc: 'left',
          sectnums: '',
          icons: 'font',
          experimental: '',
          linkcss: '',
          linkattrs: '',
          docinfo: 'shared',
          imagesdir: 'images',
          'source-highlighter': 'highlightjs',
          highlightjsdir: 'highlight',
          'allow-uri-read': ''

      extensions new File("${project.buildDir}/courseplugin/extensions/attributes.groovy"),
          new File("${project.buildDir}/courseplugin/extensions/alternatives.groovy")

    }
  }

}
