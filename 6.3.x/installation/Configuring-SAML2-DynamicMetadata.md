---
layout: default
title: CAS - SAML2 Metadata Management
---

# SAML2 Metadata Management

Management of service provider metadata in a dynamic on-the-fly fashion may be accomplished via strategies outlined here.

## Administrative Endpoints

The following endpoints are provided by CAS:
 
| Endpoint                                 | Description
|------------------------------------------|--------------------------------------------------------------------------------------
| `samlIdPRegisteredServiceMetadataCache`  | Manage and control the cache that holds metadata instances for SAML service providers. Note the cache is specific to the JVM memory of the CAS server node and it's **NOT** distributed or replicated. A `GET` operation produces the cached copy of the metadata for a given service provider, using the `serviceId` and `entityId` parameters. The `serviceId` parameter may be the numeric identifier for the registered service or its name. In case the service definition represents a metadata aggregate such as InCommon, the `entityId` parameter may be used to pinpoint and filter the exact entity within the aggregate. A `DELETE` operation will delete invalidate the metadata cache. If no parameters are provided, the metadata cache will be entirely invalidated. A `serviceId` parameter will force CAS to only invalidate the cached metadata instance for that service provider. The `serviceId` parameter may be the numeric identifier for the registered service or its name.

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
The metadata location in the registration record above needs to be specified as <code>rest://</code> to signal to CAS that SAML metadata for registered service provider must be fetched from REST endpoints defined in CAS configuration.
</p></div>

Requests are submitted to REST endpoints with `entityId` as the parameter and `Content-Type: application/xml` as the header. Upon 
a successful `200 - OK` response status, CAS expects the body of the HTTP response to match the below snippet:

```json
{  
   "id":1000,
   "name":"SAML Metadata For Service Provider",
   "value":"...",
   "signature":"..."
}
```

