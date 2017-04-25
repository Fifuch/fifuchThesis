package pl.put.poznan.whereismymoney.service;

import com.google.gson.Gson;
import pl.put.poznan.whereismymoney.crypto.CryptoUtils;
import pl.put.poznan.whereismymoney.dao.UserDao;
import pl.put.poznan.whereismymoney.http.ServerCommunicator;
import pl.put.poznan.whereismymoney.injector.annotation.Host;
import pl.put.poznan.whereismymoney.model.User;
import pl.put.poznan.whereismymoney.security.SessionManager;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.inject.Inject;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.SecureRandom;
import java.security.spec.RSAPublicKeySpec;
import java.util.HashMap;
import java.util.Map;

public class LogonService {
    private UserDao userDao;
    private SessionManager sessionManager;
    private Gson gson;
    private ServerCommunicator serverCommunicator;
    private final String logonAddress;

    @Inject
    public LogonService(SessionManager sessionManager, Gson gson, ServerCommunicator serverCommunicator,
                        @Host String logonAddress, UserDao userDao) {
        this.sessionManager = sessionManager;
        this.gson = gson;
        this.serverCommunicator = serverCommunicator;
        this.logonAddress = logonAddress;
        this.userDao = userDao;
    }

    public boolean performLogon(String username, String password) {
        boolean logonSuccessful = false;
        if (!isAnyFieldEmpty(username, password)) {
            byte[] salt = getSalt(username);
            byte[] passwordDigest = sessionManager.generatePasswordDigest(password, salt);
            logonSuccessful = validateCredentials(username, passwordDigest);
        }
        if (logonSuccessful) {
            sessionManager.setUserData(userDao.get());
        }
        return logonSuccessful;
    }

    private boolean isAnyFieldEmpty(String username, String password) {
        return username.equals("") || password.equals("");
    }

    private byte[] getSalt(String username) {
        SecretKey aesKey = CryptoUtils.generateAESKey();
        IvParameterSpec iv = new IvParameterSpec(SecureRandom.getSeed(16));

        String saltText = getSaltText(username, aesKey, iv);
        byte[] salt = new byte[0];
        if (!saltText.equals("")) {
            salt = CryptoUtils.decryptByteArrayParameter(saltText, aesKey, iv);
        }
        return salt;
    }

    private String getSaltText(String username, SecretKey aesKey, IvParameterSpec iv) {

        Map<String, String> parameters = new HashMap<>(1);
        parameters.put("username", CryptoUtils.encryptParameter(username, aesKey, iv));
        parameters.putAll(serverCommunicator.provideEncryptionParameters(aesKey, iv));
        String saltText;
        try {
            saltText = serverCommunicator.sendMessageAndWaitForResponse(logonAddress + "logon/salt", parameters);
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
        SecretKey aesKey = CryptoUtils.generateAESKey();
        IvParameterSpec iv = new IvParameterSpec(SecureRandom.getSeed(16));

        Map<String, String> parameters = new HashMap<>(2);
        parameters.put("username", CryptoUtils.encryptParameter(sessionManager.getUserData().getUsername(), aesKey, iv));
        String sessionKey = CryptoUtils.encryptParameter(sessionManager.getSessionKey(), aesKey, iv);
        parameters.put("sessionKey", sessionKey);
        parameters.putAll(serverCommunicator.provideEncryptionParameters(aesKey, iv));
        String validationResult;
        try {
            validationResult = serverCommunicator.sendMessageAndWaitForResponse(logonAddress + "logon/verify", parameters);
        } catch (IOException e) {
            validationResult = "false";
        }
        return Boolean.parseBoolean(CryptoUtils.decryptStringParameter(validationResult, aesKey, iv));
    }

    public void getKey() {
        try {
            String key;
            Map<String, String> parameters = new HashMap<>();
            key = serverCommunicator.sendMessageAndWaitForResponse(logonAddress + "/publicKey/get", parameters);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            CryptoUtils.publicKey = fact.generatePublic(gson.fromJson(key, RSAPublicKeySpec.class));
        } catch (Exception e) {
            System.err.println("CANNOT GET PUBLIC KEY!");
        }

    }
}
