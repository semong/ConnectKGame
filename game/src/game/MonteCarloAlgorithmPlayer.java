package game;





import java.util.*;

import game.GameMove;
import game.MonteCarloAlgorithmPlayer;
import game.Player;
import game.State;


class MonteCarloAlgorithmPlayer implements Player {
  private int id;
  private Random random;
  private GameMove chosenGameMove;
  private Map<State, Double> wins = new HashMap<>();
  private Map<State, Double> plays = new HashMap<>();

  /**
     * Get an instance of a MonteCarloAlgorithmPlayer
     *
     * @param id  the id of the player, 1 or 2
     * @return a MonteCarloAlgorithmPlayer object, not null
     */
  public static Player getInstance(int id) {
    assert (id == 1 || id == 2);

    return new MonteCarloAlgorithmPlayer(id, new Random());
  }

  
  /**
   * Constructor
   * @param id Player ID
   * @param random Random number generator
   */
  protected MonteCarloAlgorithmPlayer(int id, Random random) {
    assert (id == 1 || id == 2);
    assert (random != null);

    this.id = id;
    this.random = random;
  }

  /**
   * Getter for ID
   */
  @Override
  public int getId() {
    return id;
  }

  /**
   * Chooses the best game move after running MCTS simulation for specified length of time.
   * @param state Current state
   */
  @Override
  public void calculateMove(State state) {
    assert (state != null);
    assert (!state.isDone());
    //int count = 0;           //uncomment to check number of iterations

    // Run simulations while time remains.
    long stopTime = System.nanoTime() + state.getconnectkParameters().getTimeLimit() * 1000000;
    while (stopTime > System.nanoTime()) {
      runSimulation(state, true);
      //count++;              //uncomment to check number of iterations
    }

    // Choose move with highest relative win rate based on simulations.
    double bestScore = Double.NEGATIVE_INFINITY;
    GameMove bestGameMove = null;
    for (GameMove move : state.getValidGameMoves()) {
      State nextState = state.applyGameMove(move);
      if (plays.containsKey(nextState)) {
        double score = wins.get(nextState) / plays.get(nextState);
        if (score > bestScore) {
          bestScore = score;
          bestGameMove = move;
        }
      }
    }

    chosenGameMove = bestGameMove;
    //System.out.println("Number of iterations: "+ count);       
    //uncomment ^ to check number of iterations

    assert (chosenGameMove != null);
  }

  /**
   * Returns the chosen best move.
   */
  @Override
  public GameMove getMove() {
    assert (chosenGameMove != null);

    return chosenGameMove;
  }

  @Override
  public String toString() {
    return "Monte Carlo Tree Search";
  }

 
  /**
   * Runs a single simulation
   * @param state The current state
   * @param record If node visited before, sets this boolean to true
   * @return Returns the best game move for the simulation
   */
  private int runSimulation(State state, boolean record) {
    assert (state != null);

    // Add entry for first unseen child on path.
    if (record && !plays.containsKey(state)) {
      plays.put(state, 0.0);
      wins.put(state, 0.0);
      record = false;
    }

    // Recursively continue simulation and record results.
    int winner;
    if (!state.isDone()) {
      List<GameMove> moves = state.getValidGameMoves();
      GameMove bestGameMove = calculateBestGameMove(state, moves);
      winner = runSimulation(state.applyGameMove(bestGameMove), record);
    } else {
      winner = state.getWinner();
    }
    recordResult(state, winner);
    return winner;
  }

  // Calculate best move for simulations.
  private GameMove calculateBestGameMove(State state, List<GameMove> moves) {
    assert (state != null);
    assert (moves != null);
    assert (!moves.isEmpty());

    // If there are any previously untaken moves, return one at random.
    List<GameMove> unexplored = new ArrayList<>();
    for (GameMove move : moves) {
      if (!plays.containsKey(state.applyGameMove(move))) {
        unexplored.add(move);
      }
    }
    if (!unexplored.isEmpty()) {
      return unexplored.get(random.nextInt(unexplored.size()));
    }

    // If all moves have been previously taken, return the best move based on UCB.
    double numberOfPlays = 0.0;
    for (GameMove move : moves) {
      numberOfPlays += plays.get(state.applyGameMove(move));
    }
    GameMove bestGameMove = null;
    double bestScore = Double.NEGATIVE_INFINITY;
    for (GameMove move : moves) {
      double score = calculateScore(state.applyGameMove(move), numberOfPlays);
      if (score > bestScore) {
        bestScore = score;
        bestGameMove = move;
      }
    }

    assert (bestGameMove != null);

    return bestGameMove;
  }


  /**
   * Calculate the score of a move based on the UCB formula.
   * @param state Current state
   * @param numberOfPlays
   * @return Reward received
   */
  private double calculateScore(State state, double numberOfPlays) {
    assert (state != null);

    double balance = 2;
    return wins.get(state) / plays.get(state) + balance 
        * Math.sqrt(Math.log(numberOfPlays) / plays.get(state));
  }

  // Records the result of a simulation.
  private void recordResult(State state, int winner) {
    assert (state != null);
    assert (winner == 0 || winner == 1 || winner == 2);

    if (plays.containsKey(state)) {
      plays.put(state, plays.get(state) + 1);
      int lastPlayer = (state.getCurrentPlayer() == 1) ? 2 : 1;
      if (winner == lastPlayer) {
        wins.put(state, wins.get(state) + 1);
      }
    }
  }

  @Override
  public String getMessage() {
    return null;
  }
}
