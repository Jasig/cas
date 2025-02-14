---
layout: default
title: CAS - Google Apps Integration
---

# Overview
Google Apps for Education (or any of the Google Apps) utilizes SAML 2.0 to provide an integration point for external authentication services. SAML2 support for Google Apps integration is built via a special `ArgumentExtractor` and accompanying `Service` to provide process and understand SAML 2.0 requests from Google.

Support is enabled by including the following dependency in the Maven WAR overlay:

{% highlight xml %}
<dependency>
  <groupId>org.jasig.cas</groupId>
  <artifactId>cas-server-support-saml</artifactId>
  <version>${cas.version}</version>
</dependency>
{% endhighlight %}

## Generate Public/Private Keys
The first step is to generate DSA/RSA public and private keys. These are used to sign and read the Assertions. After keys are created, the public key needs to be registered with Google.

The keys will also need to be available to the CAS application (but not publicly available over the Internet) via the classpath (i.e. `WEB-INF/classes`) though any location accessible by the user running the web server instance and not served publicly to the Internet is acceptable.  Thus, inside `WEB-INF` is nice because `WEB-INF` is scoped to the web application but not normally served. `/etc/cas/keys/` is also fine as well and protects the key from being overwritten on deploy of a new CAS webapp version.

{% highlight bash %}
openssl genrsa -out private.key 1024
openssl rsa -pubout -in private.key -out public.key -inform PEM -outform DER
openssl pkcs8 -topk8 -inform PER -outform DER -nocrypt -in private.key -out private.p8
openssl req -new -x509 -key private.key -out x509.pem -days 365
{% endhighlight %}

The `public.key` and `private.p8` go into classpath. The `x509.pem` file should be uploaded into Google Apps under Security/SSO.

## Configure CAS
Google Accounts integration within CAS is enabled by simply adding an additional `ArgumentExtractor`. `WEB-INF/spring-configuration/argumentExtractorsConfiguration.xml` should be modified to add the following:

{% highlight xml %}
<bean id="googleAccountsArgumentExtractor" 
    class="org.jasig.cas.support.saml.web.support.GoogleAccountsArgumentExtractor"
      p:skewAllowance="${cas.saml.response.skewAllowance:0}"
      c:privateKey-ref="privateKeyFactoryBean"
      c:publicKey-ref="publicKeyFactoryBean" />

<bean id="privateKeyFactoryBean" class="org.jasig.cas.util.PrivateKeyFactoryBean"
      p:location="classpath:private.p8"
      p:algorithm="RSA" />

<bean id="publicKeyFactoryBean"	class="org.jasig.cas.util.PublicKeyFactoryBean"
      p:location="classpath:public.key"
      p:algorithm="RSA" />
      
<util:list id="argumentExtractors">
...
    <ref bean="googleAccountsArgumentExtractor" />
</util:list>
{% endhighlight %}

Replace the `public.key` and `private.key` with the names of your key files. If they are not available on the classpath, change the location to point to the location of the keys. If you are using DSA instead of RSA, change the algorithm as appropriate.

Ensure the 
You’ll also need to add a new generator in the `WEB-INF/spring-configuration/uniqueIdGenerators.xml` file:

{% highlight xml %}
<util:map id="uniqueIdGeneratorsMap">
...
  <entry
    key="org.jasig.cas.support.saml.authentication.principal.GoogleAccountsService"
    value-ref="serviceTicketUniqueIdGenerator" />
</util:map>
{% endhighlight %}

Also, ensure that Google Apps is registered in your Service Registry, by the `serviceId`: `https://www.google.com/a/YourGoogleDomain/acs`

## Configure Username Attribute 
As an optional step, you can configure an alternate username to be send to Google in the SAML reply. This alternate user name
can be specified in the CAS service registry via [username attribute providers](../installation/Service-Management.html)
for the registered Google Apps service.

## Configure Google
You'll need to provide Google with the URL for your SAML-based SSO service, as well as the URL your users will be redirected to when they log out of a hosted Google application.
Use the following URLs when you are configuring for Google Apps:

* Sign-in page URL: `https://sso.school.edu/cas/login`
* Sign-out page URL: `https://sso.school.edu/cas/logout`
* Change password URL: `https://mgmt.password.edu/`

## Test
Attempt to access a Google-hosted application, such as Google Calendar with the url: `http://calendar.google.com/a/YourGoogleDomain`
