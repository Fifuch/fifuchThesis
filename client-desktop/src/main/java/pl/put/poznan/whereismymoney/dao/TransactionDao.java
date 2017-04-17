package pl.put.poznan.whereismymoney.dao;

import pl.put.poznan.whereismymoney.model.Category;
import pl.put.poznan.whereismymoney.model.Transaction;

import java.time.LocalDate;
import java.util.List;

public interface TransactionDao {
    Transaction getById(long id);
    List<Transaction> getByCategory(Category category);
    List<Transaction> getByDate(LocalDate transactionDateFrom, LocalDate transactionDateTo);
    void saveOrUpdate(Transaction transaction);
    void delete(Transaction transaction);
    void delete(long id);
}
