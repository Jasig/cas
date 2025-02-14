---
layout: default
title: CAS - JBoss Cache Ticket Registry
---

# JBoss Cache Ticket Registry

<div class="alert alert-danger"><strong>Deprecated Module!</strong><p>The JBoss Cache Ticket Registry module is deprecated and will no longer be maintained. Furthermore, the module may be removed in subsequent CAS releases. Please try to use other options provided by the CAS server for your distributed ticket registry needs.</p></div>

JBoss integration is enabled by including the following dependency in the Maven WAR overlay:

{% highlight xml %}
<dependency>
     <groupId>org.jasig.cas</groupId>
     <artifactId>cas-server-integration-jboss</artifactId>
     <version>${cas.version}</version>
</dependency>
{% endhighlight %}

`JBossCacheTicketRegistry` stores data in a [JBoss Cache](http://www.jboss.org/jbosscache/) that supports distributed
caches for failover support and is therefore suitable for HA deployments. Almost all of the configuration for this
component happens in the JBoss Cache configuration file; we present a starting configuration here but readers should
consult JBoss Cache
[configuration documentation](http://docs.jboss.org/jbosscache/3.2.1.GA/userguide_en/html/configuration.html)
for further details.

Sample `JBossCacheTicketRegistry` configuration for `ticketRegistry.xml`.
{% highlight xml %}
<bean id="ticketRegistry"
      class="org.jasig.cas.ticket.registry.JBossCacheTicketRegistry"
      p:cache-ref="cache" />
<bean id="cache"
      class="org.jasig.cas.util.JBossCacheFactoryBean"
      p:configLocation="classpath:jbossCache.xml" />
  </bean>
{% endhighlight %}

The `jbossCache.xml` file may be bundled in the overlay which allows it to be accessed from the classpath, as in the
example above, or it may be located on the filesystem. A sample `jbossCache.xml` file follows.
{% highlight xml %}
<?xml version="1.0" encoding="UTF-8"?>
<server>
  <mbean code="org.jboss.cache.TreeCache" name="jboss.cache:service=TreeCache">

    <depends>jboss:service=Naming</depends>
    <depends>jboss:service=TransactionManager</depends>

    <attribute name="TransactionManagerLookupClass">
    org.jboss.cache.transaction.DummyTransactionManagerLookup
    </attribute>

    <!--
       | SERIALIZABLE, REPEATABLE_READ (default), READ_COMMITTED, READ_UNCOMMITTED, NONE
       -->
    <attribute name="IsolationLevel">REPEATABLE_READ</attribute>

    <!--
       | LOCAL, REPL_ASYNC, REPL_SYNC, INVALIDATION_ASYNC, INVALIDATION_SYNC
       -->
    <attribute name="CacheMode">REPL_SYNC</attribute>

    <!-- Just used for async repl: use a replication queue -->
    <attribute name="UseReplQueue">false</attribute>

    <!-- Replication interval for replication queue (in ms) -->
    <attribute name="ReplQueueInterval">0</attribute>

    <!-- Max number of elements which trigger replication -->
    <attribute name="ReplQueueMaxElements">0</attribute>

    <!-- Cluster name must be same for all members in cluster in order to find each other. -->
    <attribute name="ClusterName">TreeCache-Cluster</attribute>

    <attribute name="ClusterConfig">
      <config>
        <!--
           | If you have a multihomed machine, set the bind_addr, e.g bind_addr="192.168.0.2"
           -->
        <UDP mcast_addr="228.1.2.3" mcast_port="48866"
          ip_ttl="64" ip_mcast="true"
          mcast_send_buf_size="150000" mcast_recv_buf_size="80000"
          ucast_send_buf_size="150000" ucast_recv_buf_size="80000"
          loopback="false"/>
        <PING timeout="2000" num_initial_members="3" up_thread="false" down_thread="false"/>
        <MERGE2 min_interval="10000" max_interval="20000"/>
        <FD_SOCK/>
        <VERIFY_SUSPECT timeout="1500" up_thread="false" down_thread="false"/>
        <pbcast.NAKACK gc_lag="50" retransmit_timeout="600,1200,2400,4800"
          max_xmit_size="8192" up_thread="false" down_thread="false"/>
        <UNICAST timeout="600,1200,2400" window_size="100" min_threshold="10" down_thread="false"/>
        <pbcast.STABLE desired_avg_gossip="20000" up_thread="false" down_thread="false"/>
        <FRAG frag_size="8192" down_thread="false" up_thread="false"/>
        <pbcast.GMS join_timeout="5000" join_retry_timeout="2000" shun="true" print_local_addr="true"/>
        <pbcast.STATE_TRANSFER up_thread="true" down_thread="true"/>
      </config>
    </attribute>


    <!-- Whether or not to fetch state on joining a cluster. -->
    <attribute name="FetchInMemoryState">true</attribute>

    <!--
       | 
       | The max amount of time (in milliseconds) we wait until the
       | initial state (ie. the contents of the cache) are retrieved from
       | existing members in a clustered environment
       -->
    <attribute name="InitialStateRetrievalTimeout">15000</attribute>

    <!--
       | Number of milliseconds to wait until all responses for a
       | synchronous call have been received.
       -->
    <attribute name="SyncReplTimeout">15000</attribute>

    <!-- Max number of milliseconds to wait for a lock acquisition -->
    <attribute name="LockAcquisitionTimeout">10000</attribute>

    <!-- Name of the eviction policy class. -->
    <attribute name="EvictionPolicyClass"></attribute>

    <attribute name="UseMarshalling">false</attribute>
    <attribute name="StateTransferVersion">130</attribute>
  </mbean>
</server>
{% endhighlight %}
