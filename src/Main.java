import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import net.sentientturtle.OOP3Sorteren.AbstractSort;
import net.sentientturtle.OOP3Sorteren.BubbleSort;
import net.sentientturtle.OOP3Sorteren.InsertionSort;
import net.sentientturtle.OOP3Sorteren.ui.ChartPane;

import java.util.Random;

public class Main extends Application {
    private Random random = new Random();
    private Integer[] dataSet = new Integer[40];
    private ChartPane pane;

    @Override
    public void start(Stage primaryStage){
        pane = new ChartPane();
        pane.setStyle("-fx-border-color: black");

        Button step = new Button("Step");
        Button auto = new Button("Auto");

        HBox hBox = new HBox(5);
        hBox.getChildren().addAll(step, auto);

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(pane);
        borderPane.setBottom(hBox);

        // Create a scene and place it in the stage
        Scene scene = new Scene(borderPane, 600, 250);
        primaryStage.setTitle("Sorteren"); // Set the stage title
        primaryStage.setScene(scene); // Place the scene in the stage
        primaryStage.show(); // Display the stage

        for (int i = 0; i < dataSet.length; i++) dataSet[i] = random.nextInt(10)+1;
        AbstractSort<Integer> sort = new InsertionSort<>(dataSet); //new BubbleSort<>(dataSet);
        step.setOnMouseClicked(event -> {
            if (!sort.isDone()) {
                sort.step();
                pane.reDraw(dataSet);
            }
        });

        pane.reDraw(dataSet);
        BGRunnable bgRunnable = new BGRunnable(sort);
        Thread bgThread = new Thread(bgRunnable);
        bgThread.start();
        auto.setOnMouseClicked(event -> bgRunnable.toggle());
        primaryStage.setOnCloseRequest(event -> bgThread.interrupt());
    }
    public static void main(String[] args) {
        launch(args);
    }

    private class BGRunnable implements Runnable {
        private boolean isRunning = false;
        private AbstractSort<Integer> sort;

        BGRunnable(AbstractSort<Integer> sort) {
            this.sort = sort;
        }

        @Override
        public void run() {
            while (true){
                if (isRunning) {
                    if (!sort.isDone()) sort.step();
                    Platform.runLater(() -> pane.reDraw(dataSet));
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }

        public void toggle() {
            isRunning = !isRunning;
        }
    }
}
