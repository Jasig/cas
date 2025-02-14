---
layout: default
title: CAS - Release Process
---

# CAS 5.1.x Release Process

This page documents the steps that a release engineer should take for cutting a CAS server release. 

## Account Setup

- You will need to sign up for a [Sonatype account](http://central.sonatype.org/pages/ossrh-guide.html) and must ask 
to be authorized to publish releases to the `org.apereo` package. Once you have, you may be asked to have one of the
current project members *vouch* for you. 
- You will need to [generate your own PGP signatures](http://blog.sonatype.com/2010/01/how-to-generate-pgp-signatures-with-maven/) to sign the release artifacts prior to uploading them to a central repository.

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

- Checkout the CAS project: `git clone --single-branch --branch 5.1.x --depth 1 git@github.com:apereo/cas.git cas-server`
- Make sure you have the [latest version of JDK 8](http://www.oracle.com/technetwork/java/javase/downloads) installed via `java -version`. 

## Preparing the Release

Apply the following steps to prepare the release environment. There are a few variations to take into account depending on whether
a new release branch should be created. 

### Create Branch

<div class="alert alert-warning"><strong>Remember</strong><p>You should do this only for major or minor releases (i.e. <code>4.2.x</code>, <code>5.0.x</code>).
If there already exists a remote tracking branch for the version you are about to release, you should <code>git checkout</code> that branch, 
skip this step and move on to next section to build and release.</p></div>

#### Travis CI

- Change `.travis.yml` to *only* build the newly-created release branch.
- Change `travis/deploy-to-sonatype.sh` to point to the newly-created release branch.
- Change `travis/push-javadoc-to-gh-pages.sh` to point to the newly-created release branch.
 
Do not forget to commit all changes and push changes upstream, creatng a new remote branch to track the release.

### Build 

In the project's `gradle.properties`, change the project version to the release version. (i.e. `5.0.0-RC1`). Then build the project using the following command:

```bash
./gradlew gulpSetup
./gradlew clean assemble install -x test --parallel -x check
```

### Release

Release the project using the following commands:

```bash
./gradlew uploadArchives -DpublishReleases=true -DsonatypeUsername=<UID> -DsonatypePassword=<PASSWORD>
```

## Performing the Release

Follow the process for [deploying artifacts to Maven Central](https://wiki.jasig.org/display/JCH/Deploying+Maven+Artifacts) via Sonatype OSS repository.  

- Log into [https://oss.sonatype.org](https://oss.sonatype.org).
- Click on "Staged Repositories" on the left and find the CAS release artifacts at the bottom of the list.
- "Close" the repository via the toolbar button and provide a description. This step may take a few minutes. Follow the activity and make sure all goes well.
- "Release" the repository via the toolbar button and provide a description. The step is only enabled if the repository is successfully closed.

## Finalizing the Release

- Create a tag for the released version, commit the change and push the tag to the upstream repository. (i.e. `v5.0.0-RC1`).

If you did create a new release branch, you should also switch back to `master` and follow these steps:

- In the project's `gradle.properties`, change the project version to the *next* development version (i.e. `5.0.0-RC2-SNAPSHOT`). 
- Push your changes to the upstream repository. 

## Housekeeping

- Close [the milestone](https://github.com/apereo/cas/milestones) for this release.

- Find [the release](https://github.com/apereo/cas/releases) that is mapped to the released tag and update the description.

<div class="alert alert-info"><strong>Remember</strong><p>When updating the release description, try to be keep consistent and follow the same layout as previous releases.</p></div>

- Mark the release as pre-release, when releasing RC versions of the project. 
- Send an announcement message to @cas-announce, @cas-user and @cas-dev mailing lists, linking to the new release page.

## Update Overlays

Update the following overlay projects to point to the newly released CAS version.

- [CAS Webapp Maven Overlay](https://github.com/apereo/cas-overlay-template)
- [CAS Webapp Gradle Overlay](https://github.com/apereo/cas-gradle-overlay-template)
- [CAS Configuration Server Overlay](https://github.com/apereo/cas-configserver-overlay)
- [CAS Services Management WebApp Overlay](https://github.com/apereo/cas-services-management-overlay)
- [CAS Discovery Server Overlay](https://github.com/apereo/cas-discoveryserver-overlay)
- [CAS Spring Boot Admin Server Overlay](https://github.com/apereo/cas-bootadmin-overlay)

## Update Demos (Optional)

A number of CAS demos today run on Heroku and are tracked in dedicated branches inside the codebase. Take a pass and updated each, when relevant.

## Update Maintenance Policy

Update the [Maintenance Policy](Maintenance-Policy.html) to note the release schedule and EOL timeline. 
This task is only relevant when dealing with major or minor releases.

## Docker Image (Optional)

Release a new CAS [Docker image](https://github.com/apereo/cas-webapp-docker).
This task is only relevant when dealing with GA releases.
