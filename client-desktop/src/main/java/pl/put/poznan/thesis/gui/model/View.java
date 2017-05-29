package pl.put.poznan.thesis.gui.model;

import javafx.scene.Node;
import pl.put.poznan.thesis.controllers.Controller;
import pl.put.poznan.thesis.gui.ViewManager;

public class View {
    private Controller associatedController;
    private Node viewRoot;

    public View(Node viewRoot, Controller associatedController) {
        this.associatedController = associatedController;
        this.viewRoot = viewRoot;
    }

    public Controller getController() {
        return associatedController;
    }

    public Node getViewRoot() {
        return viewRoot;
    }

    public void setViewManager(ViewManager viewManager) {
        associatedController.setViewManager(viewManager);
    }
}
