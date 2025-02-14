---
layout: default
title: CAS - Multifactor Authentication Triggers
category: Multifactor Authentication
---

# Multifactor Authentication Triggers

The following triggers can be used to activate and instruct CAS to navigate to a multifactor authentication flow.
To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#multifactor-authentication).

The execution order of multifactor authentication triggers is outlined below:

1. Adaptive
2. Global
3. Opt-In Request Parameter/Header
4. REST Endpoint
5. Groovy Script
6. Principal Attribute Per Application
7. Global Principal Attribute Predicate
8. Global Principal Attribute
9. Global Authentication Attribute
10. Applications
11. Grouper
12. Entity ID Request Parameter
13. Other

Each trigger should properly try to ignore the authentication request, if applicable configuration is not found for its activation and execution. Also note that various CAS modules present and inject their own *internal triggers* into the CAS application runtime in order to translate protocol-specific authentication requests (such as those presented by SAML2 or OpenID Connect) into multifactor authentication flows.

<div class="alert alert-info"><strong>Service Requirement</strong><p>Most multifactor authentication triggers require that the original authentication request submitted to CAS contain a <code>service</code> parameter. Failure to do so will simply result in an initial successful authentication attempt where subsequent requests that carry the relevant parameter will elevate the authentication context and trigger multifactor later. If you need to test a particular trigger, remember to provide the <code>service</code> parameter appropriately to see the trigger in action.</p></div>

The trigger machinery in general should be completely oblivious to multifactor authentication; all it cares about is finding the next event in the chain in a very generic way. This means that it is technically possible to combine multiple triggers each of which may produce a different event in the authentication flow. In the event, having selected a final candidate event, the appropriate component and module that is able to support and respond to the produced event will take over and route the authentication flow appropriately.

## Global

MFA can be triggered for all applications and users regardless of individual settings.

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#multifactor-authentication).

## Per Application

MFA can be triggered for a specific application registered inside the CAS service registry.

```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "^(https|imaps)://.*",
  "id" : 100,
  "name": "test",
  "multifactorPolicy" : {
    "@class" : "org.apereo.cas.services.DefaultRegisteredServiceMultifactorPolicy",
    "multifactorAuthenticationProviders" : [ "java.util.LinkedHashSet", [ "mfa-duo" ] ],
    "bypassEnabled": false,
    "forceExecution": true
  }
}
```

The following fields are accepted by the policy definition

| Field                 | Description
|-----------------------|----------------------------------------------------------------------------------------
| `multifactorAuthenticationProviders` | Set of multifactor provider ids that should trigger for this application.
| `bypassEnabled`           | Whether multifactor authentication should be [bypassed](Configuring-Multifactor-Authentication-Bypass.html) for this service.
| `forceExecution`          | Whether multifactor authentication should forcefully trigger, even if the existing authentication context can be satisfied without MFA.

### Groovy Per Application

Additionally, you may determine the multifactor authentication policy for a registered service using a Groovy script:

```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "^(https|imaps)://.*",
  "id" : 100,
  "name": "test",
  "multifactorPolicy" : {
    "@class" : "org.apereo.cas.services.GroovyRegisteredServiceMultifactorPolicy",
    "groovyScript" : "file:///etc/cas/config/mfa-policy.groovy"
  }
}
```

The script itself may be designed as such by overriding the needed operations where necessary:

```groovy
import org.apereo.cas.services.*
import java.util.*

class GroovyMultifactorPolicy extends DefaultRegisteredServiceMultifactorPolicy {
    @Override
    Set<String> getMultifactorAuthenticationProviders() {
        ...
    }

    @Override
    RegisteredServiceMultifactorPolicyFailureModes getFailureMode() {
        ...
    }

    @Override
    String getPrincipalAttributeNameTrigger() {
        ...
    }

    @Override
    String getPrincipalAttributeValueToMatch() {
        ...
    }

    @Override
    boolean isBypassEnabled() {
        ...
    }

    @Override
    boolean isForceExecution() {
        ...
    }
}
```

Refer to the CAS API documentation to learn more about operations and expected behaviors.

## Global Principal Attribute

MFA can be triggered for all users/subjects carrying a specific attribute that matches one of the conditions below.

- Trigger MFA based on a principal attribute(s) whose value(s) matches a regex pattern.
**Note** that this behavior is only applicable if there is only a **single MFA provider** configured, since that would allow CAS
to know what provider to next activate.

- Trigger MFA based on a principal attribute(s) whose value(s) **EXACTLY** matches an MFA provider.
This option is more relevant if you have more than one provider configured or if you have the flexibility of assigning provider ids to attributes as values.

Needless to say, the attributes need to have been resolved for the principal prior to this step. To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#multifactor-authentication).

## Global Principal Attribute Predicate

