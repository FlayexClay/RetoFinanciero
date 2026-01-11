# Sistema de Consulta de Información Financiera

## Descripción:

Sistema de microservicios para consulta de información de clientes y sus productos financieros, implementado con Spring Boot, Spring Security OAuth2 (Keycloak), WebFlux y arquitectura reactiva.

### Objetivo del Sistema:

Proporcionar una API REST segura que permita consultar la información completa de un cliente (datos personales y productos financieros) mediante un código único encriptado, con autenticación OAuth2 mediante Keycloak y tracking distribuido.

### Características del Sistema: 

- Arquitectura de Microservicios: 3 Microservicios independientes que cuentan con base de datos en PostgreSQL
- Seguridad OAuth2 con Keycloak: Autenticación mediante servidor OAuth2 externo
- Programación Reactiva: Spring WebFlux y Project Reactor que usamos para alta concurrencia
- Códigos únicos encriptados en Base64 para los Clientes
- Patrón BFF: Backend for Frontend para orquestación de servicios.
- Tracking Distribuido: Header X-Trace-Id para seguimiento de requests.
- Documentación Api: OpenApi 3.0/Swagger UI 
- Loggin Avanzado: Logback con formato JSON y rotación
- Docker Ready: Containerización completa con Docker Compose
- AOP: Loggin y monitoreo mediante Aspect-Oriented Programming
- Java 17: Se uso esta versión de java para la solución.


## Arquitectura

### Microservicios:

1. Keycloak corre en el Puerto 8083
   - Servidor de autenticación OAuth2
   - Gestión de realms, clientes y usuarios
   - Generación y validación de tokens JWT

2. BFF (Backend for Frontend) corre en el Puerto 8080
   - Gateway principal
   - Validación de tokens con Keycloak
   - Desencriptación de codigoUnico
   - Orquestación de llamadas a microservicios
   - Tracking distribuido con Trace ID

3. Cliente Service corre en el Puerto 8081
   - Gestión de información de clientes
   - Base de datos PostgreSQL

4. Productos Financieros Service corre en el Puerto 8082
   - Gestión de productos financieros
   - Base de datos PostgreSQL

### Tecnologías Usadas:

- Java 17
- Spring Boot 3.5.9
- Spring WebFlux
- Spring Security OAuth2 Resource Server
- Keycloak 
- Spring Data JPA
- PostgreSQL
- MapStruct
- Lombok
- Logback
- JUnit 5
- AOP (Aspect-Oriented Programming)
- Docker & Docker Compose
- Swagger/OpenAPI

## Inicio Rápido

- Docker y Docker Compose instalados
- Java 17 JDK
- Maven 3.9.6 
- Postman

## Levantar el sistema completo:

1. Ejecutar el comando docker compose build 
2. Ejecutar el comando docker compose up
3. Ejecutamos docker ps para ver que este corriendo los servicios 
4. Accedemos a las Bases de datos de Cliente: user: postgres ,  password: postgres123 y bd_name: clientes_db
5. Accedemos a las Bases de datos de Productos: user: postgres ,  password: postgres123 y bd_name: productos_db
6. Accedemos a las Bases de datos de KeyCloak: user: postgres ,  password: postgres123 y bd_name: keycloak
7. Ingresamos a la consola de KeyCloak: http://localhost:8083/admin/master/console/  
8. Poner usuario: admin y contraseña: admin
9. Crear Client: client type: OpenId Connect, Client ID: financial-client, Name: Financial BFF, Always display in UI: OFF.
10. En Client Authentication: Client authentication: ON, Authorization: OFF, Standard flow: OFF, Direct access grants: OFF y Service accounts roles: ON.
11. Le damos a Save. 
12. Obtenemos la credencial de financial-client: Vamos a Clients -> financial-client -> Credentials y obtenemos la credentials.

## Ejecución en Postman: 

1. Colocamos la URL con el método POST: http://localhost:8083/realms/financial-realm/protocol/openid-connect/token
2. Vamos a la pestaña Headers y en Key: Content-Type y en Value: application/x-www-form-urlencoded .
3. En Body seleccionamos x-www-form-urlencoded y agregamos los campos: grant_type : client_credentials , client_id : financial-client y client_secret: LaCredencial ejm: financial-secret 
4. Damos a Send.
5. Respuesta esperada: 
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expires_in": 300,
  "token_type": "Bearer"
}

6. Usamos el toke del campo "access_token" en nuesta petición. Lo ubicamos en el apartado Authorization en Auth Type: Bearer Token y en Token: elTokenGenerado
7. Colocamos la URL http://localhost:8080/api/v1/clientes/info/Q0xJMDAx con el código del cliente encriptado como pathUrl En el método GET.
8. Respuesta esperada: 

{
  "traceId": "aa4e6608-47c8-4d6b-850a-fd6dbed2ad34",
  "nombres": "Juan Carlos",
  "apellidos": "Pérez García",
  "tipoDocumento": "DNI",
  "numeroDocumento": "12345678",
  "productos": [
    {
      "tipoProducto": "CUENTA_AHORRO",
      "nombre": "Cuenta Ahorro Premium",
      "saldo": 15000.50
    },
    {
      "tipoProducto": "TARJETA_CREDITO",
      "nombre": "Visa Platinum",
      "saldo": -2500.00
    },
    {
      "tipoProducto": "CUENTA_CORRIENTE",
      "nombre": "Cuenta Corriente Empresarial",
      "saldo": 8750.25
    }
  ]
}

## Enlaces Útiles

- Keycloak Admin: http://localhost:8083/admin/master/console/ 
- keyCloak Toekn: http://localhost:8083/realms/financial-realm/protocol/openid-connect/token
- BFF-Service: http://localhost:8080/api/v1/clientes/info/Q0xJMDAx
- OpenApiSwagger: http://localhost:8080/swagger-ui/index.html
- ApiJson: http://localhost:8080/v3/api-docs
