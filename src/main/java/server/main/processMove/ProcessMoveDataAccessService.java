package server.main.processMove;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import server.main.models.GameRecord;
import server.main.utilities.IStorageHandler;

@Repository
public class ProcessMoveDataAccessService implements ProcessMoveDataAccessServiceProvider {
    private final IStorageHandler storageHandler;

    @Autowired
    public ProcessMoveDataAccessService(IStorageHandler storageHandler) {
        this.storageHandler = storageHandler;
    }

    @Override
    public GameRecord getGameRecord(String gameID) {
        return storageHandler.getGameRecord(gameID);
    }
}
