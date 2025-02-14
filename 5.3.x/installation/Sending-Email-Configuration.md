---
layout: default
title: CAS - Sending Email
---

# Sending Email

CAS presents the ability to notify users on select actions via email messages. Example actions include notification 
of risky authentication attempts or password reset links/tokens, etc. Configuring an email provider (i.e. Amazon Simple Email Service )
is simply a matter of defining SMTP settings. Each particular feature in need of email functionality should be able to 
gracefully continue in case settings are not defined. 

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#email-submissions).
