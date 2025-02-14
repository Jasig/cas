---
layout: default
title: CAS - Build Process
category: Developer
---

# Build Process

This page documents the steps that a CAS developer/contributor should take for building a CAS server locally.

<div class="alert alert-warning"><strong>Usage Warning!</strong><p>
If you are about to deploy and configure CAS, you are in the <strong>WRONG PLACE</strong>! To deploy CAS locally, use the WAR Overlay method described in the project documentation for a specific CAS version. Cloning, downloading and building the CAS codebase from source is <strong>ONLY</strong> required if you wish to contribute to the development of the project.
</p></div>

## Source Checkout

The following shell commands may be used to grab the source from the repository:

```bash
git clone --recursive git@github.com:apereo/cas.git cas-server
```

Or a quicker clone:

```bash
git clone --recursive --depth=1 --single-branch --branch=master git@github.com:apereo/cas.git cas-server
# git fetch --unshallow
```

For a successful clone, you will need to have [set up SSH keys](https://help.github.com/articles/working-with-ssh-key-passphrases/) for your account on Github.
If that is not an option, you may clone the CAS repository under `https` via `https://github.com/apereo/cas.git`.

You may also need to update submodules linked to the CAS repository. Newer versions of Git will do this automatically, 
but older versions will require you to explicitly tell git to download the contents of submodules:

```bash
git submodule update --init --recursive
```

## Build

The following shell commands may be used to build the source:

```bash
cd cas-server
git checkout master
```

When done, you may build the codebase via the following command:

```bash
./gradlew build install --parallel -x test -x javadoc -x check --build-cache --configure-on-demand
```

The following commandline boolean flags are supported by the build and can be passed in form of system properties via `-D`:

| Flag                              | Description
|-----------------------------------+---------------------------------------------------------------------------+
| `enableRemoteDebugging`           | Allows for remote debugging via a pre-defined port (i.e. `5000`).
| `remoteDebuggingSuspend`          | Set to `true` to suspend JVM remote debugging until the debugger attaches to the running session.
| `enableIncremental`               | Enable Gradle's incremental compilation feature.
| `showStandardStreams`             | Let the build output logs that are sent to the standard streams. (i.e. console, etc)
| `skipCheckstyle`                  | Skip running Checkstyle checks.
| `skipSpotbugs`                    | Skip running Spotbugs checks.
| `skipVersionConflict`             | If a dependency conflict is found, use the latest version rather than failing the build.
| `skipNestedConfigMetadataGen`     | Skip generating configuration metadata for nested properties and generic collections.
| `skipSonarqube`                   | Ignore reporting results to Sonarqube.
| `skipErrorProneCompiler`          | Skip running the `error-prone` static-analysis compiler.
| `skipBootifulArtifact`            | Do not apply the Spring Boot plugin to bootify application artifacts.
| `ignoreJavadocFailures`           | Ignore javadoc failures and let the build resume.
| `ignoreFindbugsFailures`          | Ignore Findbugs failures and let the build resume.
| `ignoreTestFailures`              | Ignore test failures and let the build resume.
| `casModules`                      | Comma separated list of modules without the `cas-server-[support|core]` prefix.

- You can use `-x <task>` to entirely skip/ignore a phase in the build. (i.e. `-x test`, `-x check`).
- If you have no need to let Gradle resolve/update dependencies and new module versions for you, you can take advantage of the `--offline` flag when you build which tends to make the build go a lot faster.
- Using the Gradle daemon also is a big help. It should be enabled [by default](https://docs.gradle.org/current/userguide/gradle_daemon.html).
- Enabling [Gradle's build cache](https://docs.gradle.org/current/userguide/build_cache.html) via `--build-cache` can also significantly improve build times.

## Tasks

Available build tasks can be found using the command `./gradlew tasks`.

## IDE Setup

CAS development may be carried out using any modern IDE that supports Gradle. 

### IntelliJ IDEA

The following IDEA settings for Gradle may also be useful:

![image](https://user-images.githubusercontent.com/1205228/71612835-5ea5ed80-2bbc-11ea-8f49-9746dc2b3a70.png)

Additionally, you may need to customize the VM settings to ensure the development environment can load and index the codebase:

```bash
-server
-Xms1g
-Xmx8g
-Xss16m
-XX:NewRatio=3

-XX:ReservedCodeCacheSize=240m
-XX:+UseCompressedOops
-XX:SoftRefLRUPolicyMSPerMB=50

-XX:+UseParNewGC
-XX:ParallelGCThreads=4
-XX:+UseConcMarkSweepGC
-XX:ConcGCThreads=4

-XX:+CMSClassUnloadingEnabled
-XX:+CMSParallelRemarkEnabled
-XX:CMSInitiatingOccupancyFraction=65
-XX:+CMSScavengeBeforeRemark
-XX:+UseCMSInitiatingOccupancyOnly

-XX:MaxTenuringThreshold=1
-XX:SurvivorRatio=8
-XX:+UseCodeCacheFlushing
-XX:+AggressiveOpts
-XX:-TraceClassUnloading
-XX:+AlwaysPreTouch
-XX:+TieredCompilation

-Djava.net.preferIPv4Stack=true
-Dsun.io.useCanonCaches=false
-Djsse.enableSNIExtension=true
-ea
-Xverify:none
```

#### Plugins

The following plugins may prove useful during development:

- [Checkstyle](https://plugins.jetbrains.com/plugin/1065-checkstyle-idea)
- [FindBugs](https://plugins.jetbrains.com/plugin/3847-findbugs-idea)
- [Lombok](https://github.com/mplushnikov/lombok-intellij-plugin)

Once you have installed the Lombok plugin, you will also need to ensure Annotation Processing is turned on. You may need to restart IDEA in order for changes to take full effect.

![image](https://user-images.githubusercontent.com/1205228/35231112-287f625a-ffad-11e7-8c1a-af23ff33918d.png)

Note that the CAS-provided Checkstyle rules can be imported into idea to automate a number of 
formatting rules specially related to package imports and layouts. Once imported, the rules
should look something like the below screenshot:

![image](https://user-images.githubusercontent.com/1205228/42846621-b99539fc-8a2e-11e8-8128-9344bda7224d.png)

#### Running CAS

It is possible to run the CAS web application directly from IDEA by 
creating a *Run Configuration* that roughly matches the following screenshot:

![image](https://user-images.githubusercontent.com/1205228/41805461-9ea25b76-765f-11e8-9a36-fa82d286cf09.png)

This setup allows the developer to run the CAS web 
application via an [embedded servlet container](Build-Process.html#embedded-containers).

### Eclipse

For Eclipse, execute the following commands:

```bash
cd cas-server
./gradlew eclipse
```

Then, import the project into eclipse using "General\Existing Projects into Workspace" 
and choose "Add Gradle Nature" from the "Configure" context menu of the project.

<div class="alert alert-warning"><strong>YMMV</strong><p>We have had a less than ideal experience with Eclipse and its support for Gradle-based 
projects. While time changes everything and docs grow old, it is likely that you may experience issues with how Eclipse manages to 
resolve Gradle dependencies and build the project. In the end, you're welcome to use what works best for you as the ultimate goal 
is to find the appropriate tooling to build and contribute to CAS.</p></div>

## Testing Modules

Please [see this page](Test-Process.html) to learn more about the testing process and guidelines.

## Embedded Containers

The CAS project comes with a number of built-in modules that are pre-configured with embedded servlet containers such as 
Apache Tomcat, Jetty, etc for the server web application, the management web application and others. These modules are found in the `webapp` folder of the CAS project.

### Configure SSL

The `thekeystore` file must include the SSL private/public keys that are issued for your CAS server domain. You 
will need to use the `keytool` command of the JDK to create the keystore and the certificate. 

The following commands may serve as an example:

```bash
keytool -genkey -alias cas -keyalg RSA -validity 999 \
    -keystore /etc/cas/thekeystore -ext san=dns:$REPLACE_WITH_FULL_MACHINE_NAME
```

Note that the validity parameter allows you to specify, in the number of days, how long the certificate should be 
valid for. The longer the time period, the less likely you are to need to recreate it. To recreate it, you'd need 
to delete the old one and then follow these instructions again. You may also need to provide 
the *Subject Alternative Name* field, which can be done with `keytool` via `-ext san=dns:$REPLACE_WITH_FULL_MACHINE_NAME`.

The response will look something like this:

```bash
Enter keystore password: changeit
Re-enter new password: changeit
What is your first and last name?
  [Unknown]:  $REPLACE_WITH_FULL_MACHINE_NAME (i.e. mymachine.domain.edu)
What is the name of your organizational unit?
  [Unknown]:  Test
What is the name of your organization?
  [Unknown]:  Test
What is the name of your City or Locality?
  [Unknown]:  Test
What is the name of your State or Province?
  [Unknown]:  Test
What is the two-letter country code for this unit?
  [Unknown]:  US
Is CN=$FULL_MACHINE_NAME, OU=Test, O=Test, L=Test, ST=Test, C=US correct?
  [no]:  yes
```

In your `/etc/hosts` file (on Windows: `C:\Windows\System32\Drivers\etc\hosts`), you may also need to add the following entry:

```bash
127.0.0.1 mymachine.domain.edu
```

The certificate exported out of your keystore needs to also be imported into the Java platform's global keystore:

```bash
# Export the certificate into a file
keytool -export -file /etc/cas/config/cas.crt -keystore /etc/cas/thekeystore -alias cas

# Import the certificate into the global keystore
sudo keytool -import -file /etc/cas/config/cas.crt -alias cas -keystore $JAVA_HOME/lib/security/cacerts
```

...where `JAVA_HOME` is where you have the JDK installed (i.e `/Library/Java/JavaVirtualMachines/jdk[version].jdk/Contents/Home`).

On Windows, Administration right should be granted to the console instead 
of `sudo`, and `$JAVA_HOME/lib/security/cacerts` should be changed to `"%JAVA_HOME%/lib/security/cacerts"` instead.

### Deploy

Execute the following command:

```bash
cd webapp/cas-server-webapp-tomcat

../../gradlew build bootRun --parallel --offline --configure-on-demand --build-cache --stacktrace
```

The response will look something like this:

```bash
...
2017-05-26 19:10:46,470 INFO [org.apereo.cas.web.CasWebApplication] - <Started CasWebApplication in 21.893 seconds (JVM running for 36.888)>
...
```

By default CAS will be available at `https://mymachine.domain.edu:8443/cas`

### Remote Debugging

The embedded container instance is pre-configured to listen to debugger requests on port `5000` provided you 
specify the `enableRemoteDebugging` parameter. For external container 
deployments, [such as Apache Tomcat](https://wiki.apache.org/tomcat/FAQ), the following example 
shows what needs configuring in the `bin/startup.sh|bat` file:

```bash
export JPDA_ADDRESS=5000
export JPDA_TRANSPORT=dt_socket
bin/catalina.sh jpda start
```

When you're done, create a remote debugger configuration in your IDE that connects to this port and you will be able to step into the code.

![image](https://cloud.githubusercontent.com/assets/1205228/26517058/d09a8288-4245-11e7-962e-004bfe174a0a.png)

## Manual Submodule Testing

Please [see this page](Test-Process.html) to learn more about the testing process and guidelines.

## Sample Build Aliases

Below are some examples of convenient build aliases for quickly running a local cas server from the project or 
installing dependencies from the project for use in the cas-overlay.

```bash
# Adjust the cas alias to the location of cas project folder
alias cas='cd ~/Workspace/cas'

# test cas directly from project rather than using the CAS overlay
alias bc='clear; cas; cd webapp/cas-server-webapp-tomcat ; \
    ../../gradlew build bootRun --configure-on-demand --build-cache --parallel \
    -x test -x javadoc -x check -DremoteDebuggingSuspend=false \
    -DenableRemoteDebugging=true --stacktrace \
    -DskipNestedConfigMetadataGen=true'

# Install jars for use with a CAS overlay project
alias bci='clear; cas; \
    ./gradlew clean build install --configure-on-demand \
    --build-cache --parallel \
    -x test -x javadoc -x check --stacktrace \
    -DskipNestedConfigMetadataGen=true \
    -DskipBootifulArtifact=true'
```
