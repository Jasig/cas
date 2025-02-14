---
layout: default
title: CAS - Legacy Authentication
---

# Legacy Authentication
Legacy authentication components are enabled by including the following dependencies in the Maven WAR overlay:

{% highlight xml %}
    <dependency>
         <groupId>org.jasig.cas</groupId>
         <artifactId>cas-server-support-legacy</artifactId>
         <version>${cas.version}</version>
    </dependency>
{% endhighlight %}

## Legacy Components
CAS provides the following components to accommodate different legacy authentication needs for backwards compatibility:


### `LegacyAuthenticationHandlerAdapter`
Adapts a CAS 3.x `AuthenticationHandler` onto a CAS 4.x `AuthenticationHandler`. If the supplied legacy authentication handler supports `NamedAuthenticationHandler`, then its defined name will be used to identify the handler. Otherwise, the name of the class itself will be used.


### `CredentialsAdapter`
Interface to be implemented by adapters to determine how credentials need be converted over to CAS 4.


#### `UsernamePasswordCredentialsAdapter`
Adapts and converts a CAS 4 username/password credential into a CAS 3.x username/password credential.


### Sample Configuration

{% highlight xml %}
<bean id="legacyAuthHandler"
      class="org.jasig.cas.authentication.LegacyAuthenticationHandlerAdapter"
      c:legacy-ref="cas3LegacyAuthenticationHandler"
      c:adapter-ref="usernamePasswordCredentialsAdapter" />
{% endhighlight %}
