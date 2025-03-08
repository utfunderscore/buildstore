plugins {
    `java-library`
}

group = "org.readutf.buildstore"
version = "1.0.0"

repositories {
    mavenCentral()
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
