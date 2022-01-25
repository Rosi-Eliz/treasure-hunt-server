package server.main.gameStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import server.main.models.GameRecord;
import server.main.utilities.IStorageHandler;

import java.util.Date;

@Repository
public class GameStatusDataAccessService implements GameStatusDataAccessServiceProvider{
    private final IStorageHandler storageHandler;
    private final static int MAX_GAME_DURATION = 10 * 60 * 1000;

    @Autowired
    public GameStatusDataAccessService(IStorageHandler storageHandler) {
        this.storageHandler = storageHandler;
    }

    @Override
    public GameRecord getGameRecord(String gameID) {
        var gameRecord = storageHandler.getGameRecord(gameID);
        Date currentTime = new Date();
        if(gameRecord != null && gameRecord.getCreationDate().before(new Date(currentTime.getTime() - MAX_GAME_DURATION))) {
            storageHandler.removeGame(gameID);
            return null;
        }
        return gameRecord;
    }
}
