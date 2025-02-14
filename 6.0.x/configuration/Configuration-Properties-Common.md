---
layout: default
title: CAS Common Properties Overview
category: Configuration
---

# CAS Common Properties

This document describes a number of suggestions and configuration options that apply to and are common amongst a selection of CAS modules and features. 
To see the full list of CAS properties, please [review this guide](Configuration-Properties.html).

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

If you are unsure about the meaning of a given CAS setting, do **NOT** simply turn it on without hesitation.
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
password encoding. Most options are based on Spring Security's [support for password encoding](http://docs.spring.io/spring-security/site/docs/current/apidocs/org/springframework/security/crypto/password/PasswordEncoder.html).

The following options related to password encoding support in CAS apply equally to a number of CAS components (authentication handlers, etc) given the component's *configuration key*:

```properties
# ${configurationKey}.passwordEncoder.type=NONE|DEFAULT|STANDARD|BCRYPT|SCRYPT|PBKDF2
# ${configurationKey}.passwordEncoder.characterEncoding=
# ${configurationKey}.passwordEncoder.encodingAlgorithm=
# ${configurationKey}.passwordEncoder.secret=
# ${configurationKey}.passwordEncoder.strength=16
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
| `GLIBC_CRYPT`           | Use the `GlibcCryptPasswordEncoder` based on the [`encodingAlgorithm`](https://commons.apache.org/proper/commons-codec/archives/1.10/apidocs/org/apache/commons/codec/digest/Crypt.html), `strength` provided and an optional `secret`.
| `org.example.MyEncoder` | An implementation of `PasswordEncoder` of your own choosing.
| `file:///path/to/script.groovy` | Path to a Groovy script charged with handling password encoding operations.

In cases where you plan to design your own password encoder or write scripts to do so, you may also need to ensure the overlay has the following modules available at runtime:

```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-core</artifactId>
</dependency>
```

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
    return providedUsername.concat("SomethingElse)
}
```

The following options related to principal transformation support in CAS apply equally to a number of CAS components (authentication handlers, etc) given the component's *configuration key*:

```properties
# ${configurationKey}.principalTransformation.pattern=(.+)@example.org
# ${configurationKey}.principalTransformation.groovy.location=file:///etc/cas/config/principal.groovy
# ${configurationKey}.principalTransformation.suffix=
# ${configurationKey}.principalTransformation.caseConversion=NONE|UPPERCASE|LOWERCASE
# ${configurationKey}.principalTransformation.prefix=
```

## Cookie Properties

The following common properties configure cookie generator support in CAS.

```properties
# ${configurationKey}.name=
# ${configurationKey}.domain=
# ${configurationKey}.path=
# ${configurationKey}.httpOnly=true
# ${configurationKey}.secure=true
# ${configurationKey}.maxAge=-1
```

## Hibernate & JDBC

Control global properties that are relevant to Hibernate,
when CAS attempts to employ and utilize database resources,
connections and queries.

```properties
# cas.jdbc.showSql=true
# cas.jdbc.genDdl=true
# cas.jdbc.caseInsensitive=false
# cas.jdbc.physicalTableNames.{table-name}={new-table-name}
```

### Database Settings

The following options related to JPA/JDBC support in CAS apply equally to a number of CAS components (ticket registries, etc) given the component's *configuration key*:

```properties
# ${configurationKey}.user=sa
# ${configurationKey}.password=
# ${configurationKey}.driverClass=org.hsqldb.jdbcDriver
# ${configurationKey}.url=jdbc:hsqldb:mem:cas-hsql-database
# ${configurationKey}.dialect=org.hibernate.dialect.HSQLDialect

# ${configurationKey}.failFastTimeout=1
# ${configurationKey}.isolationLevelName=ISOLATION_READ_COMMITTED
# ${configurationKey}.healthQuery=
# ${configurationKey}.isolateInternalQueries=false
# ${configurationKey}.leakThreshold=10
# ${configurationKey}.propagationBehaviorName=PROPAGATION_REQUIRED
# ${configurationKey}.batchSize=1
# ${configurationKey}.defaultCatalog=
# ${configurationKey}.defaultSchema=
# ${configurationKey}.ddlAuto=create-drop
# ${configurationKey}.physicalNamingStrategyClassName=org.apereo.cas.jpa.CasHibernatePhysicalNamingStrategy

# ${configurationKey}.autocommit=false
# ${configurationKey}.idleTimeout=5000

