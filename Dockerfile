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

# Copiar código fuente
COPY src src

# Compilar (saltar tests para velocidad)
RUN chmod +x ./mvnw
RUN ./mvnw clean package -DskipTests

# Etapa 2: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copiar el JAR generado
COPY --from=build /app/target/*.jar app.jar

# Limitar memoria de JVM para el plan Free (512MB)
ENV JAVA_OPTS="-Xmx256m -Xms128m"

# Puerto expuesto
EXPOSE 8080

# Comando de inicio con opciones de memoria
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]