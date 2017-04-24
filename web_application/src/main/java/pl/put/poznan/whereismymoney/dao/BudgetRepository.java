package pl.put.poznan.whereismymoney.dao;

import org.springframework.data.repository.CrudRepository;
import pl.put.poznan.whereismymoney.model.Budget;
import pl.put.poznan.whereismymoney.model.User;

import java.util.List;

public interface BudgetRepository extends CrudRepository<Budget, Long> {
    List<Budget> findByOwner(User owner);
    List<Budget> findByOwnerUsername(String username);
    List<Budget> findByNameAndOwner(String name, User owner);
}
