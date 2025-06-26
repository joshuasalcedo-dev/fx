package io.joshuasalcedo.fx;

import atlantafx.base.theme.CupertinoDark;
import atlantafx.base.theme.Dracula;
import atlantafx.base.theme.PrimerLight;
import io.joshuasalcedo.fx.common.utility.ResourceUtility;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.Properties;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
public class Launcher extends Application {

    private ConfigurableApplicationContext applicationContext;
    private final Logger logger = LoggerFactory.getLogger(Launcher.class);

    @Override
    public void init() throws Exception {
        logger.debug("Init MonorepoManagerFxApplication");
        applicationContext = new SpringApplicationBuilder(SpringApp.class)
                .headless(false) // Important for JavaFX
                .run();
    }

    @Override
    public void start(Stage stage) throws Exception {
        logger.debug("Starting MonorepoManagerFxApplication");
        
        // Enable font smoothing and high DPI support
        System.setProperty("prism.lcdtext", "false");
        System.setProperty("prism.text", "t2k");
        System.setProperty("prism.subpixeltext", "false");
        
        Application.setUserAgentStylesheet(new Dracula().getUserAgentStylesheet());
        applicationContext.publishEvent(new StageReadyEvent(stage));
    }

    @Override
    public void stop() throws Exception {
        logger.debug("Stopping");
        applicationContext.close();
        Platform.exit();
    }
}

@SpringBootApplication
class SpringApp {
    private static final Logger logger = LoggerFactory.getLogger(SpringApp.class);
    public static void main(String[] args) {
        logger.debug("SpringApp started");
        Application.launch(Launcher.class, args);
    }

}


@Component
class StageInitializer implements ApplicationListener<StageReadyEvent> {
    private final static Logger logger = LoggerFactory.getLogger(StageInitializer.class);
    static final String ASSETS_DIR = "/assets/";

    static final String APP_ICON_PATH = ResourceUtility.getResourceAsString(ASSETS_DIR + "icons/app-icon.png");

    static final String APP_PROPERTIES_PATH = "/application.properties";

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        Stage stage = event.getStage();
        // obtain application properties from pom.xml
        loadApplicationProperties();

        var scene = new Scene(clipboardPane());
        scene.getStylesheets().addAll(
                ResourceUtility.getResourceAsString(ASSETS_DIR + "index.css"),
                ResourceUtility.getResourceAsString(ASSETS_DIR + "clipboard-page.css")
        );


        stage.setScene(scene);
        stage.setTitle(System.getProperty("devfx"));
        stage.getIcons().add(new Image(APP_ICON_PATH));
        stage.setOnCloseRequest(t -> Platform.exit());
        
        // Set utility window dimensions and type
        stage.initStyle(StageStyle.UTILITY);
        stage.setWidth(400);
        stage.setHeight(600);
        stage.setMinWidth(350);
        stage.setMinHeight(500);
        stage.setResizable(true);
        stage.setAlwaysOnTop(false);


        Platform.runLater(() -> {
            stage.show();
            stage.requestFocus();
        });
    }

    private Pane clipboardPane() {
        return ResourceUtility.loadFxml("/io/joshuasalcedo/fx/presentation/clipboard-page.fxml");
    }

    private void loadApplicationProperties() {
        try {
            Properties properties = new Properties();
            properties.load(new InputStreamReader(
                    ResourceUtility.getResourceAsStream(APP_PROPERTIES_PATH),
                    UTF_8
            ));
            properties.forEach((key, value) -> System.setProperty(
                    String.valueOf(key),
                    String.valueOf(value)
            ));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
class StageReadyEvent extends ApplicationEvent {
    private final Logger logger = LoggerFactory.getLogger(StageReadyEvent.class);

    public StageReadyEvent(Stage source) {
        super(source);
        logger.debug("StageReadyEvent");
    }

    public Stage getStage(){
        logger.debug("getStage");
        return (Stage)getSource();
    }
}
