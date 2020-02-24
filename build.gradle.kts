import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.61"
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
    api("org.worldcubeassociation.tnoodle:lib-scrambles:0.17.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.3")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    configure<JavaPluginExtension> {
        withSourcesJar()
    }
}

publishing.publications {
    create<MavenPublication>("mavenJava") {
        from(components["java"])
    }
}
