package com.gusdev.library_app;

import com.gusdev.library_app.entities.User;
import com.gusdev.library_app.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class LibraryAppApplication {

	@Autowired
	private UserRepository userRepository;

	public static void main(String[] args) {
		SpringApplication.run(LibraryAppApplication.class, args);
	}

	@Bean
	public CommandLineRunner demo(UserRepository userRepository) {
		return (args -> {
			User user= new User();
			user.setName("Gustavo");
			user.setSurname("Starace");
			user.setEmail("practicodecocina@gmail.com");
			user.setIsAdmin(true);

			userRepository.save(user);
			System.out.println("Usuario guardado " + user);
					});
	}
}



