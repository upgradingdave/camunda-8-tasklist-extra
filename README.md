[![Community Extension](https://img.shields.io/badge/Community%20Extension-An%20open%20source%20community%20maintained%20project-FF4700)](https://github.com/camunda-community-hub/community)

![Compatible with: Camunda Platform 8](https://img.shields.io/badge/Compatible%20with-Camunda%20Platform%208-0072Ce)

[![](https://img.shields.io/badge/Lifecycle-Proof%20of%20Concept-blueviolet)](https://github.com/Camunda-Community-Hub/community/blob/main/extension-lifecycle.md#proof-of-concept-)

# Camunda 8 Task List Extra

This project contains a rest api that includes all the Task List Rest API calls, plus extra functionality commonly
needed for custom Task List implementations

# Getting Started

Build and compile using:

```shell
./mvnw clean install
```

Start the Spring Boot Application using:

```shell
./mvnw spring-boot:run
```

Then browse to [http://localhost:8087/swagger-ui/index.html](http://localhost:8087/swagger-ui/index.html) to see available
rest api endpoints

# Sample ReactJs Front End

This project includes a sample front end written in ReactJs with NodeJS v18.5.0 (npm v8.12.1) and uses [Carbon](https://github.com/carbon-design-system/carbon) for css styling.

See [src/main/js/README.md](src/main/js/README.md) for more details on how to build and run the ReactJs App.


