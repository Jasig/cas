---
layout: default
title: CAS - Code Conventions
---

# Code Conventions

Following the lead of well established projects such as Apache and Eclipse, all code in CAS will comply with the [Code Conventions for the Java](http://java.sun.com/docs/codeconv/html/CodeConvTOC.doc.html) and additionally with the CAS specific conventions listed below. Javadoc should exist on all publicly exported class members and follow the [standard guidelines](http://java.sun.com/j2se/javadoc/writingdoccomments/index.html).

The following document describes the set of coding conventions that are specific to the CAS project:

## Tooling

- The current codebase takes advantage of the [Checkstyle engine](http://checkstyle.sourceforge.net) to [enforce conventions](https://github.com/Apereo/cas/blob/master/style/checkstyle-rules.xml) as much as possible. Certain number of checks are also executed using [SpotBugs](https://spotbugs.github.io/).
- Where appropriate, the codebase takes advantage of [Project Lombok](https://projectlombok.org/) for auto-generation of code constructs such as getters and setters, etc.
 
## Consistency

Try to keep consistent with the existing naming patterns when introducing new components, fields, and files. As an example if a CAS component is named as `SimpleCipherExecutor`, its natural alternative might be designed as `FancyCipherExecutor`. 

## Documentation

Generally all public APIs are expected to be adequately documented to a reasonable extent and just as well, all CAS configuration settings and properties are to be fully explained with examples, default values, etc as part of the field's Javadocs.

## Brackets

All brackets should appear in compact form and are mandatory even for single line statements.

```java
public class FooClass {
    public void barMethod() {
        if (...) {
            // single line statement
        }
    }
}
```


## Needless else clauses

```java
public class FooClass {
    public String barMethod() {
        if (...) {
            return "foo";
        }
         
        return bar;
    }
}
```

## Getter/Setter Methods

Generating Getter/Setter methods for fields is typically done using Project Lombok's `@Getter` and `@Setter` annotations.

## Constructors

Creating constructors is typically done using Project Lombok's `@NoArgsConstructor`, `@AllArgsConstructor` or `@RequiredArgsConstructor` annotations.

## Indentations

Code indentation should be set to use 4 spaces. Tabs should **never** be used for indentation.

## Static Members

Static member variables will always be in uppercase wherein each word is separated by an underscore:

```java
private static final String SOME_OBJECT = "TheObject"; 
```

## Logging

We use [SLF4J](http://www.slf4j.org/index.html) for logging. Unless noted otherwise, `LOGGER` objects are to be generated using Project Lombok's `@Slf4j` annotation.

[Parameterized log messages](http://www.slf4j.org/faq.html#logging_performance) are preferred:

```java
final Object entry = new SomeObject();
logger.debug("The entry is {}.", entry);
```

This is the preferred method over checking for a particular logging level and concatenating parameters through String objects.

## Qualifying instance variables with `this`

Try to qualify all instance variables with `this` with the exception of the Logging instances. 

## Use of the `var` keyword

Local variables are commended to use Project Lombok's `val`. In situations where mutability is needed, `var` is recommended.

## Naming testcases

If we were writing a JUnit testcase for code defined in `Foo.java`, we would name it `FooTests.java`. We do not allow any code which is not a testcase to have a name ending in "Tests". All testcase must use annotations `(@Test)` instead of extending `junit.framework.TestCase`. Furthermore, the usage of `junit.framework` classes is generally discouraged. 

## Injection

For required dependencies, the constructor injection must be used whereas setters can be used for optional dependencies.

## toString(), equals(), hashCode() methods

Take a look at Project Lombok's `@ToString` and `@EqualsAndHashCode` annotations.
