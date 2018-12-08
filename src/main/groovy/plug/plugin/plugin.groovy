package plug.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.GradleBuild

/**
 */
class PlugExtension {
    String version = "0.0.7"

    String bintrayOrg = 'plug-obp'
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
            apply plugin: "com.jfrog.bintray"

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

            def localRepository=System.getenv('PLUG_REPOSITORY')
            if (localRepository == null) {
                localRepository = "$project.buildDir/repository"
            }

            // Repositories aren't transitives from a project to another
            // See gradle issue https://github.com/gradle/gradle/issues/1352
            repositories {
                mavenCentral()

                maven { url "https://dl.bintray.com/plug-obp/maven" }

                // eclipse jars
                maven { url "https://repo.eclipse.org/content/groups/releases" }

                //need this for petitparser in tuml-interpreter (gradle bug: https://issues.gradle.org/browse/GRADLE-1940)
                maven { url = 'https://jitpack.io' }

                maven { url = localRepository }
            }

            /*
            // No sources yet
            Jar scJar = task('sourceJar', type: Jar) {
                from sourceSets.main.allSource
            }
            */

            // Alls tests depends on junit 4
            dependencies {
                testCompile group: 'junit', name: 'junit', version: '4.7'
            }
            //println "Local repository configured to: $localRepository"
            // Publication configuration
            publishing {
                publications {
                    mavenJava(MavenPublication) {
                        from components.java
                        //artifact(scJar) { classifier = 'sources' }
                    }
                }
                repositories {
                    maven {
                        url = localRepository
                    }
                }
            }

            bintray {
                user = project.hasProperty('bintrayUser') ? project.property('bintrayUser') : System.getenv('BINTRAY_USER')
                key = project.hasProperty('bintrayApiKey') ? project.property('bintrayApiKey') : System.getenv('BINTRAY_API_KEY')

                publications = ['mavenJava']

                pkg {
                    name = project.group
                    repo = 'maven'
                    userOrg = project.plug.bintrayOrg
                    desc = 'Plug artifacts'
                    vcsUrl = 'https://github.com/plug-obp'
                    websiteUrl = 'http://plug-obp.github.io'
                    licenses = ['MIT']
                    publicDownloadNumbers = true
                    override = true
                    publish = true

                    //Optional version descriptor
                    version {
                        name = project.version //Bintray logical version name
                    }
                }
            }

        }

        project.task('cleanAll', type: GradleBuild) {
            tasks = [ 'clean' ]
        }

        project.task('buildAll', type: GradleBuild) {
            tasks = [ 'build' ]
        }

        project.task('bintrayUploadAll', type: GradleBuild) {
            tasks = [ 'bintrayUpload' ]
        }

        project.task('publishAll', type: GradleBuild) {
            tasks = [ 'publish' ]
        }

        project.task('assembleAll', type: GradleBuild) {
            tasks = [ 'assemble' ]
        }

        project.task('testAll', type: GradleBuild) {
            tasks = [ 'test' ]
        }

    }
}

