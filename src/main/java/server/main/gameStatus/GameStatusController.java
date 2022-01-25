package server.main.gameStatus;
import MessagesBase.ResponseEnvelope;
import MessagesBase.UniqueGameIdentifier;
import MessagesBase.UniquePlayerIdentifier;
import MessagesGameState.GameState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import server.exceptions.GenericException;

@RestController
@RequestMapping("/games")
public class GameStatusController {
    private final GameStatusServiceProvider gameStatusServiceProvider;

    @Autowired
    public GameStatusController(GameStatusServiceProvider gameStatusServiceProvider) {
        this.gameStatusServiceProvider = gameStatusServiceProvider;
    }

    @RequestMapping(value = "/{gameID}/states/{playerID}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_XML_VALUE)
    public @ResponseBody
    ResponseEnvelope<GameState> getGameState(
            @Validated @PathVariable UniqueGameIdentifier gameID,
            @Validated @PathVariable UniquePlayerIdentifier playerID) {
        GameState gameState;
        try {
            gameState = gameStatusServiceProvider.getGameStatus(gameID.getUniqueGameID(), playerID.getUniquePlayerID());
        } catch (GenericException e) {
            return new ResponseEnvelope<>(e);
        }
        return new ResponseEnvelope<>(gameState);
    }
}
