plugins {
    `maven-publish`
    signing
    kotlin("jvm") version "1.3.71"
    id("org.jetbrains.dokka") version "0.10.0"
    id("com.github.johnrengelman.shadow") version "5.2.0"
}

allprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    group = "com.github.patrick-mc"
    version = "0.1-SNAPSHOT"

    repositories {
        maven("https://repo.maven.apache.org/maven2/")
        maven("https://dl.bintray.com/kotlin/dokka")
    }

    dependencies {
        compileOnly(kotlin("stdlib-jdk8"))
    }

    tasks {
        compileKotlin {
            kotlinOptions.jvmTarget = "1.8"
        }
    }
}

subprojects {
    apply(plugin = "org.jetbrains.dokka")
    apply(plugin = "maven-publish")

    repositories {
        mavenLocal()
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://jitpack.io/")
    }

    dependencies {
        if (project.name != "api") implementation(project(":api"))
        compileOnly("com.github.noonmaru:tap:1.0.1")
        compileOnly("com.github.noonmaru:custom-entity-bukkit:1.0")
    }

    tasks {
        dokka {
            outputFormat = "javadoc"
            outputDirectory = "$buildDir/dokka"

            configuration {
                includeNonPublic = true
                jdkVersion = 8
            }
        }

        create<Jar>("dokkaJar") {
            archiveClassifier.set("javadoc")
            from(dokka)
            dependsOn(dokka)
        }

        create<Jar>("sourcesJar") {
            archiveClassifier.set("sources")
            from(sourceSets["main"].allSource)
        }
    }

    if (project.name != "api") {
        tasks.forEach { task ->
            if (task.name != "clean") {
                task.onlyIf {
                    gradle.taskGraph.hasTask(":spigotJar")
                }
            }
        }
    }

    try {
        publishing {
            publications {
                create<MavenPublication>("hyperCoreGame") {
                    from(components["java"])

                    artifact(tasks["sourcesJar"])
                    artifact(tasks["dokkaJar"])

                    repositories {
                        mavenLocal()

                        maven {
                            name = "central"

                            credentials {
                                username = project.property("centralUsername").toString()
                                password = project.property("centralPassword").toString()
                            }

                            val releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
                            val snapshotsRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots/")
                            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
                        }
                    }

                    pom {
                        name.set("hyper-core-game")
                        description.set("A sample plugin for spigot 1.12.2")
                        url.set("https://github.com/patrick-mc/hyper-core-game")

                        licenses {
                            license {
                                name.set("GNU General Public License v2.0")
                                url.set("https://opensource.org/licenses/gpl-2.0.php")
                            }
                        }

                        developers {
                            developer {
                                id.set("patrick-mc")
                                name.set("PatrickKR")
                                email.set("mailpatrickkorea@gmail.com")
                                url.set("https://github.com/patrick-mc")
                                roles.addAll("developer")
                                timezone.set("Asia/Seoul")
                            }
                        }

                        scm {
                            connection.set("scm:git:git://github.com/patrick-mc/hyper-core-game.git")
                            developerConnection.set("scm:git:ssh://github.com:patrick-mc/hyper-core-game.git")
                            url.set("https://github.com/patrick-mc/hyper-core-game")
                        }
                    }
                }
            }
        }

        signing {
            isRequired = true
            sign(tasks["jar"], tasks["sourcesJar"], tasks["dokkaJar"])
            sign(publishing.publications["hyperCoreGame"])
        }
    } catch (e: groovy.lang.MissingPropertyException) {}
}

dependencies {
    subprojects {
        implementation(this)
    }
}

tasks {
    create<Copy>("spigotJar") {
        from(shadowJar)
        into("W:\\Servers\\gamecon (1.12.2)\\plugins")
    }
}