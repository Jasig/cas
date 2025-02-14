---
layout: default
title: CAS - JMX Integration
category: Integration
---

# JMX Integration

The JMX support in CAS provides you with the features to easily and transparently integrate your CAS deployment into a JMX infrastructure. These features are 
designed to work without coupling CAS components to either Spring or JMX interfaces and classes. CAS components need not be aware of either JMX in order 
to take advantage of the Spring JMX features. Invoking JMX operations provided by CAS managed resources can be done via the likes of Java's `jconsole` tool.

The following *wrapper* components are registered into a JMX infrastructure and sit on top of a well-defined CAS feature, 
concept or component to provide remote operations or insight:

- A JMX managed resource for the CAS services management facility
- A JMX managed resource for the CAS ticket registry

Additional wrappers and components will be worked out and added iteratively.

Support is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
  <groupId>org.apereo.cas</groupId>
  <artifactId>cas-server-support-jmx</artifactId>
  <version>${cas.version}</version>
</dependency>
```
