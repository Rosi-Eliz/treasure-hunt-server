package server.main.processHalfMap;

import MessagesBase.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import server.exceptions.GenericException;

@RestController
@RequestMapping("/games")
public class ProcessHalfMapController {
    private final ProcessHalfMapServiceProvider processHalfMapServiceProvider;
    private static Logger LOGGER = LoggerFactory.getLogger(ProcessHalfMapController.class);
    @Autowired
    public ProcessHalfMapController(ProcessHalfMapServiceProvider processHalfMapServiceProvider) {
        this.processHalfMapServiceProvider = processHalfMapServiceProvider;
    }

    @RequestMapping(value = "/{gameID}/halfmaps",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_XML_VALUE,
            produces = MediaType.APPLICATION_XML_VALUE)
    public @ResponseBody
    ResponseEnvelope<ERequestState> sendHalfMap(
            @Validated @PathVariable UniqueGameIdentifier gameID,
            @Validated @RequestBody HalfMap halfMap) {
        boolean isMapValid;
        ResponseEnvelope<ERequestState> halfMapResponse;
        try{
            isMapValid = processHalfMapServiceProvider.processHalfMap(gameID.getUniqueGameID(), halfMap);
        } catch (GenericException e) {
            return new ResponseEnvelope<>(e);
        }
        if(isMapValid) {
            LOGGER.info("HalfMap was successfully processed");
            halfMapResponse = new ResponseEnvelope<>(ERequestState.Okay);
        } else {
            halfMapResponse = new ResponseEnvelope<>("TransferHalfMapException", "Player sent an incorrect halfmap");
        }
        return halfMapResponse;
    }
}
