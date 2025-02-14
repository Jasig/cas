---
layout: default
title: CAS - Overlay Installation
---

# Servlet Container Configuration

A number of container options are available to deploy CAS. The [WAR Overlay](Maven-Overlay-Installation.html) guide
describes how to build and deploy CAS. 

## Embedded

Note that CAS itself ships with a number of embedded containers that allows the platform to be self-contained as much as possible. You **DO
NOT** need to, but can if you want to, configure and deploy to an externally configured container. 

<div class="alert alert-info"><strong>Do Less</strong><p>
Remember that most if not all aspects of the embedded container can be controlled via the CAS properties.
See <a href="Configuration-Properties.html#embedded-tomcat">this guide</a> for more info.</p></div>

To see the relevant list of CAS properties, 
please [review this guide](Configuration-Properties.html#embedded-container).

### Apache Tomcat

Note that by default, the embedded container attempts to enable the HTTP2 protocol.

```xml
<dependency>
     <groupId>org.apereo.cas</groupId>
     <artifactId>cas-server-webapp-tomcat</artifactId>
     <version>${cas.version}</version>
</dependency>
```

#### Logging

The embedded Apache Tomcat container is presently unable to display any log messages below `INFO` even if your CAS log configuration explicitly asks for `DEBUG` or `TRACE` level data. See [this bug report](https://github.com/spring-projects/spring-boot/issues/2923) to learn more.

While workarounds and fixes may become available in the future, for the time being, you may execute the following changes to get `DEBUG` level log data from the embedded Apache Tomcat. This is specially useful if you are troubleshooting the behavior of Tomcat's internal components such as valves, etc.

- Design a `logging.properties` file as such:

```properties
handlers = java.util.logging.ConsoleHandler
.level = ALL
java.util.logging.ConsoleHandler.level = DEBUG
java.util.logging.ConsoleHandler.formatter = java.util.logging.SimpleFormatter
```

- Design a`java.util.logging.config.file` setting as a system/environment variable or command-line argument whose value is set to the `logging.properties` path. Use the setting when you launch and deploy CAS.

For instance:

```bash
java -jar /path/to/cas.war -Djava.util.logging.config.file=/path/to/logging.properties
```

### Jetty

```xml
<dependency>
     <groupId>org.apereo.cas</groupId>
     <artifactId>cas-server-webapp-jetty</artifactId>
     <version>${cas.version}</version>
</dependency>
```

### Undertow

```xml
<dependency>
     <groupId>org.apereo.cas</groupId>
     <artifactId>cas-server-webapp-undertow</artifactId>
     <version>${cas.version}</version>
</dependency>
```

## External

A CAS deployment may be deployed to any number of external servlet containers. The container **MUST** support
the servlet specification `v3.1.x` at a minimum. In these scenarios, the following vanilla CAS web application
may be used, in the [WAR Overlay](Maven-Overlay-Installation.html) :

```xml
<dependency>
     <groupId>org.apereo.cas</groupId>
     <artifactId>cas-server-webapp</artifactId>
     <version>${cas.version}</version>
</dependency>
```

While there is no official project support, the following containers should be compatible with a CAS deployment:

* [Apache Tomcat](http://tomcat.apache.org/)
* [JBoss](http://www.jboss.org/)
* [Wildfly](http://wildfly.org/)
* [Undertow](http://undertow.io/)
* [Jetty](http://www.eclipse.org/jetty/)
* [GlassFish](http://glassfish.java.net/)
* [WebSphere](http://www.ibm.com/software/websphere/)

Refer to the servlet container's own documentation for more info.

### Apache Tomcat

Deploying into an external Apache Tomcat instance may require the below special considerations.

#### Async Support

In the event that an external servlet container is used, you MAY need to make sure it's configured correctly to support asynchronous requests in the event you get related errors and your container requires this. This is typically handled by setting `<async-supported>true</async-supported>` inside the container's main `web.xml`  file.

#### Async Logging

CAS logging automatically inserts itself into the runtime application context and will clean up
the logging context once Apache Tomcat is instructed to shut down. However,
Apache Tomcat seem to by default ignore all JAR files named `log4j*.jar`, which prevents
this feature from working. You may need to change the `catalina.properties`
and remove `log4j*.jar` from the `jarsToSkip` property. Failure to do so will prevent the container to gracefully shut down and causes logger context threads to hang.

You may need to do something similar on other containers if they skip scanning Log4j JAR files.

## Docker

You may also be interested to deploy CAS via [Docker](https://www.docker.com/).
See [this guide](Docker-Installation.html) for more info.
