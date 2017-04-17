package pl.put.poznan.whereismymoney.service;

import pl.put.poznan.whereismymoney.dao.UserDao;
import pl.put.poznan.whereismymoney.model.User;
import pl.put.poznan.whereismymoney.security.SessionManager;

import javax.inject.Inject;

public class RegistrationService {
    private SessionManager sessionManager;
    private UserDao userDao;

    @Inject
    public RegistrationService(SessionManager sessionManager, UserDao userDao) {
        this.sessionManager = sessionManager;
        this.userDao = userDao;
    }

    public boolean performRegistration(String username, String email, String password, String retypedPassword) {
        boolean operationSuccessful = true;
        if (userDao.getByUsername(username) != null) {
            operationSuccessful = false;
        }
        if (userDao.getByEmail(email) != null) {
            operationSuccessful = false;
        }
        if (password.equals(retypedPassword) && operationSuccessful) {
            User user = new User(username, email, sessionManager.digestPassword(password));
            userDao.saveOrUpdate(user);
        }
        return operationSuccessful;
    }
}
