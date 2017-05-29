package pl.put.poznan.whereismymoney.dao;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import pl.put.poznan.whereismymoney.crypto.CryptoUtils;
import pl.put.poznan.whereismymoney.http.ServerCommunicator;
import pl.put.poznan.whereismymoney.injector.annotation.Host;
import pl.put.poznan.whereismymoney.model.Budget;
import pl.put.poznan.whereismymoney.model.User;
import pl.put.poznan.whereismymoney.security.SessionManager;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.inject.Inject;
import java.io.IOException;
import java.lang.reflect.Type;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BudgetDao {
    private SessionManager sessionManager;
    private Gson gson;
    private ServerCommunicator serverCommunicator;
    private final String hostAddress;

    @Inject
    public BudgetDao(SessionManager sessionManager, Gson gson, ServerCommunicator serverCommunicator,
                     @Host String hostAddress) {
        this.sessionManager = sessionManager;
        this.gson = gson;
        this.serverCommunicator = serverCommunicator;
        this.hostAddress = hostAddress;
    }

    public List<Budget> getByUser(User owner) {
        SecretKey aesKey = CryptoUtils.generateAESKey();
        IvParameterSpec iv = new IvParameterSpec(SecureRandom.getSeed(16));

        String response;
        try {
            Map<String, String> parameters = serverCommunicator.provideBasicParameters(sessionManager,aesKey,iv);
            parameters.putAll(serverCommunicator.provideEncryptionParameters(aesKey,iv));
            response = serverCommunicator.sendMessageAndWaitForResponse(hostAddress + "/budget/get", parameters);
        } catch (Exception e) {
            response = "[]";
        }
        Type budgetListFormatter = new TypeToken<ArrayList<Budget>>(){}.getType();
        List<Budget> budgets = gson.fromJson(CryptoUtils.decryptStringParameter(response,aesKey,iv), budgetListFormatter);
        return budgets;
    }

    public void saveOrUpdate(Budget budget) {
        SecretKey aesKey = CryptoUtils.generateAESKey();
        IvParameterSpec iv = new IvParameterSpec(SecureRandom.getSeed(16));

        Map<String, String> parameters = serverCommunicator.provideBasicParameters(sessionManager,aesKey,iv);
        parameters.put("name", CryptoUtils.encryptParameter(budget.getName(),aesKey,iv));
        parameters.putAll(serverCommunicator.provideEncryptionParameters(aesKey,iv));
        String response;
        try {
            response = serverCommunicator.sendMessageAndWaitForResponse(hostAddress + "/budget/add", parameters);
        } catch (IOException e) {
            response = "false";
        }
    }

    public void delete(Budget budget) {
        SecretKey aesKey = CryptoUtils.generateAESKey();
        IvParameterSpec iv = new IvParameterSpec(SecureRandom.getSeed(16));

        Map<String, String> parameters = serverCommunicator.provideBasicParameters(sessionManager,aesKey,iv);
        parameters.put("id", CryptoUtils.encryptParameter(budget.getId() + "",aesKey,iv));
        parameters.putAll(serverCommunicator.provideEncryptionParameters(aesKey,iv));
        String response;
        try {
            response = serverCommunicator.sendMessageAndWaitForResponse(hostAddress + "/budget/delete", parameters);
        } catch (IOException e) {
            response = "false";
        }
    }

}
