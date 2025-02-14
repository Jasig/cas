---
layout: default
title: CAS - JSON Service Registry
---

# JSON Service Registry

This registry reads services definitions from JSON configuration files at the application context initialization time.
JSON files are expected to be found inside a configured directory location and this registry will recursively look through the directory structure to find relevant JSON files.

Support is enabled by adding the following module into the overlay:

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-json-service-registry</artifactId>
    <version>${cas.version}</version>
</dependency>
```

A sample JSON file follows:

```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "testId",
  "name" : "testJsonFile",
  "id" : 103935657744185,
  "evaluationOrder" : 10
}
```

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#json-service-registry).

<div class="alert alert-warning"><strong>Clustering Services</strong><p>
You MUST consider that if your CAS server deployment is clustered, each CAS node in the cluster must have
access to the same set of JSON configuration files as the other, or you may have to devise a strategy to keep
changes synchronized from one node to the next.
</p></div>

The JSON service registry is also able to auto detect changes to the specified directory. It will monitor changes to recognize
file additions, removals and updates and will auto-refresh CAS so changes do happen instantly.

<div class="alert alert-info"><strong>Escaping Characters</strong><p>
Please make sure all field values in the JSON blob are correctly escaped, specially for the service id. If the service is defined as a regular expression, certain regex constructs such as "." and "\d" need to be doubly escaped.
</p></div>

The naming convention for new JSON files is recommended to be the following:

```bash
JSON fileName = serviceName + "-" + serviceNumericId + ".json"
```

Based on the above formula, for example the above JSON snippet shall be named: `testJsonFile-103935657744185.json`. Remember that because files are created based on the `serviceName`, you will need to make sure [characters considered invalid for file names](https://en.wikipedia.org/wiki/Filename#Reserved_characters_and_words) are not used as part of the name. Furthermore, note that CAS **MUST** be given full read/write permissions on directory which contains service definition files.

<div class="alert alert-warning"><strong>Duplicate Services</strong><p>
As you add more files to the directory, you need to be absolutely sure that no two service definitions
will have the same id. If this happens, loading one definition will stop loading the other. While service ids
can be chosen arbitrarily, make sure all service numeric identifiers are unique. CAS will also output warnings
if duplicate data is found.
</p></div>

## JSON Syntax

CAS uses [a version of the JSON syntax](http://hjson.org/) that provides a much more relaxed
syntax with the ability to specify comments.

A given JSON file for instance could be formatted as such in CAS:

```json
{
  /*
    Generic service definition that applies to https/imaps urls
    that wish to register with CAS for authentication.
  */
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "^(https|imaps)://.*",
  "name" : "HTTPS and IMAPS",
  "id" : 10000001,
}
```

Note the trailing comma at the end. See the above link for more info on the alternative syntax.

## Legacy Syntax

A number of legacy service definitions, supported by CAs automatically, are listed below.

## CAS Add-ons

Originally [developed as an extension](https://github.com/Unicon/cas-addons/wiki/Configuring-JSON-Service-Registry) for CAS `3.5.x`, this add-on provided JSON syntax support in form of a single file that contained all service definitions. An example legacy JSON file is listed below for reference:

```json
{
    "services":[
        {
            "id":1,
            "serviceId":"https://www.example.com/**",
            "name":"GOOGLE",
            "description":"Test service with ant-style pattern matching",
            "theme":"my_example_theme",
            "allowedToProxy":true,
            "enabled":true,
            "ssoEnabled":true,
            "anonymousAccess":false,
            "evaluationOrder":1,
            "allowedAttributes":["uid", "mail"]
        }
    ]
}
```

CAS is able to transform this definition into one that is officially supported. The results of transformations are written into a temporary file where the user is warned about the presence of this legacy behavior and the location of the transformed files. Changes should be reviewed and ultimately put into use in the relevant directory location to be loaded by the registry.

To activate support for this legacy syntax, the services registry file needs to be renamed `servicesRegistry.json` and must be placed in the same directory 
as all other JSON service definition files.

A few things to note:

- The `extraAttributes` property is ignored and may not be transformed.
- Service identifier patterns in the legacy syntax may be specified as [ant patterns](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/util/AntPathMatcher.html). These patterns are automatically massaged by CAS in *small ways* during transformations to ensure they are turned into a valid regular expression as much as possible. You should of course review the results and make any manual modifications necessary to make the pattern functional.

### Jasig Namespace

CAS automatically should remain backward compatible with service definitions
that were created by a CAS `4.2.x` instance. Warnings should show up in the logs
when such deprecated service definitions are found. Deployers are advised to review each definition
and consult the docs to apply the new syntax.

An example legacy JSON file is listed below for reference:

```json
{
  "@class" : "org.jasig.cas.services.RegexRegisteredService",
  "serviceId" : "^https://www.jasig.org/cas",
  "name" : "Legacy",
  "id" : 100,
  "description" : "This service definition authorizes the legacy jasig/cas URL. It is solely here to demonstrate service backwards-compatibility",
  "proxyPolicy" : {
    "@class" : "org.jasig.cas.services.RefuseRegisteredServiceProxyPolicy"
  },
  "evaluationOrder" : 100,
  "usernameAttributeProvider" : {
    "@class" : "org.jasig.cas.services.DefaultRegisteredServiceUsernameProvider"
  },
  "logoutType" : "BACK_CHANNEL",
  "attributeReleasePolicy" : {
    "@class" : "org.jasig.cas.services.ReturnAllowedAttributeReleasePolicy",
    "principalAttributesRepository" : {
      "@class" : "org.jasig.cas.authentication.principal.cache.CachingPrincipalAttributesRepository",
      "duration" : {
        "@class" : "javax.cache.expiry.Duration",
        "timeUnit" : [ "java.util.concurrent.TimeUnit", "HOURS" ],
        "expiration" : 2
      },
      "mergingStrategy" : "NONE"
    },
    "authorizedToReleaseCredentialPassword" : false,
    "authorizedToReleaseProxyGrantingTicket" : false
  },
  "accessStrategy" : {
    "@class" : "org.jasig.cas.services.DefaultRegisteredServiceAccessStrategy",
    "enabled" : true,
    "ssoEnabled" : true
  }
}
```

## Replication

If CAS is to deployed in a cluster, the service definition files must be kept in sync for all CAS nodes. Please [review this guide](Configuring-Service-Replication.html) to learn more about available options.

## Auto Initialization

Upon startup and configuration permitting, the registry is able to auto initialize itself from default JSON service definitions available to CAS. See [this guide](AutoInitialization-Service-Management.html) for more info.
