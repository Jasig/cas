---
layout: default
title: CAS Common Properties Overview
category: Configuration
---

# CAS Common Properties

This document describes a number of suggestions and configuration options that apply to and are common amongst a selection of CAS modules and features. 
To see the full list of CAS properties, please [review this guide](Configuration-Properties.html).

## What is `${configurationKey}`?

Many CAS *sub* settings are common and applicable to a number of modules and features. For example, in dealing with database authentication there are a number of database-related modules who own an individual setting to define the database driver. These settings would typically be defined as `cas.authn.feature1.database-driver=xyz` and `cas.authn.feature2.database-driver=abc`. Rather than duplicating the shared and common `database-driver` setting, this page attempts to collect only what might be common CAS settings across features and modules while referring to the specific feature under the path `${configurationKey}`. Therefore, the documentation for either `feature1` or `feature2` might allow one to find common database-related settings (such as the `database-driver`) under `${configurationKey}.database-driver` where `${configurationKey}` would either be `cas.authn.feature1` or `cas.authn.feature2` depending on feature at hand. The notes and documentation for each feature that wants to inherit from a common block of settings should always advertise the appropriate value for `${configurationKey}`.

## Naming Convention

- Settings and properties that are controlled by the CAS platform directly always begin with the prefix `cas`. All other settings are controlled 
and provided to CAS via other underlying frameworks and may have their own schemas and syntax. **BE CAREFUL** with the distinction.

- Unrecognized properties are rejected by CAS and/or frameworks upon which CAS depends. 
This means if you somehow misspell a property definition or fail to adhere to the dot-notation syntax and such, your setting 
is entirely refused by CAS and likely the feature it controls will never be activated in the way you intend.

## Indexed Settings

CAS settings able to accept multiple values are typically documented with an index, such as `cas.some.setting[0]=value`.
The index `[0]` is meant to be incremented by the adopter to allow for distinct multiple configuration blocks:

```properties
# cas.some.setting[0]=value1
# cas.some.setting[1]=value2
```

## Trust But Verify

If you are unsure about the meaning of a given CAS setting, do **NOT** turn it on without hesitation.
Review the codebase or better yet, [ask questions](/cas/Mailing-Lists.html) to clarify the intended behavior.

<div class="alert alert-info"><strong>Keep It Simple</strong><p>If you do not know or cannot tell what a setting does, you do not need it.</p></div>

## Time Unit of Measure

All CAS settings that deal with time units, unless noted otherwise,
should support the duration syntax for full clarity on unit of measure:

```bash
"PT20S"     -- parses as "20 seconds"
"PT15M"     -- parses as "15 minutes"
"PT10H"     -- parses as "10 hours"
"P2D"       -- parses as "2 days"
"P2DT3H4M"  -- parses as "2 days, 3 hours and 4 minutes"
```

The native numeric syntax is still supported though you will have to refer to the docs
in each case to learn the exact unit of measure.

## Job Scheduling

A number of CAS components are given the ability to schedule background jobs to clean tokens, remove records, etc.
The behavior of the scheduler can be controlled using the following settings:

```properties
# ${configurationKey}.schedule.start-delay=PT10S
# ${configurationKey}.schedule.repeat-interval=PT60S
# ${configurationKey}.schedule.enabled=true
```

## Authentication Throttling

Certain functionality in CAS, such as [OAuth](../installation/OAuth-OpenId-Authentication.html) 
or [REST API](../protocol/REST-Protocol.html), allow you to throttle requests to specific endpoints in addition to the more 
generic authentication throttling functionality applied during the login flow and authentication attempts.

To fully deliver this functionality, it is expected that [authentication throttling](../installation/Configuring-Authentication-Throttling.html) is turned on.

## Authentication Credential Selection

A number of authentication handlers are allowed to determine whether they can operate on the provided credential
and as such lend themselves to be tried and tested during the authentication handler selection phase. The credential criteria
may be one of the following options:

- A regular expression pattern that is tested against the credential identifier
- A fully qualified class name of your own design that looks similar to the below example:

```java
import java.util.function.Predicate;
import org.apereo.cas.authentication.Credential;

public class PredicateExample implements Predicate<Credential> {
    @Override
    public boolean test(final Credential credential) {
        // Examine the credential and return true/false
    }
}
```

- Path to an external Groovy script that looks similar to the below example:

```groovy
import org.apereo.cas.authentication.Credential
import java.util.function.Predicate

class PredicateExample implements Predicate<Credential> {
    @Override
    boolean test(final Credential credential) {
        // test and return result
    }
}
```

## Password Encoding

Certain aspects of CAS such as authentication handling support configuration of
password encoding. Most options are based on Spring Security's [support for password encoding](https://docs.spring.io/spring-security/site/docs/current/reference/html5/).

The following options related to password encoding support in CAS apply equally to a number of CAS components (authentication handlers, etc) given the component's *configuration key*:

```properties
# ${configurationKey}.password-encoder.type=NONE|DEFAULT|STANDARD|BCRYPT|SCRYPT|PBKDF2
# ${configurationKey}.password-encoder.character-encoding=
# ${configurationKey}.password-encoder.encoding-algorithm=
# ${configurationKey}.password-encoder.secret=
# ${configurationKey}.password-encoder.strength=16
```

The following options are supported:

