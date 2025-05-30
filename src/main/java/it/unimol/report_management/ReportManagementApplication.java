package it.unimol.report_management;

import it.unimol.report_management.security.JwtAuthenticationFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ReportManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReportManagementApplication.class, args);
	}

	@Bean
	public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilter(JwtAuthenticationFilter filter) {
		FilterRegistrationBean<JwtAuthenticationFilter> registration = new FilterRegistrationBean<>();
		registration.setFilter(filter);
		registration.addUrlPatterns("/api/*"); // applica il filtro alle rotte /api
		return registration;
	}
}