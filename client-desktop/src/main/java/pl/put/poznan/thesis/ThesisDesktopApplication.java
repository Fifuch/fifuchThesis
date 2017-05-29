package pl.put.poznan.thesis;

import com.google.inject.Guice;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.stage.Stage;
import pl.put.poznan.thesis.gui.GUIConfigurator;
import pl.put.poznan.thesis.injector.MainModule;

public class ThesisDesktopApplication extends Application {

    public static void main(String... args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Injector injector = Guice.createInjector(new MainModule());
        GUIConfigurator GUIConfigurator = injector.getInstance(GUIConfigurator.class);
        GUIConfigurator.configure(primaryStage);
        primaryStage.show();
    }
}
