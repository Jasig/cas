---
layout: default
title: CAS - Maven Overlay Installation
---

# Maven Overlay Installation
CAS installation is a fundamentally source-oriented process, and we recommend a
[Maven WAR overlay](http://maven.apache.org/plugins/maven-war-plugin/overlays.html) project to organize
customizations such as component configuration and UI design.
The output of a Maven WAR overlay build is a `cas.war` file that can be deployed on a Java servlet container like
[Tomcat](http://tomcat.apache.org/whichversion.html).

A simple Maven WAR overlay project is provided for reference and study:
[https://github.com/Jasig/cas-overlay-template](https://github.com/Jasig/cas-overlay-template)

The following list of CAS components are those most often customized by deployers:

1. Authentication handlers (i.e. `LdapAuthenticationHandler`)
2. Storage backend (i.e. `MemcachedTicketRegistry`)
3. View layer files (JSP/CSS/Javascript)

The first two are controlled by modifying Spring XML configuration files under
`src/main/webapp/WEB-INF/spring-configuration`, the latter by modifying JSP and CSS files under
`src/main/webapp/WEB-INF/view/jsp/default` in the Maven WAR overlay project. Every aspect of CAS can be controlled by
adding, removing, or modifying files in the overlay; it's also possible and indeed common to customize the behavior of
CAS by adding third-party components that implement CAS APIs as Java source files or dependency references.

Once an overlay project has been created, the `cas.war` file must be built and subsequently deployed into a Java
servlet container like Tomcat. The following set of commands, issued from the Maven WAR overlay project root
directory, provides a sketch of how to accomplish this on a Unix platform.

{% highlight bash %}
$CATALINA_HOME/bin/shutdown.sh
mvn clean package
rm -rf $CATALINA_HOME/logs/*
rm -f $CATALINA_HOME/webapps/cas.war
rm -rf $CATALINA_HOME/webapps/cas
rm -rf $CATALINA_HOME/work/*
cp -v target/cas.war $CATALINA_HOME/webapps
$CATALINA_HOME/bin/startup.sh
{% endhighlight %}


## Configuration Files
CAS configuration is controlled primarily by Spring XML context configuration files. At a minimum, every deployer
must customize `deployerConfigContext.xml` and `cas.properties` by including them in the Maven WAR overlay,
but there are other optional configuration files that may be included in the overlay for further customization
or to provide additional features. The following exploded file system hierarchy shows how files should be organized
in the overlay (1):

{% highlight bash %}

\---src
    +---main
    |   +---resources
    |   |       apereo.properties
    |   |       cas-theme-default.properties
    |   |       log4j.xml
    |   |       messages.properties
    |   |       protocol_views.properties
    |   |       saml_views.properties
    |   |       truststore.jks
    |   |       
    |   \---webapp
    |       |   favicon.ico
    |       |   index.jsp
    |       |   
    |       +---css
    |       |       cas.css
    |       |       
    |       +---images
    |       |       cas-logo.png
    |       |       confirm.gif
    |       |       error.gif
    |       |       error.png
    |       |       green.gif
    |       |       info.gif
    |       |       info.png
    |       |       ja-sig-logo.gif
    |       |       jasig-logo-small.png
    |       |       jasig-logo.png
    |       |       key-point_bl.gif
    |       |       key-point_br.gif
    |       |       key-point_tl.gif
    |       |       key-point_tr.gif
    |       |       question.png
    |       |       red.gif
    |       |       success.png
    |       |       warning.png
    |       |       
    |       +---js
    |       |       cas.js
    |       |       
    |       +---themes
    |       |   \---apereo
    |       |       +---css
    |       |       |       cas.css
    |       |       |       
    |       |       +---images
    |       |       |       apereo-logo.png
    |       |       |       bg-tile.gif
    |       |       |       
    |       |       \---js
    |       |               cas.js
    |       |               
    |       \---WEB-INF
    |           |   cas-servlet.xml
    |           |   cas.properties
    |           |   deployerConfigContext.xml
    |           |   restlet-servlet.xml
    |           |   web.xml
    |           |   
    |           +---spring-configuration
    |           |       applicationContext.xml
    |           |       argumentExtractorsConfiguration.xml
    |           |       auditTrailContext.xml
    |           |       filters.xml
    |           |       log4jConfiguration.xml
    |           |       propertyFileConfigurer.xml
    |           |       README.txt
    |           |       securityContext.xml
    |           |       ticketExpirationPolicies.xml
    |           |       ticketGrantingTicketCookieGenerator.xml
    |           |       ticketRegistry.xml
    |           |       uniqueIdGenerators.xml
    |           |       warnCookieGenerator.xml
    |           |       
    |           +---unused-spring-configuration
    |           |       clearpass-configuration.xml
    |           |       lppe-configuration.xml
    |           |       mbeans.xml
    |           |       
    |           +---view
    |           |   \---jsp
    |           |       |   authorizationFailure.jsp
    |           |       |   errors.jsp
    |           |       |   
    |           |       +---default
    |           |       |   \---ui
    |           |       |       |   casAccountDisabledView.jsp
    |           |       |       |   casAccountLockedView.jsp
    |           |       |       |   casBadHoursView.jsp
    |           |       |       |   casBadWorkstationView.jsp
    |           |       |       |   casConfirmView.jsp
    |           |       |       |   casExpiredPassView.jsp
    |           |       |       |   casGenericSuccess.jsp
    |           |       |       |   casLoginMessageView.jsp
    |           |       |       |   casLoginView.jsp
    |           |       |       |   casLogoutView.jsp
    |           |       |       |   casMustChangePassView.jsp
    |           |       |       |   serviceErrorSsoView.jsp
    |           |       |       |   serviceErrorView.jsp
    |           |       |       |   
    |           |       |       \---includes
    |           |       |               bottom.jsp
    |           |       |               top.jsp
    |           |       |               
    |           |       +---monitoring
    |           |       |       viewStatistics.jsp
    |           |       |       
    |           |       \---protocol
    |           |           |   casPostResponseView.jsp
    |           |           |   
    |           |           +---2.0
    |           |           |       casProxyFailureView.jsp
    |           |           |       casProxySuccessView.jsp
    |           |           |       casServiceValidationFailure.jsp
    |           |           |       casServiceValidationSuccess.jsp
    |           |           |       
    |           |           +---3.0
    |           |           |       casServiceValidationFailure.jsp
    |           |           |       casServiceValidationSuccess.jsp
    |           |           |       
    |           |           +---clearPass
    |           |           |       clearPassFailure.jsp
    |           |           |       clearPassSuccess.jsp
    |           |           |       
    |           |           +---oauth
    |           |           |       confirm.jsp
    |           |           |       
    |           |           \---openid
    |           |                   casOpenIdAssociationFailureView.jsp
    |           |                   casOpenIdAssociationSuccessView.jsp
    |           |                   casOpenIdServiceFailureView.jsp
    |           |                   casOpenIdServiceSuccessView.jsp
    |           |                   user.jsp
    |           |                   
    |           \---webflow
    |               +---login
    |               |       login-webflow.xml
    |               |       
    |               \---logout
    |                       logout-webflow.xml
    |                                               
{% endhighlight %}

The approach to Spring configuration is to group related components into a single configuration file, which allows
deployers to include the handful of files containing components (typically authentication and ticketing) required
for their environment. The files are intended to be self-identifying with respect to the kinds of components they
contain, with the exception of `applicationContext.xml` and `cas-servlet.xml`. For example, `auditTrailContext.xml`
contains components related to the CAS audit trail where events are emitted for successful and failed authentication attempts, among other kinds of auditable events.

It is common practice to exclude `cas.properties` from the overlay and place it at a well-known filesystem location
outside the WAR deployable. In that case, `propertyFileConfigurer.xml` must be configured to point to the filesystem
location of `cas.properties`. Generally, the Spring XML configuration files under `spring-configuration` are the most
common configuration files, beyond `deployerConfigContext.xml`, to be included in an overlay. The supplementary Spring
configuration files are organized into logically separate configuration concerns that are clearly indicated by the file
name.

CAS uses Spring Webflow to drive the login process in a modular and configurable fashion; the `login-webflow.xml`
file contains a straightforward description of states and transitions in the flow. Customizing this file is probably
the most common configuration concern beyond component configuration in the Spring XML configuration files. See the
Spring Webflow Customization Guide for a thorough description of the various CAS flows and discussion of common
configuration points.

## Spring Configuration
CAS server depends heavily on the Spring framework. There are exact and specific XML configuration files under `spring-configuration` directory that control various properties of CAS as well as `cas-servlet.xml` and `deployerConfigContext.xml` the latter of which is mostly expected by CAS adopters to be included in the overlay for environment-specific CAS settings.

Spring beans in the XML configuration files can be overwritten to change behavior if need be via the Maven overlay process. There are two approaches to this:

1. The XML file can be obtained from source for the CAS version and placed at the same exact path by the same exact name in the Maven overlay build. If configured correctly, the build will use the locally-provided XML file rather than the default.
2. CAS server is able to load patterns of XML configuration files to overwrite what is provided by default. These configuration files that intend to overrule CAS default behavior can be placed at `/WEB-INF/` and must be named by the following pattern: `cas-servlet-*.xml`. Beans placed in this file will overwrite others. This configuration is recognized by the `DispatcherServlet` in the `web.xml` file:

{% highlight xml %}
...

<servlet-class>
    org.springframework.web.servlet.DispatcherServlet
</servlet-class>
<init-param>
    <param-name>contextConfigLocation</param-name>
    <param-value>/WEB-INF/cas-servlet.xml, /WEB-INF/cas-servlet-*.xml</param-value>
</init-param>

...
{% endhighlight %}

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


Also, note that for any custom Java component to compile and be included in the final `cas.war` file, the `pom.xml` in the Maven overlay must include a reference to the Maven Java compiler so classes can compiled. Here is a *sample* build configuration:


{% highlight xml %}

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

{% endhighlight %}


*(1) The filesystem hierarchy visualization is generated by the `tree` program.*