This is a more generic variant of the above trigger. It may be useful in cases where there is more than one provider configured and available in the application runtime and you need to design a strategy to dynamically decide on the provider that should be activated for the request.

The decision is handed off to a `Predicate` implementation defined in a Groovy script whose location is taught to CAS. The responsibility of the `test` function in the script is to determine eligibility of the provider to be triggered. If the predicate determines multiple providers as eligible by returning `true` more than one, the first provider in the sorted result set ranked by the provider's order will be chosen to respond.

The Groovy script predicate may be designed as such:

```groovy
import org.apereo.cas.authentication.*
import java.util.function.*
import org.apereo.cas.services.*

class PredicateExample implements Predicate<MultifactorAuthenticationProvider> {

    def service
    def principal
    def providers
    def logger

    public PredicateExample(service, principal, providers, logger) {
        this.service = service
        this.principal = principal
        this.providers = providers
        this.logger = logger
    }

    @Override
    boolean test(final MultifactorAuthenticationProvider p) {
        ...
    }
}
```

The parameters passed are as follows:

| Parameter             | Description
|-----------------------|------------------------------------------------------------------------------------
| `service`             | The object representing the corresponding service definition in the registry.
| `principal`           | The object representing the authenticated principal.
| `providers`           | Collection of `MultifactorAuthenticationProvider`s from which a selection shall be made.
| `logger`              | The object responsible for issuing log messages such as `logger.info(...)`.


To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#multifactor-authentication).

As an example, the following predicate example will begin to test each multifactor authentication provider and if the given provider is `mfa-duo` it will accept it as a valid trigger so long as the provider can be reached.

```groovy
import org.apereo.cas.authentication.*
import java.util.function.*
import org.apereo.cas.services.*

class PredicateExample implements Predicate<MultifactorAuthenticationProvider> {

    def service
    def principal
    def providers
    def logger

    public PredicateExample(service, principal, providers, logger) {
        this.service = service
        this.principal = principal
        this.providers = providers
        this.logger = logger
    }

    @Override
    boolean test(final MultifactorAuthenticationProvider p) {
        logger.info("Testing provider {}", p.getId())
        if (p.matches("mfa-duo")) {
           logger.info("Provider {} is available. Checking eligibility...", p.getId())
           if (p.isAvailable(this.service)) {
               logger.info("Provider {} matched. Good to go!", p.getId())
               return true;
           }
           logger.info("Skipping provider {}. Match failed.", p.getId())
           return false; 
        }
        logger.info("Provider {} cannot be reached", p.getId())
        return false
    }
}
```

## Global Authentication Attribute

MFA can be triggered for all users/subjects whose *authentication event/metadata* has resolved a specific attribute
that matches one of the below conditions:

- Trigger MFA based on a *authentication attribute(s)* whose value(s) matches a regex pattern.
**Note** that this behavior is only applicable if there is only a **single MFA provider** configured, since that would allow CAS
to know what provider to next activate.

- Trigger MFA based on a *authentication attribute(s)* whose value(s) **EXACTLY** matches an MFA provider.
This option is more relevant if you have more than one provider configured or if you have the flexibility of assigning
provider ids to attributes as values.

Needless to say, the attributes need to have been resolved for the authentication event prior to this step. This trigger
is generally useful when the underlying authentication engine signals CAS to perform additional validation of credentials.
This signal may be captured by CAS as an attribute that is part of the authentication event metadata which can then trigger
additional multifactor authentication events.

An example of this scenario would be the "Access Challenge response" produced by RADIUS servers.

## Adaptive

MFA can be triggered based on the specific nature of a request that may be considered outlawed. For instance,
you may want all requests that are submitted from a specific IP pattern, or from a particular geographical location
to be forced to go through MFA. CAS is able to adapt itself to various properties of the incoming request
and will route the flow to execute MFA. See [this guide](Configuring-Adaptive-Authentication.html) for more info.

## Grouper

