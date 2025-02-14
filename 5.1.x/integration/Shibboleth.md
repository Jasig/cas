---
layout: default
title: CAS - Shibboleth Integration
---

# Overview

CAS can be integrated with the [Shibboleth federated SSO platform](http://shibboleth.net/) by a couple
different strategies.
It is possible to designate CAS to serve as the authentication provider for the Shibboleth IdP.
With such a setup, when user
is routed to the IdP, the following may take place:

- If the user has already authenticated to CAS and has a valid CAS SSO session, the IdP will transparently
perform the requested action, e.g. attribute release.

- If the user does not have a valid CAS SSO session, the user will be redirected to CAS and must
authenticate before the IdP proceeds with the requested action.

## SSO for Shibboleth IdP (External)

This is a Shibboleth IdP external authentication plugin that delegates
the authentication to CAS. The advantage of using
this component over the plain `RemoteUser` solution is the ability to
utilize a full range of native CAS protocol features such as `renew` and `gateway`.

The plugin is available for both
Shibboleth Identity Provider [v2](https://github.com/Unicon/shib-cas-authn2)
and [v3](https://github.com/Unicon/shib-cas-authn3).

### Relying Party EntityId

The authentication plugin is able to pass the relying party's entity ID over
to the CAS server upon authentication requests.
The entity ID is passed in form of a url parameter to the CAS server as such:

```
https://sso.example.org/cas/login?service=<authentication-plugin-url>&entityId=<relying-party-entity-id>
```

You can also take advantage of the `entityId` parameter and treat it as a normal CAS service definition,
so it can be used for multifactor authentication and authorization.

See [this guide](../installation/Configuring-Multifactor-Authentication-Triggers.html) for more info.

## Displaying SAML MDUI

The CAS server is able to recognize the `entityId` parameter and display SAML MDUI on the login page,
that is provided by the metadata associated with the relying party.
This means that CAS will also need to know
about metadata sources that the identity provider uses.

### Configuration

Support is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
  <groupId>org.apereo.cas</groupId>
  <artifactId>cas-server-support-saml-mdui</artifactId>
  <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, please [review this guide](../installation/Configuration-Properties.html#saml-metadata-ui).

A sample screenshot of the above configuration in action:

![capture](https://cloud.githubusercontent.com/assets/1205228/8120071/095c7628-1050-11e5-810e-7bce128391df.PNG)
