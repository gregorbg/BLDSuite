import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.20"
    `maven-publish`
}

group = "com.suushiemaniac"
version = "2.0"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    runtime(kotlin("stdlib"))

    compile("com.suushiemaniac:cubing.alglib:2.0+")
    compile("net.gnehzr:tnoodle-scrambles:0.13.5")

    compile("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.0.0")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    create<Jar>("sourcesJar") {
        classifier = "sources"
        from(sourceSets["main"].allSource)
        dependsOn("classes")
    }
}

publishing.publications {
    create<MavenPublication>("mavenJava") {
        from(components["java"])
        artifact(tasks["sourcesJar"])
    }
}

kotlin.sourceSets {
    forEach {
        it.kotlin.exclude("Main.kt")
    }
}