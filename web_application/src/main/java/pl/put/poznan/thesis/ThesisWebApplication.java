package pl.put.poznan.thesis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import pl.put.poznan.thesis.crypto.RSAKeyManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@SpringBootApplication
public class ThesisWebApplication {

    public static void main(String[] args) {
        if(args.length>0){
            if(args[0].equals("-generate")){
                generateRSAKeys();
            }
        }
        SpringApplication.run(ThesisWebApplication.class, args);
    }

    @Bean
    public MessageDigest getMessageDigest() throws NoSuchAlgorithmException {
        return MessageDigest.getInstance("SHA-256");
    }

    private static void generateRSAKeys(){
        RSAKeyManager rsaKeyManager = new RSAKeyManager();
        rsaKeyManager.generateRSAKeys();
        rsaKeyManager.SaveRSAKeysToFiles();
    }
}
