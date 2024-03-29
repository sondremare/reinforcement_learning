package learning;

import flatland.Agent;
import flatland.Board;
import flatland.Position;

import java.text.DecimalFormat;
import java.util.*;

public class QLearning {
    private Random random = new Random();
    private HashMap<String, Double> qStates;
    private HashMap<String, Double> eligibilityTraces;
    private ArrayList<String> visitedStates;
    private static final List<Agent.Direction> ACTIONS = Arrays.asList(Agent.Direction.values());
    private int numberOfActions = ACTIONS.size();
    private Board board;

    public static boolean useEligibilityTraces;
    public static boolean useSoftMax;
    public static double learningRate;
    public static double discountRate;
    public static double traceDecayFactor;


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
        double temperatureMax = 10;
        double temperatureMin = 0.1;

        for (int i = 0; i < count; i++) {
            board = board.clone();
            if (useEligibilityTraces) {
                this.eligibilityTraces = new HashMap<String, Double>();
                this.visitedStates = new ArrayList<String>();
            }
            double probability = (double)1/(i+1);
            while (!board.isFinished()) {
                Position currentPosition = new Position(board.getAgent().getPosition());
                if (useSoftMax) {
                    double temperature = temperatureMin + ((temperatureMax - temperatureMin)/(double)count)*(count - i);
                    action = getActionWithBoltzmannProbability(currentPosition, temperature);
                } else {
                    action = selectAction(board, probability);
                }
                board.play(action);
                updateQValue(action, currentPosition);
            }
            System.out.println(i);
        }
        long later = System.currentTimeMillis();
        double runTime = (double)(later - now)/1000;
        DecimalFormat df = new DecimalFormat("#.##");
        String runTimeFormatted = df.format(runTime);
        System.out.println("Runtime: "+runTimeFormatted+"s");
    }

    public void updateQValue(Agent.Direction action, Position currentPosition) {
        String currentKey = getKey(action, currentPosition);
        double oldVal = getQValue(currentKey);
        double max = getBestActionValue(board.getAgent().getPosition());
        double reward = board.reward(board.getAgent().getPosition());
        double error = reward  + discountRate * max - oldVal;
        if (useEligibilityTraces) {
            visitedStates.add(currentKey);
            double eVal = getEligibilityValue(currentKey);
            double qMax = getBestActionValue(currentPosition);
            if (qMax == oldVal) {
                eligibilityTraces.put(currentKey, eVal + 1);
            } else {
                eligibilityTraces.put(currentKey, 0.0);
            }
            updateEligibilityValues(error);
        } else {
            double newVal = oldVal + learningRate * error;
            qStates.put(getKey(action, currentPosition), newVal);
        }
        board.eat(board.getAgent().getPosition());
    }

    public void updateEligibilityValues(double error) {
        for (String key : visitedStates) {
            double oldEVal = getEligibilityValue(key);
            double oldQVal = getQValue(key);
            double newQval = oldQVal + learningRate * error * oldEVal;
            qStates.put(key, newQval);
            Position position = new Position(Character.getNumericValue(key.charAt(0)), Character.getNumericValue(key.charAt(1)));
            double qMax = getBestActionValue(position);
            if (qMax == newQval) {
                double newEVal = oldEVal * discountRate * traceDecayFactor;
                eligibilityTraces.put(key, newEVal);
            } else {
                eligibilityTraces.put(key, 0.0);
            }

        }
    }

    public Agent.Direction getActionWithBoltzmannProbability(Position currentPosition, double temperature) {
        double sum = 0.0;
        double[] values = new double[ACTIONS.size()];
        for (Agent.Direction action : ACTIONS) {
            String key = getKey(action, currentPosition);
            try {
                values[ACTIONS.indexOf(action)] = qStates.get(key);
            } catch (Exception e) {
                qStates.put(key, Board.EAT_FOOD_REWARD);
                values[ACTIONS.indexOf(action)] = Board.EAT_FOOD_REWARD;
            }
            sum += Math.exp(values[ACTIONS.indexOf(action)] / temperature);
        }
        NavigableMap<Double, Agent.Direction> rouletteWheelMap = new TreeMap<Double, Agent.Direction>();
        double accumulatedRange = 0;
        for (Agent.Direction action : ACTIONS) {
            accumulatedRange += Math.exp(values[ACTIONS.indexOf(action)] / temperature) / sum;
            rouletteWheelMap.put(accumulatedRange, action);
        }
        return rouletteWheelMap.ceilingEntry(random.nextDouble() * accumulatedRange).getValue();
    }

    public double getQValue(String key) {
        Object sap = qStates.get(key);
        return (sap != null) ? (Double)sap : 0.0;
    }

    public double getEligibilityValue(String key) {
        Object sap = eligibilityTraces.get(key);
        return (sap != null) ? (Double)sap : 0.0;
    }

    public double getBestActionValue(Position position) {
        double max = -Double.MAX_VALUE;
        for (Agent.Direction action : ACTIONS) {
            double val = getQValue(getKey(action, position));
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
        for (Agent.Direction action : ACTIONS) {
            double value = getQValue(getKey(action, position));
            if (value > bestValue) {
                bestValue = value;
                bestAction = action;
            }
        }
        if (bestAction == null && randomIfNoBestAction) bestAction = ACTIONS.get(random.nextInt(numberOfActions));
        return bestAction;
    }

    public void setBoard(Board board) {
        this.board = board;
    }
}
