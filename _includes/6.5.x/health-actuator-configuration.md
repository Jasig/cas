<!-- fragment:keep -->

The `health` endpoint may also be configured to show details via the following conditions:

| URL               | Description                                                                                                              |
|-------------------|--------------------------------------------------------------------------------------------------------------------------|
| `never`           | Never display details of health monitors.                                                                                |
| `always`          | Always display details of health monitors.                                                                               |
| `when-authorized` | Details are only shown to authorized users. Authorized roles can be configured using `management.endpoint.health.roles`. |

The results and details of the `health` endpoints are produced by a number of
health indicator components that may monitor different systems, such as LDAP connection
pools, database connections, etc.

```properties
# management.health.[indicator].enabled=false|true
```

The following health indicator names are available, given the presence of the appropriate CAS feature:

| Health Indicator                               | Description                                                                               |
|------------------------------------------------|-------------------------------------------------------------------------------------------|
| `memoryHealthIndicator`                        | Reports back on the health status of CAS JVM memory usage, etc.                           |
| `systemHealthIndicator`                        | Reports back on the health of the system of the CAS server.(Load, Uptime, Heap, CPU etc.) |
| `sessionHealthIndicator`                       | Reports back on the health status of CAS tickets and SSO session usage.                   |
| `duoSecurityHealthIndicator`                   | Reports back on the health status of Duo Security APIs.                                   |
| `ehcacheHealthIndicator`                       | Reports back on the health status of Ehcache caches.                                      |
| `hazelcastHealthIndicator`                     | Reports back on the health status of Hazelcast caches.                                    |
| `dataSourceHealthIndicator`                    | Reports back on the health status of JDBC connections.                                    |
| `pooledLdapConnectionFactoryHealthIndicator`   | Reports back on the health status of LDAP connection pools.                               |
| `memcachedHealthIndicator`                     | Reports back on the health status of Memcached connections.                               |
| `mongoHealthIndicator`                         | Reports back on the health status of MongoDb connections.                                 |
| `samlRegisteredServiceMetadataHealthIndicator` | Reports back on the health status of SAML2 service provider metadata sources.             |
