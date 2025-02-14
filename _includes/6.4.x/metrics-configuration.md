### Atlas

By default, metrics are exported to Atlas running on your 
local machine. The location of the Atlas server to use can be provided using:
     
{% include casproperties.html thirdPartyStartsWith="management.metrics.export.atlas" %}

### Datadog

Datadog registry pushes metrics to `datadoghq` periodically. To export 
metrics to Datadog, your API key must be provided:

{% include casproperties.html thirdPartyStartsWith="management.metrics.export.datadog" %}

### Ganglia

By default, metrics are exported to Ganglia running on your local 
machine. The Ganglia server host and port to use can be provided using:

{% include casproperties.html thirdPartyStartsWith="management.metrics.export.ganglia" %}

### Graphite

By default, metrics are exported to Graphite running on your local 
machine. The Graphite server host and port to use can be provided using:

{% include casproperties.html thirdPartyStartsWith="management.metrics.export.graphite" %}

### InfluxDb

By default, metrics are exported to Influx running on your local 
machine. The location of the Influx server to use can be provided using:

{% include casproperties.html thirdPartyStartsWith="management.metrics.export.influx" %}

### JMX

Micrometer provides a hierarchical mapping to JMX, primarily as a 
cheap and portable way to view metrics locally.

### New Relic

New Relic registry pushes metrics to New Relic periodically. To export 
metrics to New Relic, your API key and account id must be provided:

{% include casproperties.html thirdPartyStartsWith="management.metrics.export.newrelic" %}

### Prometheus

Prometheus expects to scrape or poll individual app instances for metrics. Spring Boot provides an actuator endpoint
available at `/actuator/prometheus` to present a Prometheus scrape with the appropriate format.

Here is an example `scrape_config` to add to `prometheus.yml`:

```yaml
scrape_configs:
  - job_name: 'spring'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['HOST:PORT']
``` 

### SignalFx

SignalFx registry pushes metrics to SignalFx periodically. To export 
metrics to SignalFx, your access token must be provided:

{% include casproperties.html thirdPartyStartsWith="management.metrics.export.signalfx" %}
     
### Memory

Micrometer ships with a simple, in-memory backend that is automatically 
used as a fallback if no other registry is configured.
This allows you to see what metrics are collected in the metrics endpoint.

The in-memory backend disables itself as soon as you’re using any of 
the other available backend. You can also disable it explicitly:

{% include casproperties.html thirdPartyStartsWith="management.metrics.export.simple" %}

### StatsD

The StatsD registry pushes metrics over UDP to a StatsD agent eagerly. By default,
metrics are exported to a StatsD agent running on your local machine. 
The StatsD agent host and port to use can be provided using:

{% include casproperties.html thirdPartyStartsWith="management.metrics.export.statsd" %}

### Wavefront

Wavefront registry pushes metrics to Wavefront periodically. If you are exporting metrics to
Wavefront directly, your API token must be provided:

{% include casproperties.html thirdPartyStartsWith="management.metrics.export.wavefront" %}
