package com.eitan

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy

class CoursePlugin implements Plugin<Project> {
  private coursePluginDir

  void apply(Project project) {
    coursePluginDir = new File(project.buildDir, "courseplugin")
    this.coursePluginDir.mkdirs()

    project.pluginManager.apply 'org.asciidoctor.convert'
    project.defaultTasks = ['asciidoctor']
    extractArtifactsBeforeAsciidoctorRuns(project)
    configureAsciidoctor(project)
  }

  def extractArtifactsBeforeAsciidoctorRuns(Project project) {
    def jarPath = getClass().protectionDomain.codeSource.location.path

    def prepTask = project.tasks.create(name: 'extractArtifacts', type: Copy) {
      from(project.zipTree(jarPath)) {
        include 'web/**/*'
        include 'doc/*'
        include 'extensions/*'
      }
      into this.coursePluginDir
    }

    project.asciidoctor.dependsOn prepTask
  }

  def configureAsciidoctor(Project project) {
    project.extensions.asciidoctorj.with {
      version = "1.5.5"
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
        from("${coursePluginDir}/web") {
          include '**/*'
        }
      }
      outputDir project.buildDir
      attributes docinfodir: "${coursePluginDir}/doc",
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

      extensions new File("${coursePluginDir}/extensions/attributes.groovy"),
          new File("${coursePluginDir}/extensions/alternatives.groovy")

    }
  }

}
