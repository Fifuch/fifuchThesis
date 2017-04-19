package pl.put.poznan.whereismymoney.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name ="budgets")
public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "budget_id")
    private long id;

    @Column(name = "name")
    private String name;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "relatedBudget")
    private List<Category> availableCategories;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "transactionAffiliation")
    private List<Transaction> relatedTransactions;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public User getOwner() {
        return owner;
    }
    
    public void setOwner(User owner) {
        this.owner = owner;
    }
    
    public List<Category> getAvailableCategories() {
        return availableCategories;
    }
    
    public void setAvailableCategories(List<Category> availableCategories) {
        this.availableCategories = availableCategories;
    }
    
    public List<Transaction> getRelatedTransactions() {
        return relatedTransactions;
    }

    public void setRelatedTransactions(List<Transaction> relatedTransactions) {
        this.relatedTransactions = relatedTransactions;
    }
}
