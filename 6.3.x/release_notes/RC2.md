---
layout: default
title: CAS - Release Notes
category: Planning
---

# RC2 Release Notes

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
cas.version=6.3.0-RC2
```

<div class="alert alert-info">
  <strong>System Requirements</strong><br/>There are no changes to the minimum system/platform requirements for this release.
</div>

## New & Noteworthy

The following items are new improvements and enhancements presented in this release. 

### Amazon SDK v2

CAS is now using the Amazon SDK v2, which effectively upgrades and impacts the functionality for DynamoDb, SNS, SSM, 
S3 and all other features based on Amazon Web Services.

### YubiKey Device Management via REST

YubiKey devices used for [multifactor authentication](../mfa/YubiKey-Authentication.html) can now be externally managed via a REST API.

### Test Coverage via CodeCov

CAS test coverage across all modules in the codebase has now reached `84%` and continues to climb. Additional validation rules are also applied 
to fail all pull requests that fall below this threshold. This area will be closely monitored and improved
as progress is made with the goal of hopefully reaching at least `85%` before the final GA release. Of course, this will not be a blocker for the final release.

### YubiKey Multiple Devices

Multiple YubiKey devices can now be registered with CAS for [multifactor authentication](../mfa/YubiKey-Authentication.html). This ability can be controlled via CAS settings.

![image](https://user-images.githubusercontent.com/1205228/88883051-8b9caa80-d248-11ea-9ad5-487c6071fbc5.png)

![image](https://user-images.githubusercontent.com/1205228/88883117-bf77d000-d248-11ea-98c9-e88088fdd975.png)

<div class="alert alert-warning">
  <strong>WATCH OUT!</strong><br />This may be a breaking change. The underlying data models and repository implementations that manage device records for users are modified to handle a collection of devices per user. This does affect database or filesystem schemas and API calls where a collection is expected instead of a single result.
</div>

### Amazon S3 Service Registry

CAS registered service definitions can now be natively stored in [Amazon S3 buckets](../services/AmazonS3-Service-Management.html).

### Dynamic JPA Service Management

CAS registered service definitions that are managed by the [JPA Service Registry](../services/JPA-Service-Management.html)
are now put through a more fine-tuned dynamic registration process at runtime. Previously, database schemas were created automatically
if appropriate entity classes, representing each client application type, were found on the classpath. In this release, entity classes 
are required to be explicitly registered with the CAS service management facility and each appropriate auto-configuration module should
correctly nominate the relevant entities when declared in the [WAR Overlay](../installation/WAR-Overlay-Installation.html). 

<div class="alert alert-info">
  <strong>Remember</strong><br />If you are not using a relational database to manage application definitions,
  there is nothing for you to do here. Carry on!
</div>

The main motivation for this change is to avoid conflicts between the CAS web application server and 
the CAS management application, specially when both are configured to use JPA to manage service definitions. The management
application requires compile-time access to the CAS service definition APIs to handle data mappings, yet doing so interferes
with the JPA Service Registry expectations of database schemas and tables that should be there, given the classpath
automatic discovery process. For example, a CAS server deployment could declare support for CAS and SAML application types
allowing it to create appropriate schemas automatically based on those two definition types. When the CAS management application 
is next deployed, it might complain about missing schemas for OAUTH and OIDC applications since the type 
is found on the classpath but the definition is not actually used/supported by the deployment.

Using this new strategy, database tables and schemas are not automatically expected or created by the CAS management
 application, allowing the codebase to use entity classes on the classpath for data mapping operations. 
 To handle the registration, the management application is given the ability to register entity classes for each 
 application type with the CAS JPA Service Registry using a simple property, allowing the operator to explicitly 
 declare the set of services supported by the deployment.

### SAML2 Logout Requests & Responses

SAML2 single logout handling handling, when CAS is running as a [SAML2 identity provider](../installation/Configuring-SAML2-Authentication.html), is now 
able to produce a logout response for the service provider once the single logout sequence has completed. Additionally, logout requests
are no longer sent to the original service provider which initiated the single logout flow. 

### OpenID Connect Logout

Logout requests handled by OpenID Connect authentication flows can now properly redirect to a requested URL via `post_logout_redirect_uri`
provided `id_token_hint` is specified, and the logout URL is authorized for the relying party.

### Okta SDK v2

CAS is now using the Okta SDK v2 mainly used to handle the integration between CAS and Okta for authentication and attribute resolution.

### Attribute Consent Activation

Activation rules for [Attribute Consent](../integration/Attribute-Release-Consent.html) are re-designed to allow per-application 
overrides of the global policy activation rules. Additional documentation updates are now present to demonstrate how multiple 
attribute consent policies may be chained together.

Furthermore, activation rules can also be outsourced to external Groovy scripts. 

<div class="alert alert-warning">
  <strong>WATCH OUT!</strong><br />This may be a breaking change since the data model for the <code>DefaultRegisteredServiceConsentPolicy</code> 
  has remove the <code>enabled</code> field, replacing it with <code>status</code>. Review the documentation to adjust for proper syntax.
</div>

### Ticket-Granting Ticket Expiration Policy Per Service

The ticket-granting ticket expiration policy can be overridden on a per-service using 
the expiration policy [assigned to the service definition](../ticketing/Configuring-Ticket-Expiration-Policy.html).

### Service Matching Strategy

Service identifiers defined for applications in the CAS service registry have always been defined as patterns. This release exposes 
a few [additional options](../services/Configuring-Service-Matching-Strategy.html) while also allowing the matching strategy to be externalized to custom components. 

### SSO Participation Policy

Adjustments are put in place to correctly locate and reconstruct the authentication transaction, in the 
event that [single sign-on participation](../services/Configuring-Service-SSO-Policy.html) is disabled for a 
particular service, specially in Open ID Connect authentication flows. Furthermore, the authentication policy criteria
for a service definition is now defaulted to match the global and default authentication policies for the CAS.  

### Wildcarded Service Definitions

Consider a SAML service provider definition registered with CAS that authorizes 
all service providers found in an XML metadata aggregate file:

```json
{
  "@class": "org.apereo.cas.support.saml.services.SamlRegisteredService",
  "serviceId": ".+",
  "name": "SAML",
  "id": 2,
  "evaluationOrder": 10,
  "metadataLocation": "https://example.org/md-aggregate.xml"
}
```

Then, suppose the same CAS deployment wishes to authorize all CAS-enabled web applications:

```json
{
  "@class": "org.apereo.cas.services.RegexRegisteredService",
  "serviceId": ".+",
  "name": "ALL",
  "id": 1,
  "evaluationOrder": 9
}
```

The issue here is that depending on how the `evaluationOrder` is set up, the wrong service definition might get matched and processed
for SAML or CAS protocol authentication requests. The root cause is that the CAS matching engine attempts to locate service definitions
by their `serviceId` (which might correlate to an entity id or a redirect URI, etc) without taking into account the 
authentication protocol itself. In this release, a few additional improvements are put in place to allow grouping of 
application definitions by both type and evaluation order, and the matching engine is enhanced to process such groups while considering
both the group's evaluation priority as well as each individual service's evaluation order.

<div class="alert alert-info">
  <strong>Note</strong><br />The fixes presented in this release continue to be in progress to refine additional use cases. This area will likely be
  revisited in follow-up releases to make sure wildcarded service definitions across all protocols can work together correctly.
</div>

## Other Stuff

- Adjustments to SAML2 metadata resolution cache to ensure enough capacity for resolved metadata providers. 
- Minor fixes to SQL query execution when pushing CAS audit logs to Oracle databases.
- The expiration of access tokens is now correctly communicated back to OAuth relying parties, specially if the access token expiration policy is defined per application.
- The handling of authentication requests, set to force CAS to challenge the user credentials, is reviewed and adjusted to ensure such requests can properly honor multifactor authentication flows for qualifying requests per configured triggers. 
- The logout handling strategy is slightly broken apart to introduce a `LogoutRedirectionStrategy`, mainly responsible for handling follow-up redirects to authorized applications/endpoints as appropriate for each authentication protocol.
- Component registration with the Memcached serialization engine is now broken apart and delegated to appropriate modules owning said components.
- Signed SAML authentication requests that embed the signature in URLs are reviewed and adjusted to avoid creating long URLs exceeding browser limits.
- The naming strategy for JSON/YAML service definition files is relaxed to allow multiple words in the file name.
- Transformation of service definitions to JSON or YAML is adjusted to exclude fields with default values to produce leaner payloads.

## Library Upgrades

- Commons Lang
- Mockito
- DropWizard
- Spring
- Spring Boot
- Amazon SDK
- Spotbugs
- Gradle
- Okta
- Shiro
- Hazelcast AWS
- Hazelcast Kubernetes
- Hazelcast Azure
- EhCache
- Spring Boot Admin
- Ldaptive
- Inspektr
- Nexmo
- Twilio
- Bootstrap
- Spring Data
- Person Directory
- Azure DocumentDb
- Grouper Client
- InfluxDb
