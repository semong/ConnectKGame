package game;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;



public class Presenter {
  // Start: FXML Widgets
  @FXML
  private Canvas gameCanvas;
  @FXML
  private TextField rowsTextField;
  @FXML
  private TextField columnsTextField;
  @FXML
  private TextField timeLimitTextField;
  @FXML
  private TextField winTextField;
  @FXML
  private CheckBox gravityCheckBox;
  @FXML
  private ChoiceBox<Player> playerChoiceBox;

  @FXML
  private ChoiceBox<Player> playerTwoChoiceBox;
  @FXML
  private Button startButton;
  @FXML
  private Button stopButton;
  @FXML
  private Button evaluateHeuristicButton;
  @FXML
  private TextArea logTextArea;
  // End: FXML Widgets

  // Start: Canvas parameters
  private double radius;
  private double xOffset;
  private double yOffset;
  // End: Canvas parameters

  // Start: Misc variables
  private Game game;
  private Thread gameLoopThread;
  private BlockingQueue<GameMove> outsideGameMove = new ArrayBlockingQueue<>(1);
  private String message = null;
  // End: Misc variables

  /**
   * A PrintStream that is displayed through the view.
   */
  private final PrintStream log = new PrintStream(new OutputStream() {
    @Override
    public void write(int payload) throws IOException {
      Platform.runLater(() -> logTextArea.appendText(String.valueOf((char) payload)));
    }
  }, true);

  @FXML
  private void initialize() {
    playerChoiceBox.getItems().addAll(
        new humanPlayer(1),
        RandomPlayer.getInstance(1),
                
        MonteCarloAlgorithmPlayer.getInstance(1)
    );
    playerChoiceBox.getSelectionModel().selectFirst();
    playerTwoChoiceBox.getItems().addAll(
        new humanPlayer(2),
        RandomPlayer.getInstance(2),
               
        MonteCarloAlgorithmPlayer.getInstance(2)
    );
    playerTwoChoiceBox.getSelectionModel().selectFirst();

    enableDisableControls(false);
  }

  @FXML
  private void connectkcanvasHandler(MouseEvent mouseEvent) {
    double xAxis = mouseEvent.getX();
    double yAxis = mouseEvent.getY();

    int column = (int) Math.floor((xAxis - xOffset) / radius);
    int row = (int) Math.floor((yAxis - yOffset) / radius);

    outsideGameMove.offer(GameMove.getInstance(row, column));
  }

  @FXML
  private void startButtonHandler(ActionEvent actionEvent) {
    try {
      connectkParameters parameters = getconnectkParametersFromView();
      computeDrawingconnectkParameters(parameters);
      startGame(parameters);
    } catch (IllegalStateException error) {
      log.println("Game failed to start: Invalid parameters.");
    }
  }

  @FXML
  private void stopButtonHandler(ActionEvent actionEvent) {
    killGameLoop();
  }

  @FXML
  private void evaluateHeuristicButtonHandler(ActionEvent actionEvent) {
    log.println("Heuristic: " + new Heuristic().evaluate(game.getState()));
  }

  @FXML
  private void clearLogButtonHandler(ActionEvent actionEvent) {
    logTextArea.setText("");
  }

  // Pack parameter from GUI and check ranges.
  private connectkParameters getconnectkParametersFromView() {
    int rows;
    int columns;
    int win;
    boolean gravity;
    long timeLimit;
    try {
      rows = Integer.parseInt(rowsTextField.getText());
      columns = Integer.parseInt(columnsTextField.getText());
      win = Integer.parseInt(winTextField.getText());
      gravity = gravityCheckBox.isSelected();
      timeLimit = Long.parseLong(timeLimitTextField.getText());
    } catch (Exception parseProbleme) {
      timeLimit = 3500;
      rows = 6;
      columns = 7;
      win = 4;
      gravity = true;
    }

    boolean validRows = connectkParameters.ROWS_MIN <= rows && rows <= connectkParameters.ROWS_MAX;
    boolean validColumns = connectkParameters.COLUMNS_MIN <= columns 
        && columns <= connectkParameters.COLUMNS_MAX;
    boolean validWin = connectkParameters.WIN_LENGTH_MIN <= win 
        && win <= connectkParameters.WIN_LENGTH_MAX;

    if (!validRows || !validColumns || !validWin) {
      throw new IllegalStateException("Invalid parameters.");
    }
    return new connectkParameters(rows, columns, win, gravity, timeLimit);
  }

