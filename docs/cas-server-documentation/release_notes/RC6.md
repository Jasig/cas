---
layout: default
title: CAS - Release Notes
category: Planning
---

# 7.0.0-RC6 Release Notes

We strongly recommend that you take advantage of the release candidates as they come out. Waiting for a `GA` release is only going to set
you up for unpleasant surprises. A `GA` is [a tag and nothing more](https://apereo.github.io/2017/03/08/the-myth-of-ga-rel/). Note
that CAS releases are *strictly* time-based releases; they are not scheduled or based on specific benchmarks,
statistics or completion of features. To gain confidence in a particular
release, it is strongly recommended that you start early by experimenting with release candidates and/or follow-up snapshots.

## Apereo Membership

If you benefit from Apereo CAS as free and open-source software, we invite you
to [join the Apereo Foundation](https://www.apereo.org/content/apereo-membership)
and financially support the project at a capacity that best suits your deployment. Note that all development activity is performed
*almost exclusively* on a voluntary basis with no expectations, commitments or strings attached. Having the financial means to better
sustain engineering activities will allow the developer community to allocate *dedicated and committed* time for long-term support,
maintenance and release planning, especially when it comes to addressing critical and security issues in a timely manner.

## Get Involved

- Start your CAS deployment today. Try out features and [share feedback](/cas/Mailing-Lists.html).
- Better yet, [contribute patches](/cas/developer/Contributor-Guidelines.html).
- Suggest and apply documentation improvements.

## Resources

- [Release Schedule](https://github.com/apereo/cas/milestones)
- [Release Policy](/cas/developer/Release-Policy.html)

## System Requirements

The JDK baseline requirement for this CAS release is and **MUST** be JDK `17`. All compatible distributions
such as Amazon Corretto, Zulu, Eclipse Temurin, etc should work and are implicitly supported.

## New & Noteworthy

The following items are new improvements and enhancements presented in this release.
  
### Google Cloud Pub/Sub Ticket Registry

A new ticket registry implementation backed by [Google Cloud's PubSub](../ticketing/GCP-PubSub-Ticket-Registry.html) is now available.
  
### OpenID Connect Dynamic Registration

Supported grant types and response types are recognized during [OpenID Connect Dynamic Registration](../authentication/OIDC-Authentication-Dynamic-Registration.html). 
Furthermore, sensible defaults would be used if grant types or response types are not explicitly requested.

### Feature Removals

Modules, features and plugins that support functionality for Apache CouchDb or Couchbase, previously deprecated, are now removed.
If you are currently using any of these plugins or features, we recommend that you consider a 
better alternative or prepare to adopt and maintain the feature on your own.
    
### Spring Boot

CAS has switched and upgraded to Spring Boot `3.1.x`, presently in milestone/release-candidate mode, and one that is 
anticipated to be released around mid May 2023. It is unlikely that CAS `7.0.x` would be released prior to that date, and 
we intend to take advantage of this time window to run integration tests against the next Spring Boot release. 

### Google Cloud Firestore Ticket Registry

A new ticket registry implementation backed by [Google Cloud's Firestore](../ticketing/GCP-Firestore-Ticket-Registry.html) is now available.

## Other Stuff
   
- Ticket registry operations are now *observed* using [Micrometer Observations](https://micrometer.io) and then reported as metrics.
- JSON and YAML service registries are able to auto-organize and store service definition files in dedicated directories identified by the service type.
- Support for additional settings such as `cluster`, `family`, etc to assist with Hazelcast discovery when CAS is deployed in AWS.

## Library Upgrades
       
- Spring  
- Spring Integration
- Netty
- Logback 
- Ldaptive
- Twillio
- jQuery
- Amazon SDK
- MariaDb
- Hazelcast
- Joda-Time
- Spring Boot
- Azure CosmosDb
- Grouper Client
- Spring Cloud
- Swagger
- Spring BootAdmin
- Slf4j
- PostgreSQL
- Gradle
- Thymeleaf Dialect
