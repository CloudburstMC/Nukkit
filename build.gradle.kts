import com.github.jengelman.gradle.plugins.shadow.transformers.Log4j2PluginsCacheFileTransformer
import org.gradle.internal.impldep.org.junit.experimental.categories.Categories.CategoryFilter.exclude

@Suppress("DSL_SCOPE_VIOLATION") // https://youtrack.jetbrains.com/issue/IDEA-262280

plugins {
    id("java-library")
    id("maven-publish")
    id("application")
    alias(libs.plugins.shadow)
    alias(libs.plugins.git)
}

group = "cn.nukkit"
version = "1.0-SNAPSHOT"
description = "Nuclear powered server software for Minecraft Bedrock Edition"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.opencollab.dev/maven-releases")
    maven("https://repo.opencollab.dev/maven-snapshots")
}

dependencies {
    api(libs.network)
    api(libs.epoll)
    api(libs.fastutil)
    api(libs.bundles.fastutilmaps)
    api(libs.guava)
    api(libs.gson)
    api(libs.snakeyaml)
    api(libs.leveldb)
    api(libs.leveldbjni) {
        exclude(group = "com.google.guava", module = "guava")
        exclude(group = "io.netty", module = "netty-buffer")
        exclude(group = "org.iq80.snappy", module = "snappy")
        exclude(group = "org.iq80.leveldb", module = "leveldb")
    }
    api(libs.snappy)
    api(libs.jwt)
    api(libs.bundles.terminal)
    api(libs.bundles.log4j)
    api(libs.jopt.simple)
    api(libs.blockstateupdater)
    api(libs.lmbda) {
        exclude(group = "org.checkerframework", module = "checker-qual")
    }
    api(libs.noise) {
        exclude(group = "net.daporkchop.lib", module = "common")
        exclude(group = "net.daporkchop.lib", module = "math")
    }
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    testImplementation(libs.bundles.junit)
    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

application {
    mainClass.set("cn.nukkit.Nukkit")
}

gitProperties {
    dateFormat = "dd.MM.yyyy '@' HH:mm:ss z"
    failOnNoGitDirectory = false
}

publishing {
    repositories {
        maven {
            name = "opencollab"
            url = uri("https://repo.opencollab.dev/maven-snapshots")
            credentials {
                username = System.getenv("DEPLOY_USERNAME")
                password = System.getenv("DEPLOY_PASSWORD")
            }
        }
    }

    publications {
        create<MavenPublication>("nukkit") {
            artifact(tasks.generateGitProperties) {
                extension = "properties"
            }
            from(components["java"])
        }
    }
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }

    test {
        useJUnitPlatform()
    }

    jar {
        archiveClassifier.set("dev")
    }

    shadowJar {
        manifest.attributes["Multi-Release"] = "true"

        transform(Log4j2PluginsCacheFileTransformer())

        // Backwards compatible jar directory
        destinationDirectory.set(file("$projectDir/target"))
        archiveClassifier.set("")

        exclude("javax/annotation/**")

        // These platforms are not popular, make the jar a bit smaller by not including optional Snappy support
        exclude("org/xerial/snappy/native/AIX/**")
        exclude("org/xerial/snappy/native/FreeBSD/**")
        exclude("org/xerial/snappy/native/SunOS/**")
        exclude("org/xerial/snappy/native/Linux/loongarch64/**")
        exclude("org/xerial/snappy/native/Linux/ppc/**")
        exclude("org/xerial/snappy/native/Linux/ppc64/**")
        exclude("org/xerial/snappy/native/Linux/ppc64le/**")
        exclude("org/xerial/snappy/native/Linux/s390x/**")
    }

    runShadow {
        val dir = File(projectDir, "run")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        standardInput = System.`in`
        workingDir = dir
    }

    javadoc {
        options.encoding = "UTF-8"
    }
}

gitProperties {
    customProperty("github.repo", "CloudburstMC/Nukkit")
}
