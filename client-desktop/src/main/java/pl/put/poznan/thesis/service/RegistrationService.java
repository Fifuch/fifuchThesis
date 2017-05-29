package pl.put.poznan.thesis.service;

import com.google.gson.Gson;
import pl.put.poznan.thesis.crypto.CryptoUtils;
import pl.put.poznan.thesis.http.ServerCommunicator;
import pl.put.poznan.thesis.http.util.ResponseCodes;
import pl.put.poznan.thesis.injector.annotation.Registration;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
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
    private SecretKey aesKey;
    private IvParameterSpec iv;

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
        generateKeys();
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
        if (response.equals(ResponseCodes.COMMUNICATION_ERROR.toString())
                || response.equals(ResponseCodes.NONIDENTICAL_PASSWORDS.toString())) {
            return response;
        }
        return CryptoUtils.decryptStringParameter(response, aesKey, iv);
    }

    private void generateKeys() {
        aesKey = CryptoUtils.generateAESKey();
        iv = new IvParameterSpec(SecureRandom.getSeed(16));
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
        parameters.put("username", CryptoUtils.encryptParameter(username, aesKey, iv));
        parameters.put("email", CryptoUtils.encryptParameter(email, aesKey, iv));
        parameters.put("password", CryptoUtils.encryptParameter(gson.toJson(passwordDigest), aesKey, iv));
        parameters.put("salt", CryptoUtils.encryptParameter(gson.toJson(salt), aesKey, iv));
        parameters.putAll(serverCommunicator.provideEncryptionParameters(aesKey,iv));
        return parameters;
    }

    private byte[] generateDigest(String password, byte[] salt) throws UnsupportedEncodingException {
        messageDigest.update(password.getBytes("UTF-8"));
        messageDigest.update(salt);
        return messageDigest.digest();
    }
}
