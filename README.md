![Java](https://img.shields.io/badge/java-17-blue)
![Spring Boot](https://img.shields.io/badge/spring--boot-3.3.5-brightgreen)
![Maven](https://img.shields.io/badge/maven-3.2.5-orange)
![Swagger](https://img.shields.io/badge/swagger--springdoc-2.6.0-blue)



# ChâTop - Projet 3
Rentals is an application developed with Spring Boot to manage your rental needs.



## Prerequisites
To run this project, ensure you have the following installed on your machine:

- **Java 17** or later.
- **Maven 3.2.5** or later.
- **MySQL** or another compatible relational database.


## **Installation**


To install and run the project, follow these steps:

1. If you want to test the front-end Angular application with this API, please clone this repository first : https://github.com/OpenClassrooms-Student-Center/Developpez-le-back-end-en-utilisant-Java-et-Spring
2. Then, clone this repository to your local machine.
3. You can use a local server like MAMP, XAMPP, or WAMP to host your MySQL database. Make sure to update the database connection details in the `application.properties` (or **.env**) file.

4. **Configure your MySQL database:**
    - Open the `application.properties` (or **.env**) file and update it with the following details:

        - **Database URL**:
          ```properties
          spring.datasource.url=${SPRING_DATASOURCE_URL}
          ```  
          *Example for MAMP*:
          ```properties
          spring.datasource.url=jdbc:mysql://localhost:8889/rental?serverTimezone=UTC&createDatabaseIfNotExist=true
          ```

        - **Your DB Username**:
          ```properties
          spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
          ```

        - **Your DB Password**:
          ```properties
          spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
          ```

        - **JWT Secret Key** (Ensure the key is sufficiently long and encoded in **Base64** for it to work properly. The key should be **256 bits** long for the **HS256** algorithm):
          ```properties
          security.jwt.secret-key=${SECURITY_JWT_SECRET_KEY}
          ```
5. Install the project dependencies by running the following command:
`   mvn clean install`
6. When the build is completed, you can start the application by running:
  ` mvn spring-boot:run`
7. The application will run and the database will then be created.


## **Project Structure**

- **src/main/java:** Contains the Java source code.
- **src/main/resources**: Contains configuration files (application.properties). Please, create your **.env** file here.

## Key Dependencies

- **Spring Boot 3.3.5** : Main framework for application development.
- **Spring Security** : Provides endpoint security.
- **Spring Data JPA** : Enables database interaction using JPA.
- **MySQL Connector** : Handles connexion to the MySQL database.


## API Documentation

This application uses Swagger (via Springdoc OpenAPI) to provide an interactive API documentation.

# Accessing Swagger

Once the application is running, you can access the Swagger UI at the following URL :

```
http://localhost:3001/swagger-ui/index.html
```

You will be able to :
- View all available API endpoints.
- Test API requests directly from the browser.
- Access detailed descriptions of each endpoint's parameters and responses.

# Swagger Authentication with JWT
To access the secured endpoints, you need to provide a valid JWT token. 

Follow these steps:

1. **Obtain a JWT Token:** Use the authentication endpoint **`localhost:3001/api/auth/login`** (in the "**Authentication**" section in Swagger) to generate a JWT token by providing valid user credentials.

Example request :
```
{
  "email": "your-email",
  "password": "your-password"
}   

```

You will then obtain the following response : 
```
{
    "token": "your-token",
    "expiresIn": "token-expiration-time"
}
```
2. **If you don't have an account** : use the endpoint **localhost:3001/api/auth/register** and create an account.

```
{
    "email": "your-email",
    "name": "your-name",
    "password": "your-password"
}   

```

3. **Add the Token in Swagger:** : In Swagger UI, click on the Authorize button (located at the top-right corner).

4. After authorization, you can test the secured endpoints directly from the Swagger interface.

## **Contributing**

If you'd like to contribute to this project, here are some guidelines:

1. Fork the repository.
2. Create a new branch for your changes.
3. Make your changes.
4. Write tests to cover your changes.
5. Run the tests to ensure they pass.
6. Commit your changes.
7. Push your changes to your forked repository.
8. Submit a pull request.


## **Contact**

If you have any questions or comments about this project, please contact **[Céline](celine.intha@gmail.com)**.
