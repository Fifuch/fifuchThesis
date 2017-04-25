package pl.put.poznan.whereismymoney.dao;

import com.google.gson.Gson;
import pl.put.poznan.whereismymoney.crypto.CryptoUtils;
import pl.put.poznan.whereismymoney.http.ServerCommunicator;
import pl.put.poznan.whereismymoney.injector.annotation.Host;
import pl.put.poznan.whereismymoney.model.User;
import pl.put.poznan.whereismymoney.security.SessionManager;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.inject.Inject;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Map;

public class UserDao {
    private SessionManager sessionManager;
    private Gson gson;
    private ServerCommunicator serverCommunicator;
    private final String hostAddress;

    @Inject
    public UserDao(SessionManager sessionManager, Gson gson, ServerCommunicator serverCommunicator, @Host String hostAddress) {
        this.sessionManager = sessionManager;
        this.gson = gson;
        this.serverCommunicator = serverCommunicator;
        this.hostAddress = hostAddress;
    }

    public User get() {
        SecretKey aesKey = CryptoUtils.generateAESKey();
        IvParameterSpec iv = new IvParameterSpec(SecureRandom.getSeed(16));

        Map<String, String> parameters = serverCommunicator.provideBasicParameters(sessionManager,aesKey,iv);
        parameters.putAll(serverCommunicator.provideEncryptionParameters(aesKey,iv));
        String response;
        try {
            response = serverCommunicator.sendMessageAndWaitForResponse(hostAddress + "/user/get", parameters);
        } catch (IOException e) {
            response = "{}";
        }
        return gson.fromJson(response, User.class);
    }

    public boolean update(User user) {
        SecretKey aesKey = CryptoUtils.generateAESKey();
        IvParameterSpec iv = new IvParameterSpec(SecureRandom.getSeed(16));

        Map<String, String> parameters = serverCommunicator.provideBasicParameters(sessionManager,aesKey,iv);
        parameters.put("modifiedUser", CryptoUtils.encryptParameter(gson.toJson(user),aesKey,iv));
        parameters.putAll(serverCommunicator.provideEncryptionParameters(aesKey,iv));
        String response;
        try {
            response = serverCommunicator.sendMessageAndWaitForResponse(hostAddress + "/user/modify", parameters);
        } catch (IOException e) {
            response = "false";
        }
        return gson.fromJson(response, boolean.class);
    }
}
