---
layout: default
title: CAS - Configuration Server
category: Configuration
---

# Configuration Server

As your CAS deployment moves through the deployment pipeline from dev to test and into production
you can manage the configuration between those environments and be certain that applications
have everything they need to run when they migrate through the use of an external configuration server
provided by the [Spring Cloud](https://github.com/spring-cloud/spring-cloud-config) project. As an alternative,
you may decide to simply run CAS in a standalone mode removing the need for external configuration server deployment,
though at the cost of losing features and capabilities relevant for a cloud deployment.

## Configuration Profiles

The CAS server web application responds to the following strategies that dictate how settings should be consumed.

### Standalone

This is the default configuration mode which indicates that CAS does **NOT** require connections to an external configuration server
and will run in an embedded *standalone mode*. When this option is turned on, CAS by default will attempt to locate settings and properties
inside a given directory indicated under the setting name `cas.standalone.configurationDirectory` and otherwise falls back to using `/etc/cas/config` as the configuration directory.
You may instruct CAS to use this setting via the methods [outlined here](Configuration-Management.html#overview). 
There also exists a `cas.standalone.configurationFile` which can be used to directly feed a collection of properties to CAS in form of a file or classpath resource. 

Similar to the Spring Cloud external configuration server, the contents of this directory include `(cas|application).(yml|properties)`
files that can be used to control CAS behavior. Also, note that this configuration directory can be monitored by CAS to auto-pick up changes
and refresh the application context as needed. Please [review this guide](Configuration-Management-Reload.html#reload-strategy) to learn more.

Note that by default, all CAS settings and configuration is controlled via the embedded `application.properties` file in the CAS server
web application. There is also an embedded `application.yml` file that allows you to override all defaults if you wish to ship the configuration inside the main CAS web application and not rely on externalized configuration files. If you prefer properties to yaml, then `application-standalone.properties` will override `application.properties` as well. 

Settings found in external configuration files are and will be able to override the defaults provided by CAS. The naming of the configuration files 
inside the CAS configuration directory follows the below pattern:

- An `application.(properties|yml|yaml)` file is always loaded, if found.
- Settings located inside `properties|yml|yaml` files whose name matches the value of `spring.application.name` are loaded (i.e `cas.properties`) Note: `spring.application.name` defaults to uppercase `CAS` but the lowercase name will also be loaded.
- Settings located inside `properties|yml|yaml` files whose name matches the value of `spring.profiles.active` are loaded (i.e `ldap.properties`).
- Profile-specific application properties outside of your packaged web application (`application-{profile}.properties|yml|yaml`)
This allows you to, if needed, split your settings into multiple property files and then locate them by assigning their name
to the list of active profiles (i.e. `spring.profiles.active=standalone,testldap,stagingMfa`)

Configuration files are loaded in the following order where `spring.profiles.active=standalone,profile1,profile2`. Note that the last configuration file loaded will override any duplicate properties from configuration files loaded earlier:

1. `application.(properties|yml|yaml) `
2. (lower case) `spring.application.name.(properties|yml|yaml)`  
3. `spring.application.name.(properties|yml|yaml)`
4. `application-standalone.(properties|yml|yaml)`
5. `standalone.(properties|yml|yaml)`
6. `application-profile1.(properties|yml|yaml)`
7. `profile1.(properties|yml|yaml)`
8. `application-profile2.(properties|yml|yaml)`
9. `profile2.(properties|yml|yaml)`     

If two configuration files with same base name and different extensions exist, they are processed in the order of `properties`, `yml` and then `yaml` and then `groovy` (last one processed wins where duplicate properties exist). These external configuration files will override files located in the classpath (e.g. files from `src/main/resources` in your CAS overlay that end up in `WEB-INF/classes`) but the internal files are loaded per the [spring boot](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html) rules which differ from the CAS standalone configuration rules described here (e.g. <profile>.properties would not be loaded from classpath but `application-<profile>.properties` would).

<div class="alert alert-warning"><strong>Remember</strong><p>You are advised to not overlay or otherwise
modify the built in <code>application.properties</code> or <code>bootstrap.properties</code> files. This will only complicate and weaken your deployment.
Instead try to comply with the CAS defaults and bootstrap CAS as much as possible via the defaults, override via <code>application.yml</code>, <code>application-standalone.properties</code> or
use the <a href="Configuration-Management.html#overview">outlined strategies</a>. Likewise, try to instruct CAS to locate
configuration files external to its own. Premature optimization will only lead to chaos.</p></div>

### Spring Cloud

CAS is able to use an external and central configuration server to obtain state and settings.
The configuration server provides a very abstract way for CAS (and all of its other clients) to obtain settings from a variety
of sources, such as file system, `git` or `svn` repositories, MongoDb databases, Vault, etc. The beauty of this solution is that to the CAS
web application server, it matters not where settings come from and it has no knowledge of the underlying property sources. It simply
talks to the configuration server to locate settings and move on.

<div class="alert alert-info"><strong>Configuration Security</strong><p>This is a very good strategy to ensure configuration settings
are not scattered around various deployment environments leading to a more secure deployment. The configuration server need not be
exposed to the outside world, and it can safely and secure be hidden behind firewalls, etc allowing access to only authorized clients
such as the CAS server web application.</p></div>

A full comprehensive guide is provided by the [Spring Cloud project](https://cloud.spring.io/spring-cloud-config/spring-cloud-config.html).

#### Overlay

The configuration server itself, similar to CAS, can be deployed
via the following module in it own [WAR overlay](https://github.com/apereo/cas-configserver-overlay):

```xml
<dependency>
  <groupId>org.apereo.cas</groupId>
  <artifactId>cas-server-webapp-config-server</artifactId>
  <version>${cas.version}</version>
</dependency>
```

In addition to the [strategies outlined here](Configuration-Management.html#overview), the configuration server 
may load CAS settings and properties via the following order and mechanics:

1. Profile-specific application properties outside of your packaged web application (`application-{profile}.properties|yml`)
2. Profile-specific application properties packaged inside your jar (`application-{profile}.properties|yml`)
3. Application properties outside of your packaged jar (`application.properties|yml`).
4. Application properties packaged inside your jar (`application.properties|yml`).

The configuration and behavior of the configuration server is also controlled by its own
`src/main/resources/bootstrap.properties` file. By default, it runs under port `8888` at `/casconfigserver` inside
an embedded Apache Tomcat server whose endpoints are protected with basic authentication
where the default credentials are `casuser` and an auto-generated password defined in `src/main/resources/application.properties`. 

Furthermore, by default it runs under a `native` profile described below.

The following endpoints are secured and exposed by the configuration server:

| Parameter                         | Description
|-----------------------------------|------------------------------------------
| `/encrypt`                        | Accepts a `POST` to encrypt CAS configuration settings.
| `/decrypt`                        | Accepts a `POST` to decrypt CAS configuration settings.
| `/actuator/refresh`               | Accepts a `POST` and attempts to refresh the internal state of configuration server.
| `/actuator/env`                   | Accepts a `GET` and describes all configuration sources of the configuration server.
| `/actuator/cas/default`           | Describes what the configuration server knows about the `default` settings profile.
| `/actuator/cas/native`            | Describes what the configuration server knows about the `native` settings profile.

Once you have the configuration server deployed and assuming the credentials used to secure the configuration server match the example below, you can observe the collection of settings via:

```bash
curl -u casuser:Mellon https://config.server.url:8888/casconfigserver/cas/native
```

Assuming actuator endpoints are enabled in the configuration, you can also observe the collection of property sources that provide settings to the configuration server:

```bash
curl -u casuser:Mellon https://config.server.url:8888/casconfigserver/actuator/env
```

<div class="alert alert-info"><strong>Actuator Endpoints</strong><p>
Remember that actuator endpoints typically are prefixed with <code>/actuator</code>.
</p></div>

#### Clients and Consumers

To let the CAS server web application (or any other client for that matter) talk to the configuration server,
the following settings need to be applied to CAS' own `src/main/resources/bootstrap.properties` file.
The properties to configure the CAS server web application as the client of the configuration server
must necessarily be read in before the rest of the application’s configuration is read from the configuration server, during the *bootstrap* phase.

```properties
spring.cloud.config.uri=https://casuser:Mellon@config.server.url:8888/casconfigserver
spring.cloud.config.profile=native
spring.cloud.config.enabled=true
spring.profiles.active=default
```

Remember that configuration server serves property sources from `/{name}/{profile}/{label}` to applications,
where the default bindings in the client app are the following:

```bash
"name" = ${spring.application.name}
"profile" = ${spring.profiles.active}
"label" = "master"
```

All of them can be overridden by setting `spring.cloud.config.*` (where `*` is "name", "profile" or "label").
The "label" is useful for rolling back to previous versions of configuration; with the default Config Server implementation
it can be a git label, branch name or commit id. Label can also be provided as a comma-separated list,
in which case the items in the list are tried on-by-one until one succeeds. This can be useful when working on a feature
branch, for instance, when you might want to align the config label with your branch,
but make it optional (e.g. `spring.cloud.config.label=myfeature,develop`).

To lean more about how CAS allows you to reload configuration changes,
please [review this guide](Configuration-Management-Reload.html).

#### Profiles

Various profiles exist to determine how configuration server should retrieve properties and settings.

##### Native

The server is configured by default to load `cas.(properties|yml)` files from an external location that is `/etc/cas/config`.
This location is constantly monitored by the server to detect external changes. Note that this location simply needs to
exist, and does not require any special permissions or structure. The name of the configuration file that goes inside this
directory needs to match the `spring.application.name` (i.e. `cas.properties`).

If you want to use additional configuration files, they need to have the
form `application-<profile>.(properties|yml)`.
A file named `application.(properties|yml)` will be included by default. The profile specific
files can be activated by using the `spring.profiles.include` configuration option,
controlled via the `src/main/resources/bootstrap.properties` file:

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

##### Default

The configuration server is also able to handle `git` or `svn` based repositories that host CAS configuration.
Such repositories can either be local to the deployment, or they could be on the cloud in form of GitHub/BitBucket. Access to
cloud-based repositories can either be in form of a username/password, or via SSH so as long the appropriate keys are configured in the
CAS deployment environment which is really no different than how one would normally access a git repository via SSH.

```properties
# spring.profiles.active=default
# spring.cloud.config.server.git.uri=https://github.com/repoName/config
# spring.cloud.config.server.git.uri=file://${user.home}/config
# spring.cloud.config.server.git.username=
# spring.cloud.config.server.git.password=

# spring.cloud.config.server.svn.basedir=
# spring.cloud.config.server.svn.uri=
# spring.cloud.config.server.svn.username=
# spring.cloud.config.server.svn.password=
# spring.cloud.config.server.svn.default-label=trunk
```

Needless to say, the repositories could use both YAML and properties syntax to host configuration files.

<div class="alert alert-info"><strong>Keep What You Need!</strong><p>Again, in all of the above strategies,
an adopter is encouraged to only keep and maintain properties needed for their particular deployment. It is
UNNECESSARY to grab a copy of all CAS settings and move them to an external location. Settings that are
defined by the external configuration location or repository are able to override what is provided by CAS
as a default.</p></div>

##### MongoDb

The server is also able to locate properties entirely from a MongoDb instance.

Support is provided via the following dependency in the WAR overlay:

```xml
<dependency>
     <groupId>org.apereo.cas</groupId>
     <artifactId>cas-server-support-configuration-cloud-mongo</artifactId>
     <version>${cas.version}</version>
</dependency>
```

Note that to access and review the collection of CAS properties,
you will need to use your own native tooling for MongoDB to configure and inject settings.

MongoDb documents are required to be found in the collection `MongoDbProperty`, as the following document:

```json
{
    "id": "kfhf945jegnsd45sdg93452",
    "name": "the-setting-name",
    "value": "the-setting-value"
}
```

To see the relevant list of CAS properties for this feature, please [review this guide](Configuration-Properties.html#mongodb).

##### HashiCorp Vault

CAS is also able to use [Vault](https://www.vaultproject.io/) to
locate properties and settings. [Please review this guide](Configuration-Properties-Security.html).

##### HashiCorp Consul

CAS is also able to use [Consul](https://www.consul.io/) to
locate properties and settings. [Please review this guide](../installation/Service-Discovery-Guide-Consul.html).

##### Apache ZooKeeper

CAS is also able to use [Apache ZooKeeper](https://zookeeper.apache.org/) to locate properties and settings.

Support is provided via the following dependency in the WAR overlay:

```xml
<dependency>
     <groupId>org.apereo.cas</groupId>
     <artifactId>cas-server-support-configuration-cloud-zookeeper</artifactId>
     <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties for this feature, please [review this guide](Configuration-Properties.html#zookeeper).

You will need to map CAS settings to ZooKeeper's nodes that contain values. The parent node for all settings should 
match the configuration root value provided to CAS. Under the root, you could have folders such 
as `cas`, `cas,dev`, `cas,local`, etc where `dev` and `local` are Spring profiles.

To create nodes and values in Apache ZooKeeper, try the following commands
as a sample:

```bash
zookeeper-client -server zookeeper1:2181
create /cas cas
create /cas/config cas
create /cas/config/cas cas
create /cas/config/cas/settingName casuser::Test
```

Creating nodes and directories in Apache ZooKeeper may require providing a value. The above sample commands show that 
the value `cas` is provided when creating directories. Always check with the official Apache ZooKeeper guides. You may not need to do that step.

Finally in your CAS properties, the new `settingName` setting can be used as a reference.

```properties
# cas.something.something=${settingName}
```

...where `${settingName}` gets the value of the contents of the Apache ZooKeeper node `cas/config/cas/settingName`.

##### Amazon S3

CAS is also able to use [Amazon S3](https://docs.aws.amazon.com/AmazonS3/latest/dev) to locate properties and settings.

Support is provided via the following dependency in the WAR overlay:
 
 ```xml
 <dependency>
      <groupId>org.apereo.cas</groupId>
      <artifactId>cas-server-support-configuration-cloud-aws-s3</artifactId>
      <version>${cas.version}</version>
 </dependency>
 ```
 
 See [this guide](Configuration-Properties.html#amazon-s3) for relevant settings.

##### Amazon Secrets Manager

CAS is also able to use [Amazon Secret Manager](https://aws.amazon.com/secrets-manager/) to locate properties and settings.

Support is provided via the following dependency in the WAR overlay:

```xml
<dependency>
     <groupId>org.apereo.cas</groupId>
     <artifactId>cas-server-support-configuration-cloud-aws-secretsmanager</artifactId>
     <version>${cas.version}</version>
</dependency>
```

See [this guide](Configuration-Properties.html#amazon-secrets-manager) for relevant settings.

##### DynamoDb

CAS is also able to use [DynamoDb](https://aws.amazon.com/dynamodb/) to locate properties and settings.

Support is provided via the following dependency in the WAR overlay:

```xml
<dependency>
     <groupId>org.apereo.cas</groupId>
     <artifactId>cas-server-support-configuration-cloud-dynamodb</artifactId>
     <version>${cas.version}</version>
</dependency>
```

The `DynamoDbCasProperties` table is automatically created by CAS with the following structure:

```json
{
    "id": "primary-key",
    "name": "the-setting-name",
    "value": "the-setting-value"
}
```

See [this guide](Configuration-Properties.html#dynamodb) for relevant settings.

##### Azure KeyVault Secrets

CAS is also able to use Microsoft Azure's KeyVault Secrets to locate properties and settings. Support is provided via the following dependency in the WAR overlay:

```xml
<dependency>
     <groupId>org.apereo.cas</groupId>
     <artifactId>cas-server-support-configuration-cloud-azure-keyvault</artifactId>
     <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties for this feature, please [review this guide](Configuration-Properties.html#azure-keyvault-secrets).

**IMPORTANT**: The allowed  name pattern in Azure Key Vault is `^[0-9a-zA-Z-]+$`. For properties that contain 
that contain `.` in the name (i.e. `cas.some.property`),  replace `.` with `-` when you store the setting in Azure Key Vault (i.e. `cas-some-property`). 
The module will handle the transformation for you. 

See [this guide](Configuration-Properties.html#azure-keyvault-secrets) for relevant settings.

##### JDBC

CAS is also able to use a relational database to locate properties and settings.

Support is provided via the following dependency in the WAR overlay:

```xml
<dependency>
     <groupId>org.apereo.cas</groupId>
     <artifactId>cas-server-support-configuration-cloud-jdbc</artifactId>
     <version>${cas.version}</version>
</dependency>
```

By default, settings are expected to be found under a `CAS_SETTINGS_TABLE` that contains the fields: `id`, `name` and `value`.
To see the relevant list of CAS properties for this feature, please [review this guide](Configuration-Properties.html#jdbc).

#### CAS Server Cloud Configuration

The cloud configuration modules provided above on this page by the CAS project directly may also be used verbatim inside 
a CAS server overlay. Remember that the primary objective for these modules is to simply retrieve settings and properties 
from a source. While they are mostly and primarily useful when activated inside the Spring Cloud Configuration server and 
can be set to honor profiles and such, they nonetheless may also be used inside a CAS server overlay directly to simply 
fetch settings from a source while running in standalone mode. In such scenarios, all sources of configuration regardless 
of format or syntax will work alongside each other to retrieve settings and you can certainly mix and match as you see fit.

#### Composite Sources

In some scenarios you may wish to pull configuration data from multiple environment repositories.
To do this just enable multiple profiles in your configuration server’s application properties or YAML file.
If, for example, you want to pull configuration data from a Git repository as well as a SVN
repository you would set the following properties for your configuration server.

```yml
spring:
  profiles:
    active: git, svn
  cloud:
    config:
      server:
        svn:
          uri: file:///path/to/svn/repo
          order: 2
        git:
          uri: file:///path/to/git/repo
          order: 1
```

In addition to each repo specifying a URI, you can also specify an `order` property. The `order` property allows you to specify
the priority order for all your repositories. The lower the numerical value of the order property the
higher priority it will have. The priority order of a repository will help resolve any potential
conflicts between repositories that contain values for the same properties.

#### Property Overrides

The configuration server has an "overrides" feature that allows the operator to provide configuration properties 
to all applications that cannot be accidentally changed by the application using the normal change events and hooks. 
To declare overrides add a map of name-value pairs to `spring.cloud.config.server.overrides`. 

For example:

```yml
spring:
  cloud:
    config:
      server:
        overrides:
          foo: bar
```

This will cause the CAS server (as the client of the configuration server) to read `foo=bar` independent of its own configuration.

## Securing Settings

To learn how sensitive CAS settings can be secured via encryption, [please review this guide](Configuration-Properties-Security.html).

## Reloading Changes

To lean more about how CAS allows you to reload configuration changes,
please [review this guide](Configuration-Management-Reload.html).

## Clustered Deployments

CAS uses the [Spring Cloud Bus](http://cloud.spring.io/spring-cloud-static/spring-cloud.html)
to manage configuration in a distributed deployment. Spring Cloud Bus links nodes of a
distributed system with a lightweight message broker. This can then be used to broadcast state
changes (e.g. configuration changes) or other management instructions.

To learn how sensitive CAS settings can be secured via encryption, [please review this guide](Configuration-Management-Clustered.html).
