package Ex2;

/// Author: Nikita Khomenko ///

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


import static javafx.scene.paint.Color.RED;
import static javafx.scene.text.Font.font;


public class MainWindow extends BorderPane {
    private final int WIDTH = 1070;
    private final int SPACE = 30, DURATION = 1000;
    private BandsDataControllerImpl bandsController;
    private Band band;
    private TextField bandNameTxt;
    private TextField fansTxt;
    private TextField formedTxt;
    private TextField originTxt;
    private TextField styleTxt;
    private Text timeDisplay;
    private CheckBox didSplitCB;

    public MainWindow() throws ClassNotFoundException, IOException {
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
        HBox movingTextHbox = setMovingTextHbox();
        HBox comboBoxHbox = setComboBoxHbox();
        GridPane topPane = new GridPane();

        topPane.addRow(0, movingTextHbox);
        topPane.addRow(1, comboBoxHbox);

        HBox hbox = new HBox(topPane);

        setTopBoxSpacing(topPane, hbox, movingTextHbox, comboBoxHbox);
        setTop(hbox);
    }

        private HBox setMovingTextHbox() {
            timeDisplay = new Text();
            setupTimeline();
            setupTimeDisplayTxt();

            PathTransition timeTextTransition = new PathTransition();
            timeTextTransition.setNode(timeDisplay);
            Line path = new Line();
            path.setStartX(-WIDTH*0.15);
            path.setEndX(WIDTH*0.5);
            path.setStartY(0);
            path.setEndY(0);

            timeTextTransition.setPath(path);
            timeTextTransition.setCycleCount(Animation.INDEFINITE);
            timeTextTransition.setAutoReverse(true);
            timeTextTransition.setDuration(Duration.millis(DURATION*8));
            timeTextTransition.play();

            mouseEventToLabel(timeDisplay, timeTextTransition);

            return new HBox(timeDisplay);
        }

