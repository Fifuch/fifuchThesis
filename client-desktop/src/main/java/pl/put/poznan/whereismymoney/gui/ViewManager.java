package pl.put.poznan.whereismymoney.gui;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import pl.put.poznan.whereismymoney.gui.model.View;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

@Singleton
public class ViewManager {
    private BorderPane mainWindow;
    private Map<ViewName, View> views;
    private View currentView;

    @Inject
    public ViewManager(Map<ViewName, View> views) {
        this.mainWindow = (BorderPane) views.get(ViewName.MAIN_WINDOW).getViewRoot();
        this.views = views;
    }

    public void setMenuVisibility(boolean menuVisibility) {
        if (menuVisibility) {
            Node menu = views.get(ViewName.MENU).getViewRoot();
            mainWindow.setLeft(menu);
        } else {
            mainWindow.setLeft(null);
        }
    }

    public void switchView(ViewName viewName) {
        if (views.containsKey(viewName)) {
            clearPreviousView();
            Node viewToDisplay = views.get(viewName).getViewRoot();
            mainWindow.setCenter(viewToDisplay);
            currentView = views.get(viewName);
            currentView.getController().refresh();
        }
    }

    private void clearPreviousView() {
        if (isControllerInitialized()) {
            currentView.getController().clear();
        }
    }

    private boolean isControllerInitialized() {
        return currentView != null && currentView.getController() != null;
    }

    public void initControllers() {
        views.values()
                .stream()
                .filter(view -> view.getController() != null)
                .forEach(view -> view.setViewManager(this));
    }

    public BorderPane getMainWindow() {
        return mainWindow;
    }
}
