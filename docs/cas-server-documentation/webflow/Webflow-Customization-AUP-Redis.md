---
layout: default
title: CAS - Web Flow Acceptable Usage Policy
category: Webflow Management
---

{% include variables.html %}

# Redis Acceptable Usage Policy

CAS can be configured to use a Redis instance as the storage mechanism. Decisions
are mapped to a combination of CAS username and the designated AUP attribute name.

Support is enabled by including the following dependency in the WAR overlay:

{% include casmodule.html group="org.apereo.cas" module="cas-server-support-aup-redis" %}

{% include {{ version }}/redis-configuration.md configKey="cas.acceptable-usage-policy" %}
