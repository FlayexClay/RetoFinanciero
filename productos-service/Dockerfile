
# Etapa 1: Build (Compilación con Maven)
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Copiar archivos de configuración de Maven primero (para aprovechar cache)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar código fuente y compilar
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Runtime (Imagen ligera solo con JRE)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Metadata del contenedor
LABEL maintainer="Sistema Financiero"
LABEL service="productos-service"
LABEL version="1.0.0"

# Instalar wget para healthcheck
RUN apk add --no-cache wget

# Copiar el JAR compilado desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Crear directorio para logs
RUN mkdir -p /logs && \
    chmod 755 /logs

# Exponer puerto del servicio
EXPOSE 8082

# Variables de entorno por defecto
ENV JAVA_OPTS="-Xms256m -Xmx512m" \
    SERVER_PORT=8082 \
    SPRING_PROFILES_ACTIVE=docker \
    DB_HOST=postgres-productos \
    DB_PORT=5432 \
    DB_NAME=productos_db \
    DB_USER=postgres \
    DB_PASSWORD=postgres123

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8082/actuator/health || exit 1

# Ejecutar la aplicación
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]