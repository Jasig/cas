<div class="alert alert-warning"><strong>Usage</strong>
<p><strong>This feature is deprecated and is scheduled to be removed in the future.</strong></p>
</div>

Similar to the Groovy option but more versatile, this option takes advantage of Java's native
scripting API to invoke Groovy, Python or Javascript scripting engines to compile a pre-defined script to resolve attributes.
The following settings are relevant:

```properties
# cas.authn.attribute-repository.script[0].location=file:/etc/cas/script.groovy
# cas.authn.attribute-repository.script[0].order=0
# cas.authn.attribute-repository.script[0].id=
# cas.authn.attribute-repository.script[0].case-insensitive=false
# cas.authn.attribute-repository.script[0].engine-name=js|groovy|python
```

While Javascript and Groovy should be natively supported by CAS, Python scripts may need
to massage the CAS configuration to include the [Python modules](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22jython-standalone%22).

The Groovy script may be defined as:

```groovy
import java.util.*

Map<String, List<Object>> run(final Object... args) {
    def uid = args[0]
    def logger = args[1]

    logger.debug("Groovy things are happening just fine with UID: {}",uid)
    return[username:[uid], likes:["cheese", "food"], id:[1234,2,3,4,5], another:"attribute"]
}
```

The Javascript script may be defined as:

```javascript
function run(uid, logger) {
    print("Things are happening just fine")
    logger.warn("Javascript called with UID: {}",uid);

    // If you want to call back into Java, this is one way to do so
    var javaObj = new JavaImporter(org.yourorgname.yourpackagename);
    with (javaObj) {
        var objFromJava = JavaClassInPackage.someStaticMethod(uid);
    }

    var map = {};
    map["attr_from_java"] = objFromJava.getSomething();
    map["username"] = uid;
    map["likes"] = "cheese";
    map["id"] = [1234,2,3,4,5];
    map["another"] = "attribute";

    return map;
}
```
