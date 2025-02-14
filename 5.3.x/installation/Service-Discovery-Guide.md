---
layout: default
title: CAS - Service Discovery
---

# Service Discovery

Service Discovery is one of the key tenets of a microservice based architecture. This guide aims to describe built-in CAS supported options that can be used for locating nodes for the purpose of load balancing and failover.

## Eureka Server Discovery Service

[Eureka](https://github.com/Netflix/eureka) is a REST-based service that is primarily  used for locating services for the purpose of load balancing and failover of middle-tier servers. CAS provides a Eureka-enabled service discovery server that is based on [Spring Cloud Netflix](http://cloud.spring.io/spring-cloud-netflix) and bootstrapped via [Spring Cloud](http://cloud.spring.io/spring-cloud-static/spring-cloud.html).

[See this guide](Service-Discovery-Guide-Eureka.html) for more info.

## Consul Server Discovery Service

[HashiCorp Consul](https://www.consul.io) has multiple components, but as a whole, it is a tool for discovering and configuring services in your infrastructure. It provides key features such as service discovery, health checking and more.

[See this guide](Service-Discovery-Guide-Consul.html) for more info.
