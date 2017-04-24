package pl.put.poznan.whereismymoney.service;

import pl.put.poznan.whereismymoney.dao.BudgetDao;
import pl.put.poznan.whereismymoney.dao.CategoryDao;
import pl.put.poznan.whereismymoney.dao.TransactionDao;
import pl.put.poznan.whereismymoney.model.Budget;
import pl.put.poznan.whereismymoney.model.Category;
import pl.put.poznan.whereismymoney.model.Transaction;
import pl.put.poznan.whereismymoney.model.TransactionType;
import pl.put.poznan.whereismymoney.security.SessionManager;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class AnalysesService {
    private BudgetDao budgetDao;
    private CategoryDao categoryDao;
    private SessionManager sessionManager;
    private TransactionDao transactionDao;

    @Inject
    public AnalysesService(BudgetDao budgetDao, CategoryDao categoryDao, SessionManager sessionManager,
                           TransactionDao transactionDao) {
        this.budgetDao = budgetDao;
        this.categoryDao = categoryDao;
        this.sessionManager = sessionManager;
        this.transactionDao = transactionDao;
    }

    public List<Budget> getBudgets() {
        return budgetDao.getByUser(sessionManager.getUserData());
    }

    public BigDecimal getStake(BigDecimal divisor, BigDecimal dividend) {
        BigDecimal percentage = new BigDecimal(0);
        if (divisor.longValue() != 0) {
            percentage = dividend.divide(divisor, 4, RoundingMode.HALF_UP).scaleByPowerOfTen(2);
        }
        return percentage;
    }

    public BigDecimal getOverallOutcome(List<Category> categories, LocalDate from, LocalDate to) {
        BigDecimal result = new BigDecimal(0);
        for (Category category : categories) {
            result = result.add(getOutcome(category, from, to));
        }
        return result;
    }

    public BigDecimal getIncome(List<Category> categories, LocalDate from, LocalDate to) {
        BigDecimal result = new BigDecimal(0);
        for (Category category : categories) {
            result = result.add(getTransactionAmount(category, from, to, TransactionType.INCOME));
        }
        return result;
    }

    public BigDecimal getOutcome(Category category, LocalDate from, LocalDate to) {
        return getTransactionAmount(category, from, to, TransactionType.OUTCOME);
    }

    private BigDecimal getTransactionAmount(Category category, LocalDate from, LocalDate to, TransactionType transactionType) {
        BigDecimal amount = new BigDecimal(0);
        Iterator<Transaction> iterator = transactionDao.getByCategory(category).stream()
                .filter(dateFilter(from, to))
                .filter(transaction -> transaction.getTransactionType().equals(transactionType))
                .iterator();
        while (iterator.hasNext()) {
            amount = amount.add(iterator.next().getAmount());
        }
        return amount;
    }

    private Predicate<Transaction> dateFilter(LocalDate from, LocalDate to) {
        return transaction ->
                (transaction.getTransactionDate().isBefore(to) && transaction.getTransactionDate().isAfter(from))
                        || transaction.getTransactionDate().isEqual(from)
                        || transaction.getTransactionDate().isEqual(to);
    }

    public List<Category> getCategories(Budget budget) {
        return categoryDao.getByBudget(budget);
    }
}
