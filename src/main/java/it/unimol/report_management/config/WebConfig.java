package it.unimol.report_management.config;

import it.unimol.report_management.web.filter.CorrelationIdFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Registrazione dei filtri servlet. */
@Configuration
public class WebConfig {

    @Bean
    public FilterRegistrationBean<CorrelationIdFilter> correlationIdFilter() {
        FilterRegistrationBean<CorrelationIdFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(new CorrelationIdFilter());
        reg.setOrder(1); // esegui per primo
        reg.addUrlPatterns("/*");
        return reg;
    }
}
