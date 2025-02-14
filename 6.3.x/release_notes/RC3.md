---
layout: default
title: CAS - Release Notes
category: Planning
---

# RC3 Release Notes

We strongly recommend that you take advantage of the release candidates as they come out. Waiting for a `GA` release is only going to set 
you up for unpleasant surprises. A `GA` is [a tag and nothing more](https://apereo.github.io/2017/03/08/the-myth-of-ga-rel/). Note that CAS 
releases are *strictly* time-based releases; they are not scheduled or based on specific benchmarks, statistics or completion of features. To gain 
confidence in a particular release, it is strongly recommended that you start early by experimenting with release candidates and/or follow-up snapshots.

## Apereo Membership

If you benefit from Apereo CAS as free and open-source software, we invite you to [join the Apereo Foundation](https://www.apereo.org/content/apereo-membership) 
and financially support the project at a capacity that best suits your deployment. Note that all development activity 
is performed *almost exclusively* on a voluntary basis with no expectations, commitments or strings attached. Having the financial means to better 
sustain engineering activities will allow the developer community to allocate *dedicated and committed* time for long-term support, 
maintenance and release planning, especially when it comes to addressing critical and security issues in a timely manner. Funding will 
ensure support for the software you rely on and you gain an advantage and say in the way Apereo, and the CAS project at that, runs 
and operates. If you consider your CAS deployment to be a critical part of the identity and access management ecosystem, this is a viable option to consider.

## Get Involved

- Start your CAS deployment today. Try out features and [share feedback](/cas/Mailing-Lists.html).
- Better yet, [contribute patches](/cas/developer/Contributor-Guidelines.html).
- Suggest and apply documentation improvements.

## Resources

- [Release Schedule](https://github.com/apereo/cas/milestones)
- [Release Policy](/cas/developer/Release-Policy.html)

## Overlay

In the `gradle.properties` of the [CAS WAR Overlay](../installation/WAR-Overlay-Installation.html), adjust the following setting:

```properties
cas.version=6.3.0-RC3
```

<div class="alert alert-info">
  <strong>System Requirements</strong><br/>There are no changes to the minimum system/platform requirements for this release.
</div>

## New & Noteworthy

The following items are new improvements and enhancements presented in this release. 

### Test Coverage via CodeCov

CAS test coverage across all modules in the codebase has now reached `86%` and continues to climb. Additional validation rules are also applied 
to fail all pull requests that fall below this threshold. This area will be closely monitored and improved
as progress is made with the goal of hopefully reaching at least `88%` before the final GA release. Of course, 
this will not be a blocker for the final release.

### WebAuthn FIDO2 Support

CAS can now support [WebAuthn FIDO2](../mfa/FIDO2-WebAuthn-Authentication.html) for multifactor authentication.

### Service Authentication Policy

An additional [authentication policy for services](../services/Configuring-Service-AuthN-Policy.html) is now 
available to require allowed/defined authentication handlers.

### Delegated Authentication SSO Participation

Participation in single sign-on sessions for applications that are 
assigned a [delegated authentication policy](../integration/Delegate-Authentication.html#access-strategy) can now be conditionally renewed
if the single sign-on session is established using a disallowed provider for the application.

### SAML2 Metadata URL Proxies

SAML2 application definitions whose metadata is retrieved from URLs can now specify a proxy endpoint in the application definition,
in case the metadata is behind a proxy.

### Git Service Registry Groups

Service definitions managed by [Git Service Registry](../services/Git-Service-Management.html) are now given an option, on by default,
to locate and group service definitions by their type, and store them in dedicated folders for easier management.

### Registered Service Properties

Assigning [custom properties](../services/Configuring-Service-Custom-Properties.html) to registered service definitions can now 
take advantage of [Spring Expressions](../configuration/Configuration-Spring-Expressions.html).

## Other Stuff

- Password reset verification attempts can now properly handle expired or invalid reset attempts in the authentication webflow.
- Logged ascii-art statements are now routed to their own dedicated `AsciiArt` logging category. 
- Configuration distributed session store and its replication for OAuth, OpenID Connect and Delegated Authentication is corrected to determine the cookie path automatically if CAS is set to auto-configure the cookie path based on the context. This item would be specially applicable if the `autoConfigureCookiePath` is set to false.
- Multiple LDAP base-dns can be specified and join together for a single LDAP configuration block using a special delimiter character.
- [Integration with Ehcache](../ticketing/Ehcache-Ticket-Registry.html) gains a new setting to allow disk persistence.
- CAS configuration properties based on an embedded `application.yml` can now recognize settings based on 
active profiles via `classpath:/application-{profile}.yml` configuration files.

## Library Upgrades

- Cassandra Driver
- Oshi
- Twilio
- JUnit
- SnakeYAML
- Eureka
- Apache Tomcat
- Spring
- Spring Boot
- Spring Data
- JGit
- Mockito
- Spring Security
- Spring Cloud

