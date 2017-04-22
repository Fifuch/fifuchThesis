package pl.put.poznan.whereismymoney;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@SpringBootApplication
public class WhereIsMyMoneyWebappApplication {

    public static void main(String[] args) {
        SpringApplication.run(WhereIsMyMoneyWebappApplication.class, args);
    }

    @Bean
    public MessageDigest getMessageDigest() throws NoSuchAlgorithmException {
        return MessageDigest.getInstance("SHA-256");
    }
}
