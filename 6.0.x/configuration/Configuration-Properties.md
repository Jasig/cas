---
layout: default
title: CAS Properties
category: Configuration
---

# CAS Properties

Various properties can be specified in CAS [either inside configuration files or as command
line switches](Configuration-Management.html#overview). This section provides a list common CAS properties and
references to the underlying modules that consume them.

<div class="alert alert-warning"><strong>Be Selective</strong><p>
This section is meant as a guide only. Do <strong>NOT</strong> copy/paste the entire collection of settings into your CAS configuration; rather pick only the properties that you need. Do NOT enable settings unless you are certain of their purpose and do NOT copy settings into your configuration only to keep them as <i>reference</i>. All these ideas lead to upgrade headaches, maintenance nightmares and premature aging.</p></div>

Note that property names can be specified in very relaxed terms. For instance `cas.someProperty`, `cas.some-property`, `cas.some_property` are all valid names.

The following list of properties are controlled by and provided to CAS. Each block, for most use cases, corresponds
to a specific CAS module that is expected to be included in the final CAS distribution prepared during the build
and deployment process.

<div class="alert alert-info"><strong>YAGNI</strong><p>Note that for nearly ALL use cases,
simply declaring and configuring properties listed below is sufficient. You should NOT have to
explicitly massage a CAS XML configuration file to design an authentication handler,
create attribute release policies, etc. CAS at runtime will auto-configure all required changes for you.</p></div>

## General

A number of CAS configuration options equally apply to a number of modules and features. To understand and 
take note of those options, please [review this guide](Configuration-Properties-Common.html).

## Custom Settings

The following settings could be used to extend CAS with arbitrary configuration keys and values:

```properties
# cas.custom.properties.[property-name]=[property-value]
``` 

## Configuration Storage

### Standalone

#### By Directory

CAS by default will attempt to locate settings and properties inside a given directory indicated
under the setting name `cas.standalone.configurationDirectory` and otherwise falls back to using:

1. `/etc/cas/config`
2. `/opt/cas/config`
3. `/var/cas/config`

CAS has the ability to also load a Groovy file for loading settings. The file is expected to be found at the above matching 
directory and should be named `${cas-application-name}.groovy`, such as `cas.groovy`. The script is able to combine conditional settings for active profiles and common settings that are applicable to all environments and profiles into one location with a structure that is similar to the below example:

```groovy
// Settings may be filtered by individual profiles
profiles {
    standalone {
        cas.some.setting="value"
    }
}

// This applies to all profiles and environments
cas.common.setting="value"
``` 

#### By File

There also exists a `cas.standalone.configurationFile` which can be used to directly feed a collection of properties
to CAS in form of a file or classpath resource. This is specially useful in cases where a bare CAS server is deployed in the cloud without 
the extra ceremony of a configuration server or an external directory for that matter and the deployer wishes to avoid overriding embedded configuration files.


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

Allow the CAS Spring Cloud configuration server to load settings from an internal/external Git repository.
This then allows CAS to become a client of the configuration server, consuming settings over HTTP where needed.

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

### Consul

Allow the CAS Spring Cloud configuration server to load settings from [HashiCorp's Consul](../installation/Service-Discovery-Guide-Consul.html).

```properties
# spring.cloud.consul.config.enabled=true
# spring.cloud.consul.config.prefix=configuration
# spring.cloud.consul.config.defaultContext=apps
# spring.cloud.consul.config.profileSeparator=::

# spring.cloud.consul.config.watch.delay=1000
# spring.cloud.consul.config.watch.enabled=false
```

### Vault

Allow the CAS Spring Cloud configuration server to load settings from [HashiCorp's Vault](Configuration-Properties-Security.html).

```properties
# spring.cloud.vault.host=127.0.0.1
# spring.cloud.vault.port=8200
# spring.cloud.vault.connectionTimeout=3000
# spring.cloud.vault.readTimeout=5000
# spring.cloud.vault.enabled=true
# spring.cloud.vault.fail-fast=true
# spring.cloud.vault.scheme=http
```

#### Token Authentication

Tokens are the core method for authentication within Vault. Token authentication requires a static token to be provided.

```properties
# spring.cloud.vault.authentication=TOKEN
# spring.cloud.vault.token=1305dd6a-a754-f145-3563-2fa90b0773b7
```

#### AppID Authentication

Vault supports AppId authentication that consists of two hard to guess tokens. The AppId defaults to `spring.application.name` that is statically configured. The second token is the 
UserId which is a part determined by the application, usually related to the runtime environment. Spring Cloud Vault Config supports IP address, Mac address and static 
UserId’s (e.g. supplied via System properties). The IP and Mac address are represented as Hex-encoded SHA256 hash.

Using IP addresses:

```bash
export IP_ADDRESS=`echo -n 192.168.99.1 | sha256sum`
```

```properties
# spring.cloud.vault.authentication=APPID
# spring.cloud.vault.app-id.user-id=$IP_ADDRESS
```

Using MAC address:

```bash
export $MAC_ADDRESS=`echo -n ABCDEFGH | sha256sum`
```

```properties
# spring.cloud.vault.authentication=APPID
# spring.cloud.vault.app-id.user-id=$MAC_ADDRESS
# spring.cloud.vault.app-id.network-interface=eth0
```

#### Kubernetes Authentication

Kubernetes authentication mechanism allows to authenticate with Vault using a Kubernetes Service Account Token. The authentication is role based and the role is bound to a service account name and a namespace.

```properties
# spring.cloud.vault.authentication=KUBERNETES
# spring.cloud.vault.kubernetes.role=my-dev-role
# spring.cloud.vault.kubernetes.service-account-token-file: /var/run/secrets/kubernetes.io/serviceaccount/token
```

#### Generic Backend v1

```properties
# spring.cloud.vault.generic.enabled=true
# spring.cloud.vault.generic.backend=secret
```

#### KV Backend v2

```properties
# spring.cloud.vault.kv.enabled=true
# spring.cloud.vault.kv.backend=secret
```

### MongoDb

Allow the CAS Spring Cloud configuration server to load settings from a MongoDb instance.

```properties
# cas.spring.cloud.mongo.uri=mongodb://casuser:Mellon@ds135522.mlab.com:35522/jasigcas
```

### Azure KeyVault Secrets

Allow the CAS Spring Cloud configuration server to load settings from Microsoft Azure's KeyVault instance.

```properties
# azure.keyvault.enabled=true
# azure.keyvault.uri=put-your-azure-keyvault-uri-here
# azure.keyvault.client-id=put-your-azure-client-id-here
# azure.keyvault.client-key=put-your-azure-client-key-here
# azure.keyvault.token-acquire-timeout-seconds=60
```

### ZooKeeper

Allow the CAS Spring Cloud configuration server to load settings from an Apache ZooKeeper instance.

```properties
# spring.cloud.zookeeper.connectString=localhost:2181
# spring.cloud.zookeeper.enabled=true
# spring.cloud.zookeeper.config.enabled=true
# spring.cloud.zookeeper.maxRetries=10
# spring.cloud.zookeeper.config.root=cas/config
```

### Amazon Secrets Manager

Common AWS settings for this feature are available [here](Configuration-Properties-Common.html#amazon-integration-settings)
under the configuration key `cas.spring.cloud.aws.secretsManager`.

### Amazon S3

The following settings may be passed using strategies outlined [here](Configuration-Management.html#overview) in order for CAS to establish a connection,
using the configuration key `cas.spring.cloud.aws.s3`.

```properties
# ${configurationKey}.bucketName=cas-properties
```

Common AWS settings for this feature are available [here](Configuration-Properties-Common.html#amazon-integration-settings)
under the configuration key `cas.spring.cloud.aws.s3`.

### DynamoDb

Common AWS settings for this feature are available [here](Configuration-Properties-Common.html#amazon-integration-settings)
under the configuration key `cas.spring.cloud.dynamodb`. 

### JDBC

Allow the CAS Spring Cloud configuration server to load settings from a RDBMS instance. Database settings for this feature 
are available [here](Configuration-Properties-Common.html#database-settings) under the configuration key `cas.spring.cloud.jdbc`.

```properties
# cas.spring.cloud.jdbc.sql=SELECT id, name, value FROM CAS_SETTINGS_TABLE
```

## Configuration Security

To learn more about how sensitive CAS settings can be
secured, [please review this guide](Configuration-Properties-Security.html).

### Standalone

```properties
# cas.standalone.configurationSecurity.alg=PBEWithMD5AndTripleDES
# cas.standalone.configurationSecurity.provider=BC
# cas.standalone.configurationSecurity.iterations=
# cas.standalone.configurationSecurity.psw=
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
server.servlet.context-path=/cas

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

# cas.server.tomcat.serverName=Apereo CAS
```

#### HTTP Proxying

In the event that you decide to run CAS without any SSL configuration in the embedded Tomcat container and on a non-secure port
yet wish to customize the connector configuration that is linked to the running port (i.e. `8080`), the following settings may apply:

```properties
# cas.server.tomcat.httpProxy.enabled=true
# cas.server.tomcat.httpProxy.secure=true
# cas.server.tomcat.httpProxy.protocol=AJP/1.3
# cas.server.tomcat.httpProxy.scheme=https
# cas.server.tomcat.httpProxy.redirectPort=
# cas.server.tomcat.httpProxy.proxyPort=
# cas.server.tomcat.httpProxy.attributes.attributeName=attributeValue
```

#### HTTP

Enable HTTP connections for the embedded Tomcat container, in addition to the configuration
linked to the `server.port` setting.

```properties
# cas.server.tomcat.http.port=8080
# cas.server.tomcat.http.protocol=org.apache.coyote.http11.Http11NioProtocol
# cas.server.tomcat.http.enabled=true
# cas.server.tomcat.http.attributes.attributeName=attributeValue
```

#### AJP

Enable AJP connections for the embedded Tomcat container,

```properties
# cas.server.tomcat.ajp.secure=false
# cas.server.tomcat.ajp.enabled=false
# cas.server.tomcat.ajp.proxyPort=-1
# cas.server.tomcat.ajp.protocol=AJP/1.3
# cas.server.tomcat.ajp.asyncTimeout=5000
# cas.server.tomcat.ajp.scheme=http
# cas.server.tomcat.ajp.maxPostSize=20971520
# cas.server.tomcat.ajp.port=8009
# cas.server.tomcat.ajp.enableLookups=false
# cas.server.tomcat.ajp.redirectPort=-1
# cas.server.tomcat.ajp.allowTrace=false
# cas.server.tomcat.ajp.attributes.attributeName=attributeValue
```

#### SSL Valve

The Tomcat SSLValve is a way to get a client certificate from an SSL proxy (e.g. HAProxy or BigIP F5)
running in front of Tomcat via an HTTP header. If you enable this, make sure your proxy is ensuring
that this header does not originate with the client (e.g. the browser).

```properties
# cas.server.tomcat.sslValve.enabled=false
# cas.server.tomcat.sslValve.sslClientCertHeader=ssl_client_cert
# cas.server.tomcat.sslValve.sslCipherHeader=ssl_cipher
# cas.server.tomcat.sslValve.sslSessionIdHeader=ssl_session_id
# cas.server.tomcat.sslValve.sslCipherUserKeySizeHeader=ssl_cipher_usekeysize
```

Example HAProxy Configuration (snippet): Configure SSL frontend with cert optional, redirect to cas, if cert provided, put it on header.

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
# cas.server.tomcat.extAccessLog.enabled=false
# cas.server.tomcat.extAccessLog.pattern=c-ip s-ip cs-uri sc-status time x-threadname x-H(secure) x-H(remoteUser)
# cas.server.tomcat.extAccessLog.suffix=.log
# cas.server.tomcat.extAccessLog.prefix=localhost_access_extended
# cas.server.tomcat.extAccessLog.directory=
```

#### Rewrite Valve

Enable the [rewrite valve](https://tomcat.apache.org/tomcat-8.0-doc/rewrite.html) for the embedded Tomcat container.

```properties
# cas.server.tomcat.rewriteValve.location=classpath://container/tomcat/rewrite.config
```

#### Basic Authentication

Enable basic authentication for the embedded Apache Tomcat.

```properties
# cas.server.tomcat.basicAuthn.enabled=true
# cas.server.tomcat.basicAuthn.securityRoles[0]=admin
# cas.server.tomcat.basicAuthn.authRoles[0]=admin
# cas.server.tomcat.basicAuthn.patterns[0]=/*
```

#### Session Clustering & Replication

Enable session replication to replicate web application session deltas.

```properties
# cas.server.tomcat.clustering.enabled=false
# cas.server.tomcat.clustering.clusterMembers=ip-address:port:index

# cas.server.tomcat.clustering.expireSessionsOnShutdown=false
# cas.server.tomcat.clustering.channelSendOptions=8

# cas.server.tomcat.clustering.receiverPort=4000
# cas.server.tomcat.clustering.receiverTimeout=5000
# cas.server.tomcat.clustering.receiverMaxThreads=6
# cas.server.tomcat.clustering.receiverAddress=auto
# cas.server.tomcat.clustering.receiverAutoBind=100

# cas.server.tomcat.clustering.membershipPort=45564
# cas.server.tomcat.clustering.membershipAddress=228.0.0.4
# cas.server.tomcat.clustering.membershipFrequency=500
# cas.server.tomcat.clustering.membershipDropTime=3000
# cas.server.tomcat.clustering.membershipRecoveryEnabled=true
# cas.server.tomcat.clustering.membershipLocalLoopbackDisabled=false
# cas.server.tomcat.clustering.membershipRecoveryCounter=10

# cas.server.tomcat.clustering.managerType=DELTA|BACKUP
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

## Actuator Management Endpoints

The following properties describe access controls and settings for the `/actuator`
endpoint of CAS which provides administrative functionality and oversight into the CAS software.

To learn more about this topic, [please review this guide](../monitoring/Monitoring-Statistics.html).

```properties
# management.endpoints.enabled-by-default=true
# management.endpoints.web.base-path=/actuator

# management.endpoints.web.exposure.include=info,health,status,configurationMetadata
# management.server.add-application-context-header=false
```

### Basic Authentication Security

Credentials for basic authentication may be defined via the following settings:

```properties
# spring.security.user.name=casuser
# spring.security.user.password=
# spring.security.user.roles=
```

### JAAS Authentication Security

JAAS authentication for endpoint security may be configured via the following settings:

```properties
# cas.monitor.endpoints.jaas.refreshConfigurationOnStartup=true
# cas.monitor.endpoints.jaas.loginConfig=file:/etc/cas/config/jaas.conf
# cas.monitor.endpoints.jaas.loginContextName=CAS
```

### LDAP Authentication Security

Shared LDAP settings for this feature are available [here](Configuration-Properties-Common.html#ldap-connection-settings) 
under the configuration key `cas.monitor.endpoints.ldap`.

LDAP authentication for endpoint security may be additionally configured via the following settings:

```properties
# cas.monitor.endpoints.ldap.ldapAuthz.roleAttribute=uugid
# cas.monitor.endpoints.ldap.ldapAuthz.rolePrefix=ROLE_
# cas.monitor.endpoints.ldap.ldapAuthz.allowMultipleResults=false
# cas.monitor.endpoints.ldap.ldapAuthz.groupAttribute=
# cas.monitor.endpoints.ldap.ldapAuthz.groupPrefix=
# cas.monitor.endpoints.ldap.ldapAuthz.groupFilter=
# cas.monitor.endpoints.ldap.ldapAuthz.groupBaseDn=
# cas.monitor.endpoints.ldap.ldapAuthz.baseDn=
# cas.monitor.endpoints.ldap.ldapAuthz.searchFilter=
```

### JDBC Authentication Security

Shared database settings for this feature are available [here](Configuration-Properties-Common.html#database-settings)
under the configuration key `cas.monitor.endpoints.jdbc`.

JDBC authentication for endpoint security may be additionally configured via the following settings:

```properties
# cas.monitor.endpoints.jdbc.rolePrefix=
# cas.monitor.endpoints.jdbc.query=
```

Password encoding  settings for this feature are available [here](Configuration-Properties-Common.html#password-encoding) 
under the configuration key `cas.monitor.endpoints.jdbc`.

### Enabling Endpoints

To determine whether an endpoint is available, the calculation order for all endpoints is as follows:

1. The `enabled` setting of the individual endpoint (i.e. `info`) is consulted in CAS settings, as demonstrated below:

```properties
# management.endpoint.<endpoint-name>.enabled=true
```
2. If undefined, the global endpoint security is consulted from CAS settings.
3. If undefined, the default built-in setting for the endpoint in CAS is consulted, which is typically `false` by default.

All available endpoint ids [should be listed here](../monitoring/Monitoring-Statistics.html).

Endpoints may also be mapped to custom arbitrary endpoints. For example, to remap the `health` endpoint to `healthcheck`, 
specify the following settings:

```properties
# management.endpoints.web.path-mapping.health=healthcheck
```

The `health` endpoint may also be configured to show details using `management.endpoint.health.show-details` via the following conditions:

| URL                  | Description
|----------------------|-------------------------------------------------------
| `never`              | Never display details of health monitors.
| `always`             | Always display details of health monitors.
| `when-authorized`   | Details are only shown to authorized users. Authorized roles can be configured using `management.endpoint.health.roles`.

```properties
# management.endpoint.health.show-details=never
```

### Endpoint Security

Global endpoint security configuration activated by CAS may be controlled under the configuration key `cas.monitor.endpoints.endpoint.<endpoint-name>`.
There is a special endpoint named `defaults`  which serves as a shortcut that controls the security of all endpoints, if left undefined in CAS settings. 

Note that any individual endpoint must be first enabled before any security can be applied.

The security of all endpoints is controlled using the following settings:

```properties
# ${configurationKey}.requiredRoles[0]=
# ${configurationKey}.requiredAuthorities[0]=
# ${configurationKey}.requiredIpAddresses[0]=
# ${configurationKey}.access[0]=PERMIT|ANONYMOUS|DENY|AUTHENTICATED|ROLE|AUTHORITY|IP_ADDRESS
```

The following access levels are allowed for each individual endpoint:

| Type                    | Description
|-------------------------|----------------------------------------------------------------------------------------------------
| `PERMIT`                | Allow open access to the endpoint.
| `ANONYMOUS`             | Allow anonymous access to the endpoint. 
| `DENY`                  | Default. Block access to the endpoint.
| `AUTHENTICATED`         | Require authenticated access to the endpoint.
| `ROLE`                  | Require authenticated access to the endpoint along with a role requirement.
| `AUTHORITY`             | Require authenticated access to the endpoint along with an authority requirement.
| `IP_ADDRESS`            | Require authenticated access to the endpoint using a collection of IP addresses.
    
### Spring Boot Admin Server

To learn more about this topic, [please review this guide](../monitoring/Configuring-Monitoring-Administration.html).

```properties
# spring.boot.admin.url=https://bootadmin.example.org:8444
# spring.boot.admin.client.managementUrl=${cas.server.prefix}/status
# spring.boot.admin.client.name=Apereo CAS
# spring.boot.admin.client.metadata.user.name=
# spring.boot.admin.client.metadata.user.password=
```

## Web Application Session

Control the CAS web application session behavior
as it's treated by the underlying servlet container engine.

```properties
# server.servlet.session.timeout=PT30S
# server.servlet.session.cookie.httpOnly=true
# server.servlet.session.trackingModes=COOKIE
```

## Views

Control how CAS should treat views and other UI elements.

To learn more about this topic, [please review this guide](../ux/User-Interface-Customization-Views.html).

```properties
# spring.thymeleaf.encoding=UTF-8

# Controls  whether views should be cached by CAS.
# When turned on, ad-hoc chances to views are not automatically
# picked up by CAS until a restart. Small incremental performance
# improvements are to be expected.
# spring.thymeleaf.cache=true

# Instruct CAS to locate views at the below location.
# This location can be externalized to a directory outside
# the cas web application.
# spring.thymeleaf.prefix=classpath:/templates/

# Defines a default URL to which CAS may redirect if there is no service provided in the authentication request.
# cas.view.defaultRedirectUrl=https://www.github.com

# CAS views may be located at the following paths outside
# the web application context, in addition to prefix specified
# above which is handled via Thymeleaf.
# cas.view.templatePrefixes[0]=file:///etc/cas/templates
```

### CAS v1

```properties
# Indicates how attributes should be rendered in the validation response
# cas.view.cas1.attributeRendererType=DEFAULT|VALUES_PER_LINE
```

### CAS v2

```properties
# Ensure CAS protocol v2 can behave like v3 when validating service tickets, etc.
# cas.view.cas2.v3ForwardCompatible=false

# Indicate where core CAS-protocol related views should be found in the view directory hierarchy.
# cas.view.cas2.success=protocol/2.0/casServiceValidationSuccess
# cas.view.cas2.failure=protocol/2.0/casServiceValidationFailure
# cas.view.cas2.proxy.success=protocol/2.0/casProxySuccessView
# cas.view.cas2.proxy.failure=protocol/2.0/casProxyFailureView
```

### CAS v3

```properties
# cas.view.cas3.success=protocol/3.0/casServiceValidationSuccess
# cas.view.cas3.failure=protocol/3.0/casServiceValidationFailure

# Indicates how attributes should be rendered in the validation response
# cas.view.cas3.attributeRendererType=DEFAULT|INLINE
```

### Restful Views

Control the resolution of CAS views via REST. RESTful settings for this feature are 
available [here](Configuration-Properties-Common.html#restful-integrations) under the configuration key `cas.view.rest`.

## Logging

Control the location and other settings of the CAS logging configuration.
To learn more about this topic, [please review this guide](../logging/Logging.html).

```properties
# logging.config=file:/etc/cas/log4j2.xml
# server.servlet.contextParameters.isLog4jAutoInitializationDisabled=true

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
typically via some component of [Person Directory](../integration/Attribute-Resolution.html)
from a number of attribute sources unless noted otherwise by the specific authentication scheme.

If multiple attribute repository sources are defined, they are added into a list
and their results are cached and merged.

```properties
# cas.authn.attributeRepository.expirationTime=30
# cas.authn.attributeRepository.expirationTimeUnit=MINUTES
# cas.authn.attributeRepository.maximumCacheSize=10000
# cas.authn.attributeRepository.merger=REPLACE|ADD|MERGE
```

<div class="alert alert-info"><strong>Remember This</strong><p>Note that in certain cases,
CAS authentication is able to retrieve and resolve attributes from the authentication source in the same authentication request, which would
eliminate the need for configuring a separate attribute repository specially if both the authentication and the attribute source are the same.
Using separate repositories should be required when sources are different, or when there is a need to tackle more advanced attribute
resolution use cases such as cascading, merging, etc.
<a href="../installation/Configuring-Principal-Resolution.html">See this guide</a> for more info.</p></div>

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
8. Stubbed/Static

Note that if no *explicit* attribute mappings are defined, all permitted attributes on the record
may be retrieved by CAS from the attribute repository source and made available to the principal. On the other hand,
if explicit attribute mappings are defined, then *only mapped attributes* are retrieved.

### Multimapped Attribute

Attributes may be allowed to be virtually renamed and remapped. The following definition, for instance, attempts to grab the attribute `uid` from the attribute source and rename it to `userId`:

```properties
# cas.authn.attributeRepository.[type-placeholder].attributes.uid=userId
```

### Merging Strategies

The following merging strategies can be used to resolve conflicts when the same attribute are found from multiple sources:

| Type                    | Description
|-------------------------|----------------------------------------------------------------------------------------------------
| `REPLACE`               | Overwrites existing attribute values, if any.
| `ADD`                   | Retains existing attribute values if any, and ignores values from subsequent sources in the resolution chain.
| `MERGE`                 | Combines all values into a single attribute, essentially creating a multi-valued attribute.

### Stub

Static attributes that need to be mapped to a hardcoded value belong here.

```properties
# cas.authn.attributeRepository.stub.attributes.uid=uid
# cas.authn.attributeRepository.stub.attributes.displayName=displayName
# cas.authn.attributeRepository.stub.attributes.cn=commonName
# cas.authn.attributeRepository.stub.attributes.affiliation=groupMembership
```

### LDAP

If you wish to directly and separately retrieve attributes from an LDAP source, LDAP settings for this feature are available [here](Configuration-Properties-Common.html#ldap-connection-settings) under the configuration key `cas.authn.attributeRepository.ldap[0]`.

```properties
# cas.authn.attributeRepository.ldap[0].attributes.uid=uid
# cas.authn.attributeRepository.ldap[0].attributes.displayName=displayName
# cas.authn.attributeRepository.ldap[0].attributes.cn=commonName
# cas.authn.attributeRepository.ldap[0].attributes.affiliation=groupMembership
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
    def logger = args[1]
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

Retrieve attributes from a REST endpoint. RESTful settings for this feature are available [here](Configuration-Properties-Common.html#restful-integrations) under the configuration key `cas.authn.attributeRepository.rest[0]`.

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

Similar to the Groovy option but more versatile, this option takes advantage of Java's native scripting API to invoke Groovy, Python or Javascript scripting engines to compile a pre-defined script to resolve attributes. 
The following settings are relevant:

```properties
# cas.authn.attributeRepository.script[0].location=file:/etc/cas/script.groovy
# cas.authn.attributeRepository.script[0].order=0
# cas.authn.attributeRepository.script[0].caseInsensitive=false
# cas.authn.attributeRepository.script[0].engineName=js|groovy|ruby|python
```

While Javascript and Groovy should be natively supported by CAS, Python scripts may need
to massage the CAS configuration to include the [Python modules](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22jython-standalone%22).
Ruby scripts are supported via [JRuby](https://search.maven.org/search?q=g:org.jruby%20AND%20a:jruby)  

The Groovy script may be defined as:

```groovy
import java.util.*

Map<String, List<Object>> run(final Object... args) {
    def uid = args[0]
    def logger = args[1]

    logger.debug("Groovy things are happening just fine with UID: {}",uid)
    return[username:[uid], likes:["cheese", "food"], id:[1234,2,3,4,5], another:"attribute"]
}
```

The Javascript script may be defined as:

```javascript
function run(uid, logger) {
    print("Things are happening just fine")
    logger.warn("Javascript called with UID: {}",uid);

    // If you want to call back into Java, this is one way to do so
    var javaObj = new JavaImporter(org.yourorgname.yourpackagename);
    with (javaObj) {
        var objFromJava = JavaClassInPackage.someStaticMethod(uid);
    }

    var map = {};
    map["attr_from_java"] = objFromJava.getSomething();
    map["username"] = uid;
    map["likes"] = "cheese";
    map["id"] = [1234,2,3,4,5];
    map["another"] = "attribute";

    return map;
}
```

### JDBC

Retrieve attributes from a JDBC source. Database settings for this feature are available [here](Configuration-Properties-Common.html#database-settings) under the configuration key `cas.authn.attributeRepository.jdbc[0]`.

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
```

### Grouper

This option reads all the groups from [a Grouper instance](http://www.internet2.edu/grouper/software.html) for the given CAS principal and adopts them
as CAS attributes under a `grouperGroups` multi-valued attribute.
To learn more about this topic, [please review this guide](../integration/Attribute-Resolution.html).

```properties
# cas.authn.attributeRepository.grouper[0].enabled=true
```

You will also need to ensure `grouper.client.properties` is available on the classpath (i.e. `src/main/resources`)
with the following configured properties:

```properties
# grouperClient.webService.url = http://192.168.99.100:32768/grouper-ws/servicesRest
# grouperClient.webService.login = banderson
# grouperClient.webService.password = password
```

### Couchbase

This option will fetch attributes from a Couchbase database for a given CAS principal. To learn more about this topic, [please review this guide](../installation/Couchbase-Authentication.html). Database settings for this feature are available [here](Configuration-Properties-Common.html#couchbase-integration-settings) under the configuration key `cas.authn.attributeRepository.couchbase`.

```properties
# cas.authn.attributeRepository.couchbase.usernameAttribute=username
# cas.authn.attributeRepository.couchbase.order=0
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
# cas.authn.authenticationAttributeRelease.onlyRelease=authenticationDate,isFromNewLogin
# cas.authn.authenticationAttributeRelease.neverRelease=
# cas.authn.authenticationAttributeRelease.enabled=true
```

## Principal Resolution

In the event that a separate resolver is put into place, control how the final principal should be constructed by default. Principal resolution and Person Directory settings for this feature are available [here](Configuration-Properties-Common.html#person-directory-principal-resolution) under the configuration key `cas.personDirectory`.

## Authentication Policy

To learn more about this topic, [please review this guide](../installation/Configuring-Authentication-Components.html#authentication-policy).

Global authentication policy that is applied when CAS attempts to vend and validate tickets.

```properties
# cas.authn.policy.requiredHandlerAuthenticationPolicyEnabled=false
```

### Any

Satisfied if any handler succeeds. Supports a tryAll flag to avoid short circuiting
and try every handler even if one prior succeeded.

```properties
# cas.authn.policy.any.tryAll=false
# cas.authn.policy.any.enabled=true
```

### All

Satisfied if and only if all given credentials are successfully authenticated.
Support for multiple credentials is new in CAS and this handler
would only be acceptable in a multi-factor authentication situation.

```properties
# cas.authn.policy.all.enabled=true
```

### Source Selection

Allows CAS to select authentication handlers based on the credential source. This allows the authentication engine to restrict the task of validating credentials
to the selected source or account repository, as opposed to every authentication handler.
   
```properties
# cas.authn.policy.sourceSelectionEnabled=true
```
     
### Unique Principal

Satisfied if and only if the requesting principal has not already authenticated with CAS.
Otherwise the authentication event is blocked, preventing multiple logins.

<div class="alert alert-warning"><strong>Usage Warning</strong><p>Activating this policy is not without cost,
as CAS needs to query the ticket registry and all tickets present to determine whether the current user has established an authentication session anywhere. This will surely add a performance burden to the deployment. Use with care.</p></div>

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
To learn more about this topic, [please review this guide](../installation/Configuring-Authentication-Throttling.html).

```properties
# cas.authn.throttle.usernameParameter=username
# cas.authn.throttle.schedule.startDelay=PT10S
# cas.authn.throttle.schedule.repeatInterval=PT20S
# cas.authn.throttle.appCode=CAS

# cas.authn.throttle.failure.threshold=100
# cas.authn.throttle.failure.code=AUTHENTICATION_FAILED
# cas.authn.throttle.failure.rangeSeconds=60
```

### Bucket4j

Handle capacity planning and system overload protection using rate-limiting and token buckets.

```properties
# cas.authn.throttle.bucket4j.rangeInSeconds=60
# cas.authn.throttle.bucket4j.capacity=120
# cas.authn.throttle.bucket4j.blocking=true
# cas.authn.throttle.bucket4j.overdraft=0
```

### MongoDb

Common configuration settings for this feature are available [here](Configuration-Properties-Common.html#mongodb-configuration) under the configuration key `cas.audit`. This feature uses the same data source used by the CAS MongoDb audit facility. 

### Hazelcast

Use a distributed Hazelcast map to record throttled authentication attempts. Hazelcast settings for this feature are available [here](Configuration-Properties-Common.html#hazelcast-configuration) under the configuration key `cas.authn.throttle.hazelcast`.

### Database

Queries the data source used by the CAS audit facility to prevent successive failed login attempts for a particular username from the
same IP address. 

Database settings for this feature are available [here](Configuration-Properties-Common.html#database-settings) 
under the configuration key `cas.authn.throttle.jdbc`.

```properties
# cas.authn.throttle.jdbc.auditQuery=SELECT AUD_DATE FROM COM_AUDIT_TRAIL WHERE AUD_CLIENT_IP = ? AND AUD_USER = ? \
#                                    AND AUD_ACTION = ? AND APPLIC_CD = ? AND AUD_DATE >= ? ORDER BY AUD_DATE DESC
```

### CouchDb

Queries the data source used by the CAS audit facility to prevent successive failed login attempts for a particular username from the
same IP address. CouchDb settings for this feature are available [here](Configuration-Properties-Common.html#couchdb-configuration) under the configuration key
`cas.authn.throttle`. When using this feature the audit facility should be in synchronous mode.

## Adaptive Authentication

Control how CAS authentication should adapt itself to incoming client requests.
To learn more about this topic, [please review this guide](../mfa/Configuring-Adaptive-Authentication.html).

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

### IP Address Intelligence

Examine the client IP address via the following strategies.

#### REST Adaptive Authentication

RESTful settings for this feature are available [here](Configuration-Properties-Common.html#restful-integrations) 
under the configuration key `cas.authn.adaptive.ipIntel.rest`.

#### Groovy Adaptive Authentication

```properties
# cas.authn.adaptive.ipIntel.groovy.location=file:/etc/cas/config/GroovyIPAddressIntelligenceService.groovy
```

#### BlackDot Adaptive Authentication

```properties
# cas.authn.adaptive.ipIntel.blackDot.url=http://check.getipintel.net/check.php?ip=%s
# cas.authn.adaptive.ipIntel.blackDot.emailAddress=
# cas.authn.adaptive.ipIntel.blackDot.mode=DYNA_LIST
```

## Surrogate Authentication

Authenticate on behalf of another user.
To learn more about this topic, [please review this guide](../installation/Surrogate-Authentication.html).

```properties
# cas.authn.surrogate.separator=+
```

Principal resolution and Person Directory settings for this feature 
are available [here](Configuration-Properties-Common.html#person-directory-principal-resolution) 
under the configuration key `cas.authn.surrogate.principal`.

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

LDAP settings for this feature are available [here](Configuration-Properties-Common.html#ldap-connection-settings) under the configuration key `cas.authn.surrogate.ldap`.

```properties
# cas.authn.surrogate.ldap.surrogateSearchFilter=(&(principal={user})(memberOf=cn=edu:example:cas:something:{user},dc=example,dc=edu))
# cas.authn.surrogate.ldap.memberAttributeName=memberOf
# cas.authn.surrogate.ldap.memberAttributeValueRegex=cn=edu:example:cas:something:([^,]+),.+
```

### CouchDb Surrogate Accounts

Settings for this feature are available [here](Configuration-Properties-Common.html#couchdb-configuration) under the configuration key `cas.authn.surrogate`. Surrogates may be stored either as part of the principals profile or as a series of principal/surrogate pair. The default is a key/value pair.

```properties
# cas.authn.surrogate.ldap.surrogateSearchFilter=(&(principal={user})(memberOf=cn=edu:example:cas:something:{user},dc=example,dc=edu))
# cas.authn.surrogate.ldap.memberAttributeName=memberOf
# cas.authn.surrogate.ldap.memberAttributeValueRegex=cn=edu:example:cas:something:([^,]+),.+
```

### JDBC Surrogate Accounts

 Database settings for this feature are available [here](Configuration-Properties-Common.html#database-settings) under the configuration key `cas.authn.surrogate.jdbc`.

```properties
# cas.authn.surrogate.jdbc.surrogateSearchQuery=SELECT COUNT(*) FROM surrogate WHERE username=?
# cas.authn.surrogate.jdbc.surrogateAccountQuery=SELECT surrogate_user AS surrogateAccount FROM surrogate WHERE username=?
```

### REST Surrogate Accounts

RESTful settings for this feature are available [here](Configuration-Properties-Common.html#restful-integrations) 
under the configuration key `cas.authn.surrogate.rest`.

### Notifications

Email notifications settings for this feature are available [here](Configuration-Properties-Common.html#email-notifications) 
under the configuration key `cas.authn.surrogate`. SMS notifications settings for this feature are 
available [here](Configuration-Properties-Common.html#sms-notifications) under the configuration key `cas.authn.surrogate`.

## Risk-based Authentication

Evaluate suspicious authentication requests and take action. To learn more about this topic, [please review this guide](../installation/Configuring-RiskBased-Authentication.html).

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
```

Email notifications settings for this feature are available [here](Configuration-Properties-Common.html#email-notifications) 
under the configuration key `cas.authn.adaptive.risk.response`. SMS notifications settings for this feature are 
available [here](Configuration-Properties-Common.html#sms-notifications) under the configuration key `cas.authn.adaptive.risk.response`.

## Passwordless Authentication

To learn more about this topic, [please review this guide](../installation/Passwordless-Authentication.html).

### Account Stores

```properties
# cas.authn.passwordless.accounts.simple.casuser=cas@example.org
# cas.authn.passwordless.accounts.groovy.location=file:/etc/cas/config/pwdless.groovy
```

RESTful settings for this feature are available [here](Configuration-Properties-Common.html#restful-integrations) 
under the configuration key `cas.authn.passwordless.accounts.rest`.

### Token Management

```properties
# cas.authn.passwordless.accounts.expireInSeconds=180
```

RESTful settings for this feature are available [here](Configuration-Properties-Common.html#restful-integrations) 
under the configuration key `cas.authn.passwordless.tokens.rest`. The signing key and the encryption 
key [are both JWKs](Configuration-Properties-Common.html#signing--encryption) of size `512` and `256`. 
Signing & encryption settings for this feature are available [here](Configuration-Properties-Common.html#signing--encryption) under 
the configuration key `cas.authn.passwordless.tokens.rest`.

Email notifications settings for this feature are available [here](Configuration-Properties-Common.html#email-notifications) 
under the configuration key `cas.authn.passwordless.tokens`. SMS notifications settings for this feature are 
available [here](Configuration-Properties-Common.html#sms-notifications) under the configuration key `cas.authn.passwordless.tokens`.

## Email Submissions

Email notifications settings are available [here](Configuration-Properties-Common.html#email-notifications).

## SMS Messaging

To learn more about this topic, [please review this guide](../notifications/SMS-Messaging-Configuration.html).

### Groovy

Send text messages using a Groovy script.

```properties
# cas.smsProvider.groovy.location=file:/etc/cas/config/SmsSender.groovy
```

### REST

Send text messages using a RESTful API. RESTful settings for this feature are 
available [here](Configuration-Properties-Common.html#restful-integrations) under the configuration key `cas.smsProvider.rest`.

### Twilio

Send text messaging using Twilio.

```properties
# cas.smsProvider.twilio.accountId=
# cas.smsProvider.twilio.token=
```

### TextMagic

Send text messaging using TextMagic.

```properties
# cas.smsProvider.textMagic.username=
# cas.smsProvider.textMagic.token=
# cas.smsProvider.textMagic.url=
```

### Clickatell

Send text messaging using Clickatell.

```properties
# cas.smsProvider.clickatell.serverUrl=https://platform.clickatell.com/messages
# cas.smsProvider.clickatell.token=
```

### Nexmo

Send text messaging using Nexmo.
Nexmo needs at least apiSecret or signatureSecret field set.

```properties
# cas.smsProvider.nexmo.apiToken=
# cas.smsProvider.nexmo.apiSecret=
# cas.smsProvider.nexmo.signatureSecret=
```

### Amazon SNS

Send text messaging using Amazon SNS.

```properties
# cas.smsProvider.sns.senderId=
# cas.smsProvider.sns.maxPrice=
# cas.smsProvider.sns.smsType=Transactional
```

AWS settings for this feature are available [here](Configuration-Properties-Common.html#amazon-integration-settings) 
under the configuration key `cas.smsProvider.sns`.

## GeoTracking

To learn more about this topic, [please review this guide](../installation/GeoTracking-Authentication-Requests.html).

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

To learn more about this topic, [please review this guide](../installation/Cassandra-Authentication.html).

```properties
# cas.authn.cassandra.usernameAttribute=
# cas.authn.cassandra.passwordAttribute=
# cas.authn.cassandra.tableName=
# cas.authn.cassandra.username=
# cas.authn.cassandra.password=
# cas.authn.cassandra.query=SELECT * FROM %s WHERE %s = ? ALLOW FILTERING

# cas.authn.cassandra.protocolVersion=V1|V2|V3|V4
# cas.authn.cassandra.keyspace=
# cas.authn.cassandra.contactPoints=localhost1,localhost2
# cas.authn.cassandra.localDc=
# cas.authn.cassandra.retryPolicy=DEFAULT_RETRY_POLICY|DOWNGRADING_CONSISTENCY_RETRY_POLICY|FALLTHROUGH_RETRY_POLICY
# cas.authn.cassandra.compression=LZ4|SNAPPY|NONE
# cas.authn.cassandra.consistencyLevel=ANY|ONE|TWO|THREE|QUORUM|LOCAL_QUORUM|ALL|EACH_QUORUM|LOCAL_SERIAL|SERIAL|LOCAL_ONE
# cas.authn.cassandra.serialConsistencyLevel=ANY|ONE|TWO|THREE|QUORUM|LOCAL_QUORUM|ALL|EACH_QUORUM|LOCAL_SERIAL|SERIAL|LOCAL_ONE
# cas.authn.cassandra.maxConnections=10
# cas.authn.cassandra.coreConnections=1
# cas.authn.cassandra.maxRequestsPerConnection=1024
# cas.authn.cassandra.connectTimeoutMillis=5000
# cas.authn.cassandra.readTimeoutMillis=5000
# cas.authn.cassandra.port=9042
# cas.authn.cassandra.name=
# cas.authn.cassandra.order=
```

## Digest Authentication

To learn more about this topic, [please review this guide](../installation/Digest-Authentication.html).

```properties
# cas.authn.digest.users.casuser=3530292c24102bac7ced2022e5f1036a
# cas.authn.digest.users.anotheruser=7530292c24102bac7ced2022e5f1036b
# cas.authn.digest.realm=CAS
# cas.authn.digest.name=
# cas.authn.digest.order=
# cas.authn.digest.authenticationMethod=auth
```

## Radius Authentication

To learn more about this topic, [please review this guide](../mfa/RADIUS-Authentication.html).

Principal transformation settings for this feature are available [here](Configuration-Properties-Common.html#authentication-principal-transformation) under the configuration key `cas.authn.radius`.

Password encoding  settings for this feature are available [here](Configuration-Properties-Common.html#password-encoding) under the configuration key `cas.authn.radius`.

Radius  settings for this feature are available [here](Configuration-Properties-Common.html#radius-configuration) under the configuration key `cas.authn.radius`.

```properties
# cas.authn.radius.name=
```

## File (Whitelist) Authentication

To learn more about this topic, [please review this guide](../installation/Whitelist-Authentication.html).

Principal transformation settings for this feature are available [here](Configuration-Properties-Common.html#authentication-principal-transformation) under the configuration key `cas.authn.file`.

Password encoding  settings for this feature are available [here](Configuration-Properties-Common.html#password-encoding) under the configuration key `cas.authn.file`.

```properties
# cas.authn.file.separator=::
# cas.authn.file.filename=file:///path/to/users/file
# cas.authn.file.name=
```

## JSON (Whitelist) Authentication

To learn more about this topic, [please review this guide](../installation/Whitelist-Authentication.html).

Principal transformation settings for this feature are available [here](Configuration-Properties-Common.html#authentication-principal-transformation) under the configuration key `cas.authn.json`.

Password encoding  settings for this feature are available [here](Configuration-Properties-Common.html#password-encoding) under the configuration key `cas.authn.json`.

Password policy settings for this feature are available [here](Configuration-Properties-Common.html#password-policy-settings) under the configuration key `cas.authn.json.passwordPolicy`.

```properties
# cas.authn.json.location=file:///path/to/users/file.json
# cas.authn.json.name=
```

## Reject Users (Blacklist) Authentication

To learn more about this topic, [please review this guide](../installation/Blacklist-Authentication.html).

Principal transformation settings for this feature are available [here](Configuration-Properties-Common.html#authentication-principal-transformation) under the configuration key `cas.authn.reject`.

Password encoding  settings for this feature are available [here](Configuration-Properties-Common.html#password-encoding) under the configuration key `cas.authn.reject`.

```properties
# cas.authn.reject.users=user1,user2
# cas.authn.reject.name=
```

## Database Authentication

To learn more about this topic, [please review this guide](../installation/Database-Authentication.html).

### Query Database Authentication

Authenticates a user by comparing the user password (which can be encoded with a password encoder)
against the password on record determined by a configurable database query.

Database settings for this feature are available [here](Configuration-Properties-Common.html#database-settings) under the configuration key `cas.authn.jdbc.query[0]`.

Principal transformation settings for this feature are available [here](Configuration-Properties-Common.html#authentication-principal-transformation) under the configuration key `cas.authn.jdbc.query[0]`.

Password encoding  settings for this feature are available [here](Configuration-Properties-Common.html#password-encoding) under the configuration key `cas.authn.jdbc.query[0]`.

```properties
# cas.authn.jdbc.query[0].credentialCriteria=
# cas.authn.jdbc.query[0].name=
# cas.authn.jdbc.query[0].order=0

# cas.authn.jdbc.query[0].sql=SELECT * FROM table WHERE name=?
# cas.authn.jdbc.query[0].fieldPassword=password
# cas.authn.jdbc.query[0].fieldExpired=
# cas.authn.jdbc.query[0].fieldDisabled=
# cas.authn.jdbc.query[0].principalAttributeList=sn,cn:commonName,givenName
```

### Search Database Authentication

Searches for a user record by querying against a username and password; the user is authenticated if at least one result is found.

Database settings for this feature are available [here](Configuration-Properties-Common.html#database-settings) under the configuration key `cas.authn.jdbc.search[0]`.

Principal transformation settings for this feature are available [here](Configuration-Properties-Common.html#authentication-principal-transformation) under the configuration key `cas.authn.jdbc.search[0]`.

Password encoding  settings for this feature are available [here](Configuration-Properties-Common.html#password-encoding) under the configuration key `cas.authn.jdbc.search[0]`.

```properties
# cas.authn.jdbc.search[0].fieldUser=
# cas.authn.jdbc.search[0].tableUsers=
# cas.authn.jdbc.search[0].fieldPassword=
# cas.authn.jdbc.search[0].credentialCriteria=
# cas.authn.jdbc.search[0].name=
# cas.authn.jdbc.search[0].order=0
```

### Bind Database Authentication

Authenticates a user by attempting to create a database connection using the username and (hashed) password.

Database settings for this feature are available [here](Configuration-Properties-Common.html#database-settings) under the configuration key `cas.authn.jdbc.bind[0]`.

Principal transformation settings for this feature are available [here](Configuration-Properties-Common.html#authentication-principal-transformation) under the configuration key `cas.authn.jdbc.bind[0]`.

Password encoding  settings for this feature are available [here](Configuration-Properties-Common.html#password-encoding) under the configuration key `cas.authn.jdbc.bind[0]`.

```properties
# cas.authn.jdbc.bind[0].credentialCriteria=
# cas.authn.jdbc.bind[0].name=
# cas.authn.jdbc.bind[0].order=0
```

### Encode Database Authentication

A JDBC querying handler that will pull back the password and the private salt value for a user and validate the encoded
password using the public salt value. Assumes everything is inside the same database table. Supports settings for
number of iterations as well as private salt.

This password encoding method combines the private Salt and the public salt which it prepends to the password before hashing.
If multiple iterations are used, the bytecode hash of the first iteration is rehashed without the salt values. The final hash
is converted to hex before comparing it to the database value.

Database settings for this feature are available [here](Configuration-Properties-Common.html#database-settings) under the configuration key `cas.authn.jdbc.encode[0]`.

Principal transformation settings for this feature are available [here](Configuration-Properties-Common.html#authentication-principal-transformation) under the configuration key `cas.authn.jdbc.encode[0]`.

Password encoding  settings for this feature are available [here](Configuration-Properties-Common.html#password-encoding) under the configuration key `cas.authn.jdbc.encode[0]`.

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

# cas.authn.jdbc.encode[0].credentialCriteria=
# cas.authn.jdbc.encode[0].name=
# cas.authn.jdbc.encode[0].order=0
```

## CouchDb Authentication

To learn more about this topic, [please review this guide](../installation/CouchDb-Authentication.html).

Principal transformation settings for this feature are available [here](Configuration-Properties-Common.html#authentication-principal-transformation) under the configuration key `cas.authn.couchDb`.
Password encoding  settings for this feature are available [here](Configuration-Properties-Common.html#password-encoding) under the configuration key `cas.authn.couchDb`.

Common configuration settings for this feature are available [here](Configuration-Properties-Common.html#couchdb-configuration) under the configuration key `cas.authn`.

```properties
# cas.authn.couchDb.attributes=
# cas.authn.couchDb.usernameAttribute=username
# cas.authn.couchDb.passwordAttribute=password
# cas.authn.couchDb.name=
# cas.authn.couchDb.order=
```

## MongoDb Authentication

To learn more about this topic, [please review this guide](../installation/MongoDb-Authentication.html). 

Principal transformation settings for this feature are available [here](Configuration-Properties-Common.html#authentication-principal-transformation) under the configuration key `cas.authn.mongo`. 
Password encoding  settings for this feature are available [here](Configuration-Properties-Common.html#password-encoding) under the configuration key `cas.authn.mongo`.

Common configuration settings for this feature are available [here](Configuration-Properties-Common.html#mongodb-configuration) under the configuration key `cas.authn`.

```properties
# cas.authn.mongo.attributes=
# cas.authn.mongo.usernameAttribute=username
# cas.authn.mongo.passwordAttribute=password
# cas.authn.mongo.principalIdAttribute=
# cas.authn.mongo.name=
```

## LDAP Authentication

CAS authenticates a username/password against an LDAP directory such as Active Directory or OpenLDAP.
There are numerous directory architectures and we provide configuration for four common cases.

Note that CAS will automatically create the appropriate components internally
based on the settings specified below. If you wish to authenticate against more than one LDAP
server, simply increment the index and specify the settings for the next LDAP server.

**Note:** Attributes retrieved as part of LDAP authentication are merged with all attributes
retrieved from [other attribute repository sources](#authentication-attributes), if any.
Attributes retrieved directly as part of LDAP authentication trump all other attributes.

To learn more about this topic, [please review this guide](../installation/LDAP-Authentication.html). 
LDAP settings for this feature are available [here](Configuration-Properties-Common.html#ldap-connection-settings) under the configuration key `cas.authn.ldap[0]`.

```properties
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

### LDAP Password Policy

LDAP password policy settings for this feature are available [here](Configuration-Properties-Common.html#password-policy-settings) under the configuration key `cas.authn.ldap[0].passwordPolicy`.

### LDAP Password Encoding & Principal Transformation

Principal transformation settings for this feature are available [here](Configuration-Properties-Common.html#authentication-principal-transformation) under the configuration key `cas.authn.ldap[0]`.

Password encoding  settings for this feature are available [here](Configuration-Properties-Common.html#password-encoding) under the configuration key `cas.authn.ldap[0]`.

## REST Authentication

This allows the CAS server to reach to a remote REST endpoint via a `POST`.
To learn more about this topic, [please review this guide](../installation/Rest-Authentication.html). Password encoding  settings for this feature are available [here](Configuration-Properties-Common.html#password-encoding) under the configuration key `cas.authn.rest`.

```properties
# cas.authn.rest.uri=https://...
# cas.authn.rest.name=
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

Allow CAS to become an OpenID authentication provider. To learn more about this topic, [please review this guide](../protocol/OpenID-Protocol.html).

Principal resolution and Person Directory settings for this feature 
are available [here](Configuration-Properties-Common.html#person-directory-principal-resolution) under the configuration key `cas.authn.openid.principal`.

```properties
# cas.authn.openid.enforceRpId=false
# cas.authn.openid.name=
# cas.authn.openid.order=
```

## SPNEGO Authentication

To learn more about this topic, [please review this guide](../installation/SPNEGO-Authentication.html).

Principal resolution and Person Directory settings for this feature are available [here](Configuration-Properties-Common.html#person-directory-principal-resolution) under the configuration key `cas.authn.spnego.principal`.

```properties
# cas.authn.spnego.mixedModeAuthentication=false
# cas.authn.spnego.supportedBrowsers=MSIE,Trident,Firefox,AppleWebKit
# cas.authn.spnego.send401OnAuthenticationFailure=true
# cas.authn.spnego.ntlmAllowed=true
# cas.authn.spnego.principalWithDomainName=false
# cas.authn.spnego.name=
# cas.authn.spnego.ntlm=false
```

### System Settings

```properties
# cas.authn.spnego.system.kerberosConf=
# cas.authn.spnego.system.loginConf=
# cas.authn.spnego.system.kerberosRealm=EXAMPLE.COM
# cas.authn.spnego.system.kerberosDebug=true
# cas.authn.spnego.system.useSubjectCredsOnly=false
# cas.authn.spnego.system.kerberosKdc=172.10.1.10
```

### Spnego Authentication Settings

```properties
# cas.authn.spnego.properties[0].cachePolicy=600
# cas.authn.spnego.properties[0].jcifsDomainController=
# cas.authn.spnego.properties[0].jcifsDomain=
# cas.authn.spnego.properties[0].jcifsPassword=
# cas.authn.spnego.properties[0].jcifsUsername=
# cas.authn.spnego.properties[0].jcifsServicePassword=
# cas.authn.spnego.properties[0].timeout=300000
# cas.authn.spnego.properties[0].jcifsServicePrincipal=HTTP/cas.example.com@EXAMPLE.COM
# cas.authn.spnego.properties[0].jcifsNetbiosWins=

```

### SPNEGO Client Selection Strategy

```properties
# cas.authn.spnego.hostNameClientActionStrategy=hostnameSpnegoClientAction
```

### SPNEGO Client Selection Hostname

```properties
# cas.authn.spnego.alternativeRemoteHostAttribute=alternateRemoteHeader
# cas.authn.spnego.ipsToCheckPattern=127.+
# cas.authn.spnego.dnsTimeout=2000
# cas.authn.spnego.hostNamePatternString=.+
```

### SPNEGO LDAP Integration

LDAP settings for this feature are available [here](Configuration-Properties-Common.html#ldap-connection-settings) under the configuration key `cas.authn.spnego.ldap`.

```properties
# cas.authn.spnego.spnegoAttributeName=distinguishedName
```

### NTLM Authentication

```properties
# cas.authn.ntlm.includePattern=
# cas.authn.ntlm.loadBalance=true
# cas.authn.ntlm.domainController=
# cas.authn.ntlm.name=
# cas.authn.ntlm.order=
```

## JAAS Authentication

To learn more about this topic, [please review this guide](../installation/JAAS-Authentication.html). Principal transformation settings for this feature are available [here](Configuration-Properties-Common.html#authentication-principal-transformation) under the configuration key `cas.authn.jaas[0]`. Password encoding  settings for this feature are available [here](Configuration-Properties-Common.html#password-encoding) under the configuration key `cas.authn.jaas[0]`.

```properties
# cas.authn.jaas[0].realm=CAS
# cas.authn.jaas[0].kerberosKdcSystemProperty=
# cas.authn.jaas[0].kerberosRealmSystemProperty=
# cas.authn.jaas[0].name=
# cas.authn.jaas[0].order=
# cas.authn.jaas[0].credentialCriteria=
# cas.authn.jaas[0].loginConfigType=JavaLoginConfig
# cas.authn.jaas[0].loginConfigurationFile=/path/to/jaas.conf
```

Principal resolution and Person Directory settings for this feature 
are available [here](Configuration-Properties-Common.html#person-directory-principal-resolution) 
under the configuration key `cas.authn.jaas[0].principal`.

Password policy settings for this feature are available [here](Configuration-Properties-Common.html#password-policy-settings) 
under the configuration key `cas.authn.jaas[0].passwordPolicy`.


## GUA Authentication

To learn more about this topic, [please review this guide](../installation/GUA-Authentication.html).

### LDAP Repository

LDAP settings for this feature are available [here](Configuration-Properties-Common.html#ldap-connection-settings) under the configuration key `cas.authn.gua.ldap`.

```properties
# cas.authn.gua.ldap.imageAttribute=userImageIdentifier
```

### Static Resource Repository

```properties
# cas.authn.gua.resource.location=file:/path/to/image.jpg
```

## JWT/Token Authentication

To learn more about this topic, [please review this guide](../installation/JWT-Authentication.html). Principal transformation settings for this feature are available [here](Configuration-Properties-Common.html#authentication-principal-transformation) under the configuration key `cas.authn.token`.

```properties
# cas.authn.token.name=
```

### JWT Tickets

Allow CAS tickets through various protocol channels to be created as JWTs. See [this guide](../installation/Configure-ServiceTicket-JWT.html) 
or [this guide](../protocol/REST-Protocol.html) for more info.

```properties
# cas.authn.token.crypto.encryptionEnabled=true
# cas.authn.token.crypto.signingEnabled=true
```

The signing key and the encryption key [are both JWKs](Configuration-Properties-Common.html#signing--encryption) of size `512` and `256`. Signing & encryption settings for this feature are available [here](Configuration-Properties-Common.html#signing--encryption) under the configuration key `cas.authn.token`.

## Couchbase Authentication

To learn more about this topic, [please review this guide](../installation/Couchbase-Authentication.html).

Principal transformation settings for this feature are available [here](Configuration-Properties-Common.html#authentication-principal-transformation) under the configuration key `cas.authn.couchbase`.

 Password encoding  settings for this feature are available [here](Configuration-Properties-Common.html#password-encoding) under the configuration key `cas.authn.couchbase`.

Database settings for this feature are available [here](Configuration-Properties-Common.html#couchbase-integration-settings) under the configuration key `cas.authn.couchbase`.

```properties
# cas.authn.couchbase.usernameAttribute=username
# cas.authn.couchbase.passwordAttribute=psw

# cas.authn.couchbase.name=
# cas.authn.couchbase.order=
```

## Amazon Cloud Directory Authentication

To learn more about this topic, [please review this guide](../installation/AWS-CloudDirectory-Authentication.html).

Principal transformation settings for this feature are available [here](Configuration-Properties-Common.html#authentication-principal-transformation) under the configuration key `cas.authn.cloudDirectory`.
Password encoding  settings for this feature are available [here](Configuration-Properties-Common.html#password-encoding) under the configuration key `cas.authn.cloudDirectory`.

AWS settings for this feature are available [here](Configuration-Properties-Common.html#amazon-integration-settings) 
under the configuration key `cas.authn.cloudDirectory`.

```properties
# cas.authn.cloudDirectory.directoryArn=
# cas.authn.cloudDirectory.schemaArn=
# cas.authn.cloudDirectory.facetName=

# cas.authn.cloudDirectory.usernameAttributeName=
# cas.authn.cloudDirectory.passwordAttributeName=
# cas.authn.cloudDirectory.usernameIndexPath=

# cas.authn.cloudDirectory.name=
# cas.authn.cloudDirectory.order=
```

## Amazon Cognito Authentication

To learn more about this topic, [please review this guide](../installation/AWS-Cognito-Authentication.html).

Principal transformation settings for this feature are available [here](Configuration-Properties-Common.html#authentication-principal-transformation) under the configuration key `cas.authn.cognito`.
Password encoding  settings for this feature are available [here](Configuration-Properties-Common.html#password-encoding) under the configuration key `cas.authn.cognito`.

AWS settings for this feature are available [here](Configuration-Properties-Common.html#amazon-integration-settings) under the configuration key `cas.authn.cognito`.

```properties
# cas.authn.cognito.name=
# cas.authn.cognito.order=

# cas.authn.cognito.clientId=
# cas.authn.cognito.userPoolId=
```


## SOAP Authentication

To learn more about this topic, [please review this guide](../installation/SOAP-Authentication.html).

Principal transformation settings for this feature are available [here](Configuration-Properties-Common.html#authentication-principal-transformation) under the configuration key `cas.authn.soap`.
Password encoding  settings for this feature are available [here](Configuration-Properties-Common.html#password-encoding) under the configuration key `cas.authn.soap`.

```properties
# cas.authn.soap.name=
# cas.authn.soap.order=
# cas.authn.soap.url=
```

## Remote Address Authentication

To learn more about this topic, [please review this guide](../installation/Remote-Address-Authentication.html).

```properties
# cas.authn.remoteAddress.ipAddressRange=
# cas.authn.remoteAddress.name=
# cas.authn.remoteAddress.order=
```


## Accept Users Authentication

<div class="alert alert-warning"><strong>Default Credentials</strong><p>To test the default authentication scheme in CAS,
use <strong>casuser</strong> and <strong>Mellon</strong> as the username and password respectively. These are automatically
configured via the static authentication handler, and <strong>MUST</strong> be removed from the configuration
prior to production rollouts.</p></div>

Principal transformation settings for this feature are available [here](Configuration-Properties-Common.html#authentication-principal-transformation) under the configuration key `cas.authn.accept`.
Password policy settings for this feature are available [here](Configuration-Properties-Common.html#password-policy-settings) under the configuration key `cas.authn.accept.passwordPolicy`.
Password encoding settings for this feature are available [here](Configuration-Properties-Common.html#password-encoding) under the configuration key `cas.authn.accept`.

```properties
# cas.authn.accept.users=
# cas.authn.accept.name=
# cas.authn.accept.credentialCriteria=
```

## X509 Authentication

To learn more about this topic, [please review this guide](../installation/X509-Authentication.html).

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

For the ```CN_EDIPI``` and ```SUBJECT_ALT_NAME``` principal resolvers, since not all certificates have those attributes, 
you may specify the following property in order to have a different attribute from the certificate used as the principal.  
If no alternative attribute is specified then the principal will be null and CAS will fail auth or use a different authenticator.
```properties
# cas.authn.x509.alternatePrincipalAttribute=subjectDn|sigAlgOid|subjectX500Principal
```

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
# cas.authn.x509.order=

# cas.authn.x509.principalDescriptor=
# cas.authn.x509.maxPathLength=1
# cas.authn.x509.throwOnFetchFailure=false

# cas.authn.x509.checkAll=false
# cas.authn.x509.requireKeyUsage=false
# cas.authn.x509.refreshIntervalSeconds=3600
# cas.authn.x509.maxPathLengthAllowUnspecified=false

# SERIAL_NO_DN
# cas.authn.x509.serialNoDn.serialNumberPrefix=SERIALNUMBER=
# cas.authn.x509.serialNoDn.valueDelimiter=,

# SERIAL_NO
# cas.authn.x509.serialNo.principalSNRadix=10
# cas.authn.x509.serialNo.principalHexSNZeroPadding=false

# SUBJECT_ALT_NAME
# cas.authn.x509.subjectAltName.alternatePrincipalAttribute=[sigAlgOid|subjectDn|subjectX500Principal]

# CN_EDIPI 
# cas.authn.x509.cnEdipi.alternatePrincipalAttribute=[sigAlgOid|subjectDn|subjectX500Principal]
```

### X509 Certificate Extraction

These settings can be used to turn on and configure CAS to
extract an X509 certificate from a base64 encoded certificate
on a HTTP request header (placed there by a proxy in front of CAS).
If this is set to true, it is important that the proxy cannot
be bypassed by users and that the proxy ensures the header
never originates from the browser.

```properties
cas.authn.x509.extractCert=false
cas.authn.x509.sslHeaderName=ssl_client_cert
```

The specific parsing logic for the certificate is compatible
with the Tomcat SSLValve which can work with headers set by
Apache HTTPD, Nginx, Haproxy, BigIP F5, etc.

### X509 Principal Resolution

```properties
cas.authn.x509.principalType=SERIAL_NO|SERIAL_NO_DN|SUBJECT|SUBJECT_ALT_NAME|SUBJECT_DN
```

Principal resolution and Person Directory settings for this feature are available [here](Configuration-Properties-Common.html#person-directory-principal-resolution) under the configuration key `cas.authn.x509.principal`.

### X509 LDAP Integration

LDAP settings for this feature are available [here](Configuration-Properties-Common.html#ldap-connection-settings) under the configuration key `cas.authn.x509.ldap`.

## Syncope Authentication

To learn more about this topic, [please review this guide](../installation/Syncope-Authentication.html).

Principal transformation settings for this feature are available [here](Configuration-Properties-Common.html#authentication-principal-transformation) under the configuration key `cas.authn.syncope`.

Password encoding  settings for this feature are available [here](Configuration-Properties-Common.html#password-encoding) under the configuration key `cas.authn.syncope`.

```properties
# cas.authn.syncope.domain=Master
# cas.authn.syncope.url=https://idm.instance.org/syncope
# cas.authn.syncope.name=
```

## Shiro Authentication

To learn more about this topic, [please review this guide](../installation/Shiro-Authentication.html).

Principal transformation settings for this feature are available [here](Configuration-Properties-Common.html#authentication-principal-transformation) under the configuration key `cas.authn.shiro`.

Password encoding  settings for this feature are available [here](Configuration-Properties-Common.html#password-encoding) under the configuration key `cas.authn.shiro`.

```properties
# cas.authn.shiro.requiredPermissions=value1,value2,...
# cas.authn.shiro.requiredRoles=value1,value2,...
# cas.authn.shiro.location=classpath:shiro.ini
# cas.authn.shiro.name=
```

## Trusted Authentication

To learn more about this topic, [please review this guide](../installation/Trusted-Authentication.html). Principal resolution and Person Directory settings for this feature are available [here](Configuration-Properties-Common.html#person-directory-principal-resolution) under the configuration key `cas.authn.trusted`.

```properties
# cas.authn.trusted.name=
# cas.authn.trusted.order=
# cas.authn.trusted.remotePrincipalHeader=
```

## WS-Fed Delegated Authentication

To learn more about this topic, [please review this guide](../integration/ADFS-Integration.html).

### Attribute Types

In order to construct the final authenticated principal, CAS may be configured to use the following
strategies when collecting principal attributes:

| Type                 | Description
|----------------------|------------------------------------------------------------------------------------------------
| `CAS`                | Use attributes provided by CAS' own attribute resolution mechanics and repository.
| `WSFED`              | Use attributes provided by the delegated WS-Fed instance.
| `BOTH`               | Combine both the above options, where CAS attribute repositories take precedence over WS-Fed.

```properties
# cas.authn.wsfed[0].identityProviderUrl=https://adfs.example.org/adfs/ls/
# cas.authn.wsfed[0].identityProviderIdentifier=https://adfs.example.org/adfs/services/trust
# cas.authn.wsfed[0].relyingPartyIdentifier=urn:cas:localhost
# cas.authn.wsfed[0].signingCertificateResources=classpath:adfs-signing.crt
# cas.authn.wsfed[0].identityAttribute=upn

# cas.authn.wsfed[0].attributesType=WSFED
# cas.authn.wsfed[0].tolerance=10000
# cas.authn.wsfed[0].attributeResolverEnabled=true
# cas.authn.wsfed[0].autoRedirect=true
# cas.authn.wsfed[0].name=
# cas.authn.wsfed[0].attributeMutatorScript.location=file:/etc/cas/config/wsfed-attr.groovy

# cas.authn.wsfed[0].principal.principalAttribute=
# cas.authn.wsfed[0].principal.returnNull=false

# Private/Public keypair used to decrypt assertions, if any.
# cas.authn.wsfed[0].encryptionPrivateKey=classpath:private.key
# cas.authn.wsfed[0].encryptionCertificate=classpath:certificate.crt
# cas.authn.wsfed[0].encryptionPrivateKeyPassword=NONE
```

### Signing & Encryption

The signing and encryption keys [are both JWKs](Configuration-Properties-Common.html#signing--encryption) of size `512` and `256`.
The encryption algorithm is set to `AES_128_CBC_HMAC_SHA_256`. Signing & encryption settings for this feature are available [here](Configuration-Properties-Common.html#signing--encryption) under `cas.authn.wsfed[0].cookie`.

## Multifactor Authentication

To learn more about this topic, [please review this guide](../mfa/Configuring-Multifactor-Authentication.html).

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

# Activate MFA based on an optional request header
# cas.authn.mfa.requestHeader=authn_method

# Activate MFA based on an optional request/session attribute
# cas.authn.mfa.sessionAttribute=authn_method

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

To learn more about this topic, [please review this guide](../mfa/Multifactor-TrustedDevice-Authentication.html).

```properties
# cas.authn.mfa.trusted.authenticationContextAttribute=isFromTrustedMultifactorAuthentication
# cas.authn.mfa.trusted.deviceRegistrationEnabled=true
# cas.authn.mfa.trusted.expiration=30
# cas.authn.mfa.trusted.timeUnit=SECONDS|MINUTES|HOURS|DAYS
```

#### Signing & Encryption

The signing and encryption keys [are both JWKs](Configuration-Properties-Common.html#signing--encryption) of size `512` and `256`.
The encryption algorithm is set to `AES_128_CBC_HMAC_SHA_256`. Signing & encryption settings for this feature are available [here](Configuration-Properties-Common.html#signing--encryption) under the configuration key `cas.authn.mfa.trusted`.

#### JSON Storage

```properties
# cas.authn.mfa.trusted.json.location=file:/etc/cas/config/trusted-dev.json
```

#### JDBC Storage

Database settings for this feature are available [here](Configuration-Properties-Common.html#database-settings) under the configuration key `cas.authn.mfa.trusted.jpa`.

#### CouchDb Storage

 Configuration settings for this feature are available [here](Configuration-Properties-Common.html#couchdb-configuration) under the configuration key `cas.authn.mfa.trusted`.

#### MongoDb Storage

 Configuration settings for this feature are available [here](Configuration-Properties-Common.html#mongodb-configuration) under the configuration key `cas.authn.mfa.trusted`.

#### REST Storage

```properties
# cas.authn.mfa.trusted.rest.endpoint=https://api.example.org/trustedBrowser
```

#### Trusted Device Fingerprint

```properties
# cas.authn.mfa.trusted.deviceFingerprint.componentSeparator=@
# cas.authn.mfa.trusted.deviceFingerprint.cookie.enabled=true
# cas.authn.mfa.trusted.deviceFingerprint.cookie.order=1
# cas.authn.mfa.trusted.deviceFingerprint.clientIp.enabled=true
# cas.authn.mfa.trusted.deviceFingerprint.clientIp.order=2
# cas.authn.mfa.trusted.deviceFingerprint.userAgent.enabled=false
# cas.authn.mfa.trusted.deviceFingerprint.userAgent.order=3
```

The device fingerprint cookie component can be configured with the common cookie properties found [here](Configuration-Properties-Common.html#cookie-properties) under the configuration key `cas.authn.mfa.trusted.deviceFingerprint.cookie`.
The default cookie name is set to `MFATRUSTED` and the default maxAge is set to `2592000`.

The device fingerprint cookie component supports signing & encryption. The signing and encryption keys [are both JWKs](Configuration-Properties-Common.html#signing--encryption) of size `512` and `256`.
The encryption algorithm is set to `AES_128_CBC_HMAC_SHA_256`. Signing & encryption settings for this feature are available [here](Configuration-Properties-Common.html#signing--encryption) under the configuration key `cas.authn.mfa.trusted.deviceFingerprint.cookie`.

#### Cleaner

A cleaner process is scheduled to run in the background to clean up expired and stale tickets.
This section controls how that process should behave.

```properties
# cas.authn.mfa.trusted.cleaner.schedule.startDelay=10000
# cas.authn.mfa.trusted.cleaner.schedule.repeatInterval=60000
# cas.authn.mfa.trusted.cleaner.enabled=true
```

### Simple Multifactor Authentication

To learn more about this topic, [please review this guide](../mfa/Simple-Multifactor-Authentication.html).

```properties
# cas.authn.mfa.simple.name=
# cas.authn.mfa.simple.order=
# cas.authn.mfa.simple.timeToKillInSeconds=30
```

Email notifications settings for this feature are available [here](Configuration-Properties-Common.html#email-notifications) 
under the configuration key `cas.authn.mfa.simple`. SMS notifications settings for this feature are 
available [here](Configuration-Properties-Common.html#sms-notifications) under the configuration key `cas.authn.mfa.simple`.

Multifactor authentication bypass settings for this provider are available [here](Configuration-Properties-Common.html#multifactor-authentication-bypass)
under the configuration key `cas.authn.mfa.simple`.

### Google Authenticator

To learn more about this topic, [please review this guide](../mfa/GoogleAuthenticator-Authentication.html).

```properties
# cas.authn.mfa.gauth.issuer=
# cas.authn.mfa.gauth.label=

# cas.authn.mfa.gauth.windowSize=3
# cas.authn.mfa.gauth.codeDigits=6
# cas.authn.mfa.gauth.timeStepSize=30
# cas.authn.mfa.gauth.rank=0
# cas.authn.mfa.gauth.trustedDeviceEnabled=false

# cas.authn.mfa.gauth.name=
# cas.authn.mfa.gauth.order=

# cas.authn.mfa.gauth.cleaner.enabled=true
# cas.authn.mfa.gauth.cleaner.schedule.startDelay=20000
# cas.authn.mfa.gauth.cleaner.schedule.repeatInterval=60000
```

Multifactor authentication bypass settings for this provider are available [here](Configuration-Properties-Common.html#multifactor-authentication-bypass)
under the configuration key `cas.authn.mfa.gauth`.

#### Signing & Encryption

The signing and encryption keys [are both JWKs](Configuration-Properties-Common.html#signing--encryption) of size `512` and `256`.
The encryption algorithm is set to `AES_128_CBC_HMAC_SHA_256`.  Signing & encryption settings for this feature are
available [here](Configuration-Properties-Common.html#signing--encryption) under the configuration key `cas.authn.mfa.gauth`.

#### Google Authenticator CouchDb

Configuration settings for this feature are available [here](Configuration-Properties-Common.html#couchdb-configuration) under the configuration key `cas.authn.mfa.gauth`.  

#### Google Authenticator JSON

```properties
# cas.authn.mfa.gauth.json.location=file:/somewhere.json
```

#### Google Authenticator Rest

```properties
# cas.authn.mfa.gauth.rest.endpointUrl=https://somewhere.gauth.com
```

#### Google Authenticator MongoDb

 Configuration settings for this feature are available [here](Configuration-Properties-Common.html#mongodb-configuration) under the configuration key `cas.authn.mfa.gauth`.  The following settings are additionally available for this feature:

```properties
# cas.authn.mfa.gauth.mongo.tokenCollection=MongoDbGoogleAuthenticatorTokenRepository
```

#### Google Authenticator JPA

Database settings for this feature are available [here](Configuration-Properties-Common.html#database-settings) under the configuration key `cas.authn.mfa.gauth.jpa`.

### YubiKey

To learn more about this topic, [please review this guide](../mfa/YubiKey-Authentication.html).

```properties
# cas.authn.mfa.yubikey.clientId=
# cas.authn.mfa.yubikey.secretKey=
# cas.authn.mfa.yubikey.rank=0
# cas.authn.mfa.yubikey.apiUrls=
# cas.authn.mfa.yubikey.trustedDeviceEnabled=false

# cas.authn.mfa.yubikey.name=
# cas.authn.mfa.yubikey.order=
```

Multifactor authentication bypass settings for this provider are available [here](Configuration-Properties-Common.html#multifactor-authentication-bypass) under the configuration key `cas.authn.mfa.yubikey`.

#### YubiKey JSON Device Store

```properties
# cas.authn.mfa.yubikey.jsonFile=file:/etc/cas/deviceRegistrations.json
```

#### YubiKey Whitelist Device Store

```properties
# cas.authn.mfa.yubikey.allowedDevices.uid1=yubikeyPublicId1
# cas.authn.mfa.yubikey.allowedDevices.uid2=yubikeyPublicId2
```

#### YubiKey Registration Records Encryption and Signing

```properties
# cas.authn.mfa.yubikey.crypto.enabled=true
```

The signing key and the encryption key [are both JWKs](Configuration-Properties-Common.html#signing--encryption) of size `512` and `256`. Signing & encryption settings for this feature are available [here](Configuration-Properties-Common.html#signing--encryption) under the configuration key `cas.authn.mfa.yubikey`.

### YubiKey JPA Device Store

Database settings for this feature are available [here](Configuration-Properties-Common.html#database-settings) under the configuration key `cas.authn.mfa.gauth.yubikey`.

### YubiKey CouchDb Device Store

Configuration settings for this feature are available [here](Configuration-Properties-Common.html#couchdb-configuration) under the configuration key `cas.authn.mfa.yubikey`.

### YubiKey MongoDb Device Store

 Configuration settings for this feature are available [here](Configuration-Properties-Common.html#mongodb-configuration) under the configuration key `cas.authn.mfa.yubikey`.

### Radius OTP

To learn more about this topic, [please review this guide](../mfa/RADIUS-Authentication.html).

```properties
# cas.authn.mfa.radius.rank=0
# cas.authn.mfa.radius.trustedDeviceEnabled=false
# cas.authn.mfa.radius.allowedAuthenticationAttempts=-1
# cas.authn.mfa.radius.name=
# cas.authn.mfa.radius.order=
```

Radius  settings for this feature are available [here](Configuration-Properties-Common.html#radius-configuration) under the configuration key `cas.authn.mfa.radius`.

Multifactor authentication bypass settings for this provider are available [here](Configuration-Properties-Common.html#multifactor-authentication-bypass) under the configuration key `cas.authn.mfa.radius`.

### DuoSecurity

To learn more about this topic, [please review this guide](../mfa/DuoSecurity-Authentication.html).

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
# cas.authn.mfa.duo[0].order=
```

The `duoApplicationKey` is a string, at least 40 characters long, that you generate and keep secret from Duo.
You can generate a random string in Python with:

```python
import os, hashlib
print hashlib.sha1(os.urandom(32)).hexdigest()
```

Multifactor authentication bypass settings for this provider are available [here](Configuration-Properties-Common.html#multifactor-authentication-bypass) under the configuration key `cas.authn.mfa.duo[0]`.

### FIDO U2F

To learn more about this topic, [please review this guide](../mfa/FIDO-U2F-Authentication.html).

```properties
# cas.authn.mfa.u2f.rank=0
# cas.authn.mfa.u2f.name=
# cas.authn.mfa.u2f.order=

# cas.authn.mfa.u2f.expireRegistrations=30
# cas.authn.mfa.u2f.expireRegistrationsTimeUnit=SECONDS
# cas.authn.mfa.u2f.expireDevices=30
# cas.authn.mfa.u2f.expireDevicesTimeUnit=DAYS
```

Multifactor authentication bypass settings for this provider are
available [here](Configuration-Properties-Common.html#multifactor-authentication-bypass) under the configuration key `cas.authn.mfa.u2f`.
The signing key and the encryption key [are both JWKs](Configuration-Properties-Common.html#signing--encryption) of size `512` and `256`.
Signing & encryption settings for this feature are available [here](Configuration-Properties-Common.html#signing--encryption)
under the configuration key `cas.authn.mfa.u2f`.

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

### FIDO U2F CouchDb

Common configuration settings for this feature are available [here](Configuration-Properties-Common.html#couchdb-configuration) under the configuration key `cas.authn.mfa.u2f`.

### FIDO U2F MongoDb

Common configuration settings for this feature are available [here](Configuration-Properties-Common.html#mongodb-configuration) under the configuration key `cas.authn.mfa.u2f`.

### FIDO U2F JPA

Database settings for this feature are available [here](Configuration-Properties-Common.html#database-settings) under the configuration key `cas.authn.mfa.u2f.jpa`.

### FIDO U2F REST

RESTful settings for this feature are available [here](Configuration-Properties-Common.html#restful-integrations) under the configuration key `cas.authn.mfa.u2f.rest`.

### FIDO U2F Groovy

```properties
# cas.authn.mfa.u2f.groovy.location=file:/etc/cas/config/fido.groovy
```

### Swivel Secure

To learn more about this topic, [please review this guide](../mfa/SwivelSecure-Authentication.html).

```properties
# cas.authn.mfa.swivel.swivelTuringImageUrl=https://turing.example.edu/TURingImage
# cas.authn.mfa.swivel.swivelUrl=https://swivel.example.org/pinsafe
# cas.authn.mfa.swivel.sharedSecret=Th3Sh@r3d$ecret
# cas.authn.mfa.swivel.ignoreSslErrors=false
# cas.authn.mfa.swivel.rank=0
# cas.authn.mfa.swivel.name=
# cas.authn.mfa.swivel.order=
```

Multifactor authentication bypass settings for this provider are available [here](Configuration-Properties-Common.html#multifactor-authentication-bypass) under the configuration key `cas.authn.mfa.swivel`.


### Authy

To learn more about this topic, [please review this guide](../mfa/AuthyAuthenticator-Authentication.html).

```properties
# cas.authn.mfa.authy.apiKey=
# cas.authn.mfa.authy.apiUrl=
# cas.authn.mfa.authy.phoneAttribute=phone
# cas.authn.mfa.authy.mailAttribute=mail
# cas.authn.mfa.authy.countryCode=1
# cas.authn.mfa.authy.forceVerification=true
# cas.authn.mfa.authy.trustedDeviceEnabled=false
# cas.authn.mfa.authy.name=
# cas.authn.mfa.authy.order=
```

Multifactor authentication bypass settings for this provider are available [here](Configuration-Properties-Common.html#multifactor-authentication-bypass) under the configuration key `cas.authn.mfa.authy`.

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

To learn more about this topic, [please review this guide](../installation/Configuring-SAML2-Authentication.html).

```properties
# cas.authn.samlIdp.entityId=https://cas.example.org/idp
# cas.authn.samlIdp.scope=example.org

# cas.authn.samlIdp.authenticationContextClassMappings[0]=urn:oasis:names:tc:SAML:2.0:ac:classes:SomeClassName->mfa-duo
# cas.authn.samlIdp.authenticationContextClassMappings[1]=https://refeds.org/profile/mfa->mfa-gauth

# cas.authn.samlIdp.attributeQueryProfileEnabled=true
```

### Attributes Name Formats

Name formats for an individual attribute can be mapped to a number of pre-defined formats, or a custom format of your own choosing.
A given attribute that is to be encoded in the final SAML response may contain any of the following name formats:

| Type                 | Description
|----------------------|----------------------------------------------------------------------------
| `basic`              | Map the attribute to `urn:oasis:names:tc:SAML:2.0:attrname-format:basic`.
| `uri`                | Map the attribute to `urn:oasis:names:tc:SAML:2.0:attrname-format:uri`.
| `unspecified`        | Map the attribute to `urn:oasis:names:tc:SAML:2.0:attrname-format:basic`.
| `urn:my:own:format`  | Map the attribute to `urn:my:own:format`.

### SAML Metadata

```properties
# cas.authn.samlIdp.metadata.location=file:/etc/cas/saml

# cas.authn.samlIdp.metadata.cacheExpirationMinutes=30
# cas.authn.samlIdp.metadata.failFast=true
# cas.authn.samlIdp.metadata.privateKeyAlgName=RSA
# cas.authn.samlIdp.metadata.requireValidMetadata=true

# cas.authn.samlIdp.metadata.basicAuthnUsername=
# cas.authn.samlIdp.metadata.basicAuthnPassword=
# cas.authn.samlIdp.metadata.supportedContentTypes=

```

#### SAML Metadata JPA

Database settings for this feature are available [here](Configuration-Properties-Common.html#database-settings) 
under the configuration key `cas.authn.samlIdp.metadata.jpa`.

```properties
# cas.authn.samlIdp.metadata.jpa.idpMetadataEnabled=true
```
 
 The signing key and the encryption key [are both JWKs](Configuration-Properties-Common.html#signing--encryption) of size `512` and `256`. Signing & encryption settings for this feature are available [here](Configuration-Properties-Common.html#signing--encryption) under the configuration key `cas.authn.samlIdp.metadata.jpa`.
 
#### SAML Metadata CouchDb

 Common configuration settings for this feature are available [here](Configuration-Properties-Common.html#couchdb-configuration) 
 under the configuration key `cas.authn.samlIdp.metadata`.
 
 The signing key and the encryption key [are both JWKs](Configuration-Properties-Common.html#signing--encryption) of size `512` and `256`. Signing & encryption settings for this feature are available [here](Configuration-Properties-Common.html#signing--encryption) under the configuration key `cas.authn.samlIdp.metadata.mongo`.

#### SAML Metadata MongoDb

 Common configuration settings for this feature are available [here](Configuration-Properties-Common.html#mongodb-configuration) 
 under the configuration key `cas.authn.samlIdp.metadata`.
 
```properties
# cas.authn.samlIdp.metadata.mongo.idpMetadataCollection=saml-idp-metadata
```
 
 The signing key and the encryption key [are both JWKs](Configuration-Properties-Common.html#signing--encryption) of size `512` and `256`. Signing & encryption settings for this feature are available [here](Configuration-Properties-Common.html#signing--encryption) under the configuration key `cas.authn.samlIdp.metadata.mongo`.
 
#### SAML Metadata REST
 
RESTful settings for this feature are available [here](Configuration-Properties-Common.html#restful-integrations) 
under the configuration key `cas.authn.samlIdp.metadata.rest`.

#### SAML Metadata Amazon S3
 
Common AWS settings for this feature are available [here](Configuration-Properties-Common.html#amazon-integration-settings)
under the configuration key `cas.authn.samlIdp.metadata.amazonS3`.

 The signing key and the encryption key [are both JWKs](Configuration-Properties-Common.html#signing--encryption) of size `512` and `256`. Signing & encryption settings for this feature are available [here](Configuration-Properties-Common.html#signing--encryption) under the configuration key `cas.authn.samlIdp.metadata.amazonS3`.
 
```properties
# cas.authn.samlIdp.metadata.amazonS3.bucketName=saml-sp-bucket
# cas.authn.samlIdp.metadata.mongo.idpMetadataBucketName=saml-idp-bucket
```

### SAML Logout

```properties
# cas.authn.samlIdp.logout.forceSignedLogoutRequests=true
# cas.authn.samlIdp.logout.singleLogoutCallbacksDisabled=false
```

### SAML Algorithms & Security

```properties
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

### SAML Response

```properties
# cas.authn.samlIdp.response.defaultAuthenticationContextClass=
# cas.authn.samlIdp.response.defaultAttributeNameFormat=uri
# cas.authn.samlIdp.response.signError=false
# cas.authn.samlIdp.response.signingCredentialType=X509|BASIC
# cas.authn.samlIdp.response.attributeNameFormats=attributeName->basic|uri|unspecified|custom-format-etc,...
```

### SAML Ticket

```properties
# cas.authn.samlIdp.ticket.samlArtifactsCacheStorageName=samlArtifactsCache
# cas.authn.samlIdp.ticket.samlAttributeQueryCacheStorageName=samlAttributeQueryCache
```

## SAML SPs

Allow CAS to register and enable a number of built-in SAML service provider integrations.
To learn more about this topic, [please review this guide](../integration/Configuring-SAML-SP-Integrations.html).

<div class="alert alert-warning"><strong>Remember</strong><p>SAML2 service provider integrations listed here simply attempt to automate CAS configuration based on known and documented integration guidelines and recipes provided by the service provider owned by the vendor. These recipes can change and break CAS over time.</p></div>

Configuration settings for all SAML2 service providers are [available here](Configuration-Properties-Common.html#saml2-service-provider-integrations).

| Service Provider                       | Configuration Key | Attributes
|---------------------------|----------------------------------------------------------
| Gitlab                | `cas.samlSp.gitlab` | `last_name`,`first_name`,`name`
| Hipchat               | `cas.samlSp.hipchat` | `last_name`,`first_name`,`title`
| Dropbox               | `cas.samlSp.dropbox` | `mail`
| OpenAthens            | `cas.samlSp.openAthens` | `email`, `eduPersonPrincipalName`
| Egnyte                | `cas.samlSp.egnyte` | N/A
| EverBridge            | `cas.samlSp.everBridge` | N/A
| Simplicity            | `cas.samlSp.simplicity` | N/A
| App Dynamics          | `cas.samlSp.appDynamics` | `User.OpenIDName`, `User.email`, `User.fullName`, `AccessControl`, `Groups-Membership`
| Yuja                  | `cas.samlSp.yuja` | N/A
| Simplicity            | `cas.samlSp.simplicity` | N/A
| New Relic             | `cas.samlSp.newRelic` | N/A
| Sunshine State Education and Research Computing Alliance               | `cas.samlSp.sserca` | N/A
| CherWell               | `cas.samlSp.cherWell` | N/A
| FAMIS                 | `cas.samlSp.famis` | N/A
| Bynder                | `cas.samlSp.bynder` | N/A
| Web Advisor           | `cas.samlSp.webAdvisor` | `uid`
| Adobe Creative Cloud  | `cas.samlSp.adobeCloud` | `firstName`, `lastName`, `email`
| Securing The Human    | `cas.samlSp.sansSth` | `firstName`, `lastName`, `scopedUserId`, `department`, `reference`, `email`
| Easy IEP              | `cas.samlSp.easyIep` | `employeeId`
| Infinite Campus       | `cas.samlSp.infiniteCampus` | `employeeId`
| Slack                 | `cas.samlSp.slack` | `User.Email`, `User.Username`, `first_name`, `last_name`, `employeeId`
| Zendesk               | `cas.samlSp.zendesk` | `organization`, `tags`, `phone`, `role`, `email`
| Gartner               | `cas.samlSp.gartner` | `urn:oid:2.5.4.42`, `urn:oid:2.5.4.4`, `urn:oid:0.9.2342.19200300.100.1.3`
| Arc GIS               | `cas.samlSp.arcGIS` | `arcNameId`, `mail`, `givenName`
| Benefit Focus         | `cas.samlSp.benefitFocus` | `benefitFocusUniqueId`
| Office365             | `cas.samlSp.office365` | `IDPEmail`, `ImmutableID`, `scopedImmutableID`
| SAManage              | `cas.samlSp.saManage` | `mail`
| Salesforce            | `cas.samlSp.salesforce` | `eduPersonPrincipalName`
| Workday               | `cas.samlSp.workday` | N/A
| Academic Works            | `cas.samlSp.academicWorks` | `displayName`
| ZOOM                      | `cas.samlSp.zoom` | `mail`, `sn`, `givenName`
| Evernote                  | `cas.samlSp.evernote` | `email`
| Tableau                   | `cas.samlSp.tableau` | `username`
| Asana                     | `cas.samlSp.asana` | `email`
| Box                       | `cas.samlSp.box` | `email`, `firstName`, `lastName`
| Service Now               | `cas.samlSp.serviceNow` | `eduPersonPrincipalName`
| Net Partner               | `cas.samlSp.netPartner` | `studentId`
| Webex                     | `cas.samlSp.webex` | `firstName`, `lastName`
| InCommon                  |  `cas.samlSp.inCommon` | `eduPersonPrincipalName`
| Amazon                    |  `cas.samlSp.amazon` | `awsRoles`, `awsRoleSessionName`
| Concur Solutions          | `cas.samlSp.concurSolutions` | `email`
| PollEverywhere            | `cas.samlSp.pollEverywhere` | `email`
| BlackBaud                 | `cas.samlSp.blackBaud` | `email`, `eduPersonPrincipalName`
| GiveCampus                | `cas.samlSp.giveCampus` | `email`, `givenName`, `surname`, `displayName`
| WarpWire                  | `cas.samlSp.warpWire` | `email`, `givenName`, `eduPersonPrincipalName`, `surname`, `eduPersonScopedAffiliation`, `employeeNumber`
| WarpWire                  | `cas.samlSp.rocketChat` | `email`, `cn`, `username`

**Note**: For InCommon and other metadata aggregates, multiple entity ids can be specified to filter [the InCommon metadata](https://spaces.internet2.edu/display/InCFederation/Metadata+Aggregates). EntityIds can be regular expression patterns and are mapped to CAS' `serviceId` field in the registry. The signature location MUST BE the public key used to sign the metadata.

## OpenID Connect

Allow CAS to become an OpenID Connect provider (OP). To learn more about this topic, [please review this guide](../installation/OIDC-Authentication.html).

```properties
# cas.authn.oidc.issuer=http://localhost:8080/cas/oidc

# Skew ID tokens in minutes
# cas.authn.oidc.skew=5

# cas.authn.oidc.jwksFile=file:/keystore.jwks
# cas.authn.oidc.jwksCacheInMinutes=60

# cas.authn.oidc.dynamicClientRegistrationMode=OPEN|PROTECTED

# cas.authn.oidc.subjectTypes=public,pairwise
# cas.authn.oidc.scopes=openid,profile,email,address,phone,offline_access
# cas.authn.oidc.claims=sub,name,preferred_username,family_name, \
#    given_name,middle_name,given_name,profile, \
#    picture,nickname,website,zoneinfo,locale,updated_at,birthdate, \
#    email,email_verified,phone_number,phone_number_verified,address

# cas.authn.oidc.responseTypesSupported=code,token,id_token token
# cas.authn.oidc.introspectionSupportedAuthenticationMethods=client_secret_basic
# cas.authn.oidc.claimTypesSupported=normal
# cas.authn.oidc.grantTypesSupported=authorization_code,password,client_credentials,refresh_token
# cas.authn.oidc.idTokenSigningAlgValuesSupported=none,RS256
# cas.authn.oidc.tokenEndpointAuthMethodsSupported=client_secret_basic,client_secret_post

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
# cas.authn.pac4j.principalAttributeId=
# cas.authn.pac4j.name=
```

The following external identity providers share [common blocks of settings](Configuration-Properties-Common.html#delegated-authentication-settings) under the listed configuration keys listed below:

| Identity Provider         | Configuration Key
|---------------------------|----------------------------------------------------------
| Twitter                   | `cas.authn.pac4j.twitter`
| Paypal                    | `cas.authn.pac4j.paypal`
| Wordpress                 | `cas.authn.pac4j.wordpress`
| Yahoo                     | `cas.authn.pac4j.yahoo`
| Orcid                     | `cas.authn.pac4j.orcid`
| Dropbox                   | `cas.authn.pac4j.dropbox`
| GitHub                    | `cas.authn.pac4j.github`
| Foursquare                | `cas.authn.pac4j.foursquare`
| WindowsLive               | `cas.authn.pac4j.windowsLive`
| Google                    | `cas.authn.pac4j.google`
| HiOrg-Server              | `cas.authn.pac4j.hiOrgServer`

See below for other identity providers such as CAS, SAML2 and more.

### Signing & Encryption

The signing and encryption keys [are both JWKs](Configuration-Properties-Common.html#signing--encryption) of size `512` and `256`.
The encryption algorithm is set to `AES_128_CBC_HMAC_SHA_256`. Signing & encryption settings for this feature are available [here](Configuration-Properties-Common.html#signing--encryption) under `${configurationKey}.cookie`.


### Google

In addition to the [common block of settings](Configuration-Properties-Common.html#delegated-authentication-settings) , the following properties are additionally supported, when delegating authentication to Google:

```properties
# cas.authn.pac4j.google.scope=EMAIL|PROFILE|EMAIL_AND_PROFILE
```

### CAS

Delegate authentication to an external CAS server.

```properties
# cas.authn.pac4j.cas[0].loginUrl=
# cas.authn.pac4j.cas[0].protocol=
```

### OAuth20

Delegate authentication to an generic OAuth2 server. Common settings for this identity provider are available [here](Configuration-Properties-Common.html#delegated-authentication-settings) under the configuration key `cas.authn.pac4j.oauth2[0]`.

```properties
# cas.authn.pac4j.oauth2[0].authUrl=
# cas.authn.pac4j.oauth2[0].tokenUrl=
# cas.authn.pac4j.oauth2[0].profileUrl=
# cas.authn.pac4j.oauth2[0].profilePath=
# cas.authn.pac4j.oauth2[0].profileVerb=GET|POST
# cas.authn.pac4j.oauth2[0].profileAttrs.attr1=path-to-attr-in-profile
# cas.authn.pac4j.oauth2[0].customParams.param1=value1
```

### OpenID Connect

Delegate authentication to an external OpenID Connect server.

Common settings for this identity provider are available [here](Configuration-Properties-Common.html#delegated-authentication-settings) 
under the configuration key `cas.authn.pac4j.oidc[0]`.

#### Google

Common settings for this identity provider are available [here](Configuration-Properties-Common.html#delegated-authentication-openid-connect-settings) 
under the configuration key `cas.authn.pac4j.oidc[0].google`.

#### Azure AD

Common settings for this identity provider are available [here](Configuration-Properties-Common.html#delegated-authentication-openid-connect-settings) 
under the configuration key `cas.authn.pac4j.oidc[0].azure`.

The following settings specifically apply to this provider:

```properties
# cas.authn.pac4j.oidc[0].azure.tenant=tenant-name
```

#### KeyCloak

Common settings for this identity provider are available [here](Configuration-Properties-Common.html#delegated-authentication-openid-connect-settings) 
under the configuration key `cas.authn.pac4j.oidc[0].keycloak`.

#### Generic

Common settings for this identity provider are available [here](Configuration-Properties-Common.html#delegated-authentication-openid-connect-settings) 
under the configuration key `cas.authn.pac4j.oidc[0].generic`.

### SAML2

Delegate authentication to an external SAML2 IdP (do not use the `resource:` or `classpath:`
prefixes for the `keystorePath` or `identityProviderMetadataPath` property).

```properties
# cas.authn.pac4j.saml[0].keystorePassword=
# cas.authn.pac4j.saml[0].privateKeyPassword=
# cas.authn.pac4j.saml[0].keystorePath=
# cas.authn.pac4j.saml[0].keystoreAlias=

# cas.authn.pac4j.saml[0].serviceProviderEntityId=
# cas.authn.pac4j.saml[0].serviceProviderMetadataPath=

# cas.authn.pac4j.saml[0].maximumAuthenticationLifetime=3600
# cas.authn.pac4j.saml[0].maximumAuthenticationLifetime=300
# cas.authn.pac4j.saml[0].destinationBinding=urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect

# Path/URL to delegated IdP metadata
# cas.authn.pac4j.saml[0].identityProviderMetadataPath=

# cas.authn.pac4j.saml[0].authnContextClassRef=
# cas.authn.pac4j.saml[0].authnContextComparisonType=
# cas.authn.pac4j.saml[0].nameIdPolicyFormat=
# cas.authn.pac4j.saml[0].forceAuth=false
# cas.authn.pac4j.saml[0].passive=false

# cas.authn.pac4j.saml[0].wantsAssertionsSigned=
# cas.authn.pac4j.saml[0].signServiceProviderMetadata=false
# cas.authn.pac4j.saml[0].principalIdAttribute=eduPersonPrincipalName
# cas.authn.pac4j.saml[0].useNameQualifier=true
# cas.authn.pac4j.saml[0].attributeConsumingServiceIndex=
# cas.authn.pac4j.saml[0].assertionConsumerServiceIndex=-1

# cas.authn.pac4j.saml[0].requestedAttributes[0].name=
# cas.authn.pac4j.saml[0].requestedAttributes[0].friendlyName=
# cas.authn.pac4j.saml[0].requestedAttributes[0].nameFormat=urn:oasis:names:tc:SAML:2.0:attrname-format:uri
# cas.authn.pac4j.saml[0].requestedAttributes[0].required=false

# cas.authn.pac4j.saml[0].mappedAttributes[0].name=urn:oid:2.5.4.42
# cas.authn.pac4j.saml[0].mappedAttributes[0].mappedAs=displayName
```

Examine the generated metadata after accessing the CAS login screen to ensure all ports and endpoints are correctly adjusted.  Finally, share the CAS SP metadata with the delegated IdP and register CAS as an authorized relying party.

### Facebook

Delegate authentication to Facebook. Common settings for this identity provider are available [here](Configuration-Properties-Common.html#delegated-authentication-settings) under the configuration key `cas.authn.pac4j.facebook`.

```properties
# cas.authn.pac4j.facebook.fields=
# cas.authn.pac4j.facebook.scope=
```

### HiOrg Server

Delegate authentication to HiOrg Server. Common settings for this identity provider are available [here](Configuration-Properties-Common.html#delegated-authentication-settings) under the configuration key `cas.authn.pac4j.hiOrgServer`.

```properties
# cas.authn.pac4j.hiOrgServer.scope=eigenedaten
```

### LinkedIn

Delegate authentication to LinkedIn. Common settings for this identity provider are available [here](Configuration-Properties-Common.html#delegated-authentication-settings) under the configuration key `cas.authn.pac4j.linkedin`.

```properties
# cas.authn.pac4j.linkedIn.fields=
# cas.authn.pac4j.linkedIn.scope=
```

### Twitter
Delegate authentication to Twitter.  Common settings for this identity provider are available [here](Configuration-Properties-Common.html#delegated-authentication-settings) under the configuration key `cas.authn.pac4j.twitter`.

```properties
# cas.authn.pac4j.twitter.includeEmail=false
```

## WS Federation

Allow CAS to act as an identity provider and security token service
to support the WS-Federation protocol.

To learn more about this topic, [please review this guide](../protocol/WS-Federation-Protocol.html)

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

The signing and encryption keys [are both JWKs](Configuration-Properties-Common.html#signing--encryption) of size `512` and `256`. The encryption algorithm is set to `AES_128_CBC_HMAC_SHA_256`.  These come into play in order to secure authentication requests between the IdP and STS. Signing & encryption settings for this feature are available [here](Configuration-Properties-Common.html#signing--encryption) under the configuration key `cas.authn.wsfedIdp.sts`.

## OAuth2

Allows CAS to act as an OAuth2 provider. Here you can control how long various tokens issued by CAS should last, etc.

To learn more about this topic, [please review this guide](../installation/OAuth-OpenId-Authentication.html).

```properties
# cas.authn.oauth.refreshToken.timeToKillInSeconds=2592000

# cas.authn.oauth.code.timeToKillInSeconds=30
# cas.authn.oauth.code.numberOfUses=1

# cas.authn.oauth.accessToken.timeToKillInSeconds=7200
# cas.authn.oauth.accessToken.maxTimeToLiveInSeconds=28800

# cas.authn.oauth.deviceToken.timeToKillInSeconds=2592000
# cas.authn.oauth.deviceToken.refreshInterval=PT15S
# cas.authn.oauth.deviceToken.userCodeLength=8

# cas.authn.oauth.grants.resourceOwner.requireServiceHeader=true

# cas.authn.oauth.userProfileViewType=NESTED|FLAT
```

### OAuth2 UMA

To learn more about this topic, [please review this guide](../installation/OAuth-OpenId-Authentication.html).

```properties
# cas.authn.uma.issuer=http://localhost:8080/cas

# cas.authn.uma.requestingPartyToken.maxTimeToLiveInSeconds=PT3M
# cas.authn.uma.requestingPartyToken.jwksFile=file:/etc/cas/uma-keystore.jwks

# cas.authn.uma.permissionTicket.maxTimeToLiveInSeconds=PT3M
```

#### OAuth2 UMA JPA

Database settings for this feature are available [here](Configuration-Properties-Common.html#database-settings) under the configuration key `cas.authn.uma.resourceSet.jpa`.

## Localization

To learn more about this topic, [please review this guide](../ux/User-Interface-Customization-Localization.html).

```properties
# cas.locale.paramName=locale
# cas.locale.defaultValue=en
```

## Global SSO Behavior

```properties
# cas.sso.allowMissingServiceParameter=true
# cas.sso.createSsoCookieOnRenewAuthn=true
# cas.sso.proxyAuthnEnabled=true
# cas.sso.renewAuthnEnabled=true
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
# cas.tgc.pinToSession=true
```

### Signing & Encryption

The signing and encryption keys [are both JWKs](Configuration-Properties-Common.html#signing--encryption) of size `512` and `256`.
The encryption algorithm is set to `AES_128_CBC_HMAC_SHA_256`.
Signing & encryption settings for this feature are available [here](Configuration-Properties-Common.html#signing--encryption) under the configuration key `cas.tgc`.

## Logout

Control various settings related to CAS logout functionality. To learn more about this topic, [please review this guide](../installation/Logout-Single-Signout.html).

```properties
# cas.logout.followServiceRedirects=false
# cas.logout.redirectParameter=service
# cas.logout.redirectUrl=https://www.github.com
# cas.logout.confirmLogout=false
# cas.logout.removeDescendantTickets=false
```

## Single Logout

To learn more about this topic, [please review this guide](../installation/Logout-Single-Signout.html).

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
```

The signing and encryption keys [are both JWKs](Configuration-Properties-Common.html#signing--encryption) of size `512` and `256`. The encryption algorithm is set to `AES_128_CBC_HMAC_SHA_256`. Signing & encryption settings for this feature are available [here](Configuration-Properties-Common.html#signing--encryption) under the configuration key `cas.clearpass`.

## Message Bundles

To learn more about this topic, [please review this guide](../ux/User-Interface-Customization-Localization.html).
The baseNames are message bundle base names representing files that either end in .properties or _xx.properties where xx is a country locale code. The commonNames are not actually message bundles but they are properties files that are merged together and contain keys that are only used if they are not found in the message bundles. Keys from the later files in the list will be preferred over keys from the earlier files.

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
To learn more about this topic, [please review this guide](../installation/Audits.html).

```properties
# cas.audit.ignoreAuditFailures=false
# cas.audit.appCode=CAS
# cas.audit.numberOfDaysInHistory=30
# cas.audit.includeValidationAssertion=false
# cas.audit.alternateServerAddrHeaderName=
# cas.audit.alternateClientAddrHeaderName=X-Forwarded-For
# cas.audit.useServerHostAddress=false
```

### Slf4j Audits

Route audit logs to the Slf4j logging system which might in turn store audit logs in a file or any other
destination that the logging system supports.

The logger name is fixed at `org.apereo.inspektr.audit.support`.

```xml
<AsyncLogger name="org.apereo.inspektr.audit.support" level="info">
    <!-- Route the audit data to any number of appenders supported by the logging framework. -->
</AsyncLogger>
```

<div class="alert alert-info"><strong></strong><p>Audit records routed to the Slf4j log are not
able to read the audit data back given the abstraction layer between CAS, the logging system
and any number of log appenders that might push data to a variety of systems.</p></div>

```properties
# cas.audit.slf4j.auditFormat=DEFAULT|JSON
# cas.audit.slf4j.singlelineSeparator=|
# cas.audit.slf4j.useSingleLine=false
```

### MongoDb Audits

Store audit logs inside a MongoDb database.

Common configuration settings for this feature are available 
[here](Configuration-Properties-Common.html#mongodb-configuration) under the configuration key `cas.audit`.

### CouchDb Audits

Store audit logs inside a CouchDb database.

Common configuration settings for this feature are available [here](Configuration-Properties-Common.html#couchdb-configuration)
under the configuration key `cas.audit`.

### Couchbase Audits

Store audit logs inside a Couchbase database.

Database settings for this feature are available [here](Configuration-Properties-Common.html#couchbase-integration-settings) 
under the configuration key `cas.audit.couchbase`.

### Database Audits

Store audit logs inside a database. Database settings for this feature are available [here](Configuration-Properties-Common.html#database-settings)
under the configuration key `cas.audit.jdbc`.

```properties
# cas.audit.jdbc.asynchronous=true
# cas.audit.jdbc.maxAgeDays=180
# cas.audit.jdbc.columnLength=100
# cas.audit.jdbc.isolationLevelName=ISOLATION_READ_COMMITTED
# cas.audit.jdbc.propagationBehaviorName=PROPAGATION_REQUIRED
```

### REST Audits

Store audit logs inside a database. RESTful settings for this feature are 
available [here](Configuration-Properties-Common.html#restful-integrations) under the configuration key `cas.audit.rest`.

## Sleuth Distributed Tracing

To learn more about this topic, [please review this guide](../monitoring/Monitoring-Statistics.html#distributed-tracing).

```properties
# spring.sleuth.sampler.percentage = 0.5
# spring.sleuth.enabled=true

# spring.zipkin.enabled=true
# spring.zipkin.baseUrl=http://localhost:9411/
```

## Monitoring

To learn more about this topic, [please review this guide](../monitoring/Monitoring-Statistics.html).

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
Integration settings for this registry are available [here](Configuration-Properties-Common.html#memcached-integration-settings) 
under the configuration key `cas.monitor.memcached`.

### MongoDb Monitors

Decide how CAS should monitor the internal state of a MongoDb instance.  
Common configuration settings for this feature are available [here](Configuration-Properties-Common.html#mongodb-configuration) 
under the configuration key `cas.monitor`.

### Database Monitoring

Decide how CAS should monitor the internal state of JDBC connections used
for authentication or attribute retrieval. Database settings for this feature are 
available [here](Configuration-Properties-Common.html#database-settings) under the configuration key `cas.monitor.jdbc`.

```properties
# cas.monitor.jdbc.validationQuery=SELECT 1
# cas.monitor.jdbc.maxWait=5000
```

### LDAP Server Monitoring

Decide how CAS should monitor the LDAP server it uses for authentication, etc.  
LDAP settings for this feature are 
available [here](Configuration-Properties-Common.html#ldap-connection-settings) under the configuration key `cas.monitor.ldap`.
The default for ```cas.monitor.ldap.minPoolSize``` is zero to prevent failed ldap pool initialization to impact server startup.

The following properties are specific to the ldap monitor and configure the thread pool 
that will ping on the LDAP monitor connection pool.

```properties
# cas.monitor.ldap.maxWait=5000
# cas.monitor.ldap.pool.minSize=6
# cas.monitor.ldap.pool.maxSize=18
```

### Memory

Decide how CAS should monitor the internal state of JVM memory available at runtime.

```properties
# cas.monitor.freeMemThreshold=10
```

## Themes

To learn more about this topic, [please review this guide](../ux/User-Interface-Customization-Themes.html).

```properties
# cas.theme.paramName=theme
# cas.theme.defaultThemeName=cas-theme-default
```

## Events

Decide how CAS should track authentication events.
To learn more about this topic, [please review this guide](../installation/Configuring-Authentication-Events.html).


```properties
# Whether geolocation tracking should be turned on and requested from the browser.
# cas.events.trackGeolocation=false

# Control whether CAS should monitor configuration files and auto-refresh context.
# cas.events.trackConfigurationModifications=true
```

### InfluxDb Events

Decide how CAS should store authentication events inside an InfluxDb instance. Common 
configuration settings for this feature are available [here](Configuration-Properties-Common.html#influxdb-configuration) under the configuration key `cas.events.influxDb`.

### CouchDb Events

Decide how CAS should store authentication events inside a CouchDb instance. Common
configuration settings for this feature are available [here](Configuration-Properties-Common.html#couchdb-configuration) under the configuration key `cas.events.couchDb`.

### Database Events

Decide how CAS should store authentication events inside a database instance. Database 
settings for this feature are available [here](Configuration-Properties-Common.html#database-settings) under the configuration key `cas.events.jpa`.

### MongoDb Events

Decide how CAS should store authentication events inside a MongoDb instance. Common 
configuration settings for this feature are available [here](Configuration-Properties-Common.html#mongodb-configuration) under the configuration key `cas.events`.

## Http Web Requests

Control how CAS should respond and validate incoming HTTP requests.

```properties
# cas.httpWebRequest.header.xframe=true
# cas.httpWebRequest.header.xframeOptions=DENY

# cas.httpWebRequest.header.xss=true
# cas.httpWebRequest.header.xssOptions=1; mode=block

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

# cas.httpWebRequest.customHeaders.headerName1=headerValue1
# cas.httpWebRequest.customHeaders.headerName2=headerValue2

# spring.http.encoding.charset=UTF-8
# spring.http.encoding.enabled=true
# spring.http.encoding.force=true
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
# cas.httpClient.authorityValidationRegEx=
# cas.httpClient.authorityValidationRegExCaseSensitive=true

# cas.httpClient.truststore.psw=changeit
# cas.httpClient.truststore.file=classpath:/truststore.jks
```

### Hostname Verification

The default options are available for hostname verification:

| Type                    | Description
|-------------------------|--------------------------------------
| `NONE`                  | Ignore hostname verification.
| `DEFAULT`               | Enforce hostname verification.

## Service Registry

See [this guide](../services/Service-Management.html) to learn more.

```properties
# cas.serviceRegistry.watcherEnabled=true

# cas.serviceRegistry.schedule.repeatInterval=120000
# cas.serviceRegistry.schedule.startDelay=15000

# Auto-initialize the registry from default JSON service definitions
# cas.serviceRegistry.initFromJson=false

# cas.serviceRegistry.managementType=DEFAULT|DOMAIN
```

### Service Registry Notifications

Email notifications settings for this feature are available [here](Configuration-Properties-Common.html#email-notifications) 
under the configuration key `cas.serviceRegistry`. SMS notifications settings for this feature are 
available [here](Configuration-Properties-Common.html#sms-notifications) under the configuration key `cas.serviceRegistry`.

### JSON Service Registry

If the underlying service registry is using local system resources
to locate JSON service definitions, decide how those resources should be found.

```properties
# cas.serviceRegistry.json.location=classpath:/services
```

To learn more about this topic, [please review this guide](../services/JSON-Service-Management.html).

### YAML Service Registry

If the underlying service registry is using local system resources
to locate YAML service definitions, decide how those resources should be found.

```properties
# cas.serviceRegistry.yaml.location=classpath:/services
```

To learn more about this topic, [please review this guide](../services/YAML-Service-Management.html).

### RESTful Service Registry

To learn more about this topic, [please review this guide](../services/REST-Service-Management.html).

```properties
# cas.serviceRegistry.rest.url=https://example.api.org
# cas.serviceRegistry.rest.basicAuthUsername=
# cas.serviceRegistry.rest.basicAuthPassword=
```

### CouchDb Service Registry

To learn more about this topic, [please review this guide](../services/CouchDb-Service-Management.html). Common configuration settings for this feature are available [here](Configuration-Properties-Common.html#couchdb-configuration) under the configuration key `cas.serviceRegistry`.

### Redis Service Registry

To learn more about this topic, [please review this guide](../services/Redis-Service-Management.html). Common configuration settings for this feature are available [here](Configuration-Properties-Common.html#redis-configuration) under the configuration key `cas.serviceRegistry`.

### CosmosDb Service Registry

To learn more about this topic, [please review this guide](../services/CosmosDb-Service-Management.html).

```properties
# cas.serviceRegistry.cosmosDb.uri=
# cas.serviceRegistry.cosmosDb.key=
# cas.serviceRegistry.cosmosDb.database=
# cas.serviceRegistry.cosmosDb.collection=
# cas.serviceRegistry.cosmosDb.throughput=10000
# cas.serviceRegistry.cosmosDb.dropCollection=true
# cas.serviceRegistry.cosmosDb.consistencyLevel=Session
```

### DynamoDb Service Registry

To learn more about this topic, [please review this guide](../services/DynamoDb-Service-Management.html).
Common configuration settings for this feature are available [here](Configuration-Properties-Common.html#dynamodb-configuration)
under the configuration key `cas.serviceRegistry`.
AWS settings for this feature are available [here](Configuration-Properties-Common.html#amazon-integration-settings) 
under the configuration key `cas.serviceRegistry.dynamoDb`.

```properties
# cas.serviceRegistry.dynamoDb.tableName=DynamoDbCasServices
```

### MongoDb Service Registry

Store CAS service definitions inside a MongoDb instance. To learn more about this topic, [please review this guide](../services/MongoDb-Service-Management.html).
 Common configuration settings for this feature are available [here](Configuration-Properties-Common.html#mongodb-configuration) under the configuration key `cas.serviceRegistry`.

### LDAP Service Registry

Control how CAS services should be found inside an LDAP instance.
To learn more about this topic, [please review this guide](../services/LDAP-Service-Management.html).  LDAP settings for this feature are available [here](Configuration-Properties-Common.html#ldap-connection-settings) under the configuration key `cas.serviceRegistry.ldap`.

```properties
# cas.serviceRegistry.ldap.serviceDefinitionAttribute=description
# cas.serviceRegistry.ldap.idAttribute=uid
# cas.serviceRegistry.ldap.objectClass=casRegisteredService
# cas.serviceRegistry.ldap.searchFilter=(%s={0})
# cas.serviceRegistry.ldap.loadFilter=(objectClass=%s)
```

### Couchbase Service Registry

Control how CAS services should be found inside a Couchbase instance.
To learn more about this topic, [please review this guide](../services/Couchbase-Service-Management.html). 
Database settings for this feature are available [here](Configuration-Properties-Common.html#couchbase-integration-settings) 
under the configuration key `cas.serviceRegistry.couchbase`.

### Database Service Registry

Control how CAS services should be found inside a database instance.
To learn more about this topic, [please review this guide](../services/JPA-Service-Management.html). 
Database settings for this feature are available [here](Configuration-Properties-Common.html#database-settings) 
under the configuration key `cas.serviceRegistry.jpa`.

## Service Registry Replication

Control how CAS services definition files should be replicated across a CAS cluster.
To learn more about this topic, [please review this guide](../services/Configuring-Service-Replication.html)

Replication modes may be configured per the following options:

| Type                    | Description
|-------------------------|--------------------------------------------------------------
| `ACTIVE_ACTIVE`       | All CAS nodes sync copies of definitions and keep them locally.
| `ACTIVE_PASSIVE`    | Default. One master node keeps definitions and streams changes to other passive nodes.

```properties
# cas.serviceRegistry.stream.enabled=true
# cas.serviceRegistry.stream.replicationMode=ACTIVE_ACTIVE|ACTIVE_PASSIVE
```

## Service Registry Replication Hazelcast

Control how CAS services definition files should be replicated across a CAS cluster backed by a distributed Hazelcast cache.
To learn more about this topic, [please review this guide](../services/Configuring-Service-Replication.html).

Hazelcast settings for this feature are available [here](Configuration-Properties-Common.html#hazelcast-configuration) under the configuration key `cas.serviceRegistry.stream.hazelcast.config`.

```properties
# cas.serviceRegistry.stream.hazelcast.duration=PT1M
```

## Ticket Registry

To learn more about this topic, [please review this guide](../ticketing/Configuring-Ticketing-Components.html).

### Signing & Encryption

The encryption key must be randomly-generated string of size `16`. The signing key [is a JWK](Configuration-Properties-Common.html#signing--encryption) of size `512`.

### Cleaner

A cleaner process is scheduled to run in the background to clean up expired and stale tickets.
This section controls how that process should behave.

```properties
# cas.ticket.registry.cleaner.schedule.startDelay=10000
# cas.ticket.registry.cleaner.schedule.repeatInterval=60000
# cas.ticket.registry.cleaner.schedule.enabled=true
```

### JPA Ticket Registry

To learn more about this topic, [please review this guide](../ticketing/JPA-Ticket-Registry.html). Database settings for this feature are available [here](Configuration-Properties-Common.html#database-settings) under the configuration key `cas.ticket.registry.jpa`.

```properties
# cas.ticket.registry.jpa.ticketLockType=NONE
# cas.ticket.registry.jpa.jpaLockingTimeout=3600
```

Signing & encryption settings for this registry are available [here](Configuration-Properties-Common.html#signing--encryption) under the configuration key `cas.ticket.registry.jpa`.

### CouchDb Ticket Registry

To learn more about this topic, [please review this guide](../ticketing/CouchDb-Ticket-Registry.html). Database settings for this feature are available [here](Configuration-Properties-Common.html#couchdb-configuration) under the configuration key `cas.ticket.registry.couchdb`.

### Couchbase Ticket Registry

To learn more about this topic, [please review this guide](../ticketing/Couchbase-Ticket-Registry.html). Database settings for this feature are available [here](Configuration-Properties-Common.html#couchbase-integration-settings) under the configuration key `cas.ticket.registry.couchbase`.

Signing & encryption settings for this registry are available [here](Configuration-Properties-Common.html#signing--encryption) under the configuration key `cas.ticket.registry.couchbase`.

### Hazelcast Ticket Registry

To learn more about this topic, [please review this guide](../ticketing/Hazelcast-Ticket-Registry.html).

Hazelcast settings for this feature are available [here](Configuration-Properties-Common.html#hazelcast-configuration) under the configuration key `cas.ticket.registry.hazelcast`.

Signing & encryption settings for this registry are available [here](Configuration-Properties-Common.html#signing--encryption) under the configuration key `cas.ticket.registry.hazelcast`.

### Infinispan Ticket Registry

To learn more about this topic, [please review this guide](../ticketing/Infinispan-Ticket-Registry.html).

```properties
# cas.ticket.registry.infinispan.cacheName=
# cas.ticket.registry.infinispan.configLocation=/infinispan.xml
```

Signing & encryption settings for this registry are available [here](Configuration-Properties-Common.html#signing--encryption) under the configuration key `cas.ticket.registry.infinispan`.

### InMemory Ticket Registry

This is typically the default ticket registry instance where tickets
are kept inside the runtime environment memory.

```properties
# Enable the backing map to be cacheable
# cas.ticket.registry.inMemory.cache=true

# cas.ticket.registry.inMemory.loadFactor=1
# cas.ticket.registry.inMemory.concurrency=20
# cas.ticket.registry.inMemory.initialCapacity=1000
```

Signing & encryption settings for this registry are available [here](Configuration-Properties-Common.html#signing--encryption) under the configuration key `cas.ticket.registry.inMemory`.

### JMS Ticket Registry

To learn more about this topic, [please review this guide](../ticketing/Messaging-JMS-Ticket-Registry.html).

Signing & encryption settings for this registry are available [here](Configuration-Properties-Common.html#signing--encryption) under the configuration key `cas.ticket.registry.jms`.

#### JMS Ticket Registry ActiveMQ

```properties
# spring.activemq.broker-url=tcp://192.168.1.210:9876
# spring.activemq.user=admin
# spring.activemq.password=secret
# spring.activemq.pool.enabled=true
# spring.activemq.pool.max-connections=50
# spring.activemq.packages.trust-all=false
# spring.activemq.packages.trusted=org.apereo.cas
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

To learn more about this topic, [please review this guide](../ticketing/Ehcache-Ticket-Registry.html).

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

# The systemprops allows a map of properties to be set as system properties before configLocation config is processed.
# These properties may be referenced in the ehcache XML config via ${key}
# cas.ticket.registry.ehcache.systemprops.key1=value1
# cas.ticket.registry.ehcache.systemprops.key2=value2
```

Signing & encryption settings for this registry are available [here](Configuration-Properties-Common.html#signing--encryption) under the configuration key `cas.ticket.registry.ehcache`.

### Ignite Ticket Registry

To learn more about this topic, [please review this guide](../ticketing/Ignite-Ticket-Registry.html).

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
```

Signing & encryption settings for this registry are available [here](Configuration-Properties-Common.html#signing--encryption) under the configuration key `cas.ticket.registry.ignite`.

### Memcached Ticket Registry

To learn more about this topic, [please review this guide](../ticketing/Memcached-Ticket-Registry.html).Integration settings for this registry are available [here](Configuration-Properties-Common.html#memcached-integration-settings) under the configuration key `cas.ticket.registry.memcached`.

Signing & encryption settings for this registry are available [here](Configuration-Properties-Common.html#signing--encryption) under the configuration key `cas.ticket.registry.memcached`.

### DynamoDb Ticket Registry

To learn more about this topic, [please review this guide](../ticketing/DynamoDb-Ticket-Registry.html). 

Common configuration settings for this feature are available [here](Configuration-Properties-Common.html#dynamodb-configuration) 
under the configuration key `cas.ticket.registry`. 

Signing & encryption settings for this registry are available [here](Configuration-Properties-Common.html#signing--encryption) 
under the configuration key `cas.ticket.registry.dynamoDb`.

AWS settings for this feature are available [here](Configuration-Properties-Common.html#amazon-integration-settings) 
under the configuration key `cas.ticket.registry.dynamoDb`.

```properties
# cas.ticket.registry.dynamoDb.serviceTicketsTableName=serviceTicketsTable
# cas.ticket.registry.dynamoDb.proxyTicketsTableName=proxyTicketsTable
# cas.ticket.registry.dynamoDb.ticketGrantingTicketsTableName=ticketGrantingTicketsTable
# cas.ticket.registry.dynamoDb.proxyGrantingTicketsTableName=proxyGrantingTicketsTable
# cas.ticket.registry.dynamoDb.transientSessionTicketsTableName=transientSessionTicketsTable
```

### MongoDb Ticket Registry

To learn more about this topic, [please review this guide](../ticketing/MongoDb-Ticket-Registry.html). 
Signing & encryption settings for this registry are available [here](Configuration-Properties-Common.html#signing--encryption) 
under the configuration key `cas.ticket.registry.mongo`.  Common configuration settings for this feature are available [here](Configuration-Properties-Common.html#mongodb-configuration) under the configuration key `cas.ticket.registry`.

### Redis Ticket Registry

To learn more about this topic, [please review this guide](../ticketing/Redis-Ticket-Registry.html). 
Common configuration settings for this feature are available [here](Configuration-Properties-Common.html#redis-configuration) 
under the configuration key `cas.ticket.registry`. Signing & encryption settings for this registry are 
available [here](Configuration-Properties-Common.html#signing--encryption) under the configuration key `cas.ticket.registry.redis`.

## Protocol Ticket Security

Controls whether tickets issued by the CAS server should be secured via signing and encryption
when shared with client applications on outgoing calls. The signing and encryption 
keys [are both JWKs](Configuration-Properties-Common.html#signing--encryption) of size `512` and `256`.
The encryption algorithm is set to `AES_128_CBC_HMAC_SHA_256`. Signing & encryption settings for this 
feature are available [here](Configuration-Properties-Common.html#signing--encryption) under the configuration key `cas.ticket`.

## Service Tickets Behavior

Controls the expiration policy of service tickets, as well as other properties applicable to STs.

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


## Transient Session Tickets Behavior

```properties
# cas.ticket.tst.timeToKillInSeconds=300
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

<div class="alert alert-info"><strong>Keep What You Need!</strong><p>You are encouraged to only keep and maintain 
properties and settings needed for a particular policy. It is <strong>UNNECESSARY</strong> to grab a copy of all 
fields or keeping a copy as a reference while leaving them commented out. This strategy would ultimately lead to 
poor upgrades increasing chances of breaking changes and a messy deployment at that.</p></div>

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

## Google reCAPTCHA Integration

Display Google's reCAPTCHA widget on the CAS login page.

```properties
# cas.googleRecaptcha.enabled=true
# cas.googleRecaptcha.verifyUrl=https://www.google.com/recaptcha/api/siteverify
# cas.googleRecaptcha.siteKey=
# cas.googleRecaptcha.secret=
# cas.googleRecaptcha.invisible=
# cas.googleRecaptcha.position=bottomright
```

## Google Analytics Integration

To learn more about this topic, [please review this guide](../integration/Configuring-Google-Analytics.html).

```properties
# cas.googleAnalytics.googleAnalyticsTrackingId=
```

## Spring Webflow

Control how Spring Webflow's conversational session state should be managed by CAS,
and all other webflow related settings.

To learn more about this topic, [please review this guide](../webflow/Webflow-Customization.html).

```properties
# cas.webflow.alwaysPauseRedirect=false
# cas.webflow.refresh=true
# cas.webflow.redirectSameState=false
```

### Spring Webflow Login Decorations

#### Groovy

```properties
# cas.webflow.loginDecorator.groovy.location=file:/etc/cas/config/LoginDecorator.groovy
```

#### REST

RESTful settings for this feature are available [here](Configuration-Properties-Common.html#restful-integrations) under the configuration key `cas.webflow.loginDecorator.rest`.

### Spring Webflow Auto Configuration

Options that control how the Spring Webflow context is dynamically altered and configured by CAS. To learn more about this topic, [please review this guide](../webflow/Webflow-Customization-Extensions.html).

```properties
# cas.webflow.autoconfigure=true
```

#### Spring Webflow Groovy Auto Configuration

Control the Spring Webflow context via a custom Groovy script.

```properties
# cas.webflow.groovy.location=file:/etc/cas/config/custom-webflow.groovy
```

### Spring Webflow Session Management

To learn more about this topic, [see this guide](../webflow/Webflow-Customization-Sessions.html).

```properties
# cas.webflow.session.lockTimeout=30
# cas.webflow.session.compress=false
# cas.webflow.session.maxConversations=5

# Enable server-side session management
# cas.webflow.session.storage=false
```

The signing and encryption keys [are both JWKs](Configuration-Properties-Common.html#signing--encryption) of size `512` and `256`.

#### Spring Webflow Client-Side Session

The encryption key must be randomly-generated string of size f`16`. The signing key [is a JWK](Configuration-Properties-Common.html#signing--encryption) of size `512`.

Signing & encryption settings for this feature are available [here](Configuration-Properties-Common.html#signing--encryption) under the configuration key `cas.webflow`.

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

#### Spring Webflow JDBC Server-Side Session

```properties
# spring.session.store-type=jdbc
# spring.session.jdbc.initialize-schema=embedded
# spring.session.jdbc.schema=classpath:org/springframework/session/jdbc/schema-@@platform@@.sql
# spring.session.jdbc.table-name=SPRING_SESSION

# spring.datasource.url=jdbc:hsqldb:mem:cas-sessions
# spring.datasource.username=sa
# spring.datasource.password=
```

### Authentication Exceptions

Map custom authentication exceptions in the CAS webflow and link them to custom messages defined in message bundles.

To learn more about this topic, [please review this guide](../webflow/Webflow-Customization-Exceptions.html).

```properties
# cas.authn.exceptions.exceptions=value1,value2,...
```

### Authentication Interrupt

Interrupt the authentication flow to reach out to external services. To learn more about this topic, [please review this guide](../webflow/Webflow-Customization-Interrupt.html).

#### Authentication Interrupt JSON

```properties
# cas.interrupt.json.location=file:/etc/cas/config/interrupt.json
```

#### Authentication Interrupt Regex Attributes

```properties
# cas.interrupt.attributeName=attribute-name-pattern
# cas.interrupt.attributeValue=attribute-value-pattern
```

#### Authentication Interrupt Groovy

```properties
# cas.interrupt.groovy.location=file:/etc/cas/config/interrupt.groovy
```

#### Authentication Interrupt REST

RESTful settings for this feature are available [here](Configuration-Properties-Common.html#restful-integrations) under the configuration key `cas.interrupt.rest`.


### Acceptable Usage Policy

Decide how CAS should attempt to determine whether AUP is accepted.
To learn more about this topic, [please review this guide](../webflow/Webflow-Customization-AUP.html).

```properties
# cas.acceptableUsagePolicy.aupAttributeName=aupAccepted
# cas.acceptableUsagePolicy.scope=GLOBAL|AUTHENTICATION
```

#### REST

RESTful settings for this feature are available [here](Configuration-Properties-Common.html#restful-integrations) under the configuration key `cas.acceptableUsagePolicy.rest`.

#### JDBC

If AUP is controlled via JDBC, decide how choices should be remembered back inside the database instance. Database settings for this feature are available [here](Configuration-Properties-Common.html#database-settings) under the configuration key `cas.acceptableUsagePolicy.jdbc`.

```properties
# cas.acceptableUsagePolicy.jdbc.tableName=usage_policies_table
# cas.acceptableUsagePolicy.jdbc.aupColumn=
# cas.acceptableUsagePolicy.jdbc.principalIdColumn=username
# cas.acceptableUsagePolicy.jdbc.principalIdAttribute=
# cas.acceptableUsagePolicy.jdbc.sqlUpdateAUP=UPDATE %s SET %s=true WHERE %s=?
```

#### CouchDb

 Common configuration settings for this feature are available [here](Configuration-Properties-Common.html#couchdb-configuration) under the configuration key `cas.acceptableUsagePolicy`. This feature uses the `asynchronous` setting.

#### MongoDb

 Common configuration settings for this feature are available [here](Configuration-Properties-Common.html#mongodb-configuration) under the configuration key `cas.acceptableUsagePolicy`.

#### LDAP

If AUP is controlled via LDAP, decide how choices should be remembered back inside the LDAP instance. LDAP settings for this feature are available [here](Configuration-Properties-Common.html#ldap-connection-settings) under the configuration key `cas.acceptableUsagePolicy.ldap`.

#### Disable Acceptable Usage Policy
Allow acceptable usage policy webflow to be disabled - requires restart.
```properties
cas.acceptableUsagePolicy.enabled=true
```

## REST API

To learn more about this topic, [please review this guide](../protocol/REST-Protocol.html).

```properties
# cas.rest.attributeName=
# cas.rest.attributeValue=
# cas.rest.headerAuth=
# cas.rest.bodyAuth=
# cas.rest.tlsClientAuth=
```

## Metrics

To learn more about this topic, [please review this guide](../monitoring/Monitoring-Statistics.html).

### Atlas

By default, metrics are exported to Atlas running on your local machine. The location of the Atlas server to use can be provided using:

```properties
# management.metrics.export.atlas.uri=http://atlas.example.com:7101/api/v1/publish
```

### Datadog

Datadog registry pushes metrics to `datadoghq` periodically. To export metrics to Datadog, your API key must be provided:

```properties
# management.metrics.export.datadog.api-key=YOUR_KEY
```

You can also change the interval at which metrics are sent to Datadog:

```properties
# management.metrics.export.datadog.step=30s
```

### Ganglia

By default, metrics are exported to Ganglia running on your local machine. The Ganglia server host and port to use can be provided using:

```properties
# management.metrics.export.ganglia.host=ganglia.example.com
# management.metrics.export.ganglia.port=9649
```

### Graphite

By default, metrics are exported to Graphite running on your local machine. The Graphite server host and port to use can be provided using:

```properties
# management.metrics.export.graphite.host=graphite.example.com
# management.metrics.export.graphite.port=9004
```

### InfluxDb

By default, metrics are exported to Influx running on your local machine. The location of the Influx server to use can be provided using:

```properties
# management.metrics.export.influx.uri=http://influx.example.com:8086
```
### JMX

Micrometer provides a hierarchical mapping to JMX, primarily as a cheap and portable way to view metrics locally.

### New Relic

New Relic registry pushes metrics to New Relic periodically. To export metrics to New Relic, your API key and account id must be provided:

```properties
# management.metrics.export.newrelic.api-key=YOUR_KEY
# management.metrics.export.newrelic.account-id=YOUR_ACCOUNT_ID
```
 
You can also change the interval at which metrics are sent to New Relic:

```properties
# management.metrics.export.newrelic.step=30s
```

### Prometheus

Prometheus expects to scrape or poll individual app instances for metrics. Spring Boot provides an actuator endpoint 
available at `/actuator/prometheus` to present a Prometheus scrape with the appropriate format.

Here is an example `scrape_config` to add to `prometheus.yml`:

```yaml
scrape_configs:
  - job_name: 'spring'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['HOST:PORT']
``` 

### SignalFx

SignalFx registry pushes metrics to SignalFx periodically. To export metrics to SignalFx, your access token must be provided:

```properties
# management.metrics.export.signalfx.access-token=YOUR_ACCESS_TOKEN
```

You can also change the interval at which metrics are sent to SignalFx:

```properties
# management.metrics.export.signalfx.step=30s
```

Micrometer ships with a simple, in-memory backend that is automatically used as a fallback if no other registry is configured. 
This allows you to see what metrics are collected in the metrics endpoint.

The in-memory backend disables itself as soon as you’re using any of the other available backend. You can also disable it explicitly:

```properties
# management.metrics.export.simple.enabled=false
```

### StatsD

The StatsD registry pushes metrics over UDP to a StatsD agent eagerly. By default, 
metrics are exported to a StatsD agent running on your local machine. The StatsD agent host and port to use can be provided using:

```properties
# management.metrics.export.statsd.host=statsd.example.com
# management.metrics.export.statsd.port=9125
```

You can also change the StatsD line protocol to use (default to Datadog):

```properties
# management.metrics.export.statsd.flavor=etsy
```

### Wavefront

Wavefront registry pushes metrics to Wavefront periodically. If you are exporting metrics to 
Wavefront directly, your API token must be provided:

```properties
# management.metrics.export.wavefront.api-token=YOUR_API_TOKEN
```

Alternatively, you may use a Wavefront sidecar or an internal proxy set up in your environment that 
forwards metrics data to the Wavefront API host:

```properties
# management.metrics.export.uri=proxy://localhost:2878
```

You can also change the interval at which metrics are sent to Wavefront:

```properties
# management.metrics.export.wavefront.step=30s
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

To learn more about this topic, [please review this guide](../installation/Service-Discovery-Guide-Eureka.html).

```properties
# eureka.client.serviceUrl.defaultZone=${EUREKA_SERVER_HOST:http://localhost:8761}/eureka/
# eureka.client.enabled=true
# eureka.instance.statusPageUrl=${cas.server.prefix}/actuator/info
# eureka.instance.healthCheckUrl=${cas.server.prefix}/actuator/health
# eureka.instance.homePageUrl=${cas.server.prefix}/
# eureka.client.healthcheck.enabled=true

# spring.cloud.config.discovery.enabled=false
```

## Consul Service Discovery

To learn more about this topic, [please review this guide](../installation/Service-Discovery-Guide-Consul.html).

```properties
# spring.cloud.consul.port=8500
# spring.cloud.consul.enabled=true
# spring.cloud.consul.host=localhost

# spring.cloud.consul.discovery.healthCheckPath=<health-endpoint-url>
# spring.cloud.consul.discovery.healthCheckPath=15s
# spring.cloud.consul.discovery.instanceId=${spring.application.name}:${random.value}

# spring.cloud.consul.discovery.heartbeat.enabled=true
# spring.cloud.consul.discovery.heartbeat.ttlValue=60
# spring.cloud.consul.discovery.heartbeat.ttlUnit=s
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
```

Signing & encryption settings for this feature are available [here](Configuration-Properties-Common.html#signing--encryption) under the configuration key `cas.consent`. The signing and encryption keys [are both JWKs](Configuration-Properties-Common.html#signing--encryption) of size `512` and `256`.

### JSON Attribute Consent

```properties
# cas.consent.json.location=file:/etc/cas/config/consent.json
```

### Groovy Attribute Consent

```properties
# cas.consent.groovy.location=file:/etc/cas/config/consent.groovy
```

### JPA Attribute Consent

Database settings for this feature are available [here](Configuration-Properties-Common.html#database-settings) under the configuration key `cas.consent.jpa`.

### LDAP Attribute Consent

LDAP settings for this feature are available [here](Configuration-Properties-Common.html#ldap-connection-settings) under the configuration key `cas.consent.ldap`.

```properties
# cas.consent.ldap.consentAttributeName=casConsentDecision
```

### MongoDb Attribute Consent

 Common configuration settings for this feature are available [here](Configuration-Properties-Common.html#mongodb-configuration) under the configuration key `cas.consent`.

### CouchDb Attribute Consent

 Common configuration settings for this feature are available [here](Configuration-Properties-Common.html#couchdb-configuration) under the configuration key `cas.consent`.

### REST Attribute Consent

```properties
# cas.consent.rest.endpoint=https://api.example.org/trustedBrowser
```

## Apache Fortress Authentication

To learn more about this topic, [please review this guide](../integration/Configuring-Fortress-Integration.html).

```properties
# cas.authn.fortress.rbaccontext=HOME
```

## CAS Client

Configure settings relevant to the Java CAS client configured to handle inbound ticket validation operations, etc.

```properties
# cas.client.prefix=https://sso.example.org/cas
# cas.client.validatorType=CAS10|CAS20|CAS30
```

## Password Management

Allow the user to update their account password, etc in-place.
To learn more about this topic, [please review this guide](../installation/Password-Policy-Enforcement.html).

```properties
# cas.authn.pm.enabled=true

# Minimum 8 and Maximum 10 characters at least 1 Uppercase Alphabet, 1 Lowercase Alphabet, 1 Number and 1 Special Character
# cas.authn.pm.policyPattern=^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@$!%*?&])[A-Za-z\\d$@$!%*?&]{8,10}

# cas.authn.pm.reset.expirationMinutes=1
# cas.authn.pm.reset.securityQuestionsEnabled=true

# Automatically log in after successful password change
# cas.authn.pm.autoLogin=false
```

Email notifications settings for this feature are available [here](Configuration-Properties-Common.html#email-notifications) 
under the configuration key `cas.authn.pm.reset`.

The signing and encryption keys [are both JWKs](Configuration-Properties-Common.html#signing--encryption) of size `512` and `256`.
The encryption algorithm is set to `AES_128_CBC_HMAC_SHA_256`. Signing & encryption settings for this feature are available [here](Configuration-Properties-Common.html#signing--encryption) under the configuration key `cas.authn.pm.reset`.

### JSON Password Management

```properties
# cas.authn.pm.json.location=classpath:jsonResourcePassword.json
```

### Groovy Password Management

```properties
# cas.authn.pm.groovy.location=classpath:PasswordManagementService.groovy
```

### LDAP Password Management

LDAP settings for this feature are available [here](Configuration-Properties-Common.html#ldap-connection-settings) under the configuration key `cas.authn.pm.ldap`.

```properties
# cas.authn.pm.ldap.type=AD|GENERIC|EDirectory|FreeIPA
# cas.authn.pm.ldap.usernameAttribute=uid
# cas.authn.pm.ldap.searchFilterUsername=email={user}

# Attributes that should be fetched to indicate security questions and answers
# cas.authn.pm.ldap.securityQuestionsAttributes.attrQuestion1=attrAnswer1
# cas.authn.pm.ldap.securityQuestionsAttributes.attrQuestion2=attrAnswer2
# cas.authn.pm.ldap.securityQuestionsAttributes.attrQuestion3=attrAnswer3
```

### JDBC Password Management

Database settings for this feature are available [here](Configuration-Properties-Common.html#database-settings) 
under the configuration key `cas.authn.pm.jdbc`. Password encoding  settings for this 
feature are available [here](Configuration-Properties-Common.html#password-encoding) under the configuration key `cas.authn.pm.jdbc`.

```properties
# The two fields indicated below are expected to be returned
# cas.authn.pm.jdbc.sqlSecurityQuestions=SELECT question, answer FROM table WHERE user=?

# cas.authn.pm.jdbc.sqlFindEmail=SELECT email FROM table WHERE user=?
# cas.authn.pm.jdbc.sqlFindUser=SELECT user FROM table WHERE email=?
# cas.authn.pm.jdbc.sqlChangePassword=UPDATE table SET password=? WHERE user=?
```

### REST Password Management

```properties
# cas.authn.pm.rest.endpointUrlEmail=
# cas.authn.pm.rest.endpointUrlUser=
# cas.authn.pm.rest.endpointUrlSecurityQuestions=
# cas.authn.pm.rest.endpointUrlChange=
```
