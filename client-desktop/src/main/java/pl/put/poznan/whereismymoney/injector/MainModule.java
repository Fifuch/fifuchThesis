package pl.put.poznan.whereismymoney.injector;


import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Provides;
import pl.put.poznan.whereismymoney.controllers.Controller;
import pl.put.poznan.whereismymoney.gui.ViewLoader;
import pl.put.poznan.whereismymoney.gui.ViewManager;
import pl.put.poznan.whereismymoney.gui.ViewName;
import pl.put.poznan.whereismymoney.gui.utils.FactoryMethodResolver;
import pl.put.poznan.whereismymoney.gui.utils.GuiceFactoryMethodResolver;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

public class MainModule extends AbstractModule {

    @Override
    protected void configure() {
    }

    @Provides
    @Singleton
    private ViewManager provideViewManager(ViewLoader viewLoader) {
        ViewManager viewManager = new ViewManager(viewLoader.loadViews());
        viewLoader.passViewManagerToControllers(viewManager);
        return viewManager;
    }

    @Provides
    private FactoryMethodResolver provideFactoryMethodResolver() {
        return new GuiceFactoryMethodResolver(Guice.createInjector(this));
    }

    @Provides
    private List<Controller> provideControllerList() {
        return new ArrayList<>(ViewName.values().length);
    }

}
