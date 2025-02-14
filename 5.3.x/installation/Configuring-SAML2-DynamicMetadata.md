---
layout: default
title: CAS - SAML2 Dynamic Metadata Management
---

# SAML2 Dynamic Metadata Management

Management of service provider metadata in a dynamic on-the-fly fashion may be accomplished via strategies outlined here.

## Metadata Query Protocol

CAS also supports the [Dynamic Metadata Query Protocol](https://spaces.internet2.edu/display/InCFederation/Metadata+Query+Protocol)
which is a REST-like API for requesting and receiving arbitrary metadata. In order to configure a CAS SAML service to retrieve its metadata from a Metadata query server, the metadata location must be configured to point to the query server instance.

MDQ may be configured using the below snippet as an example:

```json
{
  "@class" : "org.apereo.cas.support.saml.services.SamlRegisteredService",
  "serviceId" : "the-entity-id-of-the-sp",
  "name" : "SAMLService",
  "id" : 10000003,
  "evaluationOrder" : 10,
  "metadataLocation" : "https://mdq.server.org/entities/{0}"
}
```

...where `{0}` serves as an entityID placeholder for which metadata is to be queried. The placeholder is dynamically processed and replaced by CAS at runtime.

## REST

Similar to the Dynamic Metadata Query Protocol (MDQ), SAML service provider metadata may also be fetched using a more traditional REST interface. This is a simpler option that does not require one to deploy a compliant MDQ server and provides the flexibility of producing SP metadata using any programming language or framework.

Support is enabled by including the following module in the overlay:

```xml
<dependency>
  <groupId>org.apereo.cas</groupId>
  <artifactId>cas-server-support-saml-idp-metadata-rest</artifactId>
  <version>${cas.version}</version>
</dependency>
```

Use the below snippet as an example to fetch metadata from REST endpoints:

```json
{
  "@class" : "org.apereo.cas.support.saml.services.SamlRegisteredService",
  "serviceId" : "the-entity-id-of-the-sp",
  "name" : "SAMLService",
  "id" : 10000003,
  "evaluationOrder" : 10,
  "metadataLocation" : "rest://"
}
```

<div class="alert alert-info"><strong>Metadata Location</strong><p>
The metadata location in the registration record above simply needs to be specified as <code>rest://</code> to signal to CAS that SAML metadata for registered service provider must be fetched from REST endpoints defined in CAS configuration.
</p></div>

Requests are submitted to REST endpoints with `entityId` as the parameter and `Content-Type: application/xml` as the header. Upon a successful `200 - OK` response status, CAS expects the body of the HTTP response to match the below snippet:

```json
{  
   "id":1000,
   "name":"SAML Metadata For Service Provider",
   "value":"...",
   "signature":"..."
}
```

To see the relevant CAS properties, please [see this guide](Configuration-Properties.html#saml-metadata-rest).

## MongoDb

Metadata documents may also be stored in and fetched from a MongoDb instance.  This may specially be used to avoid copying metadata files across CAS nodes in a cluster, particularly where one needs to deal with more than a few bilateral SAML integrations. Metadata documents are stored in and fetched from a single pre-defined collection that is taught to CAS via settings.  The outline of the document is as follows:

| Field                     | Description
|--------------|---------------------------------------------------
| `id`                          | The identifier of the record.
| `name`             | Indexed field which describes and names the metadata briefly.
| `value`              | The XML document representing the metadata for the service provider.
| `signature`              | The contents of the signing key to validate metadata, if any.

Support is enabled by including the following module in the overlay:

```xml
<dependency>
  <groupId>org.apereo.cas</groupId>
  <artifactId>cas-server-support-saml-idp-metadata-mongo</artifactId>
  <version>${cas.version}</version>
</dependency>
```

SAML service definitions must then be designed as follows to allow CAS to fetch metadata documents from MongoDb instances:

```json
{
  "@class" : "org.apereo.cas.support.saml.services.SamlRegisteredService",
  "serviceId" : "the-entity-id-of-the-sp",
  "name" : "SAMLService",
  "id" : 10000003,
  "description" : "A MongoDb-based metadata resolver",
  "metadataLocation" : "mongodb://"
}
```

<div class="alert alert-info"><strong>Metadata Location</strong><p>
The metadata location in the registration record above simply needs to be specified as <code>mongodb://</code> to signal to CAS that 
SAML metadata for registered service provider must be fetched from MongoDb data sources defined in CAS configuration. 
</p></div>

To see the relevant CAS properties, please [see this guide](Configuration-Properties.html#saml-metadata-mongodb).

## JPA

Metadata documents may also be stored in and fetched from a relational database instance. This may specially be used to avoid copying metadata files across CAS nodes in a cluster, particularly where one needs to deal with more than a few bilateral SAML integrations. Metadata documents are stored in and fetched from a single pre-defined table  (i.e. `SamlMetadataDocument`) whose connection information is taught to CAS via settings and is automatically generated.  The outline of the table is as follows:

| Field                     | Description
|--------------|---------------------------------------------------
| `id`                          | The identifier of the record.
| `name`             | Indexed field which describes and names the metadata briefly.
| `value`              | The XML document representing the metadata for the service provider.
| `signature`              | The contents of the signing key to validate metadata, if any.

Support is enabled by including the following module in the overlay:

```xml
<dependency>
  <groupId>org.apereo.cas</groupId>
  <artifactId>cas-server-support-saml-idp-metadata-jpa</artifactId>
  <version>${cas.version}</version>
</dependency>
```

SAML service definitions must then be designed as follows to allow CAS to fetch metadata documents from database  instances:

```json
{
  "@class" : "org.apereo.cas.support.saml.services.SamlRegisteredService",
  "serviceId" : "the-entity-id-of-the-sp",
  "name" : "SAMLService",
  "id" : 10000003,
  "description" : "A relational-db-based metadata resolver",
  "metadataLocation" : "jdbc://"
}
```

<div class="alert alert-info"><strong>Metadata Location</strong><p>
The metadata location in the registration record above simply needs to be specified as <code>jdbc://</code> to signal to CAS that SAML metadata for registered service provider must be fetched from JDBC data sources defined in CAS configuration. 
</p></div>

To see the relevant CAS properties, please [see this guide](Configuration-Properties.html#saml-metadata-jpa).

## Groovy

A metadata location for a SAML service definition may  point to an external Groovy script, allowing the script to programmatically determine and build the metadata resolution machinery to be added to the collection of the existing resolvers. 

```json
{
  "@class" : "org.apereo.cas.support.saml.services.SamlRegisteredService",
  "serviceId" : "the-entity-id-of-the-sp",
  "name" : "SAMLService",
  "id" : 10000003,
  "description" : "A Groovy-based metadata resolver",
  "metadataLocation" : "file:/etc/cas/config/groovy-metadata.groovy"
}
```

The outline of the script may be as follows:

```groovy
import java.util.*
import org.apereo.cas.support.saml.*
import org.apereo.cas.support.saml.services.*
import org.opensaml.saml.metadata.resolver.*

def Collection<MetadataResolver> run(final Object... args) {
    def registeredService = args[0]
    def samlConfigBean = args[1]
    def samlProperties = args[2]
    def logger = args[3]

    /*
     Stuff happens where you build the relevant metadata resolver instance(s).
     When done, simply wrap the results into a collection and return.
     A null or empty collection will be ignored by CAS.
  */
  def metadataResolver = ...
   return CollectionUtils.wrap(metadataResolver)
}
```

The parameters passed are as follows:

| Parameter             | Description
|-----------------------|--------------------------------------------------------------------------------------------------------------------
| `registeredService`   | The object representing the corresponding service definition in the registry.
| `samlConfigBean`      | The object representing the OpenSAML configuration class holding various builder and marshaller factory instances.
| `samlProperties`      | The object responsible for capturing the CAS SAML IdP properties defined in the configuration.
| `logger`              | The object responsible for issuing log messages such as `logger.info(...)`.

## Amazon S3

Metadata documents may also be stored in and fetched from a MongoDb instance.
This may specially be used to avoid copying metadata files across CAS nodes in a cluster, particularly where one needs 
to deal with more than a few bilateral SAML integrations. Metadata documents are stored in and fetched from a 
single pre-defined bucket that is taught to CAS via settings.

Support is enabled by including the following module in the overlay:

```xml
<dependency>
  <groupId>org.apereo.cas</groupId>
  <artifactId>cas-server-support-saml-idp-metadata-aws-s3</artifactId>
  <version>${cas.version}</version>
</dependency>
```

SAML service definitions must then be designed as follows to allow CAS to fetch metadata documents from MongoDb instances:

```json
{
  "@class" : "org.apereo.cas.support.saml.services.SamlRegisteredService",
  "serviceId" : "the-entity-id-of-the-sp(s)",
  "name" : "SAMLService",
  "id" : 10000003,
  "description" : "Amazon S3-based metadata resolver",
  "metadataLocation" : "awss3://"
}
```

<div class="alert alert-info"><strong>Metadata Location</strong><p>
The metadata location in the registration record above simply needs to be specified as <code>awss3://</code> to signal to CAS that 
SAML metadata for registered service provider must be fetched from Amazon S3 defined in CAS configuration. 
</p></div>

To see the relevant CAS properties, please [see this guide](Configuration-Properties.html#saml-metadata-amazon-s3).
