package pl.put.poznan.whereismymoney.gui.utils;

import javafx.scene.Node;
import javafx.stage.Stage;

public class WindowDragging {
    private static double offsetX = 0.0;
    private static double offsetY = 0.0;

    public static void enable(Node windowNode, Stage stage) {
        windowNode.setOnMousePressed(mousePressedEvent -> {
            offsetX = mousePressedEvent.getSceneX();
            offsetY = mousePressedEvent.getSceneY();
        });
        windowNode.setOnMouseDragged(mouseDraggedEvent -> {
            stage.setX(mouseDraggedEvent.getScreenX() - offsetX);
            stage.setY(mouseDraggedEvent.getScreenY() - offsetY);
        });
    }
}
