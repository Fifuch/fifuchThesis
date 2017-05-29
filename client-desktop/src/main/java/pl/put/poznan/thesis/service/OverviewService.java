package pl.put.poznan.thesis.service;

import pl.put.poznan.thesis.dao.CategoryDao;
import pl.put.poznan.thesis.dao.TransactionDao;
import pl.put.poznan.thesis.model.Budget;
import pl.put.poznan.thesis.model.Category;
import pl.put.poznan.thesis.model.Transaction;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class OverviewService {
    private TransactionDao transactionDao;
    private CategoryDao categoryDao;

    @Inject
    public OverviewService(TransactionDao transactionDao, CategoryDao categoryDao) {
        this.transactionDao = transactionDao;
        this.categoryDao = categoryDao;
    }

    public List<Transaction> filterBudgetTransaction(Budget budget, LocalDate from, LocalDate to) {
        return transactionDao.getByBudget(budget).stream().filter(dateFilter(from, to)).collect(Collectors.toList());
    }

    private Predicate<Transaction> dateFilter(LocalDate from, LocalDate to) {
        return transaction ->
                (transaction.getTransactionDate().isBefore(to) && transaction.getTransactionDate().isAfter(from))
                        || transaction.getTransactionDate().isEqual(from)
                        || transaction.getTransactionDate().isEqual(to);
    }

    public void addTransaction(Transaction transaction, Budget budget) {
        transaction.setRelatedBudget(budget);
        transactionDao.saveOrUpdate(transaction);
    }

    public void removeTransaction(Transaction transaction) {
        transactionDao.delete(transaction);
    }

    public List<Category> getCategories(Budget selectedBudget) {
        return categoryDao.getByBudget(selectedBudget);
    }
}
