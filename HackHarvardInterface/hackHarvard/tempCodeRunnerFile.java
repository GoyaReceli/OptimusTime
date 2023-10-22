import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class hackHarvard extends Application {
    private List<workTopic> workTopics = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) {

        // Top Control Bar
        Button howItWorksBtn = new Button("How it works");
        Button homeBtn = new Button("Home");
        Button aboutUsBtn = new Button("About Us");
        HBox topControlBar = new HBox(10, howItWorksBtn, homeBtn, aboutUsBtn);
        topControlBar.setAlignment(Pos.CENTER);
        topControlBar.setPadding(new Insets(10));

        // Central layout
        VBox leftBox = createLeftSideBox();
        VBox rightBox = createRightSideBox();

        Node centralProgressBar = createCircularProgressBar(0.55);
        VBox centralBox = new VBox(10, centralProgressBar);
        centralBox.setAlignment(Pos.CENTER);

        HBox centralLayout = new HBox(50, leftBox, centralBox, rightBox);
        centralLayout.setAlignment(Pos.CENTER);
        centralLayout.setPadding(new Insets(10));

        // Main layout
        VBox mainLayout = new VBox(20, topControlBar, centralLayout);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(20));

        // Set scene and stage
        Scene scene = new Scene(mainLayout, 800, 600);
        primaryStage.setTitle("JavaFX Custom App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createLeftSideBox() {
        VBox sideBox = new VBox(10);
        for (int i = 0; i < 3; i++) {
            if (workTopics.size() >= i) {
                Text heading = new Text(workTopics.get(i).getName());
                ProgressBar statusBar = new ProgressBar(workTopics.get(i).getProgress());
                sideBox.getChildren().addAll(heading, statusBar);
            }
        }
        return sideBox;
    }

    private VBox createRightSideBox() {
        VBox sideBox = new VBox(10);
        for (int i = 3; i < 6; i++) {
            if (workTopics.size() >= i) {
                Text heading = new Text(workTopics.get(i).getName());
                ProgressBar statusBar = new ProgressBar(workTopics.get(i).getProgress());
                sideBox.getChildren().addAll(heading, statusBar);
            }
        }
        return sideBox;
    }

    private Node createCircularProgressBar(double progressPercentage) {
        double radius = 100;
        double strokeWidth = 15;

        // // Create a circular ring using shape subtraction
        Circle outerCircle = new Circle(radius);
        outerCircle.setCenterX(0);
        outerCircle.setCenterX(0);

        Circle innerCircle = new Circle(radius - strokeWidth);
        innerCircle.setCenterX(0);
        innerCircle.setCenterY(0);
        Shape ring = Shape.subtract(outerCircle, innerCircle);
        ring.setFill(Color.LIGHTGRAY);

        double startAngle = 90; // Start at the top of the circle
        double length = -360 * progressPercentage; // Negative to go clockwise

        // // Progress circle
        Arc progressCircle = new Arc();
        progressCircle.setCenterX(0);
        progressCircle.setCenterY(0);
        progressCircle.setRadiusX(radius - strokeWidth / 2);
        progressCircle.setRadiusY(radius - strokeWidth / 2);
        progressCircle.setStartAngle(startAngle);
        progressCircle.setLength(length);
        progressCircle.setType(ArcType.OPEN);
        progressCircle.setFill(null);
        progressCircle.setStroke(Color.BLUE);
        progressCircle.setStrokeWidth(strokeWidth);

        Text hoursText = new Text("14 hours"); // You can adjust the text value as
        // needed

        // // Group everything together using StackPane
        Group circularProgressBar = new Group();
        circularProgressBar.getChildren().addAll(ring, progressCircle, hoursText);

        return circularProgressBar;
    }

    private void showWorkTopicDialog() {
        Dialog<workTopic> dialog = new Dialog<>();
        dialog.setTitle("Add Work Topic");
        dialog.setHeaderText("Enter Work Topic Details");

        int inputCount = 0;

        do {
            // Create input fields
            TextField nameField = new TextField();
            nameField.setPromptText("Work Topic Name");
            TextField hoursField = new TextField();
            hoursField.setPromptText("Number of Hours:");

            // Set the dialog content
            VBox dialogContent = new VBox(10);
            dialogContent.getChildren().addAll(nameField, hoursField);
            dialog.getDialogPane().setContent(dialogContent);

            // Create buttons for OK and Cancel
            ButtonType doneButton = new ButtonType("Done");
            ButtonType nextButton = new ButtonType("Next");
            dialog.getDialogPane().getButtonTypes().addAll(doneButton, nextButton);

            dialog.setResultConverter(buttonType -> {
                if (buttonType == doneButton) {
                    return null;
                } else if (buttonType.getText() == "Next") {
                    try {
                        String name = nameField.getText();
                        double hours = Double.parseDouble(hoursField.getText().replaceAll("[^0-9.]", ""));

                        if (!name.isEmpty() && hours > 0) {
                            workTopic topic = new workTopic(name, hours);
                            workTopics.add(topic);
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            });
            inputCount++;
        } while (inputCount < 6);

    }

    public static void main(String[] args) {
        launch(args);
    }
}