---
layout: default
title: CAS - Audit Configuration
---

# Audits

CAS uses the [Inspektr framework](https://github.com/apereo/inspektr) for auditing purposes
and statistics. The Inspektr project allows for non-intrusive auditing and logging of the
coarse-grained execution paths e.g. Spring-managed beans method executions by using annotations
and Spring-managed `@Aspect`-style aspects.


CAS server auto-configures all the relevant Inspektr components. 
All the available configuration
options that are injected to Inspektr classes are available to 
deployers via relevant CAS properties.

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html).


## File-based Audits

File-based audit logs appear in a `cas_audit.log` file defined in the `log4j2.xml` configuration as well as the usual `cas.log` file.

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html).

### Sample Log Output

```bash
WHO: org.apereo.cas.support.oauth.authentication.principal.OAuthCredentials@6cd7c975
WHAT: supplied credentials: org.apereo.cas.support.oauth.authentication.principal.OAuthCredentials@6cd7c975
ACTION: AUTHENTICATION_SUCCESS
APPLICATION: CAS
WHEN: Mon Aug 26 12:35:59 IST 2013
CLIENT IP ADDRESS: 172.16.5.181
SERVER IP ADDRESS: 192.168.200.22

WHO: org.apereo.cas.support.oauth.authentication.principal.OAuthCredentials@6cd7c975
WHAT: TGT-9-qj2jZKQUmu1gQvXNf7tXQOJPOtROvOuvYAxybhZiVrdZ6pCUwW-cas01.example.org
ACTION: TICKET_GRANTING_TICKET_CREATED
APPLICATION: CAS
WHEN: Mon Aug 26 12:35:59 IST 2013
CLIENT IP ADDRESS: 172.16.5.181
SERVER IP ADDRESS: 192.168.200.22
```

## Database Audits

If you intend to use a database
for auditing functionality, enable the following module in your configuration:

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-audit-jdbc</artifactId>
    <version>${cas.version}</version>
</dependency>
```

To learn how to configure database drivers, [please see this guide](JDBC-Drivers.html). To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html).
