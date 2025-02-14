---
layout: default
title: CAS - Configuring Authentication Components
---

# Authentication
The CAS authentication process is performed by several related components:

## `PrincipalNameTransformer`
Transforms the user id string that is typed into the login form into a tentative Principal Name to be
validated by a specific type of Authentication Handler.

## `AuthenticationManager`
Entry point into authentication subsystem. It accepts one or more credentials and delegates authentication to
configured `AuthenticationHandler` components. It collects the results of each attempt and determines effective
security policy.

## `AuthenticationHandler`
Authenticates a single credential and reports one of three possible results: success, failure, not attempted.

## `PrincipalResolver`
Converts information in the authentication credential into a security principal that commonly contains additional
metadata attributes (i.e. user details such as affiliations, group membership, email, display name).

## `AuthenticationMetaDataPopulator`
Strategy component for setting arbitrary metadata about a successful authentication event; these are commonly used
to set protocol-specific data.

Unless otherwise noted, the configuration for all authentication components is handled in `deployerConfigContext.xml`.

## Authentication Manager
CAS ships with a single yet flexible authentication manager, `PolicyBasedAuthenticationManager`, that should be
sufficient for most needs. It performs authentication according to the following contract.

For each given credential do the following:

1. Iterate over all configured authentication handlers.
2. Attempt to authenticate a credential if a handler supports it.
3. On success attempt to resolve a principal.
  1. Check whether a resolver is configured for the handler that authenticated the credential.
  2. If a suitable resolver is found, attempt to resolve the principal.
  3. If a suitable resolver is not found, use the principal resolved by the authentication handler.
4. Check whether the security policy (e.g. any, all) is satisfied.
  1. If security policy is met return immediately.
  2. Continue if security policy is not met.
5. After all credentials have been attempted check security policy again and throw `AuthenticationException`
if not satisfied.

There is an implicit security policy that requires at least one handler to successfully authenticate a credential,
but the behavior can be further controlled by setting `#setAuthenticationPolicy(AuthenticationPolicy)`
with one of the following policies.


## `AnyAuthenticationPolicy`
Satisfied if any handler succeeds. Supports a `tryAll` flag to avoid short circuiting at step 4.1 above and try every
handler even if one prior succeeded. This policy is the default and provides backward-compatible behavior with the
`AuthenticationManagerImpl` component of CAS 3.x.


## `AllAuthenticationPolicy`
Satisfied if and only if all given credentials are successfully authenticated. Support for multiple credentials is
new in CAS and this handler would only be acceptable in a multi-factor authentication situation.


## `RequiredHandlerAuthenticationPolicy`
Satisfied if an only if a specified handler successfully authenticates its credential. Supports a `tryAll` flag to
avoid short circuiting at step 4.1 above and try every handler even if one prior succeeded. This policy could be
used to support a multi-factor authentication situation, for example, where username/password authentication is
required but an additional OTP is optional.

## Authentication Handlers
CAS ships with support for authenticating against many common kinds of authentication systems.
The following list provides a complete list of supported authentication technologies; jump to the section(s) of
interest.

* [Database](Database-Authentication.html)
* [JAAS](JAAS-Authentication.html)
* [LDAP](LDAP-Authentication.html)
* [OAuth 1.0/2.0, OpenID](OAuth-OpenId-Authentication.html)
* [RADIUS](RADIUS-Authentication.html)
* [SPNEGO](SPNEGO-Authentication.html) (Windows)
* [Trusted](Trusted-Authentication.html) (REMOTE_USER)
* [X.509](X509-Authentication.html) (client SSL certificate)
* [Remote Address](Remote-Address-Authentication.html)
* [YubiKey](YubiKey-Authentication.html)
* [Apache Shiro](Shiro-Authentication.html)
* [pac4j](Pac4j-Authentication.html)

There are some additional handlers for small deployments and special cases:

* [Whilelist](Whitelist-Authentication.html)
* [Blacklist](Blacklist-Authentication.html)

<div class="alert alert-info"><strong>Default Credentials</strong><p>To test the default authentication scheme in CAS,
use <strong>casuser</strong> and <strong>Mellon</strong> as the username and password respectively.</p></div>

## Password Encoding

Password encoders are responsible during the authentication event to convert and encode
the credential password to a form that is acceptable by the authentication source.

### Default Encoder

```xml
<alias name="defaultPasswordEncoder" alias="passwordEncoder" />
```

The following settings are applicable:

```properties
# cas.authn.password.encoding.char=UTF-8
# cas.authn.password.encoding.alg=SHA-256
```

### Plain Text

```xml
<alias name="plainTextPasswordEncoder" alias="passwordEncoder" />
```

## Argument Extractors
Extractors are responsible to examine the http request received for parameters that describe the authentication request such as the requesting `service`, etc. Extractors exist for a number of supported authentication protocols and each create appropriate instances of `WebApplicationService` that contains the results of the extraction.

## Principal Resolution
Please [see this guide](Configuring-Principal-Resolution.html) more full details on principal resolution.

### Principal Transformation
Authentication handlers that generally deal with username-password credentials
can be configured to transform the user id prior to executing the authentication sequence. The following components are available:

#### `NoOpPrincipalNameTransformer`
Default transformer, that actually does no transformation on the user id.

```xml
<alias name="noOpPrincipalNameTransformer" alias="principalNameTransformer" />
```

#### `PrefixSuffixPrincipalNameTransformer`
Transforms the user id by adding a postfix or suffix.

```xml
<alias name="prefixSuffixPrincipalNameTransformer" alias="principalNameTransformer" />
```

The following settings are applicable:

```properties
# cas.principal.transform.prefix=
# cas.principal.transform.suffix=
```

#### `ConvertCasePrincipalNameTransformer`
A transformer that converts the form uid to either lowercase or uppercase. The result is also trimmed.
The transformer is also able to accept and work on the result of
a previous transformer that might have modified the uid, such that the two can be chained.

```xml
<alias name="convertCasePrincipalNameTransformer" alias="principalNameTransformer" />
```

The following settings are applicable:

```properties
# cas.principal.transform.upperCase=false
```

## Authentication Metadata
`AuthenticationMetaDataPopulator` components provide a pluggable strategy for injecting arbitrary metadata into the
authentication subsystem for consumption by other subsystems or external components. Some notable uses of metadata
populators:

* Supports the long term authentication feature
* SAML protocol support
* OAuth and OpenID protocol support.

The default authentication metadata populators should be sufficient for most deployments. Where the components are
required to support optional CAS features, they will be explicitly identified and configuration will be provided.

## Long Term Authentication
CAS has support for long term Ticket Granting Tickets, a feature that is also referred to as _"Remember Me"_
to extends the length of the SSO session beyond the typical configuration.
Please [see this guide](Configuring-LongTerm-Authentication.html) for more details.

## Proxy Authentication
Please [see this guide](Configuring-Proxy-Authentication.html) for more details.

## Multi-factor Authentication (MFA)
Please [see this guide](Configuring-Multifactor-Authentication.html) for more details.

## Login Throttling
CAS provides a facility for limiting failed login attempts to support password guessing and related abuse scenarios.
Please [see this guide](Configuring-Authentication-Throttling.html) for additional details on login throttling.

## SSO Session Cookie
A ticket-granting cookie is an HTTP cookie set by CAS upon the establishment of a single sign-on session.
This cookie maintains login state for the client, and while it is valid, the client can present it to CAS in lieu of primary credentials.
Please [see this guide](Configuring-SSO-Session-Cookie.html) for additional details.
