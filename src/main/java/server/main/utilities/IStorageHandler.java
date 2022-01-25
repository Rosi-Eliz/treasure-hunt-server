package server.main.utilities;

import MessagesBase.HalfMap;
import MessagesBase.PlayerRegistration;
import server.main.models.GameRecord;
import java.util.Set;

public interface IStorageHandler {
     GameRecord createGame(Boolean enableDebugMode, Boolean enableDummyCompetition);
     void removeGame(String gameID);
     GameRecord getGameRecord(String gameID);
     Set<GameRecord> getAllGameRecords();
     void storePlayerRegistation(String playerID, PlayerRegistration playerRegistration);
     void removePlayerRegistation(String playerID);
     void storeHalfMap(String gameID, String playerID, HalfMap map);
}
