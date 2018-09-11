package Ex2;

/// Author: Nikita Khomenko ///

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import java.io.IOException;


import static javafx.scene.paint.Color.RED;


public class MainWindow extends BorderPane {
    private final int SPACE = 30;
    private BandsDataControllerImpl bandsController;
    private Band band;

    public MainWindow() throws IOException, ClassNotFoundException {
        bandsController = BandsDataControllerImpl.getInstance();
        band = bandsController.CurrentBand();

        setTopBox();
        setMiddleBox();
        setBottomBox();
        setKeyBindings();
    }



    /**
     *        ******   Top Box   ******
     */

    private void setTopBox() {
        Label movingLabel = new Label("Hall Of Fame");
        movingLabel.setFont(new Font("Arial", 22));
        movingLabel.setTextFill(RED);

        HBox movingLabelHbox = new HBox(movingLabel);
        HBox comboBoxHbox = setComboBoxHbox();
        GridPane topPane = new GridPane();

        topPane.addRow(0, movingLabel);
        topPane.addRow(1, comboBoxHbox);

        HBox hbox = new HBox(topPane);


        setTopBoxSpacing(topPane, hbox, movingLabelHbox, comboBoxHbox);
        setTop(hbox);
    }

     private HBox setComboBoxHbox() {
         ComboBox<String> sortingBox = new ComboBox<String>();
         sortingBox.setEditable(true);
         sortingBox.setValue("Sort by ...");
         sortingBox.getItems().addAll("Sort By Name", "Sort By Fans", "Sort By Origin");

         sortingBox.getSelectionModel().selectedItemProperty()
                 .addListener((observable, oldValue, sortBy) -> {
                         switch (sortBy) {
                             case "Sort By Name":
                                 bandsController.sort(bandsController.getNameComparator());
                                 break;

                             case "Sort By Fans":
                                 bandsController.sort(bandsController.getFansComparator());
                                 break;

                             case "Sort By Origin":
                                 bandsController.sort(bandsController.getOriginComparator());
                                 break;
                         }
                 });

         return new HBox(sortingBox);
     }

    private void setTopBoxSpacing(GridPane topPane, HBox hbox, HBox movingLabelHbox, HBox comboBoxHbox){
        comboBoxHbox.setAlignment(Pos.BOTTOM_CENTER);
        hbox.setAlignment(Pos.CENTER);
        topPane.setVgap(SPACE);
        hbox.setPadding(new Insets(SPACE, 0, 0, 0));
        hbox.setStyle("-fx-background-color: BLACK");
    }


    /**
     *       ******   Middle Box   ******
     */

    private void setMiddleBox() {

        VBox rightVbox = setMiddleRightBox();
        VBox centerVbox = setMiddleCenterBox();
        VBox leftVbox = setMiddleLeftBox();

        HBox hbox = new HBox(leftVbox, centerVbox, rightVbox);
        setMiddleBoxSpacing(hbox, rightVbox, leftVbox);

        hbox.setStyle("-fx-background-color: BLACK");
        setCenter(hbox);
    }

    private VBox setMiddleRightBox() {
        Button rightButton = new Button(">");
        rightButton.setFont(new Font("Arial", 18));

        rightButton.setOnAction(event -> {
            band = bandsController.next(true);
        });

        return new VBox(rightButton);

    }

    private VBox setMiddleLeftBox() {
        Button leftButton = new Button("<");
        leftButton.setFont(new Font("Arial", 18));

        leftButton.setOnAction(event -> {
            band = bandsController.previous(true);
        });

        return new VBox(leftButton);
    }

    private void setMiddleBoxSpacing(HBox hbox, VBox rightVbox, VBox leftVbox){
        leftVbox.setAlignment(Pos.CENTER_LEFT);
        rightVbox.setAlignment(Pos.CENTER_RIGHT);
        Insets insets = new Insets(SPACE);
        hbox.setAlignment(Pos.CENTER);
        hbox.setSpacing(SPACE);
        setMargin(leftVbox, insets);
    }


    private VBox setMiddleCenterBox() {
        TextField bandNameTxt = new TextField(band.getName());
        TextField fansTxt = new TextField((String.valueOf(band.getNumOfFans())));
        TextField formedTxt = new TextField((String.valueOf(band.getFormedYear())));
        TextField originTxt = new TextField(band.getOrigin());
        TextField styleTxt = new TextField(band.getStyle());
        Label bandLabel = new Label("Band:");
        Label fansLabel = new Label("Fans:");
        Label formedLabel = new Label("Formed:");
        Label originLabel = new Label("Origin:");
        Label didSplitLabel = new Label("Did they split?");
        Label styleLabel = new Label("Style:");
        CheckBox didSplitCB = new CheckBox();
        if(band.hasSplit())
            didSplitCB.setSelected(true);

        setUneditable(bandNameTxt, fansTxt, formedTxt, originTxt, styleTxt, didSplitCB);
        setCenterLabelsStyle(bandLabel, fansLabel, formedLabel, originLabel, didSplitLabel, styleLabel);

        GridPane centerPane = new GridPane();

        centerPane.addRow(0, bandLabel, bandNameTxt);
        centerPane.addRow(1, fansLabel, fansTxt);
        centerPane.addRow(2, formedLabel, formedTxt);
        centerPane.addRow(3, originLabel, originTxt);
        centerPane.addRow(4, didSplitLabel, didSplitCB);
        centerPane.addRow(5, styleLabel, styleTxt);

        VBox centerVbox = new VBox(centerPane);
        MiddleCenterBoxSpacing(centerPane, centerVbox);
        return centerVbox;
    }

