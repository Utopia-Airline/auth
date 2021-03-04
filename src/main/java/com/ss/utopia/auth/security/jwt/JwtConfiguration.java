package com.ss.utopia.auth.security.jwt;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfiguration {
  @Value("${security.jwt.token.private-key}")
  private String privateKey;
  @Value("${security.jwt.token.public-key}")
  private String publicKey;

  private PrivateKey generatePrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
    final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    privateKey = parseKey(privateKey);
    final byte[] decodedPrivate = Base64.getDecoder().decode(privateKey);
    final KeySpec keySpec = new PKCS8EncodedKeySpec(decodedPrivate);
    return keyFactory.generatePrivate(keySpec);
  }

  private PublicKey generatePublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
    final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    publicKey = parseKey(publicKey);
    final byte[] decodedPublic = Base64.getDecoder().decode(publicKey);
    final KeySpec keySpec = new X509EncodedKeySpec(decodedPublic);
    return keyFactory.generatePublic(keySpec);
  }

  @Bean
  public KeyPair jwtKeyPair() {
    try {
      return new KeyPair(generatePublicKey(), generatePrivateKey());
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      return null;
    }
  }

  /**
   *
   * @param key
   * @return Clean a public/private key string that is saved as env from \n and BEGIN, END
   */
  private String parseKey(String key) {
    return key.replaceAll("-----BEGIN (.*?)-----", "").replaceAll("-----END (.*)----", "")
      .replaceAll("\r\n", "").replaceAll("\\\\n", "").replaceAll("\n", "").trim();
  }
}
