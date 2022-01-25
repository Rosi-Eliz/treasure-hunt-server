package server.main.gameCreation;

import server.main.models.GameRecord;

public interface GameCreationDataAccessServiceProvider {
    GameRecord createGame(Boolean enableDebugMode, Boolean enableDummyCompetition);
    void removeOldestGame();
}
