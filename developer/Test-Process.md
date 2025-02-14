---
layout: default
title: CAS - Test Process
category: Developer
---

{% include variables.html %}

# Test Process

This page documents the steps that a CAS developer/contributor should take for testing a CAS 
server deployment during development. For additional
instructions and guidance on the general build process, please [see this page](Build-Process.html).

<div class="alert alert-info">:information_source: <strong>Contributions</strong><p>Patches submitted to the CAS codebase 
in form of pull requests must pass all automated unit or integration tests, and/or 
provide adequate unit or integration tests for the proposed changes. In the absence of appropriate test cases,
the contribution most likely will not be accepted into the codebase and ultimately may be closed.</p></div>

## Test Cases

The following types of test cases in the project are those that CAS developers/contributors need to review,

<div class="alert alert-info mt-3">:information_source: <strong>Remember</strong><p>
If you are about to describe a problem, please try to put together a test case that concretely demonstrates
the issue in an automated fashion in an environment that is fairly 
isolated, <strong>WITHOUT</strong> manual instructions and guidance 
as much as possible. Descriptive instructions and language
to explain what one must manually do (i.e. go here, click there, wait 3 seconds, etc) in order 
to reproduce an issue or environment are not as effective or
acceptable as evidence of an issue, and may require a significant time and research investment to ultimately
get to a root cause. Save yourself and everyone else time and headache,
and put together automated, repeatable, verifiable <i>reproducers</i>.
</p></div>

### Unit Tests

Unit tests are composed of small pieces of work or functionality that generally can be tested in isolation. They 
are created as Java test classes, and individual test scenarios are typically annotated 
with the `@Test` annotation and then executed by the test framework. 

For example, a `src/main/java/Extractor.java` type of component would have 
its test cases inside a `src/test/java/ExtractorTests.java` test class.
   
### Integration Tests

In some scenarios unit tests also run a series of tests against databases, external systems or APIs to verify functionality
and correctness of integration. For example, a `MongoDbTicketRegistry` type of component would 
require a MongoDb running instance and special markup and annotations to run tests when that external instance is up 
and running. Structurally speaking, such tests are almost identical to plain vanilla unit tests and may only contain additional 
decorations and annotations,depending on the test system.

### Functional Tests
            
Functional tests are categories of tests that verify combinations of scenarios and behaviors from the perspective
of an end-user. Such tests typically are composed of an execution script and scenario, and may involve a headless browser
to run through a scenario, verify data elements on the screen, etc. This category of tests is usually very effective
at reproducing scenarios related to an issue or possible defect, and can be very helpful in troubleshooting and issue diagnosis.

## Testing Modules

To test the functionality provided by a given CAS module, execute the following steps:

- For the Apache Tomcat, undertow or Jetty web applications, add the module reference to the `webapp-dependencies.gradle` build script of web application you intend to run:

```gradle
implementation project(":support:cas-server-support-modulename")
```

Alternatively, pass the required modules automatically: 

