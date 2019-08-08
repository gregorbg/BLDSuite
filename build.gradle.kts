import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.31"
    `maven-publish`
}

group = "com.suushiemaniac"
version = "3.0"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation(kotlin("stdlib"))

    api("com.suushiemaniac:cubing.alglib:2.0")
    api("org.worldcubeassociation.tnoodle:tnoodle-scrambles:0.15.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.2.1")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    create<Jar>("sourcesJar") {
        archiveClassifier.set("sources")
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
