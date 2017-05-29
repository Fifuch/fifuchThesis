package pl.put.poznan.thesis.gui;

import com.google.inject.Inject;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import pl.put.poznan.thesis.controllers.Controller;
import pl.put.poznan.thesis.gui.model.View;
import pl.put.poznan.thesis.gui.utils.FactoryMethodResolver;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ViewLoader {
    private final String RESOURCE_EXTENSION = ".fxml";
    private final String RESOURCE_PREFIX = "gui/";

    private FactoryMethodResolver factoryMethodResolver;

    @Inject
    public ViewLoader(FactoryMethodResolver factoryMethodResolver) {
        this.factoryMethodResolver = factoryMethodResolver;
    }

    public Map<ViewName, View> loadViews() {
        Map<ViewName, View> views = new HashMap<>(ViewName.values().length);
        for (ViewName name : ViewName.values()) {
            FXMLLoader viewLoader = createViewLoader(name);
            tryToLoadView(views, name, viewLoader);
        }
        return views;
    }

    private FXMLLoader createViewLoader(ViewName name) {
        String path = RESOURCE_PREFIX + name + RESOURCE_EXTENSION;
        FXMLLoader viewLoader = new FXMLLoader(getClass().getClassLoader().getResource(path));
        viewLoader.setControllerFactory(factoryMethodResolver.getFactoryMethod());
        return viewLoader;
    }

    private void tryToLoadView(Map<ViewName, View> views, ViewName name, FXMLLoader viewLoader) {
        try {
            Node root = viewLoader.load();
            Controller controller = viewLoader.getController();
            views.putIfAbsent(name, new View(root, controller));
        } catch (IOException e) {
            System.err.println("ERROR : Can't load view named " + name +
                    ".\nFor more information see message below:\n" + e.getMessage());
        }
    }

}