To see the relevant CAS properties, please [see this guide](../configuration/Configuration-Properties.html#saml-metadata-rest).

### Identity Provider Metadata

Metadata artifacts that belong to CAS as a SAML2 identity provider may also be managed and stored via REST APIs. Artifacts such as the metadata, signing and 
encryption keys, etc are passed along to an external API endpoint in the following structure as the request body:

```json
{
    "signingCertificate": "...",
    "signingKey": "...",
    "encryptionCertificate": "...",
    "encryptionKey": "...",
    "metadata": "...",
    "appliesTo": "CAS"
}
```

The URL endpoint, defined in CAS settings is expected to be available at a path that ends in `/idp`, which is added onto the URL endpoint by CAS automatically.
The API is expected to produce a successful `200 - OK` response status on all operations outlined below:  

| Method               | Description
|----------------------|----------------------------------------------------------------------------
| `GET`                | The response is expected to produce a JSON document outlining keys and metadata as indicated above. An `appliesTo` parameter may be passed to indicate the document owner and applicability, where a value of `CAS` indicates the CAS server as the global owner of the metadata and keys.
| `POST`               | Store the metadata and keys to finalize the metadata generation process. The request body contains the JSON document that outlines metadata and keys as indicated above.

Note that the signing and encryption keys are expected to be encrypted and signed using CAS crypto keys. To see the relevant 
CAS properties, please [see this guide](../configuration/Configuration-Properties.html#saml-metadata-rest).

#### Per Service

Identity provider metadata, certificates and keys can also be defined on a per-service basis to override the global defaults.
Metadata documents that would be applicable to a service definition need to adjust the `appliesTo` field in the metadata
document to carry the service definition's name and numeric identifier using the `[service-name]-[service-numeric-identifier]` format.

## Git

Metadata documents may also be stored in and fetched from Git repositories. This may specially be used to avoid copying metadata files across CAS nodes in a cluster, particularly where one needs to deal with more than a few bilateral SAML integrations. Metadata documents are stored as XML files, and their signing certificate, optionally, is expected to be found in a `.pem` file by the same name in the repository. (i.e. `SP.xml`'s certificate can be found in `SP.pem`).

Support is enabled by including the following module in the overlay:

```xml
<dependency>
  <groupId>org.apereo.cas</groupId>
  <artifactId>cas-server-support-saml-idp-metadata-git</artifactId>
  <version>${cas.version}</version>
</dependency>
```

SAML service definitions must then be designed as follows to allow CAS to fetch metadata documents from Git repositories:

```json
{
  "@class" : "org.apereo.cas.support.saml.services.SamlRegisteredService",
  "serviceId" : "the-entity-id-of-the-sp",
  "name" : "SAMLService",
  "id" : 10000003,
  "description" : "A Git-based metadata resolver",
  "metadataLocation" : "git://"
}
```

Give the above definition, the expectation is that the git repository 
contains a `SAMLService.xml` file which may optionally also be accompanied by a `SAMLService.pem` file.

<div class="alert alert-info"><strong>Metadata Location</strong><p>
The metadata location in the registration record above simply needs to be specified as <code>git://</code> to signal to CAS that 
SAML metadata for registered service provider must be fetched from Git repositories defined in CAS configuration. 
</p></div>

To see the relevant CAS properties, please [see this guide](../configuration/Configuration-Properties.html#saml-metadata-git).

### Identity Provider Metadata

Metadata artifacts that belong to CAS as a SAML2 identity provider may also be managed and stored via Git. Artifacts such as the metadata, signing and encryption keys, etc are kept on the file-system in distinct directory locations inside the repository and data is pushed to or pulled from git repositories on demand.

Note that the signing and encryption keys are expected to be encrypted and signed using CAS crypto keys. To see the relevant CAS properties, please [see this guide](../configuration/Configuration-Properties.html#saml-metadata-git).

#### Per Service

Identity provider metadata, certificates and keys can also be defined on a per-service basis to override the global defaults.
Metadata documents that would be applicable to a service definition need to adjust the `appliesTo` field in the metadata
document, which is used to construct the directory path to metadata artifacts.

## MongoDb

Metadata documents may also be stored in and fetched from a MongoDb instance.  This may specially be used to avoid copying metadata files across CAS nodes in a cluster, particularly where one needs to deal with more than a few bilateral SAML integrations. Metadata documents are stored in and fetched from a single pre-defined collection that is taught to CAS via settings.  The outline of the document is as follows:

| Field                  | Description
|------------------------|---------------------------------------------------
| `id`                   | The identifier of the record.
| `name`                 | Indexed field which describes and names the metadata briefly.
| `value`                | The XML document representing the metadata for the service provider.
| `signature`            | The contents of the signing certificate to validate metadata, if any.

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

To see the relevant CAS properties, please [see this guide](../configuration/Configuration-Properties.html#saml-metadata-mongodb).

### Identity Provider Metadata

Metadata artifacts that belong to CAS as a SAML2 identity provider may also be managed and stored via MongoDb. Artifacts such as the metadata, signing and encryption keys, etc are kept inside a MongoDb collection taught to CAS via settings as a single document that would have the following structure:

```json
{
    "signingCertificate": "...",
    "signingKey": "...",
    "encryptionCertificate": "...",
    "encryptionKey": "...",
    "metadata": "...",
    "appliesTo": "CAS"
}
```

Note that the signing and encryption keys are expected to be encrypted and signed using CAS crypto keys. To see the relevant CAS properties, please [see this guide](../configuration/Configuration-Properties.html#saml-metadata-mongodb).

#### Per Service

Identity provider metadata, certificates and keys can also be defined on a per-service basis to override the global defaults.
Metadata documents that would be applicable to a service definition need to adjust the `appliesTo` field in the metadata
document to carry the service definition's name and numeric identifier using the `[service-name]-[service-numeric-identifier]` format.

## JPA

Metadata documents may also be stored in and fetched from a relational database instance. This may specially be used to avoid copying metadata files across CAS nodes in a cluster, particularly where one needs to deal with more than a few bilateral SAML integrations. Metadata documents are stored in and fetched from a single pre-defined table  (i.e. `SamlMetadataDocument`) whose connection information is taught to CAS via settings and is automatically generated. The outline of the table is as follows:

| Field        | Description
|--------------|---------------------------------------------------
| `id`         | The identifier of the record.
| `name`       | Indexed field which describes and names the metadata briefly.
| `value`      | The XML document representing the metadata for the service provider.
| `signature`  | The contents of the signing certificate to validate metadata, if any.

Support is enabled by including the following module in the overlay:

```xml
<dependency>
  <groupId>org.apereo.cas</groupId>
  <artifactId>cas-server-support-saml-idp-metadata-jpa</artifactId>
  <version>${cas.version}</version>
</dependency>
```

SAML service definitions must then be designed as follows to allow CAS to fetch metadata documents from database instances:

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

To see the relevant CAS properties, please [see this guide](../configuration/Configuration-Properties.html#saml-metadata-jpa).

### Identity Provider Metadata

Metadata artifacts that belong to CAS as a SAML2 identity provider may also be managed and stored via JPA. Artifacts such as the metadata, signing and encryption keys, etc are kept
inside a database table that would have the following structure:

| Field                     | Description
|---------------------------|---------------------------------------------------
| `id`                      | The identifier of the record.
| `signingCertificate`      | The signing certificate.
| `signingKey`              | The signing key.
| `encryptionCertificate`   | The encryption certificate.
| `encryptionKey`           | The encryption key.
| `metadata`                | The SAML2 identity provider metadata.
| `appliesTo`               | The owner of the SAML2 identity provider metadata (i.e. `CAS`).

Note that the signing and encryption keys are expected to be encrypted and signed using CAS crypto keys. To see the relevant CAS properties, please [see this guide](../configuration/Configuration-Properties.html#saml-metadata-jpa).

#### Per Service

Identity provider metadata, certificates and keys can also be defined on a per-service basis to override the global defaults.
Metadata documents that would be applicable to a service definition need to adjust the `appliesTo` column in the metadata
document to carry the service definition's name and numeric identifier using the `[service-name]-[service-numeric-identifier]` format.

## CouchDb

Metadata documents may also be stored in and fetched from a NoSQL database. This may specially be used to avoid copying metadata files across CAS nodes in a cluster, particularly where one needs to deal with more than a few bilateral SAML integrations. Metadata documents are stored in and fetched from a single pre-defined table  (i.e. `SamlMetadataDocument`) whose connection information is taught to CAS via settings and is automatically generated.  The outline of the database document is as follows:

| Field                     | Description
|--------------|---------------------------------------------------
| `id`                          | The identifier of the record.
| `name`             | Indexed field which describes and names the metadata briefly.
| `value`              | The XML document representing the metadata for the service provider.
| `signature`              | The contents of the signing certificate to validate metadata, if any.

Support is enabled by including the following module in the overlay:

```xml
<dependency>
  <groupId>org.apereo.cas</groupId>
  <artifactId>cas-server-support-saml-idp-metadata-couchdb</artifactId>
  <version>${cas.version}</version>
</dependency>
```

SAML service definitions must then be designed as follows to allow CAS to fetch metadata documents from CouchDb instances:

```json
{
  "@class" : "org.apereo.cas.support.saml.services.SamlRegisteredService",
  "serviceId" : "the-entity-id-of-the-sp",
  "name" : "SAMLService",
  "id" : 10000003,
  "description" : "A relational-db-based metadata resolver",
  "metadataLocation" : "couchdb://",
}
```

<div class="alert alert-info"><strong>Metadata Location</strong><p>
The metadata location in the registration record above simply needs to be specified as <code>couchdb://</code> to signal to CAS that SAML metadata for registered service provider must be fetched from CouchDb as defined in CAS configuration.
</p></div>

Note that the signing and encryption keys are expected to be encrypted and signed using CAS crypto keys. To see the relevant CAS properties, please [see this guide](../configuration/Configuration-Properties.html#saml-metadata-couchdb).

### Identity Provider Metadata

Metadata artifacts that belong to CAS as a SAML2 identity provider may also be managed and stored via CouchDb. Artifacts such as the metadata, signing and encryption keys, etc are kept
inside a database with documents that would have the following structure:

| Field                     | Description
|---------------------------|---------------------------------------------------
| `id`                      | The identifier of the record.
| `signingCertificate`      | The signing certificate.
| `signingKey`              | The signing key.
| `encryptionCertificate`   | The encryption certificate.
| `encryptionKey`           | The encryption key.
| `metadata`                | The SAML2 identity provider metadata.
| `appliesTo`               | The owner of the SAML2 identity provider metadata (i.e. `CAS`).

Note that the signing and encryption keys are expected to be encrypted and signed using CAS crypto keys. To see the relevant CAS properties, please [see this guide](../configuration/Configuration-Properties.html#saml-metadata-couchdb).

#### Per Service

Identity provider metadata, certificates and keys can also be defined on a per-service basis to override the global defaults.
Metadata documents that would be applicable to a service definition need to adjust the `appliesTo` field in the metadata
document to carry the service definition's name and numeric identifier using the `[service-name]_[service-numeric-identifier]` format.

## Groovy

A metadata location for a SAML service definition may  point to an external Groovy script, allowing the script to programmatically 
determine and build the metadata resolution machinery to be added to the collection of the existing resolvers. 

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
    def criteriaSet = args[3]
    def logger = args[4]

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
| `criteriaSet`         | The object responsible for capturing the criteria for metadata solution, if any.
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

The following parameters are expected for the Amazon S3 object metadata:

| Parameter             | Description
|-----------------------|-------------------------------------------------------
| `signature`           | The metadata signing certificate, if any.

<div class="alert alert-info"><strong>Metadata Location</strong><p>
The metadata location in the registration record above simply needs to be specified as <code>awss3://</code> to signal to CAS that 
SAML metadata for registered service provider must be fetched from Amazon S3 defined in CAS configuration. 
</p></div>

To see the relevant CAS properties, please [see this guide](../configuration/Configuration-Properties.html#saml-metadata-amazon-s3).

### Identity Provider Metadata

Metadata artifacts that belong to CAS as a SAML2 identity provider may also be managed and stored via Amazon S3 buckets. Artifacts such as the metadata, signing and encryption keys, etc are kept
inside a bucket with metadata that would have the following structure:

| Field                     | Description
|---------------------------|---------------------------------------------------
| `id`                      | The identifier of the record.
| `signingCertificate`      | The signing certificate.
| `signingKey`              | The signing key.
| `encryptionCertificate`   | The encryption certificate.
| `encryptionKey`           | The encryption key.
| `appliesTo`               | The owner of this metadata document (i.e. `CAS`).

The actual object's content/body is expected to contain the SAML2 identity provider metadata. Note that the signing and encryption keys are expected to be encrypted and signed using CAS crypto keys. 

To see the relevant CAS properties, please [see this guide](../configuration/Configuration-Properties.html#saml-metadata-amazon-s3).

#### Per Service

Identity provider metadata, certificates and keys can also be defined on a per-service basis to override the global defaults.
Metadata documents that would be applicable to a service definition need to be put in a special bucket named 
using the `[service-name][service-numeric-identifier]` format.
