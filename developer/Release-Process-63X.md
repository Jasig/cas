---
layout: default
title: CAS - Release Process
---

# CAS 6.3.x Release Process

This page documents the steps that a release engineer should take for cutting a CAS server release. 

## Sonatype Setup

You will need to sign up for a [Sonatype account](http://central.sonatype.org/pages/ossrh-guide.html) and must ask 
to be authorized to publish releases to the `org.apereo` package by creating a JIRA. Once you have, you may be asked to have one of the
current project members *vouch* for you. 

## GPG Setup

You will need to [generate your own PGP signatures](http://blog.sonatype.com/2010/01/how-to-generate-pgp-signatures-with-maven/) to sign the release artifacts prior to uploading them to a central repository. In order to create OpenPGP signatures, you will need to generate a key pair. You need to provide the build with your key information, which means three things:

- The public key ID (The last 8 symbols of the keyId. You can use `gpg -K` to get it
- The absolute path to the secret key ring file containing your private key. Since gpg 2.1, you need to export the keys with command:

```bash
gpg --keyring secring.gpg --export-secret-keys > ~/.gnupg/secring.gpg
```

- The passphrase used to protect your private key.

The above settings will need to be placed into the `~/.gradle/gradle.properties` file:

```properties
signing.keyId=7A24P9QB
signing.password=P@$$w0rd
signing.secretKeyRingFile=/Users/example/.gnupg/secring.gpg
```

Additional notes on how artifacts are signed using the Gradle signing plugin are [available here](https://docs.gradle.org/current/userguide/signing_plugin.html)

## Environment Setup

- Load your SSH key and ensure this SSH key is also referenced in GitHub.
- Adjust `$GRADLE_OPTS` to initialize the JVM heap size, if necessary.
- Load your `~/.gradle/gradle.properties` file with the following *as an example*:

```properties
org.gradle.daemon=false
org.gradle.parallel=false
```

- Checkout the CAS project: `git clone git@github.com:apereo/cas.git cas-server`
- Make sure you have the [latest version of JDK 11](http://www.oracle.com/technetwork/java/javase/downloads) installed via `java -version`. 

## Preparing the Release

Apply the following steps to prepare the release environment. There are a few variations to take into account depending on whether
a new release branch should be created. 

### Create Branch

```bash
# Replace $BRANCH with CAS version (i.e. 5.3.x)
git checkout -b $BRANCH
```

<div class="alert alert-warning"><strong>Remember</strong><p>You should do this only for major or minor releases (i.e. <code>4.2.x</code>, <code>5.0.x</code>).
If there already exists a remote tracking branch for the version you are about to release, you should <code>git checkout</code> that branch, 
skip this step and move on to next section to build and release.</p></div>

### GitHub Actions

<div class="alert alert-warning"><strong>Remember</strong><p>You should do this only for major or minor releases, when new branches are created.</p></div>
 
- Change `.github/workflows/cas-build.yml` to trigger and *only* build the newly-created release branch.
- Examine all CI shell scripts under the `ci` folder to make sure nothing points to `development` or `master`. This is particularly applicable to how CAS documentation is published to the `gh-pages` branch.
- Disable jobs in CI that report new dependency versions or update dependencies using Renovate, etc.
 
Do not forget to commit all changes and push changes upstream, creating a new remote branch to track the release.

## Performing the Release 

- In the project's `gradle.properties`, change the project version to the release version and remove the `-SNAPSHOT`. (i.e. `6.0.0-RC1`). 
- Then build and release the project using the following command:

```bash
./release.sh
```

Next:  

- Log into [https://oss.sonatype.org](https://oss.sonatype.org).
- Click on "Staged Repositories" on the left and find the CAS release artifacts at the bottom of the list.
- "Close" the repository via the toolbar button and provide a description. This step may take a few minutes. Follow the activity and make sure all goes well.
- "Release" the repository via the toolbar button and provide a description. The step is only enabled if the repository is successfully closed.

## Finalizing the Release

- Create a tag for the released version, commit the change and push the tag to the upstream repository. (i.e. `v5.0.0-RC1`).

You should also switch back to the main development branch (i.e. `master`) and follow these steps:

- In the project's `gradle.properties`, change the project version to the *next* development version (i.e. `5.0.0-SNAPSHOT`). 
- Push your changes to the upstream repository. 

## Housekeeping

- Close [the milestone](https://github.com/apereo/cas/milestones) for this release.
- Find [the release](https://github.com/apereo/cas/releases) that is mapped to the released tag and update the description.

<div class="alert alert-info"><strong>Remember</strong><p>When updating the release description, try to be keep consistent and follow the same layout as previous releases.</p></div>

- Mark the release as pre-release, when releasing RC versions of the project. 
- Send an announcement message to [@cas-announce, @cas-user and @cas-dev](/cas/Mailing-Lists.html) mailing lists, linking to the new release page.

## Update Overlays

Update the following overlay projects to point to the newly released CAS version. You may need to move the current `master` branch
over to a maintenance branch for each of the below overlay projects, specially if/when dealing with major/minor releases
and if the release process here had you create a new branch. 

- [CAS WAR Overlay](https://github.com/apereo/cas-overlay-template)

## Update Documentation

<div class="alert alert-warning"><strong>Remember</strong><p>You should do this only for major or minor releases, when new branches are created.</p></div>

- Configure docs to point `current` to the latest available version [here](https://github.com/apereo/cas/blob/gh-pages/current/index.html).
- Configure docs to include the new release in the list of [available versions](https://github.com/apereo/cas/blob/gh-pages/_layouts/default.html).
- [Update docs](https://github.com/apereo/cas/edit/gh-pages/Older-Versions.md/) and add the newly released version.
- Update the project's [`README.md` page](https://github.com/apereo/cas/blob/master/README.md) to list the new version, if necessary.
- Update [the build process](https://apereo.github.io/cas/developer/Build-Process.html) to include any needed information on how to build the new release.
- Update [the release notes](https://github.com/apereo/cas/tree/master/docs/cas-server-documentation/release_notes) and remove all previous entries.

## Update Maintenance Policy

Update the [Maintenance Policy](Maintenance-Policy.html) to note the release schedule and EOL timeline. 
This task is only relevant when dealing with major or minor releases.

## Update Demos

(Optional) A number of CAS demos today run on Heroku and are tracked in dedicated branches inside the codebase. Take a pass and update each, when relevant.