  // Toggle controls
  private void enableDisableControls(boolean isGameRunning) {
    gameCanvas.setDisable(!isGameRunning);

    rowsTextField.setDisable(isGameRunning);
    columnsTextField.setDisable(isGameRunning);
    winTextField.setDisable(isGameRunning);
    gravityCheckBox.setDisable(isGameRunning);
    timeLimitTextField.setDisable(isGameRunning);

    playerChoiceBox.setDisable(isGameRunning);
    playerTwoChoiceBox.setDisable(isGameRunning);

    startButton.setDisable(isGameRunning);
    stopButton.setDisable(!isGameRunning);
    evaluateHeuristicButton.setDisable(!isGameRunning);
  }

  // START: game loop methods

  // Start a new game.
  private void startGame(connectkParameters parameters) {
    assert (parameters != null);

    game = gameInstance(parameters);
    gameLoopThread = new Thread(this::gameLoop);
    gameLoopThread.start();
  }

  // Create a game object.
  private Game gameInstance(connectkParameters parameters) {
    assert (parameters != null);

    Player playerOne = playerChoiceBox.getValue();
    Player playerTwo = playerTwoChoiceBox.getValue();
    State state = new State(parameters);
    return new Game(playerOne, playerTwo, state);
  }

  // The game loop.
  private void gameLoop() {
    enableDisableControls(true);
    Thread thisThread = Thread.currentThread();
    Platform.runLater(this::drawBoard);
    while (!game.isDone() && thisThread == gameLoopThread) {
      game.update();
      Platform.runLater(this::drawBoard);
      String message = game.getMessage();
      if (message != null) {
        log.println(message);
      }
    }
    if (game.isDone()) {
      log.println("Player " + game.getWinner() + " won!");
    } else {
      log.println("The game loop has ended without the game ending...");
    }
    enableDisableControls(false);
  }

  // Kill the thread running the game loop.
  private void killGameLoop() {
    assert (gameLoopThread != null);

    Thread temp = gameLoopThread;
    gameLoopThread = null;
    temp.interrupt();
  }
  // END: game loop methods

  // START: drawing  methods

  // Compute geometry parameters for drawing board
  private void computeDrawingconnectkParameters(connectkParameters parameters) {
    assert (parameters != null);

    double canvasSize = gameCanvas.getWidth();
    int rows = parameters.getRows();
    int columns = parameters.getColumns();

    radius = canvasSize / Math.max(rows, columns);
    xOffset = (canvasSize - columns * radius) / 2;
    yOffset = (canvasSize - rows * radius) / 2;
  }

  // Draw the board to canvas.
  private void drawBoard() {
    Color color[] = {Color.TRANSPARENT, Color.RED, Color.BLUE};
    clearCanvas();
    State state = game.getState();
    state.forEach(m -> drawCircle(radius * m.getColumn() + xOffset,
        radius * m.getRow() + yOffset, radius, color[state.getPlayerAt(m)]));
  }

  // Clear canvas to white
  private void clearCanvas() {
    GraphicsContext gc = gameCanvas.getGraphicsContext2D();
    gc.clearRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());
  }

  // Draw a circle on the canvas
  private void drawCircle(double x, double y, double r, Color c) {
    GraphicsContext gc = gameCanvas.getGraphicsContext2D();
    gc.setFill(c);
    gc.setStroke(Color.BLACK);
    gc.fillOval(x, y, r, r);
    gc.strokeOval(x, y, r, r);
  }
  // END: drawing methods
    
  // This class implements the Player interface for human player 
  // that are using the mouse to select moves.
  private class humanPlayer implements Player {
    private int id;

    humanPlayer(int id) {
      this.id = id;
    }

    @Override
    public int getId() {
      return id;
    }

    @Override
    public void calculateMove(State state) {
    }

    @Override
    public GameMove getMove() {
      message = "Pick a move!";
      outsideGameMove.clear();
      GameMove move;
      try {
        move = outsideGameMove.take();
      } catch (InterruptedException error) {
        move = null;
      }
      return move;
    }

    @Override
    public String toString() {
      return "Presenter Player (Human)";
    }

    @Override
    public String getMessage() {
      return null;
    }
  }
}


