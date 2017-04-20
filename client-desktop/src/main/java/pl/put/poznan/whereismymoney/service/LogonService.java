package pl.put.poznan.whereismymoney.service;

import com.google.gson.Gson;
import pl.put.poznan.whereismymoney.http.ServerCommunicator;
import pl.put.poznan.whereismymoney.injector.annotation.Logon;
import pl.put.poznan.whereismymoney.model.User;
import pl.put.poznan.whereismymoney.security.SessionManager;

import javax.inject.Inject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LogonService {
    private SessionManager sessionManager;
    private Gson gson;
    private ServerCommunicator serverCommunicator;
    private final String logonAddress;

    @Inject
    public LogonService(SessionManager sessionManager, Gson gson, ServerCommunicator serverCommunicator,
                        @Logon String logonAddress) {
        this.sessionManager = sessionManager;
        this.gson = gson;
        this.serverCommunicator = serverCommunicator;
        this.logonAddress = logonAddress;
    }

    public boolean performLogon(String username, String password) {
        boolean logonSuccessful = false;
        if (!isAnyFieldEmpty(username, password)) {
            byte[] salt = getSalt(username);
            byte[] passwordDigest = sessionManager.generatePasswordDigest(password, salt);
            logonSuccessful = validateCredentials(username, passwordDigest);
        }
        return logonSuccessful;
    }

    private boolean isAnyFieldEmpty(String username, String password) {
        return username.equals("") || password.equals("");
    }

    private byte[] getSalt(String username) {
        String saltText = getSaltText(username);
        byte[] salt = new byte[0];
        if (!saltText.equals("")) {
            salt = gson.fromJson(saltText, byte[].class);
        }
        return salt;
    }

    private String getSaltText(String username) {
        Map<String, String> parameters = new HashMap<>(1);
        parameters.put("username", username);
        String saltText;
        try {
            saltText = serverCommunicator.sendMessageAndWaitForResponse(logonAddress + "/salt", parameters);
        } catch (IOException e) {
            saltText = "[]";
        }
        return saltText;
    }

    private boolean validateCredentials(String username, byte[] passwordDigest) {
        boolean operationResult = false;
        if (passwordDigest.length > 0) {
            generateSessionKey(username, passwordDigest);
            operationResult = validateOnServer();
        }
        return operationResult;
    }

    private void generateSessionKey(String username, byte[] passwordDigest) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordDigest);
        sessionManager.setUserData(user);
        sessionManager.generateSessionKey();
    }

    private boolean validateOnServer() {
        Map<String, String> parameters = new HashMap<>(2);
        parameters.put("username", sessionManager.getUserData().getUsername());
        String sessionKey = gson.toJson(sessionManager.getSessionKey());
        parameters.put("sessionKey", sessionKey);
        String validationResult;
        try {
            validationResult = serverCommunicator.sendMessageAndWaitForResponse(logonAddress + "/verify", parameters);
        } catch (IOException e) {
            validationResult = "false";
        }
        return gson.fromJson(validationResult, boolean.class);
    }
}
