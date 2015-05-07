package flatland;

public class Agent {
    private Position position;
    private Direction headDirection;
    private int poisonEaten, foodEaten;
    
    public Agent(int x, int y) {
        position = new Position(x, y);
        headDirection = Direction.Up;
        poisonEaten = 0;
        foodEaten = 0;
    }

    public Direction getHeadDirection() {
        return headDirection;
    }

    public Position getPosition() {
        return position;
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

    public int getFoodEaten() {
        return foodEaten;
    }

    public void eat(Cell.Type cellType) {
        switch (cellType) {
            case Food:
                foodEaten++;
                break;
            case Poison:
                poisonEaten++;
                break;
        }
    }

    public void move(Direction direction) {
        headDirection = direction;
        switch (direction) {
            case Up:
                position.setY(position.getY() - 1);
                break;
            case Left:
                position.setX(position.getX() - 1);
                break;
            case Right:
                position.setX(position.getX() + 1);
                break;
            case Down:
                position.setY(position.getY() + 1);
                break;
        }
    }

    public enum Direction {
        Left, Right, Up, Down
    }
}
