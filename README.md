![Java](https://img.shields.io/badge/java-17-blue)
![Spring Boot](https://img.shields.io/badge/spring--boot-3.3.5-brightgreen)
![Maven](https://img.shields.io/badge/maven-3.2.5-orange)


# ChâTop - Projet 3
Rentals is an application developed with Spring Boot to manage your rental needs. This project is designed to be simple, efficient, and easy to use.

## Project Overview

- Data management with **Spring Data JPA**.
- Endpoint security using **Spring Security**.
- Data validation with **Spring Boot Starter Validation**.
- Support for relational databases like **MySQL**.
- Dependency and build management with **Maven**.

## Prerequisites
To run this project, ensure you have the following installed on your machine:

- **Java 17** or later.
- **Maven 3.2.5** or later.
- **MySQL** or another compatible relational database.

## **Installation**

To install and run the project, follow these steps:

1. Clone this repository to your local machine.
2. Configure your MySQL database and update the application.properties or application.yml file with the following details:
   - Database URL
   - Username
   - Password
3. Install the project dependencies by running the following command:
`   mvn clean install`
4. Start the application by running:
  ` mvn spring-boot:run`

## **Project Structure**

- **src/main/java:** Contains the Java source code.
- **src/main/resources**: Contains configuration files like application.properties.


## Key Dependencies

- **Spring Boot 3.3.5** : Main framework for application development. 
- **Spring Security** : Provides endpoint security. 
- **Spring Data JPA** : Enables database interaction using JPA. 
- **MySQL Connector** : Handles connectivity to the MySQL database. 
- **Lombok** : Reduces boilerplate code.

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
