---
layout: default
title: CAS - Redis Ticket Registry
---

# Redis Ticket Registry

Redis integration is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-redis-ticket-registry</artifactId>
    <version>${cas.version}</version>
</dependency>
```

This registry stores tickets in one or more [Redis](http://redis.io/) instances. The
[spring data redis](http://projects.spring.io/spring-data-redis/) library used by this component presents Redis as a
key/value store that accepts `String` keys and CAS ticket objects as values. The key is started with `CAS_TICKET:`.

## Configuration

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#redis-ticket-regisry).

### Eviction Policy

Redis manages the internal eviction policy of cached objects via its time-alive settings.
The timeout is the ticket's `timeToLive` value. So you need to ensure the cache is alive long enough to support the
individual expiration policy of tickets, and let CAS clean the tickets as part of its own cleaner if necessary.
