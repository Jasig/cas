---
layout: default
title: CAS - Configuring Service Expiration Policy
category: Services
---

# Configure Service Expiration Policy

An application registered with CAS may be assigned an optional expiration policy that controls the lifetime of the registration. Once the service is deemed expired, it will automatically be disabled or removed from the CAS registry and [relevant contacts](Configuring-Service-Contacts.html) defined and assigned to the service will be notified via email or text messages.

A sample JSON file follows:

```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "^https://.+",
  "name" : "sample service",
  "id" : 100,
  "expirationPolicy": {
    "@class": "org.apereo.cas.services.DefaultRegisteredServiceExpirationPolicy",
    "deleteWhenExpired": true,
    "notifyWhenDeleted": false,
    "expirationDate": "2017-10-05"
  }
}
```

The following settings are available by default for expiration policies:

| Field                | Description
|----------------------|-------------------------------------------------------------------
| `expirationDate`     | The date on which the registration record is deemed expired. The expiration date may be specified in `2011-12-03T10:15:30`, `09/24/1980 04:30 PM`, `09/24/2014 6:30 AM`, `09/24/2013 18:45`, `09/24/2017` or `2017-10-25` formats.
| `deleteWhenExpired`  | When `true`, removes the application from the CAS service registry if and when expired. Otherwise the application record will be marked as disabled.
| `notifyWhenDeleted`  | Notifies [contacts](Configuring-Service-Contacts.html) of the application via email or text, assuming valid contacts with email addresses or phone numbers are defined and CAS is configured to send [email messages](../notifications/Sending-Email-Configuration.html) or [SMS notifications](../notifications/SMS-Messaging-Configuration.html). The notification is only sent if the application is expired and is about to be deleted from the registry.
