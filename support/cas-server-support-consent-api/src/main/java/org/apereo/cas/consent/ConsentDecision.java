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
import lombok.Setter;

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
@Setter
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

    public ChronoUnit getReminderTimeUnit() {
        return reminderTimeUnit;
    }

    public long getId() {
        return id;
    }

    public ConsentOptions getOptions() {
        return options;
    }

    public Long getReminder() {
        return reminder;
    }
}
