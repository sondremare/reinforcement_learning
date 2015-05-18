package flatland;

public class Board {
    private Cell[][] initalBoard;
    private Cell[][] board;
    private int width;
    private int height;
    private int foodCount;
    private Cell[] foodArray;
    private int startingX;
    private int startingY;
    private Agent agent;
    private String boardStringRepresentation;
    public static int EMPTY_CELL = 0;
    public static double STEP_PENALTY = -1;
    public static double EAT_FOOD_REWARD = 10;
    public static double EAT_POISON_PENALTY = -15;
    public static double RETURN_HOME_REWARD = 10;
    
    public Board(Cell[][] cells, int foods, int startingX, int startingY) {
        this.board = cells;
        this.width = cells.length;
        this.height = cells[0].length;
        this.foodCount = foods;
        this.foodArray = new Cell[foods];
        this.initalBoard = new Cell[width][height];
        int foodCounter = 0;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                this.initalBoard[i][j] = this.board[i][j].clone();
                if (board[i][j].getType() == Cell.Type.Food) {
                    this.foodArray[foodCounter] = board[i][j];
                    foodCounter++;
                }
            }
        }
        updateBoardStringRepresentation();
        this.startingX = startingX;
        this.startingY = startingY;
        this.agent = new Agent(startingX, startingY);
    }

    public int getFoodCount() {
        return foodCount;
    }

    public String getBoardStringRepresentation() {
        return boardStringRepresentation;
    }

    public Agent getAgent() {
        return agent;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Cell[][] getCells() {
        return board;
    }

    public void updateBoardStringRepresentation() {
        String representation = "";
        for (int i = 0; i < foodArray.length; i++) {
            representation += foodArray[i].getValue();
        }
        boardStringRepresentation = representation;
    }

    public void play(Agent.Direction action) {
        agent.move(action);
        toroidalCheck(agent.getPosition());
    }

    public double reward(Position position) {
        double reward = STEP_PENALTY;
        if (isFinished()) {
            return RETURN_HOME_REWARD;
        }
        Cell.Type cellType = getCellType(position);
        if (cellType == Cell.Type.Food) {
            reward = EAT_FOOD_REWARD;
        } else if (cellType == Cell.Type.Poison) {
            reward = EAT_POISON_PENALTY;
        }
        return reward;
    }

    public void eat(Position position) {
        Cell.Type cellType = getCellType(position);
        agent.eat(cellType);
        emptyCellValue(position);
        updateBoardStringRepresentation();
    }

    public Cell.Type getCellType(Position position) {
        return board[position.getX()][position.getY()].getType();
    }

    public void emptyCellValue(Position position) {
        Cell cell = board[position.getX()][position.getY()];
        cell.setType(Cell.Type.Nothing);
        cell.setValue(EMPTY_CELL);
    }

    public void toroidalCheck(Position position) {
        if (position.getX() < 0) position.setX(width - 1);
        if (position.getY() < 0) position.setY(height - 1);
        if (position.getX() >= width) position.setX(0);
        if (position.getY() >= height) position.setY(0);
    }

    public boolean isFinished() {
        int foodSum = 0;
        for (int i = 0; i < foodArray.length; i++) {
            foodSum += foodArray[i].getValue();
        }
        return (foodSum == 0 && agent.getPosition().getX() == startingX && agent.getPosition().getY() == startingY);
    }

    public Board clone() {
        Cell[][] cells = new Cell[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                cells[i][j] = new Cell(initalBoard[i][j].getType(), initalBoard[i][j].getValue());
            }
        }
        return new Board(cells, this.foodCount, this.startingX, this.startingY);
    }

    public int getStartingX() {
        return startingX;
    }

    public int getStartingY() {
        return startingY;
    }
}
