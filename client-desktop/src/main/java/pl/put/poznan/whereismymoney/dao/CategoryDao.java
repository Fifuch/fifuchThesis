package pl.put.poznan.whereismymoney.dao;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import pl.put.poznan.whereismymoney.crypto.CryptoUtils;
import pl.put.poznan.whereismymoney.http.ServerCommunicator;
import pl.put.poznan.whereismymoney.injector.annotation.Host;
import pl.put.poznan.whereismymoney.model.Budget;
import pl.put.poznan.whereismymoney.model.Category;
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

public class CategoryDao {
    private SessionManager sessionManager;
    private Gson gson;
    private ServerCommunicator serverCommunicator;
    private final String hostAddress;

    @Inject
    public CategoryDao(SessionManager sessionManager, Gson gson, ServerCommunicator serverCommunicator,
                       @Host String hostAddress) {
        this.sessionManager = sessionManager;
        this.gson = gson;
        this.serverCommunicator = serverCommunicator;
        this.hostAddress = hostAddress;
    }

    public List<Category> getByBudget(Budget budget) {
        SecretKey aesKey = CryptoUtils.generateAESKey();
        IvParameterSpec iv = new IvParameterSpec(SecureRandom.getSeed(16));

        Map<String, String> parameters = serverCommunicator.provideBasicParameters(sessionManager,aesKey,iv);
        parameters.put("budgetId", CryptoUtils.encryptParameter(budget.getId() + "",aesKey,iv));
        parameters.putAll(serverCommunicator.provideEncryptionParameters(aesKey,iv));

        String response;
        try {
            response = serverCommunicator.sendMessageAndWaitForResponse(hostAddress + "/category/get", parameters);
        } catch (IOException e) {
            response = "[]";
        }
        Type categoryListFormatter = new TypeToken<ArrayList<Category>>(){}.getType();
        return gson.fromJson(CryptoUtils.decryptStringParameter(response,aesKey,iv), categoryListFormatter);
    }

    public void save(Category category) {
        SecretKey aesKey = CryptoUtils.generateAESKey();
        IvParameterSpec iv = new IvParameterSpec(SecureRandom.getSeed(16));

        Map<String, String> parameters = serverCommunicator.provideBasicParameters(sessionManager,aesKey,iv);
        parameters.put("budgetId", CryptoUtils.encryptParameter(category.getRelatedBudget().getId() + "",aesKey,iv));
        parameters.put("name", CryptoUtils.encryptParameter(category.getName(),aesKey,iv));
        parameters.putAll(serverCommunicator.provideEncryptionParameters(aesKey,iv));

        try {
            serverCommunicator.sendMessageAndWaitForResponse(hostAddress + "/category/add", parameters);
        } catch (IOException ignored) {

        }
    }

    public void update(Category category) {
        SecretKey aesKey = CryptoUtils.generateAESKey();
        IvParameterSpec iv = new IvParameterSpec(SecureRandom.getSeed(16));

        Map<String, String> parameters = serverCommunicator.provideBasicParameters(sessionManager,aesKey,iv);
        parameters.put("categoryId", CryptoUtils.encryptParameter(category.getId() + "",aesKey,iv));
        parameters.put("limit", CryptoUtils.encryptParameter(category.getLimit().toPlainString(),aesKey,iv));
        parameters.putAll(serverCommunicator.provideEncryptionParameters(aesKey,iv));

        try {
            serverCommunicator.sendMessageAndWaitForResponse(hostAddress + "/category/update", parameters);
        } catch (IOException ignored) {

        }
    }

    public void delete(Category category) {
        SecretKey aesKey = CryptoUtils.generateAESKey();
        IvParameterSpec iv = new IvParameterSpec(SecureRandom.getSeed(16));

        Map<String, String> parameters = serverCommunicator.provideBasicParameters(sessionManager,aesKey,iv);
        parameters.put("categoryId", CryptoUtils.encryptParameter(category.getId() + "",aesKey,iv));
        parameters.putAll(serverCommunicator.provideEncryptionParameters(aesKey,iv));
        try {
            serverCommunicator.sendMessageAndWaitForResponse(hostAddress + "/category/delete", parameters);
        } catch (IOException ignored) {

        }
    }
}
