---
layout: default
title: CAS - Proxy Authentication
---

# Proxy Authentication

Proxy authentication support for CAS v1+ protocols is enabled by default, thus it is entirely a matter of CAS
client configuration to leverage proxy authentication features.

<div class="alert alert-info"><strong>Service Configuration</strong><p>
Note that each registered application in the registry must explicitly be configured
to allow for proxy authentication. See <a href="Service-Management.html">this guide</a>
to learn about registering services in the registry.
</p></div>

Disabling proxy authentication components is recommended for deployments that wish to strategically avoid proxy
authentication as a matter of security policy. The simplest means of removing support is to remove support for the
`/proxy` and `/proxyValidate` endpoints on the CAS server. The relevant sections of `cas-servlet.xml` are listed
below and the aspects related to proxy authentication may either be commented out or removed altogether.

{% highlight xml %}
<bean
    id="handlerMappingC"
    class="org.springframework.web.servlet.handler.SimpleUrlHandlerMapping"
    p:alwaysUseFullPath="true">
  <property name="mappings">
    <util:properties>
      <prop key="/serviceValidate">serviceValidateController</prop>
      <prop key="/validate">legacyValidateController</prop>
      <prop key="/proxy">proxyController</prop>
      <prop key="/proxyValidate">proxyValidateController</prop>
      <prop key="/authorizationFailure.html">passThroughController</prop>
      <prop key="/status">healthCheckController</prop>
      <prop key="/statistics">statisticsController</prop>
    </util:properties>
  </property>
</bean>

<bean id="proxyController" class="org.jasig.cas.web.ProxyController"
      p:centralAuthenticationService-ref="centralAuthenticationService"/>

<bean id="proxyValidateController" class="org.jasig.cas.web.ServiceValidateController"
      p:centralAuthenticationService-ref="centralAuthenticationService"
      p:proxyHandler-ref="proxy20Handler"
      p:argumentExtractor-ref="casArgumentExtractor"/>
{% endhighlight %}


## Proxy Handlers
Components responsible to determine what needs to be done to handle proxies.


### `CAS10ProxyHandler`
Proxy handler compliant with CAS v1 protocol that is designed to not handle proxy requests and simply return nothing as proxy support in the protocol is absent.

{% highlight xml %}
<bean id="proxy10Handler" class="org.jasig.cas.ticket.proxy.support.Cas10ProxyHandler"/>
{% endhighlight %}


### `CAS20ProxyHandler`
Protocol handler compliant with CAS v2 protocol that is responsible to callback the URL provided and give it a pgtIou and a pgtId.

{% highlight xml %}
<bean id="proxy20Handler" class="org.jasig.cas.ticket.proxy.support.Cas20ProxyHandler"
          p:httpClient-ref="httpClient"
          p:uniqueTicketIdGenerator-ref="proxy20TicketUniqueIdGenerator"/>
{% endhighlight %}

## Handling SSL-enabled Proxy URLs
By default, CAS ships with a bundled HTTP client that is partly responsible to callback the URL
for proxy authentication. Note that this URL need also be authorized by the CAS service registry
before the callback can be made. [See this guide](Service-Management.md) for more info.

If the callback URL is authorized by the service registry, and if the endpoint is under HTTPS
and protected by an SSL certificate, CAS will also attempt to verify the validity of the endpoint's
certificate before it can establish a successful connection. If the certificate is invalid, expired,
missing a step in its chain, self-signed or otherwise, CAS will fail to execute the callback.

The HTTP client of CAS does present a local trust store that is similar to that of the Java platform.
It is recommended that this trust store be used to handle the management of all certificates that need
to be imported into the platform to allow CAS to execute the callback URL successfully. While by default,
the local trust store to CAS is empty, CAS will still utilize **both** the default and the local trust store.
The local trust store should only be used for CAS-related functionality of course, and the trust store file
can be carried over across CAS and Java upgrades, and certainly managed by the source control system that should
host all CAS configuration.

The trust store configuration is inside the `applicationContext.xml` file, as such:

{% highlight xml %}
<bean id="trustStoreSslSocketFactory"
          class="org.jasig.cas.authentication.FileTrustStoreSslSocketFactory"
          c:trustStoreFile="${http.client.truststore.file:classpath:truststore.jks}"
          c:trustStorePassword="${http.client.truststore.psw:changeit}" />
{% endhighlight %}

