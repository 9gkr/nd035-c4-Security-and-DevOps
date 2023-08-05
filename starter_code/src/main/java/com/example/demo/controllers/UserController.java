package com.example.demo.controllers;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;


import static org.slf4j.LoggerFactory.getLogger;

@RestController
@RequestMapping("/api/user")

public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	private final static Logger log = getLogger(UserController.class);

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		if (!userRepository.findById(id).isPresent()){
			// add log for null user
			log.error("UserController: Failed to find user with id {}", id);
		}
		return ResponseEntity.of(userRepository.findById(id));
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		if (user == null){
			// add log for null user
			log.error("UserController: Failed to find user with username {}", username);
		}
		return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
		User user = new User();
		user.setUsername(createUserRequest.getUsername());
		Cart cart = new Cart();
		cartRepository.save(cart);
		user.setCart(cart);

		// make sure that the password length is at least 7 and password equals to confirmPassword
		if (createUserRequest.getPassword().length() < 7
				|| !createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())){
			// add log for create user request failures and exceptions
			log.error("UserController: Failed to set password for {}", createUserRequest.getUsername());
			log.info("UserController: Create user failure");
			return ResponseEntity.badRequest().build();
		}
		user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));

		try{
            userRepository.save(user);
        }
		// catch exception if username unique constraint is violated
        catch (DataIntegrityViolationException e){
			// add log for CreateUser request failure
			log.error("UserController: Failed to set unique username for {}", createUserRequest.getUsername());
			log.error("UserController: Create user failure");
            System.out.println(e.getMessage());
			return ResponseEntity.badRequest().build();
        }
		// add log for CreateUser request successes
		log.info("UserController: Create user success");
		return ResponseEntity.ok(user);
	}
}
