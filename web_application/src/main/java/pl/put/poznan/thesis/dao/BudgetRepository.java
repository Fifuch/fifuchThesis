package pl.put.poznan.thesis.dao;

import org.springframework.data.repository.CrudRepository;
import pl.put.poznan.thesis.model.Budget;
import pl.put.poznan.thesis.model.User;

import java.util.List;

public interface BudgetRepository extends CrudRepository<Budget, Long> {
    List<Budget> findByOwner(User owner);
    List<Budget> findByOwnerUsername(String username);
    List<Budget> findByNameAndOwner(String name, User owner);
}
