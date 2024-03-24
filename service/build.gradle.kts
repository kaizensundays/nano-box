import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("org.springframework.boot") version "2.7.18"
    id("com.jfrog.artifactory") version "4.29.0"
    id("module.publication")
}

group = "com.kaizensundays.fusion"
version = "0.0.1-SNAPSHOT"

dependencies {

    implementation(project(":lib2"))

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit5"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Test> {
    useJUnitPlatform()
    filter {
        excludeTestsMatching("*RemoteTest")
    }
}

tasks.bootJar {
    archiveFileName.set("service.jar")
    destinationDirectory.set(file("bin"))
}

tasks.publish {
    dependsOn("assemble")
}

configure<PublishingExtension> {
    publications {
        create<MavenPublication>("bootJava") {
            from(components["java"])
            artifact(tasks.bootJar) {
                artifactId = "nano-box"
            }
        }
    }
}

publishing {
    repositories {
        mavenLocal()
    }
}

artifactory {
    setContextUrl(project.properties["artifactory.url"] as String)
    publish {
        repository {
            setRepoKey("libs-snapshot-local")
            setUsername(project.properties["artifactory.username"] as String)
            setPassword(project.properties["artifactory.password"] as String)
            setMavenCompatible(true)
        }
        defaults {
            publications("bootJava")
            setPublishArtifacts(true)
            setPublishPom(true)
        }
    }
}
