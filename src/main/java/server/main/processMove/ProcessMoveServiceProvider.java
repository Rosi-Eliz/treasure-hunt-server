package server.main.processMove;

import MessagesBase.EMove;

public interface ProcessMoveServiceProvider {
    void processMove(String gameID, String playerID, EMove move);
}
