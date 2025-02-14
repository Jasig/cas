---
layout: default
title: CAS - Long Term Authentication
---

# Long Term Authentication
This feature, also known as *Remember Me*, extends the length of the SSO session beyond the typical period of hours
such that users can go days or weeks without having to log in to CAS. See the
[security guide](../planning/Security-Guide.html)
for discussion of security concerns related to long term authentication.


### Policy and Deployment Considerations
While users can elect to establish a long term authentication session, the duration is established through
configuration as a matter of security policy. Deployers must determine the length of long term authentication sessions
by weighing convenience against security risks. The length of the long term authentication session is configured
(somewhat unhelpfully) in seconds, but the Google calculator provides a convenient converter:

[2 weeks in seconds](https://www.google.com/search?q=2+weeks+in+seconds&oq=2+weeks+in+seconds)

The use of long term authentication sessions dramatically increases the length of time ticket-granting tickets are
stored in the ticket registry. Loss of a ticket-granting ticket corresponding to a long-term SSO session would require
the user to re-authenticate to CAS. A security policy that requires that long term authentication sessions MUST NOT
be terminated prior to their natural expiration would mandate a ticket registry component that provides for durable storage, such as the `JpaTicketRegistry`.


### Component Configuration
Long term authentication requires configuring CAS components in Spring configuration, modification of the CAS login
webflow, and UI customization of the login form. The length of the long term authentication session is represented
in following sections by the following property:

    # Long term authentication session length in seconds
    rememberMeDuration=1209600

The duration of the long term authentication session is configured in two different places:
1. `ticketExpirationPolicies.xml`
2. `ticketGrantingTicketCookieGenerator.xml`

Update the ticket-granting ticket expiration policy in `ticketExpirationPolicies.xml` to accommodate both long term
and stardard sessions.
{% highlight xml %}
<!--
   | The following policy applies to standard CAS SSO sessions.
   | Default 2h (7200s) sliding expiration with default 8h (28800s) maximum lifetime.
   -->
<bean id="standardSessionTGTExpirationPolicy"
      class="org.jasig.cas.ticket.support.TicketGrantingTicketExpirationPolicy"
      p:maxTimeToLiveInSeconds="${tgt.maxTimeToLiveInSeconds:28800}"
      p:timeToKillInSeconds="${tgt.timeToKillInSeconds:7200}"/>

<!--
   | The following policy applies to long term CAS SSO sessions.
   | Default duration is two weeks (1209600s).
   -->
<bean id="longTermSessionTGTExpirationPolicy"
      class="org.jasig.cas.ticket.support.TimeoutExpirationPolicy"
      c:timeToKillInMilliSeconds="#{ ${rememberMeDuration:1209600} * 1000 }" />

<bean id="grantingTicketExpirationPolicy"
      class="org.jasig.cas.ticket.support.RememberMeDelegatingExpirationPolicy"
      p:sessionExpirationPolicy-ref="standardSessionTGTExpirationPolicy"
      p:rememberMeExpirationPolicy-ref="longTermSessionTGTExpirationPolicy" />
{% endhighlight %}

Update the CASTGC cookie expiration in `ticketGrantingTicketCookieGenerator.xml` to match the long term authentication
duration:
{% highlight xml %}
<bean id="ticketGrantingTicketCookieGenerator" class="org.jasig.cas.web.support.CookieRetrievingCookieGenerator"
      p:cookieSecure="true"
      p:cookieMaxAge="-1"
      p:rememberMeMaxAge="${rememberMeDuration:1209600}"
      p:cookieName="CASTGC"
      p:cookiePath="/cas" />
{% endhighlight %}

Modify the `PolicyBasedAuthenticationManager` bean in `deployerConfigContext.xml` to include the
`RememberMeAuthenticationMetaDataPopulator` component that flags long-term SSO sessions:
{% highlight xml %}
<bean id="authenticationManager"
      class="org.jasig.cas.authentication.PolicyBasedAuthenticationManager">
  <constructor-arg>
    <map>
      <entry key-ref="passwordHandler" value-ref="ldapPrincipalResolver"/>
    </map>
  </constructor-arg>
  <property name="authenticationMetaDataPopulators">
    <list>
      <bean class="org.jasig.cas.authentication.SuccessfulHandlerMetaDataPopulator" />
      <bean class="org.jasig.cas.authentication.principal.RememberMeAuthenticationMetaDataPopulator" />
    </list>
  </property>
</bean>
{% endhighlight %}


### Webflow Configuration
Two sections of `login-webflow.xml` require changes:
1. `credential` variable declaration
2. `viewLoginForm` action state

Change the `credential` variable declaration as follows:
{% highlight xml %}
<var name="credential" class="org.jasig.cas.authentication.RememberMeUsernamePasswordCredential" />
{% endhighlight %}

Change the `viewLoginForm` action state as follows:
{% highlight xml %}
<view-state id="viewLoginForm" view="casLoginView" model="credential">
  <binder>
    <binding property="username" />
    <binding property="password" />
    <binding property="rememberMe" />
  </binder>
  <on-entry>
    <set name="viewScope.commandName" value="'credential'" />
  </on-entry>
  <transition on="submit" bind="true" validate="true" to="realSubmit"/>
</view-state>
{% endhighlight %}


### User Interface Customization
A checkbox or other suitable control must be added to the CAS login form to allow user selection of long term
authentication. We recommend adding a checkbox control to `casLoginView.jsp` as in the following code snippet.
The only functional consideration is that the name of the form element is _rememberMe_.
{% highlight xml %}
<input type="checkbox" name="rememberMe" id="rememberMe" value="true" />
<label for="rememberMe">Remember Me</label>
{% endhighlight %}
