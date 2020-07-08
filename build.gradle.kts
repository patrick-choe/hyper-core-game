/*
 * Copyright (C) 2020 PatrickKR
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 * Contact me on <mailpatrickkr@gmail.com>
 */

plugins {
    `maven-publish`
    signing
    kotlin("jvm") version "1.3.72"
    id("org.jetbrains.dokka") version "0.10.0"
    id("com.github.johnrengelman.shadow") version "5.2.0"
}

allprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    group = "com.github.patrick-mc"
    version = "0.4-beta"

    repositories {
        maven("https://repo.maven.apache.org/maven2/")
        maven("https://dl.bintray.com/kotlin/dokka")
        maven("https://jitpack.io/")
    }

    dependencies {
        compileOnly(kotlin("stdlib-jdk8"))
        compileOnly("com.github.noonmaru:custom-entity-bukkit:1.0")
        compileOnly("com.github.noonmaru:tap:1.0.1")
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
    apply(plugin = "signing")

    repositories {
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        if (project.name != "api") mavenLocal()
    }

    dependencies {
        if (project.name != "api") implementation(project(":api"))
    }

    if (project.name != "api") {
        tasks.forEach { task ->
            if (task.name != "clean") {
                task.onlyIf {
                    gradle.taskGraph.hasTask(":shadowJar")
                }
            }
        }
    }
}

dependencies {
    subprojects {
        implementation(this)
    }
}

tasks {
    shadowJar {
        archiveClassifier.set("dist")
    }

    create<Copy>("distJar") {
        from(shadowJar)
        into("W:\\Servers\\1.12.2\\plugins")
    }
}