package plug.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.compile.JavaCompile
/**
 */
class PlugExtension {
    String version = "0.2"
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
            apply plugin: 'java-library-distribution'
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

            // Sets Java compile option to use UTF-8 encoding
            compileJava.options.encoding = 'UTF-8'
            tasks.withType(JavaCompile) {
                options.encoding = 'UTF-8'
            }

            // Declares repositories to refer to
            repositories {
                mavenCentral()
                maven { url "http://mocs-artefacts.ensta-bretagne.fr/plug-repo/"}

                maven { url = 'http://repository.ops4j.org/maven2/' }
                //needed by javabdd
                maven { url "https://breda.informatik.uni-mannheim.de/nexus/content/repositories/public" }

                // Repositories aren't transitives from a project to another
                // See gradle issue https://github.com/gradle/gradle/issues/1352
                // TODO need a better way to store those jars
                flatDir { dirs rootProject.file('../external-libs/obp/OBP-1.5.1_batch/jars') }

                // UML jars
                ivy {
                    url = 'https://www.mirrorservice.org/sites/download.eclipse.org/eclipseMirror/modeling/mdt/uml2/updates/5.0/R201502020947/plugins/'
                    layout 'pattern', {
                        artifact '[artifact](_[revision])(.[ext])'
                    }
                }

                //need this for petitparser in tuml-interpreter (gradle bug: https://issues.gradle.org/browse/GRADLE-1940)
                maven { url = 'https://jitpack.io' }

                //need this for SpinJa
                ivy {
                    url = 'https://github.com/teodorov/spinja/raw/master'
                    layout 'pattern', {
                        artifact '[artifact].jar'
                    }
                }

                //need this for TLA
                ivy {
                    url = 'https://tla.msr-inria.inria.fr/tlatoolbox/staged/dist/'
                    layout 'pattern', {
                        artifact '[artifact].jar'
                    }
                }

                // For lclockrdl
                maven { url "https://breda.informatik.uni-mannheim.de/nexus/content/repositories/public" }
            }

            // Alls tests depends on junit 4
            dependencies {
                testCompile group: 'junit', name: 'junit', version: '4.+'
            }

            publishing {
                publications {
                    mavenJava(MavenPublication) {
                        from components.java
                    }
                }
            }
        }

        project.allprojects {
            group='fr.ensta-bretagne'
            version = project.plug.version
        }

    }
}

