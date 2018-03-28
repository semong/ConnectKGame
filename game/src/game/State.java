package game;

import java.util.*;

class State implements Iterable<GameMove> {
  private connectkParameters parameters;
  private int board[][];
  private int marks = 0;
  private int winner = 0;

  /**
   * Constructor
   * @param parameters Parameters for the game (rows, columns, winLength, gravity and time limit)
   */
  public State(connectkParameters parameters) {
    assert (parameters != null);

    this.parameters = parameters;
    this.board = new int[parameters.getRows()][parameters.getColumns()];
  }

  /**
   * Applies the given GameMove
   * @param move The desired move
   * @return State with the updated board
   */
  public State applyGameMove(GameMove move) {
    if (isDone()) {
      throw new IllegalStateException("State::applyGameMove: Game is done.");
    }

    int currentPlayer = marks % 2 + 1;
    State newState = new State(this);

    if (move != null && isOnBoard(move) && isAvailable(move)) {
      if (parameters.isGravity()) {
        move = applyGravity(move);
      }
      newState.setPlayerAt(move, currentPlayer);
      newState.marks = marks + 1;
      if (newState.updateWinner(move)) {
        newState.winner = currentPlayer;
      }
    } else  {
      newState.winner = (currentPlayer == 1) ? 2 : 1;
    }

    return newState;
  }

  /**
   * Getter for connectkParameters
   * @return Parameters (rows, columns, winLength, gravity and time limit)
   */
  public connectkParameters getconnectkParameters() {
    return parameters;
  }

  /**
   * Return ID of the current player.
   * @return Integer ID of the current player.
   */
  public int getCurrentPlayer() {
    return marks % 2 + 1;
  }

  /**
   * 
   * @param move GameMove object that represents the player's position.
   * @return Player ID.
   */
  public int getPlayerAt(GameMove move) {
    assert (move != null);  

    return board[move.getRow()][move.getColumn()];
  }

  /**
   * Checks if a spot is already filled on the board.
   * @param move GameMove object that represents the position.
   * @return True if the position specified is already filled.
   */
  public boolean isOnBoard(GameMove move) {
    assert (move != null);

    boolean isValidRow = 0 <= move.getRow() && move.getRow() < parameters.getRows();
    boolean isValidColumn = 0 <= move.getColumn() && move.getColumn() < parameters.getColumns();
    return  isValidRow && isValidColumn;
  }

  /**
   * Checks if a spot on the board is empty (available).
   * @param move GameMove object that represents the position.
   * @return True if the position specified is empty.
   */
  public boolean isAvailable(GameMove move) {
    assert (move != null);

    return getPlayerAt(move) == 0;
  }

  /**
   * Returns the winning player's ID once the game has finished.
   * @return Returns the Integer ID of the winning player.
   */
  public int getWinner() {
    if (!isDone()) {
      throw new IllegalStateException("State::getWinner: The game is not done.");
    }
    return winner;
  }

  /**
   * Checks if the game has finished.
   * @return True if the game has finished.
   */
  public boolean isDone() {
    return marks == parameters.getRows() * parameters.getColumns() || winner > 0;
  }

  /**
   * This method returns a list of valid moves which lie on the board. 
   * @return List of GameMoves that are valid.
   */
  public List<GameMove> getValidGameMoves() {
    List<GameMove> moves = new ArrayList<>();
    for (GameMove move : this) {
      boolean valid = (!parameters.isGravity() || move.getRow() == 0) 
          && isOnBoard(move) && isAvailable(move);
      if (valid) {
        moves.add(move);
      }
    }
    return moves;
  }

