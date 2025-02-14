---
layout: default
title: CAS - OIDC Authentication
---

# OpenID Connect Authentication

Allow CAS to act as an OpenId Connect Provider (OP). Support is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
  <groupId>org.apereo.cas</groupId>
  <artifactId>cas-server-support-oidc</artifactId>
  <version>${cas.version}</version>
</dependency>
```

To learn more about OpenId Connect, please [review this guide](http://openid.net/specs/openid-connect-basic-1_0.html).

The current implementation provides support for:

- [Authorization Code workflow](http://openid.net/specs/openid-connect-basic-1_0.html)
- [Dynamic Discovery](https://openid.net/specs/openid-connect-discovery-1_0.html)
- Administration and registration of [OIDC relying parties](Service-Management.html).
- Ability to [resolve and release claims](../integration/Attribute-Release-Policies.html).
- Ability to configure an expiration policy for various tokens.


## Endpoints

| Field                                     | Description
|-------------------------------------------|-------------------------------------------------------
| `/cas/oidc/.well-known`                       | Discovery endpoint.
| `/cas/oidc/.well-known/openid-configuration`  | Discovery endpoint.
| `/cas/oidc/jwks`                              | Provides an aggregate of all keystores.
| `/cas/oidc/authorize`                         | Authorization requests are handled here.
| `/cas/oidc/profile`                           | User profile requests are handled here.
| `/cas/oidc/accessToken`                       | Produces authorized access tokens.

## Register Clients

OpenID Connect clients can be registered with CAS as such:

```json
{
  "@class" : "org.apereo.cas.services.OidcRegisteredService",
  "clientId": "client",
  "clientSecret": "secret",
  "serviceId" : "^<https://the-redirect-uri>",
  "signIdToken": true,
  "name": "OIDC",
  "id": 1000,
  "evaluationOrder": 100,
  "jwks": "..."
}
```

| Field                   | Description
|-------------------------|------------------------------------------------------------------
| `serviceId`             | The authorized redirect URI for this OIDC client.
| `signIdToken`           | Whether ID tokens should be signed. Default is `true`.
| `jwks`                  | Path to the location of the keystore that holds the signing keys for this application. If none defined, defaults will be used.

## Settings

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html).

## Server Configuration

Remember that OpenID Connect features of CAS require session affinity (and optionally session replication),
as the authorization responses throughout the login flow
are stored via server-backed session storage mechanisms. You will need to configure your deployment environment and load balancers
accordinngly.

## Claims

OpenID connect claims are simply treated as normal CAS attributes that need to 
be [resolved and released](../integration/Attribute-Release-Policies.html).

## Authentication Context Class

Support for authentication context class references is implemented in form of `acr_values` as part of the original authorization request,
which is mostly taken into account by the [multifactor authentication features](Configuring-Multifactor-Authentication.html) of CAS.
Once successful, `acr` and `amr` values are passed back to the relying party as part of the id token.

## Keystores

Each registered application in CAS can contain its own keystore as a `jwks` resource. By default,
a global keystore can be expected and defined via CAS properties. The format of the keystore
file is similar to the following:

```json
{
  "keys": [
    {
      "d": "eg5wj4Cfsp9gvIaorkdGgIKLSvWH5oitIMrLa5KGLIv7K7Iwi1_-otTORMSi8aKcqyBTGhNYT6-j23Q_dn6Ne6a87EOC5VUiz26y8_ZnovoCxH5nZtvEY8Y-RxhhmbQadm6zsK4o4bVQgn4ZNOCNQZiJUCozh79AedbbnzSSm9LhZlhnNP8hPEMnFp9EqVB0nNLG6vZ11KeSNvYng1LHBhqEhfloRuJV9vkWK8ekrpOQ6j2kdk0XRtryoS1DHVj_a_D7EG7CnjVx3zGSyf0B9JRViRVsKPVLGAtq7O0JiJZWMwIhOJBdviDu3Gi8ovD4yBOfQa_e86cqNmEnf7f2wQ",
      "e": "AQAB",
      "n": "k_2zfdFHTepOJYH3bCe7E_3bulz00qsDK7SBnK6aUbzby4xrXfzAQ6_Uxo3uttfFtx_WclfNF0hnkQW3V06LcY5CNQJm6WYrZ7EMuXmpPV6n9PEb5IHczG0ONwJVX_GykOUNPUuAig-B3XnjjyK8W8uwPv0oJzDcB3YIU5XEQBCrcJzefNUoOuT1pYBmJcCdnasUjRGsA-SsuGuaA82cDJNFT-mDenj6YpAZFrDyLHWHYgSsTxPhF-u7q4n3Xl4Zj2Vw2gDE5pXZHzsZS9U0Dn37bIWZWkI5sQoEh6x5P1fkWOIJw630qWMWChuKboaCmp08f7JBfvGQwNlVVgDmUw",
      "kty": "RSA",
      "kid": "cas"
    }
  ]
}
```

A JWKS can be generated using [this tool](https://mkjwk.org/) 
or [this tool](http://connect2id.com/products/nimbus-jose-jwt/generator).
