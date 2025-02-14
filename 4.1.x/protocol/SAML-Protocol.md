---
layout: default
title: CAS - CAS SAML Protocol
---

# SAML Protocol
CAS has support for versions 1.1 and 2 of the SAML protocol to a specific extent. This document deals with CAS-specific concerns.

Support is enabled by including the following dependency in the Maven WAR overlay:

{% highlight xml %}
<dependency>
  <groupId>org.jasig.cas</groupId>
  <artifactId>cas-server-support-saml</artifactId>
  <version>${cas.version}</version>
</dependency>
{% endhighlight %}

# SAML 1.1
CAS supports the [standardized SAML 1.1 protocol](http://en.wikipedia.org/wiki/SAML_1.1) primarily to:

- Support a method of [attribute release](../integration/Attribute-Release.html)
- [Single Logout](../installation/Logout-Single-Signout.html)

A SAML 1.1 ticket validation response is obtained by validating a ticket via POST at the `/samlValidate URI`.


## Sample Request
{% highlight xml %}
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
{% endhighlight %}


## Sample Response
{% highlight xml %}
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
{% endhighlight %}


## Configuration

In addition to the `cas-server-support-saml` module dependency, the following steps are required to enabled the SAML 1.1 support.

### Definition/Mapping of `samlValidateController` 

In `cas-servlet.xml`:

{% highlight xml %}
<bean id="samlValidateController" class="org.jasig.cas.web.ServiceValidateController"
  p:validationSpecificationClass="org.jasig.cas.validation.Cas20WithoutProxyingValidationSpecification"
  p:centralAuthenticationService-ref="centralAuthenticationService"
  p:proxyHandler-ref="proxy20Handler"
  p:servicesManager-ref="servicesManager"
  p:argumentExtractor-ref="samlArgumentExtractor"
  p:successView="casSamlServiceSuccessView"
  p:failureView="casSamlServiceFailureView"/>

<bean id="handlerMappingC" class="org.springframework.web.servlet.handler.SimpleUrlHandlerMapping">
  <property name="mappings">
    <props>
      ...
      <prop key="/samlValidate">samlValidateController</prop>
      ...
{% endhighlight %}

### Servlet mapping for `/samlValidate` 

In the `web.xml` file:

{% highlight xml %}
<servlet-mapping>
  <servlet-name>cas</servlet-name>
  <url-pattern>/samlValidate</url-pattern>
</servlet-mapping>
{% endhighlight %}

### SAML Argument Extractor 

In the `argumentExtractorsConfiguration.xml` file:

{% highlight xml %}
<bean id="samlArgumentExtractor" class="org.jasig.cas.support.saml.web.support.SamlArgumentExtractor" />

<util:list id="argumentExtractors">
  <ref bean="casArgumentExtractor" />
  <ref bean="samlArgumentExtractor" />
</util:list>
{% endhighlight %}

### SAML ID Generator

In the uniqueIdGenerators.xml file:

{% highlight xml %}
<bean id="samlServiceTicketUniqueIdGenerator" class="org.jasig.cas.support.saml.util.SamlCompliantUniqueTicketIdGenerator">
  <constructor-arg index="0" value="[CAS-FQ-HOST-NAME]" />
</bean>

<util:map id="uniqueIdGeneratorsMap">
  <entry
    key="org.jasig.cas.authentication.principal.SimpleWebApplicationServiceImpl"
    value-ref="serviceTicketUniqueIdGenerator" />
  <entry
    key="org.jasig.cas.support.saml.authentication.principal.SamlService"
    value-ref="samlServiceTicketUniqueIdGenerator" />
</util:map>
{% endhighlight %}

### SAML Views 
In `cas-servlet.xml`, uncomment the following:

{% highlight xml %}
<bean id="xmlViewResolver" class="org.springframework.web.servlet.view.XmlViewResolver"
          p:order="3"
          p:location="${cas.viewResolver.xmlFile:classpath:/META-INF/spring/saml-protocol-views.xml}" />
{% endhighlight %}

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

CAS support for SAML 2 at this point is mostly limited to [Google Apps Integration](../integration/Google-Apps-Integration.html). Full SAML 2 support can also be achieved via Shibboleth with CAS handling the authentication and SSO. [See this guide](../integration/Shibboleth.html) for more info.

