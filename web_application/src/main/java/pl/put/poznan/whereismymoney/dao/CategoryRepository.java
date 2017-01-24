package pl.put.poznan.whereismymoney.dao;

import org.springframework.data.repository.CrudRepository;
import pl.put.poznan.whereismymoney.model.Budget;
import pl.put.poznan.whereismymoney.model.Category;

import java.util.List;

public interface CategoryRepository extends CrudRepository<Category, Long> {
    List<Category> findByRelatedBudgetAndName(Budget relatedBudget, String name);
}