MFA can be triggered by [Grouper](https://www.internet2.edu/products-services/trust-identity-middleware/grouper/)
groups to which the authenticated principal is assigned.
Groups are collected by CAS and then cross-checked against all available/configured MFA providers.
The group's comparing factor **MUST** be defined in CAS to activate this behavior
and it can be based on the group's name, display name, etc where
a successful match against a provider id shall activate the chosen MFA provider.

```xml
<dependency>
  <groupId>org.apereo.cas</groupId>
  <artifactId>cas-server-support-grouper</artifactId>
  <version>${cas.version}</version>
</dependency>
```

You will also need to ensure `grouper.client.properties` is available on the classpath
with the following configured properties:

```properties
grouperClient.webService.url = http://192.168.99.100:32768/grouper-ws/servicesRest
grouperClient.webService.login = banderson
grouperClient.webService.password = password
```

## Groovy

MFA can be triggered based on the results of a groovy script of your own design. The outcome of the script should determine the MFA provider id that CAS should attempt to activate.

The outline of the groovy script is shown below as a sample:

```groovy
import java.util.*

class SampleGroovyEventResolver {
    def String run(final Object... args) {
        def service = args[0]
        def registeredService = args[1]
        def authentication = args[2]
        def httpRequest = args[3]
        def logger = args[4]

        ...

        return "mfa-duo"
    }
}
```

The parameters passed are as follows:

| Parameter             | Description
|-----------------------|-----------------------------------------------------------------------
| `service`             | The object representing the incoming service provided in the request, if any.
| `registeredService`   | The object representing the corresponding service definition in the registry.
| `authentication`      | The object representing the established authentication event, containing the principal.
| `httpRequest`         | The object representing the `HttpServletRequest`.
| `logger`              | The object responsible for issuing log messages such as `logger.info(...)`.

As an example, the following script triggers multifactor authentication via Duo Security, if the requesting application is `https://www.example.com` and the authenticated principal contains a `mail` attribute whose values contain `email@example.org`.

```groovy
import java.util.*

class MyExampleScript {
    def String run(final Object... args) {
        def service = args[0]
        def registeredService = args[1]
        def authentication = args[2]
        def logger = args[3]

        if (service.id == "https://www.example.com") {
            logger.info("Evaluating principal attributes [{}]", authentication.principal.attributes)

            def mail = authentication.principal.attributes['mail']
            if (mail.contains("email@example.org")) {
                logger.info("Found mail attribute with value [{}]", mail)
                return "mfa-duo"
            }
        }
        return null
    }
}
```

## REST

MFA can be triggered based on the results of a remote REST endpoint of your design. If the endpoint is configured,
CAS shall issue a `POST`, providing the authenticated username as `principalId` and `serviceId` as the service 
url in the body of the request.

Endpoints must be designed to accept/process `application/json`. The body of the response in 
the event of a successful `200` status code is expected to be the MFA provider id which CAS should activate.

## Opt-In Request Parameter/Header

MFA can be triggered for a specific authentication request, provided
the initial request to the CAS `/login` endpoint contains a parameter/header
that indicates the required MFA authentication flow. The parameter/header name
is configurable, but its value must match the authentication provider id
of an available MFA provider described above.

An example request that triggers an authentication flow based on a request parameter would be:

```bash
https://.../cas/login?service=...&<PARAMETER_NAME>=<MFA_PROVIDER_ID>
```

The same strategy also applied to triggers that are based on request/session attributes, which tend to get used for internal communications between APIs and CAS components specially when designing extensions.

## Principal Attribute Per Application

As a hybrid option, MFA can be triggered for a specific application registered inside the CAS service registry, provided
the authenticated principal carries an attribute that matches a configured attribute value. The attribute
value can be an arbitrary regex pattern. See below to learn about how to configure MFA settings.

```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "^(https|imaps)://.*",
  "id" : 100,
  "name": "test",
  "multifactorPolicy" : {
    "@class" : "org.apereo.cas.services.DefaultRegisteredServiceMultifactorPolicy",
    "multifactorAuthenticationProviders" : [ "java.util.LinkedHashSet", [ "mfa-duo" ] ],
    "principalAttributeNameTrigger" : "memberOf",
    "principalAttributeValueToMatch" : "faculty|allMfaMembers"
  }
}
```

## Entity Id Request Parameter

In situations where authentication is delegated to CAS, most commonly via a [Shibboleth Identity Provider](https://shibboleth.net/products/identity-provider.html),  the entity id may be passed as a request parameter to CAS to be treated as a CAS registered service.
This allows one to activate multifactor authentication policies based on the entity id that is registered
This allows one to [activate multifactor authentication policies](#Per Application) based on the entity id that is registered
in the CAS service registry. As a side benefit, the entity id can take advantage of all other CAS features
such as access strategies and authorization rules simply because it's just another service definition known to CAS.

To learn more about integration options and to understand how to delegate authentication to CAS 
from a Shibboleth identity provider, please [see this guide](../integration/Shibboleth.html).

Support is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
  <groupId>org.apereo.cas</groupId>
  <artifactId>cas-server-support-shibboleth</artifactId>
  <version>${cas.version}</version>
</dependency>
```

The `entityId` parameter may be passed as such:

```bash
https://.../cas/login?service=http://idp.example.org&entityId=the-entity-id-passed
```

## Custom

While support for triggers may seem extensive, there is always that edge use case that would have you trigger MFA based on a special set of requirements. To learn how to design your own triggers, [please see this guide](Configuring-Multifactor-Authentication-CustomTriggers.html).
