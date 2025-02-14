---
layout: default
title: CAS - YAML Service Registry
category: Services
---

# YAML Service Registry

This registry reads services definitions from YAML configuration files at the application context initialization time.
YAML files are expected to be found inside a configured directory location and this registry will recursively look through
the directory structure to find relevant files.

Support is enabled by adding the following module into the overlay:

```xml
<dependency>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-yaml-service-registry</artifactId>
    <version>${cas.version}</version>
</dependency>
```

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#yaml-service-registry).


A sample YAML file follows:

```yml
--- !<org.apereo.cas.services.RegexRegisteredService>
serviceId: "testId"
name: "YAML"
id: 1000
description: "description"
attributeReleasePolicy: !<org.apereo.cas.services.ReturnAllAttributeReleasePolicy> {}
accessStrategy: !<org.apereo.cas.services.DefaultRegisteredServiceAccessStrategy>
  enabled: true
  ssoEnabled: true
```

<div class="alert alert-warning"><strong>YAML Validation</strong><p>
The tags containing classname hints (<code>!&lt;classname&gt;</code>) cause problems with many YAML validators. If you need to validate your YAML, try removing those tags for validation. Remember that an empty map (<code>{}</code>) may be required after the tag if you are not including any attributes for a property.
</p></div>

<div class="alert alert-warning"><strong>Clustering Services</strong><p>
You MUST consider that if your CAS server deployment is clustered, each CAS node in the cluster must have
access to the same set of configuration files as the other, or you may have to devise a strategy to keep
changes synchronized from one node to the next.
</p></div>

The service registry is also able to auto detect changes to the specified directory. It will monitor changes to recognize
file additions, removals and updates and will auto-refresh CAS so changes do happen instantly.

<div class="alert alert-info"><strong>Escaping Characters</strong><p>
Please make sure all field values in the blob are correctly escaped, specially for the service id. If the service is defined as a regular expression, certain regex constructs such as "." and "\d" need to be doubly escaped.
</p></div>


The naming convention for new files is recommended to be the following:

```bash
YAML fileName = serviceName + "-" + serviceNumericId + ".yml"
```

Remember that because files are created based on the `serviceName`, you will need to make sure [characters considered invalid for file names](https://en.wikipedia.org/wiki/Filename#Reserved_characters_and_words) are not used as part of the name. Furthermore, note that CAS **MUST** be given full read/write permissions on directory which contains service definition files.

<div class="alert alert-warning"><strong>Duplicate Services</strong><p>
As you add more files to the directory, you need to be absolutely sure that no two service definitions
will have the same id. If this happens, loading one definition will stop loading the other. While service ids
can be chosen arbitrarily, make sure all service numeric identifiers are unique. CAS will also output warnings
if duplicate data is found.
</p></div>


## Replication

If CAS is deployed in a cluster, the service definition files must be kept in sync for all CAS nodes. Please [review this guide](Configuring-Service-Replication.html) to learn more about available options.
