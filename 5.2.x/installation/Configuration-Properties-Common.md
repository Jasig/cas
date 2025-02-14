---
layout: default
title: CAS Common Properties Overview
---

# CAS Common Properties

This document describes a number of suggestions and configuration options that apply to and are common amongst a selection of CAS modules and features. To see the full list of CAS properties, please [review this guide](Configuration-Properties.html).

## Naming Convention

- Settings and properties that are controlled by the CAS platform directly always begin with the prefix `cas`. All other settings are controlled and provided to CAS via other underlying frameworks and may have their own schemas and syntax. **BE CAREFUL** with the distinction.

- Unrecognized properties are generally ignored by CAS and/or frameworks upon which CAS depends. This means if you somehow misspell a property definition or fail to adhere to the dot-notation syntax and such, your setting is entirely ignored by CAS and likely the feature it controls will never be activated in the way you intend.

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

Certain functionality in CAS, such as [OAuth](OAuth-OpenId-Authentication.html) or [REST API](../protocol/REST-Protocol.html), allow you to throttle requests to specific endpoints in addition to the more generic authentication throttling functionality applied during the login flow and authentication attempts. To activate throttling functionality for a support module, the following strategies are supported in CAS setting.

The following parameters are passed:

| Value            | Description
|------------------|-------------------------------------------
| `neverThrottle`  | Disable throttling for the feature.
| `authenticationThrottle` | Enable throttling for the feature.

To fully deliver this functionality, it is expected that [authentication throttling](Configuring-Authentication-Throttling.html) is turned on.

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

The following options are supported:

| Type                    | Description
|-------------------------|----------------------------------------------------------------------------------------------------
| `NONE`                  | No password encoding (i.e. plain-text) takes place.     
| `DEFAULT`               | Use the `DefaultPasswordEncoder` of CAS. For message-digest algorithms via `characterEncoding` and `encodingAlgorithm`.
| `BCRYPT`                | Use the `BCryptPasswordEncoder` based on the `strength` provided and an optional `secret`.     
| `SCRYPT`                | Use the `SCryptPasswordEncoder`.
| `PBKDF2`                | Use the `Pbkdf2PasswordEncoder` based on the `strength` provided and an optional `secret`.  
| `STANDARD`              | Use the `StandardPasswordEncoder` based on the `secret` provided.  
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

## Hibernate & JDBC

Control global properties that are relevant to Hibernate,
when CAS attempts to employ and utilize database resources,
connections and queries.

```properties
# cas.jdbc.showSql=true
# cas.jdbc.genDdl=true
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

### RSA Keys

Certain features such as the ability to produce [JWTs as CAS tickets](Configure-ServiceTicket-JWT.html) may allow you to use the `RSA` algorithm with public/private keypairs for signing and encryption. This behavior may prove useful generally in cases where the consumer of the CAS-encoded payload is an outsider and a client application that need not have access to the signing secrets directly and visibly and may only be given a half truth vis-a-vis a public key to verify the payload authenticity and decode it. This particular option makes little sense in situations where CAS itself is both a producer and a consumer of the payload.

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

## DDL Configuration

Note that the default value for Hibernate's DDL setting is `create-drop` which may not be appropriate for use in production. Setting the value to
`validate` may be more desirable, but any of the following options can be used:

| Type                 | Description
|----------------------|----------------------------------------------------------
| `validate`           | Validate the schema, but make no changes to the database.
| `update`             | Update the schema.
| `create`             | Create the schema, destroying previous data.
| `create-drop`        | Drop the schema at the end of the session.

Note that during a version migration where any schema has changed `create-drop` will result
in the loss of all data as soon as CAS is started. For transient data like tickets this is probably
not an issue, but in cases like the audit table important data could be lost. Using `update`, while safe
for data, is confirmed to result in invalid database state. `validate` or the undocumented `none` settings
are likely the only safe options for production use.

For more information on configuration of transaction levels and propagation behaviors,
please review [this guide](http://docs.spring.io/spring-framework/docs/current/javadoc-api/).
