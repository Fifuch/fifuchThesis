package pl.put.poznan.whereismymoney.security.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.put.poznan.whereismymoney.model.User;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

@Component
public class SessionKeyGenerator {
    private final static String ENCODING = "UTF-8";
    private MessageDigest messageDigest;
    
    @Autowired
    public SessionKeyGenerator(MessageDigest messageDigest) {
        this.messageDigest = messageDigest;
    }
    
    public byte[] generate(User user) {
        byte[] token = new byte[0];
        if(user != null) {
            try {
                messageDigest.update(user.getUsername().getBytes(ENCODING));
            } catch (UnsupportedEncodingException e) {
                messageDigest.update(user.getUsername().getBytes());
            }
            messageDigest.update(user.getPassword());
            token = messageDigest.digest();
        }
        return token;
    }
}
