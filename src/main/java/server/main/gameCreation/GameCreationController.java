package server.main.gameCreation;

import MessagesBase.UniqueGameIdentifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import server.exceptions.GenericException;

@RestController
@RequestMapping("/games")
public class GameCreationController {
    private GameCreationServiceProvider gameCreationService;

    @Autowired
    public GameCreationController(GameCreationServiceProvider gameCreationService) {
        this.gameCreationService = gameCreationService;
    }

    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
    public @ResponseBody
    UniqueGameIdentifier newGame(
            @RequestParam(required = false, defaultValue = "false", value = "enableDebugMode") boolean enableDebugMode,
            @RequestParam(required = false, defaultValue = "false", value = "enableDummyCompetition") boolean enableDummyCompetition) {

        boolean showExceptionHandling = false;
        if (showExceptionHandling) {
            throw new GenericException("Name: Something", "Message: went totally wrong");
        }
        return gameCreationService.createGame(enableDebugMode, enableDummyCompetition);
    }
}