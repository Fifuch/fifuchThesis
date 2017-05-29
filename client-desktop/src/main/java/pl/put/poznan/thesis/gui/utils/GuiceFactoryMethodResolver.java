package pl.put.poznan.thesis.gui.utils;

import com.google.inject.Injector;
import javafx.util.Callback;

public class GuiceFactoryMethodResolver implements FactoryMethodResolver {

    private Injector injector;

    public GuiceFactoryMethodResolver(Injector injector) {
        this.injector = injector;
    }

    @Override
    public Callback<Class<?>, Object> getFactoryMethod() {
        return injector::getInstance;
    }
}
