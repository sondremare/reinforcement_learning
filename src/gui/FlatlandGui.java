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

public class FlatlandGui {
    private static double PLAY_SPEED = 150;
    private static boolean isPlaying = false;
    private static Timeline loop;
    private static int WIDTH = 25;
    private static int HEIGHT = 25;
    private static ImageView agentImageView;
    private static GridPane flatlandGrid;
    private static ImageView[][] referenceArray;
    private static Board currentBoard;
    private static Agent currentAgent;
    private static int timeStep = 0;
    private static Image agentImage = new Image("/images/agent.png", true);
    private static Image foodImage = new Image("/images/food.png", true);
    private static Image poisonImage = new Image("/images/poison.png", true);
    private static Label boardCountLabel;
    private static Label stepCountLabel;
    private static Label foodEatenLabel;
    private static Label poisonEatenLabel;

    public static AnchorPane initGUI(Board board, Agent currentAgent) {
        timeStep = 0;

        AnchorPane root = new AnchorPane();
        flatlandGrid = new GridPane();
        flatlandGrid.setPrefSize(400, 400);
        flatlandGrid.setGridLinesVisible(true);
        agentImageView = new ImageView(agentImage);
        agentImageView.setFitHeight(HEIGHT);
        agentImageView.setFitWidth(WIDTH);
        setBoard(board, currentAgent);

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
        /*final Button nextBoardButton = new Button("Next board");
        nextBoardButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                getNextBoard();
            }
        });*/

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
                //boardCounter = 0;
                //currentBoard = boards.get(boardCounter).clone();
                //setBoard(currentBoard);
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
        //controlPanel.add(nextBoardButton, 0, 7);
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

    public static void setBoard(Board board, Agent agent) {
        timeStep = 0;
        currentBoard = board;
        currentAgent = agent;
        updateLabels(agent);
        int[][] cells = board.getCells();
        referenceArray = new ImageView[cells.length][cells[0].length];
        ImageView imageView = null;
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                int currentCell = cells[i][j];
                if (currentCell > 0) {
                    imageView = new ImageView(foodImage);
                } else if (currentCell == -1) {
                    imageView = new ImageView(poisonImage);
                } else {
                    imageView = new ImageView();
                }
                imageView.setFitWidth(WIDTH);
                imageView.setFitHeight(HEIGHT);
                if (referenceArray[i][j] != null) {
                    flatlandGrid.getChildren().remove(referenceArray[i][j]);
                }
                flatlandGrid.add(imageView, j, i);
                referenceArray[i][j] = imageView;
            }
        }
        move(currentAgent.getX(), currentAgent.getY());

    }

    public static void playTimeStep() {
        if (timeStep == 0) {
            //currentAgent.eat(currentBoard);
        }
        //currentAgent.playStep(currentBoard);
        move(currentAgent.getX(), currentAgent.getY());
        timeStep++;
        updateLabels(currentAgent);
    }

    public static void move(int x, int y) {
        switch (currentAgent.getHeadDirection()) {
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
        agentImageView.setX(x * WIDTH);
        agentImageView.setY(y * HEIGHT);

        flatlandGrid.getChildren().remove(referenceArray[x][y]);
        ImageView emptyImageView = new ImageView();
        emptyImageView.setFitWidth(WIDTH);
        emptyImageView.setFitHeight(HEIGHT);
        flatlandGrid.add(emptyImageView, x, y);
    }

    public static void updateLabels(Agent agent) {
        if (boardCountLabel == null) boardCountLabel = new Label();
        if (stepCountLabel == null) stepCountLabel = new Label();
        if (foodEatenLabel == null) foodEatenLabel = new Label();
        if (poisonEatenLabel == null) poisonEatenLabel = new Label();
        stepCountLabel.setText("Steps: "+timeStep+" of "+Agent.MAX_TIMESTEPS);
        foodEatenLabel.setText("Food: "+agent.getFoodEaten());
        poisonEatenLabel.setText("Poison: "+agent.getPoisonEaten());
    }
}
