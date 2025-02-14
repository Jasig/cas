---
layout: default
title: CAS - Trusted Authentication
---

# Trusted Authentication
The trusted authentication handler provides support for trusting authentication performed by some other component
in the HTTP request handling chain. Proxies (including Apache in a reverse proxy scenario) are the most common
components that perform authentication in front of CAS.

Trusted authentication handler support is enabled by including the following dependency in the Maven WAR overlay:

```xml
<dependency>
  <groupId>org.jasig.cas</groupId>
  <artifactId>cas-server-support-trusted-webflow</artifactId>
  <version>${cas.version}</version>
</dependency>
```


## Configure Trusted Authentication Handler
Update `deployerConfigContext.xml` according to the following template:

```xml
...
<entry key-ref="principalBearingCredentialsAuthenticationHandler"
       value-ref="trustedPrincipalResolver" />
<util:list id="authenticationMetadataPopulators">
  <ref bean="successfulHandlerMetaDataPopulator" />
</util:list>
...
```
