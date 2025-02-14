<!-- fragment:keep -->

{% assign healthIndicators = include.healthIndicators | split: "," %}

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
{% for indic in healthIndicators %}management.health.{{ indic }}.enabled=true
{% endfor %}```