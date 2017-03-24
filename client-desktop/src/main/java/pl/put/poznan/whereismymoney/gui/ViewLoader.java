package pl.put.poznan.whereismymoney.gui;

import com.google.inject.Inject;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import pl.put.poznan.whereismymoney.controllers.Controller;
import pl.put.poznan.whereismymoney.gui.utils.FactoryMethodResolver;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewLoader {
    private final String RESOURCE_EXTENSION = ".fxml";
    private final String RESOURCE_PREFIX = "gui/";

    private List<Controller> controllers;
    private FactoryMethodResolver factoryMethodResolver;

    @Inject
    public ViewLoader(List<Controller> controllers, FactoryMethodResolver factoryMethodResolver) {
        this.controllers = controllers;
        this.factoryMethodResolver = factoryMethodResolver;
    }

    public void passViewManagerToControllers(ViewManager viewManager) {
        controllers.forEach(controller -> controller.setViewManager(viewManager));
    }

    public Map<ViewName, Node> loadViews() {
        Map<ViewName, Node> views = new HashMap<>(ViewName.values().length);
        for (ViewName name : ViewName.values()) {
            FXMLLoader viewLoader = createViewLoader(name);
            tryToLoadView(views, name, viewLoader);
            addController(viewLoader);
        }
        return views;
    }

    private FXMLLoader createViewLoader(ViewName name) {
        String path = RESOURCE_PREFIX + name + RESOURCE_EXTENSION;
        FXMLLoader viewLoader = new FXMLLoader(getClass().getClassLoader().getResource(path));
        viewLoader.setControllerFactory(factoryMethodResolver.getFactoryMethod());
        return viewLoader;
    }

    private void tryToLoadView(Map<ViewName, Node> views, ViewName name, FXMLLoader viewLoader) {
        try {
            views.putIfAbsent(name, viewLoader.load());
        } catch (IOException e) {
            System.err.println("ERROR : Can't load view named " + name +
                    ".\nFor more information see message below:\n" + e.getMessage());
        }
    }

    private void addController(FXMLLoader viewLoader) {
        Controller controller = viewLoader.getController();
        if (controller != null) {
            controllers.add(controller);
        }
    }
}
