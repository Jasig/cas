---
layout: default
title: CAS - Multifactor Authentication Bypass
category: Multifactor Authentication
---

# Multifactor Authentication Bypass

Each [multifactor provider](Configuring-Multifactor-Authentication.html) is equipped with options to allow for bypass. Once the provider
is chosen to honor the authentication request, bypass rules are then consulted to calculate whether the provider should ignore the request and skip MFA conditionally.

## Default Bypass

CAS provides a default bypass policy for each [multifactor provider](Configuring-Multifactor-Authentication.html) that can be configured through CAS properties.  
All providers will consult this policy for bypass events before consulting any other configured bypass providers.

Bypass rules allow for the following options for each provider:

- Skip multifactor authentication based on designated **principal** attribute **names**.
- ...[and optionally] Skip multifactor authentication based on designated **principal** attribute **values**.
- Skip multifactor authentication based on designated **authentication** attribute **names**.
- ...[and optionally] Skip multifactor authentication based on designated **authentication** attribute **values**.
- Skip multifactor authentication depending on method/form of primary authentication execution.
- Skip multifactor authentication depending on the properties of the http request such as remote addr/host and/or header names.

A few simple examples follow:

- Trigger MFA except when the principal carries an `affiliation` attribute whose value is either `alum` or `member`.
- Trigger MFA except when the principal carries a `superAdmin` attribute.
- Trigger MFA except if the method of primary authentication is SPNEGO.
- Trigger MFA except if credentials used for primary authentication are of type `org.example.MyCredential`.

Note that in addition to the above options, some multifactor authentication providers
may also skip and bypass the authentication request in the event that the authenticated principal does not quite "qualify"
for multifactor authentication. See the documentation for each specific provider to learn more.

### Configuration

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#multifactor-authentication).

Note that ticket validation requests shall successfully go through if multifactor authentication is
bypassed for the given provider. In such cases, no authentication context is passed back to the application and
additional attributes are supplanted to let the application know multifactor authentication is bypassed for the provider.

### Bypass Per Service

MFA Bypass rules can be overridden per application via the CAS service registry. This is useful when
MFA may be turned on globally for all applications and services, yet a few selectively need to be excluded. Services
whose access should bypass MFA may be defined as such in the CAS service registry:

```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "^(https|imaps)://.*",
  "id" : 100,
  "multifactorPolicy" : {
    "@class" : "org.apereo.cas.services.DefaultRegisteredServiceMultifactorPolicy",
    "multifactorAuthenticationProviders" : [ "java.util.LinkedHashSet", [ "mfa-duo" ] ],
    "bypassEnabled" : "true"
  }
}
```

### Bypass Per Principal Attribute & Service

This is similar to the above option, except that bypass is only activated for
the registered application if the authenticated principal contains an attribute
with the specified value(s). 

```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "^(https|imaps)://.*",
  "id" : 100,
  "multifactorPolicy" : {
    "@class" : "org.apereo.cas.services.DefaultRegisteredServiceMultifactorPolicy",
    "bypassPrincipalAttributeName": "attributeForBypass",
    "bypassPrincipalAttributeValue": "^bypass-value-[A-Z].+",
    "bypassEnabled" : "true"
  }
}
```

## Additional Bypass Providers

In addition to the configurable default bypass rules, the following bypass providers can be defined and executed after default bypass rules are calculated.

In the case where the default rules determine that the multifactor authentication should be bypassed, the chain will be short circuited and no additional bypass providers will be consulted.

### Bypass via Groovy

Multifactor authentication bypass may be determined using a Groovy script of your own design. The outcome of the script, if `true` indicates that multifactor
 authentication for the requested provider should proceed. Otherwise `false` indicates that  multifactor authentication for this provider should be skipped and bypassed. 

The outline of the script may be as follows:

```groovy
import java.util.*

def boolean run(final Object... args) {
    def authentication = args[0]
    def principal = args[1]
    def registeredService = args[2]
    def provider = args[3]
    def logger = args[4]
    def httpRequest = args[5]

    // Stuff happens...

    return false;
}
```

The parameters passed are as follows:

| Parameter             | Description
|-----------------------|-----------------------------------------------------------------------
| `authentication`      | The object representing the established authentication event.
| `principal`           | The object representing the authenticated principal.
| `service`             | The object representing the corresponding service definition in the registry.
| `provider`            | The object representing the requested multifactor authentication provider.
| `logger`              | The object responsible for issuing log messages such as `logger.info(...)`.
| `httpRequest`         | The object responsible for capturing the http request.

As an example, the following script skips multifactor authentication if the application requesting it is registered in the CAS service registry under the name `MyApplication` and only does so if the provider is Duo Security and the authenticated principal contains an attribute named `mustBypassMfa` whose values contains `true`.

```groovy
def boolean run(final Object... args) {
    def authentication = args[0]
    def principal = args[1]
    def service = args[2]
    def provider = args[3]
    def logger = args[4]
    def httpRequest = args[5]

    if (service.name == "MyApplication") {
        logger.info("Evaluating principal attributes ${principal.attributes}")

        def bypass = principal.attributes['mustBypassMfa']
        if (bypass.contains("true") && provider.id == "mfa-duo") {
            logger.info("Skipping bypass for principal ${principal.id}")
            return false
        }
    }
    return true
}
```

### Bypass via REST

Multifactor authentication bypass may be determined using a REST API of your own design. Endpoints must be designed to accept/process `application/json` via 
`GET` requests. A returned status code `202` meaning `ACCEPTED` indicates that multifactor authentication for the requested provider should proceed. Otherwise multifactor authentication for this provider should be skipped and bypassed.

The following parameters are passed:

| Parameter        | Description
|------------------|------------------------------------------------------------
| `principal`      | The identifier of the authenticated principal.
| `provider`       | The identifier of the multifactor authentication provider.
| `service`        | The identifier of the registered service in the registry, if any.
