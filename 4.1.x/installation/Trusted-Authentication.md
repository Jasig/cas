---
layout: default
title: CAS - Trusted Authentication
---

# Trusted Authentication
The trusted authentication handler provides support for trusting authentication performed by some other component
in the HTTP request handling chain. Proxies (including Apache in a reverse proxy scenario) are the most common
components that perform authentication in front of CAS.

Trusted authentication handler support is enabled by including the following dependency in the Maven WAR overlay:

{% highlight xml %}
<dependency>
  <groupId>org.jasig.cas</groupId>
  <artifactId>cas-server-support-trusted</artifactId>
  <version>${cas.version}</version>
</dependency>
{% endhighlight %}


## Configure Trusted Authentication Handler
Modify `deployerConfigContext.xml` according to the following template:

{% highlight xml %}
<bean id="trustedHandler"
      class="org.jasig.cas.adaptors.trusted.authentication.handler.support.PrincipalBearingCredentialsAuthenticationHandler" />

<bean id="trustedPrincipalResolver"
      class="org.jasig.cas.adaptors.trusted.authentication.principal.PrincipalBearingPrincipalResolver" />

<bean id="authenticationManager"
      class="org.jasig.cas.authentication.PolicyBasedAuthenticationManager">
  <constructor-arg>
    <map>
      <entry key-ref="trustedHandler" value-ref="trustedPrincipalResolver"/>
    </map>
  </constructor-arg>
  <property name="authenticationMetaDataPopulators">
    <list>
      <bean class="org.jasig.cas.authentication.SuccessfulHandlerMetaDataPopulator" />
    </list>
  </property>
</bean>
{% endhighlight %}


## Configure Webflow Components
Add an additional state to `login-webflow.xml`:

{% highlight xml %}
<action-state id="remoteAuthenticate">
  <evaluate expression="principalFromRemoteAction" />
  <transition on="success" to="sendTicketGrantingTicket" />
  <transition on="error" to="viewLoginForm" />
</action-state>
{% endhighlight %}

Replace references to `viewLoginForm` in existing states with `remoteAuthenticate`.

Install the Webflow action into the Spring context by adding the following bean to `cas-servlet.xml`:
{% highlight xml %}
<bean id="principalFromRemoteAction"
      class="org.jasig.cas.adaptors.trusted.web.flow.PrincipalFromRequestRemoteUserNonInteractiveCredentialsAction"
      p:centralAuthenticationService-ref="centralAuthenticationService" />
{% endhighlight %}
