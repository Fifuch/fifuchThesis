package pl.put.poznan.whereismymoney.dao;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import pl.put.poznan.whereismymoney.http.ServerCommunicator;
import pl.put.poznan.whereismymoney.injector.annotation.Host;
import pl.put.poznan.whereismymoney.model.Budget;
import pl.put.poznan.whereismymoney.model.Category;
import pl.put.poznan.whereismymoney.model.Transaction;
import pl.put.poznan.whereismymoney.security.SessionManager;

import javax.inject.Inject;
import java.io.IOException;
import java.lang.reflect.Type;
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
        Map<String, String> parameters = serverCommunicator.provideBasicParameters(sessionManager);
        parameters.put("budgetId", budget.getId() + "");
        String response;
        try {
            response = serverCommunicator.sendMessageAndWaitForResponse(hostAddress + "/transaction/getByBudget", parameters);
        } catch (IOException e) {
            response = "[]";
        }
        Type transactionListFormatter = new TypeToken<ArrayList<Transaction>>(){}.getType();
        return gson.fromJson(response, transactionListFormatter);
    }

    public List<Transaction> getByCategory(Category category) {
        Map<String, String> parameters = serverCommunicator.provideBasicParameters(sessionManager);
        parameters.put("categoryId", category.getId() + "");
        String response;
        try {
            response = serverCommunicator.sendMessageAndWaitForResponse(hostAddress + "/transaction/getByCategory", parameters);
        } catch (IOException e) {
            response = "[]";
        }
        Type categoryListFormatter = new TypeToken<ArrayList<Transaction>>(){}.getType();
        return gson.fromJson(response, categoryListFormatter);
    }

    public void saveOrUpdate(Transaction transaction) {
        Map<String, String> parameters = serverCommunicator.provideBasicParameters(sessionManager);
        parameters.put("transaction", gson.toJson(transaction));
        String result;
        try {
            result = serverCommunicator.sendMessageAndWaitForResponse(hostAddress + "/transaction/add", parameters);
        } catch (IOException e) {
           result = "false";
        }
    }

    public void delete(Transaction transaction) {
        Map<String, String> parameters = serverCommunicator.provideBasicParameters(sessionManager);
        parameters.put("transactionId", transaction.getId() + "");
        try {
            serverCommunicator.sendMessageAndWaitForResponse(hostAddress + "/transaction/delete", parameters);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