# ${configurationKey}.dataSourceName=
# ${configurationKey}.dataSourceProxy=false

# Hibernate-specific properties (i.e. `hibernate.globally_quoted_identifiers`)
# ${configurationKey}.properties.propertyName=propertyValue

# ${configurationKey}.pool.suspension=false
# ${configurationKey}.pool.minSize=6
# ${configurationKey}.pool.maxSize=18
# ${configurationKey}.pool.maxWait=2000
# ${configurationKey}.pool.timeoutMillis=1000
```

### Container-based JDBC Connections

If you are planning to use a container-managed JDBC connection with CAS (i.e. JPA Ticket/Service Registry, etc)
then you can set the `dataSourceName` property on any of the configuration items that require a database
connection. When using a container configured data source, many of the pool related parameters will not be used.
If `dataSourceName` is specified but the JNDI lookup fails, a data source will be created with the configured 
(or default) CAS pool parameters.

If you experience classloading errors while trying to use a container datasource, you can try 
setting the `dataSourceProxy` setting to true which will wrap the container datasource in
a way that may resolve the error.

The `dataSourceName` property can be either a JNDI name for the datasource or a resource name prefixed with 
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

A number of components in CAS accept signing and encryption keys. In most scenarios if keys are not provided, CAS will auto-generate them. The following instructions apply if you wish to manually and beforehand create the signing and encryption keys.

Note that if you are asked to create a [JWK](https://tools.ietf.org/html/rfc7517) of a certain size for the key, you are to use the following set of commands to generate the token:

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

The generated value for `k` needs to be assigned to the relevant CAS settings. Note that keys generated via the above algorithm are processed by CAS using the Advanced Encryption Standard (`AES`) algorithm which is a specification for the encryption of electronic data established by the U.S. National Institute of Standards and Technology.

### Settings

The following crypto options apply equally to relevant CAS components (ticket registries, etc) given the component's *configuration key*:

```properties
# ${configurationKey}.crypto.signing.key=
# ${configurationKey}.crypto.signing.keySize=

# ${configurationKey}.crypto.encryption.key=
# ${configurationKey}.crypto.encryption.keySize=

# ${configurationKey}.crypto.alg=AES
# ${configurationKey}.crypto.enabled=false
```

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
# ${configurationKey}.principalAttribute=uid,sAMAccountName,etc
# ${configurationKey}.returnNull=false
# ${configurationKey}.principalResolutionFailureFatal=false
# ${configurationKey}.useExistingPrincipalId=false
```

## InfluxDb Configuration

The following options related to InfluxDb support in CAS apply equally to a number of CAS components given the component's *configuration key*:

```properties
# ${configurationKey}.url=http://localhost:8086
# ${configurationKey}.username=root
# ${configurationKey}.password=root
# ${configurationKey}.retentionPolicy=autogen
# ${configurationKey}.dropDatabase=false
# ${configurationKey}.pointsToFlush=100
# ${configurationKey}.batchInterval=PT5S
# ${configurationKey}.consistencyLevel=ALL
```

## Hazelcast Configuration

The following options related to Hazelcast support in CAS apply equally to a number of CAS components given the component's *configuration key*:

```properties
# ${configurationKey}.cluster.members=123.456.789.000,123.456.789.001
# ${configurationKey}.cluster.instanceName=localhost
# ${configurationKey}.cluster.port=5701
```

More advanced Hazelcast configuration settings are listed below, given the component's *configuration key*:

```properties
# ${configurationKey}.cluster.tcpipEnabled=true

# ${configurationKey}.cluster.partitionMemberGroupType=HOST_AWARE|CUSTOM|PER_MEMBER|ZONE_AWARE|SPI

# ${configurationKey}.cluster.evictionPolicy=LRU
# ${configurationKey}.cluster.maxNoHeartbeatSeconds=300
# ${configurationKey}.cluster.loggingType=slf4j
# ${configurationKey}.cluster.portAutoIncrement=true
# ${configurationKey}.cluster.maxHeapSizePercentage=85
# ${configurationKey}.cluster.backupCount=1
# ${configurationKey}.cluster.asyncBackupCount=0
# ${configurationKey}.cluster.maxSizePolicy=USED_HEAP_PERCENTAGE
# ${configurationKey}.cluster.timeout=5
```

### Multicast Discovery

