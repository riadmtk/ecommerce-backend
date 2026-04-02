# ===== ÉTAPE 1 : BUILD =====
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Copier le pom.xml en premier pour profiter du cache Docker
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copier le code source et builder
COPY src ./src
RUN mvn clean package -DskipTests

# ===== ÉTAPE 2 : RUN =====
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Créer un utilisateur non-root pour la sécurité
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copier le jar depuis l'étape de build
COPY --from=builder /app/target/*.jar app.jar

# Port exposé
EXPOSE 8086

# Lancement
ENTRYPOINT ["java", "-jar", "app.jar"]
