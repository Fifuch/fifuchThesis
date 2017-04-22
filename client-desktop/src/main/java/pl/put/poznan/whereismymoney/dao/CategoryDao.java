package pl.put.poznan.whereismymoney.dao;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import pl.put.poznan.whereismymoney.http.ServerCommunicator;
import pl.put.poznan.whereismymoney.injector.annotation.Host;
import pl.put.poznan.whereismymoney.model.Budget;
import pl.put.poznan.whereismymoney.model.Category;
import pl.put.poznan.whereismymoney.security.SessionManager;

import javax.inject.Inject;
import java.io.IOException;
import java.lang.reflect.Type;
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
        Map<String, String> parameters = serverCommunicator.provideBasicParameters(sessionManager);
        parameters.put("budgetId", budget.getId() + "");
        String response;
        try {
            response = serverCommunicator.sendMessageAndWaitForResponse(hostAddress + "/category/get", parameters);
        } catch (IOException e) {
            response = "[]";
        }
        Type categoryListFormatter = new TypeToken<ArrayList<Category>>(){}.getType();
        return gson.fromJson(response, categoryListFormatter);
    }

    public void save(Category category) {
        Map<String, String> parameters = serverCommunicator.provideBasicParameters(sessionManager);
        parameters.put("budgetId", category.getRelatedBudget().getId() + "");
        parameters.put("name", category.getName());
        try {
            serverCommunicator.sendMessageAndWaitForResponse(hostAddress + "/category/add", parameters);
        } catch (IOException ignored) {

        }
    }

    public void update(Category category) {
        Map<String, String> parameters = serverCommunicator.provideBasicParameters(sessionManager);
        parameters.put("categoryId", category.getId() + "");
        parameters.put("limit", category.getLimit().toPlainString());
        try {
            serverCommunicator.sendMessageAndWaitForResponse(hostAddress + "/category/update", parameters);
        } catch (IOException ignored) {

        }
    }

    public void delete(Category category) {
        Map<String, String> parameters = serverCommunicator.provideBasicParameters(sessionManager);
        parameters.put("categoryId", category.getId() + "");
        try {
            serverCommunicator.sendMessageAndWaitForResponse(hostAddress + "/category/delete", parameters);
        } catch (IOException ignored) {

        }
    }
}
