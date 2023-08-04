package com.example.demo;

import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    @Before
    // the code of this method is from Udacity Java Web Developer Nanodegree
    public void setup(){
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);
    }

    @Test
    // the code of this method is from Udacity Java Web Developer Nanodegree
    public void create_user_happy_path() throws Exception {
        // stubbing
        when(bCryptPasswordEncoder.encode("testPassword")).thenReturn("thisIsHashed");

        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("test");
        request.setPassword("testPassword");
        request.setConfirmPassword("testPassword");

        ResponseEntity<User> response = userController.createUser(request);
        // first check the response is not null
        assertNotNull(response);
        // then check the status code is 200
        assertEquals(200, response.getStatusCodeValue());

        User user = response.getBody();
        // first check the response body (which is user) is not null
        assertNotNull(user);
        // then check the info (id, username, password) is the same
        assertEquals(0, user.getId());
        assertEquals("test", user.getUsername());
        assertEquals("thisIsHashed", user.getPassword());
    }

    @Test
    // this is to test createUser method when password length is less than 7
    public void passwordLess7Test(){
        when(bCryptPasswordEncoder.encode("1234")).thenReturn("hash");

        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("test");
        request.setPassword("1234");
        request.setConfirmPassword("1234");

        ResponseEntity<User> response = userController.createUser(request);
        // first check the response is not null
        assertNotNull(response);
        // then check the status code is bad request (400)
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    // this is to test createUser method when password and confirmpassword are different
    public void passwordConfirmpasswordNotMatchTest(){
        when(bCryptPasswordEncoder.encode("testPassword")).thenReturn("hashedTestPassword");
        when(bCryptPasswordEncoder.encode("testPass")).thenReturn("hashedTestPass");

        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("test");
        request.setPassword("testPassword");
        request.setConfirmPassword("testPass");

        ResponseEntity<User> response = userController.createUser(request);
        // first check the response is not null
        assertNotNull(response);
        // then check the status code is bad request (400)
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    // this is to test findByUserName method in standard case
    public void findByNameTest(){
        // stubbing
        User test = new User();
        test.setId(0L);
        test.setUsername("test");
        test.setPassword("thisIsHashed");
        when(userRepository.findByUsername("test")).thenReturn(test);

        ResponseEntity<User> response = userController.findByUserName("test");
        // first check if response not null
        assertNotNull(response);
        // check if response is successful
        assertEquals(200, response.getStatusCodeValue());

        // first check if user not null
        User user = response.getBody();
        assertNotNull(user);
        // then check if the correct user is fetched
        assertEquals(0L, user.getId());
        assertEquals("test", user.getUsername());
        assertEquals("thisIsHashed", user.getPassword());
    }

    @Test
    // this is to test findByUserName when user is null
    public void findByNameTestNullUser(){
        User testUser = null;
        when(userRepository.findByUsername("test")).thenReturn(testUser);

        ResponseEntity<User> response = userController.findByUserName("test");
        // first check if response not null
        assertNotNull(response);
        // check if response is not found (404)
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    // this is to test findById method
    public void findByIdTest(){
        // stubbing
        User test = new User();
        test.setId(0L);
        test.setUsername("test");
        test.setPassword("thisIsHashed");
        when(userRepository.findById(0L)).thenReturn(Optional.of(test));

        ResponseEntity<User> response = userController.findById(0L);
        // first check if response not null
        assertNotNull(response);
        // check if response is successful
        assertEquals(200, response.getStatusCodeValue());

        // first check if user not null
        User user = response.getBody();
        assertNotNull(user);
        // then check if the correct user is fetched
        assertEquals(0L, user.getId());
        assertEquals("test", user.getUsername());
        assertEquals("thisIsHashed", user.getPassword());
    }
}
