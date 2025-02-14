---
layout: default
title: CAS - Attribute Resolution
category: Attributes
---

# Attribute Resolution

Attribute resolution strategies are controlled by
the [Person Directory project](https://github.com/apereo/person-directory).
The Person Directory dependency is automatically bundled with the CAS server. Therefore,
declaring an additional dependency will not be required.
This Person Directory project supports both LDAP and JDBC attribute resolution,
caching, attribute aggregation from multiple attribute sources, etc.

<div class="alert alert-info"><strong>Default Caching Policy</strong><p>By default,
attributes are cached to the length of the SSO session.
This means that while the underlying component provided by Person Directory may have
a different caching model, attributes by default and from
a CAS perspective will not be refreshed and retrieved again on subsequent requests
as long as the SSO session exists.</p></div>

## Administrative Endpoints

The following endpoints are provided by CAS:
 
| Endpoint                 | Description
|--------------------------|------------------------------------------------
| `resolveAttributes/{name}`    | Invoke the CAS [attribute resolution](Attribute-Resolution.html) engine to locate attributes for `{name}`.

## Person Directory

A framework for resolving persons and attributes from a variety of underlying sources.
It consists of a collection of components that retrieve, cache, resolve, aggregate,
merge person attributes from JDBC, LDAP and more.

To see the relevant list of CAS properties that deal with resolving principals, please [review this guide](../configuration/Configuration-Properties.html#principal-resolution).

Attribute sources are defined and configured to describe the global set of attributes to be fetched
for each authenticated principal. That global set of attributes is then filtered by the
service manager according to service-specific attribute release rules.

Note that each attribute repository source can be assigned a unique identifier to be used for additional filtering. The attribute resolution engine
provided by Person Directory can also be configured to only consult not all but a selection of attribute repository sources, *deferring* the task
of attribute retrieval for later phases in the authentication process, such as [releasing attributes](Attribute-Release-Caching.html).

<div class="alert alert-info"><strong>Principal Resolution</strong><p>Note that in most if not all cases,
CAS authentication is able to retrieve and resolve attributes from the authentication source, which would
eliminate the need for configuring a separate resolver specially if both the authentication and the attribute source are the same.
Using separate resolvers should only be required when sources are different, or when there is a need to tackle more advanced attribute
resolution use cases such as cascading, merging, etc. <a href="../installation/Configuring-Principal-Resolution.html">See this guide</a> for more info.</p></div>

The goal of the resolver is to construct a final identifiable authenticated principal for CAS which carries a number of attributes inside it.
The behavior of the person-directory resolver is such that it attempts to locate the principal id, which in most cases is the same thing as the credential
id provided during authentication or it could be noted by a custom attribute. Then the resolver starts to construct attributes from attribute repositories defined. If it realizes that a custom attribute is used to determine the principal id AND the same attribute is also set to be collected into the final set of attributes, it will then remove that attribute from the final collection.

Note that by default, CAS auto-creates attribute repository sources that are appropriate for LDAP, JDBC, etc.
If you need something more, you will need to resort to more elaborate measures of defining the bean configuration.

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#authentication-attributes).
More about the Person Directory and its configurable sources [can be found here](https://github.com/apereo/person-directory).

### JDBC

CAS does allow for attributes to be retrieved from a variety of SQL databases.
To learn how to configure database drivers, [please see this guide](../installation/JDBC-Drivers.html).

JDBC attribute sources can be defined based on the following mechanics:

#### Single Row

Designed to work against a table where there is a mapping of one row to one user.
An example of this table format would be:

| uid      | first_name | last_name | email
|----------|------------|-----------|----------------------
| `jsmith` | `John`     | `Smith`   | `jsmith@example.org`

#### Multi Row

Designed to work against a table where there is a mapping of one row to one user.
An example of this table format would be:

| uid      | attr_name    | attr_value
|----------|--------------|-----------------------------
| `jsmith` | `first_name` | `John`
| `jsmith` | `last_name`  | `Smith`
| `jsmith` | `email`      | `jsmith@example.org`

You will need to define column mappings
in your configuration to map the `attr_name` column to the `attr_value` column

## Examples

Suppose CAS is configured to authenticate against Active Directory. The account whose details are defined below
authenticates via `sAMAccountName`.

| Attribute            | Value
|--------------------- |-----------------------
| `sAMAccountName`     | `johnsmith`
| `cn`                 | `John Smith`

### Example #1

If the resolver is configured to use `sAMAccoutName` as the attribute for the principal id, then when authentication is complete the resolver attempts
to construct attributes from attribute repository sources, it sees `sAMAccoutName` as the attribute and sees the principal id is to
be created by `sAMAccoutName`. So it would remove the `sAMAccoutName` from the attributes.
The final result is is a principal whose id is `johnsmith` who has a `cn` attribute of `John Smith`.

### Example #2

If the resolver is configured to use `cn` as the attribute for the principal id, then when authentication is complete the resolver attempts to
construct attributes from attribute repository sources. It then sees `sAMAccoutName` as the attribute and sees the principal id is to be created by `cn`.
So it would remove the `cn` from the attributes. The final result is is a principal whose id is `John Smith`
who has a `sAMAccountName` attribute of `johnsmith`.

## Attribute Definitions

CAS attributes may be decorated with additional metadata which can later be used depending on the 
requirements of the protocol and nature of the integration with a target application. To learn 
more, please [see this guide](Attribute-Definitions.html).
