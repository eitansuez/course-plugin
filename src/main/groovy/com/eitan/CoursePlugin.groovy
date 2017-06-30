package com.eitan

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

  def setupToExtractArtifactsBeforeAsciidoctorRuns(Project project) {
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

  def configureAsciidoctor(Project project) {
    project.extensions.getByName('asciidoctorj').with {
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
        from('build/courseplugin/web') {
          include '**/*'
        }
      }
      outputDir project.file('build')
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

      extensions new File('build/courseplugin/extensions/attributes.groovy'),
          new File('build/courseplugin/extensions/alternatives.groovy')

    }
  }

}
