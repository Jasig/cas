---
layout: default
title: CAS - Release Notes
category: Planning
---

# RC6 Release Notes

We strongly recommend that you take advantage of the release candidates as they come out. Waiting for a `GA` release is only going to set 
you up for unpleasant surprises. A `GA` is [a tag and nothing more](https://apereo.github.io/2017/03/08/the-myth-of-ga-rel/). Note that CAS 
releases are *strictly* time-based releases; they are not scheduled or based on specific benchmarks, 
statistics or completion of features. To gain 
confidence in a particular release, it is strongly recommended that you start early by experimenting with 
release candidates and/or follow-up snapshots.

## Apereo Membership

If you benefit from Apereo CAS as free and open-source software, we 
invite you to [join the Apereo Foundation](https://www.apereo.org/content/apereo-membership) 
and financially support the project at a capacity that best suits your deployment. Note that all development activity 
is performed *almost exclusively* on a voluntary basis with no expectations, commitments 
or strings attached. Having the financial means to better 
sustain engineering activities will allow the developer community to allocate *dedicated and committed* time for long-term support, 
maintenance and release planning, especially when it comes to addressing critical and security issues in a timely manner. Funding will 
ensure support for the software you rely on and you gain an advantage and say in the way Apereo, and the CAS project at that, runs 
and operates. If you consider your CAS deployment to be a critical part of the identity and access 
management ecosystem, this is a viable option to consider.

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
cas.version=6.4.0-RC6
```

Alternatively and for new deployments, [CAS Initializr](../installation/WAR-Overlay-Initializr.html) has 
been updated and can also be used
to generate an overlay project template for this release.

<div class="alert alert-info">
  <strong>System Requirements</strong><br/>There are no changes to the minimum system/platform requirements for this release.
</div>

## New & Noteworthy
 
## SAML2 Identity Provider w/ Delegation

[SAML2 authentication requests](../authentication/Configuring-SAML2-Authentication.html) can now be translated and adapted, to 
contribute to the properties of the authentication request sent off to a delegated SAML2 identity provider. For example, a 
requested SAML2 authentication context class can now be passed to an external SAML2 identity provider using
[delegated authentication](../integration/Delegate-Authentication-SAML.html).

## Other Stuff
       
- Service registry lookup enforcements to ensure located service definition types can be supported by the enabled protocols.
- Evaluation of [authentication policies](../authentication/Configuring-Authentication-Policy.html) is now 
  able to consider the entire authentication history.
- Person Directory [principal resolution](../authentication/Configuring-Authentication-PrincipalResolution.html) now 
  can receive the credential type and id as query attributes.
- JDBC attribute repositories are able to specify query attributes for advanced `WHERE` clauses in query builders.
- Execution order of [authentication throttlers](../authentication/Configuring-Authentication-Throttling.html) for 
  OAuth and OpenID Connect protocols is now restored and corrected.
- Scheduled jobs can now be activated conditionally using a regular expression matched against the running CAS node hostname.

## Library Upgrades

- Apache Tomcat
- Twilio
- DropWizard
- PostgreSQL Driver
- Hazelcast Kubernetes
- Azure DocumentDb
- Amazon SDK
- Lettuce
- Spring Session
