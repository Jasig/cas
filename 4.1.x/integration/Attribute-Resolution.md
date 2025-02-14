---
layout: default
title: CAS - Attribute Resolution
---

# Attribute Resolution
Attribute resolution strategies are controlled by the [Person Directory project](https://github.com/Jasig/person-directory). The Person Directory dependency is automatically bundled with the CAS server. Therefor, declaring an additional dependency will not be required. This Person Directory project supports both LDAP and JDBC attribute resolution, caching, attribute aggregation from multiple attribute sources, etc.

<div class="alert alert-info"><strong>Default Caching Policy</strong><p>By default, attributes are cached to the length of the SSO session. This means that while the underlying component provided by Person Directory may have a different caching model, attributes by default and from a CAS perspective will not be refreshed and retrieved again on subsequent requests as long as the SSO session exists.</p></div>


## Components
A Person Directory `IPersonAttributeDao` attribute source is defined and configured to describe the global set of attributes to be fetched for each authenticated principal. That global set of attributes is then filtered by the service manager according to service-specific attribute release rules. 

### Person Directory

| Component         					| Description 
|-----------------------------------+--------------------------------------------------------------------------------+
| `MergingPersonAttributeDaoImpl`| Designed to query multiple `IPersonAttributeDaos` in order and merge the results into a single result set. Merging strategies may be configured via instances of `IAttributeMerger`.
| `CachingPersonAttributeDaoImpl`| Provides the ability to cache results of executed inner DAOs.
| `CascadingPersonAttributeDao`| Designed to query multiple `IPersonAttributeDaos` in order and merge the results into a single result set. As each `IPersonAttributesAttributeDao` is queried the attributes from the first `IPersonAttributes` in the result set are used as the query for the next `IPersonAttributesAttributeDao`. 
| `StubPersonAttributeDao`| Backed by a single Map which this implementation will always return, useful for returning static values.
| `MessageFormatPersonAttributeDao`| Provides the ability to create attributes based on other other attribute values as arguments.
| `RegexGatewayPersonAttributeDao`| Conditionally execute an inner DAO if the data in the seed matches criteria set out by the configured patterns.
| `SingleRowJdbcPersonAttributeDao`| The implementation that maps from column names in the result of a SQL query to attribute names.
| `MultiRowJdbcPersonAttributeDao`| Designed to work against a table where there is a mapping of one row to many users. Should be used if the database is structured such that there is a column for attribute names and column(s) for the corresponding values.
| `XmlPersonAttributeDao`| XML backed person attribute DAO that supports wildcard searching.
| `LdapPersonAttributeDao`| Queries an LDAP directory to populate person attributes using Spring Framework.
| `GroovyPersonAttributeDao`| Resolve attributes based on an external groovy script.
| `TomlLdapPersonAttributeDao`| Resolve person attributes and insert the ldap/context settings from an external Toml file. 
| `JsonBackedComplexStubPersonAttributeDao`| Resolve person attributes that are specified in an external JSON file.

Note that the Person Directory project requires the following configuration in CAS overlays:

```xml
<dependency>
      <groupId>org.apache.commons</groupId>
      <artifactId>commons-collections4</artifactId>
      <version>4.1</version>
</dependency>
```

More about the Person Directory and its configurable sources [can be found here](https://wiki.jasig.org/display/PDM15/Person+Directory+1.5+Manual).


### CAS
The CAS project provides the following additional implementations:

| Component         					| Description 
|-----------------------------------+--------------------------------------------------------------------------------+
| `LdapPersonAttributeDao`| Queries an LDAP directory to populate person attributes using the bundled CAS LDAP libraries.

### Sample Usage


#### LDAP
The following snippet assumes that connection information beans are already defined.

{% highlight xml %}
<bean id="ldapPersonAttributeDao"
      class="org.jasig.services.persondir.support.ldap.LdaptivePersonAttributeDao"
      p:connectionFactory-ref="pooledLdapConnectionFactory"
      p:baseDN="${ldap.baseDn}"
      p:searchControls-ref="searchControls"
      p:searchFilter="mail={0}">
    <property name="resultAttributeMapping">
        <map>
            <!--
               | Key is LDAP attribute name, value is principal attribute name.
               -->
            <entry key="member" value="member" />
            <entry key="mail" value="mail" />
            <entry key="displayName" value="displayName" />
        </map>
    </property>
</bean>
{% endhighlight %}


#### JDBC
The following snippet assumes that connection information beans are already defined.

{% highlight xml %}
<bean id="singleRowJdbcPersonAttributeDao"
    class="org.jasig.services.persondir.support.jdbc.SingleRowJdbcPersonAttributeDao">
    <constructor-arg index="0" ref="dataSource" />
    <constructor-arg index="1" value="SELECT * FROM USER_DATA WHERE {0}" />
    <property name="queryAttributeMapping">
        <map>
            <entry key="username" value="uid" />
        </map>
    </property>
    <property name="resultAttributeMapping">
        <map>
            <entry key="uid" value="username" />
            <entry key="first_name" value="first_name" />
            <entry key="last_name" value="last_name" />
            <entry key="email" value="email" />
        </map>
    </property>
</bean>
{% endhighlight %}


#### Caching, Merging and Cascading
Note that this snippet below strictly uses the Person Directory components for resolving attributes.

{% highlight xml %}
<bean id="mergedPersonAttributeDao"
        class="org.jasig.services.persondir.support.CachingPersonAttributeDaoImpl">
    <property name="cacheNullResults" value="true" />
    <property name="userInfoCache">
        <bean class="org.jasig.portal.utils.cache.MapCacheFactoryBean">
            <property name="cacheFactory" ref="cacheFactory" />
            <property name="cacheName" value="org.jasig.services.persondir.USER_INFO.merged" />
        </bean>
    </property>
    <property name="cachedPersonAttributesDao" >
        <bean id="mergedPersonAttributeDao"                 
                class="org.jasig.services.persondir.support.MergingPersonAttributeDaoImpl">
            <property name="merger">
                <bean class="org.jasig.services.persondir.support.merger.NoncollidingAttributeAdder" />
            </property>
            <property name="personAttributeDaos">
                <list>
                    <bean class="org.jasig.services.persondir.support.CascadingPersonAttributeDao">
                        <property name="personAttributeDaos">
                            <list>
                                <ref bean="anotherDao" />
                            </list>
                        </property>
                    </bean>
                </list>
            </property>
        </bean>
    </property>
</bean>
{% endhighlight %}
