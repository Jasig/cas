---
layout: default
title: CAS - Password Management
---

# Password Management - Groovy

Accounts and password may be determined and handled using a customized Groovy script. The outline of the script may match the following:

```groovy
def change(Object[] args) {
    def credential = args[0]
    def passwordChangeBean = args[1]
    def logger = args[2]
    return true
}

def findEmail(Object[] args) {
    def username = args[0]
    def logger = args[1]
    return "cas@example.org"
}

def getSecurityQuestions(Object[] args) {
    def username = args[0]
    def logger = args[1]
    return [securityQuestion1: "securityAnswer1"]
}
```

To see the relevant list of CAS properties, please [review this guide](Configuration-Properties.html#groovy-password-management).
