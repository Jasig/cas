---
layout: default
title: CAS - Configuration Management - Reloading Changes
category: Configuration
---

# Reloading Changes

The CAS spring cloud configuration server is able to consume properties and settings
via the [profiles outlined here](Configuration-Server-Management.html). The server is constantly monitoring
changes to the underlying property sources automatically, but has no way to broadcast those changes
to its own clients, such as the CAS server itself, which would act as *a client of the configuration
server* expecting change notifications to quietly reload its configuration.

Therefore, in order to broadcast such `change` events CAS
presents [various endpoints](../monitoring/Monitoring-Statistics.html) that allow the adopter
to **refresh** the configuration as needed. This means that an adopter would simply
change a required CAS settings and then would submit
a request to CAS to refresh its current state. All CAS internal components that are affected
by the external change are quietly reloaded
and the setting takes immediate effect, completely removing the need for container restarts or CAS re-deployments.

<div class="alert alert-info"><strong>Do Not Discriminate!</strong><p>Most if not all CAS settings are eligible candidates
for reloads. CAS should be smart enough to reload the appropriate configuration, regardless of setting/module that
ends up using that setting. All is fair game, as the entire CAS web application inclusive of all modules and all
relevant settings may be completely and utterly reloadable. If you find an instance where this statement does not hold, please speak up.</p></div>

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#cloud-configuration-bus).

## Reload Strategy

CAS uses [Spring Cloud](https://github.com/spring-cloud/spring-cloud-config)
to manage the internal state of the configuration. The configuration server that
is provided by Spring Cloud embedded in CAS is constantly monitoring sources
that house CAS settings and upon changes will auto-refresh itself.

### Standalone

In the event that the [standalone configuration profile](Configuration-Server-Management.html#standalone)
is used to control and direct settings and Spring Cloud configuration server is disabled,
CAS will begin to automatically watch and monitor the configuration files indicated by the profile and will auto-reload the state of the runtime
application context automatically. You may also attempt to [refresh settings manually](../monitoring/Monitoring-Statistics.html)
via the CAS admin endpoints.

Support is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
  <groupId>org.apereo.cas</groupId>
  <artifactId>cas-server-core-events-configuration</artifactId>
  <version>${cas.version}</version>
</dependency>
```

### Spring Cloud

Clients of the configuration server (i.e. CAS server web application) do also expose a `/refresh` endpoint
that allow one to refresh the configuration based on the current state of the configuration server and reconfigure
the application runtime without the need to restart the JVM.

```bash
curl -X POST https://cas.server.url/cas/actuator/refresh
```

[See this guide](../monitoring/Monitoring-Statistics.html) to learn more about various monitoring endpoints, etc.
