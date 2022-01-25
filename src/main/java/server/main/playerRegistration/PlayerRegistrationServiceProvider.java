package server.main.playerRegistration;

import MessagesBase.PlayerRegistration;
import MessagesBase.UniqueGameIdentifier;
import MessagesBase.UniquePlayerIdentifier;

public interface PlayerRegistrationServiceProvider {
    UniquePlayerIdentifier registerPlayer(UniqueGameIdentifier gameID, PlayerRegistration playerRegistration);
}
