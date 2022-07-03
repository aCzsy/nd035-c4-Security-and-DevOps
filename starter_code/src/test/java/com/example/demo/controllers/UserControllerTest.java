package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UserControllerTest {
    private UserController userController;
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setup(){
        userController = new UserController();
        TestUtils.injectObjects(userController,"userRepository",userRepository);
        TestUtils.injectObjects(userController,"cartRepository",cartRepository);
        TestUtils.injectObjects(userController,"bCryptPasswordEncoder",encoder);

    }

    @Test
    public void createUserHappyPath(){
        when(encoder.encode("testPassword")).thenReturn("thisIsHashed");
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("testPassword");
        r.setConfirmPassword("testPassword");

        ResponseEntity<User> response = userController.createUser(r);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User u = response.getBody();
        CharSequence charSequence = "testPassword";
        //String hashedPassword = encoder.encode(charSequence);

        assertNotNull(u);
        assertEquals(0, u.getId());
        assertEquals("test",u.getUsername());
        assertEquals("thisIsHashed",u.getPassword());

        verify(encoder).encode("testPassword");
    }

    @Test
    public void userCreateTestShortPassword(){
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername("johnDoe");
        userRequest.setPassword("pass");
        userRequest.setConfirmPassword("pass");

        ResponseEntity<User> responseEntity = userController.createUser(userRequest);
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void userCreateTestPasswordsDontMatch(){
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername("johnDoe");
        userRequest.setPassword("password");
        userRequest.setConfirmPassword("password1");

        ResponseEntity<User> responseEntity = userController.createUser(userRequest);
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void findByUsernameHappyPath(){
        when(encoder.encode("testPassword")).thenReturn("thisIsHashed");
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("testPassword");
        r.setConfirmPassword("testPassword");

        ResponseEntity<User> response = userController.createUser(r);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User u = response.getBody();
        //String hashedPassword = encoder.encode("testPassword");

        assertNotNull(u);
        assertEquals(0, u.getId());
        assertEquals("test",u.getUsername());
        assertEquals("thisIsHashed",u.getPassword());

        when(userRepository.findByUsername(u.getUsername())).thenReturn(u);
        //User userr = userRepository.findByUsername("test");
        ResponseEntity<User> foundUser = userController.findByUserName(u.getUsername());
        User u2 = foundUser.getBody();
        assertEquals(200, foundUser.getStatusCodeValue());
        assertNotNull(u2);
        assertEquals("test", u2.getUsername());

        Mockito.verify(encoder).encode("testPassword");
        Mockito.verify(userRepository).findByUsername(u.getUsername());

    }

    @Test
    public void findByUsernameDoesntExist(){
        ResponseEntity<User> foundUser = userController.findByUserName("noname");

        assertEquals(HttpStatus.NOT_FOUND, foundUser.getStatusCode());
    }

    @Test
    public void findByIdHappyPath(){
        when(encoder.encode("testPassword")).thenReturn("thisIsHashed");
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("testPassword");
        r.setConfirmPassword("testPassword");

        ResponseEntity<User> response = userController.createUser(r);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User u = response.getBody();
        //String hashedPassword = encoder.encode("testPassword");

        assertNotNull(u);
        assertEquals(0, u.getId());
        assertEquals("test",u.getUsername());
        assertEquals("thisIsHashed",u.getPassword());

        when(userRepository.findById(u.getId())).thenReturn(Optional.of(u));
        ResponseEntity<User> foundUser = userController.findById(u.getId());
        User u2 = foundUser.getBody();
        assertEquals(200, foundUser.getStatusCodeValue());
        assertNotNull(u2);
        assertEquals("test", u2.getUsername());

        Mockito.verify(encoder).encode("testPassword");
        Mockito.verify(userRepository).findById(u.getId());
    }

    @Test
    public void findByIdDoesntExist(){
        ResponseEntity<User> foundUser = userController.findById(-1L);
        assertEquals(HttpStatus.NOT_FOUND, foundUser.getStatusCode());
    }
}
