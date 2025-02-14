---
layout: default
title: CAS - Whitelist Authentication
---


# Whitelist Authentication
Whitelist authentication components fall into two categories: Those that accept a set of credentials stored directly in the configuration and those that accept a set of credentials from a file resource on the server.

These are:
* `AcceptUsersAuthenticationHandler`
* `FileAuthenticationHandler`


## Authentication Components
Support is enabled by including the following dependency in the Maven WAR overlay:

{% highlight xml %}
<dependency>
  <groupId>org.jasig.cas</groupId>
  <artifactId>cas-server-support-generic</artifactId>
  <version>${cas.version}</version>
</dependency>
{% endhighlight %}

### `AcceptUsersAuthenticationHandler`
{% highlight xml %}
<bean class="org.jasig.cas.adaptors.generic.AcceptUsersAuthenticationHandler">
    <property name="users">
       <map>
          <entry key="scott" value="password" />
       </map>
    </property>
</bean>
{% endhighlight %}



### `FileAuthenticationHandler`
{% highlight xml %}
<bean class="org.jasig.cas.adaptors.generic.FileAuthenticationHandler"
   p:fileName="file:/opt/cas/file_of_passwords.txt" />
{% endhighlight %}


#### Example Password File
{% highlight bash %}
scott::password
bob::password2
{% endhighlight %}


