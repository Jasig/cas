---
layout: default
title: CAS - SOAP Authentication
category: Authentication
---

# SOAP Authentication

Verify and authenticate credentials where CAS acts as a SOAP client. Credentials are submitted to the SOAP endpoint whereupon authentication,
the expected response is to return a username, a set of attributes and possibly a status that is loosely based on HTTP status codes which might help
determine the *account status*. 

The current schema is as such:

```xml
<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema"
           xmlns:tns="http://apereo.org/cas"
           targetNamespace="http://apereo.org/cas"
           elementFormDefault="qualified">

    <xs:element name="getSoapAuthenticationRequest">
        <xs:complexType>
            <xs:sequence>
                <xs:element name="username" type="xs:string"/>
            </xs:sequence>
        </xs:complexType>
    </xs:element>
    <xs:element name="getSoapAuthenticationResponse">
        <xs:complexType>
            <xs:sequence>
                <xs:element name="attributes" type="tns:MapItemType" minOccurs="0" maxOccurs="unbounded"/>
                <xs:element name="status" type="xs:int"/>
                <xs:element name="username" type="xs:string" minOccurs="0"/>
                <xs:element name="message" type="xs:string" minOccurs="0"/>
            </xs:sequence>
        </xs:complexType>
    </xs:element>
    <xs:complexType name="MapItemType">
        <xs:sequence>
            <xs:element name="key" type="xs:anyType"/>
            <xs:element name="value" type="xs:anyType"/>
        </xs:sequence>
    </xs:complexType>
</xs:schema>
```

Support is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
  <groupId>org.apereo.cas</groupId>
  <artifactId>cas-server-support-soap-authentication</artifactId>
  <version>${cas.version}</version>
</dependency>
```

## Configuration

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#soap-authentication).

The `status` returned in the SOAP response as interpreted as one of the following values:

| Code           | Result
|----------------|---------------------------------------------
| `200`          | Successful authentication.
| `403`          | Produces a `AccountDisabledException`
| `404`          | Produces a `AccountNotFoundException`
| `423`          | Produces a `AccountLockedException`
| `412`          | Produces a `AccountExpiredException`
| `428`          | Produces a `AccountPasswordMustChangeException`
| Other          | Produces a `FailedLoginException`

## Troubleshooting

To enable additional logging, configure the log4j configuration file to add the following levels:

```xml
...
<AsyncLogger name="org.springframework.ws" level="debug" additivity="false">
    <AppenderRef ref="console"/>
    <AppenderRef ref="file"/>
</AsyncLogger>
<AsyncLogger name="org.apache.ws" level="debug" additivity="false">
    <AppenderRef ref="console"/>
    <AppenderRef ref="file"/>
</AsyncLogger>
<AsyncLogger name="org.apache.wss4j" level="debug" additivity="false">
    <AppenderRef ref="console"/>
    <AppenderRef ref="file"/>
</AsyncLogger>
...
```
