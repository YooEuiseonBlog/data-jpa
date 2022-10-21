package study.datajpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Optional;
import java.util.UUID;

@EnableJpaAuditing
@SpringBootApplication
//@EnableJpaRepositories(basePackages = "study.datajpa.repository") // spring boot 사용시 생략 가능
public class DataJpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataJpaApplication.class, args);
	}

	@Bean
	public AuditorAware<String> auditorProvider() {

		/*return new AuditorAware<String>() {
			@Override
			public Optional<String> getCurrentAuditor() {
				return Optional.of(UUID.randomUUID().toString());
			}*/
		return () -> Optional.of(UUID.randomUUID().toString());
	}

}
