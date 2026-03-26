dependencies {
    implementation(project(":auction-domain"))
    implementation(project(":auction-common"))

    // PostgreSQL 도입
    runtimeOnly("org.postgresql:postgresql")

    // Redis & Redisson 캐싱
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.redisson:redisson:3.42.0")

    // Oracle S3
    implementation(platform("software.amazon.awssdk:bom:2.20.0"))
    implementation("software.amazon.awssdk:s3")

    // Oracle
    implementation("com.oracle.database.jdbc:ojdbc11")

    // WebFlux (외부 API 호출, Toss 결제 등 WebClient용)
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // Mail
    implementation("org.springframework.boot:spring-boot-starter-mail")

    // AI Embedding
    implementation("dev.langchain4j:langchain4j-embeddings-all-minilm-l6-v2-q:0.33.0")

    // MyBatis
    implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:3.0.3")

    // security
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")

    // jwt
    implementation("io.jsonwebtoken:jjwt-api:0.12.3")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.3")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.3")
}