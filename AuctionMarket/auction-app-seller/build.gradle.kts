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

    // 개발 편의성
    developmentOnly("org.springframework.boot:spring-boot-devtools")
}