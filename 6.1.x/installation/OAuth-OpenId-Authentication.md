---
layout: default
title: CAS - OAuth Authentication
category: Authentication
---

# OAuth/OpenID Authentication

Allow CAS to act as an OAuth/OpenID authentication provider. Please [review the specification](https://oauth.net/2/) to learn more.

<div class="alert alert-info"><strong>CAS as OAuth Server</strong><p>This page specifically describes how to enable
OAuth/OpenID server support for CAS. If you would like to have CAS act as an OAuth/OpenID client communicating with
other providers (such as Google, Facebook, etc), <a href="../integration/Delegate-Authentication.html">see this page</a>.</p></div>

## Administrative Endpoints

The following endpoints are provided by CAS:
 
| Endpoint                 | Description
|--------------------------|------------------------------------------------
| `oauthTokens`            | Manage and control [OAuth2 access tokens](OAuth-OpenId-Authentication.html). A `GET` operation produces a list of all access/refresh tokens. A `DELETE` operation will delete the provided access/refresh token provided in form of a parameter selector. (i.e. `/{token}`). A `GET` operation produces with a parameter selector of `/{token}` will list the details of the fetched access/refresh token.

## Configuration

Support is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
  <groupId>org.apereo.cas</groupId>
  <artifactId>cas-server-support-oauth-webflow</artifactId>
  <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#oauth2).

## Endpoints

After enabling OAuth support, the following endpoints will be available:

| Endpoint                  | Description                                                           | Method
|---------------------------|-----------------------------------------------------------------------|---------
| `/oauth2.0/authorize`     | Authorize the user and start the CAS authentication flow.                   | `GET`
| `/oauth2.0/accessToken`,`/oauth2.0/token`      | Get an access token in plain-text or JSON              | `POST`
| `/oauth2.0/profile`       | Get the authenticated user profile in JSON via `access_token` parameter.    | `GET`
| `/oauth2.0/introspect`    | Query CAS to detect the status of a given access token via [introspection](https://tools.ietf.org/html/rfc7662).  | `POST`
| `/oauth2.0/device`        | Approve device user codes via the [device flow protocol](https://tools.ietf.org/html/draft-denniss-oauth-device-flow). | `POST`

## Response/Grant Types

The following types are supported; they allow you to get an access token representing the current user and OAuth 
client application. With the access token, you'll be able to query the `/profile` endpoint and get the user profile.

### Authorization Code

The authorization code type is made for UI interactions: the user will enter credentials, shall receive a code and will exchange that code for an access token.

| Endpoint                | Parameters                                               | Response
|-------------------------|----------------------------------------------------------|---------------------------
| `/oauth2.0/authorize`   | `response_type=code&client_id=<ID>&redirect_uri=<CALLBACK>`  | OAuth code as a parameter of the `CALLBACK` url.
| `/oauth2.0/accessToken` | `grant_type=authorization_code&client_id=ID`<br/>`&client_secret=SECRET&code=CODE&redirect_uri=CALLBACK`  | The access token.

#### Proof Key Code Exchange (PKCE)

The [Proof Key for Code Exchange](https://tools.ietf.org/html/rfc7636) (PKCE, pronounced pixie) extension describes a technique for public clients to mitigate the threat of having the authorization code intercepted. The technique involves the client first creating a secret, and then using that secret again when exchanging the authorization code for an access token. This way if the code is intercepted, it will not be useful since the token request relies on the initial secret.

The authorization code type at the authorization endpoint `/oauth2.0/authorize` is able to accept the following parameters to activate PKCE:

| Parameter                | Description                                            
|-------------------------|------------------------------------------------------
| `code_challenge`        |  The code challenge generated using the method below.
| `code_challenge_method` | `plain`, `S256`. This parameter is optional, where `plain` is assumed by default.

The `/oauth2.0/accessToken`  endpoint is able to accept the following parameters to activate PKCE:

| Parameter                | Description                                            
|-------------------------|------------------------------------------------------
| `code_verifier`        | The original code verifier for the PKCE request, that the app originally generated before the authorization request.

If the method is `plain`, then the CAS needs only to check that the provided `code_verifier` matches the expected `code_challenge` string. 
If the method is `S256`, then the CAS should take the provided `code_verifier` and transform it using the same method the client will have used initially. This means calculating the SHA256 hash of the verifier and base64-url-encoding it, then comparing it to the stored `code_challenge`.

If the verifier matches the expected value, then the CAS can continue on as normal, issuing an access token and responding appropriately.

### Token/Implicit

The `token` type is also made for UI interactions as well as indirect non-interactive (i.e. Javascript) applications.

| Endpoint                | Parameters                                               | Response
|-------------------------|----------------------------------------------------------|------------------------------------------------
| `/oauth2.0/authorize`   | `response_type=token&client_id=ID&redirect_uri=CALLBACK` | The access token as an anchor parameter of the `CALLBACK` url.

### Resource Owner Credentials

The `password` grant type allows the OAuth client to directly send the user's credentials to the OAuth server.
This grant is a great user experience for trusted first party clients both on the web and in native device applications.

| Endpoint                | Parameters                                               | Response
|-------------------------|----------------------------------------------------------|-----------------------------------------------------
| `/oauth2.0/accessToken` | `grant_type=password&client_id=ID`<br/>`&client_secret=<SECRET>`<br/>`&username=USERNAME&password=PASSWORD` | The access token.

Because there is no `redirect_uri` specified by this grant type, the service identifier recognized by CAS and matched in the service registry is taken as the `client_id` instead. You may optionally also pass along a `service` or `X-service` header value that identifies the target application url. The header value must match the OAuth service definition in the registry that is linked to the client id.

### Client Credentials

The simplest of all of the OAuth grants, this grant is suitable for machine-to-machine authentication 
where a specific user’s permission to access data is not required.

| Endpoint                | Parameters                                               | Response
|-------------------------|----------------------------------------------------------|---------------------------
| `/oauth2.0/accessToken` | `grant_type=client_credentials&client_id=client&client_secret=secret` | The access token.

Because there is no `redirect_uri` specified by this grant type, the service identifier recognized by CAS and matched in the service registry is taken as the `client_id` instead. You may optionally also pass along a `service` or `X-service` header value that identifies the target application url. The header value must match the OAuth service definition in the registry that is linked to the client id.

### Refresh Token

The refresh token grant type retrieves a new access token from a refresh token (emitted for a previous access token),
when this previous access token is expired.

| Endpoint                | Parameters                                               | Response
|-------------------------|----------------------------------------------------------|---------------------------
| `/oauth2.0/accessToken`   | `grant_type=refresh_token&client_id=<ID>`<br/>`&client_secret=SECRET&refresh_token=REFRESH_TOKEN` | The new access token.

### Device Flow

| Endpoint                | Parameters                                               | Response
|-------------------------|----------------------------------------------------------|---------------------------
| `/oauth2.0/accessToken`   | `response_type=device_code&client_id=<ID>` | Device authorization url, device code and user code.
| `/oauth2.0/accessToken`   | `response_type=device_code&client_id=<ID>&code=<DEVICE_CODE>` | New access token once the user code is approved.

## Grant Type Selection

A grant is a method of acquiring an access token. Deciding which grants to implement depends on the type of client the end user will be using, and the experience you want for your users.

![](https://alexbilbie.com/images/oauth-grants.svg)

To learn more about profiles and grant types, please [review this guide](https://alexbilbie.com/guide-to-oauth-2-grants/).

## Register Clients

Every OAuth client must be defined as a CAS service (notice the new *clientId* and *clientSecret* properties, specific to OAuth):

```json
{
  "@class" : "org.apereo.cas.support.oauth.services.OAuthRegisteredService",
  "clientId": "clientid",
  "clientSecret": "clientSecret",
  "serviceId" : "^(https|imaps)://<redirect-uri>.*",
  "name" : "OAuthService",
  "id" : 100,
  "supportedGrantTypes": [ "java.util.HashSet", [ "...", "..." ] ],
  "supportedResponseTypes": [ "java.util.HashSet", [ "...", "..." ] ]
}
```

The following fields are supported:

| Field                             | Description
|-----------------------------------|---------------------------------------------------------------------------------
| `clientId`                        | The client identifier for the application/service.
| `clientSecret`                    | The client secret for the application/service.
| `supportedGrantTypes`             | Collection of supported grant types for this service.
| `supportedResponseTypes`          | Collection of supported response types for this service.
| `bypassApprovalPrompt`            | Whether approval prompt/consent screen should be bypassed. Default is `false`.
| `generateRefreshToken`            | Whether a refresh token should be generated along with the access token. Default is `false`.
| `renewRefreshToken`               | Whether the existing refresh token should be expired and a new one generated (and sent along) whenever a new access token is requested (with `grant_type` = `refresh_token`). Only possible if `generateRefreshToken` is set to `true`. Default is `false`.
| `jwtAccessToken`                  | Whether access tokens should be created as JWTs. Default is `false`.
| `serviceId`                       | The pattern that authorizes the redirect URI(s), or same as `clientId` in case `redirect_uri` is not required by the grant type (i.e `client_credentials`, etc).

<div class="alert alert-info"><strong>Keep What You Need!</strong><p>You are encouraged to only keep and maintain properties and settings needed for a particular integration. It is <strong>UNNECESSARY</strong> to grab a copy of all service fields and try to configure them yet again based on their default. While you may wish to keep a copy as a reference, this strategy would ultimately lead to poor upgrades increasing chances of breaking changes and a messy deployment at that.</p></div>

Service definitions are typically managed by the [service management](../services/Service-Management.html) facility.

<div class="alert alert-warning"><strong>Usage Warning!</strong><p>CAS today does not strictly enforce the collection of authorized supported response/grant types for backward compatibility reasons. This means that if left undefined, all grant and response types may be allowed by the service definition and related policies. Do please note that this behavior is <strong>subject to change</strong> in future releases and thus, it is strongly recommended that all authorized grant/response types for each profile be declared in the service definition immediately to avoid surprises in the future.</p></div>

### Encryptable Client Secrets

Client secrets for OAuth relying parties may be defined as encrypted values prefixed with `{cas-cipher}`:

```json
{
  "@class": "org.apereo.cas.support.oauth.services.OAuthRegisteredService",
  "clientId": "clientid",
  "clientSecret": "{cas-cipher}eyJhbGciOiJIUzUxMiIs...",
  "serviceId" : "^(https|imaps)://<redirect-uri>.*",
  "name": "Sample",
  "id": 100
}
``` 

Client secrets may be encrypted using CAS-provided cipher operations either manually or via the [CAS Command-line shell](Configuring-Commandline-Shell.html).
To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#oauth2).

### Attribute Release

Attribute/claim filtering and release policies are defined per OAuth service.
See [this guide](../integration/Attribute-Release-Policies.html) for more info.

## OAuth Token Expiration Policy

The expiration policy for OAuth tokens is controlled by CAS settings and properties. Note that while access and refresh tokens may have their own lifetime and expiration policy, they are typically upper-bound to the length of the CAS single sign-on session.

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#oauth2).

### Per Service

The expiration policy of certain OAuth tokens can be conditionally decided on a per-application basis. The candidate service 
whose token expiration policy is to deviate from the default configuration must be designed as the following snippets demonstrate.

#### OAuth Code

```json
{
  "@class" : "org.apereo.cas.support.oauth.services.OAuthRegisteredService",
  "clientId": "clientid",
  "clientSecret": "clientSecret",
  "serviceId" : "^(https|imaps)://<redirect-uri>.*",
  "name" : "OAuthService",
  "id" : 100,
  "codeExpirationPolicy": {
    "@class": "org.apereo.cas.support.oauth.services.DefaultRegisteredServiceOAuthCodeExpirationPolicy",
    "numberOfUses": 1,
    "timeToLive": "10"
  }
}
```

#### OAuth Access Token

```json
{
  "@class" : "org.apereo.cas.support.oauth.services.OAuthRegisteredService",
  "clientId": "clientid",
  "clientSecret": "clientSecret",
  "serviceId" : "^(https|imaps)://<redirect-uri>.*",
  "name" : "OAuthService",
  "id" : 100,
  "accessTokenExpirationPolicy": {
    "@class": "org.apereo.cas.support.oauth.services.DefaultRegisteredServiceOAuthAccessTokenExpirationPolicy",
    "maxTimeToLive": "1000",
    "timeToLive": "100"
  }
}
```

#### OAuth Device Token

```json
{
  "@class" : "org.apereo.cas.support.oauth.services.OAuthRegisteredService",
  "clientId": "clientid",
  "clientSecret": "clientSecret",
  "serviceId" : "^(https|imaps)://<redirect-uri>.*",
  "name" : "OAuthService",
  "id" : 100,
  "accessTokenExpirationPolicy": {
    "@class": "org.apereo.cas.support.oauth.services.DefaultRegisteredServiceOAuthDeviceTokenExpirationPolicy",
    "timeToLive": "100"
  }
}
```

#### OAuth Refresh Token

```json
{
  "@class" : "org.apereo.cas.support.oauth.services.OAuthRegisteredService",
  "clientId": "clientid",
  "clientSecret": "clientSecret",
  "serviceId" : "^(https|imaps)://<redirect-uri>.*",
  "name" : "OAuthService",
  "id" : 100,
  "accessTokenExpirationPolicy": {
    "@class": "org.apereo.cas.support.oauth.services.DefaultRegisteredServiceOAuthRefreshTokenExpirationPolicy",
    "timeToLive": "100"
  }
}
```

## JWT Access Tokens

By default, OAuth access tokens are created as opaque identifiers. There is also the option to generate JWTs as access tokens on a per-service basis:
        
```json
{
    "@class" : "org.apereo.cas.support.oauth.services.OAuthRegisteredService",
    "clientId": "clientid",
    "clientSecret": "clientSecret",
    "serviceId" : "^(https|imaps)://<redirect-uri>.*",
    "name" : "OAuthService",
    "id" : 100,
    "jwtAccessToken": true,
    "properties" : {
      "@class" : "java.util.HashMap",
      "accessTokenAsJwtSigningKey" : {
         "@class" : "org.apereo.cas.services.DefaultRegisteredServiceProperty",
         "values" : [ "java.util.HashSet", [ "..." ] ]
      },
      "accessTokenAsJwtEncryptionKey" : {
           "@class" : "org.apereo.cas.services.DefaultRegisteredServiceProperty",
           "values" : [ "java.util.HashSet", [ "..." ] ]
      },
      "accessTokenAsJwtSigningEnabled" : {
         "@class" : "org.apereo.cas.services.DefaultRegisteredServiceProperty",
         "values" : [ "java.util.HashSet", [ "true" ] ]
      },
      "accessTokenAsJwtEncryptionEnabled" : {
         "@class" : "org.apereo.cas.services.DefaultRegisteredServiceProperty",
         "values" : [ "java.util.HashSet", [ "true" ] ]
      }
    }
}
```

Signing and encryption keys may also be defined on a per-service basis, or globally via CAS settings.
To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#oauth2).

## OAuth User Profile Structure

The requested user profile may be rendered and consumed by the application using the following options.

### Nested

By default, the requested user profile is rendered using a `NESTED` format where the authenticated principal and attributes are placed inside `id` and `attributes` tags respectively in the final structure.

```json
{
  "id": "casuser",
  "attributes": {
    "email": "casuser@example.org",
    "name": "CAS"
  },
  "something": "else"
}
```

### Flat

This option flattens principal attributes by one degree, putting them at the same level as `id`. Other nested elements in the final payload are left untouched.

```json
{
  "id": "casuser",
  "email": "casuser@example.org",
  "name": "CAS",
  "something": "else"
}
```

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#oauth2).

### Custom

If you wish to create your own profile structure, you will need to design a component and register it with CAS to handle the rendering of the user profile:

```java
package org.apereo.cas.support.oauth;

@Configuration("MyOAuthConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class MyOAuthConfiguration {

    @Bean
    @RefreshScope
    public OAuth20UserProfileViewRenderer oauthUserProfileViewRenderer() {
        ...
    }
}
```

[See this guide](../configuration/Configuration-Management-Extensions.html) to learn more about how to register configurations into the CAS runtime.

## Throttling

Authentication throttling may be enabled for the `/oauth2.0/accessToken` provided support is included in the overlay to [turn on authentication 
throttling](Configuring-Authentication-Throttling.html) support. The throttling mechanism that handles the usual CAS server endpoints for authentication
and ticket validation, etc is then activated for the OAuth endpoints that are supported for throttling. 

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#oauth2).

## Server Configuration

Remember that OAuth features of CAS require session affinity (and optionally session replication),
as the authorization responses throughout the login flow are stored via server-backed session storage mechanisms. You will need to configure your deployment 
environment and load balancers accordingly.

## Sample Client Applications

- [OAuth2 Sample Webapp](https://github.com/cas-projects/oauth2-sample-java-webapp)

# OpenID Authentication

To configure CAS to act as an OpenID provider, please [see this page](../protocol/OpenID-Protocol.html).
