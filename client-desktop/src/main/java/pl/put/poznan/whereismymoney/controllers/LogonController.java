package pl.put.poznan.whereismymoney.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import pl.put.poznan.whereismymoney.gui.ViewManager;
import pl.put.poznan.whereismymoney.gui.ViewName;
import pl.put.poznan.whereismymoney.gui.utils.DialogFactory;
import pl.put.poznan.whereismymoney.gui.utils.builder.WarningBuilder;
import pl.put.poznan.whereismymoney.service.SignInService;

import javax.inject.Inject;

public class SignInController implements Controller {
    private ViewManager viewManager;
    private SignInService signInService;

    @FXML
    private TextField login;
    @FXML
    private PasswordField password;

    @Inject
    public SignInController(SignInService signInService) {
        this.signInService = signInService;
    }

    @FXML
    private void performSignIn() {
        boolean loginSuccessful = false;
        if(!(login.getText() == null || password.getText() == null)) {
            loginSuccessful = signInService.performSignIn(login.getText(), password.getText());
        }
        if (loginSuccessful) {
            viewManager.setMenuVisibility(true);
            viewManager.switchView(ViewName.VIEW_GENERAL);
            clear();
        } else {
            DialogFactory.get(new WarningBuilder("Sign In Failure",
                    "Login/password is empty or incorrect.")).showAndWait();

        }
    }

    @FXML
    private void goToRegistrationView() {
        viewManager.switchView(ViewName.VIEW_REGISTRATION);
        clear();
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
