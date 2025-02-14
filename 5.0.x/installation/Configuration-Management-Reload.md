---
layout: default
title: CAS - Configuration Management - Reloading Changes
---

# Reloading Changes

CAS contains an embedded configuration server that is able to consume properties and settings
via the above strategies. The server is constantly monitoring changes automatically,
but has no way to broadcast those changes
to the rest of the CAS application, which would act as a client of the configuration
server expecting change notifications to quietly reload its configuration.

Therefor, in order to broadcast such `change` events CAS
presents [various endpoints](Monitoring-Statistics.html) that allow the adopter
to **refresh** the configuration as needed. 
For that, you will need to go to the page 'cas/status/dashboard' and click on Refresh button, or to go directly to 'cas/status/refresh' (POST request).

This means that an adopter would simply
change a required CAS settings and then would submit
a request to CAS to refresh its current state. All CAS internal components that are affected
by the external change are quietly reloaded
and the setting takes immediate effect, completely removing the need for container restarts or CAS redeployments.

<div class="alert alert-info"><strong>Do Not Discriminate!</strong><p>Most if not all CAS settings are eligible candidates
for reloads. CAS should be smart enough to reload the appropriate configuration, regardless of setting/module that
ends up using that setting. All is fair game, as the entire CAS web application inclusive of all modules and all
relevant settings is completely and utterly reloadable. </p></div>

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html).

## Reload Strategy

CAS uses [Spring Cloud](https://github.com/spring-cloud/spring-cloud-config)
to manage the internal state of the configuration. The configuration server that
is provided by Spring Cloud embedded in CAS is constantly monitoring sources
that house CAS settings and upon changes will auto-refresh itself.

Next, any changes you make to the externally-defined `application.properties`
file [MUST be refreshed manually](Monitoring-Statistics.html).
If you are using the CAS admin screens to update and edit properties,
the configuration state of the CAS server
is refreshed seamlessly and automatically without your resorting
to manual and forceful refresh.
