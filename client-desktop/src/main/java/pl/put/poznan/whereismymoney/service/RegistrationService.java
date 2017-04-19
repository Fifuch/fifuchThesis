package pl.put.poznan.whereismymoney.service;

import pl.put.poznan.whereismymoney.http.ServerCommunicator;
import pl.put.poznan.whereismymoney.http.util.ResponseCodes;
import pl.put.poznan.whereismymoney.injector.annotation.Registration;

import javax.inject.Inject;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

public class RegistrationService {
    private MessageDigest sha256;
    private ServerCommunicator serverCommunicator;
    private final String registrationAddress;

    @Inject
    public RegistrationService(MessageDigest sha256, ServerCommunicator serverCommunicator,
                               @Registration String registrationAddress) {
        this.sha256 = sha256;
        this.serverCommunicator = serverCommunicator;
        this.registrationAddress = registrationAddress;
    }

    public String performRegistration(String username, String email, String password, String retypedPassword) {
        String response = ResponseCodes.NONIDENTICAL_PASSWORDS.toString();
        if (!isAnyFieldEmpty(username, email, password) && password.equals(retypedPassword)) {
            try {
                Map<String, String> parameters = prepareRegistrationParameters(username, email, password);
                response = serverCommunicator.sendMessageAndWaitForResponse(registrationAddress, parameters);
            } catch (IOException e) {
                response = ResponseCodes.COMMUNICATION_ERROR.toString();
            }
        }
        return response;
    }

    private boolean isAnyFieldEmpty(String username, String email, String password) {
        return username == null || email == null || password == null;
    }

    private Map<String, String> prepareRegistrationParameters(String username, String email,
                                                              String password) throws UnsupportedEncodingException {
        String passwordDigest = new String(sha256.digest(password.getBytes("UTF-8")), "UTF-8");
        Map<String, String> parameters = new HashMap<>(3);
        parameters.put("username", username);
        parameters.put("email", email);
        parameters.put("password", passwordDigest);
        return parameters;
    }
}
