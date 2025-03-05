import com.vanniktech.maven.publish.SonatypeHost

plugins {
    `java-library`
    id("com.vanniktech.maven.publish") version "0.30.0"
    id("signing")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    compileOnly("com.google.code.gson:gson:2.12.1")
    api("org.jspecify:jspecify:1.0.0")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

mavenPublishing {

    coordinates(
        groupId = group.toString(),
        version = version.toString(),
        artifactId = name,
    )

    pom {
        name.set("Hermes")
        description.set("A simple, lightweight, and easy-to-use networking library for Kotlin.")
        inceptionYear.set("2024")

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

tasks.test {
    useJUnitPlatform()
}

tasks {
    compileJava {
        options.release = 21
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
}
