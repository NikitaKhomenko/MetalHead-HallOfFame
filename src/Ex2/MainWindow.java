package Ex2;

// Author: Nikita Khomenko ///


import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class MainWindow extends BorderPane {
    private final int SPACE = 30;


    public MainWindow() throws IOException {
        setTopBox();
        setMiddleBox();
        setBottomBox();
    }


    private void setTopBox() {
        HBox hbox = new HBox();


        hbox.setStyle("-fx-background-color: BLACK");
        setTop(hbox);
    }

    private void setMiddleBox() {
        HBox hbox = new HBox();


        hbox.setStyle("-fx-background-color: BLACK");
        setCenter(hbox);
    }

    private void setBottomBox() {
        HBox hbox = new HBox();


        hbox.setStyle("-fx-background-color: BLACK");
        setBottom(hbox);
    }
}