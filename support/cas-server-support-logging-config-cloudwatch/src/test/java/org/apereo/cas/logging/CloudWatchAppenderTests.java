package org.apereo.cas.logging;

import com.amazonaws.services.logs.AWSLogs;
import com.amazonaws.services.logs.model.CreateLogGroupRequest;
import com.amazonaws.services.logs.model.CreateLogStreamRequest;
import com.amazonaws.services.logs.model.DescribeLogGroupsRequest;
import com.amazonaws.services.logs.model.DescribeLogGroupsResult;
import com.amazonaws.services.logs.model.DescribeLogStreamsRequest;
import com.amazonaws.services.logs.model.DescribeLogStreamsResult;
import com.amazonaws.services.logs.model.LogGroup;
import com.amazonaws.services.logs.model.LogStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertNotNull;



    /*
     createIfNeeded | createLogGroupIfNeeded | createLogStreamIfNeeded | expectations
     null           | null                   | null                    | createLogGroup > 1, createLogStream > 1
     true           | null                   | null                    | createLogGroup > 1, createLogStream > 1
     null           | true                   | null                    | createLogGroup > 1, createLogStream never
     null           | null                   | true                    | createLogGroup never, createLogStream > 1
     null           | true                   | true                    | createLogGroup > 1, createLogStream > 1
     true           | true                   | null                    | createLogGroup > 1, createLogStream > 1
     ...
     */

public class CloudWatchAppenderTests {
    @Test
    @DisplayName("make sure that log4j plugin file is generated")
    public void fileGenerated() {
        ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
        builder.add(builder.newAppender("cloudwatch", "CloudWatchAppender"));
        Configuration configuration = builder.build();
        Configurator.initialize(configuration);
        assertNotNull(configuration.getAppender("cloudwatch"));
    }

    @ParameterizedTest
    @MethodSource("generateTestCases")
    @DisplayName("making sure incoming parameters are set correctly")
    void specTest(TestCase tC) {
        AWSLogs mock = Mockito.mock(AWSLogs.class);
        //TODO test, save to repo and have JJ go through test scenarios.
        if (tC.logGroupExists) {
            Mockito.when(mock.describeLogStreams(Mockito.any(DescribeLogStreamsRequest.class))).thenReturn(createDescribeLogStreamsResult());
        }
        if (tC.logGroupExists) {
            Mockito.when(mock.describeLogGroups(Mockito.any(DescribeLogGroupsRequest.class))).thenReturn(createDescribeLogGroupsResult());
        }

        // we do this because the lifecycle is a little different for this sort of programmatic configuration
        CloudWatchAppender appender = new CloudWatchAppender("test", "test", "test", "30", null, tC.createIfNeeded, tC.createLogGroupIfNeeded, tC.createLogStreamIfNeeded, mock);
        appender.initialize();

        ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
        BuiltConfiguration configuration = builder.build();
        configuration.addAppender(appender);
        Configurator.initialize(configuration);

        Logger logger = LogManager.getLogger("test");
        logger.info("here is a message");

        createLogGroup(mock, tC.resultCreateLogGroupIfNeeded);
        createLogStream(mock, tC.resultCreateLogStreamIfNeeded);

    }

    private static DescribeLogStreamsResult createDescribeLogStreamsResult() {
        DescribeLogStreamsResult stream = new DescribeLogStreamsResult();
        LogStream logStream = new LogStream();
        logStream.setLogStreamName("test");
        logStream.setUploadSequenceToken("test");
        stream.getLogStreams().add(logStream);

        return stream;
    }

    private static DescribeLogGroupsResult createDescribeLogGroupsResult() {
        DescribeLogGroupsResult group = new DescribeLogGroupsResult();
        LogGroup logGroup = new LogGroup();
        logGroup.setLogGroupName("test");

        return group;
    }

    private static void createLogGroup(AWSLogs logs, Boolean value) {
        if (value) {
            Mockito.verify(logs, Mockito.atLeastOnce()).createLogGroup(Mockito.any(CreateLogGroupRequest.class));
        } else {
            Mockito.verify(logs, Mockito.never()).createLogGroup(Mockito.any(CreateLogGroupRequest.class));
        }
    }

