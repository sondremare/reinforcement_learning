package learning;

import flatland.Agent;
import flatland.Board;
import flatland.Position;

import java.util.*;

public class QLearning {
    private Random random = new Random();
    private HashMap<String, Double> qStates;
    private static final List<Agent.Direction> ACTIONS = Arrays.asList(Agent.Direction.values());
    private int numberOfActions = ACTIONS.size();
    private Board board;

    private double learningRate = 0.5;
    private double discountRate = 1;


    public QLearning(Board board) {
        this.qStates = new HashMap<String, Double>();
        //init(board);
        this.board = board;
    }

    public void init(Board board) {
        String boardRepresentation = board.getBoardStringRepresentation();
        for (int i = 0; i < board.getWidth(); i++) {
            for (int j = 0; j < board.getHeight(); j++) {
                for (int k = 0; k < ACTIONS.size(); k++) {
                    String key = "" + i + j + k + boardRepresentation;
                    qStates.put(key, 0.0);
                }
            }
        }
    }

    public Board getBoard() {
        return board;
    }

    public String getKey(Agent.Direction action) {
        Position agentPosition = board.getAgent().getPosition();
        return ""+agentPosition.getX()+agentPosition.getY()+ACTIONS.indexOf(action)+board.getBoardStringRepresentation();
    }

    public void playStep() {
        Agent.Direction action = selectAction(board, 0); //best action
        board.play(action);
    }

    public void iterate(int count) {
        Agent.Direction action;
        for (int i = 0; i < count; i++) {
            System.out.println(i);
            board = board.clone();
            while (!board.isFinished()) {
                learningRate = 1;//(double)1/(i+1);
                discountRate = 1;//(double)1/(i+1);
                action = selectAction(board, 0.5);//(double)1/(i+1));
                String oldStateKey = getKey(action); //agentPosition.getX()+agentPosition.getY()+ACTIONS.indexOf(action)+board.getBoardStringRepresentation();
                board.play(action);
                updateQValue(action);
            }
        }
    }

    public void updateQValue(Agent.Direction action) {
        Object state = qStates.get(getKey(action));
        double oldVal = 0.0;
        if (state != null) oldVal = (Double)state;
        double max = getBestActionValue();
        String key = getKey(action);
        double reward = board.reward(board.getAgent().getPosition());
        double newVal = oldVal + learningRate * ( reward  + discountRate * max - oldVal);
        qStates.put(key, newVal);
    }

    public double getBestActionValue() {
        double max = -Double.MAX_VALUE;
        for (Agent.Direction action : ACTIONS) {
            double val = 0.0;
            Object state = qStates.get(getKey(action));
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
