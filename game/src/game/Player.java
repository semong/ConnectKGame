package game;
/**
 * This interface represents a single Player and contains abstract methods.
 * @author Semon Ganguly
 *
 */
public interface Player {

  /**
   * Returns the ID of the player
   * @return Integer ID of the player
   */
  int getId();

  /**
   * Calculates the next move
   * @param state current state
   */
  void calculateMove(State state);
  
  /**
   * Returns the move calculated by calculateMove()
   * @return The move to be played
   */
  GameMove getMove();

  /**
   * Used to receive messages
   * @return String message about the game
   */
  String getMessage();
}