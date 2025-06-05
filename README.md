# E-commerce Backend 
>[!WARNING]
>Note: This system is still under construction and some important parts are missing.

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

## Documentation
For more information, refer to the [project documentation](https://szczygieldev.github.io/ecommerce-backend/).

## Licences
This project is licensed under the Apache License 2.0.