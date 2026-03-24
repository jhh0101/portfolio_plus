plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":auction-common"))
    implementation(project(":auction-domain"))
    implementation(project(":auction-infra"))

    // Web & Security
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")

    // OAuth2 Client (소셜 로그인이 필요한 User 모듈 등에만 추가)
    implementation("org.springframework.boot:spring-boot-starter-security-oauth2-client")

    // 개발 편의성
    developmentOnly("org.springframework.boot:spring-boot-devtools")
}