package server.main.playerRegistration;

import MessagesBase.PlayerRegistration;
import MessagesBase.UniquePlayerIdentifier;

public interface PlayerRegistrationDataAccessServiceProvider {
    UniquePlayerIdentifier registerPlayer(String gameId, PlayerRegistration playerRegistration);
}
