---
layout: default
title: CAS - ClearPass
---

# ClearPass: Credential Caching and Replay

<div class="alert alert-warning"><strong>Deprecated!</strong><p>Exercising ClearPass via the architecture and configuration that is described below is deprecated. Consider <a href="ClearPass.html">using the alternative</a> that would allow adopters to consume the credential directly in the CAS validation response.</p></div>

To enable single sign-on into some legacy application it may be necessary to provide them with the actual cleartext password. While such approach inevitably increases security risk, at times this may be a necessary evil in order to integrate applications with CAS.

<div class="alert alert-warning"><strong>Usage Warning!</strong><p>ClearPass is turned off by default. No applications will be able to obtain the user credentials unless ClearPass is explicitly turned on by the below configuration.</p></div>

## Architecture
A service may obtain cleartext credentials for an authenticated user by presenting a valid proxy ticket obtained specifically for the CAS cleartext extension service end-point that is ClearPass. Tickets issued for a ClearPass response are validated in the same way you would access a traditional proxied service. ClearPass ensures this by just being another CAS Client. Credentials are cached inside an Ehcache-backed map with support for encryption of the obtained password in memory.

Upon receiving the request, ClearPass ensures that the following validation criteria are met:

* The proxy ticket was obtained for the URL specifying location of the ClearPass service
* The proxy is valid according to standard CAS spec
* The proxy ticket is indeed a proxy ticket, not a service ticket
* Each member of the proxy chain has been given explicit permission to receive cleartext credentials


## Validation Responses
Upon successful validation the ClearPass service provides credentials in the following response:

{% highlight xml %}
<cas:clearPassResponse xmlns:cas='http://www.yale.edu/tp/cas'>
    <cas:clearPassSuccess>
        <cas:credentials>actual_password</cas:credentials>
    </cas:clearPassSuccess>
</cas:clearPassResponse>
{% endhighlight %}

If the validation fails, the traditional response is a 403 Status code being returned. If there are failures for any other reason other than authorization, the following error response is returned:

{% highlight xml %}
<cas:clearPassResponse xmlns:cas='http://www.yale.edu/tp/cas'>
    <cas:clearPassFailure>description of the problem</cas:clearPassFailure>
</cas:clearPassResponse>
{% endhighlight %}


## Components
Support is enabled by including the following dependency in the Maven WAR overlay:

{% highlight xml %}
<dependency>
    <groupId>org.jasig.cas</groupId>
    <artifactId>cas-server-extension-clearpass</artifactId>
    <version>${cas.version}</version>
    <scope>runtime</scope>
</dependency>
{% endhighlight %}

### `CacheCredentialsMetaDataPopulator`
Retrieve and store the password in our cache.
 

### `ClearPassController`
A controller that returns the password based on some external authentication/authorization rules. Attempts to obtain the password from the cache and render the appropriate output.


### `EncryptedMapDecorator`
A `Map` implementation that will hash and store cached credentials.


### `EhcacheBackedMap`
A `Map` implementation that will use Ehcache as the backend storage.


### `TicketRegistryDecorator`
A ticket registry implementation that dispatches ticketing operations to the *real* registry while mapping tickets to user names and placing them in the provided cache.


## Single Node Configuration


### `AuthenticationMetaDataPopulator` in `deployerConfigContext.xml`
Uncomment the below element that is responsible for capturing and caching the password:

{% highlight xml %}
<property name="authenticationMetaDataPopulators">
  <list>
    <bean class="org.jasig.cas.extension.clearpass.CacheCredentialsMetaDataPopulator"
      c:credentialCache-ref="encryptedMap" />
  </list>
</property>
{% endhighlight %}


### Modifying `web.xml`
In your Maven overlay, modify the `web.xml` to include the following:
{% highlight xml %}
<servlet-mapping>
  <servlet-name>cas</servlet-name>
  <url-pattern>/clearPass</url-pattern>
</servlet-mapping>
{% endhighlight %}
 Be sure to put this snippet with the other servlet-mappings.

Next, add the following filter and filter-mapping:

{% highlight xml %}
<filter>
  <filter-name>clearPassFilterChainProxy</filter-name>
  <filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
</filter>
 
<filter-mapping>
  <filter-name>clearPassFilterChainProxy</filter-name>
  <url-pattern>/clearPass</url-pattern>
</filter-mapping>
{% endhighlight %}

Be sure to put this snippet with the other filter and filter-mappings.


