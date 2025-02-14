---
layout: default
title: CAS - Attribute Release Consent
---

# Attribute Consent

CAS provides the ability to enforce user consent upon attribute release.

<div class="alert alert-warning"><strong>Usage Warning!</strong><p>This feature, currently in development, is very experimental and incomplete at this point.</p></div>

Support is enabled by including the following module in the Overlay:

```xml
<dependency>
     <groupId>org.apereo.cas</groupId>
     <artifactId>cas-server-support-consent-webflow</artifactId>
     <version>${cas.version}</version>
</dependency>
```

## Storage

User consent decisions may be stored and remembered using one of the following options.

### JDBC

Support is enabled by including the following module in the Overlay:

```xml
<dependency>
     <groupId>org.apereo.cas</groupId>
     <artifactId>cas-server-support-consent-jdbc</artifactId>
     <version>${cas.version}</version>
</dependency>
```
