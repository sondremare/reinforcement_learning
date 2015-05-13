import flatland.Agent;
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

import javax.xml.soap.Text;
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
        Label iterationsLabel = new Label("Iterations: ");
        final TextField iterationsInput = new TextField("100");
        iterationsInput.setPrefWidth(100);
        final CheckBox useEligibilityTracesCheckbox = new CheckBox("Eligibility traces");
        useEligibilityTracesCheckbox.setSelected(false);
        Button learnButton = new Button("Learn!");

        GridPane controlPane = new GridPane();
        controlPane.add(openFileButton, 0, 0);
        controlPane.add(iterationsLabel, 1, 0);
        controlPane.add(iterationsInput, 2, 0);
        controlPane.add(useEligibilityTracesCheckbox, 3, 0);
        controlPane.add(learnButton, 4, 0);

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
                    boolean useEligibilityTraces = useEligibilityTracesCheckbox.isSelected();
                    QLearning qLearning = new QLearning(board, useEligibilityTraces);
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
