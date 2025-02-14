---
layout: default
title: CAS - InMemory Service Registry
---

# InMemory Service Registry

This is an in-memory services management tool seeded from registration beans wired via Spring beans.

```java
@Configuration("myConfiguration")
public class MyConfiguration {

  @Bean
  public List inMemoryRegisteredServices() {
      final List services = new ArrayList<>();
      final RegexRegisteredService service = new RegexRegisteredService();
      ...
      services.add(service);
      return services;
  }
}
```

[See this guide](Configuration-Management-Extensions.html) to learn more about how to register configurations into the CAS runtime.

Given registered services are injected into the context as Spring bean definitions, you will need to consult the project's javadocs
to learn more about the CAS service API, and how to inject various other components into the service definition. 

<div class="alert alert-info"><strong>Caveat</strong><p>
This component is <strong>NOT</strong> suitable for use with the service management webapp since it does not persist data.
On the other hand, it is perfectly acceptable for deployments where the hard-coded configuration is authoritative and good-enough for
service registry data and the UI will not be used where there only exist a handful of applications integrated with CAS.
</p></div>

## Auto Initialization

Upon startup and configuration permitting, the registry is able to auto initialize itself from default JSON service definitions available to CAS. See [this guide](AutoInitialization-Service-Management.html) for more info.