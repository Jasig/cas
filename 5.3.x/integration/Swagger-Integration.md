---
layout: default
title: CAS - Swagger API Integration
---

# Overview

CAS takes advantages of [Swagger](https://swagger.io/) to produce API documentation automatically. 
The generated documentation supports all CAS endpoints and REST APIs provided they are made available to the runtime 
application context and are present in the overlay. 

Support is enabled by including the following dependency in the overlay:

```xml
<dependency>
  <groupId>org.apereo.cas</groupId>
  <artifactId>cas-server-documentation-swagger</artifactId>
  <version>${cas.version}</version>
</dependency>
```

## Endpoints

The following Swagger endpoints may be used to analyze and test the APIs: 

| Description                 | URL              
|-----------------------------|-------------------------------------------------
| Swagger API Specification   | `https://sso.example.org/cas/v2/api-docs`       
| Swagger UI                  | `https://sso.example.org/cas/swagger-ui.html`       
