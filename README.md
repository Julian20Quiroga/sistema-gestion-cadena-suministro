# LogiSync — Backend

## Tecnologías Usadas

- **Lenguaje:** Java 21
- **Framework Principal:** Spring Boot 4.1.1
- **Seguridad:** Spring Security y JSON Web Tokens (JJWT 0.13.0)
- **Persistencia y ORM:** Spring Data JPA con Hibernate
- **Bases de Datos:** MySQL (base de datos principal) y H2 Database (exclusivamente para pruebas automáticas / tests)
- **Documentación API:** SpringDoc OpenAPI / Swagger UI
- **Monitoreo y Métricas:** Spring Boot Actuator y Micrometer Prometheus
- **Herramientas de Desarrollo:** Project Lombok y MapStruct
- **Gestor de Dependencias y Construcción:** Apache Maven 3.9+

---

## Cómo Ejecutar el Backend

### 1. Requisitos Previos

- Tener instalado **Java 21** (JDK 21 o superior).
- Tener instalado **Apache Maven 3.9+** (o usar el wrapper de Maven).
- Servidor **MySQL** en ejecución con la base de datos configurada.

### 2. Pasos de Ejecución

1. Abrir una terminal y ubicarse en el directorio del backend:
   ```bash
   cd backend/sistema_cadena_suministro
   ```

2. (Opcional) Compilar el proyecto y verificar que todas las pruebas pasen:
   ```bash
   mvn clean verify
   ```

3. Iniciar el servidor de desarrollo:
   ```bash
   mvn spring-boot:run
   ```

4. El backend estará en ejecución y escuchando peticiones en:
   ```
   http://localhost:5050
   ```
   *(Documentación Swagger disponible en `http://localhost:5050/swagger-ui.html`)*
