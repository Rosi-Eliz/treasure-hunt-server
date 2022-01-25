package server.main.gameCreation;

import MessagesBase.UniqueGameIdentifier;
import org.springframework.stereotype.Component;

@Component
public interface GameCreationServiceProvider {
    UniqueGameIdentifier createGame(Boolean enableDebugMode, Boolean enableDummyCompetition);
}
