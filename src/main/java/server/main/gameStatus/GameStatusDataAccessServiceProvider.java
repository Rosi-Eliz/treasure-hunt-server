package server.main.gameStatus;

import server.main.models.GameRecord;

public interface GameStatusDataAccessServiceProvider {
    GameRecord getGameRecord(String gameID);
}
