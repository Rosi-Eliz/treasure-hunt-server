package server.main.gameStatus;
import MessagesGameState.GameState;

public interface GameStatusServiceProvider {
    GameState getGameStatus(String gameID, String playerID);
}
