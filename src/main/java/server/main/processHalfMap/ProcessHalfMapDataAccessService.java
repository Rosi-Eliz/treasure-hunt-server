package server.main.processHalfMap;

import MessagesBase.HalfMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import server.exceptions.TransferHalfMapException;
import server.main.models.GameRecord;
import server.main.utilities.IStorageHandler;

@Repository
public class ProcessHalfMapDataAccessService implements ProcessHalfMapDataAccessServiceProvider{

    private final IStorageHandler storageHandler;
    private static Logger LOGGER = LoggerFactory.getLogger(ProcessHalfMapDataAccessService.class);

    @Autowired
    public ProcessHalfMapDataAccessService(IStorageHandler storageHandler) {
        this.storageHandler = storageHandler;
    }

    @Override
    public boolean validateHalfMap(String gameID, HalfMap map) {
        var game = storageHandler.getGameRecord(gameID);
        if(game == null) {
            LOGGER.error("Trying to validate map for non existing game");
            throw new TransferHalfMapException("Game does not exist");
        } else if(map.getUniquePlayerID() == null || !game.getPlayerIDs().contains(map.getUniquePlayerID())) {
            LOGGER.error("Trying to validate map for non existing player");
            throw new TransferHalfMapException("Player does not exist");
        }

        if(MapValidator.isHalfMapValid(map))
        {
            storageHandler.storeHalfMap(gameID, map.getUniquePlayerID(), map);
            GameRecord gameRecord = storageHandler.getGameRecord(gameID);
            gameRecord.switchTurn();
            LOGGER.info("Half map for player {} is validated and set", map.getUniquePlayerID());
            return true;
        }
        LOGGER.error("Half map for player {} is not valid", map.getUniquePlayerID());
        GameRecord gameRecord = storageHandler.getGameRecord(gameID);
        gameRecord.setPlayerHasViolatedRule(map.getUniquePlayerID(), true);
        return false;
    }
}
