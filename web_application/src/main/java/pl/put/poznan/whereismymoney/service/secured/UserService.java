package pl.put.poznan.whereismymoney.service.secured;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.put.poznan.whereismymoney.dao.BudgetRepository;
import pl.put.poznan.whereismymoney.dao.CategoryRepository;
import pl.put.poznan.whereismymoney.dao.TransactionRepository;
import pl.put.poznan.whereismymoney.dao.UserRepository;
import pl.put.poznan.whereismymoney.model.User;

@RestController
@RequestMapping("/user")
public class UserService {
    private UserRepository userRepository;
    private Gson gson;

    @Autowired
    public UserService(UserRepository userRepository, Gson gson) {
        this.userRepository = userRepository;
        this.gson = gson;
    }

    @PostMapping("/modify")
    public boolean updateUser(String sessionKey, String username, String modifiedUser) {
        User newUser = gson.fromJson(modifiedUser, User.class);
        boolean modificationSuccessful = true;
        if(userRepository.findByUsername(newUser.getUsername()) != null || userRepository.findByEmail(newUser.getEmail()) != null) {
            modificationSuccessful = false;
        }
        User oldUser = userRepository.findByUsername(username);
        if (oldUser.getId() == newUser.getId() && modificationSuccessful) {
            modificationSuccessful = userRepository.save(newUser) != null;
        } else {
            modificationSuccessful = false;
        }
        return modificationSuccessful;
    }

    @PostMapping("/get")
    public String getUser(String sessionKey, String username) {
        return gson.toJson(userRepository.findByUsername(username));
    }
}
