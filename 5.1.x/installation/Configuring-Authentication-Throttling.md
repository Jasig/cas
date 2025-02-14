---
layout: default
title: CAS - Configuring Authentication Throttling
---

# Throttling Authentication Attempts

CAS provides a facility for limiting failed login attempts to support password guessing and related abuse scenarios.
A couple strategies are provided for tracking failed attempts:

1. Source IP - Limit successive failed logins against any username from the same IP address.
2. Source IP and username - Limit successive failed logins against a particular user from the same IP address.

It would be straightforward to develop new components that implement alternative strategies.

All login throttling components that ship with CAS limit successive failed login attempts that exceed a threshold
rate in failures per second. The following properties are provided to define the failure rate.

* `failureRangeInSeconds` - Period of time in seconds during which the threshold applies.
* `failureThreshold` - Number of failed login attempts permitted in the above period.

A failure rate of more than 1 per 3 seconds is indicative of an automated authentication attempt, which is a
reasonable basis for throttling policy. Regardless of policy care should be taken to weigh security against access;
overly restrictive policies may prevent legitimate authentication attempts.

## IP Address

Uses a memory map to prevent successive failed login attempts from the same IP address.

## IP Address and Username

Uses a memory map to prevent successive failed login attempts for
a particular username from the same IP address.

## Inspektr + JDBC

Queries the data source used by the CAS audit facility to prevent successive failed login attempts for a particular
username from the same IP address. This component requires that the
[inspektr library](https://github.com/apereo/inspektr) used for CAS auditing be configured with
`JdbcAuditTrailManager`, which writes audit data to a database.

Enable the following module in your configuration overlay:

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-throttle-jdbc</artifactId>
    <version>${cas.version}</version>
</dependency>
```

For additional instructions on how to configure auditing via Inspektr,
please [review the following guide](Logging.html).

## Configuration

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#authentication-throttling).

## High Availability Considerations for Throttling

All of the throttling components are suitable for a CAS deployment that satisfies the
[recommended HA architecture](../planning/High-Availability-Guide.html). In particular deployments with multiple CAS
nodes behind a load balancer configured with session affinity can use either in-memory or _inspektr_ components. It is
instructive to discuss the rationale. Since load balancer session affinity is determined by source IP address, which
is the same criterion by which throttle policy is applied, an attacker from a fixed location should be bound to the
same CAS server node for successive authentication attempts. A distributed attack, on the other hand, where successive
request would be routed indeterminately, would cause haphazard tracking for in-memory CAS components since attempts
would be split across N systems. However, since the source varies, accurate accounting would be pointless since the
throttling components themselves assume a constant source IP for tracking purposes. The login throttling components
are simply not sufficient for detecting or preventing a distributed password brute force attack.

For stateless CAS clusters where there is no session affinity, the in-memory
components may afford some protection but
they cannot apply the rate strictly since requests to CAS hosts would be split across N systems.
The _inspektr_ components, on the other hand, fully support stateless clusters.
