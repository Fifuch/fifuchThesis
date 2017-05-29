package pl.put.poznan.thesis.gui.utils;

import javafx.util.Callback;

public interface FactoryMethodResolver {
    Callback<Class<?>, Object> getFactoryMethod();
}
