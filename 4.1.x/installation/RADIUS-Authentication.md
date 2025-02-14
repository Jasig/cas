---
layout: default
title: CAS - RADIUS Authentication
---

# RADIUS Authentication
RADIUS support is enabled by including the following dependency in the Maven WAR overlay:

{% highlight xml %}
    <dependency>
      <groupId>org.jasig.cas</groupId>
      <artifactId>cas-server-support-radius</artifactId>
      <version>${cas.version}</version>
    </dependency>
{% endhighlight %}

## RADIUS Components

###### `RadiusAuthenticationHandler`
The RADIUS handler accepts username/password credentials and delegates authentication to one or more RADIUS
servers. It supports two types of failovers: failover on an authentication failure, and failover on a server exception.

* `failoverOnAuthenticationFailure` - True to continue to the next configured RADIUS server on authentication failure,
false otherwise. This flag is typically set when user accounts are spread across one or more RADIUS servers.
* `failoverOnException` - True to continue to next configured RADIUS server on an error other than authentication
failure, false otherwise. This flag is typically set to support highly available deployments where authentication
should proceed in the face of one or more RADIUS server failures.
* `servers` - Array of RADIUS servers to delegate to for authentication.


###### `JRadiusServerImpl`
Component representing a RADIUS server has the following configuration properties.

* `protocol` - radius protocol to use.
* `clientFactory` - factory establish and create radius client instances.


## RADIUS Configuration Example
{% highlight xml %}
<bean id="radiusServer"
      class="org.jasig.cas.adaptors.radius.JRadiusServerImpl"
      c:protocol="EAP_MSCHAPv2"
      c:clientFactory-ref="radiusClientFactory" />

<bean id="radiusClientFactory"
      class="org.jasig.cas.adaptors.radius.RadiusClientFactory"
      p:inetAddress="localhost"
      p:sharedSecret="fqhwhgads" />

<bean id="radiusAuthenticationHandler"
      class="org.jasig.cas.adaptors.radius.authentication.handler.support.RadiusAuthenticationHandler">
  <property name="servers">
      <list>
          <ref local="radiusServer" />
      </list>
  </property>
</bean>
{% endhighlight %}
