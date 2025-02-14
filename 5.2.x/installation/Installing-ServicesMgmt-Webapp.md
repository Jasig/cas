---
layout: default
title: CAS - Services Management Webapp
---

# Services Management Webapp

The services management webapp is no longer part of the CAS server and
is a standalone Spring Boot web application that ships with an embedded Apache Tomcat container.

* The management webapp is used to add/edit/delete all the CAS services.
* The CAS server loads/relies on all these defined CAS services to process all incoming requests.

<div class="alert alert-warning"><strong>Synchronized Configuration</strong><p>
You <strong>MUST</strong> keep in mind that both applications (the CAS server and the services management webapp)
share the <strong>same</strong> service registry configuration for CAS services.
</p></div>

The management web application is purely an administrative interface that may be deployed in a completely different environment separate from CAS. It allows CAS administrators and application owners delegated access so they can manage and modify policies associated with their applications. The operational capacity of the CAS server itself is not in any way tied to the deployment status of the management web application; you may decide to take the application offline for maintenance or completely remove it from your deployment scenario at any given time.

Note that for certain type of service registry backends, deploying the management web application is a requirement since it acts as the interface fronting CRUD operations that deal with the storage backend. The absence of the management web application means that you will need to find alternative tooling to *manually* interact with your registry of choice and the storage backend it employs.

## Installation

The source code repository for the CAS management web application is [located here](https://github.com/apereo/cas-management).

### Maven

A template overlay for the management webapp is [provided here](https://github.com/apereo/cas-services-management-overlay).

### Gradle

A template overlay for the management webapp is [provided here](https://github.com/apereo/cas-services-management--gradle-overlay).

## Configuration

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#management-webapp).

The management web application is primarily controlled by a `management.yml|properties` file. However, all strategies outlined in [CAS configuration management](Configuration-Management.html) equally apply here as well in the way that settings are defined, passed and resolved. The primary difference of course is the name of the configuration file.

## Services Registry

The [persistence storage](Service-Management.html) for services **MUST** be the same as that of the CAS server. The same service registry component that is configured for the CAS server, including module and settings, needs to be configured in the same exact way for the management web application.

## User Attributes
The set of user attributes defined in the CAS Server's [authentication attributes](Configuration-Properties.html#authentication-attributes) or [attribute resolution](Attribute-Resolution.html) configurations should be mapped in the Service Management webapp's configuration using the [stub-based attribute repository](Configuration-Properties.html#attributes). This will make the attributes available for selction in the management webapp's various user attributes-related dropdowns.

## Authentication

Access to the management webapp can be configured via the following strategies.

### CAS Server

The management web application can be configured to authenticate against a CAS server. To activate this mode, simply specify the location of the CAS server via configuration settings. To disable this mode, blank out the settings that describe the external CAS server.

If this strategy is used, access strategy rules can then further be controlled via the outlined strategies for authorization.

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#management-webapp).

### IP Address

The management web application can be configured to allow anonymous access if the request's IP address matches a predefined regular expression. To disable this mode, blank out the settings that describe the external CAS server. If this strategy is used, access strategy and authorized rules do not apply as the resolved identity is simply anonymous.

<div class="alert alert-danger"><strong>Be Careful</strong><p>Keep in mind that this authentication
mechanism should only be enabled for internal network clients with relatively static IP addresses.</p></div>

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#management-webapp).

### Anonymous

The management web application can be configured to allow anonymous access if no other authentication strategy is defined. This mode is mostly useful for development and testing while additional warnings show up in the logs that explain the caveats of this option.

<div class="alert alert-danger"><strong>Be Careful</strong><p>Be sure to specify an authentication strategy, as failure to do so would effectively leave the management web application open for access.</p></div>

## Authorization

Learn how to control access to the management web application. The following options describe how authorization rules for authenticated users are generated and made available to the management web application. Once roles, permissions and such are produced then the user authenticated profile that is now fully populated is compared to rules required and defined by the cas management web application for access. Essentially, the following steps execute:

1. Load roles and permissions required of authenticated users to have to enter the management web application.
2. Authenticate a given user and establish a profile.
3. Populate profile with authorization rules that contain roles, permissions, etc.
4. Compare the profile against required rules and permissions.

### Static List of Users

#### Properties

By default, access is limited to a static list of users whose credentials may be specified in a single properties file which is watched and monitored at runtime for changes and reloaded automatically. The format of the file which houses a list of authorized users to access the web application mimics that of Spring Security, which is:

```properties
# casuser=notused,ROLE_ADMIN
```

The format of the file is as such:

- `casuser`: This is the authenticated user id received from CAS
- `notused`: This is the password field that isn't used by CAS. You could literally put any value you want in its place.
- `ROLE_ADMIN`: Role assigned to the authorized user as an attribute, which is then cross checked against CAS configuration.

#### JSON & YAML

File-based authorization rules may also be specified inside a single `.json` or `.yml` that maps usernames to roles and permissions. A JSON example follows:

```json
{
  "casuser" : {
    "@class" : "org.apereo.cas.mgmt.authz.json.UserAuthorizationDefinition",
    "roles" : [ "ROLE_ADMIN" ],
    "permissions" : [ "CAN_DO_XYZ" ]
  }
}
```

### Attribute

Alternatively, the authorization generator examines the CAS validation response and principal for attributes
and will grant access if an attribute name matches the value of `adminRoles` defined in the configuration.

### LDAP

Access to the management web application may also be controlled directly by querying an LDAP server, via the following modes.

#### Groups

The authorization framework will search for groups of which the user is a member. Retrieved groups and roles are the compared with the management webapp configuration to find a match (i.e. `ROLE_ADMIN`).

#### Attributes

The authorization framework will examine the attributes assigned to the user, looking for a predefined role attribute to compare with the configuration for access (i.e. `ROLE_ADMIN`).

Support is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
  <groupId>org.apereo.cas</groupId>
  <artifactId>cas-management-webapp-support-ldap</artifactId>
  <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#ldap-authorization).

### Custom

You may also decide to design your own authorization generator for the management web application:

```java
package org.apereo.cas.support;

@Configuration("myConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class MyConfiguration {

 /**
  * Decide how roles and permissions should be stuffed into the authenticated profile.
  */
  @Bean
  public AuthorizationGenerator authorizationGenerator() {
      ...
  }

 /**
  * Decide the profile should be compared to the required rules for access.
  */
  @Bean
  public Authorizer managementWebappAuthorizer() {
      ...
  }

}
```

[See this guide](Configuration-Management-Extensions.html) to learn more about how to register configurations into the CAS runtime.
