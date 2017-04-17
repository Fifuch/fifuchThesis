package pl.put.poznan.whereismymoney.security;

import pl.put.poznan.whereismymoney.model.User;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.time.LocalDate;

public class SessionManager {
    private User userData;
    private MessageDigest sessionKeyProvider;

    @Inject
    public SessionManager(MessageDigest sessionKeyProvider) {
        this.sessionKeyProvider = sessionKeyProvider;
    }

    public User getUserData() {
        return userData;
    }

    public byte[] getSessionKey() {
        sessionKeyProvider.update(userData.getUsername().getBytes());
        sessionKeyProvider.update(userData.getPassword());
        sessionKeyProvider.update((LocalDate.now().toString().getBytes()));
        return sessionKeyProvider.digest();
    }

    public byte[] digestPassword(String password) {
        byte[] result;
        try {
            result = sessionKeyProvider.digest(password.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            System.err.println("ERROR: UTF-8 Not supported.");
            result = sessionKeyProvider.digest(password.getBytes());
        }
        return result;
    }

    public void setUserData(User userData) {
        this.userData = userData;
    }

}
