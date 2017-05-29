package pl.put.poznan.thesis.dao;

import org.springframework.data.repository.CrudRepository;
import pl.put.poznan.thesis.model.Budget;
import pl.put.poznan.thesis.model.Category;

import java.util.List;

public interface CategoryRepository extends CrudRepository<Category, Long> {
    Category findByRelatedBudgetAndName(Budget relatedBudget, String name);
    List<Category> findByRelatedBudget(Budget budget);
    List<Category> findByRelatedBudgetId(long relatedBudget_id);
}
