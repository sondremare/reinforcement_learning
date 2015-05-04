package flatland;

public class Agent {
    private Position position;
    private Direction headDirection;
    private int poisonEaten, foodEaten;
    public static int FOOD = 0;
    public static int POISION = 1;
    public static int MAX_TIMESTEPS = 60;
    private int SENSORS = 6;
    public static int FOOD_FRONT_SENSOR = 0;
    public static int FOOD_LEFT_SENSOR = 1;
    public static int FOOD_RIGHT_SENSOR = 2;
    public static int POISON_FRONT_SENSOR = 3;
    public static int POISON_LEFT_SENSOR = 4;
    public static int POISON_RIGHT_SENSOR = 5;
    
    public Agent(int x, int y) {
        position = new Position(x, y);
        headDirection = Direction.Right;
    }

    public Direction getHeadDirection() {
        return headDirection;
    }

    public int getX() {
        return position.getX();
    }

    public int getY() {
        return position.getY();
    }

    public int getPoisonEaten() {
        return poisonEaten;
    }

    public void setPoisonEaten(int poisonEaten) {
        this.poisonEaten = poisonEaten;
    }

    public int getFoodEaten() {
        return foodEaten;
    }

    public void setFoodEaten(int foodEaten) {
        this.foodEaten = foodEaten;
    }

    /**
     * Returns a double array with the sensed items in order of: Front, Left, Right
     * Values are:
     *  1 - Food
     *  -1 - Poison
     *  0 - Nothing
     */
    public double[] getSensorValues(Board board) {
        double[] sensedValues = new double[SENSORS];
        Cell.Type frontSensorCellType = null;
        Cell.Type leftSensorCellType = null;
        Cell.Type rightSensorCellType = null;
        switch (headDirection) {
            case Up:
                frontSensorCellType = getCellType(new Position(position.getX(), position.getY()-1), board);
                leftSensorCellType = getCellType(new Position(position.getX() - 1, position.getY()), board);
                rightSensorCellType = getCellType(new Position(position.getX() + 1, position.getY()), board);
                break;
            case Left:
                frontSensorCellType = getCellType(new Position(position.getX() - 1, position.getY()), board);
                leftSensorCellType = getCellType(new Position(position.getX(), position.getY() + 1), board);
                rightSensorCellType = getCellType(new Position(position.getX(), position.getY() - 1), board);
                break;
            case Right:
                frontSensorCellType = getCellType(new Position(position.getX() + 1, position.getY()), board);
                leftSensorCellType = getCellType(new Position(position.getX(), position.getY() - 1), board);
                rightSensorCellType = getCellType(new Position(position.getX(), position.getY() + 1), board);
                break;
            case Down:
                frontSensorCellType = getCellType(new Position(position.getX(), position.getY() + 1), board);
                leftSensorCellType = getCellType(new Position(position.getX() + 1, position.getY()), board);
                rightSensorCellType = getCellType(new Position(position.getX() - 1, position.getY()), board);
                break;
        }
        if (frontSensorCellType == Cell.Type.Food) {
            sensedValues[FOOD_FRONT_SENSOR] = 1;
        } else if (frontSensorCellType == Cell.Type.Poison) {
            sensedValues[POISON_FRONT_SENSOR] = 1;
        }
        if (leftSensorCellType == Cell.Type.Food) {
            sensedValues[FOOD_LEFT_SENSOR] = 1;
        } else if (leftSensorCellType == Cell.Type.Poison) {
            sensedValues[POISON_LEFT_SENSOR] = 1;
        }
        if (rightSensorCellType == Cell.Type.Food) {
            sensedValues[FOOD_RIGHT_SENSOR] = 1;
        } else if (rightSensorCellType == Cell.Type.Poison) {
            sensedValues[POISON_RIGHT_SENSOR] = 1;
        }
        return sensedValues;
    }

    public int[] play(Board board, int sleepTime) {
        foodEaten = 0;
        poisonEaten = 0;
        //eat(board);
        for (int i = 0; i < MAX_TIMESTEPS; i++) {
            playStep(board);
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        int[] eaten = {foodEaten, poisonEaten};
        return eaten;
    }

    public void playStep(Board board) {
        double[] sensorValues = getSensorValues(board);
        //neuralNetwork.setInput(sensorValues);
        //double[] output = neuralNetwork.getOutput();
        //TODO temporary for loop, need to use threshholds
        Movement movement = Movement.Stay;
        int highestIndex = 0;//getMaxIndex(output);
        if (highestIndex == 0) movement = Movement.Forward;
        else if (highestIndex == 1) movement = Movement.Left;
        else if (highestIndex == 2) movement = Movement.Right;
        move(board, movement); //TODO base it on the phenotypes weighting
        //eat(board);
    }
    
    private int getMaxIndex(double[] output) {
        double highestOutput = 0;
        int highestIndex = 0;
        for (int j = 0; j < output.length; j++) {
            if (output[j] > highestOutput) {
                highestOutput = output[j];
                highestIndex = j;
            }
        }
        return highestIndex;
    }

    public void eat(Board board) {
        Cell.Type cellType = getCellType(position, board);
        switch (cellType) {
            case Food:
                foodEaten++;
                break;
            case Poison:
                poisonEaten++;
                break;
        }
        emptyCell(position, board);
    }

    public void emptyCell(Position position, Board board) {
        board.emptyCell(position.getX(), position.getY());
    }
    public Cell.Type getCellType(Position position, Board board) {
        return board.getCell(position.getX(), position.getY()).getType();
    }

    public void move(Board board, Movement movement) {
        switch (headDirection) {
            case Up:
                if (movement == Movement.Forward) {
                    position.setY(position.getY() - 1);
                } else if (movement == Movement.Left) {
                    headDirection = Direction.Left;
                    position.setX(position.getX() - 1);
                } else if (movement == Movement.Right) {
                    headDirection = Direction.Right;
                    position.setX(position.getX() + 1);
                }
                break;
            case Left:
                if (movement == Movement.Forward) {
                    position.setX(position.getX() - 1);
                } else if (movement == Movement.Left) {
                    headDirection = Direction.Down;
                    position.setY(position.getY() + 1);
                } else if (movement == Movement.Right) {
                    headDirection = Direction.Up;
                    position.setY(position.getY() - 1);
                }
                break;
            case Right:
                if (movement == Movement.Forward) {
                    position.setX(position.getX() + 1);
                } else if (movement == Movement.Left) {
                    headDirection = Direction.Up;
                    position.setY(position.getY() - 1);
                } else if (movement == Movement.Right) {
                    headDirection = Direction.Down;
                    position.setY(position.getY() + 1);
                }
                break;
            case Down:
                if (movement == Movement.Forward) {
                    position.setY(position.getY() + 1);
                } else if (movement == Movement.Left) {
                    headDirection = Direction.Right;
                    position.setX(position.getX() + 1);
                } else if (movement == Movement.Right) {
                    headDirection = Direction.Left;
                    position.setX(position.getX() - 1);
                }
                break;

        }
        board.toroidalCheck(position);
    }

    public enum Direction {
        Left, Right, Up, Down
    }

    public enum Movement {
        Left, Right, Forward, Stay
    }
}
