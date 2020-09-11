/*
 * Copyright (C) 2020  Rosetta Roberts <rosettafroberts@gmail.com>
 *
 * This file is part of VettingBot.
 *
 * VettingBot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VettingBot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with VettingBot.  If not, see <https://www.gnu.org/licenses/>.
 */

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.4.0-M2"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
    id("com.github.ben-manes.versions") version "0.31.0"
    kotlin("jvm") version "1.4.10"
    kotlin("plugin.spring") version "1.4.10"
    id("com.google.cloud.tools.jib") version "2.5.0"
}

group = "vettingbot"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    maven { setUrl("https://oss.sonatype.org/content/repositories/snapshots") }
    maven { setUrl("https://repo.spring.io/milestone") }
    mavenCentral()
    maven { setUrl("https://jitpack.io") }
    jcenter()
}

dependencies {
//    implementation(platform("io.projectreactor:reactor-bom:2020.0.0-M2"))
    implementation("com.discord4j:discord4j-core:3.2.0-SNAPSHOT")
    implementation("org.liquigraph:liquigraph-core:4.0.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("net.bytebuddy:byte-buddy-agent")
    implementation("org.neo4j.springframework.data:spring-data-neo4j-rx-spring-boot-starter:1.1.1")
    implementation("io.github.microutils:kotlin-logging:1.8.3")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.springframework.boot:spring-boot-starter-logging")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict", "-Xinline-classes", "-Xopt-in=kotlin.ExperimentalUnsignedTypes")
        jvmTarget = "11"
    }
}

jib {
    from {
        image = "adoptopenjdk/openjdk11:alpine"
    }
    to {
        image = "thenumeralone/vettingbot:$version"
        tags = setOf(
            if ((version as String).endsWith("SNAPSHOT")) {
                "latest-snapshot"
            } else {
                "latest"
            }
        )
        val usr = findProperty("dockerUsername") as? String
        val pswd = findProperty("dockerPassword") as? String
        if (usr != null && pswd != null) {
            auth {
                username = usr
                password = pswd
            }
        }
    }
    extraDirectories {
        setPaths("src/main/jib")
        permissions = mapOf(
            "/wait-for" to "755"
        )
    }
}
