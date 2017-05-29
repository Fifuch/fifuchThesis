package pl.put.poznan.thesis.dao;

import org.springframework.data.repository.CrudRepository;
import pl.put.poznan.thesis.model.User;

public interface UserRepository extends CrudRepository<User, Long> {
    User findByUsername(String username);
    User findByEmail(String email);
}
