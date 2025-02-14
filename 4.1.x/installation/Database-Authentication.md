---
layout: default
title: CAS - Database Authentication
---

# Database Authentication
Database authentication components are enabled by including the following dependencies in the Maven WAR overlay:

{% highlight xml %}
<dependency>
    <groupId>org.jasig.cas</groupId>
    <artifactId>cas-server-support-jdbc</artifactId>
    <version>${cas.version}</version>
</dependency>
<dependency>
    <groupId>c3p0</groupId>
    <artifactId>c3p0</artifactId>
    <version>0.9.1.2</version>
</dependency>
{% endhighlight %}

## Connection Pooling
All database authentication components require a `DataSource` for acquiring connections to the underlying database.
The use of connection pooling is _strongly_ recommended, and the [c3p0 library](http://www.mchange.com/projects/c3p0/)
is a good choice that we discuss here.
[Tomcat JDBC Pool](http://tomcat.apache.org/tomcat-7.0-doc/jdbc-pool.html) is another competent alternative.
Note that the connection pool dependency mentioned above should be modified according to the choice of connection pool
components.


### Pooled Data Source Example
A bean named `dataSource` must be defined for CAS components that use a database. A bean like the following should be
defined in `deployerConfigContext.xml`.
{% highlight xml %}
<bean id="dataSource"
  class="com.mchange.v2.c3p0.ComboPooledDataSource"
  p:driverClass="${database.driverClass}"
  p:jdbcUrl="${database.url}"
  p:user="${database.user}"
  p:password="${database.password}"
  p:initialPoolSize="${database.pool.minSize}"
  p:minPoolSize="${database.pool.minSize}"
  p:maxPoolSize="${database.pool.maxSize}"
  p:maxIdleTimeExcessConnections="${database.pool.maxIdleTime}"
  p:checkoutTimeout="${database.pool.maxWait}"
  p:acquireIncrement="${database.pool.acquireIncrement}"
  p:acquireRetryAttempts="${database.pool.acquireRetryAttempts}"
  p:acquireRetryDelay="${database.pool.acquireRetryDelay}"
  p:idleConnectionTestPeriod="${database.pool.idleConnectionTestPeriod}"
  p:preferredTestQuery="${database.pool.connectionHealthQuery}" />
{% endhighlight %}

The following properties may be used as a starting point for connection pool configuration/tuning.

{% highlight properties %}
    # == Basic database connection pool configuration ==
    database.driverClass=org.postgresql.Driver
    database.url=jdbc:postgresql://database.example.com/cas?ssl=true
    database.user=somebody
    database.password=meaningless
    database.pool.minSize=6
    database.pool.maxSize=18
     
    # Maximum amount of time to wait in ms for a connection to become
    # available when the pool is exhausted
    database.pool.maxWait=10000
     
    # Amount of time in seconds after which idle connections
    # in excess of minimum size are pruned.
    database.pool.maxIdleTime=120
     
    # Number of connections to obtain on pool exhaustion condition.
    # The maximum pool size is always respected when acquiring
    # new connections.
    database.pool.acquireIncrement=6
     
    # == Connection testing settings ==
     
    # Period in s at which a health query will be issued on idle
    # connections to determine connection liveliness.
    database.pool.idleConnectionTestPeriod=30
     
    # Query executed periodically to test health
    database.pool.connectionHealthQuery=select 1
     
    # == Database recovery settings ==
     
    # Number of times to retry acquiring a _new_ connection
    # when an error is encountered during acquisition.
    database.pool.acquireRetryAttempts=5
     
    # Amount of time in ms to wait between successive aquire retry attempts.
    database.pool.acquireRetryDelay=2000
{% endhighlight %}


## Database Components
CAS provides the followng components to accommodate different database authentication needs.

###### `QueryDatabaseAuthenticationHandler`
Authenticates a user by comparing the (hashed) user password against the password on record determined by a
configurable database query. `QueryDatabaseAuthenticationHandler` is by far the most flexible and easiest to
configure for anyone proficient with SQL, but `SearchModeSearchDatabaseAuthenticationHandler` provides an alternative
for simple queries based solely on username and password and builds the SQL query using straightforward inputs.

The following database schema for user data is assumed in the following two examples that leverage SQL queries
to authenticate users.

{% highlight sql %}
    create table users (
        username varchar(50) not null,
        password varchar(50) not null,
        active bit not null );
{% endhighlight %}

The following example uses an MD5 hash algorithm and searches exclusively for _active_ users.
{% highlight xml %}
<bean id="passwordEncoder"
      class="org.jasig.cas.authentication.handler.DefaultPasswordEncoder"
      c:encodingAlgorithm="MD5"
      p:characterEncoding="UTF-8" />

<bean id="dbAuthHandler"
      class="org.jasig.cas.adaptors.jdbc.QueryDatabaseAuthenticationHandler"
      p:dataSource-ref="dataSource"
      p:passwordEncoder-ref="passwordEncoder"
      p:sql="select password from users where username=? and active=1" />
{% endhighlight %}


###### `SearchModeSearchDatabaseAuthenticationHandler`
Searches for a user record by querying against a username and (hashed) password; the user is authenticated if at
least one result is found.

The following example uses a SHA1 hash algorithm to authenticate users.
{% highlight xml %}
<bean id="passwordEncoder"
      class="org.jasig.cas.authentication.handler.DefaultPasswordEncoder"
      c:encodingAlgorithm="SHA1"
      p:characterEncoding="UTF-8" />

<bean id="dbAuthHandler"
      class="org.jasig.cas.adaptors.jdbc.SearchModeSearchDatabaseAuthenticationHandler"
      p:dataSource-ref="dataSource"
      p:passwordEncoder-ref="passwordEncoder"
      p:tableUsers="users"
      p:fieldUser="username"
      p:fieldPassword="password" />
{% endhighlight %}


###### `BindModeSearchDatabaseAuthenticationHandler`
Authenticates a user by attempting to create a database connection using the username and (hashed) password.

The following example does not perform any password encoding since most JDBC drivers natively encode plaintext
passwords to the appropriate format required by the underlying database. Note authentication is equivalent to the
ability to establish a connection with username/password credentials. This handler is the easiest to configure
(usually none required), but least flexible, of the database authentication components.
{% highlight xml %}
<bean id="dbAuthHandler"
      class="org.jasig.cas.adaptors.jdbc.BindModeSearchDatabaseAuthenticationHandler"
      p:dataSource-ref="dataSource" />
{% endhighlight %}


###### `QueryAndEncodeDatabaseAuthenticationHandler`
A JDBC querying handler that will pull back the password and
the private salt value for a user and validate the encoded
password using the public salt value. Assumes everything
is inside the same database table. Supports settings for
number of iterations as well as private salt.

This password encoding method, combines the private Salt and the public salt which it prepends to the password before hashing.
If multiple iterations are used, the bytecode Hash of the first iteration is rehashed without the salt values.
The final hash is converted to Hex before comparing it to the database value.

{% highlight xml %}

<util:constant id="ALG" static-field="org.apache.commons.codec.digest.MessageDigestAlgorithms.SHA_512"/>

<bean id="dbAuthHandler"
      class="org.jasig.cas.adaptors.jdbc.QueryAndEncodeDatabaseAuthenticationHandler"
      c:dataSource-ref="dataSource"
      c:algorithmName-ref="ALG"
      c:sql="SELECT * FROM table WHERE username = ?"
      p:staticSalt="private_salt"
      p:passwordFieldName="password"
      p:saltFieldName="public_salt"
      p:numberOfIterationsFieldName="num_iter"
      p:numberOfIterations="10" />
{% endhighlight %}
