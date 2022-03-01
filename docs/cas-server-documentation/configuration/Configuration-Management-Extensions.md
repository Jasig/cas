---
layout: default
title: CAS - Configuration Extensions
category: Configuration
---

{% include variables.html %}

# Extending CAS Configuration

Being a [Spring Boot](https://github.com/spring-projects/spring-boot) application at its core, designing and extending CAS configuration components 
very much comes down to [the following guide](https://docs.spring.io/spring-boot/docs/current/reference/html/) some aspects 
of which are briefly highlighted in this document.

## Configuration Components

This is the recommended approach to create additional Spring beans, override existing ones and inject your own 
custom behavior into the CAS application runtime.

Given CAS’ adoption of Spring Boot, most if not all of the old XML configuration is transformed into `@Configuration` 
components. These are classes declared by each relevant module that are automatically picked up at runtime whose job 
is to declare and configure beans and register them into the application context. Another way of thinking about it 
is, components that are decorated with `@Configuration` are loose equivalents of old XML configuration files that 
are highly organized where `<bean>` tags are translated to java methods tagged with `@Bean` and configured dynamically.

### Design

To design your own configuration class, take inspiration from the following sample:

```java
package org.apereo.cas.custom.config;

@Configuration(value = "SomethingConfiguration", proxyBeanMethods = false)
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class SomethingConfiguration {

    @Autowired
    private CasConfigurationProperties casProperties;

    @Autowired
    @Qualifier("someOtherBeanId")
    private SomeBean someOtherBeanId;

    @RefreshScope(proxyMode = ScopedProxyMode.DEFAULT)
    @Bean
    public MyBean myBean() {
        return new MyBean();
    }
} 
```

- The `@Bean` definitions can also be tagged with `@RefreshScope(proxyMode = ScopedProxyMode.DEFAULT)` to become auto-reloadable when the CAS 
  context is refreshed as a result of an external property change.
- `@Configuration` classes can be assigned an order with `@Order(1984)` which would place 
  them in an ordered queue waiting to be loaded in that sequence.
- To be more explicit, `@Configuration` classes can also be loaded exactly before/after 
  another `@Configuration` component with `@AutoConfigureBefore` or `@AutoConfigureAfter` annotations.

<div class="alert alert-info"><strong>To Build & Beyond</strong><p>Note that compiling configuration classes and any other
piece of Java code that is put into the CAS Overlay may require additional CAS modules and dependencies on the classpath. You will need
to study the CAS codebase and find the correct modules that contain the components you need, such 
as <code>CasConfigurationProperties</code> and others.</p></div>

### Register

How are `@Configuration` components picked up? Each CAS module declares its set of configuration components as such, 
per guidelines [laid out by Spring Boot](https://docs.spring.io/spring-boot/docs/current/reference/html/):

- Create a `src/main/resources/META-INF/spring.factories` file
- Add the following into the file:

```properties
org.springframework.boot.autoconfigure.EnableAutoConfiguration=org.apereo.cas.custom.config.SomethingConfiguration
```

### Overrides

What if you needed to override the definition of a CAS-provided bean and replace it entirely with your own?

This is where `@Conditional` components come to aid. Most component/bean definitions in CAS are registered 
with some form of `@Conditional` tag that indicates to the bootstrapping process to ignore their 
creation, if *a bean definition with the same id* is already defined. This means you can create 
your own configuration class, register it and the design a `@Bean` definition only to have the 
context utilize yours rather than what ships with CAS by default.

<div class="alert alert-info"><strong>Bean Names</strong><p>To correctly define a conditional <code>Bean</code>, 
you generally need to make sure your own bean definition is created using the same name or identifier as its original equivalent. 
It is impractical and certainly overwhelming to document all runtime bean definitions and their identifiers. So, you will
need to study the CAS codebase to find the correct configuration classes and bean definitions to note their name.</p></div>

### Feature Toggles

You can control the list of auto-configuration classes to exclude them in the `cas.properties` file:

```properties
spring.autoconfigure.exclude=org.apereo.cas.custom.config.SomethingConfigurationClass
```
     
While the above allows control over individual auto-configuration classes, in some cases it may be desirable
to entirely disable a feature altogether by excluding all applicable auto-configuration classes without having to
identify all of them. This can be done using the following feature toggles:

<table>
    <thead>
    
    <th>Feature</th>
    <th>Property</th>
    </thead>
    <tbody>
        {% for module in site.data[siteDataVersion]["features"] %}
            {% assign moduleEntry = module[1] | sort: "feature" %}
            {% for cfg in moduleEntry %}
                <tr>
                    <td><code data-bs-toggle="tooltip" 
                        data-bs-placment="top" data-bs-html="true" 
                        title="{{ cfg.type }}">{{ cfg.feature }}</code>
                    </td>
                    <td><code>{{ cfg.property }}</code></td>
                </tr>
            {% endfor %}
        {% endfor %}
    </tbody>
</table>

<div class="alert alert-info mt-3"><strong>Usage</strong><p>Note that not every single CAS feature may be registered in the <i>Feature Catalog</i> and as such regarded as a standalone feature. The catalog continues to grow throughout the CAS release lifecycle to recognize more modules as grouped distinct features, allowing for a one-shop store to disable or enable a given CAS feature.</p></div>

Note that the above setting enforces conditional access to the auto-configuration class where a whole suite of `@Bean`s would be included or excluded in the application context upon initialization and startup. Conditional inclusion or exclusion of beans generally has consequences when it comes to `@RefreshScope` and [supporting refreshable beans](Configuration-Management-Reload.html). Note that feature modules are *not refreshable* at this point; they are processed on startup and will either be included in the assembled application context or skipped entirely, depending on the result of the enforced condition.

## CAS Properties

The collection of CAS-provided settings are all encapsulated 
inside a `CasConfigurationProperties` component. This is a parent class that brings all elements of the 
entire CAS platform together and binds values to the relevant fields inside in a 
very type-safe manner. The [configuration binding](Configuration-Server-Management.html) is 
typically done via `@EnableConfigurationProperties(CasConfigurationProperties.class)` on the actual configuration class. 

<div class="alert alert-info"><strong>Prefix Notation</strong><p>Note that all CAS-provided settings 
exclusively begin with the prefix <code>cas</code>. Other frameworks and packages upon which CAS
depends may present their own configuration naming scheme. Note the difference.</p></div>

If you wish to design your own and extend the CAS configuration file, you can surely follow 
the same approach with the `@EnableConfigurationProperties` annotation or use the good ol' `@Value`.
