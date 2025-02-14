---
layout: default
title: CAS - Shibboleth Integration
category: Integration
---

# Overview

CAS can be integrated with the [Shibboleth federated SSO platform](http://shibboleth.net/) by a couple
different strategies. It is possible to designate CAS to serve as the authentication provider for the Shibboleth IdP.
With such a setup, when user is routed to the IdP, the following may take place:

- If the user has already authenticated to CAS and has a valid CAS SSO session, the IdP will transparently
perform the requested action, e.g. attribute release.
- If the user does not have a valid CAS SSO session, the user will be redirected to CAS and must
authenticate before the IdP proceeds with the requested action.

<div class="alert alert-info"><strong>Note</strong><p>Remember that this page is specifically dedicated to integration options with the Shibboleth Identity Provider. If you need CAS to act as a SAML2 identity provider on its own, you should <a href="../installation/Configuring-SAML2-Authentication.html">start here instead</a>.</p></div>

## SSO for Shibboleth IdP (External)

This is a Shibboleth IdP external authentication plugin that delegates
the authentication to CAS. This solution has the ability to
utilize a full range of native CAS protocol features such as `renew` and `gateway`.

The plugin is available for both
Shibboleth Identity Provider [v2](https://github.com/Unicon/shib-cas-authn2)
and [v3](https://github.com/Unicon/shib-cas-authn3) and [v4](https://github.com/Unicon/shib-cas-authn).

Support is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
  <groupId>org.apereo.cas</groupId>
  <artifactId>cas-server-support-shibboleth</artifactId>
  <version>${cas.version}</version>
</dependency>
```

### Relying Party EntityId

The authentication plugin is able to pass the relying party's entity ID over
to the CAS server upon authentication requests.
The entity ID is passed in form of a url parameter to the CAS server as such:

```
https://sso.example.org/cas/login?service=<authentication-plugin-url>&entityId=<relying-party-entity-id>
```

You can also take advantage of the `entityId` parameter and treat it as a normal CAS service definition,
so it can be used for multifactor authentication and authorization.

See [this guide](../mfa/Configuring-Multifactor-Authentication-Triggers.html) for more info.

## Displaying SAML MDUI

The CAS server is able to recognize the `entityId` parameter and display SAML MDUI on the login page,
that is provided by the metadata associated with the relying party.

### Configuration

Support is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
  <groupId>org.apereo.cas</groupId>
  <artifactId>cas-server-support-saml-mdui</artifactId>
  <version>${cas.version}</version>
</dependency>
```

### Relying Party Metadata

You may allow CAS to recognize SAML MDUI directly from metadata documents that are fed to CAS via settings. If the metadata for the relying party matches the requested `entityId` and contains MDUI elements, those will be passed onto the login page for decorations. If MDUI is not available in the metadata, the relevant elements from the matching service in the service registry will be used all the same.

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#saml-metadata-ui).

### Service Registry Metadata

You may also register the relying party in the CAS service registry as a regular service application and simply specify a number of MDUI-like elements in the body of the registration record. An example follows:

```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "relying-party-entity-id",
  "name" : "Test",
  "id" : 100,
  "description" : "This is the test application.",
  "evaluationOrder" : 10000,
  "logo": "images/logo.png",
  "informationUrl": "https://test.example.org/info",
  "privacyUrl": "https://test.example.org/privacy"
}
```