```bash
bc ldap,x509
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

| System     | Badge                                                                                                                                                                                                                        |
|------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Codacy     | [![Codacy Badge](https://app.codacy.com/project/badge/Coverage/29973e19266547dab7ab73f1a511c826)](https://app.codacy.com/gh/apereo/cas/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_coverage) |
| SonarCloud | [![Coverage](https://sonarcloud.io/api/project_badges/measure?project=org.apereo.cas%3Acas-server&metric=coverage)](https://sonarcloud.io/dashboard?id=org.apereo.cas%3Acas-server)                                          |
| CodeCov    | [![codecov](https://codecov.io/gh/apereo/cas/branch/master/graph/badge.svg)](https://codecov.io/gh/apereo/cas)                                                                                                               |

Quality metrics are collected and reported by the following platforms:

| System                     | Badge                                                                                                                                                                                                                  |
|----------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Codacy                     | [![Codacy Badge](https://app.codacy.com/project/badge/Grade/29973e19266547dab7ab73f1a511c826)](https://app.codacy.com/gh/apereo/cas/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade) |
| SonarCloud Quality Gate    | [![Sonarqube Quality](https://sonarcloud.io/api/project_badges/measure?project=org.apereo.cas%3Acas-server&metric=alert_status)](https://sonarcloud.io/dashboard?id=org.apereo.cas%3Acas-server)                       |
| SonarCloud Maintainability | [![Sonarqube Quality](https://sonarcloud.io/api/project_badges/measure?project=org.apereo.cas%3Acas-server&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=org.apereo.cas%3Acas-server)                       |

## Browser & Functional Testing

Automated browser testing is done via the [Puppeteer framework](https://pptr.dev/). Puppeteer is a Node library which provides a high-level 
API to control Chrome or Chromium over the DevTools Protocol and runs headless by default.

Functional tests start by generating a plain CAS overlay as a baseline that is able to run under HTTPS using a pre-generated keystore.
This overlay is supplied the test scenario configuration that explains the required modules, properties, etc to use when CAS is deployed
inside an embedded Apache Tomcat container. Once running, the Puppeteer script is executed by Node for the given test scenario to verify
specific functionality such as successful logins, generation of tickets, etc.
   
To see the full list of all Puppeteer test scenarios, you may run:

```bash
ls ./ci/tests/puppeteer/scenarios
```

All functional and browser test scenarios are executed by the [continuous integration system](Test-Process.html#continuous-integration). 
  
To install Puppeteer once:

```bash
npm i -g puppeteer
```

<div class="alert alert-info">:information_source: <strong>Usage</strong><p>
The above step is not strictly necessary. CAS will automatically attempt to install Puppeteer when necessary when you run test scenarios.</p></div>

To help simplify the testing process, you may use the following bash function in your `~/.profile`:

```bash
function pupcas() {
  cd /path/to/cas
  scenario=$1
  shift 1
  ./ci/tests/puppeteer/run.sh --scenario ./ci/tests/puppeteer/scenarios/"$scenario" $@
}
```

...which can later be invoked as:

```bash
pupcas <scenario-name>
```
                                 
To see the list of available test scenarios:

```bash
./gradlew --build-cache --configure-on-demand --no-daemon -q puppeteerScenarios
```

Remote debugging may be available on port `5000`. To successfully run tests, 
you also need to make sure [jq](https://stedolan.github.io/jq/) is installed.
   
The following command-line options are supported for test execution:

| Flag                                      | Description                                                                              |
|-------------------------------------------|------------------------------------------------------------------------------------------|
| `--scenario`                              | The scenario name, typically modeled after the folder name that contains the test        |
| `--install-puppeteer`, `--install`, `--i` | Install or update Puppeteer node modules.                                                |
| `--debug`, `--d`                          | Launch the CAS web application with remote debugging enabled.                            |
| `--debug-port`, `--port`                  | Specify the remote debugging port, typically `5000`.                                     |
| `--debug-suspend`, `--suspend`, `--s`     | Suspend the CAS web application on startup until a debugger session connects.            |
| `--rebuild`, `--build`                    | Rebuild the CAS web application, and disregard previously-built WAR artifacts.           |
| `--dry-run`, `--y`                        | Launch the CAS web application configured in the test without actually running the test. |
| `--headless`, `--h`                       | Launch the test scenario with a headless browser.                                        |
| `--rerun`, `--resume`, `--r`              | Launch and assume CAS is already running from a previous attempt.                        |
| `--hbo`                                   | A combination of `--headless` and `--build` and Gradle's `--offline` flag.               |
| `--hbod`                                  | A combination of `--hbo` and Gradle's `--offline` flag.                                  |
| `--bo`                                    | A combination of `--build` and Gradle's `--offline` flag.                                |
| `--hr`                                    | A combination of `--headless` and `--resume`. Disables the linter automatically.         |
| `--ho`                                    | A combination of `--headless` and Gradle's `--offline` flag.                             |
| `--hd`                                    | A combination of `--headless` and `--debug`.                                             |
| `--hb`                                    | A combination of `--headless` and `--build`.                                             |
| `--body`, `--bogy`, `--boyd`              | A combination of `--build`, `--debug`, `--dry-run` and Gradle's `--offline` flag.        |
| `--boy`                                   | A combination of `--build`, `--dry-run` and Gradle's `--offline` flag.                   |
| `--io`, `--initonly`                      | Initialize the execution of the test scenario, but do not run it.                        |
| `--native`, `--graalvm`, `--nb`           | Build the test scenario and produce a native-image as the final build artifact.          |
| `--nr`, `--native-run`                    | Run the test scenario as a native-image. Requires a native build via `--nb`.             |
| `--nbr`                                   | A combination of `--nb` and `--nr` flags.                                                |
| `--nl`, `--no-lint`, `--nol`              | Disable the linter that validates scenario scripts.                                      |
| `--hol`                                   | A combination of `--headless` and `--offline` flags with linter disabled.                |

For example, the `login-success` test scenario with a remote debugger may be run using: 

```bash
pupcas login-success --hbod
```
         
All other build related options and flags (i.e. `--info`) may be passed directly to the script. 

...and once the scenario is still running, you can rerun it (in a new terminal window) using:

```bash
pupcas login-success --hr
```

### Test Scenario Anatomy

Each test scenario is composed of the following files:

- `script.js`: The main driver of the test that executes the test, via launching a headless browser via Puppeteer. The basic outline of the test script may be:

```js
const puppeteer = require('puppeteer');
const cas = require('../../cas.js');
const assert = require("assert");

