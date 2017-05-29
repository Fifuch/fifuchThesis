package pl.put.poznan.thesis.service;

import pl.put.poznan.thesis.dao.UserDao;
import pl.put.poznan.thesis.model.User;
import pl.put.poznan.thesis.security.SessionManager;

import javax.inject.Inject;
import java.util.Arrays;

public class UserSettingsService {
    private SessionManager sessionManager;
    private UserDao userDao;

    @Inject
    public UserSettingsService(SessionManager sessionManager, UserDao userDao) {
        this.sessionManager = sessionManager;
        this.userDao = userDao;
    }

    public String getCurrentLogin() {
        User user = sessionManager.getUserData();
        if (user != null) {
            return user.getUsername();
        }

        return "-";
    }

    public String getCurrentMail() {
        User user = sessionManager.getUserData();
        if (user != null) {
            return user.getEmail();
        }
        return "-";
    }

    public boolean updateUserData(String login, String email, String oldPassword, String newPassword) {
        boolean operationSuccessful = true;
        User user = sessionManager.getUserData();
        if (isAnyFieldIncorrectlyFilled(login, email, oldPassword, user)) {
            operationSuccessful = false;
        }
        User newUser = null;
        if (operationSuccessful) {
            newUser = new User(login, email, sessionManager.generatePasswordDigest(newPassword, user.getSalt()));
            newUser.setSalt(user.getSalt());
            newUser.setId(user.getId());
            operationSuccessful = userDao.update(newUser);
        }
        if(operationSuccessful) {
            sessionManager.setUserData(newUser);
            sessionManager.generateSessionKey();
        }
        return operationSuccessful;
    }

    private boolean isAnyFieldIncorrectlyFilled(String login, String email, String oldPassword, User user) {
        return login == null || login.equals("") || email == null || email.equals("")
                || !Arrays.equals(sessionManager.generatePasswordDigest(oldPassword, user.getSalt()), user.getPassword());
    }
}
