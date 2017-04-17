package pl.put.poznan.whereismymoney.gui.utils.builder;

import javafx.scene.control.Dialog;
import javafx.scene.control.TextInputDialog;

public class CategoryDialogBuilder implements DialogBuilder {
    @Override
    public Dialog construct() {
        TextInputDialog result = new TextInputDialog();
        result.setTitle("Category Wizard");
        result.setHeaderText("Category Wizard");
        result.setContentText("Enter category's name: ");
        return result;
    }


}
