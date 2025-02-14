---
layout: default
title: CAS - CosmosDb Service Registry
---

# CosmosDb Service Registry

Stores registered service data in an [Azure CosmosDb](https://docs.microsoft.com/en-us/azure/cosmos-db/introduction) instance.

Support is enabled by adding the following module into the overlay:

```xml
<dependency>
     <groupId>org.apereo.cas</groupId>
     <artifactId>cas-server-support-cosmosdb-service-registry</artifactId>
     <version>${cas.version}</version>
</dependency>
```

## Configuration

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#cosmosdb-service-registry).

## Troubleshooting

To enable additional logging, configure the log4j configuration file to add the following levels:

```xml
...
<AsyncLogger name="com.microsoft.azure" level="debug" additivity="false">
    <AppenderRef ref="console"/>
    <AppenderRef ref="file"/>
</AsyncLogger>
...
```


## Auto Initialization

Upon startup and configuration permitting, the registry is able to auto initialize itself from default JSON service definitions available to CAS. See [this guide](AutoInitialization-Service-Management.html) for more info.