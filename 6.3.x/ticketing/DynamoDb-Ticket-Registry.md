---
layout: default
title: CAS - DynamoDb Ticket Registry
category: Ticketing
---

# DynamoDb Ticket Registry

DynamoDb ticket registry integration is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
     <groupId>org.apereo.cas</groupId>
     <artifactId>cas-server-support-dynamodb-ticket-registry</artifactId>
     <version>${cas.version}</version>
</dependency>
```

This registry stores tickets in [DynamoDb](https://aws.amazon.com/dynamodb/) instances. Each ticket type is linked to a distinct table.

## Configuration

You will need to provide CAS with your [AWS credentials](https://aws.amazon.com/console/). Also, to gain a better understanding
of DynamoDb's core components and concepts, please [start with this guide](http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Introduction.html) first.
To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#dynamodb-ticket-registry).

## Troubleshooting

To enable additional logging, configure the log4j configuration file to add the following levels:

```xml
...
<Logger name="com.amazonaws" level="debug" additivity="false">
    <AppenderRef ref="console"/>
    <AppenderRef ref="file"/>
</Logger>
...
```
