package pl.put.poznan.whereismymoney.service.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.put.poznan.whereismymoney.dao.UserRepository;
import pl.put.poznan.whereismymoney.model.User;

@RestController
@RequestMapping("/registration")
public class RegistrationService {
    private UserRepository userRepository;
    
    @Autowired
    public RegistrationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @RequestMapping("/add")
    public long registrateUser(String username, String email, byte[] password) {
        long id = 0L;
        User user = new User(username, email, password);
        if(!doesUserExist(username, email)) {
            user = userRepository.save(user);
            id = user.getId();
        }
        return id;
    }
    
    private boolean doesUserExist(String username, String email) {
        return userRepository.findByEmail(email) != null || userRepository.findByUsername(username) != null;
    }
}
