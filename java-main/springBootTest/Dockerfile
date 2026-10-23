# ---------- 构建阶段：用 Maven 镜像打包 ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# 先拷 pom.xml 单独下载依赖，利用 Docker 层缓存（源码变动时不重复下载）
COPY pom.xml .
RUN mvn -q -B dependency:go-offline

COPY src ./src
RUN mvn -q -B package -DskipTests

# ---------- 运行阶段：只保留 JRE 和 jar ----------
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/springBootTest-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
