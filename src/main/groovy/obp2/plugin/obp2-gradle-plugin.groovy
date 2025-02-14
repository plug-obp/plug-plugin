package obp2.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JavaToolchainService

class OBPExtension {
    JavaLanguageVersion javaVersion = JavaLanguageVersion.of(8)
}

class OBP2GradleBuild implements Plugin<Project> {
    void apply(Project project) {
        // Register custom extension
        project.extensions.create("obp2", OBPExtension)

        // Apply necessary plugins
        project.pluginManager.apply("java-library")
        project.pluginManager.apply("maven-publish")

        // Set project metadata
        project.group = "fr.ensta-bretagne"

        // Configure Java toolchain **AFTER** evaluating the project
        project.afterEvaluate {
            def obp2 = project.extensions.getByType(OBPExtension)
            
            project.extensions.configure(JavaPluginExtension) { java ->
                java.toolchain.languageVersion.set(obp2.javaVersion)
                java.withSourcesJar()
                java.withJavadocJar()
            }

            project.tasks.withType(JavaCompile).configureEach { task ->
                task.options.encoding = "UTF-8"
                task.javaCompiler.set(
                    project.extensions.getByType(JavaToolchainService).compilerFor {
                        languageVersion.set(obp2.javaVersion)
                    }
                )
            }
        }

        // Configure repositories
        project.repositories {
            mavenCentral()
            maven {
                url = project.findProperty("OBP2ReadOnlyMavenRepository") ?: "https://missing_read_only_maven_repo_url"
                allowInsecureProtocol = true
            }
        }

        // Configure publishing
        project.extensions.configure(PublishingExtension) { publishing ->
            publishing.repositories {
                def publishRepo = project.findProperty("OBP2PublishMavenRepository")
                if (publishRepo) {  // Only add if defined
                    maven {
                        url = publishRepo
                    }
                }
                maven {
                    url = project.findProperty("OBP2LocalMavenRepository") ?: "file://${project.buildDir}/local-repo"
                }
            }
            publishing.publications {
                create("mavenJava", MavenPublication) { publication ->
                    def javaComponent = project.components.findByName("java")
                    if (javaComponent != null) {
                        publication.from(javaComponent)
                    }
                }
            }
        }
    }
}
