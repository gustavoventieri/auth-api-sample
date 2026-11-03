############################
# Build
############################
FROM eclipse-temurin:21.0.13_9-jdk-alpine-3.24 AS builder
WORKDIR /build

COPY framework/mvnw ./mvnw
COPY framework/.mvn ./.mvn
COPY pom.xml ./
COPY core/pom.xml core/
COPY framework/pom.xml framework/
COPY core/src/main core/src/main
COPY framework/src/main framework/src/main

RUN chmod +x mvnw \
    && ./mvnw -B -q clean package -DskipTests \
    && cp framework/target/framework-*.jar /build/app.jar

############################
# Runtime
############################
FROM eclipse-temurin:21.0.13_9-jre-alpine-3.24 AS runner

# Aplica os patches de segurança do Alpine e remove o gnupg, que não é usado em runtime.
RUN apk upgrade --no-cache \
    && apk del --no-cache gnupg

# Usuário de sistema sem shell, sem home e com UID/GID fixos (compatível com runAsNonRoot).
RUN addgroup -S -g 10001 appuser \
    && adduser -S -D -H -u 10001 -G appuser -h /nonexistent -s /sbin/nologin appuser

WORKDIR /app

# O jar pertence ao root (0644): o processo da aplicação só consegue lê-lo.
COPY --from=builder /build/app.jar /app/app.jar

USER 10001:10001

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-XX:+ExitOnOutOfMemoryError", "-jar", "/app/app.jar"]
