package pl.put.poznan.whereismymoney.gui.utils.builder;

import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;

public class WarningBuilder implements DialogBuilder {
    private String title;
    private String contentText;

    public WarningBuilder(String title, String contentText) {
        this.title = title;
        this.contentText = contentText;
    }

    @Override
    public Dialog construct() {
        Alert budgetWarning = new Alert(Alert.AlertType.ERROR);
        budgetWarning.setTitle(title);
        budgetWarning.setHeaderText("");
        budgetWarning.setContentText(contentText);
        return budgetWarning;
    }
}
