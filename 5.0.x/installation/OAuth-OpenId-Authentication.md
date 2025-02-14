---
layout: default
title: CAS - OAuth Authentication
---

# OAuth/OpenID Authentication

<div class="alert alert-info"><strong>CAS as OAuth Server</strong><p>This page specifically describes how to enable 
OAuth/OpenID server support for CAS. If you would like to have CAS act as an OAuth/OpenID client communicating with 
other providers (such as Google, Facebook, etc), <a href="../integration/Delegate-Authentication.html">see this page</a>.</p></div>

## Configuration

Support is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
  <groupId>org.apereo.cas</groupId>
  <artifactId>cas-server-support-oauth-webflow</artifactId>
  <version>${cas.version}</version>
</dependency>
```

After enabling OAuth support, the following endpoints will be available:

* **/cas/oauth2.0/authorize**  
It's the url to call to authorize the user: the CAS login page will be displayed and the user will login.

* **/cas/oauth2.0/accessToken**  
It's the url to call to get an access token. The returned format will be plain text by default, but it can be JSON 
if set so in the management webapp per OAuth client.

* **/cas/oauth2.0/profile**  
It's the url to call to get the profile of the authorized user. The response is in JSON format with all attributes of the user.


## Grant types

The following types are supported; they allow you to get an access token representing the current user and OAuth client application.
With the access token, you'll be able to query the `/profile` endpoint and get the user profile.

`/cas/oauth2.0/profile?access_token=ACCESS_TOKEN` returns the user profile.


### Authorization Code

The authorization code grant type is made for UI interactions: the user will enter his own credentials.

- `/cas/oauth2.0/authorize?response_type=code&client_id=ID&redirect_uri=CALLBACK` returns the code as a parameter of the CALLBACK url
- `/cas/oauth2.0/accessToken?grant_type=authorization_code&client_id=ID&client_secret=SECRET&code=CODE&redirect_uri=CALLBACK` returns the access token

### Implicit

The implicit grant type is also made for UI interactions, but for Javascript applications.

- `/cas/oauth2.0/authorize?response_type=token&client_id=ID&redirect_uri=CALLBACK` returns the access token as an anchor parameter of the
 CALLBACK url


### Resource Owner

The resource owner password credentials grant type allows the OAuth client to directly send the user's credentials to the OAuth server.

- `/cas/oauth2.0/accessToken?grant_type=password&client_id=ID&username=USERNAME&password=PASSWORD` returns the access token (based on the
 username/password credentials of a user)


### Refresh Token

The refresh token grant type retrieves a new access token from a refresh token (emitted for a previous access token), 
when this previous access token is expired

- `/cas/oauth2.0/accessToken?grant_type=refresh_token&client_id=ID&client_secret=SECRET&refresh_token=REFRESH_TOKEN` returns the access 
token

To get refresh tokens, the OAuth client must be configured to return refresh tokens (`generateRefreshToken` property).

Notice that sensitive information (`client_secret`, `password` and `refresh_token`) should be sent via POST requests.


## Add OAuth Clients

Every OAuth client must be defined as a CAS service (notice the new *clientId* and *clientSecret* properties, specific to OAuth):

```json
{
  "@class" : "org.apereo.cas.support.oauth.services.OAuthRegisteredService",
  "clientId": "clientid",
  "clientSecret": "clientSecret",
  "bypassApprovalPrompt": false,
  "serviceId" : "^(https|imaps)://hello.*",
  "name" : "HTTPS and IMAPS",
  "id" : 10000001
}
```

## OAuth Expiration Policy

The expiration policy for OAuth tokens is controlled by CAS settings and properties.
To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html).

## Server Configuration

Remember that OAuth features of CAS require session affinity (and optionally session replication),
as the authorization responses throughout the login flow
are stored via server-backed session storage mechanisms. You will need to configure your deployment environment and load balancers
accordinngly.


# OpenID Authentication

To configure CAS to act as an OpenID provider, please [see this page](../protocol/OpenID-Protocol.html).
