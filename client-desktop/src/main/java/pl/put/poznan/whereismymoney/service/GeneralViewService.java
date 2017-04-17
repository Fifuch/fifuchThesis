package pl.put.poznan.whereismymoney.service;

import pl.put.poznan.whereismymoney.dao.BudgetDao;
import pl.put.poznan.whereismymoney.model.Budget;
import pl.put.poznan.whereismymoney.security.SessionManager;

import javax.inject.Inject;
import java.util.List;

public class GeneralViewService {
    private BudgetDao budgetDao;
    private SessionManager sessionManager;

    @Inject
    public GeneralViewService(BudgetDao budgetDao, SessionManager sessionManager) {
        this.budgetDao = budgetDao;
        this.sessionManager = sessionManager;
    }

    public List<Budget> getBudgets() {
        return budgetDao.getByUser(sessionManager.getUserData());
    }

    public boolean addBudget(String name) {
        boolean transactionResult = false;
        Budget budget = new Budget();
        if (budgetDao.getByUser(sessionManager.getUserData()).stream()
                .filter(currentBudget -> currentBudget.getName().equals(name))
                .count() == 0) {
            budget.setName(name);
            budget.setOwner(sessionManager.getUserData());
            budgetDao.saveOrUpdate(budget);
            transactionResult = true;
        }
        return transactionResult;
    }
}
