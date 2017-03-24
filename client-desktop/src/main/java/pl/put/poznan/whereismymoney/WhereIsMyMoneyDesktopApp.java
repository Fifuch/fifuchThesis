package pl.put.poznan.whereismymoney;

import com.google.inject.Guice;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.stage.Stage;
import pl.put.poznan.whereismymoney.gui.GUIConfigurator;
import pl.put.poznan.whereismymoney.injector.MainModule;

public class WhereIsMyMoneyDesktopApp extends Application {

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
