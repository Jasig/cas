---
layout: default
title: CAS - Upgrade Guide
---

# Upgrade Guide

In general, it is recommended that adopters try to keep their CAS deployment in alignment with the latest CAS version available.
In particular, releases that are of `PATCH` or `SECURITY` nature should be immediately applied as they are drop-in replacements
for their corresponding parent version. See CAS [Release Policy](../../developer/Release-Policy.html) for more info. 

The general objectives of a CAS upgrade could be:

1. Does the upgrade fix a critical security vulnerability or annoying issue? Is my CAS deployment 
affected by that vulnerability and/or bug?
2. Does the upgrade present features that might be useful to achieve local use cases?
3. Does the upgrade provide functionality that is carried locally within my overlay, such that by getting rid of those
local changes, I can realize their benefit from CAS directly and end up with a smaller more-maintainable overlay? 

This document attempts to describe, at a very high level, the scope and effort required to upgrade a given 
[CAS deployment](../installation/Maven-Overlay-Installation.html). Rather than describing all steps/changes that would be required
to review and adjust (which would be impossible), we describe a strategy by which the upgrade could be executed. 

## Change Log

Before attempting to upgrade, please review the [CAS change log](https://github.com/apereo/cas/releases) to determine
what changes/fixes are contained in the version you intend to upgrade to, and whether those are applicable to your environment
and your CAS deployment. If you are working with an older CAS version and are experiencing what appears to be a bug, chances are
by reviewing the change log, you will find a drop-in replacement for your overlay that takes care of the issue. 

## Discuss Issue

Having reviewed the change log, if you do not see an improvement that fixes/adjusts the behavior you have in mind, 
please discuss the issue on the appropriate CAS mailing lists. The result of the discussing would be a scope/effort
evaluation to determine feasibility of the solution and the target version in which the fix will be done. 

## Scope Review

Once you decide your ideal CAS version for the upgrade, before attempting to upgrade, 
please review the CAS [Release Policy](../../developer/Release-Policy.html). This will provide you
with an understanding of what changes you may expect from new version and what the required effort
may be for the upgrade.

## Evaluate Local Overlay

As a best practice, it is recommended that you deploy CAS via the [overlay method](../installation/Maven-Overlay-Installation.html).
If you have, the task here would be to identify the number of files your overlay has touched and modified. Catalog the 
what and why of the changes applied, and cross-check those changes with the CAS change log. Chances are, many of the
local changes that are present within your overlay are provided by default via CAS as a result of that upgrade which will
have you shed many of those improvements locally. 

Your changes typically are:

* Authentication scheme and strategy (i.e. LDAP, JDBC, etc)
* Settings controlling CAS behavior in CAS properties files
* User Interface changes may include CSS and JavaScript
* Attribute resolution and release policy
* Services registered and authorized to use CAS

## Prepare Development Environment

<img src="http://i.imgur.com/jcdDHWb.jpg" width="160px" height="200px">

Um, No. 

Make sure you have a separate development environment ready for configuration and testing. Regardless of how small
the upgrade is, you want to make sure it is well tested in your environment before you flip the switch. Evaluate
the software dependencies and platform requirements of the new upgrade (i.e. Java, etc)
and make sure you have everything installed and configured correctly before you attempt. 

## Sanitize Configuration

We recommend that you first start out with a separate clean [CAS overlay](../installation/Maven-Overlay-Installation.html) targeted
at the version to which you want to upgrade. This has the advantage of guaranteeing that your new CAS deployment 
will be functional without any local changes. Build and deploy the clean CAS overlay once to make sure
your build/deployment process is functional.

## Apply Changes

Go through your catalog of changes found in your local overlay. Compare and diff those files with their
original version. You can find out the delta between two versions via the following ways:

1. If you have built the clean CAS overlay once, you will automatically get the original version typically
in the `target` or `build/libs` directory of CAS overlay. Find the correct file at the correct path and compare.

2. Go directly to the [project source repository](https://github.com/apereo/cas), find the appropriate branch
and compare files. 

Needless to say, you are going to need:

1. A decent diff tool, such as [KDiff3](http://kdiff3.sourceforge.net/), [WinDiff](http://winmerge.org), 
[Beyond Compare](http://www.scootersoftware.com/), etc.
2. A decent intelligent text editor, such as [Sublime](http://www.sublimetext.com),
[Atom](https://atom.io/) or a full blown IDE such as [IntelliJ IDEA](https://www.jetbrains.com/idea/).

## Document Changes

Remember to document the remaining changes that exist within your local overlay, so that the next time you do the
same process, you have a clue as for why the overlay looks the way it does. 
