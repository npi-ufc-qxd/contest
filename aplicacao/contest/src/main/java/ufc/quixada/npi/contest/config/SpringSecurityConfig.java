package ufc.quixada.npi.contest.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;


@Configuration
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	@Qualifier("authenticationProviderContest")
	private AuthenticationProvider provider;
	
	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
		        .antMatchers("/cadastro*", "/dashboard", "/resetarSenha", "/esqueciMinhaSenha").hasRole("USER")
		       // .antMatchers("/resetarSenha", "/esqueciMinhaSenha").permitAll()
				.antMatchers("/css/**", "/js/**", "/img/**", "/webjars/**").permitAll()
				.antMatchers("/autor/file/**").permitAll()
				.antMatchers("/autor/**").hasRole("USER")
				.antMatchers("/evento/**").hasRole("ADMIN")
				.antMatchers("/secao/**").hasRole("USER")
				.antMatchers("/eventoOrganizador/**").hasRole("USER")
				.antMatchers("/revisor/**").hasRole("USER")
				.anyRequest()
				.fullyAuthenticated()
			.and()
				.formLogin()
				.loginPage("/login")
				.loginProcessingUrl("/login")
				.successHandler(new AuthenticationSuccessHandler() {
					
					@Override
					public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
							throws IOException, ServletException {
						
						String role = authentication.getAuthorities().toString();
						
						
						if(role.contains("ADMIN")){
							redirectStrategy.sendRedirect(request, response, "/evento/ativos");
						} else{
							redirectStrategy.sendRedirect(request, response, "/dashboard");
						}
						
					}
				})
				.failureUrl("/loginfailed")
				.usernameParameter("username")
				.passwordParameter("password")
				.permitAll()
			.and()
				.logout()
				.logoutSuccessUrl("/login")
				.logoutUrl("/logout")
				.invalidateHttpSession(true)
				.permitAll();
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