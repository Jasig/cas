---
layout: default
title: CAS - Configuring Multifactor Authentication Custom Triggers
---

# Multifactor Authentication Custom Triggers

To create your own custom multifactor authentication trigger, you will need to design a component that is able to resolve events in the CAS authentication chain. The trigger's (i.e. event resolver's) job is to examine a set of conditions and requirements and provide an event id to CAS that would indicate the next step in the authentication flow.

A typical custom trigger, as an example, might be:

- Activate MFA provider identified by `mfa-duo` if the client browser's IP address matches the pattern `123.+`.

Note that:

- You are really not doing anything *custom* per se. All built-in CAS triggers behave in the same exact way when they attempt to resolve the next event.
- As you will observe below, the event resolution machinery is completely oblivious to multifactor authentication; all it cares about is finding the next event in the chain in a very generic way. Our custom implementation of course wants to have the next event deal with some form of MFA via a provider, but in theory we could have resolved the next event to be `hello-world`.

## Requirements

You will need to have compile-time access to the following modules in the Overlay:

```xml
<dependency>
  <groupId>org.apereo.cas</groupId>
  <artifactId>cas-server-core-webflow</artifactId>
  <version>${cas.version}</version>
</dependency>
```

These are modules that ship with CAS by default and thou shall mark them with a `compile` or `provided` scope in your build configuration.

## Design Triggers

The below example demonstrates a reasonable outline of a custom event resolver:

```java
package org.apereo.cas.custom.mfa;

public class CustomWebflowEventResolver extends AbstractCasWebflowEventResolver {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomWebflowEventResolver.class);

    @Autowired
    private CasConfigurationProperties casProperties;

    @Override
    protected Set<Event> resolveInternal(final RequestContext context) {
        final RegisteredService service = WebUtils.getRegisteredService(context);
        final Authentication authentication = WebUtils.getAuthentication(context);
        final HttpServletRequest request = WebUtils.getHttpServletRequest(context);

        final Map<String, MultifactorAuthenticationProvider> providerMap =
            WebUtils.getAllMultifactorAuthenticationProviders(this.applicationContext);

        /*
         * Choose the provider you need from the above map (i.e. 'mfa-duo`)
         */
        final MultifactorAuthenticationProvider provider = ...

        if (yesWeDoingMfaBasedOnClientIpAddress()) {
            final Map eventAttributes = buildEventAttributeMap(authentication.getPrincipal(), service, provider));
            final Event event = validateEventIdForMatchingTransitionInContext(provider.getId(), context, eventAttributes);
            return ImmutableSet.of(event);
        }
        LOGGER.warn("Not doing MFA, sorry.");
        return null;
    }
}
```

## Register Triggers

The event resolver trigger then needs to be registered. [See this guide](Configuration-Management-Extensions.html) for better details.

The below example demonstrates a reasonable outline of a custom event resolver:

```java
package org.apereo.cas.custom.config;

@Configuration("SomethingConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class SomethingConfiguration {

    @Autowired
    @Qualifier("initialAuthenticationAttemptWebflowEventResolver")
    private CasDelegatingWebflowEventResolver initialEventResolver;

    @Bean
    public CasWebflowEventResolver customWebflowEventResolver() {
        return new CustomWebflowEventResolver();
    }

    @PostConstruct
    public void initialize() {
        initialEventResolver.addDelegate(customWebflowEventResolver());
    }
}
```

Do not forget to register the configuration class with CAS. [See this guide](Configuration-Management-Extensions.html) for better details.
