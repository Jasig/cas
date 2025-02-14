---
layout: default
title: CAS - Blacklist Authentication
---

# Blacklist Authentication
Blacklist authentication components are those that specifically deny access to a set of credentials. Those that fail to match against the predefined set will blindly be accepted.
 
These are:

* `RejectUsersAuthenticationHandler`


## Authentication Components
Support is enabled by including the following dependency in the Maven WAR overlay:

{% highlight xml %}
<dependency>
  <groupId>org.jasig.cas</groupId>
  <artifactId>cas-server-support-generic</artifactId>
  <version>${cas.version}</version>
</dependency>
{% endhighlight %}

### `RejectUsersAuthenticationHandler`
{% highlight xml %}
<bean class="org.jasig.cas.adaptors.generic.RejectUsersAuthenticationHandler">
    <property name="users">
       <set>
        <value>casuser</value>
       </set>
    </property>
</bean>
{% endhighlight %}
