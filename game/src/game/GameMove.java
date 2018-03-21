package game;

class GameMove {
  private int row;
  private int column;

  /**
     * Get instance of GameMove.
     *
     * @param row  the row
     * @param column  the column
     */
  public static GameMove getInstance(int row, int column) {
    return new GameMove(row, column);
  }

  // Constructor
  protected GameMove(int row, int column) {
    this.row = row;
    this.column = column;
  }

  /**
     * Getter for row.
     *
     * @return  the row
     */
  public int getRow() {
    return row;
  }

  /**
     * Getter for column.
     *
     * @return  the column
     */
  public int getColumn() {
    return column;
  }

  @Override
  public String toString() {
    return "GameMove{" + "row=" + row + ", column=" + column + '}';
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (object == null || getClass() != object.getClass()) {
      return false;
    }

    GameMove move = (GameMove) object;

    return row == move.row && column == move.column;
  }

  @Override
  public int hashCode() {
    int result = row;
    result = 31 * result + column;
    return result;
  }
}

