package server.main.playerRegistration;

import MessagesBase.PlayerRegistration;
import MessagesBase.UniquePlayerIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import server.exceptions.PlayerRegistrationException;
import server.main.utilities.IStorageHandler;

import java.util.UUID;

@Repository
public class PlayerRegistrationDataAccessService implements PlayerRegistrationDataAccessServiceProvider {
    IStorageHandler storageHandler;
    private static Logger LOGGER = LoggerFactory.getLogger(PlayerRegistrationDataAccessService.class);

    @Autowired
    public PlayerRegistrationDataAccessService(IStorageHandler storageHandler) {
        this.storageHandler = storageHandler;
    }

    @Override
    public UniquePlayerIdentifier registerPlayer(String gameId, PlayerRegistration playerRegistration) {
        UniquePlayerIdentifier identifier = new UniquePlayerIdentifier(UUID.randomUUID().toString());
        var game = storageHandler.getGameRecord(gameId);
        if(game == null) {
            LOGGER.error("Trying to register player for non existing game");
            throw new PlayerRegistrationException("Game does not exist");
        }
        storageHandler.storePlayerRegistation(identifier.getUniquePlayerID(), playerRegistration);

        game.setPlayerID(identifier.getUniquePlayerID(), playerRegistration);
        return identifier;

    }
}
