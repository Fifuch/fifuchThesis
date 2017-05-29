package pl.put.poznan.thesis.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import pl.put.poznan.thesis.gui.utils.DialogFactory;
import pl.put.poznan.thesis.gui.utils.builder.WarningBuilder;
import pl.put.poznan.thesis.service.UserSettingsService;

import javax.inject.Inject;

public class UserSettingsController implements Controller {
    private UserSettingsService userSettingsService;

    @Inject
    public UserSettingsController(UserSettingsService userSettingsService) {
        this.userSettingsService = userSettingsService;
    }

    @FXML
    private Label currentLogin, currentMail;
    @FXML
    private TextField newLogin, newMail;
    @FXML
    private PasswordField oldPassword, newPassword;

    @FXML
    private void initialize() {
        clear();
    }

    @Override
    public void refresh() {
        clear();
    }

    @Override
    public void clear() {
        currentLogin.setText(userSettingsService.getCurrentLogin());
        currentMail.setText(userSettingsService.getCurrentMail());
        newLogin.clear();
        newMail.clear();
        oldPassword.clear();
        newPassword.clear();
    }

    public void updateUserData() {
        if(!userSettingsService.updateUserData(newLogin.getText(), newMail.getText(), oldPassword.getText(), newPassword.getText())) {
            DialogFactory.get(new WarningBuilder("User update failure",
                    "There is another user with your credentials or some fields are empty.")).showAndWait();
        } else {
            clear();
            Alert successInfo = new Alert(Alert.AlertType.INFORMATION);
            successInfo.setTitle("User data updated!");
            successInfo.setContentText("Data updated correctly.");
            successInfo.show();
        }

    }
}
