package pl.put.poznan.thesis.service.common;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.put.poznan.thesis.crypto.Encryption;
import pl.put.poznan.thesis.crypto.RSAKeyManager;
import pl.put.poznan.thesis.dao.UserRepository;
import pl.put.poznan.thesis.model.User;
import pl.put.poznan.thesis.service.util.ResponseCodes;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

@RestController
@RequestMapping("/register")
public class RegistrationService {
    private UserRepository userRepository;
    private Gson gson;
    private RSAKeyManager rsaKeyManager;
    private SecretKey aesKey;
    IvParameterSpec ivParameter;

    @Autowired
    public RegistrationService(UserRepository userRepository, Gson gson, RSAKeyManager rsaKeyManager) {
        this.userRepository = userRepository;
        this.gson = gson;
        this.rsaKeyManager = rsaKeyManager;
    }

    @PostMapping("/add")
    public String registerNewUser(String username, String email, String password, String salt, String cipherKey, String iv) {
        aesKey = Encryption.decryptAesKey(cipherKey, rsaKeyManager.getPrivateKey());
        ivParameter = Encryption.decryptIv(iv, rsaKeyManager.getPrivateKey());

        username = Encryption.decryptStringParameter(username, aesKey, ivParameter);
        email = Encryption.decryptStringParameter(email, aesKey, ivParameter);
        password = Encryption.decryptStringParameter(password, aesKey, ivParameter);
        salt = Encryption.decryptStringParameter(salt, aesKey, ivParameter);

        String response = ResponseCodes.REGISTERED.toString();
        if (userRepository.findByUsername(username) != null) {
            response = ResponseCodes.LOGIN_ALREADY_IN_USE.toString();
        } else if (userRepository.findByEmail(email) != null) {
            response = ResponseCodes.EMAIL_ALREADY_IN_USE.toString();
        } else {
            byte[] passwordBytes = gson.fromJson(password, byte[].class);
            byte[] saltBytes = gson.fromJson(salt, byte[].class);
            User user = new User(username, email, passwordBytes, saltBytes);
            userRepository.save(user);
        }
        return Encryption.encryptParameter(response, aesKey, ivParameter);
    }

}
