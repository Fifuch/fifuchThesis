package pl.put.poznan.whereismymoney.service.secured;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.put.poznan.whereismymoney.dao.BudgetRepository;
import pl.put.poznan.whereismymoney.dao.CategoryRepository;
import pl.put.poznan.whereismymoney.dao.TransactionRepository;
import pl.put.poznan.whereismymoney.dao.UserRepository;
import pl.put.poznan.whereismymoney.model.Budget;
import pl.put.poznan.whereismymoney.model.User;

import java.util.List;

@RestController
@RequestMapping("/budget")
public class BudgetService {
    private BudgetRepository budgetRepository;
    private UserRepository userRepository;
    private CategoryRepository categoryRepository;
    private TransactionRepository transactionRepository;
    private Gson gson;

    @Autowired
    public BudgetService(BudgetRepository budgetRepository, UserRepository userRepository,
                         CategoryRepository categoryRepository, TransactionRepository transactionRepository, Gson gson) {
        this.budgetRepository = budgetRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
        this.gson = gson;
    }

    @PostMapping("/get")
    public String getBudgets(String sessionKey, String username) {
        List<Budget> budgets = budgetRepository.findByOwnerUsername(username);
        String response = gson.toJson(budgets);
        return response;
    }

    @PostMapping("/add")
    public boolean addBudget(String sessionKey, String username, String name) {
        User owner = userRepository.findByUsername(username);
        int size = budgetRepository.findByNameAndOwner(name, owner).size();
        boolean result = false;
        if (size == 0) {
            Budget budget = new Budget(name, owner);
            result = budgetRepository.save(budget) != null;
        }
        return result;
    }

    @PostMapping("/delete")
    public void deleteBudget(String sessionKey, String username, long id) {
        if (budgetRepository.findOne(id).getOwner().getUsername().equals(username)) {
            transactionRepository.delete(transactionRepository.findByRelatedBudgetId(id));
            categoryRepository.delete(categoryRepository.findByRelatedBudgetId(id));
            budgetRepository.delete(id);
        }
    }

}
