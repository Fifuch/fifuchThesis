package pl.put.poznan.whereismymoney.dao;

import pl.put.poznan.whereismymoney.model.Budget;
import pl.put.poznan.whereismymoney.model.User;

import java.util.List;

public interface BudgetDao {
    Budget getById(long id);
    List<Budget> getByUser(User owner);
    void saveOrUpdate(Budget budget);
    void delete(Budget budget);
    void delete(long id);
}
