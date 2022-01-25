package server.main.processMove;

import server.main.models.GameRecord;

public interface ProcessMoveDataAccessServiceProvider {
    GameRecord getGameRecord(String gameID);
}
