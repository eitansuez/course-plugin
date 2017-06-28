package com.eitan;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ResolvedDependency;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class CoursePluginTest {

  private Project project;

  @Before
  public void setup() {
    project = ProjectBuilder.builder().build();
    project.getPluginManager().apply("com.eitan.course");
  }

  @Test
  public void shouldSetAsciidoctorTaskAsDefaultTask() {
    assertThat(project.getDefaultTasks().size()).isEqualTo(1);
    assertThat(project.getDefaultTasks().get(0)).isEqualTo("asciidoctor");
  }

  @Test
  public void shouldApplyAsciidoctorPluginTransitively() {
    assertThat(project.getPlugins().hasPlugin("org.asciidoctor.convert")).isTrue();
  }

  @Test
  public void shouldAddJcenterToProjectRepositories() {
    assertThat(project.getRepositories().contains(project.getRepositories().jcenter()));
  }

  @Test
  public void shouldApplyAsciidoctorDependencyResolutionStrategy() {

    project.getRepositories().add(project.getRepositories().mavenCentral());
    project.getRepositories().add(project.getRepositories().jcenter());

    Configuration asciidoctorConfiguration = project.getConfigurations().getByName("asciidoctor");

    Set<ResolvedDependency> firstLevelDependencies =
        asciidoctorConfiguration.getResolvedConfiguration().getFirstLevelModuleDependencies();

    ResolvedDependency groovyDslDependency = firstLevelDependencies.stream()
        .filter(dependency -> "asciidoctorj-groovy-dsl".equals(dependency.getModuleName()))
        .collect(Collectors.toList()).get(0);

    assertThat(groovyDslDependency).isNotNull();
    assertThat(groovyDslDependency.getModuleVersion()).isEqualTo("1.0.0.Alpha2");
  }

}
