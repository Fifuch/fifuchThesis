package pl.put.poznan.whereismymoney.dao;

import org.springframework.data.repository.CrudRepository;
import pl.put.poznan.whereismymoney.model.Budget;
import pl.put.poznan.whereismymoney.model.Category;
import pl.put.poznan.whereismymoney.model.Transaction;
import pl.put.poznan.whereismymoney.model.TransactionType;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends CrudRepository<Transaction, Long> {
    List<Transaction> findByTransactionAffiliationAndTransactionDate(Budget transactionAffiliation, LocalDate transactionDate);
    List<Transaction> findByTransactionAffiliationAndTransactionCategory(Budget transactionAffiliation, Category transactionCategory);
    List<Transaction> findByTransactionAffiliationAndTransactionType(Budget transactionAffiliation, TransactionType transactionType);
}
