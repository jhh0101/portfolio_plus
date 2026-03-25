// auction-domain/build.gradle.kts
plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    kotlin("kapt")
}

dependencies {
    implementation(project(":auction-common"))
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // Redis & Redisson
    implementation("org.redisson:redisson:3.42.0")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    // --- Querydsl 필수 설정 ---
    // 1. 라이브러리 (jakarta 버전 사용)
    implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")

    // 2. Kapt 설정
    kapt(platform("org.springframework.boot:spring-boot-dependencies:4.0.0"))

    kapt("com.querydsl:querydsl-apt:5.0.0:jakarta")
    kapt("jakarta.annotation:jakarta.annotation-api")
    kapt("jakarta.persistence:jakarta.persistence-api")
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}
tasks.getByName<Jar>("jar") {
    enabled = true
}