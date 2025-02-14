---
layout: default
title: CAS - CAS SAML Protocol
category: Protocols
---

# SAML Protocol

CAS has support for versions 1.1 and 2 of the SAML protocol to a specific extent.
This document deals with CAS-specific concerns.

## SAML2

CAS provides support for [SAML2 Authentication](../installation/Configuring-SAML2-Authentication.html).

## Google Apps

CAS provides support for [Google Apps Integration](../integration/Google-Apps-Integration.html).

## SAML 1.1

CAS supports the [standardized SAML 1.1 protocol](http://en.wikipedia.org/wiki/SAML_1.1) primarily to:

- Support a method of [attribute release](../integration/Attribute-Release.html)
- [Single Logout](../installation/Logout-Single-Signout.html)

A SAML 1.1 ticket validation response is obtained by validating a ticket via POST at the `/samlValidate URI`.

Support is enabled by including the following dependency in the WAR overlay:

```xml
<dependency>
  <groupId>org.apereo.cas</groupId>
  <artifactId>cas-server-support-saml</artifactId>
  <version>${cas.version}</version>
</dependency>
```

### Administrative Endpoints

The following endpoints are provided by CAS:
 
| Endpoint          | Description
|-------------------|---------------------------------------------------------------------------------------------------
| `samlValidate`    | Obtain a SAML 1.1 validation payload by supplying a `username`, `password` and `service` as parameters.

### Sample Request

```xml
POST /cas/samlValidate?ticket=
Host: cas.example.com
Content-Length: 491
Content-Type: text/xml

<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
  <SOAP-ENV:Header/>
  <SOAP-ENV:Body>
    <samlp:Request xmlns:samlp="urn:oasis:names:tc:SAML:1.0:protocol" MajorVersion="1"
      MinorVersion="1" RequestID="_192.168.16.51.1024506224022"
      IssueInstant="2002-06-19T17:03:44.022Z">
      <samlp:AssertionArtifact>
        ST-1-u4hrm3td92cLxpCvrjylcas.example.com
      </samlp:AssertionArtifact>
    </samlp:Request>
  </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
```

### Sample Response

```xml
<?xml version="1.0" encoding="UTF-8"?>
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
  <SOAP-ENV:Body>
    <saml1p:Response xmlns:saml1p="urn:oasis:names:tc:SAML:1.0:protocol" InResponseTo="...." IssueInstant="2017-08-15T06:30:04.622Z" MajorVersion="1" MinorVersion="1" ResponseID="_bf6957bad275fc74a1c079a445581441">
      <saml1p:Status>
        <saml1p:StatusCode Value="saml1p:Success" />
      </saml1p:Status>
      <saml1:Assertion xmlns:saml1="urn:oasis:names:tc:SAML:1.0:assertion" AssertionID="_d9673d8af414cc9612929480b58cb2a1" IssueInstant="2017-08-15T06:30:04.622Z" Issuer="testIssuer" MajorVersion="1" MinorVersion="1">
        <saml1:Conditions NotBefore="2017-08-15T06:30:04.622Z" NotOnOrAfter="2017-08-15T06:30:05.622Z">
          <saml1:AudienceRestrictionCondition>
            <saml1:Audience>https://google.com</saml1:Audience>
          </saml1:AudienceRestrictionCondition>
        </saml1:Conditions>
        <saml1:AuthenticationStatement AuthenticationInstant="2017-08-15T06:46:43.585Z" AuthenticationMethod="urn:ietf:rfc:2246">
          <saml1:Subject>
            <saml1:NameIdentifier>testPrincipal</saml1:NameIdentifier>
            <saml1:SubjectConfirmation>
              <saml1:ConfirmationMethod>urn:oasis:names:tc:SAML:1.0:cm:artifact</saml1:ConfirmationMethod>
            </saml1:SubjectConfirmation>
          </saml1:Subject>
        </saml1:AuthenticationStatement>
        <saml1:AttributeStatement>
          <saml1:Subject>
            <saml1:NameIdentifier>testPrincipal</saml1:NameIdentifier>
            <saml1:SubjectConfirmation>
              <saml1:ConfirmationMethod>urn:oasis:names:tc:SAML:1.0:cm:artifact</saml1:ConfirmationMethod>
            </saml1:SubjectConfirmation>
          </saml1:Subject>
          <saml1:Attribute AttributeName="testAttribute" AttributeNamespace="whatever">
            <saml1:AttributeValue>testValue</saml1:AttributeValue>
          </saml1:Attribute>
          <saml1:Attribute AttributeName="samlAuthenticationStatementAuthMethod" AttributeNamespace="whatever">
            <saml1:AttributeValue>urn:ietf:rfc:2246</saml1:AttributeValue>
          </saml1:Attribute>
          <saml1:Attribute AttributeName="testSamlAttribute" AttributeNamespace="whatever">
            <saml1:AttributeValue>value</saml1:AttributeValue>
          </saml1:Attribute>
          <saml1:Attribute AttributeName="testAttributeCollection" AttributeNamespace="whatever">
            <saml1:AttributeValue>tac1</saml1:AttributeValue>
            <saml1:AttributeValue>tac2</saml1:AttributeValue>
          </saml1:Attribute>
        </saml1:AttributeStatement>
      </saml1:Assertion>
    </saml1p:Response>
  </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
```


## Configuration

To see the relevant list of CAS properties, please [review this guide](../configuration/Configuration-Properties.html#saml-core).

You may also need to declare the following repository in
your CAS Overlay to be able to resolve dependencies:

```xml
<repositories>
    ...
    <repository>
        <id>shibboleth-releases</id>
        <url>https://build.shibboleth.net/nexus/content/repositories/releases</url>
    </repository>
    ...
</repositories>
```
