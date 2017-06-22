package edu.hm.cs.projektstudium.findlunch.webapp.config;

import edu.hm.cs.projektstudium.findlunch.webapp.security.ConsumerUserDetailsService;
import edu.hm.cs.projektstudium.findlunch.webapp.security.CsrfAccessDeniedHandler;
import edu.hm.cs.projektstudium.findlunch.webapp.security.RestaurantUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.header.writers.StaticHeadersWriter;

/**
 * This class is responsible for configuring the Spring Security context.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	
	
	/**
	 * Class for configuring the stateless security configuration (REST). The
	 * filter chain is executed before the stateful security chain. How the urls
	 * are secured is defined on a method level using the method security
	 * annotations.
	 */
	@Configuration
	@Order(1)
	@EnableGlobalMethodSecurity(prePostEnabled = true)
	public static class StatelessApiSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

		/** Userdetailsserice to get consumers for authentication */
		@Autowired
		@Qualifier("consumerUserDetailsService")
		private ConsumerUserDetailsService consumerUserDetailsService;

		/** The password encoder. */
		@Autowired
		private BCryptPasswordEncoder passwordEncoder;

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.springframework.security.config.annotation.web.configuration.
		 * WebSecurityConfigurerAdapter#configure(org.springframework.security.
		 * config.annotation.authentication.builders.
		 * AuthenticationManagerBuilder)
		 */
		/** Configures where to look for users during authentication process **/
		@Override
		public void configure(AuthenticationManagerBuilder auth) throws Exception {

			// The framework should not erase potential sensitive data from an object.
			auth.eraseCredentials(false);

			auth.userDetailsService(consumerUserDetailsService).passwordEncoder(passwordEncoder);

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.springframework.security.config.annotation.web.configuration.
		 * WebSecurityConfigurerAdapter#configure(org.springframework.security.
		 * config.annotation.web.builders.HttpSecurity)
		 */
		/**
		 * Configures which urls are protected by this configuration and that no
		 * session management and basic authentication should be used
		 **/
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			
			http
					// Change the sent server name.
					.headers().addHeaderWriter(new StaticHeadersWriter("Server", "Unbekannter Webserver")).and()
					// Add an elementary Content-Security-Policy-Report-Only-header with a reporting URL.
					.headers().addHeaderWriter(new StaticHeadersWriter("Content-Security-Policy-Report-Only",
					"default-src 'self' script-src 'self' 'unsafe-inline' " +
							"https://www.google.com/recaptcha/ https://www.gstatic.com/recaptcha/ ;" +
							"; report-uri /api/csp-report-uri"+ "; /js/**"))
					.and()
					.csrf().disable().requestMatchers()
					// Add a Content-Security-Policy-violation-report-endpoint
					// The CSRF-protection should be disabled as it is a POST-request.
					// Otherwise a CSRF-token exception will be sent.
					.antMatchers(HttpMethod.POST, "/api/csp-report-uri")
					.antMatchers(HttpMethod.POST, "/api/register_user")
					.antMatchers(HttpMethod.PUT, "/api/submitToken/**")
					.antMatchers(HttpMethod.GET, "/api/login_user")
					.antMatchers(HttpMethod.POST, "/api/register_push")
					.antMatchers(HttpMethod.GET, "/api/get_push")
					.antMatchers(HttpMethod.DELETE, "/api/unregister_push/**")
					.antMatchers(HttpMethod.PUT, "/api/register_favorite/**")
					.antMatchers(HttpMethod.DELETE, "/api/unregister_favorite/**")
					.antMatchers(HttpMethod.GET, "/api/restaurants")
					.antMatchers(HttpMethod.POST, "/api/register_reservation")
					.antMatchers(HttpMethod.PUT, "/api/confirm_reservation/**")
					.antMatchers(HttpMethod.GET, "/api/get_points")
					.antMatchers(HttpMethod.GET, "/api/get_points_restaurant/**")
					.antMatchers(HttpMethod.POST, "/api/get_reset_token")
					.antMatchers(HttpMethod.PUT, "/api/reset_password/**")
					.antMatchers(HttpMethod.GET, "/api/getCustomerReservations")
				.and().httpBasic().and().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		}
		
	}


	/**
	 * Class for configuring the stateful security configuration (websize). The
	 * filter chain is executed after the stateless security chain.
	 */
	@Configuration
	@Order(2)
	public static class StatefulLoginWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

		/** Userdetailsserice to get restaurant users for authentication */
		@Autowired
		@Qualifier("restaurantUserDetailsService")
		private RestaurantUserDetailsService restaurantUserDetailsService;
	
		/** The password encoder. */
		@Autowired
		private BCryptPasswordEncoder passwordEncoder;

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.springframework.security.config.annotation.web.configuration.
		 * WebSecurityConfigurerAdapter#configure(org.springframework.security.
		 * config.annotation.authentication.builders.
		 * AuthenticationManagerBuilder)
		 */
		/** Configures where to look for users during authentication process **/
		@Override
		public void configure(AuthenticationManagerBuilder auth) throws Exception {

			// The framework should not erase potential sensitive data from an object.
			auth.eraseCredentials(false);

			auth.userDetailsService(restaurantUserDetailsService).passwordEncoder(passwordEncoder);

			
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.springframework.security.config.annotation.web.configuration.
		 * WebSecurityConfigurerAdapter#configure(org.springframework.security.
		 * config.annotation.web.builders.HttpSecurity)
		 */
		/**
		 * Configures which urls are protected by this configuration. A login
		 * page should be used, using a formlogin with the default Spring
		 * Security login entrypoints
		 **/
		
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http
					.authorizeRequests().antMatchers("https://localhost:8443/**").permitAll().and()
					// Change the sent server name.
					.headers().addHeaderWriter(new StaticHeadersWriter("Server", "Unbekannter Webserver")).and()
					// Add an elementary Content-Security-Policy-header with a reporting URL.
					// (Google.com has to be added as reCaptcha is loaded from their website(s).)
					// See: https://developers.google.com/recaptcha/docs/faq#im-using-content-security-policy-csp-on-my-website-how-can-i-configure-it-to-work-with-recaptcha
					.headers().addHeaderWriter(new StaticHeadersWriter("Content-Security-Policy",
							"default-src 'self' data:; script-src 'self' 'unsafe-inline' " +
									"https://www.google.com/recaptcha/ https://www.gstatic.com/recaptcha/ ;" +
									"; /js/**"+"child-src https://www.google.com/recaptcha/;"+
									"style-src 'self' data: 'unsafe-inline'" +
									"; report-uri /api/csp-report-uri")).and()
					// A custom AccessDeniedHandler in order to handle CSRF-attacks.
					.exceptionHandling().accessDeniedHandler(new CsrfAccessDeniedHandler()).and()
					.authorizeRequests()
					.antMatchers("/", "/login", "/home", "/register", "/privacy", "/terms", "/faq_customer",
							"/faq_restaurant", "/about_findlunch", "/css/**", "/api/**", "/js/**", "/fonts/**",
							"/images/**", "/course_type/**", "coursetype/**","/resetpassword/**")
					.permitAll()
					.antMatchers("/booking/**").hasAuthority("Betreiber")
					.anyRequest().authenticated().and().formLogin().loginPage("/login").permitAll().and()
					.logout();
			// The CSRF-protection should be enabled.
			//.and().csrf().disable();

		}
		
	}
	
}
