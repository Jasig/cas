---
layout: default
title: CAS - OpenID Connect Authentication
---

# OpenID Connect Authentication

Allow CAS to act as an OpenId Connect Provider (OP).

Support is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
  <groupId>org.apereo.cas</groupId>
  <artifactId>cas-server-support-oidc</artifactId>
  <version>${cas.version}</version>
</dependency>
```

To learn more about OpenId Connect, please [review this guide](http://openid.net/specs/openid-connect-basic-1_0.html).

The current implementation provides support for:

- [Authorization Code Flow](http://openid.net/specs/openid-connect-basic-1_0.html)
- [Implicit Flow](https://openid.net/specs/openid-connect-implicit-1_0.html)
- [Dynamic Discovery](https://openid.net/specs/openid-connect-discovery-1_0.html)
- Administration and registration of [OIDC clients and relying parties](Service-Management.html).
- Administration and registration of [OIDC clients and relying parties](Service-Management.html) via [Dynamic Client Registration protocol](https://tools.ietf.org/html/draft-ietf-oauth-dyn-reg-management-01).
- Ability to [resolve, map and release claims](../integration/Attribute-Release-Policies.html).
- Ability to configure expiration policies for various tokens.

## Endpoints

| Field                                         | Description
|-----------------------------------------------|-------------------------------------------------------
| `/cas/oidc/.well-known`                       | The discovery endpoint is a static page that you/clients use to query for CAS OIDC configuration information and metadata. No session is required. CAS returns basic information about endpoints, supported scopes, etc used for OIDC authentication.
| `/cas/oidc/.well-known/openid-configuration`  | Same as above.
| `/cas/oidc/jwks`                              | A read-only endpoint that contains the server’s public signing keys, which clients may use to verify the digital signatures of access tokens and ID tokens issued by CAS.
| `/cas/oidc/authorize`                         | Authorization requests are handled here.
| `/cas/oidc/profile`                           | User profile requests are handled here.
| `/cas/oidc/accessToken`, `/cas/oidc/token`    | Produces authorized access tokens.
| `/cas/oidc/register`                          | Register clients via the [dynamic client registration](https://tools.ietf.org/html/draft-ietf-oauth-dyn-reg-management-01) protocol.

## Register Clients

Clients can be registered with CAS in the following ways.

### Statically 

OpenID Connect clients can be *statically* registered with CAS as such:

```json
{
  "@class" : "org.apereo.cas.services.OidcRegisteredService",
  "clientId": "client",
  "clientSecret": "secret",
  "serviceId" : "^<https://the-redirect-uri>",
  "signIdToken": true,
  "implicit": false,
  "name": "OIDC",
  "id": 1000,
  "evaluationOrder": 100,
  "jwks": "...",
  "encryptIdToken": false,
  "idTokenEncryptionAlg": "...",
  "idTokenEncryptionEncoding": "..."
}
```

| Field                         | Description
|-------------------------------|------------------------------------------------------------------
| `serviceId`                   | The authorized redirect URI for this OIDC client.
| `implicit`                    | Whether the response produced for this service should be [implicit](https://openid.net/specs/openid-connect-implicit-1_0.html).
| `signIdToken`                 | Whether ID tokens should be signed. Default is `true`.
| `jwks`                        | Resource path to the keystore location that holds the keys for this application.
| `encryptIdToken`              | Whether ID tokens should be encrypted. Default is `false`.
| `idTokenEncryptionAlg`        | The algorithm header value used to encrypt the id token.
| `idTokenEncryptionEncoding`   | The algorithm method header value used to encrypt the id token.

Service definitions are typically managed by the [service management](Service-Management.html) facility.

### Dynamically

Clients applications may dynamically be registered with CAS for authentication. By default, CAS operates 
in a `PROTECTED` mode where the registration endpoint requires user authentication. This behavior may be relaxed via 
CAS settings to allow CAS to operate in an `OPEN` mode.

## Settings

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#openid-connect).

## Server Configuration

Remember that OpenID Connect features of CAS require session affinity (and optionally session replication),
as the authorization responses throughout the login flow
are stored via server-backed session storage mechanisms. You will need to configure your deployment environment and load balancers accordinngly.

## Claims

OpenID connect claims are simply treated as normal CAS attributes that need to
be [resolved, mapped and released](../integration/Attribute-Release-Policies.html).

### Scope-based Claims

You may chain various attribute release policies that authorize claim release based on specific scopes:


```json
{
  "@class" : "org.apereo.cas.services.OidcRegisteredService",
  "clientId": "...",
  "clientSecret": "...",
  "serviceId" : "...",
  "name": "OIDC Test",
  "id": 10,
  "scopes" : [ "java.util.HashSet", 
    [ "profile", "email", "address", "phone", "offline_access", "displayName", "eduPerson" ]
  ]
}
```

### Mapping Claims

Claims associated with a scope (i.e. `given_name` for `profile`) are fixed in the [OpenID specification](http://openid.net/specs/openid-connect-basic-1_0.html). In the event that custom arbitrary attributes should be mapped to claims, mappings can be defined in CAS settings to link a CAS-defined attribute to a fixed given scope. For instance, CAS configuration may allow the value of the attribute `sys_given_name` to be mapped and assigned to the claim `given_name` without having an impact on the attribute resolution configuration and all other CAS-enabled applications. 

If mapping is not defined, by default CAS attributes are expected to match claim names.

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#openid-connect).

### User-Defined Scopes

Note that in addition to standard system scopes, you may define your own custom scope with a number of attributes within. These such as `displayName` above, get bundled into a `custom` scope which can be used and requested by services and clients.

If you however wish to define your custom scopes as an extension of what OpenID Connect defines
such that you may bundle attributes together, then you need to first register your `scope`,
define its attribute bundle and then use it a given service definition such as `eduPerson` above.
Such user-defined scopes are also able to override the definition of system scopes.

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#openid-connect).

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
      "d": "...",
      "e": "AQAB",
      "n": "...",
      "kty": "RSA",
      "kid": "cas"
    }
  ]
}
```

CAS will attempt to auto-generate a keystore if it can't find one, but if you wish to generate one manually, 
a JWKS can be generated using [this tool](https://mkjwk.org/)
or [this tool](http://connect2id.com/products/nimbus-jose-jwt/generator).
