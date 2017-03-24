package pl.put.poznan.whereismymoney.gui.utils;

import javafx.util.Callback;

public interface FactoryMethodResolver {
    Callback<Class<?>, Object> getFactoryMethod();
}
