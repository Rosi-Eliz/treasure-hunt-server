package server.main.gameCreation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import server.main.models.GameRecord;
import server.main.utilities.IStorageHandler;
import java.util.Comparator;
import java.util.Date;
import java.util.stream.Collectors;

@Repository
public class GameCreationDataAccessService implements GameCreationDataAccessServiceProvider {
    private final IStorageHandler storageHandler;
    private final static int MAX_SUPPORTED_GAMES = 999;
    @Autowired
    public GameCreationDataAccessService(IStorageHandler storageHandler) {
        this.storageHandler = storageHandler;
    }

    @Override
    public GameRecord createGame(Boolean enableDebugMode, Boolean enableDummyCompetition) {
        if(storageHandler.getAllGameRecords().size() >= MAX_SUPPORTED_GAMES) {
            removeOldestGame();
        }
        return storageHandler.createGame(enableDebugMode, enableDummyCompetition);
    }

    @Override
    public void removeOldestGame() {
        storageHandler.getAllGameRecords().stream()
                .min(Comparator.comparing(GameRecord::getCreationDate))
                .ifPresent(oldestGame -> storageHandler.removeGame(oldestGame.getId()));
    }
}
