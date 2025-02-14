---
layout: default
title: CAS - Security Guide
---

# Security Guide
CAS is security software that provides secure Web-based single sign-on to Web-based applications. Single sign-on
provides a win/win in terms of security and convenience: it reduces password exposure to a single, trusted credential
broker while transparently providing access to multiple services without repetitious logins. The use of CAS generally
improves the security environment, but there are several CAS configuration, policy, and deployment concerns that should
be considered to achieve suitable security.


## System Security Considerations


### Secure Transport (https)
All communication with the CAS server MUST occur over a secure channel (i.e. TLSv1). There are two primary
justifications for this requirement:

1. The authentication process requires transmission of security credentials.
2. The CAS ticket-granting ticket is a bearer token.

Since the disclosure of either data would allow impersonation attacks, it's vitally important to secure the
communication channel between CAS clients and the CAS server.

Practically, it means that all CAS urls must use HTTPS, but it **also** means that all connections from the CAS server to the application must be done using HTTPS:

- when the generated service ticket is sent back to the application on the "service" url
- when a proxy callback url is called.


### Connections to Dependent Systems
CAS commonly requires connections to other systems such as LDAP directories, databases, and caching services.
We generally recommend to use secure transport (SSL/TLS, IPSec) to those systems where possible, but there may
be compensating controls that make secure transport uncessary. Private networks and corporate networks with strict
acces controls are common exceptions, but secure transport is recommended nonetheless.
Client certification validation can be another good solution for LDAP to bring sufficient security.

As stated previously, connections to other systems must be secured. But if the CAS server is deployed on several nodes, the same applies to the CAS server itself. If a cache-based ticket registry runs without any security issue on a single CAS server, synchronization can become a security problem when using multiple nodes if the network is not protected.

Any disk storage is also vulnerable if not properly secured. EhCache overflow to disk may be turned off to increase protection whereas advanced encryption data mechanism should be used for the database disk storage.

## Deployment-Driven Security Features
CAS supports a number of features that can be leveraged to implement various security policies.
The following features are provided through CAS configuration and CAS client integration. Note that many features
are available out of the box, while others require explicit setup.


