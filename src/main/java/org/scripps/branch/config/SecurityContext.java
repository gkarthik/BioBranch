package org.scripps.branch.config;

import javax.sql.DataSource;

import org.scripps.branch.login.CustomLoginSuccessHandler;
import org.scripps.branch.repository.UserRepository;
import org.scripps.branch.service.SocialUserDetailsServices;
import org.scripps.branch.service.UserRepositoryDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.social.security.SpringSocialConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityContext extends WebSecurityConfigurerAdapter {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private UserRepository userRepository;

	/**
	 * Configures the authentication manager bean which processes authentication
	 * requests.
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder auth)
			throws Exception {
		auth.userDetailsService(userDetailsService()).passwordEncoder(
				passwordEncoder());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
		// Configures form login
		.formLogin()
				.loginPage("/login")
				.successHandler(successHandler())
				.loginProcessingUrl("/login")
				.failureUrl("/login?error=bad_credentials")
				// Configures the logout function
				.and()
				.logout()
				.deleteCookies("JSESSIONID",
						"SPRING_SECURITY_REMEMBER_ME_COOKIE")
				.logoutUrl("/logout")
				.invalidateHttpSession(true)
				.logoutSuccessUrl("/login")
				.and()
				.rememberMe()
				.key("uniqueSecret")
				.rememberMeServices(rememberMeServices())
				.tokenValiditySeconds(172800)
				// Configures url based authorization
				.and()
				.authorizeRequests()
				// Anyone can access the urls
				.antMatchers("/auth/**", "/login", "/signin/**", "/signup/**",
						"/user/register/**", "/profile", "/save",
						"/publicCollection", "/features", "/contact", "/MetaServer").permitAll()
				// The rest of the our application is protected.
				.antMatchers("/**").hasRole("USER").antMatchers("/new")
				.hasRole("USER")
				// Adds the SocialAuthenticationFilter to Spring Security's
				// filter chain.

				.and().apply(new SpringSocialConfigurer());
		// .and().csrf().disable();

	}
	
	@Bean
	public AuthenticationSuccessHandler successHandler() {
	    return new CustomLoginSuccessHandler();
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web
		// Spring Security ignores request to static resources such as CSS or JS
		// files.
		.ignoring().antMatchers("/static/**");
	}

	/**
	 * This is used to hash the password of the user.
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(10);
	}

	@Bean
	public PersistentTokenRepository persistentTokenRepository() {
		JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
		tokenRepository.setDataSource(dataSource);
		return tokenRepository;
	}

	@Bean
	public RememberMeServices rememberMeServices() {
		PersistentTokenBasedRememberMeServices rememberMeServices = new PersistentTokenBasedRememberMeServices(
				"uniqueSecret", userDetailsService(),
				persistentTokenRepository());
		rememberMeServices.setAlwaysRemember(true);
		return rememberMeServices;
	}

	/**
	 * This bean is used to load the user specific data when social sign in is
	 * used.
	 */
	@Bean
	public SocialUserDetailsService socialUserDetailsService() {
		return new SocialUserDetailsServices(userDetailsService());
	}

	/**
	 * This bean is load the user specific data when form login is used.
	 */
	@Override
	@Bean
	public UserDetailsService userDetailsService() {
		return new UserRepositoryDetailService(userRepository);
	}

}
