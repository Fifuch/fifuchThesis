package pl.put.poznan.whereismymoney.gui.utils.builder;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import pl.put.poznan.whereismymoney.model.Category;
import pl.put.poznan.whereismymoney.model.Transaction;
import pl.put.poznan.whereismymoney.model.TransactionType;

import java.math.BigDecimal;
import java.util.List;

public class TransactionDialogBuilder implements DialogBuilder {
    private TextField amount;
    private ComboBox<TransactionType> transactionTypes;
    private ComboBox<Category> categories;
    private DatePicker transactionDatePicker;

    public TransactionDialogBuilder(List<Category> availableCategories) {
        categories = new ComboBox<>();
        categories.getItems().addAll(availableCategories);
        transactionTypes = new ComboBox<>();
        transactionTypes.getItems().addAll(TransactionType.values());
        transactionDatePicker = new DatePicker();
        amount = new TextField();
    }

    @Override
    public Dialog construct() {
        Dialog<Transaction> result = new Dialog<>();
        result.setTitle("Transaction Wizard");
        result.setHeaderText("Add new transaction");
        result.getDialogPane().setContent(createContent());
        result.getDialogPane().getButtonTypes().addAll(createButtonList());
        result.setResultConverter(this::createTransaction);
        return result;
    }

    private Node createContent() {
        GridPane grid = createGrid();
        grid = fillWithContent(grid);
        return grid;
    }

    private GridPane createGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        return grid;
    }

    private GridPane fillWithContent(GridPane grid) {
        grid.add(new Label("Amount: "), 0, 0);
        grid.add(amount, 1, 0);

        grid.add(new Label("Transaction's date: "), 0, 1);
        grid.add(transactionDatePicker, 1, 1);

        grid.add(new Label("Transaction's type: "), 0, 2);
        grid.add(transactionTypes, 1, 2);

        grid.add(new Label("Categories: "), 0, 3);
        grid.add(categories, 1, 3);
        return grid;
    }

    private ButtonType[] createButtonList() {
        return new ButtonType[]{ButtonType.FINISH, ButtonType.CANCEL};
    }

    private Transaction createTransaction(ButtonType clickedButton) {
        Transaction transaction = null;
        if (clickedButton == ButtonType.FINISH && everythingIsFilled()) {
            transaction = new Transaction();
            transaction.setAmount(new BigDecimal(amount.getText()));
            transaction.setTransactionDate(transactionDatePicker.getValue());
            transaction.setTransactionCategory(categories.getSelectionModel().getSelectedItem());
            transaction.setTransactionType(transactionTypes.getSelectionModel().getSelectedItem());
        }
        return transaction;
    }

    private boolean everythingIsFilled() {
        return amount.getText() != null
                && amount.getText().matches("-?[0-9]+(\\.[0-9]+)?")
                && transactionDatePicker.getValue() != null
                && categories.getSelectionModel().getSelectedItem() != null
                && transactionTypes.getSelectionModel().getSelectedItem() != null;
    }
}
