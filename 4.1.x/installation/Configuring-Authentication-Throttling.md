---
layout: default
title: CAS - Configuring Authentication Throttling
---

# Throttling Authentication Attempts
CAS provides a facility for limiting failed login attempts to support password guessing and related abuse scenarios.
A couple strategies are provided for tracking failed attempts:

1. Source IP - Limit successive failed logins against any username from the same IP address.
2. Source IP and username - Limit succesive failed logins against a particular user from the same IP address.

It would be straightforward to develop new components that implement alternative strategies.

All login throttling components that ship with CAS limit successive failed login attempts that exceed a threshold
rate in failures per second. The following properties are provided to define the failure rate.

* `failureRangeInSeconds` - Period of time in seconds during which the threshold applies.
* `failureThreshold` - Number of failed login attempts permitted in the above period.

A failure rate of more than 1 per 3 seconds is indicative of an automated authentication attempt, which is a
reasonable basis for throttling policy. Regardless of policy care should be taken to weigh security against access;
overly restrictive policies may prevent legitimate authentication attempts.


## Throttling Components
The CAS login throttling components are listed below along with a sample configuration that implements a policy
preventing more than 1 failed login every 3 seconds.


##### `InMemoryThrottledSubmissionByIpAddressHandlerInterceptorAdapter`
Uses a memory map to prevent successive failed login attempts from the same IP address.
{% highlight xml %}
<bean id="loginThrottle"
      class="org.jasig.cas.web.support.InMemoryThrottledSubmissionByIpAddressHandlerInterceptorAdapter"
      p:failureRangeInSeconds="3"
      p:failureThreshold="1"
      p:usernameParameter="username" />
{% endhighlight %}


##### `InMemoryThrottledSubmissionByIpAddressAndUsernameHandlerInterceptorAdapter`
Uses a memory map to prevent successive failed login attempts for a particular username from the same IP address.
{% highlight xml %}
<bean id="loginThrottle"
      class="org.jasig.cas.web.support.InMemoryThrottledSubmissionByIpAddressAndUsernameHandlerInterceptorAdapter"
      p:failureRangeInSeconds="3"
      p:failureThreshold="1"
      p:usernameParameter="username" />
{% endhighlight %}


##### `InspektrThrottledSubmissionByIpAddressAndUsernameHandlerInterceptorAdapter`
Queries the data source used by the CAS audit facility to prevent successive failed login attempts for a particular
username from the same IP address. This component requires that the
[inspektr library](https://github.com/Jasig/inspektr) used for CAS auditing be configured with
`JdbcAuditTrailManager`, which writes audit data to a database.
{% highlight xml %}
<bean id="loginThrottle"
      class="org.jasig.cas.web.support.InspektrThrottledSubmissionByIpAddressAndUsernameHandlerInterceptorAdapter"
      c:auditTrailManager-ref="auditTrailManager"
      c:dataSource-ref="dataSource"
      p:failureRangeInSeconds="3"
      p:failureThreshold="1" />

<bean id="auditTrailManager"
      class="org.jasig.inspektr.audit.support.JdbcAuditTrailManager"
      c:transactionTemplate-ref="inspektrTransactionTemplate"
      p:dataSource-ref="dataSource" />
{% endhighlight %}

For additional instructions on how to configure auditing via Inspektr,
please [review the following guide](Logging.html).

## High Availability Considerations for Throttling

All of the throttling components are suitable for a CAS deployment that satisfies the
[recommended HA architecture](../planning/High-Availability-Guide.html). In particular deployments with multiple CAS
nodes behind a load balancer configured with session affinity can use either in-memory or _inspektr_ components. It is
instructive to discuss the rationale. Since load balancer session affinity is determined by source IP address, which
is the same criterion by which throttle policy is applied, an attacker from a fixed location should be bound to the
same CAS server node for successive authentication attempts. A distributed attack, on the other hand, where successive
request would be routed indeterminately, would cause haphazard tracking for in-memory CAS components since attempts
would be split across N systems. However, since the source varies, accurate accounting would be pointless since the
throttling components themselves assume a constant source IP for tracking purposes. The login throttling components
are simply not sufficient for detecting or preventing a distributed password brute force attack.

For stateless CAS clusters where there is no session affinity, the in-memory components may afford some protection but
they cannot apply the rate strictly since requests to CAS hosts would be split across N systems.
The _inspektr_ components, on the other hand, fully support stateless clusters.


### Configuring Login Throttling
Login throttling configuration consists of two core components:

1. A login throttle modeled as a Spring `HandlerInterceptorAdapter` component.
2. A scheduled task that periodically cleans up state to allow the throttle to relax.

The period of scheduled task execution MUST be less than that defined by `failureRangeInSeconds` for proper throttle policy enforcement. For example, if `failureRangeInSeconds` is 3, then the quartz trigger that drives the task would be configured for less than 3000 (ms).

It is convenient to place Spring configuration for login throttling components in `deployerConfigContext.xml`.
{% highlight xml %}
<bean id="loginThrottle"
      class="org.jasig.cas.web.support.InMemoryThrottledSubmissionByIpAddressHandlerInterceptorAdapter"
      p:failureRangeInSeconds="3"
      p:failureThreshold="1" />

<bean id="loginThrottleJobDetail"
      class="org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean"
      p:targetObject-ref="loginThrottle"
      p:targetMethod="decrementCounts"/>

<!-- A scheduler that drives all configured triggers is provided by default in applicationContext.xml. -->
<bean id="loginThrottleTrigger"
      class="org.springframework.scheduling.quartz.SimpleTriggerFactoryBean"
      p:jobDetail-ref="loginThrottleJobDetail"
      p:startDelay="1000"
      p:repeatInterval="1000"/>
{% endhighlight %}

Configure the throttle to fire during the login webflow by editing `cas-servlet.xml`:
{% highlight xml %}
<bean id="loginFlowHandlerMapping" class="org.springframework.webflow.mvc.servlet.FlowHandlerMapping"
      p:flowRegistry-ref="loginFlowRegistry"
      p:order="2">
      <property name="interceptors">
          <array value-type="org.springframework.web.servlet.HandlerInterceptor">
            <ref bean="localeChangeInterceptor" />
        <ref bean="loginThrottle" />		
          </array>
      </property>
</bean>
{% endhighlight %}


