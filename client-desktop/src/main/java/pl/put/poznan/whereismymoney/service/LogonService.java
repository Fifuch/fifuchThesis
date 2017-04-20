package pl.put.poznan.whereismymoney.service;

import pl.put.poznan.whereismymoney.model.User;
import pl.put.poznan.whereismymoney.security.SessionManager;

import javax.inject.Inject;

public class SignInService {
    private SessionManager sessionManager;

    @Inject
    public SignInService(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public boolean performSignIn(String username, String password) {
        byte[] passwordDigest = sessionManager.digestPassword(password);
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordDigest);
        sessionManager.setUserData(user);
        long id = validate(sessionManager.getSessionKey(), username);
        if (id >= 0) {
            user.setId(id);
            return true;
        } else {
            return false;
        }
    }

    private long validate(byte[] sessionKey, String username) {
        return 0;
    }
}
