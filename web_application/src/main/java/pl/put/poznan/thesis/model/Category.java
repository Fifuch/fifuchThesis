package pl.put.poznan.thesis.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "limitation")
    private BigDecimal limit;

    @ManyToOne
    @JoinColumn(name = "budget_id")
    private Budget relatedBudget;

    public Category() {

    }

    public Category(String name, Budget budget) {
        this.name = name;
        this.relatedBudget = budget;
    }

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

    public BigDecimal getLimit() {
        return limit;
    }

    public void setLimit(BigDecimal limit) {
        this.limit = limit;
    }

    public Budget getRelatedBudget() {
        return relatedBudget;
    }

    public void setRelatedBudget(Budget relatedBudget) {
        this.relatedBudget = relatedBudget;
    }
}
