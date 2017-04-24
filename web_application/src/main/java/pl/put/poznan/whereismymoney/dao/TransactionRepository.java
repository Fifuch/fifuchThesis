package pl.put.poznan.whereismymoney.dao;

import org.springframework.data.repository.CrudRepository;
import pl.put.poznan.whereismymoney.model.Transaction;

import java.util.List;

public interface TransactionRepository extends CrudRepository<Transaction, Long> {
    List<Transaction> findByRelatedBudgetId(long relatedBudget_id);
    List<Transaction> findByTransactionCategoryId(long categoryId);
}
