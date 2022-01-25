package server.main.playerRegistration;


import MessagesBase.PlayerRegistration;
import MessagesBase.ResponseEnvelope;
import MessagesBase.UniqueGameIdentifier;
import MessagesBase.UniquePlayerIdentifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import server.exceptions.PlayerRegistrationException;

@RestController
@RequestMapping("/games")
public class PlayerRegistrationController {
    private final PlayerRegistrationServiceProvider playerRegistrationService;

    @Autowired
    public PlayerRegistrationController(PlayerRegistrationServiceProvider playerRegistrationService) {
        this.playerRegistrationService = playerRegistrationService;
    }

    @RequestMapping(value = "/{gameID}/players",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_XML_VALUE,
            produces = MediaType.APPLICATION_XML_VALUE)
    public @ResponseBody
    ResponseEnvelope<UniquePlayerIdentifier> registerPlayer(
            @Validated @PathVariable UniqueGameIdentifier gameID,
            @Validated @RequestBody PlayerRegistration playerRegistration) {
        UniquePlayerIdentifier newPlayerID;
        try {
            newPlayerID = playerRegistrationService
                    .registerPlayer(gameID, playerRegistration);
            } catch (PlayerRegistrationException e) {
            return new ResponseEnvelope<>(e);
        }
        return new ResponseEnvelope<>(newPlayerID);
    }
}
