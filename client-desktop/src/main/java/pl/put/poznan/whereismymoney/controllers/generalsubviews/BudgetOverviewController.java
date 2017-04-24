package pl.put.poznan.whereismymoney.controllers.generalsubviews;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import pl.put.poznan.whereismymoney.controllers.Controller;
import pl.put.poznan.whereismymoney.gui.utils.DialogFactory;
import pl.put.poznan.whereismymoney.gui.utils.builder.TransactionDialogBuilder;
import pl.put.poznan.whereismymoney.model.Budget;
import pl.put.poznan.whereismymoney.model.Transaction;
import pl.put.poznan.whereismymoney.model.TransactionType;
import pl.put.poznan.whereismymoney.service.OverviewService;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class BudgetOverviewController implements GeneralSubviewController {
    private Budget selectedBudget;
    private Controller generalViewController;
    private OverviewService overviewService;

    @FXML
    private DatePicker from, to;
    @FXML
    private TableView<Transaction> transactions;
    @FXML
    private TableColumn<Transaction, BigDecimal> amount;
    @FXML
    private TableColumn<Transaction, LocalDate> date;
    @FXML
    private TableColumn<Transaction, TransactionType> type;
    @FXML
    private TableColumn<Transaction, String> category;

    @Inject
    public BudgetOverviewController(OverviewService overviewService) {
        this.overviewService = overviewService;
    }

    @FXML
    private void initialize() {
        amount.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper(cellData.getValue().getAmount()));
        date.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper(cellData.getValue().getTransactionDate()));
        type.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper(cellData.getValue().getTransactionType()));
        category.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper(cellData.getValue().getTransactionCategory().getName()));
    }

    @FXML
    private void addTransaction() {
        if(selectedBudget != null) {
            Optional<Transaction> transaction = DialogFactory
                    .get(new TransactionDialogBuilder(overviewService.getCategories(selectedBudget)))
                    .showAndWait();
            transaction.ifPresent(value -> overviewService.addTransaction(value, selectedBudget));
            generalViewController.refresh();
        }
    }

    @FXML
    private void removeTransaction() {
        Optional<Transaction> transaction = Optional.of(transactions.getSelectionModel().getSelectedItem());
        transaction.ifPresent(value -> overviewService.removeTransaction(value));
        generalViewController.refresh();
    }

    @FXML
    private void onDateSelection() {
        refresh();
    }

    @Override
    public void refresh() {
        if (selectedBudget != null) {
            if (!(from.getValue() == null || to.getValue() == null)) {
                List<Transaction> filteredTransactions = overviewService
                        .filterBudgetTransaction(selectedBudget, from.getValue(), to.getValue());
                transactions.setItems(FXCollections.observableArrayList(filteredTransactions));
            }
        } else {
            transactions.setItems(null);
        }
    }

    @Override
    public void clear() {
        transactions.setItems(null);
    }

    @Override
    public void setSelectedBudget(Budget selectedBudget) {
        this.selectedBudget = selectedBudget;
    }

    @Override
    public void setGeneralViewController(Controller generalViewController) {
        this.generalViewController = generalViewController;
    }
}
