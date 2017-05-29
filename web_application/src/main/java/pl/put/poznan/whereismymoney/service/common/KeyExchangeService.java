package pl.put.poznan.whereismymoney.service.common;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.put.poznan.whereismymoney.crypto.RSAKeyManager;
import pl.put.poznan.whereismymoney.dao.UserRepository;
import pl.put.poznan.whereismymoney.model.User;
import pl.put.poznan.whereismymoney.security.authentication.SessionKeyGenerator;

import java.security.Key;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;

@RestController
@RequestMapping("/publicKey")
public class KeyExchangeService {

    private Gson gson;
    private RSAKeyManager rsaKeyManager;

    @Autowired
    public KeyExchangeService(Gson gson, RSAKeyManager rsaKeyManager) {
        this.gson = gson;
        this.rsaKeyManager = rsaKeyManager;
    }

    @PostMapping("/get")
    public String getPublicKey() {
            RSAPublicKeySpec publicKey = rsaKeyManager.getRSAPublicKeySpec();
           return gson.toJson(publicKey);

    }

}
