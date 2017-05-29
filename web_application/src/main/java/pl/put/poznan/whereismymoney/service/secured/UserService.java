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
import pl.put.poznan.whereismymoney.model.User;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

@RestController
@RequestMapping("/user")
public class UserService {
    private UserRepository userRepository;
    private Gson gson;
    private RSAKeyManager rsaKeyManager;


    @Autowired
    public UserService(UserRepository userRepository, Gson gson, RSAKeyManager rsaKeyManager) {
        this.userRepository = userRepository;
        this.gson = gson;
        this.rsaKeyManager = rsaKeyManager;

    }

    @PostMapping("/modify")
    public String updateUser(String sessionKey, String username, String modifiedUser, String cipherKey, String iv) {
        SecretKey aesKey = Encryption.decryptAesKey(cipherKey, rsaKeyManager.getPrivateKey());
        IvParameterSpec ivParameter = Encryption.decryptIv(iv, rsaKeyManager.getPrivateKey());

        User newUser = gson.fromJson(Encryption.decryptStringParameter(modifiedUser,aesKey,ivParameter), User.class);
        boolean modificationSuccessful = true;
        if(userRepository.findByUsername(newUser.getUsername()) != null || userRepository.findByEmail(newUser.getEmail()) != null) {
            modificationSuccessful = false;
        }
        User oldUser = userRepository.findByUsername(Encryption.decryptStringParameter(username,aesKey,ivParameter));
        if (oldUser.getId() == newUser.getId() && modificationSuccessful) {
            modificationSuccessful = userRepository.save(newUser) != null;
        } else {
            modificationSuccessful = false;
        }
        return Encryption.encryptParameter(String.valueOf(modificationSuccessful),aesKey,ivParameter);
    }

    @PostMapping("/get")
    public String getUser(String sessionKey, String username, String cipherKey, String iv) {
        SecretKey aesKey = Encryption.decryptAesKey(cipherKey, rsaKeyManager.getPrivateKey());
        IvParameterSpec ivParameter = Encryption.decryptIv(iv, rsaKeyManager.getPrivateKey());
        String decryptedUsername = Encryption.decryptStringParameter(username,aesKey,ivParameter);


        return Encryption.encryptParameter(gson.toJson(userRepository.findByUsername(decryptedUsername)),aesKey,ivParameter);
    }
}
