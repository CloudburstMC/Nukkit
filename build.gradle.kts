project.version = "1.0-SNAPSHOT"

application {
    mainClassName = "cn.nukkit.Nukkit"
}

plugins {
    application
    maven
    `maven-publish`

    id("com.github.johnrengelman.shadow") version "5.2.0"
    id("io.freefair.lombok") version "4.1.6"
}

repositories {
    mavenCentral()
    jcenter()
    maven(url = "https://plugins.gradle.org/m2/")
    maven(url = "https://repo.nukkitx.com/maven-releases")
    maven(url = "https://repo.nukkitx.com/maven-snapshots")
}

object jline {
    val version = "3.13.3"
}

object log4j2 {
    val version = "2.13.0"
}

object junit {
    object jupiter {
        val version  = "5.6.0-M1"
    }
}

object netty {
    val version = "4.1.44.Final"
}

val implementations = mapOf(
	"com.nukkitx:fastutil-lite" to "8.1.1",
	"com.google.guava:guava" to "28.2-jre",
	"com.google.code.gson:gson" to "2.8.6",
	"org.yaml:snakeyaml" to "1.25",
	"org.iq80.leveldb:leveldb" to "0.11-SNAPSHOT",
	"io.netty:netty-handler" to netty.version,
	"io.netty:netty-transport-native-epoll" to netty.version,
	"com.nimbusds:nimbus-jose-jwt" to "8.4",
	"org.apache.logging.log4j:log4j-api" to log4j2.version,
	"org.apache.logging.log4j:log4j-core" to log4j2.version,
	"net.sf.jopt-simple:jopt-simple" to "5.0.4",
	"net.minecrell:terminalconsoleappender" to "1.2.0",
	"org.jline:jline-terminal" to jline.version,
	"org.jline:jline-terminal-jna" to jline.version,
	"org.jline:jline-reader" to jline.version
)

dependencies {
    implementations.forEach{ implementation("${it.key}:${it.value}") }
    compileOnly("com.github.edwgiz:maven-shade-plugin.log4j2-cachefile-transformer:2.8.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${junit.jupiter.version}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junit.jupiter.version}")
}

/* Free config below */

val jvmPlatformTarget = JavaVersion.VERSION_1_8

tasks {
    withType<JavaCompile> {
        sourceCompatibility = jvmPlatformTarget.toString()
        targetCompatibility = jvmPlatformTarget.toString()
        options.encoding = "UTF-8"
    }
    withType<Javadoc> {
        options.encoding = "UTF-8"
    }
    withType<Test> {
        useJUnitPlatform()
    }
    shadowJar {
        transform(com.github.jengelman.gradle.plugins.shadow.transformers.Log4j2PluginsCacheFileTransformer())
    }
}
