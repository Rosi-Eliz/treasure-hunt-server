package server.main.processHalfMap;

import MessagesBase.HalfMap;

public interface ProcessHalfMapDataAccessServiceProvider {
    boolean validateHalfMap(String gameID, HalfMap map);
}
