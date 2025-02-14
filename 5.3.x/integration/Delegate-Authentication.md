---
layout: default
title: CAS - Delegate Authentication
---

# Delegated Authentication

CAS can act as a client using the [pac4j security engine](https://github.com/pac4j/pac4j) and delegate the authentication to:

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

To see the relevant list of CAS properties, please [review this guide](../installation/Configuration-Properties.html#pac4j-delegated-authn).

Notice that for each OAuth provider, the CAS server is considered as an OAuth client and therefore should be declared as
an OAuth client at the OAuth provider. After the declaration, a key and a secret is given by the OAuth provider which has
to be defined in the CAS configuration as well.

## User Interface

All available clients are automatically displayed on the login page as clickable buttons.
CAS does allow options for auto-redirection of the authentication flow to a provider,
if only there is a single provider available and configured.

## Authenticated User Id

After a successful delegated authentication, a user is created inside the CAS server with a specific identifier:
this one can be created only from the technical identifier received from the external identity provider (like `1234`)
or as a "typed identifier" (like `FacebookProfile#1234`), which is the default.

To see the relevant list of CAS properties, please [review this guide](../installation/Configuration-Properties.html#pac4j-delegated-authn).

## Returned Payload

Once you have configured (see information above) your CAS server to act as an OAuth,
CAS, OpenID (Connect) or SAML client, users will be able to authenticate at a OAuth/CAS/OpenID/SAML
provider (like Facebook) instead of authenticating directly inside the CAS server.

In the CAS server, after this kind of delegated authentication, users have specific authentication data.

The `Authentication` object has:

* The attribute `AuthenticationManager.AUTHENTICATION_METHOD_ATTRIBUTE`
set to `org.apereo.cas.support.pac4j.authentication.handler.support.ClientAuthenticationHandler`
* The attribute `clientName` set to the type of the provider used during authentication process.

The `Principal` object of the `Authentication` object has:

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
      "allowedProviders" : [ "java.util.ArrayList", [ "Facebook", "Twitter" ] ]
    }
  }
}
```

The list of allowed providers should contain the external identity provider names (i.e. client names).

## Configuration

### SAML2 Identity Providers

In the event that CAS is configured to delegate authentication to an external identity provider, the service provider (CAS) metadata as well as the identity provider metadata automatically become available at the following endpoints. Note that you can use more than one external identity provider with CAS, where each integration may be done with a different set of metadata and keys for CAS acting as the service provider. Each integration (referred to as a client, since CAS itself becomes a client of the identity provider) may be given a name optionally.

| Endpoint                   | Description
|---------------------------|--------------------------------------------------------------------------------------------------------------------
| `/sp/metadata`         | Displays the service provider (CAS) metadata. Works well if there is only one SAML2 IdP is defined.
| `/sp/idp/metadata`         | Displays the identity provider metadata. Works well if there is only one SAML2 IdP is defined.
| `/sp/{clientName}/metadata`         | Displays the service provider metadata for the requested client name.
| `/sp/{clientName}/idp/metadata`         | Displays the identity provider metadata for the requested client name.

Remember that the service provider (CAS) metadata is automatically generated once you access the above endpoints or view the CAS login screen. This is required because today, generating the metadata requires access to the HTTP request/response. In the event that metadata cannot be resolved, a status code of `406 - Not Acceptable` is returned.

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
