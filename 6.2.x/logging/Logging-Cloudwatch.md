---
layout: default
title: CAS - Cloudwatch Logging Configuration
category: Logs & Audits
---

# Cloudwatch Logging

Log data can be automatically routed to [AWS CloudWatch](https://aws.amazon.com/cloudwatch/). Support is enabled by including the following module in the overlay:

```xml
<dependency>
     <groupId>org.apereo.cas</groupId>
     <artifactId>cas-server-support-logging-config-cloudwatch</artifactId>
     <version>${cas.version}</version>
</dependency>
```

With the above module, you may then declare a specific appender to communicate with AWS CloudWatch:

```xml
<CloudWatchAppender name="cloudWatch"
                    awsLogGroupName="LogGroupName"
                    awsLogStreamName="LogStreamName"
                    awsLogRegionName="us-west-1"
                    credentialAccessKey="..."
                    credentialSecretKey="..."
                    awsLogStreamFlushPeriodInSeconds="5"
                    createIfNeeded="true"
                    createLogGroupIfNeeded="false"
                    createLogStreamIfNeeded="false">
    <PatternLayout>
        <Pattern>%5p | %d{ISO8601}{UTC} | %t | %C | %M:%L | %m %ex %n</Pattern>
    </PatternLayout>
</CloudWatchAppender>
...
<AsyncLogger name="org.apereo" additivity="true" level="debug">
    <appender-ref ref="cloudWatch" />
</AsyncLogger>
```

AWS credentials are fetched from the following sources automatically, where relevant and made possible via CAS configuration:

1. EC2 instance metadata linked to the IAM role.
2. External properties file that contains `accessKey` and `secretKey` as property keys.
3. AWS profile path and profile name.
4. System properties that include `aws.accessKeyId`, `aws.secretKey` and `aws.sessionToken`
5. Environment variables that include `AWS_ACCESS_KEY_ID`, `AWS_SECRET_KEY` and `AWS_SESSION_TOKEN`.
6. Properties file on the classpath as `awscredentials.properties` that contains `accessKey` and `secretKey` as property keys.
7. Static credentials for access key and secret provided directly by the configuration at hand (logging, etc).

`createIfNeeded`, `createLogGroupIfNeeded`, and `createLogStreamIfNeeded` are optional; `createIfNeeded` will default to
 `true` while `createLogGroupIfNeeded` and `createLogStreamIfNeeded` default to `false`. Any `true` value will take
 precendence (i.e., set all to `false` to not create anything).