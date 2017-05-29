package pl.put.poznan.thesis.injector;

import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Provides;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import pl.put.poznan.thesis.controllers.Controller;
import pl.put.poznan.thesis.gui.ViewLoader;
import pl.put.poznan.thesis.gui.ViewManager;
import pl.put.poznan.thesis.gui.ViewName;
import pl.put.poznan.thesis.gui.utils.FactoryMethodResolver;
import pl.put.poznan.thesis.gui.utils.GuiceFactoryMethodResolver;
import pl.put.poznan.thesis.injector.annotation.Host;
import pl.put.poznan.thesis.injector.annotation.Registration;
import pl.put.poznan.thesis.injector.annotation.Logon;
import pl.put.poznan.thesis.security.SessionManager;

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
    @Logon
    private String provideSaltProviderAddress(@Host String hostAddress) {
        return hostAddress + "/logon";
    }

    @Provides
    @Host
    private String provideHostAddress() {
        return "http://localhost:8080";
    }

    @Provides
    private Gson provideGson() {
        return new Gson();
    }
}
