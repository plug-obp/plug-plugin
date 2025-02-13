package plug.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.GradleBuild

/**
 */
class OBPExtension {
    String version = "1.1.0"
}

/**
 * Gradle build plugin for Plug projects
 *
 * Created by charlie on 12/01/2018.
 */
class OBP2GradleBuild implements Plugin<Project> {

    void apply(Project project) {

        project.extensions.create("obp2", OBPExtension)

        project.allprojects {
            apply plugin: 'java'
            apply plugin: 'maven-publish'

            group='fr.ensta-bretagne'
            version = project.obp2.version
            
            javaVersion = project.obp2.javaVersion

            java {
                toolchain {
                    languageVersion.set(javaVersion)
                }
                withSourcesJar()
                withJavadocJar()
            }

            // Sets Java compile option to use UTF-8 encoding
            compileJava {
		        options.encoding = 'UTF-8'
		        javaCompiler.set(javaToolchains.compilerFor { languageVersion.set(javaVersion) } )
	        }

            repositories {
                mavenCentral()
                maven {
                    url="${OBP2ReadOnlyMavenRepository}"
                    allowInsecureProtocol = true
                }
            }

            // Alls tests depends on junit 4
            dependencies {
                testImplementation group: 'junit', name: 'junit', version: '4.13.1'
            }
            
            publishing {
                repositories {
                    maven {
                        url = "${OBP2PublishMavenRepository}"
                    }
                    maven {
                        url = "${OBP2LocalMavenRepository}"
                    }
                }
                publications {
                    maven(MavenPublication) {
                        from components.java
                    }
                }
            }
        }
    }
}

