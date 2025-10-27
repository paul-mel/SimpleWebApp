package com.learning.springboot.webapplication.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SecurityConfigurationTest {

    private static final String TEST_USERNAME= "test";

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void contextLoads() {
        assertNotNull(userDetailsService);
        assertNotNull(passwordEncoder);
    }

    @Test
    void testUserExistsInUserDetailsManager() {
        UserDetails user = userDetailsService.loadUserByUsername(TEST_USERNAME);
        assertNotNull(user);
        assertEquals(TEST_USERNAME, user.getUsername());
    }

    @Test
    void testPasswordIsEncoded() {
        UserDetails user = userDetailsService.loadUserByUsername(TEST_USERNAME);
        String encodedPassword = user.getPassword();

        assertNotEquals("test1", encodedPassword);
        assertTrue(passwordEncoder.matches("test1", encodedPassword));
    }

    @Test
    void testUserHasExpectedRoles() {
        UserDetails user = userDetailsService.loadUserByUsername(TEST_USERNAME);
        var authorities = user.getAuthorities();

        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }
}
