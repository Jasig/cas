---
layout: default
title: CAS - RESTful Service Registry
---

# RESTful Service Registry

```xml
<dependency>
     <groupId>org.apereo.cas</groupId>
     <artifactId>cas-server-support-rest-service-registry</artifactId>
     <version>${cas.version}</version>
</dependency>
```

## Configuration

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#restful-service-registry).

| Operation         | Method          | Body                       | Response
|-------------------|-----------------|----------------------------------------------------------------------
| Save              | `POST`          | `RegisteredService` object | `RegisteredService` object
| Delete            | `DELETE`        | `RegisteredService` object | None
| Load              | `GET`           | None                       | Collection of `RegisteredService` objects
| FindById          | `GET`           | Service numeric id         | `RegisteredService` object
| FindById          | `GET`           | Service url                | `RegisteredService` object

All operations are expected to return a `200` status code. All other response status codes will force CAS to consider the requested operation nullified.

## Auto Initialization

Upon startup and configuration permitting, the registry is able to auto initialize itself from default JSON service definitions available to CAS. See [this guide](AutoInitialization-Service-Management.html) for more info.