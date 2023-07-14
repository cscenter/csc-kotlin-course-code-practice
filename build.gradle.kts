
plugins {
    kotlin("jvm") version "1.8.21"
    id("me.champeau.jmh") version "0.7.1"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = uri("https://plugins.gradle.org/m2/")
    }
}


dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.2")

    val jmhVersion = "1.36"
    implementation("org.openjdk.jmh:jmh-core:$jmhVersion")
    implementation("org.openjdk.jmh:jmh-generator-annprocess:$jmhVersion")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}
