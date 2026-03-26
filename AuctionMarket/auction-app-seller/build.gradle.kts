plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")

    // 💡 [핵심] 코틀린과 스프링 연동을 위한 필수 플러그인
    kotlin("jvm")
    kotlin("plugin.spring")
}

dependencies {
    implementation(project(":auction-common"))
    implementation(project(":auction-domain"))
    implementation(project(":auction-infra"))

    // Web & Security
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-security-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // jpa
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // 개발 편의성
    developmentOnly("org.springframework.boot:spring-boot-devtools")
}