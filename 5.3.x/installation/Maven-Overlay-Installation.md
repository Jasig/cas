---
layout: default
title: CAS - Overlay Installation
---

# WAR Overlay Installation

CAS installation is a fundamentally source-oriented process, and we recommend a
WAR overlay (1) project to organize
customizations such as component configuration and UI design.
The output of a WAR overlay build is a `cas.war` file that can be deployed to a servlet container like
[Apache Tomcat](Configuring-Servlet-Container.html).

## Requirements

[See this guide](../planning/Installation-Requirements.html) to learn more.

## What is a WAR Overlay?

Overlays are a strategy to combat repetitive code and/or resources. Rather than downloading the CAS codebase and building from source,
overlays allow you to download a pre-built vanilla CAS web application server provided by the project itself and override/insert specific behavior into it.
At build time, the Maven/Gradle installation process will attempt to download the provided binary artifact first. Then the tool will locate your configuration files and settings made available inside the same project directory and will merge those into the downloaded artifact in order to produce
one wholesome archive (i.e. `cas.war`) . Overridden artifacts may include resources, java classes, images, CSS and javascript files. In order for the merge
process to successfully execute, the location and names of the overridden artifacts locally must **EXACTLY** match that of those provided by the project
inside the originally downloaded archive.

It goes without saying that while up-front ramp-up time could be slightly complicated, there are significant advantages to this approach:

1. There is no need to download/build from the source.
2. Upgrades are tremendously easier in most cases by simply adjusting the build script to download the newer CAS release.
3. Rather than hosting the entire software source code, as the deployer you **ONLY** keep your own local customizations which makes change tracking much easier.
4. Tracking changes inside a source control repository is very lightweight, again simply because only relevant changes (and not the entire software) is managed.


### Managing Overlays

Every aspect of CAS can be controlled by
adding, removing, or modifying files in the overlay; it's also possible and indeed common to customize the behavior of
CAS by adding third-party components that implement CAS APIs as Java source files or dependency references.

The process of working with an overlay, whether Maven or Gradle, can be summarized in the following steps:

- Start with and build the provided basic vanilla build/deployment.
- Identify the artifacts from the produced build that need changes. These artifacts are generally produced by the build in the `target` or `build` directory for Maven or Gradle, respectively.
- Copy the identified artifacts from the identified above directories over to the `src` directory.
1. Create the `src` directory and all of its children, if they don't already exist.
2. Copied paths and file names **MUST EXACTLY MATCH** their build counterparts, or the change won't take effect. See the table below to understand how to map folders and files from the build to `src`.
- After changes, rebuild and repeat the process as many times as possible.
- Double check your changes inside the built binary artifact to make sure the overlay process is working.

<div class="alert alert-warning"><strong>Be Exact</strong><p>Do NOT copy everything produced by the build. Attempt to keep changes and customizations to a minimum and only grab what you actually need. Make sure the deployment environment is kept clean and precise, or you incur the risk of terrible upgrade issues and painful headaches.</p></div>

## CAS WAR Overlays

CAS WAR overlay projects are provided for reference and study.

<div class="alert alert-info"><strong>Review Branch!</strong><p>The below repositories point you towards their <code>master</code> branch.
You should always make sure the branch you are on matches the version of CAS you wish to configure and deploy. The <code>master</code>
branch typically points to the latest stable release of the CAS server. Check the build configuration and if inappropriate,
use <code>git branch -a</code> to see available branches, and then <code>git checkout [branch-name]</code> to switch if necessary.</p></div>

