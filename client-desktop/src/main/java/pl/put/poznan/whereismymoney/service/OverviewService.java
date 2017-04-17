package pl.put.poznan.whereismymoney.service;

import pl.put.poznan.whereismymoney.dao.TransactionDao;
import pl.put.poznan.whereismymoney.model.Budget;
import pl.put.poznan.whereismymoney.model.Transaction;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class OverviewService {
    private TransactionDao transactionDao;

    @Inject
    public OverviewService(TransactionDao transactionDao) {
        this.transactionDao = transactionDao;
    }

    public List<Transaction> filterTransaction(List<Transaction> transactions, LocalDate from, LocalDate to) {
        return transactions.stream().filter(dateFilter(from, to)).collect(Collectors.toList());
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
}
