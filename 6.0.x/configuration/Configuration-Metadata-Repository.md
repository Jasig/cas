---
layout: default
title: CAS Configuration Metadata Repository
category: Configuration
---

# Configuration Metadata

CAS ships with meta-data files that provide details of all supported configuration properties and settings. The repository of all configuration metadata
is generated automatically at build and release time by processing all items annotated with `@ConfigurationProperties` within the codebase. This repository
is then made available for additional querying and filtering to look up definitions of a given property or locate relevant settings 
that apply to a particular group of functionality in CAS, such as LDAP authentication.

Configuration metadata may also be accessed and queried using the CAS actuator endpoints. [See this guide](../monitoring/Monitoring-Statistics.html) to learn more.

## Metadata Endpoint

Configuration metadata can be queried via the following endpoint that are prefixed at `/actuator/configuration-metadata/{name}`. The default endpoint present a list of all settings
recognized by CAS, with the added capability to search for specific CAS setting by its partial `name`. This interface ships with CAS by default and you need 
not do anything special to enable it.

## Metadata via Commandline

The metadata repository can also be examined using the command-line as a separate utility. 
[See this guide](../installation/Configuring-Commandline-Shell.html) for more info.
