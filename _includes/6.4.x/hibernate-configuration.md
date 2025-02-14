<p/>

Control global properties that are relevant to Hibernate,
when CAS attempts to employ and utilize database resources,
connections and queries.

{% assign jdbcProperties = "cas.jdbc." | split: "," %}

<table>
    <tbody>
    {% for prop in jdbcProperties %} 
        {% for module in site.data[siteDataVersion] %}
            {% assign moduleEntry = module[1] %}
            {% for cfg in moduleEntry %}
                {% assign configBlock = cfg[1] %}
                {% for config in configBlock %}
                    {% if config.name contains prop %}  
                        {% include casproperty.html 
                            name=config.name 
                            defaultValue=config.defaultValue 
                            description=config.description 
                            deprecationLevel=config.deprecationLevel
                            deprecationReplacement=config.deprecationReplacement %}
                    {% endif %}
                {% endfor %}
            {% endfor %}
        {% endfor %}
    {% endfor %}
    </tbody>
</table>
