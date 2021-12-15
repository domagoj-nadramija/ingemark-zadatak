# ingemark-zadatak


## Environment setup
1. Install Java 15
2. Start database using Docker compose
    ```
    docker-compose up
    ```
3. Start webshop in development mode
    ```
    ./mvnw spring-boot:run
    ```
## Create Java JAR file
You can create the JAR file by using the provided maven wrapper:
```
./mvnw package
```
Or by running the same goal with a locally installed Maven.

You can then run the JAR file like this:
```
java -jar target/webshop-1.0.0.jar
```
provided the java on your path is Java 15.

## Configuration
Currently, all configuration is located in the `application.properties` file.

These can be overwritten as usual, by providing an external properties file or setting the appropriate environment variables when running the JAR.

## Database migrations
Resources are created in the database using Flway migrations that are performed automatically the first time the application is run.

There are currently 2 migrations run:
 - V1__Initial.sql - sets up everything as outlined in the assignment documentation: tables, types and relationships
 - V2__SampleData.sql - creates sample data: customers, products, orders and their order items

These SQL files are located in `src/main/resources/db/migration`.

## Additional information

Swagger documentation is available, by default on **/swagger-ui/**. So when running locally on port 5000 that would be:
```
http://localhost:5000/swagger-ui/
```
Many useful endpoints are exposed on **/actuator** like health checks, metrics, and migration status. So, again, locally:
```
http://localhost:5000/actuator
```
