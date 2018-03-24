package game;

public class Game {
  private Player playerOne;
  private Player playerTwo;
  private Player currentPlayer;
  private State state;

  /**
     * Constructs a new Game.
     *
     * @param playerOne  the first player, not null
     * @param playerTwo  the second player, not null
     * @param state      the initial game state, not null
     */
  public Game(Player playerOne, Player playerTwo, State state) {
    assert (playerOne != null);
    assert (playerTwo != null);
    assert (state != null);

    this.playerOne = playerOne;
    this.playerTwo = playerTwo;
    this.state = state;
    currentPlayer = playerOne;
  }

  /**
     * Getter for state.
     *
     * @return the state, not null
     */
  public State getState() {
    return state;
  }

  /**
     * Update the game for one turn:
     * 1) get move from current player
     * 2) apply the move to the current game state
     * 3) update the current player
     *
     * @throws IllegalStateException if the game is done, i.e., isDone returns true.
     */
  public void update() {
    if (isDone()) {
      throw new IllegalStateException("Game::update: Game is done.");
    }

    currentPlayer.calculateMove(state);
    GameMove move = currentPlayer.getMove();
    state = state.applyGameMove(move);
    currentPlayer = (currentPlayer == playerOne) ? playerTwo : playerOne;
  }

  /**
     * Getter for the winner.
     *
     * @return the player id of the winner or 0 if there is no winner
     * @throws IllegalStateException if the game is not yet done, i.e., isDone returns false.
     */
  public int getWinner() {
    if (!isDone()) {
      throw new IllegalStateException("Game::getWinner: Game is not done.");
    }

    return state.getWinner();
  }

  /**
     * Checks if the game is over.
     *
     * @return true if the game is over and false otherwise
     */
  public boolean isDone() {
    return state.isDone();
  }

  /**
     * Get message about the game, mostly from players.
     *
     * @return the message
     */
  public String getMessage() {
    String message = currentPlayer.getMessage();
    if (message == null) {
      return null;
    }
    int currentPlayerId = (currentPlayer == playerOne) ? 1 : 2;
    return "Player " + currentPlayerId + ": " + message;
  }
}
