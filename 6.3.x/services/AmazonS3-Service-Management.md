---
layout: default
title: CAS - Amazon S3 Service Registry
category: Services
---

# Amazon S3 Service Registry

Stores registered service data in a [Amazon S3](https://aws.amazon.com/s3/) buckets. Each service definition is managed inside its own separate bucket
and the body of the service definition is managed as a JSON blob, similar to that of [JSON service registry](JSON-Service-Management.html).

Support is enabled by adding the following module into the overlay:

```xml
<dependency>
     <groupId>org.apereo.cas</groupId>
     <artifactId>cas-server-support-aws-s3-service-registry</artifactId>
     <version>${cas.version}</version>
</dependency>
```

## Configuration

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#amazon-s3-service-registry).

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

## Auto Initialization

Upon startup and configuration permitting, the registry is able to auto initialize itself from default JSON service 
definitions available to CAS. See [this guide](AutoInitialization-Service-Management.html) for more info.
