---
layout: default
title: CAS - Service Discovery
---

# Service Discovery

Service Discovery is one of the key tenets of a microservice based architecture. This guide aims to describe built-in CAS supported options that can be used for locating nodes for the purpose of load balancing and failover.

## Eureka Server Discovery Service

[Eureka](https://github.com/Netflix/eureka) is a REST-based service that is primarily 
used for locating services for the purpose of load balancing and failover of middle-tier servers. Eureka provides both a discovery server and also support for clients which would be the individual CAS servers themselves in the pool. The server can be configured and deployed to be highly available, with each server replicating state about the registered services to the others.

CAS provides a Eureka-enabled service discovery server that is based on [Spring Cloud Netflix](http://cloud.spring.io/spring-cloud-netflix) and bootstrapped via [Spring Cloud](http://cloud.spring.io/spring-cloud-static/spring-cloud.html).

### Installation

- To run the Eureka discovery server, please [use this WAR overlay](https://github.com/apereo/cas-discoveryserver-overlay).
- Look for a suitable and relevant ready-made Docker image via `docker search eureka`.

When deployed the following URLs become available:

| URL                | Description
|--------------------|-----------------------------------------------
| `/`                | Home page listing service registrations.
| `/eureka/apps`     | Raw registration metadata.

### High Availability Mode

You always want to make sure the discovery server is run in high-availabilty mode. One option is to ensure each individual Eureka server is peer aware. See [this guide](http://cloud.spring.io/spring-cloud-static/spring-cloud.html#_peer_awareness) to learn how to manage that.

## CAS Discovery Service Clients

Each individual CAS server is given the ability to auto-register itself with the discovery server, provided configuration is made available to instruct the CAS server how to locate and connect to the discover server service.

Support is added by including the following dependency in the WAR overlay:

```xml
<dependency>
  <groupId>org.apereo.cas</groupId>
  <artifactId>cas-server-support-eureka-client</artifactId>
  <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties,
please [review this guide](Configuration-Properties.html#eureka-service-discovery).

### Authentication

Support for HTTP basic authentication will be automatically added if one of Eureka server URLs in the configuration has credentials embedded in it (curl style, like `http://user:password@localhost:8761/eureka`). 

### Troubleshooting

To enable additional logging, configure the log4j configuration file to add the following levels:

```xml
<AsyncLogger name="com.netflix" level="debug" additivity="false">
    <AppenderRef ref="casConsole"/>
    <AppenderRef ref="casFile"/>
</AsyncLogger>
```