        private void MiddleCenterBoxSpacing(GridPane centerPane, VBox centerVbox){
            centerPane.setVgap(SPACE);
            centerPane.setHgap(SPACE*3);
            centerVbox.setAlignment(Pos.CENTER);
            centerVbox.setPadding(new Insets(0, SPACE*6, SPACE, SPACE*6));
        }

        public void setCenterLabelsStyle(Label bandLabel, Label fansLabel, Label formedLabel, Label originLabel,
                                         Label didSplitLabel, Label styleLabel) {
            bandLabel.setTextFill(RED);
            fansLabel.setTextFill(RED);
            formedLabel.setTextFill(RED);
            originLabel.setTextFill(RED);
            didSplitLabel.setTextFill(RED);
            styleLabel.setTextFill(RED);

            Font mainPaneFont = new Font("Arial", 23);
            bandLabel.setFont(mainPaneFont);
            fansLabel.setFont(mainPaneFont);
            formedLabel.setFont(mainPaneFont);
            originLabel.setFont(mainPaneFont);
            didSplitLabel.setFont(mainPaneFont);
            styleLabel.setFont(mainPaneFont);
        }

        public void setUneditable(TextField bandNameTxt, TextField fansTxt, TextField formedTxt, TextField originTxt,
                                  TextField styleTxt, CheckBox didSplitCB) {
             bandNameTxt.setEditable(false);
             fansTxt.setEditable(false);
             formedTxt.setEditable(false);
             originTxt.setEditable(false);
             styleTxt.setEditable(false);
             didSplitCB.setAllowIndeterminate(false);
        }




    /**
     *       ******   Bottom Box   ******
     */

    private void setBottomBox() {
        Button saveButton = new Button("Save");
        Button removeButton = new Button("Remove Band");
        Button undoButton = new Button("Undo");
        Button revertButton = new Button("Revert");
        setButtonsStyle(saveButton, removeButton, undoButton, revertButton);
        buttonsHandlers(saveButton, removeButton, undoButton, revertButton);

        HBox hbox = new HBox(saveButton, removeButton, undoButton, revertButton);
        buttomBoxSpacing(hbox);
        setBottom(hbox);
    }

    private void buttonsHandlers(Button saveButton, Button removeButton, Button undoButton, Button revertButton) {
        saveButton.setOnAction(event -> {    // to save the list as it is at the moment
            try {
                bandsController.save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        removeButton.setOnAction(event -> {
            bandsController.remove();
        });
        undoButton.setOnAction(event -> {
            bandsController.undo();
        });
        revertButton.setOnAction(event -> {
            bandsController.revert();
        });
    }

    private void buttomBoxSpacing(HBox hbox) {
        hbox.setAlignment(Pos.CENTER);
        hbox.setSpacing(SPACE);
        hbox.setPadding(new Insets(SPACE, SPACE, SPACE*1.5, SPACE));
        hbox.setStyle("-fx-background-color: BLACK");
    }

    private void setButtonsStyle(Button saveButton, Button removeButton, Button undoButton, Button revertButton) {
        Font buttonFont = new Font("Arial", 18);
        saveButton.setFont(buttonFont);
        removeButton.setFont(buttonFont);
        undoButton.setFont(buttonFont);
        revertButton.setFont(buttonFont);
    }



    ///////////////// ********* Key Bindings ********* /////////////////


    public void setKeyBindings() {
        this.setOnKeyPressed(keyEvent -> {

            if (keyEvent.getCode() == KeyCode.LEFT) {
                band = bandsController.previous(true);
                keyEvent.consume();
            }

            if (keyEvent.getCode() == KeyCode.RIGHT) {
                band = bandsController.next(true);
                keyEvent.consume();
            }

        });

        final KeyCombination keyCombSave = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_ANY);
        final KeyCombination keyCombUndo = new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_ANY);

        this.addEventHandler(KeyEvent.KEY_RELEASED, (EventHandler) event -> {

            if (keyCombSave.match((KeyEvent) event)) {
                try {
                    bandsController.save();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (keyCombUndo.match((KeyEvent) event)) {
                bandsController.undo();
            }
        });
    }

}


