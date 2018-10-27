import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.Coroutines

plugins {
    kotlin("jvm") version "1.2.71"
    `maven-publish`
}

group = "com.suushiemaniac"
version = "2.0"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    runtime(kotlin("stdlib-jdk8"))

    compile("com.suushiemaniac:cubing.alglib:2.0+")
    compile("com.suushiemaniac:lang.json:2.0+")
    compile("net.gnehzr:tnoodle-scrambles:0.13.5")

    compile("org.jetbrains.kotlinx:kotlinx-coroutines-core:0.30+")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    create<Jar>("sourcesJar") {
        classifier = "sources"
        from(java.sourceSets["main"].allSource)
        dependsOn("classes")
    }
}

publishing.publications {
    create<MavenPublication>("mavenJava") {
        from(components["java"])
        artifact(tasks["sourcesJar"])
    }
}

kotlin {
    experimental.coroutines = Coroutines.ENABLE

    sourceSets {
        getByName("main") {
            kotlin.exclude("Main.kt")
        }
    }
}