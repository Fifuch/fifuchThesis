package pl.put.poznan.whereismymoney.controllers;

import javafx.fxml.FXML;
import pl.put.poznan.whereismymoney.gui.ViewManager;
import pl.put.poznan.whereismymoney.gui.ViewName;
import pl.put.poznan.whereismymoney.security.SessionManager;

import javax.inject.Inject;

public class MenuController implements Controller {
    private ViewManager viewManager;
    private SessionManager sessionManager;

    @Inject
    public MenuController( SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @FXML
    private void goToUserSettings() {
        viewManager.switchView(ViewName.VIEW_USER_SETTINGS);
    }

    @FXML
    private void goToBudgetOverview() {
        viewManager.switchView(ViewName.VIEW_GENERAL);
    }

    @FXML
    private void goToAnalyses() {
        viewManager.switchView(ViewName.VIEW_ANALYSES);
    }

    @FXML
    private void performLogout() {
        viewManager.setMenuVisibility(false);
        viewManager.switchView(ViewName.VIEW_SIGN_IN);
        sessionManager.setUserData(null);
    }

    @Override
    public void setViewManager(ViewManager viewManager) {
        this.viewManager = viewManager;
    }

}
