plugins {
    `java-library`
    `maven-publish`
    signing

    id("org.ajoberstar.grgit") version "5.2.2"

    id("io.spring.dependency-management") version "1.1.6"
    id("io.freefair.lombok") version "8.10"
}

group = "systems.fehn"
version = grgit.describe(mapOf("tags" to true))
val isRelease = !version.toString().substringAfterLast('-').startsWith('g')

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.springframework.boot:spring-boot-dependencies:3.3.2"))

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")

    implementation("com.fasterxml.jackson.core:jackson-databind")

    api("org.hashids:hashids:1.0.3")
}

java.withSourcesJar()
java.withJavadocJar()

tasks.jar {
    enabled = true
}

tasks.register("version") {
    doLast {
        println(version)
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            version = if (isRelease) project.version.toString() else "${project.version}-SNAPSHOT"
            from(components["java"])
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
            pom {
                name.set("Hashids Spring Boot Starter")
                description.set("Use Hashids with Spring Boot")
                url.set("https://github.com/fehnomenal/hashids-spring-boot-starter")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://www.opensource.org/licenses/mit-license.php")
                    }
                }
                developers {
                    developer {
                        id.set("afehn")
                        name.set("Andreas Fehn")
                        email.set("fehnomenal@fehn.systems")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/fehnomenal/hashids-spring-boot-starter.git")
                    developerConnection.set("scm:git:ssh://github.com:fehnomenal/hashids-spring-boot-starter.git")
                    url.set("https://github.com/fehnomenal/hashids-spring-boot-starter")
                }
            }
        }
    }
    repositories {
        mavenLocal()
        maven {
            url = if (isRelease) {
                uri("https://oss.sonatype.org/service/local/staging/deploy/maven2")
            } else {
                uri("https://oss.sonatype.org/content/repositories/snapshots")
            }

            credentials {
                username = project.property("ossrh.username").toString()
                password = project.property("ossrh.password").toString()
            }
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}