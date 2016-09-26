package ufc.quixada.npi.contest.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import ufc.quixada.npi.contest.model.StorageProperties;
import ufc.quixada.npi.contest.service.StorageService;
import org.springframework.context.annotation.Bean;

import org.springframework.boot.CommandLineRunner;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@ComponentScan({"br.ufc.quixada.npi.ldap", "ufc.quixada.npi.contest"})
@EntityScan(basePackages="ufc.quixada.npi.contest.model")
@EnableJpaRepositories("ufc.quixada.npi.contest.repository")
@EnableConfigurationProperties(StorageProperties.class)
public class ContestApplication extends SpringBootServletInitializer {
	private static Class<ContestApplication> applicationClass = ContestApplication.class;

	public static void main(String[] args) {
		SpringApplication.run(applicationClass, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(applicationClass);
	}
	
	@Bean
	CommandLineRunner init(StorageService storageService) {
		return (args) -> {
            storageService.deleteAll();
            storageService.init();
		};
	}
}
