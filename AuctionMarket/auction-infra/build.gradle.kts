dependencies {
    implementation(project(":auction-domain"))

    // PostgreSQL 도입
    runtimeOnly("org.postgresql:postgresql")

    // Redis & Redisson 캐싱
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.redisson:redisson:3.42.0")

    // AWS S3
    implementation(platform("software.amazon.awssdk:bom:2.20.0"))
    implementation("software.amazon.awssdk:s3")

    // WebFlux (외부 API 호출, Toss 결제 등 WebClient용)
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // Mail
    implementation("org.springframework.boot:spring-boot-starter-mail")

    // AI Embedding
    implementation("dev.langchain4j:langchain4j-embeddings-all-minilm-l6-v2-q:0.33.0")

    // MyBatis (JPA로 완전 전환이 아니라면 유지, 만약 안 쓴다면 삭제)
    implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:3.0.3")
}