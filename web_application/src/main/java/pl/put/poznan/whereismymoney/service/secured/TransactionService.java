package pl.put.poznan.whereismymoney.service.secured;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.put.poznan.whereismymoney.dao.BudgetRepository;
import pl.put.poznan.whereismymoney.dao.CategoryRepository;
import pl.put.poznan.whereismymoney.dao.TransactionRepository;
import pl.put.poznan.whereismymoney.model.Budget;
import pl.put.poznan.whereismymoney.model.Transaction;

import java.util.List;

@RestController
@RequestMapping("/transaction")
public class TransactionService {
    private BudgetRepository budgetRepository;
    private CategoryRepository categoryRepository;
    private TransactionRepository transactionRepository;
    private Gson gson;

    @Autowired
    public TransactionService(BudgetRepository budgetRepository, CategoryRepository categoryRepository,
                              TransactionRepository transactionRepository, Gson gson) {
        this.budgetRepository = budgetRepository;
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
        this.gson = gson;
    }

    @PostMapping("/getByBudget")
    public String getTransactionsByBudget(String sessionKey, String username, long budgetId) {
        Budget budget = budgetRepository.findOne(budgetId);
        String response = "[]";
        if (budget.getOwner().getUsername().equals(username)) {
            List<Transaction> transactions = transactionRepository.findByRelatedBudgetId(budgetId);
            response = gson.toJson(transactions);
        }
        return response;
    }

    @PostMapping("/getByCategory")
    public String getTransactionsByCategory(String sessionKey, String username, long categoryId) {
        boolean authorizationSuccessful = categoryRepository.findOne(categoryId).
                getRelatedBudget().getOwner().getUsername().equals(username);
        String response = "[]";
        if (authorizationSuccessful) {
            List<Transaction> transactions = transactionRepository.findByTransactionCategoryId(categoryId);
            response = gson.toJson(transactions);
        }
        return response;
    }

    @PostMapping("/add")
    public boolean addBudget(String sessionKey, String username, String transaction) {
        Transaction newTransaction = gson.fromJson(transaction, Transaction.class);
        if (newTransaction.getRelatedBudget().getOwner().getUsername().equals(username)) {
            return transactionRepository.save(newTransaction) != null;
        }
        return false;
    }

    @PostMapping("/delete")
    public void deleteCategory(String sessionKey, String username, long transactionId) {
        boolean authorizationSuccessful = transactionRepository.findOne(transactionId)
                .getRelatedBudget().getOwner().getUsername().equals(username);
        if (authorizationSuccessful) {
            transactionRepository.delete(transactionId);
        }
    }
}
