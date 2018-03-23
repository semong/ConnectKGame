package game;

class Heuristic {

  /**
     * Evaluates a given state.
     *
     * @param state  the state being evaluated, not null
     * @return the heuristic value of state
     */
  public double evaluate(State state) {
    assert (state != null);

    if (!state.isDone()) {
      int[] winningLines = winningLines(state);
      return winningLines[1] - winningLines[2];
    }
    if (state.getWinner() == 1) {
      return Double.POSITIVE_INFINITY;
    } else if (state.getWinner() == 2) {
      return Double.NEGATIVE_INFINITY;
    } else {  // Draw
      return 0;
    }
  }

  // Computes the player's winning lines.
  private int[] winningLines(State state) {
    assert (state != null);

    int[] playerLines = {0, 0, 0};

    for (GameMove move : state) {
      int[] playerCount = countPlayersInLine(state, move, 1, 0);
      int owner = lineOwner(playerCount);
      playerLines[owner]++;

      playerCount = countPlayersInLine(state, move, 0, 1);
      owner = lineOwner(playerCount);
      playerLines[owner]++;

      playerCount = countPlayersInLine(state, move, -1, 1);
      owner = lineOwner(playerCount);
      playerLines[owner]++;

      playerCount = countPlayersInLine(state, move, 1, 1);
      owner = lineOwner(playerCount);
      playerLines[owner]++;
    }

    return playerLines;
  }

  // For a given line count computes the owner of the line.
  private int lineOwner(int[] playerCount) {
    assert (playerCount != null);
    assert (playerCount.length == 3);

    int owner = 0;
    if (playerCount[1] > 0 && playerCount[2] == 0) {
      owner = 1;
    } else if (playerCount[1] == 0 && playerCount[2] > 0) {
      owner = 2;
    }
    return owner;
  }

  // For a given line (specified by a starting move and direction) 
  //count how many pieces each player has in the line.
  private int[] countPlayersInLine(State state, GameMove move, 
      int rowDirection, int columnDirection) {
    assert (state != null);
    assert (move != null);
    assert (state.isOnBoard(move));
    assert (-1 <= rowDirection && rowDirection <= 1);
    assert (-1 <= columnDirection && columnDirection <= 1);

    int winLength = state.getconnectkParameters().getWinLength();
    int[] playerCount = {0, 0, 0};

    GameMove endGameMove = GameMove.getInstance(move.getRow() + rowDirection * (winLength - 1),
                move.getColumn() + columnDirection * (winLength - 1));
    if (state.isOnBoard(endGameMove)) {
      for (int i = 0; i < winLength; i++) {
        GameMove possibleMove = GameMove.getInstance(move.getRow()
            + rowDirection * i, move.getColumn() + columnDirection * i);
        int player = state.getPlayerAt(possibleMove);
        playerCount[player]++;
      }
    }
    return playerCount;
  }
}
