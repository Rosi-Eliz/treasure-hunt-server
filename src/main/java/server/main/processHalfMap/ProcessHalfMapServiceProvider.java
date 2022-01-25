package server.main.processHalfMap;

import MessagesBase.HalfMap;

public interface ProcessHalfMapServiceProvider {
    boolean processHalfMap(String gameID, HalfMap map);
}
