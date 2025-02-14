---
layout: default
title: CAS - CAS SAML Protocol
---

# SAML Protocol
CAS has support for versions 1.1 and 2 of the SAML protocol to a specific extent. This document deals with CAS-specific concerns.

Support is enabled by including the following dependency in the Maven WAR overlay:

```xml
<dependency>
  <groupId>org.jasig.cas</groupId>
  <artifactId>cas-server-support-saml</artifactId>
  <version>${cas.version}</version>
</dependency>
```

# SAML 1.1
CAS supports the [standardized SAML 1.1 protocol](http://en.wikipedia.org/wiki/SAML_1.1) primarily to:

- Support a method of [attribute release](../integration/Attribute-Release.html)
- [Single Logout](../installation/Logout-Single-Signout.html)

A SAML 1.1 ticket validation response is obtained by validating a ticket via POST at the `/samlValidate URI`.


## Sample Request
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


## Sample Response
```xml
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
  <SOAP-ENV:Header />
  <SOAP-ENV:Body>
    <Response xmlns="urn:oasis:names:tc:SAML:1.0:protocol" xmlns:saml="urn:oasis:names:tc:SAML:1.0:assertion"
    xmlns:samlp="urn:oasis:names:tc:SAML:1.0:protocol" xmlns:xsd="http://www.w3.org/2001/XMLSchema"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" IssueInstant="2008-12-10T14:12:14.817Z"
    MajorVersion="1" MinorVersion="1" Recipient="https://eiger.iad.vt.edu/dat/home.do"
    ResponseID="_5c94b5431c540365e5a70b2874b75996">
      <Status>
        <StatusCode Value="samlp:Success">
        </StatusCode>
      </Status>
      <Assertion xmlns="urn:oasis:names:tc:SAML:1.0:assertion" AssertionID="_e5c23ff7a3889e12fa01802a47331653"
      IssueInstant="2008-12-10T14:12:14.817Z" Issuer="localhost" MajorVersion="1"
      MinorVersion="1">
        <Conditions NotBefore="2008-12-10T14:12:14.817Z" NotOnOrAfter="2008-12-10T14:12:44.817Z">
          <AudienceRestrictionCondition>
            <Audience>
              https://some-service.example.com/app/
            </Audience>
          </AudienceRestrictionCondition>
        </Conditions>
        <AttributeStatement>
          <Subject>
            <NameIdentifier>johnq</NameIdentifier>
            <SubjectConfirmation>
              <ConfirmationMethod>
                urn:oasis:names:tc:SAML:1.0:cm:artifact
              </ConfirmationMethod>
            </SubjectConfirmation>
          </Subject>
          <Attribute AttributeName="uid" AttributeNamespace="http://www.ja-sig.org/products/cas/">
            <AttributeValue>12345</AttributeValue>
          </Attribute>
          <Attribute AttributeName="groupMembership" AttributeNamespace="http://www.ja-sig.org/products/cas/">
            <AttributeValue>
              uugid=middleware.staff,ou=Groups,dc=vt,dc=edu
            </AttributeValue>
          </Attribute>
          <Attribute AttributeName="eduPersonAffiliation" AttributeNamespace="http://www.ja-sig.org/products/cas/">
            <AttributeValue>staff</AttributeValue>
          </Attribute>
          <Attribute AttributeName="accountState" AttributeNamespace="http://www.ja-sig.org/products/cas/">
            <AttributeValue>ACTIVE</AttributeValue>
          </Attribute>
        </AttributeStatement>
        <AuthenticationStatement AuthenticationInstant="2008-12-10T14:12:14.741Z"
        AuthenticationMethod="urn:oasis:names:tc:SAML:1.0:am:password">
          <Subject>
            <NameIdentifier>johnq</NameIdentifier>
            <SubjectConfirmation>
              <ConfirmationMethod>
                urn:oasis:names:tc:SAML:1.0:cm:artifact
              </ConfirmationMethod>
            </SubjectConfirmation>
          </Subject>
        </AuthenticationStatement>
      </Assertion>
    </Response>
  </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
```


## Configuration
SAML configuration in CAS is contained within the `cas.properties` file.

```properties
# Indicates the SAML response issuer
# cas.saml.response.issuer=localhost

# Indicates the skew allowance which controls the issue instant of the SAML response
# cas.saml.response.skewAllowance=0
```

### Repositories
You may also need to declare the following Maven repository in your CAS Overlay to be able to resolve dependencies:

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

# SAML 2

CAS support for SAML 2 at this point is mostly limited to 
[Google Apps Integration](../integration/Google-Apps-Integration.html). 
Full SAML 2 support can also be achieved via Shibboleth with CAS 
handling the authentication and SSO. [See this guide](../integration/Shibboleth.html) for more info.

