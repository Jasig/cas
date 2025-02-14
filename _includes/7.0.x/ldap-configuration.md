<!-- fragment:keep -->

<p/>

#### LDAP Scriptable Search Filter

LDAP search filters can point to an external Groovy script to dynamically construct the final filter template.

The script itself may be designed as:

```groovy
import org.ldaptive.*
import org.springframework.context.*

def run(Object[] args) {
    def (filter,parameters,applicationContext,logger) = args

    logger.info("Configuring LDAP filter")
    filter.setFilter("uid=something")
}
```

The following parameters are passed to the script:

| Parameter            | Description                                                                 |
|----------------------|-----------------------------------------------------------------------------|
| `filter`             | `FilterTemplate` to be updated by the script and used for the LDAP query.   |
| `parameters`         | Map of query parameters which may be used to construct the final filter.    |
| `applicationContext` | Reference to the Spring `ApplicationContext` reference.                     |
| `logger`             | The object responsible for issuing log messages such as `logger.info(...)`. |

