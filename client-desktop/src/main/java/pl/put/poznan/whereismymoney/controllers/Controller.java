package pl.put.poznan.whereismymoney.controllers;

import pl.put.poznan.whereismymoney.gui.ViewManager;

public interface Controller {
    default void setViewManager(ViewManager viewManager) {
    }

    default void refresh() {
    }

    default void clear() {
    }
}
