---
layout: default
title: CAS - Services Management Webapp
---
# Services Management Webapp

The services management webapp is no longer part of the CAS server and
is a standalone web application.

* The management webapp is used to add/edit/delete all the CAS services
* The CAS server loads/relies on all these defined CAS services to process all incoming requests.

<div class="alert alert-warning"><strong>Synchronized Configuration</strong><p>
You <strong>MUST</strong> keep in mind that both applications (the CAS server and the services management webapp)
share the <strong>same</strong> service registry configuration for CAS services.
</p></div>

A sample overlay for the services management webapp is provided
 here: [https://github.com/Apereo/cas-services-management-overlay](https://github.com/Apereo/cas-services-management-overlay)
 
 To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html).

## Services Registry

The [persistence storage](Service-Management.html) for services **MUST** be the same as that of the CAS server.

## Authentication Method

Access to the management webapp is by default configured to authenticate against a CAS server. 
To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html).

## Authorization

Learn how to control access to the management web application. 

### Static List of Users

By default, access is limited to a static list of users whose credentials may be 
specified in a `user-details.properties` file that should be available on the runtime classpath. 

### Attribute

Alternatively, the authorization generator examines the CAS validation response and principal for attributes
and will grant access if an attribute name matches the value of `adminRoles` defined in the configuration.

### LDAP

Support is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
  <groupId>org.apereo.cas</groupId>
  <artifactId>cas-management-webapp-support-ldap</artifactId>
  <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html).
