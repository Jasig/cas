---
layout: default
title: CAS - Overlay Installation
---

# WAR Overlay Installation

CAS installation is a fundamentally source-oriented process, and we recommend a
[WAR overlay](http://maven.apache.org/plugins/maven-war-plugin/overlays.html) project to organize
customizations such as component configuration and UI design.
The output of a WAR overlay build is a `cas.war` file that can be deployed on a Java servlet container like
[Tomcat](http://tomcat.apache.org/whichversion.html).

WAR overlay projects are provided for reference and study.

## Gradle

- [CAS Gradle Overlay](https://github.com/apereo/cas-gradle-overlay-template/tree/4.2)

## Maven

- [CAS Maven Overlay](https://github.com/apereo/cas-overlay-template/tree/4.2)


CAS uses Spring Webflow to drive the login process in a modular and configurable fashion; the `login-webflow.xml`
file contains a straightforward description of states and transitions in the flow. Customizing this file is probably
the most common configuration concern beyond component configuration in the Spring XML configuration files. See the
Spring Webflow Customization Guide for a thorough description of the various CAS flows and discussion of common
configuration points.

## Spring Configuration

CAS server depends heavily on the Spring framework. There are exact and specific XML configuration files under `spring-configuration` directory that control various properties of CAS as well as `cas-servlet.xml` and `deployerConfigContext.xml` the latter of which is mostly expected by CAS adopters to be included in the overlay for environment-specific CAS settings.

Spring beans in the XML configuration files can be overwritten to change behavior if need be via the Maven overlay process. There are two approaches to this:

1. The XML file can be obtained from source for the CAS version and placed at the same exact path by the same exact name in the Maven overlay build. If configured correctly, the build will use the locally-provided XML file rather than the default.
2. CAS server is able to load patterns of XML configuration files to overwrite what is provided by default. These configuration files that intend to overrule CAS default behavior can be placed at `/WEB-INF/` and must be named by the following pattern: `cas-servlet-*.xml`. Beans placed in this file will overwrite others.

## Custom and Third-Party Source

It is common to customize or extend the functionality of CAS by developing Java components that implement CAS APIs or
to include third-party source by Maven dependency references. Including third-party source is trivial; simply include
the relevant dependency in the overlay `pom.xml` file. In order to include custom Java source, it should be included
under a `src/java/main` directory in the overlay project source tree.

    ├── src
    │   ├── main
    │   │   ├── java
    │   │   │   └── edu
    │   │   │       └── vt
    │   │   │           └── middleware
    │   │   │               └── cas
    │   │   │                   ├── audit
    │   │   │                   │   ├── CompactSlf4jAuditTrailManager.java
    │   │   │                   │   ├── CredentialsResourceResolver.java
    │   │   │                   │   ├── ServiceResourceResolver.java
    │   │   │                   │   └── TicketOrCredentialPrincipalResolver.java
    │   │   │                   ├── authentication
    │   │   │                   │   └── principal
    │   │   │                   │       ├── AbstractCredentialsToPrincipalResolver.java
    │   │   │                   │       ├── PDCCredentialsToPrincipalResolver.java
    │   │   │                   │       └── UsernamePasswordCredentialsToPrincipalResolver.java
    │   │   │                   ├── services
    │   │   │                   │   └── JsonServiceRegistryDao.java
    │   │   │                   ├── util
    │   │   │                   │   └── X509Helper.java
    │   │   │                   └── web
    │   │   │                       ├── HelpController.java
    │   │   │                       ├── RegisteredServiceController.java
    │   │   │                       ├── StatsController.java
    │   │   │                       ├── WarnController.java
    │   │   │                       ├── flow
    │   │   │                       │   ├── AbstractForgottenCredentialAction.java
    │   │   │                       │   ├── AbstractLdapQueryAction.java
    │   │   │                       │   ├── AffiliationHandlerAction.java
    │   │   │                       │   ├── CheckAccountRecoveryMaintenanceAction.java
    │   │   │                       │   ├── CheckPasswordExpirationAction.java
    │   │   │                       │   ├── ForgottenCredentialTypeAction.java
    │   │   │                       │   ├── LookupRegisteredServiceAction.java
    │   │   │                       │   ├── NoSuchFlowHandler.java
    │   │   │                       │   ├── User.java
    │   │   │                       │   ├── UserLookupAction.java
    │   │   │                       │   └── WarnCookieHandlerAction.java
    │   │   │                       └── util
    │   │   │                           ├── ProtocolParameterAuthority.java
    │   │   │                           ├── UriEncoder.java
    │   │   │                           └── UrlBuilder.java


Also, note that for any custom Java component to compile and be included in the final `cas.war` file, the `pom.xml` in the Maven overlay must include a reference to the Maven Java compiler so classes can compile. 

Here is a *sample* Maven build configuration:


```xml

...

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-war-plugin</artifactId>
            <version>2.3</version>
            <configuration>
                <warName>cas</warName>
                <overlays>
                    <overlay>
                        <groupId>org.jasig.cas</groupId>
                        <artifactId>cas-server-webapp</artifactId>
                        <excludes>
                <exclude>WEB-INF/cas.properties</exclude>
                            <exclude>WEB-INF/classes/log4j.xml</exclude>
                            <exclude>...</exclude>
                        </excludes>
                    </overlay>
                </overlays>
            </configuration>
        </plugin>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.1</version>
            <configuration>
                <source>${java.source.version}</source>
                <target>${java.target.version}</target>
            </configuration>
        </plugin>

    </plugins>
    <finalName>cas</finalName>
</build>

...

```


*(1) The filesystem hierarchy visualization is generated by the `tree` program.*
