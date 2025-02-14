<!-- fragment:keep -->

<p/>

If you need to design your own password encoding scheme where the type is specified as a fully 
qualified Java class name, the structure of the class would be similar to the following:

```java
package org.example.cas;

import org.springframework.security.crypto.codec.*;
import org.springframework.security.crypto.password.*;

public class MyEncoder extends AbstractPasswordEncoder {
    @Override
    protected byte[] encode(CharSequence rawPassword, byte[] salt) {
        return ...
    }
}
```

If you need to design your own password encoding scheme where the type is 
specified as a path to a Groovy script, the structure of the script would be similar to the following:

```groovy
import java.util.*

byte[] run(final Object... args) {
    def rawPassword = args[0]
    def generatedSalt = args[1]
    def logger = args[2]
    def casApplicationContext = args[3]

    logger.debug("Encoding password...")
    return ...
}

Boolean matches(final Object... args) {
    def rawPassword = args[0]
    def encodedPassword = args[1]
    def logger = args[2]
    def casApplicationContext = args[3]

   logger.debug("Does match or not ?");
   return ...
```
