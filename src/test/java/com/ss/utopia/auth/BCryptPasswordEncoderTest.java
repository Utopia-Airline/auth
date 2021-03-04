package com.ss.utopia.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BCryptPasswordEncoderTest {
  @Test
  @DisplayName("Generate BCryptPassword")
  void generateBCryptPassword() {
    BCryptPasswordEncoder b = new BCryptPasswordEncoder(11);
    System.out.println("Pass is: " + b.encode("pass"));
    assertEquals(true, b.matches("pass", "$2a$11$OFJttcoJOuobBnH67CVji.A//xzw108cHRfUeDmLvQPyk/kz70ULy"));
  }
}
