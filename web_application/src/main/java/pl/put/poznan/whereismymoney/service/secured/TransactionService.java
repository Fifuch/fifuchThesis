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
import pl.put.poznan.whereismymoney.model.Transaction;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.util.List;

@RestController
@RequestMapping("/transaction")
public class TransactionService {
    private BudgetRepository budgetRepository;
    private CategoryRepository categoryRepository;
    private TransactionRepository transactionRepository;
    private Gson gson;
    private RSAKeyManager rsaKeyManager;


    @Autowired
    public TransactionService(BudgetRepository budgetRepository, CategoryRepository categoryRepository,
                              TransactionRepository transactionRepository, Gson gson, RSAKeyManager rsaKeyManager) {
        this.budgetRepository = budgetRepository;
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
        this.gson = gson;
        this.rsaKeyManager = rsaKeyManager;

    }

    @PostMapping("/getByBudget")
    public String getTransactionsByBudget(String sessionKey, String username, String budgetId, String cipherKey, String iv) {
        SecretKey aesKey = Encryption.decryptAesKey(cipherKey, rsaKeyManager.getPrivateKey());
        IvParameterSpec ivParameter = Encryption.decryptIv(iv, rsaKeyManager.getPrivateKey());
        long decryptedBudgetId = Long.parseLong(Encryption.decryptStringParameter(budgetId,aesKey,ivParameter));

        Budget budget = budgetRepository.findOne(decryptedBudgetId);
        String response = "[]";
        if (budget.getOwner().getUsername().equals(Encryption.decryptStringParameter(username,aesKey,ivParameter))) {
            List<Transaction> transactions = transactionRepository.findByRelatedBudgetId(decryptedBudgetId);
            response = gson.toJson(transactions);
        }
        return Encryption.encryptParameter(response,aesKey,ivParameter);
    }

    @PostMapping("/getByCategory")
    public String getTransactionsByCategory(String sessionKey, String username, String categoryId, String cipherKey, String iv) {
        SecretKey aesKey = Encryption.decryptAesKey(cipherKey, rsaKeyManager.getPrivateKey());
        IvParameterSpec ivParameter = Encryption.decryptIv(iv, rsaKeyManager.getPrivateKey());
        long decryptedCategoryId = Long.parseLong(Encryption.decryptStringParameter(categoryId,aesKey,ivParameter));

        boolean authorizationSuccessful = categoryRepository.findOne(decryptedCategoryId).
                getRelatedBudget().getOwner().getUsername().equals(Encryption.decryptStringParameter(username,aesKey,ivParameter));
        String response = "[]";
        if (authorizationSuccessful) {
            List<Transaction> transactions = transactionRepository.findByTransactionCategoryId(decryptedCategoryId);
            response = gson.toJson(transactions);
        }
        return Encryption.encryptParameter(response,aesKey,ivParameter);
    }

    @PostMapping("/add")
    public String addBudget(String sessionKey, String username, String transaction, String cipherKey, String iv) {
        SecretKey aesKey = Encryption.decryptAesKey(cipherKey, rsaKeyManager.getPrivateKey());
        IvParameterSpec ivParameter = Encryption.decryptIv(iv, rsaKeyManager.getPrivateKey());

        Transaction newTransaction = gson.fromJson(transaction, Transaction.class);
        if (newTransaction.getRelatedBudget().getOwner().getUsername().equals(Encryption.decryptStringParameter(username,aesKey,ivParameter))) {
            return Encryption.encryptParameter(String.valueOf(transactionRepository.save(newTransaction) != null),aesKey,ivParameter);
        }
        return Encryption.encryptParameter(String.valueOf(false),aesKey,ivParameter);
    }

    @PostMapping("/delete")
    public void deleteCategory(String sessionKey, String username, long transactionId, String cipherKey, String iv) {
        SecretKey aesKey = Encryption.decryptAesKey(cipherKey, rsaKeyManager.getPrivateKey());
        IvParameterSpec ivParameter = Encryption.decryptIv(iv, rsaKeyManager.getPrivateKey());

        boolean authorizationSuccessful = transactionRepository.findOne(transactionId)
                .getRelatedBudget().getOwner().getUsername().equals(username);
        if (authorizationSuccessful) {
            transactionRepository.delete(transactionId);
        }
    }
}
