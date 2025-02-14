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
| FindById          | `GET`           | Service numeric id appended to the endpoint url as a path variable   | `RegisteredService` object
| FindById          | `GET`           | Service url appended to the endpoint url as a path variable    | `RegisteredService` object

All operations are expected to return a `200` status code. All other response status codes will force CAS to consider the requested operation nullified.

## Auto Initialization

Upon startup and configuration permitting, the registry is able to auto initialize itself from 
default JSON service definitions available to CAS. See [this guide](AutoInitialization-Service-Management.html) for more info.

## Implementation

The following code snippet demonstrates an *example* implementation of the REST API expected by CAS via Spring Boot:

```java
@RestController
@RequestMapping("/services")
public class ServicesController {
    
    @DeleteMapping
    public Integer findByServiceId(@RequestBody final RegisteredService service) {
        // Locate the service...
        return HttpStatus.SC_OK;
    }

    @PostMapping
    public RegisteredService save(@RequestBody final RegisteredService service) {
        // Save the provided service...
        return ...;
    }

    @GetMapping("/{id}")
    public RegisteredService findServiceById(@PathVariable(name = "id") final String id) {
        if (NumberUtils.isParsable(id)) {
            // Locate service by its numeric internal identifier
            return ...
        }
         // Locate service by its service identifier
        return ...
    }

    @GetMapping
    public RegisteredService[] load() {
        // Load services...
        return new RegisteredService[]{...};
    }
}
```
