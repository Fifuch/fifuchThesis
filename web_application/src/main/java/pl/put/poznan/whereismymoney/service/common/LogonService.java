package pl.put.poznan.whereismymoney.service.common;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.put.poznan.whereismymoney.crypto.Encryption;
import pl.put.poznan.whereismymoney.crypto.RSAKeyManager;
import pl.put.poznan.whereismymoney.dao.UserRepository;
import pl.put.poznan.whereismymoney.model.User;
import pl.put.poznan.whereismymoney.security.authentication.SessionKeyGenerator;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;

@RestController
@RequestMapping("/logon")
public class LogonService {
    private UserRepository userRepository;
    private Gson gson;
    private SessionKeyGenerator sessionKeyGenerator;
    private RSAKeyManager rsaKeyManager;

    @Autowired
    public LogonService(UserRepository userRepository, Gson gson, SessionKeyGenerator sessionKeyGenerator, RSAKeyManager rsaKeyManager) {
        this.userRepository = userRepository;
        this.gson = gson;
        this.sessionKeyGenerator = sessionKeyGenerator;
        this.rsaKeyManager = rsaKeyManager;
    }

    @PostMapping("/salt")
    public String getSalt(String username, String cipherKey, String iv) {

        SecretKey aesKey = Encryption.decryptAesKey(cipherKey, rsaKeyManager.getPrivateKey());
        IvParameterSpec ivParameter = Encryption.decryptIv(iv, rsaKeyManager.getPrivateKey());
        byte[] salt = new byte[0];
        String decryptedUsername = Encryption.decryptStringParameter(username, aesKey, ivParameter);
        User user = userRepository.findByUsername(decryptedUsername);
        if (user != null) {
            salt = user.getSalt();
        }
        return Encryption.encryptParameter(salt,aesKey,ivParameter);
    }

    @PostMapping("/verify")
    public String verifySessionKey(String username, String sessionKey, String cipherKey, String iv) {
        SecretKey aesKey = Encryption.decryptAesKey(cipherKey, rsaKeyManager.getPrivateKey());
        IvParameterSpec ivParameter = Encryption.decryptIv(iv, rsaKeyManager.getPrivateKey());
        byte[] obtainedSessionKey = Encryption.decryptByteArrayParameter(sessionKey,aesKey,ivParameter);

        String decryptedUsername = Encryption.decryptStringParameter(username,aesKey,ivParameter);
        User user = userRepository.findByUsername(decryptedUsername);

        Boolean verified = false;
        if (user != null) {
            byte[] generatedSessionKey = sessionKeyGenerator.generate(user);
            verified = Arrays.equals(obtainedSessionKey, generatedSessionKey);
        }
        return Encryption.encryptParameter(verified.toString(),aesKey,ivParameter);
    }
}
