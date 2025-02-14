---
layout: default
title: CAS - Delegate Authentication
category: Authentication
---

# Delegated Authentication

CAS can act as a client (i.e. service provider or proxy) using the [Pac4j library](https://github.com/pac4j/pac4j) and delegate the authentication to:

* CAS servers
* SAML2 identity providers
* OAuth2 providers such as Facebook, Twitter, Google, LinkedIn, Yahoo, etc
* OpenID providers
* OpenID Connect identity providers
* [ADFS](ADFS-Integration.html)

Support is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-pac4j-webflow</artifactId>
    <version>${cas.version}</version>
</dependency>
```

<div class="alert alert-info"><strong>Note</strong><p>The client issuing the authentication request can be of any type (SAML, OAuth2, OpenID Connect, etc) and is allowed to submit the authentication request using any protocol that the CAS server supports and is configured to understand. This means that you may have an OAuth2 client using CAS in delegation mode to authenticate at an external SAML2 identity provider, another CAS server or Facebook and in the end of that flow receiving an OAuth2 user profile. The CAS server is able to act as a proxy, doing the protocol translation in the middle.</p></div>

## Register Providers

An identity provider is a server which can authenticate users (like Google, Yahoo...) instead of a CAS server.
If you want to delegate the CAS authentication to Twitter for example, you have to add an
OAuth client for the Twitter provider, which will be done automatically for you once provider settings are taught to CAS.

Notice that for each OAuth provider, the CAS server is considered as an OAuth client and therefore should be declared as
an OAuth client at the OAuth provider. After the declaration, a key and a secret is given by the OAuth provider which has
to be defined in the CAS configuration as well.

### Default

Identity providers for delegated authentication can be registered with CAS using settings. To see the relevant list of 
CAS properties, please [review this guide](../configuration/Configuration-Properties.html#pac4j-delegated-authn).

### REST

Identity providers for delegated authentication can be provided to CAS using an external REST endpoint. This allows the CAS server to reach to 
a remote REST endpoint whose responsibility is to produce the following payload in the response body:

```json
{
    "callbackUrl": "https://sso.example.org/cas/login",
    "properties": {
        "github.id": "...",
        "github.secret": "...",
        
        "cas.loginUrl.1": "...",
        "cas.protocol.1": "..."
    }
}
```

The syntax and collection of available `properties` in the above payload is controlled by [Pac4j]((https://pac4j.org/docs/index.html). 
The response that is returned must be accompanied by a 200 status code.

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#pac4j-delegated-authn).

## User Interface

All available clients are automatically displayed on the login page as clickable buttons.
CAS does allow options for auto-redirection of the authentication flow to a provider,
if only there is a single provider available and configured.

## Authenticated User Id

After a successful delegated authentication, a user is created inside the CAS server with a specific identifier:
this one can be created only from the technical identifier received from the external identity provider (like `1234`)
or as a "typed identifier" (like `FacebookProfile#1234`), which is the default.

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#pac4j-delegated-authn).

## Returned Payload

Once you have configured (see information above) your CAS server to act as an OAuth,
CAS, OpenID (Connect) or SAML client, users will be able to authenticate at a OAuth/CAS/OpenID/SAML
provider (like Facebook) instead of authenticating directly inside the CAS server.

In the CAS server, after this kind of delegated authentication, users have specific authentication data. These include:

* An identifier which is the profile type + `#` + the identifier of the user for this provider (i.e `FacebookProfile#0000000001`)
* Attributes populated by the data retrieved from the provider (first name, last name, birthdate...)

## Profile Attributes

In CAS-protected applications, through service ticket validation, user information
are pushed to the CAS client and therefore to the application itself.

The identifier of the user is always pushed to the CAS client. For user attributes, it involves both the configuration
at the server and the way of validating service tickets.

On CAS server side, to push attributes to the CAS client, it should be configured in the expected service:

```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "sample",
  "name" : "sample",
  "id" : 100,
  "description" : "sample",
  "attributeReleasePolicy" : {
    "@class" : "org.apereo.cas.services.ReturnAllowedAttributeReleasePolicy",
    "allowedAttributes" : [ "java.util.ArrayList", [ "name", "first_name", "middle_name" ] ]
  }
}
```

## Access Strategy

Service definitions may be conditionally authorized to use an external identity provider by defining their own access strategy and policy:

```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "sample",
  "name" : "sample",
  "id" : 100,
  "accessStrategy" : {
    "@class" : "org.apereo.cas.services.DefaultRegisteredServiceAccessStrategy",
    "delegatedAuthenticationPolicy" : {
      "@class" : "org.apereo.cas.services.DefaultRegisteredServiceDelegatedAuthenticationPolicy",
      "allowedProviders" : [ "java.util.ArrayList", [ "Facebook", "Twitter" ] ],
      "permitUndefined": true,
      "exclusive": true
    }
  }
}
```

Note that:

- The list of allowed providers should contain the external identity provider names (i.e. client names).
- The `permitUndefined` flag decides whether access should be granted in the event that no allowed providers are defined explicitly.
- The `exclusive` flag decides whether authentication should be exclusively limited to allowed providers, disabling other methods such as username/password, etc.

## Provisioning

By default, user profiles that are extracted from external identity providers and merged into a CAS
authenticated principal are not stored or tracked anywhere. CAS does provide additional options to allow
such profiles to be managed outside of CAS and/or provisioned into identity stores, allowing you optionally to link
external/guest accounts with their equivalent found in the authentication source used by CAS, etc. 

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#pac4j-delegated-authn).

### Groovy Provisioner

Provisioning tasks can be carried out using an external Groovy script with the following structure:

```groovy
def run(Object[] args) {
    def principal = args[0]
    def userProfile = args[1]
    def client = args[2]
    def logger = args[3]
    ...
}
```

It is not expected for the script to return a value. The following parameters are passed to the script:

| Parameter             | Description
|-----------------------|----------------------------------------------------------------------------------------------
| `principal`           | CAS authenticated `Principal` that contains all attributes and claims.
| `userProfile`         | The original `UserProfile` extracted from the external identity provider. 
| `client`              | The `Client` configuration responsible for the exchange between CAS and the identity provider. 
| `logger`              | The object responsible for issuing log messages such as `logger.info(...)`.

### REST Provisioner

Provisioning tasks can be carried out using an external REST endpoint expected to receive the following:
     
| Header                  | Description
|-------------------------|----------------------------------------------------------------------------------------------
| `principalId`           | CAS authenticated principal identifier.
| `principalAttributes`   | CAS authenticated principal attributes.
| `profileId`             | The identifier of the user profile extracted from the identity provider. 
| `profileTypedId`        | The *typed* identifier of the user profile extracted from the identity provider. 
| `profileAttributes`     | Collection of attributes extracted from the identity provider's response.
| `clientName`            | The client name responsible for the exchange between CAS and the identity provider.

## SAML2 Identity Providers

To learn more about delegating authentication to SAML2 identity providers, 
please [review this guide](Delegate-Authentication-SAML.html).

## Troubleshooting

To enable additional logging, configure the log4j configuration file to add the following
levels:

```xml
...
<AsyncLogger name="org.pac4j" level="debug" additivity="false">
    <AppenderRef ref="console"/>
    <AppenderRef ref="file"/>
</AsyncLogger>
...
```
