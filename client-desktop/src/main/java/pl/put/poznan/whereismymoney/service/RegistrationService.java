package pl.put.poznan.whereismymoney.service;

import com.google.gson.Gson;
import pl.put.poznan.whereismymoney.http.ServerCommunicator;
import pl.put.poznan.whereismymoney.http.util.ResponseCodes;
import pl.put.poznan.whereismymoney.injector.annotation.Registration;

import javax.inject.Inject;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class RegistrationService {
    private MessageDigest messageDigest;
    private ServerCommunicator serverCommunicator;
    private final String registrationAddress;
    private final SecureRandom secureRandom;
    private Gson gson;

    @Inject
    public RegistrationService(MessageDigest messageDigest, ServerCommunicator serverCommunicator,
                               @Registration String registrationAddress, SecureRandom secureRandom, Gson gson) {
        this.messageDigest = messageDigest;
        this.serverCommunicator = serverCommunicator;
        this.registrationAddress = registrationAddress;
        this.secureRandom = secureRandom;
        this.gson = gson;
    }

    public String performRegistration(String username, String email, String password, String retypedPassword) {
        String response = ResponseCodes.NONIDENTICAL_PASSWORDS.toString();
        if (!isAnyFieldEmpty(username, email, password) && password.equals(retypedPassword)) {
            try {
                byte[] salt = generateSalt();
                Map<String, String> parameters = prepareRegistrationParameters(username, email, password, salt);
                response = serverCommunicator.sendMessageAndWaitForResponse(registrationAddress, parameters);
            } catch (IOException e) {
                response = ResponseCodes.COMMUNICATION_ERROR.toString();
            }
        }
        return response;
    }

    private byte[] generateSalt() {
        byte[] result = new byte[32];
        secureRandom.nextBytes(result);
        return result;
    }

    private boolean isAnyFieldEmpty(String username, String email, String password) {
        return username == null || email == null || password == null;
    }

    private Map<String, String> prepareRegistrationParameters(String username, String email, String password,
                                                              byte[] salt) throws UnsupportedEncodingException {
        byte[] passwordDigest = generateDigest(password, salt);
        Map<String, String> parameters = new HashMap<>(3);
        parameters.put("username", username);
        parameters.put("email", email);
        parameters.put("password", gson.toJson(passwordDigest));
        parameters.put("salt", gson.toJson(salt));
        return parameters;
    }

    private byte[] generateDigest(String password, byte[] salt) throws UnsupportedEncodingException {
        messageDigest.update(password.getBytes("UTF-8"));
        messageDigest.update(salt);
        return messageDigest.digest();
    }
}
