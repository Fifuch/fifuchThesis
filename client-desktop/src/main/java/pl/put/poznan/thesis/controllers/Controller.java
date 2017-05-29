package pl.put.poznan.thesis.controllers;

import pl.put.poznan.thesis.gui.ViewManager;

public interface Controller {
    default void setViewManager(ViewManager viewManager) {
    }

    default void refresh() {
    }

    default void clear() {
    }
}
