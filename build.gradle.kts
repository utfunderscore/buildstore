import com.vanniktech.maven.publish.SonatypeHost

plugins {
    id("java")
    id("com.vanniktech.maven.publish") version "0.30.0"
    signing
}

group = "org.readutf.buildstore"
version = "1.1.0"

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

    mavenPublishing {

        coordinates(
            groupId = group.toString(),
            version = version.toString(),
            artifactId = name,
        )

        publishing {
            repositories {
                maven {
                    name = "utfRepoReleases"
                    // or when a separate snapshot repository is required
                    url =
                        uri(
                            if (version.toString().endsWith(
                                    "SNAPSHOT",
                                )
                            ) {
                                "https://mvn.utf.lol/snapshots"
                            } else {
                                "https://mvn.utf.lol/releases"
                            },
                        )
                    credentials(PasswordCredentials::class)
                }
            }
        }

        pom {
            name.set("Hermes")
            description.set("A simple, lightweight, and easy-to-use networking library for Kotlin.")
            inceptionYear.set("2024")

            url.set("https://github.com/utfunderscore/hermes")
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
                url.set("https://github.com/utfunderscore/hermes/")
                connection.set("scm:git:git://github.com/utfunderscore/hermes.git")
                developerConnection.set("scm:git:ssh://git@github.com/utfunderscore/hermes.git")
            }
        }

        publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
        signAllPublications()
    }

    signing {
        sign(publishing.publications)
    }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}
