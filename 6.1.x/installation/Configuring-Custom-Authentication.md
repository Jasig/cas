---
layout: default
title: CAS - Design Authentication Strategies
category: Authentication
---

# Custom Authentication Strategies

While authentication support in CAS for a variety of systems is somewhat comprehensive and complex, a common deployment use case is the task of designing custom authentication schemes. This document describes the necessary steps needed to design and register a custom authentication strategy (i.e. `AuthenticationHandler`) in CAS.

This guide really is intended for developers with a basic-to-medium familiarity with Spring, Spring Boot and Spring Webflow. This is *NOT* a tutorial to be used verbatim via copy/paste. It is instead a recipe for developers to extend CAS based on specialized requirements.

## Overview

The overall tasks may be categorized as such:

1. Design the authentication handler.
2. Register the authentication handler with the CAS authentication engine.
3. Let CAS to recognize the authentication configuration.

## Design

First step is to define the skeleton for the authentication handler itself. This is the core principal component whose job is to declare support for a given type of credential only to then attempt to validate it and produce a successful result. The core parent component from which all handlers extend is the `AuthenticationHandler` interface.

With the assumption that the type of credentials used here deal with the traditional username and password, noted by the infamous `UsernamePasswordCredential` below, a more appropriate skeleton to define for a custom authentication handler may seem like the following example:

```java
package com.example.cas;

public class MyAuthenticationHandler extends AbstractUsernamePasswordAuthenticationHandler {
    ...
    protected HandlerResult authenticateUsernamePasswordInternal(final UsernamePasswordCredential credential,
                                                                 final String originalPassword) {
        if (everythingLooksGood()) {
            return createHandlerResult(credential,
                    this.principalFactory.createPrincipal(username), null);
        }
        throw new FailedLoginException("Sorry, you are a failure!");
    }
    ...
}
```

### Review

- Authentication handlers have the ability to produce a fully resolved principal along with attributes. If you have the ability to retrieve attributes from the same place as the original user/principal account store, the final `Principal` object that is resolved here must then be able to carry all those attributes and claims inside it at construction time.

- The last parameter, `null`, is effectively a collection of warnings that is eventually worked into the authentication chain and conditionally shown to the user. Examples of such warnings include password status nearing an expiration date, etc.

- Authentication handlers also have the ability to block authentication by throwing a number of specific exceptions. A more common exception to throw back is `FailedLoginException` to note authentication failure. Other specific exceptions may be thrown to indicate abnormalities with the account status itself, such as `AccountDisabledException`.

- Various other components such as `PrincipalNameTransformer`s, `PasswordEncoder`s and such may also be injected into our handler if need be, though these are skipped for now in this post for simplicity.

## Register

Once the handler is designed, it needs to be registered with CAS and put into the authentication engine.
This is done via the magic of `@Configuration` classes that are picked up automatically at runtime, per your approval,
whose job is to understand how to dynamically modify the application context.

```java
package com.example.cas;

@Configuration("MyAuthenticationEventExecutionPlanConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class MyAuthenticationEventExecutionPlanConfiguration
                    implements AuthenticationEventExecutionPlanConfigurer {
    @Autowired
    private CasConfigurationProperties casProperties;

    @Bean
    public AuthenticationHandler myAuthenticationHandler() {
        final MyAuthenticationHandler handler = new MyAuthenticationHandler();
        /*
            Configure the handler by invoking various setter methods.
            Note that you also have full access to the collection of resolved CAS settings.
            Note that each authentication handler may optionally qualify for an 'order`
            as well as a unique name.
        */
        return h;
    }

    @Override
    public void configureAuthenticationExecutionPlan(final AuthenticationEventExecutionPlan plan) {
        if (feelingGoodOnAMondayMorning()) {
            plan.registerAuthenticationHandler(myAuthenticationHandler());
        }
    }
}
```


Now that we have properly created and registered our handler with the CAS authentication machinery, we just need to ensure that CAS is able to pick up our special configuration. To do so, create a `src/main/resources/META-INF/spring.factories` file and reference the configuration class in it as such:

```properties
org.springframework.boot.autoconfigure.EnableAutoConfiguration=com.example.cas.MyAuthenticationEventExecutionPlanConfiguration
```

To learn more about the registration strategy, [please see this guide](http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-developing-auto-configuration.html).

At runtime, CAS will try to automatically detect all components and beans that advertise themselves as `AuthenticationEventExecutionPlanConfigurers`. Each detected component is then invoked to register its own authentication execution plan. The result of this operation at the end will produce a ready-made collection of authentication handlers that are ready to be invoked by CAS in the given order defined, if any.