| Type                    | Description
|-------------------------|----------------------------------------------------------------------------------------------------
| `NONE`                  | No password encoding (i.e. plain-text) takes place.     
| `DEFAULT`               | Use the `DefaultPasswordEncoder` of CAS. For message-digest algorithms via `characterEncoding` and `encodingAlgorithm`.
| `BCRYPT`                | Use the `BCryptPasswordEncoder` based on the `strength` provided and an optional `secret`.     
| `SCRYPT`                | Use the `SCryptPasswordEncoder`.
| `PBKDF2`                | Use the `Pbkdf2PasswordEncoder` based on the `strength` provided and an optional `secret`.
| `STANDARD`              | Use the `StandardPasswordEncoder` based on the `secret` provided.
| `SSHA`                  | Use the `LdapShaPasswordEncoder` supports Ldap SHA and SSHA (salted-SHA). The values are base-64 encoded and have the label `{SHA}` (or `{SSHA}`) prepended to the encoded hash. 
| `GLIBC_CRYPT`           | Use the `GlibcCryptPasswordEncoder` based on the [`encoding-algorithm`](https://commons.apache.org/proper/commons-codec/archives/1.10/apidocs/org/apache/commons/codec/digest/Crypt.html), `strength` provided and an optional `secret`.
| `org.example.MyEncoder` | An implementation of `PasswordEncoder` of your own choosing.
| `file:///path/to/script.groovy` | Path to a Groovy script charged with handling password encoding operations.

In cases where you plan to design your own password encoder or write scripts to do so, 
you may also need to ensure the overlay has access to `org.springframework.security:spring-security-core` at runtime. Make sure the artifact is marked as `provided`
or `compileOnly` to avoid conflicts.

If you need to design your own password encoding scheme where the type is specified as a fully qualified Java class name, the structure of the class would be
 similar to the following:

```java
package org.example.cas;

import org.springframework.security.crypto.codec.*;
import org.springframework.security.crypto.password.*;

public class MyEncoder extends AbstractPasswordEncoder {
    @Override
    protected byte[] encode(CharSequence rawPassword, byte[] salt) {
        return ...
    }
}
```

If you need to design your own password encoding scheme where the type is specified as a path to a Groovy script, the structure of the script would be similar
 to the following:

```groovy
import java.util.*

def byte[] run(final Object... args) {
    def rawPassword = args[0]
    def generatedSalt = args[1]
    def logger = args[2]
    def casApplicationContext = args[3]

    logger.debug("Encoding password...")
    return ...
}

def Boolean matches(final Object... args) {
    def rawPassword = args[0]
    def encodedPassword = args[1]
    def logger = args[2]
    def casApplicationContext = args[3]

   logger.debug("Does match or not ?");
   return ...
```

## Authentication Principal Transformation

Authentication handlers that generally deal with username-password credentials
can be configured to transform the user id prior to executing the authentication sequence.
The following options may be used:

| Type                    | Description
|-------------------------|----------------------------------------------------------
| `NONE`                  | Do not apply any transformations.
| `UPPERCASE`             | Convert the username to uppercase.
| `LOWERCASE`             | Convert the username to lowercase.

Authentication handlers as part of principal transformation may also be provided a path to a Groovy script to transform the provided username. The outline of the script may take on the following form:

```groovy
def String run(final Object... args) {
    def providedUsername = args[0]
    def logger = args[1]
    return providedUsername.concat("SomethingElse")
}
```

The following options related to principal transformation support in CAS apply equally to a number of CAS components (authentication handlers, etc) given the component's *configuration key*:

```properties
# ${configurationKey}.principal-transformation.pattern=(.+)@example.org
# ${configurationKey}.principal-transformation.groovy.location=file:///etc/cas/config/principal.groovy
# ${configurationKey}.principal-transformation.suffix=
# ${configurationKey}.principal-transformation.case-conversion=NONE|UPPERCASE|LOWERCASE
# ${configurationKey}.principal-transformation.prefix=
```

## Cookie Properties

The following common properties configure cookie generator support in CAS.

```properties
# ${configurationKey}.path=
# ${configurationKey}.max-age=-1
# ${configurationKey}.domain=
# ${configurationKey}.name=
# ${configurationKey}.secure=true
# ${configurationKey}.http-only=true
# ${configurationKey}.same-site-policy=none|lax|strict
# ${configurationKey}.comment=CAS Cookie
```

## Cassandra Configuration

Control properties that are relevant to Cassandra,
when CAS attempts to establish connections, run queries, etc.

```properties
# ${configurationKey}.keyspace=
# ${configurationKey}.contact-points=localhost:9042
# ${configurationKey}.local-dc=
# ${configurationKey}.consistency-level=ANY|ONE|TWO|THREE|QUORUM|LOCAL_QUORUM|ALL|EACH_QUORUM|LOCAL_SERIAL|SERIAL|LOCAL_ONE
# ${configurationKey}.serial-consistency-level=ANY|ONE|TWO|THREE|QUORUM|LOCAL_QUORUM|ALL|EACH_QUORUM|LOCAL_SERIAL|SERIAL|LOCAL_ONE
# ${configurationKey}.timeout=PT5S
```

## Hibernate & JDBC

Control global properties that are relevant to Hibernate,
when CAS attempts to employ and utilize database resources,
connections and queries.

```properties
# cas.jdbc.show-sql=true
# cas.jdbc.gen-ddl=true
# cas.jdbc.case-insensitive=false
# cas.jdbc.physical-table-names.{table-name}={new-table-name}
```

### Database Settings

The following options related to JPA/JDBC support in CAS apply equally to a number of CAS components (ticket registries, etc) given the component's *configuration key*:

```properties
# ${configurationKey}.user=sa
# ${configurationKey}.password=
# ${configurationKey}.driver-class=org.hsqldb.jdbcDriver
# ${configurationKey}.url=jdbc:hsqldb:mem:cas-hsql-database
# ${configurationKey}.dialect=org.hibernate.dialect.HSQLDialect

# ${configurationKey}.fail-fast-timeout=1
# ${configurationKey}.isolation-level-name=ISOLATION_READ_COMMITTED 
# ${configurationKey}.health-query=
# ${configurationKey}.isolate-internal-queries=false
# ${configurationKey}.leak-threshold=10
# ${configurationKey}.propagation-behaviorName=PROPAGATION_REQUIRED
# ${configurationKey}.batchSize=1
# ${configurationKey}.default-catalog=
# ${configurationKey}.default-schema=
# ${configurationKey}.ddl-auto=create-drop
# ${configurationKey}.physical-naming-strategy-class-name=org.apereo.cas.hibernate.CasHibernatePhysicalNamingStrategy

# ${configurationKey}.autocommit=false
# ${configurationKey}.idle-timeout=5000

# ${configurationKey}.data-source-name=
# ${configurationKey}.data-source-roxy=false

# Hibernate-specific properties (i.e. `hibernate.globally_quoted_identifiers`)
# ${configurationKey}.properties.property-name=propertyValue

# ${configurationKey}.pool.suspension=false
# ${configurationKey}.pool.mi-size=6
# ${configurationKey}.pool.max-size=18
# ${configurationKey}.pool.max-wait=2000
# ${configurationKey}.pool.timeout-millis=1000
```

### Container-based JDBC Connections

If you are planning to use a container-managed JDBC connection with CAS (i.e. JPA Ticket/Service Registry, etc)
then you can set the `data-source-name` property on any of the configuration items that require a database
connection. When using a container configured data source, many of the pool related parameters will not be used.
If `data-source-name` is specified but the JNDI lookup fails, a data source will be created with the configured 
(or default) CAS pool parameters.

If you experience classloading errors while trying to use a container datasource, you can try 
setting the `data-source-proxy` setting to true which will wrap the container datasource in
a way that may resolve the error.

The `data-source-name` property can be either a JNDI name for the datasource or a resource name prefixed with 
`java:/comp/env/`. If it is a resource name then you need an entry in a `web.xml` that you can add to your
CAS overlay. It should contain an entry like this:

```xml
<resource-ref>
    <res-ref-name>jdbc/casDataSource</res-ref-name>
    <res-type>javax.sql.DataSource</res-type>
    <res-auth>Container</res-auth>
</resource-ref>
```

In Apache Tomcat a container datasource can be defined like this in the `context.xml`:

```xml
<Resource name="jdbc/casDataSource"
    auth="Container"
    type="javax.sql.DataSource"
    driverClassName="org.postgresql.Driver"
    url="jdbc:postgresql://casdb.example.com:5432/xyz_db"
    username="cas"
    password="xyz"
    testWhileIdle="true"
    testOnBorrow="true"
    testOnReturn="false"
    validationQuery="select 1"
    validationInterval="30000"
    timeBetweenEvictionRunsMillis="30000"
    factory="org.apache.tomcat.jdbc.pool.DataSourceFactory"
    minIdle="0"
    maxIdle="5"
    initialSize="0"
    maxActive="20"
    maxWait="10000" />
```

In Jetty, a pool can be put in JNDI with a `jetty.xml` or `jetty-env.xml` file like this:

```xml
<?xml version="1.0"?>
<!DOCTYPE Configure PUBLIC "-//Jetty//Configure//EN" "http://www.eclipse.org/jetty/configure_9_3.dtd">

<Configure class="org.eclipse.jetty.webapp.WebAppContext">
<New id="datasource.cas" class="org.eclipse.jetty.plus.jndi.Resource">
    <Arg></Arg> <!-- empty scope arg is JVM scope -->
    <Arg>jdbc/casDataSource</Arg> <!-- name that matches resource in web.xml-->
    <Arg>
        <New class="org.apache.commons.dbcp.BasicDataSource">
            <Set name="driverClassName">oracle.jdbc.OracleDriver</Set>
            <Set name="url">jdbc:oracle:thin:@//casdb.example.com:1521/ntrs"</Set>
            <Set name="username">cas</Set>
            <Set name="password">xyz</Set>
            <Set name="validationQuery">select dummy from dual</Set>
            <Set name="testOnBorrow">true</Set>
            <Set name="testOnReturn">false</Set>
            <Set name="testWhileIdle">false</Set>
            <Set name="defaultAutoCommit">false</Set>
            <Set name="initialSize">0</Set>
            <Set name="maxActive">15</Set>
            <Set name="minIdle">0</Set>
            <Set name="maxIdle">5</Set>
            <Set name="maxWait">2000</Set>
        </New>
    </Arg>
</New>
</Configure>
```

## Signing & Encryption

A number of components in CAS accept signing and encryption keys. In most scenarios if keys are not provided, CAS will 
auto-generate them. The following instructions apply if you wish to manually and beforehand create the signing and encryption keys.

Note that if you are asked to create a [JWK](https://tools.ietf.org/html/rfc7517) of a certain size for the key, you are to use 
the following set of commands to generate the token:

```bash
wget https://raw.githubusercontent.com/apereo/cas/master/etc/jwk-gen.jar
java -jar jwk-gen.jar -t oct -s [size]
```

The outcome would be similar to:

```json
{
  "kty": "oct",
  "kid": "...",
  "k": "..."
}
```

The generated value for `k` needs to be assigned to the relevant CAS settings. Note that keys generated via 
the above algorithm are processed by CAS using the Advanced Encryption Standard (`AES`) algorithm which is a 
specification for the encryption of electronic data established by the U.S. National Institute of Standards and Technology.

### Settings

The following crypto options apply equally to relevant CAS 
components (ticket registries, etc) given the component's *configuration key*:

```properties
# ${configurationKey}.crypto.signing.key=
# ${configurationKey}.crypto.signing.key-size=

# ${configurationKey}.crypto.encryption.key=
# ${configurationKey}.crypto.encryption.key-size=

# ${configurationKey}.crypto.alg=AES
# ${configurationKey}.crypto.enabled=false   

# ${configurationKey}.crypto.strategy-type=ENCRYPT_AND_SIGN|SIGN_AND_ENCRYPT
```

The following cipher strategy types are available:

| Type                | Description
|---------------------|---------------------------------------------------
| `ENCRYPT_AND_SIGN`  | Default strategy; encrypt values, and then sign. 
| `SIGN_AND_ENCRYPT`  | Sign values, and then encrypt.

### RSA Keys

Certain features such as the ability to produce [JWTs as CAS tickets](../installation/Configure-ServiceTicket-JWT.html) may allow you to use the `RSA` algorithm with public/private keypairs for signing and encryption. This behavior may prove useful generally in cases where the consumer of the CAS-encoded payload is an outsider and a client application that need not have access to the signing secrets directly and visibly and may only be given a half truth vis-a-vis a public key to verify the payload authenticity and decode it. This particular option makes little sense in situations where CAS itself is both a producer and a consumer of the payload.

<div class="alert alert-info"><strong>Remember</strong><p>Signing and encryption options are not mutually exclusive. While it would be rather nonsensical, it is entirely possible for CAS to use <code>AES</code> keys for signing and <code>RSA</code> keys for encryption, or vice versa.</p></div>

In order to enable RSA functionality for signing payloads, you will need to generate a private/public keypair via the following sample commands:

```bash
openssl genrsa -out private.key 2048
openssl rsa -pubout -in private.key -out public.key -inform PEM -outform DER
```

The private key path (i.e. `file:///path/to/private.key`) needs to be configured for the signing key in CAS properties for the relevant feature. The public key needs to be shared with client applications and consumers of the payload in order to validate the payload signature.

```properties
# cas.xyz.crypto.signing.key=file:///etc/cas/config/private.key
```

<div class="alert alert-info"><strong>Key Size</strong><p>Remember that RSA key sizes are required to be at least <code>2048</code> and above. Smaller key sizes are not accepted by CAS and will cause runtime errors. Choose wisely.</p></div>

In order to enable RSA functionality for encrypting payloads, you will need to essentially execute the reverse of the above operations. The client application will provide you with a public key which will be used to encrypt the payload and whose path (i.e. `file:///path/to/public.key`) needs to be configured for the encryption key in CAS properties for the relevant feature. Once the payload is submitted, the client should use its own private key to decode the payload and unpack it.

```properties
# cas.xyz.crypto.encryption.key=file:///etc/cas/config/public.key
```

## Person Directory Principal Resolution

The following options related to Person Directory support in CAS when it attempts to resolve and build the authenticated principal, given the component's *configuration key*:

```properties
# ${configurationKey}.principal-attribute=uid,sAMAccountName,etc
# ${configurationKey}.return-null=false
# ${configurationKey}.principal-resolution-failure-fatal=false
# ${configurationKey}.use-existing-principal-id=false
# ${configurationKey}.attribute-resolution-enabled=true
# ${configurationKey}.active-attribute-repository-ids=StubRepository,etc
```

## Git Configuration

The following options related to Git integration support in CAS when it attempts to connect and pull/push changes, given the component's *configuration key*:

```properties
# ${configurationKey}.git.repository-url=https://github.com/repository
# ${configurationKey}.git.branches-to-clone=master
# ${configurationKey}.git.active-branch=master
# ${configurationKey}.git.sign-commits=false
# ${configurationKey}.git.username=
# ${configurationKey}.git.password=
# ${configurationKey}.git.clone-directory.location=file:/tmp/cas-service-registry
# ${configurationKey}.git.push-changes=false
# ${configurationKey}.git.private-key-passphrase=
# ${configurationKey}.git.private-key.location=file:/tmp/privkey.pem
# ${configurationKey}.git.ssh-session-password=
# ${configurationKey}.git.timeout=PT10S
# ${configurationKey}.git.strict-host-key-checking=true
# ${configurationKey}.git.clear-existing-identities=false
```

## InfluxDb Configuration

The following options related to InfluxDb support in CAS apply equally to a number of CAS components given the component's *configuration key*:

```properties
# ${configurationKey}.url=http://localhost:8086
# ${configurationKey}.username=root
# ${configurationKey}.password=root
# ${configurationKey}.retention-policy=autogen
# ${configurationKey}.drop-database=false
# ${configurationKey}.points-to-flush=100
# ${configurationKey}.batch-interval=PT5S
# ${configurationKey}.consistency-level=ALL
```

## Apache Kafka Configuration

The following options related to Kafka support in CAS apply equally to a number of CAS components given the component's *configuration key*:

```properties
# ${configurationKey}.bootstrap-address=localhost:9092
```

### Apache Kafka Topic Configuration

The following options related to Kafka support in CAS apply equally to a number of CAS components given the component's *configuration key*:

```properties
# ${configurationKey}.name=
# ${configurationKey}.partitions=1
# ${configurationKey}.replicas=1
# ${configurationKey}.compression-type=gzip
# ${configurationKey}.config.key=value
```

## Hazelcast Configuration

The following options related to Hazelcast support in CAS apply equally to a number of CAS components given the component's *configuration key*:

```properties
# ${configurationKey}.cluster.members=123.456.789.000,123.456.789.001
# ${configurationKey}.cluster.instance-name=localhost
# ${configurationKey}.cluster.port=5701

# ${configurationKey}.license-key=
# ${configurationKey}.enable-compression=false
# ${configurationKey}.enable-management-center-scripting=true
```

More advanced Hazelcast configuration settings are listed below, given the component's *configuration key*:

```properties
# ${configurationKey}.cluster.tcpip-enabled=true

# ${configurationKey}.cluster.partition-member-group-type=HOST_AWARE|CUSTOM|PER_MEMBER|ZONE_AWARE|SPI
# ${configurationKey}.cluster.map-merge-policy=PUT_IF_ABSENT|HIGHER_HITS|DISCARD|PASS_THROUGH|EXPIRATION_TIME|LATEST_UPDATE|LATEST_ACCESS

# ${configurationKey}.cluster.eviction-policy=LRU
# ${configurationKey}.cluster.max-no-heartbeat-seconds=300
# ${configurationKey}.cluster.logging-type=slf4j
# ${configurationKey}.cluster.port-auto-increment=true
# ${configurationKey}.cluster.max-size=85
# ${configurationKey}.cluster.backup-count=1
# ${configurationKey}.cluster.async-backup-count=0
# ${configurationKey}.cluster.max-size-policy=USED_HEAP_PERCENTAGE
# ${configurationKey}.cluster.timeout=5

# ${configurationKey}.cluster.local-address=
# ${configurationKey}.cluster.public-address=

# ${configurationKey}.cluster.outbound-ports[0]=45000
```

### Static WAN Replication

```properties
# ${configurationKey}.cluster.wan-replication.enabled=false
# ${configurationKey}.cluster.wan-replication.replication-name=CAS

# ${configurationKey}.cluster.wan-replication.targets[0].endpoints=1.2.3.4,4.5.6.7
# ${configurationKey}.cluster.wan-replication.targets[0].publisher-className=com.hazelcast.enterprise.wan.replication.WanBatchReplication
# ${configurationKey}.cluster.wan-replication.targets[0].queue-full-behavior=THROW_EXCEPTION
# ${configurationKey}.cluster.wan-replication.targets[0].acknowledge-type=ACK_ON_OPERATION_COMPLETE
# ${configurationKey}.cluster.wan-replication.targets[0].queue-capacity=10000
# ${configurationKey}.cluster.wan-replication.targets[0].batch-size=500
# ${configurationKey}.cluster.wan-replication.targets[0].snapshot-enabled=false
# ${configurationKey}.cluster.wan-replication.targets[0].batch-maximum-delay-milliseconds=1000
# ${configurationKey}.cluster.wan-replication.targets[0].response-timeout-milliseconds=60000
# ${configurationKey}.cluster.wan-replication.targets[0].executor-thread-count=2

# ${configurationKey}.cluster.wan-replication.targets[0].consistency-check-strategy=NONE|MERKLE_TREES
# ${configurationKey}.cluster.wan-replication.targets[0].cluster-name=
# ${configurationKey}.cluster.wan-replication.targets[0].publisher-id=
# ${configurationKey}.cluster.wan-replication.targets[0].properties=
```

### Multicast Discovery

```properties
# ${configurationKey}.cluster.multicast-trusted-interfaces=
# ${configurationKey}.cluster.multicast-enabled=false
# ${configurationKey}.cluster.multicast-port=
# ${configurationKey}.cluster.multicast-group=
# ${configurationKey}.cluster.multicast-timeout=2
# ${configurationKey}.cluster.multicast-time-to-live=32
```

### AWS EC2 Discovery

```properties
# ${configurationKey}.cluster.discovery.enabled=true

# ${configurationKey}.cluster.discovery.aws.access-ley=
# ${configurationKey}.cluster.discovery.aws.secret-ley=

# ${configurationKey}.cluster.discovery.aws.iam-role=

# ${configurationKey}.cluster.discovery.aws.region=us-east-1
# ${configurationKey}.cluster.discovery.aws.host-header=
# ${configurationKey}.cluster.discovery.aws.security-group-name=
# ${configurationKey}.cluster.discovery.aws.tag-key=
# ${configurationKey}.cluster.discovery.aws.tag-value=
# ${configurationKey}.cluster.discovery.aws.port=-1
# ${configurationKey}.cluster.discovery.aws.connection-timeout-seconds=5
```

### Apache jclouds Discovery

```properties
# ${configurationKey}.cluster.discovery.enabled=true

# ${configurationKey}.cluster.discovery.jclouds.provider=
# ${configurationKey}.cluster.discovery.jclouds.identity=
# ${configurationKey}.cluster.discovery.jclouds.credential=
# ${configurationKey}.cluster.discovery.jclouds.endpoint=
# ${configurationKey}.cluster.discovery.jclouds.zones=
# ${configurationKey}.cluster.discovery.jclouds.regions=
# ${configurationKey}.cluster.discovery.jclouds.tag-keys=
# ${configurationKey}.cluster.discovery.jclouds.tag-values=
# ${configurationKey}.cluster.discovery.jclouds.group=
# ${configurationKey}.cluster.discovery.jclouds.port=-1
# ${configurationKey}.cluster.discovery.jclouds.role-name=
# ${configurationKey}.cluster.discovery.jclouds.credential-path=
```

### Kubernetes Discovery

```properties
# ${configurationKey}.cluster.discovery.enabled=true

# ${configurationKey}.service-dns=
# ${configurationKey}.service-dns-timeout=-1
# ${configurationKey}.service-name=
# ${configurationKey}.service-label-name=
# ${configurationKey}.service-label-value=
# ${configurationKey}.cluster.discovery.kubernetes.namespace=
# ${configurationKey}.resolve-not-ready-addresses=false
# ${configurationKey}.cluster.discovery.kubernetes.kubernetes-master=
# ${configurationKey}.api-token=
```

### Docker Swarm Discovery

```properties
# ${configurationKey}.cluster.discovery.enabled=true

# ${configurationKey}.cluster.discovery.docker-swarm.dns-provider.enabled=true
# ${configurationKey}.cluster.discovery.docker-swarm.dns-provider.service-name=
# ${configurationKey}.cluster.discovery.docker-swarm.dns-provider.service-port=5701
# ${configurationKey}.cluster.discovery.docker-swarm.dns-provider.peer-services=service-a,service-b,etc

# ${configurationKey}.cluster.discovery.docker-swarm.member-provider.enabled=true
# ${configurationKey}.cluster.discovery.docker-swarm.member-provider.group-name=
# ${configurationKey}.cluster.discovery.docker-swarm.member-provider.group-password=
# ${configurationKey}.cluster.discovery.docker-swarm.member-provider.docker-network-names=
# ${configurationKey}.cluster.discovery.docker-swarm.member-provider.docker-service-names=
# ${configurationKey}.cluster.discovery.docker-swarm.member-provider.docker-service-labels=
# ${configurationKey}.cluster.discovery.docker-swarm.member-provider.swarm-mgr-uri=
# ${configurationKey}.cluster.discovery.docker-swarm.member-provider.skip-verify-ssl=false
# ${configurationKey}.cluster.discovery.docker-swarm.member-provider.hazelcast-peer-port=5701
```

### Microsoft Azure Discovery

```properties
# ${configurationKey}.cluster.discovery.enabled=true

# ${configurationKey}.cluster.discovery.azure.subscription-id=
# ${configurationKey}.cluster.discovery.azure.client-id=
# ${configurationKey}.cluster.discovery.azure.client-secret=
# ${configurationKey}.cluster.discovery.azure.tenant-id=
# ${configurationKey}.cluster.discovery.azure.cluster-id=
# ${configurationKey}.cluster.discovery.azure.group-name=
```

## RADIUS Configuration

The following options related to RADIUS support in CAS apply equally to a number of CAS components (authentication, etc) 
given the component's *configuration key*.

`server` parameters defines identification values of authenticated service (CAS server), primarily `server.protocol`
 for communication to RADIUS server identified by `client`.

`client` parameters defines values for connecting RADIUS server. 
Parameter `client.inetAddress` has possibility to contain more addresses separated by comma to define failover servers 
when `failoverOnException` is set.   

```properties
# ${configurationKey}.server.nas-port-id=-1
# ${configurationKey}.server.nas-real-port=-1
# ${configurationKey}.server.protocol=EAP_MSCHAPv2
# ${configurationKey}.server.retries=3
# ${configurationKey}.server.nas-port-type=-1
# ${configurationKey}.server.nas-port=-1
# ${configurationKey}.server.nas-ip-address=
# ${configurationKey}.server.nas-ipv6-address=
# ${configurationKey}.server.nas-identifier=-1
# ${configurationKey}.client.authentication-port=1812
# ${configurationKey}.client.shared-secret=N0Sh@ar3d$ecReT
# ${configurationKey}.client.socket-timeout=0
# ${configurationKey}.client.inet-address=localhost
# ${configurationKey}.client.accounting-port=1813
# ${configurationKey}.failover-on-exception=false
# ${configurationKey}.failover-on-authentication-failure=false
```

## CouchDb Configuration

The following options related to CouchDb support in CAS apply equally to a number of CAS components (ticket registries, etc) given the component's *configuration key*:

```properties
# ${configurationKey}.couch-db.url=http://localhost:5984
# ${configurationKey}.couch-db.username=
# ${configurationKey}.couch-db.password=
# ${configurationKey}.couch-db.socket-timeout=10000
# ${configurationKey}.couch-db.connection-timeout=1000
# ${configurationKey}.couch-db.drop-collection=false
# ${configurationKey}.couch-db.max-connections=20
# ${configurationKey}.couch-db.enable-ssl=
# ${configurationKey}.couch-db.relaxed-ssl-settings=
# ${configurationKey}.couch-db.caching=false
# ${configurationKey}.couch-db.max-cache-entries=1000
# ${configurationKey}.couch-db.max-object-size-bytes=8192
# ${configurationKey}.couch-db.use-expect-continue=true
# ${configurationKey}.couch-db.cleanup-idle-connections=true
# ${configurationKey}.couch-db.create-if-not-exists=true
# ${configurationKey}.couch-db.proxy-host=
# ${configurationKey}.couch-db.proxy-port=-1

# Defaults are based on the feature name.
# ${configurationKey}.couch-db.db-name=

# For the few features that can't have update conflicts automatically resolved.
# ${configurationKey}.couch-db.retries=5

# Depending on the feature at hand, CAS may perform some actions asynchronously.
# ${configurationKey}.couch-db.asynchronous=true
```

## MongoDb Configuration

The following options related to MongoDb support in CAS apply equally to a number of CAS components (ticket registries, etc) given the component's *configuration key*:

```properties
# ${configurationKey}.mongo.host=localhost
# ${configurationKey}.mongo.client-uri=localhost
# ${configurationKey}.mongo.port=27017
# ${configurationKey}.mongo.drop-collection=false
# ${configurationKey}.mongo.socket-keep-alive=false
# ${configurationKey}.mongo.password=

# ${configurationKey}.mongo.collection=cas-service-registry

# ${configurationKey}.mongo.database-name=cas-mongo-database
# ${configurationKey}.mongo.timeout=5000
# ${configurationKey}.mongo.user-id=
# ${configurationKey}.mongo.write-concern=NORMAL
# ${configurationKey}.mongo.read-concern=AVAILABLE
# ${configurationKey}.mongo.read-preference=PRIMARY
# ${configurationKey}.mongo.authentication-database-name=
# ${configurationKey}.mongo.replica-set=
# ${configurationKey}.mongo.ssl-enabled=false
# ${configurationKey}.mongo.retry-writes=false

# ${configurationKey}.mongo.pool.life-time=60000
# ${configurationKey}.mongo.pool.idle-time=30000
# ${configurationKey}.mongo.pool.max-wait-time=60000
# ${configurationKey}.mongo.pool.max-size=10
# ${configurationKey}.mongo.pool.min-size=1
# ${configurationKey}.mongo.pool.per-host=10
```

## DynamoDb Configuration

The following options related to DynamoDb support in CAS apply equally to a number of CAS components (ticket registries, etc) given the component's *configuration key*:

```properties
# ${configurationKey}.dynamo-db.drop-tables-on-startup=false
# ${configurationKey}.dynamo-db.prevent-table-creation-on-startup=false
# ${configurationKey}.dynamo-db.local-instance=false
```
    
AWS settings for this feature are available [here](#amazon-integration-settings).

## RESTful Integrations

The following options related to features in CAS that provide REST support to fetch and update data. These settings apply equally, given the component's *configuration key*:

```properties
# ${configurationKey}.method=GET|POST
# ${configurationKey}.order=0
# ${configurationKey}.case-insensitive=false
# ${configurationKey}.basic-auth-username=uid
# ${configurationKey}.basic-auth-password=password
# ${configurationKey}.headers.key=value
# ${configurationKey}.url=https://rest.somewhere.org/attributes
```

## Redis Configuration

The following options related to Redis support in CAS apply equally to a number of CAS components (ticket registries, etc) given the component's *configuration key*:

```properties
# ${configurationKey}.redis.host=localhost
# ${configurationKey}.redis.database=0
# ${configurationKey}.redis.port=6380
# ${configurationKey}.redis.password=
# ${configurationKey}.redis.timeout=2000
# ${configurationKey}.redis.use-ssl=false
# ${configurationKey}.redis.read-from=MASTER
```

### Redis Pool Configuration

```properties
# ${configurationKey}.redis.pool.enabled=false
# ${configurationKey}.redis.pool.max-active=20
# ${configurationKey}.redis.pool.max-idle=8
# ${configurationKey}.redis.pool.min-idle=0
# ${configurationKey}.redis.pool.max-active=8
# ${configurationKey}.redis.pool.max-wait=-1
# ${configurationKey}.redis.pool.num-tests-per-eviction-run=0
# ${configurationKey}.redis.pool.soft-min-evictable-idle-time-millis=0
# ${configurationKey}.redis.pool.min-evictable-idle-time-millis=0
# ${configurationKey}.redis.pool.lifo=true
# ${configurationKey}.redis.pool.fairness=false
# ${configurationKey}.redis.pool.test-on-create=false
# ${configurationKey}.redis.pool.test-on-borrow=false
# ${configurationKey}.redis.pool.test-on-return=false
# ${configurationKey}.redis.pool.test-while-idle=false
```

### Redis Sentinel Configuration

```properties
# ${configurationKey}.redis.sentinel.master=mymaster
# ${configurationKey}.redis.sentinel.node[0]=localhost:26377
# ${configurationKey}.redis.sentinel.node[1]=localhost:26378
# ${configurationKey}.redis.sentinel.node[2]=localhost:26379
```

### Redis Cluster Configuration

```properties
# ${configurationKey}.redis.cluster.password=
# ${configurationKey}.redis.cluster.max-redirects=0
# ${configurationKey}.redis.cluster.nodes[0].host=
# ${configurationKey}.redis.cluster.nodes[0].port=
# ${configurationKey}.redis.cluster.nodes[0].replica-of=
# ${configurationKey}.redis.cluster.nodes[0].id=
# ${configurationKey}.redis.cluster.nodes[0].name=
# ${configurationKey}.redis.cluster.nodes[0].type=MASTER|SLAVE
```

## DDL Configuration

Note that the default value for Hibernate's DDL setting is `create-drop` which may not be appropriate for use in production. Setting the value to
`validate` may be more desirable, but any of the following options can be used:

| Type                 | Description
|----------------------|----------------------------------------------------------
| `validate`           | Validate the schema, but make no changes to the database.
| `update`             | Update the schema.
| `create`             | Create the schema, destroying previous data.
| `create-drop`        | Drop the schema at the end of the session.
| `none`               | Do nothing.

Note that during a version migration where any schema has changed `create-drop` will result
in the loss of all data as soon as CAS is started. For transient data like tickets this is probably
not an issue, but in cases like the audit table important data could be lost. Using `update`, while safe
for data, is confirmed to result in invalid database state. `validate` or `none` settings
are likely the only safe options for production use.

For more information on configuration of transaction levels and propagation behaviors,
please review [this guide](http://docs.spring.io/spring-framework/docs/current/javadoc-api/).

## SAML2 Service Provider Integrations

The settings defined for each service provider simply attempt to automate the creation of 
a [SAML service definition](../installation/Configuring-SAML2-Authentication.html#saml-services) and nothing more. If you find the 
applicable settings lack in certain areas, it is best to fall back onto the native configuration strategy for registering 
SAML service providers with CAS which would depend on your service registry of choice.

Each SAML service provider supports the following settings:

| Name                  |  Description
|-----------------------|---------------------------------------------------------------------------
| `metadata`            | Location of metadata for the service provider (i.e URL, path, etc)
| `name`                | The name of the service provider registered in the service registry.
| `description`         | The description of the service provider registered in the service registry.
| `nameIdAttribute`     | Attribute to use when generating name ids for this service provider.
| `nameIdFormat`        | The forced NameID Format identifier (i.e. `urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress`).
| `attributes`          | Attributes to release to the service provider, which may virtually be mapped and renamed.
| `signatureLocation`   | Signature location to verify metadata.
| `entityIds`           | List of entity ids allowed for this service provider.
| `signResponses`       | Indicate whether responses should be signed. Default is `true`.
| `signAssertions`      | Indicate whether assertions should be signed. Default is `false`.

The only required setting that would activate the automatic configuration for a service provider is the presence and definition of metadata. All other settings are optional. 

The following options apply equally to SAML2 service provider integrations, given the provider's *configuration key*:

```properties
# ${configurationKey}.metadata=/etc/cas/saml/dropbox.xml
# ${configurationKey}.name=SP Name
# ${configurationKey}.description=SP Integration
# ${configurationKey}.name-id-attribute=mail
# ${configurationKey}.name-id-format=
# ${configurationKey}.signature-location=
# ${configurationKey}.attributes=
# ${configurationKey}.entity-ids=
# ${configurationKey}.sign-responses=
# ${configurationKey}.sign-assertions=
```

## Multifactor Authentication Providers

All configurable multifactor authentication providers have these base properties available given the provider's *configuration key*:

```properties
# ${configurationKey}.rank=
# ${configurationKey}.id=
# ${configurationKey}.name=
# ${configurationKey}.failure-mode=UNDEFINED
```

## Multifactor Authentication Bypass

The following bypass options apply equally to multifactor authentication providers given the provider's *configuration key*:

```properties
# ${configurationKey}.bypass.principal-attribute-name=bypass|skip
# ${configurationKey}.bypass.principal-attribute-value=true|enabled.+

# ${configurationKey}.bypass.authentication-attribute-name=bypass|skip
# ${configurationKey}.bypass.authentication-attribute-value=allowed.+|enabled.+

# ${configurationKey}.bypass.authentication-handler-name=AcceptUsers.+
# ${configurationKey}.bypass.authentication-method-name=LdapAuthentication.+

# ${configurationKey}.bypass.credential-class-type=UsernamePassword.+

# ${configurationKey}.bypass.http-request-remote-address=127.+|example.*
# ${configurationKey}.bypass.http-request-headers=header-X-.+|header-Y-.+

# ${configurationKey}.bypass.groovy.location=file:/etc/cas/config/mfa-bypass.groovy
```

If multifactor authentication bypass is determined via REST, 
RESTful settings are available [here](#restful-integrations) under the configuration key `${configurationKey}.bypass.rest`.

## Couchbase Integration Settings

The following options are shared and apply when CAS is configured to integrate with Couchbase (i.e ticket registry, etc), given the provider's *configuration key*:

```properties
# ${configurationKey}.addresses[0]=localhost
# ${configurationKey}.cluster-username=
# ${configurationKey}.cluster-password= 

# ${configurationKey}.bucket=testbucket    

# ${configurationKey}.connection-timeout=PT60S
# ${configurationKey}.search-timeout=PT30S
# ${configurationKey}.query-timeout=PT30S
# ${configurationKey}.view-timeout=PT30S
# ${configurationKey}.kv-timeout=PT30S 
# ${configurationKey}.max-http-connections=PT30S
# ${configurationKey}.idle-connection-timeout=PT30S
# ${configurationKey}.query-threshold=PT30S
# ${configurationKey}.scan-consistency=NOT_BOUNDED|REQUEST_PLUS
```

## Amazon Integration Settings

The following options are shared and apply when CAS is configured to integrate with various 
Amazon Web Service features, given the provider's *configuration key*:

```properties
# ${configurationKey}.credential-access-key=
# ${configurationKey}.credential-secret-key=

# ${configurationKey}.endpoint=http://localhost:8000
# ${configurationKey}.region=US_WEST_2|US_EAST_2|EU_WEST_2|<REGION-NAME>
# ${configurationKey}.local-address=
# ${configurationKey}.retry-mode=STANDARD|LEGACY

# ${configurationKey}.proxy-host=
# ${configurationKey}.proxy-password=
# ${configurationKey}.proxy-username=

# ${configurationKey}.read-capacity=10
# ${configurationKey}.write-capacity=10
# ${configurationKey}.connection-timeout=5000
# ${configurationKey}.socket-timeout=5000
# ${configurationKey}.use-reaper=false

# ${configurationKey}.client-execution-timeout=10000
# ${configurationKey}.max-connections=10
```

## Memcached Integration Settings

The following  options are shared and apply when CAS is configured to integrate with memcached (i.e ticket registry, etc), given the provider's *configuration key*:

```properties
# ${configurationKey}.memcached.servers=localhost:11211
# ${configurationKey}.memcached.locator-type=ARRAY_MOD
# ${configurationKey}.memcached.failure-mode=Redistribute
# ${configurationKey}.memcached.hash-algorithm=FNV1_64_HASH
# ${configurationKey}.memcached.protocol=TEXT
# ${configurationKey}.memcached.should-optimize=false
# ${configurationKey}.memcached.daemon=true
# ${configurationKey}.memcached.max-reconnect-delay=-1
# ${configurationKey}.memcached.use-nagle-algorithm=false
# ${configurationKey}.memcached.shutdown-timeout-seconds=-1
# ${configurationKey}.memcached.op-timeout=-1
# ${configurationKey}.memcached.timeout-exception-threshold=2
# ${configurationKey}.memcached.max-total=20
# ${configurationKey}.memcached.max-idle=8
# ${configurationKey}.memcached.min-idle=0

# ${configurationKey}.memcached.transcoder=KRYO|SERIAL|WHALIN|WHALINV1
# ${configurationKey}.memcached.transcoder-compression-threshold=16384
# ${configurationKey}.memcached.kryo-auto-reset=false
# ${configurationKey}.memcached.kryo-objects-by-reference=false
# ${configurationKey}.memcached.kryo-registration-required=false
```

## Password Policy Settings

The following  options are shared and apply when CAS is configured to integrate with account sources and authentication strategies that support password policy enforcement and detection, given the provider's *configuration key*. Note that certain setting may only be applicable if the underlying account source is LDAP and are only taken into account if the authentication strategy configured in CAS is able to honor and recognize them: 

```properties
# ${configurationKey}.type=GENERIC|AD|FreeIPA|EDirectory

# ${configurationKey}.enabled=true
# ${configurationKey}.policy-attributes.account-locked=javax.security.auth.login.AccountLockedException
# ${configurationKey}.login-failures=5
# ${configurationKey}.warning-attribute-value=
# ${configurationKey}.warning-attribute-name=
# ${configurationKey}.display-warning-on-match=true
# ${configurationKey}.warn-all=true
# ${configurationKey}.warning-days=30
# ${configurationKey}.account-state-handling-enabled=true

# An implementation of `org.ldaptive.auth.AuthenticationResponseHandler`
# ${configurationKey}.custom-policy-class=com.example.MyAuthenticationResponseHandler

# ${configurationKey}.strategy=DEFAULT|GROOVY|REJECT_RESULT_CODE
# ${configurationKey}.groovy.location=file:/etc/cas/config/password-policy.groovy
```

#### Password Policy Strategies

Password policy strategy types are outlined below. The strategy evaluates the authentication response received from LDAP, etc and is allowed to review it upfront in order to further examine whether account state, messages and warnings is eligible for further investigation.

| Option        | Description
|---------------|-----------------------------------------------------------------------------
| `DEFAULT`     | Accepts the authentication response as is, and processes account state, if any.
| `GROOVY`      | Examine the authentication response as part of a Groovy script dynamically. The responsibility of handling account state changes and warnings is entirely delegated to the script.
| `REJECT_RESULT_CODE`  | An extension of the `DEFAULT` where account state is processed only if the result code of the authentication response is not denied in the configuration. By default `INVALID_CREDENTIALS(49)` prevents CAS from handling account states.

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
    def applicationContext = args[3]

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

## Email Notifications

To learn more about this topic, [please review this guide](../notifications/Sending-Email-Configuration.html).

The following options are shared and apply when CAS is configured to send email notifications, given the provider's *configuration key*:

```properties
# ${configurationKey}.mail.from=
# ${configurationKey}.mail.text=
# ${configurationKey}.mail.subject=
# ${configurationKey}.mail.cc=
# ${configurationKey}.mail.bcc=
# ${configurationKey}.mail.reply-to=
# ${configurationKey}.mail.validate-addresses=false
# ${configurationKey}.mail.html=false

# ${configurationKey}.mail.attribute-name=mail
```

The following settings may also need to be defined to describe the mail server settings:

```properties
# spring.mail.host=
# spring.mail.port=
# spring.mail.username=
# spring.mail.password=
# spring.mail.properties.mail.smtp.auth=true
# spring.mail.properties.mail.smtp.starttls.enable=true
```

## SMS Notifications
 
The following options are shared and apply when CAS is configured to send SMS notifications, given the provider's *configuration key*:
 
```properties
# ${configurationKey}.sms.from=
# ${configurationKey}.sms.text=
# ${configurationKey}.sms.attribute-name=phone
```

You will also need to ensure a provider is defined that is able to send SMS messages. To learn more about this 
topic, [please review this guide](../notifications/SMS-Messaging-Configuration.html).

## Webflow Auto Configuration

Control aspects of webflow that relate to auto-configuration of webflow states, transitions and execution order.

```properties
# ${configurationKey}.order=
``` 

## Delegated Authentication Settings

The following options are shared and apply when CAS is configured to delegate authentication 
to an external provider such as Yahoo, given the provider's *configuration key*:

```properties
# ${configurationKey}.id=
# ${configurationKey}.secret=
# ${configurationKey}.client-name=My Provider
# ${configurationKey}.auto-redirect=false
# ${configurationKey}.css-class=
# ${configurationKey}.principal-attribute-id=
# ${configurationKey}.enabled=true
# ${configurationKey}.callback-url-type=PATH_PARAMETER|QUERY_PARAMETER|NONE

```

The following types are supported with callback URL resolution:

| Type               | Description              
|--------------------|--------------------------------------------------------------------------------------
| `PATH_PARAMETER`   | When constructing a callback URL, client name is added to the url as a path parameter.
| `QUERY_PARAMETER`  | When constructing a callback URL, client name is added to the url as a query parameter.
| `NONE`             | No client name is added to the url.

### Delegated Authentication OpenID Connect Settings

The following options are shared and apply when CAS is configured to delegate authentication 
to an external OpenID Connect provider such as Azure AD, given the provider's *configuration key*:

```properties
# ${configurationKey}.discovery-uri=
# ${configurationKey}.logout-url=
# ${configurationKey}.max-clock-skew=
# ${configurationKey}.scope=
# ${configurationKey}.use-nonce=false
# ${configurationKey}.disable-nonce=false
# ${configurationKey}.preferred-jws-algorithm=
# ${configurationKey}.response-mode=
# ${configurationKey}.response-type=
# ${configurationKey}.custom-params.param1=value1
# ${configurationKey}.read-timeout=PT5S
# ${configurationKey}.connect-timeout=PT5S
# ${configurationKey}.expire-session-with-token=false
# ${configurationKey}.token-expiration-advance=0
```

## LDAP Connection Settings

The following  options apply  to features that integrate with an LDAP server (i.e. authentication, attribute resolution, etc) given the provider's *configuration key*:

```properties
# ${configurationKey}.ldap-url=ldaps://ldap1.example.edu ldaps://ldap2.example.edu
# ${configurationKey}.bind-dn=cn=-directory -manager,dc=example,dc=org
# ${configurationKey}.bind-credential=Password

# ${configurationKey}.pool-passivator=NONE|BIND
# ${configurationKey}.connection-strategy=
# ${configurationKey}.connect-timeout=PT5S
# ${configurationKey}.trust-certificates=
# ${configurationKey}.trust-store=
# ${configurationKey}.trust-store-password=
# ${configurationKey}.trust-store-type=JKS|JCEKS|PKCS12
# ${configurationKey}.keystore=
# ${configurationKey}.keystore-password=
# ${configurationKey}.keystore-type=JKS|JCEKS|PKCS12
# ${configurationKey}.disable-pooling=false
# ${configurationKey}.min-pool-size=3
# ${configurationKey}.max-pool-size=10
# ${configurationKey}.validate-on-checkout=true
# ${configurationKey}.validate-periodically=true
# ${configurationKey}.validate-period=PT5M
# ${configurationKey}.validate-timeout=PT5S
# ${configurationKey}.fail-fast=true
# ${configurationKey}.idle-time=PT10M
# ${configurationKey}.prune-period=PT2H
# ${configurationKey}.block-wait-time=PT3S

# ${configurationKey}.use-start-tls=false
# ${configurationKey}.response-timeout=PT5S
# ${configurationKey}.allow-multiple-dns=false
# ${configurationKey}.allow-multiple-entries=false
# ${configurationKey}.follow-referrals=false
# ${configurationKey}.binary-attributes=objectGUID,someOtherAttribute
# ${configurationKey}.name=
```

### Connection Initialization

LDAP connection configuration injected into the LDAP connection pool can be initialized with the following parameters:

| Behavior                               | Description              
|----------------------------------------|-------------------------------------------------------------------
| `bindDn`/`bindCredential` provided     | Use the provided credentials to bind when initializing connections.
| `bindDn`/`bindCredential` set to `*`   | Use a fast-bind strategy to initialize the pool.   
| `bindDn`/`bindCredential` set to blank | Skip connection initializing; perform operations anonymously.
| SASL mechanism provided                | Use the given SASL mechanism to bind when initializing connections.

### Passivators

The following options can be used to passivate objects when they are checked back into the LDAP connection pool:

| Type                    | Description
|-------------------------|----------------------------------------------------------------------------------------------------
| `NONE`                  | No passivation takes place.
| `BIND`                  | The default behavior which passivates a connection by performing a bind operation on it. This option requires the availability of bind credentials when establishing connections to LDAP.

#### Why Passivators?

You may receive unexpected LDAP failures, when CAS is configured to authenticate using `DIRECT` or `AUTHENTICATED` types and LDAP is locked down to not allow anonymous binds/searches. Every second attempt with a given LDAP connection from the pool would fail if it was on the same connection as a failed login attempt, and the regular connection validator would similarly fail. When a connection is returned back to a pool, it still may contain the principal and credentials from the previous attempt. Before the next bind attempt using that connection, the validator tries to validate the connection again but fails because it's no longer trying with the configured bind credentials but with whatever user DN was used in the previous step. Given the validation failure, the connection is closed and CAS would deny access by default. Passivators attempt to reconnect to LDAP with the configured bind credentials, effectively resetting the connection to what it should be after each bind request.

Furthermore if you are seeing errors in the logs that resemble a *<Operation exception encountered, reopening connection>* type of message, this usually is an indication that the connection pool's validation timeout established and created by CAS is greater than the timeout configured in the LDAP server, or more likely, in the load balancer in front of the LDAP servers. You can adjust the LDAP server session's timeout for connections, or you can teach CAS to use a validity period that is equal or less than the LDAP server session's timeout.

### Connection Strategies

If multiple URLs are provided as the LDAP url, this describes how each URL will be processed.

| Provider              | Description              
|-----------------------|-----------------------------------------------------------------------------------------------
| `ACTIVE_PASSIVE`      | First LDAP will be used for every request unless it fails and then the next shall be used.    
| `ROUND_ROBIN`         | For each new connection the next url in the list will be used.      
| `RANDOM`              | For each new connection a random LDAP url will be selected.
| `DNS_SRV`             | LDAP urls based on DNS SRV records of the configured/given LDAP url will be used.  

### LDAP SASL Mechanisms

```properties
# ${configurationKey}.sasl-mechanism=GSSAPI|DIGEST_MD5|CRAM_MD5|EXTERNAL
# ${configurationKey}.sasl-realm=EXAMPLE.COM
# ${configurationKey}.sasl-authorization-id=
# ${configurationKey}.sasl-mutual-auth=
# ${configurationKey}.sasl-quality-of-protection=
# ${configurationKey}.sasl-security-strength=

```

### LDAP Connection Validators

The following LDAP validators can be used to test connection health status:

| Type                    | Description
|-------------------------|----------------------------------------------------------------------------------------------------
| `NONE`                  | No validation takes place.
| `SEARCH`                | Validates a connection is healthy by performing a search operation. Validation is considered successful if the search result size is greater than zero.
| `COMPARE`               | Validates a connection is healthy by performing a compare operation.

```properties
# ${configurationKey}.validator.type=NONE|SEARCH|COMPARE
# ${configurationKey}.validator.base-dn=
# ${configurationKey}.validator.search-filter=(object-class=*)
# ${configurationKey}.validator.scope=OBJECT|ONELEVEL|SUBTREE
# ${configurationKey}.validator.attribute-name=objectClass
# ${configurationKey}.validator.attribute-value=top
# ${configurationKey}.validator.dn=

```

### LDAP SSL Hostname Verification

The following LDAP validators can be used to test connection health status:

| Type                    | Description
|-------------------------|------------------------------------
| `DEFAULT`               | Default option to enable and force hostname verification of the LDAP SSL configuration.
| `ANY`                   | Skip and ignore the hostname verification of the LDAP SSL configuration.

```properties
#${configurationKey}.hostname-verifier=DEFAULT|ANY
```

### LDAP SSL Trust Managers

Trust managers are responsible for managing the trust material that is used when making LDAP trust decisions, 
and for deciding whether credentials presented by a peer should be accepted.

| Type                    | Description
|-------------------------|---------------------------------------------------------------------------------------------
| `DEFAULT`               | Enable and force the default JVM trust managers.
| `ANY`                   | Trust any client or server.

```properties
#${configurationKey}.trust-manager=DEFAULT|ANY
```

### LDAP Types

A number of components/features in CAS allow you to explicitly indicate a `type` for the LDAP server, specially in cases where CAS needs to update an attribute, etc in LDAP (i.e. consent, password management, etc). The relevant setting would be:

```properties
#${configurationKey}.type=AD|FreeIPA|EDirectory|Generic
```

The following types are supported:

| Type                    | Description
|-------------------------|--------------------------------------------------
| `AD`                                                     | Active Directory.
| `FreeIPA`                                    | FreeIPA Directory Server.
| `EDirectory`                         | NetIQ eDirectory.
| `GENERIC`                              | All other directory servers (i.e OpenLDAP, etc).

### LDAP Authentication/Search Settings

In addition to common LDAP connection settings above, there are cases where CAS simply need to execute 
authenticate against an LDAP server to fetch an account or set of attributes or execute a search query in general. 
The following  options apply  given the provider's *configuration key*:

**Note:** Failure to specify adequate properties such as `type`, `ldapUrl`, etc will simply deactivate LDAP  altogether silently.

```properties
# ${configurationKey}.type=AD|AUTHENTICATED|DIRECT|ANONYMOUS

# ${configurationKey}.base-dn=dc=example,dc=org
# ${configurationKey}.subtree-search=true
# ${configurationKey}.search-filter=cn={user}
# ${configurationKey}.page-size=0

# ${configurationKey}.enhance-with-entry-resolver=true
# ${configurationKey}.deref-aliases=NEVER|SEARCHING|FINDING|ALWAYS
# ${configurationKey}.dn-format=uid=%s,ou=people,dc=example,dc=org
# ${configurationKey}.principal-attribute-password=password

```

The following authentication types are supported:

| Type                    | Description                            
|-------------------------|----------------------------------------------------------------------------------------------------
| `AD`                    | Active Directory - Users authenticate with `sAMAccountName` typically using a DN format.     
| `AUTHENTICATED`         | Manager bind/search type of authentication. If `principalAttributePassword` is empty then a user simple bind is done to validate credentials. Otherwise the given attribute is compared with the given `principalAttributePassword` using the `SHA` encrypted value of it.
| `DIRECT`                | Compute user DN from a format string and perform simple bind. This is relevant when no search is required to compute the DN needed for a bind operation. This option is useful when all users are under a single branch in the directory, e.g. `ou=Users,dc=example,dc=org`, or the username provided on the CAS login form is part of the DN, e.g. `uid=%s,ou=Users,dc=exmaple,dc=org`
| `ANONYMOUS`             | Similar semantics as `AUTHENTICATED` except no `bindDn` and `bindCredential` may be specified to initialize the connection. If `principalAttributePassword` is empty then a user simple bind is done to validate credentials. Otherwise the given attribute is compared with the given `principalAttributePassword` using the `SHA` encrypted value of it.

### LDAP Search Entry Handlers

```properties
# ${configurationKey}.search-entry-handlers[0].type=

# ${configurationKey}.search-entry-handlers[0].case-change.dn-case-change=NONE|LOWER|UPPER
# ${configurationKey}.search-entry-handlers[0].case-change.attribute-name-case-change=NONE|LOWER|UPPER
# ${configurationKey}.search-entry-handlers[0].case-change.attribute-value-case-change=NONE|LOWER|UPPER
# ${configurationKey}.search-entry-handlers[0].case-change.attribute-names=

# ${configurationKey}.search-entry-handlers[0].dn-attribute.dn-attribute-name=entryDN
# ${configurationKey}.search-entry-handlers[0].dn-attribute.add-if-exists=false

# ${configurationKey}.search-entry-handlers[0].primary-group-id.group-filter=(&(object-class=group)(object-sid={0}))
# ${configurationKey}.search-entry-handlers[0].primary-group-id.base-dn=

# ${configurationKey}.search-entry-handlers[0].merge-attribute.merge-attribute-name=
# ${configurationKey}.search-entry-handlers[0].merge-attribute.attribute-names=

# ${configurationKey}.search-entry-handlers[0].recursive.search-attribute=
# ${configurationKey}.search-entry-handlers[0].recursive.merge-attributes=

```

The following types are supported:

| Type                    | Description                            
|-------------------------|----------------------------------------------------------------------------------------------------
| `CASE_CHANGE` | Provides the ability to modify the case of search entry DNs, attribute names, and attribute values.
| `DN_ATTRIBUTE_ENTRY` | Adds the entry DN as an attribute to the result set. Provides a client side implementation of RFC 5020.
| `MERGE` | Merges the values of one or more attributes into a single attribute.
| `OBJECT_GUID` | Handles the `objectGUID` attribute fetching and conversion. 
| `OBJECT_SID` | Handles the `objectSid` attribute fetching and conversion. 
| `PRIMARY_GROUP` | Constructs the primary group SID and then searches for that group and puts it's DN in the 'memberOf' attribute of the original search entry. 
| `RANGE_ENTRY` |  Rewrites attributes returned from Active Directory to include all values by performing additional searches.
| `RECURSIVE_ENTRY` | This recursively searches based on a supplied attribute and merges those results into the original entry.

### LDAP Multiple Base DNs

There may be scenarios where different parts of a single LDAP tree could be considered as base-dns. Rather than duplicating 
the LDAP configuration block for each individual base-dn, each entry can be specified and joined together using a special delimiter character.
The user DN is retrieved using the combination of all base-dn and DN resolvers in the order defined. DN resolution should fail if multiple DNs 
are found. Otherwise the first DN found is returned.

```properties
# ${configurationKey}.base-dn=subtreeA,dc=example,dc=net|subtreeC,dc=example,dc=net
```
