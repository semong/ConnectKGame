package game;

import java.util.List;
import java.util.Random;

/**
 * A class implementing a ConnectK player, using Monte Carlo Tree Search without any heuristics.
 */

/**
 * A class representing a single move in ConnectK or more accurately a position on the board.
 */
 
 
 class RandomPlayer implements Player {
  private int id;
  private GameMove move;
  private Random random;

    /**
     * Get an instance of a RandomPlayer
     *
     * @param id  the id of the player, 1 or 2
     * @return a RandomPlayer object, not null
     */
  public static Player getInstance(int id) {
    assert (id == 1 || id == 2);

    return new RandomPlayer(id, new Random());
  }

  // Constructor
  protected RandomPlayer(int id, Random random) {
    assert (id == 1 || id == 2);
    assert (random != null);

    this.id = id;
    this.random = random;
  }

  @Override
  public void calculateMove(State state) {
    assert (state != null);
    assert (!state.isDone());

    List<GameMove> moves = state.getValidGameMoves();
    move = moves.get(random.nextInt(moves.size()));
  }

  @Override
  public GameMove getMove() {
    assert (move != null);

    return move;
  }

  @Override
  public String toString() {
    return "Random (uniform)";
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public String getMessage() {
    return null;
  }
}
