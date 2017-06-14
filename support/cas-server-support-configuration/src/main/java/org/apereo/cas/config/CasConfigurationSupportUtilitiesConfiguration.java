
package org.apereo.cas.config;

import com.google.common.base.Throwables;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.configuration.CasConfigurationPropertiesEnvironmentManager;
import org.apereo.cas.support.events.AbstractCasEvent;
import org.apereo.cas.support.events.config.CasConfigurationCreatedEvent;
import org.apereo.cas.support.events.config.CasConfigurationDeletedEvent;
import org.apereo.cas.support.events.config.CasConfigurationModifiedEvent;
import org.apereo.cas.util.function.ComposableFunction;
import org.apereo.cas.util.PathWatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.function.Consumer;

/**
 * This is {@link CasConfigurationSupportUtilitiesConfiguration}.
 *
 * @author Misagh Moayyed
 * @since 5.1.0
 */
@Configuration("casConfigurationSupportUtilitiesConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class CasConfigurationSupportUtilitiesConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(CasConfigurationSupportUtilitiesConfiguration.class);

    @Autowired
    private CasConfigurationProperties casProperties;

    private ComposableFunction<File, AbstractCasEvent> createConfigurationCreatedEvent = file -> new CasConfigurationCreatedEvent(this, file.toPath());
    private ComposableFunction<File, AbstractCasEvent> createConfigurationModifiedEvent = file -> new CasConfigurationModifiedEvent(this, file.toPath());
    private ComposableFunction<File, AbstractCasEvent> createConfigurationDeletedEvent = file -> new CasConfigurationDeletedEvent(this, file.toPath());

    /**
     * The watch configuration.
     */
    @Configuration("casCoreConfigurationWatchConfiguration")
    @Profile("standalone")
    @ConditionalOnProperty(value = "spring.cloud.config.enabled", havingValue = "false")
    public class CasCoreConfigurationWatchConfiguration {
        @Autowired
        private ApplicationEventPublisher eventPublisher;

        @Autowired
        @Qualifier("configurationPropertiesEnvironmentManager")
        private CasConfigurationPropertiesEnvironmentManager configurationPropertiesEnvironmentManager;

        // PLEASE REVIEW: make sense to this publisher to live in a where ApplicationEventPublisher lives?? and just reference to it
        private Consumer<AbstractCasEvent> publish = event -> eventPublisher.publishEvent(event);

        @PostConstruct
        public void init() {
            runNativeConfigurationDirectoryPathWatchService();
        }

        public void runNativeConfigurationDirectoryPathWatchService() {
            try {
                final File config = configurationPropertiesEnvironmentManager.getStandaloneProfileConfigurationDirectory();
                if (casProperties.getEvents().isTrackConfigurationModifications() && config.exists()) {
                    LOGGER.debug("Starting to watch configuration directory [{}]", config);
                    new Thread(new PathWatcher(config.toPath(),
                            createConfigurationCreatedEvent.andThen(publish),
                            createConfigurationModifiedEvent.andThen(publish),
                            createConfigurationDeletedEvent.andThen(publish)))
                        .start();
                } else {
                    LOGGER.info("CAS is configured to NOT watch configuration directory [{}]. Changes require manual reloads/restarts.", config);
                }
            } catch (final Exception e) {
                throw Throwables.propagate(e);
            }
        }
    }
}
