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
import pl.put.poznan.whereismymoney.model.Category;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/category")
class CategoryService {
    private BudgetRepository budgetRepository;
    private CategoryRepository categoryRepository;
    private TransactionRepository transactionRepository;
    private Gson gson;

    @Autowired
    public CategoryService(BudgetRepository budgetRepository, CategoryRepository categoryRepository,
                           TransactionRepository transactionRepository, Gson gson) {
        this.budgetRepository = budgetRepository;
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
        this.gson = gson;
    }

    @PostMapping("/get")
    public String getCategories(String sessionKey, String username, long budgetId) {
        Budget budget = budgetRepository.findOne(budgetId);
        String response = "[]";
        if (budget.getOwner().getUsername().equals(username)) {
            List<Category> categories = categoryRepository.findByRelatedBudget(budget);
            response = gson.toJson(categories);
        }
        return response;
    }

    @PostMapping("/add")
    public boolean addCategory(String sessionKey, String username, long budgetId, String name) {
        Budget budget = budgetRepository.findOne(budgetId);
        boolean result = categoryRepository.findByRelatedBudgetAndName(budget, name) == null
                && budget.getOwner().getUsername().equals(username);
        if (result) {
            Category category = new Category(name, budget);
            result = categoryRepository.save(category) != null;
        }
        return result;
    }

    @PostMapping("/update")
    public boolean updateCategory(String sessionKey, String username, long categoryId, String limit) {
        double numericalLimit;
        try {
            numericalLimit = Double.parseDouble(limit);
            Category category = categoryRepository.findOne(categoryId);
            if (category.getRelatedBudget().getOwner().getUsername().equals(username)) {
                category.setLimit(new BigDecimal(numericalLimit));
                categoryRepository.save(category);
                return true;
            }
        } catch (NumberFormatException nfe) {
        }
        return false;
    }

    @PostMapping("/delete")
    public void deleteCategory(String sessionKey, String username, long categoryId) {
        if (categoryRepository.findOne(categoryId).getRelatedBudget().getOwner().getUsername().equals(username)) {
            transactionRepository.delete(transactionRepository.findByTransactionCategoryId(categoryId));
            categoryRepository.delete(categoryId);
        }
    }

}
