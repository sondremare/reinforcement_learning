package learning;

import flatland.Agent;
import flatland.Board;
import flatland.Position;

import java.text.DecimalFormat;
import java.util.*;

public class QLearning {
    private Random random = new Random();
    private HashMap<String, Double> qStates;
    private static final List<Agent.Direction> ACTIONS = Arrays.asList(Agent.Direction.values());
    private int numberOfActions = ACTIONS.size();
    private Board board;

    private double learningRate;
    private double discountRate;


    public QLearning(Board board) {
        this.qStates = new HashMap<String, Double>();
        this.board = board;
    }

    public Board getBoard() {
        return board;
    }

    public String getKey(Agent.Direction action, Position position) {
        return ""+position.getX()+position.getY()+ACTIONS.indexOf(action)+board.getBoardStringRepresentation();
    }

    public void playStep() {
        Agent.Direction action = selectAction(board, 0); //probability 0 equals best action
        board.play(action);
    }

    public void iterate(int count) {
        long now = System.currentTimeMillis();
        Agent.Direction action;
        for (int i = 0; i < count; i++) {
            System.out.println(i);
            board = board.clone();
            while (!board.isFinished()) {
                learningRate = 1;//(double)1/(i+1);
                discountRate = 0.9;//(double)1/(i+1);
                double probability = (double)1/(i+1);//0.8 - ((double)i/(double)(1.5 * count));
                action = selectAction(board, probability);//(double)1/(i+1));
                Position currentPosition = new Position(board.getAgent().getPosition());
                board.play(action);
                updateQValue(action, currentPosition);
            }
        }
        long later = System.currentTimeMillis();
        double runTime = (double)(later - now)/1000;
        DecimalFormat df = new DecimalFormat("#.##");
        String runTimeFormatted = df.format(runTime);
        System.out.println("Runtime: "+runTimeFormatted+"s");
    }

    public void updateQValue(Agent.Direction action, Position currentPosition) {
        Object state = qStates.get(getKey(action, currentPosition));
        double oldVal = 0.0;
        if (state != null) oldVal = (Double)state;
        double max = getBestActionValue(board.getAgent().getPosition());
        double reward = board.reward(board.getAgent().getPosition());
        double newVal = oldVal + learningRate * ( reward  + discountRate * max - oldVal);
        qStates.put(getKey(action, currentPosition), newVal);
        board.eat(board.getAgent().getPosition());
    }

    public double getBestActionValue(Position position) {
        double max = -Double.MAX_VALUE;
        for (Agent.Direction action : ACTIONS) {
            double val = 0.0;
            Object state = qStates.get(getKey(action, position));
            if (state != null) val = (Double) state;
            if (val > max) {
                max = val;
            }
        }
        return max;
    }

    private Agent.Direction selectAction(Board board, double probability) {
        if (probability >= random.nextDouble()) {
            return ACTIONS.get(random.nextInt(numberOfActions));
        } else {
            return findBestAction(board, board.getAgent().getPosition(), true);
        }
    }

    public Agent.Direction findBestAction(Board board, Position position, boolean randomIfNoBestAction) {
        double bestValue = -Double.MAX_VALUE;
        Agent.Direction bestAction = null;
        String boardRepresentation = board.getBoardStringRepresentation();
        for (int i = 0; i < ACTIONS.size(); i++) {
            String key = "" + position.getX() + position.getY() + i + boardRepresentation;
            double value = 0.0;
            Object state = qStates.get(key);
            if (state != null) value = (Double) state;
            if (value > bestValue) {
                bestValue = value;
                bestAction = ACTIONS.get(i);
            }
        }
        if (bestAction == null && randomIfNoBestAction) bestAction = ACTIONS.get(random.nextInt(numberOfActions));
        return bestAction;
    }

    public void setBoard(Board board) {
        this.board = board;
    }
}
