package pl.put.poznan.whereismymoney.dao;

import com.google.gson.Gson;
import pl.put.poznan.whereismymoney.http.ServerCommunicator;
import pl.put.poznan.whereismymoney.injector.annotation.Host;
import pl.put.poznan.whereismymoney.model.User;
import pl.put.poznan.whereismymoney.security.SessionManager;

import javax.inject.Inject;
import java.io.IOException;
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
        Map<String, String> parameters = serverCommunicator.provideBasicParameters(sessionManager);
        String response;
        try {
            response = serverCommunicator.sendMessageAndWaitForResponse(hostAddress + "/user/get", parameters);
        } catch (IOException e) {
            response = "{}";
        }
        return gson.fromJson(response, User.class);
    }

    public boolean update(User user) {
        Map<String, String> parameters = serverCommunicator.provideBasicParameters(sessionManager);
        parameters.put("modifiedUser", gson.toJson(user));
        String response;
        try {
            response = serverCommunicator.sendMessageAndWaitForResponse(hostAddress + "/user/modify", parameters);
        } catch (IOException e) {
            response = "false";
        }
        return gson.fromJson(response, boolean.class);
    }
}
