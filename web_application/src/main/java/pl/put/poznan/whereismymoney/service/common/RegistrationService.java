package pl.put.poznan.whereismymoney.service.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pl.put.poznan.whereismymoney.dao.UserRepository;
import pl.put.poznan.whereismymoney.model.User;
import pl.put.poznan.whereismymoney.service.util.ResponseCodes;

@RestController
@RequestMapping("/register")
public class RegistrationService {
    private UserRepository userRepository;

    @Autowired
    public RegistrationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String registerNewUser(String username, String email, byte[] password) {
        String response = ResponseCodes.REGISTERED.toString();
        if (userRepository.findByUsername(username) != null) {
            response = ResponseCodes.LOGIN_ALREADY_IN_USE.toString();
        } else if (userRepository.findByEmail(email) != null) {
            response = ResponseCodes.EMAIL_ALREADY_IN_USE.toString();
        } else {
            User user = new User(username, email, password);
            userRepository.save(user);
        }
        return response;
    }

}
