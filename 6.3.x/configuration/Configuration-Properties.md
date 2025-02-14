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
This section is meant as a guide only. Do <strong>NOT</strong> copy/paste the entire 
collection of settings into your CAS configuration; rather pick only the properties that you need. Do NOT enable settings unless you are 
certain of their purpose and do NOT copy settings into your configuration only to keep 
them as <i>reference</i>. All these ideas lead to upgrade headaches, maintenance nightmares and premature aging.</p></div>

The following list of properties are controlled by and provided to CAS. Each block, for most use cases, corresponds
to a specific CAS module that is expected to be included in the final CAS distribution prepared during the build
and deployment process.

<div class="alert alert-info"><strong>YAGNI</strong><p>Note that for nearly ALL use cases,
 declaring and configuring properties listed below is sufficient. You should NOT have to
explicitly massage a CAS XML configuration file to design an authentication handler,
create attribute release policies, etc. CAS at runtime will auto-configure all required changes for you.</p></div>

## Naming Convention

Property names can be specified in very relaxed terms. For 
instance `cas.someProperty`, `cas.some-property`, `cas.some_property` are all valid names. While all forms are accepted by CAS, there are 
certain components (in CAS and other frameworks used) whose activation at runtime is conditional on a 
property value, where this property is required to have been specified in CAS configuration using kebab case. This 
is both true for properties that are owned by CAS as well as those that might be presented to the system via 
an external library or framework such as Spring Boot, etc.

> When possible, properties should be stored in lower-case kebab format, such as cas.property-name=value.

## General

A number of CAS configuration options equally apply to a number of modules and features. To understand and 
take note of those options, please [review this guide](Configuration-Properties-Common.html).

## Validation

Configuration properties are automatically validated on CAS startup to report issues with configuration binding,
specially if defined CAS settings cannot be recognized or validated by the configuration schema. The validation process
is on by default and can be skipped on startup using a special *system property* `SKIP_CONFIG_VALIDATION` 
that should be set to `true`. 

Additional validation processes are also handled via [Configuration Metadata](Configuration-Metadata-Repository.html)
and property migrations applied automatically on startup by Spring Boot and family.

## Custom Settings

The following settings could be used to extend CAS with arbitrary configuration keys and values:

```properties
# cas.custom.properties.[property-name]=[property-value]
``` 

## Configuration Storage

This section outlines strategies that can be used to store CAS configuration and settings.

### Standalone

This is the default configuration mode which indicates that CAS does NOT require connections 
to an external configuration server and will run in an embedded standalone mode.

#### By Directory

CAS by default will attempt to locate settings and properties inside a given directory indicated
under the setting name `cas.standalone.configuration-directory` and otherwise falls back to using:

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

There also exists a `cas.standalone.configuration-file` which can be used to directly feed a collection of properties
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
# spring.cloud.config.server.native.search-locations=file:///etc/cas/config
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
# spring.cloud.consul.config.default-context=apps
# spring.cloud.consul.config.profile-separator=::

# spring.cloud.consul.config.watch.delay=1000
# spring.cloud.consul.config.watch.enabled=false
```

### Vault

Allow the CAS Spring Cloud configuration server to load settings from [HashiCorp's Vault](Configuration-Properties-Security.html).

```properties
# spring.cloud.vault.host=127.0.0.1
# spring.cloud.vault.port=8200
# spring.cloud.vault.connection-timeout=3000
# spring.cloud.vault.read-timeout=5000
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

Vault supports AppId authentication that consists of two hard to guess tokens. The AppId defaults to `spring.application.name` that is statically configured. The second token is the UserId which is a part determined by the application, usually related to the runtime environment. Spring Cloud Vault Config supports IP address, Mac address and static 
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
# spring.cloud.zookeeper.connect-string=localhost:2181
# spring.cloud.zookeeper.enabled=true
# spring.cloud.zookeeper.config.enabled=true
# spring.cloud.zookeeper.max-retries=10
# spring.cloud.zookeeper.config.root=cas/config
```

### Amazon Secrets Manager

Common AWS settings for this feature are available [here](Configuration-Properties-Common.html#amazon-integration-settings)
under the configuration key `cas.spring.cloud.aws.secrets-manager`.

### Amazon Parameter Store

Common AWS settings for this feature are available [here](Configuration-Properties-Common.html#amazon-integration-settings)
under the configuration key `cas.spring.cloud.aws.ssm`.

### Amazon S3

The following settings may be passed using strategies outlined [here](Configuration-Management.html#overview) in order for CAS to establish a connection,
using the configuration key `cas.spring.cloud.aws.s3`.

```properties
# ${configuration-key}.bucket-name=cas-properties
```

Common AWS settings for this feature are available [here](Configuration-Properties-Common.html#amazon-integration-settings)
under the configuration key `cas.spring.cloud.aws.s3`.

### DynamoDb

Common AWS settings for this feature are available [here](Configuration-Properties-Common.html#amazon-integration-settings)
under the configuration key `cas.spring.cloud.dynamo-db`. 

### JDBC

Allow the CAS Spring Cloud configuration server to load settings from a RDBMS instance. Database settings for this feature 
are available [here](Configuration-Properties-Common.html#database-settings) under the configuration key `cas.spring.cloud.jdbc`.

```properties
# cas.spring.cloud.jdbc.sql=SELECT id, name, value FROM CAS_SETTINGS_TABLE
```

### REST

Allow the CAS Spring Cloud configuration server to load settings from a REST API.

```properties
# cas.spring.cloud.rest.url=
# cas.spring.cloud.rest.basic-auth-username=
# cas.spring.cloud.rest.basic-auth-password=
# cas.spring.cloud.rest.method=
# cas.spring.cloud.rest.headers=Header1:Value1;Header2:Value2
```

## Configuration Security

To learn more about how sensitive CAS settings can be
secured, [please review this guide](Configuration-Properties-Security.html).

### Standalone

```properties
# cas.standalone.configuration-security.alg=PBEWithMD5AndTripleDES
# cas.standalone.configuration-security.provider=BC
# cas.standalone.configuration-security.iterations=
# cas.standalone.configuration-security.psw=
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
server.port=8443
server.ssl.key-store=file:/etc/cas/thekeystore
server.ssl.key-store-password=changeit
server.ssl.key-password=changeit

# server.ssl.enabled=true
# server.ssl.ciphers=
# server.ssl.key-alias=
# server.ssl.key-store-provider=
# server.ssl.key-store-type=
# server.ssl.protocol=

# server.max-http-header-size=2097152
# server.use-forward-headers=true
# server.connection-timeout=20000
```

### X.509 Client Authentication

```properties
# server.ssl.trust-store=
# server.ssl.trust-store-password=
# server.ssl.trust-store-provider=
# server.ssl.trust-store-type=
# server.ssl.client-auth=NEED|NONE|WANT
```

The following values are supported for client authentication type:

| Type                 | Description
|----------------------|-------------------------------------------------------
| `NEED`               | Client authentication is needed and mandatory. 
| `NONE`               | Client authentication is not wanted.
| `WANT`               | Client authentication is wanted but not mandatory.

### Embedded Jetty Container

The following settings affect the runtime behavior of the embedded Jetty container.

```properties
# server.jetty.acceptors=-1       

# server.jetty.accesslog.append=false
# server.jetty.accesslog.custom-format=
# server.jetty.accesslog.enabled=false
# server.jetty.accesslog.file-date-format=
# server.jetty.accesslog.filename=
# server.jetty.accesslog.format=
# server.jetty.accesslog.ignore-paths=
# server.jetty.accesslog.retention-period=31

# server.jetty.connection-idle-timeout=
# server.jetty.max-http-form-post-size=200000B
# server.jetty.max-threads=200
# server.jetty.min-threads=8
# server.jetty.selectors=-1
# server.jetty.thread-idle-timeout=-1
```

### Embedded Apache Tomcat Container

The following settings affect the runtime behavior of the embedded Apache Tomcat container.

```properties
# server.tomcat.basedir=build/tomcat

# server.tomcat.accesslog.enabled=true
# server.tomcat.accesslog.pattern=%t %a "%r" %s (%D ms)
# server.tomcat.accesslog.suffix=.log

# server.tomcat.max-http-post-size=20971520
# server.tomcat.max-threads=5
# server.tomcat.port-header=X-Forwarded-Port
# server.tomcat.protocol-header=X-Forwarded-Proto
# server.tomcat.protocol-header-https-value=https
# server.tomcat.remote-ip-header=X-FORWARDED-FOR
# server.tomcat.uri-encoding=UTF-8

# cas.server.tomcat.server-name=Apereo CAS
```

#### HTTP Proxying

In the event that you decide to run CAS without any SSL configuration in the embedded Tomcat container and on a non-secure port
yet wish to customize the connector configuration that is linked to the running port (i.e. `8080`), the following settings may apply:

```properties
# cas.server.tomcat.http-proxy.enabled=true
# cas.server.tomcat.http-proxy.secure=true
# cas.server.tomcat.http-proxy.protocol=AJP/1.3
# cas.server.tomcat.http-proxy.scheme=https
# cas.server.tomcat.http-proxy.redirect-port=
# cas.server.tomcat.http-proxy.proxy-port=
# cas.server.tomcat.http-proxy.attributes.attribute-name=attributeValue
```

#### HTTP

Enable HTTP connections for the embedded Tomcat container, in addition to the configuration
linked to the `server.port` setting.

```properties
# cas.server.tomcat.http.port=8080
# cas.server.tomcat.http.protocol=org.apache.coyote.http11.Http11NioProtocol
# cas.server.tomcat.http.enabled=true
# cas.server.tomcat.http.attributes.attribute-name=attributeValue
```

#### AJP

Enable AJP connections for the embedded Tomcat container,

```properties
# cas.server.tomcat.ajp.secure=false
# cas.server.tomcat.ajp.enabled=false
# cas.server.tomcat.ajp.proxy-port=-1
# cas.server.tomcat.ajp.protocol=AJP/1.3
# cas.server.tomcat.ajp.async-timeout=5000
# cas.server.tomcat.ajp.scheme=http
# cas.server.tomcat.ajp.max-post-size=20971520
# cas.server.tomcat.ajp.port=8009
# cas.server.tomcat.ajp.enable-lookups=false
# cas.server.tomcat.ajp.redirect-port=-1
# cas.server.tomcat.ajp.allow-trace=false
# cas.server.tomcat.ajp.attributes.attribute-name=attributeValue
```

#### SSL Valve

The Tomcat SSLValve is a way to get a client certificate from an SSL proxy (e.g. HAProxy or BigIP F5)
running in front of Tomcat via an HTTP header. If you enable this, make sure your proxy is ensuring
that this header does not originate with the client (e.g. the browser).

```properties
# cas.server.tomcat.ssl-valve.enabled=false
# cas.server.tomcat.ssl-valve.ssl-client-cert-header=ssl_client_cert
# cas.server.tomcat.ssl-valve.ssl-cipher-header=ssl_cipher
# cas.server.tomcat.ssl-valve.ssl-session-id-header=ssl_session_id
# cas.server.tomcat.ssl-valve.ssl-cipher-user-key-size-header=ssl_cipher_usekeysize
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

