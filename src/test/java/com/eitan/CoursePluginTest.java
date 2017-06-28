package com.eitan;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ResolvedDependency;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;
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
    assertResolvedDependencyVersion("asciidoctorj-groovy-dsl", "1.0.0.Alpha2");
  }

  @Test
  public void shouldConfigureAsciidoctorjVersion() {
    assertResolvedDependencyVersion("asciidoctorj", "1.5.4.1");
  }

  private void assertResolvedDependencyVersion(String dependencyName, String expectedVersion) {
    project.getRepositories().add(project.getRepositories().mavenCentral());
    project.getRepositories().add(project.getRepositories().jcenter());

    Configuration asciidoctorConfiguration = project.getConfigurations().getByName("asciidoctor");

    Set<ResolvedDependency> firstLevelDependencies =
        asciidoctorConfiguration.getResolvedConfiguration().getFirstLevelModuleDependencies();

    Optional<ResolvedDependency> resolvedDependency = firstLevelDependencies.stream()
        .filter(dependency -> dependencyName.equals(dependency.getModuleName()))
        .findAny();

    assertThat(resolvedDependency.isPresent()).isTrue();
    assertThat(resolvedDependency.get().getModuleVersion()).isEqualTo(expectedVersion);
  }


}
