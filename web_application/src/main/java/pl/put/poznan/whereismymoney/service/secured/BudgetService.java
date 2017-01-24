package pl.put.poznan.whereismymoney.service.secured;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.put.poznan.whereismymoney.dao.BudgetRepository;
import pl.put.poznan.whereismymoney.model.Budget;

@RestController
@RequestMapping("/{userId}/budget")
public class BudgetService {
    BudgetRepository budgetRepository;
    
    @Autowired
    public BudgetService(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }
    
    @RequestMapping("/get")
    public Budget getConcreteBudget(@PathVariable("userId") final long userId, String token, long budgetId) {
        return budgetRepository.findOne(budgetId);
    }
    
    @RequestMapping("/add")
    public boolean addBudget(String token, String name) {return false;}
    
}
