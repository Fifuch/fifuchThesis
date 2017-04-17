package pl.put.poznan.whereismymoney.dao;

import pl.put.poznan.whereismymoney.model.User;

public interface UserDao {
    User getById(long id);
    User getByUsername(String username);
    User getByEmail(String email);
    void saveOrUpdate(User User);
    void delete(User User);
    void delete(long id);
}
