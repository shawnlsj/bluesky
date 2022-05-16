package com.bluesky.mainservice;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.jupiter.api.Test;

@Slf4j
public class JasyptTest {

    @Test
    void EncryptorTest() {
        //given
        String password = "My Awesome Password";
        String msg = "Hello World";

        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(password);
        encryptor.setAlgorithm("PBEWithMD5AndDES");

        StandardPBEStringEncryptor decryptor = new StandardPBEStringEncryptor();
        decryptor.setPassword(password);
        decryptor.setAlgorithm("PBEWithMD5AndDES");

        //when
        String encryptedMsg = encryptor.encrypt(msg);
        String decryptedMsg = decryptor.decrypt(encryptedMsg);

        //then
        Assertions.assertThat(decryptedMsg).isEqualTo(msg);
        log.debug("msg : {}, decryptedMsg : {}", msg, decryptedMsg);
    }
}
