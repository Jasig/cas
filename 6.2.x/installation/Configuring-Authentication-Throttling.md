---
layout: default
title: CAS - Configuring Authentication Throttling
category: Authentication
---

# Throttling Authentication Attempts

## Capacity Throttling

CAS is able to support request rate-limiting based on the token-bucket algorithm. This means that authentication requests that reach a certain configurable 
capacity within a time window may either be blocked or _throttled_ to slow down. This is done to protect the system from overloading, allowing you to introduce
a scenario to allow CAS 120 authentication requests per minute with a refill rate of 10 requests per second that would continually increase in the capacity bucket.

Enable the following module in your configuration overlay:

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-throttle-bucket4j</artifactId>
    <version>${cas.version}</version>
</dependency>
```

## Failure Throttling

CAS provides a facility for limiting failed login attempts to support password guessing and related abuse scenarios.
A couple strategies are provided for tracking failed attempts:

1. Source IP - Limit successive failed logins against any username from the same IP address.
2. Source IP and username - Limit successive failed logins against a particular user from the same IP address.

All login throttling components that ship with CAS limit successive failed login attempts that exceed a threshold
rate in failures per second. The following properties are provided to define the failure rate.

* `failureRangeInSeconds` - Period of time in seconds during which the threshold applies.
* `failureThreshold` - Number of failed login attempts permitted in the above period.

A failure rate of more than 1 per 3 seconds is indicative of an automated authentication attempt, which is a
reasonable basis for throttling policy. Regardless of policy care should be taken to weigh security against access;
overly restrictive policies may prevent legitimate authentication attempts.


Enable the following module in your configuration overlay:

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-throttle</artifactId>
    <version>${cas.version}</version>
</dependency>
```

### IP Address

Uses a memory map to prevent successive failed login attempts from the same IP address.

### IP Address and Username

Uses a memory map to prevent successive failed login attempts for
a particular username from the same IP address.

### JDBC

Queries a database data source used by the CAS audit facility to prevent successive failed login attempts for a particular username from the same IP address. 
This component requires and depends on the [CAS auditing functionality](Audits.html) via databases.

Enable the following module in your configuration overlay:

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-throttle-jdbc</artifactId>
    <version>${cas.version}</version>
</dependency>
```

For additional instructions on how to configure auditing, please [review the following guide](Audits.html).

### MongoDb

Queries a MongoDb data source used by the CAS audit facility to prevent successive failed login attempts for a particular username from the same IP address. This component requires and depends on the [CAS auditing functionality](Audits.html) via MongoDb.

Enable the following module in your configuration overlay:

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-throttle-mongo</artifactId>
    <version>${cas.version}</version>
</dependency>
```

### Redis

Queries a Redis data source used by the CAS audit facility to prevent successive failed login attempts for a particular username from the same IP address. This component requires and depends on the [CAS auditing functionality](Audits.html) via Redis.

Enable the following module in your configuration overlay:

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-throttle-redis</artifactId>
    <version>${cas.version}</version>
</dependency>
```

### Hazelcast

This feature uses a distributed Hazelcast map to record throttled authentication attempts. 
This component requires and depends on the [CAS auditing functionality](Audits.html)

Enable the following module in your configuration overlay:

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-throttle-hazelcast</artifactId>
    <version>${cas.version}</version>
</dependency>
```

### CouchDb

Queries a CouchDb data source used by the CAS audit facility to prevent successive failed login attempts for a particular username from the same IP address. This component requires and depends on the [CAS auditing functionality](Audits.html) via CouchDb.

Enable the following module in your configuration overlay:

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-throttle-mongo</artifactId>
    <version>${cas.version}</version>
</dependency>
```

For additional instructions on how to configure auditing, please [review the following guide](Audits.html).

## Configuration

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#authentication-throttling).

## High Availability Considerations for Throttling

All of the throttling components are suitable for a CAS deployment that satisfies the
[recommended HA architecture](../high_availability/High-Availability-Guide.html). In particular deployments with multiple CAS
nodes behind a load balancer configured with session affinity can use either in-memory or _inspektr_ components. It is
instructive to discuss the rationale. Since load balancer session affinity is determined by source IP address, which
is the same criterion by which throttle policy is applied, an attacker from a fixed location should be bound to the
same CAS server node for successive authentication attempts. A distributed attack, on the other hand, where successive
request would be routed indeterminately, would cause haphazard tracking for in-memory CAS components since attempts
would be split across N systems. However, since the source varies, accurate accounting would be pointless since the
throttling components themselves assume a constant source IP for tracking purposes. The login throttling components
are simply not sufficient for detecting or preventing a distributed password brute force attack.
