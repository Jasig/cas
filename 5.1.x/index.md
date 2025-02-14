---
layout: default
title: CAS - Home
---

# Enterprise Single Sign-On

* Spring Webflow/Spring Boot [Java server component](planning/Architecture.html).
* [Pluggable authentication support](Configuring-Authentication-Components.html) ([LDAP](installation/LDAP-Authentication.html), 
[Database](installation/Database-Authentication.html), [X.509](installation/X509-Authentication.html), [SPNEGO](installation/SPNEGO-Authentication.html), 
[JAAS](installation/JAAS-Authentication.html), [JWT](installation/JWT-Authentication.html), 
[RADIUS](installation/RADIUS-Authentication.html), [MongoDb](installation/MongoDb-Authentication.html), etc)
* Support for multiple protocols ([CAS](protocol/CAS-Protocol.html), [SAML](protocol/SAML-Protocol.html), [WS-Federation](protocol/WS-Federation-Protocol.html),
[OAuth2](protocol/OAuth-Protocol.html), [OpenID](protocol/OpenID-Protocol.html), [OpenID Connect](protocol/OIDC-Protocol.html))
* Support for [multifactor authentication](installation/Configuring-Multifactor-Authentication.html) via a variety of 
providers ([Duo Security](installation/DuoSecurity-Authentication.html), [FIDO U2F](installation/FIDO-U2F-Authentication.html), 
[YubiKey](installation/YubiKey-Authentication.html), [Google Authenticator](installation/GoogleAuthenticator-Authentication.html), etc)
* Support for [delegated authentication](integration/Delegate-Authentication.html) to external providers such as [ADFS](integration/ADFS-Integration.html), Facebook, Twitter, SAML2 IdPs, etc.
* [Monitor and track](installation/Monitoring-Statistics.html) application behavior, statistics and logs in real time.
* Manage and register [client applications and services](installation/Service-Management.html) with specific authentication policies.
* [Cross-platform client support](integration/CAS-Clients.html) (Java, .Net, PHP, Perl, Apache, etc).
* Integrations with [InCommon, Box, Office365, ServiceNow, Salesforce, Workday, WebAdvisor](integration/Configuring-SAML-SP-Integrations.html), Drupal, Blackboard, Moodle, [Google Apps](integration/Google-Apps-Integration.html), etc.

## Contribute

To learn how to contribute to the project, [please see this guide](/cas/developer/Contributor-Guidelines.html).

## Getting Started

We recommend reading the following documentation in order to plan and execute a CAS deployment.

* [Architecture](planning/Architecture.html)
* [Getting Started](planning/Getting-Started.html)
* [Installation Requirements](planning/Installation-Requirements.html)
* [Overlay Installation](installation/Maven-Overlay-Installation.html)
* [Authentication](installation/Configuring-Authentication-Components.html)
* [Application Registration](installation/Service-Management.html)
* [Attribute Release](integration/Attribute-Release.html)

## Demos

The following demos are provided by the Apereo CAS project:

| Topic                                                                                      | Source Branch            | Location
|-------------------------------------------------------------------------------------------|--------------------------|---------------------------------------------------------------------
| [CAS Overlay Project Initializr](installation/Maven-Overlay-Installation.html)            | `heroku-casinitializr`   | [Link](https://casinitializr.herokuapp.com)
| [CAS Web Application Server](index.html)                                                  | `heroku-caswebapp`       | [Link](https://casserver.herokuapp.com/cas)
| [CAS Services Management Server](installation/Installing-ServicesMgmt-Webapp.html)        | `heroku-mgmtwebapp`      | [Link](https://casservermgmt.herokuapp.com/cas-management) 
| [CAS Boot Administration Server](installation/Configuring-Monitoring-Administration.html) | `heroku-bootadminserver` | [Link](https://casbootadminserver.herokuapp.com/)
| [CAS Zipkin Server](installation/Monitoring-Statistics.html#distributed-tracing)          | `heroku-zipkinserver`    | [Link](https://caszipkinserver.herokuapp.com/)
| [CAS Service Discovery Server](installation/Service-Discovery-Guide.html)                 | `heroku-discoveryserver` | [Link](https://caseureka.herokuapp.com/)
| [CAS Configuration Server](installation/Configuration-Server-Management.html)             | `heroku-casconfigserver` | [Link](https://casconfigserver.herokuapp.com/casconfigserver)

Credentials used for the above demos, where needed, are: `casuser` / `Mellon`.

It is important to note that these are public demo sites, used by the project for basic showcases and integration tests. They are **NOT** set up for internal demos as they may go up and down as the project needs without notice. 

If you have a need for a demo instance with a modified UI, that would be one you [set up for your deployment](installation/Maven-Overlay-Installation.html). 

## Development

CAS development is powered by: <br/>

<a href="http://www.jetbrains.com/idea/" target="_blank"><img src="../images/intellijidea.gif" valign="middle" style="vertical-align:middle"></a>
