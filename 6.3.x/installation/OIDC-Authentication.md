---
layout: default
title: CAS - OpenID Connect Authentication
category: Authentication
---

# OpenID Connect Authentication

Allow CAS to act as an [OpenId Connect Provider (OP)](http://openid.net/connect/).

<div class="alert alert-info"><strong>Remember</strong><p>OpenId Connect is a continuation of the <a href="OAuth-OpenId-Authentication.html">OAuth protocol</a> with some additional variations. If you enable OpenId Connect, you will have automatically enabled OAuth as well. Options and behaviors that are documented for the <a href="OAuth-OpenId-Authentication.html">OAuth protocol</a> support may apply here just the same.</p></div>

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
- [WebFinger Issuer Discovery](https://openid.net/specs/openid-connect-discovery-1_0-21.html)
- Administration and registration of [OIDC clients and relying parties](../services/Service-Management.html).
- Administration and registration of [OIDC clients and relying parties](../services/Service-Management.html) via [Dynamic Client Registration protocol](https://tools.ietf.org/html/draft-ietf-oauth-dyn-reg-management-01).
- Ability to [resolve, map and release claims](../integration/Attribute-Release-Policies.html).
- Ability to configure expiration policies for various tokens.

## Endpoints

| Field                                     | Description
|-------------------------------------------|-------------------------------------------------------
| `/oidc/.well-known`                       | The discovery endpoint used to query for CAS OIDC configuration information and metadata.
| `/oidc/.well-known/openid-configuration`  | Same as `.well-known` discovery endpoint.
| `/oidc/.well-known/webfinger`             | [WebFinger](https://tools.ietf.org/html/rfc7033) discovery endpoint
| `/oidc/jwks`                              | Contains the server’s public signing keys, which clients may use to verify the digital signatures of access tokens and ID tokens issued by CAS.
| `/oidc/authorize`                         | Authorization requests are handled here.
| `/oidc/profile`                           | User profile requests are handled here.
| `/oidc/introspect`                        | Query CAS to detect the status of a given access token via [introspection](https://tools.ietf.org/html/rfc7662). This endpoint expects HTTP basic authentication with OIDC service `client_id` and `client_secret` associated as username and password.
| `/oidc/accessToken`, `/oidc/token`        | Produces authorized access tokens.
| `/oidc/revoke`                            | [Revoke](https://tools.ietf.org/html/rfc7009) access or refresh tokens. This endpoint expects HTTP basic authentication with OIDC service `client_id` and `client_secret` associated as username and password.
| `/oidc/register`                          | Register clients via the [dynamic client registration](https://tools.ietf.org/html/draft-ietf-oauth-dyn-reg-management-01) protocol.

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
  "name": "OIDC",
  "id": 1000
}
```

Note that OpenID connect clients as service definitions are an extension of [OAuth services](OAuth-OpenId-Authentication.html) in CAS. All settings that apply to an OAuth service definition should equally apply here as well. 
The following fields are specifically available for OpenID connect services:

| Field                         | Description
|-------------------------------|---------------------------------------------------------------------------------------
| `clientId`                    | Required. The identifier for this client application.
| `clientSecret`                | Required. The secret for this client application.
| `serviceId`                   | Required. The authorized redirect URI for this OIDC client.
| `supportedGrantTypes`         | Optional. Collection of supported grant types for this service.
| `supportedResponseTypes`      | Optional. Collection of supported response types for this service.
| `signIdToken`                 | Optional. Whether ID tokens should be signed. Default is `true`.
| `jwks`                        | Optional. Resource path to the keystore location that holds the keys for this application.
| `jwksCacheDuration`           | Optional. The expiration policy time value applied to loaded/cached keys for this application.
| `jwksCacheTimeUnit`           | Optional. The expiration policy time unit of measure (i.e. `seconds`, `minutes`, etc) applied to loaded/cached keys.
| `encryptIdToken`              | Optional. Whether ID tokens should be encrypted. Default is `false`.
| `idTokenEncryptionAlg`        | Optional. The algorithm header value used to encrypt the id token.
| `idTokenSigningAlg`           | Optional. The algorithm header value used to sign the id token.
| `userInfoSigningAlg`          | Optional. The algorithm header value used to sign user profile responses.
| `userInfoEncryptedResponseAlg`   | Optional. The algorithm header value used to encrypt user profile responses.
| `tokenEndpointAuthenticationMethod`    | Optional. The requested client authentication method to the token endpoint. Default is `client_secret_basic`.
| `applicationType`             | Optional. `web`, `native`, or blank. Defined the kind of the application. The default, if omitted, is `web`. 
| `idTokenEncryptionEncoding`   | Optional. The algorithm method header value used to encrypt the id token.
| `userInfoEncryptedResponseEncoding`   | Optional. The algorithm method header value used to encrypt the user profile response.
| `subjectType`                 | Optional value chosen from `public` or `pairwise`. Type to use when generating principal identifiers. Default is `public`.
| `sectorIdentifierUri`         | Optional. Host value of this URL is used as the sector identifier for the pairwise identifier calculation. If left undefined, the host value of the `serviceId` will be used instead.

<div class="alert alert-info"><strong>Keep What You Need!</strong><p>You are encouraged to only keep and maintain properties and settings needed for a 
particular integration. It is UNNECESSARY to grab a copy of all service fields and try to configure them yet again based on their default. While 
you may wish to keep a copy as a reference, this strategy would ultimately lead to poor upgrades increasing chances of breaking changes and a messy 
deployment at that.</p></div>

Service definitions are typically managed and registered with CAS by the [service management](../services/Service-Management.html) facility.

<div class="alert alert-warning"><strong>Usage Warning!</strong><p>CAS today does not strictly enforce the collection of authorized supported 
response/grant types for backward compatibility reasons if left blank. This means that if left undefined, all grant and response types may be allowed by 
the service definition and related policies. Do please note that this behavior is <strong>subject to change</strong> in future releases 
and thus, it is strongly recommended that all authorized grant/response types for each profile be declared in the service definition 
immediately to avoid surprises in the future.</p></div>

### Dynamically

Client applications may dynamically be registered with CAS for authentication. By default, CAS operates 
in a `PROTECTED` mode where the registration endpoint requires user authentication. This behavior may be relaxed via 
CAS settings to allow CAS to operate in an `OPEN` mode.

## Settings

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#openid-connect).

## Server Configuration

Remember that OpenID Connect features of CAS require session affinity (and optionally session replication),
as the authorization responses throughout the login flow are stored via server-backed session storage mechanisms. 
You will need to configure your deployment environment and load-balancers accordingly.

## Sample Client Applications

- [MITREid Sample Java Webapp](https://github.com/cas-projects/oidc-sample-java-webapp)

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

Standard scopes that internally catalog pre-defined claims all belong to the namespace `org.apereo.cas.oidc.claims` and are described below:

| Policy                                              | Description
|-----------------------------------------------------|-----------------------------------------------------------------------------------------
| `o.a.c.o.c.OidcProfileScopeAttributeReleasePolicy`  | Release claims mapped to the spec-predefined `profile` scope.
| `o.a.c.o.c.OidcEmailScopeAttributeReleasePolicy`  | Release claims mapped to the spec-predefined `email` scope.
| `o.a.c.o.c.OidcAddressScopeAttributeReleasePolicy`  | Release claims mapped to the spec-predefined `address` scope.
| `o.a.c.o.c.OidcPhoneScopeAttributeReleasePolicy`  | Release claims mapped to the spec-predefined `phone` scope.
 
### Mapping Claims

Claims associated with a scope (i.e. `given_name` for `profile`) are fixed in 
the [OpenID specification](http://openid.net/specs/openid-connect-basic-1_0.html). In the 
event that custom arbitrary attributes should be mapped to claims, mappings can be defined in CAS 
settings to link a CAS-defined attribute to a fixed given scope. For instance, CAS configuration may 
allow the value of the attribute `sys_given_name` to be mapped and assigned to the claim `given_name` 
without having an impact on the attribute resolution configuration and all other CAS-enabled applications. 

If mapping is not defined, by default CAS attributes are expected to match claim names.

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#openid-connect).

### User-Defined Scopes

Note that in addition to standard system scopes, you may define your own custom scope with a number of attributes within:

```json
{
  "@class" : "org.apereo.cas.services.OidcRegisteredService",
  "clientId": "...",
  "clientSecret": "...",
  "serviceId" : "...",
  "name": "OIDC Test",
  "id": 10,
  "scopes" : [ "java.util.HashSet", [ "displayName", "eduPerson" ] ]
}
```
 
These such as `displayName` above, get bundled into a `custom` scope which can be used and requested by services and clients.

If you however wish to define your custom scopes as an extension of what OpenID Connect defines
such that you may bundle attributes together, then you need to first register your `scope`,
define its attribute bundle and then use it a given service definition such as `eduPerson` above.
Such user-defined scopes are also able to override the definition of system scopes.

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#openid-connect).

### Releasing Claims

Defined scopes for a given service definition control and build attribute release policies internally. Such attribute release
policies allow one to release standard claims, remap attributes to standard claims, or define custom claims and scopes altogether. 

It is also possible to define and use *free-form* attribute release policies outside the confines of a *scope* to freely build and release claims/attributes.  

For example, the following service definition will decide on relevant attribute release policies based on the semantics
of the scopes `profile` and `email`. There is no need to design or list individual claims as CAS will auto-configure
the relevant attribute release policies:

```json
{
  "@class" : "org.apereo.cas.services.OidcRegisteredService",
  "clientId": "client",
  "clientSecret": "secret",
  "serviceId" : "...",
  "name": "OIDC",
  "id": 1,
  "scopes" : [ "java.util.HashSet",
    [ "profile", "email" ]
  ]
}
```

A *scope-free* attribute release policy may just as equally apply, allowing one in 
the following example to release `userX` as a *claim*:

```json
{
  "@class" : "org.apereo.cas.services.OidcRegisteredService",
  "clientId": "client",
  "clientSecret": "secret",
  "serviceId" : "...",
  "name": "OIDC",
  "id": 1,
  "attributeReleasePolicy" : {
    "@class" : "org.apereo.cas.services.ReturnMappedAttributeReleasePolicy",
    "allowedAttributes" : {
      "@class" : "java.util.TreeMap",
      "userX" : "groovy { return attributes['uid'].get(0) + '-X' }"
    }
  }
}
```

It is also possible to mix *free-form* release policies with those that operate based on a scope by chaining such policies together. For example, the below policy
allows the release of `user-x` as a claim, as well as all claims assigned and internally defined for the standard `email` scope.

```json
{
  "@class": "org.apereo.cas.services.OidcRegisteredService",
  "clientId": "client",
  "clientSecret": "secret",
  "serviceId": "...",
  "name": "OIDC",
  "id": 10,
  "attributeReleasePolicy": {
    "@class": "org.apereo.cas.services.ChainingAttributeReleasePolicy",
    "policies": [
      "java.util.ArrayList",
      [
        {
          "@class" : "org.apereo.cas.services.ReturnAllowedAttributeReleasePolicy",
          "allowedAttributes" : [ "java.util.ArrayList", [ "cn", "uid", "givenName" ] ],
          "order": 0  
        },
        {
          "@class": "org.apereo.cas.services.ReturnMappedAttributeReleasePolicy",
          "allowedAttributes": {
            "@class": "java.util.TreeMap",
            "user-x": "groovy { return attributes['uid'].get(0) + '-X' }"
          },
          "order": 1
        },
        {
          "@class": "org.apereo.cas.oidc.claims.OidcEmailScopeAttributeReleasePolicy",
          "order": 2
        }
      ]
    ]
  }
}
```

To learn more about attribute release policies and the chain of command, please [see this guide](../integration/Attribute-Release-Policies.html).

## Authentication Context Class

Support for authentication context class references is implemented in form of `acr_values` as part of the original 
authorization request, which is mostly taken into account by 
the [multifactor authentication features](../mfa/Configuring-Multifactor-Authentication.html) of CAS. 
Once successful, `acr` and `amr` values are passed back to the relying party as part of the id token.

## Pairwise Identifiers

When `pairwise` subject type is used, CAS will calculate a unique `sub` value for each sector identifier. This identifier 
should not be reversible by any party other than CAS and is somewhat akin to CAS generating persistent anonymous user 
identifiers. Each value provided to every relying party is different so as not to enable clients to correlate the user's activities without permission.

```json
{
  "@class" : "org.apereo.cas.services.OidcRegisteredService",
  "clientId": "client",
  "clientSecret": "secret",
  "serviceId" : "^<https://the-redirect-uri>",
  "usernameAttributeProvider" : {
    "@class" : "org.apereo.cas.services.PairwiseOidcRegisteredServiceUsernameAttributeProvider",
    "persistentIdGenerator" : {
      "@class" : "org.apereo.cas.authentication.principal.OidcPairwisePersistentIdGenerator",
      "salt" : "aGVsbG93b3JsZA=="
    }
  }
}
```

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

## WebFinger Issuer Discovery

OpenID Provider Issuer discovery is the process of determining the location of the OpenID Provider. Issuer discovery is optional; if a Relying Party 
knows the OP's Issuer location through an out-of-band mechanism, it can skip this step.

Issuer discovery requires the following information to make a discovery request:

| Parameter                     | Description
|-------------------------------|---------------------------------------------------------------------------------------
| `resource`                    | Required. Identifier for the target End-User that is the subject of the discovery request.
| `host`                        | Server where a WebFinger service is hosted.
| `rel`                         | URI identifying the type of service whose location is being requested:`http://openid.net/specs/connect/1.0/issuer`

To start discovery of OpenID endpoints, the End-User supplies an Identifier to the Relying Party. The RP applies normalization rules to the Identifier to
determine the Resource and Host. Then it makes an HTTP `GET` request to the CAS WebFinger endpoint with the `resource` and `rel` parameters to obtain 
the location of the requested service. The Issuer location **MUST** be returned in the WebFinger response as the value 
of the `href` member of a links array element with `rel` member value `http://openid.net/specs/connect/1.0/issuer`.

Example invocation of the `webfinger` endpoint follows:

```bash
curl https://sso.example.org/cas/oidc/.well-known/webfinger?resource=acct:casuser@somewhere.example.org
```

The expected response shall match the following example:

```json
{
  "subject": "acct:casuser@somewhere.example.org",
  "links": [
    {
      "rel": "http://openid.net/specs/connect/1.0/issuer",
      "href": "https://sso.example.org/cas/oidc/"
    }
  ]
}
```

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#openid-connect-webfinger).

### WebFinger Resource UserInfo

To determine the correct issuer, resources that are provided to the `webfinger` discovery endpoint using the `acct` URI scheme
can be located and fetched using external user repositories via `email` or `username`.

<div class="alert alert-warning"><strong>Usage Warning!</strong><p>The default repository implementation will 
simply echo back the provided email or username, etc as it is <strong>ONLY</strong> relevant for demo/testing purposes.</p></div>

The following user-info repository choices are available for configuration and production use.

#### Groovy UserInfo Repository

The task of locating accounts linked to webfinger resources can be handled using an external Groovy script whose outline would match the following:

```groovy
def findByUsername(Object[] args) {
    def username = args[0]
    def logger = args[1]
    return [username: username]
}

def findByEmailAddress(Object[] args) {
    def email = args[0]
    def logger = args[1]
    return [email: email]
}
```

The expected return value from the script is a `Map` that contains key-value objects, representing user account details. An empty `Map`
would indicate the absence of the user record, leading to a `404` response status back to the relying party.

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#openid-connect-webfinger).

#### REST UserInfo Repository

The REST repository allows the CAS server to reach to a remote REST endpoint via the configured HTTP method to fetch user account information.

Query data is passed via either `email` or `username` HTTP headers. The response that is returned must be accompanied by a `200`
status code where the body should contain `Map` representing the user account information. All other responses will lead to a `404` 
response status back to the relying party.

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#openid-connect-webfinger).

#### Custom UserInfo Repository

It is possible to design and inject your own version of webfinger user repositories into CAS. First, you will need to design
a `@Configuration` class to contain your own `OidcWebFingerUserInfoRepository` implementation:

```java
@Configuration("customWebFingerUserInfoConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class CustomWebFingerUserInfoConfiguration {

    @Bean
    public OidcWebFingerUserInfoRepository oidcWebFingerUserInfoRepository() {
        ...
    }
}
```

Your configuration class needs to be registered with CAS. [See this guide](../configuration/Configuration-Management-Extensions.html) for better details.
