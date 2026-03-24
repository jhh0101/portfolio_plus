dependencies {
    // 공통으로 쓰이는 Validation
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // 공통으로 쓰이는 JWT 유틸리티
    implementation("io.jsonwebtoken:jjwt-api:0.12.3")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.3")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.3")
}