# ===========================================
# DOCKERFILE PARA SPRING BOOT + RENDER
# ===========================================

# Etapa 1: Build de la aplicación
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Copiar archivos de Maven
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Descargar dependencias primero (cache)
RUN chmod +x ./mvnw
RUN ./mvnw dependency:go-offline -B

# Copiar código fuente
COPY src src

# Compilar (saltar tests para velocidad)
RUN ./mvnw clean package -DskipTests -B

# Etapa 2: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copiar el JAR generado
COPY --from=build /app/target/*.jar app.jar

# Puerto expuesto
EXPOSE 8080

# Optimizaciones JVM para arranque rápido y poca memoria
ENV JAVA_OPTS="-Xmx256m -Xms128m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

# Comando de inicio
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT} -jar app.jar"]