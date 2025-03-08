import com.vanniktech.maven.publish.SonatypeHost

plugins {
    `java-library`
    id("com.vanniktech.maven.publish") version "0.30.0"
    signing
}

group = "org.readutf.buildstore"
version = "1.0.0"

repositories {
    mavenCentral()
}

subprojects {

    group = rootProject.group
    version = rootProject.version

    apply(plugin = "java")
    apply(plugin = "com.vanniktech.maven.publish")

    java {
        withSourcesJar()
    }

    tasks.test {
        useJUnitPlatform()
    }
    repositories {
        mavenCentral()
    }

    if (name != "server") {
        mavenPublishing {

            coordinates(
                groupId = group.toString(),
                version = version.toString(),
                artifactId = name,
            )

            pom {
                name.set("Hermes")
                description.set("Build storage system for minecraft schematics")
                inceptionYear.set("2025")

                url.set("https://github.com/utfunderscore/buildstore")
                licenses {
                    license {
                        name.set("GPLv3")
                        url.set("https://www.gnu.org/licenses/gpl-3.0.html")
                        distribution.set("https://www.gnu.org/licenses/gpl-3.0.html")
                    }
                }
                developers {
                    developer {
                        id.set("utfunderscore")
                        name.set("utfunderscore")
                        url.set("utf.lol")
                    }
                }
                scm {
                    url.set("https://github.com/utfunderscore/buildstore/")
                    connection.set("scm:git:git://github.com/utfunderscore/buildstore.git")
                    developerConnection.set("scm:git:ssh://git@github.com/utfunderscore/buildstore.git")
                }
            }

            publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
            signAllPublications()
        }

        signing {
            sign(publishing.publications)
        }
    }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("com.h2database:h2:2.3.232")
    testImplementation(project(":api"))

    compileOnly(project(":api"))

    // jdbc h2

    api("com.j256.ormlite:ormlite-jdbc:6.1")
}

tasks.test {
    useJUnitPlatform()
}