  @Override
  public Iterator<GameMove> iterator() {
    return new Iterator<GameMove>() {
      int row = 0;
      int column = 0;

      @Override
      public boolean hasNext() {
        return row < parameters.getRows() && column < parameters.getColumns();
      }

      @Override
      public GameMove next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }

        GameMove move = GameMove.getInstance(row, column);

        column++;
        if (column == parameters.getColumns()) {
          column = 0;
          row++;
        }

        return move;
      }
    };
  }

  /**
   * Applies gravity so the discs fall to the available bottom-most row.
   * @param move GameMove object that represents the position.
   * @return A new GameMove object with the disc at the bottom of the column.
   */
  private GameMove applyGravity(GameMove move) {
    assert (move != null);

    move = GameMove.getInstance(0, move.getColumn());
    for (int i = 1; i < parameters.getRows(); i++) {
      GameMove temp = GameMove.getInstance(i, move.getColumn());
      if (isOnBoard(temp) && isAvailable(temp)) {
        move = temp;
      } else {
        break;
      }
    }
    return move;
  }

  /**
   * Second constructor
   * @param other Create a state from this state
   */
  private State(State other) {
    this(other.parameters);
    for (int row = 0; row < parameters.getRows(); row++) {
      System.arraycopy(other.board[row], 0, board[row], 0, parameters.getColumns());
    }
    marks = other.marks;
  }


  /**
   * Setter for the player at given position.
   * @param move GameMove object that represents the position.
   * @param player player's ID
   */
  private void setPlayerAt(GameMove move, int player) {
    assert (move != null);
    assert (isOnBoard(move));
    assert (isAvailable(move));
    assert (player == 1 || player == 2);

    board[move.getRow()][move.getColumn()] = player;
  }

  /**
   * Checks the 4 possible directions of the connected discs to determine a winner.
   * @param move GameMove object that represents the position.
   * @return Returns true if winLength number of a player's discs 
   * are connected in any of the 4 directions. 
   */
  private boolean updateWinner(GameMove move) {
    assert (move != null);
    assert (isOnBoard(move));
    assert (!isAvailable(move));

    boolean vertical = updateWinner(move, 1, 0);
    boolean horizontal = updateWinner(move, 0, 1);
    boolean positiveDiagonal = updateWinner(move, -1, 1);
    boolean negativeDiagonal = updateWinner(move, 1, 1);

    return vertical || horizontal || positiveDiagonal || negativeDiagonal;
  }

  /**
   * Checks the direction of the line to determine the winner.
   * @param move GameMove object that represents the position.
   * @param row x-axis direction.
   * @param columnDirection y-axis direction.
   * @return Returns true if the length of the disc chain matches .
   * the required winLength in any direction. 
   */
  private boolean updateWinner(GameMove move, int rowDirection, int columnDirection) {
    assert (move != null);
    assert (isOnBoard(move));
    assert (!isAvailable(move));
    assert (-1 <= rowDirection && rowDirection <= 1);
    assert (-1 <= columnDirection && columnDirection <= 1);

    int winLength = parameters.getWinLength();
    int row = move.getRow();
    int column = move.getColumn();

    List<Integer> line = new ArrayList<>();
    for (int i = -winLength + 1; i <= winLength - 1; i++) {
      GameMove temp = GameMove.getInstance(row + rowDirection * i, column + columnDirection  * i);
      if (isOnBoard(temp)) {
        int player = getPlayerAt(temp);
        line.add(player);
      }
    }
    return updateWinner(line, getPlayerAt(move));
  }
  
  
  /**
 * Updates the winner ID according to the line and the owner of the line.
 * @param line List of integer values representing the line.
 * @param player Integer ID of the player.
 * @return True if the length of the disc chain matches the required winLength.
 */
  private boolean updateWinner(List<Integer> line, int player) {
    assert (player == 1 || player == 2);

    int winLength = parameters.getWinLength();
    int chainLength = 0;
    for (int currentPlayer : line) {
      if (currentPlayer == player) {
        chainLength++;
        if (chainLength == winLength) {
          return true;
        }
      } else {
        chainLength = 0;
      }
    }
    return false;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (object == null || getClass() != object.getClass()) {
      return false;
    }

    State moves = (State) object;

    return Arrays.deepEquals(board, moves.board);
  }

  @Override
  public int hashCode() {
    return Arrays.deepHashCode(board);
  }
}

