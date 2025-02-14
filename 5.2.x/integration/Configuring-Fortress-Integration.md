---
layout: default
title: CAS - Apache Fortress Authentication
---

# Apache Fortress Authentication

Services connected to CAS can use [Apache Fortress](http://directory.apache.org/fortress/) to handle the authentication and authorization with Apache Fortress. 
The idea of this is because Apache Fortress does not have any SSO mechanism. However, Apache Fortress is complied for `ANSI INCITS 359 RBAC`.  
[See this link](http://directory.apache.org/fortress/testimonials.html) for background and history.

## Overview

The following diagram is a typical CAS deployment integrated with Apache Fortress:
![](https://cloud.githubusercontent.com/assets/493782/26521160/f9987de0-430b-11e7-833d-a0e6257a9ebd.PNG)

In the above diagram, CAS will delegate the authentication to Fortress on behalf the Fortress admin user, which is 
configured in the `fortress.properties` file. CAS automatically search for this file (assuming classpath) 
and constructs an access manager component with the admin user as the default communication user to fortress.

To enable this feature, ensure Apache Fortress is [installed](http://directory.apache.org/fortress/installation.html "apache fortress installation").

Next include the following module in the WAR overlay:  

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-fortress</artifactId>
    <version>${cas.version}</version>
</dependency>
```  

At this time, Apache Fortress support is limited to Apache Tomcat as the web container. 
Support for additional containers such as Jetty will be worked out in future releases.

## CAS Configuration

- Configure `fortress.properties` file and put it under your `$TOMCAT_HOME/lib` or you can append your own classpath configuration. An example configuration
file follows:

```properties
http.user=fortress-super-user
http.pw=verysecretpassword
http.host=localhost
http.port=8080
http.protocol=http
```
 
- Put Fortress Realm Proxy under your `$TOMCAT_HOME/lib`.
- Add `-Dversion=<your.fortress.version>` to `JAVA_OPTS` or `CATALINA_OPTS`.  

## Client Configuration

The fortress session is stored as a principal attribute `fortressSession`. As the client you need to extract 
this key in order to get [Session](http://directory.apache.org/fortress/gen-docs/latest/apidocs/org/apache/directory/fortress/core/model/Session.html) 
in xml form. With Fortress session later you can get the roles or get the permission dynamically by calling fortress rest.