Enable the [extended access log](https://tomcat.apache.org/tomcat-9.0-doc/config/valve.html#Extended_Access_Log_Valve)
for the embedded Tomcat container.

```properties
# cas.server.tomcat.ext-access-log.enabled=false
# cas.server.tomcat.ext-access-log.pattern=c-ip s-ip cs-uri sc-status time x-threadname x-H(secure) x-H(remoteUser)
# cas.server.tomcat.ext-access-log.suffix=.log
# cas.server.tomcat.ext-access-log.prefix=localhost_access_extended
# cas.server.tomcat.ext-access-log.directory=
```

#### Rewrite Valve

Enable the [rewrite valve](https://tomcat.apache.org/tomcat-9.0-doc/rewrite.html) for the embedded Tomcat container.

```properties
# cas.server.tomcat.rewrite-valve.location=classpath://container/tomcat/rewrite.config
```

#### Basic Authentication

Enable basic authentication for the embedded Apache Tomcat.

```properties
# cas.server.tomcat.basic-authn.enabled=true
# cas.server.tomcat.basic-authn.security-roles[0]=admin
# cas.server.tomcat.basic-authn.auth-roles[0]=admin
# cas.server.tomcat.basic-authn.patterns[0]=/*
```

#### Apache Portable Runtime (APR)

Tomcat can use the [Apache Portable Runtime](https://tomcat.apache.org/tomcat-9.0-doc/apr.html) to provide superior 
scalability, performance, and better integration with native server technologies.

```properties
# cas.server.tomcat.apr.enabled=false

# cas.server.tomcat.apr.ssl-protocol=
# cas.server.tomcat.apr.ssl-verify-depth=10
# cas.server.tomcat.apr.ssl-verify-client=require
# cas.server.tomcat.apr.ssl-cipher-suite=
# cas.server.tomcat.apr.ssl-disable-compression=false
# cas.server.tomcat.apr.ssl-honor-cipher-order=false

# cas.server.tomcat.apr.ssl-certificate-chain-file=
# cas.server.tomcat.apr.ssl-ca-certificate-file=
# cas.server.tomcat.apr.ssl-certificate-key-file=
# cas.server.tomcat.apr.ssl-certificate-file=
```

Enabling APR requires the following JVM system property that indicates the location of the APR library binaries (i.e. `usr/local/opt/tomcat-native/lib`):

```bash
-Djava.library.path=/path/to/tomcat-native/lib
```
   
The APR connector can be assigned an SSLHostConfig element as such:

```properties
# cas.server.tomcat.apr.ssl-host-config.enabled=false
# cas.server.tomcat.apr.ssl-host-config.revocation-enabled=false
# cas.server.tomcat.apr.ssl-host-config.ca-certificate-file=false
# cas.server.tomcat.apr.ssl-host-config.host-name=
# cas.server.tomcat.apr.ssl-host-config.ssl-protocol=
# cas.server.tomcat.apr.ssl-host-config.protocols=all
# cas.server.tomcat.apr.ssl-host-config.insecure-renegotiation=false
# cas.server.tomcat.apr.ssl-host-config.certificate-verification-depth=10

# cas.server.tomcat.apr.ssl-host-config.certificates[0].certificate-file=
# cas.server.tomcat.apr.ssl-host-config.certificates[0].certificate-key-file=
# cas.server.tomcat.apr.ssl-host-config.certificates[0].certificate-key-password=
# cas.server.tomcat.apr.ssl-host-config.certificates[0].certificate-chain-file=
# cas.server.tomcat.apr.ssl-host-config.certificates[0].type=UNDEFINED
```

#### Connector IO

```properties
# cas.server.tomcat.socket.app-read-buf-size=0
# cas.server.tomcat.socket.app-write-buf-size=0
# cas.server.tomcat.socket.buffer-pool=0
# cas.server.tomcat.socket.performance-connection-time=-1
# cas.server.tomcat.socket.performance-latency=-1
# cas.server.tomcat.socket.performance-bandwidth=-1
```

#### Session Clustering & Replication

Enable in-memory session replication to replicate web application session deltas. 

| Clustering Type      | Description
|----------------------|-------------------------------------------------------
| `DEFAULT`            | Discovers cluster members via multicast discovery and optionally via staticly defined cluster members using the `clusterMembers`. [SimpleTcpCluster with McastService](http://tomcat.apache.org/tomcat-9.0-doc/cluster-howto.html) 
| `CLOUD`              | For use in Kubernetes where members are discovered via accessing the Kubernetes API or doing a DNS lookup of the members of a Kubernetes service. [Documentation](https://cwiki.apache.org/confluence/display/TOMCAT/ClusteringCloud) is currently light, see code for details.

| Membership Providers   | Description
|----------------------|-------------------------------------------------------
| `kubernetes`         | Uses [Kubernetes API](https://github.com/apache/tomcat/blob/master/java/org/apache/catalina/tribes/membership/cloud/KubernetesMembershipProvider.java) to find other pods in a deployment. API is discovered and accessed via information in environment variables set in the container. The KUBERNETES_NAMESPACE environment variable is used to query the pods in the namespace and it will treat other pods in that namespace as potential cluster members but they can be filtered using the KUBERNETES_LABELS environment variable which are used as a [label selector](https://kubernetes.io/docs/concepts/overview/working-with-objects/labels/#api).
| `dns`                | Uses [DNS lookups](https://github.com/apache/tomcat/blob/master/java/org/apache/catalina/tribes/membership/cloud/DNSMembershipProvider.java) to find addresses of the cluster members behind a DNS name specified by DNS_MEMBERSHIP_SERVICE_NAME environment variable. Works in Kubernetes but doesn't rely on Kubernetes.
| `MembershipProvider` class | Use a [membership provider implementation](https://github.com/apache/tomcat/blob/master/java/org/apache/catalina/tribes/MembershipProvider.java) of your choice.

Most settings apply to the `DEFAULT` clustering type, which requires members to be defined via `clusterMembers` if multicast discovery doesn't work. The `cloudMembershipProvider` setting applies to the `CLOUD` type.

```properties
# cas.server.tomcat.clustering.enabled=false
# cas.server.tomcat.clustering.clustering-type=DEFAULT|CLOUD
# cas.server.tomcat.clustering.cluster-members=ip-address:port:index
# cas.server.tomcat.clustering.cloud-membership-provider=kubernetes|dns|[MembershipProvider impl classname](https://github.com/apache/tomcat/blob/master/java/org/apache/catalina/tribes/MembershipProvider.java)
# cas.server.tomcat.clustering.expire-sessions-on-shutdown=false
# cas.server.tomcat.clustering.channel-send-options=8

# cas.server.tomcat.clustering.receiver-port=4000
# cas.server.tomcat.clustering.receiver-timeout=5000
# cas.server.tomcat.clustering.receiver-max-threads=6
# cas.server.tomcat.clustering.receiver-address=auto
# cas.server.tomcat.clustering.receiver-auto-bind=100

# cas.server.tomcat.clustering.membership-port=45564
# cas.server.tomcat.clustering.membership-address=228.0.0.4
# cas.server.tomcat.clustering.membership-frequency=500
# cas.server.tomcat.clustering.membership-drop-time=3000
# cas.server.tomcat.clustering.membership-recovery-enabled=true
# cas.server.tomcat.clustering.membership-local-loopback-disabled=false
# cas.server.tomcat.clustering.membership-recovery-counter=10

# cas.server.tomcat.clustering.manager-type=DELTA|BACKUP
```

## CAS Server

Identify the CAS server. `name` and `prefix` are always required settings.

A CAS host is automatically appended to the ticket ids generated by CAS.
If none is specified, one is automatically detected and used by CAS.

```properties
# cas.server.name=https://cas.example.org:8443
# cas.server.prefix=https://cas.example.org:8443/cas 
# cas.server.scope=example.org
# cas.host.name=
```

## Session replication

Control aspects of session replication for certain CAS features, such as OAuth or OpenID Connect,
allowing session and authentication profile data to be kept with the client as a cookie.

Common cookie properties found [here](Configuration-Properties-Common.html#cookie-properties) under 
the configuration key `cas.session-replication.cookie`.

```properties
# cas.session-replication.auto-configure-cookie-path=true
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
# management.endpoints.web.exposure.exclude=

# management.endpoints.jmx.exposure.exclude=*
# management.endpoints.jmx.exposure.include=
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
# cas.monitor.endpoints.jaas.refresh-configuration-on-startup=true
# cas.monitor.endpoints.jaas.login-config=file:/etc/cas/config/jaas.conf
# cas.monitor.endpoints.jaas.login-context-name=CAS
```

### LDAP Authentication Security

Shared LDAP settings for this feature are available [here](Configuration-Properties-Common.html#ldap-connection-settings) 
under the configuration key `cas.monitor.endpoints.ldap`.

LDAP authentication for endpoint security may be additionally configured via the following settings:

```properties
# cas.monitor.endpoints.ldap.ldap-authz.role-attribute=uugid
# cas.monitor.endpoints.ldap.ldap-authz.role-prefix=ROLE_
# cas.monitor.endpoints.ldap.ldap-authz.allow-multiple-results=false
# cas.monitor.endpoints.ldap.ldap-authz.group-attribute=
# cas.monitor.endpoints.ldap.ldap-authz.group-prefix=
# cas.monitor.endpoints.ldap.ldap-authz.group-filter=
# cas.monitor.endpoints.ldap.ldap-authz.group-base-dn=
# cas.monitor.endpoints.ldap.ldap-authz.base-dn=
# cas.monitor.endpoints.ldap.ldap-authz.search-filter=
```

### JDBC Authentication Security

Shared database settings for this feature are available [here](Configuration-Properties-Common.html#database-settings)
under the configuration key `cas.monitor.endpoints.jdbc`.

JDBC authentication for endpoint security may be additionally configured via the following settings:

```properties
# cas.monitor.endpoints.jdbc.role-prefix=
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

A number of available endpoint ids [should be listed here](../monitoring/Monitoring-Statistics.html).

Endpoints may also be mapped to custom arbitrary endpoints. For example, to remap the `health` endpoint to `healthcheck`, 
specify the following settings:

```properties
# management.endpoints.web.path-mapping.health=healthcheck
```

### Health Endpoint

The `health` endpoint may also be configured to show details using `management.endpoint.health.show-details` via the following conditions:

| URL                  | Description
|----------------------|-------------------------------------------------------
| `never`              | Never display details of health monitors.
| `always`             | Always display details of health monitors.
| `when-authorized`   | Details are only shown to authorized users. Authorized roles can be configured using `management.endpoint.health.roles`.

```properties
# management.endpoint.health.show-details=never
```

The results and details of the `health` endpoints are produced by a number of health indicator components that may monitor different systems, such as LDAP connection
pools, database connections, etc. Such health indicators are turned off by default and may individually be controlled and turned on via the following settings:

```properties
# management.health.<name>.enabled=true
# management.health.defaults.enabled=false 
```

The following health indicator names are available, given the presence of the appropriate CAS feature:

| Health Indicator          | Description
|----------------------|------------------------------------------------------------------------------------------
| `memoryHealthIndicator`   | Reports back on the health status of CAS JVM memory usage, etc.
| `systemHealthIndicator`   | Reports back on the health of the system of the CAS server.(Load, Uptime, Heap, CPU etc.)
| `sessionHealthIndicator`   | Reports back on the health status of CAS tickets and SSO session usage.
| `duoSecurityHealthIndicator`   | Reports back on the health status of Duo Security APIs.
| `ehcacheHealthIndicator`   | Reports back on the health status of Ehcache caches.
| `hazelcastHealthIndicator`   | Reports back on the health status of Hazelcast caches.
| `dataSourceHealthIndicator`   | Reports back on the health status of JDBC connections.
| `pooledLdapConnectionFactoryHealthIndicator`   | Reports back on the health status of LDAP connection pools.
| `memcachedHealthIndicator`   | Reports back on the health status of Memcached connections.
| `mongoHealthIndicator`   | Reports back on the health status of MongoDb connections.
| `samlRegisteredServiceMetadataHealthIndicator`   | Reports back on the health status of SAML2 service provider metadata sources.

### Endpoint Security

Global endpoint security configuration activated by CAS may be controlled under the configuration key `cas.monitor.endpoints.endpoint.<endpoint-name>`.
There is a special endpoint named `defaults`  which serves as a shortcut that controls the security of all endpoints, if left undefined in CAS settings. Accessing an endpoint
over the web can be allowed via a special login form whose access and presence can be controlled via:

```properties
# cas.monitor.endpoints.form-login-enabled=false
``` 

Note that any individual endpoint must be first enabled before any security can be applied. The security of all endpoints is controlled using the following settings:

```properties
# ${configuration-key}.required-roles[0]=
# ${configuration-key}.required-authorities[0]=
# ${configuration-key}.required-ip-addresses[0]=
# ${configuration-key}.access[0]=PERMIT|ANONYMOUS|DENY|AUTHENTICATED|ROLE|AUTHORITY|IP_ADDRESS
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
# spring.boot.admin.client.url=https://bootadmin.example.org:8444
# spring.boot.admin.client.instance.service-base-url=${cas.server.prefix}
# spring.boot.admin.client.instance.name=Apereo CAS
# In case Spring Boot Admin endpoints are protected via basic authn :
# spring.boot.admin.client.username=
# spring.boot.admin.client.password=
# In case CAS endpoints are protected via basic authn :
# spring.boot.admin.client.instance.metadata.user.name=
# spring.boot.admin.client.instance.metadata.user.password=
```

### JavaMelody

To learn more about this topic, [please review this guide](../monitoring/Configuring-Monitoring-JavaMelody.html).

```properties
# javamelody.enabled=true
# javamelody.excluded-datasources=one,two,etc
# javamelody.spring-monitoring-enabled=true
# javamelody.init-parameters.log=true
# javamelody.init-parameters.url-exclude-pattern=(/webjars/.*|/css/.*|/images/.*|/fonts/.*|/js/.*)
# javamelody.init-parameters.monitoring-path=/monitoring

# Control access via IP regular expression patterns
# javamelody.init-parameters.allowed-addr-pattern=.+
# Control access via Basic AuthN
# javamelody.init-parameters.authorized-users=admin:pwd
```

## Web Application Session

Control the CAS web application session behavior
as it's treated by the underlying servlet container engine.

```properties
# server.servlet.session.timeout=PT30S
# server.servlet.session.cookie.http-only=true
# server.servlet.session.tracking-modes=COOKIE
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
# cas.view.default-redirect-url=https://www.github.com

# CAS views may be located at the following paths outside
# the web application context, in addition to prefix specified
# above which is handled via Thymeleaf.
# cas.view.template-prefixes[0]=file:///etc/cas/templates
```

## Custom Login Fields

```properties
# cas.view.custom-login-form-fields.[field-name].message-bundle-key=
# cas.view.custom-login-form-fields.[field-name].required=true
# cas.view.custom-login-form-fields.[field-name].converter=
```

### CAS v1

```properties
# cas.view.cas1.attribute-renderer-type=DEFAULT|VALUES_PER_LINE
```

### CAS v2

```properties
# cas.view.cas2.v3-forward-compatible=false
# cas.view.cas2.success=protocol/2.0/casServiceValidationSuccess
# cas.view.cas2.failure=protocol/2.0/casServiceValidationFailure
# cas.view.cas2.proxy.success=protocol/2.0/casProxySuccessView
# cas.view.cas2.proxy.failure=protocol/2.0/casProxyFailureView
```

### CAS v3

```properties
# cas.view.cas3.success=protocol/3.0/casServiceValidationSuccess
# cas.view.cas3.failure=protocol/3.0/casServiceValidationFailure
# cas.view.cas3.attribute-renderer-type=DEFAULT|INLINE
```

### Restful Views

Control the resolution of CAS views via REST. RESTful settings for this feature are 
available [here](Configuration-Properties-Common.html#restful-integrations) under the configuration key `cas.view.rest`.

## Logging

Control the location and other settings of the CAS logging configuration.
To learn more about this topic, [please review this guide](../logging/Logging.html).

```properties
# logging.config=file:/etc/cas/log4j2.xml
# server.servlet.context-parameters.is-log4j-auto-initialization-disabled=true
       
# cas.logging.mdc-enabled=true

# Control log levels via properties
# logging.level.org.apereo.cas=DEBUG
```

To disable log sanitization, start the container with the system property `CAS_TICKET_ID_SANITIZE_SKIP=true`.

## AspectJ Configuration

```properties
# spring.aop.auto=true
# spring.aop.proxy-target-class=true
```

## Authentication Attributes

Set of authentication attributes that are retrieved by the principal resolution process,
typically via some component of [Person Directory](../integration/Attribute-Resolution.html)
from a number of attribute sources unless noted otherwise by the specific authentication scheme.

If multiple attribute repository sources are defined, they are added into a list
and their results are cached and merged.

```properties
# cas.authn.attribute-repository.expiration-time=30
# cas.authn.attribute-repository.expiration-time-unit=MINUTES
# cas.authn.attribute-repository.maximum-cache-size=10000
# cas.authn.attribute-repository.merger=REPLACE|ADD|MULTIVALUED|NONE
# cas.authn.attribute-repository.aggregation=MERGE|CASCADE
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
By default, the execution order (when defined) is the following but can be adjusted per source:

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

Attributes may be allowed to be virtually renamed and remapped. The following definition, for instance, attempts to 
grab the attribute `uid` from the attribute source and rename it to `userId`:

```properties
# cas.authn.attribute-repository.[type-placeholder].attributes.uid=userId
```

### Merging Strategies

The following merging strategies can be used to resolve conflicts when the same attribute are found from multiple sources:

| Type                    | Description
|-------------------------|----------------------------------------------------------------------------------------------------
| `REPLACE`               | Overwrites existing attribute values, if any.
| `ADD`                   | Retains existing attribute values if any, and ignores values from subsequent sources in the resolution chain.
| `MULTIVALUED`           | Combines all values into a single attribute, essentially creating a multi-valued attribute.
| `NONE`                  | Do not merge attributes, only use attributes retrieved during authentication.

### Aggregation Strategies

The following aggregation strategies can be used to resolve and merge attributes
when multiple attribute repository sources are defined to fetch data:
  
| Type            | Description
|-----------------|----------------------------------------------------------------------------------------------------
| `MERGE`         | Default. Query multiple repositories in order and merge the results into a single result set.
| `CASCADE`       | Same as above; results from each query are passed down to the next attribute repository source.

### Stub

Static attributes that need to be mapped to a hardcoded value belong here.

```properties
# cas.authn.attribute-repository.stub.id=

# cas.authn.attribute-repository.stub.attributes.uid=uid
# cas.authn.attribute-repository.stub.attributes.displayName=displayName
# cas.authn.attribute-repository.stub.attributes.cn=commonName
# cas.authn.attribute-repository.stub.attributes.affiliation=groupMembership
```

### LDAP

If you wish to directly and separately retrieve attributes from an LDAP source, LDAP settings for this 
feature are available [here](Configuration-Properties-Common.html#ldap-connection-settings) under the 
configuration key `cas.authn.attribute-repository.ldap[0]`.

```properties
# cas.authn.attribute-repository.ldap[0].id=
# cas.authn.attribute-repository.ldap[0].order=0

# cas.authn.attribute-repository.ldap[0].attributes.uid=uid
# cas.authn.attribute-repository.ldap[0].attributes.display-name=displayName
# cas.authn.attribute-repository.ldap[0].attributes.cn=commonName
# cas.authn.attribute-repository.ldap[0].attributes.affiliation=groupMembership
```

To fetch and resolve attributes that carry tags/options, consider tagging the mapped attribute as such:

```properties
# cas.authn.attribute-repository.ldap[0].attributes.affiliation=affiliation;
```
                                  
### Groovy

If you wish to directly and separately retrieve attributes from a Groovy script,
the following settings are then relevant:

```properties
# cas.authn.attribute-repository.groovy[0].location=file:/etc/cas/attributes.groovy
# cas.authn.attribute-repository.groovy[0].case-insensitive=false
# cas.authn.attribute-repository.groovy[0].order=0
# cas.authn.attribute-repository.groovy[0].id=
```

The Groovy script may be designed as:

```groovy
import java.util.*

def Map<String, List<Object>> run(final Object... args) {
    def username = args[0]
    def attributes = args[1]
    def logger = args[2]
    def properties = args[3]
    def appContext = args[4]

    logger.debug("[{}]: The received uid is [{}]", this.class.simpleName, uid)
    return[username:[uid], likes:["cheese", "food"], id:[1234,2,3,4,5], another:"attribute"]
}
```

### JSON

If you wish to directly and separately retrieve attributes from a static JSON source,
the following settings are then relevant:

```properties
# cas.authn.attribute-repository.json[0].location=file://etc/cas/attribute-repository.json
# cas.authn.attribute-repository.json[0].order=0
# cas.authn.attribute-repository.json[0].id=
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

Retrieve attributes from a REST endpoint. RESTful settings for this feature 
are available [here](Configuration-Properties-Common.html#restful-integrations) under 
the configuration key `cas.authn.attribute-repository.rest[0]`.

```properties
# cas.authn.attribute-repository.rest[0].order=0
# cas.authn.attribute-repository.rest[0].id=
# cas.authn.attribute-repository.rest[0].case-insensitive=false
```

The authenticating user id is passed in form of a request parameter under `username`. The response is expected
to be a JSON map as such:

```json
{
  "name" : "JohnSmith",
  "age" : 29,
  "messages": ["msg 1", "msg 2", "msg 3"]
}
```

### Python/Javascript/Groovy

<div class="alert alert-warning"><strong>Usage</strong>
<p><strong>This feature is deprecated and is scheduled to be removed in the future.</strong></p>
</div>

Similar to the Groovy option but more versatile, this option takes advantage of Java's native 
scripting API to invoke Groovy, Python or Javascript scripting engines to compile a pre-defined script to resolve attributes. 
The following settings are relevant:

```properties
# cas.authn.attribute-repository.script[0].location=file:/etc/cas/script.groovy
# cas.authn.attribute-repository.script[0].order=0
# cas.authn.attribute-repository.script[0].id=
# cas.authn.attribute-repository.script[0].case-insensitive=false
# cas.authn.attribute-repository.script[0].engine-name=js|groovy|python
```

While Javascript and Groovy should be natively supported by CAS, Python scripts may need
to massage the CAS configuration to include the [Python modules](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22jython-standalone%22).

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

Retrieve attributes from a JDBC source. Database settings for this feature 
are available [here](Configuration-Properties-Common.html#database-settings) under 
the configuration key `cas.authn.attribute-repository.jdbc[0]`.

```properties
# cas.authn.attribute-repository.jdbc[0].attributes.uid=uid
# cas.authn.attribute-repository.jdbc[0].attributes.display-name=displayName
# cas.authn.attribute-repository.jdbc[0].attributes.cn=commonName
# cas.authn.attribute-repository.jdbc[0].attributes.affiliation=groupMembership

# cas.authn.attribute-repository.jdbc[0].single-row=true
# cas.authn.attribute-repository.jdbc[0].order=0
# cas.authn.attribute-repository.jdbc[0].id=
# cas.authn.attribute-repository.jdbc[0].require-all-attributes=true
# cas.authn.attribute-repository.jdbc[0].case-canonicalization=NONE|LOWER|UPPER
# cas.authn.attribute-repository.jdbc[0].query-type=OR|AND
# cas.authn.attribute-repository.jdbc[0].case-insensitive-query-attributes=username

# Used only when there is a mapping of many rows to one user
# cas.authn.attribute-repository.jdbc[0].column-mappings.column-attr-name1=columnAttrValue1
# cas.authn.attribute-repository.jdbc[0].column-mappings.column-attr-name2=columnAttrValue2
# cas.authn.attribute-repository.jdbc[0].column-mappings.column-attr-name3=columnAttrValue3

# cas.authn.attribute-repository.jdbc[0].sql=SELECT * FROM table WHERE {0}
# cas.authn.attribute-repository.jdbc[0].username=uid
```

### Grouper

This option reads all the groups from [a Grouper instance](https://incommon.org/software/grouper/) for the given CAS principal and adopts them
as CAS attributes under a `grouperGroups` multi-valued attribute.
To learn more about this topic, [please review this guide](../integration/Attribute-Resolution.html).

```properties
# cas.authn.attribute-repository.grouper[0].enabled=true
# cas.authn.attribute-repository.grouper[0].id=
# cas.authn.attribute-repository.grouper[0].order=0
```

You will also need to ensure `grouper.client.properties` is available on the classpath (i.e. `src/main/resources`)
with the following configured properties:

```properties
# grouperClient.webService.url = http://192.168.99.100:32768/grouper-ws/servicesRest
# grouperClient.webService.login = banderson
# grouperClient.webService.password = password
```

### Couchbase

This option will fetch attributes from a Couchbase database for a given CAS principal. To 
learn more about this topic, [please review this guide](../installation/Couchbase-Authentication.html). 
Database settings for this feature are available [here](Configuration-Properties-Common.html#couchbase-integration-settings) under the configuration key `cas.authn.attribute-repository.couchbase`.

```properties
# cas.authn.attribute-repository.couchbase.usernameAttribute=username
# cas.authn.attribute-repository.couchbase.order=0
# cas.authn.attribute-repository.couchbase.id=
```

### Redis

This option will fetch attributes from a Redis database for a given CAS principal. 

To learn more about this topic, [please review this guide](../installation/Redis-Authentication.html).

Common configuration settings for this feature are available [here](Configuration-Properties-Common.html#redis-configuration) 
under the configuration key `cas.authn.attribute-repository.redis`.

```properties
# cas.authn.attribute-repository.redis.order=0
# cas.authn.attribute-repository.redis.id=
```

### Microsoft Azure Active Directory

This option will fetch attributes from Microsoft Azure Active Directory using the Microsoft Graph API.

The following settings are available:

```properties
# cas.authn.attribute-repository.azure-active-directory[0].client-id=
# cas.authn.attribute-repository.azure-active-directory[0].client-secret=
# cas.authn.attribute-repository.azure-active-directory[0].client-secret=
# cas.authn.attribute-repository.azure-active-directory[0].tenant=

# cas.authn.attribute-repository.azure-active-directory[0].id=
# cas.authn.attribute-repository.azure-active-directory[0].order=0
# cas.authn.attribute-repository.azure-active-directory[0].case-insensitive=false
# cas.authn.attribute-repository.azure-active-directory[0].resource=
# cas.authn.attribute-repository.azure-active-directory[0].scope=
# cas.authn.attribute-repository.azure-active-directory[0].grant-type=
# cas.authn.attribute-repository.azure-active-directory[0].api-base-url=
# cas.authn.attribute-repository.azure-active-directory[0].attributes=
# cas.authn.attribute-repository.azure-active-directory[0].domain=
# cas.authn.attribute-repository.azure-active-directory[0].logging-level=
```

### Shibboleth Integrations

To learn more about this topic, [please review this guide](../integration/Shibboleth.html).

```properties
# cas.authn.shib-idp.server-url=https://idp.example.org
```

### Default Bundle

If you wish to release a default bundle of attributes to all applications,
and you would rather not duplicate the same attribute per every service definition,
then the following settings are relevant:

```properties
# cas.authn.attribute-repository.default-attributes-to-release=cn,givenName,uid,affiliation
```

To learn more about this topic, [please review this guide](../integration/Attribute-Release.html).

### Protocol Attributes

Defines whether CAS should include and release protocol attributes defined in the specification in addition to the
principal attributes. By default all authentication attributes are released when protocol attributes are enabled for
release. If you wish to restrict which authentication attributes get released, you can use the below settings to control authentication attributes more globally.

Protocol/authentication attributes may also be released conditionally on a per-service 
basis. To learn more about this topic, [please review this guide](../integration/Attribute-Release.html).

```properties
# cas.authn.authentication-attribute-release.only-release=authenticationDate,isFromNewLogin
# cas.authn.authentication-attribute-release.never-release=
# cas.authn.authentication-attribute-release.enabled=true
```

## Principal Resolution

In the event that a separate resolver is put into place, control how the final principal should be constructed by default. Principal resolution 
and Person Directory settings for this feature are 
available [here](Configuration-Properties-Common.html#person-directory-principal-resolution) under the configuration key `cas.person-directory`.

## Attribute Definitions

An attribute definition store allows one to describe metadata about necessary attributes 
with special decorations to be considered during attribute resolution and release.

```properties
# cas.person-directory.attribute-definition-store.json.location=file:/etc/cas/config/attribute-defns.json
```
 
## Authentication Engine

Control inner-workings of the CAS authentication engine, before and after the execution.

```properties
cas.authn.core.groovy-authentication-resolution.location=file:/etc/cas/config/GroovyAuthentication.groovy
cas.authn.core.groovy-authentication-resolution.order=0

cas.authn.core.service-authentication-resolution.order=0
```           

### Authentication Pre-Processing

#### Groovy

```properties
# cas.authn.core.engine.groovy-pre-processor.location=file:/etc/cas/config/GroovyPreProcessor.groovy
```

The script itself may be designed as:

```groovy
def run(Object[] args) {
    def transaction = args[0]
    def logger = args[1]
    true
}

def supports(Object[] args) {
    def credential = args[0]
    def logger = args[1]
    true
}
```

### Authentication Post-Processing

#### Groovy

```properties
# cas.authn.core.engine.groovy-post-processor.location=file:/etc/cas/config/GroovyPostProcessor.groovy
```

The script itself may be designed as:

```groovy
def run(Object[] args) {
    def builder = args[0]
    def transaction = args[1]
    def logger = args[2]
    true
}

def supports(Object[] args) {
    def credential = args[0]
    def logger = args[1]
    true
}
```

## Authentication Policy

To learn more about this topic, [please review this guide](../installation/Configuring-Authentication-Components.html#authentication-policy).

Global authentication policy that is applied when CAS attempts to vend and validate tickets.

```properties
# cas.authn.policy.required-handler-authentication-policy-enabled=false
```

### Any

Satisfied if any handler succeeds. Supports a tryAll flag to avoid short circuiting
and try every handler even if one prior succeeded.

```properties
# cas.authn.policy.any.try-all=false
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
# cas.authn.policy.source-selection-enabled=true
```
     
### Unique Principal

Satisfied if and only if the requesting principal has not already authenticated with CAS.
Otherwise the authentication event is blocked, preventing multiple logins.

<div class="alert alert-warning"><strong>Usage Warning</strong><p>Activating this policy is not without cost,
as CAS needs to query the ticket registry and all tickets present to determine whether the current user has established an authentication session anywhere. This will surely add a performance burden to the deployment. Use with care.</p></div>

```properties
# cas.authn.policy.unique-principal.enabled=true
```

### Not Prevented

Satisfied if and only if the authentication event is not blocked by a `PreventedException`.

```properties
# cas.authn.policy.not-prevented.enabled=true
```

### Required

Satisfied if and only if a specified handler successfully authenticates its credential.

```properties
# cas.authn.policy.req.try-all=false
# cas.authn.policy.req.handler-name=handlerName
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

RESTful settings for this feature are 
available [here](Configuration-Properties-Common.html#restful-integrations) under the configuration key `cas.authn.policy.rest[0]`.

Response codes from the REST endpoint are translated as such:

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
# cas.authn.throttle.username-parameter=username
# cas.authn.throttle.app-code=CAS

# cas.authn.throttle.failure.threshold=100
# cas.authn.throttle.failure.code=AUTHENTICATION_FAILED
# cas.authn.throttle.failure.range-seconds=60
```

Scheduler settings for this feature are available [here](Configuration-Properties-Common.html#job-scheduling) under the configuration key `cas.authn.throttle`.

### Bucket4j

Handle capacity planning and system overload protection using rate-limiting and token buckets.

```properties
# cas.authn.throttle.bucket4j.range-in-seconds=60
# cas.authn.throttle.bucket4j.capacity=120
# cas.authn.throttle.bucket4j.blocking=true
# cas.authn.throttle.bucket4j.overdraft=0
```

### MongoDb

Common configuration settings for this feature are available [here](Configuration-Properties-Common.html#mongodb-configuration) under the configuration key `cas.audit`. This feature uses the same data source used by the CAS MongoDb audit facility. 

### Redis

Common configuration settings for this feature are available [here](Configuration-Properties-Common.html#redis-configuration) under the configuration key `cas.audit`. This feature uses the same data source used by the CAS Redis audit facility.

### Hazelcast

Use a distributed Hazelcast map to record throttled authentication attempts. Hazelcast settings for this feature are available [here](Configuration-Properties-Common.html#hazelcast-configuration) under the configuration key `cas.authn.throttle.hazelcast`.

### Database

Queries the data source used by the CAS audit facility to prevent successive failed login attempts for a particular username from the
same IP address. 

Database settings for this feature are available [here](Configuration-Properties-Common.html#database-settings) 
under the configuration key `cas.authn.throttle.jdbc`.

```properties
# cas.authn.throttle.jdbc.audit-query=SELECT AUD_DATE FROM COM_AUDIT_TRAIL \
#   WHERE AUD_CLIENT_IP = ? AND AUD_USER = ? \
#   AND AUD_ACTION = ? AND APPLIC_CD = ? \
#   AND AUD_DATE >= ? ORDER BY AUD_DATE DESC
```

### CouchDb

Queries the data source used by the CAS audit facility to prevent successive failed login attempts for a particular username from the
same IP address. CouchDb settings for this feature are available [here](Configuration-Properties-Common.html#couchdb-configuration) under the configuration key
`cas.authn.throttle`. When using this feature the audit facility should be in synchronous mode.

## Adaptive Authentication

Control how CAS authentication should adapt itself to incoming client requests.
To learn more about this topic, [please review this guide](../mfa/Configuring-Adaptive-Authentication.html).

```properties
# cas.authn.adaptive.reject-countries=United.+
# cas.authn.adaptive.reject-browsers=Gecko.+
# cas.authn.adaptive.reject-ip-addresses=127.+

# cas.authn.adaptive.require-multifactor.mfa-duo=127.+|United.+|Gecko.+
```

Adaptive authentication can also react to specific times in order to trigger multifactor authentication.

```properties
# cas.authn.adaptive.require-timed-multifactor[0].provider-id=mfa-duo
# cas.authn.adaptive.require-timed-multifactor[0].on-or-after-hour=20
# cas.authn.adaptive.require-timed-multifactor[0].on-or-before-hour=7
# cas.authn.adaptive.require-timed-multifactor[0].on-days=Saturday,Sunday
```

### IP Address Intelligence

Examine the client IP address via the following strategies.

#### REST Adaptive Authentication

RESTful settings for this feature are available [here](Configuration-Properties-Common.html#restful-integrations) 
under the configuration key `cas.authn.adaptive.ip-intel.rest`.

#### Groovy Adaptive Authentication

```properties
# cas.authn.adaptive.ip-intel.groovy.location=file:/etc/cas/config/GroovyIPAddressIntelligenceService.groovy
```

#### BlackDot Adaptive Authentication

```properties
# cas.authn.adaptive.ip-intel.black-dot.url=http://check.getipintel.net/check.php?ip=%s
# cas.authn.adaptive.ip-intel.black-dot.email-address=
# cas.authn.adaptive.ip-intel.black-dot.mode=DYNA_LIST
```

## Surrogate Authentication

Authenticate on behalf of another user.
To learn more about this topic, [please review this guide](../installation/Surrogate-Authentication.html).

```properties
# cas.authn.surrogate.separator=+
# cas.authn.surrogate.tgt.time-to-kill-in-seconds=30
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
# cas.authn.surrogate.ldap.surrogate-search-filter=(&(principal={user})(memberOf=cn=edu:example:cas:something:{user},dc=example,dc=edu))
# cas.authn.surrogate.ldap.member-attribute-name=memberOf
# cas.authn.surrogate.ldap.member-attribute-value-regex=cn=edu:example:cas:something:([^,]+),.+
```

### CouchDb Surrogate Accounts

Settings for this feature are available [here](Configuration-Properties-Common.html#couchdb-configuration) under the configuration key `cas.authn.surrogate`. Surrogates may be stored either as part of the principals profile or as a series of principal/surrogate pair. The default is a key/value pair.

```properties
# cas.authn.surrogate.ldap.surrogate-search-filter=(&(principal={user})(memberOf=cn=edu:example:cas:something:{user},dc=example,dc=edu))
# cas.authn.surrogate.ldap.member-attribute-name=memberOf
# cas.authn.surrogate.ldap.member-attribute-value-regex=cn=edu:example:cas:something:([^,]+),.+
```

### JDBC Surrogate Accounts

 Database settings for this feature are available [here](Configuration-Properties-Common.html#database-settings) under the configuration key `cas.authn.surrogate.jdbc`.

```properties
# cas.authn.surrogate.jdbc.surrogate-search-query=SELECT COUNT(*) FROM surrogate WHERE username=?
# cas.authn.surrogate.jdbc.surrogate-account-query=SELECT surrogate_user AS surrogateAccount FROM surrogate WHERE username=?
```

### REST Surrogate Accounts

RESTful settings for this feature are available [here](Configuration-Properties-Common.html#restful-integrations) 
under the configuration key `cas.authn.surrogate.rest`.

### Notifications

Email notifications settings for this feature are available [here](Configuration-Properties-Common.html#email-notifications) 
under the configuration key `cas.authn.surrogate`. SMS notifications settings for this feature are 
available [here](Configuration-Properties-Common.html#sms-notifications) under the configuration key `cas.authn.surrogate`.

## QR Authentication

Attempt to login via a mobile device via a QR code. To learn more about this 
topic, [please review this guide](../installation/QRCode-Authentication.html).

```properties   
# Configure allowed Origin header values for browser clients.
# cas.authn.qr.allowed-origins=*
```

### JSON Device Repository

Attempt to login via a mobile device via a QR code. To learn more about this 
topic, [please review this guide](../installation/QRCode-Authentication.html).

```properties
# cas.authn.qr.json.location=file:/etc/cas/config/qrdevices.json
```

## Risk-based Authentication

Evaluate suspicious authentication requests and take action. To learn 
more about this topic, [please review this guide](../installation/Configuring-RiskBased-Authentication.html).

```properties
# cas.authn.adaptive.risk.threshold=0.6
# cas.authn.adaptive.risk.days-in-recent-history=30

# cas.authn.adaptive.risk.ip.enabled=false

# cas.authn.adaptive.risk.agent.enabled=false

# cas.authn.adaptive.risk.geo-location.enabled=false

# cas.authn.adaptive.risk.date-time.enabled=false
# cas.authn.adaptive.risk.date-time.window-in-hours=2

# cas.authn.adaptive.risk.response.block-attempt=false

# cas.authn.adaptive.risk.response.mfa-provider=
# cas.authn.adaptive.risk.response.risky-authentication-attribute=triggeredRiskBasedAuthentication
```

Email notifications settings for this feature are available [here](Configuration-Properties-Common.html#email-notifications) 
under the configuration key `cas.authn.adaptive.risk.response`. SMS notifications settings for this feature are 
available [here](Configuration-Properties-Common.html#sms-notifications) under the configuration key `cas.authn.adaptive.risk.response`.

## Passwordless Authentication

To learn more about this topic, [please review this guide](../installation/Passwordless-Authentication.html).

### Account Stores

```properties   
# cas.authn.passwordless.multifactor-authentication-activated=false
# cas.authn.passwordless.delegated-authentication-activated=false
```

#### Simple Account Store

```properties
# cas.authn.passwordless.accounts.simple.casuser=cas@example.org
```

#### Groovy Account Store

```properties
# cas.authn.passwordless.accounts.groovy.location=file:/etc/cas/config/pwdless.groovy
```

#### JSON Account Store

```properties
# cas.authn.passwordless.accounts.json.location=file:/etc/cas/config/pwdless-accounts.json
```

#### RESTful Account Store

RESTful settings for this feature are available [here](Configuration-Properties-Common.html#restful-integrations) 
under the configuration key `cas.authn.passwordless.accounts.rest`.

#### LDAP Account Store

LDAP settings for this feature are available [here](Configuration-Properties-Common.html#ldap-connection-settings) 
under the configuration key `cas.authn.passwordless.accounts.ldap`.

#### MongoDb Account Store

MongoDb settings for this feature are available [here](Configuration-Properties-Common.html#mongodb-configuration) 
under the configuration key `cas.authn.passwordless.accounts.mongo`.

### Token Management

```properties
# cas.authn.passwordless.accounts.expire-in-seconds=180
```

RESTful settings for this feature are available [here](Configuration-Properties-Common.html#restful-integrations) 
under the configuration key `cas.authn.passwordless.tokens.rest`. The signing key and the encryption 
key [are both JWKs](Configuration-Properties-Common.html#signing--encryption) of size `512` and `256`. 
Signing & encryption settings for this feature are available [here](Configuration-Properties-Common.html#signing--encryption) under 
the configuration key `cas.authn.passwordless.tokens`.

Email notifications settings for this feature are available [here](Configuration-Properties-Common.html#email-notifications) 
under the configuration key `cas.authn.passwordless.tokens`. SMS notifications settings for this feature are 
available [here](Configuration-Properties-Common.html#sms-notifications) under the configuration key `cas.authn.passwordless.tokens`.

Database settings for this feature are available [here](Configuration-Properties-Common.html#database-settings) under 
the configuration key `cas.authn.passwordless.tokens.jpa`. Scheduler settings for this feature are available [here](Configuration-Properties-Common.html#job-scheduling) under the configuration key `cas.authn.passwordless.tokens.jpa.cleaner`.

## Email Submissions

Email notifications settings are available [here](Configuration-Properties-Common.html#email-notifications).

## SMS Messaging

To learn more about this topic, [please review this guide](../notifications/SMS-Messaging-Configuration.html).

### Groovy

Send text messages using a Groovy script.

```properties
# cas.sms-provider.groovy.location=file:/etc/cas/config/SmsSender.groovy
```

### REST

Send text messages using a RESTful API. RESTful settings for this feature are 
available [here](Configuration-Properties-Common.html#restful-integrations) under the configuration key `cas.sms-provider.rest`.

### Twilio

Send text messaging using Twilio.

```properties
# cas.sms-provider.twilio.account-id=
# cas.sms-provider.twilio.token=
```

### TextMagic

Send text messaging using TextMagic.

```properties
# cas.sms-provider.text-magic.username=
# cas.sms-provider.text-magic.token=
# cas.sms-provider.text-magic.debugging=false
# cas.sms-provider.text-magic.password=
# cas.sms-provider.text-magic.read-timeout=5000
# cas.sms-provider.text-magic.connection-timeout=5000
# cas.sms-provider.text-magic.write-timeout=5000
# cas.sms-provider.text-magic.verifying-ssl=true
# cas.sms-provider.text-magic.api-key=
# cas.sms-provider.text-magic.api-key-prefix=
```

### Clickatell

Send text messaging using Clickatell.

```properties
# cas.sms-provider.clickatell.server-url=https://platform.clickatell.com/messages
# cas.sms-provider.clickatell.token=
```

### Nexmo

Send text messaging using Nexmo.
Nexmo needs at least apiSecret or signatureSecret field set.

```properties
# cas.sms-provider.nexmo.api-token=
# cas.sms-provider.nexmo.api-secret=
# cas.sms-provider.nexmo.signature-secret=
```

### Amazon SNS

Send text messaging using Amazon SNS.

```properties
# cas.sms-provider.sns.sender-id=
# cas.sms-provider.sns.max-price=
# cas.sms-provider.sns.sms-type=Transactional
```

AWS settings for this feature are available [here](Configuration-Properties-Common.html#amazon-integration-settings) 
under the configuration key `cas.sms-provider.sns`.

## Google Cloud Firebase Messaging

To learn more about this topic, [please review this guide](../notifications/Notifications-Configuration.html).

```properties
# cas.google-firebase-messaging.service-account-key.location=/path/to/account-key.json",
# cas.google-firebase-messaging.database-url=https://cassso-123456.firebaseio.com",
# cas.google-firebase-messaging.registration-token-attribute-name=registrationToken
# cas.google-firebase-messaging.scopes=https://www.googleapis.com/auth/firebase.messaging
```

## GeoTracking

To learn more about this topic, [please review this guide](../installation/GeoTracking-Authentication-Requests.html).

### GoogleMaps GeoTracking

Used to geo-profile authentication events.

```properties
# cas.google-maps.api-key=
# cas.google-maps.client-id=
# cas.google-maps.client-secret=
# cas.google-maps.connect-timeout=3000
# cas.google-maps.google-apps-engine=false
```

### Maxmind GeoTracking

Used to geo-profile authentication events.

```properties
# cas.maxmind.city-database=file:/etc/cas/maxmind/GeoLite2-City.mmdb
# cas.maxmind.country-database=file:/etc/cas/maxmind/GeoLite2-Country.mmdb
```

## Cassandra Authentication

To learn more about this topic, [please review this guide](../installation/Cassandra-Authentication.html).

```properties
# cas.authn.cassandra.username-attribute=
# cas.authn.cassandra.password-attribute=
# cas.authn.cassandra.table-name=
# cas.authn.cassandra.username=
# cas.authn.cassandra.password=
# cas.authn.cassandra.query=SELECT * FROM %s WHERE %s = ? ALLOW FILTERING
# cas.authn.cassandra.name=
# cas.authn.cassandra.order=
```

Common Cassandra settings for this feature are available [here](Configuration-Properties-Common.html#cassandra-configuration) under the configuration key `cas.authn.cassandra`.

Principal transformation settings for this feature are available [here](Configuration-Properties-Common.html#authentication-principal-transformation) under the configuration key `cas.authn.cassandra`. 

Password encoding settings for this feature are available [here](Configuration-Properties-Common.html#password-encoding) under the configuration key `cas.authn.cassandra`.

## Digest Authentication

To learn more about this topic, [please review this guide](../installation/Digest-Authentication.html).

```properties
# cas.authn.digest.users.casuser=3530292c24102bac7ced2022e5f1036a
# cas.authn.digest.users.anotheruser=7530292c24102bac7ced2022e5f1036b
# cas.authn.digest.realm=CAS
# cas.authn.digest.name=
# cas.authn.digest.order=
# cas.authn.digest.authentication-method=auth
```

## Radius Authentication

To learn more about this topic, [please review this guide](../mfa/RADIUS-Authentication.html).

Principal transformation settings for this feature are available [here](Configuration-Properties-Common.html#authentication-principal-transformation) under the configuration key `cas.authn.radius`.

Password encoding  settings for this feature are available [here](Configuration-Properties-Common.html#password-encoding) under the configuration key `cas.authn.radius`.

Radius  settings for this feature are available [here](Configuration-Properties-Common.html#radius-configuration) under the configuration key `cas.authn.radius`.

```properties
# cas.authn.radius.name=
```

## File Authentication

To learn more about this topic, [please review this guide](../installation/Permissive-Authentication.html).

Principal transformation settings for this feature are available [here](Configuration-Properties-Common.html#authentication-principal-transformation) under the configuration key `cas.authn.file`.

Password encoding  settings for this feature are available [here](Configuration-Properties-Common.html#password-encoding) under the configuration key `cas.authn.file`.

```properties
# cas.authn.file.separator=::
# cas.authn.file.filename=file:///path/to/users/file
# cas.authn.file.name=
```

## Groovy Authentication

To learn more about this topic, [please review this guide](../installation/Groovy-Authentication.html).

```properties
# cas.authn.groovy.order=0
# cas.authn.groovy.name=
```

## JSON Authentication

To learn more about this topic, [please review this guide](../installation/Permissive-Authentication.html).

Principal transformation settings for this feature are available [here](Configuration-Properties-Common.html#authentication-principal-transformation) under the configuration key `cas.authn.json`.

Password encoding  settings for this feature are available [here](Configuration-Properties-Common.html#password-encoding) under the configuration key `cas.authn.json`.

Password policy settings for this feature are available [here](Configuration-Properties-Common.html#password-policy-settings) under the configuration key `cas.authn.json.passwordPolicy`.

```properties
# cas.authn.json.location=file:///path/to/users/file.json
# cas.authn.json.name=
```

## Reject Users Authentication

To learn more about this topic, [please review this guide](../installation/Reject-Authentication.html).

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

Password encoding settings for this feature are available [here](Configuration-Properties-Common.html#password-encoding) under the configuration key `cas.authn.jdbc.query[0]`.

```properties
# cas.authn.jdbc.query[0].credential-criteria=
# cas.authn.jdbc.query[0].name=
# cas.authn.jdbc.query[0].order=0

# cas.authn.jdbc.query[0].sql=SELECT * FROM table WHERE name=?
# cas.authn.jdbc.query[0].field-password=password
# cas.authn.jdbc.query[0].field-expired=
# cas.authn.jdbc.query[0].field-disabled=
# cas.authn.jdbc.query[0].principal-attribute-list=sn,cn:commonName,givenName
```

### Search Database Authentication

Searches for a user record by querying against a username and password; the user is authenticated if at least one result is found.

Database settings for this feature are available [here](Configuration-Properties-Common.html#database-settings) under the configuration key `cas.authn.jdbc.search[0]`.

Principal transformation settings for this feature are available [here](Configuration-Properties-Common.html#authentication-principal-transformation) under the configuration key `cas.authn.jdbc.search[0]`.

Password encoding  settings for this feature are available [here](Configuration-Properties-Common.html#password-encoding) under the configuration key `cas.authn.jdbc.search[0]`.

```properties
# cas.authn.jdbc.search[0].field-user=
# cas.authn.jdbc.search[0].table-users=
# cas.authn.jdbc.search[0].field-password=
# cas.authn.jdbc.search[0].credential-criteria=
# cas.authn.jdbc.search[0].name=
# cas.authn.jdbc.search[0].order=0
```

### Bind Database Authentication

Authenticates a user by attempting to create a database connection using the username and (hashed) password.

Database settings for this feature are available [here](Configuration-Properties-Common.html#database-settings) under the configuration key `cas.authn.jdbc.bind[0]`.

Principal transformation settings for this feature are available [here](Configuration-Properties-Common.html#authentication-principal-transformation) under the configuration key `cas.authn.jdbc.bind[0]`.

Password encoding  settings for this feature are available [here](Configuration-Properties-Common.html#password-encoding) under the configuration key `cas.authn.jdbc.bind[0]`.

```properties
# cas.authn.jdbc.bind[0].credential-criteria=
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
# cas.authn.jdbc.encode[0].number-of-iterations=0
# cas.authn.jdbc.encode[0].number-of-iterations-field-name=numIterations
# cas.authn.jdbc.encode[0].salt-field-name=salt
# cas.authn.jdbc.encode[0].static-salt=
# cas.authn.jdbc.encode[0].sql=
# cas.authn.jdbc.encode[0].algorithm-name=
# cas.authn.jdbc.encode[0].password-field-name=password
# cas.authn.jdbc.encode[0].expired-field-name=
# cas.authn.jdbc.encode[0].disabled-field-name=

# cas.authn.jdbc.encode[0].credential-criteria=
# cas.authn.jdbc.encode[0].name=
# cas.authn.jdbc.encode[0].order=0
```

## CouchDb Authentication

To learn more about this topic, [please review this guide](../installation/CouchDb-Authentication.html).

Principal transformation settings for this feature are available [here](Configuration-Properties-Common.html#authentication-principal-transformation) under the configuration key `cas.authn.couch-db`.
Password encoding  settings for this feature are available [here](Configuration-Properties-Common.html#password-encoding) under the configuration key `cas.authn.couch-db`.

Common configuration settings for this feature are available [here](Configuration-Properties-Common.html#couchdb-configuration) under the configuration key `cas.authn`.

```properties
# cas.authn.couch-db.attributes=
# cas.authn.couch-db.username-attribute=username
# cas.authn.couch-db.password-attribute=password
# cas.authn.couch-db.name=
# cas.authn.couch-db.order=
```

## Redis Authentication

To learn more about this topic, [please review this guide](../installation/Redis-Authentication.html).

Principal transformation settings for this feature are available [here](Configuration-Properties-Common.html#authentication-principal-transformation) under the configuration key `cas.authn.redis`. Password encoding  settings for this feature are available [here](Configuration-Properties-Common.html#password-encoding) under the configuration key `cas.authn.redis`.

Common configuration settings for this feature are available [here](Configuration-Properties-Common.html#redis-configuration) 
under the configuration key `cas.authn`.

```properties
# cas.authn.redis.name=
# cas.authn.redis.order=
```

## MongoDb Authentication

To learn more about this topic, [please review this guide](../installation/MongoDb-Authentication.html). 

Principal transformation settings for this feature are available [here](Configuration-Properties-Common.html#authentication-principal-transformation) under the configuration key `cas.authn.mongo`. 
Password encoding  settings for this feature are available [here](Configuration-Properties-Common.html#password-encoding) under the configuration key `cas.authn.mongo`.

Common configuration settings for this feature are available [here](Configuration-Properties-Common.html#mongodb-configuration) under the configuration key `cas.authn`.

```properties
# cas.authn.mongo.attributes=
# cas.authn.mongo.username-attribute=username
# cas.authn.mongo.password-attribute=password
# cas.authn.mongo.principal-id-attribute=
# cas.authn.mongo.name=
```

## LDAP Authentication

CAS authenticates a username/password against an LDAP directory such as Active Directory or OpenLDAP.
There are numerous directory architectures and we provide configuration for four common cases.

Note that CAS will automatically create the appropriate components internally
based on the settings specified below. If you wish to authenticate against more than one LDAP
server, increment the index and specify the settings for the next LDAP server.

**Note:** Attributes retrieved as part of LDAP authentication are merged with all attributes
retrieved from [other attribute repository sources](#authentication-attributes), if any.
Attributes retrieved directly as part of LDAP authentication trump all other attributes.

To learn more about this topic, [please review this guide](../installation/LDAP-Authentication.html). 
LDAP settings for this feature are available [here](Configuration-Properties-Common.html#ldap-connection-settings) under the configuration key `cas.authn.ldap[0]`.

```properties
# Define attributes to be retrieved from LDAP as part of the same authentication transaction
# The left-hand size notes the source while the right-hand size indicate an optional renaming/remapping
# of the attribute definition. The same attribute name is allowed to be mapped multiple times to
# different attribute names.

# cas.authn.ldap[0].principal-attribute-list=sn,cn:commonName,givenName,eduPersonTargettedId:SOME_IDENTIFIER

# cas.authn.ldap[0].collect-dn-attribute=false
# cas.authn.ldap[0].principal-dn-attribute-name=principalLdapDn
# cas.authn.ldap[0].allow-multiple-principal-attribute-values=true
# cas.authn.ldap[0].allow-missing-principal-attribute-value=true
# cas.authn.ldap[0].credential-criteria=
```

To fetch and resolve attributes that carry tags/options, consider tagging the mapped attribute as such:

```properties
# cas.authn.ldap[0].principal-attribute-list=homePostalAddress:homePostalAddress;
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
# cas.authn.rest.charset=US-ASCII
```

## Google Apps Authentication

Authenticate via CAS into Google Apps services and applications.
To learn more about this topic, [please review this guide](../integration/Google-Apps-Integration.html).

```properties
# cas.google-apps.public-key-location=file:/etc/cas/public.key
# cas.google-apps.key-algorithm=RSA
# cas.google-apps.private-key-location=file:/etc/cas/private.key
```

## OpenID Authentication

Allow CAS to become an OpenID authentication provider. To learn more about this topic, [please review this guide](../protocol/OpenID-Protocol.html).

Principal resolution and Person Directory settings for this feature 
are available [here](Configuration-Properties-Common.html#person-directory-principal-resolution) under the configuration key `cas.authn.openid.principal`.

```properties
# cas.authn.openid.enforce-rp-id=false
# cas.authn.openid.name=
# cas.authn.openid.order=
```

## SPNEGO Authentication

To learn more about this topic, [please review this guide](../installation/SPNEGO-Authentication.html).

Principal resolution and Person Directory settings for this feature are available [here](Configuration-Properties-Common.html#person-directory-principal-resolution) under the configuration key `cas.authn.spnego.principal`.

```properties
# cas.authn.spnego.mixed-mode-authentication=false
# cas.authn.spnego.supported-browsers=MSIE,Trident,Firefox,AppleWebKit
# cas.authn.spnego.send401-on-authentication-failure=true
# cas.authn.spnego.ntlm-allowed=true
# cas.authn.spnego.principal-with-domain-name=false
# cas.authn.spnego.name=
# cas.authn.spnego.ntlm=false
```

### Webflow configuration

Webflow auto-configuration settings for this feature are available [here](Configuration-Properties-Common.html#webflow-auto-configuration) under the configuration key `cas.authn.spnego.webflow`.

### System Settings

```properties
# cas.authn.spnego.system.kerberos-conf=
# cas.authn.spnego.system.login-conf=
# cas.authn.spnego.system.kerberos-realm=EXAMPLE.COM
# cas.authn.spnego.system.kerberos-debug=true
# cas.authn.spnego.system.use-subject-creds-only=false
# cas.authn.spnego.system.kerberos-kdc=172.10.1.10
```

### Spnego Authentication Settings

```properties
# cas.authn.spnego.properties[0].cache-policy=600
# cas.authn.spnego.properties[0].jcifs-domain-controller=
# cas.authn.spnego.properties[0].jcifs-domain=
# cas.authn.spnego.properties[0].jcifs-password=
# cas.authn.spnego.properties[0].jcifs-username=
# cas.authn.spnego.properties[0].jcifs-service-password=
# cas.authn.spnego.properties[0].timeout=300000
# cas.authn.spnego.properties[0].jcifs-service-principal=HTTP/cas.example.com@EXAMPLE.COM
# cas.authn.spnego.properties[0].jcifs-netbios-wins=
```

### SPNEGO Client Selection Strategy

```properties
# cas.authn.spnego.host-name-client-action-strategy=hostnameSpnegoClientAction
```

### SPNEGO Client Selection Hostname

```properties
# cas.authn.spnego.alternative-remote-host-attribute=alternateRemoteHeader
# cas.authn.spnego.ips-to-check-pattern=127.+
# cas.authn.spnego.dns-timeout=2000
# cas.authn.spnego.host-name-pattern-string=.+
```

### SPNEGO LDAP Integration

LDAP settings for this feature are available [here](Configuration-Properties-Common.html#ldap-connection-settings) under the configuration key `cas.authn.spnego.ldap`.

```properties
# cas.authn.spnego.spnego-attribute-name=distinguishedName
```

### NTLM Authentication

```properties
# cas.authn.ntlm.include-pattern=
# cas.authn.ntlm.load-balance=true
# cas.authn.ntlm.domain-controller=
# cas.authn.ntlm.name=
# cas.authn.ntlm.order=
# cas.authn.ntlm.enabled=false
```

## JAAS Authentication

To learn more about this topic, [please review this guide](../installation/JAAS-Authentication.html). Principal transformation settings for this feature are available [here](Configuration-Properties-Common.html#authentication-principal-transformation) under the configuration key `cas.authn.jaas[0]`. Password encoding  settings for this feature are available [here](Configuration-Properties-Common.html#password-encoding) under the configuration key `cas.authn.jaas[0]`.

```properties
# cas.authn.jaas[0].realm=CAS
# cas.authn.jaas[0].kerberos-kdc-system-property=
# cas.authn.jaas[0].kerberos-realm-system-property
# cas.authn.jaas[0].name=
# cas.authn.jaas[0].order=
# cas.authn.jaas[0].credential-criteria=
# cas.authn.jaas[0].login-config-type=JavaLoginConfig
# cas.authn.jaas[0].login-configuration-file=/path/to/jaas.con
```

Principal resolution and Person Directory settings for this feature 
are available [here](Configuration-Properties-Common.html#person-directory-principal-resolution) 
under the configuration key `cas.authn.jaas[0].principal`.

Password policy settings for this feature are available [here](Configuration-Properties-Common.html#password-policy-settings) 
under the configuration key `cas.authn.jaas[0].password-policy`.


## GUA Authentication

To learn more about this topic, [please review this guide](../installation/GUA-Authentication.html).

### LDAP Repository

LDAP settings for this feature are available [here](Configuration-Properties-Common.html#ldap-connection-settings) under the configuration key `cas.authn.gua.ldap`.

```properties
# cas.authn.gua.ldap.image-attribute=userImageIdentifier
```

### Static Resource Repository

```properties
# cas.authn.gua.simple.[username1]=file:/path/to/image.jpg
# cas.authn.gua.simple.[username2]=file:/path/to/image.jpg
```

## JWT/Token Authentication

To learn more about this topic, [please review this guide](../installation/JWT-Authentication.html). Principal transformation settings for this feature are available [here](Configuration-Properties-Common.html#authentication-principal-transformation) under the configuration key `cas.authn.token`.

```properties
# cas.authn.token.name=
```

### Webflow configuration

Webflow auto-configuration settings for this feature are available [here](Configuration-Properties-Common.html#webflow-auto-configuration) under 
the configuration key `cas.authn.token.webflow`.

### JWT Tickets

Allow CAS tickets through various protocol channels to be created as JWTs. See [this guide](../installation/Configure-ServiceTicket-JWT.html) 
or [this guide](../protocol/REST-Protocol.html) for more info.

```properties
# cas.authn.token.crypto.encryption-enabled=true
# cas.authn.token.crypto.signing-enabled=true
```

The signing key and the encryption key [are both JWKs](Configuration-Properties-Common.html#signing--encryption) of size `512` and `256`. Signing & encryption settings for this feature are available [here](Configuration-Properties-Common.html#signing--encryption) under the configuration key `cas.authn.token`.

## Couchbase Authentication

To learn more about this topic, [please review this guide](../installation/Couchbase-Authentication.html).

Principal transformation settings for this feature are available [here](Configuration-Properties-Common.html#authentication-principal-transformation) under the configuration key `cas.authn.couchbase`.

 Password encoding  settings for this feature are available [here](Configuration-Properties-Common.html#password-encoding) under the configuration key `cas.authn.couchbase`.

Database settings for this feature are available [here](Configuration-Properties-Common.html#couchbase-integration-settings) under the configuration key `cas.authn.couchbase`.

```properties
# cas.authn.couchbase.username-attribute=username
# cas.authn.couchbase.password-attribute=psw

# cas.authn.couchbase.name=
# cas.authn.couchbase.order=
```

## Amazon Cloud Directory Authentication

To learn more about this topic, [please review this guide](../installation/AWS-CloudDirectory-Authentication.html).

Principal transformation settings for this feature are available [here](Configuration-Properties-Common.html#authentication-principal-transformation) under the configuration key `cas.authn.cloud-directory`.
Password encoding  settings for this feature are available [here](Configuration-Properties-Common.html#password-encoding) under the configuration key `cas.authn.cloud-directory`.

AWS settings for this feature are available [here](Configuration-Properties-Common.html#amazon-integration-settings) 
under the configuration key `cas.authn.cloud-directory`.

```properties
# cas.authn.cloud-directory.directory-arn=
# cas.authn.cloud-directory.schema-arn=
# cas.authn.cloud-directory.facet-name=

# cas.authn.cloud-directory.username-attribute-name=
# cas.authn.cloud-directory.password-attribute-name=
# cas.authn.cloud-directory.username-index-path=

# cas.authn.cloud-directory.name=
# cas.authn.cloud-directory.order=
```

## Amazon Cognito Authentication

To learn more about this topic, [please review this guide](../installation/AWS-Cognito-Authentication.html).

Principal transformation settings for this feature are available [here](Configuration-Properties-Common.html#authentication-principal-transformation) under the configuration key `cas.authn.cognito`.
Password encoding  settings for this feature are available [here](Configuration-Properties-Common.html#password-encoding) under the configuration key `cas.authn.cognito`.

AWS settings for this feature are available [here](Configuration-Properties-Common.html#amazon-integration-settings) under the configuration key `cas.authn.cognito`.

```properties
# cas.authn.cognito.name=
# cas.authn.cognito.order=

# cas.authn.cognito.client-id=
# cas.authn.cognito.user-pool-id=

# cas.authn.cognito.mapped-attributes.given_name=givenName
# cas.authn.cognito.mapped-attributes.[custom\:netid]=netid
```

## Okta Authentication

To learn more about this topic, [please review this guide](../installation/Okta-Authentication.html).

Principal transformation settings for this feature are available [here](Configuration-Properties-Common.html#authentication-principal-transformation) under the configuration key `cas.authn.okta`.
Password encoding settings for this feature are available [here](Configuration-Properties-Common.html#password-encoding) under the configuration key `cas.authn.okta`.

```properties
# cas.authn.okta.name=
# cas.authn.okta.order=  
# cas.authn.okta.credential-criteria=

# cas.authn.okta.organization-url=     

# cas.authn.okta.connection-timeout=5000
# cas.authn.okta.proxy-username=
# cas.authn.okta.proxy-password=
# cas.authn.okta.proxy-host=
# cas.authn.okta.proxy-port=
```

## Microsoft Azure Active Directory Authentication

To learn more about this topic, [please review this guide](../installation/Azure-ActiveDirectory-Authentication.html).

Principal transformation settings for this feature are available [here](Configuration-Properties-Common.html#authentication-principal-transformation) under the configuration key `cas.authn.azure-active-directory`.
Password encoding  settings for this feature are available [here](Configuration-Properties-Common.html#password-encoding) under the configuration key `cas.authn.azure-active-directory`.

```properties
# cas.authn.azure-active-directory.name=
# cas.authn.azure-active-directory.order=
# cas.authn.azure-active-directory.credential-criteria=

# cas.authn.azure-active-directory.client-id=
# cas.authn.azure-active-directory.login-url=https://login.microsoftonline.com/common/
# cas.authn.azure-active-directory.resource=https://graph.microsoft.com/
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
# cas.authn.remote-address.ip-address-range=
# cas.authn.remote-address.name=
# cas.authn.remote-address.order=
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
# cas.authn.accept.enabled=true
# cas.authn.accept.credential-criteria=
```

## X509 Authentication

To learn more about this topic, [please review this guide](../installation/X509-Authentication.html).

### Webflow configuration

Webflow auto-configuration settings for this feature are available [here](Configuration-Properties-Common.html#webflow-auto-configuration) under 
the configuration key `cas.authn.x509.webflow`.

```properties
# cas.authn.x509.webflow.port=8446
# cas.authn.x509.webflow.client-auth=want
```

### Principal Resolution

X.509 principal resolution can act on the following principal types:

| Type                    | Description
|-------------------------|----------------------------------------------------------------------
| `SERIAL_NO`             | Resolve the principal by the serial number with a configurable <strong>radix</strong>, ranging from 2 to 36. If <code>radix</code> is <code>16</code>, then the serial number could be filled with leading zeros to even the number of digits.
| `SERIAL_NO_DN`          | Resolve the principal by serial number and issuer dn.
| `SUBJECT`               | Resolve the principal by extracting one or more attribute values from the certificate subject DN and combining them with intervening delimiters.
| `SUBJECT_ALT_NAME`      | Resolve the principal by the subject alternative name extension. (type: otherName)
| `SUBJECT_DN`            | The default type; Resolve the principal by the certificate's subject dn.
| `CN_EDIPI`              | Resolve the principal by the Electronic Data Interchange Personal Identifier (EDIPI) from the Common Name.
| `RFC822_EMAIL`          | Resolve the principal by the [RFC822 Name](https://tools.ietf.org/html/rfc5280#section-4.2.1.6) (aka E-mail address) type of subject alternative name field. 

For the `CN_EDIPI`,`SUBJECT_ALT_NAME`, and `RFC822_EMAIL` principal resolvers, since not all certificates have those attributes, 
you may specify the following property in order to have a different attribute from the certificate used as the principal.  
If no alternative attribute is specified then the principal will be null and CAS will fail auth or use a different authenticator.

```properties
# cas.authn.x509.alternate-principal-attribute=subjectDn|sigAlgOid|subjectX500Principal|x509Rfc822Email|x509subjectUPN
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
# cas.authn.x509.crl-expired-policy=DENY|ALLOW|THRESHOLD
# cas.authn.x509.crl-unavailable-policy=DENY|ALLOW|THRESHOLD
# cas.authn.x509.crl-resource-expired-policy=DENY|ALLOW|THRESHOLD
# cas.authn.x509.crl-resource-unavailable-policy=DENY|ALLOW|THRESHOLD

# cas.authn.x509.revocation-checker=NONE|CRL|RESOURCE
# cas.authn.x509.crl-fetcher=RESOURCE|LDAP

# cas.authn.x509.crl-resources[0]=file:/...

# cas.authn.x509.cache-max-elements-in-memory=1000
# cas.authn.x509.cache-disk-overflow=false
# cas.authn.x509.cache-disk-size=100MB
# cas.authn.x509.cache-eternal=false
# cas.authn.x509.cache-time-to-live-seconds=7200

# cas.authn.x509.check-key-usage=false
# cas.authn.x509.revocation-policy-threshold=172800

# cas.authn.x509.reg-ex-subject-dn-pattern=.+
# cas.authn.x509.reg-ex-trusted-issuer-dn-pattern=.+

# cas.authn.x509.name=
# cas.authn.x509.order=

# cas.authn.x509.principal-descriptor=
# cas.authn.x509.max-path-length=1
# cas.authn.x509.throw-on-fetch-failure=false

# cas.authn.x509.check-all=false
# cas.authn.x509.require-key-usage=false
# cas.authn.x509.refresh-interval-seconds=3600
# cas.authn.x509.max-path-length-allow-unspecified=false

# SUBJECT_DN
# cas.authn.x509.subject-dn.format=[DEFAULT,RFC1779,RFC2253,CANONICAL]
```  

| Type          | Description
|---------------|----------------------------------------------------------------------
| `DEFAULT`     | Calls certificate.getSubjectDN() method for backwards compatibility but that method is ["denigrated"](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/security/cert/X509Certificate.html#getIssuerDN()). 
| `RFC1779`     | Calls [X500Principal.getName("RFC1779")](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/javax/security/auth/x500/X500Principal.html#getName()) which emits a subject DN with the attribute keywords defined in RFC 1779 (CN, L, ST, O, OU, C, STREET). Any other attribute type is emitted as an OID.
| `RFC2253`     | Calls [X500Principal.getName("RFC2253")](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/javax/security/auth/x500/X500Principal.html#getName()) which emits a subject DN with the attribute keywords defined in RFC 2253 (CN, L, ST, O, OU, C, STREET, DC, UID). Any other attribute type is emitted as an OID.
| `CANONICAL`   | Calls X500Principal.getName("CANONICAL" which emits a subject DN that starts with RFC 2253 and applies additional canonicalizations described in the [javadoc](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/javax/security/auth/x500/X500Principal.html#getName()).

```properties
# SERIAL_NO_DN
# cas.authn.x509.serial-no-dn.serial-number-prefix=SERIALNUMBER=
# cas.authn.x509.serial-no-dn.value-delimiter=,

# SERIAL_NO
# cas.authn.x509.serial-no.principal-s-n-radix=10
# cas.authn.x509.serial-no.principal-hex-s-n-zero-padding=false

# SUBJECT_ALT_NAME
# cas.authn.x509.subject-alt-name.alternate-principal-attribute=[sigAlgOid|subjectDn|subjectX500Principal|x509Rfc822Email]

# CN_EDIPI 
# cas.authn.x509.cn-edipi.alternate-principal-attribute=[sigAlgOid|subjectDn|subjectX500Principal|x509Rfc822Email|x509subjectUPN]

# RFC822_EMAIL 
# cas.authn.x509.rfc822-email.alternate-principal-attribute=[sigAlgOid|subjectDn|subjectX500Principal|x509subjectUPN]
```

### X509 Certificate Extraction

These settings can be used to turn on and configure CAS to
extract an X509 certificate from a base64 encoded certificate
on a HTTP request header (placed there by a proxy in front of CAS).
If this is set to true, it is important that the proxy cannot
be bypassed by users and that the proxy ensures the header
never originates from the browser.

```properties
# cas.authn.x509.extract-cert=false
# cas.authn.x509.ssl-header-name=ssl_client_cert
```

The specific parsing logic for the certificate is compatible
with the Tomcat SSLValve which can work with headers set by
Apache HTTPD, Nginx, Haproxy, BigIP F5, etc.

### X509 Principal Resolution

```properties
# cas.authn.x509.principal-type=SERIAL_NO|SERIAL_NO_DN|SUBJECT|SUBJECT_ALT_NAME|SUBJECT_DN
```

Principal resolution and Person Directory settings for this feature are available [here](Configuration-Properties-Common.html#person-directory-principal-resolution) under the configuration key `cas.authn.x509.principal`.

### X509 LDAP Integration

LDAP settings for the X509 feature (used if fetching CRLs from LDAP) are available [here](Configuration-Properties-Common.html#ldap-connection-settings) under the configuration key `cas.authn.x509.ldap`.

See LDAP attribute repositories [here](Configuration-Properties.html#ldap) to fetch additional LDAP attributes using the principal extracted from the X509 certificate. 

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
# cas.authn.shiro.required-permissions=value1,value2,...
# cas.authn.shiro.required-roles=value1,value2,...
# cas.authn.shiro.location=classpath:shiro.ini
# cas.authn.shiro.name=
```

## Trusted Authentication

To learn more about this topic, [please review this guide](../installation/Trusted-Authentication.html). Principal resolution and Person Directory settings for this feature are available [here](Configuration-Properties-Common.html#person-directory-principal-resolution) under the configuration key `cas.authn.trusted`.

```properties
# cas.authn.trusted.name=
# cas.authn.trusted.order=
# cas.authn.trusted.remote-principal-header=
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
# cas.authn.wsfed[0].identity-provider-url=https://adfs.example.org/adfs/ls/
# cas.authn.wsfed[0].identity-provider-identifier=https://adfs.example.org/adfs/services/trust
# cas.authn.wsfed[0].relying-party-identifier=urn:cas:localhost
# cas.authn.wsfed[0].signing-certificate-resources=classpath:adfs-signing.crt
# cas.authn.wsfed[0].identity-attribute=upn

# cas.authn.wsfed[0].attributes-type=WSFED
# cas.authn.wsfed[0].tolerance=10000
# cas.authn.wsfed[0].attribute-resolver-enabled=true
# cas.authn.wsfed[0].auto-redirect=true
# cas.authn.wsfed[0].name=
# cas.authn.wsfed[0].attribute-mutator-script.location=file:/etc/cas/config/wsfed-attr.groovy

# cas.authn.wsfed[0].principal.principal-attribute=
# cas.authn.wsfed[0].principal.return-null=false

# Private/Public keypair used to decrypt assertions, if any.
# cas.authn.wsfed[0].encryption-private-key=classpath:private.key
# cas.authn.wsfed[0].encryption-certificate=classpath:certificate.crt
# cas.authn.wsfed[0].encryption-private-key-password=NONE
```

### Signing & Encryption

The signing and encryption keys [are both JWKs](Configuration-Properties-Common.html#signing--encryption) of size `512` and `256`.
The encryption algorithm is set to `AES_128_CBC_HMAC_SHA_256`. Signing & encryption settings for this feature are available [here](Configuration-Properties-Common.html#signing--encryption) under `cas.authn.wsfed[0].cookie`.

## Multifactor Authentication

To learn more about this topic, [please review this guide](../mfa/Configuring-Multifactor-Authentication.html).

```properties
# Describe the global failure mode in case provider cannot be reached
# cas.authn.mfa.global-failure-mode=CLOSED

# Design the attribute chosen to communicate the authentication context
# cas.authn.mfa.authentication-context-attribute=authnContextClass

# Identify the request content type for non-browser MFA requests
# cas.authn.mfa.content-type=application/cas
```

### Multifactor Authentication: Global Trigger

```properties
# Activate MFA globally for all, regardless of other settings
# cas.authn.mfa.global-provider-id=mfa-duo
```

### Multifactor Authentication: Authentication Attribute Trigger

```properties
# Activate MFA globally based on authentication metadata attributes
# cas.authn.mfa.global-authentication-attribute-name-triggers=customAttributeName
# cas.authn.mfa.global-authentication-attribute-value-regex=customRegexValue
```

### Multifactor Authentication: Principal Attribute Trigger

```properties
# Activate MFA globally based on principal attributes
# cas.authn.mfa.global-principal-attribute-name-triggers=memberOf,eduPersonPrimaryAffiliation

# Specify the regular expression pattern to trigger multifactor when working with a single provider.
# Comment out the setting when working with multiple multifactor providers
# cas.authn.mfa.global-principal-attribute-value-regex=faculty|staff

# Activate MFA globally based on principal attributes and a groovy-based predicate
# cas.authn.mfa.global-principal-attribute-predicate=file:/etc/cas/PredicateExample.groovy
```

### Multifactor Authentication: REST API Trigger

RESTful settings for this feature are available [here](Configuration-Properties-Common.html#restful-integrations) under the configuration key `cas.authn.mfa.rest`.

### Multifactor Authentication: Groovy Trigger

```properties
# Activate MFA based on a Groovy script
# cas.authn.mfa.groovyScript=file:/etc/cas/mfaGroovyTrigger.groovy
```

### Multifactor Authentication: Internet2 Grouper Trigger

```properties
# Activate MFA based on Internet2's Grouper
# cas.authn.mfa.grouper-group-field=NAME|EXTENSION|DISPLAY_NAME|DISPLAY_EXTENSION
```

### Multifactor Authentication: Http Request Trigger

```properties
# cas.authn.mfa.request-parameter=authn_method
# cas.authn.mfa.request-header=authn_method
# cas.authn.mfa.session-attribute=authn_method
```

### Multifactor Authentication: Provider Selection

```properties
# Select MFA provider, if resolved more than one, via Groovy script
# cas.authn.mfa.provider-selector-groovy-script=file:/etc/cas/mfaGroovySelector.groovy

# Enable provider selection menu, if resolved more than one
cas.authn.mfa.provider-selection-enabled=true
```

### Multifactor Trusted Device/Browser

To learn more about this topic, [please review this guide](../mfa/Multifactor-TrustedDevice-Authentication.html).

```properties
# cas.authn.mfa.trusted.authentication-context-attribute=isFromTrustedMultifactorAuthentication
# cas.authn.mfa.trusted.device-registration-enabled=true
# cas.authn.mfa.trusted.key-generator-type=DEFAULT|LEGACY
```

The following strategies can be used to generate keys for trusted device records:

| Type                 | Description
|----------------------|------------------------------------------------------------------------------------------------
| `DEFAULT`            | Uses a combination of the username, device name and device fingerprint to generate the device key.
| `LEGACY`             | Deprecated. Uses a combination of the username, record date and device fingerprint to generate the device key.

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
 
#### DynamoDb Storage
 
Common configuration settings for this feature are available [here](Configuration-Properties-Common.html#dynamodb-configuration)
under the configuration key `cas.authn.mfa.trusted`.

AWS settings for this feature are available [here](Configuration-Properties-Common.html#amazon-integration-settings) 
under the configuration key `cas.authn.mfa.trusted.dynamo-db`.

```properties
# cas.authn.mfa.trusted.dynamo-db.tableName=DynamoDbCasMfaTrustRecords
```

#### REST Storage

```properties
# cas.authn.mfa.trusted.rest.endpoint=https://api.example.org/trustedBrowser
```

#### Trusted Device Fingerprint

```properties
# cas.authn.mfa.trusted.device-fingerprint.componentSeparator=@  

# cas.authn.mfa.trusted.device-fingerprint.cookie.enabled=true
# cas.authn.mfa.trusted.device-fingerprint.cookie.order=1

# cas.authn.mfa.trusted.device-fingerprint.client-ip.enabled=true
# cas.authn.mfa.trusted.device-fingerprint.client-ip.order=2

# cas.authn.mfa.trusted.device-fingerprint.geolocation.enabled=false
# cas.authn.mfa.trusted.device-fingerprint.geolocation.order=4
```

The device fingerprint cookie component can be configured with the common cookie properties found [here](Configuration-Properties-Common.html#cookie-properties) under the configuration key `cas.authn.mfa.trusted.device-fingerprint.cookie`.
The default cookie name is set to `MFATRUSTED` and the default maxAge is set to `2592000`.

The device fingerprint cookie component supports signing & encryption. The signing and encryption keys [are both JWKs](Configuration-Properties-Common.html#signing--encryption) of size `512` and `256`.
The encryption algorithm is set to `AES_128_CBC_HMAC_SHA_256`. Signing & encryption settings for this feature are available [here](Configuration-Properties-Common.html#signing--encryption) under the configuration key `cas.authn.mfa.trusted.device-fingerprint.cookie`.

#### Cleaner

A cleaner process is scheduled to run in the background to clean up expired and stale tickets.
This section controls how that process should behave. Scheduler settings for this feature 
are available [here](Configuration-Properties-Common.html#job-scheduling) under the configuration key `cas.authn.mfa.trusted.cleaner`.


### Simple Multifactor Authentication

To learn more about this topic, [please review this guide](../mfa/Simple-Multifactor-Authentication.html).

```properties
# cas.authn.mfa.simple.name=
# cas.authn.mfa.simple.order=
# cas.authn.mfa.simple.time-to-kill-in-seconds=30
# cas.authn.mfa.simple.token-length=6
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

# cas.authn.mfa.gauth.window-size=3
# cas.authn.mfa.gauth.code-digits=6
# cas.authn.mfa.gauth.time-step-size=30
# cas.authn.mfa.gauth.rank=0
# cas.authn.mfa.gauth.trusted-device-enabled=false
# cas.authn.mfa.gauth.multiple-device-registration-enabled=false

# cas.authn.mfa.gauth.name=
# cas.authn.mfa.gauth.order=
```

Multifactor authentication bypass settings for this provider are available [here](Configuration-Properties-Common.html#multifactor-authentication-bypass)
under the configuration key `cas.authn.mfa.gauth`. Scheduler settings for this feature are available [here](Configuration-Properties-Common.html#job-scheduling) under the configuration key `cas.authn.mfa.gauth.cleaner`.

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

RESTful settings for this feature are available [here](Configuration-Properties-Common.html#restful-integrations) under the configuration key `cas.authn.mfa.gauth.rest`.

Additionally, tokens can be managed via REST using the following settings:

```properties
# cas.authn.mfa.gauth.rest.token-url=https://somewhere.gauth.com
```

#### Google Authenticator MongoDb

 Configuration settings for this feature are available [here](Configuration-Properties-Common.html#mongodb-configuration) under the configuration key `cas.authn.mfa.gauth`.  The following settings are additionally available for this feature:

```properties
# cas.authn.mfa.gauth.mongo.token-collection=MongoDbGoogleAuthenticatorTokenRepository
```

#### Google Authenticator LDAP

LDAP settings for this feature are available [here](Configuration-Properties-Common.html#ldap-connection-settings) under the configuration key `cas.authn.mfa.gauth.ldap`. 

The following settings are additionally available for this feature:

```properties
# cas.authn.mfa.gauth.ldap.account-attribute-name=gauthRecord
```

#### Google Authenticator Redis

 Configuration settings for this feature are available [here](Configuration-Properties-Common.html#redis-configuration) 
 under the configuration key `cas.authn.mfa.gauth`.  
 
 
#### Google Authenticator JPA

Database settings for this feature are available [here](Configuration-Properties-Common.html#database-settings) under the configuration key `cas.authn.mfa.gauth.jpa`.

### YubiKey

To learn more about this topic, [please review this guide](../mfa/YubiKey-Authentication.html).

```properties
# cas.authn.mfa.yubikey.client-id=
# cas.authn.mfa.yubikey.secret-key=
# cas.authn.mfa.yubikey.rank=0
# cas.authn.mfa.yubikey.api-urls=
# cas.authn.mfa.yubikey.trusted-device-enabled=false
# cas.authn.mfa.yubikey.multiple-device-registration-enabled=false

# cas.authn.mfa.yubikey.name=
# cas.authn.mfa.yubikey.order=
```

Multifactor authentication bypass settings for this provider are 
available [here](Configuration-Properties-Common.html#multifactor-authentication-bypass) under the configuration key `cas.authn.mfa.yubikey`.

#### YubiKey REST Device Store

RESTful settings for this feature are available [here](Configuration-Properties-Common.html#restful-integrations) under the configuration key `cas.authn.mfa.yubikey.rest`.

#### YubiKey JSON Device Store

```properties
# cas.authn.mfa.yubikey.json-file=file:/etc/cas/deviceRegistrations.json
```

#### YubiKey Allowed Device Store

```properties
# cas.authn.mfa.yubikey.allowed-devices.uid1=yubikeyPublicId1
# cas.authn.mfa.yubikey.allowed-devices.uid2=yubikeyPublicId2
```

#### YubiKey Registration Records Encryption and Signing

```properties
# cas.authn.mfa.yubikey.crypto.enabled=true
```

The signing key and the encryption key [are both JWKs](Configuration-Properties-Common.html#signing--encryption) of size `512` and `256`. Signing & encryption settings for this feature are available [here](Configuration-Properties-Common.html#signing--encryption) under the configuration key `cas.authn.mfa.yubikey`.

### YubiKey JPA Device Store

Database settings for this feature are available [here](Configuration-Properties-Common.html#database-settings) under the configuration key `cas.authn.mfa.yubikey.jpa`.

### YubiKey CouchDb Device Store

Configuration settings for this feature are available [here](Configuration-Properties-Common.html#couchdb-configuration) under the configuration key `cas.authn.mfa.yubikey`.

### YubiKey MongoDb Device Store

Configuration settings for this feature are available [here](Configuration-Properties-Common.html#mongodb-configuration) under the configuration key `cas.authn.mfa.yubikey`.

### YubiKey DynamoDb Device Store

Configuration settings for this feature are available [here](Configuration-Properties-Common.html#dynamodb-configuration) under the configuration key `cas.authn.mfa.yubikey`.

### YubiKey Redis Device Store

Common configuration settings for this feature are available [here](Configuration-Properties-Common.html#redis-configuration)
under the configuration key `cas.authn.mfa.yubikey`.
 
### Radius OTP

To learn more about this topic, [please review this guide](../mfa/RADIUS-Authentication.html).

```properties
# cas.authn.mfa.radius.rank=0
# cas.authn.mfa.radius.trusted-device-enabled=false
# cas.authn.mfa.radius.allowed-authentication-attempts=-1
# cas.authn.mfa.radius.name=
# cas.authn.mfa.radius.order=
```

Radius  settings for this feature are available [here](Configuration-Properties-Common.html#radius-configuration) under the configuration key `cas.authn.mfa.radius`.

Multifactor authentication bypass settings for this provider are available [here](Configuration-Properties-Common.html#multifactor-authentication-bypass) under the configuration key `cas.authn.mfa.radius`.

### DuoSecurity

To learn more about this topic, [please review this guide](../mfa/DuoSecurity-Authentication.html).

```properties
# cas.authn.mfa.duo[0].duo-secret-key=
# cas.authn.mfa.duo[0].rank=0
# cas.authn.mfa.duo[0].duo-application-key=
# cas.authn.mfa.duo[0].duo-integration-key=
# cas.authn.mfa.duo[0].duo-api-host=
# cas.authn.mfa.duo[0].trusted-device-enabled=false
# cas.authn.mfa.duo[0].id=mfa-duo
# cas.authn.mfa.duo[0].registration-url=https://registration.example.org/duo-enrollment
# cas.authn.mfa.duo[0].name=
# cas.authn.mfa.duo[0].order=
```

Multifactor authentication bypass settings for this provider are 
available [here](Configuration-Properties-Common.html#multifactor-authentication-bypass) under 
the configuration key `cas.authn.mfa.duo[0]`.


#### Web SDK

The `duo-application-key` is a required string, at least 40 characters long, that you 
generate and keep secret from Duo. You can generate a random string in Python with:

```python
import os, hashlib
print hashlib.sha1(os.urandom(32)).hexdigest()
```

#### Universal Prompt

Universal Prompt no longer requires you to generate and use a application key value. Instead, it requires a *client id* and *client secret*,
which are known and taught CAS using the integration key and secret key configuration settings. You will need get your integration key, 
secret key, and API hostname from Duo Security when you register CAS as a protected application. 

### FIDO2 WebAuthn

To learn more about this topic, [please review this guide](../mfa/FIDO2-WebAuthn-Authentication.html).

```properties
# cas.authn.mfa.web-authn.allowed-origins=
# cas.authn.mfa.web-authn.application-id=
# cas.authn.mfa.web-authn.relying-party-name=CAS WebAuthn 
# cas.authn.mfa.web-authn.relying-party-id=

# cas.authn.mfa.web-authn.display-name-attribute=displayName
# cas.authn.mfa.web-authn.allow-primary-authentication=false

# cas.authn.mfa.web-authn.allow-unrequested-extensions=false
# cas.authn.mfa.web-authn.allow-untrusted-attestation=false
# cas.authn.mfa.web-authn.validate-signature-counter=true
# cas.authn.mfa.web-authn.attestation-conveyance-preference=DIRECT|INDIRECT|NONE
# cas.authn.mfa.web-authn.trusted-device-metadata.location=

# cas.authn.mfa.web-authn.trusted-device-enabled=false

# cas.authn.mfa.web-authn.expire-devices=30
# cas.authn.mfa.web-authn.expire-devices-time-unit=DAYS
```   

Multifactor authentication bypass settings for this provider are
available [here](Configuration-Properties-Common.html#multifactor-authentication-bypass) under the configuration key `cas.authn.mfa.web-authn`.

The signing key and the encryption key [are both JWKs](Configuration-Properties-Common.html#signing--encryption) of size `512` and `256`.
Signing & encryption settings for this feature are available [here](Configuration-Properties-Common.html#signing--encryption)
under the configuration key `cas.authn.mfa.web-authn`.                                                      

### FIDO2 WebAuthn Cleaner

Scheduler settings for this feature are 
available [here](Configuration-Properties-Common.html#job-scheduling) under the configuration key `cas.authn.mfa.web-authn.cleaner`.

### FIDO2 WebAuthn JSON

```properties
# cas.authn.mfa.web-authn.json.location=file:///etc/cas/config/devices.json
```

### FIDO2 WebAuthn MongoDb

Common configuration settings for this feature are 
available [here](Configuration-Properties-Common.html#mongodb-configuration) under the configuration key `cas.authn.mfa.web-authn`.

### FIDO2 WebAuthn LDAP

Common configuration settings for this feature are 
available [here](Configuration-Properties-Common.html#ldap-connection-settings) under the configuration key `cas.authn.mfa.web-authn.ldap`.

```properties
# cas.authn.mfa.web-authn.ldap.account-attribute-name=casWebAuthnRecord
```

### FIDO2 WebAuthn JPA

Database settings for this feature are available [here](Configuration-Properties-Common.html#database-settings) 
under the configuration key `cas.authn.mfa.web-authn.jpa`.

### FIDO2 WebAuthn Redis

Common configuration settings for this feature are available [here](Configuration-Properties-Common.html#redis-configuration)
under the configuration key `cas.authn.mfa.web-authn`.

### FIDO2 WebAuthn DynamoDb

Common configuration settings for this feature are available [here](Configuration-Properties-Common.html#dynamodb-configuration)
under the configuration key `cas.authn.mfa.web-authn`.

AWS settings for this feature are available [here](Configuration-Properties-Common.html#amazon-integration-settings) 
under the configuration key `cas.authn.mfa.web-authn.dynamo-db`.

### FIDO U2F

To learn more about this topic, [please review this guide](../mfa/FIDO-U2F-Authentication.html).

```properties
# cas.authn.mfa.u2f.rank=0
# cas.authn.mfa.u2f.name=
# cas.authn.mfa.u2f.order=

# Expiry of U2F device registration requests:
# cas.authn.mfa.u2f.expire-registrations=30
# cas.authn.mfa.u2f.expire-registrations-time-unit=SECONDS

# Expiry of U2F devices since registration, independent of last time used:
# cas.authn.mfa.u2f.expire-devices=30
# cas.authn.mfa.u2f.expire-devices-time-unit=DAYS
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

Scheduler settings for this feature are 
available [here](Configuration-Properties-Common.html#job-scheduling) under the configuration key `cas.authn.mfa.u2f.cleaner`.

### FIDO U2F CouchDb

Common configuration settings for this feature are 
available [here](Configuration-Properties-Common.html#couchdb-configuration) under the configuration key `cas.authn.mfa.u2f`.

### FIDO U2F MongoDb

Common configuration settings for this feature are 
available [here](Configuration-Properties-Common.html#mongodb-configuration) under the configuration key `cas.authn.mfa.u2f`.

### FIDO U2F DynamoDb

Common configuration settings for this feature are available [here](Configuration-Properties-Common.html#dynamodb-configuration)
under the configuration key `cas.authn.mfa.u2f`.

AWS settings for this feature are available [here](Configuration-Properties-Common.html#amazon-integration-settings) 
under the configuration key `cas.authn.mfa.u2f.dynamo-db`.

### FIDO U2F Redis

Common configuration settings for this feature are available [here](Configuration-Properties-Common.html#redis-configuration)
under the configuration key `cas.authn.mfa.u2f`.

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
# cas.authn.mfa.swivel.swivel-turing-image-url=https://turing.example.edu/TURingImage
# cas.authn.mfa.swivel.swivel-url=https://swivel.example.org/pinsafe
# cas.authn.mfa.swivel.shared-secret=Th3Sh@r3d$ecret
# cas.authn.mfa.swivel.ignore-ssl-errors=false
# cas.authn.mfa.swivel.rank=0
# cas.authn.mfa.swivel.name=
# cas.authn.mfa.swivel.order=
```

Multifactor authentication bypass settings for this provider are available [here](Configuration-Properties-Common.html#multifactor-authentication-bypass) under the configuration key `cas.authn.mfa.swivel`.


### Authy

To learn more about this topic, [please review this guide](../mfa/AuthyAuthenticator-Authentication.html).

```properties
# cas.authn.mfa.authy.api-key=
# cas.authn.mfa.authy.api-url=
# cas.authn.mfa.authy.phone-attribute=phone
# cas.authn.mfa.authy.mail-attribute=mail
# cas.authn.mfa.authy.country-code=1
# cas.authn.mfa.authy.force-verification=true
# cas.authn.mfa.authy.trusted-device-enabled=false
# cas.authn.mfa.authy.name=
# cas.authn.mfa.authy.order=
```

Multifactor authentication bypass settings for this provider are 
available [here](Configuration-Properties-Common.html#multifactor-authentication-bypass) under the configuration key `cas.authn.mfa.authy`.


### Acceptto

To learn more about this topic, [please review this guide](../mfa/Acceptto-Authentication.html).

```properties
# cas.authn.mfa.acceptto.application-id=
# cas.authn.mfa.acceptto.secret=
# cas.authn.mfa.acceptto.organization-id=
# cas.authn.mfa.acceptto.organization-secret=

# cas.authn.mfa.acceptto.authn-selection-url=https://mfa.acceptto.com/mfa/index
# cas.authn.mfa.acceptto.api-url=https://mfa.acceptto.com/api/v9/
# cas.authn.mfa.acceptto.message=Do you want to login via CAS?
# cas.authn.mfa.acceptto.timeout=120
# cas.authn.mfa.acceptto.email-attribute=mail    
# cas.authn.mfa.acceptto.group-attribute=    

# cas.authn.mfa.acceptto.registration-api-url=https://mfa.acceptto.com/api/integration/v1/mfa/authenticate
# cas.authn.mfa.acceptto.registration-api-public-key=file:/path/to/publickey.pem

# cas.authn.mfa.acceptto.name=
# cas.authn.mfa.acceptto.order=
# cas.authn.mfa.acceptto.rank=0
```

Multifactor authentication bypass settings for this provider are available [here](Configuration-Properties-Common.html#multifactor-authentication-bypass) under the configuration key `cas.authn.mfa.acceptto`.

## SAML Core

Control core SAML functionality within CAS.

```properties
# cas.saml-core.ticketid-saml2=false
# cas.saml-core.skew-allowance=5
# cas.saml-core.issue-length=30
# cas.saml-core.attribute-namespace=http://www.ja-sig.org/products/cas/
# cas.saml-core.issuer=localhost
# cas.saml-core.security-manager=org.apache.xerces.util.SecurityManager
```

## SAML IdP

Allow CAS to become a SAML2 identity provider.

To learn more about this topic, [please review this guide](../installation/Configuring-SAML2-Authentication.html).

```properties
# cas.authn.saml-idp.entity-id=https://cas.example.org/idp
# cas.authn.saml-idp.replicate-sessions=false

# cas.authn.saml-idp.authentication-context-class-mappings[0]=urn:oasis:names:tc:SAML:2.0:ac:classes:SomeClassName->mfa-duo
# cas.authn.saml-idp.authentication-context-class-mappings[1]=https://refeds.org/profile/mfa->mfa-gauth

# cas.authn.saml-idp.attribute-friendly-names[0]=urn:oid:1.3.6.1.4.1.5923.1.1.1.6->eduPersonPrincipalName
  
# cas.authn.saml-idp.attribute-query-profile-enabled=true
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
# cas.authn.saml-idp.metadata.location=file:/etc/cas/saml
# cas.authn.saml-idp.metadata.metadata-backup-location=

# cas.authn.saml-idp.metadata.cache-expiration-minutes=30
# cas.authn.saml-idp.metadata.fail-fast=true
# cas.authn.saml-idp.metadata.private-key-alg-name=RSA
# cas.authn.saml-idp.metadata.require-valid-metadata=true
# cas.authn.saml-idp.metadata.force-metadata-refresh=true

# cas.authn.saml-idp.metadata.basic-authn-username=
# cas.authn.saml-idp.metadata.basic-authn-password=
# cas.authn.saml-idp.metadata.supported-content-types=
```

#### SAML Metadata JPA

Database settings for this feature are available [here](Configuration-Properties-Common.html#database-settings) 
under the configuration key `cas.authn.saml-idp.metadata.jpa`.

```properties
# cas.authn.saml-idp.metadata.jpa.idp-metadata-enabled=true
```
 
The signing key and the encryption key [are both JWKs](Configuration-Properties-Common.html#signing--encryption) of size `512` and `256`. Signing & 
encryption settings for this feature are available [here](Configuration-Properties-Common.html#signing--encryption) under the configuration key `cas.authn.saml-idp.metadata.jpa`.
 
#### SAML Metadata CouchDb

Common configuration settings for this feature are available [here](Configuration-Properties-Common.html#couchdb-configuration) 
under the configuration key `cas.authn.saml-idp.metadata`.
 
```properties
# cas.authn.saml-idp.metadata.couch-db.idp-metadata-enabled=true
```

The signing key and the encryption key [are both JWKs](Configuration-Properties-Common.html#signing--encryption) of size `512` and `256`. Signing & encryption 
settings for this feature are available [here](Configuration-Properties-Common.html#signing--encryption) under the configuration key `cas.authn.saml-idp.metadata.mongo`.

#### SAML Metadata Git

```properties
# cas.authn.saml-idp.metadata.git.idp-metadata-enabled=true
```

Common configuration settings for this feature are available [here](Configuration-Properties-Common.html#git-configuration) 
under the configuration key `cas.authn.saml-idp.metadata`.
 
The signing key and the encryption key [are both JWKs](Configuration-Properties-Common.html#signing--encryption) of size `512` and `256`. Signing & 
encryption settings for this feature are available [here](Configuration-Properties-Common.html#signing--encryption) under the 
configuration key `cas.authn.saml-idp.metadata.git`.

#### SAML Metadata MongoDb

Common configuration settings for this feature are available [here](Configuration-Properties-Common.html#mongodb-configuration) under the configuration key `cas.authn.saml-idp.metadata`.
 
```properties
# cas.authn.saml-idp.metadata.mongo.idp-metadata-collection=saml-idp-metadata
```
 
The signing key and the encryption key [are both JWKs](Configuration-Properties-Common.html#signing--encryption) of size `512` and `256`. Signing & 
encryption settings for this feature are available [here](Configuration-Properties-Common.html#signing--encryption) under the 
configuration key `cas.authn.saml-idp.metadata.mongo`.
 
#### SAML Metadata REST
 
RESTful settings for this feature are available [here](Configuration-Properties-Common.html#restful-integrations) 
under the configuration key `cas.authn.saml-idp.metadata.rest`.

```properties
# cas.authn.saml-idp.metadata.rest.idp-metadata-enabled=true
```

The signing key and the encryption key [are both JWKs](Configuration-Properties-Common.html#signing--encryption) of size `512` and `256`. Signing & 
encryption settings for this feature are available [here](Configuration-Properties-Common.html#signing--encryption) under the 
configuration key `cas.authn.saml-idp.metadata.rest`.

#### SAML Metadata Amazon S3
 
Common AWS settings for this feature are available [here](Configuration-Properties-Common.html#amazon-integration-settings)
under the configuration key `cas.authn.saml-idp.metadata.amazon-s3`.

The signing key and the encryption key [are both JWKs](Configuration-Properties-Common.html#signing--encryption) of size `512` and `256`. Signing & encryption 
settings for this feature are available [here](Configuration-Properties-Common.html#signing--encryption) under the configuration key `cas.authn.saml-idp.metadata.amazon-s3`.
 
```properties
# cas.authn.saml-idp.metadata.amazon-s3.bucket-name=saml-sp-bucket
# cas.authn.saml-idp.metadata.mongo.idp-metadata-bucket-name=saml-idp-bucket
```

### SAML Logout

```properties
# cas.authn.saml-idp.logout.force-signed-logout-requests=true
# cas.authn.saml-idp.logout.single-logout-callbacks-disabled=false
# cas.authn.saml-idp.logout.sign-logout-response=false
# cas.authn.saml-idp.logout.send-logout-response=true
# cas.authn.saml-idp.logout.logout-response-binding=
```

### SAML Algorithms & Security

```properties
# cas.authn.saml-idp.algs.override-signature-canonicalization-algorithm=
# cas.authn.saml-idp.algs.override-data-encryption-algorithms=
# cas.authn.saml-idp.algs.override-key-encryption-algorithms=
# cas.authn.saml-idp.algs.override-blocked-encryption-algorithms=
# cas.authn.saml-idp.algs.override-allowed-algorithms=
# cas.authn.saml-idp.algs.override-signature-reference-digest-methods=
# cas.authn.saml-idp.algs.override-signature-algorithms=
# cas.authn.saml-idp.algs.override-blocked-signature-signing-algorithms=
# cas.authn.saml-idp.algs.override-allowed-signature-signing-algorithms=
```

### SAML Response

```properties
# cas.authn.saml-idp.response.default-authentication-context-class=
# cas.authn.saml-idp.response.default-attribute-name-format=uri
# cas.authn.saml-idp.response.sign-error=false
# cas.authn.saml-idp.response.signing-credential-type=X509|BASIC
# cas.authn.saml-idp.response.attribute-name-formats=attributeName->basic|uri|unspecified|custom-format-etc,...
```

### SAML Ticket

```properties
# cas.authn.saml-idp.ticket.saml-artifacts-cache-storage-name=samlArtifactsCache
# cas.authn.saml-idp.ticket.saml-attribute-query-cache-storage-name=samlAttributeQueryCache
```

### SAML Profiles

```properties
# cas.authn.saml-idp.profile.slo.url-decode-redirect-request=false
# cas.authn.saml-idp.profile.sso.url-decode-redirect-request=false
# cas.authn.saml-idp.profile.sso-post-simple-sign.url-decode-redirect-request=false
```

## SAML SPs

Allow CAS to register and enable a number of built-in SAML service provider integrations.
To learn more about this topic, [please review this guide](../integration/Configuring-SAML-SP-Integrations.html).

<div class="alert alert-warning"><strong>Remember</strong><p>SAML2 service provider integrations listed here simply attempt to automate CAS configuration based on known and documented integration guidelines and recipes provided by the service provider owned by the vendor. These recipes can change and break CAS over time.</p></div>

Configuration settings for all SAML2 service providers are [available here](Configuration-Properties-Common.html#saml2-service-provider-integrations).

| Service Provider      | Configuration Key     | Attributes
|-----------------------|-----------------------|----------------------------------
| Gitlab                | `cas.saml-sp.gitlab`   | `last_name`,`first_name`,`name`
| Hipchat               | `cas.saml-sp.hipchat`  | `last_name`,`first_name`,`title`
| Dropbox               | `cas.saml-sp.dropbox`  | `mail`
| OpenAthens            | `cas.saml-sp.openAthens`   | `email`, `eduPersonPrincipalName`
| Egnyte                | `cas.saml-sp.egnyte`       | N/A
| EverBridge            | `cas.saml-sp.ever-bridge`   | N/A
| Simplicity            | `cas.saml-sp.simplicity`   | N/A
| App Dynamics          | `cas.saml-sp.app-dynamics`  | `User.OpenIDName`, `User.email`, `User.fullName`, `AccessControl`, `Groups-Membership`
| Yuja                  | `cas.saml-sp.yuja`         | N/A
| Simplicity            | `cas.saml-sp.simplicity`   | N/A
| New Relic             | `cas.saml-sp.new-relic`     | N/A
| Sunshine State Education & Research Computing Alliance | `cas.saml-sp.sserca` | N/A
| CherWell              | `cas.saml-sp.cherWell`         | N/A
| FAMIS                 | `cas.saml-sp.famis`            | N/A
| Bynder                | `cas.saml-sp.bynder`           | N/A
| Web Advisor           | `cas.saml-sp.webAdvisor`       | `uid`
| Adobe Creative Cloud  | `cas.saml-sp.adobe-cloud`       | `firstName`, `lastName`, `email`
| Securing The Human    | `cas.saml-sp.sans-sth`          | `firstName`, `lastName`, `scopedUserId`, `department`, `reference`, `email`
| Easy IEP              | `cas.saml-sp.easy-iep`          | `employeeId`
| Infinite Campus       | `cas.saml-sp.infinite-campus`   | `employeeId`
| Slack                 | `cas.saml-sp.slack`        | `User.Email`, `User.Username`, `first_name`, `last_name`, `employeeId`
| Zendesk               | `cas.saml-sp.zendesk`      | `organization`, `tags`, `phone`, `role`, `email`
| Gartner               | `cas.saml-sp.gartner`      | `urn:oid:2.5.4.42`, `urn:oid:2.5.4.4`, `urn:oid:0.9.2342.19200300.100.1.3`
| Arc GIS               | `cas.saml-sp.arcGIS`       | `arcNameId`, `mail`, `givenName`
| Benefit Focus         | `cas.saml-sp.benefit-focus` | `benefitFocusUniqueId`
| Office365             | `cas.saml-sp.office365`    | `IDPEmail`, `ImmutableID`
| SAManage              | `cas.saml-sp.sa-manage`     | `mail`
| Salesforce            | `cas.saml-sp.salesforce`   | `eduPersonPrincipalName`
| Workday               | `cas.saml-sp.workday`      | N/A
| Academic Works            | `cas.saml-sp.academic-works`    | `displayName`
| ZOOM                      | `cas.saml-sp.zoom`             | `mail`, `sn`, `givenName`
| Evernote                  | `cas.saml-sp.evernote`         | `email`
| Tableau                   | `cas.saml-sp.tableau`          | `username`
| Asana                     | `cas.saml-sp.asana`            | `email`
| Box                       | `cas.saml-sp.box`              | `email`, `firstName`, `lastName`
| Service Now               | `cas.saml-sp.service-now`   | `eduPersonPrincipalName`
| Net Partner               | `cas.saml-sp.net-partner`   | `studentId`
| Webex                     | `cas.saml-sp.webex`        | `firstName`, `lastName`
| InCommon                  | `cas.saml-sp.in-common`     | `eduPersonPrincipalName`
| Amazon                    | `cas.saml-sp.amazon`       | `awsRoles`, `awsRoleSessionName`
| Concur Solutions          | `cas.saml-sp.concur-solutions`  | `email`
| PollEverywhere            | `cas.saml-sp.poll-everywhere`   | `email`
| DocuSign                  | `cas.saml-sp.docuSign`   | `email`, `givenName`, `surname`, `employeeNumber`
| SafariOnline              | `cas.saml-sp.safari-online`   | `email`, `givenName`, `surname`, `employeeNumber`,`eduPersonAffiliation`
| BlackBaud                 | `cas.saml-sp.black-baud`    | `email`, `eduPersonPrincipalName`
| GiveCampus                | `cas.saml-sp.give-campus`   | `email`, `givenName`, `surname`, `displayName`
| WarpWire                  | `cas.saml-sp.warp-wire`     | `email`, `givenName`, `eduPersonPrincipalName`, `surname`, `eduPersonScopedAffiliation`, `employeeNumber`
| RocketChat                | `cas.saml-sp.rocket-chat`   | `email`, `cn`, `username`
| ArmsSoftware              | `cas.saml-sp.arms-software` | `email`, `uid`, `eduPersonPrincipalName`
| TopHat                    | `cas.saml-sp.top-hat` | `email`, `eduPersonPrincipalName`
| Academic HealthPlans      | `cas.saml-sp.academic-health-plans` | `email`, `givenName`, `surname`, `studentId`
| Confluence                | `cas.saml-sp.confluence` | `email`, `givenName`, `surname`, `uid`, `displayName`
| JIRA                      | `cas.saml-sp.jira` | `email`, `givenName`, `surname`, `uid`, `displayName`
| CrashPlan                 | `cas.saml-sp.crash-plan` | `email`, `givenName`, `surname`
| Emma                      | `cas.saml-sp.emma` | `email`, `givenName`, `surname`
| Qualtrics                 | `cas.saml-sp.qualtrics` | `email`, `givenName`, `surname`, `employeeNumber`, `eduPersonPrincipalName`
| NeoGov                    | `cas.saml-sp.neoGov` | `email`, `ImmutableID`
| Zimbra                    | `cas.saml-sp.zimbra` | `email`
| PagerDuty                 | `cas.saml-sp.pager-duty` | `email`
| CraniumCafe               | `cas.saml-sp.cranium-cafe` | `email`, `eduPersonPrincipalName`, `displayName`, `eduPersonScopedAffiliation`, `studentId`
| CCC Central               | `cas.saml-sp.cccco` | `email`, `eduPersonPrincipalName`, `displayName`, `eduPersonScopedAffiliation`, `uid`, `givenName`, `commonName`, `surname`, `eduPersonPrimaryffiliation`
                                
**Note**: For InCommon and other metadata aggregates, multiple entity ids can be specified to 
filter [the InCommon metadata](https://spaces.internet2.edu/display/InCFederation/Metadata+Aggregates). EntityIds 
can be regular expression patterns and are mapped to 
CAS' `serviceId` field in the registry. The signature location MUST BE the public key used to sign the metadata.

## OpenID Connect

Allow CAS to become an OpenID Connect provider (OP). To learn more about this topic, [please review this guide](../installation/OIDC-Authentication.html).

```properties
# cas.authn.oidc.issuer=http://localhost:8080/cas/oidc
# cas.authn.oidc.skew=5

# cas.authn.oidc.dynamic-client-registration-mode=OPEN|PROTECTED

# cas.authn.oidc.subject-types=public,pairwise
# cas.authn.oidc.scopes=openid,profile,email,address,phone,offline_access
# cas.authn.oidc.claims=sub,name,preferred_username,family_name, \
#    given_name,middle_name,given_name,profile, \
#    picture,nickname,website,zoneinfo,locale,updated_at,birthdate, \
#    email,email_verified,phone_number,phone_number_verified,address

# cas.authn.oidc.response-types-supported=code,token,id_token token
# cas.authn.oidc.introspection-supported-authentication-methods=client_secret_basic
# cas.authn.oidc.claim-types-supported=normal
# cas.authn.oidc.grant-types-supported=authorization_code,password,client_credentials,refresh_token
# cas.authn.oidc.token-endpoint-auth-methods-supported=client_secret_basic,client_secret_post,private_key_jwt,client_secret_jwt
# cas.authn.oidc.code-challenge-methods-supported=plain,S256

# cas.authn.oidc.id-token-signing-alg-values-supported=none,RS256,RS384,RS512,PS256,PS384,PS512,ES256,ES384,ES512,HS256,HS384,HS512
# cas.authn.oidc.id-token-encryption-alg-values-supported=RSA1_5,RSA-OAEP,RSA-OAEP-256,A128KW,A192KW,A256KW,\
    A128GCMKW,A192GCMKW,A256GCMKW,ECDH-ES,ECDH-ES+A128KW,ECDH-ES+A192KW,ECDH-ES+A256KW
# cas.authn.oidc.id-token-encryption-encoding-values-supported=A128CBC-HS256,A192CBC-HS384,A256CBC-HS512,A128GCM,A192GCM,A256GCM

# cas.authn.oidc.user-info-signing-alg-values-supported=none,RS256,RS384,RS512,PS256,PS384,PS512,ES256,ES384,ES512,HS256,HS384,HS512
# cas.authn.oidc.user-info-encryption-alg-values-supported=RSA1_5,RSA-OAEP,RSA-OAEP-256,A128KW,A192KW,A256KW,\
    A128GCMKW,A192GCMKW,A256GCMKW,ECDH-ES,ECDH-ES+A128KW,ECDH-ES+A192KW,ECDH-ES+A256KW
# cas.authn.oidc.user-info-encryption-encoding-values-supported=A128CBC-HS256,A192CBC-HS384,A256CBC-HS512,A128GCM,A192GCM,A256GCM
```
  
### OpenID Connect JWKS

```properties
# cas.authn.oidc.jwks.jwks-cache-in-minutes=60
# cas.authn.oidc.jwks.jwks-key-size=2048
# cas.authn.oidc.jwks.jwks-type=RSA|EC
```                         

#### File-based JWKS

Manage the JSON web keyset for OpenID Connect as a static file resource.

```properties 
# cas.authn.oidc.jwks.jwks-file=file:/etc/cas/config/keystore.jwks
```                                                               

#### REST-based JWKS

Reach out to an external REST API to ask for the JSON web keyset. The expected response code is `200`
where the response body should contain the contents of the JSON web keyset.

RESTful settings for this feature are available [here](Configuration-Properties-Common.html#restful-integrations) 
under the configuration key `cas.authn.oidc.jwks.rest`.

### OpenID Connect Scopes & Claims

```properties
# Define custom scopes and claims
# cas.authn.oidc.user-defined-scopes.scope1=cn,givenName,photos,customAttribute
# cas.authn.oidc.user-defined-scopes.scope2=cn,givenName,photos,customAttribute2

# Map fixed claims to CAS attributes
# cas.authn.oidc.claims-map.given_name=custom-given-name
# cas.authn.oidc.claims-map.preferred_username=global-user-attribute
```

### OpenID Connect WebFinger

WebFinger is a protocol specified by the Internet Engineering Task Force IETF that allows for 
discovery of information about people and things identified by a URI.[1] Information about a person 
might be discovered via an "acct:" URI, for example, which is a URI that looks like an email address.

#### WebFinger UserInfo via Groovy

```properties
# cas.authn.oidc.webfinger.userInfo.groovy.location=classpath:/webfinger.groovy
```

#### WebFinger UserInfo via REST

RESTful settings for this feature are available [here](Configuration-Properties-Common.html#restful-integrations) 
under the configuration key `cas.authn.oidc.webfinger.user-info.rest`.

### OpenID Connect Logout

The supported logout channels can be defined via the following properties:

```properties
# cas.authn.oidc.logout.backchannel-logout-supported=true
# cas.authn.oidc.logout.frontchannel-logout-supported=true
```

## Pac4j Delegated AuthN

Act as a proxy, and delegate authentication to external identity providers.
To learn more about this topic, [please review this guide](../integration/Delegate-Authentication.html).

```properties
# cas.authn.pac4j.typed-id-used=false
# cas.authn.pac4j.principal-attribute-id=
# cas.authn.pac4j.name=
# cas.authn.pac4j.order=
# cas.authn.pac4j.lazy-init=true
# cas.authn.pac4j.replicate-sessions=true
```

### REST-based Configuration

Identity providers for delegated authentication can be provided to CAS using an external REST endpoint. 

RESTful settings for this feature are 
available [here](Configuration-Properties-Common.html#restful-integrations) under the configuration key `cas.authn.pac4j.rest`.

### Default Configuration

The following external identity providers share [common blocks of settings](Configuration-Properties-Common.html#delegated-authentication-settings) 
under the listed configuration keys listed below:

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
| WindowsLive               | `cas.authn.pac4j.windows-live`
| Google                    | `cas.authn.pac4j.google`
| HiOrg-Server              | `cas.authn.pac4j.hi-org-server`

See below for other identity providers such as CAS, SAML2 and more.

### Provisioning

Provision and create established user profiles to identity stores.

#### Groovy

```properties
# cas.authn.pac4j.provisioning.groovy.location=file:/etc/cas/config/Provisioner.groovy
```

#### REST

RESTful settings for this feature are available [here](Configuration-Properties-Common.html#restful-integrations) under the configuration key `cas.authn.pac4j.provisioning.rest`.

### GitHub

In addition to the [common block of settings](Configuration-Properties-Common.html#delegated-authentication-settings), the following 
properties are additionally supported, when delegating authentication to GitHub:

```properties
# cas.authn.pac4j.github.scope=user|read:user|user:email|...
```       

The default scope is `user`, i.e. `read/write` access to the GitHub user account.

For a full list of possible scopes, please [see this link](https://developer.github.com/apps/building-oauth-apps/understanding-scopes-for-oauth-apps/). 

### Google

In addition to the [common block of settings](Configuration-Properties-Common.html#delegated-authentication-settings) , the following properties are additionally supported, when delegating authentication to Google:

```properties
# cas.authn.pac4j.google.scope=EMAIL|PROFILE|EMAIL_AND_PROFILE
```

### CAS

Delegate authentication to an external CAS server.

```properties
# cas.authn.pac4j.cas[0].login-url=
# cas.authn.pac4j.cas[0].protocol=
```

### OAuth20

Delegate authentication to an generic OAuth2 server. Common settings for this 
identity provider are available [here](Configuration-Properties-Common.html#delegated-authentication-settings) under the configuration key `cas.authn.pac4j.oauth2[0]`.

```properties
# cas.authn.pac4j.oauth2[0].auth-url=
# cas.authn.pac4j.oauth2[0].token-url=
# cas.authn.pac4j.oauth2[0].profile-url=
# cas.authn.pac4j.oauth2[0].profile-path=
# cas.authn.pac4j.oauth2[0].scope=
# cas.authn.pac4j.oauth2[0].profile-verb=GET|POST
# cas.authn.pac4j.oauth2[0].response-type=code
# cas.authn.pac4j.oauth2[0].profile-attrs.attr1=path-to-attr-in-profile
# cas.authn.pac4j.oauth2[0].custom-params.param1=value1
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

Common settings for this identity provider are 
available [here](Configuration-Properties-Common.html#delegated-authentication-openid-connect-settings) 
under the configuration key `cas.authn.pac4j.oidc[0].keycloak`.

```properties
# cas.authn.pac4j.oidc[0].keycloak.realm=
# cas.authn.pac4j.oidc[0].keycloak.base-uri=
```                                     

#### Apple Signin

Common settings for this identity provider are 
available [here](Configuration-Properties-Common.html#delegated-authentication-openid-connect-settings) 
under the configuration key `cas.authn.pac4j.oidc[0].apple`.

```properties
# cas.authn.pac4j.oidc[0].apple.private-key=
# cas.authn.pac4j.oidc[0].apple.private-key-id=
# cas.authn.pac4j.oidc[0].apple.team-id=
# cas.authn.pac4j.oidc[0].apple.timeout=PT30S
```  

#### Generic

Common settings for this identity provider are available [here](Configuration-Properties-Common.html#delegated-authentication-openid-connect-settings) 
under the configuration key `cas.authn.pac4j.oidc[0].generic`.

### SAML2

Delegate authentication to an external SAML2 IdP.

```properties
# cas.authn.pac4j.saml[0].keystore-password=
# cas.authn.pac4j.saml[0].private-key-password=
# cas.authn.pac4j.saml[0].keystore-path=
# cas.authn.pac4j.saml[0].keystore-alias=

# cas.authn.pac4j.saml[0].service-provider-entity-id=
# cas.authn.pac4j.saml[0].service-provider-metadata-path=

# cas.authn.pac4j.saml[0].certificate-name-to-append=

# cas.authn.pac4j.saml[0].maximum-authentication-lifetime=3600
# cas.authn.pac4j.saml[0].maximum-authentication-lifetime=300
# cas.authn.pac4j.saml[0].destination-binding=urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect

# cas.authn.pac4j.saml[0].identity-provider-metadata-path=

# cas.authn.pac4j.saml[0].authn-context-class-ref[0]=
# cas.authn.pac4j.saml[0].authn-context-comparison-type=
# cas.authn.pac4j.saml[0].name-id-policy-format=
# cas.authn.pac4j.saml[0].force-auth=false
# cas.authn.pac4j.saml[0].passive=false

# cas.authn.pac4j.saml[0].wants-assertions-signed=
# cas.authn.pac4j.saml[0].wants-responses-signed=
# cas.authn.pac4j.saml[0].all-signature-validation-disabled=false
# cas.authn.pac4j.saml[0].sign-service-provider-metadata=false
# cas.authn.pac4j.saml[0].principal-id-attribute=eduPersonPrincipalName
# cas.authn.pac4j.saml[0].use-name-qualifier=true
# cas.authn.pac4j.saml[0].attribute-consuming-service-index=
# cas.authn.pac4j.saml[0].assertion-consumer-service-index=-1
# cas.authn.pac4j.saml[0].provider-name=
# cas.authn.pac4j.saml[0].name-id-policy-allow-create=TRUE|FALSE|UNDEFINED


# cas.authn.pac4j.saml[0].sign-authn-request=false
# cas.authn.pac4j.saml[0].sign-service-provider-logout-request=false
# cas.authn.pac4j.saml[0].black-listed-signature-signing-algorithms[0]=
# cas.authn.pac4j.saml[0].signature-algorithms[0]=
# cas.authn.pac4j.saml[0].signature-reference-digest-methods[0]=
# cas.authn.pac4j.saml[0].signature-canonicalization-algorithm=

# cas.authn.pac4j.saml[0].requested-attributes[0].name=
# cas.authn.pac4j.saml[0].requested-attributes[0].friendly-name=
# cas.authn.pac4j.saml[0].requested-attributes[0].name-format=urn:oasis:names:tc:SAML:2.0:attrname-format:uri
# cas.authn.pac4j.saml[0].requested-attributes[0].required=false

# cas.authn.pac4j.saml[0].mapped-attributes[0].name=urn:oid:2.5.4.42
# cas.authn.pac4j.saml[0].mapped-attributes[0].mapped-as=displayName

# cas.authn.pac4j.saml[0].message-store-factory=org.pac4j.saml.store.EmptyStoreFactory
```

Examine the generated metadata after accessing the CAS login screen to ensure all 
ports and endpoints are correctly adjusted. Finally, share the CAS SP metadata with the delegated IdP and register CAS as an authorized relying party.

#### SAML2 Identity Provider Discovery

```properties
cas.authn.pac4j.saml-discovery.resource[0].location=file:/etc/cas/config/json-feed.json
```

### Facebook

Delegate authentication to Facebook. Common settings for this identity provider are available [here](Configuration-Properties-Common.html#delegated-authentication-settings) under the configuration key `cas.authn.pac4j.facebook`.

```properties
# cas.authn.pac4j.facebook.fields=
# cas.authn.pac4j.facebook.scope=
```

### HiOrg Server

Delegate authentication to HiOrg Server. Common settings for this identity provider are available [here](Configuration-Properties-Common.html#delegated-authentication-settings) under the configuration key `cas.authn.pac4j.hi-org-server`.

```properties
# cas.authn.pac4j.hi-org-server.scope=eigenedaten
```

### LinkedIn

Delegate authentication to LinkedIn. Common settings for this identity provider are available [here](Configuration-Properties-Common.html#delegated-authentication-settings) under the configuration key `cas.authn.pac4j.linkedin`.

```properties
# cas.authn.pac4j.linked-in.scope=
```

### Twitter
Delegate authentication to Twitter.  Common settings for this identity provider are available [here](Configuration-Properties-Common.html#delegated-authentication-settings) under the configuration key `cas.authn.pac4j.twitter`.

```properties
# cas.authn.pac4j.twitter.include-email=false
```

## WS Federation

Allow CAS to act as an identity provider and security token service
to support the WS-Federation protocol.

To learn more about this topic, [please review this guide](../protocol/WS-Federation-Protocol.html)

```properties
# cas.authn.wsfed-idp.idp.realm=urn:org:apereo:cas:ws:idp:realm-CAS
# cas.authn.wsfed-idp.idp.realm-name=CAS

# cas.authn.wsfed-idp.sts.signing-keystore-file=/etc/cas/config/ststrust.jks
# cas.authn.wsfed-idp.sts.signing-keystore-password=storepass
# cas.authn.wsfed-idp.sts.encryption-keystore-file=/etc/cas/config/stsencrypt.jks
# cas.authn.wsfed-idp.sts.encryption-keystore-password=storepass

# cas.authn.wsfed-idp.sts.subject-name-id-format=unspecified
# cas.authn.wsfed-idp.sts.subject-name-qualifier=http://cxf.apache.org/sts
# cas.authn.wsfed-idp.sts.encrypt-tokens=true
# cas.authn.wsfed-idp.sts.sign-tokens=true

# cas.authn.wsfed-idp.sts.conditions-accept-client-lifetime=true
# cas.authn.wsfed-idp.sts.conditions-fail-lifetime-exceedance=false
# cas.authn.wsfed-idp.sts.conditions-future-time-to-live=PT60S
# cas.authn.wsfed-idp.sts.conditions-lifetime=PT30M
# cas.authn.wsfed-idp.sts.conditions-max-lifetime=PT12H

# cas.authn.wsfed-idp.sts.realm.keystore-file=/etc/cas/config/stscasrealm.jks
# cas.authn.wsfed-idp.sts.realm.keystore-password=storepass
# cas.authn.wsfed-idp.sts.realm.keystore-alias=realmcas
# cas.authn.wsfed-idp.sts.realm.key-password=cas
# cas.authn.wsfed-idp.sts.realm.issuer=CAS
```

### Signing & Encryption

The signing and encryption keys [are both JWKs](Configuration-Properties-Common.html#signing--encryption) of size `512` and `256`. The encryption algorithm is set to `AES_128_CBC_HMAC_SHA_256`.  These come into play in order to secure authentication requests between the IdP and STS. Signing & encryption settings for this feature are available [here](Configuration-Properties-Common.html#signing--encryption) under the configuration key `cas.authn.wsfed-idp.sts`.

## OAuth2

Allows CAS to act as an OAuth2 provider. Here you can control how long various tokens issued by CAS should last, etc.

Signing & encryption settings for this feature are available [here](Configuration-Properties-Common.html#signing--encryption) 
under the configuration key `cas.authn.oauth`.

To learn more about this topic, [please review this guide](../installation/OAuth-OpenId-Authentication.html).

```properties
# cas.authn.oauth.replicate-sessions=false 
# cas.authn.oauth.grants.resource-owner.require-service-header=true
# cas.authn.oauth.user-profile-view-type=NESTED|FLAT
```

### Refresh Tokens

```properties
# cas.authn.oauth.refresh-token.time-to-kill-in-seconds=2592000
```

### Codes

```properties
# cas.authn.oauth.code.time-to-kill-in-seconds=30
# cas.authn.oauth.code.number-of-uses=1
```

### Access Tokens

```properties
# cas.authn.oauth.access-token.time-to-kill-in-seconds=7200
# cas.authn.oauth.access-token.max-time-to-live-in-seconds=28800
```

### Device Tokens
 
```
# cas.authn.oauth.device-token.time-to-kill-in-seconds=2592000
# cas.authn.oauth.device-token.refresh-interval=PT15S 
```

### Device User Codes

```
# cas.authn.oauth.device-user-code.time-to-kill-in-seconds=2592000
# cas.authn.oauth.device-user-code.user-code-length=8
```

### OAuth2 JWT Access Tokens

```properties
# cas.authn.oauth.access-token.create-as-jwt=false
# cas.authn.oauth.access-token.crypto.encryption-enabled=true
# cas.authn.oauth.access-token.crypto.signing-enabled=true
```

The signing key and the encryption key [are both JWKs](Configuration-Properties-Common.html#signing--encryption) of size `512` and `256`.
Signing & encryption settings for this feature are available [here](Configuration-Properties-Common.html#signing--encryption) 
under the configuration key `cas.authn.oauth.access-token`.

### OAuth2 UMA

To learn more about this topic, [please review this guide](../installation/OAuth-OpenId-Authentication.html).

```properties
# cas.authn.uma.issuer=http://localhost:8080/cas

# cas.authn.uma.requesting-party-token.max-time-to-live-in-seconds=PT3M
# cas.authn.uma.requesting-party-token.jwks-file=file:/etc/cas/config/uma-keystore.jwks

# cas.authn.uma.permission-ticket.max-time-to-live-in-seconds=PT3M
```

#### OAuth2 UMA JPA

Database settings for this feature are available [here](Configuration-Properties-Common.html#database-settings) under the configuration key `cas.authn.uma.resource-set.jpa`.

## Localization

To learn more about this topic, [please review this guide](../ux/User-Interface-Customization-Localization.html).

```properties
# cas.locale.param-name=locale
# cas.locale.default-value=en
```

If the user changes the language, a special cookie is created by CAS to contain the selected language. Cookie 
settings for this feature are available [here](Configuration-Properties-Common.html#cookie-properties) under the configuration key `cas.locale.cookie`.

## Global SSO Behavior

To learn more about this topic, [please review this guide](../installation/Configuring-SSO.html).

```properties
# cas.sso.allow-missing-service-parameter=true
# cas.sso.create-sso-cookie-on-renew-authn=true
# cas.sso.proxy-authn-enabled=true
# cas.sso.renew-authn-enabled=true
# cas.sso.sso-enabled=true
# cas.sso.required-service-pattern=
```

## Warning Cookie

Created by CAS if and when users are to be warned when accessing CAS protected services. Cookie settings for this feature are available [here](Configuration-Properties-Common.html#cookie-properties) under the configuration key `cas.warning-cookie`.

```properties
# cas.warningCookie.auto-configure-cookie-path=true
```

## Ticket Granting Cookie

Cookie settings for this feature are available [here](Configuration-Properties-Common.html#cookie-properties) under the configuration key `cas.tgc`.

```properties
# cas.tgc.pin-to-session=true
# cas.tgc.remember-me-max-age=P14D
# cas.tgc.auto-configure-cookie-path=true
```

### Signing & Encryption

The signing and encryption keys [are both JWKs](Configuration-Properties-Common.html#signing--encryption) of size `512` and `256`.
The encryption algorithm is set to `AES_128_CBC_HMAC_SHA_256`.
Signing & encryption settings for this feature are available [here](Configuration-Properties-Common.html#signing--encryption) under the configuration key `cas.tgc`.

## Logout

Control various settings related to CAS logout functionality. To learn more about this topic, [please review this guide](../installation/Logout-Single-Signout.html).

```properties
# cas.logout.follow-service-redirects=false
# cas.logout.redirect-parameter=service
# cas.logout.redirect-url=https://www.github.com
# cas.logout.confirm-logout=false
# cas.logout.remove-descendant-tickets=false
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
# cas.clearpass.cache-credential=false
```

The signing and encryption keys [are both JWKs](Configuration-Properties-Common.html#signing--encryption) of size `512` and `256`. The encryption algorithm is set to `AES_128_CBC_HMAC_SHA_256`. Signing & encryption settings for this feature are available [here](Configuration-Properties-Common.html#signing--encryption) under the configuration key `cas.clearpass`.

## Message Bundles

To learn more about this topic, [please review this guide](../ux/User-Interface-Customization-Localization.html).
The baseNames are message bundle base names representing files that either end in .properties or _xx.properties where xx is a country locale code. The commonNames are not actually message bundles but they are properties files that are merged together and contain keys that are only used if they are not found in the message bundles. Keys from the later files in the list will be preferred over keys from the earlier files.

```properties
# cas.message-bundle.encoding=UTF-8
# cas.message-bundle.fallback-system-locale=false
# cas.message-bundle.cache-seconds=180
# cas.message-bundle.use-code-message=true
# cas.message-bundle.base-names=classpath:custom_messages,classpath:messages
# cas.message-bundle.common-names=classpath:/common_messages.properties,file:/etc/cas/config/common_messages.properties
```

## Audits

Control how audit messages are formatted.
To learn more about this topic, [please review this guide](../installation/Audits.html).

```properties 
# cas.audit.enabled=true
# cas.audit.ignore-audit-failures=false
# cas.audit.app-code=CAS
# cas.audit.number-of-days-in-history=30
# cas.audit.include-validation-assertion=false
# cas.audit.alternate-server-addr-header-name=
# cas.audit.alternate-client-addr-header-name=X-Forwarded-For
# cas.audit.use-server-host-address=false  

# cas.audit.supported-actions=*
# cas.audit.excluded-actions=
```

### Slf4j Audits

Route audit logs to the Slf4j logging system which might in turn store audit logs in a file or any other
destination that the logging system supports.

The logger name is fixed at `org.apereo.inspektr.audit.support`.

```xml
<Logger name="org.apereo.inspektr.audit.support" level="info">
    <!-- Route the audit data to any number of appenders supported by the logging framework. -->
</Logger>
```

<div class="alert alert-info"><strong></strong><p>Audit records routed to the Slf4j log are not
able to read the audit data back given the abstraction layer between CAS, the logging system
and any number of log appenders that might push data to a variety of systems.</p></div>

```properties
# cas.audit.slf4j.audit-format=DEFAULT|JSON
# cas.audit.slf4j.singleline-separator=|
# cas.audit.slf4j.use-single-line=false
# cas.audit.slf4j.enabled=true
```

### MongoDb Audits

Store audit logs inside a MongoDb database.

Common configuration settings for this feature are available 
[here](Configuration-Properties-Common.html#mongodb-configuration) under the configuration key `cas.audit`.

```properties
# cas.audit.mongo.asynchronous=true
```

### Redis Audits

Store audit logs inside a Redis database.

Common configuration settings for this feature are available 
[here](Configuration-Properties-Common.html#redis-configuration) under the configuration key `cas.audit`.

```properties
# cas.audit.redis.asynchronous=true
```

### CouchDb Audits

Store audit logs inside a CouchDb database.

Common configuration settings for this feature are available [here](Configuration-Properties-Common.html#couchdb-configuration)
under the configuration key `cas.audit`.

### Couchbase Audits

Store audit logs inside a Couchbase database.

Database settings for this feature are available [here](Configuration-Properties-Common.html#couchbase-integration-settings) 
under the configuration key `cas.audit.couchbase`.

```properties
# cas.audit.couchbase.asynchronous=true
```

### DynamoDb Audits

Store audit logs inside a DynamoDb database.

Common configuration settings for this feature are available [here](Configuration-Properties-Common.html#dynamodb-configuration)
under the configuration key `cas.audit`.

AWS settings for this feature are available [here](Configuration-Properties-Common.html#amazon-integration-settings) 
under the configuration key `cas.audit.dynamo-db`.

```properties
# cas.audit.dynamo-db.asynchronous=true
```

### Database Audits

Store audit logs inside a database. Database settings for this feature are available [here](Configuration-Properties-Common.html#database-settings)
under the configuration key `cas.audit.jdbc`.

```properties
# cas.audit.jdbc.asynchronous=true
# cas.audit.jdbc.max-age-days=180
# cas.audit.jdbc.column-length=100
# cas.audit.jdbc.select-sql-query-template=
# cas.audit.jdbc.date-formatter-pattern=
```

Scheduler settings for this feature are available [here](Configuration-Properties-Common.html#job-scheduling) under the configuration key `cas.audit.jdbc`.

### REST Audits

Store audit logs inside a database. RESTful settings for this feature are 
available [here](Configuration-Properties-Common.html#restful-integrations) under the configuration key `cas.audit.rest`.

```properties
# cas.audit.rest.asynchronous=true
```

## Sleuth Distributed Tracing

To learn more about this topic, [please review this guide](../monitoring/Monitoring-Statistics.html#distributed-tracing).

```properties
# spring.sleuth.sampler.percentage = 0.5
# spring.sleuth.enabled=true

# spring.zipkin.enabled=true
# spring.zipkin.base-url=http://localhost:9411/
```

## Monitoring

To learn more about this topic, [please review this guide](../monitoring/Monitoring-Statistics.html).

### Ticket Granting Tickets

Decide how CAS should monitor the generation of TGTs.

```properties
# cas.monitor.tgt.warn.threshold=10
# cas.monitor.tgt.warn.eviction-threshold=0
```

### Service Tickets

Decide how CAS should monitor the generation of STs.

```properties
# cas.monitor.st.warn.threshold=10
# cas.monitor.st.warn.evictionThreshold=0
```
### Load 

Decide how CAS should monitor system load of a CAS Server.  

```properties
# cas.monitor.load.warn.threshold=25
```

### Cache Monitors

Decide how CAS should monitor the internal state of various cache storage services.

```properties
# cas.monitor.warn.threshold=10
# cas.monitor.warn.eviction-threshold=0
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
# cas.monitor.jdbc.validation-query=SELECT 1
# cas.monitor.jdbc.max-wait=5000
```

### LDAP Server Monitoring

Decide how CAS should monitor the LDAP server it uses for authentication, etc.  
LDAP settings for this feature are 
available [here](Configuration-Properties-Common.html#ldap-connection-settings) under the configuration key `cas.monitor.ldap[0]`.
The default for the pool size is zero to prevent failed ldap pool initialization to impact server startup.

The following properties are specific to the ldap monitor and configure the thread pool 
that will ping on the LDAP monitor connection pool.

```properties
# cas.monitor.ldap[0].max-wait=5000
# cas.monitor.ldap[0].pool.min-size=0
# cas.monitor.ldap[0].pool.max-size=18
# cas.monitor.ldap[0].pool.enabled=true
```

### Memory

Decide how CAS should monitor the internal state of JVM memory available at runtime.

```properties
# cas.monitor.free-mem-threshold=10
```

## Themes

To learn more about this topic, [please review this guide](../ux/User-Interface-Customization-Themes.html).

```properties
# cas.theme.param-name=theme
# cas.theme.default-theme-name=cas-theme-default
```

## Events

Decide how CAS should track authentication events.
To learn more about this topic, [please review this guide](../installation/Configuring-Authentication-Events.html).

```properties
cas.events.enabled=true

# Whether geolocation tracking should be turned on and requested from the browser.
# cas.events.track-geolocation=false

# Control whether CAS should monitor configuration files and auto-refresh context.
# cas.events.track-configuration-modifications=true
```

### InfluxDb Events

Decide how CAS should store authentication events inside an InfluxDb instance. Common 
configuration settings for this feature are available [here](Configuration-Properties-Common.html#influxdb-configuration) under the configuration key `cas.events.influx-db`.

### CouchDb Events

Decide how CAS should store authentication events inside a CouchDb instance. Common
configuration settings for this feature are available [here](Configuration-Properties-Common.html#couchdb-configuration) under the configuration key `cas.events.couch-db`.

### Database Events

Decide how CAS should store authentication events inside a database instance. Database 
settings for this feature are available [here](Configuration-Properties-Common.html#database-settings) under the configuration key `cas.events.jpa`.

### MongoDb Events

Decide how CAS should store authentication events inside a MongoDb instance. Common 
configuration settings for this feature are available [here](Configuration-Properties-Common.html#mongodb-configuration) under the configuration key `cas.events`.

### DynamoDb Events

Decide how CAS should store authentication events inside a DynamoDb instance.

Common configuration settings for this feature are available [here](Configuration-Properties-Common.html#dynamodb-configuration)
under the configuration key `cas.events`.

AWS settings for this feature are available [here](Configuration-Properties-Common.html#amazon-integration-settings) 
under the configuration key `cas.evnts.dynamo-db`.

```properties
# cas.events.dynamo-db.table-name=DynamoDbCasEvents
```

## Http Web Requests

Control how CAS should respond and validate incoming HTTP requests.

```properties
# cas.http-web-request.header.enabled=true

# cas.http-web-request.header.xframe=true
# cas.http-web-request.header.xframe-options=DENY

# cas.http-web-request.header.xss=true
# cas.http-web-request.header.xss-options=1; mode=block

# cas.http-web-request.header.hsts=true
# cas.http-web-request.header.xcontent=true
# cas.http-web-request.header.cache=true
# cas.http-web-request.header.content-security-policy=

# cas.http-web-request.cors.enabled=false
# cas.http-web-request.cors.allow-credentials=false
# cas.http-web-request.cors.allow-origins[0]=
# cas.http-web-request.cors.allow-methods[0]=*
# cas.http-web-request.cors.allow-headers[0]=*
# cas.http-web-request.cors.max-age=3600
# cas.http-web-request.cors.exposed-headers[0]=

# cas.http-web-request.web.force-encoding=true
# cas.http-web-request.web.encoding=UTF-8

# cas.http-web-request.allow-multi-value-parameters=false
# cas.http-web-request.only-post-params=username,password
# cas.http-web-request.params-to-check=ticket,service,renew,gateway,warn,method,target,SAMLart,pgtUrl,pgt,pgtId,pgtIou,targetService,entityId,token
# cas.http-web-request.pattern-to-block=
# cas.http-web-request.characters-to-forbid=none

# cas.http-web-request.custom-headers.header-name1=headerValue1
# cas.http-web-request.custom-headers.header-name2=headerValue2

# server.servlet.encoding.charset=UTF-8
# server.servlet.encoding.enabled=true
# server.servlet.encoding.force=true
```

## Http Client

Control how CAS should attempt to contact resources on the web
via its own Http Client. This is most commonly used when responding
to ticket validation events and/or single logout.

In the event that local certificates are to be imported into the CAS running environment,
a local truststore is provided by CAS to improve portability of configuration across environments.

```properties
# cas.http-client.connection-timeout=5000
# cas.http-client.async-timeout=5000
# cas.http-client.read-timeout=5000 

# cas.http-client.proxy-host=
# cas.http-client.proxy-port=0 

# cas.http-client.host-name-verifier=NONE|DEFAULT
# cas.http-client.allow-local-logout-urls=false
# cas.http-client.authority-validation-reg-ex=
# cas.http-client.authority-validation-reg-ex-case-sensitive=true
# cas.http-client.default-headers=

# cas.http-client.truststore.psw=changeit
# cas.http-client.truststore.file=classpath:/truststore.jks
# cas.http-client.truststore.type=
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
# cas.service-registry.watcher-enabled=true

# Auto-initialize the registry from default JSON service definitions
# cas.service-registry.init-from-json=false

# cas.service-registry.management-type=DEFAULT|DOMAIN
# cas.service-registry.cache=PT5M
# cas.service-registry.cache-size=1000
# cas.service-registry.cache-capacity=1000
```

Scheduler settings for this feature are available [here](Configuration-Properties-Common.html#job-scheduling) under
 the configuration key `cas.service-registry`.

### Service Registry Notifications

Email notifications settings for this feature are available [here](Configuration-Properties-Common.html#email-notifications) 
under the configuration key `cas.service-registry`. SMS notifications settings for this feature are 
available [here](Configuration-Properties-Common.html#sms-notifications) under the configuration key `cas.service-registry`.

### JSON Service Registry

If the underlying service registry is using local system resources
to locate JSON service definitions, decide how those resources should be found.

```properties
# cas.service-registry.json.location=classpath:/services
```

To learn more about this topic, [please review this guide](../services/JSON-Service-Management.html).

### YAML Service Registry

If the underlying service registry is using local system resources
to locate YAML service definitions, decide how those resources should be found.

```properties
# cas.service-registry.yaml.location=classpath:/services
```

To learn more about this topic, [please review this guide](../services/YAML-Service-Management.html).

### Git Service Registry

Works with git repository to fetch and manage service registry definitions.

Common configuration settings for this feature are available [here](Configuration-Properties-Common.html#git-configuration) 
under the configuration key `cas.service-registry`.

```properties
# cas.service-registry.git.group-by-type=true
# cas.service-registry.git.root-directory=
```

To learn more about this topic, [please review this guide](../services/Git-Service-Management.html).

### RESTful Service Registry

To learn more about this topic, [please review this guide](../services/REST-Service-Management.html).

```properties
# cas.service-registry.rest.url=https://example.api.org
# cas.service-registry.rest.basic-auth-username=
# cas.service-registry.rest.basic-auth-password=
```

### CouchDb Service Registry

To learn more about this topic, [please review this guide](../services/CouchDb-Service-Management.html). Common configuration settings for this feature are available [here](Configuration-Properties-Common.html#couchdb-configuration) under the configuration key `cas.service-registry`.

### Redis Service Registry

To learn more about this topic, [please review this guide](../services/Redis-Service-Management.html). Common configuration settings for this feature are available [here](Configuration-Properties-Common.html#redis-configuration) under the configuration key `cas.service-registry`.

### CosmosDb Service Registry

To learn more about this topic, [please review this guide](../services/CosmosDb-Service-Management.html).

```properties
# cas.service-registry.cosmos-db.uri=
# cas.service-registry.cosmos-db.key=
# cas.service-registry.cosmos-db.database=
# cas.service-registry.cosmos-db.collection=
# cas.service-registry.cosmos-db.throughput=10000
# cas.service-registry.cosmos-db.drop-collection=true
# cas.service-registry.cosmos-db.consistency-level=Session
```

### Amazon S3 Service Registry

To learn more about this topic, [please review this guide](../services/AmazonS3-Service-Management.html).

AWS settings for this feature are available [here](Configuration-Properties-Common.html#amazon-integration-settings) 
under the configuration key `cas.service-registry.amazon-s3`.

### DynamoDb Service Registry

To learn more about this topic, [please review this guide](../services/DynamoDb-Service-Management.html).
Common configuration settings for this feature are available [here](Configuration-Properties-Common.html#dynamodb-configuration)
under the configuration key `cas.service-registry`.
AWS settings for this feature are available [here](Configuration-Properties-Common.html#amazon-integration-settings) 
under the configuration key `cas.service-registry.dynamo-db`.

```properties
# cas.service-registry.dynamo-db.table-name=DynamoDbCasServices
```

### Cassandra Service Registry

To learn more about this topic, [please review this guide](../services/Cassandra-Service-Management.html).

Common Cassandra settings for this feature are available [here](Configuration-Properties-Common.html#cassandra-configuration) under the configuration key `cas.service-registry.cassandra`.

### MongoDb Service Registry

Store CAS service definitions inside a MongoDb instance. To learn more about this topic, [please review this guide](../services/MongoDb-Service-Management.html).
 Common configuration settings for this feature are available [here](Configuration-Properties-Common.html#mongodb-configuration) under the configuration key `cas.service-registry`.

### LDAP Service Registry

Control how CAS services should be found inside an LDAP instance.
To learn more about this topic, [please review this guide](../services/LDAP-Service-Management.html).  LDAP settings for this feature are available [here](Configuration-Properties-Common.html#ldap-connection-settings) under the configuration key `cas.service-registry.ldap`.

```properties
# cas.service-registry.ldap.service-definition-attribute=description
# cas.service-registry.ldap.id-attribute=uid
# cas.service-registry.ldap.object-class=casRegisteredService
# cas.service-registry.ldap.search-filter=(%s={0})
# cas.service-registry.ldap.load-filter=(objectClass=%s)
```

### Couchbase Service Registry

Control how CAS services should be found inside a Couchbase instance.
To learn more about this topic, [please review this guide](../services/Couchbase-Service-Management.html). 
Database settings for this feature are available [here](Configuration-Properties-Common.html#couchbase-integration-settings) 
under the configuration key `cas.service-registry.couchbase`.

### Database Service Registry

Control how CAS services should be found inside a database instance.
To learn more about this topic, [please review this guide](../services/JPA-Service-Management.html). 
Database settings for this feature are available [here](Configuration-Properties-Common.html#database-settings) 
under the configuration key `cas.service-registry.jpa`.

### Cache Service Registry

Services cache duration specifies the fixed duration for an entry to be automatically removed from the cache after its creation or update.

### Cache Size Service Registry

Services cache size specifies the maximum number of entries the cache may contain.

### Cach Capacity Service Registry

Services cache capacity sets the minimum total size for the internal data structures.

## Service Registry Replication

Control how CAS services definition files should be replicated across a CAS cluster.
To learn more about this topic, [please review this guide](../services/Configuring-Service-Replication.html)

Replication modes may be configured per the following options:

| Type                    | Description
|-------------------------|--------------------------------------------------------------
| `ACTIVE_ACTIVE`       | All CAS nodes sync copies of definitions and keep them locally.
| `ACTIVE_PASSIVE`    | Default. One master node keeps definitions and streams changes to other passive nodes.

```properties
# cas.service-registry.stream.enabled=true
# cas.service-registry.stream.replication-mode=ACTIVE_ACTIVE|ACTIVE_PASSIVE
```

## Service Registry Replication Hazelcast

Control how CAS services definition files should be replicated across a CAS cluster backed by a distributed Hazelcast cache.
To learn more about this topic, [please review this guide](../services/Configuring-Service-Replication.html).

Hazelcast settings for this feature are available [here](Configuration-Properties-Common.html#hazelcast-configuration) under 
the configuration key `cas.service-registry.stream.hazelcast.config`.

```properties
# cas.service-registry.stream.hazelcast.duration=PT1M
```

## Service Registry Replication Kafka

Control how CAS services definition files should be replicated across a CAS cluster backed by Apache Kafka.
To learn more about this topic, [please review this guide](../services/Configuring-Service-Replication.html).

Kafka common settings for this feature are available [here](Configuration-Properties-Common.html#apache-kafka-configuration) under 
the configuration key `cas.service-registry.stream.kafka`. Kafka topic settings for this feature are 
available [here](Configuration-Properties-Common.html#apache-kafka-configuration)
under the configuration key `cas.service-registry.stream.kafka.topic`.

## Ticket Registry

To learn more about this topic, [please review this guide](../ticketing/Configuring-Ticketing-Components.html).

### Signing & Encryption

The encryption key must be randomly-generated string of size `16`. The signing key [is a JWK](Configuration-Properties-Common.html#signing--encryption) of size `512`.

### Cleaner

A cleaner process is scheduled to run in the background to clean up expired and stale tickets.
This section controls how that process should behave. Scheduler settings for this feature are 
available [here](Configuration-Properties-Common.html#job-scheduling) under the configuration key `cas.ticket.registry.cleaner`.

### JPA Ticket Registry

To learn more about this topic, [please review this guide](../ticketing/JPA-Ticket-Registry.html). Database settings for this feature are available [here](Configuration-Properties-Common.html#database-settings) under the configuration key `cas.ticket.registry.jpa`.

```properties
# cas.ticket.registry.jpa.ticket-lock-type=NONE
# cas.ticket.registry.jpa.jpa-locking-timeout=3600
```

Signing & encryption settings for this registry are available [here](Configuration-Properties-Common.html#signing--encryption) under the configuration key `cas.ticket.registry.jpa`.

### CouchDb Ticket Registry

To learn more about this topic, [please review this guide](../ticketing/CouchDb-Ticket-Registry.html). Database settings for this feature are available [here](Configuration-Properties-Common.html#couchdb-configuration) under the configuration key `cas.ticket.registry.couch-db`.

### Couchbase Ticket Registry

To learn more about this topic, [please review this guide](../ticketing/Couchbase-Ticket-Registry.html). Database settings for this feature are available [here](Configuration-Properties-Common.html#couchbase-integration-settings) under the configuration key `cas.ticket.registry.couchbase`.

Signing & encryption settings for this registry are available [here](Configuration-Properties-Common.html#signing--encryption) under the configuration key `cas.ticket.registry.couchbase`.

### Hazelcast Ticket Registry

To learn more about this topic, [please review this guide](../ticketing/Hazelcast-Ticket-Registry.html).

Common Hazelcast settings for this feature are available [here](Configuration-Properties-Common.html#hazelcast-configuration) under the configuration key `cas.ticket.registry.hazelcast`.

```properties
# cas.ticket.registry.hazelcast.page-size=500
```

Signing & encryption settings for this registry are available [here](Configuration-Properties-Common.html#signing--encryption) under the configuration key `cas.ticket.registry.hazelcast`.

### Cassandra Ticket Registry

To learn more about this topic, [please review this guide](../ticketing/Cassandra-Ticket-Registry.html).

Common Cassandra settings for this feature are available [here](Configuration-Properties-Common.html#cassandra-configuration) under the configuration key `cas.ticket.registry.cassandra`.

Signing & encryption settings for this registry are available [here](Configuration-Properties-Common.html#signing--encryption) under the configuration key `cas.ticket.registry.cassandra`.

```properties
# cas.ticket.registry.cassandra.drop-tables-on-startup=false
```

### Infinispan Ticket Registry

To learn more about this topic, [please review this guide](../ticketing/Infinispan-Ticket-Registry.html).

```properties
# cas.ticket.registry.infinispan.cache-name=
# cas.ticket.registry.infinispan.config-location=/infinispan.xml
```

Signing & encryption settings for this registry are available [here](Configuration-Properties-Common.html#signing--encryption) under the configuration key `cas.ticket.registry.infinispan`.

### InMemory Ticket Registry

This is typically the default ticket registry instance where tickets
are kept inside the runtime environment memory.

```properties
# Enable the backing map to be cacheable
# cas.ticket.registry.in-memory.cache=true

# cas.ticket.registry.in-memory.load-factor=1
# cas.ticket.registry.in-memory.concurrency=20
# cas.ticket.registry.in-memory.initial-capacity=1000
```

Signing & encryption settings for this registry are available [here](Configuration-Properties-Common.html#signing--encryption) under the configuration key `cas.ticket.registry.in-memory`.

### JMS Ticket Registry

To learn more about this topic, [please review this guide](../ticketing/Messaging-JMS-Ticket-Registry.html).

Signing & encryption settings for this registry are available [here](Configuration-Properties-Common.html#signing--encryption)
under the configuration key `cas.ticket.registry.jms`.

```properties
# cas.ticket.registry.jms.id=
```

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

Theses properties are for the module that uses version 2.x of the Ehcache library. 
To learn more about this topic, [please review this guide](../ticketing/Ehcache-Ticket-Registry.html).

```properties
# cas.ticket.registry.ehcache.replicate-updates-via-copy=true
# cas.ticket.registry.ehcache.cache-manager-name=ticketRegistryCacheManager
# cas.ticket.registry.ehcache.replicate-puts=true
# cas.ticket.registry.ehcache.replicate-updates=true
# cas.ticket.registry.ehcache.memory-store-eviction-policy=LRU
# cas.ticket.registry.ehcache.config-location=classpath:/ehcache-replicated.xml
# cas.ticket.registry.ehcache.maximum-batch-size=100
# cas.ticket.registry.ehcache.shared=false
# cas.ticket.registry.ehcache.replication-interval=10000
# cas.ticket.registry.ehcache.cache-time-to-live=2147483647
# cas.ticket.registry.ehcache.disk-expiry-thread-interval-seconds=0
# cas.ticket.registry.ehcache.replicate-removals=true
# cas.ticket.registry.ehcache.max-chunk-size=5000000
# cas.ticket.registry.ehcache.max-elements-on-disk=0
# cas.ticket.registry.ehcache.max-elements-in-cache=0
# cas.ticket.registry.ehcache.max-elements-in-memory=10000
# cas.ticket.registry.ehcache.eternal=false
# cas.ticket.registry.ehcache.loader-async=true
# cas.ticket.registry.ehcache.replicate-puts-via-copy=true
# cas.ticket.registry.ehcache.cache-time-to-idle=0
# cas.ticket.registry.ehcache.persistence=LOCALTEMPSWAP|NONE|LOCALRESTARTABLE|DISTRIBUTED
# cas.ticket.registry.ehcache.synchronous-writes=

# The systemprops allows a map of properties to be set as system properties before configLocation config is processed.
# These properties may be referenced in the ehcache XML config via ${key}
# cas.ticket.registry.ehcache.systemprops.key1=value1
# cas.ticket.registry.ehcache.systemprops.key2=value2
```

Signing & encryption settings for this registry are available [here](Configuration-Properties-Common.html#signing--encryption) under the configuration key `cas.ticket.registry.ehcache`.

### Ehcache 3 Ticket Registry

To learn more about this topic, [please review this guide](../ticketing/Ehcache-Ticket-Registry.html).

```properties
# cas.ticket.registry.ehcache3.enabled=true
# cas.ticket.registry.ehcache3.max-elements-in-memory=10000
# cas.ticket.registry.ehcache3.per-cache-size-on-disk=20MB
# cas.ticket.registry.ehcache3.eternal=false
# cas.ticket.registry.ehcache3.enable-statistics=true
# cas.ticket.registry.ehcache3.enable-management=true
# cas.ticket.registry.ehcache3.terracotta-cluster-uri=
# cas.ticket.registry.ehcache3.default-server-resource=main
# cas.ticket.registry.ehcache3.resource-pool-name=cas-ticket-pool
# cas.ticket.registry.ehcache3.resource-pool-size=15MB
# cas.ticket.registry.ehcache3.root-directory=/tmp/cas/ehcache3
# cas.ticket.registry.ehcache3.persist-on-disk=true
# cas.ticket.registry.ehcache3.cluster-connection-timeout=150
# cas.ticket.registry.ehcache3.cluster-read-write-timeout=5
# cas.ticket.registry.ehcache3.clustered-cache-consistency=STRONG
```                                              

There is no default value for the Terracota Cluster URI but the format is `terracotta://host1.company.org:9410,host2.company.org:9410/cas-application`

Signing & encryption settings for this registry are available [here](Configuration-Properties-Common.html#signing--encryption) under the configuration key `cas.ticket.registry.ehcache3`.

### Ignite Ticket Registry

To learn more about this topic, [please review this guide](../ticketing/Ignite-Ticket-Registry.html).

```properties
# cas.ticket.registry.ignite.key-algorithm=
# cas.ticket.registry.ignite.protocol=
# cas.ticket.registry.ignite.trust-store-password=
# cas.ticket.registry.ignite.key-store-type=
# cas.ticket.registry.ignite.key-store-file-path=
# cas.ticket.registry.ignite.key-store-password=
# cas.ticket.registry.ignite.trust-store-type=
# cas.ticket.registry.ignite.ignite-address[0]=localhost:47500
# cas.ticket.registry.ignite.ignite-address[1]=
# cas.ticket.registry.ignite.trust-store-file-path=
# cas.ticket.registry.ignite.ack-timeout=2000
# cas.ticket.registry.ignite.join-timeout=1000
# cas.ticket.registry.ignite.local-address=
# cas.ticket.registry.ignite.local-port=-1
# cas.ticket.registry.ignite.network-timeout=5000
# cas.ticket.registry.ignite.socket-timeout=5000
# cas.ticket.registry.ignite.thread-priority=10
# cas.ticket.registry.ignite.force-server-mode=false
# cas.ticket.registry.ignite.client-mode=false

# cas.ticket.registry.ignite.tickets-cache.write-synchronization-mode=FULL_SYNC
# cas.ticket.registry.ignite.tickets-cache.atomicity-mode=TRANSACTIONAL
# cas.ticket.registry.ignite.tickets-cache.cache-mode=REPLICATED
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
under the configuration key `cas.ticket.registry.dynamo-db`.

AWS settings for this feature are available [here](Configuration-Properties-Common.html#amazon-integration-settings) 
under the configuration key `cas.ticket.registry.dynamo-db`.

```properties
# cas.ticket.registry.dynamo-db.service-tickets-table-name=serviceTicketsTable
# cas.ticket.registry.dynamo-db.proxy-tickets-table-name=proxyTicketsTable
# cas.ticket.registry.dynamo-db.ticket-granting-tickets-table-name=ticketGrantingTicketsTable
# cas.ticket.registry.dynamo-db.proxy-granting-tickets-table-name=proxyGrantingTicketsTable
# cas.ticket.registry.dynamo-db.transient-session-tickets-table-name=transientSessionTicketsTable
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
# cas.ticket.st.max-length=20

# cas.ticket.st.number-of-uses=1
# cas.ticket.st.time-to-kill-in-seconds=10
```

## Proxy Granting Tickets Behavior

```properties
# cas.ticket.pgt.max-length=50
```

## Proxy Tickets Behavior

```properties
# cas.ticket.pt.time-to-kill-in-seconds=10
# cas.ticket.pt.number-of-uses=1
```


## Transient Session Tickets Behavior

```properties
# cas.ticket.tst.time-to-kill-in-seconds=300
```

## Ticket Granting Tickets Behavior

```properties
# cas.ticket.tgt.only-track-most-recent-session=true
# cas.ticket.tgt.max-length=50
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
# cas.ticket.tgt.max-time-to-live-in-seconds=28800
# cas.ticket.tgt.time-to-kill-in-seconds=7200
```

### Remember Me

```properties
# cas.ticket.tgt.remember-me.enabled=true
# cas.ticket.tgt.remember-me.time-to-kill-in-seconds=28800
```

### Timeout

The expiration policy applied to TGTs provides for most-recently-used expiration policy, similar to a Web server session timeout.

```properties
# cas.ticket.tgt.timeout.max-time-to-live-in-seconds=28800
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
# cas.ticket.tgt.hard-timeout.time-to-kill-in-seconds=28800
```

## Google reCAPTCHA Integration

Display Google's reCAPTCHA widget on the CAS login page.

```properties
# cas.google-recaptcha.enabled=true
# cas.google-recaptcha.verify-url=https://www.google.com/recaptcha/api/siteverify
# cas.google-recaptcha.site-key=
# cas.google-recaptcha.secret=
# cas.google-recaptcha.invisible=
# cas.google-recaptcha.position=bottomright
```

## Google Analytics

To learn more about this topic, [please review this guide](../integration/Configuring-Google-Analytics.html).

```properties
# cas.googleAnalytics.googleAnalyticsTrackingId=
```

### Google Analytics Cookie

The common cookie settings applicable to this feature are [available here](Configuration-Properties-Common.html#cookie-properties) 
under the configuration key `cas.google-analytics.cookie`.

```properties
cas.google-analytics.cookie.attribute-name=
cas.google-analytics.cookie.attribute-value-pattern=.+
```

## Spring Webflow

Control how Spring Webflow's conversational session state should be managed by CAS,
and all other webflow related settings.

To learn more about this topic, [please review this guide](../webflow/Webflow-Customization.html).

```properties
# cas.webflow.always-pause-redirect=false
# cas.webflow.refresh=true
# cas.webflow.redirect-same-state=false
# cas.webflow.autoconfigure=true
# cas.webflow.base-path=
```

### Spring Webflow Login Decorations

#### Groovy

```properties
# cas.webflow.login-decorator.groovy.location=file:/etc/cas/config/LoginDecorator.groovy
```

#### REST

RESTful settings for this feature are available [here](Configuration-Properties-Common.html#restful-integrations) under the configuration key `cas.webflow.login-decorator.rest`.

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
# cas.webflow.session.lock-timeout=30
# cas.webflow.session.compress=false
# cas.webflow.session.max-conversations=5

# Enable server-side session management
# cas.webflow.session.storage=false
```

The signing and encryption keys [are both JWKs](Configuration-Properties-Common.html#signing--encryption) of size `512` and `256`.

#### Spring Webflow Client-Side Session

The encryption key must be randomly-generated string of size `16`. The signing key [is a JWK](Configuration-Properties-Common.html#signing--encryption) of size `512`.

Signing & encryption settings for this feature are available [here](Configuration-Properties-Common.html#signing--encryption) under the configuration key `cas.webflow`.

#### Spring Webflow Hazelcast Server-Side Session

```properties
# cas.webflow.session.hz-location=classpath:/hazelcast.xml
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
# cas.authn.errors.exceptions=value1,value2,...
```

### Authentication Interrupt

Interrupt the authentication flow to reach out to external services. To learn more about this topic, [please review this guide](../webflow/Webflow-Customization-Interrupt.html).

#### Authentication Interrupt JSON

```properties
# cas.interrupt.json.location=file:/etc/cas/config/interrupt.json
```

#### Authentication Interrupt Regex Attributes

```properties
# cas.interrupt.attribute-name=attribute-name-pattern
# cas.interrupt.attribute-value=attribute-value-pattern
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
# cas.acceptable-usage-policy.aup-attribute-name=aupAccepted
# cas.acceptable-usage-policy.aup-policy-terms-attribute-name=membership
```

#### Default

```properties
# cas.acceptable-usage-policy.in-memory.scope=GLOBAL|AUTHENTICATION
```                                                    

The following scopes are supported:

| Scope                | Description
|----------------------|----------------------------------
| `GLOBAL`             | Store decisions in the global in-memory map (for life of server).
| `AUTHENTICATION`     | Store decisions such that user is prompted when they authenticate via credentials.

#### Groovy

```properties
# cas.acceptable-usage-policy.groovy.location=file:/etc/cas/config/aup.groovy
```

#### REST

RESTful settings for this feature are available [here](Configuration-Properties-Common.html#restful-integrations) under the configuration key `cas.acceptable-usage-policy.rest`.

#### JDBC

If AUP is controlled via JDBC, decide how choices should be remembered back inside the database instance. Database settings for this feature are available [here](Configuration-Properties-Common.html#database-settings) under the configuration key `cas.acceptable-usage-policy.jdbc`.

```properties
# cas.acceptable-usage-policy.jdbc.table-name=usage_policies_table
# cas.acceptable-usage-policy.jdbc.aup-column=
# cas.acceptable-usage-policy.jdbc.principal-id-column=username
# cas.acceptable-usage-policy.jdbc.principal-id-attribute=
# cas.acceptable-usage-policy.jdbc.sql-update=UPDATE %s SET %s=true WHERE %s=?
```

#### CouchDb

Common configuration settings for this feature are available [here](Configuration-Properties-Common.html#couchdb-configuration) under 
the configuration key `cas.acceptable-usage-policy`. This feature uses the `asynchronous` setting.

#### Couchbase

Database settings for this feature are available [here](Configuration-Properties-Common.html#couchbase-integration-settings) under the 
configuration key `cas.acceptable-usage-policy.couchbase`.

#### MongoDb

Common configuration settings for this feature are available [here](Configuration-Properties-Common.html#mongodb-configuration) under 
the configuration key `cas.acceptable-usage-policy`.

#### Redis

Common configuration settings for this feature are available [here](Configuration-Properties-Common.html#redis-configuration) under the configuration key `cas.acceptable-usage-policy`.

#### LDAP

If AUP is controlled via LDAP, decide how choices should be remembered back inside the LDAP instance. LDAP settings for this feature are available [here](Configuration-Properties-Common.html#ldap-connection-settings) under the configuration key `cas.acceptable-usage-policy.ldap[0]`.

#### Disable Acceptable Usage Policy

Allow acceptable usage policy webflow to be disabled - requires restart.

```properties
cas.acceptable-usage-policy.enabled=true
```

## REST API

To learn more about this topic, [please review this guide](../protocol/REST-Protocol.html).

```properties
# cas.rest.attribute-name=
# cas.rest.attribute-value=
# cas.rest.header-auth=
# cas.rest.body-auth=
# cas.rest.tls-client-auth=
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
# cas.saml-metadata-ui.require-valid-metadata=true
# cas.saml-metadata-ui.resources=classpath:/sp-metadata::classpath:/pub.key,http://md.incommon.org/InCommon/InCommon-metadata.xml::classpath:/inc-md-pub.key
# cas.saml-metadata-ui.max-validity=0
# cas.saml-metadata-ui.require-signed-root=false
# cas.saml-metadata-ui.parameter=entityId
```         

Scheduler settings for this feature are available [here](Configuration-Properties-Common.html#job-scheduling) under the configuration key `cas.saml-metadata-ui`.

## Eureka Service Discovery

To learn more about this topic, [please review this guide](../installation/Service-Discovery-Guide-Eureka.html).

```properties
# eureka.client.service-url.default-zone=${EUREKA_SERVER_HOST:http://localhost:8761}/eureka/
# eureka.client.enabled=true
# eureka.instance.status-page-url=${cas.server.prefix}/actuator/info
# eureka.instance.health-check-url=${cas.server.prefix}/actuator/health
# eureka.instance.home-page-url=${cas.server.prefix}/
# eureka.client.healthcheck.enabled=true

# spring.cloud.config.discovery.enabled=false
```

## Consul Service Discovery

To learn more about this topic, [please review this guide](../installation/Service-Discovery-Guide-Consul.html).

```properties
# spring.cloud.consul.port=8500
# spring.cloud.consul.enabled=true
# spring.cloud.consul.host=localhost

# spring.cloud.consul.discovery.health-check-path=<health-endpoint-url>
# spring.cloud.consul.discovery.health-check-interval=15s
# spring.cloud.consul.discovery.instance-id=${spring.application.name}:${random.value}

# spring.cloud.consul.discovery.heartbeat.enabled=true
# spring.cloud.consul.discovery.heartbeat.ttl-value=60
# spring.cloud.consul.discovery.heartbeat.ttl-unit=s
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
# cas.consent.reminder-time-unit=HOURS|DAYS|MONTHS
# cas.consent.enabled=true
# cas.consent.active=true

# cas.consent.activation-strategy-groovy-script.location=
```

Signing & encryption settings for this feature are available [here](Configuration-Properties-Common.html#signing--encryption) under the configuration key `cas.consent`. The signing and encryption keys [are both JWKs](Configuration-Properties-Common.html#signing--encryption) of size `512` and `256`.

### Webflow configuration

Webflow auto-configuration settings for this feature are available [here](Configuration-Properties-Common.html#webflow-auto-configuration) under 
the configuration key `cas.consent.webflow`.

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
# cas.consent.ldap.consent-attribute-name=casConsentDecision
```

### MongoDb Attribute Consent

Common configuration settings for this feature are available [here](Configuration-Properties-Common.html#mongodb-configuration) under the configuration key `cas.consent`.
 
### Redis Attribute Consent
 
Common configuration settings for this feature are available [here](Configuration-Properties-Common.html#redis-configuration) under the configuration key `cas.consent`.

### CouchDb Attribute Consent

Common configuration settings for this feature are available [here](Configuration-Properties-Common.html#couchdb-configuration) under the configuration key `cas.consent`.

### REST Attribute Consent

```properties
# cas.consent.rest.endpoint=https://api.example.org/trustedBrowser
```

## Apache Fortress Authentication

To learn more about this topic, [please review this guide](../installation/Configuring-Fortress-Authentication.html).

```properties
# cas.authn.fortress.rbaccontext=HOME
```

## CAS Client

Configure settings relevant to the Java CAS client configured to handle inbound ticket validation operations, etc.

```properties
# cas.client.prefix=https://sso.example.org/cas
# cas.client.validator-type=CAS10|CAS20|CAS30|JSON
```

## Password Synchronization

Allow the user to synchronize account password to a variety of destinations in-place. To learn more about this 
topic, [please review this guide](../installation/Password-Synchronization.html).

### LDAP Password Sync

Common LDAP settings for this feature are available [here](Configuration-Properties-Common.html#ldap-connection-settings) under 
the configuration key `cas.authn.passwordSync.ldap[0]`.

```properties
# cas.authn.password-sync.enabled=true
# cas.authn.password-sync.ldap[0].enabled=false
``` 

## Password Management

Allow the user to update their account password, etc in-place.
To learn more about this topic, [please review this guide](../installation/Password-Policy-Enforcement.html).

```properties
# cas.authn.pm.enabled=true
# cas.authn.pm.captcha-enabled=false

# Minimum 8 and Maximum 10 characters at least 1 Uppercase Alphabet, 1 Lowercase Alphabet, 1 Number and 1 Special Character
# cas.authn.pm.policy-pattern=^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@$!%*?&])[A-Za-z\\d$@$!%*?&]{8,10}

# cas.authn.pm.reset.expirationMinutes=1
# cas.authn.pm.reset.security-questions-enabled=true

# Whether the Password Management Token will contain the client or server IP Address.
# cas.authn.pm.reset.include-server-ip-address=true
# cas.authn.pm.reset.include-client-ip-address=true

# Automatically log in after successful password change
# cas.authn.pm.auto-login=false
```

Common email notifications settings for this feature are available [here](Configuration-Properties-Common.html#email-notifications) 
under the configuration key `cas.authn.pm.reset`. SMS notifications settings for this feature are 
available [here](Configuration-Properties-Common.html#sms-notifications) under the configuration key `cas.authn.pm.reset`.

The signing and encryption keys [are both JWKs](Configuration-Properties-Common.html#signing--encryption) of size `512` and `256`.
The encryption algorithm is set to `AES_128_CBC_HMAC_SHA_256`. Signing & encryption settings for this feature are available [here](Configuration-Properties-Common.html#signing--encryption) under the configuration key `cas.authn.pm.reset`.

### Webflow configuration

Webflow auto-configuration settings for this feature are available [here](Configuration-Properties-Common.html#webflow-auto-configuration) under 
the configuration key `cas.authn.pm.webflow`.

### Password History

To learn more about this topic, [please review this guide](../installation/Password-Policy-Enforcement.html).

```properties
# cas.authn.pm.history.enabled=false

# cas.authn.pm.history.groovy.location=classpath:PasswordHistory.groovy
```

### JSON Password Management

```properties
# cas.authn.pm.json.location=classpath:jsonResourcePassword.json
```

### Groovy Password Management

```properties
# cas.authn.pm.groovy.location=classpath:PasswordManagementService.groovy
```

### LDAP Password Management

Common LDAP settings for this feature are available [here](Configuration-Properties-Common.html#ldap-connection-settings) 
under the configuration key `cas.authn.pm.ldap[0]`.

```properties
# cas.authn.pm.ldap[0].type=AD|GENERIC|EDirectory|FreeIPA
# cas.authn.pm.ldap[0].username-attribute=uid

# Attributes that should be fetched to indicate security questions and answers
# cas.authn.pm.ldap[0].security-questions-attributes.attr-question1=attrAnswer1
# cas.authn.pm.ldap[0].security-questions-attributes.attr-question2=attrAnswer2
# cas.authn.pm.ldap[0].security-questions-attributes.attr-question3=attrAnswer3
```

### JDBC Password Management

Common Database settings for this feature are available [here](Configuration-Properties-Common.html#database-settings) 
under the configuration key `cas.authn.pm.jdbc`. Common password encoding  settings for this 
feature are available [here](Configuration-Properties-Common.html#password-encoding) under the configuration key `cas.authn.pm.jdbc`.

```properties
# The two fields indicated below are expected to be returned
# cas.authn.pm.jdbc.sql-security-questions=SELECT question, answer FROM table WHERE user=?

# cas.authn.pm.jdbc.sql-find-email=SELECT email FROM table WHERE user=?
# cas.authn.pm.jdbc.sql-find-phone=SELECT phone FROM table WHERE user=?
# cas.authn.pm.jdbc.sql-find-user=SELECT user FROM table WHERE email=?
# cas.authn.pm.jdbc.sql-change-password=UPDATE table SET password=? WHERE user=?
```

### REST Password Management

```properties
# cas.authn.pm.rest.endpoint-url-email=
# cas.authn.pm.rest.endpoint-url-phone=
# cas.authn.pm.rest.endpoint-url-user=
# cas.authn.pm.rest.endpoint-url-security-questions=
# cas.authn.pm.rest.endpoint-url-change=
# cas.authn.pm.rest.endpoint-username=
# cas.authn.pm.rest.endpoint-password=
```
