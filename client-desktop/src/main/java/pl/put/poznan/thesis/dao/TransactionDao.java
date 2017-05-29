package pl.put.poznan.thesis.dao;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import pl.put.poznan.thesis.crypto.CryptoUtils;
import pl.put.poznan.thesis.http.ServerCommunicator;
import pl.put.poznan.thesis.injector.annotation.Host;
import pl.put.poznan.thesis.model.Budget;
import pl.put.poznan.thesis.model.Category;
import pl.put.poznan.thesis.model.Transaction;
import pl.put.poznan.thesis.security.SessionManager;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.inject.Inject;
import java.io.IOException;
import java.lang.reflect.Type;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TransactionDao {
    private SessionManager sessionManager;
    private Gson gson;
    private ServerCommunicator serverCommunicator;
    private final String hostAddress;

    @Inject
    public TransactionDao(SessionManager sessionManager, Gson gson, ServerCommunicator serverCommunicator, @Host String hostAddress) {
        this.sessionManager = sessionManager;
        this.gson = gson;
        this.serverCommunicator = serverCommunicator;
        this.hostAddress = hostAddress;
    }

    public Transaction getById(long id) {
        return null;
    }

    public List<Transaction> getByDate(LocalDate transactionDateFrom, LocalDate transactionDateTo) {
        return null;
    }

    public List<Transaction> getByBudget(Budget budget) {
        SecretKey aesKey = CryptoUtils.generateAESKey();
        IvParameterSpec iv = new IvParameterSpec(SecureRandom.getSeed(16));

        Map<String, String> parameters = serverCommunicator.provideBasicParameters(sessionManager,aesKey,iv);
        parameters.put("budgetId", CryptoUtils.encryptParameter(budget.getId() + "",aesKey,iv));
        parameters.putAll(serverCommunicator.provideEncryptionParameters(aesKey,iv));
        String response;
        try {
            response = serverCommunicator.sendMessageAndWaitForResponse(hostAddress + "/transaction/getByBudget", parameters);
        } catch (IOException e) {
            response = "[]";
        }
        Type transactionListFormatter = new TypeToken<ArrayList<Transaction>>(){}.getType();
        return gson.fromJson(CryptoUtils.decryptStringParameter(response,aesKey,iv), transactionListFormatter);
    }

    public List<Transaction> getByCategory(Category category) {
        SecretKey aesKey = CryptoUtils.generateAESKey();
        IvParameterSpec iv = new IvParameterSpec(SecureRandom.getSeed(16));

        Map<String, String> parameters = serverCommunicator.provideBasicParameters(sessionManager,aesKey,iv);
        parameters.put("categoryId", CryptoUtils.encryptParameter(category.getId() + "",aesKey,iv));
        parameters.putAll(serverCommunicator.provideEncryptionParameters(aesKey,iv));
        String response;
        try {
            response = serverCommunicator.sendMessageAndWaitForResponse(hostAddress + "/transaction/getByCategory", parameters);
        } catch (IOException e) {
            response = "[]";
        }
        Type categoryListFormatter = new TypeToken<ArrayList<Transaction>>(){}.getType();
        return gson.fromJson(CryptoUtils.decryptStringParameter(response,aesKey,iv), categoryListFormatter);
    }

    public void saveOrUpdate(Transaction transaction) {
        SecretKey aesKey = CryptoUtils.generateAESKey();
        IvParameterSpec iv = new IvParameterSpec(SecureRandom.getSeed(16));

        Map<String, String> parameters = serverCommunicator.provideBasicParameters(sessionManager,aesKey,iv);
        parameters.put("transaction", CryptoUtils.encryptParameter(gson.toJson(transaction),aesKey,iv));
        parameters.putAll(serverCommunicator.provideEncryptionParameters(aesKey,iv));
        String result;
        try {
            result = serverCommunicator.sendMessageAndWaitForResponse(hostAddress + "/transaction/add", parameters);
        } catch (IOException e) {
           result = "false";
        }
    }

    public void delete(Transaction transaction) {
        SecretKey aesKey = CryptoUtils.generateAESKey();
        IvParameterSpec iv = new IvParameterSpec(SecureRandom.getSeed(16));

        Map<String, String> parameters = serverCommunicator.provideBasicParameters(sessionManager,aesKey,iv);
        parameters.put("transactionId", CryptoUtils.encryptParameter(transaction.getId() + "",aesKey,iv));
        parameters.putAll(serverCommunicator.provideEncryptionParameters(aesKey,iv));

        try {
            serverCommunicator.sendMessageAndWaitForResponse(hostAddress + "/transaction/delete", parameters);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
