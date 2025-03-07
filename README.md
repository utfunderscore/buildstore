<h1 align="center">
<img width="300px" src="https://p84.cooltext.com/Rendered/Cool%20Text%20-%20buildstore%20477568402573791.png" />

[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
</h1>


## What is buildstore?
Buildstore allows you to save, upload, and track the history your schematics. It is intended for use on minigame servers for dynamic map loading, so that updates
and changes can be done on a seperate buildData server.

## Supported Remotes
 * Local filesystem
 * Postgres
 * AWS S3 (And any other s3 API compatible services)

## Adding it to your project
The API for loading and saving schematics is available on **Maven Central** for use in your projects.

**Kotlin Gradle DSL**
```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("org.readutf.buildstore:api:1.0.0")
}
```

**Maven**
```xml
<dependency>
    <groupId>org.readutf.buildstore</groupId>
    <artifactId>api</artifactId>
    <version>1.0.0</version>
</dependency>
```

[contributors-shield]: https://img.shields.io/github/contributors/utfunderscore/buildstore.svg
[contributors-url]: https://github.com/utfunderscore/buildstore/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/utfunderscore/buildstore.svg
[forks-url]: https://github.com/utfunderscore/buildstore/network/members
[stars-shield]: https://img.shields.io/github/stars/utfunderscore/buildstore.svg
[stars-url]: https://github.com/utfunderscore/buildstore/stargazers
[issues-shield]: https://img.shields.io/github/issues/utfunderscore/buildstore.svg
[issues-url]: https://github.com/utfunderscore/buildstore/issues