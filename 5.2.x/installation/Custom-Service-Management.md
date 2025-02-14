---
layout: default
title: CAS - Custom Service Registry
---

# Custom Service Registry

If you wish to design your own implementation of a service registry, you will need to inject your implementation into CAS as such:

```java
package org.apereo.cas.support;

@Configuration("myConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class MyConfiguration {

  @Bean
  @RefreshScope
  public ServiceRegistryDao serviceRegistryDao() {
      ...
  }
}
```

At a minimum, your overlay will need to include the following modules:

```xml
<dependency>
     <groupId>org.apereo.cas</groupId>
     <artifactId>cas-server-core-services</artifactId>
     <version>${cas.version}</version>
</dependency>
```

[See this guide](Configuration-Management-Extensions.html) to learn more about how to register configurations into the CAS runtime.
