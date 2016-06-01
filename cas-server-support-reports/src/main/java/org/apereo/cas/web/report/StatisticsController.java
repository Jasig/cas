package org.apereo.cas.web.report;

import org.apereo.cas.CentralAuthenticationService;
import org.apereo.cas.ticket.ServiceTicket;
import org.apereo.cas.ticket.Ticket;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.servlets.HealthCheckServlet;
import com.codahale.metrics.servlets.MetricsServlet;
import com.google.common.base.Predicates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * @author Scott Battaglia
 * @since 3.3.5
 */
@Controller("statisticsController")
@RequestMapping("/status/stats")
public class StatisticsController implements ServletContextAware {

    private static final int NUMBER_OF_MILLISECONDS_IN_A_DAY = 86400000;

    private static final int NUMBER_OF_MILLISECONDS_IN_AN_HOUR = 3600000;

    private static final int NUMBER_OF_MILLISECONDS_IN_A_MINUTE = 60000;

    private static final int NUMBER_OF_MILLISECONDS_IN_A_SECOND = 1000;

    private static final int NUMBER_OF_BYTES_IN_A_KILOBYTE = 1024;

    private  static final String MONITORING_VIEW_STATISTICS = "monitoring/viewStatistics";

    private transient Logger logger = LoggerFactory.getLogger(this.getClass());

    private ZonedDateTime upTimeStartDate = ZonedDateTime.now(ZoneOffset.UTC);

    @Value("${host.name:cas01.example.org}")
    private String casTicketSuffix;

    @Autowired
    private CentralAuthenticationService centralAuthenticationService;

    @Autowired
    @Qualifier("metrics")
    private MetricRegistry metricsRegistry;

    @Autowired
    @Qualifier("healthCheckMetrics")
    private HealthCheckRegistry healthCheckRegistry;

    /**
     * Gets availability times of the server.
     *
     * @param httpServletRequest  the http servlet request
     * @param httpServletResponse the http servlet response
     * @return the availability
     */
    @RequestMapping(value = "/getAvailability", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getAvailability(final HttpServletRequest httpServletRequest,
                                              final HttpServletResponse httpServletResponse) {
        final Map<String, Object> model = new HashMap<>();
        model.put("startTime", this.upTimeStartDate);
        final double difference = this.upTimeStartDate.until(ZonedDateTime.now(ZoneOffset.UTC), 
                ChronoUnit.MILLIS);

        model.put("upTime", calculateUptime(difference, new LinkedList<>(
                        Arrays.asList(NUMBER_OF_MILLISECONDS_IN_A_DAY, NUMBER_OF_MILLISECONDS_IN_AN_HOUR,
                                NUMBER_OF_MILLISECONDS_IN_A_MINUTE, NUMBER_OF_MILLISECONDS_IN_A_SECOND, 1)),
                new LinkedList<>(Arrays.asList("day", "hour", "minute", "second", "millisecond"))));
        return model;
    }

    /**
     * Gets memory stats.
     *
     * @param httpServletRequest  the http servlet request
     * @param httpServletResponse the http servlet response
     * @return the memory stats
     */
    @RequestMapping(value = "/getMemStats", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getMemoryStats(final HttpServletRequest httpServletRequest,
                                              final HttpServletResponse httpServletResponse) {
        final Map<String, Object> model = new HashMap<>();
        model.put("totalMemory", convertToMegaBytes(Runtime.getRuntime().totalMemory()));
        model.put("maxMemory", convertToMegaBytes(Runtime.getRuntime().maxMemory()));
        model.put("freeMemory", convertToMegaBytes(Runtime.getRuntime().freeMemory()));
        model.put("availableProcessors", Runtime.getRuntime().availableProcessors());
        return model;
    }

    /**
     * Gets ticket stats.
     *
     * @param httpServletRequest  the http servlet request
     * @param httpServletResponse the http servlet response
     * @return the ticket stats
     */
    @RequestMapping(value = "/getTicketStats", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getTicketStats(final HttpServletRequest httpServletRequest,
                                              final HttpServletResponse httpServletResponse) {
        final Map<String, Object> model = new HashMap<>();

        int unexpiredTgts = 0;
        int unexpiredSts = 0;
        int expiredTgts = 0;
        int expiredSts = 0;

        try {
            final Collection<Ticket> tickets = 
                    this.centralAuthenticationService.getTickets(Predicates.<Ticket>alwaysTrue());

            for (final Ticket ticket : tickets) {
                if (ticket instanceof ServiceTicket) {
                    if (ticket.isExpired()) {
                        expiredSts++;
                    } else {
                        unexpiredSts++;
                    }
                } else {
                    if (ticket.isExpired()) {
                        expiredTgts++;
                    } else {
                        unexpiredTgts++;
                    }
                }
            }
        } catch (final UnsupportedOperationException e) {
            logger.trace("The ticket registry doesn't support this information.");
        }

        model.put("unexpiredTgts", unexpiredTgts);
        model.put("unexpiredSts", unexpiredSts);
        model.put("expiredTgts", expiredTgts);
        model.put("expiredSts", expiredSts);
        model.put("casTicketSuffix", this.casTicketSuffix);
        return model;
    }
    
    
    /**
     * Handles the request.
     *
     * @param httpServletRequest the http servlet request
     * @param httpServletResponse the http servlet response
     * @return the model and view
     * @throws Exception the exception
     */
    @RequestMapping(method = RequestMethod.GET)
    protected ModelAndView handleRequestInternal(final HttpServletRequest httpServletRequest, 
                                                 final HttpServletResponse httpServletResponse)
                throws Exception {
        final ModelAndView modelAndView = new ModelAndView(MONITORING_VIEW_STATISTICS);
        modelAndView.addObject("pageTitle", modelAndView.getViewName());
        return modelAndView;
    }

    /**
     * Convert to megabytes from bytes.
     * @param bytes the total number of bytes
     * @return value converted to MB
     */
    private static double convertToMegaBytes(final double bytes) {
        return bytes / NUMBER_OF_BYTES_IN_A_KILOBYTE / NUMBER_OF_BYTES_IN_A_KILOBYTE;
    }
    
    /**
     * Calculates the up time.
     *
     * @param difference the difference
     * @param calculations the calculations
     * @param labels the labels
     * @return the uptime as a string.
     */
    protected String calculateUptime(final double difference, final Queue<Integer> calculations, 
                                     final Queue<String> labels) {
        if (calculations.isEmpty()) {
            return "";
        }

        final int value = calculations.remove();
        final double time = Math.floor(difference / value);
        final double newDifference = difference - time * value;
        final String currentLabel = labels.remove();
        final String label = time == 0 || time > 1 ? currentLabel + 's' : currentLabel;

        return Integer.toString((int) time) + ' ' + label + ' ' 
                + calculateUptime(newDifference, calculations, labels);
    }

    @Override
    public void setServletContext(final ServletContext servletContext) {
        servletContext.setAttribute(MetricsServlet.METRICS_REGISTRY, this.metricsRegistry);
        servletContext.setAttribute(MetricsServlet.SHOW_SAMPLES, Boolean.TRUE);
        servletContext.setAttribute(HealthCheckServlet.HEALTH_CHECK_REGISTRY, this.healthCheckRegistry);
    }
}
