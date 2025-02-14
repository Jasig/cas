---
layout: default
title: CAS - Delegate authentication
---

<p align="center">
  <img src="https://pac4j.github.io/pac4j/img/logo-cas.png" width="300" />
</p>

# Overview
The CAS server implements the CAS protocol on server side and may even behave like an OAuth provider, an OpenID provider or a SAML IdP. Whatever the protocol, the CAS server is first of all a server.

But the CAS server can also act as a client using the [pac4j security engine](https://github.com/pac4j/pac4j) and delegate the authentication to:

* Another CAS server
* An OAuth provider: Facebook, Twitter, Google, LinkedIn, Yahoo and several other providers
* An OpenID provider: myopenid.com
* A SAML identity provider
* An OpenID Connect identity provider.

Support is enabled by including the following dependency in the Maven WAR overlay:

```xml
<dependency>
    <groupId>org.jasig.cas</groupId>
    <artifactId>cas-server-support-pac4j-webflow</artifactId>
    <version>${cas.version}</version>
</dependency>
```


## Configuration

### Add the needed clients

An identity provider is a server which can authenticate users (like Google, Yahoo...) instead of a CAS server. If you want to delegate the CAS authentication to Twitter for example, you have to add an OAuth client for the provider: Twitter. For each delegated authentication mechanism, you must define the appropriate client.

Clients can be defined via properties for the most common ones (in the `cas.properties` file):

```properties
# cas.pac4j.facebook.id=
# cas.pac4j.facebook.secret=
# cas.pac4j.facebook.scope=
# cas.pac4j.facebook.fields=
# cas.pac4j.twitter.id=
# cas.pac4j.twitter.secret=
# cas.pac4j.saml.keystorePassword=
# cas.pac4j.saml.privateKeyPassword=
# cas.pac4j.saml.keystorePath=
# cas.pac4j.saml.identityProviderMetadataPath=
# cas.pac4j.saml.maximumAuthenticationLifetime=
# cas.pac4j.saml.serviceProviderEntityId=
# cas.pac4j.saml.serviceProviderMetadataPath=
# cas.pac4j.cas.loginUrl=
# cas.pac4j.cas.protocol=
# cas.pac4j.oidc.id=
# cas.pac4j.oidc.secret=
# cas.pac4j.oidc.discoveryUri=
# cas.pac4j.oidc.useNonce=
# cas.pac4j.oidc.preferredJwsAlgorithm=
# cas.pac4j.oidc.maxClockSkew=
# cas.pac4j.oidc.customParamKey1=
# cas.pac4j.oidc.customParamValue1=
# cas.pac4j.oidc.customParamKey2=
# cas.pac4j.oidc.customParamValue2=
```

Or like any bean, in a dedicated `WEB-INF/spring-configuration/pac4jContext.xml` file:

```xml
<bean id="facebook1" class="org.pac4j.oauth.client.FacebookClient">
  <property name="key" value="fbkey" />
  <property name="secret" value="fbsecret" />
  <property name="scope"
    value="email,user_likes,user_about_me,user_birthday,user_education_history,user_hometown" />
  <property name="fields"
    value="id,name,first_name,middle_name,last_name,gender,locale,languages,link,username,third_party_id,timezone,updated_time" />
</bean>

<bean id="twitter1" class="org.pac4j.oauth.client.TwitterClient">
  <property name="key" value="twkey" />
  <property name="secret" value="twsecret" />
</bean>

<bean id="caswrapper1" class="org.pac4j.oauth.client.CasOAuthWrapperClient">
  <property name="key" value="this_is_the_key" />
  <property name="secret" value="this_is_the_secret" />
  <property name="casOAuthUrl" value="http://mycasserver2/oauth2.0" />
</bean>

<bean id="cas1" class="org.pac4j.cas.client.CasClient">
  <property name="casLoginUrl" value="http://mycasserver2/login" />
</bean>

<bean id="myopenid1" class="org.pac4j.openid.client.MyOpenIdClient" />
```

Notice that for each OAuth provider, the CAS server is considered as an OAuth client and therefore should be declared as an OAuth client at the OAuth provider. After the declaration, a key and a secret is given by the OAuth provider which has to be defined in the beans (*the_key_for_xxx* and *the_secret_for_xxx* values for the *key* and *secret* properties).

For the CAS OAuth wrapping, the *casOAuthUrl* property must be set to the OAuth wrapping url of the other CAS server which is using OAuth wrapping (for example: *http://mycasserver2/oauth2.0*).

### Add links on the login page to authenticate on remote providers

All available clients are automatically displayed on the login page as clickable buttons under the "Or login with:" label.

If you customize the login page, you can access the text to display (which is mostly the name of the client) and the url for the redirection to the identity provider in the `pac4jUrls` object (which is a map of names to urls).


### Identifier of the authenticated user

After a successful delegated authentication, a user is created inside the CAS server with a specific identifier: this one can be created only from the technical identifier received from the external identity provider (like 1234) or as a "typed identifier" (like FacebookProfile#1234), which is the default.

This can be defined in the `cas.properties` file:

```properties
cas.pac4j.client.authn.typedidused=true
```


## Demo

Take a look at this demo: [cas-pac4j-oauth-demo](https://github.com/leleuj/cas-pac4j-oauth-demo) to see this authentication delegation mechanism in action.


## How to use this support on CAS applications side?

### Information returned by a delegated authentication

Once you have configured (see information above) your CAS server to act as an OAuth, CAS, OpenID (Connect) or SAML client, users will be able to authenticate at a OAuth/CAS/OpenID/SAML provider (like Facebook) instead of authenticating directly inside the CAS server.

In the CAS server, after this kind of delegated authentication, users have specific authentication data.

The `Authentication` object has:

* The attribute `AuthenticationManager.AUTHENTICATION_METHOD_ATTRIBUTE` (authenticationMethod) set to *`org.jasig.cas.support.pac4j.authentication.handler.support.ClientAuthenticationHandler`*
* The attribute *`clientName`* set to the type of the provider used during authentication process.

The `Principal` object of the `Authentication` object has:

* An identifier which is the profile type + `#` + the identifier of the user for this provider (i.e `FacebookProfile#0000000001`)
* Attributes populated by the data retrieved from the provider (first name, last name, birthdate...)

### How to send profile attributes to CAS client applications?

In CAS applications, through service ticket validation, user information are pushed to the CAS client and therefore to the application itself.

The identifier of the user is always pushed to the CAS client. For user attributes, it involves both the configuration at the server and the way of validating service tickets.

On CAS server side, to push attributes to the CAS client, it should be configured in the expected service:

```json
{
  "@class" : "org.jasig.cas.services.RegexRegisteredService",
  "serviceId" : "sample",
  "name" : "sample",
  "id" : 100,
  "description" : "sample",
  "attributeReleasePolicy" : {
    "@class" : "org.jasig.cas.services.ReturnAllowedAttributeReleasePolicy",
    "allowedAttributes" : [ "java.util.ArrayList", [ "name", "first_name", "middle_name" ] ]
  }
}
```

On CAS client side, to receive attributes, you need to use the SAML validation or the CAS 3.0 validation, that is the `/p3/serviceValidate` url.

### How to recreate user profiles in CAS applications?

In the CAS server, the complete user profile is known but when attributes are sent back to the CAS client applications, there is some kind of "CAS serialization" which makes data uneasy to be restored at their original state.

Though, you can now completely rebuild the original user profile from data returned in the CAS `Assertion`.

After validating the service ticket, an `Assertion` is available in the CAS client from which you can get the identifier and the attributes of the authenticated user using the pac4j library:

```java
final AttributePrincipal principal = assertion.getPrincipal();
final String id = principal.getName();
final Map<String, Object> attributes = principal.getAttributes();
```

As the identifier stores the kind of profile in its own definition (`*clientName#idAtProvider*`), you can use the `org.pac4j.core.profile.ProfileHelper.buildProfile(id, attributes)` method to recreate the original profile:

```java
final FacebookProfile rebuiltProfileOnCasClientSide =
    (FacebookProfile) ProfileHelper.buildProfile(id, attributes);
```

and then use it in your application.