```properties
# ${configurationKey}.cluster.multicastTrustedInterfaces=
# ${configurationKey}.cluster.multicastEnabled=false
# ${configurationKey}.cluster.multicastPort=
# ${configurationKey}.cluster.multicastGroup=
# ${configurationKey}.cluster.multicastTimeout=2
# ${configurationKey}.cluster.multicastTimeToLive=32
```

### AWS EC2 Discovery

```properties
# ${configurationKey}.cluster.discovery.enabled=true

# ${configurationKey}.cluster.discovery.aws.accessKey=
# ${configurationKey}.cluster.discovery.aws.secretKey=

# ${configurationKey}.cluster.discovery.aws.iamRole=

# ${configurationKey}.cluster.discovery.aws.region=us-east-1
# ${configurationKey}.cluster.discovery.aws.hostHeader=
# ${configurationKey}.cluster.discovery.aws.securityGroupName=
# ${configurationKey}.cluster.discovery.aws.tagKey=
# ${configurationKey}.cluster.discovery.aws.tagValue=
# ${configurationKey}.cluster.discovery.aws.port=-1
# ${configurationKey}.cluster.discovery.aws.connectionTimeoutSeconds=5
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
# ${configurationKey}.cluster.discovery.jclouds.tagKeys=
# ${configurationKey}.cluster.discovery.jclouds.tagValues=
# ${configurationKey}.cluster.discovery.jclouds.group=
# ${configurationKey}.cluster.discovery.jclouds.port=-1
# ${configurationKey}.cluster.discovery.jclouds.roleName=
# ${configurationKey}.cluster.discovery.jclouds.credentialPath=
```

### Kubernetes Discovery

```properties
# ${configurationKey}.cluster.discovery.enabled=true

# ${configurationKey}.cluster.discovery.kubernetes.serviceDns=
# ${configurationKey}.cluster.discovery.kubernetes.serviceDnsTimeout=-1
# ${configurationKey}.cluster.discovery.kubernetes.serviceName=
# ${configurationKey}.cluster.discovery.kubernetes.serviceLabelName=
# ${configurationKey}.cluster.discovery.kubernetes.serviceLabelValue=
# ${configurationKey}.cluster.discovery.kubernetes.namespace=
# ${configurationKey}.cluster.discovery.kubernetes.resolveNotReadyAddresses=false
# ${configurationKey}.cluster.discovery.kubernetes.kubernetesMaster=
# ${configurationKey}.cluster.discovery.kubernetes.apiToken=
```

### Docker Swarm Discovery

```properties
# ${configurationKey}.cluster.discovery.enabled=true

# ${configurationKey}.cluster.discovery.dockerSwarm.dnsProvider.enabled=true
# ${configurationKey}.cluster.discovery.dockerSwarm.dnsProvider.serviceName=
# ${configurationKey}.cluster.discovery.dockerSwarm.dnsProvider.servicePort=5701
# ${configurationKey}.cluster.discovery.dockerSwarm.dnsProvider.peerServices=service-a,service-b,etc

# ${configurationKey}.cluster.discovery.dockerSwarm.memberProvider.enabled=true
# ${configurationKey}.cluster.discovery.dockerSwarm.memberProvider.groupName=
# ${configurationKey}.cluster.discovery.dockerSwarm.memberProvider.groupPassword=
# ${configurationKey}.cluster.discovery.dockerSwarm.memberProvider.dockerNetworkNames=
# ${configurationKey}.cluster.discovery.dockerSwarm.memberProvider.dockerServiceNames=
# ${configurationKey}.cluster.discovery.dockerSwarm.memberProvider.dockerServiceLabels=
# ${configurationKey}.cluster.discovery.dockerSwarm.memberProvider.swarmMgrUri=
# ${configurationKey}.cluster.discovery.dockerSwarm.memberProvider.skipVerifySsl=false
# ${configurationKey}.cluster.discovery.dockerSwarm.memberProvider.hazelcastPeerPort=5701
```

### Microsoft Azure Discovery

