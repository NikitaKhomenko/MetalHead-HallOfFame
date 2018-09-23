package Ex2;

/// Author: Nikita Khomenko ///

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    private final int WIDTH = 1070;
    private final int HEIGHT = 700;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage mainStage) throws Exception {

        MainWindow mainApp = new MainWindow();
        mainStage.setTitle("Hall Of Fame");
        mainStage.setResizable(false);
        Scene scene = new Scene(mainApp, WIDTH, HEIGHT);
        mainStage.setScene(scene);
        mainStage.show();
        scene.getRoot().requestFocus();
    }

}


