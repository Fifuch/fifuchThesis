package pl.put.poznan.whereismymoney.service.common;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.put.poznan.whereismymoney.dao.UserRepository;
import pl.put.poznan.whereismymoney.model.User;
import pl.put.poznan.whereismymoney.service.util.ResponseCodes;

@RestController
@RequestMapping("/register")
public class RegistrationService {
    private UserRepository userRepository;
    private Gson gson;

    @Autowired
    public RegistrationService(UserRepository userRepository, Gson gson) {
        this.userRepository = userRepository;
        this.gson = gson;
    }

    @PostMapping("/add")
    public String registerNewUser(String username, String email, String password, String salt) {
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
        return response;
    }

}