    private static void createLogStream(AWSLogs logs, Boolean value) {
        if (value) {
            Mockito.verify(logs, Mockito.atLeastOnce()).createLogStream(Mockito.any(CreateLogStreamRequest.class));
        } else {
            Mockito.verify(logs, Mockito.never()).createLogStream(Mockito.any(CreateLogStreamRequest.class));
        }
    }

    public static ArrayList<TestCase> generateTestCases() {
        ArrayList<TestCase> Cases = new ArrayList<>();

        //Cases.add(new TestCase(null, null, null, true, true));
        //Cases.add(new TestCase(null, null, false, false, false));
        Cases.add(new TestCase(null, null, true, false, true));
        Cases.add(new TestCase(null, false, null, false, false));
        Cases.add(new TestCase(null, false, false, false, false));
        Cases.add(new TestCase(null, false, true, false, true));
        Cases.add(new TestCase(null, true, null, true, false));
        Cases.add(new TestCase(null, true, false, true, false));
        Cases.add(new TestCase(null, true, true, true, true));
        Cases.add(new TestCase(false, null, null, false, false));
        Cases.add(new TestCase(false, null, false, false, false));
        Cases.add(new TestCase(false, null, true, false, true));
        Cases.add(new TestCase(false, false, null, false, false));
        Cases.add(new TestCase(false, false, false, false, false));
        Cases.add(new TestCase(false, false, true, false, true));
        Cases.add(new TestCase(false, true, null, true, false));
        Cases.add(new TestCase(false, true, false, true, false));
        Cases.add(new TestCase(false, true, true, true, true));
        Cases.add(new TestCase(true, null, null, false, false));
        Cases.add(new TestCase(true, null, false, false,false));
        Cases.add(new TestCase(true, null, true, false,true));
        Cases.add(new TestCase(true, false, null, false,false));
        Cases.add(new TestCase(true, false, false, false,false));
        Cases.add(new TestCase(true, false, true, false,true));
        Cases.add(new TestCase(true, true, null, true,false));
        Cases.add(new TestCase(true, true, false, true,false));
        Cases.add(new TestCase(true, true, true, true,true));

        return Cases;
    }

    public static class TestCase {
        public Boolean createIfNeeded;
        public Boolean createLogGroupIfNeeded;
        public Boolean createLogStreamIfNeeded;
        public Boolean resultCreateLogGroupIfNeeded;
        public Boolean resultCreateLogStreamIfNeeded;
        public Boolean logGroupExists;
        public Boolean logStreamExists;

        public TestCase(Boolean createIfNeeded, Boolean createLogGroupIfNeeded, Boolean createLogStreamIfNeeded, Boolean resultCreateLogGroupIfNeeded, Boolean resultCreateLogStreamIfNeeded) {
            this.createIfNeeded = createIfNeeded;
            this.createLogGroupIfNeeded = createLogGroupIfNeeded;
            this.createLogStreamIfNeeded = createLogStreamIfNeeded;
            this.resultCreateLogGroupIfNeeded = resultCreateLogGroupIfNeeded;
            this.resultCreateLogStreamIfNeeded = resultCreateLogStreamIfNeeded;
            this.logGroupExists = true;
            this.logStreamExists = true;
        }

        public TestCase(Boolean createIfNeeded, Boolean createLogGroupIfNeeded, Boolean createLogStreamIfNeeded, Boolean resultCreateLogGroupIfNeeded, Boolean resultCreateLogStreamIfNeeded, Boolean logGroupExists, Boolean logStreamExists) {
            this.createIfNeeded = createIfNeeded;
            this.createLogGroupIfNeeded = createLogGroupIfNeeded;
            this.createLogStreamIfNeeded = createLogStreamIfNeeded;
            this.resultCreateLogGroupIfNeeded = resultCreateLogGroupIfNeeded;
            this.resultCreateLogStreamIfNeeded = resultCreateLogStreamIfNeeded;
            this.logGroupExists = logGroupExists;
            this.logStreamExists = logStreamExists;
        }
    }
}
