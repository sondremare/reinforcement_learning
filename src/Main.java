import flatland.Board;
import flatland.Cell;
import gui.FlatlandGui;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import learning.QLearning;

import java.io.*;

public class Main extends Application {

    private Board board;

    private static final int WIDTH_PARAM = 0;
    private static final int HEIGHT_PARAM = 1;
    private static final int X_PARAM = 2;
    private static final int Y_PARAM = 3;
    private static final int FOODS_PARAM = 4;

    @Override
    public void start(final Stage primaryStage) throws Exception{
        /*Stage flatlandStage = new Stage();
        Scene scene = new Scene(FlatlandGui.initGUI(), 625, 400);
        flatlandStage.setTitle("Flatland Agent");
        flatlandStage.setScene(scene);
        flatlandStage.show();*/

        final FileChooser fileChooser = new FileChooser();

        Button openFileButton = new Button("Choose file");
        Label iterationsLabel = new Label("It: ");
        final TextField iterationsInput = new TextField("1000");
        iterationsInput.setPrefWidth(50);
        Label learningRateLabel = new Label("alpha: ");
        final TextField learningRateInput = new TextField("0.1");
        learningRateInput.setPrefWidth(30);
        Label discountRateLabel = new Label("gamma: ");
        final TextField discountRateInput = new TextField("0.9");
        discountRateInput.setPrefWidth(30);
        Label traceDecayRateLabel = new Label("lambda: ");
        final TextField traceDecayRateInput = new TextField("0.9");
        traceDecayRateInput.setPrefWidth(30);
        final CheckBox useEligibilityTracesCheckbox = new CheckBox("Eligibility traces");
        useEligibilityTracesCheckbox.setSelected(false);
        final CheckBox useSoftMaxCheckbox = new CheckBox("SoftMax");
        Button learnButton = new Button("Learn!");

        GridPane controlPane = new GridPane();
        controlPane.add(openFileButton, 0, 0);
        controlPane.add(iterationsLabel, 1, 0);
        controlPane.add(iterationsInput, 2, 0);
        controlPane.add(learningRateLabel, 3, 0);
        controlPane.add(learningRateInput, 4, 0);
        controlPane.add(discountRateLabel, 5, 0);
        controlPane.add(discountRateInput, 6, 0);
        controlPane.add(traceDecayRateLabel, 7, 0);
        controlPane.add(traceDecayRateInput, 8, 0);
        controlPane.add(useEligibilityTracesCheckbox, 9, 0);
        controlPane.add(useSoftMaxCheckbox, 10, 0);
        controlPane.add(learnButton, 11, 0);

        final GridPane gridPane = new GridPane();
        gridPane.setPrefSize(1200, 525);
        gridPane.add(controlPane, 0, 0);

        primaryStage.setScene(new Scene(gridPane));
        primaryStage.show();


        openFileButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                File boardsDir = new File("D:\\Code\\School\\SubSymbolic\\Project5\\src\\boards\\");
                if (boardsDir != null && boardsDir.exists()) {
                    fileChooser.setInitialDirectory(boardsDir);
                }
                File file = fileChooser.showOpenDialog(primaryStage);

                if (file != null) {
                    try {
                        FileReader fileReader = new FileReader(file);
                        BufferedReader bufferedReader = new BufferedReader(fileReader);

                        int[][] boardValues = null;
                        int height = 0;
                        int width = 0;
                        int foods = 0;
                        int xPos = 0;
                        int yPos = 0;

                        int counter = 0;
                        String line;
                        boolean firstLine = true;
                        while ((line  = bufferedReader.readLine()) != null) {
                            if (firstLine) {
                                String[] boardParams = line.split(" ");
                                width = Integer.parseInt(boardParams[WIDTH_PARAM]);
                                height = Integer.parseInt(boardParams[HEIGHT_PARAM]);
                                foods = Integer.parseInt(boardParams[FOODS_PARAM]);
                                boardValues = new int[height][width];

                                xPos = Integer.parseInt(boardParams[X_PARAM]);
                                yPos = Integer.parseInt(boardParams[Y_PARAM]);
                                firstLine = false;
                            } else {
                                System.out.println(line);
                                String[] lineStringValues = line.split(" ");
                                for (int i = 0; i < lineStringValues.length; i++) {
                                    boardValues[counter][i] = Integer.parseInt(lineStringValues[i]);
                                }
                                counter++;
                            }
                        }
                        Cell[][] correctlyFlippedBoardValues = new Cell[width][height];
                        for (int i = 0; i < boardValues.length; i++) {
                            for (int j = 0; j < boardValues[i].length; j++) {
                                int val = boardValues[i][j];
                                if (val > 0) {
                                    correctlyFlippedBoardValues[j][i] = new Cell(Cell.Type.Food, val);
                                } else if (val == -1) {
                                    correctlyFlippedBoardValues[j][i] = new Cell(Cell.Type.Poison, val);
                                } else {
                                    correctlyFlippedBoardValues[j][i] = new Cell(Cell.Type.Nothing, val);
                                }
                            }
                        }
                        fileReader.close();
                        board = new Board(correctlyFlippedBoardValues, foods, xPos, yPos);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        learnButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (board != null) {
                    int iterations = Integer.parseInt(iterationsInput.getText());
                    QLearning.learningRate = Double.parseDouble(learningRateInput.getText());
                    QLearning.discountRate = Double.parseDouble(discountRateInput.getText());
                    QLearning.traceDecayFactor = Double.parseDouble(traceDecayRateInput.getText());
                    QLearning.useEligibilityTraces = useEligibilityTracesCheckbox.isSelected();
                    QLearning.useSoftMax = useSoftMaxCheckbox.isSelected();
                    QLearning qLearning = new QLearning(board);
                    qLearning.iterate(iterations);
                    if (gridPane.getChildren().size() > 1) gridPane.getChildren().remove(1);
                    gridPane.add(FlatlandGui.initGUI(qLearning), 0, 1);
                } else {
                    System.out.println("No board loaded!");
                }
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
