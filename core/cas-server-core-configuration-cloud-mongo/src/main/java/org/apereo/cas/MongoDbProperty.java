package org.apereo.cas;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Getter;

/**
 * This is {@link MongoDbProperty}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
@Document
@Slf4j
@Getter
public class MongoDbProperty {

    @Id
    private String id;

    @Indexed
    private String name;

    private Object value;

    public void setId(final String id) {
        this.id = id;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(final Object value) {
        this.value = value;
    }
}
