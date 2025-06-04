# Overview

An e-commerce system implemented in Kotlin with Spring Boot, serving as my sandbox for exploring topics and concepts
such as:

- Domain-Driven Design
- Modular Monolith
- Hexagonal Architecture
- CQRS
- Event-Sourcing

## Running

To build this application, you must
be [authenticated with GitHub Packages](https://docs.github.com/en/packages/learn-github-packages/introduction-to-github-packages#authenticating-to-github-packages).
Authentication is required to access internal libraries used by the project.

Once you have obtained your token, save it in the gradle.properties file located in the root directory of the project:

```
gpr.user=<username>
gpr.key=<token>
```

After configuring your GitHub Packages credentials, you can run the project using Docker Compose:

```
docker compose up
``` 

This command may vary depending on your Docker version.

## Ports

Main Backend:
http://localhost:8080/

Swagger API Documentation:
http://localhost:8080/swagger-ui/index.html

H2 Database Console:
http://localhost:8080/h2-console/

Mailpit Web Interface:
http://localhost:8025/

## Debugging
The easiest way to debug the application is to run all required infrastructure containers without the backend, allowing you to manually develop and debug the backend service.

```
docker compose up master volume filer s3 s3-proxy mailpit
``` 

## Documentation

To modify and host the documentation, refer to the [Docsify Guide](https://docsify.js.org/#/quickstart).

The easiest way to preview the documentation locally is by using the docsify-cli tool with the following command:
```
docsify serve ./docs
```
