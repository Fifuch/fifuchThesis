package pl.put.poznan.whereismymoney.controllers.generalsubviews;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import pl.put.poznan.whereismymoney.controllers.Controller;
import pl.put.poznan.whereismymoney.gui.utils.DialogFactory;
import pl.put.poznan.whereismymoney.gui.utils.builder.WarningBuilder;
import pl.put.poznan.whereismymoney.gui.utils.builder.CategoryDialogBuilder;
import pl.put.poznan.whereismymoney.model.Budget;
import pl.put.poznan.whereismymoney.model.Category;
import pl.put.poznan.whereismymoney.service.ManagementService;

import javax.inject.Inject;
import java.util.Optional;

public class BudgetManagementController implements GeneralSubviewController {
    private Budget selectedBudget;
    private Controller generalViewController;
    private ManagementService managementService;

    @FXML
    private TextField categoryLimit;
    @FXML
    private Label currentLimit;
    @FXML
    private ListView<Category> categories;

    @Inject
    public BudgetManagementController(ManagementService managementService) {
        this.managementService = managementService;
    }

    @FXML
    public void addCategory() {
        boolean operationSuccessful = false;
        Optional<String> name = DialogFactory.get(new CategoryDialogBuilder()).showAndWait();
        if(name.isPresent() && selectedBudget != null) {
            operationSuccessful = managementService.addCategory(name.get(), selectedBudget);
            if (operationSuccessful) {
                generalViewController.refresh();
            } else {
                DialogFactory.get(new WarningBuilder("Category creation error", "Category named " + name.get() + " is already created.")).showAndWait();
            }
        }
    }

    @FXML
    private void addLimit() {
        Category category = categories.getSelectionModel().getSelectedItem();
        String limit = categoryLimit.getText();
        if (category != null) {
            managementService.setNewLimit(category, limit);
            refresh();
        }
    }

    @FXML
    private void cancelLimit() {
        categoryLimit.clear();
        currentLimit.setText("-");
    }

    @FXML
    private void deleteBudget() {
        managementService.delete(selectedBudget);
        generalViewController.refresh();
    }

    @FXML
    public void deleteCategory() {
        Category selected = categories.getSelectionModel().getSelectedItem();
        managementService.delete(selected);
        generalViewController.refresh();
    }

    @FXML
    private void onCategorySelection() {
        Category category = categories.getSelectionModel().getSelectedItem();
        if (category != null) {
            currentLimit.setText(category.getLimit().toPlainString() + " PLN");
        }
    }

    @Override
    public void clear() {
        categoryLimit.clear();
        currentLimit.setText("-");
        categories.getSelectionModel().clearSelection();
    }

    @Override
    public void refresh() {
        if (selectedBudget != null) {
            selectedBudget = managementService.get(selectedBudget);
            categories.setItems(FXCollections.observableArrayList(selectedBudget.getAvailableCategories()));
        } else {
            categories.setItems(null);
        }
    }

    @Override
    public void setGeneralViewController(Controller generalViewController) {
        this.generalViewController = generalViewController;
    }

    @Override
    public void setSelectedBudget(Budget selectedBudget) {
        this.selectedBudget = selectedBudget;
    }

}
