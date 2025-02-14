---
layout: default
title: CAS - Spring Expressions
category: Installation
---

# Spring Expression Language

A number of components in CAS are able to take advantage of 
the [Spring Expression Language](https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html#expressions) syntax
for their internal configuration. This is primarily useful when the component
wishes to have access to system variables, environment properties or in general requires a more dynamic or
programmatic strategy before it can be fully functional.

Expressions are expected to be encapsulated inside the `${...}` syntax. Predefined variables 
are expected to be preceded with the `#` character. The following predefined variables are available:

| Variable                 | Description
|--------------------------|----------------------------------------------------------
| `systemProperties`       | Map of system properties, loaded once typically on startup.
| `sysProps`               | Same as above.
| `environmentVars`        | Map of environment variables, loaded once typically on startup.
| `environmentVariables`   | Same as above.
| `envVars`                | Same as above.
| `env`                    | Same as above.
| `tempDir`                | Path to the temp directory.
| `uuid`                   | Auto-generated `UUID` value.
| `randomNumber2`          | 2-digit random number.
| `randomNumber4`          | 4-digit random number.
| `randomNumber6`          | 6-digit random number.
| `randomNumber8`          | 8-digit random number.
| `randomString4`          | 4-character random word.
| `randomString6`          | 6-character random word.
| `randomString8`          | 8-character random word.

## Examples

- Assuming system property `tier` with a value of `production` is available, the configuration 
value `file://${#systemProperties['tier']}/file.json` translates to `file://production/file.json`
- Assuming environment variable `tier` with a value of `qa` is available, the configuration 
value `file://${#environmentVariables['tier']}/file.json` translates to `file://qa/file.json`
- Using `${#randomString6}` translates to a 6-character random word, such as `qemguz`.
- Using `${#randomNumber8}` translates to a 8-digit random number, such as `75915283`.