        private void  setupTimeDisplayTxt() {
            timeDisplay.setText("Hall Of Fame  " + getFormattedTime());
            timeDisplay.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 30));
            timeDisplay.setFill(Color.RED);
        }


        private void setupTimeline(){
            Timeline timeline = new Timeline();
            timeline.getKeyFrames()
                    .add(new KeyFrame(Duration.millis(DURATION/3), event ->
                            setupTimeDisplayTxt()));
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();
        }

        private String getFormattedTime(){
            return LocalTime.now().format(DateTimeFormatter.ofPattern("HH : mm : ss"));
        }

         private HBox setComboBoxHbox() {
             ComboBox<String> sortingBox = new ComboBox<>();
             sortingBox.setValue("Sort by ...");
             sortingBox.getItems().addAll("Sort By Name", "Sort By Fans", "Sort By Origin");
             sortingBox.setStyle("-fx-font: 17px \"Arial\";");

             sortingBox.getSelectionModel().selectedItemProperty()
                     .addListener((observable, oldValue, sortBy) -> {
                             switch (sortBy) {
                                 case "Sort By Name":
                                     bandsController.sort(bandsController.getNameComparator());
                                     band = bandsController.CurrentBand();
                                     setBandAttributesShown();
                                     break;

                                 case "Sort By Fans":
                                     bandsController.sort(bandsController.getFansComparator());
                                     band = bandsController.CurrentBand();
                                     setBandAttributesShown();
                                     break;

                                 case "Sort By Origin":
                                     bandsController.sort(bandsController.getOriginComparator());
                                     band = bandsController.CurrentBand();
                                     setBandAttributesShown();
                                     break;
                             }
                         setBandAttributesShown();
                         getFocus();
                     });

             return new HBox(sortingBox);
         }

    private void setTopBoxSpacing(GridPane topPane, HBox hbox, HBox movingTextHbox, HBox comboBoxHbox){
        comboBoxHbox.setAlignment(Pos.BOTTOM_CENTER);
        hbox.setAlignment(Pos.CENTER);
        topPane.setVgap(SPACE);
        movingTextHbox.setPadding(new Insets(0, 0, SPACE/4, 0));
        hbox.setPadding(new Insets(SPACE/2, 0, SPACE/5, 0));
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
        rightButton.setFont(font("Arial", FontWeight.EXTRA_BOLD, 19));
        rightButton.setBackground(new Background(new BackgroundFill(Color.DARKRED, CornerRadii.EMPTY, Insets.EMPTY)));
        rightButton.setTextFill(Color.WHITE);
        rightButton.setOnAction(event -> {
            band = bandsController.next();
            setBandAttributesShown();
            getFocus();
        });

        return new VBox(rightButton);

    }

    private VBox setMiddleLeftBox() {
        Button leftButton = new Button("<");
        leftButton.setFont(font("Arial", FontWeight.EXTRA_BOLD, 19));
        leftButton.setBackground(new Background(new BackgroundFill(Color.DARKRED, CornerRadii.EMPTY, Insets.EMPTY)));
        leftButton.setTextFill(Color.WHITE);
        leftButton.setOnAction(event -> {
            band = bandsController.previous();
            setKeyBindings();
            setBandAttributesShown();
            getFocus();
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
        bandNameTxt = new TextField();
        fansTxt = new TextField();
        formedTxt = new TextField();
        originTxt = new TextField();
        styleTxt = new TextField();
        Label bandLabel = new Label("Band:");
        Label fansLabel = new Label("Fans:");
        Label formedLabel = new Label("Formed:");
        Label originLabel = new Label("Origin:");
        Label didSplitLabel = new Label("Did they split?");
        Label styleLabel = new Label("Style:");
        didSplitCB = new CheckBox();

        setBandAttributesShown();
        setTextFieldStyle(bandNameTxt, fansTxt, formedTxt, originTxt, styleTxt, didSplitCB);
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

    private void setBandAttributesShown() {
        bandNameTxt.setText(band.getName());
        fansTxt.setText((String.valueOf(band.getNumOfFans())));
        formedTxt.setText((String.valueOf(band.getFormedYear())));
        originTxt.setText(band.getOrigin());
        styleTxt.setText(band.getStyle());
        if(band.hasSplit())
            didSplitCB.setSelected(true);
        else
            didSplitCB.setSelected(false);
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

        public void setTextFieldStyle(TextField bandNameTxt, TextField fansTxt, TextField formedTxt, TextField originTxt,
                                      TextField styleTxt, CheckBox didSplitCB) {
            Font textFieldFont = font("Arial", FontWeight.BOLD, 16);
            bandNameTxt.setFont(textFieldFont);
            fansTxt.setFont(textFieldFont);
            formedTxt.setFont(textFieldFont);
            originTxt.setFont(textFieldFont);
            styleTxt.setFont(textFieldFont);
            didSplitCB.setFont(textFieldFont);

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
                getFocus();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        removeButton.setOnAction(event -> {
            bandsController.remove();
            band = bandsController.CurrentBand();
            setBandAttributesShown();
            getFocus();
        });
        undoButton.setOnAction(event -> {
            bandsController.undo();
            band = bandsController.CurrentBand();
            setBandAttributesShown();
            getFocus();
        });
        revertButton.setOnAction(event -> {
            bandsController.revert();
            band = bandsController.CurrentBand();
            setBandAttributesShown();
            getFocus();
        });
    }

    private void buttomBoxSpacing(HBox hbox) {
        hbox.setAlignment(Pos.CENTER);
        hbox.setSpacing(SPACE);
        hbox.setPadding(new Insets(SPACE, SPACE, SPACE*1.5, SPACE));
        hbox.setStyle("-fx-background-color: BLACK");
    }

    private void setButtonsStyle(Button saveButton, Button removeButton, Button undoButton, Button revertButton) {
        Font buttonFont = font("Arial", FontWeight.EXTRA_BOLD, 19);
        Background buttonBG = new Background(new BackgroundFill(Color.DARKRED, CornerRadii.EMPTY, Insets.EMPTY));
        Color buttonColor = Color.WHITE;

        saveButton.setFont(buttonFont);
        removeButton.setFont(buttonFont);
        undoButton.setFont(buttonFont);
        revertButton.setFont(buttonFont);

        saveButton.setBackground(buttonBG);
        removeButton.setBackground(buttonBG);
        undoButton.setBackground(buttonBG);
        revertButton.setBackground(buttonBG);

        saveButton.setTextFill(buttonColor);
        removeButton.setTextFill(buttonColor);
        undoButton.setTextFill(buttonColor);
        revertButton.setTextFill(buttonColor);

    }



    ///////////////// ********* Key Bindings ********* /////////////////


    public void setKeyBindings() {
        this.setOnKeyPressed(keyEvent -> {

            if (keyEvent.getCode() == KeyCode.LEFT) {
                band = bandsController.previous();
                setBandAttributesShown();
                keyEvent.consume();
            }

            if (keyEvent.getCode() == KeyCode.RIGHT) {
                band = bandsController.next();
                setBandAttributesShown();
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
                band = bandsController.CurrentBand();
                setBandAttributesShown();
            }


        });
    }

    public void getFocus() {
        this.getScene().getRoot().requestFocus();
    }

    public void mouseEventToLabel(Text movingLabel,  PathTransition labelTransition) {
        movingLabel.setOnMouseEntered((event) -> {
            labelTransition.pause();
        });

        movingLabel.setOnMouseExited((event) -> {
            labelTransition.play();
        });
    }

}