### Forced Authentication
Many CAS clients and supported protocols support the concept of forced authentication whereby a user must
reauthenticate to access a particular service. The CAS protocols support forced authentication via the _renew_
parameter. Forced authentication provides additional assurance in the identity of
the principal of an SSO session since the user must verify his or her credentials prior to access.
Forced authentication is suitable for services where higher security is desired or mandated. Typically forced
authentication is configured on a per-service basis, but the [service management](#service-management) facility
provides some support for implementing forced authentication as a matter of centralized security policy.
Forced authentication may be combined with [multi-factor authentication](#multifactor-authentication) features to
implement arbitrary service-specific access control policy.


### Passive Authentication
Some CAS protocols support passive authentication where access to a CAS-protected service is granted anonymously
when requested. The CASv2 and CASv3 protocols support this capability via the _gateway_ feature. Passive authentication
complements forced authentication; where forced authentication requires authentication to access a service, passive
authentication permits service access, albeit anonymously, without authentication.


### Proxy Authentication
Proxy authentication, or delegated authentication, provides a powerful, important, and potentially security-improving
feature of CAS. Proxy authentication is supported by the CASv2 and CASv3 protocols and is mediated by proxy tickets
that are requested by a service on behalf of a user; thus the service proxies authentication for the user.
Proxy authentication is commonly used in cases where a service cannot interact directly with the user and as an
alternative to replaying end-user credentials to a service.

However, proxy tickets carry risk in that services accepting proxy tickets are responsible for validating the
proxy chain (the list of services through which the end-user's authentication have been delegated to arrive at
the ticket validating service). Services can opt out of accepting proxy tickets entirely (and avoid
responsibility for validating proxy chains) by simply validating tickets against the /serviceValidate
validation endpoint, but experience has shown it's easy to be confused about this and configure to
unintentionally use the /proxyValidate endpoint yet not scrutinize any proxy chains that appear in the
ticket validation response. Thus proxy authentication requires careful configuration for proper security controls;
it is recommended to disable proxy authentication components at the CAS server if proxy authentication is not
needed.

Historically any service could obtain a proxy-granting ticket and from it a proxy ticket to access any other service.
In other words, the security model is decentralized rather than centralized. The service management facility affords
some centralized control of proxy authentication by exposing a proxy authentication flag that can enabled or disabled
on a per-service basis. By default registered services are not granted proxy authentication capability.


### Multi-factor Authentication
CAS provides support for multi-factor authentication in one of two modes: global and per-service. The global case
where multiple credentials are invariably required on the login form is straightforward: the user interface is
modified to accept multiple credentials and authentication components are configured to require successful
authentication of all provided credentials.

The per-service case is both more interesting and more complicated:

* Levels of identity assurance (LOA) for credentials and groups of credentials must be established.
* Security policy versus credential LOA must be established per service.
* Service access policy must be configured via the [service management](#service-management) facility.

The first two tasks are vital but outside the scope of this document. Application of service access policy via the
service management facility is implemented by declaring the
[authentication handlers](../installation/Configuring-Authentication-Components.html#authentication-handlers)
that must successfully authenticate credentials in order to permit access; for example, an LDAP authentication
handler and an RSA SecureID authentication handler.

Since multi-factor authentication requires development of institutional security policy, advanced component
configuration (and possibly custom component development), and UI design, it should be regarded more as a framework
than a feature. See the
[multi-factor configuration](../installation/Configuring-Authentication-Components.html#multifactor-authentication-mfa)
section for detailed discussion of configuration concerns and implementation recommendations.


### Credential Caching and Replay
The _ClearPass_ extension provides a mechanism to capture primary authentication credentials, cache them (encrypted),
and replay on demand as needed to access legacy services. While [proxy authentication](#proxy-authentication)
is recommended in lieu of password replay, it may be required to integrate legacy services with CAS. See the
[ClearPass](../integration/ClearPass.html) documentation for detailed information.


### Service Management
The service management facility provides a number of service-specific configuration controls that affect security
policy and provide some support for centralized security policy. (Note that CAS has historically supported the
decentralized security policy model.) Some highlights of service management controls:

* Authorized services
* Forced authentication
* Attribute release
* Proxy authentication control
* Theme control
* Multi-factor service access policy

The service management facility is comprised of a service registry containing one or more registered services, each
of which specifies the management controls above. The service registry can be controlled via static configuration files,
a Web user interface, or both. See the [Service Management](../installation/Service-Management.html) section for more
information.

<div class="alert alert-warning"><strong>Authorized Services</strong><p>
As a security best practice, it is <strong>strongly</strong> recommended to limit the service management facility
to only include the list of known applications that are authorized to use CAS. Leaving the management interface
open for all applications may create an opportunity for security attacks.
</p></div>

### SSO Cookie Encryption
A ticket-granting cookie is an HTTP cookie set by CAS upon the establishment of a single sign-on session. The cookie value is by default encrypted and signed via settings defined in `cas.properties`. While sample data is provided for initial deployments, these keys MUST be regenerated per your specific environment. Please [see this guide](../installation/Configuring-SSO-Session-Cookie.html) for more info.

### Ticket Expiration Policies
Ticket expiration policies are a primary mechanism for implementing security policy. Ticket expiration policy allows
control of some important aspects of CAS SSO session behavior:

* SSO session duration (sliding expiration, absolute)
* Ticket reuse

See the [Configuring Ticketing Components](../installation/Configuring-Ticketing-Components.html) section for a
detailed discussion of the various expiration policies and configuration instructions.


### Single Sign-Out
Single sign-out, or single log-out (SLO), is a feature by which CAS services are notified of the termination of a CAS
SSO session with the expectation that services terminate access for the SSO session owner. While single sign-out can
improve security, it is fundamentally a best-effort facility and may not actually terminate access to all services
consumed during an SSO session. The following compensating controls may be used to improve risks associated with
single sign-out shortcomings:

* Require forced authentication for sensitive services
* Reduce application session timeouts
* Reduce SSO session duration

SLO can happen in two ways: from the CAS server (back-channel logout) and/or from the browser (front-channel logout).
For back-channel logout, the SLO process relies on the `SimpleHttpClient` class which has a threads pool: its size must be defined to properly treat all the logout requests.
Additional not-already-processed logout requests are temporarily stored in a queue before being sent: its size is defined to 20% of the global capacity of the threads pool and can be adjusted.
Both sizes are critical settings of the CAS system and their values should never exceed the real capacity of the CAS server.


### Login Throttling
CAS supports a policy-driven feature to limit successive failed authentication attempts to help prevent brute force
and denial of service attacks. The feature is beneficial in environments where back-end authentication stores lack
equivalent features. In cases where this support is available in underlying systems, we encourage using it instead
of CAS features; the justification is that enabling support in underlying systems provides the feature in all dependent
systems including CAS. See the
[login throttling configuration](../installation/Configuring-Authentication-Components.html#login-throttling)
section for further information.


### Credential Encryption
An open source product called [Java Simplified Encryption](http://www.jasypt.org/cli.html)  allows you to replace clear text passwords in files with encrypted strings that are decrypted at run time. Jasypt can be integrated into the Spring configuration framework so that property values are decrypted as the configuration file is loaded.  Jasypt's approach replaces the the property management technique with one that recognizes encrypted strings and decrypts them. This method uses password-based encryption, which means that the system still needs a secret password in order to decrypt our credentials. We don't want to simply move the secret from one file to another, and Jasypt avoids that by passing the key as an environment variable or even directly to the application through a web interface each time it is deployed.

This ability is beneficial since it removes the need to embed plain-text credentials in configuration files, and allows the adopter to securely keep track of all encrypted settings in source control systems, safely sharing the build configuration with others. Sensitive pieces of data are only restricted to the deployment environment.

### CAS Security Filter
The CAS project provides a number of a blunt [generic security filters][cas-sec-filter] suitable for patching-in-place Java CAS server and Java CAS client deployments vulnerable to certain request parameter based bad-CAS-protocol-input attacks.
The filters are configured to sanitize authentication request parameters and reject the request if it is not compliant with the CAS protocol in the event that for instance, a parameter is repeated multiple times, includes multiple values, contains unacceptable values, etc.

It is **STRONGLY** recommended that all CAS deployments be evaluated and include this configuration if necessary to prevent protocol attacks in situations where the CAS container and environment are unable to block malicious and badly-configured requests.

### Spring Webflow Sessions
The CAS project uses Spring Webflow to manage and orchestrate the authentication process. The conversational state of the
webflow used by CAS is managed by the client which is then passed and tracked throughout various states of the authentication
process. This state must be secured and encrypted to prevent session hijacking. While CAS provides default encryptions
settings out of the box, it is **STRONGLY** recommended that [all CAS deployments](../installation/Webflow-Customization.html) be evaluated prior to production rollouts and regenerate this configuration to prevent attacks. 

## User-Driven Security Features
The following features may be employed to afford some user control of the SSO experience.


### Long Term Authentication
The long term authentication feature, commonly referred to as "Remember Me", is selected (usually via checkbox) on the CAS login form to avoid reauthentication for an extended period of time. Long term authentication allows users to elect additional convenience at the expense of reduced security. The extent of reduced security is a function of the characteristics of the device used to establish a CAS SSO session. A long-term SSO session established from a device owned or operated by a single user is marginally less secure than a standard CAS SSO session. The only real concern would be the increased lifetime and resulting increased exposure of the CAS ticket-granting ticket. Establishing a long-term CAS SSO session from a shared device, on the other hand, may dramatically reduce security.
The likelihood of artifacts from previous SSO sessions affecting subsequent SSO sessions established by other users, even in the face of single sign-out, may increase the likelihood of impersonation. While there are no feasible mitigations for improving security of long-term SSO sessions on a shared device, educating users on the inherent risks may improve overall security.

It is important to note that forced authentication supercedes long term authentication, thus if a service were
configured for forced authentication, authentication would be required for service access even in the context of a
long-term session.

Long term authentication support must be explicitly enabled through
[configuration and UI customization](../installation/Configuring-Authentication-Components.html#long-term-authentication)
during the installation process. Thus deployers choose to offer long-term authentication support, and when available
users may elect to use it via selection on the CAS login form.


### Warn
CAS supports optional notification of service access during an established SSO session. By default CAS
transparently requests tickets needed for service access and presents them to the target service for validation,
whereby upon successful validation access to the service is permitted. In most cases this happens nearly instantly
and the user is not aware of the CAS authentication process required to access CAS-enabled services. There may be
some security benefit to awareness of this process, and CAS supports a _warn_ flag that may be selected by the user
on the CAS login screen to provide an interstitial notification page that is displayed prior to accessing a service.
By default the notification page offers the user an option to proceed with CAS authentication or abort by
navigating away from the target service.

[cas-sec-filter]: https://github.com/Jasig/cas-server-security-filter
