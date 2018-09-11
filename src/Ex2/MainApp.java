package Ex2;

/// Author: Nikita Khomenko ///

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    private final int width = 1070;
    private final int height = 700;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage mainStage) throws Exception {

        MainWindow mainApp = new MainWindow();
        mainStage.setTitle("Hall Of Fame");
        mainStage.setResizable(false);
        mainStage.setScene(new Scene(mainApp, width, height));
        mainStage.show();
    }

}