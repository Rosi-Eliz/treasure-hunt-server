package server.main.processMove;

import MessagesBase.EMove;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.exceptions.ProcessMoveException;
import server.main.models.GameRecord;

@Service
public class ProcessMoveService implements ProcessMoveServiceProvider{
    private final ProcessMoveDataAccessServiceProvider processMoveDataAccessServiceProvider;

    @Autowired
    public ProcessMoveService(ProcessMoveDataAccessServiceProvider processMoveDataAccessServiceProvider) {
        this.processMoveDataAccessServiceProvider = processMoveDataAccessServiceProvider;
    }

    @Override
    public void processMove(String gameID, String playerID, EMove move) {
        GameRecord gameRecord = processMoveDataAccessServiceProvider.getGameRecord(gameID);
        if(gameRecord == null)
        {
            throw new ProcessMoveException("Game does not exist");
        }
        gameRecord.processMoveForPlayer(playerID, move);
    }
}
