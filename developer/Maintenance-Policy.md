---
layout: default
title: CAS - Maintenance Policy
---

# Maintenance Policy

This document describes the official CAS policy as it regards maintenance and management of released CAS server versions.

In particular, the following questions are addressed:

- How long should a CAS release be maintained?
- What is the appropriate scope for release maintenance once a release is retired?

## Schedules

The project release schedule is [available here](https://github.com/apereo/cas/milestones).

<div class="alert alert-info">:information_source: <strong>Which version is more stable?</strong><p>CAS releases are strictly time-based releases; they 
are not scheduled or based on specific benchmarks, statistics or completion of features or bug fixes. To gain confidence in a 
release, you should start early by experimenting with release candidates and/or follow-up snapshots.
In terms of stability, all versions of the Apereo CAS software are loosely based on the <i>Schrödinger's Cat</i> theory: there are no guarantees 
until you open the box. Software distributed under the project license is distributed on an "AS IS" basis without warranties or conditions 
of any kind, either express or implied. See the project license file for the specific language.
</p></div>

## Policy

- CAS adopters MAY expect a CAS release to be maintained for **six months**, starting from the [original release date](https://github.com/apereo/cas/releases).
- Maintenance during this period includes bug fixes, security patches and general upkeep of the release. Patch releases may be expected during this period per the release schedule.
- Afterwards, maintenance of the release is *strictly* limited to security patches and fixing vulnerabilities for another **six months** (i.e. SPM mode).
- The lifespan of a release MAY be extended beyond a single year, to be decided by the [CAS PMC](Project-Committee.html) and the community at large when and where reasonable, assuming sufficient availability, resources and funding.

“CAS Release” is any CAS feature release. (i.e. `5.0.x`, `5.1.x`, etc).

<div class="alert alert-info">:information_source: <strong>Uh...Maintenance?</strong><p>
In this context, maintenance strictly means that the release line and the target destination branch in the CAS codebase remains open to
  accept patches and <strong>contributions from the community</strong> and that there will be follow-up binary releases forthcoming until the designated dates. 
</p></div>

## Timeline

On a good day, the CAS project maintains three active branches/releases at the same time: the main branch which leads the 
project's development efforts as well as two other maintenance releases whose maintenance cycle is addressed in this document. 
This policy and relevant maintenance cycle timelines are decided based on the availability, time, and interest of current project 
members and volunteers and reflect the project's funding, resources, and commitment in practical and realistic ways. Should 
circumstances change, so might the policy to shorten or extend the maintenance cycle.

## What is EOL?

"End-of-life" ("EOL") is a term used to describe that a given CAS release line (i.e. `6.1.x`, `6.2.x`, `7.0.0` etc) is in the end 
of its practical life (from the project's point of view), and the project stops accepting, adding and/or releasing patches of any 
kind once it reaches that designated date. The EOL release is considered dead and it will not receive any attention whatsoever 
regardless of the issue impact or severity, unless absolutely justified by the [CAS PMC](Project-Committee.html), subject to people's availability, 
project interest, resources and sufficient funding.

<div class="alert alert-info">:information_source: <strong>Documentation</strong><p>
The documentation maintenance and hosting efforts will cease to exist for EOL versions after some time. If you do need access to the documentation
for an EOL version, you could always find the original pages in the CAS codebase where you could take the pages and render and host
them as you would like and take on the maintenance and hosting burden on your own.
</p></div>

## EOL Schedule

The following CAS releases will transition into a security-patch mode (SPM) only and will be EOLed at the indicated dates.

| Release | SPM Starting Date  |             Full EOL |
|---------|:------------------:|---------------------:|
| `7.1.x` |  March 31st, 2025  | September 30th, 2025 |
| `7.0.x` | October 31st, 2024 |     April 30th, 2025 |

All previous releases absent in the above table are considered EOLed.

## Security-Patch Mode (SPM)

Once a CAS release transitions into an SPM phase, the release line and relevant milestones will be publicly 
closed. Patches and contributions must be communicated and reported via [desginated channels](/cas/Mailing-Lists.html) designed 
for security-related issues and reports. Such reports will be reviewed and analyzed per the [Security Vulnerability Response](/cas/developer/Sec-Vuln-Response.html). 
Please make sure your report has enough information and detail so the issue can be reproduced based on a concrete 
use case or one that truly in practice affects the inner workings of the Apereo CAS software in a real way.

## Long-Term Support (LTS)

Based on the availability, funding status, and interest of existing project members and volunteers, 
the CAS project can not offer LTS releases in a practical and sustainable sense and keep to a committed schedule 
long-term based on volunteer efforts and free time. If you and/or your organization are interested in LTS releases 
and long-term commitments, please reach out to the project to discuss the details.
