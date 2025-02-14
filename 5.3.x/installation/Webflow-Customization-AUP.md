---
layout: default
title: CAS - Web Flow Acceptable Usage Policy
---

# Acceptable Usage Policy

CAS presents the ability to allow the user to accept the usage policy before moving on to the application.
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

Customize the policy by modifying the `src/main/resources/templates/casAcceptableUsagePolicyView.html`. See [this guide](User-Interface-Customization.html)
to learn more about user interface customizations. Note that the view here should have full access to the resolved principal and attributes,
if you wish to dynamically alter the page to present different text, etc.

<div class="alert alert-info"><strong>Webflow Sequence</strong><p>Remember that acceptable usage policy executes
after a successful authentication event where CAS has already established the authentication principal, since the 
policy record is strongly tied to the identified user record. Implement this feature before the authentication event
would require heavy modifications to the CAS webflow as well as alternative means of storing and remembering decisions
such as cookies or browser storage, etc.</p></div>

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#acceptable-usage-policy).

## Storage Mechanism

Usage policy user decisions are stored and remembered via the following ways. 

In almost all storage strategies, CAS allows the deployer
to detect the current user's policy choice via a CAS single-valued `boolean` attribute.
The attribute must be resolved using
the [CAS attribute resolution strategy](../integration/Attribute-Resolution.html).
If the attribute contains a value of `false`, CAS will attempt to
ask for policy acceptance. Upon accepting the policy, the result will be stored back into storage.

### Default

By default the task of remembering the user's choice is kept in memory by default and will be lost upon
container restarts and/or in clustered deployments. This option is only useful during development, testing
and demos and is not at all suitable for production.

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

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#ldap-1).

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

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#acceptable-usage-policy).

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

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#acceptable-usage-policy).

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

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#acceptable-usage-policy).

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

[See this guide](Configuration-Management-Extensions.html) to learn more about how to register configurations into the CAS runtime.
