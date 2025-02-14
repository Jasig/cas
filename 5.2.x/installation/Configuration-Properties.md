---
layout: default
title: CAS Properties
---

# CAS Properties

Various properties can be specified in CAS [either inside configuration files or as command
line switches](Configuration-Management.html#overview). This section provides a list common CAS properties and
references to the underlying modules that consume them.

<div class="alert alert-info"><strong>Be Selective</strong><p>
This section is meant as a guide only. Do <strong>NOT</strong> copy/paste the entire collection of settings into your CAS configuration; rather pick only the properties that you need. Do NOT enable settings unless you are certain of their purpose and do NOT copy settings into your configuration only to keep them as <i>reference</i>. All these ideas lead to upgrade headaches, maintenance nightmares and premature aging.</p></div>

Note that property names can be specified
in very relaxed terms. For instance `cas.someProperty`, `cas.some-property`, `cas.some_property`
and `CAS_SOME_PROPERTY` are all valid names.

The following list of properties are controlled by and provided to CAS. Each block, for most use cases, corresponds
to a specific CAS module that is expected to be included in the final CAS distribution prepared during the build
and deployment process.

<div class="alert alert-info"><strong>YAGNI</strong><p>Note that for nearly ALL use cases,
simply declaring and configuring properties listed below is sufficient. You should NOT have to
explicitly massage a CAS XML configuration file to design an authentication handler,
create attribute release policies, etc. CAS at runtime will auto-configure all required changes for you.</p></div>

## General

A number of CAS configuration options equally apply to a number of modules and features. To understand and take note of those options, please [review this guide](Configuration-Properties-Common.html).

## Configuration Storage

### Standalone

CAS by default will attempt to locate settings and properties inside a given directory indicated 
under the setting name `cas.standalone.config` and otherwise falls back to using `/etc/cas/config`.

There also exists a `cas.standalone.config.file` which can be used to directly feed a collection of properties
to CAS in form of a file or classpath resource. This is specially useful in cases where a bare CAS server is deployed in the cloud without the extra ceremony of a configuration server or an external directory for that matter and the deployer wishes to avoid overriding embedded configuration files.

### Spring Cloud

The following settings are to be loaded by the CAS configuration runtime, which bootstraps
the entire CAS running context. They are to be put inside the `src/main/resources/bootstrap.properties`
of the configuration server itself. See [this guide](Configuration-Server-Management.html) for more info. 

The configuration server backed by Spring Cloud supports the following profiles.
 
### Native

Load settings from external properties/yaml configuration files.

```properties
# spring.profiles.active=native

# The configuration directory where CAS should monitor to locate settings.
# spring.cloud.config.server.native.searchLocations=file:///etc/cas/config
```

### Git Repository

Load settings from an internal/external Git repository.

```properties
# spring.profiles.active=default

# The location of the git repository that contains CAS settings.
# The location can point to an HTTP/SSH/directory.
# spring.cloud.config.server.git.uri=https://github.com/repoName/config
# spring.cloud.config.server.git.uri=file://${user.home}/config

# The credentials used to authenticate git requests, specially
# when using HTTPS. If connecting to the repository via SSH, remember
# to register your public keys with an SSH agent just as your normal would have
# with any other public repository.
# spring.cloud.config.server.git.username=
# spring.cloud.config.server.git.password=
```

The above configuration also applies to online git-based repositories such as Github, BitBucket, etc.

### Vault

Load settings from [HashiCorp's Vault](Configuration-Properties-Security.html).

```properties
# spring.cloud.vault.host=127.0.0.1
# spring.cloud.vault.port=8200
# spring.cloud.vault.token=1305dd6a-a754-f145-3563-2fa90b0773b7
# spring.cloud.vault.connectionTimeout=3000
# spring.cloud.vault.readTimeout=5000
# spring.cloud.vault.enabled=true
# spring.cloud.vault.fail-fast=true
# spring.cloud.vault.scheme=http
# spring.cloud.vault.generic.enabled=true
# spring.cloud.vault.generic.backend=secret
```

### MongoDb

Load settings from a MongoDb instance.

```properties
# cas.spring.cloud.mongo.uri=mongodb://casuser:Mellon@ds135522.mlab.com:35522/jasigcas
```

### ZooKeeper

Load settings from an Apache ZooKeeper instance.

```properties
# spring.cloud.zookeeper.connectString=localhost:2181
# spring.cloud.zookeeper.enabled=true
# spring.cloud.zookeeper.config.enabled=true
# spring.cloud.zookeeper.maxRetries=10
# spring.cloud.zookeeper.config.root=cas/config
```

### DynamoDb

Load settings from a DynamoDb instance.

```properties
# cas.spring.cloud.dynamodb.credentialAccessKey=
# cas.spring.cloud.dynamodb.credentialSecretKey=
# cas.spring.cloud.dynamodb.endpoint=http://localhost:8000
# cas.spring.cloud.dynamodb.localAddress=
# cas.spring.cloud.dynamodb.endpoint=
# cas.spring.cloud.dynamodb.region=
# cas.spring.cloud.dynamodb.regionOverride=
```

### JDBC

Load settings from a RDBMS instance.

```properties
# cas.spring.cloud.jdbc.sql=SELECT id, name, value FROM CAS_SETTINGS_TABLE
# cas.spring.cloud.jdbc.url=
# cas.spring.cloud.jdbc.user=
# cas.spring.cloud.jdbc.password=
# cas.spring.cloud.jdbc.driverClass=
```

## Configuration Security

To learn more about how sensitive CAS settings can be
secured, [please review this guide](Configuration-Properties-Security.html).

### Standalone

```properties
# cas.standalone.config.security.alg=PBEWithMD5AndTripleDES
# cas.standalone.config.security.provider=BC
# cas.standalone.config.security.iterations=
# cas.standalone.config.security.psw=
```

The above settings may be passed to CAS using any of the [strategies outline here](Configuration-Management.html#overview),
though it might be more secure to pass them to CAS as either command-line or system properties.

### Spring Cloud

Encrypt and decrypt configuration via Spring Cloud, if the Spring Cloud configuration server is used.

```properties
# spring.cloud.config.server.encrypt.enabled=true

# encrypt.keyStore.location=file:///etc/cas/casconfigserver.jks
# encrypt.keyStore.password=keystorePassword
# encrypt.keyStore.alias=DaKey
# encrypt.keyStore.secret=changeme
```

## Cloud Configuration Bus

CAS uses the Spring Cloud Bus to manage configuration in a distributed deployment. Spring Cloud Bus links nodes of a
distributed system with a lightweight message broker.

```properties
# spring.cloud.bus.enabled=false
# spring.cloud.bus.refresh.enabled=true
# spring.cloud.bus.env.enabled=true
# spring.cloud.bus.destination=CasCloudBus
# spring.cloud.bus.ack.enabled=true
```

To learn more about this topic, [please review this guide](Configuration-Management.html).

### RabbitMQ

Broadcast CAS configuration updates to other nodes in the cluster
via [RabbitMQ](http://docs.spring.io/spring-cloud-stream/docs/current/reference/htmlsingle/#_rabbitmq_binder).

```properties
# spring.rabbitmq.host=
# spring.rabbitmq.port=
# spring.rabbitmq.username=
# spring.rabbitmq.password=

# Or all of the above in one line
# spring.rabbitmq.addresses=
```

### Kafka

Broadcast CAS configuration updates to other nodes in the cluster
via [Kafka](http://docs.spring.io/spring-cloud-stream/docs/current/reference/htmlsingle/#_apache_kafka_binder).

```properties
# spring.cloud.stream.bindings.output.content-type=application/json
# spring.cloud.stream.kafka.binder.zkNodes=...
# spring.cloud.stream.kafka.binder.brokers=...
```

## Embedded Container

The following properties are related to the embedded containers that ship with CAS.

```properties
server.contextPath=/cas

# By default and if you remove this setting, CAS runs on port 8080
server.port=8443

# To disable SSL configuration, comment out the following settings or set to blank values.
server.ssl.keyStore=file:/etc/cas/thekeystore
server.ssl.keyStorePassword=changeit
server.ssl.keyPassword=changeit
# server.ssl.ciphers=
# server.ssl.clientAuth=
# server.ssl.enabled=
# server.ssl.keyAlias=
# server.ssl.keyStoreProvider=
# server.ssl.keyStoreType=
# server.ssl.protocol=
# server.ssl.trustStore=
# server.ssl.trustStorePassword=
# server.ssl.trustStoreProvider=
# server.ssl.trustStoreType=

server.maxHttpHeaderSize=2097152
server.useForwardHeaders=true
server.connectionTimeout=20000
```

### Embedded Apache Tomcat Container

The following settings affect the runtime behavior of the embedded Apache Tomcat container.

```properties
# server.tomcat.basedir=build/tomcat

# server.tomcat.accesslog.enabled=true
# server.tomcat.accesslog.pattern=%t %a "%r" %s (%D ms)
# server.tomcat.accesslog.suffix=.log

# server.tomcat.maxHttpPostSize=20971520
# server.tomcat.maxThreads=5
# server.tomcat.portHeader=X-Forwarded-Port
# server.tomcat.protocolHeader=X-Forwarded-Proto
# server.tomcat.protocolHeaderHttpsValue=https
# server.tomcat.remoteIpHeader=X-FORWARDED-FOR
# server.tomcat.uriEncoding=UTF-8
```

#### HTTP Proxying

In the event that you decide to run CAS without any SSL configuration in the embedded Tomcat container and on a non-secure port
yet wish to customize the connector configuration that is linked to the running port (i.e. `8080`), the following settings may apply:

```properties
# cas.server.httpProxy.enabled=true
# cas.server.httpProxy.secure=true
# cas.server.httpProxy.protocol=AJP/1.3
# cas.server.httpProxy.scheme=https
# cas.server.httpProxy.redirectPort=
# cas.server.httpProxy.proxyPort=
# cas.server.httpProxy.attributes.attributeName=attributeValue
```

#### HTTP

Enable HTTP connections for the embedded Tomcat container, in addition to the configuration
linked to the `server.port` setting.

```properties
# cas.server.http.port=8080
# cas.server.http.protocol=org.apache.coyote.http11.Http11NioProtocol
# cas.server.http.enabled=true
# cas.server.http.attributes.attributeName=attributeValue
```

#### AJP

Enable AJP connections for the embedded Tomcat container,

```properties
# cas.server.ajp.secure=false
# cas.server.ajp.enabled=false
# cas.server.ajp.proxyPort=-1
# cas.server.ajp.protocol=AJP/1.3
# cas.server.ajp.asyncTimeout=5000
# cas.server.ajp.scheme=http
# cas.server.ajp.maxPostSize=20971520
# cas.server.ajp.port=8009
# cas.server.ajp.enableLookups=false
# cas.server.ajp.redirectPort=-1
# cas.server.ajp.allowTrace=false
# cas.server.ajp.attributes.attributeName=attributeValue
```

#### SSL Valve

The Tomcat SSLValve is a way to get a client certificate from an SSL proxy (e.g. HAProxy or BigIP F5)
running in front of Tomcat via an HTTP header. If you enable this, make sure your proxy is ensuring
that this header doesn't originate with the client (e.g. the browser).

```properties
# cas.server.sslValve.enabled=false
# cas.server.sslValve.sslClientCertHeader=ssl_client_cert
# cas.server.sslValve.sslCipherHeader=ssl_cipher
# cas.server.sslValve.sslSessionIdHeader=ssl_session_id
# cas.server.sslValve.sslCipherUserKeySizeHeader=ssl_cipher_usekeysize
```

Example HAProxy Configuration (snippet)
Configure SSL frontend with cert optional, redirect to cas, if cert provided, put it on header
```
frontend web-vip
  bind 192.168.2.10:443 ssl crt /var/lib/haproxy/certs/www.example.com.pem ca-file /var/lib/haproxy/certs/ca.pem verify optional
  mode http
  acl www-cert ssl_fc_sni if { www.example.com }
  acl empty-path path /
  http-request redirect location /cas/ if empty-path www-cert
  http-request del-header ssl_client_cert unless { ssl_fc_has_crt }
  http-request set-header ssl_client_cert -----BEGIN\ CERTIFICATE-----\ %[ssl_c_der,base64]\ -----END\ CERTIFICATE-----\  if { ssl_fc_has_crt }
  acl cas-path path_beg -i /cas
  reqadd X-Forwarded-Proto:\ https
  use_backend cas-pool if cas-path

backend cas-pool
  option httpclose
  option forwardfor
  cookie SERVERID-cas insert indirect nocache
  server cas-1 192.168.2.10:8080 check cookie cas-1
```

#### Extended Access Log Valve

Enable the [extended access log](http://tomcat.apache.org/tomcat-8.0-doc/config/valve.html#Extended_Access_Log_Valve)
for the embedded Tomcat container.

```properties
# cas.server.extAccessLog.enabled=false
# cas.server.extAccessLog.pattern=c-ip s-ip cs-uri sc-status time x-threadname x-H(secure) x-H(remoteUser)
# cas.server.extAccessLog.suffix=.log
# cas.server.extAccessLog.prefix=localhost_access_extended
# cas.server.extAccessLog.directory=
```

#### Rewrite Valve Valve

Enable the [rewrite valve](https://tomcat.apache.org/tomcat-8.0-doc/rewrite.html) for the embedded Tomcat container.

```properties
# cas.server.rewriteValve.location=classpath://container/tomcat/rewrite.config
```

## CAS Server

Identify the CAS server. `name` and `prefix` are always required settings.

A CAS host is automatically appended to the ticket ids generated by CAS.
If none is specified, one is automatically detected and used by CAS.

```properties
# cas.server.name=https://cas.example.org:8443
# cas.server.prefix=https://cas.example.org:8443/cas
# cas.host.name=
```

## CAS Banner

On startup, CAS will display a banner along with some diagnostics info.
In order to skip this step and summarize, set the system property `-DCAS_BANNER_SKIP=true`.

### Update Check

CAS may also be conditionally configured to report, as part of the banner, whether a newer CAS release is available for an upgrade.
This check is off by default and may be enabled with a system property of `-DCAS_UPDATE_CHECK_ENABLED=true`.  

## Spring Boot Endpoints

The following properties describe access controls and settings for the `/status`
endpoint of CAS which provides administrative functionality and oversight into the CAS software. These endpoints are specific to Spring Boot.

To learn more about this topic, [please review this guide](Monitoring-Statistics.html).

```properties
# Globally control whether endpoints are enabled
# or marked as sesitive to require authentication.
# endpoints.enabled=true
# endpoints.sensitive=true

management.contextPath=/status
management.security.enabled=true
management.security.roles=ACTUATOR,ADMIN
management.security.sessions=if_required

# Each of the below endpoints can either be disabled
# or can be marked as 'sensitive' (or not)
# to enable authentication. The global flags above control
# everything and individual settings below act as overrides.

# endpoints.restart.enabled=false
# endpoints.shutdown.enabled=false
# endpoints.autoconfig.enabled=true
# endpoints.beans.enabled=true
# endpoints.bus.enabled=true
# endpoints.configprops.enabled=true
# endpoints.dump.enabled=true
# endpoints.env.enabled=true
# endpoints.health.enabled=true
# endpoints.features.enabled=true
# endpoints.info.enabled=true
# endpoints.loggers.enabled=true
# endpoints.logfile.enabled=true
# endpoints.trace.enabled=true
# endpoints.docs.enabled=false
# endpoints.heapdump.enabled=true

# IP address may be enough to protect all endpoints.
# If you wish to protect the admin pages via CAS itself, configure the rest.
# cas.adminPagesSecurity.ip=127\.0\.0\.1
# cas.adminPagesSecurity.loginUrl=https://sso.example.org/cas/login
# cas.adminPagesSecurity.service=https://sso.example.org/cas/status/dashboard
# cas.adminPagesSecurity.users=file:/etc/cas/config/adminusers.properties
# cas.adminPagesSecurity.adminRoles[0]=ROLE_ADMIN

# cas.adminPagesSecurity.actuatorEndpointsEnabled=true
```

The format of the `adminusers.properties` file which houses a list of authorized users to access the admin pages via CAS is:

```properties
# casuser=notused,ROLE_ADMIN
```

The format of the file is as such:

- `casuser`: This is the authenticated user id received from CAS
- `notused`: This is the password field that isn't used by CAS. You could literally put any value you want in its place.
- `ROLE_ADMIN`: Role assigned to the authorized user as an attribute, which is then cross checked against CAS configuration.

### Spring Boot Admin Server

To learn more about this topic, [please review this guide](Configuring-Monitoring-Administration.html).

```properties
# spring.boot.admin.url=https://bootadmin.example.org:8444
# spring.boot.admin.client.managementUrl=${cas.server.prefix}/status
# spring.boot.admin.client.name=Apereo CAS
# spring.boot.admin.client.metadata.user.name=
# spring.boot.admin.client.metadata.user.password=
```

## CAS Endpoints

These are the collection of endpoints that are specific to CAS.
To learn more about this topic, [please review this guide](Monitoring-Statistics.html).

```properties
# cas.monitor.endpoints.enabled=false
# cas.monitor.endpoints.sensitive=true

# cas.monitor.endpoints.dashboard.enabled=false
# cas.monitor.endpoints.dashboard.sensitive=true

# cas.monitor.endpoints.discovery.enabled=false
# cas.monitor.endpoints.discovery.sensitive=true

# cas.monitor.endpoints.auditEvents.enabled=false
# cas.monitor.endpoints.auditEvents.sensitive=true

# cas.monitor.endpoints.authenticationEvents.enabled=false
# cas.monitor.endpoints.authenticationEvents.sensitive=true

# cas.monitor.endpoints.configurationState.enabled=false
# cas.monitor.endpoints.configurationState.sensitive=true

# cas.monitor.endpoints.healthCheck.enabled=false
# cas.monitor.endpoints.healthCheck.sensitive=true

# cas.monitor.endpoints.loggingConfig.enabled=false
# cas.monitor.endpoints.loggingConfig.sensitive=true

# cas.monitor.endpoints.metrics.enabled=false
# cas.monitor.endpoints.metrics.sensitive=true

# cas.monitor.endpoints.attributeResolution.enabled=false
# cas.monitor.endpoints.attributeResolution.sensitive=true

# cas.monitor.endpoints.singleSignOnReport.enabled=false
# cas.monitor.endpoints.singleSignOnReport.sensitive=true

# cas.monitor.endpoints.statistics.enabled=false
# cas.monitor.endpoints.statistics.sensitive=true

# cas.monitor.endpoints.trustedDevices.enabled=false
# cas.monitor.endpoints.trustedDevices.sensitive=true

# cas.monitor.endpoints.status.enabled=false
# cas.monitor.endpoints.status.sensitive=true

# cas.monitor.endpoints.singleSignOnStatus.enabled=false
# cas.monitor.endpoints.singleSignOnStatus.sensitive=true

# cas.monitor.endpoints.springWebflowReport.enabled=false
# cas.monitor.endpoints.springWebflowReport.sensitive=true

# cas.monitor.endpoints.registeredServicesReport.enabled=false
# cas.monitor.endpoints.registeredServicesReport.sensitive=true

# cas.monitor.endpoints.configurationMetadata.enabled=false
# cas.monitor.endpoints.configurationMetadata.sensitive=true
```

### Securing Endpoints With Spring Security

Monitoring endpoints may also be secured by Spring Security. You can define the authentication scheme/paths via the below settings.

```properties
# security.ignored[0]=/**
# security.filterOrder=0
# security.requireSsl=true
# security.sessions=if_required
# security.user.name=<predefined-userid>
# security.user.password=<predefined-password>
# security.user.role=ACTUATOR
```

#### Basic Authentication

Enable basic authentication for Spring Security to secure endpoints.

```properties
# security.basic.authorizeMode=none|role|authenticated
# security.basic.enabled=true
# security.basic.path=/cas/status/**
# security.basic.realm=CAS
```

#### JAAS Authentication

Enable JAAS authentication for Spring Security to secure endpoints.

```properties
# cas.adminPagesSecurity.jaas.loginConfig=file:/path/to/config
# cas.adminPagesSecurity.jaas.refreshConfigurationOnStartup=true
# cas.adminPagesSecurity.jaas.loginContextName=
```

#### JDBC Authentication

Enable JDBC authentication for Spring Security to secure endpoints.

```properties
# cas.adminPagesSecurity.jdbc.query=SELECT username,password,enabled FROM users WHERE username=?
# cas.adminPagesSecurity.jdbc.healthQuery=
# cas.adminPagesSecurity.jdbc.isolateInternalQueries=false
# cas.adminPagesSecurity.jdbc.url=jdbc:hsqldb:mem:cas-hsql-database
# cas.adminPagesSecurity.jdbc.failFastTimeout=1
# cas.adminPagesSecurity.jdbc.isolationLevelName=ISOLATION_READ_COMMITTED
# cas.adminPagesSecurity.jdbc.dialect=org.hibernate.dialect.HSQLDialect
# cas.adminPagesSecurity.jdbc.leakThreshold=10
# cas.adminPagesSecurity.jdbc.propagationBehaviorName=PROPAGATION_REQUIRED
# cas.adminPagesSecurity.jdbc.batchSize=1
# cas.adminPagesSecurity.jdbc.user=sa
# cas.adminPagesSecurity.jdbc.ddlAuto=create-drop
# cas.adminPagesSecurity.jdbc.maxAgeDays=180
# cas.adminPagesSecurity.jdbc.password=
# cas.adminPagesSecurity.jdbc.autocommit=false
# cas.adminPagesSecurity.jdbc.driverClass=org.hsqldb.jdbcDriver
# cas.adminPagesSecurity.jdbc.idleTimeout=5000
# cas.adminPagesSecurity.jdbc.dataSourceName=
# cas.adminPagesSecurity.jdbc.dataSourceProxy=false
# Hibernate-specific properties (i.e. `hibernate.globally_quoted_identifiers`)
# cas.adminPagesSecurity.jdbc.properties.propertyName=propertyValue
```

#### LDAP Authentication

Enable LDAP authentication for Spring Security to secure endpoints.

```properties
# cas.adminPagesSecurity.ldap.type=AD|AUTHENTICATED|DIRECT|ANONYMOUS

# cas.adminPagesSecurity.ldap.ldapUrl=ldaps://ldap1.example.edu ldaps://ldap2.example.edu
# cas.adminPagesSecurity.ldap.connectionStrategy=
# cas.adminPagesSecurity.ldap.useSsl=true
# cas.adminPagesSecurity.ldap.useStartTls=false
# cas.adminPagesSecurity.ldap.connectTimeout=5000
# cas.adminPagesSecurity.ldap.baseDn=dc=example,dc=org
# cas.adminPagesSecurity.ldap.userFilter=cn={user}
# cas.adminPagesSecurity.ldap.subtreeSearch=true
# cas.adminPagesSecurity.ldap.bindDn=cn=Directory Manager,dc=example,dc=org
# cas.adminPagesSecurity.ldap.bindCredential=Password

# cas.adminPagesSecurity.ldap.enhanceWithEntryResolver=true
# cas.adminPagesSecurity.ldap.dnFormat=uid=%s,ou=people,dc=example,dc=org
# cas.adminPagesSecurity.ldap.principalAttributePassword=password

# cas.adminPagesSecurity.ldap.saslMechanism=GSSAPI|DIGEST_MD5|CRAM_MD5|EXTERNAL
# cas.adminPagesSecurity.ldap.saslRealm=EXAMPLE.COM
# cas.adminPagesSecurity.ldap.saslAuthorizationId=
# cas.adminPagesSecurity.ldap.saslMutualAuth=
# cas.adminPagesSecurity.ldap.saslQualityOfProtection=

# cas.adminPagesSecurity.ldap.trustCertificates=
# cas.adminPagesSecurity.ldap.keystore=
# cas.adminPagesSecurity.ldap.keystorePassword=
# cas.adminPagesSecurity.ldap.keystoreType=JKS|JCEKS|PKCS12

# cas.adminPagesSecurity.ldap.poolPassivator=NONE|CLOSE|BIND
# cas.adminPagesSecurity.ldap.minPoolSize=3
# cas.adminPagesSecurity.ldap.maxPoolSize=10
# cas.adminPagesSecurity.ldap.validateOnCheckout=true
# cas.adminPagesSecurity.ldap.validatePeriodically=true
# cas.adminPagesSecurity.ldap.validatePeriod=600
# cas.adminPagesSecurity.ldap.validateTimeout=5000

# cas.adminPagesSecurity.ldap.ldapAuthz.groupAttribute=
# cas.adminPagesSecurity.ldap.ldapAuthz.groupPrefix=
# cas.adminPagesSecurity.ldap.ldapAuthz.groupFilter=
# cas.adminPagesSecurity.ldap.ldapAuthz.rolePrefix=ROLE_
# cas.adminPagesSecurity.ldap.ldapAuthz.roleAttribute=uugid
```

## Web Application Session

Control the CAS web application session behavior 
as it's treated by the underlying servlet container engine.

```properties
# server.session.timeout=300
# server.session.cookie.httpOnly=true
# server.session.trackingModes=COOKIE
```

## Views

Control how CAS should treat views and other UI elements.

To learn more about this topic, [please review this guide](User-Interface-Customization-Views.html).

```properties
spring.thymeleaf.encoding=UTF-8

# Controls  whether views should be cached by CAS.
# When turned on, ad-hoc chances to views are not automatically
# picked up by CAS until a restart. Small incremental performance
# improvements are to be expected.
spring.thymeleaf.cache=true

# Instruct CAS to locate views at the below location.
# This location can be externalized to a directory outside
# the cas web application.
# spring.thymeleaf.prefix=classpath:/templates/

# cas.view.cas2.v3ForwardCompatible=false

# Indicate where core CAS-protocol related views should be found
# in the view directory hierarchy.
# cas.view.cas2.success=protocol/2.0/casServiceValidationSuccess
# cas.view.cas2.failure=protocol/2.0/casServiceValidationFailure
# cas.view.cas2.proxy.success=protocol/2.0/casProxySuccessView
# cas.view.cas2.proxy.failure=protocol/2.0/casProxyFailureView


# cas.view.cas3.success=protocol/3.0/casServiceValidationSuccess
# cas.view.cas3.failure=protocol/3.0/casServiceValidationFailure

# Defines a default URL to which CAS may redirect if there is no service
# provided in the authentication request.
# cas.view.defaultRedirectUrl=https://www.github.com
```

## Logging

Control the location and other settings of the CAS logging configuration.
To learn more about this topic, [please review this guide](Logging.html).

```properties
# logging.config=file:/etc/cas/log4j2.xml
server.contextParameters.isLog4jAutoInitializationDisabled=true

# Control log levels via properties
# logging.level.org.apereo.cas=DEBUG
```

To disable log sanitization, start the container with the system property `CAS_TICKET_ID_SANITIZE_SKIP=true`.

## AspectJ Configuration

```properties
# spring.aop.auto=true
# spring.aop.proxyTargetClass=true
```

## Authentication Attributes

Set of authentication attributes that are retrieved by the principal resolution process,
typically via some component of [Person Directory](..\integration\Attribute-Resolution.html)
from a number of attribute sources unless noted otherwise by the specific authentication scheme.

If multiple attribute repository sources are defined, they are added into a list
and their results are cached and merged.

```properties
# cas.authn.attributeRepository.expireInMinutes=30
# cas.authn.attributeRepository.maximumCacheSize=10000
# cas.authn.attributeRepository.merger=REPLACE|ADD|MERGE
```

<div class="alert alert-info"><strong>Remember This</strong><p>Note that in certain cases,
CAS authentication is able to retrieve and resolve attributes from the authentication source in the same authentication request, which would
eliminate the need for configuring a separate attribute repository specially if both the authentication and the attribute source are the same.
Using separate repositories should be required when sources are different, or when there is a need to tackle more advanced attribute
resolution use cases such as cascading, merging, etc.
<a href="Configuring-Principal-Resolution.html">See this guide</a> for more info.</p></div>

Attributes for all sources are defined in their own individual block.
CAS does not care about the source owner of attributes. It finds them where they can be found and otherwise, it moves on.
This means that certain number of attributes can be resolved via one source and the remaining attributes
may be resolved via another. If there are commonalities across sources, the merger shall decide the final result and behavior.

The story in plain english is:

- I have a bunch of attributes that I wish to resolve for the authenticated principal.
- I have a bunch of sources from which said attributes are retrieved.
- Figure it out.

Note that attribute repository sources, if/when defined, execute in a specific order.
This is important to take into account when attribute merging may take place.
By default, the execution order is the following but can be adjusted per source:

1. LDAP
2. JDBC
3. JSON
4. Groovy
5. [Internet2 Grouper](http://www.internet2.edu/products-services/trust-identity/grouper/)
6. REST
7. Script
8. Shibboleth
9. Stubbed/Static

Note that if no *explicit* attribute mappings are defined, all permitted attributes on the record
may be retrieved by CAS from the attribute repository source and made available to the principal. On the other hand,
if explicit attribute mappings are defined, then *only mapped attributes* are retrieved.

### Multimapped Attribute

Attributes may be allowed to be virtually renamed and remapped. The following definition, for instance, attempts to grab the attribute `uid` from the attribute source and rename it to `userId`:

```properties
# cas.authn.attributeRepository.[type-placeholder].attributes.uid=userId
```

### Merging Strategies

The following mergeing strategies can be used to resolve conflicts when the same attribute are found from multiple sources:

| Type                    | Description
|-------------------------|----------------------------------------------------------------------------------------------------
| `REPLACE`               | Overwrites existing attribute values, if any.
| `ADD`                   | Retains existing attribute values if any, and ignores values from subsequent sources in the resolution chain.
| `MERGE`                 | Combines all values into a single attribute, essentially creating a multi-valued attribute.

### Stub

Static attributes that need to be mapped to a hardcoded value belong here.

```properties
# Those values are only strings
# cas.authn.attributeRepository.stub.attributes.uid=frank@hello.com
# cas.authn.attributeRepository.stub.attributes.displayName=frank sinatra
# cas.authn.attributeRepository.stub.attributes.cn=frank
# cas.authn.attributeRepository.stub.attributes.affiliation=musician
```

### LDAP

If you wish to directly and separately retrieve attributes from an LDAP source,
the following settings are then relevant:

```properties
# cas.authn.attributeRepository.ldap[0].attributes.uid=uid
# cas.authn.attributeRepository.ldap[0].attributes.displayName=displayName
# cas.authn.attributeRepository.ldap[0].attributes.cn=commonName
# cas.authn.attributeRepository.ldap[0].attributes.affiliation=groupMembership

# cas.authn.attributeRepository.ldap[0].ldapUrl=ldaps://ldap1.example.edu ldaps://ldap2.example.edu
# cas.authn.attributeRepository.ldap[0].connectionStrategy=
# cas.authn.attributeRepository.ldap[0].order=0
# cas.authn.attributeRepository.ldap[0].useSsl=true
# cas.authn.attributeRepository.ldap[0].useStartTls=false
# cas.authn.attributeRepository.ldap[0].connectTimeout=5000
# cas.authn.attributeRepository.ldap[0].baseDn=dc=example,dc=org
# cas.authn.attributeRepository.ldap[0].userFilter=cn={user}
# cas.authn.attributeRepository.ldap[0].subtreeSearch=true
# cas.authn.attributeRepository.ldap[0].bindDn=cn=Directory Manager,dc=example,dc=org
# cas.authn.attributeRepository.ldap[0].bindCredential=Password
# cas.authn.attributeRepository.ldap[0].trustCertificates=
# cas.authn.attributeRepository.ldap[0].keystore=
# cas.authn.attributeRepository.ldap[0].keystorePassword=
# cas.authn.attributeRepository.ldap[0].keystoreType=JKS|JCEKS|PKCS12
# cas.authn.attributeRepository.ldap[0].poolPassivator=NONE|CLOSE|BIND
# cas.authn.attributeRepository.ldap[0].minPoolSize=3
# cas.authn.attributeRepository.ldap[0].maxPoolSize=10
# cas.authn.attributeRepository.ldap[0].validateOnCheckout=true
# cas.authn.attributeRepository.ldap[0].validatePeriodically=true
# cas.authn.attributeRepository.ldap[0].validatePeriod=600
# cas.authn.attributeRepository.ldap[0].validateTimeout=5000
# cas.authn.attributeRepository.ldap[0].failFast=true
# cas.authn.attributeRepository.ldap[0].idleTime=500
# cas.authn.attributeRepository.ldap[0].prunePeriod=600
# cas.authn.attributeRepository.ldap[0].blockWaitTime=5000
# cas.authn.attributeRepository.ldap[0].providerClass=org.ldaptive.provider.unboundid.UnboundIDProvider

# cas.authn.attributeRepository.ldap[0].validator.type=NONE|SEARCH|COMPARE
# cas.authn.attributeRepository.ldap[0].validator.baseDn=
# cas.authn.attributeRepository.ldap[0].validator.searchFilter=(objectClass=*)
# cas.authn.attributeRepository.ldap[0].validator.scope=OBJECT|ONELEVEL|SUBTREE
# cas.authn.attributeRepository.ldap[0].validator.attributeName=objectClass
# cas.authn.attributeRepository.ldap[0].validator.attributeValues=top
# cas.authn.attributeRepository.ldap[0].validator.dn=
```

### Groovy

If you wish to directly and separately retrieve attributes from a Groovy script,
the following settings are then relevant:

```properties
# cas.authn.attributeRepository.groovy[0].location=file:/etc/cas/attributes.groovy
# cas.authn.attributeRepository.groovy[0].caseInsensitive=false
# cas.authn.attributeRepository.groovy[0].order=0
```

The Groovy script may be designed as:

```groovy
import java.util.*

def Map<String, List<Object>> run(final Object... args) {
    def uid = args[0]
    def logger = args[1];
    def casProperties = args[2]
    def casApplicationContext = args[3]

    logger.debug("[{}]: The received uid is [{}]", this.class.simpleName, uid)
    return[username:[uid], likes:["cheese", "food"], id:[1234,2,3,4,5], another:"attribute"]
}
```

### JSON

If you wish to directly and separately retrieve attributes from a static JSON source,
the following settings are then relevant:

```properties
# cas.authn.attributeRepository.json[0].location=file://etc/cas/attribute-repository.json
# cas.authn.attributeRepository.json[0].order=0
```

The format of the file may be:

```json
{
    "user1": {
        "firstName":["Json1"],
        "lastName":["One"]
    },
    "user2": {
        "firstName":["Json2"],
        "eduPersonAffiliation":["employee", "student"]
    }
}
```

### REST

If you wish to directly and separately retrieve attributes from a REST endpoint,
the following settings are then relevant:

```properties
# cas.authn.attributeRepository.rest[0].method=GET|POST
# cas.authn.attributeRepository.rest[0].order=0
# cas.authn.attributeRepository.rest[0].caseInsensitive=false
# cas.authn.attributeRepository.rest[0].basicAuthUsername=uid
# cas.authn.attributeRepository.rest[0].basicAuthPassword=password
# cas.authn.attributeRepository.rest[0].url=https://rest.somewhere.org/attributes
```

The authenticating user id is passed in form of a request parameter under `username.` The response is expected
to be a JSON map as such:

```json
{
  "name" : "JohnSmith",
  "age" : 29,
  "messages": ["msg 1", "msg 2", "msg 3"]
}
```

### Ruby/Python/Javascript/Groovy

Similar to the Groovy option but more versatile, this option takes advantage of Java's native scripting API to invoke Groovy, Python or Javascript scripting engines to compile a pre-defined script o resolve attributes. The following settings are relevant:

```properties
# cas.authn.attributeRepository.script[0].location=file:/etc/cas/script.groovy
# cas.authn.attributeRepository.script[0].order=0
# cas.authn.attributeRepository.script[0].caseInsensitive=false
```

While Javascript and Groovy should be natively supported by CAS, Python scripts may need
to massage the CAS configuration to include the [Python modules](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22jython-standalone%22).

The Groovy script may be defined as:

```groovy
import java.util.*

Map<String, List<Object>> run(final Object... args) {
    def uid = args[0]
    def logger = args[1]

    logger.debug("Things are happening just fine")
    return[username:[uid], likes:["cheese", "food"], id:[1234,2,3,4,5], another:"attribute"]
}
```

The Javascript script may be defined as:

```javascript
function run(args) {
    var uid = args[0]
    var logger = args[1]
    print("Things are happening just fine")

    var map = {};
    map["username"] = uid;
    map["likes"] = "chees";
    map["id"] = [1234,2,3,4,5];
    map["another"] = "attribute";

    return map;
}
```

### JDBC

If you wish to directly and separately retrieve attributes from a JDBC source,
the following settings are then relevant:

```properties

# cas.authn.attributeRepository.jdbc[0].attributes.uid=uid
# cas.authn.attributeRepository.jdbc[0].attributes.displayName=displayName
# cas.authn.attributeRepository.jdbc[0].attributes.cn=commonName
# cas.authn.attributeRepository.jdbc[0].attributes.affiliation=groupMembership

# cas.authn.attributeRepository.jdbc[0].singleRow=true
# cas.authn.attributeRepository.jdbc[0].order=0
# cas.authn.attributeRepository.jdbc[0].requireAllAttributes=true
# cas.authn.attributeRepository.jdbc[0].caseCanonicalization=NONE|LOWER|UPPER
# cas.authn.attributeRepository.jdbc[0].queryType=OR|AND

# Used only when there is a mapping of many rows to one user
# cas.authn.attributeRepository.jdbc[0].columnMappings.columnAttrName1=columnAttrValue1
# cas.authn.attributeRepository.jdbc[0].columnMappings.columnAttrName2=columnAttrValue2
# cas.authn.attributeRepository.jdbc[0].columnMappings.columnAttrName3=columnAttrValue3

# cas.authn.attributeRepository.jdbc[0].sql=SELECT * FROM table WHERE {0}
# cas.authn.attributeRepository.jdbc[0].username=uid
# cas.authn.attributeRepository.jdbc[0].healthQuery=
# cas.authn.attributeRepository.jdbc[0].isolateInternalQueries=false
# cas.authn.attributeRepository.jdbc[0].url=jdbc:hsqldb:mem:cas-hsql-database
# cas.authn.attributeRepository.jdbc[0].failFastTimeout=1
# cas.authn.attributeRepository.jdbc[0].isolationLevelName=ISOLATION_READ_COMMITTED
# cas.authn.attributeRepository.jdbc[0].dialect=org.hibernate.dialect.HSQLDialect
# cas.authn.attributeRepository.jdbc[0].leakThreshold=10
# cas.authn.attributeRepository.jdbc[0].propagationBehaviorName=PROPAGATION_REQUIRED
# cas.authn.attributeRepository.jdbc[0].batchSize=1
# cas.authn.attributeRepository.jdbc[0].user=sa
# cas.authn.attributeRepository.jdbc[0].ddlAuto=create-drop
# cas.authn.attributeRepository.jdbc[0].password=
# cas.authn.attributeRepository.jdbc[0].autocommit=false
# cas.authn.attributeRepository.jdbc[0].driverClass=org.hsqldb.jdbcDriver
# cas.authn.attributeRepository.jdbc[0].idleTimeout=5000
# cas.authn.attributeRepository.jdbc[0].pool.suspension=false
# cas.authn.attributeRepository.jdbc[0].pool.minSize=6
# cas.authn.attributeRepository.jdbc[0].pool.maxSize=18
# cas.authn.attributeRepository.jdbc[0].pool.maxWait=2000
# cas.authn.attributeRepository.jdbc[0].dataSourceName=
# cas.authn.attributeRepository.jdbc[0].dataSourceProxy=false

# Hibernate-specific properties (i.e. `hibernate.globally_quoted_identifiers`)
# cas.authn.attributeRepository.jdbc[0].properties.propertyName=propertyValue
```

### Grouper

This option reads all the groups from [a Grouper instance](www.internet2.edu/grouper/software.html) for the given CAS principal and adopts them
as CAS attributes under a `grouperGroups` multi-valued attribute. 
To learn more about this topic, [please review this guide](../integration/Attribute-Resolution.html).

```properties
# cas.authn.attributeRepository.grouper[0].enabled=true
```

You will also need to ensure `grouper.client.properties` is available on the classpath (i.e. `src/main/resources`)
with the following configured properties:

```properties
grouperClient.webService.url = http://192.168.99.100:32768/grouper-ws/servicesRest
grouperClient.webService.login = banderson
grouperClient.webService.password = password
```

### Shibboleth Attribute Resolver

To learn more about this topic, [please review this guide](../integration/Attribute-Resolution.html).

```properties
# cas.shibAttributeResolver.resources=classpath:/attribute-resolver.xml
```

### Shibboleth Integrations

To learn more about this topic, [please review this guide](../integration/Shibboleth.html).

```properties
# cas.authn.shibIdp.serverUrl=https://idp.example.org
```

### Default Bundle

If you wish to release a default bundle of attributes to all applications,
and you would rather not duplicate the same attribute per every service definition,
then the following settings are relevant:

```properties
# cas.authn.attributeRepository.defaultAttributesToRelease=cn,givenName,uid,affiliation
```

To learn more about this topic, [please review this guide](../integration/Attribute-Release.html).

### Protocol Attributes

Defines whether CAS should include and release protocol attributes defined in the specification in addition to the
principal attributes. By default all authentication attributes are released when protocol attributes are enabled for
release. If you wish to restrict which authentication attributes get released, you can use the below settings to control authentication attributes more globally.

Protocol/authentication attributes may also be released conditionally on a per-service basis. To learn more about this topic, [please review this guide](../integration/Attribute-Release.html).

```properties
# cas.authn.releaseProtocolAttributes=true

# cas.authn.authenticationAttributeRelease.onlyRelease=authenticationDate,isFromNewLogin
# cas.authn.authenticationAttributeRelease.neverRelease=
```

## Principal Resolution

In the event that a separate resolver is put into place, control
how the final principal should be constructed by default.

```properties
# cas.personDirectory.principalAttribute=
# cas.personDirectory.returnNull=false
# cas.personDirectory.principalResolutionFailureFatal=false
```

## Authentication Policy

To learn more about this topic, [please review this guide](Configuring-Authentication-Components.html#authentication-policy).

Global authentication policy that is applied when
CAS attempts to vend and validate tickets.

```properties
# cas.authn.policy.requiredHandlerAuthenticationPolicyEnabled=false
```

### Any

Satisfied if any handler succeeds. Supports a tryAll flag to avoid short circuiting
and try every handler even if one prior succeeded.

```properties
# cas.authn.policy.any.tryAll=false
```

### All

Satisfied if and only if all given credentials are successfully authenticated.
Support for multiple credentials is new in CAS and this handler
would only be acceptable in a multi-factor authentication situation.

```properties
# cas.authn.policy.all.enabled=true
```

### Unique Principal

Satisfied if and only if the requesting principal has not already authenticated with CAS.
Otherwise the authentication event is blocked, preventing multiple logins. 

<div class="alert alert-warning"><strong>Usage Warning</strong><p>Activating this policy is not without cost,
as CAS needs to query the ticket registry and all tickets present to determine whether the current user has established
a authentication session anywhere. This will surely add a performance burden to the deployment. Use with care.</p></div>

```properties
# cas.authn.policy.uniquePrincipal.enabled=true
```

### Not Prevented

Satisfied if and only if the authentication event is not blocked by a `PreventedException`.

```properties
# cas.authn.policy.notPrevented.enabled=true
```

### Required

Satisfied if and only if a specified handler successfully authenticates its credential.

```properties
# cas.authn.policy.req.tryAll=false
# cas.authn.policy.req.handlerName=handlerName
# cas.authn.policy.req.enabled=true
```

### Groovy

Execute a groovy script to detect authentication policy.

```properties
# cas.authn.policy.groovy[0].script=file:/etc/cas/config/account.groovy
```

The script may be designed as:

```groovy
import java.util.*
import org.apereo.cas.authentication.exceptions.*
import javax.security.auth.login.*

def Exception run(final Object... args) {
    def principal = args[0]
    def logger = args[1]

    if (conditionYouMayDesign() == true) {
        return new AccountDisabledException()
    }
    return null;
}
```

### REST

Contact a REST endpoint via `POST` to detect authentication policy.
The message body contains the CAS authenticated principal that can be used
to examine account status and policy.

```properties
# cas.authn.policy.rest[0].endpoint=https://account.example.org/endpoint
```

| Code                   | Result
|------------------------|---------------------------------------------
| `200`          | Successful authentication.
| `403`, `405`   | Produces a `AccountDisabledException`
| `404`          | Produces a `AccountNotFoundException`
| `423`          | Produces a `AccountLockedException`
| `412`          | Produces a `AccountExpiredException`
| `428`          | Produces a `AccountPasswordMustChangeException`
| Other          | Produces a `FailedLoginException`

## Authentication Throttling

CAS provides a facility for limiting failed login attempts to support password guessing and related abuse scenarios.
To learn more about this topic, [please review this guide](Configuring-Authentication-Throttling.html).


```properties
# cas.authn.throttle.usernameParameter=username
# cas.authn.throttle.schedule.startDelay=10000
# cas.authn.throttle.schedule.repeatInterval=20000
# cas.authn.throttle.appcode=CAS

# cas.authn.throttle.failure.threshold=100
# cas.authn.throttle.failure.code=AUTHENTICATION_FAILED
# cas.authn.throttle.failure.rangeSeconds=60
```

### Database

Queries the data source used by the CAS audit facility to prevent successive failed login attempts for a particular username from the
same IP address.

```properties
# cas.authn.throttle.jdbc.auditQuery=SELECT AUD_DATE FROM COM_AUDIT_TRAIL WHERE AUD_CLIENT_IP = ? AND AUD_USER = ? \
#                                    AND AUD_ACTION = ? AND APPLIC_CD = ? AND AUD_DATE >= ? ORDER BY AUD_DATE DESC
# cas.authn.throttle.jdbc.healthQuery=
# cas.authn.throttle.jdbc.isolateInternalQueries=false
# cas.authn.throttle.jdbc.url=jdbc:hsqldb:mem:cas-hsql-database
# cas.authn.throttle.jdbc.failFastTimeout=1
# cas.authn.throttle.jdbc.isolationLevelName=ISOLATION_READ_COMMITTED
# cas.authn.throttle.jdbc.dialect=org.hibernate.dialect.HSQLDialect
# cas.authn.throttle.jdbc.leakThreshold=10
# cas.authn.throttle.jdbc.propagationBehaviorName=PROPAGATION_REQUIRED
# cas.authn.throttle.jdbc.batchSize=1
# cas.authn.throttle.jdbc.user=sa
# cas.authn.throttle.jdbc.ddlAuto=create-drop
# cas.authn.throttle.jdbc.maxAgeDays=180
# cas.authn.throttle.jdbc.password=
# cas.authn.throttle.jdbc.autocommit=false
# cas.authn.throttle.jdbc.driverClass=org.hsqldb.jdbcDriver
# cas.authn.throttle.jdbc.idleTimeout=5000

# cas.authn.throttle.jdbc.pool.suspension=false
# cas.authn.throttle.jdbc.pool.minSize=6
# cas.authn.throttle.jdbc.pool.maxSize=18
# cas.authn.throttle.jdbc.pool.maxWait=2000
# cas.authn.throttle.jdbc.dataSourceName=
# cas.authn.throttle.jdbc.dataSourceProxy=false

# Hibernate-specific properties (i.e. `hibernate.globally_quoted_identifiers`)
# cas.authn.throttle.jdbc.properties.propertyName=propertyValue
```

## Adaptive Authentication

Control how CAS authentication should adapt itself to incoming client requests.
To learn more about this topic, [please review this guide](Configuring-Adaptive-Authentication.html).

```properties
# cas.authn.adaptive.rejectCountries=United.+
# cas.authn.adaptive.rejectBrowsers=Gecko.+
# cas.authn.adaptive.rejectIpAddresses=127.+

# cas.authn.adaptive.requireMultifactor.mfa-duo=127.+|United.+|Gecko.+
```

Adaptive authentication can also react to specific times in order to trigger multifactor authentication.

```properties
# cas.authn.adaptive.requireTimedMultifactor[0].providerId=mfa-duo
# cas.authn.adaptive.requireTimedMultifactor[0].onOrAfterHour=20
# cas.authn.adaptive.requireTimedMultifactor[0].onOrBeforeHour=7
# cas.authn.adaptive.requireTimedMultifactor[0].onDays=Saturday,Sunday
```

## Surrogate Authentication

Authenticate on behalf of another user.
To learn more about this topic, [please review this guide](Surrogate-Authentication.html).

```properties
# cas.authn.surrogate.separator=+
```

### Static Surrogate Accounts

```properties
# cas.authn.surrogate.simple.surrogates.casuser=jsmith,jsmith2
# cas.authn.surrogate.simple.surrogates.casuser2=jsmith4,jsmith2
```

### JSON Surrogate Accounts

```properties
# cas.authn.surrogate.json.location=file:/etc/cas/config/surrogates.json
```

### LDAP Surrogate Accounts

```properties
# cas.authn.surrogate.ldap.ldapUrl=ldaps://ldap1.example.edu ldaps://ldap2.example.edu
# cas.authn.surrogate.ldap.connectionStrategy=
# cas.authn.surrogate.ldap.baseDn=dc=example,dc=org
# cas.authn.surrogate.ldap.userFilter=cn={user}
# cas.authn.surrogate.ldap.bindDn=cn=Directory Manager,dc=example,dc=org
# cas.authn.surrogate.ldap.bindCredential=Password
# cas.authn.surrogate.ldap.providerClass=org.ldaptive.provider.unboundid.UnboundIDProvider
# cas.authn.surrogate.ldap.connectTimeout=5000
# cas.authn.surrogate.ldap.trustCertificates=
# cas.authn.surrogate.ldap.keystore=
# cas.authn.surrogate.ldap.keystorePassword=
# cas.authn.surrogate.ldap.keystoreType=JKS|JCEKS|PKCS12
# cas.authn.surrogate.ldap.poolPassivator=NONE|CLOSE|BIND
# cas.authn.surrogate.ldap.minPoolSize=3
# cas.authn.surrogate.ldap.maxPoolSize=10
# cas.authn.surrogate.ldap.validateOnCheckout=true
# cas.authn.surrogate.ldap.validatePeriodically=true
# cas.authn.surrogate.ldap.validatePeriod=600
# cas.authn.surrogate.ldap.validateTimeout=5000
# cas.authn.surrogate.ldap.failFast=true
# cas.authn.surrogate.ldap.idleTime=500
# cas.authn.surrogate.ldap.prunePeriod=600
# cas.authn.surrogate.ldap.blockWaitTime=5000
# cas.authn.surrogate.ldap.useSsl=true
# cas.authn.surrogate.ldap.useStartTls=false

# cas.authn.surrogate.ldap.validator.type=NONE|SEARCH|COMPARE
# cas.authn.surrogate.ldap.validator.baseDn=
# cas.authn.surrogate.ldap.validator.searchFilter=(objectClass=*)
# cas.authn.surrogate.ldap.validator.scope=OBJECT|ONELEVEL|SUBTREE
# cas.authn.surrogate.ldap.validator.attributeName=objectClass
# cas.authn.surrogate.ldap.validator.attributeValues=top
# cas.authn.surrogate.ldap.validator.dn=

# cas.authn.surrogate.ldap.searchFilter=principal={user}
# cas.authn.surrogate.ldap.surrogateSearchFilter=(&(principal={user})(memberOf=cn=edu:example:cas:something:{user},dc=example,dc=edu))
# cas.authn.surrogate.ldap.memberAttributeName=memberOf
# cas.authn.surrogate.ldap.memberAttributeValueRegex=cn=edu:example:cas:something:([^,]+),.+
```

### JDBC Surrogate Accounts

```properties
# cas.authn.surrogate.jdbc.validationQuery=SELECT 1
# cas.authn.surrogate.jdbc.maxWait=5000
# cas.authn.surrogate.jdbc.healthQuery=
# cas.authn.surrogate.jdbc.isolateInternalQueries=false
# cas.authn.surrogate.jdbc.url=jdbc:hsqldb:mem:cas-hsql-database
# cas.authn.surrogate.jdbc.failFastTimeout=1
# cas.authn.surrogate.jdbc.isolationLevelName=ISOLATION_READ_COMMITTED
# cas.authn.surrogate.jdbc.dialect=org.hibernate.dialect.HSQLDialect
# cas.authn.surrogate.jdbc.leakThreshold=10
# cas.authn.surrogate.jdbc.propagationBehaviorName=PROPAGATION_REQUIRED
# cas.authn.surrogate.jdbc.batchSize=1
# cas.authn.surrogate.jdbc.user=sa
# cas.authn.surrogate.jdbc.ddlAuto=create-drop
# cas.authn.surrogate.jdbc.maxAgeDays=180
# cas.authn.surrogate.jdbc.password=
# cas.authn.surrogate.jdbc.autocommit=false
# cas.authn.surrogate.jdbc.driverClass=org.hsqldb.jdbcDriver
# cas.authn.surrogate.jdbc.idleTimeout=5000
# cas.authn.surrogate.jdbc.dataSourceName=
# cas.authn.surrogate.jdbc.dataSourceProxy=false

# Hibernate-specific properties (i.e. `hibernate.globally_quoted_identifiers`)
# cas.authn.surrogate.jdbc.properties.propertyName=propertyValue

# cas.authn.surrogate.jdbc.surrogateSearchQuery=SELECT COUNT(*) FROM surrogate WHERE username=?
# cas.authn.surrogate.jdbc.surrogateAccountQuery=SELECT surrogate_user AS surrogateAccount FROM surrogate WHERE username=?
```

### REST Surrogate Accounts

```properties
# cas.authn.surrogate.rest.url=https://somewhere.interrupt.org
# cas.authn.surrogate.rest.method=GET|POST
# cas.authn.surrogate.rest.basicAuthUsername=
# cas.authn.surrogate.rest.basicAuthPassword=
```

### Notifications

```properties
# cas.authn.surrogate.sms.from=
# cas.authn.surrogate.sms.text=
# cas.authn.surrogate.sms.attributeName=phone

# cas.authn.surrogate.mail.from=
# cas.authn.surrogate.mail.text=
# cas.authn.surrogate.mail.subject=
# cas.authn.surrogate.mail.cc=
# cas.authn.surrogate.mail.bcc=
# cas.authn.surrogate.mail.attributeName=mail

```

## Risk-based Authentication

Evaluate suspicious authentication requests and take action.
To learn more about this topic, [please review this guide](Configuring-RiskBased-Authentication.html).

```properties
# cas.authn.adaptive.risk.threshold=0.6
# cas.authn.adaptive.risk.daysInRecentHistory=30

# cas.authn.adaptive.risk.ip.enabled=false

# cas.authn.adaptive.risk.agent.enabled=false

# cas.authn.adaptive.risk.geoLocation.enabled=false

# cas.authn.adaptive.risk.dateTime.enabled=false
# cas.authn.adaptive.risk.dateTime.windowInHours=2

# cas.authn.adaptive.risk.response.blockAttempt=false

# cas.authn.adaptive.risk.response.mfaProvider=
# cas.authn.adaptive.risk.response.riskyAuthenticationAttribute=triggeredRiskBasedAuthentication

# cas.authn.adaptive.risk.response.mail.from=
# cas.authn.adaptive.risk.response.mail.text=
# cas.authn.adaptive.risk.response.mail.subject=
# cas.authn.adaptive.risk.response.mail.cc=
# cas.authn.adaptive.risk.response.mail.bcc=
# cas.authn.adaptive.risk.response.mail.attributeName=mail

# cas.authn.adaptive.risk.response.sms.from=
# cas.authn.adaptive.risk.response.sms.text=
# cas.authn.adaptive.risk.response.sms.attributeName=phone
```

## Email Submissions

To learn more about this topic, [please review this guide](SMS-Email-Configuration.html).


```properties
# spring.mail.host=
# spring.mail.port=
# spring.mail.username=
# spring.mail.password=
# spring.mail.testConnection=true
# spring.mail.properties.mail.smtp.auth=true
# spring.mail.properties.mail.smtp.starttls.enable=true
```

## SMS Messaging

To learn more about this topic, [please review this guide](SMS-Messaging-Configuration.html).

### Twilio

```properties
# cas.twilio.accountId=
# cas.twilio.token=
```

### TextMagic

```properties
# cas.textMagic.username=
# cas.textMagic.token=
```

### Clickatell

```properties
# cas.clickatell.serverUrl=https://platform.clickatell.com/messages
# cas.clickatell.token=
```

## GeoTracking

To learn more about this topic, [please review this guide](GeoTracking-Authentication-Requests.html).

### GoogleMaps GeoTracking

Used to geo-profile authentication events.

```properties
# cas.googleMaps.apiKey=
# cas.googleMaps.clientId=
# cas.googleMaps.clientSecret=
# cas.googleMaps.connectTimeout=3000
# cas.googleMaps.googleAppsEngine=false
```

### Maxmind GeoTracking

Used to geo-profile authentication events.

```properties
# cas.maxmind.cityDatabase=file:/etc/cas/maxmind/GeoLite2-City.mmdb
# cas.maxmind.countryDatabase=file:/etc/cas/maxmind/GeoLite2-Country.mmdb
```

## Cassandra Authentication

To learn more about this topic, [please review this guide](Cassandra-Authentication.html).

```properties
# cas.authn.cassandra.usernameAttribute=
# cas.authn.cassandra.passwordAttribute=
# cas.authn.cassandra.tableName=
# cas.authn.cassandra.username=
# cas.authn.cassandra.password=

# cas.authn.cassandra.protocolVersion=V1|V2|V3|V4
# cas.authn.cassandra.keyspace=
# cas.authn.cassandra.contactPoints=localhost1,localhost2
# cas.authn.cassandra.localDc=
# cas.authn.cassandra.shuffleReplicas=true
# cas.authn.cassandra.retryPolicy=DEFAULT_RETRY_POLICY|DOWNGRADING_CONSISTENCY_RETRY_POLICY|FALLTHROUGH_RETRY_POLICY
# cas.authn.cassandra.compression=LZ4|SNAPPY|NONE
# cas.authn.cassandra.consistencyLevel=ANY|ONE|TWO|THREE|QUORUM|LOCAL_QUORUM|ALL|EACH_QUORUM|LOCAL_SERIAL|SERIAL|LOCAL_ONE
# cas.authn.cassandra.serialConsistencyLevel=ANY|ONE|TWO|THREE|QUORUM|LOCAL_QUORUM|ALL|EACH_QUORUM|LOCAL_SERIAL|SERIAL|LOCAL_ONE
# cas.authn.cassandra.maxConnections=10
# cas.authn.cassandra.coreConnections=1
# cas.authn.cassandra.maxRequestsPerConnection=1024
# # cas.authn.cassandra.connectTimeoutMillis=5000
cas.authn.cassandra.readTimeoutMillis=5000
# cas.authn.cassandra.port=9042
# cas.authn.cassandra.name=
# cas.authn.cassandra.order=
```

## Digest Authentication

To learn more about this topic, [please review this guide](Digest-Authentication.html).

```properties
# cas.authn.digest.users.casuser=3530292c24102bac7ced2022e5f1036a
# cas.authn.digest.users.anotheruser=7530292c24102bac7ced2022e5f1036b
# cas.authn.digest.realm=CAS
# cas.authn.digest.name=
# cas.authn.digest.authenticationMethod=auth
```

## Radius Authentication

To learn more about this topic, [please review this guide](RADIUS-Authentication.html).

```properties
# cas.authn.radius.server.nasPortId=-1
# cas.authn.radius.server.nasRealPort=-1
# cas.authn.radius.server.protocol=EAP_MSCHAPv2
# cas.authn.radius.server.retries=3
# cas.authn.radius.server.nasPortType=-1
# cas.authn.radius.server.nasPort=-1
# cas.authn.radius.server.nasIpAddress=
# cas.authn.radius.server.nasIpv6Address=
# cas.authn.radius.server.nasIdentifier=-1

# cas.authn.radius.client.authenticationPort=1812
# cas.authn.radius.client.sharedSecret=N0Sh@ar3d$ecReT
# cas.authn.radius.client.socketTimeout=0
# cas.authn.radius.client.inetAddress=localhost
# cas.authn.radius.client.accountingPort=1813

# cas.authn.radius.name=
# cas.authn.radius.failoverOnException=false
# cas.authn.radius.failoverOnAuthenticationFailure=false

# cas.authn.radius.passwordEncoder.type=NONE|DEFAULT|STANDARD|BCRYPT|SCRYPT|PBKDF2|com.example.CustomPasswordEncoder
# cas.authn.radius.passwordEncoder.characterEncoding=
# cas.authn.radius.passwordEncoder.encodingAlgorithm=
# cas.authn.radius.passwordEncoder.secret=
# cas.authn.radius.passwordEncoder.strength=16

# cas.authn.radius.principalTransformation.pattern=(.+)@example.org
# cas.authn.radius.principalTransformation.groovy.location=file:///etc/cas/config/principal.groovy
# cas.authn.radius.principalTransformation.suffix=
# cas.authn.radius.principalTransformation.caseConversion=NONE|UPPERCASE|LOWERCASE
# cas.authn.radius.principalTransformation.prefix=
```

## File (Whitelist) Authentication

To learn more about this topic, [please review this guide](Whitelist-Authentication.html).

```properties
# cas.authn.file.separator=::
# cas.authn.file.filename=file:///path/to/users/file
# cas.authn.file.name=

# cas.authn.file.passwordEncoder.type=NONE|DEFAULT|STANDARD|BCRYPT|SCRYPT|PBKDF2|com.example.CustomPasswordEncoder
# cas.authn.file.passwordEncoder.characterEncoding=
# cas.authn.file.passwordEncoder.encodingAlgorithm=
# cas.authn.file.passwordEncoder.secret=
# cas.authn.file.passwordEncoder.strength=16

# cas.authn.file.principalTransformation.pattern=(.+)@example.org
# cas.authn.file.principalTransformation.groovy.location=file:///etc/cas/config/principal.groovy
# cas.authn.file.principalTransformation.suffix=
# cas.authn.file.principalTransformation.caseConversion=NONE|UPPERCASE|LOWERCASE
# cas.authn.file.principalTransformation.prefix=
```

## Reject Users (Blacklist) Authentication

To learn more about this topic, [please review this guide](Blacklist-Authentication.html).

```properties
# cas.authn.reject.users=user1,user2
# cas.authn.reject.name=

# cas.authn.reject.passwordEncoder.type=NONE|DEFAULT|STANDARD|BCRYPT|SCRYPT|PBKDF2|com.example.CustomPasswordEncoder
# cas.authn.reject.passwordEncoder.characterEncoding=
# cas.authn.reject.passwordEncoder.encodingAlgorithm=
# cas.authn.reject.passwordEncoder.secret=
# cas.authn.reject.passwordEncoder.strength=16

# cas.authn.reject.principalTransformation.pattern=(.+)@example.org
# cas.authn.reject.principalTransformation.groovy.location=file:///etc/cas/config/principal.groovy
# cas.authn.reject.principalTransformation.suffix=
# cas.authn.reject.principalTransformation.caseConversion=NONE|UPPERCASE|LOWERCASE
# cas.authn.reject.principalTransformation.prefix=
```

## Database Authentication

To learn more about this topic, [please review this guide](Database-Authentication.html).

### Query Database Authentication

Authenticates a user by comparing the user password (which can be encoded with a password encoder)
against the password on record determined by a configurable database query.

```properties
# cas.authn.jdbc.query[0].sql=SELECT * FROM table WHERE name=?
# cas.authn.jdbc.query[0].healthQuery=
# cas.authn.jdbc.query[0].isolateInternalQueries=false
# cas.authn.jdbc.query[0].url=jdbc:hsqldb:mem:cas-hsql-database
# cas.authn.jdbc.query[0].failFastTimeout=1
# cas.authn.jdbc.query[0].isolationLevelName=ISOLATION_READ_COMMITTED
# cas.authn.jdbc.query[0].dialect=org.hibernate.dialect.HSQLDialect
# cas.authn.jdbc.query[0].leakThreshold=10
# cas.authn.jdbc.query[0].propagationBehaviorName=PROPAGATION_REQUIRED
# cas.authn.jdbc.query[0].batchSize=1
# cas.authn.jdbc.query[0].user=user
# cas.authn.jdbc.query[0].ddlAuto=create-drop
# cas.authn.jdbc.query[0].maxAgeDays=180
# cas.authn.jdbc.query[0].password=secret
# cas.authn.jdbc.query[0].autocommit=false
# cas.authn.jdbc.query[0].driverClass=org.hsqldb.jdbcDriver
# cas.authn.jdbc.query[0].idleTimeout=5000
# cas.authn.jdbc.query[0].credentialCriteria=
# cas.authn.jdbc.query[0].name=
# cas.authn.jdbc.query[0].order=0
# cas.authn.jdbc.query[0].dataSourceName=
# cas.authn.jdbc.query[0].dataSourceProxy=false
# Hibernate-specific properties (i.e. `hibernate.globally_quoted_identifiers`)
# cas.authn.jdbc.query[0].properties.propertyName=propertyValue

# cas.authn.jdbc.query[0].fieldPassword=password
# cas.authn.jdbc.query[0].fieldExpired=
# cas.authn.jdbc.query[0].fieldDisabled=
# cas.authn.jdbc.query[0].principalAttributeList=sn,cn:commonName,givenName

# cas.authn.jdbc.query[0].passwordEncoder.type=NONE|DEFAULT|STANDARD|BCRYPT|SCRYPT|PBKDF2|com.example.CustomPasswordEncoder
# cas.authn.jdbc.query[0].passwordEncoder.characterEncoding=
# cas.authn.jdbc.query[0].passwordEncoder.encodingAlgorithm=
# cas.authn.jdbc.query[0].passwordEncoder.secret=
# cas.authn.jdbc.query[0].passwordEncoder.strength=16

# cas.authn.jdbc.query[0].principalTransformation.pattern=(.+)@example.org
# cas.authn.jdbc.query[0].principalTransformation.groovy.location=file:///etc/cas/config/principal.groovy
# cas.authn.jdbc.query[0].principalTransformation.suffix=
# cas.authn.jdbc.query[0].principalTransformation.caseConversion=NONE|UPPERCASE|LOWERCASE
# cas.authn.jdbc.query[0].principalTransformation.prefix=
```

### Search Database Authentication

Searches for a user record by querying against a username and password; the user is authenticated if at least one result is found.

```properties
# cas.authn.jdbc.search[0].fieldUser=
# cas.authn.jdbc.search[0].tableUsers=
# cas.authn.jdbc.search[0].fieldPassword=
# cas.authn.jdbc.search[0].healthQuery=
# cas.authn.jdbc.search[0].isolateInternalQueries=false
# cas.authn.jdbc.search[0].url=jdbc:hsqldb:mem:cas-hsql-database
# cas.authn.jdbc.search[0].failFastTimeout=1
# cas.authn.jdbc.search[0].isolationLevelName=ISOLATION_READ_COMMITTED
# cas.authn.jdbc.search[0].dialect=org.hibernate.dialect.HSQLDialect
# cas.authn.jdbc.search[0].leakThreshold=10
# cas.authn.jdbc.search[0].propagationBehaviorName=PROPAGATION_REQUIRED
# cas.authn.jdbc.search[0].batchSize=1
# cas.authn.jdbc.search[0].user=sa
# cas.authn.jdbc.search[0].ddlAuto=create-drop
# cas.authn.jdbc.search[0].maxAgeDays=180
# cas.authn.jdbc.search[0].password=
# cas.authn.jdbc.search[0].autocommit=false
# cas.authn.jdbc.search[0].driverClass=org.hsqldb.jdbcDriver
# cas.authn.jdbc.search[0].idleTimeout=5000
# cas.authn.jdbc.search[0].credentialCriteria=
# cas.authn.jdbc.search[0].name=
# cas.authn.jdbc.search[0].order=0
# cas.authn.jdbc.search[0].dataSourceName=
# cas.authn.jdbc.search[0].dataSourceProxy=false
# Hibernate-specific properties (i.e. `hibernate.globally_quoted_identifiers`)
# cas.authn.jdbc.search[0].properties.propertyName=propertyValue

# cas.authn.jdbc.search[0].passwordEncoder.type=NONE|DEFAULT|STANDARD|BCRYPT|SCRYPT|PBKDF2|com.example.CustomPasswordEncoder
# cas.authn.jdbc.search[0].passwordEncoder.characterEncoding=
# cas.authn.jdbc.search[0].passwordEncoder.encodingAlgorithm=
# cas.authn.jdbc.search[0].passwordEncoder.secret=
# cas.authn.jdbc.search[0].passwordEncoder.strength=16

# cas.authn.jdbc.search[0].principalTransformation.pattern=(.+)@example.org
# cas.authn.jdbc.search[0].principalTransformation.groovy.location=file:///etc/cas/config/principal.groovy
# cas.authn.jdbc.search[0].principalTransformation.suffix=
# cas.authn.jdbc.search[0].principalTransformation.caseConversion=NONE|UPPERCASE|LOWERCASE
# cas.authn.jdbc.search[0].principalTransformation.prefix=
```

### Bind Database Authentication

Authenticates a user by attempting to create a database connection using the username and (hashed) password.

```properties
# cas.authn.jdbc.bind[0].healthQuery=
# cas.authn.jdbc.bind[0].isolateInternalQueries=false
# cas.authn.jdbc.bind[0].url=jdbc:hsqldb:mem:cas-hsql-database
# cas.authn.jdbc.bind[0].failFastTimeout=1
# cas.authn.jdbc.bind[0].isolationLevelName=ISOLATION_READ_COMMITTED
# cas.authn.jdbc.bind[0].dialect=org.hibernate.dialect.HSQLDialect
# cas.authn.jdbc.bind[0].leakThreshold=10
# cas.authn.jdbc.bind[0].propagationBehaviorName=PROPAGATION_REQUIRED
# cas.authn.jdbc.bind[0].batchSize=1
# cas.authn.jdbc.bind[0].user=sa
# cas.authn.jdbc.bind[0].ddlAuto=create-drop
# cas.authn.jdbc.bind[0].maxAgeDays=180
# cas.authn.jdbc.bind[0].password=
# cas.authn.jdbc.bind[0].autocommit=false
# cas.authn.jdbc.bind[0].driverClass=org.hsqldb.jdbcDriver
# cas.authn.jdbc.bind[0].idleTimeout=5000
# cas.authn.jdbc.bind[0].credentialCriteria=
# cas.authn.jdbc.bind[0].name=
# cas.authn.jdbc.bind[0].order=0
# cas.authn.jdbc.bind[0].dataSourceName=
# cas.authn.jdbc.bind[0].dataSourceProxy=false
# Hibernate-specific properties (i.e. `hibernate.globally_quoted_identifiers`)
# cas.authn.jdbc.bind[0].properties.propertyName=propertyValue

# cas.authn.jdbc.bind[0].passwordEncoder.type=NONE|DEFAULT|STANDARD|BCRYPT|SCRYPT|PBKDF2|com.example.CustomPasswordEncoder
# cas.authn.jdbc.bind[0].passwordEncoder.characterEncoding=
# cas.authn.jdbc.bind[0].passwordEncoder.encodingAlgorithm=
# cas.authn.jdbc.bind[0].passwordEncoder.secret=
# cas.authn.jdbc.bind[0].passwordEncoder.strength=16

# cas.authn.jdbc.bind[0].principalTransformation.pattern=(.+)@example.org
# cas.authn.jdbc.bind[0].principalTransformation.groovy.location=file:///etc/cas/config/principal.groovy
# cas.authn.jdbc.bind[0].principalTransformation.suffix=
# cas.authn.jdbc.bind[0].principalTransformation.caseConversion=NONE|UPPERCASE|LOWERCASE
# cas.authn.jdbc.bind[0].principalTransformation.prefix=
```

### Encode Database Authentication

A JDBC querying handler that will pull back the password and the private salt value for a user and validate the encoded
password using the public salt value. Assumes everything is inside the same database table. Supports settings for
number of iterations as well as private salt.

This password encoding method combines the private Salt and the public salt which it prepends to the password before hashing.
If multiple iterations are used, the bytecode hash of the first iteration is rehashed without the salt values. The final hash
is converted to hex before comparing it to the database value.

```properties
# cas.authn.jdbc.encode[0].numberOfIterations=0
# cas.authn.jdbc.encode[0].numberOfIterationsFieldName=numIterations
# cas.authn.jdbc.encode[0].saltFieldName=salt
# cas.authn.jdbc.encode[0].staticSalt=
# cas.authn.jdbc.encode[0].sql=
# cas.authn.jdbc.encode[0].algorithmName=
# cas.authn.jdbc.encode[0].passwordFieldName=password
# cas.authn.jdbc.encode[0].expiredFieldName=
# cas.authn.jdbc.encode[0].disabledFieldName=
# cas.authn.jdbc.encode[0].healthQuery=
# cas.authn.jdbc.encode[0].isolateInternalQueries=false
# cas.authn.jdbc.encode[0].url=jdbc:hsqldb:mem:cas-hsql-database
# cas.authn.jdbc.encode[0].failFastTimeout=1
# cas.authn.jdbc.encode[0].isolationLevelName=ISOLATION_READ_COMMITTED
# cas.authn.jdbc.encode[0].dialect=org.hibernate.dialect.HSQLDialect
# cas.authn.jdbc.encode[0].leakThreshold=10
# cas.authn.jdbc.encode[0].propagationBehaviorName=PROPAGATION_REQUIRED
# cas.authn.jdbc.encode[0].batchSize=1
# cas.authn.jdbc.encode[0].user=sa
# cas.authn.jdbc.encode[0].ddlAuto=create-drop
# cas.authn.jdbc.encode[0].maxAgeDays=180
# cas.authn.jdbc.encode[0].password=
# cas.authn.jdbc.encode[0].autocommit=false
# cas.authn.jdbc.encode[0].driverClass=org.hsqldb.jdbcDriver
# cas.authn.jdbc.encode[0].idleTimeout=5000
# cas.authn.jdbc.encode[0].credentialCriteria=
# cas.authn.jdbc.encode[0].name=
# cas.authn.jdbc.encode[0].order=0
# cas.authn.jdbc.encode[0].dataSourceName=
# cas.authn.jdbc.encode[0].dataSourceProxy=false
# Hibernate-specific properties (i.e. `hibernate.globally_quoted_identifiers`)
# cas.authn.jdbc.encode[0].properties.propertyName=propertyValue

# cas.authn.jdbc.encode[0].passwordEncoder.type=NONE|DEFAULT|STANDARD|BCRYPT|SCRYPT|PBKDF2|com.example.CustomPasswordEncoder
# cas.authn.jdbc.encode[0].passwordEncoder.characterEncoding=
# cas.authn.jdbc.encode[0].passwordEncoder.encodingAlgorithm=
# cas.authn.jdbc.encode[0].passwordEncoder.secret=
# cas.authn.jdbc.encode[0].passwordEncoder.strength=16

# cas.authn.jdbc.encode[0].principalTransformation.pattern=(.+)@example.org
# cas.authn.jdbc.encode[0].principalTransformation.groovy.location=file:///etc/cas/config/principal.groovy
# cas.authn.jdbc.encode[0].principalTransformation.suffix=
# cas.authn.jdbc.encode[0].principalTransformation.caseConversion=NONE|UPPERCASE|LOWERCASE
# cas.authn.jdbc.encode[0].principalTransformation.prefix=
```

## MongoDb Authentication

To learn more about this topic, [please review this guide](MongoDb-Authentication.html).

```properties
# cas.authn.mongo.mongoHostUri=mongodb://uri
# cas.authn.mongo.usernameAttribute=username
# cas.authn.mongo.attributes=
# cas.authn.mongo.passwordAttribute=password
# cas.authn.mongo.collectionName=users
# cas.authn.mongo.name=

# cas.authn.mongo.principalTransformation.pattern=(.+)@example.org
# cas.authn.mongo.principalTransformation.groovy.location=file:///etc/cas/config/principal.groovy
# cas.authn.mongo.principalTransformation.suffix=
# cas.authn.mongo.principalTransformation.caseConversion=NONE|UPPERCASE|LOWERCASE
# cas.authn.mongo.principalTransformation.prefix=

# cas.authn.mongo.passwordEncoder.type=NONE|DEFAULT|STANDARD|BCRYPT|SCRYPT|PBKDF2|com.example.CustomPasswordEncoder
# cas.authn.mongo.passwordEncoder.characterEncoding=
# cas.authn.mongo.passwordEncoder.encodingAlgorithm=
# cas.authn.mongo.passwordEncoder.secret=
# cas.authn.mongo.passwordEncoder.strength=16
```

## LDAP Authentication

CAS authenticates a username/password against an LDAP directory such as Active Directory or OpenLDAP.
There are numerous directory architectures and we provide configuration for four common cases.

Note that CAS will automatically create the appropriate components internally
based on the settings specified below. If you wish to authenticate against more than one LDAP
server, simply increment the index and specify the settings for the next LDAP server.

**Note:** Failure to specify adequate properties such as `type`, `ldapUrl`, etc
will simply deactivate LDAP authentication altogether silently.

**Note:** Attributes retrieved as part of LDAP authentication are merged with all attributes
retrieved from [other attribute repository sources](#authentication-attributes), if any.
Attributes retrieved directly as part of LDAP authentication trump all other attributes.

To learn more about this topic, [please review this guide](LDAP-Authentication.html).

The following authentication types are supported:


| Type                    | Description                            
|-------------------------|----------------------------------------------------------------------------------------------------
| `AD`                    | Acive Directory - Users authenticate with `sAMAccountName` typically using a DN format.     
| `AUTHENTICATED`         | Manager bind/search type of authentication. If `principalAttributePassword` is empty then a user simple bind is done to validate credentials. Otherwise the given attribute is compared with the given `principalAttributePassword` using the `SHA` encrypted value of it.
| `DIRECT`                | Compute user DN from a format string and perform simple bind. This is relevant when no search is required to compute the DN needed for a bind operation. This option is useful when all users are under a single branch in the directory, e.g. `ou=Users,dc=example,dc=org`, or the username provided on the CAS login form is part of the DN, e.g. `uid=%s,ou=Users,dc=exmaple,dc=org`
| `ANONYMOUS`             | Similar semantics as `AUTHENTICATED` except no `bindDn` and `bindCredential` may be specified to initialize the connection. If `principalAttributePassword` is empty then a user simple bind is done to validate credentials. Otherwise the given attribute is compared with the given `principalAttributePassword` using the `SHA` encrypted value of it.

### Connection Strategies

If multiple URLs are provided as the LDAP url, this describes how each URL will be processed.

| Provider              | Description              
|-----------------------|-----------------------------------------------------------------------------------------------
| `DEFAULT`             | The default JNDI provider behavior will be used.    
| `ACTIVE_PASSIVE`      | First LDAP will be used for every request unless it fails and then the next shall be used.    
| `ROUND_ROBIN`         | For each new connection the next url in the list will be used.      
| `RANDOM`              | For each new connection a random LDAP url will be selected.
| `DNS_SRV`             | LDAP urls based on DNS SRV records of the configured/given LDAP url will be used.  

### Connection Initialization

LDAP connection configuration injected into the LDAP connection pool can be initialized with the following parameters:

| Behavior                               | Description              
|----------------------------------------|-------------------------------------------------------------------
| `bindDn`/`bindCredential` provided     | Use the provided credentials to bind when initializing connections.
| `bindDn`/`bindCredential` set to `*`   | Use a fast-bind strategy to initialize the pool.   
| `bindDn`/`bindCredential` set to blank | Skip connection initializing; perform operations anonymously.
| SASL mechanism provided                | Use the given SASL mechanism to bind when initializing connections.


### Validators

The following LDAP validators can be used to test connection health status:

| Type                    | Description
|-------------------------|----------------------------------------------------------------------------------------------------
| `NONE`                  | No validation takes place.
| `SEARCH`                | Validates a connection is healthy by performing a search operation. Validation is considered successful if the search result size is greater than zero.
| `COMPARE`               | Validates a connection is healthy by performing a compare operation.

### Passivators

The following options can be used to passivate objects when they are checked back into the LDAP connection pool:

| Type                    | Description
|-------------------------|----------------------------------------------------------------------------------------------------
| `NONE`                  | No passivation takes place.
| `CLOSE`                 | Passivates a connection by attempting to close it.
| `BIND`                  | The default behavior which passivates a connection by performing a bind operation on it. This option requires the availability of bind credentials when establishing connections to LDAP.

#### Why Passivators?

You may receive unexpected LDAP failures, when CAS is configured to authenticate using `DIRECT` or `AUTHENTICATED` types and LDAP is locked down to not allow anonymous binds/searches. Every second attempt with a given LDAP connection from the pool would fail if it was on the same connection as a failed login attempt, and the regular connection validator would similarly fail. When a connection is returned back to a pool, it still may contain the principal and credentials from the previous attempt. Before the next bind attempt using that connection, the validator tries to validate the connection again but fails because it's no longer trying with the configured bind credentials but with whatever user DN was used in the previous step. Given the validation failure, the connection is closed and CAS would deny access by default. Passivators attempt to reconnect to LDAP with the configured bind credentials, effectively resetting the connection to what it should be after each bind request.

Furthermore if you are seeing errors in the logs that resemble a *<Operation exception encountered, reopening connection>* type of message, this usually is an indication that the connection pool's validation timeout established and created by CAS is greater than the timeout configured in the LDAP server, or more likely, in the load balancer in front of the LDAP servers. You can adjust the LDAP server session's timeout for connections, or you can teach CAS to use a validatity period that is equal or less than the LDAP server session's timeout.

```properties
# cas.authn.ldap[0].type=AD|AUTHENTICATED|DIRECT|ANONYMOUS

# cas.authn.ldap[0].ldapUrl=ldaps://ldap1.example.edu ldaps://ldap2.example.edu
# cas.authn.ldap[0].connectionStrategy=
# cas.authn.ldap[0].useSsl=true
# cas.authn.ldap[0].useStartTls=false
# cas.authn.ldap[0].connectTimeout=5000
# cas.authn.ldap[0].subtreeSearch=true

# BaseDn used to start the LDAP search looking for accounts
# cas.authn.ldap[0].baseDn=dc=example,dc=org

# The search filter to use while looking for accounts.
# cas.authn.ldap[0].userFilter=cn={user}
#
# Bind credentials used to connect to the LDAP instance
#
# cas.authn.ldap[0].bindDn=cn=Directory Manager,dc=example,dc=org
# cas.authn.ldap[0].bindCredential=Password

# cas.authn.ldap[0].enhanceWithEntryResolver=true
# cas.authn.ldap[0].dnFormat=uid=%s,ou=people,dc=example,dc=org
# cas.authn.ldap[0].principalAttributeId=uid
# cas.authn.ldap[0].principalAttributePassword=password

#
# Define attributes to be retrieved from LDAP as part of the same authentication transaction
# The left-hand size notes the source while the right-hand size indicate an optional renaming/remapping
# of the attribute definition. The same attribute name is allowed to be mapped multiple times to
# different attribute names.
#
# cas.authn.ldap[0].principalAttributeList=sn,cn:commonName,givenName,eduPersonTargettedId:SOME_IDENTIFIER

# cas.authn.ldap[0].collectDnAttribute=false
# cas.authn.ldap[0].principalDnAttributeName=principalLdapDn
# cas.authn.ldap[0].allowMultiplePrincipalAttributeValues=true
# cas.authn.ldap[0].allowMissingPrincipalAttributeValue=true
# cas.authn.ldap[0].credentialCriteria=
```

### LDAP SSL

```properties
# cas.authn.ldap[0].saslMechanism=GSSAPI|DIGEST_MD5|CRAM_MD5|EXTERNAL
# cas.authn.ldap[0].saslRealm=EXAMPLE.COM
# cas.authn.ldap[0].saslAuthorizationId=
# cas.authn.ldap[0].saslMutualAuth=
# cas.authn.ldap[0].saslQualityOfProtection=
# cas.authn.ldap[0].saslSecurityStrength=

# cas.authn.ldap[0].trustCertificates=
# cas.authn.ldap[0].keystore=
# cas.authn.ldap[0].keystorePassword=
# cas.authn.ldap[0].keystoreType=JKS|JCEKS|PKCS12
```

### LDAP Pooling

```properties
# cas.authn.ldap[0].poolPassivator=NONE|CLOSE|BIND
# cas.authn.ldap[0].minPoolSize=3
# cas.authn.ldap[0].maxPoolSize=10
# cas.authn.ldap[0].validateOnCheckout=true
# cas.authn.ldap[0].validatePeriodically=true
# cas.authn.ldap[0].validatePeriod=600
# cas.authn.ldap[0].validateTimeout=5000

# cas.authn.ldap[0].failFast=true
# cas.authn.ldap[0].idleTime=5000
# cas.authn.ldap[0].prunePeriod=5000
# cas.authn.ldap[0].blockWaitTime=5000
```

### LDAP Search Entry Handlers

```properties
# cas.authn.ldap[0].providerClass=org.ldaptive.provider.unboundid.UnboundIDProvider
# cas.authn.ldap[0].allowMultipleDns=false

# cas.authn.ldap[0].searchEntryHandlers[0].type=CASE_CHANGE|DN_ATTRIBUTE_ENTRY|MERGE| \
#                                               OBJECT_GUID|OBJECT_SID|PRIMARY_GROUP| \
#                                               RANGE_ENTRY|RECURSIVE_ENTRY

# cas.authn.ldap[0].searchEntryHandlers[0].caseChange.dnCaseChange=NONE|LOWER|UPPER
# cas.authn.ldap[0].searchEntryHandlers[0].caseChange.attributeNameCaseChange=NONE|LOWER|UPPER
# cas.authn.ldap[0].searchEntryHandlers[0].caseChange.attributeValueCaseChange=NONE|LOWER|UPPER
# cas.authn.ldap[0].searchEntryHandlers[0].caseChange.attributeNames=

# cas.authn.ldap[0].searchEntryHandlers[0].dnAttribute.dnAttributeName=entryDN
# cas.authn.ldap[0].searchEntryHandlers[0].dnAttribute.addIfExists=false

# cas.authn.ldap[0].searchEntryHandlers[0].primaryGroupId.groupFilter=(&(objectClass=group)(objectSid={0}))
# cas.authn.ldap[0].searchEntryHandlers[0].primaryGroupId.baseDn=

# cas.authn.ldap[0].searchEntryHandlers[0].mergeAttribute.mergeAttributeName=
# cas.authn.ldap[0].searchEntryHandlers[0].mergeAttribute.attribueNames=

# cas.authn.ldap[0].searchEntryHandlers[0].recursive.searchAttribute=
# cas.authn.ldap[0].searchEntryHandlers[0].recursive.mergeAttributes=

# cas.authn.ldap[0].name=
# cas.authn.ldap[0].order=0
```

### LDAP Password Encoding & Principal Transformation

```properties
# cas.authn.ldap[0].passwordEncoder.type=NONE|DEFAULT|STANDARD|BCRYPT|SCRYPT|PBKDF2|com.example.CustomPasswordEncoder
# cas.authn.ldap[0].passwordEncoder.characterEncoding=
# cas.authn.ldap[0].passwordEncoder.encodingAlgorithm=
# cas.authn.ldap[0].passwordEncoder.secret=
# cas.authn.ldap[0].passwordEncoder.strength=16

# cas.authn.ldap[0].principalTransformation.pattern=(.+)@example.org
# cas.authn.ldap[0].principalTransformation.groovy.location=file:///etc/cas/config/principal.groovy
# cas.authn.ldap[0].principalTransformation.suffix=
# cas.authn.ldap[0].principalTransformation.caseConversion=NONE|UPPERCASE|LOWERCASE
# cas.authn.ldap[0].principalTransformation.prefix=
```

### LDAP Connection Validators

```properties
# cas.authn.ldap[0].validator.type=NONE|SEARCH|COMPARE
# cas.authn.ldap[0].validator.baseDn=
# cas.authn.ldap[0].validator.searchFilter=(objectClass=*)
# cas.authn.ldap[0].validator.scope=OBJECT|ONELEVEL|SUBTREE
# cas.authn.ldap[0].validator.attributeName=objectClass
# cas.authn.ldap[0].validator.attributeValues=top
# cas.authn.ldap[0].validator.dn=
```

### LDAP Password Policy

```properties
# cas.authn.ldap[0].passwordPolicy.type=GENERIC|AD|FreeIPA|EDirectory

# cas.authn.ldap[0].passwordPolicy.enabled=true
# cas.authn.ldap[0].passwordPolicy.policyAttributes.accountLocked=javax.security.auth.login.AccountLockedException
# cas.authn.ldap[0].passwordPolicy.loginFailures=5
# cas.authn.ldap[0].passwordPolicy.warningAttributeValue=
# cas.authn.ldap[0].passwordPolicy.warningAttributeName=
# cas.authn.ldap[0].passwordPolicy.displayWarningOnMatch=true
# cas.authn.ldap[0].passwordPolicy.warnAll=true
# cas.authn.ldap[0].passwordPolicy.warningDays=30

# An implementation of `org.ldaptive.auth.AuthenticationResponseHandler`
# cas.authn.ldap[0].passwordPolicy.customPolicyClass=com.example.MyAuthenticationResponseHandler

# cas.authn.ldap[0].passwordPolicy.strategy=DEFAULT|GROOVY|REJECT_RESULT_CODE
# cas.authn.ldap[0].passwordPolicy.groovy.location=file:/etc/cas/config/password-policy.groovy
```

#### Password Policy Strategies

Password policy strategy types are outlined below. The strategy evaluates the authentication response received from LDAP and is allowed to review it upfront in order to further examine whether account state, messages and warnings is eligible for further investigation.

| Option        | Description
|---------------|-----------------------------------------------------------------------------
| `DEFAULT`     | Accepts the auhentication response as is, and processes account state, if any.
| `GROOVY`      | Examine the authentication response as part of a Groovy script dynamically. The responsibility of handling account state changes and warnings is entirely delegated to the script.
| `REJECT_RESULT_CODE`  | An extension of the `DEFAULT` where account state is processed only if the result code of the authentication response is not blacklisted in the configuration. By default `INVALID_CREDENTIALS(49)` prevents CAS from handling account states.

If the password policy strategy is to be handed off to a Groovy script, the outline of the script may be as follows:

```groovy
import java.util.*
import org.ldaptive.auth.*
import org.apereo.cas.*
import org.apereo.cas.authentication.*
import org.apereo.cas.authentication.support.*

def List<MessageDescriptor> run(final Object... args) {
    def response = args[0]
    def configuration = args[1];
    def logger = args[2]

    logger.info("Handling password policy [{}] via ${configuration.getAccountStateHandler()}", response)

    def accountStateHandler = configuration.getAccountStateHandler()
    return accountStateHandler.handle(response, configuration)
}
```

The parameters passed are as follows:

| Parameter             | Description
|-----------------------|-----------------------------------------------------------------------------------
| `response`            | The LDAP authentication response of type `org.ldaptive.auth.AuthenticationResponse`
| `configuration`       | The LDAP password policy configuration carrying the account state handler defined.
| `logger`              | The object responsible for issuing log messages such as `logger.info(...)`.


## REST Authentication

This allows the CAS server to reach to a remote REST endpoint via a `POST`.
To learn more about this topic, [please review this guide](Rest-Authentication.html).

```properties
# cas.authn.rest.uri=https://...
# cas.authn.rest.name=

# cas.authn.rest.passwordEncoder.type=NONE|DEFAULT|STANDARD|BCRYPT|SCRYPT|PBKDF2|com.example.CustomPasswordEncoder
# cas.authn.rest.passwordEncoder.characterEncoding=
# cas.authn.rest.passwordEncoder.encodingAlgorithm=
# cas.authn.rest.passwordEncoder.secret=
# cas.authn.rest.passwordEncoder.strength=16
```

## Google Apps Authentication

Authenticate via CAS into Google Apps services and applications.
To learn more about this topic, [please review this guide](../integration/Google-Apps-Integration.html).

```properties
# cas.googleApps.publicKeyLocation=file:/etc/cas/public.key
# cas.googleApps.keyAlgorithm=RSA
# cas.googleApps.privateKeyLocation=file:/etc/cas/private.key
```

## OpenID Authentication

Allow CAS to become an OpenID authentication provider.
To learn more about this topic, [please review this guide](../protocol/OpenID-Protocol.html).

```properties
# cas.authn.openid.enforceRpId=false
# cas.authn.openid.principal.principalAttribute=
# cas.authn.openid.principal.returnNull=false
# cas.authn.openid.name=
```

## SPNEGO Authentication

To learn more about this topic, [please review this guide](SPNEGO-Authentication.html).

```properties
# cas.authn.spnego.kerberosConf=
# cas.authn.spnego.mixedModeAuthentication=false
# cas.authn.spnego.cachePolicy=600
# cas.authn.spnego.timeout=300000
# cas.authn.spnego.jcifsServicePrincipal=HTTP/cas.example.com@EXAMPLE.COM
# cas.authn.spnego.jcifsNetbiosWins=
# cas.authn.spnego.loginConf=
# cas.authn.spnego.ntlmAllowed=true
# cas.authn.spnego.hostNamePatternString=.+
# cas.authn.spnego.jcifsUsername=
# cas.authn.spnego.useSubjectCredsOnly=false
# cas.authn.spnego.supportedBrowsers=MSIE,Trident,Firefox,AppleWebKit
# cas.authn.spnego.jcifsDomainController=
# cas.authn.spnego.dnsTimeout=2000
# cas.authn.spnego.hostNameClientActionStrategy=hostnameSpnegoClientAction
# cas.authn.spnego.kerberosKdc=172.10.1.10
# cas.authn.spnego.alternativeRemoteHostAttribute=alternateRemoteHeader
# cas.authn.spnego.jcifsDomain=
# cas.authn.spnego.ipsToCheckPattern=127.+
# cas.authn.spnego.kerberosDebug=true
# cas.authn.spnego.send401OnAuthenticationFailure=true
# cas.authn.spnego.kerberosRealm=EXAMPLE.COM
# cas.authn.spnego.ntlm=false
# cas.authn.spnego.principalWithDomainName=false
# cas.authn.spnego.jcifsServicePassword=
# cas.authn.spnego.jcifsPassword=
# cas.authn.spnego.spnegoAttributeName=distinguishedName
# cas.authn.spnego.name=

# cas.authn.spnego.principal.principalAttribute=
# cas.authn.spnego.principal.returnNull=false

# cas.authn.spnego.ldap.ldapUrl=ldaps://ldap1.example.edu ldaps://ldap2.example.edu
# cas.authn.spnego.ldap.connectionStrategy=
# cas.authn.spnego.ldap.baseDn=dc=example,dc=org
# cas.authn.spnego.ldap.bindDn=cn=Directory Manager,dc=example,dc=org
# cas.authn.spnego.ldap.bindCredential=Password
# cas.authn.spnego.ldap.providerClass=org.ldaptive.provider.unboundid.UnboundIDProvider
# cas.authn.spnego.ldap.connectTimeout=5000
# cas.authn.spnego.ldap.trustCertificates=
# cas.authn.spnego.ldap.keystore=
# cas.authn.spnego.ldap.keystorePassword=
# cas.authn.spnego.ldap.keystoreType=JKS|JCEKS|PKCS12
# cas.authn.spnego.ldap.poolPassivator=NONE|CLOSE|BIND
# cas.authn.spnego.ldap.minPoolSize=3
# cas.authn.spnego.ldap.maxPoolSize=10
# cas.authn.spnego.ldap.validateOnCheckout=true
# cas.authn.spnego.ldap.validatePeriodically=true
# cas.authn.spnego.ldap.validatePeriod=600
# cas.authn.spnego.ldap.validateTimeout=5000
# cas.authn.spnego.ldap.failFast=true
# cas.authn.spnego.ldap.idleTime=500
# cas.authn.spnego.ldap.prunePeriod=600
# cas.authn.spnego.ldap.blockWaitTime=5000
# cas.authn.spnego.ldap.subtreeSearch=true
# cas.authn.spnego.ldap.useSsl=true
# cas.authn.spnego.ldap.useStartTls=false
# cas.authn.spnego.ldap.searchFilter=host={host}

# cas.authn.spnego.ldap.validator.type=NONE|SEARCH|COMPARE
# cas.authn.spnego.ldap.validator.searchFilter=(objectClass=*)
# cas.authn.spnego.ldap.validator.scope=OBJECT|ONELEVEL|SUBTREE
# cas.authn.spnego.ldap.validator.attributeName=objectClass
# cas.authn.spnego.ldap.validator.attributeValues=top
# cas.authn.spnego.ldap.validator.dn=
```

### NTLM Authentication

```properties
# cas.authn.ntlm.includePattern=
# cas.authn.ntlm.loadBalance=true
# cas.authn.ntlm.domainController=
# cas.authn.ntlm.name=
```

## JAAS Authentication

To learn more about this topic, [please review this guide](JAAS-Authentication.html).

```properties
# cas.authn.jaas[0].realm=CAS
# cas.authn.jaas[0].kerberosKdcSystemProperty=
# cas.authn.jaas[0].kerberosRealmSystemProperty=
# cas.authn.jaas[0].name=
# cas.authn.jaas[0].credentialCriteria=

# cas.authn.jaas[0].passwordEncoder.type=NONE|DEFAULT|STANDARD|BCRYPT|SCRYPT|PBKDF2|com.example.CustomPasswordEncoder
# cas.authn.jaas[0].passwordEncoder.characterEncoding=
# cas.authn.jaas[0].passwordEncoder.encodingAlgorithm=
# cas.authn.jaas[0].passwordEncoder.secret=
# cas.authn.jaas[0].passwordEncoder.strength=16

# cas.authn.jaas[0].principalTransformation.pattern=(.+)@example.org
# cas.authn.jaas[0].principalTransformation.groovy.location=file:///etc/cas/config/principal.groovy
# cas.authn.jaas[0].principalTransformation.suffix=
# cas.authn.jaas[0].principalTransformation.caseConversion=NONE|UPPERCASE|LOWERCASE
# cas.authn.jaas[0].principalTransformation.prefix=
```

## GUA Authentication

To learn more about this topic, [please review this guide](GUA-Authentication.html).

### LDAP Repository

```properties
# cas.authn.gua.ldap.imageAttribute=userImageIdentifier

# cas.authn.gua.ldap.ldapUrl=ldaps://ldap1.example.edu ldaps://ldap2.example.edu
# cas.authn.gua.ldap.connectionStrategy=
# cas.authn.gua.ldap.baseDn=dc=example,dc=org
# cas.authn.gua.ldap.userFilter=cn={user}
# cas.authn.gua.ldap.bindDn=cn=Directory Manager,dc=example,dc=org
# cas.authn.gua.ldap.bindCredential=Password
# cas.authn.gua.ldap.providerClass=org.ldaptive.provider.unboundid.UnboundIDProvider
# cas.authn.gua.ldap.connectTimeout=5000
# cas.authn.gua.ldap.trustCertificates=
# cas.authn.gua.ldap.keystore=
# cas.authn.gua.ldap.keystorePassword=
# cas.authn.gua.ldap.keystoreType=JKS|JCEKS|PKCS12
# cas.authn.gua.ldap.poolPassivator=NONE|CLOSE|BIND
# cas.authn.gua.ldap.minPoolSize=3
# cas.authn.gua.ldap.maxPoolSize=10
# cas.authn.gua.ldap.validateOnCheckout=true
# cas.authn.gua.ldap.validatePeriodically=true
# cas.authn.gua.ldap.validatePeriod=600
# cas.authn.gua.ldap.validateTimeout=5000
# cas.authn.gua.ldap.failFast=true
# cas.authn.gua.ldap.idleTime=500
# cas.authn.gua.ldap.prunePeriod=600
# cas.authn.gua.ldap.blockWaitTime=5000
# cas.authn.gua.ldap.useSsl=true
# cas.authn.gua.ldap.useStartTls=false

# cas.authn.gua.ldap.validator.type=NONE|SEARCH|COMPARE
# cas.authn.gua.ldap.validator.baseDn=
# cas.authn.gua.ldap.validator.searchFilter=(objectClass=*)
# cas.authn.gua.ldap.validator.scope=OBJECT|ONELEVEL|SUBTREE
# cas.authn.gua.ldap.validator.attributeName=objectClass
# cas.authn.gua.ldap.validator.attributeValues=top
# cas.authn.gua.ldap.validator.dn=
```

### Static Resource Repository

```properties
# cas.authn.gua.resource.location=file:/path/to/image.jpg
```

## JWT/Token Authentication

To learn more about this topic, [please review this guide](JWT-Authentication.html).

```properties
# cas.authn.token.name=

# cas.authn.token.principalTransformation.pattern=(.+)@example.org
# cas.authn.token.principalTransformation.groovy.location=file:///etc/cas/config/principal.groovy
# cas.authn.token.principalTransformation.suffix=
# cas.authn.token.principalTransformation.caseConversion=NONE|UPPERCASE|LOWERCASE
# cas.authn.token.principalTransformation.prefix=
```

### JWT Tickets

Allow CAS tickets through various protocol channels to be created as JWTs. See [this guide](Configure-ServiceTicket-JWT.html) or [this guide](../protocol/REST-Protocol.html) for more info.

```properties
# cas.authn.token.crypto.enabled=true
# cas.authn.token.crypto.encryptionEnabled=true
# cas.authn.token.crypto.signing.key=
# cas.authn.token.crypto.signing.keySize=512
# cas.authn.token.crypto.encryption.key=
# cas.authn.token.crypto.encryption.keySize=256
# cas.authn.token.crypto.alg=AES
```

The encryption key must be randomly-generated string whose length is defined by the encryption key size setting.
The signing key [is a JWK](Configuration-Properties-Common.html#signing--encryption) whose length is defined by the signing key size setting.

## Couchbase Authentication

To learn more about this topic, [please review this guide](Couchbase-Authentication.html).

```properties

# cas.authn.couchbase.nodeSet=localhost:8091
# cas.authn.couchbase.password=
# cas.authn.couchbase.bucket=default
# cas.authn.couchbase.timeout=5000
# cas.authn.couchbase.queryEnabled=true
# cas.authn.couchbase.usernameAttribute=username
# cas.authn.couchbase.passwordAttribute=psw

# cas.authn.couchbase.name=
# cas.authn.couchbase.order=

# cas.authn.couchbase.passwordEncoder.type=NONE|DEFAULT|STANDARD|BCRYPT|SCRYPT|PBKDF2|com.example.CustomPasswordEncoder
# cas.authn.couchbase.passwordEncoder.characterEncoding=
# cas.authn.couchbase.passwordEncoder.encodingAlgorithm=
# cas.authn.couchbase.passwordEncoder.secret=
# cas.authn.couchbase.passwordEncoder.strength=16

# cas.authn.couchbase.principalTransformation.pattern=(.+)@example.org
# cas.authn.couchbase.principalTransformation.groovy.location=file:///etc/cas/config/principal.groovy
# cas.authn.couchbase.principalTransformation.suffix=
# cas.authn.couchbase.principalTransformation.caseConversion=NONE|UPPERCASE|LOWERCASE
# cas.authn.couchbase.principalTransformation.prefix=
```

## Amazon Cloud Directory Authentication

To learn more about this topic, [please review this guide](AWS-CloudDirectory-Authentication.html).

```properties
# cas.authn.cloudDirectory.credentialAccessKey=
# cas.authn.cloudDirectory.credentialSecretKey=

# cas.authn.cloudDirectory.region=

# cas.authn.cloudDirectory.profileName=
# cas.authn.cloudDirectory.profilePath=

# cas.authn.cloudDirectory.directoryArn=
# cas.authn.cloudDirectory.schemaArn=
# cas.authn.cloudDirectory.facetName=

# cas.authn.cloudDirectory.usernameAttributeName=
# cas.authn.cloudDirectory.passwordAttributeName=
# cas.authn.cloudDirectory.usernameIndexPath=

# cas.authn.cloudDirectory.name=
# cas.authn.cloudDirectory.order=

# cas.authn.cloudDirectory.passwordEncoder.type=NONE|DEFAULT|STANDARD|BCRYPT|SCRYPT|PBKDF2|com.example.CustomPasswordEncoder
# cas.authn.cloudDirectory.passwordEncoder.characterEncoding=
# cas.authn.cloudDirectory.passwordEncoder.encodingAlgorithm=
# cas.authn.cloudDirectory.passwordEncoder.secret=
# cas.authn.cloudDirectory.passwordEncoder.strength=16

# cas.authn.cloudDirectory.principalTransformation.pattern=(.+)@example.org
# cas.authn.cloudDirectory.principalTransformation.groovy.location=file:///etc/cas/config/principal.groovy
# cas.authn.cloudDirectory.principalTransformation.suffix=
# cas.authn.cloudDirectory.principalTransformation.caseConversion=NONE|UPPERCASE|LOWERCASE
# cas.authn.cloudDirectory.principalTransformation.prefix=
```

## Remote Address Authentication

To learn more about this topic, [please review this guide](Remote-Address-Authentication.html).

```properties
# cas.authn.remoteAddress.ipAddressRange=
# cas.authn.remoteAddress.name=
```


## Accept Users Authentication

<div class="alert alert-warning"><strong>Default Credentials</strong><p>To test the default authentication scheme in CAS,
use <strong>casuser</strong> and <strong>Mellon</strong> as the username and password respectively. These are automatically
configured via the static authentication handler, and <strong>MUST</strong> be removed from the configuration
prior to production rollouts.</p></div>

```properties
# cas.authn.accept.users=
# cas.authn.accept.name=
# cas.authn.accept.credentialCriteria=

# cas.authn.accept.passwordEncoder.type=NONE|DEFAULT|STANDARD|BCRYPT|SCRYPT|PBKDF2
# cas.authn.accept.passwordEncoder.characterEncoding=
# cas.authn.accept.passwordEncoder.encodingAlgorithm=
# cas.authn.accept.passwordEncoder.secret=
# cas.authn.accept.passwordEncoder.strength=16

# cas.authn.accept.principalTransformation.pattern=(.+)@example.org
# cas.authn.accept.principalTransformation.groovy.location=file:///etc/cas/config/principal.groovy
# cas.authn.accept.principalTransformation.suffix=
# cas.authn.accept.principalTransformation.caseConversion=NONE|UPPERCASE|LOWERCASE
# cas.authn.accept.principalTransformation.prefix=
```

## X509 Authentication

To learn more about this topic, [please review this guide](X509-Authentication.html).

### Principal Resolution

X.509 principal resolution can act on the following principal types:

| Type                    | Description                            
|-------------------------|----------------------------------------------------------------------
| `SERIAL_NO`             | Resolve the principal by the serial number with a configurable <strong>radix</strong>, ranging from 2 to 36. If <code>radix</code> is <code>16</code>, then the serial number could be filled with leading zeros to even the number of digits.
| `SERIAL_NO_DN`          | Resolve the principal by serial number and issuer dn.
| `SUBJECT`               | Resolve the principal by extracting one or more attribute values from the certificate subject DN and combining them with intervening delimiters.
| `SUBJECT_ALT_NAME`      | Resolve the principal by the subject alternative name extension.
| `SUBJECT_DN`            | The default type; Resolve the principal by the certificate's subject dn.
| `CN_EDIPI`              | Resolve the principal by the Electronic Data Interchange Personal Identifier (EDIPI) from the Common Name.

### CRL Fetching / Revocation

CAS provides a flexible policy engine for certificate revocation checking. This facility arose due to lack of configurability
in the revocation machinery built into the JSSE.

Available policies cover the following events:

- CRL Expiration
- CRL Unavailability

In either event, the following options are available:

| Type                    | Description                            
|-------------------------|----------------------------------------------------------------------------------------------------
| `ALLOW`                 | Allow authentication to proceed.
| `DENY`                  | Deny authentication and block.
| `THRESHOLD`             | Applicable to CRL expiration, throttle the request whereby expired data is permitted up to a threshold period of time but not afterward.


Revocation certificate checking can be carried out in one of the following ways:

| Type                    | Description                            
|-------------------------|----------------------------------------------------------------------------------------------------
| `NONE`                  | No revocation is performed.
| `CRL`                   | The CRL URI(s) mentioned in the certificate `cRLDistributionPoints` extension field. Caches are available to prevent excessive IO against CRL endpoints; CRL data is fetched if does not exist in the cache or if it is expired.
| `RESOURCE`              | A CRL hosted at a fixed location. The CRL is fetched at periodic intervals and cached.


To fetch CRLs, the following options are available:

| Type                    | Description                            
|-------------------------|----------------------------------------------------------------------------------------------------
| `RESOURCE`              | By default, all revocation checks use fixed resources to fetch the CRL resource from the specified location.
| `LDAP`                  | A CRL resource may be fetched from a pre-configured attribute, in the event that the CRL resource location is an LDAP URI


```properties
# cas.authn.x509.crlExpiredPolicy=DENY|ALLOW|THRESHOLD
# cas.authn.x509.crlUnavailablePolicy=DENY|ALLOW|THRESHOLD
# cas.authn.x509.crlResourceExpiredPolicy=DENY|ALLOW|THRESHOLD
# cas.authn.x509.crlResourceUnavailablePolicy=DENY|ALLOW|THRESHOLD

# cas.authn.x509.revocationChecker=NONE|CRL|RESOURCE
# cas.authn.x509.crlFetcher=RESOURCE|LDAP

# cas.authn.x509.crlResources[0]=file:/...

# cas.authn.x509.cacheMaxElementsInMemory=1000
# cas.authn.x509.cacheDiskOverflow=false
# cas.authn.x509.cacheEternal=false
# cas.authn.x509.cacheTimeToLiveSeconds=7200
# cas.authn.x509.cacheTimeToIdleSeconds=1800

# cas.authn.x509.checkKeyUsage=false
# cas.authn.x509.revocationPolicyThreshold=172800

# cas.authn.x509.regExSubjectDnPattern=.+
# cas.authn.x509.regExTrustedIssuerDnPattern=.+

# cas.authn.x509.name=
# cas.authn.x509.principalDescriptor=
# cas.authn.x509.principalSNRadix=10
# cas.authn.x509.principalHexSNZeroPadding=false
# cas.authn.x509.maxPathLength=1
# cas.authn.x509.throwOnFetchFailure=false
# cas.authn.x509.valueDelimiter=,
# cas.authn.x509.checkAll=false
# cas.authn.x509.requireKeyUsage=false
# cas.authn.x509.serialNumberPrefix=SERIALNUMBER=
# cas.authn.x509.refreshIntervalSeconds=3600
# cas.authn.x509.maxPathLengthAllowUnspecified=false


# cas.authn.x509.ldap.ldapUrl=ldaps://ldap1.example.edu ldaps://ldap2.example.edu
# cas.authn.x509.ldap.connectionStrategy=
# cas.authn.x509.ldap.useSsl=true
# cas.authn.x509.ldap.useStartTls=false
# cas.authn.x509.ldap.connectTimeout=5000
# cas.authn.x509.ldap.baseDn=dc=example,dc=org
# cas.authn.x509.ldap.searchFilter=cn={user}
# cas.authn.x509.ldap.subtreeSearch=true
# cas.authn.x509.ldap.bindDn=cn=Directory Manager,dc=example,dc=org
# cas.authn.x509.ldap.bindCredential=Password
# cas.authn.x509.ldap.trustCertificates=
# cas.authn.x509.ldap.keystore=
# cas.authn.x509.ldap.keystorePassword=
# cas.authn.x509.ldap.keystoreType=JKS|JCEKS|PKCS12
# cas.authn.x509.ldap.poolPassivator=NONE|CLOSE|BIND
# cas.authn.x509.ldap.minPoolSize=3
# cas.authn.x509.ldap.maxPoolSize=10
# cas.authn.x509.ldap.validateOnCheckout=true
# cas.authn.x509.ldap.validatePeriodically=true
# cas.authn.x509.ldap.validatePeriod=600
# cas.authn.x509.ldap.validateTimeout=5000
# cas.authn.x509.ldap.failFast=true
# cas.authn.x509.ldap.idleTime=500
# cas.authn.x509.ldap.prunePeriod=600
# cas.authn.x509.ldap.blockWaitTime=5000
# cas.authn.x509.ldap.providerClass=org.ldaptive.provider.unboundid.UnboundIDProvider
# cas.authn.x509.ldap.certificateAttribute=certificateRevocationList

# cas.authn.x509.ldap.validator.type=NONE|SEARCH|COMPARE
# cas.authn.x509.ldap.validator.baseDn=
# cas.authn.x509.ldap.validator.searchFilter=(objectClass=*)
# cas.authn.x509.ldap.validator.scope=OBJECT|ONELEVEL|SUBTREE
# cas.authn.x509.ldap.validator.attributeName=objectClass
# cas.authn.x509.ldap.validator.attributeValues=top
# cas.authn.x509.ldap.validator.dn=

# cas.authn.x509.principal.principalAttribute=
# cas.authn.x509.principal.returnNull=false
# cas.authn.x509.principalType=SERIAL_NO|SERIAL_NO_DN|SUBJECT|SUBJECT_ALT_NAME|SUBJECT_DN
```

## Shiro Authentication

To learn more about this topic, [please review this guide](Shiro-Authentication.html).

```properties
# cas.authn.shiro.requiredPermissions=value1,value2,...
# cas.authn.shiro.requiredRoles=value1,value2,...
# cas.authn.shiro.location=classpath:shiro.ini
# cas.authn.shiro.name=

# cas.authn.shiro.passwordEncoder.type=NONE|DEFAULT|STANDARD|BCRYPT|SCRYPT|PBKDF2|com.example.CustomPasswordEncoder
# cas.authn.shiro.passwordEncoder.characterEncoding=
# cas.authn.shiro.passwordEncoder.encodingAlgorithm=
# cas.authn.shiro.passwordEncoder.secret=
# cas.authn.shiro.passwordEncoder.strength=16

# cas.authn.shiro.principalTransformation.pattern=(.+)@example.org
# cas.authn.shiro.principalTransformation.groovy.location=file:///etc/cas/config/principal.groovy
# cas.authn.shiro.principalTransformation.suffix=
# cas.authn.shiro.principalTransformation.caseConversion=NONE|UPPERCASE|LOWERCASE
# cas.authn.shiro.principalTransformation.prefix=
```


## Trusted Authentication

To learn more about this topic, [please review this guide](Trusted-Authentication.html).

```properties
# cas.authn.trusted.principalAttribute=
# cas.authn.trusted.returnNull=false
# cas.authn.trusted.name=

# cas.authn.trusted.remotePrincipalHeader=
```

## WS-Fed Delegated Authentication

To learn more about this topic, [please review this guide](../integration/ADFS-Integration.html).

### Attribute Types

In order to construct the final authenticated principal, CAS may be configured to use the following
strategies when collecting principal attributes:

| Type                 | Description
|----------------------|------------------------------------------------------------------------------------------------
| `CAS`                | Use attributes provided by the delegated WS-Fed instance.
| `WSFED`              | Use attributes provided by CAS' own attribute resolution mechanics and repository.
| `BOTH`               | Combine both the above options, where CAS attribute repositories take precedence over WS-Fed.

```properties
# cas.authn.wsfed[0].identityProviderUrl=https://adfs.example.org/adfs/ls/
# cas.authn.wsfed[0].identityProviderIdentifier=https://adfs.example.org/adfs/services/trust
# cas.authn.wsfed[0].relyingPartyIdentifier=urn:cas:localhost
# cas.authn.wsfed[0].attributesType=WSFED
# cas.authn.wsfed[0].signingCertificateResources=classpath:adfs-signing.crt
# cas.authn.wsfed[0].tolerance=10000
# cas.authn.wsfed[0].identityAttribute=upn
# cas.authn.wsfed[0].attributeResolverEnabled=true
# cas.authn.wsfed[0].autoRedirect=true
# cas.authn.wsfed[0].name=

# cas.authn.wsfed[0].principal.principalAttribute=
# cas.authn.wsfed[0].principal.returnNull=false

# Private/Public keypair used to decrypt assertions, if any.
# cas.authn.wsfed[0].encryptionPrivateKey=classpath:private.key
# cas.authn.wsfed[0].encryptionCertificate=classpath:certificate.crt
# cas.authn.wsfed[0].encryptionPrivateKeyPassword=NONE
```

## Multifactor Authentication

To learn more about this topic, [please review this guide](Configuring-Multifactor-Authentication.html).

```properties
# Activate MFA globally for all, regardless of other settings
# cas.authn.mfa.globalProviderId=mfa-duo

# Activate MFA globally based on authentication metadata attributes
# cas.authn.mfa.globalAuthenticationAttributeNameTriggers=memberOf,eduPersonPrimaryAffiliation
# cas.authn.mfa.globalAuthenticationAttributeValueRegex=faculty|staff

# Activate MFA globally based on principal attributes
# cas.authn.mfa.globalPrincipalAttributeNameTriggers=memberOf,eduPersonPrimaryAffiliation

# Specify the regular expression pattern to trigger multifactor when working with a single provider.
# Comment out the setting when working with multiple multifactor providers
# cas.authn.mfa.globalPrincipalAttributeValueRegex=faculty|staff

# Activate MFA globally based on principal attributes and a groovy-based predicate
# cas.authn.mfa.globalPrincipalAttributePredicate=file:/etc/cas/PredicateExample.groovy

# Activate MFA based on a custom REST API/endpoint
# cas.authn.mfa.restEndpoint=https://entity.example.org/mfa

# Activate MFA based on a Groovy script
# cas.authn.mfa.groovyScript=file:/etc/cas/mfaGroovyTrigger.groovy

# Activate MFA based on Internet2's Grouper
# cas.authn.mfa.grouperGroupField=NAME|EXTENSION|DISPLAY_NAME|DISPLAY_EXTENSION

# Activate MFA based on an optional request parameter
# cas.authn.mfa.requestParameter=authn_method

# Describe the global failure mode in case provider cannot be reached
# cas.authn.mfa.globalFailureMode=CLOSED

# Design the attribute chosen to communicate the authentication context
# cas.authn.mfa.authenticationContextAttribute=authnContextClass

# Identify the request content type for non-browser MFA requests
# cas.authn.mfa.contentType=application/cas

# Select MFA provider, if resolved more than one, via Groovy script
# cas.authn.mfa.providerSelectorGroovyScript=file:/etc/cas/mfaGroovySelector.groovy
```

### Multifactor Trusted Device/Browser

To learn more about this topic, [please review this guide](Multifactor-TrustedDevice-Authentication.html).

```properties
# cas.authn.mfa.trusted.authenticationContextAttribute=isFromTrustedMultifactorAuthentication
# cas.authn.mfa.trusted.deviceRegistrationEnabled=true
# cas.authn.mfa.trusted.expiration=30
# cas.authn.mfa.trusted.timeUnit=SECONDS|MINUTES|HOURS|DAYS

# cas.authn.mfa.trusted.crypto.encryption.key=
# cas.authn.mfa.trusted.crypto.signing.key=
# cas.authn.mfa.trusted.crypto.enabled=true
```

### Signing & Encryption

The signing and encryption keys [are both JWKs](Configuration-Properties-Common.html#signing--encryption) of size `512` and `256`.
The encryption algorithm is set to `AES_128_CBC_HMAC_SHA_256`.

### JSON Storage

```properties
# cas.authn.mfa.trusted.json.location=file:/etc/cas/config/trusted-dev.json
```

### JDBC Storage

```properties
# cas.authn.mfa.trusted.jpa.healthQuery=
# cas.authn.mfa.trusted.jpa.isolateInternalQueries=false
# cas.authn.mfa.trusted.jpa.url=jdbc:hsqldb:mem:cas-jdbc-storage
# cas.authn.mfa.trusted.jpa.failFastTimeout=1
# cas.authn.mfa.trusted.jpa.dialect=org.hibernate.dialect.HSQLDialect
# cas.authn.mfa.trusted.jpa.leakThreshold=10
# cas.authn.mfa.trusted.jpa.batchSize=1
# cas.authn.mfa.trusted.jpa.defaultCatalog=
# cas.authn.mfa.trusted.jpa.defaultSchema=
# cas.authn.mfa.trusted.jpa.user=sa
# cas.authn.mfa.trusted.jpa.ddlAuto=create-drop
# cas.authn.mfa.trusted.jpa.password=
# cas.authn.mfa.trusted.jpa.autocommit=false
# cas.authn.mfa.trusted.jpa.driverClass=org.hsqldb.jdbcDriver
# cas.authn.mfa.trusted.jpa.idleTimeout=5000
# cas.authn.mfa.trusted.jpa.dataSourceName=
# cas.authn.mfa.trusted.jpa.dataSourceProxy=false
# Hibernate-specific properties (i.e. `hibernate.globally_quoted_identifiers`)
# cas.authn.mfa.trusted.jpa.properties.propertyName=propertyValue

# cas.authn.mfa.trusted.jpa.pool.suspension=false
# cas.authn.mfa.trusted.jpa.pool.minSize=6
# cas.authn.mfa.trusted.jpa.pool.maxSize=18
# cas.authn.mfa.trusted.jpa.pool.maxWait=2000
```

### MongoDb Storage

```properties
# cas.authn.mfa.trusted.mongo.clientUri=
# cas.authn.mfa.trusted.mongo.dropCollection=false
# cas.authn.mfa.trusted.mongo.collection=MongoDbCasTrustedAuthnMfaRepository
```

### REST Storage

```properties
# cas.authn.mfa.trusted.rest.endpoint=https://api.example.org/trustedBrowser
```


### Cleaner

A cleaner process is scheduled to run in the background to clean up expired and stale tickets.
This section controls how that process should behave.

```properties
# cas.authn.mfa.trusted.cleaner.schedule.startDelay=10000
# cas.authn.mfa.trusted.cleaner.schedule.repeatInterval=60000
# cas.authn.mfa.trusted.cleaner.enabled=true
```

### Google Authenticator

To learn more about this topic, [please review this guide](GoogleAuthenticator-Authentication.html).

```properties
# cas.authn.mfa.gauth.windowSize=3
# cas.authn.mfa.gauth.issuer=
# cas.authn.mfa.gauth.codeDigits=6
# cas.authn.mfa.gauth.label=
# cas.authn.mfa.gauth.timeStepSize=30
# cas.authn.mfa.gauth.rank=0
# cas.authn.mfa.gauth.trustedDeviceEnabled=false
# cas.authn.mfa.gauth.name=

# cas.authn.mfa.gauth.cleaner.enabled=true
# cas.authn.mfa.gauth.cleaner.schedule.startDelay=20000
# cas.authn.mfa.gauth.cleaner.schedule.repeatInterval=60000


# cas.authn.mfa.gauth.bypass.type=DEFAULT|GROOVY|REST
# cas.authn.mfa.gauth.bypass.principalAttributeName=bypass|skip
# cas.authn.mfa.gauth.bypass.principalAttributeValue=true|enabled.+
# cas.authn.mfa.gauth.bypass.authenticationAttributeName=bypass|skip
# cas.authn.mfa.gauth.bypass.authenticationAttributeValue=allowed.+|enabled.+
# cas.authn.mfa.gauth.bypass.authenticationHandlerName=AcceptUsers.+
# cas.authn.mfa.gauth.bypass.authenticationMethodName=LdapAuthentication.+
# cas.authn.mfa.gauth.bypass.credentialClassType=UsernamePassword.+
# cas.authn.mfa.gauth.bypass.httpRequestRemoteAddress=127.+|example.*
# cas.authn.mfa.gauth.bypass.httpRequestHeaders=header-X-.+|header-Y-.+
```

#### Google Authenticator JSON

```properties
# cas.authn.mfa.gauth.json.location=file:/somewhere.json
```

#### Google Authenticator Rest

```properties
# cas.authn.mfa.gauth.rest.endpointUrl=https://somewhere.gauth.com
```

#### Google Authenticator MongoDb

```properties
# cas.authn.mfa.gauth.mongo.clientUri=
# cas.authn.mfa.gauth.mongo.dropCollection=false
# cas.authn.mfa.gauth.mongo.collection=MongoDbGoogleAuthenticatorRepository
# cas.authn.mfa.gauth.mongo.tokenCollection=MongoDbGoogleAuthenticatorTokenRepository
```

#### Google Authenticator JPA

```properties
# cas.authn.mfa.gauth.jpa.database.healthQuery=
# cas.authn.mfa.gauth.jpa.database.isolateInternalQueries=false
# cas.authn.mfa.gauth.jpa.database.url=jdbc:hsqldb:mem:cas-gauth
# cas.authn.mfa.gauth.jpa.database.failFastTimeout=1
# cas.authn.mfa.gauth.jpa.database.dialect=org.hibernate.dialect.HSQLDialect
# cas.authn.mfa.gauth.jpa.database.leakThreshold=10
# cas.authn.mfa.gauth.jpa.database.batchSize=1
# cas.authn.mfa.gauth.jpa.database.user=sa
# cas.authn.mfa.gauth.jpa.database.ddlAuto=create-drop
# cas.authn.mfa.gauth.jpa.database.password=
# cas.authn.mfa.gauth.jpa.database.autocommit=false
# cas.authn.mfa.gauth.jpa.database.driverClass=org.hsqldb.jdbcDriver
# cas.authn.mfa.gauth.jpa.database.idleTimeout=5000
# cas.authn.mfa.gauth.jpa.database.dataSourceName=
# cas.authn.mfa.gauth.jpa.database.dataSourceProxy=false
# Hibernate-specific properties (i.e. `hibernate.globally_quoted_identifiers`)
# cas.authn.mfa.gauth.jpa.database.properties.propertyName=propertyValue

# cas.authn.mfa.gauth.jpa.database.pool.suspension=false
# cas.authn.mfa.gauth.jpa.database.pool.minSize=6
# cas.authn.mfa.gauth.jpa.database.pool.maxSize=18
# cas.authn.mfa.gauth.jpa.database.pool.maxWait=2000
```

### YubiKey

To learn more about this topic, [please review this guide](YubiKey-Authentication.html).

```properties
# cas.authn.mfa.yubikey.clientId=
# cas.authn.mfa.yubikey.secretKey=
# cas.authn.mfa.yubikey.rank=0
# cas.authn.mfa.yubikey.apiUrls=
# cas.authn.mfa.yubikey.trustedDeviceEnabled=false
# cas.authn.mfa.yubikey.name=

# cas.authn.mfa.yubikey.bypass.type=DEFAULT|GROOVY|REST
# cas.authn.mfa.yubikey.bypass.principalAttributeName=bypass|skip
# cas.authn.mfa.yubikey.bypass.principalAttributeValue=true|enabled.+
# cas.authn.mfa.yubikey.bypass.authenticationAttributeName=bypass|skip
# cas.authn.mfa.yubikey.bypass.authenticationAttributeValue=allowed.+|enabled.+
# cas.authn.mfa.yubikey.bypass.authenticationHandlerName=AcceptUsers.+
# cas.authn.mfa.yubikey.bypass.authenticationMethodName=LdapAuthentication.+
# cas.authn.mfa.yubikey.bypass.credentialClassType=UsernamePassword.+
# cas.authn.mfa.yubikey.bypass.httpRequestRemoteAddress=127.+|example.*
# cas.authn.mfa.yubikey.bypass.httpRequestHeaders=header-X-.+|header-Y-.+
```

#### YubiKey JSON Device Store

```properties
# cas.authn.mfa.yubikey.jsonFile=file:/etc/cas/deviceRegistrations.json
```

#### YubiKey Whitelist Device Store

```properties
# cas.authn.mfa.yubikey.allowedDevices.uid1=yubikeyPublicId1
# cas.authn.mfa.yubikey.allowedDevices.uid2=yubikeyPublicId2
```

### YubiKey JPA Device Store

```properties
# cas.authn.mfa.yubikey.jpa.healthQuery=
# cas.authn.mfa.yubikey.jpa.isolateInternalQueries=false
# cas.authn.mfa.yubikey.jpa.url=jdbc:hsqldb:mem:cas-yubikeymfa
# cas.authn.mfa.yubikey.jpa.failFastTimeout=1
# cas.authn.mfa.yubikey.jpa.dialect=org.hibernate.dialect.HSQLDialect
# cas.authn.mfa.yubikey.jpa.leakThreshold=10
# cas.authn.mfa.yubikey.jpa.batchSize=1
# cas.authn.mfa.yubikey.jpa.defaultCatalog=
# cas.authn.mfa.yubikey.jpa.defaultSchema=
# cas.authn.mfa.yubikey.jpa.user=sa
# cas.authn.mfa.yubikey.jpa.ddlAuto=create-drop
# cas.authn.mfa.yubikey.jpa.password=
# cas.authn.mfa.yubikey.jpa.autocommit=false
# cas.authn.mfa.yubikey.jpa.driverClass=org.hsqldb.jdbcDriver
# cas.authn.mfa.yubikey.jpa.idleTimeout=5000
# cas.authn.mfa.yubikey.jpa.dataSourceName=
# cas.authn.mfa.yubikey.jpa.dataSourceProxy=false
# Hibernate-specific properties (i.e. `hibernate.globally_quoted_identifiers`)
# cas.authn.mfa.yubikey.jpa.properties.propertyName=propertyValue

# cas.authn.mfa.yubikey.jpa.pool.suspension=false
# cas.authn.mfa.yubikey.jpa.pool.minSize=6
# cas.authn.mfa.yubikey.jpa.pool.maxSize=18
# cas.authn.mfa.yubikey.jpa.pool.maxWait=2000
```

### YubiKey MongoDb Device Store

```properties
# cas.authn.mfa.yubikey.mongo.clientUri=
# cas.authn.mfa.yubikey.mongo.dropCollection=false
# cas.authn.mfa.yubikey.mongo.collection=MongoDbYubiKeyRepository
```

### Radius OTP

To learn more about this topic, [please review this guide](RADIUS-Authentication.html).

```properties
# cas.authn.mfa.radius.failoverOnAuthenticationFailure=false
# cas.authn.mfa.radius.failoverOnException=false
# cas.authn.mfa.radius.rank=0
# cas.authn.mfa.radius.trustedDeviceEnabled=false
# cas.authn.mfa.radius.name=

# cas.authn.mfa.radius.client.socketTimeout=0
# cas.authn.mfa.radius.client.sharedSecret=N0Sh@ar3d$ecReT
# cas.authn.mfa.radius.client.authenticationPort=1812
# cas.authn.mfa.radius.client.accountingPort=1813
# cas.authn.mfa.radius.client.inetAddress=localhost

# cas.authn.mfa.radius.server.retries=3
# cas.authn.mfa.radius.server.nasPortType=-1
# cas.authn.mfa.radius.server.protocol=EAP_MSCHAPv2
# cas.authn.mfa.radius.server.nasRealPort=-1
# cas.authn.mfa.radius.server.nasPortId=-1
# cas.authn.mfa.radius.server.nasIdentifier=-1
# cas.authn.mfa.radius.server.nasPort=-1
# cas.authn.mfa.radius.server.nasIpAddress=
# cas.authn.mfa.radius.server.nasIpv6Address=

# cas.authn.mfa.radius.bypass.type=DEFAULT|GROOVY|REST
# cas.authn.mfa.radius.bypass.principalAttributeName=bypass|skip
# cas.authn.mfa.radius.bypass.principalAttributeValue=true|enabled.+
# cas.authn.mfa.radius.bypass.authenticationAttributeName=bypass|skip
# cas.authn.mfa.radius.bypass.authenticationAttributeValue=allowed.+|enabled.+
# cas.authn.mfa.radius.bypass.authenticationHandlerName=AcceptUsers.+
# cas.authn.mfa.radius.bypass.authenticationMethodName=LdapAuthentication.+
# cas.authn.mfa.radius.bypass.credentialClassType=UsernamePassword.+
# cas.authn.mfa.radius.bypass.httpRequestRemoteAddress=127.+|example.*
# cas.authn.mfa.radius.bypass.httpRequestHeaders=header-X-.+|header-Y-.+
```

### DuoSecurity

To learn more about this topic, [please review this guide](DuoSecurity-Authentication.html).

```properties
# cas.authn.mfa.duo[0].duoSecretKey=
# cas.authn.mfa.duo[0].rank=0
# cas.authn.mfa.duo[0].duoApplicationKey=
# cas.authn.mfa.duo[0].duoIntegrationKey=
# cas.authn.mfa.duo[0].duoApiHost=
# cas.authn.mfa.duo[0].trustedDeviceEnabled=false
# cas.authn.mfa.duo[0].id=mfa-duo
# cas.authn.mfa.duo[0].registrationUrl=https://registration.example.org/duo-enrollment
# cas.authn.mfa.duo[0].name=

# cas.authn.mfa.duo[0].bypass.type=DEFAULT|GROOVY|REST
# cas.authn.mfa.duo[0].bypass.principalAttributeName=bypass|skip
# cas.authn.mfa.duo[0].bypass.principalAttributeValue=true|enabled.+
# cas.authn.mfa.duo[0].bypass.authenticationAttributeName=bypass|skip
# cas.authn.mfa.duo[0].bypass.authenticationAttributeValue=allowed.+|enabled.+
# cas.authn.mfa.duo[0].bypass.authenticationHandlerName=AcceptUsers.+
# cas.authn.mfa.duo[0].bypass.authenticationMethodName=LdapAuthentication.+
# cas.authn.mfa.duo[0].bypass.credentialClassType=UsernamePassword.+
# cas.authn.mfa.duo[0].bypass.httpRequestRemoteAddress=127.+|example.*
# cas.authn.mfa.duo[0].bypass.httpRequestHeaders=header-X-.+|header-Y-.+
```

The `duoApplicationKey` is a string, at least 40 characters long, that you generate and keep secret from Duo.
You can generate a random string in Python with:

```python
import os, hashlib
print hashlib.sha1(os.urandom(32)).hexdigest()
```

### FIDO U2F

To learn more about this topic, [please review this guide](FIDO-U2F-Authentication.html).

```properties
# cas.authn.mfa.u2f.rank=0
# cas.authn.mfa.u2f.name=

# cas.authn.mfa.u2f.bypass.type=DEFAULT|GROOVY|REST
# cas.authn.mfa.u2f.bypass.principalAttributeName=bypass|skip
# cas.authn.mfa.u2f.bypass.principalAttributeValue=true|enabled.+
# cas.authn.mfa.u2f.bypass.authenticationAttributeName=bypass|skip
# cas.authn.mfa.u2f.bypass.authenticationAttributeValue=allowed.+|enabled.+
# cas.authn.mfa.u2f.bypass.authenticationHandlerName=AcceptUsers.+
# cas.authn.mfa.u2f.bypass.authenticationMethodName=LdapAuthentication.+
# cas.authn.mfa.u2f.bypass.credentialClassType=UsernamePassword.+
# cas.authn.mfa.u2f.bypass.httpRequestRemoteAddress=127.+|example.*
# cas.authn.mfa.u2f.bypass.httpRequestHeaders=header-X-.+|header-Y-.+

# cas.authn.mfa.u2f.expireRegistrations=30
# cas.authn.mfa.u2f.expireRegistrationsTimeUnit=SECONDS
# cas.authn.mfa.u2f.expireDevices=30
# cas.authn.mfa.u2f.expireDevicesTimeUnit=DAYS
```

### FIDO U2F JSON

```properties
# cas.authn.mfa.u2f.json.location=file:///etc/cas/config/u2fdevices.json
```

### FIDO U2F Cleaner

```properties
# cas.authn.mfa.u2f.cleaner.schedule.enabled=true
# cas.authn.mfa.u2f.cleaner.schedule.startDelay=PT10S
# cas.authn.mfa.u2f.cleaner.schedule.repeatInterval=PT60S
```

#### FIDO U2F MongoDb

```properties
# cas.authn.mfa.u2f.mongo.host=localhost
# cas.authn.mfa.u2f.mongo.clientUri=localhost
# cas.authn.mfa.u2f.mongo.idleTimeout=30000
# cas.authn.mfa.u2f.mongo.port=27017
# cas.authn.mfa.u2f.mongo.dropCollection=false
# cas.authn.mfa.u2f.mongo.socketKeepAlive=false
# cas.authn.mfa.u2f.mongo.password=
# cas.authn.mfa.u2f.mongo.collection=cas-fido-repository
# cas.authn.mfa.u2f.mongo.databaseName=cas-mongo-database
# cas.authn.mfa.u2f.mongo.timeout=5000
# cas.authn.mfa.u2f.mongo.userId=
# cas.authn.mfa.u2f.mongo.writeConcern=NORMAL
# cas.authn.mfa.u2f.mongo.authenticationDatabaseName=
# cas.authn.mfa.u2f.mongo.replicaSet=
# cas.authn.mfa.u2f.mongo.sslEnabled=false
# cas.authn.mfa.u2f.mongo.conns.lifetime=60000
# cas.authn.mfa.u2f.mongo.conns.perHost=10
```

#### FIDO U2F JPA

```properties
# cas.authn.mfa.u2f.jpa.healthQuery=
# cas.authn.mfa.u2f.jpa.isolateInternalQueries=false
# cas.authn.mfa.u2f.jpa.url=jdbc:hsqldb:mem:cas-u2f
# cas.authn.mfa.u2f.jpa.failFastTimeout=1
# cas.authn.mfa.u2f.jpa.dialect=org.hibernate.dialect.HSQLDialect
# cas.authn.mfa.u2f.jpa.leakThreshold=10
# cas.authn.mfa.u2f.jpa.batchSize=1
# cas.authn.mfa.u2f.jpa.defaultCatalog=
# cas.authn.mfa.u2f.jpa.defaultSchema=
# cas.authn.mfa.u2f.jpa.user=sa
# cas.authn.mfa.u2f.jpa.ddlAuto=create-drop
# cas.authn.mfa.u2f.jpa.password=
# cas.authn.mfa.u2f.jpa.autocommit=false
# cas.authn.mfa.u2f.jpa.driverClass=org.hsqldb.jdbcDriver
# cas.authn.mfa.u2f.jpa.idleTimeout=5000
# cas.authn.mfa.u2f.jpa.dataSourceName=
# cas.authn.mfa.u2f.jpa.dataSourceProxy=false
# Hibernate-specific properties (i.e. `hibernate.globally_quoted_identifiers`)
# cas.authn.mfa.u2f.jpa.properties.propertyName=propertyValue

# cas.authn.mfa.u2f.jpa.pool.suspension=false
# cas.authn.mfa.u2f.jpa.pool.minSize=6
# cas.authn.mfa.u2f.jpa.pool.maxSize=18
# cas.authn.mfa.u2f.jpa.pool.maxWait=2000
```

### FIDO U2F REST

```properties
# cas.authn.mfa.u2f.rest.url=https://somewhere.fido.org
# cas.authn.mfa.u2f.rest.basicAuthUsername=
# cas.authn.mfa.u2f.rest.basicAuthPassword=
```

### FIDO U2F Groovy

```properties
# cas.authn.mfa.u2f.groovy.location=file:/etc/cas/config/fido.groovy
```

### Swivel Secure

To learn more about this topic, [please review this guide](SwivelSecure-Authentication.html).

```properties
# cas.authn.mfa.swivel.swivelTuringImageUrl=https://turing.example.edu/TURingImage
# cas.authn.mfa.swivel.swivelUrl=https://swivel.example.org/pinsafe
# cas.authn.mfa.swivel.sharedSecret=Th3Sh@r3d$ecret
# cas.authn.mfa.swivel.ignoreSslErrors=false
# cas.authn.mfa.swivel.rank=0
# cas.authn.mfa.swivel.name=

# cas.authn.mfa.swivel.bypass.type=DEFAULT|GROOVY|REST
# cas.authn.mfa.swivel.bypass.principalAttributeName=bypass|skip
# cas.authn.mfa.swivel.bypass.principalAttributeValue=true|enabled.+
# cas.authn.mfa.swivel.bypass.authenticationAttributeName=bypass|skip
# cas.authn.mfa.swivel.bypass.authenticationAttributeValue=allowed.+|enabled.+
# cas.authn.mfa.swivel.bypass.authenticationHandlerName=AcceptUsers.+
# cas.authn.mfa.swivel.bypass.authenticationMethodName=LdapAuthentication.+
# cas.authn.mfa.swivel.bypass.credentialClassType=UsernamePassword.+
# cas.authn.mfa.swivel.bypass.httpRequestRemoteAddress=127.+|example.*
# cas.authn.mfa.swivel.bypass.httpRequestHeaders=header-X-.+|header-Y-.+
```

### Microsoft Azure

To learn more about this topic, [please review this guide](MicrosoftAzure-Authentication.html).

```properties
# cas.authn.mfa.azure.phoneAttribute=phone
# cas.authn.mfa.azure.configDir=/etc/cas/azure
# cas.authn.mfa.azure.privateKeyPassword=
# cas.authn.mfa.azure.mode=POUND|PIN
# cas.authn.mfa.azure.rank=0
# cas.authn.mfa.azure.name=
# cas.authn.mfa.azure.allowInternationalCalls=false

# cas.authn.mfa.azure.bypass.type=DEFAULT|GROOVY|REST
# cas.authn.mfa.azure.bypass.principalAttributeName=bypass|skip
# cas.authn.mfa.azure.bypass.principalAttributeValue=true|enabled.+
# cas.authn.mfa.azure.bypass.authenticationAttributeName=bypass|skip
# cas.authn.mfa.azure.bypass.authenticationAttributeValue=allowed.+|enabled.+
# cas.authn.mfa.azure.bypass.authenticationHandlerName=AcceptUsers.+
# cas.authn.mfa.azure.bypass.authenticationMethodName=LdapAuthentication.+
# cas.authn.mfa.azure.bypass.credentialClassType=UsernamePassword.+
# cas.authn.mfa.azure.bypass.httpRequestRemoteAddress=127.+|example.*
# cas.authn.mfa.azure.bypass.httpRequestHeaders=header-X-.+|header-Y-.+
```

### Authy

To learn more about this topic, [please review this guide](AuthyAuthenticator-Authentication.html).

```properties
# cas.authn.mfa.authy.apiKey=
# cas.authn.mfa.authy.apiUrl=
# cas.authn.mfa.authy.phoneAttribute=phone
# cas.authn.mfa.authy.mailAttribute=mail
# cas.authn.mfa.authy.countryCode=1
# cas.authn.mfa.authy.forceVerification=true
# cas.authn.mfa.authy.trustedDeviceEnabled=false
# cas.authn.mfa.authy.name=

# cas.authn.mfa.authy.bypass.type=DEFAULT|GROOVY|REST
# cas.authn.mfa.authy.bypass.principalAttributeName=bypass|skip
# cas.authn.mfa.authy.bypass.principalAttributeValue=true|enabled.+
# cas.authn.mfa.authy.bypass.authenticationAttributeName=bypass|skip
# cas.authn.mfa.authy.bypass.authenticationAttributeValue=allowed.+|enabled.+
# cas.authn.mfa.authy.bypass.authenticationHandlerName=AcceptUsers.+
# cas.authn.mfa.authy.bypass.authenticationMethodName=LdapAuthentication.+
# cas.authn.mfa.authy.bypass.credentialClassType=UsernamePassword.+
# cas.authn.mfa.authy.bypass.httpRequestRemoteAddress=127.+|example.*
# cas.authn.mfa.authy.bypass.httpRequestHeaders=header-X-.+|header-Y-.+
```

## SAML Core

Control core SAML functionality within CAS.

```properties
# cas.samlCore.ticketidSaml2=false
# cas.samlCore.skewAllowance=5
# cas.samlCore.issueLength=30
# cas.samlCore.attributeNamespace=http://www.ja-sig.org/products/cas/
# cas.samlCore.issuer=localhost
# cas.samlCore.securityManager=org.apache.xerces.util.SecurityManager
```

## SAML IdP

Allow CAS to become a SAML2 identity provider.
To learn more about this topic, [please review this guide](Configuring-SAML2-Authentication.html).

### Attributes Name Formats

Name formats for an individual attribute can be mapped to a number of pre-defined formats, or a custom format of your own choosing.
A given attribute that is to be encoded in the final SAML response may contain any of the following name formats:

| Type                 | Description
|----------------------|----------------------------------------------------------------------------
| `basic`              | Map the attribute to `urn:oasis:names:tc:SAML:2.0:attrname-format:basic`.
| `uri`                | Map the attribute to `urn:oasis:names:tc:SAML:2.0:attrname-format:uri`.
| `unspecified`        | Map the attribute to `urn:oasis:names:tc:SAML:2.0:attrname-format:basic`.
| `urn:my:own:format`  | Map the attribute to `urn:my:own:format`.


```properties
# cas.authn.samlIdp.entityId=https://cas.example.org/idp
# cas.authn.samlIdp.scope=example.org
# cas.authn.samlIdp.authenticationContextClassMappings[0]=urn:oasis:names:tc:SAML:2.0:ac:classes:SomeClassName->mfa-duo

# cas.authn.samlIdp.metadata.cacheExpirationMinutes=30
# cas.authn.samlIdp.metadata.failFast=true
# cas.authn.samlIdp.metadata.location=file:/etc/cas/saml
# cas.authn.samlIdp.metadata.privateKeyAlgName=RSA
# cas.authn.samlIdp.metadata.requireValidMetadata=true

# cas.authn.samlIdp.metadata.basicAuthnUsername=
# cas.authn.samlIdp.metadata.basicAuthnPassword=
# cas.authn.samlIdp.metadata.supportedContentTypes=

# cas.authn.samlIdp.attributeQueryProfileEnabled=true

# cas.authn.samlIdp.logout.forceSignedLogoutRequests=true
# cas.authn.samlIdp.logout.singleLogoutCallbacksDisabled=false

# cas.authn.samlIdp.response.defaultAuthenticationContextClass=
# cas.authn.samlIdp.response.defaultAttributeNameFormat=uri
# cas.authn.samlIdp.response.signError=false
# cas.authn.samlIdp.response.signingCredentialType=X509|BASIC
# cas.authn.samlIdp.response.useAttributeFriendlyName=true
# cas.authn.samlIdp.response.attributeNameFormats=attributeName->basic|uri|unspecified|custom-format-etc,...
# cas.authn.samlIdp.response.skewAllowance=5

# cas.authn.samlIdp.algs.overrideSignatureCanonicalizationAlgorithm=
# cas.authn.samlIdp.algs.overrideDataEncryptionAlgorithms=
# cas.authn.samlIdp.algs.overrideKeyEncryptionAlgorithms=
# cas.authn.samlIdp.algs.overrideBlackListedEncryptionAlgorithms=
# cas.authn.samlIdp.algs.overrideWhiteListedAlgorithms=
# cas.authn.samlIdp.algs.overrideSignatureReferenceDigestMethods=
# cas.authn.samlIdp.algs.overrideSignatureAlgorithms=
# cas.authn.samlIdp.algs.overrideBlackListedSignatureSigningAlgorithms=
# cas.authn.samlIdp.algs.overrideWhiteListedSignatureSigningAlgorithms=
```

## SAML SPs

Allow CAS to register and enable a number of built-in SAML service provider integrations.
To learn more about this topic, [please review this guide](../integration/Configuring-SAML-SP-Integrations.html).

<div class="alert alert-warning"><strong>Remember</strong><p>SAML2 service provider integrations listed here simply attempt to automate CAS configuration based on known and documented integration guidelines and recipes provided by the service provider owned by the vendor. These recipes can change and break CAS over time.</p></div>

The settings defined for each service provider simply attempt to automate the creation of a [SAML service definition](Configuring-SAML2-Authentication.html#saml-services) and nothing more. If you find the applicable settings lack in certain areas, it is best to fall back onto the native configuration strategy for registering SAML service providers with CAS which would depend on your service registry of choice.

Each SAML service provider supports the following settings:

| Name                  |  Description
|-----------------------|---------------------------------------------------------------------------
| `metadata`            | Location of metadata for the service provider (i.e URL, path, etc)
| `name`                | The name of the service provider registered in the service registry.
| `description`         | The description of the service provider registered in the service registry.
| `nameIdAttribute`     | Attribute to use when generating name ids for this service provider.
| `nameIdFormat`        | The name of the service provider registered in the service registry.
| `attributes`          | Attributes to release to the service provider, which may virtually be mapped and renamed.
| `signatureLocation`   | Signature location to verify metadata.
| `entityIds`           | List of entity ids allowed for this service provider.
| `signResponses`       | Indicate whether responses should be signed. Default is `true`.
| `signAssertions`      | Indicate whether assertions should be signed. Default is `false`.


The only required setting that would activate the automatic configuration for a service provider is the presence and definition of metadata. All other settings are optional. 

<div class="alert alert-info"><strong>Keep What You Need!</strong><p>The adopter is encouraged to only keep and maintain properties needed for their particular deployment and remove all other optional settings for which there exist sane defaults.</p></div>

### Dropbox

```properties
# cas.samlSp.dropbox.metadata=/etc/cas/saml/dropbox.xml
# cas.samlSp.dropbox.name=Dropbox
# cas.samlSp.dropbox.description=Dropbox Integration
# cas.samlSp.dropbox.nameIdAttribute=mail
# cas.samlSp.dropbox.signatureLocation=
```

### TestShib

```properties
# cas.samlSp.testShib.metadata=http://www.testshib.org/metadata/testshib-providers.xml
# cas.samlSp.testShib.name=TestShib
# cas.samlSp.testShib.description=TestShib Integration
# cas.samlSp.testShib.attributes=eduPersonPrincipalName
# cas.samlSp.testShib.signatureLocation=
```

### OpenAthens

```properties
# cas.samlSp.openAthens.metadata=/path/to/openAthens-metadata.xml
# cas.samlSp.openAthens.name=openAthens
# cas.samlSp.openAthens.description=openAthens Integration
# cas.samlSp.openAthens.attributes=eduPersonPrincipalName,email
```

### Egnyte

```properties
# cas.samlSp.egnyte.metadata=/path/to/egnyte-metadata.xml
# cas.samlSp.egnyte.name=Egnyte
# cas.samlSp.egnyte.description=Egnyte Integration
```

### EverBridge

```properties
# cas.samlSp.everBridge.metadata=/path/to/everBridge-metadata.xml
# cas.samlSp.everBridge.name=Everbridge
# cas.samlSp.everBridge.description=EverBridge Integration
```

### Symplicity

```properties
# cas.samlSp.symplicity.metadata=/path/to/symplicity-metadata.xml
# cas.samlSp.symplicity.name=Symplicity
# cas.samlSp.symplicity.description=Symplicity Integration
```

### Yuja

```properties
# cas.samlSp.yuja.metadata=/path/to/yuja-metadata.xml
# cas.samlSp.yuja.name=Yuja
# cas.samlSp.yuja.description=Yuja Integration
```

### New Relic

```properties
# cas.samlSp.newRelic.metadata=/path/to/newRelic-metadata.xml
# cas.samlSp.newRelic.name=NewRelic
# cas.samlSp.newRelic.description=New Relic Integration
```

### Sunshine State Education and Research Computing Alliance

```properties
# cas.samlSp.sserca.metadata=/path/to/sserca-metadata.xml
# cas.samlSp.sserca.name=SSERCA
# cas.samlSp.sserca.description=SSERCA Integration
```

### CherWell

```properties
# cas.samlSp.cherWell.metadata=/path/to/cherwell-metadata.xml
# cas.samlSp.cherWell.name=CherWell
# cas.samlSp.cherWell.description=CherWell Integration
```

### FAMIS

```properties
# cas.samlSp.famis.metadata=/path/to/famis-metadata.xml
# cas.samlSp.famis.name=Famis
# cas.samlSp.famis.description=Famis Integration
```

### Bynder

```properties
# cas.samlSp.bynder.metadata=/path/to/bynder-metadata.xml
# cas.samlSp.bynder.name=Bynder
# cas.samlSp.bynder.description=Bynder Integration
```

### Web Advisor

```properties
# cas.samlSp.webAdvisor.metadata=/path/to/webadvisor-metadata.xml
# cas.samlSp.webAdvisor.name=Web Advisor
# cas.samlSp.webAdvisor.description=Web Advisor Integration
# cas.samlSp.webAdvisor.attributes=uid
```

### Adobe Creative Cloud

```properties
# cas.samlSp.adobeCloud.metadata=/path/to/adobe-metadata.xml
# cas.samlSp.adobeCloud.name=Adobe Creative Cloud
# cas.samlSp.adobeCloud.description=Adobe Creative Cloud Integration
# cas.samlSp.adobeCloud.attributes=Email,FirstName,LastName
```

### Securing The Human

```properties
# cas.samlSp.sansSth.metadata=/path/to/sth-metadata.xml
# cas.samlSp.sansSth.name=Securing The Human
# cas.samlSp.sansSth.description=Securing The Human Integration
# cas.samlSp.sansSth.attributes=email,firstName,lastName,scopedUserId,department,reference
```

### Easy IEP

```properties
# cas.samlSp.easyIep.metadata=/path/to/easyiep-metadata.xml
# cas.samlSp.easyIep.name=Easy IEP
# cas.samlSp.easyIep.description=Easy IEP Integration
# cas.samlSp.easyIep.attributes=employeeId
```

### Infinite Campus

```properties
# cas.samlSp.infiniteCampus.metadata=/path/to/infinitecampus-metadata.xml
# cas.samlSp.infiniteCampus.name=Infinite Campus
# cas.samlSp.infiniteCampus.description=Infinite Campus Integration
# cas.samlSp.infiniteCampus.attributes=employeeId
```

### Slack

```properties
# cas.samlSp.slack.metadata=/path/to/slack-metadata.xml
# cas.samlSp.slack.name=Slack
# cas.samlSp.slack.description=Slack Integration
# cas.samlSp.slack.attributes=User.Email,User.Username,first_name,last_name
# cas.samlSp.slack.nameIdFormat=persistent
# cas.samlSp.slack.nameIdAttribute=employeeId
```

### Zendesk

```properties
# cas.samlSp.zendesk.metadata=/path/to/zendesk-metadata.xml
# cas.samlSp.zendesk.name=Zendesk
# cas.samlSp.zendesk.description=Zendesk Integration
# cas.samlSp.zendesk.attributes=organization,tags,phone,role
# cas.samlSp.zendesk.nameIdFormat=emailAddress
# cas.samlSp.zendesk.nameIdAttribute=email
```

### Gartner

```properties
# cas.samlSp.gartner.metadata=/path/to/gartner-metadata.xml
# cas.samlSp.gartner.name=Gartner
# cas.samlSp.gartner.description=Gartner Integration
# cas.samlSp.gartner.attributes=urn:oid:2.5.4.42,urn:oid:2.5.4.4,urn:oid:0.9.2342.19200300.100.1.3
```

### Arc GIS

```properties
# cas.samlSp.arcGIS.metadata=/path/to/arc-metadata.xml
# cas.samlSp.arcGIS.name=ArcGIS
# cas.samlSp.arcGIS.description=ArcGIS Integration
# cas.samlSp.arcGIS.nameIdAttribute=arcNameId
# cas.samlSp.arcGIS.attributes=mail,givenName,arcNameId
# cas.samlSp.arcGIS.nameIdFormat=unspecified
```

### Benefit Focus

```properties
# cas.samlSp.benefitFocus.metadata=/path/to/benefitFocus-metadata.xml
# cas.samlSp.benefitFocus.name=Benefit Focus
# cas.samlSp.benefitFocus.description=Benefit Focus Integration
# cas.samlSp.benefitFocus.nameIdAttribute=benefitFocusUniqueId
# cas.samlSp.benefitFocus.nameIdFormat=unspecified
```

### Office365

```properties
# cas.samlSp.office365.metadata=/etc/cas/saml/azure.xml
# cas.samlSp.office365.name=O365
# cas.samlSp.office365.description=Office365 Integration
# cas.samlSp.office365.nameIdAttribute=scopedImmutableID
# cas.samlSp.office365.attributes=IDPEmail,ImmutableID
# cas.samlSp.office365.signatureLocation=
```

### SAManage

```properties
# cas.samlSp.saManage.metadata=/etc/cas/saml/samanage.xml
# cas.samlSp.saManage.name=SAManage
# cas.samlSp.saManage.description=SAManage Integration
# cas.samlSp.saManage.nameIdAttribute=mail
# cas.samlSp.saManage.signatureLocation=
```

### Workday

```properties
# cas.samlSp.workday.metadata=/etc/cas/saml/workday.xml
# cas.samlSp.workday.name=Workday
# cas.samlSp.workday.description=Workday Integration
# cas.samlSp.workday.signatureLocation=
```

### Salesforce

```properties
# cas.samlSp.salesforce.metadata=/etc/cas/saml/salesforce.xml
# cas.samlSp.salesforce.name=Salesforce
# cas.samlSp.salesforce.description=Salesforce Integration
# cas.samlSp.salesforce.attributes=mail,eduPersonPrincipalName
# cas.samlSp.salesforce.signatureLocation=
```

### Academic Works

```properties
# cas.samlSp.academicWorks.metadata=/etc/cas/saml/aw.xml
# cas.samlSp.academicWorks.name=AcademicWorks
# cas.samlSp.academicWorks.description=AcademicWorks Integration
# cas.samlSp.academicWorks.attributes=mail,displayName
```

### Zoom

```properties
# cas.samlSp.zoom.metadata=/etc/cas/saml/zoom.xml
# cas.samlSp.zoom.name=Zoom
# cas.samlSp.zoom.description=Zoom Integration
# cas.samlSp.zoom.attributes=mail,sn,givenName
# cas.samlSp.zoom.nameIdAttribute=mail
```

### Evernote

```properties
# cas.samlSp.evernote.metadata=/etc/cas/saml/evernote.xml
# cas.samlSp.evernote.name=Evernote
# cas.samlSp.evernote.description=Evernote Integration
# cas.samlSp.evernote.nameIdAttribute=mail
# cas.samlSp.evernote.nameIdFormat=emailAddress
```

### Tableau

```properties
# cas.samlSp.tableau.metadata=/etc/cas/saml/tableau.xml
# cas.samlSp.tableau.name=Tableau
# cas.samlSp.tableau.description=Tableau Integration
# cas.samlSp.tableau.attributes=username
```

### Asana

```properties
# cas.samlSp.asana.metadata=/etc/cas/saml/asana.xml
# cas.samlSp.asana.name=Asana
# cas.samlSp.asana.description=Asana Integration
# cas.samlSp.asana.nameIdAttribute=mail
# cas.samlSp.asana.nameIdFormat=emailAddress
```

### Box

```properties
# cas.samlSp.box.metadata=/etc/cas/saml/box.xml
# cas.samlSp.box.name=Box
# cas.samlSp.box.description=Box Integration
# cas.samlSp.box.attributes=email,firstName,lastName
# cas.samlSp.box.signatureLocation=
```

### Service Now

```properties
# cas.samlSp.serviceNow.metadata=/etc/cas/saml/serviceNow.xml
# cas.samlSp.serviceNow.name=ServiceNow
# cas.samlSp.serviceNow.description=serviceNow Integration
# cas.samlSp.serviceNow.attributes=eduPersonPrincipalName
# cas.samlSp.serviceNow.signatureLocation=
```

### Net Partner

```properties
# cas.samlSp.netPartner.metadata=/etc/cas/saml/netPartner.xml
# cas.samlSp.netPartner.name=Net Partner
# cas.samlSp.netPartner.description=Net Partner Integration
# cas.samlSp.netPartner.nameIdAttribute=studentId
# cas.samlSp.netPartner.attributes=
# cas.samlSp.netPartner.signatureLocation=
```

### Webex

```properties
# cas.samlSp.webex.metadata=/etc/cas/saml/webex.xml
# cas.samlSp.webex.name=Webex
# cas.samlSp.webex.description=Webex Integration
# cas.samlSp.webex.nameIdAttribute=email
# cas.samlSp.webex.attributes=firstName,lastName
```

### InCommon

Multiple entity ids can be specified to filter [the InCommon metadata](https://spaces.internet2.edu/display/InCFederation/Metadata+Aggregates).
EntityIds can be regular expression patterns and are mapped to CAS' `serviceId` field in the registry.
The signature location MUST BE the public key used to sign the metadata.

```properties
# cas.samlSp.inCommon.metadata=http://md.incommon.org/InCommon/InCommon-metadata.xml
# cas.samlSp.inCommon.name=InCommon Aggregate
# cas.samlSp.inCommon.description=InCommon Metadata Aggregate
# cas.samlSp.inCommon.attributes=eduPersonPrincipalName,givenName,cn,sn
# cas.samlSp.inCommon.signatureLocation=/etc/cas/saml/inc-md-public-key.pem
# cas.samlSp.inCommon.entityIds[0]=sampleSPEntityId
```


## OpenID Connect

Allow CAS to become an OpenID Connect provider (OP). To learn more about this topic, [please review this guide](OIDC-Authentication.html).

```properties
# cas.authn.oidc.issuer=http://localhost:8080/cas/oidc

# Skew ID tokens in minutes
# cas.authn.oidc.skew=5

# cas.authn.oidc.jwksFile=file:/keystore.jwks
# cas.authn.oidc.jwksCacheInMinutes=60

# cas.authn.oidc.dynamicClientRegistrationMode=OPEN|PROTECTED

# cas.authn.oidc.subjectTypes=public,pairwise

# Supported scopes
# cas.authn.oidc.scopes=openid,profile,email,address,phone,offline_access

# Supported claims
# cas.authn.oidc.claims=sub,name,preferred_username,family_name, \
#    given_name,middle_name,given_name,profile, \
#    picture,nickname,website,zoneinfo,locale,updated_at,birthdate, \
#    email,email_verified,phone_number,phone_number_verified,address

# Define custom scopes and claims
# cas.authn.oidc.userDefinedScopes.scope1=cn,givenName,photos,customAttribute
# cas.authn.oidc.userDefinedScopes.scope2=cn,givenName,photos,customAttribute2

# Map fixed claims to CAS attributes
# cas.authn.oidc.claimsMap.given_name=custom-given-name
# cas.authn.oidc.claimsMap.preferred_username=global-user-attribute
```

## Pac4j Delegated AuthN

Act as a proxy, and delegate authentication to external identity providers.
To learn more about this topic, [please review this guide](../integration/Delegate-Authentication.html).

```properties
# cas.authn.pac4j.typedIdUsed=false
# cas.authn.pac4j.autoRedirect=false
# cas.authn.pac4j.name=
```

### CAS

Delegate authentication to an external CAS server.

```properties
# cas.authn.pac4j.cas[0].loginUrl=
# cas.authn.pac4j.cas[0].protocol=
# (Optional) Friendly name for CAS, e.g. "This Organization" or "That Organization"
# cas.authn.pac4j.cas[0].clientName=
```

### Facebook

Delegate authentication to Facebook.

```properties
# cas.authn.pac4j.facebook.fields=
# cas.authn.pac4j.facebook.id=
# cas.authn.pac4j.facebook.secret=
# cas.authn.pac4j.facebook.scope=
# cas.authn.pac4j.facebook.clientName=
```

### LinkedIn

Delegate authentication to LinkedIn.

```properties
# cas.authn.pac4j.linkedIn.fields=
# cas.authn.pac4j.linkedIn.id=
# cas.authn.pac4j.linkedIn.secret=
# cas.authn.pac4j.linkedIn.scope=
# cas.authn.pac4j.linkedIn.clientName=
```

### Twitter

Delegate authentication to Twitter.

```properties
# cas.authn.pac4j.twitter.id=
# cas.authn.pac4j.twitter.secret=
# cas.authn.pac4j.twitter.clientName=
```


### Paypal

Delegate authentication to Paypal.

```properties
# cas.authn.pac4j.paypal.id=
# cas.authn.pac4j.paypal.secret=
# cas.authn.pac4j.paypal.clientName=
```


### Wordpress

Delegate authentication to Wordpress.

```properties
# cas.authn.pac4j.wordpress.id=
# cas.authn.pac4j.wordpress.secret=
# cas.authn.pac4j.wordpress.clientName=
```

### OAuth20

Delegate authentication to an generic OAuth2 server.

```properties
# cas.authn.pac4j.oauth2[0].id=
# cas.authn.pac4j.oauth2[0].secret=
# cas.authn.pac4j.oauth2[0].authUrl=
# cas.authn.pac4j.oauth2[0].tokenUrl=
# cas.authn.pac4j.oauth2[0].profileUrl=
# cas.authn.pac4j.oauth2[0].profilePath=
# cas.authn.pac4j.oauth2[0].profileVerb=GET|POST
# cas.authn.pac4j.oauth2[0].profileAttrs.attr1=path-to-attr-in-profile
# cas.authn.pac4j.oauth2[0].customParams.param1=value1
# (Optional) Friendly name for OAuth 2 provider, e.g. "This Organization" or "That Organization"
# cas.authn.pac4j.oauth2[0].clientName=
```

### OpenID Connect

Delegate authentication to an external OpenID Connect server.

```properties
# cas.authn.pac4j.oidc[0].type=KEYCLOAK|GOOGLE|AZURE|GENERIC
# cas.authn.pac4j.oidc[0].discoveryUri=
# cas.authn.pac4j.oidc[0].maxClockSkew=
# cas.authn.pac4j.oidc[0].scope=
# cas.authn.pac4j.oidc[0].id=
# cas.authn.pac4j.oidc[0].secret=
# cas.authn.pac4j.oidc[0].useNonce=
# cas.authn.pac4j.oidc[0].preferredJwsAlgorithm=
# cas.authn.pac4j.oidc[0].customParams.param1=value1
# (Optional) Friendly name for OIDC provider, e.g. "This Organization" or "That Organization"
# cas.authn.pac4j.oidc[0].clientName=
```

### SAML

Delegate authentication to an external SAML2 IdP (do not use the `resource:` or `classpath:`
prefixes for the `keystorePath` or `identityProviderMetadataPath` property).

```properties

# Settings required for CAS SP metadata generation process
# The keystore will be automatically generated by CAS with
# keys required for the metadata generation and/or exchange.
#
# cas.authn.pac4j.saml[0].keystorePassword=
# cas.authn.pac4j.saml[0].privateKeyPassword=
# cas.authn.pac4j.saml[0].keystorePath=
# cas.authn.pac4j.saml[0].keystoreAlias=

# The entityID assigned to CAS acting as the SP
# cas.authn.pac4j.saml[0].serviceProviderEntityId=

# Path to the auto-generated CAS SP metadata
# cas.authn.pac4j.saml[0].serviceProviderMetadataPath=

# cas.authn.pac4j.saml[0].maximumAuthenticationLifetime=
# cas.authn.pac4j.saml[0].destinationBinding=urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect

# Path/URL to delegated IdP metadata
# cas.authn.pac4j.saml[0].identityProviderMetadataPath=

# (Optional) Friendly name for IdP, e.g. "This Organization" or "That Organization"
# This name, with 'nonword' characters converted to '-' (e.g. This Org (New) become This-Org--New- ),
# is added to the "class" attribute of the redirect link on the login page, to allow for
# custom styling of individual IdPs (e.g. for an organization logo).
# cas.authn.pac4j.saml[0].clientName=

# Control aspects of the authentication request sent to IdP
# cas.authn.pac4j.saml[0].authnContextClassRef=
# cas.authn.pac4j.saml[0].authnContextComparisonType=
# cas.authn.pac4j.saml[0].nameIdPolicyFormat=
# cas.authn.pac4j.saml[0].forceAuth=false
# cas.authn.pac4j.saml[0].passive=false

# Define whether metadata requires assertions signed
# cas.authn.pac4j.saml[0].wantsAssertionsSigned=

# Specifies the AttributeConsumingServiceIndex attribute (positive values to enable)
# cas.authn.pac4j.saml[0].attributeConsumingServiceIndex=
```

Examine the generated metadata after accessing the CAS login screen to ensure all ports and endpoints are correctly adjusted.  
Finally, share the CAS SP metadata with the delegated IdP and register CAS as an authorized relying party.

### Yahoo

Delegate authentication to Yahoo.

```properties
# cas.authn.pac4j.yahoo.id=
# cas.authn.pac4j.yahoo.secret=
# cas.authn.pac4j.yahoo.clientName=
```

### Orcid

Delegate authentication to Orcid.

```properties
# cas.authn.pac4j.orcid.id=
# cas.authn.pac4j.orcid.secret=
# cas.authn.pac4j.orcid.clientName=
```

### Dropbox

Delegate authentication to Dropbox.

```properties
# cas.authn.pac4j.dropbox.id=
# cas.authn.pac4j.dropbox.secret=
# cas.authn.pac4j.dropbox.clientName=
```

### Github

Delegate authentication to Github.

```properties
# cas.authn.pac4j.github.id=
# cas.authn.pac4j.github.secret=
# cas.authn.pac4j.github.clientName=
```

### Foursquare

Delegate authentication to Foursquare.

```properties
# cas.authn.pac4j.foursquare.id=
# cas.authn.pac4j.foursquare.secret=
# cas.authn.pac4j.foursquare.clientName=
```

### WindowsLive

Delegate authentication to WindowsLive.

```properties
# cas.authn.pac4j.windowsLive.id=
# cas.authn.pac4j.windowsLive.secret=
# cas.authn.pac4j.windowsLive.clientName=
```

### Google

Delegate authentication to Google.

```properties
# cas.authn.pac4j.google.id=
# cas.authn.pac4j.google.secret=
# cas.authn.pac4j.google.scope=EMAIL|PROFILE|EMAIL_AND_PROFILE
# cas.authn.pac4j.google.clientName=
```

## WS Federation

Allow CAS to act as an identity provider and security token service
to support the WS-Federation protocol.

To learn more about this topic, [please review this guide](WS-Federation-Protocol.html)

```properties
# cas.authn.wsfedIdp.idp.realm=urn:org:apereo:cas:ws:idp:realm-CAS
# cas.authn.wsfedIdp.idp.realmName=CAS

# cas.authn.wsfedIdp.sts.signingKeystoreFile=/etc/cas/config/ststrust.jks
# cas.authn.wsfedIdp.sts.signingKeystorePassword=storepass
# cas.authn.wsfedIdp.sts.encryptionKeystoreFile=/etc/cas/config/stsencrypt.jks
# cas.authn.wsfedIdp.sts.encryptionKeystorePassword=storepass

# cas.authn.wsfedIdp.sts.subjectNameIdFormat=unspecified
# cas.authn.wsfedIdp.sts.encryptTokens=true

# cas.authn.wsfedIdp.sts.realm.keystoreFile=/etc/cas/config/stscasrealm.jks
# cas.authn.wsfedIdp.sts.realm.keystorePassword=storepass
# cas.authn.wsfedIdp.sts.realm.keystoreAlias=realmcas
# cas.authn.wsfedIdp.sts.realm.keyPassword=cas
# cas.authn.wsfedIdp.sts.realm.issuer=CAS
```

### Signing & Encryption

```properties
# Used to secure authentication requests between the IdP and STS
# cas.authn.wsfedIdp.sts.crypto.encryption.key=
# cas.authn.wsfedIdp.sts.crypto.signing.key=
# cas.authn.wsfedIdp.sts.crypto.enabled=true
```

The signing and encryption keys [are both JWKs](Configuration-Properties-Common.html#signing--encryption) of size `512` and `256`.
The encryption algorithm is set to `AES_128_CBC_HMAC_SHA_256`.

## OAuth2

Allows CAS to act as an OAuth2 provider. Here you can control how
long various tokens issued by CAS should last, etc.

To learn more about this topic, [please review this guide](OAuth-OpenId-Authentication.html).


```properties
# cas.authn.oauth.refreshToken.timeToKillInSeconds=2592000

# cas.authn.oauth.code.timeToKillInSeconds=30
# cas.authn.oauth.code.numberOfUses=1

# cas.authn.oauth.accessToken.releaseProtocolAttributes=true
# cas.authn.oauth.accessToken.timeToKillInSeconds=7200
# cas.authn.oauth.accessToken.maxTimeToLiveInSeconds=28800

# cas.authn.oauth.grants.resourceOwner.requireServiceHeader=true

# cas.authn.oauth.userProfileViewType=NESTED|FLAT
# cas.authn.oauth.throttler=neverThrottle|authenticationThrottle
```

## Localization

To learn more about this topic, [please review this guide](User-Interface-Customization-Localization.html).

```properties
# cas.locale.paramName=locale
# cas.locale.defaultValue=en
```

## Global SSO Behavior

```properties
# cas.sso.missingService=true
# cas.sso.renewedAuthn=true
```

## Warning Cookie

Created by CAS if and when users are to be warned when accessing CAS protected services.

```properties
# cas.warningCookie.path=
# cas.warningCookie.maxAge=-1
# cas.warningCookie.domain=
# cas.warningCookie.name=CASPRIVACY
# cas.warningCookie.secure=true
# cas.warningCookie.httpOnly=true
```

## Ticket Granting Cookie

```properties
# cas.tgc.path=
# cas.tgc.maxAge=-1
# cas.tgc.domain=
# cas.tgc.name=TGC
# cas.tgc.secure=true
# cas.tgc.httpOnly=true
# cas.tgc.rememberMeMaxAge=1209600

# cas.tgc.crypto.encryption.key=
# cas.tgc.crypto.signing.key=
# cas.tgc.crypto.enabled=true
```

### Signing & Encryption

The signing and encryption keys [are both JWKs](Configuration-Properties-Common.html#signing--encryption) of size `512` and `256`.
The encryption algorithm is set to `AES_128_CBC_HMAC_SHA_256`.

## Logout

Control various settings related to CAS logout functionality.
To learn more about this topic, [please review this guide](Logout-Single-Signout.html).

```properties
# cas.logout.followServiceRedirects=false
# cas.logout.redirectParameter=service
# cas.logout.confirmLogout=false
# cas.logout.removeDescendantTickets=false
```

## Single Logout

To learn more about this topic, [please review this guide](Logout-Single-Signout.html).

```properties
# cas.slo.disabled=false
# cas.slo.asynchronous=true
```

## Clearpass

Capture and cache user credentials and optionally release them to trusted applications.
To learn more about this topic, [please review this guide](../integration/ClearPass.html).


<div class="alert alert-warning"><strong>Usage Warning!</strong><p>ClearPass is turned off by default.
Think <strong>VERY CAREFULLY</strong> before turning on this feature, as it <strong>MUST</strong> be
the last resort in getting an integration to work...maybe not even then.</p></div>

```properties
# cas.clearpass.cacheCredential=false
# cas.clearpass.crypto.encryption.key=
# cas.clearpass.crypto.signing.key=
# cas.clearpass.crypto.enabled=true
```

The signing and encryption keys [are both JWKs](Configuration-Properties-Common.html#signing--encryption) of size `512` and `256`.
The encryption algorithm is set to `AES_128_CBC_HMAC_SHA_256`.

## Message Bundles

To learn more about this topic, [please review this guide](User-Interface-Customization-Localization.html).
The baseNames are message bundle base names representing files that either end in .properties or _xx.properties where
xx is a country locale code. 
The commonNames are not actually message bundles but they are properties files that are merged together and contain
keys that are only used if they are not found in the message bundles. Keys from the later files in the list will be preferred over keys from the earlier files. 

```properties
# cas.messageBundle.encoding=UTF-8
# cas.messageBundle.fallbackSystemLocale=false
# cas.messageBundle.cacheSeconds=180
# cas.messageBundle.useCodeMessage=true
# cas.messageBundle.baseNames=classpath:custom_messages,classpath:messages
# cas.messageBundle.commonNames=classpath:/common_messages.properties,file:/etc/cas/config/common_messages.properties
```

## Audits

Control how audit messages are formatted.
To learn more about this topic, [please review this guide](Audits.html).

```properties
# cas.audit.auditFormat=DEFAULT|JSON
# cas.audit.ignoreAuditFailures=false
# cas.audit.singlelineSeparator=|
# cas.audit.useSingleLine=false
# cas.audit.appCode=CAS
# cas.audit.alternateServerAddrHeaderName=
# cas.audit.alternateClientAddrHeaderName=X-Forwarded-For
# cas.audit.useServerHostAddress=false
```

### MongoDb Audits

Store audit logs inside a MongoDb database.

```properties
# cas.audit.mongo.host=localhost
# cas.audit.mongo.clientUri=localhost
# cas.audit.mongo.idleTimeout=30000
# cas.audit.mongo.port=27017
# cas.audit.mongo.dropCollection=false
# cas.audit.mongo.socketKeepAlive=false
# cas.audit.mongo.password=
# cas.audit.mongo.collection=cas-audit-database
# cas.audit.mongo.databaseName=cas-mongo-database
# cas.audit.mongo.timeout=5000
# cas.audit.mongo.userId=
# cas.audit.mongo.writeConcern=NORMAL
# cas.audit.mongo.authenticationDatabaseName=
# cas.audit.mongo.replicaSet=
# cas.audit.mongo.sslEnabled=false
# cas.audit.mongo.conns.lifetime=60000
# cas.audit.mongo.conns.perHost=10
```

### Database Audits

Store audit logs inside a database.

```properties
# cas.audit.jdbc.healthQuery=
# cas.audit.jdbc.isolateInternalQueries=false
# cas.audit.jdbc.url=jdbc:hsqldb:mem:cas-hsql-database
# cas.audit.jdbc.failFastTimeout=1
# cas.audit.jdbc.isolationLevelName=ISOLATION_READ_COMMITTED
# cas.audit.jdbc.dialect=org.hibernate.dialect.HSQLDialect
# cas.audit.jdbc.leakThreshold=10
# cas.audit.jdbc.propagationBehaviorName=PROPAGATION_REQUIRED
# cas.audit.jdbc.batchSize=1
# cas.audit.jdbc.user=sa
# cas.audit.jdbc.ddlAuto=create-drop
# cas.audit.jdbc.maxAgeDays=180
# cas.audit.jdbc.password=
# cas.audit.jdbc.autocommit=false
# cas.audit.jdbc.driverClass=org.hsqldb.jdbcDriver
# cas.audit.jdbc.idleTimeout=5000
# cas.audit.jdbc.dataSourceName=
# cas.audit.jdbc.dataSourceProxy=false
# Hibernate-specific properties (i.e. `hibernate.globally_quoted_identifiers`)
# cas.audit.jdbc.properties.propertyName=propertyValue

# cas.audit.jdbc.pool.suspension=false
# cas.audit.jdbc.pool.minSize=6
# cas.audit.jdbc.pool.maxSize=18
# cas.audit.jdbc.pool.maxWait=2000
```

## Sleuth Distributed Tracing

To learn more about this topic, [please review this guide](Monitoring-Statistics.html#distributed-tracing).

```properties
# spring.sleuth.sampler.percentage = 0.5
# spring.sleuth.enabled=true

# spring.zipkin.enabled=true
# spring.zipkin.baseUrl=http://localhost:9411/
```

## Monitoring

To learn more about this topic, [please review this guide](Monitoring-Statistics.html).

### Ticket Granting Tickets

Decide how CAS should monitor the generation of TGTs.

```properties
# cas.monitor.tgt.warn.threshold=10
# cas.monitor.tgt.warn.evictionThreshold=0
```

### Service Tickets

Decide how CAS should monitor the generation of STs.

```properties
# cas.monitor.st.warn.threshold=10
# cas.monitor.st.warn.evictionThreshold=0
```

### Cache Monitors

Decide how CAS should monitor the internal state of various cache storage services.

```properties
# cas.monitor.warn.threshold=10
# cas.monitor.warn.evictionThreshold=0
```

### Memcached Monitors

Decide how CAS should monitor the internal state of a memcached connection pool.

```properties
# cas.monitor.memcached.servers=localhost:11211
# cas.monitor.memcached.locatorType=ARRAY_MOD
# cas.monitor.memcached.failureMode=Redistribute
# cas.monitor.memcached.hashAlgorithm=FNV1_64_HASH
# cas.monitor.memcached.shouldOptimize=false
# cas.monitor.memcached.daemon=true
# cas.monitor.memcached.maxReconnectDelay=-1
# cas.monitor.memcached.useNagleAlgorithm=false
# cas.monitor.memcached.shutdownTimeoutSeconds=-1
# cas.monitor.memcached.opTimeout=-1
# cas.monitor.memcached.timeoutExceptionThreshold=2
# cas.monitor.memcached.maxTotal=20
# cas.monitor.memcached.maxIdle=8
# cas.monitor.memcached.minIdle=0

# cas.monitor.memcached.transcoder=KRYO|SERIAL|WHALIN|WHALINV1
# cas.monitor.memcached.transcoderCompressionThreshold=16384
# cas.monitor.memcached.kryoAutoReset=false
# cas.monitor.memcached.kryoObjectsByReference=false
# cas.monitor.memcached.kryoRegistrationRequired=false

```

### MongoDb Monitors

Decide how CAS should monitor the internal state of a MongoDb instance.

```properties
# cas.monitor.mongo.host=localhost
# cas.monitor.mongo.clientUri=localhost
# cas.monitor.mongo.idleTimeout=30000
# cas.monitor.mongo.port=27017
# cas.monitor.mongo.socketKeepAlive=false
# cas.monitor.mongo.password=
# cas.monitor.mongo.databaseName=cas-mongo-database
# cas.monitor.mongo.timeout=5000
# cas.monitor.mongo.userId=
# cas.monitor.mongo.writeConcern=NORMAL
# cas.monitor.mongo.authenticationDatabaseName=
# cas.monitor.mongo.replicaSet=
# cas.monitor.mongo.sslEnabled=false
# cas.monitor.mongo.conns.lifetime=60000
# cas.monitor.mongo.conns.perHost=10
```

### Database Monitoring

Decide how CAS should monitor the internal state of JDBC connections used
for authentication or attribute retrieval.

```properties
# cas.monitor.jdbc.validationQuery=SELECT 1
# cas.monitor.jdbc.maxWait=5000
# cas.monitor.jdbc.healthQuery=
# cas.monitor.jdbc.isolateInternalQueries=false
# cas.monitor.jdbc.url=jdbc:hsqldb:mem:cas-hsql-database
# cas.monitor.jdbc.failFastTimeout=1
# cas.monitor.jdbc.isolationLevelName=ISOLATION_READ_COMMITTED
# cas.monitor.jdbc.dialect=org.hibernate.dialect.HSQLDialect
# cas.monitor.jdbc.leakThreshold=10
# cas.monitor.jdbc.propagationBehaviorName=PROPAGATION_REQUIRED
# cas.monitor.jdbc.batchSize=1
# cas.monitor.jdbc.user=sa
# cas.monitor.jdbc.ddlAuto=create-drop
# cas.monitor.jdbc.maxAgeDays=180
# cas.monitor.jdbc.password=
# cas.monitor.jdbc.autocommit=false
# cas.monitor.jdbc.driverClass=org.hsqldb.jdbcDriver
# cas.monitor.jdbc.idleTimeout=5000
# cas.monitor.jdbc.dataSourceName=
# cas.monitor.jdbc.dataSourceProxy=false
# Hibernate-specific properties (i.e. `hibernate.globally_quoted_identifiers`)
# cas.monitor.jdbc.properties.propertyName=propertyValue
```

### LDAP Connection Pool

Decide how CAS should monitor the internal state of LDAP connections
used for authentication, etc.

```properties
# Define the thread pool that will ping on the LDAP connection pool.
# cas.monitor.ldap.pool.suspension=false
# cas.monitor.ldap.pool.minSize=6
# cas.monitor.ldap.pool.maxSize=18
# cas.monitor.ldap.pool.maxWait=2000

# cas.monitor.ldap.maxWait=5000

# Define the LDAP connection pool settings for monitoring
# cas.monitor.ldap.ldapUrl=ldaps://ldap1.example.edu ldaps://ldap2.example.edu
# cas.monitor.ldap.connectionStrategy=
# cas.monitor.ldap.baseDn=dc=example,dc=org
# cas.monitor.ldap.userFilter=cn={user}
# cas.monitor.ldap.bindDn=cn=Directory Manager,dc=example,dc=org
# cas.monitor.ldap.bindCredential=Password
# cas.monitor.ldap.providerClass=org.ldaptive.provider.unboundid.UnboundIDProvider
# cas.monitor.ldap.connectTimeout=5000
# cas.monitor.ldap.trustCertificates=
# cas.monitor.ldap.keystore=
# cas.monitor.ldap.keystorePassword=
# cas.monitor.ldap.keystoreType=JKS|JCEKS|PKCS12
# cas.monitor.ldap.poolPassivator=NONE|CLOSE|BIND
# cas.monitor.ldap.minPoolSize=3
# cas.monitor.ldap.maxPoolSize=10
# cas.monitor.ldap.validateOnCheckout=true
# cas.monitor.ldap.validatePeriodically=true
# cas.monitor.ldap.validatePeriod=600
# cas.monitor.ldap.validateTimeout=5000
# cas.monitor.ldap.failFast=true
# cas.monitor.ldap.idleTime=500
# cas.monitor.ldap.prunePeriod=600
# cas.monitor.ldap.blockWaitTime=5000
# cas.monitor.ldap.subtreeSearch=true
# cas.monitor.ldap.useSsl=true
# cas.monitor.ldap.useStartTls=false

# cas.monitor.ldap.validator.type=NONE|SEARCH|COMPARE
# cas.monitor.ldap.validator.baseDn=
# cas.monitor.ldap.validator.searchFilter=(objectClass=*)
# cas.monitor.ldap.validator.scope=OBJECT|ONELEVEL|SUBTREE
# cas.monitor.ldap.validator.attributeName=objectClass
# cas.monitor.ldap.validator.attributeValues=top
# cas.monitor.ldap.validator.dn=
```

### Memory

Decide how CAS should monitor the internal state of JVM memory available at runtime.

```properties
# cas.monitor.freeMemThreshold=10
```

## Themes

To learn more about this topic, [please review this guide](User-Interface-Customization-Themes.html).

```properties
# cas.theme.paramName=theme
# cas.theme.defaultThemeName=cas-theme-default
```


## Events

Decide how CAS should track authentication events.
To learn more about this topic, [please review this guide](Configuring-Authentication-Events.html).


```properties
# Whether geolocation tracking should be turned on and requested from the browser.
# cas.events.trackGeolocation=false

# Control whether CAS should monitor configuration files and auto-refresh context.
# cas.events.trackConfigurationModifications=true
```

### InfluxDb Events

Decide how CAS should store authentication events inside an InfluxDb instance.

```properties
# cas.events.influxDb.url=http://localhost:8086
# cas.events.influxDb.username=root
# cas.events.influxDb.password=root
# cas.events.influxDb.retentionPolicy=autogen
# cas.events.influxDb.dropDatabase=false
# cas.events.influxDb.pointsToFlush=100
# cas.events.influxDb.batchInterval=PT5S
# cas.events.influxDb.consistencyLevel=ALL
```

### Database Events

Decide how CAS should store authentication events inside a database instance.

```properties
# cas.events.jpa.healthQuery=
# cas.events.jpa.isolateInternalQueries=false
# cas.events.jpa.url=jdbc:hsqldb:mem:cas-events
# cas.events.jpa.failFastTimeout=1
# cas.events.jpa.dialect=org.hibernate.dialect.HSQLDialect
# cas.events.jpa.leakThreshold=10
# cas.events.jpa.batchSize=1
# cas.events.jpa.defaultCatalog=
# cas.events.jpa.defaultSchema=
# cas.events.jpa.user=sa
# cas.events.jpa.ddlAuto=create-drop
# cas.events.jpa.password=
# cas.events.jpa.autocommit=false
# cas.events.jpa.driverClass=org.hsqldb.jdbcDriver
# cas.events.jpa.idleTimeout=5000
# cas.events.jpa.dataSourceName=
# cas.events.jpa.dataSourceProxy=false
# Hibernate-specific properties (i.e. `hibernate.globally_quoted_identifiers`)
# cas.events.jpa.properties.propertyName=propertyValue

# cas.events.jpa.pool.suspension=false
# cas.events.jpa.pool.minSize=6
# cas.events.jpa.pool.maxSize=18
# cas.events.jpa.pool.maxWait=2000
```

### MongoDb Events

Decide how CAS should store authentication events inside a MongoDb instance.

```properties
# cas.events.mongo.clientUri=
# cas.events.mongo.dropCollection=false
# cas.events.mongo.collection=MongoDbCasEventRepository
```

## Http Web Requests

Control how CAS should respond and validate incoming HTTP requests.

```properties
# cas.httpWebRequest.header.xframe=true
# cas.httpWebRequest.header.xss=true
# cas.httpWebRequest.header.hsts=true
# cas.httpWebRequest.header.xcontent=true
# cas.httpWebRequest.header.cache=true
# cas.httpWebRequest.header.contentSecurityPolicy=

# cas.httpWebRequest.cors.enabled=false
# cas.httpWebRequest.cors.allowCredentials=false
# cas.httpWebRequest.cors.allowOrigins[0]=
# cas.httpWebRequest.cors.allowMethods[0]=*
# cas.httpWebRequest.cors.allowHeaders[0]=*
# cas.httpWebRequest.cors.maxAge=3600
# cas.httpWebRequest.cors.exposedHeaders[0]=

# cas.httpWebRequest.web.forceEncoding=true
# cas.httpWebRequest.web.encoding=UTF-8

# cas.httpWebRequest.allowMultiValueParameters=false
# cas.httpWebRequest.onlyPostParams=username,password
# cas.httpWebRequest.paramsToCheck=ticket,service,renew,gateway,warn,method,target,SAMLart,pgtUrl,pgt,pgtId,pgtIou,targetService,entityId,token

spring.http.encoding.charset=UTF-8
spring.http.encoding.enabled=true
spring.http.encoding.force=true
```

## Http Client

Control how CAS should attempt to contact resources on the web
via its own Http Client. This is most commonly used when responding
to ticket validation events and/or single logout.

In the event that local certificates are to be imported into the CAS running environment,
a local truststore is provided by CAS to improve portability of configuration across environments.

```properties
# cas.httpClient.connectionTimeout=5000
# cas.httpClient.asyncTimeout=5000
# cas.httpClient.readTimeout=5000
# cas.httpClient.hostNameVerifier=NONE|DEFAULT
# cas.httpClient.allowLocalLogoutUrls=false

# cas.httpClient.truststore.psw=changeit
# cas.httpClient.truststore.file=classpath:/truststore.jks
```

### Hostname Verification

The default options are avaiable for hostname verification:

| Type                    | Description                            
|-------------------------|--------------------------------------
| `NONE`                  | Ignore hostname verification.
| `DEFAULT`               | Enforce hostname verification.

## Service Registry

See [this guide](Service-Management.html) to learn more.

```properties
# cas.serviceRegistry.watcherEnabled=true

# cas.serviceRegistry.schedule.repeatInterval=120000
# cas.serviceRegistry.schedule.startDelay=15000

# Auto-initialize the registry from default JSON service definitions
# cas.serviceRegistry.initFromJson=false

# cas.serviceRegistry.managementType=DEFAULT|DOMAIN
```

### Service Registry Notifications

```properties
# cas.serviceRegistry.sms.from=
# cas.serviceRegistry.sms.text=
# cas.serviceRegistry.sms.attributeName=phone

# cas.serviceRegistry.mail.from=
# cas.serviceRegistry.mail.text=
# cas.serviceRegistry.mail.subject=
# cas.serviceRegistry.mail.cc=
# cas.serviceRegistry.mail.bcc=
# cas.serviceRegistry.mail.attributeName=mail
```

### JSON Service Registry

If the underlying service registry is using local system resources
to locate JSON service definitions, decide how those resources should be found.

```properties
# cas.serviceRegistry.json.location=classpath:/services
```

To learn more about this topic, [please review this guide](JSON-Service-Management.html).

### YAML Service Registry

If the underlying service registry is using local system resources
to locate YAML service definitions, decide how those resources should be found.

```properties
# cas.serviceRegistry.yaml.location=classpath:/services
```

To learn more about this topic, [please review this guide](YAML-Service-Management.html).

### RESTful Service Registry

To learn more about this topic, [please review this guide](REST-Service-Management.html).

```properties
# cas.serviceRegistry.rest.url=classpath:/services
# cas.serviceRegistry.rest.basicAuthUsername=
# cas.serviceRegistry.rest.basicAuthPassword=
```

### Redis Service Registry

To learn more about this topic, [please review this guide](Redis-Service-Management.html).

```properties
# cas.serviceRegistry.redis.host=localhost
# cas.serviceRegistry.redis.database=0
# cas.serviceRegistry.redis.port=6380
# cas.serviceRegistry.redis.password=
# cas.serviceRegistry.redis.timeout=2000
# cas.serviceRegistry.redis.useSsl=false
# cas.serviceRegistry.redis.usePool=true

# cas.serviceRegistry.redis.pool.max-active=20
# cas.serviceRegistry.redis.pool.maxIdle=8
# cas.serviceRegistry.redis.pool.minIdle=0
# cas.serviceRegistry.redis.pool.maxActive=8
# cas.serviceRegistry.redis.pool.maxWait=-1
# cas.serviceRegistry.redis.pool.numTestsPerEvictionRun=0
# cas.serviceRegistry.redis.pool.softMinEvictableIdleTimeMillis=0
# cas.serviceRegistry.redis.pool.minEvictableIdleTimeMillis=0
# cas.serviceRegistry.redis.pool.lifo=true
# cas.serviceRegistry.redis.pool.fairness=false

# cas.serviceRegistry.redis.pool.testOnCreate=false
# cas.serviceRegistry.redis.pool.testOnBorrow=false
# cas.serviceRegistry.redis.pool.testOnReturn=false
# cas.serviceRegistry.redis.pool.testWhileIdle=false

# cas.serviceRegistry.redis.sentinel.master=mymaster
# cas.serviceRegistry.redis.sentinel.nodes=localhost:26379,localhost:26380,localhost:26381
```

### DynamoDb Service Registry

To learn more about this topic, [please review this guide](DynamoDb-Service-Management.html).

```properties
# cas.serviceRegistry.dynamoDb.tableName=DynamoDbCasServices

# Path to an external properties file that contains 'accessKey' and 'secretKey' fields.
# cas.serviceRegistry.dynamoDb.credentialsPropertiesFile=file:/path/to/file.properties

# Alternatively, you may directly provide credentials to CAS
# cas.serviceRegistry.dynamoDb.credentialAccessKey=
# cas.serviceRegistry.dynamoDb.credentialSecretKey=

# cas.serviceRegistry.dynamoDb.endpoint=http://localhost:8000
# cas.serviceRegistry.dynamoDb.region=US_WEST_2|US_EAST_2|EU_WEST_2|<REGION-NAME>
# cas.serviceRegistry.dynamoDb.regionOverride=
# cas.serviceRegistry.dynamoDb.serviceNameIntern=

# cas.serviceRegistry.dynamoDb.dropTablesOnStartup=false
# cas.serviceRegistry.dynamoDb.preventTableCreationOnStartup=false
# cas.serviceRegistry.dynamoDb.timeOffset=0

# cas.serviceRegistry.dynamoDb.readCapacity=10
# cas.serviceRegistry.dynamoDb.writeCapacity=10
# cas.serviceRegistry.dynamoDb.connectionTimeout=5000
# cas.serviceRegistry.dynamoDb.requestTimeout=5000
# cas.serviceRegistry.dynamoDb.socketTimeout=5000
# cas.serviceRegistry.dynamoDb.useGzip=false
# cas.serviceRegistry.dynamoDb.useReaper=false
# cas.serviceRegistry.dynamoDb.useThrottleRetries=false
# cas.serviceRegistry.dynamoDb.useTcpKeepAlive=false
# cas.serviceRegistry.dynamoDb.protocol=HTTPS
# cas.serviceRegistry.dynamoDb.clientExecutionTimeout=10000
# cas.serviceRegistry.dynamoDb.cacheResponseMetadata=false
# cas.serviceRegistry.dynamoDb.localAddress=
# cas.serviceRegistry.dynamoDb.maxConnections=10

# cas.serviceRegistry.dynamoDb.crypto.signing.key=
# cas.serviceRegistry.dynamoDb.crypto.signing.keySize=512
# cas.serviceRegistry.dynamoDb.crypto.encryption.key=
# cas.serviceRegistry.dynamoDb.crypto.encryption.keySize=16
# cas.serviceRegistry.dynamoDb.crypto.alg=AES
# cas.serviceRegistry.dynamoDb.crypto.enabled=false
```

### MongoDb Service Registry

Store CAS service definitions inside a MongoDb instance.
To learn more about this topic, [please review this guide](Mongo-Service-Management.html).

```properties
# cas.serviceRegistry.mongo.host=localhost
# cas.serviceRegistry.mongo.clientUri=localhost
# cas.serviceRegistry.mongo.idleTimeout=30000
# cas.serviceRegistry.mongo.port=27017
# cas.serviceRegistry.mongo.dropCollection=false
# cas.serviceRegistry.mongo.socketKeepAlive=false
# cas.serviceRegistry.mongo.password=
# cas.serviceRegistry.mongo.collection=cas-service-registry
# cas.serviceRegistry.mongo.databaseName=cas-mongo-database
# cas.serviceRegistry.mongo.timeout=5000
# cas.serviceRegistry.mongo.userId=
# cas.serviceRegistry.mongo.writeConcern=NORMAL
# cas.serviceRegistry.mongo.authenticationDatabaseName=
# cas.serviceRegistry.mongo.replicaSet=
# cas.serviceRegistry.mongo.sslEnabled=false
# cas.serviceRegistry.mongo.conns.lifetime=60000
# cas.serviceRegistry.mongo.conns.perHost=10
```

### LDAP Service Registry

Control how CAS services should be found inside an LDAP instance.
To learn more about this topic, [please review this guide](LDAP-Service-Management.html)

```properties
# cas.serviceRegistry.ldap.serviceDefinitionAttribute=description
# cas.serviceRegistry.ldap.idAttribute=uid
# cas.serviceRegistry.ldap.objectClass=casRegisteredService

# cas.serviceRegistry.ldap.ldapUrl=ldaps://ldap1.example.edu ldaps://ldap2.example.edu
# cas.serviceRegistry.ldap.connectionStrategy=
# cas.serviceRegistry.ldap.baseDn=dc=example,dc=org
# cas.serviceRegistry.ldap.bindDn=cn=Directory Manager,dc=example,dc=org
# cas.serviceRegistry.ldap.bindCredential=Password
# cas.serviceRegistry.ldap.providerClass=org.ldaptive.provider.unboundid.UnboundIDProvider
# cas.serviceRegistry.ldap.connectTimeout=5000
# cas.serviceRegistry.ldap.trustCertificates=
# cas.serviceRegistry.ldap.keystore=
# cas.serviceRegistry.ldap.keystorePassword=
# cas.serviceRegistry.ldap.keystoreType=JKS|JCEKS|PKCS12
# cas.serviceRegistry.ldap.poolPassivator=NONE|CLOSE|BIND
# cas.serviceRegistry.ldap.minPoolSize=3
# cas.serviceRegistry.ldap.maxPoolSize=10
# cas.serviceRegistry.ldap.validateOnCheckout=true
# cas.serviceRegistry.ldap.validatePeriodically=true
# cas.serviceRegistry.ldap.validatePeriod=600
# cas.serviceRegistry.ldap.validateTimeout=5000
# cas.serviceRegistry.ldap.failFast=true
# cas.serviceRegistry.ldap.idleTime=500
# cas.serviceRegistry.ldap.prunePeriod=600
# cas.serviceRegistry.ldap.blockWaitTime=5000
# cas.serviceRegistry.ldap.useSsl=true
# cas.serviceRegistry.ldap.useStartTls=false

# cas.serviceRegistry.ldap.validator.type=NONE|SEARCH|COMPARE
# cas.serviceRegistry.ldap.validator.baseDn=
# cas.serviceRegistry.ldap.validator.searchFilter=(objectClass=*)
# cas.serviceRegistry.ldap.validator.scope=OBJECT|ONELEVEL|SUBTREE
# cas.serviceRegistry.ldap.validator.attributeName=objectClass
# cas.serviceRegistry.ldap.validator.attributeValues=top
# cas.serviceRegistry.ldap.validator.dn=
```

### Couchbase Service Registry

Control how CAS services should be found inside a Couchbase instance.
To learn more about this topic, [please review this guide](Couchbase-Service-Management.html)

```properties
# cas.serviceRegistry.couchbase.nodeSet=localhost:8091
# cas.serviceRegistry.couchbase.password=
# cas.serviceRegistry.couchbase.queryEnabled=true
# cas.serviceRegistry.couchbase.bucket=default
# cas.serviceRegistry.couchbase.timeout=10
```

### Database Service Registry

Control how CAS services should be found inside a database instance.
To learn more about this topic, [please review this guide](JPA-Service-Management.html)

```properties
# cas.serviceRegistry.jpa.healthQuery=
# cas.serviceRegistry.jpa.isolateInternalQueries=false
# cas.serviceRegistry.jpa.url=jdbc:hsqldb:mem:cas-service-registry
# cas.serviceRegistry.jpa.failFastTimeout=1
# cas.serviceRegistry.jpa.dialect=org.hibernate.dialect.HSQLDialect
# cas.serviceRegistry.jpa.leakThreshold=10
# cas.serviceRegistry.jpa.batchSize=1
# cas.serviceRegistry.jpa.user=sa
# cas.serviceRegistry.jpa.ddlAuto=create-drop
# cas.serviceRegistry.jpa.password=
# cas.serviceRegistry.jpa.autocommit=false
# cas.serviceRegistry.jpa.driverClass=org.hsqldb.jdbcDriver
# cas.serviceRegistry.jpa.idleTimeout=5000
# cas.serviceRegistry.jpa.dataSourceName=
# cas.serviceRegistry.jpa.dataSourceProxy=false
# Hibernate-specific properties (i.e. `hibernate.globally_quoted_identifiers`)
# cas.serviceRegistry.jpa.properties.propertyName=propertyValue

# cas.serviceRegistry.jpa.pool.suspension=false
# cas.serviceRegistry.jpa.pool.minSize=6
# cas.serviceRegistry.jpa.pool.maxSize=18
# cas.serviceRegistry.jpa.pool.maxWait=2000
```

## Service Registry Replication Hazelcast

```properties
# cas.serviceRegistry.stream.hazelcast.duration=PT30S
# cas.serviceRegistry.stream.hazelcast.config.configLocation=
# cas.serviceRegistry.stream.hazelcast.config.cluster.evictionPolicy=LRU
# cas.serviceRegistry.stream.hazelcast.config.cluster.maxNoHeartbeatSeconds=300
# cas.serviceRegistry.stream.hazelcast.config.cluster.multicastEnabled=false
# cas.serviceRegistry.stream.hazelcast.config.cluster.tcpipEnabled=true
# cas.serviceRegistry.stream.hazelcast.config.cluster.members=localhost
# cas.serviceRegistry.stream.hazelcast.config.cluster.loggingType=slf4j
# cas.serviceRegistry.stream.hazelcast.config.cluster.instanceName=localhost
# cas.serviceRegistry.stream.hazelcast.config.cluster.port=5801
# cas.serviceRegistry.stream.hazelcast.config.cluster.portAutoIncrement=true
# cas.serviceRegistry.stream.hazelcast.config.cluster.maxHeapSizePercentage=85
# cas.serviceRegistry.stream.hazelcast.config.cluster.backupCount=1
# cas.serviceRegistry.stream.hazelcast.config.cluster.asyncBackupCount=0
# cas.serviceRegistry.stream.hazelcast.config.cluster.maxSizePolicy=USED_HEAP_PERCENTAGE
# cas.serviceRegistry.stream.hazelcast.config.cluster.timeout=5
# cas.serviceRegistry.stream.hazelcast.config.cluster.multicastTrustedInterfaces=
# cas.serviceRegistry.stream.hazelcast.config.cluster.multicastPort=
# cas.serviceRegistry.stream.hazelcast.config.cluster.multicastGroup=
# cas.serviceRegistry.stream.hazelcast.config.cluster.multicastTimeout=2
# cas.serviceRegistry.stream.hazelcast.config.cluster.multicastTimeToLive=32
```

## Ticket Registry

To learn more about this topic, [please review this guide](Configuring-Ticketing-Components.html).

### Signing & Encryption

The encryption key must be randomly-generated string whose length is defined by the encryption key size setting.
The signing key [is a JWK](Configuration-Properties-Common.html#signing--encryption) whose length is defined by the signing key size setting.

### Cleaner

A cleaner process is scheduled to run in the background to clean up expired and stale tickets.
This section controls how that process should behave.

```properties
# cas.ticket.registry.cleaner.schedule.startDelay=10000
# cas.ticket.registry.cleaner.schedule.repeatInterval=60000
# cas.ticket.registry.cleaner.schedule.enabled=true
```

### JPA Ticket Registry

To learn more about this topic, [please review this guide](JPA-Ticket-Registry.html).

```properties
# cas.ticket.registry.jpa.ticketLockType=NONE
# cas.ticket.registry.jpa.jpaLockingTimeout=3600

# cas.ticket.registry.jpa.healthQuery=
# cas.ticket.registry.jpa.isolateInternalQueries=false
# cas.ticket.registry.jpa.url=jdbc:hsqldb:mem:cas-ticket-registry
# cas.ticket.registry.jpa.failFastTimeout=1
# cas.ticket.registry.jpa.dialect=org.hibernate.dialect.HSQLDialect
# cas.ticket.registry.jpa.leakThreshold=10
# cas.ticket.registry.jpa.jpaLockingTgtEnabled=true
# cas.ticket.registry.jpa.batchSize=1
# cas.ticket.registry.jpa.defaultCatalog=
# cas.ticket.registry.jpa.defaultSchema=
# cas.ticket.registry.jpa.user=sa
# cas.ticket.registry.jpa.ddlAuto=create-drop
# cas.ticket.registry.jpa.password=
# cas.ticket.registry.jpa.autocommit=false
# cas.ticket.registry.jpa.driverClass=org.hsqldb.jdbcDriver
# cas.ticket.registry.jpa.idleTimeout=5000
# cas.ticket.registry.jpa.dataSourceName=
# cas.ticket.registry.jpa.dataSourceProxy=false
# Hibernate-specific properties (i.e. `hibernate.globally_quoted_identifiers`)
# cas.ticket.registry.jpa.properties.propertyName=propertyValue

# cas.ticket.registry.jpa.pool.suspension=false
# cas.ticket.registry.jpa.pool.minSize=6
# cas.ticket.registry.jpa.pool.maxSize=18
# cas.ticket.registry.jpa.pool.maxWait=2000

# cas.ticket.registry.jpa.crypto.signing.key=
# cas.ticket.registry.jpa.crypto.signing.keySize=512
# cas.ticket.registry.jpa.crypto.encryption.key=
# cas.ticket.registry.jpa.crypto.encryption.keySize=16
# cas.ticket.registry.jpa.crypto.alg=AES
# cas.ticket.registry.jpa.crypto.enabled=false
```

### Couchbase Ticket Registry

To learn more about this topic, [please review this guide](Couchbase-Ticket-Registry.html).

```properties
# cas.ticket.registry.couchbase.timeout=10
# cas.ticket.registry.couchbase.nodeSet=localhost:8091
# cas.ticket.registry.couchbase.password=
# cas.ticket.registry.couchbase.queryEnabled=true
# cas.ticket.registry.couchbase.bucket=default

# cas.ticket.registry.couchbase.crypto.signing.key=
# cas.ticket.registry.couchbase.crypto.signing.keySize=512
# cas.ticket.registry.couchbase.crypto.encryption.key=
# cas.ticket.registry.couchbase.crypto.encryption.keySize=16
# cas.ticket.registry.couchbase.crypto.alg=AES
# cas.ticket.registry.couchbase.crypto.enabled=false
```

### Hazelcast Ticket Registry

To learn more about this topic, [please review this guide](Hazelcast-Ticket-Registry.html).


```properties
# cas.ticket.registry.hazelcast.pageSize=500
# cas.ticket.registry.hazelcast.configLocation=

# cas.ticket.registry.hazelcast.cluster.evictionPolicy=LRU
# cas.ticket.registry.hazelcast.cluster.maxNoHeartbeatSeconds=300
# cas.ticket.registry.hazelcast.cluster.multicastEnabled=false
# cas.ticket.registry.hazelcast.cluster.tcpipEnabled=true
# cas.ticket.registry.hazelcast.cluster.members=localhost
# cas.ticket.registry.hazelcast.cluster.loggingType=slf4j
# cas.ticket.registry.hazelcast.cluster.instanceName=localhost
# cas.ticket.registry.hazelcast.cluster.port=5701
# cas.ticket.registry.hazelcast.cluster.portAutoIncrement=true
# cas.ticket.registry.hazelcast.cluster.maxHeapSizePercentage=85
# cas.ticket.registry.hazelcast.cluster.backupCount=1
# cas.ticket.registry.hazelcast.cluster.asyncBackupCount=0
# cas.ticket.registry.hazelcast.cluster.maxSizePolicy=USED_HEAP_PERCENTAGE
# cas.ticket.registry.hazelcast.cluster.timeout=5

# cas.ticket.registry.hazelcast.cluster.multicastTrustedInterfaces=
# cas.ticket.registry.hazelcast.cluster.multicastPort=
# cas.ticket.registry.hazelcast.cluster.multicastGroup=
# cas.ticket.registry.hazelcast.cluster.multicastTimeout=2
# cas.ticket.registry.hazelcast.cluster.multicastTimeToLive=32

# cas.ticket.registry.hazelcast.crypto.signing.key=
# cas.ticket.registry.hazelcast.crypto.signing.keySize=512
# cas.ticket.registry.hazelcast.crypto.encryption.key=
# cas.ticket.registry.hazelcast.crypto.encryption.keySize=16
# cas.ticket.registry.hazelcast.crypto.alg=AES
# cas.ticket.registry.hazelcast.crypto.enabled=false
```

### Infinispan Ticket Registry

To learn more about this topic, [please review this guide](Infinispan-Ticket-Registry.html).

```properties
# cas.ticket.registry.infinispan.cacheName=
# cas.ticket.registry.infinispan.configLocation=/infinispan.xml

# cas.ticket.registry.infinispan.crypto.signing.key=
# cas.ticket.registry.infinispan.crypto.signing.keySize=512
# cas.ticket.registry.infinispan.crypto.encryption.key=
# cas.ticket.registry.infinispan.crypto.encryption.keySize=16
# cas.ticket.registry.infinispan.crypto.alg=AES
# cas.ticket.registry.infinispan.crypto.enabled=false
```

### InMemory Ticket Registry

This is typically the default ticket registry instance where tickets
are kept inside the runtime environment memory.

```properties
# Enable the backing map to be cacheable
# cas.ticket.registry.inMemory.cache=true

# cas.ticket.registry.inMemory.loadFactor=1
# cas.ticket.registry.inMemory.concurrency=20
# cas.ticket.registry.inMemory.initialCapacity=1000

# cas.ticket.registry.inMemory.crypto.signing.key=
# cas.ticket.registry.inMemory.crypto.signing.keySize=512
# cas.ticket.registry.inMemory.crypto.encryption.key=
# cas.ticket.registry.inMemory.crypto.encryption.keySize=16
# cas.ticket.registry.inMemory.crypto.alg=AES
# cas.ticket.registry.inMemory.crypto.enabled=false
```

### JMS Ticket Registry

To learn more about this topic, [please review this guide](Messaging-JMS-Ticket-Registry.html).

#### JMS Ticket Registry ActiveMQ

```properties
# spring.activemq.broker-url=tcp://192.168.1.210:9876
# spring.activemq.user=admin
# spring.activemq.password=secret
# spring.activemq.pool.enabled=true
# spring.activemq.pool.max-connections=50
```

#### JMS Ticket Registry Artemis

```properties
# spring.artemis.mode=native
# spring.artemis.host=192.168.1.210
# spring.artemis.port=9876
# spring.artemis.user=admin
# spring.artemis.password=secret
```

#### JMS Ticket Registry JNDI

```properties
# spring.jms.jndi-name=java:/MyConnectionFactory
```

### Ehcache Ticket Registry

To learn more about this topic, [please review this guide](Ehcache-Ticket-Registry.html).

```properties
# cas.ticket.registry.ehcache.replicateUpdatesViaCopy=true
# cas.ticket.registry.ehcache.cacheManagerName=ticketRegistryCacheManager
# cas.ticket.registry.ehcache.replicatePuts=true
# cas.ticket.registry.ehcache.replicateUpdates=true
# cas.ticket.registry.ehcache.memoryStoreEvictionPolicy=LRU
# cas.ticket.registry.ehcache.configLocation=classpath:/ehcache-replicated.xml
# cas.ticket.registry.ehcache.maximumBatchSize=100
# cas.ticket.registry.ehcache.shared=false
# cas.ticket.registry.ehcache.replicationInterval=10000
# cas.ticket.registry.ehcache.cacheTimeToLive=2147483647
# cas.ticket.registry.ehcache.diskExpiryThreadIntervalSeconds=0
# cas.ticket.registry.ehcache.replicateRemovals=true
# cas.ticket.registry.ehcache.maxChunkSize=5000000
# cas.ticket.registry.ehcache.maxElementsOnDisk=0
# cas.ticket.registry.ehcache.maxElementsInCache=0
# cas.ticket.registry.ehcache.maxElementsInMemory=10000
# cas.ticket.registry.ehcache.eternal=false
# cas.ticket.registry.ehcache.loaderAsync=true
# cas.ticket.registry.ehcache.replicatePutsViaCopy=true
# cas.ticket.registry.ehcache.cacheTimeToIdle=0
# cas.ticket.registry.ehcache.persistence=LOCALTEMPSWAP|NONE|LOCALRESTARTABLE|DISTRIBUTED
# cas.ticket.registry.ehcache.synchronousWrites=

# cas.ticket.registry.ehcache.crypto.signing.key=
# cas.ticket.registry.ehcache.crypto.signing.keySize=512
# cas.ticket.registry.ehcache.crypto.encryption.key=
# cas.ticket.registry.ehcache.crypto.encryption.keySize=16
# cas.ticket.registry.ehcache.crypto.alg=AES
# cas.ticket.registry.ehcache.crypto.enabled=false
```

### Ignite Ticket Registry

To learn more about this topic, [please review this guide](Ignite-Ticket-Registry.html).

```properties
# cas.ticket.registry.ignite.keyAlgorithm=
# cas.ticket.registry.ignite.protocol=
# cas.ticket.registry.ignite.trustStorePassword=
# cas.ticket.registry.ignite.keyStoreType=
# cas.ticket.registry.ignite.keyStoreFilePath=
# cas.ticket.registry.ignite.keyStorePassword=
# cas.ticket.registry.ignite.trustStoreType=
# cas.ticket.registry.ignite.igniteAddress[0]=localhost:47500
# cas.ticket.registry.ignite.igniteAddress[1]=
# cas.ticket.registry.ignite.trustStoreFilePath=
# cas.ticket.registry.ignite.ackTimeout=2000
# cas.ticket.registry.ignite.joinTimeout=1000
# cas.ticket.registry.ignite.localAddress=
# cas.ticket.registry.ignite.localPort=-1
# cas.ticket.registry.ignite.networkTimeout=5000
# cas.ticket.registry.ignite.socketTimeout=5000
# cas.ticket.registry.ignite.threadPriority=10
# cas.ticket.registry.ignite.forceServerMode=false
# cas.ticket.registry.ignite.clientMode=false

# cas.ticket.registry.ignite.ticketsCache.writeSynchronizationMode=FULL_SYNC
# cas.ticket.registry.ignite.ticketsCache.atomicityMode=TRANSACTIONAL
# cas.ticket.registry.ignite.ticketsCache.cacheMode=REPLICATED

# cas.ticket.registry.ignite.crypto.signing.key=
# cas.ticket.registry.ignite.crypto.signing.keySize=512
# cas.ticket.registry.ignite.crypto.encryption.key=
# cas.ticket.registry.ignite.crypto.encryption.keySize=16
# cas.ticket.registry.ignite.crypto.alg=AES
# cas.ticket.registry.ignite.crypto.enabled=false
```

### Memcached Ticket Registry

To learn more about this topic, [please review this guide](Memcached-Ticket-Registry.html).

```properties
# cas.ticket.registry.memcached.servers=localhost:11211
# cas.ticket.registry.memcached.locatorType=ARRAY_MOD
# cas.ticket.registry.memcached.failureMode=Redistribute
# cas.ticket.registry.memcached.hashAlgorithm=FNV1_64_HASH
# cas.ticket.registry.memcached.shouldOptimize=false
# cas.ticket.registry.memcached.daemon=true
# cas.ticket.registry.memcached.maxReconnectDelay=-1
# cas.ticket.registry.memcached.useNagleAlgorithm=false
# cas.ticket.registry.memcached.shutdownTimeoutSeconds=-1
# cas.ticket.registry.memcached.opTimeout=-1
# cas.ticket.registry.memcached.timeoutExceptionThreshold=2
# cas.ticket.registry.memcached.maxTotal=20
# cas.ticket.registry.memcached.maxIdle=8
# cas.ticket.registry.memcached.minIdle=0

# cas.ticket.registry.memcached.transcoder=KRYO|SERIAL|WHALIN|WHALINV1
# cas.ticket.registry.memcached.transcoderCompressionThreshold=16384
# cas.ticket.registry.memcached.kryoAutoReset=false
# cas.ticket.registry.memcached.kryoObjectsByReference=false
# cas.ticket.registry.memcached.kryoRegistrationRequired=false

# cas.ticket.registry.memcached.crypto.signing.key=
# cas.ticket.registry.memcached.crypto.signing.keySize=512
# cas.ticket.registry.memcached.crypto.encryption.key=
# cas.ticket.registry.memcached.crypto.encryption.keySize=16
# cas.ticket.registry.memcached.crypto.alg=AES
# cas.ticket.registry.memcached.crypto.enabled=false
```

### DynamoDb Ticket Registry

To learn more about this topic, [please review this guide](DynamoDb-Ticket-Registry.html).

```properties
# cas.ticket.registry.dynamoDb.serviceTicketsTableName=serviceTicketsTable
# cas.ticket.registry.dynamoDb.proxyTicketsTableName=proxyTicketsTable
# cas.ticket.registry.dynamoDb.ticketGrantingTicketsTableName=ticketGrantingTicketsTable
# cas.ticket.registry.dynamoDb.proxyGrantingTicketsTableName=proxyGrantingTicketsTable

# Path to an external properties file that contains 'accessKey' and 'secretKey' fields.
# cas.ticket.registry.dynamoDb.credentialsPropertiesFile=file:/path/to/file.properties

# Alternatively, you may directly provide credentials to CAS
# cas.ticket.registry.dynamoDb.credentialAccessKey=
# cas.ticket.registry.dynamoDb.credentialSecretKey=

# cas.ticket.registry.dynamoDb.endpoint=http://localhost:8000
# cas.ticket.registry.dynamoDb.region=US_WEST_2|US_EAST_2|EU_WEST_2|<REGION-NAME>
# cas.ticket.registry.dynamoDb.regionOverride=
# cas.ticket.registry.dynamoDb.serviceNameIntern=

# cas.ticket.registry.dynamoDb.dropTablesOnStartup=false
# cas.ticket.registry.dynamoDb.preventTableCreationOnStartup=false
# cas.ticket.registry.dynamoDb.timeOffset=0

# cas.ticket.registry.dynamoDb.readCapacity=10
# cas.ticket.registry.dynamoDb.writeCapacity=10
# cas.ticket.registry.dynamoDb.connectionTimeout=5000
# cas.ticket.registry.dynamoDb.requestTimeout=5000
# cas.ticket.registry.dynamoDb.socketTimeout=5000
# cas.ticket.registry.dynamoDb.useGzip=false
# cas.ticket.registry.dynamoDb.useReaper=false
# cas.ticket.registry.dynamoDb.useThrottleRetries=false
# cas.ticket.registry.dynamoDb.useTcpKeepAlive=false
# cas.ticket.registry.dynamoDb.protocol=HTTPS
# cas.ticket.registry.dynamoDb.clientExecutionTimeout=10000
# cas.ticket.registry.dynamoDb.cacheResponseMetadata=false
# cas.ticket.registry.dynamoDb.localAddress=
# cas.ticket.registry.dynamoDb.maxConnections=10

# cas.ticket.registry.dynamoDb.crypto.signing.key=
# cas.ticket.registry.dynamoDb.crypto.signing.keySize=512
# cas.ticket.registry.dynamoDb.crypto.encryption.key=
# cas.ticket.registry.dynamoDb.crypto.encryption.keySize=16
# cas.ticket.registry.dynamoDb.crypto.alg=AES
# cas.ticket.registry.dynamoDb.crypto.enabled=false
```

### MongoDb Ticket Registry

To learn more about this topic, [please review this guide](MongoDb-Ticket-Registry.html).

```properties
# cas.ticket.registry.mongo.idleTimeout=30000
# cas.ticket.registry.mongo.port=27017
# cas.ticket.registry.mongo.dropCollection=false
# cas.ticket.registry.mongo.socketKeepAlive=false
# cas.ticket.registry.mongo.password=
# cas.ticket.registry.mongo.databaseName=cas-database
# cas.ticket.registry.mongo.timeout=5000
# cas.ticket.registry.mongo.userId=
# cas.ticket.registry.mongo.writeConcern=NORMAL
# cas.ticket.registry.mongo.host=localhost
# cas.ticket.registry.mongo.clientUri=
# cas.ticket.registry.mongo.authenticationDatabaseName=
# cas.ticket.registry.mongo.replicaSet=
# cas.ticket.registry.mongo.sslEnabled=false

# cas.ticket.registry.mongo.conns.lifetime=60000
# cas.ticket.registry.mongo.conns.perHost=10

# cas.ticket.registry.mongo.crypto.signing.key=
# cas.ticket.registry.mongo.crypto.signing.keySize=512
# cas.ticket.registry.mongo.crypto.encryption.key=
# cas.ticket.registry.mongo.crypto.encryption.keySize=16
# cas.ticket.registry.mongo.crypto.alg=AES
# cas.ticket.registry.mongo.crypto.enabled=false
```

### Redis Ticket Registry

To learn more about this topic, [please review this guide](Redis-Ticket-Registry.html).

```properties
# cas.ticket.registry.redis.host=localhost
# cas.ticket.registry.redis.database=0
# cas.ticket.registry.redis.port=6379
# cas.ticket.registry.redis.password=
# cas.ticket.registry.redis.timeout=
# cas.ticket.registry.redis.useSsl=false
# cas.ticket.registry.redis.usePool=true

# cas.ticket.registry.redis.pool.max-active=20
# cas.ticket.registry.redis.pool.maxIdle=8
# cas.ticket.registry.redis.pool.minIdle=0
# cas.ticket.registry.redis.pool.maxActive=8
# cas.ticket.registry.redis.pool.maxWait=-1
# cas.ticket.registry.redis.pool.numTestsPerEvictionRun=0
# cas.ticket.registry.redis.pool.softMinEvictableIdleTimeMillis=0
# cas.ticket.registry.redis.pool.minEvictableIdleTimeMillis=0
# cas.ticket.registry.redis.pool.lifo=true
# cas.ticket.registry.redis.pool.fairness=false

# cas.ticket.registry.redis.pool.testOnCreate=false
# cas.ticket.registry.redis.pool.testOnBorrow=false
# cas.ticket.registry.redis.pool.testOnReturn=false
# cas.ticket.registry.redis.pool.testWhileIdle=false

# cas.ticket.registry.redis.sentinel.master=mymaster
# cas.ticket.registry.redis.sentinel.node[0]=localhost:26379
# cas.ticket.registry.redis.sentinel.node[1]=localhost:26380
# cas.ticket.registry.redis.sentinel.node[2]=localhost:26381

# cas.ticket.registry.redis.crypto.signing.key=
# cas.ticket.registry.redis.crypto.signing.keySize=512
# cas.ticket.registry.redis.crypto.encryption.key=
# cas.ticket.registry.redis.crypto.encryption.keySize=16
# cas.ticket.registry.redis.crypto.alg=AES
# cas.ticket.registry.redis.crypto.enabled=false
```

## Protocol Ticket Security

Controls whether tickets issued by the CAS server should be secured via signing and encryption
when shared with client applications on outgoing calls.

```properties
# cas.ticket.crypto.enabled=true
# cas.ticket.crypto.encryption.key=
# cas.ticket.crypto.signing.key=
```

The signing and encryption keys [are both JWKs](Configuration-Properties-Common.html#signing--encryption) of size `512` and `256`.
The encryption algorithm is set to `AES_128_CBC_HMAC_SHA_256`.

## Service Tickets Behavior

Controls the expiration policy of service tickets, as well as other properties
applicable to STs.

```properties
# cas.ticket.st.maxLength=20

# cas.ticket.st.numberOfUses=1
# cas.ticket.st.timeToKillInSeconds=10
```

## Proxy Granting Tickets Behavior

```properties
# cas.ticket.pgt.maxLength=50
```

## Proxy Tickets Behavior

```properties
# cas.ticket.pt.timeToKillInSeconds=10
# cas.ticket.pt.numberOfUses=1
```

## Ticket Granting Tickets Behavior

```properties
# cas.ticket.tgt.onlyTrackMostRecentSession=true
# cas.ticket.tgt.maxLength=50
```

## TGT Expiration Policy

Ticket expiration policies are activated in the following conditions:

- If the timeout values for the default policy are all set to zero or less, CAS shall ensure tickets are *never* considered expired.
- Disabling a policy requires that all its timeout settings be set to a value equal or less than zero.
- If not ticket expiration policy is determined, CAS shall ensure the ticket are *always* considered expired.

<div class="alert alert-info"><strong>Keep What You Need!</strong><p>You are encouraged to only keep and maintain properties and settings needed for a particular policy. It is <strong>UNNECESSARY</strong> to grab a copy of all fields or keeping a copy as a reference while leaving them commented out. This strategy would ultimately lead to poor upgrades increasing chances of breaking changes and a messy deployment at that.</p></div>

Ticket expiration policies are activated in the following order:

1. Tickets are never expired, if and when settings for the default policy are configured accordingly.
2. Timeout
3. Default
4. Throttled Timeout
5. Hard Timeout
6. Tickets always expire immediately.

### Default

Provides a hard-time out as well as a sliding window.

```properties
# Set to a negative value to never expire tickets
# cas.ticket.tgt.maxTimeToLiveInSeconds=28800
# cas.ticket.tgt.timeToKillInSeconds=7200
```

### Remember Me

```properties
# cas.ticket.tgt.rememberMe.enabled=true
# cas.ticket.tgt.rememberMe.timeToKillInSeconds=28800
```

### Timeout

The expiration policy applied to TGTs provides for most-recently-used expiration policy, similar to a Web server session timeout.

```properties
# cas.ticket.tgt.timeout.maxTimeToLiveInSeconds=28800
```

### Throttled Timeout

The throttled timeout policy extends the Timeout policy with the concept of throttling where a ticket may be used at most every N seconds.

```properties
# cas.ticket.tgt.throttledTimeout.timeToKillInSeconds=28800
# cas.ticket.tgt.throttledTimeout.timeInBetweenUsesInSeconds=5
```

### Hard Timeout

The hard timeout policy provides for finite ticket lifetime as measured from the time of creation.

```properties
# cas.ticket.tgt.hardTimeout.timeToKillInSeconds=28800
```

## Management Webapp

To learn more about this topic, [please review this guide](Installing-ServicesMgmt-Webapp.html).

The configuration of the CAS management web application is handled inside a `management.properties|yml` file. Some of the settings, specially those that deal with service registry and loading services or those that might define an external CAS server for authentication are shared between this application and the core CAS web application. You have to note that the [persistence storage](Service-Management.html) for services **MUST** be the same as that of the CAS server. The same service registry component that is configured for the CAS server, including module and all settings that affect the behavior of services, needs to be configured in the same exact way for the management web application.

```properties
# server.port=8444
# server.contextPath=/cas-management

# cas.mgmt.adminRoles[0]=ROLE_ADMIN
# cas.mgmt.adminRoles[1]=ROLE_SUPER_USER

# cas.mgmt.userPropertiesFile=classpath:/user-details.[json|yml]
# cas.mgmt.userPropertiesFile=classpath:/user-details.properties

# cas.mgmt.serverName=https://localhost:8443
# cas.mgmt.defaultLocale=en

# cas.mgmt.authzAttributes[0]=memberOf
# cas.mgmt.authzAttributes[1]=groupMembership

# Connect to a CAS server for authentication
# cas.server.name=
# cas.server.prefix=

# Use regex for authorized IPs
#cas.mgmt.authzIpRegex=
```

### Attributes

Attribute configuration and customizations that are processed and accepted by the management web application are defined via the following settings:

```properties
# cas.authn.attributeRepository.stub.attributes.uid=uid
# cas.authn.attributeRepository.stub.attributes.givenName=givenName
# cas.authn.attributeRepository.stub.attributes.eppn=eppn
```

### LDAP Authorization

Use LDAP to enforce access into the management web application either by group or attribute.

```properties
# Enable authorization based on groups
# cas.mgmt.ldap.ldapAuthz.groupAttribute=
# cas.mgmt.ldap.ldapAuthz.groupPrefix=
# cas.mgmt.ldap.ldapAuthz.groupFilter=
# cas.mgmt.ldap.ldapAuthz.groupBaseDn=

# Enable authorization based on attributes and roles
# cas.mgmt.ldap.ldapAuthz.rolePrefix=ROLE_
# cas.mgmt.ldap.ldapAuthz.roleAttribute=uugid

# cas.mgmt.ldap.ldapAuthz.searchFilter=cn={user}
# cas.mgmt.ldap.ldapAuthz.baseDn=

# cas.mgmt.ldap.allowMultipleResults=false
# cas.mgmt.ldap.baseDn=dc=example,dc=org
# cas.mgmt.ldap.ldapUrl=ldaps://ldap1.example.edu ldaps://ldap2.example.edu
# cas.mgmt.ldap.connectionStrategy=
# cas.mgmt.ldap.baseDn=dc=example,dc=org
# cas.mgmt.ldap.userFilter=cn={user}
# cas.mgmt.ldap.bindDn=cn=Directory Manager,dc=example,dc=org
# cas.mgmt.ldap.bindCredential=Password
# cas.mgmt.ldap.providerClass=org.ldaptive.provider.unboundid.UnboundIDProvider
# cas.mgmt.ldap.connectTimeout=5000
# cas.mgmt.ldap.trustCertificates=
# cas.mgmt.ldap.keystore=
# cas.mgmt.ldap.keystorePassword=
# cas.mgmt.ldap.keystoreType=JKS|JCEKS|PKCS12
# cas.mgmt.ldap.poolPassivator=NONE|CLOSE|BIND
# cas.mgmt.ldap.minPoolSize=3
# cas.mgmt.ldap.maxPoolSize=10
# cas.mgmt.ldap.validateOnCheckout=true
# cas.mgmt.ldap.validatePeriodically=true
# cas.mgmt.ldap.validatePeriod=600
# cas.mgmt.ldap.validateTimeout=5000
# cas.mgmt.ldap.failFast=true
# cas.mgmt.ldap.idleTime=500
# cas.mgmt.ldap.prunePeriod=600
# cas.mgmt.ldap.blockWaitTime=5000
# cas.mgmt.ldap.subtreeSearch=true
# cas.mgmt.ldap.useSsl=true
# cas.mgmt.ldap.useStartTls=false
```

## Google reCAPTCHA Integration

Display Google's reCAPTCHA widget on the CAS login page.

```properties
# cas.googleRecaptcha.verifyUrl=https://www.google.com/recaptcha/api/siteverify
# cas.googleRecaptcha.siteKey=
# cas.googleRecaptcha.secret=
```

## Google Analytics Integration

To learn more about this topic, [please review this guide](../integration/Configuring-Google-Analytics.html).

```properties
# cas.googleAnalytics.googleAnalyticsTrackingId=
```

## Spring Webflow

Control how Spring Webflow's conversational session state should be managed by CAS,
and all other webflow related settings.

To learn more about this topic, [please review this guide](Webflow-Customization.html).

```properties
# cas.webflow.alwaysPauseRedirect=false
# cas.webflow.refresh=true
# cas.webflow.redirectSameState=false
```

### Spring Webflow Auto Configuration

Options that control how the Spring Webflow context is dynamically altered and configured by CAS. To learn more about this topic, [please review this guide](Webflow-Customization-Extensions.html).

```properties
# cas.webflow.autoconfigure=true
```

#### Spring Webflow Groovy Auto Configuration

Control the Spring Webflow context via a custom Groovy script.

```properties
# cas.webflow.groovy.location=file:/etc/cas/config/custom-webflow.groovy
```

### Spring Webflow Session Management

To learn more about this topic, [see this guide](Webflow-Customization-Sessions.html).

```properties
# cas.webflow.session.lockTimeout=30
# cas.webflow.session.compress=false
# cas.webflow.session.maxConversations=5

# Enable server-side session management
# cas.webflow.session.storage=false
```

#### Spring Webflow Client-Side Session

```properties
# cas.webflow.crypto.enabled=true

# cas.webflow.crypto.signing.key=
# cas.webflow.crypto.signing.keySize=512

# cas.webflow.crypto.encryption.keySize=16
# cas.webflow.crypto.encryption.key=

# cas.webflow.crypto.alg=AES
```

The encryption key must be randomly-generated string whose length is defined by the encryption key size setting.
The signing key [is a JWK](Configuration-Properties-Common.html#signing--encryption) whose length is defined by the signing key size setting.

#### Spring Webflow Hazelcast Server-Side Session

```properties
# cas.webflow.session.hzLocation=classpath:/hazelcast.xml
```

#### Spring Webflow MongoDb Server-Side Session

```properties
# spring.data.mongodb.host=mongo-srv
# spring.data.mongodb.port=27018
# spring.data.mongodb.database=prod
```

#### Spring Webflow Redis Server-Side Session

```properties
# spring.session.store-type=redis
# spring.redis.host=localhost
# spring.redis.password=secret
# spring.redis.port=6379
```

### Authentication Exceptions

Map custom authentication exceptions in the CAS webflow and link them to custom messages defined in message bundles.

To learn more about this topic, [please review this guide](Webflow-Customization-Exceptions.html).

```properties
# cas.authn.exceptions.exceptions=value1,value2,...
```

### Authentication Interrupt

Interrupt the authentication flow to reach out to external services. To learn more about this topic, [please review this guide](Webflow-Customization-Interrupt.html).

#### Authentication Interrupt JSON

```properties
# cas.interrupt.json.location=file:/etc/cas/config/interrupt.json
```

#### Authentication Interrupt Groovy

```properties
# cas.interrupt.groovy.location=file:/etc/cas/config/interrupt.groovy
```

#### Authentication Interrupt REST

```properties
# cas.interrupt.rest.url=https://somewhere.interrupt.org
# cas.interrupt.rest.method=GET|POST
# cas.interrupt.rest.basicAuthUsername=
# cas.interrupt.rest.basicAuthPassword=
```

### Acceptable Usage Policy

Decide how CAS should attempt to determine whether AUP is accepted.
To learn more about this topic, [please review this guide](Webflow-Customization-AUP.html).

```properties
# cas.acceptableUsagePolicy.aupAttributeName=aupAccepted
```

#### REST

```properties
# cas.acceptableUsagePolicy.rest.method=GET|POST
# cas.acceptableUsagePolicy.rest.order=0
# cas.acceptableUsagePolicy.rest.caseInsensitive=false
# cas.acceptableUsagePolicy.rest.basicAuthUsername=uid
# cas.acceptableUsagePolicy.rest.basicAuthPassword=password
# cas.acceptableUsagePolicy.rest.url=https://rest.somewhere.org/attributes
```

#### JDBC

If AUP is controlled via JDBC, decide how choices should be remembered back inside the database instance.

```properties
# cas.acceptableUsagePolicy.jdbc.tableName=usage_policies_table

# cas.acceptableUsagePolicy.jdbc.validationQuery=SELECT 1
# cas.acceptableUsagePolicy.jdbc.maxWait=5000
# cas.acceptableUsagePolicy.jdbc.healthQuery=
# cas.acceptableUsagePolicy.jdbc.isolateInternalQueries=false
# cas.acceptableUsagePolicy.jdbc.url=jpa:hsqldb:mem:cas-hsql-database
# cas.acceptableUsagePolicy.jdbc.failFastTimeout=1
# cas.acceptableUsagePolicy.jdbc.isolationLevelName=ISOLATION_READ_COMMITTED
# cas.acceptableUsagePolicy.jdbc.dialect=org.hibernate.dialect.HSQLDialect
# cas.acceptableUsagePolicy.jdbc.leakThreshold=10
# cas.acceptableUsagePolicy.jdbc.propagationBehaviorName=PROPAGATION_REQUIRED
# cas.acceptableUsagePolicy.jdbc.batchSize=1
# cas.acceptableUsagePolicy.jdbc.user=sa
# cas.acceptableUsagePolicy.jdbc.ddlAuto=create-drop
# cas.acceptableUsagePolicy.jdbc.maxAgeDays=180
# cas.acceptableUsagePolicy.jdbc.password=
# cas.acceptableUsagePolicy.jdbc.autocommit=false
# cas.acceptableUsagePolicy.jdbc.driverClass=org.hsqldb.jpaDriver
# cas.acceptableUsagePolicy.jdbc.idleTimeout=5000
# cas.acceptableUsagePolicy.jdbc.dataSourceName=
# cas.acceptableUsagePolicy.jdbc.dataSourceProxy=false
# Hibernate-specific properties (i.e. `hibernate.globally_quoted_identifiers`)
# cas.acceptableUsagePolicy.jdbc.properties.propertyName=propertyValue
```

#### MongoDb

```properties
# cas.acceptableUsagePolicy.mongo.host=localhost
# cas.acceptableUsagePolicy.mongo.clientUri=localhost
# cas.acceptableUsagePolicy.mongo.idleTimeout=30000
# cas.acceptableUsagePolicy.mongo.port=27017
# cas.acceptableUsagePolicy.mongo.dropCollection=false
# cas.acceptableUsagePolicy.mongo.socketKeepAlive=false
# cas.acceptableUsagePolicy.mongo.password=
# cas.acceptableUsagePolicy.mongo.collection=cas-acceptableUsagePolicy-repository
# cas.acceptableUsagePolicy.mongo.databaseName=cas-mongo-database
# cas.acceptableUsagePolicy.mongo.timeout=5000
# cas.acceptableUsagePolicy.mongo.userId=
# cas.acceptableUsagePolicy.mongo.writeConcern=NORMAL
# cas.acceptableUsagePolicy.mongo.authenticationDatabaseName=
# cas.acceptableUsagePolicy.mongo.replicaSet=
# cas.acceptableUsagePolicy.mongo.sslEnabled=false
# cas.acceptableUsagePolicy.mongo.conns.lifetime=60000
# cas.acceptableUsagePolicy.mongo.conns.perHost=10
```

#### LDAP

If AUP is controlled via LDAP, decide how choices should be remembered back inside the LDAP instance.

```properties
# cas.acceptableUsagePolicy.ldap.ldapUrl=ldaps://ldap1.example.edu ldaps://ldap2.example.edu
# cas.acceptableUsagePolicy.ldap.connectionStrategy=
# cas.acceptableUsagePolicy.ldap.baseDn=dc=example,dc=org
# cas.acceptableUsagePolicy.ldap.userFilter=cn={user}
# cas.acceptableUsagePolicy.ldap.bindDn=cn=Directory Manager,dc=example,dc=org
# cas.acceptableUsagePolicy.ldap.bindCredential=Password
# cas.acceptableUsagePolicy.ldap.providerClass=org.ldaptive.provider.unboundid.UnboundIDProvider
# cas.acceptableUsagePolicy.ldap.connectTimeout=5000
# cas.acceptableUsagePolicy.ldap.trustCertificates=
# cas.acceptableUsagePolicy.ldap.keystore=
# cas.acceptableUsagePolicy.ldap.keystorePassword=
# cas.acceptableUsagePolicy.ldap.keystoreType=JKS|JCEKS|PKCS12
# cas.acceptableUsagePolicy.ldap.poolPassivator=NONE|CLOSE|BIND
# cas.acceptableUsagePolicy.ldap.minPoolSize=3
# cas.acceptableUsagePolicy.ldap.maxPoolSize=10
# cas.acceptableUsagePolicy.ldap.validateOnCheckout=true
# cas.acceptableUsagePolicy.ldap.validatePeriodically=true
# cas.acceptableUsagePolicy.ldap.validatePeriod=600
# cas.acceptableUsagePolicy.ldap.validateTimeout=5000
# cas.acceptableUsagePolicy.ldap.failFast=true
# cas.acceptableUsagePolicy.ldap.idleTime=500
# cas.acceptableUsagePolicy.ldap.prunePeriod=600
# cas.acceptableUsagePolicy.ldap.blockWaitTime=5000
# cas.acceptableUsagePolicy.ldap.useSsl=true
# cas.acceptableUsagePolicy.ldap.useStartTls=false

# cas.acceptableUsagePolicy.ldap.validator.type=NONE|SEARCH|COMPARE
# cas.acceptableUsagePolicy.ldap.validator.baseDn=
# cas.acceptableUsagePolicy.ldap.validator.searchFilter=(objectClass=*)
# cas.acceptableUsagePolicy.ldap.validator.scope=OBJECT|ONELEVEL|SUBTREE
# cas.acceptableUsagePolicy.ldap.validator.attributeName=objectClass
# cas.acceptableUsagePolicy.ldap.validator.attributeValues=top
# cas.acceptableUsagePolicy.ldap.validator.dn=
```

## REST API

To learn more about this topic, [please review this guide](../protocol/REST-Protocol.html).

```properties
# cas.rest.attributeName=
# cas.rest.attributeValue=
# cas.rest.throttler=neverThrottle|authenticationThrottle
```

## Metrics

To learn more about this topic, [please review this guide](Monitoring-Statistics.html).

```properties
# cas.metrics.loggerName=perfStatsLogger
# cas.metrics.refreshInterval=30
```

### Metrics Storage

#### Redis

```properties
# cas.metrics.redis.key=
# cas.metrics.redis.prefix=

# cas.metrics.redis.host=localhost
# cas.metrics.redis.database=0
# cas.metrics.redis.port=6380
# cas.metrics.redis.password=
# cas.metrics.redis.timeout=2000
# cas.metrics.redis.useSsl=false
# cas.metrics.redis.usePool=true

# cas.metrics.redis.pool.max-active=20
# cas.metrics.redis.pool.maxIdle=8
# cas.metrics.redis.pool.minIdle=0
# cas.metrics.redis.pool.maxActive=8
# cas.metrics.redis.pool.maxWait=-1
# cas.metrics.redis.pool.numTestsPerEvictionRun=0
# cas.metrics.redis.pool.softMinEvictableIdleTimeMillis=0
# cas.metrics.redis.pool.minEvictableIdleTimeMillis=0
# cas.metrics.redis.pool.lifo=true
# cas.metrics.redis.pool.fairness=false

# cas.metrics.redis.pool.testOnCreate=false
# cas.metrics.redis.pool.testOnBorrow=false
# cas.metrics.redis.pool.testOnReturn=false
# cas.metrics.redis.pool.testWhileIdle=false
```

#### Statsd

```properties
# cas.metrics.statsd.host=
# cas.metrics.statsd.port=8125
# cas.metrics.statsd.prefix=cas
```

#### MongoDb

```properties
# cas.metrics.mongo.host=localhost
# cas.metrics.mongo.clientUri=localhost
# cas.metrics.mongo.idleTimeout=30000
# cas.metrics.mongo.port=27017
# cas.metrics.mongo.dropCollection=false
# cas.metrics.mongo.socketKeepAlive=false
# cas.metrics.mongo.password=
# cas.metrics.mongo.collection=cas-metrics-repository
# cas.metrics.mongo.databaseName=cas-mongo-database
# cas.metrics.mongo.timeout=5000
# cas.metrics.mongo.userId=
# cas.metrics.mongo.writeConcern=NORMAL
# cas.metrics.mongo.authenticationDatabaseName=
# cas.metrics.mongo.replicaSet=
# cas.metrics.mongo.sslEnabled=false
# cas.metrics.mongo.conns.lifetime=60000
# cas.metrics.mongo.conns.perHost=10
```

#### Open TSDB

```properties
# cas.metrics.openTsdb.connectTimeout=10000
# cas.metrics.openTsdb.readTimeout=30000
# cas.metrics.openTsdb.prefix=url
```

#### InfluxDb

```properties
# cas.metrics.influxDb.url=http://localhost:8086
# cas.metrics.influxDb.username=root
# cas.metrics.influxDb.password=root
# cas.metrics.influxDb.retentionPolicy=autogen
# cas.metrics.influxDb.dropDatabase=false
# cas.metrics.influxDb.pointsToFlush=100
# cas.metrics.influxDb.batchInterval=PT5S
# cas.metrics.influxDb.consistencyLevel=ALL
```

## Groovy Shell

Control access and configuration of the embedded Groovy shell in CAS.
To learn more about this topic, [please review this guide](Configuring-Groovy-Console.html).

```properties
# shell.commandRefreshInterval=15
# shell.commandPathPatterns=classpath*:/commands/**
# shell.auth.simple.user.name=
# shell.auth.simple.user.password=
# shell.ssh.enabled=true
# shell.ssh.port=2000
# shell.telnet.enabled=false
# shell.telnet.port=5000
# shell.ssh.authTimeout=3000
# shell.ssh.idleTimeout=30000
```

## SAML Metadata UI

Control how SAML MDUI elements should be displayed on the main CAS login page
in the event that CAS is handling authentication for an external SAML2 IdP.

To learn more about this topic, [please review this guide](../integration/Shibboleth.html).

```properties
# cas.samlMetadataUi.requireValidMetadata=true
# cas.samlMetadataUi.schedule.repeatInterval=120000
# cas.samlMetadataUi.schedule.startDelay=30000
# cas.samlMetadataUi.resources=classpath:/sp-metadata::classpath:/pub.key,http://md.incommon.org/InCommon/InCommon-metadata.xml::classpath:/inc-md-pub.key
# cas.samlMetadataUi.maxValidity=0
# cas.samlMetadataUi.requireSignedRoot=false
# cas.samlMetadataUi.parameter=entityId
```

## Eureka Service Discovery

To learn more about this topic, [please review this guide](Service-Discovery-Guide.html).

```properties
# eureka.client.serviceUrl.defaultZone=${EUREKA_SERVER_HOST:http://localhost:8761}/eureka/
# eureka.client.enabled=true
# eureka.instance.statusPageUrl=${cas.server.prefix}/status/info
# eureka.instance.healthCheckUrl=${cas.server.prefix}/status/health
# eureka.instance.homePageUrl=${cas.server.prefix}/
# eureka.client.healthcheck.enabled=true

# spring.cloud.config.discovery.enabled=false
```

## Provisioning

### SCIM

Provision the authenticated CAS principal via SCIM.
To learn more about this topic, [please review this guide](../integration/SCIM-Integration.html).


```properties
# cas.scim.version=2
# cas.scim.target=
# cas.scim.oauthToken=
# cas.scim.username=
# cas.scim.password=
```

## Attribute Consent

CAS provides the ability to enforce user-informed consent upon attribute release. 
To learn more about this topic, [please review this guide](../integration/Attribute-Release-Consent.html).

```properties
# cas.consent.reminder=30
# cas.consent.reminderTimeUnit=HOURS|DAYS|MONTHS

# cas.consent.crypto.encryption.key=
# cas.consent.crypto.signing.key=
# cas.consent.crypto.enabled=true
```

### JSON Attribute Consent

```properties
# cas.consent.json.location=file:/etc/cas/config/consent.json
```

### Groovy Attribute Consent

```properties
# cas.consent.groovy.location=file:/etc/cas/config/consent.groovy
```

### JPA Attribute Consent

```properties
# cas.consent.jpa.validationQuery=SELECT 1
# cas.consent.jpa.maxWait=5000
# cas.consent.jpa.healthQuery=
# cas.consent.jpa.isolateInternalQueries=false
# cas.consent.jpa.url=jpa:hsqldb:mem:cas-hsql-database
# cas.consent.jpa.failFastTimeout=1
# cas.consent.jpa.isolationLevelName=ISOLATION_READ_COMMITTED
# cas.consent.jpa.dialect=org.hibernate.dialect.HSQLDialect
# cas.consent.jpa.leakThreshold=10
# cas.consent.jpa.propagationBehaviorName=PROPAGATION_REQUIRED
# cas.consent.jpa.batchSize=1
# cas.consent.jpa.user=sa
# cas.consent.jpa.ddlAuto=create-drop
# cas.consent.jpa.maxAgeDays=180
# cas.consent.jpa.password=
# cas.consent.jpa.autocommit=false
# cas.consent.jpa.driverClass=org.hsqldb.jpaDriver
# cas.consent.jpa.idleTimeout=5000
# cas.consent.jpa.dataSourceName=
# cas.consent.jpa.dataSourceProxy=false
# Hibernate-specific properties (i.e. `hibernate.globally_quoted_identifiers`)
# cas.consent.jpa.properties.propertyName=propertyValue
```

### LDAP Attribute Consent

```properties
# cas.consent.ldap.consentAttributeName=casConsentDecision
# cas.consent.ldap.userFilter=cn={user}
# cas.consent.ldap.subtreeSearch=true

# cas.consent.ldap.type=GENERIC|AD|FreeIPA|EDirectory
# cas.consent.ldap.ldapUrl=ldaps://ldap1.example.edu ldaps://ldap2.example.edu
# cas.consent.ldap.connectionStrategy=
# cas.consent.ldap.baseDn=dc=example,dc=org
# cas.consent.ldap.bindDn=cn=Directory Manager,dc=example,dc=org
# cas.consent.ldap.bindCredential=Password
# cas.consent.ldap.providerClass=org.ldaptive.provider.unboundid.UnboundIDProvider
# cas.consent.ldap.connectTimeout=PT5S
# cas.consent.ldap.trustCertificates=
# cas.consent.ldap.keystore=
# cas.consent.ldap.keystorePassword=
# cas.consent.ldap.keystoreType=JKS|JCEKS|PKCS12
# cas.consent.ldap.poolPassivator=NONE|CLOSE|BIND
# cas.consent.ldap.minPoolSize=3
# cas.consent.ldap.maxPoolSize=10
# cas.consent.ldap.validateOnCheckout=true
# cas.consent.ldap.validatePeriodically=true
# cas.consent.ldap.validatePeriod=PT5M
# cas.consent.ldap.validateTimeout=PT5S
# cas.consent.ldap.failFast=true
# cas.consent.ldap.idleTime=PT10M
# cas.consent.ldap.prunePeriod=PT2H
# cas.consent.ldap.blockWaitTime=PT3S
# cas.consent.ldap.useSsl=true
# cas.consent.ldap.useStartTls=false
# cas.consent.ldap.responseTimeout=PT5S
# cas.consent.ldap.allowMultipleDns=false
# cas.consent.ldap.name=

# cas.consent.ldap.saslMechanism=GSSAPI|DIGEST_MD5|CRAM_MD5|EXTERNAL
# cas.consent.ldap.saslRealm=EXAMPLE.COM
# cas.consent.ldap.saslAuthorizationId=
# cas.consent.ldap.saslMutualAuth=
# cas.consent.ldap.saslQualityOfProtection=
# cas.consent.ldap.saslSecurityStrength=

# cas.consent.ldap.validator.type=NONE|SEARCH|COMPARE
# cas.consent.ldap.validator.baseDn=
# cas.consent.ldap.validator.searchFilter=(objectClass=*)
# cas.consent.ldap.validator.scope=OBJECT|ONELEVEL|SUBTREE
# cas.consent.ldap.validator.attributeName=objectClass
# cas.consent.ldap.validator.attributeValues=top
# cas.consent.ldap.validator.dn=
```

### MongoDb Attribute Consent

```properties
# cas.consent.mongo.host=localhost
# cas.consent.mongo.clientUri=localhost
# cas.consent.mongo.idleTimeout=30000
# cas.consent.mongo.port=27017
# cas.consent.mongo.dropCollection=false
# cas.consent.mongo.socketKeepAlive=false
# cas.consent.mongo.password=
# cas.consent.mongo.collection=cas-consent-repository
# cas.consent.mongo.databaseName=cas-mongo-database
# cas.consent.mongo.timeout=5000
# cas.consent.mongo.userId=
# cas.consent.mongo.writeConcern=NORMAL
# cas.consent.mongo.authenticationDatabaseName=
# cas.consent.mongo.replicaSet=
# cas.consent.mongo.sslEnabled=false
# cas.consent.mongo.conns.lifetime=60000
# cas.consent.mongo.conns.perHost=10
```

### REST Attribute Consent

```properties
# cas.consent.rest.endpoint=https://api.example.org/trustedBrowser
```

## Apache Fortress Authentication

To learn more about this topic, [please review this guide](../integration/Configuring-Fortress-Integration.html).

```properties
# cas.authn.fortress.rbaccontext=HOME
```

## Password Management

Allow the user to update their account password, etc in-place.
To learn more about this topic, [please review this guide](Password-Policy-Enforcement.html).

```properties
# cas.authn.pm.enabled=true

# Minimum 8 and Maximum 10 characters at least 1 Uppercase Alphabet, 1 Lowercase Alphabet, 1 Number and 1 Special Character
# cas.authn.pm.policyPattern=^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@$!%*?&])[A-Za-z\\d$@$!%*?&]{8,10}

# cas.authn.pm.reset.text=Reset your password with this link: %s
# cas.authn.pm.reset.subject=Password Reset Request
# cas.authn.pm.reset.from=
# cas.authn.pm.reset.expirationMinutes=1
# cas.authn.pm.reset.emailAttribute=mail
# cas.authn.pm.reset.securityQuestionsEnabled=true

# Automatically log in after successful password change
# cas.authn.pm.autoLogin=false

# Used to sign/encrypt the password-reset link
# cas.authn.pm.reset.crypto.encryption.key=
# cas.authn.pm.reset.crypto.signing.key=
# cas.authn.pm.reset.crypto.enabled=true
```

The signing and encryption keys [are both JWKs](Configuration-Properties-Common.html#signing--encryption) of size `512` and `256`.
The encryption algorithm is set to `AES_128_CBC_HMAC_SHA_256`.

### JSON Password Management

```properties
# cas.authn.pm.json.location=classpath:jsonResourcePassword.json
```

### LDAP Password Management

The following LDAP types are supported:

| Type                    | Description                            
|-------------------------|--------------------------------------------------
| `AD`                    | Active Directory.
| `FreeIPA`               | FreeIPA Directory Server.
| `EDirectory`            | NetIQ eDirectory.
| `GENERIC`               | All other directory servers (i.e OpenLDAP, etc).

```properties
# cas.authn.pm.ldap.type=GENERIC|AD|FreeIPA|EDirectory

# cas.authn.pm.ldap.ldapUrl=ldaps://ldap1.example.edu ldaps://ldap2.example.edu
# cas.authn.pm.ldap.connectionStrategy=
# cas.authn.pm.ldap.useSsl=true
# cas.authn.pm.ldap.useStartTls=false
# cas.authn.pm.ldap.connectTimeout=5000
# cas.authn.pm.ldap.baseDn=dc=example,dc=org
# cas.authn.pm.ldap.userFilter=cn={user}
# cas.authn.pm.ldap.subtreeSearch=true
# cas.authn.pm.ldap.bindDn=cn=Directory Manager,dc=example,dc=org
# cas.authn.pm.ldap.bindCredential=Password
# cas.authn.pm.ldap.trustCertificates=
# cas.authn.pm.ldap.keystore=
# cas.authn.pm.ldap.keystorePassword=
# cas.authn.pm.ldap.keystoreType=JKS|JCEKS|PKCS12
# cas.authn.pm.ldap.poolPassivator=NONE|CLOSE|BIND
# cas.authn.pm.ldap.minPoolSize=3
# cas.authn.pm.ldap.maxPoolSize=10
# cas.authn.pm.ldap.validateOnCheckout=true
# cas.authn.pm.ldap.validatePeriodically=true
# cas.authn.pm.ldap.validatePeriod=600
# cas.authn.pm.ldap.validateTimeout=5000
# cas.authn.pm.ldap.failFast=true
# cas.authn.pm.ldap.idleTime=500
# cas.authn.pm.ldap.prunePeriod=600
# cas.authn.pm.ldap.blockWaitTime=5000
# cas.authn.pm.ldap.providerClass=org.ldaptive.provider.unboundid.UnboundIDProvider

# Attributes that should be fetched to indicate security questions and answers,
# assuming security questions are enabled.
# cas.authn.pm.ldap.securityQuestionsAttributes.attrQuestion1=attrAnswer1
# cas.authn.pm.ldap.securityQuestionsAttributes.attrQuestion2=attrAnswer2
# cas.authn.pm.ldap.securityQuestionsAttributes.attrQuestion3=attrAnswer3

# cas.authn.pm.ldap.validator.type=NONE|SEARCH|COMPARE
# cas.authn.pm.ldap.validator.baseDn=
# cas.authn.pm.ldap.validator.searchFilter=(objectClass=*)
# cas.authn.pm.ldap.validator.scope=OBJECT|ONELEVEL|SUBTREE
# cas.authn.pm.ldap.validator.attributeName=objectClass
# cas.authn.pm.ldap.validator.attributeValues=top
# cas.authn.pm.ldap.validator.dn=
```

### JDBC Password Management

```properties
# The two fields indicated below are expected to be returned
# cas.authn.pm.jdbc.sqlSecurityQuestions=SELECT question, answer FROM table WHERE user=?

# cas.authn.pm.jdbc.sqlFindEmail=SELECT email FROM table WHERE user=?
# cas.authn.pm.jdbc.sqlChangePassword=UPDATE table SET password=? WHERE user=?

# cas.authn.pm.jdbc.healthQuery=
# cas.authn.pm.jdbc.isolateInternalQueries=false
# cas.authn.pm.jdbc.url=jdbc:hsqldb:mem:cas-hsql-database
# cas.authn.pm.jdbc.failFastTimeout=1
# cas.authn.pm.jdbc.isolationLevelName=ISOLATION_READ_COMMITTED
# cas.authn.pm.jdbc.dialect=org.hibernate.dialect.HSQLDialect
# cas.authn.pm.jdbc.leakThreshold=10
# cas.authn.pm.jdbc.propagationBehaviorName=PROPAGATION_REQUIRED
# cas.authn.pm.jdbc.batchSize=1
# cas.authn.pm.jdbc.user=sa
# cas.authn.pm.jdbc.ddlAuto=create-drop
# cas.authn.pm.jdbc.maxAgeDays=180
# cas.authn.pm.jdbc.password=
# cas.authn.pm.jdbc.autocommit=false
# cas.authn.pm.jdbc.driverClass=org.hsqldb.jdbcDriver
# cas.authn.pm.jdbc.idleTimeout=5000
# cas.authn.pm.jdbc.dataSourceName=
# cas.authn.pm.jdbc.dataSourceProxy=false
# Hibernate-specific properties (i.e. `hibernate.globally_quoted_identifiers`)
# cas.authn.pm.jdbc.properties.propertyName=propertyValue

# cas.authn.pm.jdbc.passwordEncoder.type=NONE|DEFAULT|STANDARD|BCRYPT|SCRYPT|PBKDF2|com.example.CustomPasswordEncoder
# cas.authn.pm.jdbc.passwordEncoder.characterEncoding=
# cas.authn.pm.jdbc.passwordEncoder.encodingAlgorithm=
# cas.authn.pm.jdbc.passwordEncoder.secret=
# cas.authn.pm.jdbc.passwordEncoder.strength=16
```

### REST Password Management

```properties
# cas.authn.pm.rest.endpointUrlEmail=
# cas.authn.pm.rest.endpointUrlSecurityQuestions=
# cas.authn.pm.rest.endpointUrlChange=
```


