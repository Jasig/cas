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

A simple Maven WAR overlay project is provided for reference:
[https://github.com/apereo/cas-overlay-template](https://github.com/apereo/cas-overlay-template)

The following list of CAS components are those most often customized by deployers:

1. Authentication handlers (i.e. `LdapAuthenticationHandler`)
2. Storage backend (i.e. `MemcachedTicketRegistry`)
3. View layer files (HTML/CSS/Javascript)
4. Logging (`log4j2.xml`)

Every aspect of CAS can be controlled by
adding, removing, or modifying files in the overlay; it's also possible and indeed common to customize the behavior of
CAS by adding third-party components that implement CAS APIs as Java source files or dependency references.

Once an overlay project has been created, the `cas.war` file must be built and subsequently deployed into a Java
servlet container like Tomcat. 

CAS uses Spring Webflow to drive the login process in a modular and configurable fashion; the `login-webflow.xml`
file contains a straightforward description of states and transitions in the flow. Customizing this file is probably
the most common configuration concern beyond component configuration in the Spring XML configuration files. 

## Spring Configuration

CAS server depends heavily on the Spring framework. Two modes of configuration are available. Note that both modes
can be used at the same time. 

### XML

There is a `deployerConfigContext.xml` which is mostly expected by CAS adopters to be 
included in the overlay for environment-specific CAS settings.

### Groovy

The CAS application context is able to load a `deployerConfigContext.groovy`. 
For advanced use cases, CAS beans can be dynamically defined via the Groovy programming language. 
As an example, here is an `exampleBean` defined inside a `applicationContext.groovy` file:

```groovy
beans {
    xmlns([context:'http://www.springframework.org/schema/context'])
    xmlns([lang:'http://www.springframework.org/schema/lang'])
    xmlns([util:'http://www.springframework.org/schema/util'])

    exampleBean(org.apereo.cas.example.ExampleBean) {
        beanProperty = propertyValue
    }
}
```

Additionally, dynamic reloadable Groovy beans can be defined in `deployerConfigContext.xml`. These definitions
are directly read from a `.groovy` script which is monitored for changes and reloaded automatically.
Here is a dynamic `messenger` bean defined whose definition is read from a `Messenger.groovy` file,
and is monitored for changes every 5 seconds. 

```
<lang:groovy id="messenger"
    refresh-check-delay="5000" 
    script-source="classpath:Messenger.groovy">
    <lang:property name="message" value="Hello, CAS!" />
</lang:groovy>
```

The contents of the `Messenger.groovy` must resolve to a valid Java class:

```groovy
class ExampleMessenger implements Messenger {
    String message = "Welcome"
    
    String getMessage() {
        this.message
    }
    void setMessage(String message) {
        this.message = message
    }
}
```

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


Also, note that for any custom Java component to compile and be included in the final `cas.war` file, the `pom.xml` 
in the Maven overlay must include a reference to the Maven Java compiler so classes can compile. Here is a *sample* build configuration:


```xml

...

<build>
    <plugins>
...
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.1</version>
            <configuration>
                <source>${java.source.version}</source>
                <target>${java.target.version}</target>
            </configuration>
        </plugin>
...
    </plugins>
    <finalName>cas</finalName>
</build>
...
```

## Dependency Management

Each release of CAS provides a curated list of dependencies it supports. In practice, you do not need to provide a version for any of 
these dependencies in your build configuration as the CAS distribution is managing that for you. When you upgrade CAS itself, 
these dependencies will be upgraded as well in a consistent way.

The curated list contains a refined list of third party libraries. The list is available as a standard Bills of Materials (BOM).

To configure your project to inherit from the BOM, simply set the parent:

```xml
<!-- Inherit defaults from Spring Boot -->
<parent>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-bom</artifactId>
    <version>${cas.version}</version>
</parent>
```

Not everyone likes inheriting from the BOM. 
You may have your own corporate standard parent that you need to use, 
or you may just prefer to explicitly declare all your Maven configuration.

If you don’t want to use the `cas-server-support-bom`, you can still 
keep the benefit of the dependency management (but not the plugin management) 
by using a `scope=import` dependency:

```xml
<dependencyManagement>
     <dependencies>
     
        <!-- Override a dependency by including it BEFORE the BOM -->
        <dependency>
            <groupId>org.group</groupId>
            <artifactId>artifact-name</artifactId>
            <version>X.Y.Z</version>
        </dependency>
             
        <dependency>
            <groupId>org.apereo.cas</groupId>
            <artifactId>cas-server-support-bom</artifactId>
            <version>${cas.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

*(1) The filesystem hierarchy visualization is generated by the `tree` program.*
