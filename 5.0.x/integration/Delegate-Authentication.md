---
layout: default
title: CAS - Delegate Authentication
---

<p align="center">
  <img src="https://pac4j.github.io/pac4j/img/logo-cas.png" width="150" />
</p>

# Delegate Authentication

CAS can act as a client using the [pac4j security engine](https://github.com/pac4j/pac4j) and delegate the authentication to:

* Another CAS server
* An OAuth provider: Facebook, Twitter, Google, LinkedIn, Yahoo and several other providers.
* An OpenID provider: myopenid.com
* A SAML identity provider
* An OpenID Connect identity provider.

Support is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-pac4j-webflow</artifactId>
    <version>${cas.version}</version>
</dependency>
```

## Register Providers

An identity provider is a server which can authenticate users (like Google, Yahoo...) instead of a CAS server. 
If you want to delegate the CAS authentication to Twitter for example, you have to add an 
OAuth client for the Twitter provider, which will be done automatically for you once provider settings are taught to CAS.

To see the relevant list of CAS properties, please [review this guide](../installation/Configuration-Properties.html).

Notice that for each OAuth provider, the CAS server is considered as an OAuth client and therefore should be declared as 
an OAuth client at the OAuth provider. After the declaration, a key and a secret is given by the OAuth provider which has 
to be defined in the CAS configuration as well.

## User Interface

All available clients are automatically displayed on the login page as clickable buttons.

## Authenticated User Id

After a successful delegated authentication, a user is created inside the CAS server with a specific identifier: 
this one can be created only from the technical identifier received from the external identity provider (like `1234`) 
or as a "typed identifier" (like `FacebookProfile#1234`), which is the default. 

To see the relevant list of CAS properties, please [review this guide](../installation/Configuration-Properties.html).

## Demo

Take a look at [cas-pac4j-oauth-demo](https://github.com/leleuj/cas-pac4j-oauth-demo) 
to see this authentication delegation mechanism in action.

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

On CAS client side, to receive attributes, you need to use the SAML validation or the CAS 3.0 
validation, that is the `/p3/serviceValidate` url.

## Recreate Profiles

In the CAS server, the complete user profile is known but when attributes are sent back to the CAS client applications, 
there is some kind of "CAS serialization" which makes data uneasy to be restored at their original state.

Though, you can now completely rebuild the original user profile from data returned in the CAS `Assertion`.

After validating the service ticket, an `Assertion` is available in the CAS client from which you can get the identifier 
and the attributes of the authenticated user using the pac4j library:

```java
final AttributePrincipal principal = assertion.getPrincipal();
final String id = principal.getName();
final Map<String, Object> attributes = principal.getAttributes();
```

As the identifier stores the kind of profile in its own definition (`*clientName#idAtProvider*`), 
you can use the `org.pac4j.core.profile.ProfileHelper.buildProfile(id, attributes)` method to recreate the original profile:

```java
final FacebookProfile rebuiltProfileOnCasClientSide = (FacebookProfile) ProfileHelper.buildProfile(id, attributes);
```

...and then use it in your application.

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
