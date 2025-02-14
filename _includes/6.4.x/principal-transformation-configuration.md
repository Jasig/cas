<!-- fragment:keep -->

<p/>

Authentication handlers as part of principal transformation may also be provided a path to a 
Groovy script to transform the provided username. The outline of the script may take on the following form:

```groovy
def String run(final Object... args) {
    def providedUsername = args[0]
    def logger = args[1]
    return providedUsername.concat("SomethingElse")
}
```

