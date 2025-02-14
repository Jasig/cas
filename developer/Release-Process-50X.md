---
layout: default
title: CAS - Release Process
---

# CAS 5.0.x Release Process

This page documents the steps that a release engineer should take for cutting a CAS server release. 

## Account Setup

You will need to sign up for a [Sonatype account](http://central.sonatype.org/pages/ossrh-guide.html) and must ask 
to be authorized to publish releases to the `org.apereo` package. Once you have, you may be asked to have one of the
current project members *vouch* for you. 

## Environment Review

- Load your SSH key and ensure this SSH key is also referenced in Github.
- Adjust `$GRADLE_OPTS` to initialize the JVM heap size, if necessary.
- Load your `~/.gradle/gradle.properties` file with the following *as an example*:

```bash
signing.keyId=7A24P9QB
signing.password=P@$$w0rd
signing.secretKeyRingFile=/Users/example/.gnupg/secring.gpg
org.gradle.daemon=false
```

- Checkout the CAS project: `git --depth=10 clone git@github.com:apereo/cas.git cas-server`
- Make sure you have the [latest version of JDK 8](http://www.oracle.com/technetwork/java/javase/downloads) installed via `java -version`. 

## Preparing the Release

- If necessary, create an appropriate branch for the next release. Generally, you should do this only for major or minor releases. (i.e. `4.2.x`, `5.0.x`)
- In the project's `gradle.properties`, change the project version to the release version. (i.e. `5.0.0-RC1`)
- Build the project using the following command:

```bash
./gradlew clean assemble install -x test --parallel -x check
```

- Release the project using the following commands:

```bash
./gradlew uploadArchives -DpublishReleases=true -DsonatypeUsername=<UID> -DsonatypePassword=<PASSWORD>
```

## Performing the Release

Follow the process for [deploying artifacts to Maven Central](https://wiki.jasig.org/display/JCH/Deploying+Maven+Artifacts) via Sonatype OSS repository.  

- Log into [https://oss.sonatype.org](https://oss.sonatype.org).
- Find the staged repository for CAS artifacts
- "Close" the repository.
- "Release" the repository.

## Finalizing the Release

- Create a tag for the released version, commit the change and push the tag to the upstream repository. (i.e. `v5.0.0-RC1`).
- Switch to the release branch and in the project's `gradle.properties`, change the project version to the *next* development version (i.e. `5.0.0-RC2-SNAPSHOT`). 
- Push your changes to the upstream repository. 

## Housekeeping

- Close [the milestone](https://github.com/apereo/cas/milestones) for this release.
- Find [the release](https://github.com/apereo/cas/releases) that is mapped to the released tag, update the description with the list of resolved/fixed issues and publish it as released. 
- Mark the release as pre-release, when releasing RC versions of the project. 
- Send an announcement message to @cas-announce, @cas-user and @cas-dev mailing lists. A template follows:

```bash
CAS Community,

CAS x.y.z is available for testing and evaluation. We encourage adopters to grab 
this release from Maven Central, integrate into your environment, and provide feedback.

Regards,
John Smith
```

## Update Overlays

Update the following overlay projects to point to the newly released CAS version. 

- [CAS Webapp Maven Overlay](https://github.com/apereo/cas-overlay-template)
- [CAS Webapp Gradle Overlay](https://github.com/apereo/cas-gradle-overlay-template)
- [CAS Services Management WebApp Overlay](https://github.com/apereo/cas-services-management-overlay)


## Update Maintenance Policy

Update the [Maintenance Policy](Maintenance-Policy.html) to note the release schedule and EOL timeline. This task is only relevant when dealing with major or minor releases.

## Docker Image (Optional)

Release a new CAS [Docker image](https://github.com/apereo/cas-webapp-docker).
This task is only relevant when dealing with GA releases.
