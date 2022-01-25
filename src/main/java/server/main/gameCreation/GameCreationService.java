package server.main.gameCreation;

import MessagesBase.UniqueGameIdentifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.main.models.GameRecord;

@Service
public class GameCreationService implements GameCreationServiceProvider{
    private GameCreationDataAccessServiceProvider gameCreationDataAccessServiceProvider;

    @Autowired
    public GameCreationService(GameCreationDataAccessServiceProvider gameCreationDataAccessServiceProvider) {
        this.gameCreationDataAccessServiceProvider = gameCreationDataAccessServiceProvider;
    }

    @Override
    public UniqueGameIdentifier createGame(Boolean enableDebugMode, Boolean enableDummyCompetition) {
        GameRecord gameRecord = gameCreationDataAccessServiceProvider.createGame(enableDebugMode, enableDummyCompetition);
        return new UniqueGameIdentifier(gameRecord.getId());
    }
}
