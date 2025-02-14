---
layout: default
title: CAS - LDAP Service Registry
---

# LDAP Service Registry

Service registry implementation which stores the services in a LDAP Directory and attempts to *map* service records to LDAP entrues in order to configure 
settings for retrieval, search and persistence of service definitions. By default, entries are assigned the `objectclass` that is `casRegisteredService` attribute and are looked up by the `uid` attribute.

Support is enabled by adding the following module into the overlay:

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-ldap-service-registry</artifactId>
    <version>${cas.version}</version>
</dependency>
```

## Configuration

The default mapper has support for the following optional items:

| Field                             | Default Value
|-----------------------------------|---------------------------------------------------
| `objectClass`                     | casRegisteredService
| `serviceDefinitionAttribute`      | description
| `idAttribute`                     | uid

Service definitions are by default stored inside the `serviceDefinitionAttribute` attribute as
JSON objects. The format and syntax of the JSON is identical to that of
[JSON Service Registry](JSON-Service-Management.html). That's all, as far as the schema goes.

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#ldap-service-registry).

## Auto Initialization

Upon startup and configuration permitting, the registry is able to auto initialize itself from default JSON service definitions available to CAS. See [this guide](AutoInitialization-Service-Management.html) for more info.
