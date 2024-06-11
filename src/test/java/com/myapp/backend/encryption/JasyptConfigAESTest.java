package com.myapp.backend.encryption;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.iv.RandomIvGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class JasyptConfigAESTest {

    @BeforeEach
    void before() {
        System.getProperties().setProperty("jasypt.encryptor.key", "MyApplicationKey");
    }

    @Test
    void stringEncryptor() {
        StandardPBEStringEncryptor encryptor = jasyptEncryptor();

        String url = "jdbc:mysql://localhost:3306/testdb?serverTimezone=Asia/Seoul";
        String username = "root";
        String password = "1234";

        assertThat(encryptor.decrypt(encryptor.encrypt(url))).isEqualTo(url);
        assertThat(encryptor.decrypt(encryptor.encrypt(username))).isEqualTo(username);
        assertThat(encryptor.decrypt(encryptor.encrypt(password))).isEqualTo(password);
    }

    private StandardPBEStringEncryptor jasyptEncryptor() {
        StandardPBEStringEncryptor pbeEnc = new StandardPBEStringEncryptor();
        pbeEnc.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
        pbeEnc.setPassword(System.getProperty("jasypt.encryptor.key"));
        pbeEnc.setIvGenerator(new RandomIvGenerator());

        return pbeEnc;
    }
}