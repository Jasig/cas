---
layout: default
title: CAS - Service Management
---

# Service Management
The CAS service management facility allows CAS server administrators to declare and configure which services
(CAS clients) may make use of CAS in which ways. The core component of the service management facility is the
service registry, provided by the `ServiceRegistryDao` component, that stores one or more registered services
containing metadata that drives a number of CAS behaviors:

* Authorized services - Control which services may participate in a CAS SSO session.
* Forced authentication - Provides administrative control for forced authentication.
* Attribute release - Provide user details to services for authorization and personalization.
* Proxy control - Further restrict authorized services by granting/denying proxy authentication capability.
* Theme control - Define alternate CAS themes to be used for particular services.

The service management webapp is a Web application that may be deployed along side CAS that provides a GUI
to manage service registry data.


## Demo
The Service Management web application is available for demo at [https://jasigcasmgmt.herokuapp.com/cas-management](https://jasigcasmgmt.herokuapp.com/cas-management)


## Considerations
It is not required to use the service management facility explicitly. CAS ships with a default configuration that is
suitable for deployments that do not need or want to leverage the capabilities above. The default configuration allows
any service contacting CAS over https/imaps to use CAS and receive any attribute configured by an `IPersonAttributeDao`
bean.

A premier consideration around service management is whether to leverage the user interface. If the service management
webapp is used, then a `ServiceRegistryDao` that provides durable storage (e.g. `JpaServiceRegistryDaoImpl`) must be
used to preserve state across restarts.

It is perfectly acceptable to avoid the service management webapp Web application for managing registered service data.
In fact, configuration-driven methods (e.g. XML, JSON) may be preferable in environments where strict configuration
management controls are required.

## Registered Services

Registered services present the following metadata:

| Field                             | Description
|-----------------------------------+--------------------------------------------------------------------------------+
| `id`                              | Required unique identifier. In most cases this is managed automatically by the `ServiceRegistryDao`.
| `name`                            | Required name (255 characters or less). Must include valid characters allowed by the file system.
| `description`                     | Optional free-text description of the service. (255 characters or less)
| `logo`                              | Optional path to an image file that is the logo for this service. The image will be displayed on the login page along with the service description and name.  
| `serviceId`                       | Required [Ant pattern](http://ant.apache.org/manual/dirtasks.html#patterns) or [regular expression](http://docs.oracle.com/javase/tutorial/essential/regex/) describing a logical service. A logical service defines one or more URLs where a service or services are located. The definition of the url pattern must be **done carefully** because it can open security breaches. For example, using Ant pattern, if you define the following service : `http://example.*/myService` to match `http://example.com/myService` and `http://example.fr/myService`, it's a bad idea as it can be tricked by `http://example.hostattacker.com/myService`. The best way to proceed is to define the more precise url patterns.
| `theme`                           | Optional [Spring theme](http://static.springsource.org/spring/docs/3.2.x/spring-framework-reference/html/mvc.html#mvc-themeresolver) that may be used to customize the CAS UI when the service requests a ticket. See [this guide](User-Interface-Customization.html) for more details.
| `proxyPolicy`                     | Determines whether the service is able to proxy authentication, not whether the service accepts proxy authentication.
| `evaluationOrder`                 | Required value that determines relative order of evaluation of registered services. This flag is particularly important in cases where two service URL expressions cover the same services; evaluation order determines which registration is evaluated first.
| `requiredHandlers`                | Set of authentication handler names that must successfully authenticate credentials in order to access the service.
| `attributeReleasePolicy`          | The policy that describes the set of attributes allows to be released to the application, as well as any other filtering logic needed to weed some out. See [this guide](../integration/Attribute-Release.html) for more details on attribute release and filters.
| `logoutType`                      | Defines how this service should be treated once the logout protocol is initiated. Acceptable values are `LogoutType.BACK_CHANNEL`, `LogoutType.FRONT_CHANNEL` or `LogoutType.NONE`. See [this guide](Logout-Single-Signout.html) for more details on logout.
| `usernameAttributeProvider`       | The provider configuration which dictates what value as the "username" should be sent back to the application. See [this guide](../integration/Attribute-Release.html) for more details on attribute release and filters.
| `accessStrategy`                  | The strategy configuration that outlines and access rules for this service. It describes whether the service is allowed, authorized to participate in SSO, or can be granted access from the CAS perspective based on a particular attribute-defined role, aka RBAC. See [this guide](../integration/Attribute-Release.html) for more details on attribute release and filters.  
| `publicKey`                       | The public key associated with this service that is used to authorize the request by encrypting certain elements and attributes in the CAS validation protocol response, such as [the PGT](Configuring-Proxy-Authentication.html) or [the credential](../integration/ClearPass.html). See [this guide](../integration/Attribute-Release.html) for more details on attribute release and filters.  
| `logoutUrl`                       | URL endpoint for this service to receive logout requests. See [this guide](Logout-Single-Signout.html) for more details
| `properties`                  		| Extra metadata associated with this service in form of key/value pairs. This is used to inject custom fields into the service definition, to be used later by extension modules to define additional behavior on a per-service basis.

### Configure Service Access Strategy

[See this guide](Configuring-Service-Access-Strategy.html) for more info please.

### Configure Service Custom Properties

[See this guide](Configuring-Service-Custom-Properties.html) for more info please.


### Configure Proxy Authentication Policy
Each registered application in the registry may be assigned a proxy policy to determine whether the service is allowed for proxy authentication. This means that a PGT will not be issued to a service unless the proxy policy is configured to allow it. Additionally, the policy could also define which endpoint urls are in fact allowed to receive the PGT.

Note that by default, the proxy authentication is disallowed for all applications.

#### Components

##### `RefuseRegisteredServiceProxyPolicy`
Disallows proxy authentication for a service. This is default policy and need not be configured explicitly.

{% highlight json %}
{
  "@class" : "org.jasig.cas.services.RegexRegisteredService",
  "serviceId" : "testId",
  "name" : "testId",
  "id" : 1,
  "proxyPolicy" : {
    "@class" : "org.jasig.cas.services.RefuseRegisteredServiceProxyPolicy"
  }
}
{% endhighlight %}

##### `RegexMatchingRegisteredServiceProxyPolicy`
A proxy policy that only allows proxying to PGT urls that match the specified regex pattern.


{% highlight json %}
{
  "@class" : "org.jasig.cas.services.RegexRegisteredService",
  "serviceId" : "testId",
  "name" : "testId",
  "id" : 1,
  "proxyPolicy" : {
    "@class" : "org.jasig.cas.services.RegexMatchingRegisteredServiceProxyPolicy",
    "pattern" : "^https?://.*"
  }
}
{% endhighlight %}

## Persisting Registered Service Data

###### `InMemoryServiceRegistryDaoImpl`
This DAO is an in-memory services management seeded from registration beans wired via Spring beans.

{% highlight xml %}
<bean id="serviceRegistryDao"
      class="org.jasig.cas.services.InMemoryServiceRegistryDaoImpl"
      p:registeredServices-ref="registeredServicesList" />

<util:list id="registeredServicesList">
    <bean class="org.jasig.cas.services.RegexRegisteredService"
          p:id="1"
          p:name="HTTPS and IMAPS services on example.com"
          p:serviceId="^(https|imaps)://([A-Za-z0-9_-]+\.)*example\.com/.*"
          p:evaluationOrder="0" />
</util:list>

{% endhighlight %}

This component is _NOT_ suitable for use with the service management webapp since it does not persist data.
On the other hand, it is perfectly acceptable for deployments where the XML configuration is authoritative for
service registry data and the UI will not be used.

###### `JsonServiceRegistryDao`
This DAO reads services definitions from JSON configuration files at the application context initialization time. JSON files are
expected to be found inside a configured directory location and this DAO will recursively look through the directory structure to find relevant JSON files.

{% highlight xml %}
<bean id="serviceRegistryDao" class="org.jasig.cas.services.JsonServiceRegistryDao"
          c:configDirectory="file:/etc/cas/json" />
{% endhighlight %}

A sample JSON file follows:

{% highlight json %}
{
  "@class" : "org.jasig.cas.services.RegexRegisteredService",
  "serviceId" : "testId",
  "name" : "testId",
  "id" : 1,
  "evaluationOrder" : 0
}
{% endhighlight %}


<div class="alert alert-warning"><strong>Clustering Services</strong><p>
You MUST consider that if your CAS server deployment is clustered, each CAS node in the cluster must have
access to the same set of JSON configuration files as the other, or you may have to devise a strategy to keep
changes synchronized from one node to the next.
</p></div>

The JSON service registry is also able to auto detect changes to the specified directory. It will monitor changes to recognize
file additions, removals and updates and will auto-refresh CAS so changes do happen instantly.

<div class="alert alert-info"><strong>Escaping Characters</strong><p>
Please make sure all field values in the JSON blob are correctly escaped, specially for the service id. If the service is defined as a regular expression, certain regex constructs such as "." and "\d" need to be doubly escaped.
</p></div>

The naming convention for new JSON files is recommended to be the following:


{% highlight bash %}

JSON fileName = serviceName + "-" + serviceNumericId + ".json"

{% endhighlight %}


Based on the above formula, for example the above JSON snippet shall be named: `testJsonFile-103935657744185.json`

<div class="alert alert-warning"><strong>Duplicate Services</strong><p>
As you add more files to the directory, you need to be absolutely sure that no two service definitions
will have the same id. If this happens, loading one definition will stop loading the other. While service ids
can be chosen arbitrarily, make sure all service numeric identifiers are unique. CAS will also output warnings
if duplicate data is found.
</p></div>


###### `MongoServiceRegistryDao`
This DAO uses a [MongoDb](https://www.mongodb.org/) instance to load and persist service definitions. Support is enabled by adding the following module into the Maven overlay:

{% highlight xml %}
<dependency>
    <groupId>org.jasig.cas</groupId>
    <artifactId>cas-server-integration-mongo</artifactId>
    <version>${cas.version}</version>
</dependency>
{% endhighlight %}

###### Configuration

This implementation auto-configures most of the internal details.
The following configuration in `cas.properties` is required.

{% highlight properties %}
mongodb.host=mongodb database url
mongodb.port=mongodb database port
mongodb.userId=mongodb userid to bind
mongodb.userPassword=mongodb password to bind
cas.service.registry.mongo.db=Collection name to store service definitions
{% endhighlight %}

###### `LdapServiceRegistryDao`
Service registry implementation which stores the services in a LDAP Directory. Uses an instance of `LdapRegisteredServiceMapper`, that by default is `DefaultLdapRegisteredServiceMapper` in order to configure settings for retrieval, search and persistence of service definitions. By default, entries are assigned the `objectclass` `casRegisteredService` attribute and are looked up by the `uid` attribute.

{% highlight xml %}

<context:component-scan base-package="org.jasig.cas" />

<bean id="serviceRegistryDao"
      class="org.jasig.cas.adaptors.ldap.services.LdapServiceRegistryDao"
      p:connectionFactory-ref="pooledLdapConnectionFactory"
      p:searchRequest-ref="searchRequest"
      p:ldapServiceMapper-ref="ldapServiceMapper" />

<bean id="ldapServiceMapper"
      class="org.jasig.cas.adaptors.ldap.services.DefaultLdapRegisteredServiceMapper"/>
{% endhighlight %}

Note that the configuration of the mapper is optional and need not explicitly exist.

<p/>

###### `DefaultLdapRegisteredServiceMapper`
The default mapper has support for the following optional items:

| Field                             | Default Value
|-----------------------------------+--------------------------------------------------+
| `objectClass`                     | casRegisteredService
| `serviceDefinitionAttribute`      | description
| `idAttribute`                     | uid

Service definitions are by default stored inside the `serviceDefinitionAttribute` attribute as JSON objects. The format and syntax of the JSON is identical to that of `JsonServiceRegistryDao`.


###### `JpaServiceRegistryDaoImpl`
Stores registered service data in a database; the preferred choice when using the service management webapp.
The following schema shall be generated by CAS automatically for brand new deployments, and must be massaged
when doing CAS upgrades:

{% highlight sql %}

create table RegisteredServiceImpl (
    expression_type VARCHAR(15) DEFAULT 'ant' not null,
    id bigint generated by default as identity (start with 1),
    access_strategy blob(255),
    attribute_release blob(255),
    description varchar(255) not null,
    evaluation_order integer not null,
    logo varchar(255),
    logout_type integer,
    logout_url varchar(255),
    name varchar(255) not null,
    proxy_policy blob(255),
    required_handlers blob(255),
    public_key blob(255),
    serviceId varchar(255) not null,
    theme varchar(255),
    username_attr blob(255),
    primary key (id)
)

{% endhighlight %}


The following configuration template may be applied to `deployerConfigContext.xml` to provide for persistent
registered service storage.

{% highlight xml %}
<bean
    id="dataSource"
    class="com.mchange.v2.c3p0.ComboPooledDataSource"
    p:driverClass="${database.driverClass:org.hsqldb.jdbcDriver}"
    p:jdbcUrl="${database.url:jdbc:hsqldb:mem:cas-ticket-registry}"
    p:user="${database.user:sa}"
    p:password="${database.password:}"
    p:initialPoolSize="${database.pool.minSize:6}"
    p:minPoolSize="${database.pool.minSize:6}"
    p:maxPoolSize="${database.pool.maxSize:18}"
    p:maxIdleTimeExcessConnections="${database.pool.maxIdleTime:1000}"
    p:checkoutTimeout="${database.pool.maxWait:2000}"
    p:acquireIncrement="${database.pool.acquireIncrement:16}"
    p:acquireRetryAttempts="${database.pool.acquireRetryAttempts:5}"
    p:acquireRetryDelay="${database.pool.acquireRetryDelay:2000}"
    p:idleConnectionTestPeriod="${database.pool.idleConnectionTestPeriod:30}"
    p:preferredTestQuery="${database.pool.connectionHealthQuery:select 1}"
/>

<bean class="org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor"/>

<util:list id="packagesToScan">
    <value>org.jasig.cas.services</value>
    <value>org.jasig.cas.support.oauth.services</value>
</util:list>

<bean class="org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter"
      id="jpaVendorAdapter"
      p:generateDdl="true"
      p:showSql="true" />

<bean id="entityManagerFactory"
      class="org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean"
      p:dataSource-ref="dataSource"
      p:jpaVendorAdapter-ref="jpaVendorAdapter"
      p:packagesToScan-ref="packagesToScan">
    <property name="jpaProperties">
        <props>
            <prop key="hibernate.dialect">${database.dialect:org.hibernate.dialect.HSQLDialect}</prop>
            <prop key="hibernate.hbm2ddl.auto">create-drop</prop>
            <prop key="hibernate.jdbc.batch_size">${database.batchSize:1}</prop>
        </props>
    </property>
</bean>

<bean id="transactionManager" class="org.springframework.orm.jpa.JpaTransactionManager"
      p:entityManagerFactory-ref="entityManagerFactory" />

<tx:advice id="txAdvice" transaction-manager="transactionManager">
    <tx:attributes>
        <tx:method name="delete*" read-only="false"/>
        <tx:method name="save*" read-only="false"/>
        <tx:method name="update*" read-only="false"/>
        <tx:method name="get*" read-only="true"/>
        <tx:method name="*" />
    </tx:attributes>
</tx:advice>

<aop:config>
    <aop:pointcut id="servicesManagerOperations" expression="execution(* org.jasig.cas.services.JpaServiceRegistryDaoImpl.*(..))"/>
    <aop:advisor advice-ref="txAdvice" pointcut-ref="servicesManagerOperations"/>
</aop:config>

<bean id="serviceRegistryDao"
      class="org.jasig.cas.services.JpaServiceRegistryDaoImpl" />
{% endhighlight %}

You will also need to change the property `hibernate.dialect` in adequacy with your database in `cas.properties` and `deployerConfigContext.xml`.

For example, for MYSQL the setting would be:

In `cas.properties`:

{% highlight bash %}
database.hibernate.dialect=org.hibernate.dialect.MySQLDialect
{% endhighlight %}

In `deployerConfigContext.xml`:

{% highlight xml %}
<prop key="hibernate.dialect">${database.hibernate.dialect}</prop>
{% endhighlight %}

You will also need to ensure that the xml configuration file contains the `tx`, `util` and `aop` namespaces:

{% highlight xml %}
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:p="http://www.springframework.org/schema/p"
       xmlns:util="http://www.springframework.org/schema/util"
       xsi:schemaLocation="
       http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop.xsd
       http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx.xsd">
{% endhighlight %}

Finally, be sure to add those to your `pom.xml`:

{% highlight xml %}

<dependency>
    <groupId>org.jasig.cas</groupId>
    <artifactId>cas-server-support-jdbc</artifactId>
    <version>${cas.version}</version>
    <scope>runtime</scope>
</dependency>

<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-entitymanager</artifactId>
    <version>${hibernate.entitymgmr.version}</version>
</dependency>

<dependency>
  <groupId>com.mchange</groupId>
  <artifactId>c3p0</artifactId>
  <version>${c3p0.version}</version>
</dependency>

<!-- Required for MySQL. Swap with the appropriate driver for your deployment.
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>${mysql.connector.version}</version>
</dependency>
-->

{% endhighlight %}

## Service Management Webapp
The Services Management web application is a standalone application that helps one manage service registrations and entries via a customizable user interface. The management web application *MUST* share the same registry configuration as
the CAS server itself so the entire system can load the same services data. To learn more about the management webapp,
[please see this guide](Installing-ServicesMgmt-Webapp.html).
