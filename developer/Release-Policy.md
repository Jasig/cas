---
layout: default
title: CAS - Release Policy
---

# Release Policy

CAS follows the below release strategy in determining the kinds of changes different releases can
accommodate and how lines of development are managed. CAS adopters can also expect the following
for each release type. All new improvements, fixes, enhancements, and changes are weighed against
the release strategy for consideration in future release.

<div class="alert alert-warning">:warning: <strong>Stop Coding</strong><p>
It is in your best interest to try and avoid making changes to the internals of the CAS software, to make future upgrades relatively easy and painless. The more you
 modify the inner workings of CAS to implement and deliver <i>custom</i> use cases, the more trouble it would be for the deployment down the road as you would 
 face configuration and API changes. Being a single parent is tough, so avoid carrying the maintenance burden solely on your own. Consider discussing and sharing
 use cases with the community and contribute changes back so as to let your deployment simply become a consumer, and not an owner, of such changes down the road.</p>
</div>

## Schedules

The project release schedule is [available here](https://github.com/apereo/cas/milestones). The
[maintenance policy](Maintenance-Policy.html) is also available.

## SECURITY

Security releases are a critical minimal change on a release to address a serious confirmed
security issue. That is, `2.5.0.1` would be `2.5.0` with a minimal change to patch a vulnerability.
All adopters are strongly urged to upgrade to a SECURITY releases as soon as possible.
CAS overlays should build with no changes, unless required and highlighted in the release notes.

No third-party library updates take place in a security release, unless there exists is solid evidence to demonstrate and require
the need and the effort for a package upgrade. [See this guide](Sec-Vuln-Response.html) for more info.

## PATCH

A conservative incremental improvement that includes bug fixes, enhancements and new features
and is absolutely backward compatible with previous PATCH releases of the same
MINOR release. (i.e. `2.4.15` is a drop in replacement for `2.4.14`, `2.4.13`, `2.4.12`, etc.).
Adopters can expect that major APIs, integration points, default behavior, and general
configuration is mostly the same. CAS overlays should build with little to no changes,
unless required and highlighted in the release notes and change logs.

No third-party library updates take place in a patch release, unless there exists is solid evidence to demonstrate and require
the need and the effort for a package upgrade. [See this guide](Sec-Vuln-Response.html) for more info.

## FEATURE

An *evolutionary* incremental improvement that includes all PATCH release improvements
along with fixes and enhancements that could not easily be accommodated without
breaking backward compatibility or changing default behavior. (i.e. Transitioning from `3.4.x` to `3.5.x`, etc.)
Adopters can expect general improvements that require moderate changes in APIs, integration points,
default behavior, and general configuration. Overall the CAS server code along the MINOR
development line looks pretty much the same from release to release with clear moderate evolutionary
changes. FEATURE releases may have a theme or focus that helps coordinate development.
CAS overlays may require minor to moderate changes, some APIs may have changed or
been deprecated, default behavior and configuration may have changed.
While implementation APIs may change, [CAS APIs](https://github.com/apereo/cas/tree/master/api)
will remain unchanged between FEATURE release versions.

Third-party library updates are acceptable for feature releases. However, library updates that lead to a change
in platform requirements (i.e. Java, OS, Server Container) are ignored.

## MAJOR

A *revolutionary* change accommodating sweeping architecture, approach, and
implementation changes. (i.e. Transitioning from `3.5.x` to `4.0.x`, etc.)
Significant changes in APIs, default behavior, and configuration can be expected.
Overlays may require significant changes and possibly a complete reworking.
Overall the CAS server code line may look markedly different and integration
points may require reworking. 

Third-party library updates are acceptable for feature releases, including changes that might lead to an upgrade
of platform requirements (i.e. Java, OS, Server Container).
