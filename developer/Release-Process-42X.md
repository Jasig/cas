---
layout: default
title: CAS - Release Process
---

# CAS 4.2.x Release Process
This page documents the steps that a release engineer should take for cutting a CAS server release.

## Environment Review

- Set up your environment:
	- Load your SSH key and ensure this SSH key is also referenced in Github.
	- Adjust `$GRADLE_OPTS` to initialize the JVM heap size, if necessary.
	- Load your `~/.gradle/gradle.properties` file with the following:

```bash
signing.keyId=
signing.password=
signing.secretKeyRingFile=
sonatypeUsername=
sonatypePassword=
org.gradle.daemon=false
```

- Checkout the CAS project: `git clone git@github.com:Apereo/cas.git cas-server`

## Preparing the Release

- Create a branch for the release version, if necessary (i.e `4.2.x`).
- Switch to the release branch. 
- In the project's `gradle.properties`, change the project version to the release version (i.e. `4.2.0-RC1`)
- Build the project using the following command:

```bash
./gradlew clean build -x test --parallel -DskipCheckstyle=true -DskipFindbugs=true
```

- Release the project using the following commands:

```bash
./gradlew uploadArchives -DpublishReleases=true
```

- In the project's `gradle.properties`, change the project version to the *next* release version (i.e. `4.2.0-RC2`) 
- Create a tag for the released version and push the tag to the upstream Apereo repository. (i.e. `v4.2.0-RC1`).
- Switch to the `master` branch and in the project's `gradle.properties`, change the project version to the *next* development version (i.e. `4.3.0-SNAPSHOT`). Push your changes to the upstream Apereo repository. 

## Performing the Release

Follow the process for [deploying artifacts to Maven Central](https://wiki.jasig.org/display/JCH/Deploying+Maven+Artifacts) via Sonatype OSS repository.  

- Log into [https://oss.sonatype.org](https://oss.sonatype.org).
- Find the staged repository for CAS artifacts
- "Close" the repository.
- "Release" the repository.  Both c and d should be accompanied by email confirmation.

## Housekeeping

- Close [the milestone](https://github.com/Apereo/cas/milestones) for this release.
- Find [the release](https://github.com/Apereo/cas/releases) that is mapped to the released tag, update the description with the list of resolved/fixed issues and publish it as released. 
- Mark the release as pre-release, when releasing RC versions of the project. 
- Send an announcement message to @cas-announce, @cas-user and @cas-dev mailing lists. A template follows:

```bash
CAS Community,

CAS x.y.z is available for testing and evaluation. We encourage adopters to grab 
this release from Maven Central, integrate into your environment, and provide feedback.

Regards,
John Smith

```

## Update Maven Overlay

Update the following overlay projects to point to the newly released CAS version. This task is only relevant when dealing with GA releases.

- [CAS Webapp Maven Overlay](https://github.com/apereo/cas-overlay-template)
- [CAS Webapp Gradle Overlay](https://github.com/apereo/cas-gradle-overlay-template)
- [CAS Services Management WebApp Overlay](https://github.com/apereo/cas-services-management-overlay)


## Docker Image


Release a new CAS [Docker image](https://github.com/apereo/cas-webapp-docker).
This task is only relevant when dealing with GA releases.
