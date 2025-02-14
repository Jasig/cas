---
layout: default
title: CAS - Vulnerability Response
---

# Security Vulnerability Response

Members involved in [CAS application security](/cas/Mailing-Lists.html):

- Proactively work to improve the security of CAS focusing on the Apereo CAS server, the protocols and various CAS clients.
- Respond to potential vulnerabilities and create, maintain, and execute on vulnerability triage and notification policy, fielding handoffs from the Apereo security and otherwise.
- Issue vulnerability reports and work to coordinate workarounds and fix responses to security concerns that arise.
- Produce artifacts that help potential CAS adopters to evaluate the security of CAS both as open source product and as they intend to implement the product. This includes threat modeling, data flow diagrams, etc.
- Create and maintain recommendations on good practices for CAS implementation around hardening, configuration, failing safe, security by default, etc.

To review the old archives of the application security working group, [please see this guide](https://wiki.jasig.org/display/CAS/CAS+AppSec+Working+Group).

## Context & Definition

What is a security vulnerability? how and when discovered defects may be tagged as a vulnerability?

A security vulnerability is almost always a software defect and typically is a direct result of a CAS deployment behaving in unexpected ways
despite correct system configuration and application state. Correctness and intention is typically judged against available CAS documentation or a project/community member
acting as a subject-matter expert who may be able to clarify intended, expected behavior. Security bugs and vulnerabilities that present themselves as a result of CAS misconfiguration, misuse
or misunderstanding of how features and use cases work do not qualify. Likewise, scenarios that stem from *plausible deniability* (i.e. "I had no idea X works this way")
generally do not qualify. While features and enhancements can always be reviewed and debated to remove or improve a degree of flexibility, and project documentation can always be further improved to clarify intended behavior, a lack of understanding or
intentional misuse are almost always seen as a normal defect or opportunity for improvement. You are more than welcome to contribute.

Furthermore, in an ecosystem that involves many moving and variable parts, the CAS project generally tends to only focus on the CAS piece
and does not make a serious claim or attempt to secure (or even at times explain or document) the behavior of the systems present in its deployment environment particularly when such systems are not owned, managed or controlled by CAS and are seen as completely external. For example, if you
are managing a clustered CAS deployment backed by Hazelcast Enterprise that sits in front of a proxy, behind a load balancer, connecting to Active Directory and is deployed via Docker and Kubernetes,
CAS security only then goes as far as CAS itself. You are, *independently and separately*, responsible for managing and securing all other external components that live in this ecosystem.
Your proxy might allow untrusted data to pass through or your LDAP directory could support insecure connections, or you may intentionally configure CAS to accept authentication
requests from any application on this planet and beyond. None of these constitute a CAS security vulnerability.

If you find that aspects of the CAS documentation can be improved to clarify and explain a specific situation better, you are more than welcome to contribute.

## Response Model

- Security issues are privately reported to CAS [via the appropriate mailing list](/cas/Mailing-Lists.html).
- Project members acknowledge the issue and internally and privately work with the reporters to ascertain the merits of the issue.
- A fix is planned, produced and made available privately as a *direct commit* to the codebase.
- Project members or the original reporter **SHOULD** verify and confirm that the produced patch does indeed fix the issue.
- A security or patch release is published for public consumption.

<div class="alert alert-warning">:warning: <strong>List Etiquette</strong><p>CAS asks that you <strong>DO NOT</strong> 
create publicly viewable issues or posts to address the problem. There is no good sense in creating panic and chaos. All community 
members <strong>SHOULD</strong> ignore all such public announcements and reports.</p></div>

## Dependency Upgrades

Given the amount of effort involved in planning and releasing a security patch and disclosing the issue, communicating descriptions, intent,
attack window and fixes, etc it is generally best to keep security releases reserved for issues that **actually can be reproduced** based on a
concrete use case or those that truly and in practice affect the inner workings of the CAS software in a real way. If you could have such a
use case at some point, please be sure to **supply details to reproduce issues**. Supposed *vulnerabilities* that are picked up and
reported by security scanners, static code analyzers and such affecting a third-party library used by CAS generally do not qualify, unless
there is solid evidence **provided by the reporter** to indicate a real, practical issue affecting CAS daily ops. Such warnings often appear
superficial in the context of a CAS deployment. Usually, the best course of action would be to make the upgrades either locally to the
installation script or switch to a more recent CAS version that might remove such warnings.

CAS generally tends to upgrade dependencies for [feature (minor) and major releases](Release-Policy.html), and does so rather frequently. Dependency
versions are not typically upgraded for patch and security fixes unless and until, as stated above, there is solid evidence to demonstrate and require
the need and the effort for an upgrade. If you have tools, audit reports and processes in place that scan your CAS deployment and report back libraries
and dependencies with known CVEs and your policies require a build without such CVEs, we recommend that you modify
your CAS build to substitute, remove, upgrade or replace affected libraries and remove the warnings as you see best. Please note that doing so
may jeopardize the stability of your CAS build and deployment and most likely will make your upgrades quite difficult
as you might run into various dependency and library conflicts with newer versions.

## Security Bounty

Apereo CAS project does *not* provide a bounty or reward for discovering and reporting security issues. Security reports are received
on an as-is basis without any warranties, guarantees, or promises of any kind.

## Report Format

When you are preparing to communicate a security issue to the appropriate channel privately, please make sure your
report contains enough diagnostics data to expedite reviews and feedback:

- Indicate the *exact CAS version numbers* that exhibit the seemingly-faulty behavior.
- Describe your deployment/development environment in sufficient relevant detail.
- Include error logs, debug logs, screenshots and other useful snippets of your configuration.
- Include steps to explain how the issue might be reproduced.
- Include an overlay project that can duplicate the issue in practice.
- If possible, prepare and share unit/integration tests to recreate the issue.

<div class="alert alert-warning">:warning: <strong>Remember</strong><p>Make sure your reports present
automated, isolated and reproducible tests that demonstrate the security issue. This is a <b>MUST</b>. Anecdotal descriptive
evidence that asks for manual verification steps, i.e. <i>"go here, click this, type that, watch this, run that"</i>, etc are often
less than ideal and make it very difficult and sluggish for the project to justify time and resource to chase after fairy tales. If you are 
unable to reproduce the issue in an automated and isolated way, chances are the issue is either environment specific or a PEBCAK.</p></div>

Before posting a possible security issue, please make sure the affected CAS release line
is still [under maintenance](/cas/developer/Maintenance-Policy.html). Releases that are
considered EOL will not receive further updates and/or attention from designated project members.

## Time to Respond

If you have posted a question or reported a possible vulnerability to the appropriate channels and mailing lists and have yet to receive a response,
it is likely that your submission matches one of the categories listed below:

- Activity on the mailing lists and all other support channels is entirely voluntary. Contractually, there exists no sense of urgency in the response model. When in doubt, please consult the project license to learn more.
- The report description lacks fundamental details about the problem or fails to adequately follow the recommended format to justify spending time and/or resources.
- The report describes a vulnerable CAS deployment in the wild owned by a different organization, company or entity. Instead of reporting issues to the appropriate system owners, the reporter intends to report this vulnerable deployment to the Apereo CAS project.
- The reported vulnerability is the direct result of system misconfiguration or misuse that is carried out by the CAS operator.

If you would rather not wait for project volunteers to find the time to respond to security issues and reports, please consider negotiating a professional support agreement with Apereo commercial [service/support providers](/cas/Support.html) particularly if your organization's livelihood and reputation depend on a successful and secure Apereo CAS deployment.

## Time to Fix

As stated above, remember that activity on the mailing lists and all other support channels is entirely voluntary. There is no official
meaning or sense of urgency built into the response model and as such, fixes to potential security issues are 100% dependent
on individuals' availability and willingness. We strongly recommend that you study the project's license for more information
on this matter. If you are interested in contractual obligations, SLAs and a response model based on calculated levels of
urgency, please consider negotiating a professional support agreement with Apereo commercial [service/support providers](/cas/Support.html).

## Security Fixes

The CAS application security group encourages all reporters to not only include instructions and steps to reproduce and verify a reported issue, but
to also include and propose fixes in form of patch files that are shared privately with the group in the same issue report. Most, if not all, security
fixes that are applied to the CAS codebase, regardless of contribution type (i.e. direct developer commit or outsider contribution), are almost always
made privately. The details of the fix and application of the patch are kept privately, and are not exposed in the public
commit log, until due time. Security fixes and contributions in the form of publicly-viewable pull requests are *automatically closed*
with a cautious warning to the contributor to follow due process to get a patch fix applied and verified correctly.

## CVEs

The CAS project does not request or provide CVEs when handling security issues. This is a task that must entirely be handled by you,
should you need to obtain a CVE in order to justify an unplanned upgrade, etc. The CAS project, generally speaking, moves much quicker
to announce and disclose security issues than any CVE procurement process; we simply cannot delay security notifications and announcements
for as long as it could take to receive a CVE.

If you wish to volunteer to become a point person to get the CVE creation process going, by all means, speak up.

## Community Notification

Once the security release is made available, the following procedure may be observed:

- Community members [are notified](/cas/Mailing-Lists.html) about the release availability.
- The notification message SHOULD discuss:
  - The security issue in generic terms providing only enough detail to cause concern and attract community attention, but not to cause chaos or create an opportunity for adversaries to take advantage of the issue.
  - The severity of the security issue and nature of the patch release.
  - Affected CAS versions
  - Recommended guidelines for upgrades and applications of the patch.
- At the end of a grace period (see below), a public security announcement is posted that should fully disclose the issue and nuances of the security patch or workarounds.

<div class="alert alert-info">:information_source: <strong>Be Careful</strong><p>At the risk of stating the obvious, remember to only heed and accept community notifications about security fixes from <a href="/cas/developer/Project-Committee.html">trusted project contacts</a> and members. Posts detailing <i>security fixes</i> from random folks should entirely be ignored.</p></div>

Security announcements, notifications and details are typically posted on the [Apereo blog](https://apereo.github.io).

## Grace Period

Once the security release is published and the initial version of the security vulnerability becomes public, CAS will observe
a **four-week grace period** to allow community members and deployers to transition and upgrade to the security release. During this period,
no additional details (i.e. steps to reproduce, severity and impact, etc) about the security issue are made public and fixes and
commits to the codebase are kept privately. As a result, all release tags and other modifications to the CAS codebase related to the security fix
are put on hold until the grace period is over. At the end of the grace period, details about the security vulnerability are made public and the
actual patch, commit and release tags are then pushed to the CAS codebase, available to everyone.

<div class="alert alert-info">:information_source: <strong>GitHub Notifications</strong><p>Release notifications that are sent out by GitHub
to those that watch the Apereo CAS repository on GitHub can only be sent out when and once after a tagged commit is made public. As a result, security releases whose
grace period has not yet expired and whose tag remains private do not trigger a notification from GitHub. Once the grace period is passed and details
are made public, the tagged commit will appear on GitHub and release notifications will be sent out.</p></div>

As an example, let's imagine that a recent CAS security release `6.6.1.X` is now published. The following process and timeline is then generally observed:

| Version   | Release Date | Grace Period Ends | Binary Release Availability | Source Code Availability | GitHub Notifications |
|-----------|:------------:|------------------:|-----------------------------|:------------------------:|---------------------:|
| `6.6.1.X` |  July 10th   |       August 10th | July 10th                   |       August 10th        |          August 10th |
                         
If you are building the CAS codebase from source rather than relying on published binary releases, you should of course note that the above policy
has a direct effect on your build and release cycle and likely negative effects on your security posture. Given source-level changes only 
show up in the repository once after the grace period has ended, this practically means that in the event of a security release you will 
always be at least **four weeks** behind, which may or may not be ideal.

Furthermore, security releases (and all other types of CAS releases) always roll forward. That is to say, the CAS security release `6.6.1.X+1`
contains any and all changes that were presented in `6.6.1.X`, `6.6.1.X-1`, `6.6.1.X-2` and everything else prior to that release. Security releases
are not produced in isolation. 

## Trusted Contacts

Please note that the CAS project cannot disclose details about the security issue and its effects to private individuals without first verifying
their identity and Apereo Foundation membership status. If you consider yourself a trusted contact for your institution who is registered and vetted
by the Apereo Foundation, please provide the project with enough background information so we can happily proceed to explain the details in a relaxed
and trusted environment. If you are not a trusted member yet, please be patient during the grace period and allow other trusted contacts to properly
and securely take action to remedy the issue before the issue publicly disclosed to the wider user community.

To learn more about how to become an Apereo Foundation member and a registered trusted contact, please [contact Apereo directly](https://www.apereo.org).

## EOL Versions

Before posting a possible security issue, please make sure the affected CAS release line is still [under maintenance](/cas/developer/Maintenance-Policy.html).
Releases that are considered EOL will not receive further updates and/or attention from designated project members. Remember that if your CAS version is not
listed or affected in a security release announcement and is no longer part of an active maintenance cycle per the CAS maintenance policy, then a best course of action would
be to upgrade to a community-maintained CAS version. The project makes no effort to evaluate, verify, patch or test EOL versions of the software, has no interest, 
standing or budget to do so and produces no claims to ascertain the severity or effects of a security defect in an EOL CAS version. While this rule can be 
amended in *rare and extreme circumstances* depending on the security of the issue and community standing, the general policy allows that one may not speak ill of the dead.
