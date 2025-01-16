# Resource Aggregator Service

## Description
Resource Aggregator Service is a microservice for consolidating data from various databases and providing an API for processing it. This service supports integration with multiple databases, including PostgreSQL, MySQL, and Oracle. It uses the OpenAPI specification for API documentation, but no code generation is performed based on it.

## Key Features
- Connect to multiple databases.
- Execute queries on specified tables.
- Consolidate and return data via API.
- Support OpenAPI specification for documentation.

## Requirements
- **Docker** >= 20.10
- **Docker Compose** >= 1.29
- **Java** 17
- **Gradle** 8.5

## Installation and Launch

### 1. Start Database Containers

The application requires several database containers to run. Use the `docker-compose.yml` file located in the project root to start the necessary containers:

```bash
docker-compose up -d
```

This will start the following services:

- **MySQL**: Available on port `3306` with the following credentials:
  - Database: `mysql`
  - User: `mysql`
  - Password: `mysql`

- **PostgreSQL**: Available on port `5432` with the following credentials:
  - Database: `postgres`
  - User: `postgres`
  - Password: `postgres`

### 2. Build the Application

The application uses a Dockerfile for building a containerized version of the service. Run the following command to build the image:

```bash
docker build -t resource-aggregator-service .
```

### 3. Run the Application Container

After building the image, start the application container with environment variables for database connections by running following command in command line:

```bash
docker run -p 8080:8080 --name resource-aggregator-app --network custom_database_network -e SPRING_DATASOURCE_POSTGRES_URL=jdbc:postgresql://postgres:5432/postgres -e SPRING_DATASOURCE_POSTGRES_USER=postgres -e SPRING_DATASOURCE_POSTGRES_PASSWORD=postgres -e SPRING_DATASOURCE_MYSQL_URL=jdbc:mysql://mysql:3306/mysql -e SPRING_DATASOURCE_MYSQL_USER=mysql -e SPRING_DATASOURCE_MYSQL_PASSWORD=mysql resource-aggregator-service
```

The application will be available on port `8080`.

## API Documentation

Once the service is running, API documentation is available at:

- Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- OpenAPI JSON: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

## Configuration

### Example of Connecting to Multiple Databases

`application.yml` file:

```yaml
application:
  data-sources:
    - name: 'data-base-1'
      strategy: 'postgres'
      url: ${SPRING_DATASOURCE_POSTGRES_URL:jdbc:postgresql://localhost:5432/postgres}
      table: 'users'
      user: ${SPRING_DATASOURCE_POSTGRES_USER:postgres}
      password: ${SPRING_DATASOURCE_POSTGRES_PASSWORD:postgres}
      mapping:
        id: 'user_id'
        username: 'login'
        name: 'first_name'
        surname: 'last_name'
    - name: 'data-base-2'
      strategy: 'mysql'
      url: ${SPRING_DATASOURCE_MYSQL_URL:jdbc:mysql://localhost:3306/mysql}
      table: 'users'
      user: ${SPRING_DATASOURCE_MYSQL_USER:mysql}
      password: ${SPRING_DATASOURCE_MYSQL_PASSWORD:mysql}
      mapping:
        id: 'emp_id'
        username: 'username'
        name: 'first_name'
        surname: 'last_name'
    - name: 'data-base-3'
      strategy: 'oracle'
      url: ${SPRING_DATASOURCE_ORACLE_URL:jdbc:oracle:thin:@localhost:1521:XE}
      table: 'users'
      user: ${SPRING_DATASOURCE_ORACLE_USER:oracle}
      password: ${SPRING_DATASOURCE_ORACLE_PASSWORD:secret}
      mapping:
        id: 'user_id'
        username: 'login'
        name: 'first_name'
        surname: 'last_name'
```

### Adding Support for a New Database Connector

To support a new database connector, you need to:
1. Implement the `AbstractDatabaseConnector` interface with the logic specific to the new database type.
2. Add the corresponding database driver to the project dependencies in `build.gradle`.

For example, to support MongoDB, you would implement the connector and add the `mongo-java-driver` to the dependencies.

## Notes on Architecture

The project uses synchronous queries and is designed this way specifically for the purpose of a test assignment. In a production-grade implementation, it is recommended to adopt a reactive approach using **WebFlux** and **Project Reactor** to achieve better scalability and performance.

## Testing

To run tests, execute:

```bash
./gradlew test
```

## Development

### Run the Application Locally

To run the application locally, use:

```bash
./gradlew bootRun
```
