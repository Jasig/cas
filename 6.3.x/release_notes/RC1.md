---
layout: default
title: CAS - Release Notes
category: Planning
---

# RC1 Release Notes

We strongly recommend that you take advantage of the release candidates as they come out. Waiting for a `GA` release is only going to set 
you up for unpleasant surprises. A `GA` is simply [a tag and nothing more](https://apereo.github.io/2017/03/08/the-myth-of-ga-rel/). Note that CAS 
releases are *strictly* time-based releases; they are not scheduled or based on specific benchmarks, statistics or completion of features. To gain 
confidence in a particular release, it is strongly recommended that you start early by experimenting with release candidates and/or follow-up snapshots.

## Apereo Membership

If you benefit from Apereo CAS as free and open-source software, we invite you to [join the Apereo Foundation](https://www.apereo.org/content/apereo-membership) and financially support the project at a capacity that best suits your deployment. Note that all development activity is performed *almost exclusively* on a voluntary basis with no expectations, commitments or strings attached. Having the financial means to better sustain engineering activities will allow the developer community to allocate *dedicated and committed* time for long-term support, maintenance and release planning, especially when it comes to addressing critical and security issues in a timely manner. Funding will ensure support for the software you rely on and you gain an advantage and say in the way Apereo, and the CAS project at that, runs and operates. If you consider your CAS deployment to be a critical part of the identity and access management ecosystem, this is a viable option to consider.

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
cas.version=6.3.0-RC1
```

<div class="alert alert-info">
  <strong>System Requirements</strong><br/>There are no changes to the minimum system/platform requirements for this release.
</div>

## New & Noteworthy

### Spring Boot 2.3

CAS has switched to Spring Boot `2.3.x.RELEASE`. The CAS Overlay has also been updated to be in sync with this change. While 
this is classified as a Spring Boot feature/minor release, the effects of the upgrade might be more apparent given the framework's significant usage in CAS.

The following settings are affected by the upgrade and should be adjusted to match below:

```properties
server.tomcat.threads.min-spare=10
server.tomcat.threads.max=200

server.servlet.encoding.charset=UTF-8
server.servlet.encoding.enabled=true
server.servlet.encoding.force=true

management.endpoint.health.status.order=WARN,DOWN,OUT_OF_SERVICE,UNKNOWN,UP

server.tomcat.connection-timeout=PT20S
server.tomcat.max-http-form-post-size=2097152

server.tomcat.remoteip.port-header=X-Forwarded-Port
server.tomcat.remoteip.protocol-header=X-Forwarded-Proto
server.tomcat.remoteip.protocol-header-https-value=https
server.tomcat.remoteip.remote-ip-header=X-FORWARDED-FOR
```

There were no compatibility issues discovered during the upgrade and the configuration namespace remains largely unaffected for CAS. That said, please suspect and verify.

### Test Coverage via CodeCov

CAS test coverage across all modules in the codebase has now reached `82%` and continues to climb. Additional validation rules are also applied 
to fail all pull requests that fall below this threshold. This area will be closely monitored and improved
as progress is made with the goal of hopefully reaching at least `85%` before the final GA release. Of course, this will not be a blocker for the final release.

### Redis Cluster Support

Redis support and configuration namespace are now capable of supporting connections to Redis clusters.

### DynamoDb Storage for CAS Events

[CAS Events](../installation/Configuring-Authentication-Events.html) can now be stored in DynamoDb instances.

### Couchbase Acceptable Usage Policy

[Acceptable Usage Policy](../webflow/Webflow-Customization-AUP.html) decisions can now be managed and tracked via Couchbase databases.

### SAML2 Metadata via Git Repositories

[SAML2 Metadata](../installation/Configuring-SAML2-DynamicMetadata.html) artifacts can now be fetched and pulled from Git repositories. This capability supports both service and identity provider artifacts.

### Multifactor Authentication Webflows

Webflow definitions for multifactor authentication providers (i.e Google Authenticator, Authy, etc) are now constructed dynamically 
at runtime via webflow auto-configuration rather than static XML definitions. This allows for better flexibility as well as test coverage when it comes to customizations.

### U2F Multifactor Authentication Trusted Devices

Support for [Multifactor Authentication Trusted Device/Browser](../mfa/Multifactor-TrustedDevice-Authentication.html) is now extended 
to also include [U2F](../mfa/FIDO-U2F-Authentication.html). Furthermore, a number of new administrative actuator endpoints are 
presented to report back on the registered devices or delete/deregister devices.

### Authentication Actuator Endpoints

A number of new [administrative actuator endpoints](../installation/Configuring-Authentication-Components.html) are presented 
to report back on the registered authentication handlers and policies.

### DynamoDb Storage for U2F Multifactor Authentication

[U2F Multifactor Authentication](../mfa/FIDO-U2F-Authentication.html) devices can now be stored in DynamoDb instances.

### Gradle Remote Build Cache

The CAS Gradle build is now connected to a remote build cache server to maximize performance for continuous integration builds.

![image](https://user-images.githubusercontent.com/1205228/84562682-9d46f300-ad6b-11ea-8ed8-3042a3facbec.png)

### Google Authenticator Account Registration

Google Authenticator for multifactor authentication is now enhanced to ask for tokens prior to finalizing the account registration process. Once the provided token is validated, the account will be registered with CAS and is prepared for follow-up multifactor authentication.

![image](https://user-images.githubusercontent.com/1205228/86023135-83323380-ba40-11ea-8d16-4fe8ff560c99.png)

### Apache JMeter Performance Tests

[Apache JMeter performance tests](../high_availability/Performance-Testing-JMeter.html) that ship with CAS are now 
added to [GitHub Actions](https://github.com/apereo/cas/actions). At this point, only the *CAS* variant is tested and 
other test categories for SAML2 and OAuth will be gradually added once a CAS runtime context (i.e. WAR Overlay) can 
be dynamically constructed on-demand with a module selection menu. The goal is to ensure the JMeter test artifacts 
and scripts are maintainable and manageable from one CAS release to the next.

### Google Firebase Cloud Messaging

Preliminary support is available for notification based on [Google Firebase Cloud Messaging](../notifications/Notifications-Configuration.html). The very first consumer
of this feature is the [Simple Multifactor Authentication](../mfa/Simple-Multifactor-Authentication.html) module.

### Service Registry Replication via Apache Kafka

In the event that CAS service definitions are not managed globally via a centralized store, definitions need to be kept in 
sync throughout all CAS nodes in a cluster when more than one node is deployed. If you’d rather not resort to outside tooling and processes or if the native options for your 
deployment are not that attractive, you can take advantage of CAS’ own tooling [backed by Apache Kafka](../services/Configuring-Service-Replication.html) that provides a 
distributed cache to broadcast service definition files across the cluster.

### Google Authenticator Multiple Devices

<div class="alert alert-warning">
  <strong>WATCH OUT!</strong><br />This may be a breaking change. The underlying data models and repository implementations that manage device records for users are modified to handle a collection of devices per user. This does affect database or filesystem schemas and API calls where a collection is expected instead of a single result.
</div>

Google Authenticator for multifactor authentication is now allowed to accept and register multiple devices. Accounts or devices must be assigned a name on registration that is used for the device selection menu when multiple registration records are found. When validating Google Authenticator tokens via REST, the account identifier must be specified if the user account has more than one registered device. Furthermore, note that allowing multiple devices per user is controlled via CAS settings and is disabled by default to preserve behavioral compatibility with previous versions.

|   |  |
| ------------- | ------------- |
| ![image](https://user-images.githubusercontent.com/1205228/85271898-ad0fb700-b490-11ea-9f69-60ae4aa59bd2.png) | ![image](https://user-images.githubusercontent.com/1205228/85271811-8a7d9e00-b490-11ea-9d49-5689f7f539f2.png) |

### DynamoDb Storage for YubiKey Devices

[YubiKey Devices](../mfa/YubiKey-Authentication.html) can now be stored in DynamoDb instances.

### Swagger Integration

[Swagger Integration](../integration/Swagger-Integration.html) can is upgraded to use Swagger v2 via [SpringDoc](https://springdoc.org/).

## Other Stuff

- Attribute definitions mapped to an external Groovy script are corrected to handle caching in more resource-friendly ways.
- The management of service definitions is now delegating search operations to the service registry rather than filtering matches internally while also utilizing a caching layer to improve performance as much as possible.
- Generation of OAuth/OIDC `code` tokens is now properly audited. Additionally, the `who` flag for OAuth/OIDC functionality is restored back to the active principal id.
- The authentication strategy backed by [Apache Syncope](../installation/Syncope-Authentication.html) is enhanced to not require a dependency on Apache Syncope modules, allowing the integration to work with all Apache Syncope versions. Additional improvements are put in to ensure the configuration can comply with reload requests and the likes of `@RefreshScope`.
- The eligibility of passwordless accounts for multifactor & delegated authentication has switched to a `TriStateBoolean` type to allow for easier overrides and undefined states when examined against the global settings.
- When working with Git integrations, username and email attributes used for commit operations are now resolved via local, global and system git configuration before falling back onto default CAS-controlled values.
- Service management `findServiceBy()` operations are now delegated to the service registry directly with a modest cache layer in between to improve and preserve performance as much as possible.
- Test improvements to reduce the number of duplicate configuration classes required to bootstrap the runtime context.
- OpenID Connect ID tokens can now be correctly signed using the algorithm fetched from the keystore, and the `iss` field should properly reflect the configured issuer in CAS configuration.
- [Locust performance tests](../high_availability/Performance-Testing-Locust.html) are now upgraded to use locust `1.1`.
- Generation of id tokens or user-info payloads for OAuth or OpenID Connect are now hardenized to prevent the `none` algorithm if undefined in discovery settings.
 
## Library Upgrades

- ErrorProne Compiler
- UnboundID LDAP SDK
- Spring Boot
- Spring Cloud
- Spring Data
- Spring Boot Admin
- Nimbus
- Swagger
- Swagger
- Amazon SDK
- Apache Tomcat
- Pac4j
- Twillio
- ActiveMQ
- BouncyCastle
- Swagger
- DropWizard
- Apache Curator
- Locust
- OpenSAML
- Oshi
- Couchbase Driver
- MongoDb Driver
- Nimbus OIDC
