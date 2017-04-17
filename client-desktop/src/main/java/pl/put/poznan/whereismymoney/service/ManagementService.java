package pl.put.poznan.whereismymoney.service;

import pl.put.poznan.whereismymoney.dao.BudgetDao;
import pl.put.poznan.whereismymoney.dao.CategoryDao;
import pl.put.poznan.whereismymoney.model.Budget;
import pl.put.poznan.whereismymoney.model.Category;

import javax.inject.Inject;
import java.math.BigDecimal;

public class ManagementService {
    private BudgetDao budgetDao;
    private CategoryDao categoryDao;

    @Inject
    public ManagementService(BudgetDao budgetDao, CategoryDao categoryDao) {
        this.budgetDao = budgetDao;
        this.categoryDao = categoryDao;
    }

    public void setNewLimit(Category categoryToUpdate, String limit) {
        double numericalLimit = 0.0;
        try {
            numericalLimit = Double.parseDouble(limit);
        } catch (NumberFormatException nfe) {
        }
        BigDecimal newLimit = new BigDecimal(numericalLimit);
        categoryToUpdate.setLimit(newLimit);
        categoryDao.saveOrUpdate(categoryToUpdate);
    }

    public void delete(Budget budget) {
        budgetDao.delete(budget);
    }

    public Budget get(Budget budget) {
        return budgetDao.getById(budget.getId());
    }

    public void delete(Category category) {
        categoryDao.delete(category);
    }

    public boolean addCategory(String name, Budget relatedBudget) {
        boolean transactionResult = false;
        if (categoryDao.getByBudget(relatedBudget).stream()
                .filter(category -> category.getName().equals(name))
                .count() == 0) {
            Category category = new Category();
            category.setName(name);
            category.setLimit(new BigDecimal(0));
            category.setRelatedBudget(relatedBudget);
            categoryDao.saveOrUpdate(category);
            transactionResult = true;
        }
        return transactionResult;
    }
}
