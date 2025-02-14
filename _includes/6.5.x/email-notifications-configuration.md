<p/>

The following settings may also need to be defined to describe the mail server settings:

{% assign mailServerProperties = "spring.mail." | split: "," %}

<table>
    <tbody>
    {% for prop in mailServerProperties %} 
        {% for module in site.data[siteDataVersion] %}
            {% assign moduleEntry = module[1] %}
            {% for cfg in moduleEntry %}
                {% assign configBlock = cfg[1] %}
                {% for config in configBlock %}
                    {% if config.name contains prop %}  
                        {% include_cached casproperty.html 
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
