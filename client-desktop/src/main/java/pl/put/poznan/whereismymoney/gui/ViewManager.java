package pl.put.poznan.whereismymoney.gui;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

@Singleton
public class ViewManager {
    private BorderPane mainWindow;
    private Map<ViewName, Node> views;

    @Inject
    public ViewManager(Map<ViewName, Node> views) {
        this.mainWindow = (BorderPane) views.get(ViewName.MAIN_WINDOW);
        this.views = views;
    }

    public void setMenuVisibility(boolean menuVisibility) {
        if (menuVisibility) {
            Node menu = views.get(ViewName.MENU);
            mainWindow.setLeft(menu);
        } else {
            mainWindow.setLeft(null);
        }
    }

    public void switchView(ViewName viewName) {
        if (views.containsKey(viewName)) {
            Node viewToDisplay = views.get(viewName);
            mainWindow.setCenter(viewToDisplay);
        }
    }

    public BorderPane getMainWindow() {
        return mainWindow;
    }
}
