package game;

import java.util.*;

class State implements Iterable<GameMove> {
  private connectkParameters parameters;
  public int board[][];
  private int marks = 0;
  public int winner = 0;

  public State(connectkParameters parameters) {
    assert (parameters != null);

    this.parameters = parameters;
    this.board = new int[parameters.getRows()][parameters.getColumns()];
  }

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

  public connectkParameters getconnectkParameters() {
    return parameters;
  }

  public int getCurrentPlayer() {
    return marks % 2 + 1;
  }

  public int getPlayerAt(GameMove move) {
    assert (move != null);

    return board[move.getRow()][move.getColumn()];
  }

  public boolean isOnBoard(GameMove move) {
    assert (move != null);

    boolean isValidRow = 0 <= move.getRow() && move.getRow() < parameters.getRows();
    boolean isValidColumn = 0 <= move.getColumn() && move.getColumn() < parameters.getColumns();
    return  isValidRow && isValidColumn;
  }

  public boolean isAvailable(GameMove move) {
    assert (move != null);

    return getPlayerAt(move) == 0;
  }

  public int getWinner() {
    if (!isDone()) {
      throw new IllegalStateException("State::getWinner: The game is not done.");
    }
    return winner;
  }

  public boolean isDone() {
    return marks == parameters.getRows() * parameters.getColumns() || winner > 0;
  }

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

  // Copy constructor
  private State(State other) {
    this(other.parameters);
    for (int row = 0; row < parameters.getRows(); row++) {
      System.arraycopy(other.board[row], 0, board[row], 0, parameters.getColumns());
    }
    marks = other.marks;
  }

  // Setter for the player at given position.
  private void setPlayerAt(GameMove move, int player) {
    assert (move != null);
    assert (isOnBoard(move));
    assert (isAvailable(move));
    assert (player == 1 || player == 2);

    board[move.getRow()][move.getColumn()] = player;
  }

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

  private boolean updateWinner(List<Integer> line, int player) {
    assert (player == 1 || player == 2);

    int winLength = parameters.getWinLength();
    int chainLength = 0;
    for (int e : line) {
      if (e == player) {
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

