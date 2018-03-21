package game;

public interface Player {

  int getId();

  void calculateMove(State state);
  
  GameMove getMove();

  String getMessage();
}