(async () => {
    const browser = await cas.newBrowser(cas.browserOptions());
    const page = await cas.newPage(browser);
    
    // Do stuff and check/assert behavior...
    
    await browser.close();
})();
```

- `script.json`: The *optional* test configuration in JSON that includes necessary CAS modules, properties, and other specifics. 
        
A basic modest outline of the test configuration may be:

```json
{
  "dependencies": "module1,module2,module3,...",
  "conditions": {
    "docker": "true"
  },
  "environmentVariables": [
    "NAME=VALUE"
  ],
  "requirements": {
    "graalvm": {
      "build": false
    },
    "docker": {
      "build": false
    }
  },
  "properties": [
    "--cas.server.something=something"
  ],
  "healthcheck": {
    "urls": ["https://localhost:8443/cas/actuator/health"]
  },
  "jvmArgs": "...",
  "server": "tomcat|jetty|undertow|...",
  "SPRING_APPLICATION_JSON": {},
  "bootstrapScript": "${PWD}/ci/tests/puppeteer/scenarios/${SCENARIO}/bootstrap.sh",
  "initScript": "${PWD}/ci/tests/puppeteer/scenarios/${SCENARIO}/init.sh",
  "readyScript": "${PWD}/ci/tests/puppeteer/scenarios/${SCENARIO}/ready.sh",
  "exitScript": "${PWD}/ci/tests/puppeteer/scenarios/${SCENARIO}/exit.sh",
  "instances": 2,
  "instance1": {
    "properties": [ ]
  },
  "instance2": {
    "properties": [ ]
  },
  "variations": [
    {
      "properties": [
      ]
    }
  ]
}
```
  
The only required bit in the test JSON configuration might be the `dependencies` attribute.

### MacOS Firewall Popup
                      
To allow the firewall to accept incoming network connections for Chromium on MacOS, 
you may apply the following command:

```bash
chromium="/path/to/cas/ci/tests/puppeteer/node_modules/puppeteer/.local-chromium"
sudo codesign --force --deep --sign - "${chromium}/mac-*/chrome-mac/Chromium.app"
```

## Continuous Integration

Unit and integration tests are automatically executed by the CAS CI system, [GitHub Actions](https://github.com/apereo/cas/actions).
