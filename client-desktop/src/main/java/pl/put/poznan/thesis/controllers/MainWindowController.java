package pl.put.poznan.thesis.controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainWindowController implements Controller {
    @FXML
    private BorderPane mainWindow;

    @FXML
    private void performClose() {
        getPrimaryStage().close();
    }

    @FXML
    private void performMinimize() {
        getPrimaryStage().setIconified(true);
    }

    private Stage getPrimaryStage() {
        return (Stage) mainWindow.getScene().getWindow();
    }

}
