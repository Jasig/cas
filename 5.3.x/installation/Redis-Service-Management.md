---
layout: default
title: CAS - Redis Service Registry
---

# Redis Service Registry

This service registry stores tickets in one or more [Redis](http://redis.io/) instances. The
[spring data redis](http://projects.spring.io/spring-data-redis/) library used by this component presents Redis as a
key/value store that accepts `String` keys and CAS service definition objects as values. The key is started with `CAS_SERVICE:`.

The Redis service registry supports Redis Sentinel, which provides high availability for Redis. In practical terms this means that using Sentinel you can create a Redis deployment that resists without human intervention to certain kind of failures. Redis Sentinel also provides other collateral tasks such as monitoring, notifications and acts as a configuration provider for clients.

Support is enabled by including the following dependency in the overlay:

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-redis-service-registry</artifactId>
    <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#redis-service-registry).

## Auto Initialization

Upon startup and configuration permitting, the registry is able to auto initialize itself from default JSON service definitions available to CAS. See [this guide](AutoInitialization-Service-Management.html) for more info.