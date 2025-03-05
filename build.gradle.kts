plugins {
    id("java")
}

group = "org.readutf.buildstore"
version = "1.0.0"

repositories {
    mavenCentral()
}

subprojects {

    group = rootProject.group
    version = rootProject.version
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}
