package game;

class connectkParameters {
  private int rows;
  private int columns;
  private int winLength;
  private boolean gravity;
  private long timeLimit;

  public static final int ROWS_MIN = 1;
  public static final int ROWS_MAX = 30;
  public static final int COLUMNS_MIN = 1;
  public static final int COLUMNS_MAX = 30;
  public static final int WIN_LENGTH_MIN = 1;
  public static final int WIN_LENGTH_MAX = 30;

  
  /**
   * Constructs a new connectkParameters objects from the passed parameters.
   * @param rows Number of rows on the board
   * @param columns Number of columns on the board
   * @param winLength Number of linked discs required to win
   * @param gravity Toggle gravity
   * @param timeLimit Time (in milliseconds) given for the MCTS algorithm to run
   */
  public connectkParameters(int rows, int columns, int winLength, boolean gravity, long timeLimit) {
    if (rows > ROWS_MAX || rows < ROWS_MIN) {
      throw new IllegalArgumentException("connectkParameters:connectkParameters: "
            + "rows out of bounds, " + rows);
    }
    if (columns > COLUMNS_MAX || columns < COLUMNS_MIN) {
      throw new IllegalArgumentException("connectkParameters:connectkParameters: "
            + "columns out of bounds, " + columns);
    }
    if (winLength > WIN_LENGTH_MAX || winLength < WIN_LENGTH_MIN) {
      throw new IllegalArgumentException("connectkParameters:connectkParameters: "
            + "winLength out of bounds, " + winLength);
    }

    this.rows = rows;
    this.columns = columns;
    this.winLength = winLength;
    this.gravity = gravity;
    this.timeLimit = timeLimit;
  }

  /**
   * 
   * @return Integer number of rows
   */
  public int getRows() {
    return rows;
  }

  /**
   * 
   * @return Integer number of columns
   */
  public int getColumns() {
    return columns;
  }

  /**
   * 
   * @return  Integer number of discs required to be chained to win
   */
  public int getWinLength() {
    return winLength;
  }

  /**
   * 
   * @return Boolean value for gravity
   */
  public boolean isGravity() {
    return gravity;
  }
  
  /**
   * 
   * @return Long Time in milliseconds given to run simulations of MCTS algorithm.
   */
  public long getTimeLimit() {
    return timeLimit;
  }

  @Override
  public String toString() {
    return "connectkParameters{" + "rows=" + rows + ", columns=" + columns 
      + ", winLength=" + winLength + ", gravity=" + gravity + '}';
  }
}

