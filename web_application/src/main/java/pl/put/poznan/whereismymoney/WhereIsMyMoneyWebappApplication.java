package pl.put.poznan.whereismymoney;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import pl.put.poznan.whereismymoney.crypto.RSAKeyManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@SpringBootApplication
public class WhereIsMyMoneyWebappApplication {

    public static void main(String[] args) {
        if(args.length>0){
            if(args[0].equals("-generate")){
                generateRSAKeys();
            }
        }
        SpringApplication.run(WhereIsMyMoneyWebappApplication.class, args);
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
