package pl.put.poznan.thesis.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import pl.put.poznan.thesis.gui.ViewManager;
import pl.put.poznan.thesis.gui.ViewName;
import pl.put.poznan.thesis.gui.utils.DialogFactory;
import pl.put.poznan.thesis.gui.utils.builder.WarningBuilder;
import pl.put.poznan.thesis.service.LogonService;

import javax.inject.Inject;

public class LogonController implements Controller {
    private ViewManager viewManager;
    private LogonService logonService;

    @FXML
    private TextField login;
    @FXML
    private PasswordField password;

    @Inject
    public LogonController(LogonService logonService) {
        this.logonService = logonService;
        logonService.getKey();
    }

    @FXML
    private void performSignIn() {
        boolean loginSuccessful = false;
        if(!(login.getText() == null || password.getText() == null)) {
            loginSuccessful = logonService.performLogon(login.getText(), password.getText());
        }
        if (loginSuccessful) {
            viewManager.setMenuVisibility(true);
            viewManager.switchView(ViewName.VIEW_GENERAL);
        } else {
            DialogFactory.get(new WarningBuilder("Sign In Failure",
                    "Login/password is empty or incorrect.")).showAndWait();

        }
    }

    @FXML
    private void goToRegistrationView() {
        viewManager.switchView(ViewName.VIEW_REGISTRATION);
    }

    @Override
    public void clear() {
        login.clear();
        password.clear();
    }

    @Override
    public void setViewManager(ViewManager viewManager) {
        this.viewManager = viewManager;
    }




}
