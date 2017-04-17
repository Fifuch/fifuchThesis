package pl.put.poznan.whereismymoney.gui.utils.builder;

import javafx.scene.control.Dialog;
import javafx.scene.control.TextInputDialog;

public class BudgetDialogBuilder implements DialogBuilder {
    @Override
    public Dialog construct() {
        TextInputDialog result = new TextInputDialog();
        result.setTitle("Budget Wizard");
        result.setHeaderText("Budget Wizard");
        result.setContentText("Enter budget's name: ");
        return result;
    }
}
