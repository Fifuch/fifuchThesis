package pl.put.poznan.whereismymoney.service;

import pl.put.poznan.whereismymoney.dao.UserDao;
import pl.put.poznan.whereismymoney.model.User;
import pl.put.poznan.whereismymoney.security.SessionManager;

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
        if (operationSuccessful) {
            user.setPassword(sessionManager.digestPassword(newPassword));
            user.setEmail(email);
            user.setUsername(login);
            userDao.saveOrUpdate(user);
        }
        return operationSuccessful;
    }

    private boolean isAnyFieldIncorrectlyFilled(String login, String email, String oldPassword, User user) {
        return login == null || login.equals("") || userDao.getByUsername(login) != null
                || email == null || email.equals("") || userDao.getByEmail(email) != null
                || !Arrays.equals(sessionManager.digestPassword(oldPassword), user.getPassword());
    }
}
