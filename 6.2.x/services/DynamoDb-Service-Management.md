---
layout: default
title: CAS - DynamoDb Service Registry
category: Services
---

# DynamoDb Service Registry

Stores registered service data in a [DynamoDb](https://aws.amazon.com/dynamodb/) instance.

Support is enabled by adding the following module into the overlay:

```xml
<dependency>
     <groupId>org.apereo.cas</groupId>
     <artifactId>cas-server-support-dynamodb-service-registry</artifactId>
     <version>${cas.version}</version>
</dependency>
```

## Configuration

You will need to provide CAS with your [AWS credentials](https://aws.amazon.com/console/). Also, to gain a better understanding
of DynamoDb's core components and concepts, please [start with this guide](http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Introduction.html) first. To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#dynamodb-service-registry).

## Troubleshooting

To enable additional logging, configure the log4j configuration file to add the following levels:

```xml
...
<AsyncLogger name="com.amazonaws" level="debug" additivity="false">
    <AppenderRef ref="console"/>
    <AppenderRef ref="file"/>
</AsyncLogger>
...
```


## Auto Initialization

Upon startup and configuration permitting, the registry is able to auto initialize itself from default JSON service definitions available to CAS. See [this guide](AutoInitialization-Service-Management.html) for more info.