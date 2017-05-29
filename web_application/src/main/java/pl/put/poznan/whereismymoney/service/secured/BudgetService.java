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
import pl.put.poznan.whereismymoney.dao.UserRepository;
import pl.put.poznan.whereismymoney.model.Budget;
import pl.put.poznan.whereismymoney.model.User;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.util.List;

@RestController
@RequestMapping("/budget")
public class BudgetService {
    private BudgetRepository budgetRepository;
    private UserRepository userRepository;
    private CategoryRepository categoryRepository;
    private TransactionRepository transactionRepository;
    private Gson gson;
    private RSAKeyManager rsaKeyManager;


    @Autowired
    public BudgetService(BudgetRepository budgetRepository, UserRepository userRepository,
                         CategoryRepository categoryRepository, TransactionRepository transactionRepository, Gson gson, RSAKeyManager rsaKeyManager) {
        this.budgetRepository = budgetRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
        this.gson = gson;
        this.rsaKeyManager = rsaKeyManager;

    }

    @PostMapping("/get")
    public String getBudgets(String sessionKey, String username,String cipherKey, String iv ) {
        SecretKey aesKey = Encryption.decryptAesKey(cipherKey, rsaKeyManager.getPrivateKey());
        IvParameterSpec ivParameter = Encryption.decryptIv(iv, rsaKeyManager.getPrivateKey());

        List<Budget> budgets = budgetRepository.findByOwnerUsername(Encryption.decryptStringParameter(username,aesKey,ivParameter));
        String response = gson.toJson(budgets);
        return Encryption.encryptParameter(response,aesKey,ivParameter);
    }

    @PostMapping("/add")
    public String addBudget(String sessionKey, String username, String name, String cipherKey, String iv ) {
        SecretKey aesKey = Encryption.decryptAesKey(cipherKey, rsaKeyManager.getPrivateKey());
        IvParameterSpec ivParameter = Encryption.decryptIv(iv, rsaKeyManager.getPrivateKey());
        String decryptedName = Encryption.decryptStringParameter(name,aesKey,ivParameter);

        User owner = userRepository.findByUsername(Encryption.decryptStringParameter(username,aesKey,ivParameter));
        int size = budgetRepository.findByNameAndOwner(decryptedName, owner).size();
        boolean result = false;
        if (size == 0) {
            Budget budget = new Budget(decryptedName, owner);
            result = budgetRepository.save(budget) != null;
        }
        return Encryption.encryptParameter(String.valueOf(result),aesKey,ivParameter);
    }

    @PostMapping("/delete")
    public void deleteBudget(String sessionKey, String username, String id, String cipherKey, String iv ) {
        SecretKey aesKey = Encryption.decryptAesKey(cipherKey, rsaKeyManager.getPrivateKey());
        IvParameterSpec ivParameter = Encryption.decryptIv(iv, rsaKeyManager.getPrivateKey());
        long decryptedId = Long.parseLong(Encryption.decryptStringParameter(id,aesKey,ivParameter));

        if (budgetRepository.findOne(decryptedId).getOwner().getUsername().equals(Encryption.decryptStringParameter(username,aesKey,ivParameter))) {
            transactionRepository.delete(transactionRepository.findByRelatedBudgetId(decryptedId));
            categoryRepository.delete(categoryRepository.findByRelatedBudgetId(decryptedId));
            budgetRepository.delete(decryptedId);
        }
    }

}
