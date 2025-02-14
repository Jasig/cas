---
layout: default
title: CAS - Release Notes
category: Planning
---

# RC4 Release Notes

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
cas.version=6.3.0-RC4
```

<div class="alert alert-info">
  <strong>System Requirements</strong><br/>There are no changes to the minimum system/platform requirements for this release.
</div>

## New & Noteworthy

The following items are new improvements and enhancements presented in this release.

### JDK 15 Compatibility

CAS is able to build and run successfully against the latest versions of JDK 15. The JDK baseline requirement 
continues to be set at JDK 11, and this release is keeping up with JDK releases to ensure CAS 
can correctly switch the baseline requirement when deemed necessary.

### Test Coverage via CodeCov

CAS test coverage across all modules in the codebase has now reached `88%` and continues to climb. Additional validation rules are also applied 
to fail all pull requests that fall below this threshold. This area will be closely monitored and improved
as progress is made with the goal of hopefully reaching at least `90%` before the final GA release. Of course, 
this will not be a blocker for the final release.

### Wildcarded Service Definitions

Following up on previous release notes, wildcarded service definitions should now be correctly located via CAS service management facilities
by improving lookup operations to be protocol-aware. For example, in a scenario where two service definitions for OAuth 
and CAS service providers might have a very permissive `.*` for the service URL pattern, CAS should be able
to correctly locate the right definition depending on whether an authentication request is submitted by an 
OAUth relying party or a CAS-enabled client application.

### UI Themes Bundles

Minor improvements to handling of [UI themes](../ux/User-Interface-Customization-Themes.html) when 
it comes to defining a custom theme on a per application basis.
A theme bundle is now also available that will incrementally act as a drawer and a toolbox for a number of common
user interface designs and themes, and may also serve as a reference example on how to define or modify a CAS theme.

### WebAuthN FIDO2 DynamoDb

[WebAuthn FIDO2](../mfa/FIDO2-WebAuthn-Authentication.html) multifactor authentication is able manage and maintain registration records in DynamoDb.

### Kryo v5

The [Kryo library](https://github.com/EsotericSoftware/kryo) used by 
the [Memcached ticket registry](../ticketing/Memcached-Ticket-Registry.html) and family is now upgraded to version `5.0.0`. 
This is a major upgrade and a number of internal components that affect Kryo pooling operations are also revised and upgraded.
However, the upgrade should remain invisible for the most part.

### Google Authenticator w/ LDAP/AD

[Google Authenticator](../mfa/GoogleAuthenticator-Authentication.html) registration records may be kept inside 
LDAP/AD systems. Account registration records are kept inside a designated configurable multi-valued attribute as JSON blobs.

### Integration Tests Upgrades

A number of docker images for backend integration tests are updated to ensure support for 
latest versions of each system. These include the following systems:

- Amazon DynamoDb
- Couchbase
- MongoDb
- Redis
- Apache Cassandra

Furthermore, the test infrastructure is updated to also support OpenLDAP in addition to 389-ds and Samba (AD).

### Optional (Mixed) X.509 Authentication

[X.509 authentication](../installation/X509-Authentication.html), when running an embedded Apache Tomcat, is enhanced to provide for mixed-mode authentication.
In this mode, the user is given an additional option to login via X.509 without being prompted for a certificate preemptively.

## Other Stuff

- LDAP trust managers can now be specified in CAS properties.
- Apereo CAS [Docker images](https://hub.docker.com/r/apereo/cas) are now automatically published by CI for the `latest` tag.
- CAS [Release Process](../developer/Release-Process.html) can now automatically close and publish releases on Sonatype. 
- Webflow auto-configuration is slightly improved to allow Kerberos/SPNEGO and X509 modules to work together.
- [Configuration metadata](../configuration/Configuration-Metadata-Repository.html) is improved to better track required modules, properties and owners for CAS settings.
- The publishing process of the Bill of Materials (BOM) is improved to include all CAS modules in `dependencyManagement` blocks, removing them from the `dependencies` block in the final generated POM with versions separated and extracted as properties in the `properties` section. 
- [Google Authenticator](../mfa/GoogleAuthenticator-Authentication.html) for multifactor authentication is now patched to securely generate QR codes.
 
## Library Upgrades

- Lettuce
- Apache Tomcat
- Java Melody
- Oshi
- Guava
- Nimbus Jose
- Twillio
- Kryo
- Couchbase Driver
- Lombok
- Person Directory
- WebAuthN FIDO2
- Maxmind
- Amazon SDK
- Eureka Client
- Gradle
- Spring Boot



