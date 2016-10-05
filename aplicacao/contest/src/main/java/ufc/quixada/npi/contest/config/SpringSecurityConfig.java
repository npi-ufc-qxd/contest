package ufc.quixada.npi.contest.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	@Qualifier("authenticationProviderContest")
	private AuthenticationProvider provider;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/css/**", "/js/**", "/img/**", "/webjars/**").permitAll()
				.antMatchers("/autor/**").hasRole("DISCENTE")
				.antMatchers("/evento/**").hasRole("ADMIN")
				.antMatchers("/eventoOrganizador/**").hasAnyRole("DOCENTE", "STA")
				.antMatchers("/revisor/**").hasRole("DOCENTE")
				.anyRequest()
				.fullyAuthenticated()
			.and()
				.formLogin()
				.loginPage("/login")
				.loginProcessingUrl("/login")
				.defaultSuccessUrl("/")
				.failureUrl("/loginfailed")
				.usernameParameter("username")
				.passwordParameter("password")
				.permitAll()
			.and()
				.logout()
				.logoutSuccessUrl("/login")
				.logoutUrl("/logout")
				.invalidateHttpSession(true)
				.permitAll()
			.and()
				.exceptionHandling().accessDeniedPage("/403");
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(provider);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
	    return new BCryptPasswordEncoder();
	}
	
}