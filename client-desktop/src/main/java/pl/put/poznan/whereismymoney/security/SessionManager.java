package pl.put.poznan.whereismymoney.security;

import pl.put.poznan.whereismymoney.model.User;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

public class SessionManager {
    private User userData;
    private MessageDigest messageDigest;
    private byte[] sessionKey;

    @Inject
    public SessionManager(MessageDigest messageDigest) {
        this.messageDigest = messageDigest;
        sessionKey = new byte[0];
    }

    public User getUserData() {
        return userData;
    }

    public void generateSessionKey() {
        try {
            messageDigest.update(userData.getUsername().getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            messageDigest.update(userData.getUsername().getBytes());
        }
        messageDigest.update(userData.getPassword());
        sessionKey = messageDigest.digest();
    }

    public byte[] generatePasswordDigest(String password, byte[] salt) {
        byte[] passwordDigest = new byte[0];
        if (salt.length > 0) {
            try {
                messageDigest.update(password.getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                messageDigest.update(password.getBytes());
            }
            messageDigest.update(salt);
            passwordDigest = messageDigest.digest();
        }
        return passwordDigest;
    }

    public byte[] getSessionKey() {
        return sessionKey;
    }

    public void setUserData(User userData) {
        this.userData = userData;
    }
}
