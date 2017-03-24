package pl.put.poznan.whereismymoney.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import pl.put.poznan.whereismymoney.gui.ViewManager;
import pl.put.poznan.whereismymoney.gui.ViewName;

public class WelcomeController implements Controller {
    private ViewManager viewManager;
    @FXML
    private TextField login;
    @FXML
    private PasswordField password;

    @FXML
    private void performSignIn() {
        viewManager.setMenuVisibility(true);
        viewManager.switchView(ViewName.VIEW_BUDGET_GENERAL);
    }

    @FXML
    private void goToRegistrationView() {
        viewManager.switchView(ViewName.VIEW_REGISTRATION);
    }

    @Override
    public void setViewManager(ViewManager viewManager) {
        this.viewManager = viewManager;
    }
}