```properties
# ${configurationKey}.cluster.discovery.enabled=true

# ${configurationKey}.cluster.discovery.azure.subscriptionId=
# ${configurationKey}.cluster.discovery.azure.clientId=
# ${configurationKey}.cluster.discovery.azure.clientSecret=
# ${configurationKey}.cluster.discovery.azure.tenantId=
# ${configurationKey}.cluster.discovery.azure.clusterId=
# ${configurationKey}.cluster.discovery.azure.groupName=
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
# ${configurationKey}.server.nasPortId=-1
# ${configurationKey}.server.nasRealPort=-1
# ${configurationKey}.server.protocol=EAP_MSCHAPv2
# ${configurationKey}.server.retries=3
# ${configurationKey}.server.nasPortType=-1
# ${configurationKey}.server.nasPort=-1
# ${configurationKey}.server.nasIpAddress=
# ${configurationKey}.server.nasIpv6Address=
# ${configurationKey}.server.nasIdentifier=-1

# ${configurationKey}.client.authenticationPort=1812
# ${configurationKey}.client.sharedSecret=N0Sh@ar3d$ecReT
# ${configurationKey}.client.socketTimeout=0
# ${configurationKey}.client.inetAddress=localhost
# ${configurationKey}.client.accountingPort=1813

# ${configurationKey}.failoverOnException=false
# ${configurationKey}.failoverOnAuthenticationFailure=false
```

## CouchDb Configuration

The following options related to CouchDb support in CAS apply equally to a number of CAS components (ticket registries, etc) given the component's *configuration key*:

```properties
# ${configurationKey}.couchDb.url=http://localhost:5984
# ${configurationKey}.couchDb.username=
# ${configurationKey}.couchDb.password=
# ${configurationKey}.couchDb.socketTimeout=10000
# ${configurationKey}.couchDb.connectionTimeout=1000
# ${configurationKey}.couchDb.dropCollection=false
# ${configurationKey}.couchDb.maxConnections=20
# ${configurationKey}.couchDb.enableSSL=
# ${configurationKey}.couchDb.relaxedSSLSettings=
# ${configurationKey}.couchDb.caching=true
# ${configurationKey}.couchDb.maxCacheEntries=1000
# ${configurationKey}.couchDb.maxObjectSizeBytes=8192
# ${configurationKey}.couchDb.useExpectContinue=true
# ${configurationKey}.couchDb.cleanupIdleConnections=true
# ${configurationKey}.couchDb.createIfNotExists=true
# ${configurationKey}.couchDb.proxyHost=
# ${configurationKey}.couchDb.proxyPort=-1

# Defaults are based on the feature name.
# ${configurationKey}.couchDb.dbName=

# For the few features that can't have update conflicts automatically resolved.
# ${configurationKey}.couchDb.retries=5

# Depending on the feature at hand, CAS may perform some actions asynchronously.
# ${configurationKey}.couchDb.asynchronous=true
```

## MongoDb Configuration

The following options related to MongoDb support in CAS apply equally to a number of CAS components (ticket registries, etc) given the component's *configuration key*:

```properties
# ${configurationKey}.mongo.host=localhost
# ${configurationKey}.mongo.clientUri=localhost
# ${configurationKey}.mongo.idleTimeout=30000
# ${configurationKey}.mongo.port=27017
# ${configurationKey}.mongo.dropCollection=false
# ${configurationKey}.mongo.socketKeepAlive=false
# ${configurationKey}.mongo.password=

# Depending on the feature at hand, CAS may decide to dynamically create its own collections and ignore this setting.
# ${configurationKey}.mongo.collection=cas-service-registry

# ${configurationKey}.mongo.databaseName=cas-mongo-database
# ${configurationKey}.mongo.timeout=5000
# ${configurationKey}.mongo.userId=
# ${configurationKey}.mongo.writeConcern=NORMAL
# ${configurationKey}.mongo.authenticationDatabaseName=
# ${configurationKey}.mongo.replicaSet=
# ${configurationKey}.mongo.sslEnabled=false
# ${configurationKey}.mongo.conns.lifetime=60000
# ${configurationKey}.mongo.conns.perHost=10
```

## DynamoDb Configuration

The following options related to DynamoDb support in CAS apply equally to a number of CAS components (ticket registries, etc) given the component's *configuration key*:

```properties
# ${configurationKey}.dynamoDb.dropTablesOnStartup=false
# ${configurationKey}.dynamoDb.preventTableCreationOnStartup=false
# ${configurationKey}.dynamoDb.localInstance=false
```
    
