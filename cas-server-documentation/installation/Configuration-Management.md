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

## Embedded

By default, all CAS settings and configuration is controlled via the `application.properties` file. This file
serves as a reference and a placeholder for all settings that are available to CAS. 

## Native

CAS is also configured to load `*.properties` files from an external location that is `/etc/cas/config`. This location is constantly
monitored by CAS to detect external changes. Note that this location simply needs to exist, and does not require any special permissions
or structure. The names of the configuration files that go inside this directory also do not matter, and they can be many. 

The configuration of this behavior is controlled via the `bootstrap.properties` file:

```properties
spring.profiles.active=native
spring.cloud.config.server.native.searchLocations=file:///etc/cas/config
```

An example of an external `cas.properties` file hosted by an external location follows:

```properties
accept.authn.users: casuser::Mellon
st.timeToKillInSeconds:10000
st.numberOfUses:100
```

You could have just as well used a `cas.yml` file to host the changes. 

## Default

CAS is also able to handle git-based repositories that host CAS configuration. Such repositories can either be local to the CAS
deployment, or they could be on the cloud in form of GitHub/Bitbucket. Access to cloud-based repositories can either be in form of a
username/password, or via SSH so as long the appropriate keys are configured in the CAS deployment environment which is really no different
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
unnecessary to grab a copy of all CAS settings and move it to an external location. Settings that are
defined by the external configuration location or repository are able to override what is provided by CAS
as a default.</p></div>

## Reloading Changes

CAS contains an embedded configuration server that is able to consume properties and settings
via the above strategies. The server is constantly monitoring changes automatically, but has no way to broadcast those changes
to the rest of the CAS application, which would act as a client of the configuration server expecting change notifications
to quietly reload its configuration. 

Therefor, in order to broadcast such `change` events CAS presents [various endpoints](Monitoring-Statistics.html) that allow the adopter
to **refresh** the configuration as needed. This means that an adopter would simply change a required CAS settings and then would submit 
a request to CAS to refresh its current state. All CAS internal components that are affected by the external change are quietly reloaded
and the setting takes immediate effect, completely removing the need for container restarts or CAS redeployments. 

<div class="alert alert-info"><strong>Do Not Discriminate!</strong><p>Most if not all CAS settings are eligible candidates
for reloads. CAS should be smart enough to reload the appropriate configuration, regardless of setting/module that
ends up using that setting. All is fair game, as the entire CAS web application inclusive of all modules and all
relevant settings is completely and utterly reloadable. </p></div>

## Clustered Deployments


