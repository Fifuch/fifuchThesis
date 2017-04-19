package pl.put.poznan.whereismymoney.injector;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Provides;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import pl.put.poznan.whereismymoney.controllers.Controller;
import pl.put.poznan.whereismymoney.gui.ViewLoader;
import pl.put.poznan.whereismymoney.gui.ViewManager;
import pl.put.poznan.whereismymoney.gui.ViewName;
import pl.put.poznan.whereismymoney.gui.utils.FactoryMethodResolver;
import pl.put.poznan.whereismymoney.gui.utils.GuiceFactoryMethodResolver;
import pl.put.poznan.whereismymoney.injector.annotation.Host;
import pl.put.poznan.whereismymoney.injector.annotation.Registration;
import pl.put.poznan.whereismymoney.security.SessionManager;

import javax.inject.Singleton;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class MainModule extends AbstractModule {
    @Override
    protected void configure() {
    }

    @Provides
    @Singleton
    private SessionManager provideSessionManager(MessageDigest messageDigest) {
        return new SessionManager(messageDigest);
    }

    @Provides
    private MessageDigest provideSHA256() throws NoSuchAlgorithmException {
        return MessageDigest.getInstance("SHA-256");
    }

    @Provides
    @Singleton
    private ViewManager provideViewManager(ViewLoader viewLoader) {
        ViewManager viewManager = new ViewManager(viewLoader.loadViews());
        viewManager.initControllers();
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

    @Provides
    private HttpClient provideHttpClient() {
        return HttpClients.createDefault();
    }

    @Provides
    @Registration
    private String provideRegistrationString(@Host String hostAddress) {
        return hostAddress + "/register/add";
    }

    @Provides
    @Host
    private String provideHostAddress() {
        return "http://localhost:8080";
    }
}
