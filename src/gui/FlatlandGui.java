package gui;

import flatland.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import java.util.ArrayList;

public class FlatlandGui {/*
    private static double PLAY_SPEED = 150;
    private static boolean isPlaying = false;
    private static Timeline loop;
    private static int width = 40;
    private static int height = 40;
    private static ImageView agentImageView;
    private static GridPane flatlandGrid;
   // private static ImageView[][] referenceArray = new ImageView[Board.SIZE][Board.SIZE];
    private static ArrayList<Board> boards;
    private static Board currentBoard;
    private static int boardCounter = 0;
    private static Agent agent;
    private static int timeStep = 0;
    private static Image agentImage = new Image("/images/agent.png", true);
    private static Image foodImage = new Image("/images/food.png", true);
    private static Image poisonImage = new Image("/images/poison.png", true);
    private static Label boardCountLabel;
    private static Label stepCountLabel;
    private static Label foodEatenLabel;
    private static Label poisonEatenLabel;

    public static AnchorPane initGUI() {
        timeStep = 0;
        boardCounter= 0;

        boards = new ArrayList<Board>();

        AnchorPane root = new AnchorPane();

        flatlandGrid = new GridPane();
        flatlandGrid.setPrefSize(400, 400);
        flatlandGrid.setGridLinesVisible(true);
        agentImageView = new ImageView(agentImage);
        agentImageView.setFitHeight(40);
        agentImageView.setFitWidth(40);
        setBoard(currentBoard);

        GridPane controlPanel = new GridPane();
        controlPanel.setPrefSize(300, 300);
        controlPanel.setStyle("-fx-background-color: lightgrey");
        controlPanel.setPadding(new Insets(10));
        controlPanel.setVgap(5);
        controlPanel.setHgap(5);

        final Button stepButton = new Button("Step");
        stepButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (timeStep < Agent.MAX_TIMESTEPS) {
                    playTimeStep();
                }
            }
        });

        final Button playButton = new Button("Play");
        playButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (isPlaying) {
                    loop.stop();
                    isPlaying = false;
                    playButton.setText("Play");
                } else {
                    playButton.setText("Pause");
                    loop = new Timeline(new KeyFrame(Duration.millis(PLAY_SPEED), new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent actionEvent) {
                            if (timeStep < Agent.MAX_TIMESTEPS) {
                                playTimeStep();
                            }
                        }
                    }));
                    loop.setCycleCount(Timeline.INDEFINITE);
                    loop.play();
                    isPlaying = true;
                }
            }
        });
        final Button nextBoardButton = new Button("Next board");
        nextBoardButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                getNextBoard();
            }
        });

        final Slider playSpeedSlider = new Slider(0, 2000, FlatlandGui.PLAY_SPEED);
        playSpeedSlider.setBlockIncrement(100);
        playSpeedSlider.setStyle("-fx-padding: 10px");
        final Label playSpeedValue = new Label(Double.toString(playSpeedSlider.getValue()));

        playSpeedSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldVal, Number newVal) {
                FlatlandGui.PLAY_SPEED = newVal.doubleValue();
                playSpeedValue.setText(String.format("%.1f", newVal));
            }
        });

        Label numberOfBoardsLabel = new Label("Number of boards: ");
        final TextField numberOfBoardsInput = new TextField();
        numberOfBoardsInput.setMaxWidth(50);
        Button generateBoardsButton = new Button("Generate random board");
        generateBoardsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                int numberOfBoards = Integer.parseInt(numberOfBoardsInput.getText());
                //boards = neuralProblem.generateScenarios(numberOfBoards);
                boardCounter = 0;
                currentBoard = boards.get(boardCounter).clone();
                setBoard(currentBoard);
            }
        });
        controlPanel.add(boardCountLabel, 0, 0);
        controlPanel.add(stepCountLabel, 0, 1);
        controlPanel.add(foodEatenLabel, 0, 2);
        controlPanel.add(poisonEatenLabel, 0, 3);
        controlPanel.add(stepButton, 0, 4);
        controlPanel.add(playButton, 0, 5);
        controlPanel.add(playSpeedSlider, 0, 6);
        controlPanel.add(playSpeedValue, 1, 6);
        controlPanel.add(nextBoardButton, 0, 7);
        controlPanel.add(numberOfBoardsLabel, 0, 8);
        controlPanel.add(numberOfBoardsInput, 1, 8);
        controlPanel.add(generateBoardsButton, 0, 9);

        GridPane mainGrid = new GridPane();
        mainGrid.add(flatlandGrid, 0, 0);
        mainGrid.add(controlPanel, 1, 0);
        root.getChildren().add(mainGrid);
        root.getChildren().add(agentImageView);
        return root;
    }

    public static void setBoard(Board board) {
        agent = new Agent();
        timeStep = 0;
        updateLabels(agent);
        Cell[][] cell = board.getCells();
        ImageView imageView = null;
        for (int i = 0; i < cell.length; i++) {
            for (int j = 0; j < cell[i].length; j++) {
                Cell currentCell = cell[i][j];
                if (currentCell.getType() == Cell.Type.Food) {
                    imageView = new ImageView(foodImage);
                } else if (currentCell.getType() == Cell.Type.Poison) {
                    imageView = new ImageView(poisonImage);
                } else {
                    imageView = new ImageView();
                }
                imageView.setFitWidth(40);
                imageView.setFitHeight(40);
                if (referenceArray[i][j] != null) {
                    flatlandGrid.getChildren().remove(referenceArray[i][j]);
                }
                flatlandGrid.add(imageView, i, j);
                referenceArray[i][j] = imageView;
            }
        }

        move(agent.getX(), agent.getY());

    }

    public static void playTimeStep() {
        if (timeStep == 0) {
            agent.eat(currentBoard);
        }
        agent.playStep(currentBoard);
        move(agent.getX(), agent.getY());
        timeStep++;
        updateLabels(agent);
    }

    public static void getNextBoard() {
        boardCounter++;
        if (boardCounter < boards.size()) {
            currentBoard = boards.get(boardCounter).clone();
            setBoard(currentBoard);
            updateLabels(agent);
        }
    }

    public static void move(int x, int y) {
        switch (agent.getHeadDirection()) {
            case Left:
                agentImageView.rotateProperty().setValue(-90);
                break;
            case Up:
                agentImageView.rotateProperty().setValue(0);
                break;
            case Right:
                agentImageView.rotateProperty().setValue(90);
                break;
            case Down:
                agentImageView.rotateProperty().setValue(180);
                break;
        }
        agentImageView.setX(x*width);
        agentImageView.setY(y*height);

        flatlandGrid.getChildren().remove(referenceArray[x][y]);
        ImageView emptyImageView = new ImageView();
        emptyImageView.setFitWidth(40);
        emptyImageView.setFitHeight(40);
        flatlandGrid.add(emptyImageView, x, y);
    }

    public static void updateLabels(Agent agent) {
        if (boardCountLabel == null) boardCountLabel = new Label();
        if (stepCountLabel == null) stepCountLabel = new Label();
        if (foodEatenLabel == null) foodEatenLabel = new Label();
        if (poisonEatenLabel == null) poisonEatenLabel = new Label();
        boardCountLabel.setText("Board: "+(boardCounter+1)+" of "+boards.size());
        stepCountLabel.setText("Steps: "+timeStep+" of "+Agent.MAX_TIMESTEPS);
        foodEatenLabel.setText("Food: "+agent.getFoodEaten());
        poisonEatenLabel.setText("Poison: "+agent.getPoisonEaten());
    }*/
}