| Project                                                               | Build Directory                               | Source Directory
|-----------------------------------------------------------------------|-----------------------------------------------|-----------------------
| [CAS Maven WAR Overlay](https://github.com/apereo/cas-overlay-template)   | `target/cas.war!WEB-INF/classes/`     | `src/main/resources`
| [CAS Gradle WAR Overlay](https://github.com/apereo/cas-gradle-overlay-template) | `cas/build/libs/cas.war!WEB-INF/classes/`     | `src/main/resources`

To construct the overlay project, you need to copy directories and files *that you need to customize* in the build directory over to the source directory.

The Gradle overlay also provides additional tasks to explode the binary artifact first before re-assembling it again.
You may need to do that step manually yourself to learn what files/directories need to be copied over to the source directory.

Note: Do **NOT** ever make changes in the above-noted build directory. The changesets will be cleaned out and 
set back to defaults every time you do a build. Put overlaid components inside the source directory
and/or other instructed locations to avoid surprises.

## CAS Configuration Server Overlay

See this [Maven WAR overlay](https://github.com/apereo/cas-configserver-overlay) for more details.

To learn more about the configuration server, please [review this guide](Configuration-Server-Management.html).

## Dockerized Deployment

See [this guide](Docker-Installation.html) for more info.

## Servlet Container

CAS can be deployed to a number of servlet containers. See [this guide](Configuring-Servlet-Container.html) for more info.

## Custom and Third-Party Source

It is common to customize or extend the functionality of CAS by developing Java components that implement CAS APIs or
to include third-party source by Maven dependency references. Including third-party source is trivial; simply include
the relevant dependency in the overlay `pom.xml` or `build.gradle` file. In order to include custom Java source, it should be included under a `src/main/java` directory in the overlay project source tree.

    ├── src
    │   ├── main
    │   │   ├── java
    │   │   │   └── edu
    │   │   │       └── sso
    │   │   │           └── middleware
    │   │   │               └── cas
    │   │   │                   ├── audit
    │   │   │                   │   ├── CompactSlf4jAuditTrailManager.java
    │   │   │                   ├── authentication
    │   │   │                   │   └── principal
    │   │   │                   │       └── UsernamePasswordCredentialsToPrincipalResolver.java
    │   │   │                   ├── services
    │   │   │                   │   └── JsonServiceRegistryDao.java
    │   │   │                   ├── util
    │   │   │                   │   └── X509Helper.java
    │   │   │                   └── web
    │   │   │                       ├── HelpController.java
    │   │   │                       ├── flow
    │   │   │                       │   ├── AbstractForgottenCredentialAction.java
    │   │   │                       └── util
    │   │   │                           ├── ProtocolParameterAuthority.java

### Maven Caveat

Also, note that for any custom Java component to compile and be included in the final `cas.war` file, the `pom.xml`
in the Maven overlay must include a reference to the Maven Java compiler so classes can compile.

Here is a *sample* Maven build configuration:


```xml
...

<build>
    <plugins>
...
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.1</version>
            <configuration>
                <source>${java.source.version}</source>
                <target>${java.target.version}</target>
            </configuration>
        </plugin>
...
    </plugins>
    <finalName>cas</finalName>
</build>
```

## Dependency Management

Each release of CAS provides a curated list of dependencies it supports. In practice, you do not need to provide a version for any of
these dependencies in your build configuration as the CAS distribution is managing that for you. When you upgrade CAS itself, these dependencies will be upgraded as well in a consistent way.

The curated list of dependencies contains a refined list of third party libraries. The list is available as a standard Bills of Materials (BOM).

To configure your project to inherit from the BOM, simply set the parent:

### Maven

```xml
<parent>
    <groupId>org.apereo.cas</groupId>
    <artifactId>cas-server-support-bom</artifactId>
    <version>${cas.version}</version>
</parent>
```

Not everyone likes inheriting from the BOM.
You may have your own corporate standard parent that you need to use,
or you may just prefer to explicitly declare all your Maven configuration.

If you don’t want to use the `cas-server-support-bom`, you can still
keep the benefit of the dependency management (but not the plugin management)
by using a `scope=import` dependency:

```xml
<dependencyManagement>
     <dependencies>

        <!-- Override a dependency by including it BEFORE the BOM -->
        <dependency>
            <groupId>org.group</groupId>
            <artifactId>artifact-name</artifactId>
            <version>X.Y.Z</version>
        </dependency>

        <dependency>
            <groupId>org.apereo.cas</groupId>
            <artifactId>cas-server-support-bom</artifactId>
            <version>${cas.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### Gradle

To take advantage of the CAS BOM via Gradle, please [use this guide](https://plugins.gradle.org/plugin/io.spring.dependency-management)
and configure the Gradle build accordingly.

<sub>(1) [WAR Overlays](http://maven.apache.org/plugins/maven-war-plugin/overlays.html)</sub>
