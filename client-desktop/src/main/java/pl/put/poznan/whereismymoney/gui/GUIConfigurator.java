package pl.put.poznan.whereismymoney.gui;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pl.put.poznan.whereismymoney.gui.utils.WindowDragging;

import javax.inject.Inject;

public class GUIConfigurator {
    private ViewManager viewManager;

    @Inject
    public GUIConfigurator(ViewManager viewManager) {
        this.viewManager = viewManager;
    }

    public void configure(Stage primaryStage) {
        configureWindow(primaryStage);
        configureStage(primaryStage);
    }

    private void configureWindow(Stage primaryStage) {
        enableWindowDragging(primaryStage);
        viewManager.switchView(ViewName.VIEW_WELCOME);
    }

    private void enableWindowDragging(Stage primaryStage) {
        BorderPane mainWindow = viewManager.getMainWindow();
        WindowDragging.enable(mainWindow, primaryStage);
    }

    private void configureStage(Stage primaryStage) {
        primaryStage.setScene(new Scene(viewManager.getMainWindow()));
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setResizable(false);
    }
}
