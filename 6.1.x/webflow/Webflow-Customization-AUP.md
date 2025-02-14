---
layout: default
title: CAS - Web Flow Acceptable Usage Policy
category: Webflow Management
---

# Acceptable Usage Policy

Also known as *Terms of Use* or *EULA*, CAS presents the ability to allow the user to accept the usage policy before moving on to the application.
Production-level deployments of this feature would require modifications to the flow such that the retrieval
and/or acceptance of the policy would be handled via an external storage mechanism such as LDAP or JDBC.

## Configuration

Support is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
  <groupId>org.apereo.cas</groupId>
  <artifactId>cas-server-support-aup-webflow</artifactId>
  <version>${cas.version}</version>
</dependency>
```

Customize the policy by modifying the `src/main/resources/templates/casAcceptableUsagePolicyView.html`. See [this guide](../ux/User-Interface-Customization.html)
to learn more about user interface customizations. Note that the view here should have full access to the resolved principal and attributes,
if you wish to dynamically alter the page to present different text, etc.

<div class="alert alert-info"><strong>Webflow Sequence</strong><p>Remember that acceptable usage policy executes
after a successful authentication event where CAS has already established the authentication principal, since the 
policy record is strongly tied to the identified user record. Implementing this feature before the authentication event
would require rather heavy modifications to the CAS webflow as well as alternative means of storing and remembering decisions
such as cookies or browser storage, etc.</p></div>

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#acceptable-usage-policy).

## Per Service 

Acceptable usage policy can be disabled and skipped on a per-service basis:

```json
{
  "@class": "org.apereo.cas.services.RegexRegisteredService",
  "serviceId": "https://app.example.org",
  "name": "Example",
  "id": 1,
  "properties" : {
    "@class" : "java.util.HashMap",
    "acceptableUsagePolicyEnabled" : {
      "@class" : "org.apereo.cas.services.DefaultRegisteredServiceProperty",
      "values" : [ "java.util.HashSet", [ "false" ] ]
    }
  }
}
```                                             

## Storage Mechanism

Usage policy user decisions are stored and remembered via the following ways. 

In almost all storage strategies, CAS allows the deployer
to detect the current user's policy choice via a CAS single-valued `boolean` attribute.
The attribute must be resolved using the [CAS attribute resolution strategy](../integration/Attribute-Resolution.html).
If the attribute contains a value of `false`, CAS will attempt to
ask for policy acceptance. Upon accepting the policy, the result will be stored back into storage.

### Default

By default the task of remembering the user's choice is kept in memory by default and will be lost upon
container restarts and/or in clustered deployments. This option is only useful during development, testing
and demos and is not at all suitable for production.

The scope of the default storage mechanism can be adjusted from the default of GLOBAL (described above) to
AUTHENTICATION which will result in the user having to agree to the policy during each authentication event.
The user will not have to agree to the policy when CAS grants access based on an existing ticket granting
ticket cookie. 

### Groovy

Alternatively, CAS can be configured to use a Groovy script to verify status of policies and store results. The script should match the following:

```groovy
import org.apereo.cas.authentication.principal.*
import org.apereo.cas.authentication.*
import org.apereo.cas.util.*
import org.apereo.cas.aup.*
import org.springframework.webflow.execution.*

def verify(Object[] args) {
    def requestContext = args[0]
    def credential = args[1]
    def applicationContext = args[2]
    def principal = args[3]
    def logger = args[4]
    ...
    if (policyAccepted()) {
        return AcceptableUsagePolicyStatus.accepted(principal)
    }
    return AcceptableUsagePolicyStatus.denied(principal)
}

def submit(Object[] args) {
     def requestContext = args[0]
     def credential = args[1]
     def applicationContext = args[2]
     def principal = args[3]
     def logger = args[4]
     ...
     return true
 }
```

The parameters passed are as follows:

| Parameter             | Description
|-----------------------|-----------------------------------------------------------------------
| `requestContext`      | The object representing the Spring Webflow `RequestContext`.
| `credential`          | The object representing the authentication `Credential`.
| `applicationContext`  | The object representing the Spring `ApplicationContext`.
| `principal`           | The object representing the authenticated `Principal`.
| `logger`              | The object responsible for issuing log messages such as `logger.info(...)`.

### LDAP

Alternatively, CAS can be configured to use LDAP as the storage mechanism. Upon accepting the policy, the result will be stored back into LDAP and
remembered via the same attribute. Support is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
  <groupId>org.apereo.cas</groupId>
  <artifactId>cas-server-support-aup-ldap</artifactId>
  <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#ldap-1).

### MongoDb

CAS can be configured to use a MongoDb instance as the storage mechanism. Upon accepting the policy, the adopter is expected to provide a collection name where the 
decision is kept and the document is assumed to contain a `username` column as well as one that matches the AUP attribute name defined.

Support is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
  <groupId>org.apereo.cas</groupId>
  <artifactId>cas-server-support-aup-mongo</artifactId>
  <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#acceptable-usage-policy).

### Redis

CAS can be configured to use a Redis instance as the storage mechanism. Decisions are mapped to a combination of CAS username and the designated AUP attribute name.

Support is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
  <groupId>org.apereo.cas</groupId>
  <artifactId>cas-server-support-aup-redis</artifactId>
  <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#acceptable-usage-policy).

### CouchDb

CAS can be configured to use a CouchDb instance as the storage mechanism. Upon accepting the policy, the adopter is expected to provide a collection name where the
decision is kept and the document is assumed to contain a `username` column as well as one that matches the AUP attribute name defined.

Support is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
  <groupId>org.apereo.cas</groupId>
  <artifactId>cas-server-support-aup-couchdb</artifactId>
  <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#acceptable-usage-policy).

### JDBC

CAS can be configured to use a database as the storage mechanism. Upon accepting the policy, the adopter is expected to provide a table name where the 
decision is kept and the table is assumed to contain a `username` column as well as one that matches the AUP attribute name defined.

Support is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
  <groupId>org.apereo.cas</groupId>
  <artifactId>cas-server-support-aup-jdbc</artifactId>
  <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#acceptable-usage-policy).

### REST

CAS can be configured to use a REST API as the storage mechanism. Upon accepting the policy, the API is contacted passing along a `username` parameter
who has accepted the policy. The expected response status code is `200`.

Support is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
  <groupId>org.apereo.cas</groupId>
  <artifactId>cas-server-support-aup-rest</artifactId>
  <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#acceptable-usage-policy).

### Custom

If you wish to design your own storage mechanism, you may follow the below approach:

```java
package org.apereo.cas.custom;

@Configuration("myUsagePolicyConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class MyUsagePolicyConfiguration {

    @Bean
    public AcceptableUsagePolicyRepository acceptableUsagePolicyRepository() {
      ...
    }

}
```

[See this guide](../configuration/Configuration-Management-Extensions.html) to learn more about how to register configurations into the CAS runtime.
