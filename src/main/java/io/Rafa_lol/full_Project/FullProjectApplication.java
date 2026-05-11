package io.Rafa_lol.full_Project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
///exclude = { SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class }
@SpringBootApplication()
public class FullProjectApplication {

	private static final int STRENGHT = 12;

	public static void main(String[] args) {

		SpringApplication.run(FullProjectApplication.class, args);
	}


	@Bean
	public BCryptPasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder(STRENGHT);
	}
}
