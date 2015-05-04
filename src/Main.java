import flatland.Agent;
import flatland.Board;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;

public class Main extends Application {

    private Agent agent;
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

                                int xPos = Integer.parseInt(boardParams[X_PARAM]);
                                int yPos = Integer.parseInt(boardParams[Y_PARAM]);
                                agent = new Agent(xPos, yPos);
                                firstLine = false;
                            } else {
                                String[] lineStringValues = line.split(" ");
                                for (int i = 0; i < lineStringValues.length; i++) {
                                    boardValues[counter][i] = Integer.parseInt(lineStringValues[i]);
                                }
                                counter++;
                            }
                        }
                        fileReader.close();
                        board = new Board(boardValues, foods);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        GridPane gridPane = new GridPane();
        gridPane.add(openFileButton, 0, 0);

        primaryStage.setScene(new Scene(gridPane));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
