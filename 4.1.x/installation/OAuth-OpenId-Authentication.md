---
layout: default
title: CAS - OAuth Authentication
---

# OAuth/OpenID Authentication

<div class="alert alert-info"><strong>CAS as OAuth Server</strong><p>This page specifically describes how to enable OAuth/OpenID server support for CAS. If you would like to have CAS act as an OAuth/OpenID client communicating with other providers (such as Google, Facebook, etc), <a href="../integration/Delegate-Authentication.html">see this page</a>.</p></div>

To get a better understanding of the OAuth/OpenID protocol support in CAS, [see this page](../protocol/OAuth-Protocol.html).

## Configuration
Support is enabled by including the following dependency in the Maven WAR overlay:

{% highlight xml %}
<dependency>
  <groupId>org.jasig.cas</groupId>
  <artifactId>cas-server-support-oauth</artifactId>
  <version>${cas.version}</version>
</dependency>
{% endhighlight %}

# Configuration


## Add the OAuth20WrapperController

To add the `OAuth20WrapperController`, you need to add the mapping between the /oauth2.0/* url and the CAS servlet in the *web.xml* file:

{% highlight xml %}
<servlet-mapping>
  <servlet-name>cas</servlet-name>
  <url-pattern>/oauth2.0/*</url-pattern>
</servlet-mapping>
{% endhighlight %}

You have to create the controller itself in the *cas-servlet.xml* file:

{% highlight xml %}
<bean
  id="oauth20WrapperController"
  class="org.jasig.cas.support.oauth.web.OAuth20WrapperController"
  p:loginUrl="http://mycasserverwithoauthwrapper/login"
  p:servicesManager-ref="servicesManager"
  p:ticketRegistry-ref="ticketRegistry"
  p:timeout="7200" />
{% endhighlight %}

The *loginUrl* is the login url of the CAS server. The timeout is the lifetime of a CAS ticket granting ticket (in seconds, not in milliseconds!) with its mapping in the `handlerMappingC` bean (*cas-servlet.xml* file):

{% highlight xml %}
<bean id="handlerMappingC" class="org.springframework.web.servlet.handler.SimpleUrlHandlerMapping">
  <property name="mappings">
    <props>
      <prop key="/serviceValidate">serviceValidateController</prop>
 
   ...
 
      <prop key="/statistics">statisticsController</prop>
      <prop key="/oauth2.0/*">oauth20WrapperController</prop>
    </props>
  </property>
  <property name="alwaysUseFullPath" value="true" />
</bean>
{% endhighlight %}


## Add the needed CAS services

### Callback Authorization

One service is needed to make the OAuth wrapper works in CAS. It defines the callback url after CAS authentication to return to the OAuth wrapper as a CAS service.  
**Note**: the callback url must end with "callbackAuthorize".

{% highlight xml %}
<bean id="serviceRegistryDao" class="org.jasig.cas.services.InMemoryServiceRegistryDaoImpl">
  <property name="registeredServices">
    <list>
      <!-- A dedicated component to recognize OAuth Callback Authorization requests -->
      <!-- By default, service ids only support regex patterns if/when needed -->
      <bean class="org.jasig.cas.support.oauth.services.OAuthCallbackAuthorizeService"
            p:id="0"
            p:name="HTTP" 
            p:description="oauth wrapper callback url" 
            p:serviceId="${server.prefix}/oauth2.0/callbackAuthorize" />
...
{% endhighlight %}


### OAuth Clients

Every OAuth client must be defined as a CAS service (notice the new *clientId* and *clientSecret* properties, specific to OAuth):

{% highlight xml %}
<bean id="serviceRegistryDao" class="org.jasig.cas.services.InMemoryServiceRegistryDaoImpl">
  <property name="registeredServices">
    <list>
      <!-- Supports regex patterns by default for service ids --> 
      <bean class="org.jasig.cas.support.oauth.services.OAuthRegisteredService"
            p:id="1"
            p:name="serviceName" 
            p:description="Service Description"
            p:serviceId="oauth client service url"
            p:bypassApprovalPrompt="false" 
            p:clientId="client id goes here" 
            p:clientSecret="client secret goes here" /> 
...
{% endhighlight %}

