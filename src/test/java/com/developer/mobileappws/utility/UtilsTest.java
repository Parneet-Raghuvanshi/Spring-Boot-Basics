package com.developer.mobileappws.utility;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UtilsTest {

    @Autowired
    Utils utils;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    final void testGenerateUserId() {
        String userId1 = utils.generateUserId(30);
        String userId2 = utils.generateUserId(30);

        assertNotNull(userId1);
        assertNotNull(userId2);

        assertEquals(30, userId1.length());
        assertEquals(30, userId2.length());
        assertFalse(userId1.equalsIgnoreCase(userId2));
    }

    @Test
    final void testHasTokenNotExpired() {
        String token = utils.generateEmailVerificationToken("eab418fg15");
        assertNotNull(token);

        boolean hasTokenExpired = Utils.hasTokenExpired(token);
        assertFalse(hasTokenExpired);
    }

    @Test
    final void testHasTokenExpired() {
        String expiredToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJwYXJuZWV0cmFnaHV2YW5zaGlAZ21haWwuY29tIiwiZXhwIjoxNjU2ODM5ODc2fQ.g5kklSKmpOueMPKUOQz3LLhipVHx79Kt9E4zi3BeLeo";

        boolean hasTokenExpired = Utils.hasTokenExpired(expiredToken);
        assertTrue(hasTokenExpired);
    }
}