### Modifying `clearpass-configuration.xml`
Obtain a copy of the [`clearpass-configuration.xml`](https://github.com/Jasig/cas/blob/master/cas-server-webapp/src/main/webapp/WEB-INF/unused-spring-configuration/clearpass-configuration.xml) file inside the `WEB-INF/unused-spring-configuration` of the project. Place that in your project's `WEB-INF/spring-configuration` directory.

Next, declare the following bean inside the file:
{% highlight xml %}
<bean id="clearPassProxyList" class="org.jasig.cas.client.validation.ProxyList">
    <constructor-arg>
        <list>
            <value>https://proxy.server.edu/proxyCallback</value>
            <value>...</value>
        </list>
    </constructor-arg>
</bean>
{% endhighlight %}

The above bean defines the list of proxying services authorized to obtain ClearPass credentials. Also note that
proxy urls in the above list have to be fully specified and must produce an exact match. Otherwise, the CAS server
will report back that the proxy chain is invalid for the requesting proxy url. 


Alternatively, you may replace:

{% highlight xml %}
<property name="allowedProxyChains" ref="clearPassProxyList" />
{% endhighlight %}

with:

{% highlight xml %}
<property name="acceptAnyProxy" value="true" />
{% endhighlight %}

...to allow all proxying services to be able to obtain ClearPass credentials.

<div class="alert alert-warning"><strong>Usage Warning!</strong><p>It's not appropriate in your environment to allow any service that can obtain a proxy ticket to proxy to ClearPass. Explicitly authorizing proxy chains to access ClearPass (and denying all unauthorized proxy chains) is an important access control on release of the end-user's password.</p></div>


### Modifying `ticketRegistry.xml`
Obtain a copy of the [`ticketRegistry.xml`](https://github.com/Jasig/cas/blob/master/cas-server-webapp/src/main/webapp/WEB-INF/spring-configuration/ticketRegistry.xml) file and place that in your project's `WEB-INF/spring-configuration` directory.

Replace:

{% highlight xml %}
<bean id="ticketRegistry" class="org.jasig.cas.ticket.registry.DefaultTicketRegistry" />
{% endhighlight %}

with:

{% highlight xml %}
<bean id="ticketRegistryValue" class="org.jasig.cas.ticket.registry.DefaultTicketRegistry" />
{% endhighlight %}


## Multiple Nodes Configuration
ClearPass stores the password information it collects in a non-distributed EhCache-based Map. This works fine in single-server CAS environments but causes issues in multi-server CAS environments. In a normal multi-server CAS environment you would use a Distributed Ticket Registry like the `MemcacheTicketRegistry` or the `EhcacheTicketRegistry` so that all CAS servers would have knowledge of all the tickets. After the distributed Ticket Registry is setup you should replace ClearPass's default in-memory Map with a Map implemenation that matches your ticket registry. 


### EhCache-based Map
By default ClearPass is setup to use a non-distrbuted EhCache to store its passwords. If you are using the `EhcacheTicketRegistry` you will want to ensure that your ehcacheClearPass.xml file is setup to replicate the ClearPass Ehcache to all your CAS servers. 


#### Configuration


##### Sample `clearpass-configuration.xml`
{% highlight xml %}
<!--  Credentials Cache implementation -->
<bean id="ehCacheManager" class="org.springframework.cache.ehcache.EhCacheManagerFactoryBean">
    <property name="configLocation" value="file:/etc/cas/clearpass-replicated.xml" />
    <property name="shared" value="false" />
    <property name="cacheManagerName" value="clearPassEhCacheManager" />
</bean>

<bean id="clearPassEhCache" class="org.springframework.cache.ehcache.EhCacheFactoryBean"
    p:cacheManager-ref="ehCacheManager"
    p:bootstrapCacheLoader-ref="ticketCacheBootstrapCacheLoader" 
    p:cacheEventListeners-ref="ticketRMISynchronousCacheReplicator"
    p:cacheName="org.jasig.cas.extension.clearpass.CACHE"
    p:timeToIdle="7201"
    p:timeToLive="7201" />

<bean id="ticketRMISynchronousCacheReplicator" class="net.sf.ehcache.distribution.RMISynchronousCacheReplicator">
    <constructor-arg name="replicatePuts" value="true"/> 
    <constructor-arg name="replicatePutsViaCopy" value="true"/> 
    <constructor-arg name="replicateUpdates" value="true"/>  
    <constructor-arg name="replicateUpdatesViaCopy" value="true"/>  
    <constructor-arg name="replicateRemovals" value="true"/>       
</bean>

<bean id="ticketCacheBootstrapCacheLoader" class="net.sf.ehcache.distribution.RMIBootstrapCacheLoader">
    <constructor-arg name="asynchronous" value="true"/>  
    <constructor-arg name="maximumChunkSize" value="5000000"/>  
</bean>

<bean id="credentialsCache" class="org.jasig.cas.extensions.clearpass.EhcacheBackedMap">
    <constructor-arg index="0" ref="clearPassEhCache" />
</bean>

<bean id="ticketRegistry" class="org.jasig.cas.extensions.clearpass.TicketRegistryDecorator">
    <constructor-arg index="0" ref="ticketRegistryValue"/>
    <constructor-arg index="1" ref="credentialsCache"/>
</bean>

<!-- implementation of the clear pass vending service -->
<bean id="clearPassController" class="org.jasig.cas.extensions.clearpass.ClearPassController">
    <constructor-arg index="0" ref="credentialsCache" />
</bean>

<bean id="handlerMappingClearPass" class="org.springframework.web.servlet.handler.SimpleUrlHandlerMapping"
    p:alwaysUseFullPath="true">
    <property name="mappings">
        <props>
            <prop key="/clearPass">
                clearPassController
            </prop>
        </props>
    </property>
</bean>
{% endhighlight %}


##### Sample `clearpass-replicated.xml`
{% highlight xml %}
<ehcache name="clearPassEhCacheManager" updateCheck="false" 
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
         xsi:noNamespaceSchemaLocation="http://ehcache.sf.net/ehcache.xsd">

   <diskStore path="java.io.tmpdir/cas"/>
    
     <cacheManagerPeerProviderFactory 
                class="net.sf.ehcache.distribution.RMICacheManagerPeerProviderFactory"
                properties="peerDiscovery=manual,
                rmiUrls=//10.1.1.123:40002/org.jasig.cas.extension.clearpass.CACHE" />
   
   <!-- Port where it listens for peers. Should be different from peer provider port defined above -->
   <cacheManagerPeerListenerFactory 
            class="net.sf.ehcache.distribution.RMICacheManagerPeerListenerFactory"
            properties="port=40002" />
</ehcache>
{% endhighlight %}

Note that the above uses manual peer discovery with RMI replication to transfer cached objects that are obtained by ClearPass. The IP addresses need to be changed for each CAS node to point to each other.


### Memcached  Map
The spymemcached java client includes a Memcached Map implementation called `CacheMap`. 


#### Configuration


##### Sample `clearpass-configuration.xml`
{% highlight xml %}
<bean id="CPserialTranscoder" class="net.spy.memcached.transcoders.SerializingTranscoder"
    p:compressionThreshold="2048" />
     
<bean id="memcachedMap" class="net.spy.memcached.CacheMap">
  <constructor-arg index="0">
    <bean class="net.spy.memcached.spring.MemcachedClientFactoryBean"
          p:servers="memcache1.college.edu:11211,memcache2.college.edu:11211"
          p:protocol="BINARY"
          p:locatorType="ARRAY_MOD"
          p:failureMode="Redistribute"
          p:transcoder-ref="CPserialTranscoder">
      <property name="hashAlg">
        <util:constant static-field="net.spy.memcached.DefaultHashAlgorithm.FNV1A_64_HASH" />
      </property>
    </bean>
  </constructor-arg>
  <!-- this is the timeout for the cache in seconds -->
  <constructor-arg index="1" value="7200" />
  <!-- this is the prefix for the keys stored in the map --> 
  <constructor-arg index="2" value="clearPass_" /> 
</bean>  
 
<bean id="credentialsCache" class="org.jasig.cas.extension.clearpass.EncryptedMapDecorator">
  <constructor-arg index="0" ref="memcachedMap" />
  <!-- Replace the salt and secret key with one of your choosing -->      
  <constructor-arg index="1" value="salt1234" />
  <constructor-arg index="2" value="seCretKey0123456" />
</bean>

<bean id="ticketRegistry" class="org.jasig.cas.extension.clearpass.TicketRegistryDecorator">
  <constructor-arg index="0" ref="ticketRegistryValue"/>
  <constructor-arg index="1" ref="credentialsCache"/>
</bean>
{% endhighlight %}

Note that if you are using an SSH tunnel for your Memcached connections the Encrypted Map Decorator would not be necessary.
