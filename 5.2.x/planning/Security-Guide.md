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

<div class="alert alert-info"><strong>Reporting Issues</strong><p>The security team asks that you please <strong>DO NOT</strong> create publicly-viewable issues or posts to discuss what you may consider a security vulnerability. To report issues properly and learn about how responses are produced, please <a href="/cas/developer/Sec-Vuln-Response.html">see this guide</a>.</p></div>

## Announcements

- [Mar 6 2017 Vulnerability Disclosure](https://apereo.github.io/2017/03/01/moncfgsecvulndisc/)
- [Oct 24 2016 Vulnerability Disclosure](https://apereo.github.io/2016/10/24/servlvulndisc/)
- [Apr 8 2016 Vulnerability Disclosure](https://apereo.github.io/2016/04/08/commonsvulndisc/)

## System Security Considerations

Infrastructure security matters to consider may include the following.

### Secure Transport (https)

All communication with the CAS server MUST occur over a secure channel (i.e. TLSv1). There are two primary
justifications for this requirement:

1. The authentication process requires transmission of security credentials.
2. The CAS ticket-granting ticket is a bearer token.

Since the disclosure of either data would allow impersonation attacks, it's vitally important to secure the
communication channel between CAS clients and the CAS server.

Practically, it means that all CAS urls must use HTTPS, but it **also** means that all connections
from the CAS server to the application must be done using HTTPS:

- when the generated service ticket is sent back to the application on the "service" url
- when a proxy callback url is called.

To see the relevant list of CAS properties and tune this behavior, please [review this guide](../installation/Configuration-Properties.html#http-client).


### Connections to Dependent Systems

CAS commonly requires connections to other systems such as LDAP directories, databases, and caching services.
We generally recommend to use secure transport (SSL/TLS, IPSec) to those systems where possible, but there may
be compensating controls that make secure transport unnecessary. Private networks and corporate networks with strict
access controls are common exceptions, but secure transport is recommended nonetheless.
Client certification validation can be another good solution for LDAP to bring sufficient security.

As stated previously, connections to other systems must be secured. But if the CAS server is deployed on several nodes,
the same applies to the CAS server itself. If a cache-based ticket registry runs without any security issue on a single
CAS server, synchronization can become a security problem when using multiple nodes if the network is not protected.

Any disk storage is also vulnerable if not properly secured. EhCache overflow to disk may be turned off to increase
protection whereas advanced encryption data mechanism should be used for the database disk storage.

## Deployment-Driven Security Features

CAS supports a number of features that can be leveraged to implement various security policies.
The following features are provided through CAS configuration and CAS client integration. Note that many features
are available out of the box, while others require explicit setup

### Forced Authentication

Many CAS clients and supported protocols support the concept of forced authentication whereby a user must
re-authenticate to access a particular service. The CAS protocols support forced authentication via the _renew_
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
* Service authorization control
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

A ticket-granting cookie is an HTTP cookie set by CAS upon the establishment of a single sign-on session.
The cookie value is by default encrypted and signed via settings defined in CAS properties.
While sample data is provided for initial deployments, these keys **MUST** be regenerated per your specific
environment. Please [see this guide](../installation/Configuring-SSO-Session-Cookie.html) for more info.

### Password Management Secure Links

Account password reset requests are handled via a secured link that is sent to the registered
email address of the user. The link is available only within a defined time window
and the request is properly signed and encrypted by CAS. While sample data is provided for initial deployments, these keys **MUST** be regenerated per your specific environment.

Please [see this guide](../installation/Password-Policy-Enforcement.html) for more info.

### Protocol Ticket Encryption

Protocol tickets that are issued by CAS and shared with other applications such as service tickets may optionally go through a signing/encryption process. Even though the CAS server will always cross check ticket validity and expiration policy, this may be forced as an extra check to ensure tickets in transit to other applications are not tampered with and remain to be authentic. While sample data is provided for initial deployments, these keys **MUST** be regenerated per your specific environment.

<div class="alert alert-warning"><strong>Pay Attention</strong><p>Encrypting and signing a generated ticket will, depending on the encryption method and algorithm used, increase the generated ticket length. Not all CAS clients are equipped to handle lengthy ticket strings and may get upset with you. Evaluate existing integrations before turning this on and consider whether this feature is truly needed for your deployment.</p></div>

To see the relevant list of CAS properties, please [review this guide](../installation/Configuration-Properties.html#protocol-ticket-security).


### Ticket Registry Encryption

Secure ticket replication as it regards clustered CAS deployments may be required to ensure generated tickets by CAS are not tampered with in transit. CAS covers this issue by allowing tickets to be natively encrypted and signed. While sample data is provided for initial deployments, these keys **MUST** be regenerated per your specific environment.
Please [see this guide](../installation/Ticket-Registry-Replication-Encryption.html) for more info.

### Administrative Pages Security

CAS provides a large variety of web interfaces that are aimed at system administrators and deployers.
These screens along with a number of REST endpoints allow a CAS deployer to manage and reconfigure CAS behavior without resorting to
native command-line interfaces. Needless to say, these endpoints and screens must be secured and allowed proper access only to
authorized parties. Please [see this guide](../installation/Monitoring-Statistics.html) for more info.

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

To learn how sensitive CAS settings can be secured via encryption, [please review this guide](Configuration-Properties-Security.html).

### CAS Security Filter

The CAS project provides a number of a blunt [generic security filters][cas-sec-filter] suitable for patching-in-place Java
CAS server and Java CAS client deployments vulnerable to certain request parameter based bad-CAS-protocol-input attacks.
The filters are configured to sanitize authentication request parameters and reject the request if it is not compliant with
the CAS protocol in the event that for instance, a parameter is repeated multiple times, includes multiple values, contains unacceptable values, etc.

It is **STRONGLY** recommended that all CAS deployments be evaluated and include this configuration if necessary to prevent
protocol attacks in situations where the CAS container and environment are unable to block malicious and badly-configured requests.

#### CORS

CAS provides first-class support for enabling HTTP access control (CORS).
One application of CORS is when a resource makes a cross-origin HTTP request when it requests a resource from a
different domain than the one which the first resource itself serves. This should help more with CAS-enabled
applications are accessed via XHR/Ajax requests.

To see the relevant list of CAS properties and tune this behavior, please [review this guide](../installation/Configuration-Properties.html#http-web-requests).

#### Security Response Headers

As part of the CAS Security Filter, the CAS project automatically provides the necessary configuration to
insert HTTP Security headers into the web response to prevent against HSTS, XSS, X-FRAME and other attacks.
These settings are presently off by default.
To see the relevant list of CAS properties and tune this behavior, please [review this guide](../installation/Configuration-Properties.html#http-web-requests).

To review and learn more about these options, please visit [this guide][cas-sec-filter].

### Spring Webflow Sessions

The CAS project uses Spring Webflow to manage and orchestrate the authentication process. The conversational state of the
webflow used by CAS is managed by the client which is then passed and tracked throughout various states of the authentication
process. This state must be secured and encrypted to prevent session hijacking. While CAS provides default encryption
settings out of the box, it is **STRONGLY** recommended that [all CAS deployments](../installation/Webflow-Customization.html) be
evaluated prior to production deployments and regenerate this configuration to prevent attacks.

### Long Term Authentication

The long term authentication feature, commonly referred to as "Remember Me", is selected (usually via checkbox) on the CAS login
form to avoid re-authentication for an extended period of time. Long term authentication allows users to elect additional convenience at
the expense of reduced security. The extent of reduced security is a function of the characteristics of the device used to establish
a CAS SSO session. A long-term SSO session established from a device owned or operated by a single user is marginally less secure than
a standard CAS SSO session. The only real concern would be the increased lifetime and resulting increased exposure of the
CAS ticket-granting ticket. Establishing a long-term CAS SSO session from a shared device, on the other hand, may dramatically reduce security.
The likelihood of artifacts from previous SSO sessions affecting subsequent SSO sessions established by other users, even in the face
of single sign-out, may increase the likelihood of impersonation. While there is no feasible mitigation for improving security
of long-term SSO sessions on a shared device, educating users on the inherent risks may improve overall security.

It is important to note that forced authentication supersedes long term authentication, thus if a service were
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

[cas-sec-filter]: https://github.com/apereo/cas-server-security-filter
