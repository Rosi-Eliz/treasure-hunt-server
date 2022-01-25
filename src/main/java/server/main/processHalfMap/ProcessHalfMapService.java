package server.main.processHalfMap;

import MessagesBase.HalfMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProcessHalfMapService implements ProcessHalfMapServiceProvider{

    private final ProcessHalfMapDataAccessServiceProvider processHalfMapDataAccessServiceProvider;

    @Autowired
    public ProcessHalfMapService(ProcessHalfMapDataAccessServiceProvider processHalfMapDataAccessServiceProvider) {
        this.processHalfMapDataAccessServiceProvider = processHalfMapDataAccessServiceProvider;
    }

    @Override
    public boolean processHalfMap(String gameID, HalfMap map) {
        return processHalfMapDataAccessServiceProvider.validateHalfMap(gameID, map);
    }
}
