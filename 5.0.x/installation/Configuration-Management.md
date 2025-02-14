---
layout: default
title: CAS - Configuration Management
---

# Configuration Management

The core foundations of CAS that deal with configuration management, settings and replication of changes
across multiple CAS nodes are all entirely handled automatically via the 
[Spring Cloud](https://github.com/spring-cloud/spring-cloud-config) project. The strategies listed below
present a very flexible and powerful way to manage CAS configuration for production deployments, by
allowing the CAS adopter to **ONLY** keep track of settings required for their specific deployment concerns
and leaving all else behind to be handled by the default CAS configuration.

The following strategies may be used to fully extend the CAS configuration model.

<div class="alert alert-info"><strong>YAML or Properties?</strong><p>CAS configuration allows for both
YAML and Properties syntax in any of the below strategies used.</p></div>

## Overview

CAS allows you to externalize your configuration so you can work with the same CAS instance in 
different environments. You can use properties files, YAML files, environment variables and 
command-line arguments to externalize configuration.

CAS uses a very particular order that is designed to allow sensible overriding of values, 
properties are considered in the following order:

1. Command line arguments, starting with `--` (e.g. `--server.port=9000`)

Example:
```sh
$ java -jar build/libs/cas.war --spring.cloud.config.server.native.searchLocations=file:///C:/www/cas-gradle-overlay-template/etc/cas/config
```

2. Properties from `SPRING_APPLICATION_JSON` (inline JSON embedded in an environment variable/system property)
3. JNDI attributes from `java:comp/env`.
4. Java System properties.
5. OS environment variables.
6. Profile-specific application properties outside of your packaged jar (application-{profile}.properties and YAML variants)
7. Profile-specific application properties packaged inside your jar (application-{profile}.properties and YAML variants)
8. Application properties outside of your packaged jar (`application.properties` and YAML variants).
9. Application properties packaged inside your jar (`application.properties` and YAML variants).

All CAS settings can be overridden via the above outlined strategies.

<div class="alert alert-info"><strong>Managing Configuration</strong><p>In order to manage 
the CAS configuration, you should configure access 
to <a href="Monitoring-Statistics.html">CAS administration panels.</a></p></div>

## Configuration Server

CAS provides a built-in configuration server that is responsible for bootstrapping the configuration 
environment and loading of externalized settings in a distributed system. You may have a central 
place to manage external properties for CAS nodes across all environments. As your CAS deployment 
moves through the deployment pipeline from dev to test and into production you can manage the configuration 
between those environments and be certain that applications have everything they need to run when they migrate. 
The default implementation of the server storage backend uses git so it easily supports labelled versions 
of configuration environments, as well as being accessible to a wide range of tooling for managing the content. 
Note that CAS also is a client of its own configuration, because not only it has to manage and control 
CAS settings, it also needs to contact the configuration server to retrieve and use those settings. 

The configuration server is controlled and defined by the `bootstrap.properties` file.

The following endpoints are secured and exposed by the configuration server's `/status/configserver` endpoint:

| Parameter                         | Description
|-----------------------------------|------------------------------------------
| `/encrypt`           | Accepts a `POST` to encrypt CAS configuration settings.
| `/decrypt`           | Accepts a `POST` to decrypt CAS configuration settings.
| `/cas/default`       | Describes what the configuration server knows about the `default` settings profile.
| `/cas/native`        | Describes what the configuration server knows about the `native` settings profile.
| `/bus/refresh`       | Reload the configuration of all CAS nodes in the cluster if the cloud bus is turned on.      | 
| `/bus/env`           | Sends key/values pairs to update each CAS node if the cloud bus is turned on.

## Auto Configuration Strategy

To see a complete list of CAS properties, please [review this guide](Configuration-Properties.html).

Note that CAS in most if not all cases will attempt to auto-configure the context based on the declaration 
and presence of feature-specific dedicated modules. This generally SHOULD relieve the deployer
from manually massaging the application context via XML configuration files. 

The idea is twofold:

- Declare your intention for a given CAS feature by declaring the appropriate module in your overlay.
- Optionally, configure the appropriate properties and settings.

CAS will automatically take care of injecting appropriate beans and other components into the runtime application context,
Depending on the presence of a module and/or its settings configured by the deployer.

<div class="alert alert-info"><strong>No XML</strong><p>Again, the entire point of 
the auto-configuration strategy is ensure deployers aren't swimming in a sea of XML files
configuring beans and such. CAS should take care of it all. If you find an instance where
this claim does not hold, consider that a "bug" and file a feature request.</p></div>

## Profiles

Various profiles exist to determine how CAS should retrieve properties and settings. 

### Embedded

By default, all CAS settings and configuration is controlled via the embedded `application.properties` file. 

### Native

CAS is also configured to load a `cas.properties` or `cas.yml` file from an external location that is `/etc/cas/config`. 
This location is constantly monitored by CAS to detect external changes. Note that this location simply needs to 
exist, and does not require any special permissions or structure. The name of the configuration file that goes inside 
this directory needs to match the `spring.application.name` (i.e. cas.properties). If you want to use additional configuration 
files they need to have the form application-\<profile\>.properties or  application-\<profile\>.yml. A file named 
application.properties or application.yml will be included by default. The profile specific files can be activated by 
using the `spring.profiles.include` configuration option.

The configuration of this behavior is controlled via the `src/main/resources/bootstrap.properties` file:

```properties
spring.profiles.active=native
spring.cloud.config.server.native.searchLocations=file:///etc/cas/config
spring.profiles.include=profile1,profile2
```

An example of an external `.properties` file hosted by an external location follows:

```properties
cas.server.name=...
```

You could have just as well used a `cas.yml` file to host the changes.

### Default

CAS is also able to handle git-based repositories that host CAS configuration. 
Such repositories can either be local to the CAS
deployment, or they could be on the cloud in form of GitHub/BitBucket. Access to 
cloud-based repositories can either be in form of a
username/password, or via SSH so as long the appropriate keys are configured in the 
CAS deployment environment which is really no different
than how one would normally access a git repository via SSH. 

```properties
# spring.profiles.active=default
# spring.cloud.config.server.git.uri=https://github.com/repoName/config
# spring.cloud.config.server.git.uri=file://${user.home}/config
# spring.cloud.config.server.git.username=
# spring.cloud.config.server.git.password=
```

Needless to say, the repositories could use both YAML and Properties syntax to host configuration files. 

<div class="alert alert-info"><strong>Keep What You Need!</strong><p>Again, in all of the above strategies,
an adopter is encouraged to only keep and maintain properties needed for their particular deployment. It is
unnecessary to grab a copy of all CAS settings and move them to an external location. Settings that are
defined by the external configuration location or repository are able to override what is provided by CAS
as a default.</p></div>


### MongoDb

CAS is also able to locate properties entirely from a MongoDb instance.

Support is provided via the following dependency:
                                                    
```xml
<dependency>
     <groupId>org.apereo.cas</groupId>
     <artifactId>cas-server-core-configuration-mongo</artifactId>
     <version>${cas.version}</version>
</dependency>
```

Note that to access and review the collection of CAS properties, 
you will need to use [the CAS administrative interfaces](Monitoring-Statistics.html), or you may
also use your own native tooling for MongoDB to configure and inject settings.

MongoDb documents are required to be found in the collection `MongoDbProperty`, as the following document:

```json
{
	"id": "kfhf945jegnsd45sdg93452",
	"name": "the-setting-name",
	"value": "the-setting-value"
} 
```


To see the relevant list of CAS properties for this feature, please [review this guide](Configuration-Properties.html).

### HashiCorp Vault

CAS is also able to use [Vault](https://www.vaultproject.io/) to 
locate properties and settings. [Please review this guide](Configuration-Properties-Security.html).

## Securing Settings

To learn how sensitive CAS settings can be secured via encryption, [please review this guide](Configuration-Properties-Security.html).

## Reloading Changes

To lean more about CAS allows you to reload configuration changes, 
please [review this guide](Configuration-Management-Reload.html).

## Clustered Deployments

CAS uses the [Spring Cloud Bus](http://cloud.spring.io/spring-cloud-static/spring-cloud.html) 
to manage configuration in a distributed deployment. Spring Cloud Bus links nodes of a 
distributed system with a lightweight message broker. This can then be used to broadcast state 
changes (e.g. configuration changes) or other management instructions.

To learn how sensitive CAS settings can be secured via encryption, [please review this guide](Configuration-Management-Clustered.html).
