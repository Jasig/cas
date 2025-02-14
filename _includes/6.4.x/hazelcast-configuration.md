#### Hazelcast Configuration

{% capture cfgkey %}{{ include.configKey }}.core{% endcapture %}
{% include casproperties.html properties=cfgkey %}
   
#### Hazelcast Cluster Core

{% capture cfgkey %}{{ include.configKey }}.cluster.core{% endcapture %}
{% include casproperties.html properties=cfgkey %}

#### Hazelcast Cluster Networking

{% capture cfgkey %}{{ include.configKey }}.cluster.network{% endcapture %}
{% include casproperties.html properties=cfgkey %}
