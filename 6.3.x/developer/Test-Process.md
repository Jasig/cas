---
layout: default
title: CAS - Test Process
category: Developer
---

# Test Process

This page documents the steps that a CAS developer/contributor should take for testing a CAS server deployment during development. For additional
instructions and guidance on the general build process, please [see this page](Build-Process.html).

## Testing Modules

To test the functionality provided by a given CAS module, execute the following steps:

- For the tomcat, undertow or jetty webapp, add the module reference to the `webapp.gradle` build script of web application you intend to run:

```gradle
implementation project(":support:cas-server-support-modulename")
```

Alternatively, set a `casModules` property in the root project's `gradle.properties` or `~/.gradle/gradle.properties` to a 
comma separated list of modules without the `cas-server-` prefix:

For example:

```properties
casModules=monitor,\
    ldap,\
    x509,\
    bootadmin-client
```

Or set the property on the command-line:

```bash
bc -PcasModules=ldap,x509
```

...where `bc` is an [alias for building CAS](Build-Process.html#sample-build-aliases).

Prepare [the embedded container](Build-Process.html#embedded-containers), to run and deploy the web application.

## Unit / Integration Testing

To simplify the test execution process, you may take advantage of the `testcas.sh` script found at the root of the repository as such:

```bash
# chmod +x ./testcas.sh
./testcas.sh --category <category> [--test <test-class>] [--debug] [--with-coverage]
```

To learn more about the script, use:

```bash
./testcas.sh --help
```

All unit and integration tests are executed by the [continuous integration system](Test-Process.html#continuous-integration).

## Code Coverage & Metrics

Code coverage metrics are collected and reported by the following platforms:

| System                            | Badge
|-----------------------------------+---------------------------------------------------------------------------+
| Codacy           | [![Codacy Badge](https://app.codacy.com/project/badge/Coverage/29973e19266547dab7ab73f1a511c826)](https://www.codacy.com/gh/apereo/cas/dashboard?utm_source=github.com&utm_medium=referral&utm_content=apereo/cas&utm_campaign=Badge_Coverage)
| SonarCloud           | [![Coverage](https://sonarcloud.io/api/project_badges/measure?project=org.apereo.cas%3Acas-server&metric=coverage)](https://sonarcloud.io/dashboard?id=org.apereo.cas%3Acas-server)
| CodeCov           | [![codecov](https://codecov.io/gh/apereo/cas/branch/master/graph/badge.svg)](https://codecov.io/gh/apereo/cas)

Quality metrics are collected and reported by the following platforms:

| System                            | Badge
|-----------------------------------+---------------------------------------------------------------------------+
| Codacy           | [![Codacy Badge](https://app.codacy.com/project/badge/Grade/29973e19266547dab7ab73f1a511c826)](https://www.codacy.com/gh/apereo/cas/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=apereo/cas&amp;utm_campaign=Badge_Grade)
| SonarCloud Quality Gate           | [![Sonarqube Quality](https://sonarcloud.io/api/project_badges/measure?project=org.apereo.cas%3Acas-server&metric=alert_status)](https://sonarcloud.io/dashboard?id=org.apereo.cas%3Acas-server)
| SonarCloud Maintainability            | [![Sonarqube Quality](https://sonarcloud.io/api/project_badges/measure?project=org.apereo.cas%3Acas-server&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=org.apereo.cas%3Acas-server) 

## Browser & Functional Testing

Automated browser testing is done via the [Puppeteer framework](https://pptr.dev/). Puppeteer is a Node library which provides a high-level 
API to control Chrome or Chromium over the DevTools Protocol and runs headless by default.

Functional tests start by generating a plain CAS overlay as a baseline that is able to run under HTTPS using a pre-generated keystore.
This overlay is supplied the test scenario configuration that explain the required modules, properties, etc to use when CAS is deployed
inside an embedded Apache Tomcat container. Once running, the Puppeteer script is executed by Node for the given test scenario to verify
specific functionality such as successful logins, generation of tickets, etc.

All functional and browser tests are executed by the [continuous integration system](Test-Process.html#continuous-integration). If you 
are adding a new batch of tests, make sure the scenario (i.e. test) name is included in the CI configuration.

To help simplify the testing process, you may use the following bash function in your `.profile`:

```bash
function pupcas() {
  scenario=$1
  /path/to/cas/ci/tests/puppeteer/run.sh /path/to/cas/ci/tests/puppeteer/scenarios/"${scenario}"
}
```

...which can later be invoked as:

```bash
pupcas <scenario-name>
```
 
To successfully run tests, you need to make [jq](https://stedolan.github.io/jq/) is installed.

## Continuous Integration

Unit and integration tests are automatically executed by the CAS CI system, [GitHub Actions](https://github.com/apereo/cas/actions).
