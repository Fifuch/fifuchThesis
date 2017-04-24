package pl.put.poznan.whereismymoney.service.common;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.put.poznan.whereismymoney.dao.UserRepository;
import pl.put.poznan.whereismymoney.model.User;
import pl.put.poznan.whereismymoney.security.authentication.SessionKeyGenerator;

import java.util.Arrays;

@RestController
@RequestMapping("/logon")
public class LogonService {
    private UserRepository userRepository;
    private Gson gson;
    private SessionKeyGenerator sessionKeyGenerator;

    @Autowired
    public LogonService(UserRepository userRepository, Gson gson, SessionKeyGenerator sessionKeyGenerator) {
        this.userRepository = userRepository;
        this.gson = gson;
        this.sessionKeyGenerator = sessionKeyGenerator;
    }

    @PostMapping("/salt")
    public String getSalt(String username) {
        byte[] salt = new byte[0];
        User user = userRepository.findByUsername(username);
        if (user != null) {
            salt = user.getSalt();
        }
        return gson.toJson(salt);
    }

    @PostMapping("/verify")
    public boolean verifySessionKey(String username, String sessionKey) {
        byte[] obtainedSessionKey = gson.fromJson(sessionKey, byte[].class);
        User user = userRepository.findByUsername(username);
        boolean verified = false;
        if (user != null) {
            byte[] generatedSessionKey = sessionKeyGenerator.generate(user);
            verified = Arrays.equals(obtainedSessionKey, generatedSessionKey);
        }
        return verified;
    }
}