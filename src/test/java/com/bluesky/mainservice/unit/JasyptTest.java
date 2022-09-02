package com.bluesky.mainservice.unit;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Slf4j
class JasyptTest {

    @Test
    @DisplayName("암복호화 테스트")
    void test_encrypt_and_decrypt() {
        //given
        String password = "-HeD:WF!xtA&/xl2tyl6(x]%Zn`vE";
        String msg = "wTc%=M}D(C0h2z+=S5>t5E{5J)NAkX}$V:jPJ@Tc{?[95|N)Eoz)oQblD34IkG";

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
    }
}
