---
layout: default
title: CAS - Messaging (JMS) Ticket Registry
category: Ticketing
---

# JMS Ticket Registry

CAS can be enabled with a variety of messaging systems in order to distribute and share ticket data: 
from simplified use of the JMS API to a complete infrastructure to receive messages asynchronously. 
Integration with messaging systems is entirely built on 
top of [Spring Boot](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-messaging.html).

Support is enabled by including the following dependency in the overlay:

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-jms-ticket-registry</artifactId>
    <version>${cas.version}</version>
</dependency>
```

This registry is very much an extension of the [default ticket registry](Default-Ticket-Registry.html). 
The difference is that ticket operations applied to the registry are broadcasted using a messaging queue 
to other listening CAS nodes on the queue. Each node keeps copies of ticket state on its own and only 
instructs others to keep their copy accurate by broadcasting messages and data associated with each. 
Each message and ticket registry instance running inside a CAS node in the cluster is tagged with a unique 
identifier in order to avoid endless looping behavior and recursive needless inbound operations.

## Configuration

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#jms-ticket-registry).

## ActiveMQ

CAS can configure the ticket registry when it detects that ActiveMQ 
is available on the classpath. If the broker is present, an embedded broker is started and 
configured automatically, as long as no broker URL is specified through configuration. 
By default, ActiveMQ creates a destination if it does not exist yet, so destinations are resolved against their provided names.

ActiveMQ configuration is controlled by external configuration properties in [CAS settings](../configuration/Configuration-Properties.html#jms-ticket-registry).

The default setting for ActiveMQ is that all persistent messages outside of a transaction 
are sent to a broker are synchronous. This means that the send method is blocked until the 
message is received by the broker, its then written to disk - then a response is returned 
to the client and the `send()` unblocks with success or throws an error if the send could not complete (e.g. due to a security exception).

## Artemis

CAS can auto-configure the ticket registry when it detects that [Artemis](https://activemq.apache.org/artemis/) 
is available on the classpath. If the broker is present, an embedded broker is started and 
configured automatically (unless the mode property has been explicitly set). The supported modes are: 
embedded (to make explicit that an embedded broker is required and should lead to an error if the broker 
is not available in the classpath), and native to connect to a broker using the netty transport protocol. 
When the latter is configured, CAS configures the registry connecting to a broker running on the local machine with the default settings.

Support is enabled by including the following dependency in the overlay:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-artemis</artifactId>
    <version>${springboot.version}</version>
</dependency>
```

Artemis configuration is controlled by external configuration properties in [CAS settings](../configuration/Configuration-Properties.html#jms-ticket-registry).

## JNDI

If you are [running CAS in an application server](../installation/Configuring-Servlet-Container.html), 
CAS will attempt to locate a JMS connection using JNDI. By default, the locations 
`java:/JmsXA` and `java:/XAConnectionFactory` will be checked. Of course, alternative locations may be 
specified using [CAS settings](../configuration/Configuration-Properties.html#jms-ticket-registry).


## Troubleshooting

To enable additional logging, configure the log4j configuration file to add the following levels:

```xml
...
<AsyncLogger name="org.springframework.jms" level="debug" additivity="false">
    <AppenderRef ref="console"/>
    <AppenderRef ref="file"/>
</AsyncLogger>
...
```
