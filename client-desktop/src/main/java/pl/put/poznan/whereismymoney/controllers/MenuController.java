package pl.put.poznan.whereismymoney.controllers;

import javafx.fxml.FXML;
import pl.put.poznan.whereismymoney.gui.ViewManager;
import pl.put.poznan.whereismymoney.gui.ViewName;

public class MenuController implements Controller {
    private ViewManager viewManager;

    @FXML
    private void goToUserDetails() {
        viewManager.switchView(ViewName.VIEW_USER_ACCOUNT);
    }

    @FXML
    private void goToBudgetOverview() {
        viewManager.switchView(ViewName.VIEW_BUDGET_GENERAL);
    }

    @FXML
    private void goToAnalyses() {
        viewManager.switchView(ViewName.VIEW_ANALYSES);
    }

    @FXML
    private void performLogout() {
        viewManager.setMenuVisibility(false);
        viewManager.switchView(ViewName.VIEW_WELCOME);
    }

    @Override
    public void setViewManager(ViewManager viewManager) {
        this.viewManager = viewManager;
    }

}
