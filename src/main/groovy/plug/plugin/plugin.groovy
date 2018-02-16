package plug.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
/**
 */
class PlugExtension {
    String version = "0.0.4"
    String repository = "/Volumes/data/webperso/mocs-artefacts/plug-repo"
}

/**
 * Gradle build plugin for Plug projects
 *
 * Created by charlie on 12/01/2018.
 */
class PlugBuild implements Plugin<Project> {

    void apply(Project project) {

        project.extensions.create("plug", PlugExtension)

        project.allprojects {
            apply plugin: 'java'
            apply plugin: 'maven-publish'

            sourceSets {
                main {
                    java { srcDir  'src' }
                    resources { srcDir 'resources'}
                }
                test {
                    java { srcDir 'tests/src' }
                    resources { srcDir 'tests/resources'}
                }
            }

            group='fr.ensta-bretagne'
            version = project.plug.version

            // Sets Java compile option to use UTF-8 encoding
            compileJava.options.encoding = 'UTF-8'

            // Declares repositories to refer to for all projects

            // Repositories aren't transitives from a project to another
            // See gradle issue https://github.com/gradle/gradle/issues/1352
            repositories {
                mavenCentral()

                maven { url "https://dl.bintray.com/plug-obp/maven" }

                // eclipse jars
                maven { url "https://repo.eclipse.org/content/groups/releases"}

                //need this for petitparser in tuml-interpreter (gradle bug: https://issues.gradle.org/browse/GRADLE-1940)
                maven { url = 'https://jitpack.io' }
            }

            /*
            // No sources yet
            Jar scJar = task('sourceJar', type: Jar) {
                from sourceSets.main.allSource
            }
            */

            // Alls tests depends on junit 4
            dependencies {
                testCompile group: 'junit', name: 'junit', version: '4.+'
            }

            // Publication configuration
            publishing {
                repositories {
                    maven { url project.plug.repository }
                }
                publications {
                    mavenJava(MavenPublication) {
                        from components.java
                        //artifact(scJar) { classifier = 'sources' }
                    }
                }
            }
        }

    }
}

