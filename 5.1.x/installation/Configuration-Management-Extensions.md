---
layout: default
title: CAS - Configuration Extensions
---

# Extending CAS Configuration

Being a [Spring Boot](https://github.com/spring-projects/spring-boot) application at its core, designing and extending CAS configuration components very much comes down to [the following guide](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-developing-auto-configuration.html) some aspects of which are briefly highlighted in this document.

## Configuration Components

This is the recommended approach to create additional Spring beans, override existing ones and simply inject your own custom behavior into the CAS application runtime.

<div class="alert alert-info"><strong>No XML</strong><p>You are still given the ability to configure and inject beans into the application context via XML configuration files. We STRONGLY recommend that you abandon that approach altogether.</p></div>

Given CAS’ adoption of Spring Boot, most if not all of the old XML configuration is transformed into `@Configuration` components. These are classes declared by each relevant module that are automatically picked up at runtime whose job is to declare and configure beans and register them into the application context. Another way of thinking about it is, components that are decorated with `@Configuration` are loose equivalents of old XML configuration files that are highly organized where `<bean>` tags are translated to java methods tagged with `@Bean` and configured dynamically.

### Design

To design your own configuration class, take inspiration from the following sample:

```java
package org.apereo.cas.custom.config;

@Configuration("SomethingConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class SomethingConfiguration {

    @Autowired
    private CasConfigurationProperties casProperties;

    @Autowired
    @Qualifier("someOtherBeanId")
    private SomeBean someOtherBeanId;

    @RefreshScope
    @Bean
    public MyBean myBean() {
        return new MyBean();
    }
} 
```

- The `@Bean` definitions can also be tagged with `@RefreshScope` to become auto-reloadable when the CAS context is refreshed as a result of an external property change.
- `@Configuration` classes can be assigned an order with `@Order(1984)` which would place them in an ordered queue waiting to be loaded in that sequence.
- To be more explicit, `@Configuration` classes can also be loaded exactly before/after another `@Configuration` component with `@AutoConfigureBefore` or `@AutoConfigureAfter` annotations.

### Register

How are `@Configuration` components picked up? Each CAS module declares its set of configuration components as such, per guidelines [laid out by Spring Boot](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-developing-auto-configuration.html):

- Create a `src/main/resources/META-INF/spring.factories` file
- Add the following into the file:

```properties
org.springframework.boot.autoconfigure.EnableAutoConfiguration=org.apereo.cas.custom.config.SomethingConfiguration
```

### Overrides

What if you needed to override the definition of a CAS-provided bean and replace it entirely with your own?

This is where `@Conditional` components come to aid. Most component/bean definitions in CAS are registered with some form of `@Conditional` tag that indicates to the bootstrapping process to ignore their creation, if *a bean definition with the same id* is already defined. This means you can create your own configuration class, register it and the design a `@Bean` definition only to have the context utilize yours rather than what ships with CAS by default.

## CAS Properties

The [collection of CAS-provided settings](Configuration-Properties.html) are all encapsulated inside a `CasConfigurationProperties` component. This is a parent class that brings all elements of the entire CAS platform together and binds values to the relevant fields inside in a very type-safe manner. The [configuration binding](Configuration-Server-Management.html) is typically done via `@EnableConfigurationProperties(CasConfigurationProperties.class)` on the actual configuration class. 

<div class="alert alert-info"><strong>Prefix Notation</strong><p>Note that all CAS-provided settings exclusively begin with the prefix <code>cas</code>. Other frameworks and packages upon which CAS depends may present their own configuration naming scheme. Note the difference.</p></div>

If you wish to design your own and extend the CAS configuration file, you can surely follow the same approach with the `@EnableConfigurationProperties` annotation or use the good ol' `@Value`.