AWS settings for this feature are available [here](#amazon-integration-settings).

## RESTful Integrations

The following options related to features in CAS that provide REST support to fetch and update data. These settings apply equally, given the component's *configuration key*:

```properties
# ${configurationKey}.method=GET|POST
# ${configurationKey}.order=0
# ${configurationKey}.caseInsensitive=false
# ${configurationKey}.basicAuthUsername=uid
# ${configurationKey}.basicAuthPassword=password
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
# ${configurationKey}.redis.useSsl=false

# ${configurationKey}.redis.pool.max-active=20
# ${configurationKey}.redis.pool.maxIdle=8
# ${configurationKey}.redis.pool.minIdle=0
# ${configurationKey}.redis.pool.maxActive=8
# ${configurationKey}.redis.pool.maxWait=-1
# ${configurationKey}.redis.pool.numTestsPerEvictionRun=0
# ${configurationKey}.redis.pool.softMinEvictableIdleTimeMillis=0
# ${configurationKey}.redis.pool.minEvictableIdleTimeMillis=0
# ${configurationKey}.redis.pool.lifo=true
# ${configurationKey}.redis.pool.fairness=false

# ${configurationKey}.redis.pool.testOnCreate=false
# ${configurationKey}.redis.pool.testOnBorrow=false
# ${configurationKey}.redis.pool.testOnReturn=false
# ${configurationKey}.redis.pool.testWhileIdle=false

# ${configurationKey}.redis.sentinel.master=mymaster
# ${configurationKey}.redis.sentinel.node[0]=localhost:26377
# ${configurationKey}.redis.sentinel.node[1]=localhost:26378
# ${configurationKey}.redis.sentinel.node[2]=localhost:26379
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

The following  options apply equally to SAML2 service provider integrations, given the provider's *configuration key*:

```properties
# ${configurationKey}.metadata=/etc/cas/saml/dropbox.xml
# ${configurationKey}.name=Dropbox
# ${configurationKey}.description=Dropbox Integration
# ${configurationKey}.nameIdAttribute=mail
# ${configurationKey}.nameIdFormat=
# ${configurationKey}.signatureLocation=
# ${configurationKey}.attributes=
# ${configurationKey}.entityIds=
# ${configurationKey}.signResponses=
# ${configurationKey}.signAssertions=
```

## Multifactor Authentication Providers

All configurable multifactor authentication providers have these base properties available given the provider's *configuration key*:

```properties
# ${configurationKey}.rank=
# ${configurationKey}.id=
# ${configurationKey}.name=
# ${configurationKey}.failureMode=UNDEFINED
```

## Multifactor Authentication Bypass

The following bypass options apply equally to multifactor authentication providers given the provider's *configuration key*:

```properties
# ${configurationKey}.bypass.type=DEFAULT|GROOVY|REST

# ${configurationKey}.bypass.principalAttributeName=bypass|skip
# ${configurationKey}.bypass.principalAttributeValue=true|enabled.+

# ${configurationKey}.bypass.authenticationAttributeName=bypass|skip
# ${configurationKey}.bypass.authenticationAttributeValue=allowed.+|enabled.+

# ${configurationKey}.bypass.authenticationHandlerName=AcceptUsers.+
# ${configurationKey}.bypass.authenticationMethodName=LdapAuthentication.+

# ${configurationKey}.bypass.credentialClassType=UsernamePassword.+

# ${configurationKey}.bypass.httpRequestRemoteAddress=127.+|example.*
# ${configurationKey}.bypass.httpRequestHeaders=header-X-.+|header-Y-.+

# ${configurationKey}.bypass.groovy.location=file:/etc/cas/config/mfa-bypass.groovy
```

If multifactor authentication bypass is determined via REST, 
RESTful settings are available [here](#restful-integrations) under the configuration key `${configurationKey}.bypass.rest`.

## Couchbase Integration Settings

The following options are shared and apply when CAS is configured to integrate with Couchbase (i.e ticket registry, etc), given the provider's *configuration key*:

```properties
# ${configurationKey}.nodeSet=localhost:8091
# ${configurationKey}.password=
# ${configurationKey}.queryEnabled=true
# ${configurationKey}.bucket=default
# ${configurationKey}.timeout=PT30S
```

## Amazon Integration Settings

The following options are shared and apply when CAS is configured to integrate with various 
Amazon Web Service features, given the provider's *configuration key*:

```properties
# Path to an external properties file that contains 'accessKey' and 'secretKey' fields.
# ${configurationKey}.credentialsPropertiesFile=file:/path/to/file.properties

# Alternatively, you may directly provide credentials to CAS
# ${configurationKey}.credentialAccessKey=
# ${configurationKey}.credentialSecretKey=

# ${configurationKey}.endpoint=http://localhost:8000
# ${configurationKey}.region=US_WEST_2|US_EAST_2|EU_WEST_2|<REGION-NAME>
# ${configurationKey}.regionOverride=
# ${configurationKey}.localAddress=

# ${configurationKey}.maxErrorRetry=-1
# ${configurationKey}.proxyHost=
# ${configurationKey}.proxyPassword=
# ${configurationKey}.proxyPort=-1

# ${configurationKey}.readCapacity=10
# ${configurationKey}.writeCapacity=10
# ${configurationKey}.connectionTimeout=5000
# ${configurationKey}.requestTimeout=5000
# ${configurationKey}.socketTimeout=5000
# ${configurationKey}.useGzip=false
# ${configurationKey}.useReaper=false
# ${configurationKey}.useThrottleRetries=false
# ${configurationKey}.useTcpKeepAlive=false
# ${configurationKey}.protocol=HTTPS
# ${configurationKey}.clientExecutionTimeout=10000
# ${configurationKey}.cacheResponseMetadata=false
# ${configurationKey}.maxConnections=10
```

## Memcached Integration Settings

The following  options are shared and apply when CAS is configured to integrate with memcached (i.e ticket registry, etc), given the provider's *configuration key*:

```properties
# ${configurationKey}.memcached.servers=localhost:11211
# ${configurationKey}.memcached.locatorType=ARRAY_MOD
# ${configurationKey}.memcached.failureMode=Redistribute
# ${configurationKey}.memcached.hashAlgorithm=FNV1_64_HASH
# ${configurationKey}.memcached.shouldOptimize=false
# ${configurationKey}.memcached.daemon=true
# ${configurationKey}.memcached.maxReconnectDelay=-1
# ${configurationKey}.memcached.useNagleAlgorithm=false
# ${configurationKey}.memcached.shutdownTimeoutSeconds=-1
# ${configurationKey}.memcached.opTimeout=-1
# ${configurationKey}.memcached.timeoutExceptionThreshold=2
# ${configurationKey}.memcached.maxTotal=20
# ${configurationKey}.memcached.maxIdle=8
# ${configurationKey}.memcached.minIdle=0

# ${configurationKey}.memcached.transcoder=KRYO|SERIAL|WHALIN|WHALINV1
# ${configurationKey}.memcached.transcoderCompressionThreshold=16384
# ${configurationKey}.memcached.kryoAutoReset=false
# ${configurationKey}.memcached.kryoObjectsByReference=false
# ${configurationKey}.memcached.kryoRegistrationRequired=false
```

## Password Policy Settings

The following  options are shared and apply when CAS is configured to integrate with account sources and authentication strategies that support password policy enforcement and detection, given the provider's *configuration key*. Note that certain setting may only be applicable if the underlying account source is LDAP and are only taken into account if the authentication strategy configured in CAS is able to honor and recognize them: 

```properties
# ${configurationKey}.type=GENERIC|AD|FreeIPA|EDirectory

# ${configurationKey}.enabled=true
# ${configurationKey}.policyAttributes.accountLocked=javax.security.auth.login.AccountLockedException
# ${configurationKey}.loginFailures=5
# ${configurationKey}.warningAttributeValue=
# ${configurationKey}.warningAttributeName=
# ${configurationKey}.displayWarningOnMatch=true
# ${configurationKey}.warnAll=true
# ${configurationKey}.warningDays=30
# ${configurationKey}.accountStateHandlingEnabled=true

# An implementation of `org.ldaptive.auth.AuthenticationResponseHandler`
# ${configurationKey}.customPolicyClass=com.example.MyAuthenticationResponseHandler

# ${configurationKey}.strategy=DEFAULT|GROOVY|REJECT_RESULT_CODE
# ${configurationKey}.groovy.location=file:/etc/cas/config/password-policy.groovy
```

#### Password Policy Strategies

Password policy strategy types are outlined below. The strategy evaluates the authentication response received from LDAP, etc and is allowed to review it upfront in order to further examine whether account state, messages and warnings is eligible for further investigation.

| Option        | Description
|---------------|-----------------------------------------------------------------------------
| `DEFAULT`     | Accepts the authentication response as is, and processes account state, if any.
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
# ${configurationKey}.mail.attributeName=mail
```

The following settings may also need to be defined to describe the mail server settings:

```properties
# spring.mail.host=
# spring.mail.port=
# spring.mail.username=
# spring.mail.password=
# spring.mail.testConnection=true
# spring.mail.properties.mail.smtp.auth=true
# spring.mail.properties.mail.smtp.starttls.enable=true
```

## SMS Notifications
 
The following options are shared and apply when CAS is configured to send SMS notifications, given the provider's *configuration key*:
 
```properties
# ${configurationKey}.sms.from=
# ${configurationKey}.sms.text=
# ${configurationKey}.sms.attributeName=phone
```

You will also need to ensure a provider is defined that is able to send SMS messages. To learn more about this 
topic, [please review this guide](../notifications/SMS-Messaging-Configuration.html).
 
## Delegated Authentication Settings

The following options are shared and apply when CAS is configured to delegate authentication 
to an external provider such as Yahoo, given the provider's *configuration key*:

```properties
# ${configurationKey}.id=
# ${configurationKey}.secret=
# ${configurationKey}.clientName=My Provider
# ${configurationKey}.autoRedirect=false
# ${configurationKey}.principalAttributeId=
```

### Delegated Authentication OpenID Connect Settings

The following options are shared and apply when CAS is configured to delegate authentication 
to an external OpenID Connect provider such as Azure AD, given the provider's *configuration key*:

```properties
# ${configurationKey}.discoveryUri=
# ${configurationKey}.logoutUrl=
# ${configurationKey}.maxClockSkew=
# ${configurationKey}.scope=
# ${configurationKey}.useNonce=
# ${configurationKey}.preferredJwsAlgorithm=
# ${configurationKey}.responseMode=
# ${configurationKey}.responseType=
# ${configurationKey}.customParams.param1=value1
```

## LDAP Connection Settings

The following  options apply  to features that integrate with an LDAP server (i.e. authentication, attribute resolution, etc) given the provider's *configuration key*:

```properties
#${configurationKey}.ldapUrl=ldaps://ldap1.example.edu ldaps://ldap2.example.edu
#${configurationKey}.bindDn=cn=Directory Manager,dc=example,dc=org
#${configurationKey}.bindCredential=Password

#${configurationKey}.poolPassivator=NONE|CLOSE|BIND
#${configurationKey}.connectionStrategy=
#${configurationKey}.providerClass=org.ldaptive.provider.unboundid.UnboundIDProvider
#${configurationKey}.connectTimeout=PT5S
#${configurationKey}.trustCertificates=
#${configurationKey}.keystore=
#${configurationKey}.keystorePassword=
#${configurationKey}.keystoreType=JKS|JCEKS|PKCS12
#${configurationKey}.minPoolSize=3
#${configurationKey}.maxPoolSize=10
#${configurationKey}.validateOnCheckout=true
#${configurationKey}.validatePeriodically=true
#${configurationKey}.validatePeriod=PT5M
#${configurationKey}.validateTimeout=PT5S
#${configurationKey}.failFast=true
#${configurationKey}.idleTime=PT10M
#${configurationKey}.prunePeriod=PT2H
#${configurationKey}.blockWaitTime=PT3S
#${configurationKey}.useSsl=true
#${configurationKey}.useStartTls=false
#${configurationKey}.responseTimeout=PT5S
#${configurationKey}.allowMultipleDns=false
#${configurationKey}.allowMultipleEntries=false
#${configurationKey}.followReferrals=false
#${configurationKey}.binaryAttributes=objectGUID,someOtherAttribute
#${configurationKey}.name=
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
| `CLOSE`                 | Passivates a connection by attempting to close it.
| `BIND`                  | The default behavior which passivates a connection by performing a bind operation on it. This option requires the availability of bind credentials when establishing connections to LDAP.

#### Why Passivators?

You may receive unexpected LDAP failures, when CAS is configured to authenticate using `DIRECT` or `AUTHENTICATED` types and LDAP is locked down to not allow anonymous binds/searches. Every second attempt with a given LDAP connection from the pool would fail if it was on the same connection as a failed login attempt, and the regular connection validator would similarly fail. When a connection is returned back to a pool, it still may contain the principal and credentials from the previous attempt. Before the next bind attempt using that connection, the validator tries to validate the connection again but fails because it's no longer trying with the configured bind credentials but with whatever user DN was used in the previous step. Given the validation failure, the connection is closed and CAS would deny access by default. Passivators attempt to reconnect to LDAP with the configured bind credentials, effectively resetting the connection to what it should be after each bind request.

Furthermore if you are seeing errors in the logs that resemble a *<Operation exception encountered, reopening connection>* type of message, this usually is an indication that the connection pool's validation timeout established and created by CAS is greater than the timeout configured in the LDAP server, or more likely, in the load balancer in front of the LDAP servers. You can adjust the LDAP server session's timeout for connections, or you can teach CAS to use a validity period that is equal or less than the LDAP server session's timeout.

### Connection Strategies

If multiple URLs are provided as the LDAP url, this describes how each URL will be processed.

| Provider              | Description              
|-----------------------|-----------------------------------------------------------------------------------------------
| `DEFAULT`             | The default JNDI provider behavior will be used.    
| `ACTIVE_PASSIVE`      | First LDAP will be used for every request unless it fails and then the next shall be used.    
| `ROUND_ROBIN`         | For each new connection the next url in the list will be used.      
| `RANDOM`              | For each new connection a random LDAP url will be selected.
| `DNS_SRV`             | LDAP urls based on DNS SRV records of the configured/given LDAP url will be used.  

### LDAP SASL Mechanisms

```properties
#${configurationKey}.saslMechanism=GSSAPI|DIGEST_MD5|CRAM_MD5|EXTERNAL
#${configurationKey}.saslRealm=EXAMPLE.COM
#${configurationKey}.saslAuthorizationId=
#${configurationKey}.saslMutualAuth=
#${configurationKey}.saslQualityOfProtection=
#${configurationKey}.saslSecurityStrength=
```

### LDAP Connection Validators

The following LDAP validators can be used to test connection health status:

| Type                    | Description
|-------------------------|----------------------------------------------------------------------------------------------------
| `NONE`                  | No validation takes place.
| `SEARCH`                | Validates a connection is healthy by performing a search operation. Validation is considered successful if the search result size is greater than zero.
| `COMPARE`               | Validates a connection is healthy by performing a compare operation.

```properties
#${configurationKey}.validator.type=NONE|SEARCH|COMPARE
#${configurationKey}.validator.baseDn=
#${configurationKey}.validator.searchFilter=(objectClass=*)
#${configurationKey}.validator.scope=OBJECT|ONELEVEL|SUBTREE
#${configurationKey}.validator.attributeName=objectClass
#${configurationKey}.validator.attributeValues=top
#${configurationKey}.validator.dn=
```

### LDAP SSL Hostname Verification

The following LDAP validators can be used to test connection health status:

| Type                    | Description
|-------------------------|------------------------------------
| `DEFAULT`               | Default option to enable and force hostname verification of the LDAP SSL configuration.
| `ANY`                   | Skip and ignore the hostname verification of the LDAP SSL configuration.

```properties
#${configurationKey}.hostnameVerifier=DEFAULT|ANY
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

# ${configurationKey}.baseDn=dc=example,dc=org
# ${configurationKey}.subtreeSearch=true
# ${configurationKey}.searchFilter=cn={user}

# ${configurationKey}.enhanceWithEntryResolver=true
# ${configurationKey}.derefAliases=NEVER|SEARCHING|FINDING|ALWAYS
# ${configurationKey}.dnFormat=uid=%s,ou=people,dc=example,dc=org
# ${configurationKey}.principalAttributePassword=password
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
# ${configurationKey}.searchEntryHandlers[0].type=

# ${configurationKey}.searchEntryHandlers[0].caseChange.dnCaseChange=NONE|LOWER|UPPER
# ${configurationKey}.searchEntryHandlers[0].caseChange.attributeNameCaseChange=NONE|LOWER|UPPER
# ${configurationKey}.searchEntryHandlers[0].caseChange.attributeValueCaseChange=NONE|LOWER|UPPER
# ${configurationKey}.searchEntryHandlers[0].caseChange.attributeNames=

# ${configurationKey}.searchEntryHandlers[0].dnAttribute.dnAttributeName=entryDN
# ${configurationKey}.searchEntryHandlers[0].dnAttribute.addIfExists=false

# ${configurationKey}.searchEntryHandlers[0].primaryGroupId.groupFilter=(&(objectClass=group)(objectSid={0}))
# ${configurationKey}.searchEntryHandlers[0].primaryGroupId.baseDn=

# ${configurationKey}.searchEntryHandlers[0].mergeAttribute.mergeAttributeName=
# ${configurationKey}.searchEntryHandlers[0].mergeAttribute.attributeNames=

# ${configurationKey}.searchEntryHandlers[0].recursive.searchAttribute=
# ${configurationKey}.searchEntryHandlers[0].recursive.mergeAttributes=
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
