package server.main.processMove;

import MessagesBase.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/games")
public class ProcessMoveController {
    private final ProcessMoveService processMoveService;

    @Autowired
    public ProcessMoveController(ProcessMoveService processMoveService) {
        this.processMoveService = processMoveService;
    }

    @RequestMapping(value = "/{gameID}/moves",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_XML_VALUE,
            produces = MediaType.APPLICATION_XML_VALUE)
    public @ResponseBody
    ResponseEnvelope<ERequestState> processMove(
            @Validated @PathVariable UniqueGameIdentifier gameID,
            @Validated @RequestBody PlayerMove move) {

        try {
            processMoveService.processMove(gameID.getUniqueGameID(), move.getUniquePlayerID(), move.getMove());
        } catch (Exception e) {
            return new ResponseEnvelope<>(e);
        }
        return new ResponseEnvelope<>();
    }
}
