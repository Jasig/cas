---
layout: default
title: CAS Configuration Metadata Repository
---

# Configuration Metadata

CAS ships with meta-data files that provide details of all supported configuration properties and settings. The repository of all configuration metadata
is generated automatically at build and release time by processing all items annotated with `@ConfigurationProperties` within the codebase. This repository
is then made available for additional querying and filtering to look up definitions of a given property or locate relevant settings that apply to a particular group of functionality in CAS, such as LDAP authentication.

Configuration metadata may also be accessed and queried using the CAS dashboard UIs. [See this guide](Monitoring-Statistics.html) to learn more.

## Metadata via REST

Configuration metadata can be queried via the following REST endpoint that are prefixed at `/status/configmetadata`:

| Endpoint              | Method      | Description
|-----------------------|-------------|----------------------------------------------------------
| `/properties`         | `GET`       | List all properties available in the metadata repository.
| `/groups`             | `GET`       | List all groups available in the metadata repository. 
| `/group`              | `GET`       | Look up a group by its name using a `name` request parameter.
| `/property`           | `GET`       | Look up a property by its name using a `name` request parameter.
| `/search`             | `GET`       | Search for a property by its relaxed `name` and aggregated results.

This interface ships with CAS by default and you need not do anything special to configure it.

## Metadata via Commandline

The metadata repository can also be examined using the command-line as a separate utility. 
[See this guide](Configuring-Commandline-Shell.html) for more info.
