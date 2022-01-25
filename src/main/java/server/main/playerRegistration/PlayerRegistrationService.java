package server.main.playerRegistration;

import MessagesBase.PlayerRegistration;
import MessagesBase.UniqueGameIdentifier;
import MessagesBase.UniquePlayerIdentifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlayerRegistrationService implements PlayerRegistrationServiceProvider {
    private final PlayerRegistrationDataAccessServiceProvider playerRegistrationDataAccessService;

    @Autowired
    public PlayerRegistrationService(PlayerRegistrationDataAccessServiceProvider playerRegistrationDataAccessService) {
        this.playerRegistrationDataAccessService = playerRegistrationDataAccessService;
    }

    @Override
    public UniquePlayerIdentifier registerPlayer(UniqueGameIdentifier gameID, PlayerRegistration playerRegistration) {
        return playerRegistrationDataAccessService.registerPlayer(gameID.getUniqueGameID(), playerRegistration);
    }
}
