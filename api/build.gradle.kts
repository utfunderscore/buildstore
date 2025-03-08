plugins {
    `java-library`
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
