---
layout: default
title: CAS - Database Authentication
---

# Database Authentication
Database authentication components are enabled by including the following dependencies in the Maven WAR overlay:

```xml
<dependency>
    <groupId>org.jasig.cas</groupId>
    <artifactId>cas-server-support-jdbc</artifactId>
    <version>${cas.version}</version>
</dependency>
```

## Connection Pooling
All database authentication components require a `DataSource` for acquiring connections to the underlying database.
The use of connection pooling is _strongly_ recommended, and the [c3p0 library](http://www.mchange.com/projects/c3p0/)
is a good choice that we discuss here.

### Pooled Data Source Example
A bean named `dataSource` must be defined for CAS components that use a database.

```xml
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
```

The following properties may be used as a starting point for connection pool configuration/tuning.

```properties
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
```


## Database Components
CAS provides the following components to accommodate different database authentication needs.

###### `QueryDatabaseAuthenticationHandler`
Authenticates a user by comparing the (hashed) user password against the password on record determined by a
configurable database query.

```xml
<alias name="queryDatabaseAuthenticationHandler" alias="primaryAuthenticationHandler" />
<alias name="dataSource" alias="queryDatabaseDataSource" />
```

The following settings are applicable:

```properties
# cas.jdbc.authn.query.sql=select password from users where username=?
```

###### `SearchModeSearchDatabaseAuthenticationHandler`
Searches for a user record by querying against a username and password; the user is authenticated if at
least one result is found.

```xml
<alias name="searchModeSearchDatabaseAuthenticationHandler" alias="primaryAuthenticationHandler" />
<alias name="dataSource" alias="searchModeDatabaseDataSource" />
```

The following settings are applicable:

```properties
# cas.jdbc.authn.search.password=
# cas.jdbc.authn.search.user=
# cas.jdbc.authn.search.table=
```


###### `BindModeSearchDatabaseAuthenticationHandler`
Authenticates a user by attempting to create a database connection using the username and (hashed) password.

The following example does not perform any password encoding since most JDBC drivers natively encode plaintext
passwords to the appropriate format required by the underlying database. Note authentication is equivalent to the
ability to establish a connection with username/password credentials. This handler is the easiest to configure
(usually none required), but least flexible, of the database authentication components.

```xml
<alias name="bindModeSearchDatabaseAuthenticationHandler" alias="primaryAuthenticationHandler" />
<alias name="dataSource" alias="bindSearchDatabaseDataSource" />
```

###### `QueryAndEncodeDatabaseAuthenticationHandler`
A JDBC querying handler that will pull back the password and
the private salt value for a user and validate the encoded
password using the public salt value. Assumes everything
is inside the same database table. Supports settings for
number of iterations as well as private salt.

This password encoding method, combines the private Salt and the public salt which it
prepends to the password before hashing.
If multiple iterations are used, the bytecode Hash of the first iteration is
rehashed without the salt values.
The final hash is converted to Hex before comparing it to the database value.

```xml
<alias name="queryAndEncodeDatabaseAuthenticationHandler" alias="primaryAuthenticationHandler" />
<alias name="dataSource" alias="queryEncodeDatabaseDataSource" />
```

The following settings are applicable:

```properties
# cas.jdbc.authn.query.encode.sql=
# cas.jdbc.authn.query.encode.alg=
# cas.jdbc.authn.query.encode.salt.static=
# cas.jdbc.authn.query.encode.password=
# cas.jdbc.authn.query.encode.salt=
# cas.jdbc.authn.query.encode.iterations.field=
# cas.jdbc.authn.query.encode.iterations=
```
