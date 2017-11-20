package com.eitan

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy

class CoursePlugin implements Plugin<Project> {
  private coursePluginDir

  void apply(Project project) {
    project.extensions.create("course", CourseInfo)

    project.pluginManager.apply 'org.asciidoctor.convert'
    project.defaultTasks = ['asciidoctor']

    coursePluginDir = new File(project.buildDir, "courseplugin")
    this.coursePluginDir.mkdirs()

    extractArtifactsBeforeAsciidoctorRuns(project)
    configureAsciidoctor(project)
    writeManifestAfterAsciidoctorRuns(project)

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
      version = '1.5.6'
      groovyDslVersion = '1.0.0.Alpha2'
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
          'allow-uri-read': '',
          stylesdir: 'stylesheets',
          stylesheet: 'asciidoctor-spring.css'

      extensions new File("${coursePluginDir}/extensions/attributes.groovy"),
          new File("${coursePluginDir}/extensions/alternatives.groovy")

    }
  }

  def writeManifestAfterAsciidoctorRuns(Project project) {
    def postTask = project.tasks.create(name: 'writeCfManifest') {
      doLast {
        project.file("${project.buildDir}/html5/manifest.yml") << """---
applications:
- name: ${project.course.name}
  host: ${project.course.hostname}
  memory: 64M
  buildpack: staticfile_buildpack
"""
      }
    }

    project.asciidoctor.finalizedBy postTask

  }


}
