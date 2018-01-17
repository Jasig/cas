package org.apereo.cas.consent;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.ToString;
import lombok.Getter;

/**
 * This is {@link ConsentDecision}.
 *
 * @author Misagh Moayyed
 * @since 5.1.0
 */
@Entity
@Table(name = "ConsentDecision")
@Slf4j
@ToString
@Getter
public class ConsentDecision {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id = -1;

    @Column(length = 255, updatable = true, insertable = true, nullable = false)
    private String principal;

    @Column(length = 255, updatable = true, insertable = true, nullable = false)
    private String service;

    @Column(nullable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(nullable = false)
    private ConsentOptions options = ConsentOptions.ATTRIBUTE_NAME;

    @Column(length = 255, updatable = true, insertable = true, nullable = false)
    private Long reminder = 14L;

    @Column(length = 255, updatable = true, insertable = true, nullable = false)
    private ChronoUnit reminderTimeUnit = ChronoUnit.DAYS;

    @Lob
    @Column(name = "attributes", length = Integer.MAX_VALUE)
    private String attributes;

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(final LocalDateTime date) {
        this.createdDate = date;
    }

    public ChronoUnit getReminderTimeUnit() {
        return reminderTimeUnit;
    }

    public void setReminderTimeUnit(final ChronoUnit reminderTimeUnit) {
        this.reminderTimeUnit = reminderTimeUnit;
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public void setPrincipal(final String principal) {
        this.principal = principal;
    }

    public void setService(final String service) {
        this.service = service;
    }

    public ConsentOptions getOptions() {
        return options;
    }

    public void setOptions(final ConsentOptions options) {
        this.options = options;
    }

    public void setReminder(final Long reminder) {
        this.reminder = reminder;
    }

    public Long getReminder() {
        return reminder;
    }

    public void setAttributes(final String attributes) {
        this.attributes = attributes;
    }
}
