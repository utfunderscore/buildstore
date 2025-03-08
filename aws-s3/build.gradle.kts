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

    compileOnly(project(":api"))

    api("software.amazon.awssdk:s3:2.25.14")
    api("software.amazon.awssdk:aws-core:2.25.14")
}

tasks.test {
    useJUnitPlatform()
}
