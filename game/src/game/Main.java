package game;

import javafx.application.Application;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {

  /**
   * Starts the Game.
   */
  @Override
  public void start(Stage stage) throws Exception {
    stage.setTitle("Connect K");
    stage.setResizable(false);
    stage.setOnCloseRequest(this::shutdown);
    FXMLLoader loader = new FXMLLoader(getClass().getResource("connectk.fxml"));
    Parent root = loader.load();
    stage.setScene(new Scene(root));
    stage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }


  /**
   * Called when the GUI systems goes down.
   * @param error Any GUI error
   */
  private void shutdown(Event error) {
    System.exit(0);
  }
}

