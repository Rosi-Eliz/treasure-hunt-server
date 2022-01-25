package server.main.gameStatus;

import MessagesBase.PlayerRegistration;
import MessagesBase.UniquePlayerIdentifier;
import MessagesGameState.FullMap;
import MessagesGameState.GameState;
import MessagesGameState.PlayerState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.exceptions.CheckGameStatusException;

import java.util.*;

@Service
public class GameStatusService implements GameStatusServiceProvider{
    private final GameStatusDataAccessServiceProvider gameStatusDataAccessServiceProvider;
    private static Logger LOGGER = LoggerFactory.getLogger(GameStatusService.class);

    @Autowired
    public GameStatusService(GameStatusDataAccessServiceProvider gameStatusDataAccessServiceProvider) {
        this.gameStatusDataAccessServiceProvider = gameStatusDataAccessServiceProvider;
    }

    @Override
    public GameState getGameStatus(String gameID, String playerID) {
        var gameRecord = gameStatusDataAccessServiceProvider.getGameRecord(gameID);
        if(gameRecord == null) {
            throw new CheckGameStatusException("Game not found");

        } else if(playerID== null || !gameRecord.getPlayerIDs().contains(playerID)) {
            LOGGER.error("Wrong player ID: {}", playerID);
            throw new CheckGameStatusException("Player " + playerID + " does not exist for game ID: " + gameID);
        }

        var playerIDs = gameRecord.getPlayerIDs();

        List<PlayerState> playerStates = new ArrayList<>();
        for(var currentPlayerID : playerIDs) {
            UniquePlayerIdentifier uniquePlayerIdentifier  = currentPlayerID.equals(playerID) ?
                    new UniquePlayerIdentifier(currentPlayerID) : new UniquePlayerIdentifier(UUID.randomUUID().toString());
            PlayerRegistration playerRegistration = gameRecord.playerRegistrationFor(currentPlayerID);
            PlayerState playerState = new PlayerState(playerRegistration.getStudentFirstName(),
                    playerRegistration.getStudentLastName(),
                    playerRegistration.getStudentID(),
                    gameRecord.getPlayerState(currentPlayerID),
                    uniquePlayerIdentifier,
                    gameRecord.hasPlayerCollectedTreasure(currentPlayerID));
            playerStates.add(playerState);
        }

        FullMap fullMap = gameRecord.getFullMapForPlayer(playerID);
        String gameStateID = gameRecord.getGameStateIDForPlayer(playerID);
        return new GameState(Optional.ofNullable(fullMap), playerStates, gameStateID);
    }
}
