---
layout: default
title: CAS - Release Process
---


# CAS 4.1.x Release Process
This page documents the steps that a release engineer should take for cutting a CAS server release.


## Environment Review

- Sanity test pending CAS release by testing protocol and major features.
- Ensure criteria for a release has been met and that there are no outstanding JIRA issues, etc.
- Ensure all outstanding changes are committed.
- Ensure all tests pass on CI 
- Set up your environment :
	- Load your SSH key on your local computer and ensure this SSH key is also referenced in Github
	- Ensure your `settings.xml` file has the Sonatype repository defined with the appropriate credentials:
	
{% highlight xml %}
<servers>
	<server>
	  <id>sonatype-nexus-snapshots</id>
	  <username>uid</username>
	  <password>psw</password>
	</server>
	<server>
	  <id>sonatype-nexus-staging</id>
	  <username>uid</username>
	  <password>psw</password>
	</server>
</servers>
{% endhighlight %}
	
- Checkout the CAS project :
{% highlight bash %}
mkdir casrelease
cd casrelease
git clone git@github.com:Apereo/cas.git
{% endhighlight %}

- Ensure conformity before release by executing the following goals from the project root:
{% highlight bash %}
mvn -o notice:check
mvn -o license:check
mvn -o checkstyle:check
mvn -o findbugs:findbugs
mvn -o javadoc:javadoc
{% endhighlight %}

If notice and license checks fail, the `notice:generate` and `license:format` goals [may be used](https://wiki.jasig.org/display/LIC/maven-notice-plugin) to help remedy the situation.
All other checks and reported issues need to be resolved manually. 

## Preparing the Release

Ensure you use JDK 7 by running the following command:
{% highlight bash %}
java -version
{% endhighlight %}

Prepare for release by running prepare goal of Maven Release Plugin, which prompts for version numbers:
{% highlight bash %}
export MAVEN_OPTS="-Xmx2048m -XX:MaxPermSize=128m"
mvn release:prepare
{% endhighlight %}

Alternatively, you may specify the release version number directly on the command line:
{% highlight bash %}
export MAVEN_OPTS="-Xmx2048m -XX:MaxPermSize=128m"
mvn -DreleaseVersion=x.y.z -DdevelopmentVersion=a.b.c release:prepare
{% endhighlight %}

## Performing the Release

{% highlight bash %}
mvn release:perform
{% endhighlight %}

Follow the process for [deploying artifacts to Maven Central](https://wiki.jasig.org/display/JCH/Deploying+Maven+Artifacts) via Sonatype OSS repository.  

- Log into [https://oss.sonatype.org](https://oss.sonatype.org).
- Find the staged repository for CAS artifacts created by the `mvn release:perform` goal.
- "Close" the repository.
- "Release" the repository.  Both c and d should be accompanied by email confirmation.

## Housekeeping

- Close [the milestone](https://github.com/Apereo/cas/milestones) for this release.
- Find [the release](https://github.com/Apereo/cas/releases) that is mapped to the released tag, update the description with the list of resolved/fixed issues and publish it as released. 

To generate the changelog and release notes, use the below steps:

1. Download and install [this tool](https://github.com/lalitkapoor/github-changes)
2. Generate a github access token [here](https://github.com/settings/tokens)
3. Execute the following command:

{% highlight bash %}
github-changes -o Apereo -r cas -b x.y.z -k <TOKEN> -a --use-commit-body
{% endhighlight %}

Note that `x.y.z` is the name of the branch that is released. The output will be saved in `ChangeLog.md` file. Comb
through the file, edit, format and paste the final content under the release tag. 


- Send an announcement message to @cas-announce, @cas-user and @cas-dev mailing lists. A template follows:

{% highlight bash %}
CAS Community,

CAS x.y.z is available for testing and evaluation. We encourage adopters to grab 
this release from Maven Central, integrate into your environment, and provide feedback.

Regards,
John Smith

{% endhighlight %}

## Post Release

### Documentation
- Documentation site is available under the `gh-changes` branch. Check it out.
- Copy the contents of the `development` directory to a new directory to match the new CAS release version (i.e `4.0.0`)
- Navigate to `Older-Version.md` page and include a link to the new directory that points to the new release.
- Modify the root `index.html` file of the `current` folder to point to the latest stable release such that `location.href = "../4.0.0/index.html";`
- Push the changes to the repository.

## Common Issues
If you're preparing and cutting the release, please be wary of the following possible issues:

### SSH Authentication: private key's passphrase prompt hangs
SSH authentication with git on Windows is unable to cache the private key's password regardless of any authorized SSH agent running in the background. The maven-release-plugin currently is unable to actually present the password prompt. 

To mitigate the problem, you will need to modify the release.properties file, and switch the authentication scheme from SSH to HTTPS. 

### "Directory is outside the repository" error
When the maven-release plugin attempts to add/commit/push back files to the repository from the project directory, you might receive the error that the directory is outside the repository! This has to do with the fact that the plugin and git consider path names to the project directory to be case-sensitive while Windows does not. In other words, git might consider the project repository directory to be available at "c:\project", while the current directory is pointed to "C:\project". 

To mitigate the problem, simply "CD" into the directory path of the working directory given by git, based on the console output. 

### "Dependencies cannot be found/resolved" error
Once you have prepped the release and start to execute `release:perform`, the maven-release plugin will attempt to checkout the prepped release from source first and run through the build to upload artifacts. You may at this stage encounter the error that artifact dependencies (that are based on the new tagged release) cannot be found. For example, if you have prepped the release V4-RC1 and are attempting to cut it,  you may receive the error that certain modules for V4-RC1 cannot be resolved during the build. 

To mitigate the issue, navigate to the `target\checkout` directory and run `mvn install` once to locally capture the newly tagged artifacts. You may alternatively, checkout the CAS project again, switch to the tag and perform `mvn -o install -Pnocheck`.

### Java heap space "Out of memory" errors
Depending on settings, the build might run out of Java heap space. You can tweak settings through `JAVA_OPTS` and `MAVEN_OPTS` and allow more via the `-Xmx` parameter. Usually, `2g` should suffice on a 64-bit Java VM:

`export MAVEN_OPTS="-Xmx2048m"`

### The build is sluggish

The current CAS build runs a number of checks and processes as it progresses forward in the release process. Disabling these checks will help speed up the build because, the maven-release plugin attempts to run all said checks for all modules prior to processing the active module during the build. In other words, if the build is attempting to process module C, it will run the checks for modules A and B...and then it moves on to C. Similarly, if the build is at module D, it will yet again run all checks for modules, A, B and C before it starts with D. 

You can disable some of these checks in the Maven's `settings.xml` file. Here are some of the steps you can through configuration ignore by setting them to *`true`*:

<div class="alert alert-danger"><strong>Caution!</strong><p>You must ensure that all above checks do actually pass, separately and independently, if you do decide to disable them in the build process.</p></div>

- Skip running tests (Tests still would have to be compiled): `<skipTests>true<skipTests>`
- Checkstyle checks: `<checkstyle.skip>true</checkstyle.skip>`
- License checks: `<license.skip>true</license.skip>`
- Notice file checks: `<notice.skip>true</notice.skip>`
- Dependency version checks: `<versions.skip>true</versions.skip>`
- FindBugs checks: `<findbugs.skip>true</findbugs.skip>`

The current Maven build contains a `nocheck` profile that encapsulates the above settings. The profile may be invoked via `-Pnocheck` parameter.
