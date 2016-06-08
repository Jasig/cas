package org.apereo.cas.config;

import org.apereo.cas.configuration.model.core.events.EventsProperties;
import org.apereo.cas.configuration.model.support.jpa.DatabaseProperties;
import org.apereo.cas.configuration.model.support.jpa.JpaConfigDataHolder;
import org.apereo.cas.support.events.dao.CasEventRepository;
import org.apereo.cas.support.events.jpa.JpaCasEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import static org.apereo.cas.configuration.support.BeansUtils.*;

/**
 * This is {@link JpaEventsConfiguration}, defines certain beans via configuration
 * while delegating some to Spring namespaces inside the context config file.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
@Configuration("jpaEventsConfiguration")
@EnableConfigurationProperties({EventsProperties.class, DatabaseProperties.class})
public class JpaEventsConfiguration {

    @Autowired
    private DatabaseProperties databaseProperties;

    @Autowired
    private EventsProperties eventsProperties;

    /**
     * Jpa event vendor adapter hibernate jpa vendor adapter.
     *
     * @return the hibernate jpa vendor adapter
     */
    @RefreshScope
    @Bean
    public HibernateJpaVendorAdapter jpaEventVendorAdapter() {
        return newHibernateJpaVendorAdapter(this.databaseProperties);
    }


    /**
     * Data source event combo pooled data source.
     *
     * @return the combo pooled data source
     */
    @RefreshScope
    @Bean
    public DataSource dataSourceEvent() {
        return newHickariDataSource(this.eventsProperties.getJpa().getDatabase());
    }

    /**
     * Jpa event packages to scan string [ ].
     *
     * @return the string [ ]
     */

    public String[] jpaEventPackagesToScan() {
        return new String[] {"org.apereo.cas.support.events.dao"};
    }

    /**
     * Events entity manager factory local container entity manager factory bean.
     *
     * @return the local container entity manager factory bean
     */
    @RefreshScope
    @Bean
    public LocalContainerEntityManagerFactoryBean eventsEntityManagerFactory() {
        final LocalContainerEntityManagerFactoryBean bean =
                newEntityManagerFactoryBean(
                        new JpaConfigDataHolder(
                                jpaEventVendorAdapter(),
                                "jpaEventRegistryContext",
                                jpaEventPackagesToScan(),
                                dataSourceEvent()),
                        this.eventsProperties.getJpa().getDatabase());

        bean.getJpaPropertyMap().put("hibernate.enable_lazy_load_no_trans", Boolean.TRUE);
        return bean;
    }


    /**
     * Transaction manager events jpa transaction manager.
     *
     * @param emf the emf
     *
     * @return the jpa transaction manager
     */
    @Autowired
    @Bean
    public JpaTransactionManager transactionManagerEvents(@Qualifier("eventsEntityManagerFactory")
                                                          final EntityManagerFactory emf) {
        final JpaTransactionManager mgmr = new JpaTransactionManager();
        mgmr.setEntityManagerFactory(emf);
        return mgmr;
    }


    @Bean
    public CasEventRepository casEventRepository() {
        return new JpaCasEventRepository();
    }
}
