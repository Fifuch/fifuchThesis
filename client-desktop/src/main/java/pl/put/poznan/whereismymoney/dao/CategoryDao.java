package pl.put.poznan.whereismymoney.dao;

import pl.put.poznan.whereismymoney.model.Budget;
import pl.put.poznan.whereismymoney.model.Category;

import java.util.List;

public interface CategoryDao {
    Category getById(long id);
    List<Category> getByBudget(Budget budget);
    void saveOrUpdate(Category category);
    void delete(Category category);
    void delete(long id);
}
