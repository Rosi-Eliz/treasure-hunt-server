package server.main.utilities;

import MessagesBase.HalfMap;
import MessagesBase.PlayerRegistration;
import org.springframework.stereotype.Component;
import server.exceptions.TransferHalfMapException;
import server.main.models.GameRecord;

import java.util.*;
@Component
public class StorageHandler implements IStorageHandler {
    private HashMap<String, GameRecord> gameRecordsMap = new HashMap <>();
    private HashMap<String, PlayerRegistration> playerRegistrationMap = new HashMap <>();

    @Override
    public GameRecord createGame(Boolean enableDebugMode, Boolean enableDummyCompetition) {
        String identifier;
        do{
            identifier = UUID.randomUUID().toString().replace("-","").substring(0,5);
        } while (gameRecordsMap.containsKey(identifier));

        Date currentDate = new Date();
        GameRecord gameRecord = new GameRecord(identifier, currentDate, enableDebugMode, enableDummyCompetition);
        gameRecordsMap.put(identifier, gameRecord);
        return gameRecord;
    }

    @Override
    public void removeGame(String gameID) {
        var game = gameRecordsMap.get(gameID);
        if(game != null) {
            gameRecordsMap.remove(gameID);
            removePlayerRegistation(game.getPlayer1ID());
            removePlayerRegistation(game.getPlayer2ID());
        }
    }

    public GameRecord getGameRecord(String gameID) {
        if(gameRecordsMap.containsKey(gameID)) {
            return gameRecordsMap.get(gameID);
        }
        return null;
    }

    @Override
    public Set<GameRecord> getAllGameRecords() {
        return new HashSet<>(gameRecordsMap.values());
    }

    @Override
    public void storePlayerRegistation(String playerID, PlayerRegistration playerRegistration) {
        playerRegistrationMap.put(playerID, playerRegistration);
    }

    @Override
    public void removePlayerRegistation(String playerID) {
        if(playerID != null) {
            playerRegistrationMap.remove(playerID);
        }
    }

    @Override
    public void storeHalfMap(String gameID, String playerID, HalfMap map) {
        var gameRecord = gameRecordsMap.get(gameID);
        if(gameRecord == null) {
            throw new TransferHalfMapException("Game with ID " + gameID + " does not exist");
        }
        if(gameRecord.getPlayer1ID() != null && gameRecord.getPlayer1ID().equals(playerID)) {
            if(!gameRecord.player1DidSetMap())
                gameRecord.setMapForPlayer(playerID, map);
            else
                throw new TransferHalfMapException("Map for player 1 already set!");
        } else if(gameRecord.getPlayer2ID() != null && gameRecord.getPlayer2ID().equals(playerID)) {
            if(!gameRecord.player2DidSetMap())
                gameRecord.setMapForPlayer(playerID, map);
            else
                throw new TransferHalfMapException("Map for player 2 already set!");
        } else {
            throw new TransferHalfMapException("Player with ID " + playerID + " does not exist");
        }
    }

    public List<PlayerRegistration> getPlayerRegistrationsForPlayerID(String gameID) {
        var gameRecord = gameRecordsMap.get(gameID);
        if(gameRecord == null) {
            throw new IllegalArgumentException("Game with ID " + gameID + " does not exist");
        }
        var playerRegistrations = new ArrayList<PlayerRegistration>();
        if(gameRecord.getPlayer1ID() != null) {
            var playerRegistration = playerRegistrationMap.get(gameRecord.getPlayer1ID());
            if(playerRegistration == null) {
                throw new IllegalArgumentException("Player with ID " + gameRecord.getPlayer1ID() + " does not exist");
            }
            playerRegistrations.add(playerRegistration);
        }
        if(gameRecord.getPlayer2ID() != null) {
            var playerRegistration = playerRegistrationMap.get(gameRecord.getPlayer2ID());
            if(playerRegistration == null) {
                throw new IllegalArgumentException("Player with ID " + gameRecord.getPlayer2ID() + " does not exist");
            }
            playerRegistrations.add(playerRegistration);
        }
        return playerRegistrations;
    }
}
