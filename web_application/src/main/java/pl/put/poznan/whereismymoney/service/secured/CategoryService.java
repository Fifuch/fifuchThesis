package pl.put.poznan.whereismymoney.service.secured;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.put.poznan.whereismymoney.crypto.Encryption;
import pl.put.poznan.whereismymoney.crypto.RSAKeyManager;
import pl.put.poznan.whereismymoney.dao.BudgetRepository;
import pl.put.poznan.whereismymoney.dao.CategoryRepository;
import pl.put.poznan.whereismymoney.dao.TransactionRepository;
import pl.put.poznan.whereismymoney.model.Budget;
import pl.put.poznan.whereismymoney.model.Category;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryService {
    private BudgetRepository budgetRepository;
    private CategoryRepository categoryRepository;
    private TransactionRepository transactionRepository;
    private Gson gson;
    private RSAKeyManager rsaKeyManager;


    @Autowired
    public CategoryService(BudgetRepository budgetRepository, CategoryRepository categoryRepository,
                           TransactionRepository transactionRepository, Gson gson, RSAKeyManager rsaKeyManager) {
        this.budgetRepository = budgetRepository;
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
        this.gson = gson;
        this.rsaKeyManager = rsaKeyManager;

    }

    @PostMapping("/get")
    public String getCategories(String sessionKey, String username, String budgetId, String cipherKey, String iv) {
        SecretKey aesKey = Encryption.decryptAesKey(cipherKey, rsaKeyManager.getPrivateKey());
        IvParameterSpec ivParameter = Encryption.decryptIv(iv, rsaKeyManager.getPrivateKey());
        long decryptedBudgetId = Long.parseLong(Encryption.decryptStringParameter(budgetId,aesKey,ivParameter));
        Budget budget = budgetRepository.findOne(decryptedBudgetId);
        String response = "[]";
        if (budget.getOwner().getUsername().equals(Encryption.decryptStringParameter(username,aesKey,ivParameter))) {
            List<Category> categories = categoryRepository.findByRelatedBudget(budget);
            response = gson.toJson(categories);
        }
        return Encryption.encryptParameter(response,aesKey,ivParameter);
    }

    @PostMapping("/add")
    public String addCategory(String sessionKey, String username, String budgetId, String name, String cipherKey, String iv) {
        SecretKey aesKey = Encryption.decryptAesKey(cipherKey, rsaKeyManager.getPrivateKey());
        IvParameterSpec ivParameter = Encryption.decryptIv(iv, rsaKeyManager.getPrivateKey());
        long decryptedBudgetId = Long.parseLong(Encryption.decryptStringParameter(budgetId,aesKey,ivParameter));
        String decryptedName = Encryption.decryptStringParameter(name,aesKey,ivParameter);

        Budget budget = budgetRepository.findOne(decryptedBudgetId);
        boolean result = categoryRepository.findByRelatedBudgetAndName(budget,decryptedName ) == null
                && budget.getOwner().getUsername().equals(Encryption.decryptStringParameter(username,aesKey,ivParameter));
        if (result) {
            Category category = new Category(decryptedName, budget);
            result = categoryRepository.save(category) != null;
        }
        return Encryption.encryptParameter(String.valueOf(result),aesKey,ivParameter);
    }

    @PostMapping("/update")
    public String updateCategory(String sessionKey, String username, String categoryId, String limit, String cipherKey, String iv) {
        SecretKey aesKey = Encryption.decryptAesKey(cipherKey, rsaKeyManager.getPrivateKey());
        IvParameterSpec ivParameter = Encryption.decryptIv(iv, rsaKeyManager.getPrivateKey());
        long decryptedCategoryId = Long.parseLong(Encryption.decryptStringParameter(categoryId,aesKey,ivParameter));

        double numericalLimit;
        try {
            numericalLimit = Double.parseDouble(Encryption.decryptStringParameter(limit,aesKey,ivParameter));
            Category category = categoryRepository.findOne(decryptedCategoryId);
            if (category.getRelatedBudget().getOwner().getUsername().equals(Encryption.decryptStringParameter(username,aesKey,ivParameter))) {
                category.setLimit(new BigDecimal(numericalLimit));
                categoryRepository.save(category);
                return Encryption.encryptParameter(String.valueOf(true),aesKey,ivParameter);
            }
        } catch (NumberFormatException nfe) {
        }
        return Encryption.encryptParameter(String.valueOf(false),aesKey,ivParameter);
    }

    @PostMapping("/delete")
    public void deleteCategory(String sessionKey, String username, String categoryId, String cipherKey, String iv) {
        SecretKey aesKey = Encryption.decryptAesKey(cipherKey, rsaKeyManager.getPrivateKey());
        IvParameterSpec ivParameter = Encryption.decryptIv(iv, rsaKeyManager.getPrivateKey());
        long decryptedCategoryId = Long.parseLong(Encryption.decryptStringParameter(categoryId,aesKey,ivParameter));

        if (categoryRepository.findOne(decryptedCategoryId).getRelatedBudget().getOwner().getUsername().equals(Encryption.decryptStringParameter(username,aesKey,ivParameter))) {
            transactionRepository.delete(transactionRepository.findByTransactionCategoryId(decryptedCategoryId));
            categoryRepository.delete(decryptedCategoryId);
        }
    }

}