## Returning PGT in Validation Response
In situations where using `CAS20ProxyHandler` may be undesirable, such that invoking a callback url to receive the proxy granting ticket is not feasible,
CAS may be configured to return the proxy-granting ticket id directly in the validation response. In order to successfully establish trust between the
CAS server and the application, private/public key pairs are generated by the client application and then **the public key** distributed and
configured inside CAS. CAS will use the public key to encrypt the proxy granting ticket id and will issue a new attribute `<proxyGrantingTicketId>`
in the validation response, only if the service is authorized to receive it.

Note that the return of the proxy granting ticket id is only carried out by the CAS validation response, provided the client
application issues a request to the `/p3/serviceValidate` endpoint (or `/p3/proxyValidate`). Other means of returning attributes to CAS, such as SAML1
will **not** support the additional returning of the proxy granting ticket.

### Configuration

### Register Service
Once you have received the public key from the client application owner, it must be first
registered inside the CAS server's service registry. The service that holds the public key above must also
be authorized to receive the PGT
as an attribute for the given attribute release policy of choice.

{% highlight json %}
{
  "@class" : "org.jasig.cas.services.RegexRegisteredService",
  "serviceId" : "^https://.+",
  "name" : "test",
  "id" : 1,
  "evaluationOrder" : 0,
  "attributeReleasePolicy" : {
    "@class" : "org.jasig.cas.services.ReturnAllowedAttributeReleasePolicy",
    "principalAttributesRepository" : {
      "@class" : "org.jasig.cas.authentication.principal.DefaultPrincipalAttributesRepository"
    },
    "authorizedToReleaseCredentialPassword" : false,
    "authorizedToReleaseProxyGrantingTicket" : true
  },
  "publicKey" : {
    "@class" : "org.jasig.cas.services.RegisteredServicePublicKeyImpl",
    "location" : "classpath:RSA1024Public.key",
    "algorithm" : "RSA"
  }
}
{% endhighlight %}

#### Decrypt the PGT id
Once the client application has received the `proxyGrantingTicket` id attribute in the CAS validation response, it can decrypt it
via its own private key. Since the attribute is base64 encoded by default, it needs to be decoded first before
decryption can occur. Here's a sample code snippet:

{% highlight java %}

final Map<?, ?> attributes = ...
final String encodedPgt = (String) attributes.get("proxyGrantingTicket");
final PrivateKey privateKey = ...
final Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
final byte[] cred64 = decodeBase64ToByteArray(encodedPgt);
cipher.init(Cipher.DECRYPT_MODE, privateKey);
final byte[] cipherData = cipher.doFinal(cred64);
return new String(cipherData);

{% endhighlight %}

### Components

- `RegisteredServiceCipherExecutor`
Defines how to encrypt data based on registered service's public key, etc.

- `DefaultRegisteredServiceCipherExecutor`
A default implementation of the `RegisteredServiceCipherExecutor`
that will use the service's public key to initialize the cipher to
encrypt and encode the value. All results are converted to base-64.

- `CasAttributeEncoder`
Parent component that defines how a CAS attribute
is to be encoded and signed in the CAS validation response.

- `DefaultCasAttributeEncoder`
The default implementation of the attribute encoder that will use a per-service key-pair
to encrypt. It will attempt to query the collection of attributes that resolved to determine
which attributes can be encoded. Attributes will be encoded via a `RegisteredServiceCipherExecutor`.

{% highlight xml %}
<bean id="cas3ServiceSuccessView"
    class="org.jasig.cas.web.view.Cas30ResponseView"
    c:view-ref="cas3JstlSuccessView"
    p:successResponse="true"
    p:servicesManager-ref="servicesManager"
    p:casAttributeEncoder-ref="casAttributeEncoder"  />

<bean id="casRegisteredServiceCipherExecutor"
    class="org.jasig.cas.services.DefaultRegisteredServiceCipherExecutor" />

<bean id="casAttributeEncoder"
    class="org.jasig.cas.authentication.support.DefaultCasAttributeEncoder"
    c:servicesManager-ref="servicesManager"
    c:cipherExecutor-ref="casRegisteredServiceCipherExecutor"  />
{% endhighlight %}
