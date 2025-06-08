plugins {
    java
    `java-library` // 添加 java-library 插件用于 API 模块
}

group = "cn.fandmc.flametech"
version = "0.1.3"

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

dependencies {
    // API 模块的依赖
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    api("com.github.TechnicallyCoded:FoliaLib:0.4.4") // 使用 api 而不是 implementation
}

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

tasks.test {
    useJUnitPlatform()
}