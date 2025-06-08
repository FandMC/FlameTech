plugins {
    java
    id("com.gradleup.shadow") version "9.0.0-beta13"
}

repositories {
    mavenCentral()
    maven {
        name = "papermc-repo"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
    maven {
        name = "jitpack"
        url = uri("https://jitpack.io")
    }
}

group = "cn.fandmc"
version = "0.1.3"

val targetJavaVersion = 21
java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
        options.release.set(targetJavaVersion)
    }
}

// 配置根项目的依赖，包含所有子模块
dependencies {
    // 将 server 模块作为实现依赖，这样会自动包含 api 模块
    implementation(project(":server"))
}

// 配置 shadowJar 任务
tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    // 重定位 FoliaLib 包避免冲突
    relocate("com.tcoded.folialib", "cn.fandmc.flametech.libs.folialib")
    archiveClassifier.set("")
    exclude("org/jetbrains/**")
    exclude("org/intellij/**")
    exclude("META-INF/maven/org.jetbrains.annotations/**")
}

// 让 build 任务依赖 shadowJar
tasks.build {
    dependsOn(tasks.shadowJar)
}

// 确保子项目先构建
tasks.shadowJar {
    dependsOn(":api:build", ":server:build")
}