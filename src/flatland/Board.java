package flatland;

public class Board {
    private int[][] board;
    private int width;
    private int height;
    private int foods;
    
    public Board(int width, int height, int foods) {
        this.width = width;
        this.height = height;
        this.foods = foods;
        this.board = new int[width][height];
    }

    public Board(int[][] cells, int foods) {
        this.board = cells;
        this.width = cells.length;
        this.height = cells[0].length;
        this.foods = foods;
    }

    public int[][] getCells() {
        return board;
    }

    public int getCell(int x, int y) {
        Position position = new Position(x, y);
        toroidalCheck(position);
        return board[position.getX()][position.getY()];
    }

    public void toroidalCheck(Position position) {
        if (position.getX() < 0) position.setX(width - 1);
        if (position.getY() < 0) position.setY(height - 1);
        if (position.getX() >= width) position.setX(0);
        if (position.getY() >= height) position.setY(0);
    }


    public Board clone() {
        int[][] cells = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                cells[i][j] = board[i][j];
            }
        }
        return new Board(cells, this.foods);
    }
}
