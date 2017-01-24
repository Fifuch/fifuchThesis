package pl.put.poznan.whereismymoney.security.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.put.poznan.whereismymoney.model.User;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

@Component
public class TokenCreator {
    private final static String ENCODING = "UTF-8";
    private MessageDigest messageDigest;
    
    @Autowired
    public TokenCreator(MessageDigest messageDigest) {
        this.messageDigest = messageDigest;
    }
    
    public String createToken(User user) {
        String token = "";
        if(user != null) {
            try {
                collectDataToDigest(user);
                token = createToken();
                messageDigest.reset();
            } catch (UnsupportedEncodingException e) {
                System.err.print(e.getMessage());
            }
        }
        return token;
    }
    
    private void collectDataToDigest(User user) throws UnsupportedEncodingException {
        messageDigest.update(user.getUsername().getBytes(ENCODING));
        messageDigest.update(user.getEmail().getBytes(ENCODING));
        messageDigest.update(user.getPassword());
    }
    
    private String createToken() throws UnsupportedEncodingException {
        byte[] digest = messageDigest.digest();
        return new String(digest, ENCODING);
    }
}
