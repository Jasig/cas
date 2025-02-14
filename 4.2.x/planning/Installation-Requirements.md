---
layout: default
title: CAS - Installation Requirements
---

# Installation Requirements

Requirements at a glance:

1. [Java](http://www.java.com) >=1.7
2. [Servlet container](http://tomcat.apache.org/) supporting servlet specification >=3.0
3. [Apache Maven](http://maven.apache.org/) >=3.3
4. Familiarity with the [Spring Framework](http://www.springsource.org/)
5. Internet connectivity

Depending on choice of configuration components, there may be additional requirements such as LDAP directory,
database, and caching infrastructure. In most cases, however, requirements should be self evident to deployers who
choose components with clear hardware and software dependencies. In any case where additional requirements are
not obvious, the discussion of component configuration should mention system, software, hardware, and other
requirements.


## Servlet Containers
There is no officially supported servlet container for CAS, but [Apache Tomcat](http://tomcat.apache.org/) is the most
commonly used. Support for a particular servlet container depends on the expertise of community members, but the
following are known to work well and should receive first-class support on the
[Community Discussion Mailing List](../Mailing-Lists.html):

* [JBoss](http://www.jboss.org/)
* [Jetty](http://www.eclipse.org/jetty/)
* [GlassFish](http://glassfish.java.net/)
* [WebSphere](http://www.ibm.com/software/websphere/)


## Apache Maven
CAS uses Maven for building and creating a deployable package for installation into a Java servlet container. Maven is
also strongly recommended for configuration management required for the CAS installation process. CAS is fundamentally
a complex software product that becomes embedded and tighly integrated into the software environment of an institution.
For this reason it tends to require customization well beyond turnkey solutions, and the integration requirements tend
to change over time. A source-based installation process like
[Maven WAR overlay](../installation/Maven-Overlay-Installation.html) provides a straightforward and flexible solution
to complex and dynamic requirements. While it admittedly requires a high up-front cost in learning, it reaps numerous
benefits in the long run


## Spring Framework
CAS uses the many aspects of the Spring Framework; most notably,
[Spring MVC](http://static.springsource.org/spring/docs/3.2.x/spring-framework-reference/html/mvc.html) and
[Spring Webflow](http://www.springsource.org/spring-web-flow). Spring provides a complete and extensible framework for
the core CAS codebase as well as for deployers; it's straightforward to customize or extend CAS behavior by hooking
CAS and Spring API extension points. General knowledge of Spring is beneficial to understanding the interplay among
some framework compoents, but it's not strictly required. The XML-based configuration used to configure CAS and Spring
components, however, is a core concern for installation, customization, and extension. Competence with XML generally
and the
[Spring IOC Container](http://static.springsource.org/spring/docs/3.2.x/spring-framework-reference/html/beans.html)
in particular are prerequisites to CAS installation.


## Internet Connectivity
Internet connectivity is generally required for the build phase of any Maven-based project, including the recommended
Maven WAR overlays used to install CAS. Maven resolves dependencies by searching online repositories containing
artifacts (jar files in most cases) that are downloaded and installed locally. While it is possible to override this
behavior by alterning Maven configuration settings, it is considered advanced usage and not supported.

A common solution to overcoming lack of Internet connectivity on a CAS server is to build CAS on a dedicated build
host with internet connectivity. The `cas.war` file produced by the build is subsequently copied to the CAS server
for deployment.
