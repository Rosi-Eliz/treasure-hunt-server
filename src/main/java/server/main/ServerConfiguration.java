package server.main;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import server.main.gameCreation.GameCreationDataAccessService;
import server.main.gameCreation.GameCreationDataAccessServiceProvider;
import server.main.gameCreation.GameCreationService;
import server.main.gameCreation.GameCreationServiceProvider;
import server.main.gameStatus.GameStatusDataAccessService;
import server.main.gameStatus.GameStatusDataAccessServiceProvider;
import server.main.gameStatus.GameStatusService;
import server.main.gameStatus.GameStatusServiceProvider;
import server.main.playerRegistration.PlayerRegistrationDataAccessService;
import server.main.playerRegistration.PlayerRegistrationDataAccessServiceProvider;
import server.main.playerRegistration.PlayerRegistrationService;
import server.main.playerRegistration.PlayerRegistrationServiceProvider;
import server.main.processHalfMap.ProcessHalfMapDataAccessService;
import server.main.processHalfMap.ProcessHalfMapDataAccessServiceProvider;
import server.main.processHalfMap.ProcessHalfMapService;
import server.main.processHalfMap.ProcessHalfMapServiceProvider;
import server.main.processMove.ProcessMoveDataAccessService;
import server.main.processMove.ProcessMoveDataAccessServiceProvider;
import server.main.processMove.ProcessMoveService;
import server.main.processMove.ProcessMoveServiceProvider;
import server.main.utilities.IStorageHandler;
import server.main.utilities.StorageHandler;

@Configuration
public class ServerConfiguration {
    private final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ServerConfiguration.class);
    @Bean(name = "IStorageHandler")
    public IStorageHandler getStorageHandler() {
        return new StorageHandler();
    }

    @Bean(name = "GameCreationDataAccessServiceProvider")
    public GameCreationDataAccessServiceProvider getGameCreationDataAccessServiceProvider() {
        IStorageHandler storageHandler = (IStorageHandler) context.getBean("IStorageHandler");
        return new GameCreationDataAccessService(storageHandler);
    }

    @Bean(name = "GameCreationServiceProvider")
    public GameCreationServiceProvider getGameCreationServiceProvider() {
        GameCreationDataAccessServiceProvider service = (GameCreationDataAccessServiceProvider) context.getBean("PlayerRegistrationDataAccessServiceProvider");
        return new GameCreationService(service);
    }

    @Bean(name = "PlayerRegistrationDataAccessServiceProvider")
    public PlayerRegistrationDataAccessServiceProvider getPlayerRegistrationDataAccessServiceProvider() {
        IStorageHandler storageHandler = (IStorageHandler) context.getBean("IStorageHandler");
        return new PlayerRegistrationDataAccessService(storageHandler);
    }

    @Bean(name = "PlayerRegistrationServiceProvider")
    public PlayerRegistrationServiceProvider getPlayerRegistrationServiceProvider() {
        PlayerRegistrationDataAccessServiceProvider service = (PlayerRegistrationDataAccessServiceProvider) context.getBean("PlayerRegistrationDataAccessServiceProvider");
        return new PlayerRegistrationService(service);
    }

    @Bean(name = "GameStatusDataAccessServiceProvider")
    public GameStatusDataAccessServiceProvider getGameStatusDataAccessServiceProvider() {
        IStorageHandler storageHandler = (IStorageHandler) context.getBean("IStorageHandler");
        return new GameStatusDataAccessService(storageHandler);
    }

    @Bean(name = "GameStatusServiceProvider")
    public GameStatusServiceProvider getGameStatusServiceProvider() {
        GameStatusDataAccessServiceProvider service = (GameStatusDataAccessServiceProvider) context.getBean("GameStatusDataAccessServiceProvider");
        return new GameStatusService(service);
    }

    @Bean(name = "ProcessHalfMapDataAccessServiceProvider")
    public ProcessHalfMapDataAccessServiceProvider getProcessHalfmapDataAccessServiceProvider() {
        IStorageHandler storageHandler = (IStorageHandler) context.getBean("IStorageHandler");
        return new ProcessHalfMapDataAccessService(storageHandler);
    }

    @Bean(name = "ProcessHalfMapServiceProvider")
    public ProcessHalfMapServiceProvider getProcessHalfmapServiceProvider() {
        ProcessHalfMapDataAccessServiceProvider service = (ProcessHalfMapDataAccessServiceProvider) context.getBean("ProcessMoveDataAccessServiceProvider");
        return new ProcessHalfMapService(service);
    }

    @Bean(name = "ProcessMoveDataAccessServiceProvider")
    public ProcessMoveDataAccessServiceProvider getProcessMoveDataAccessServiceProvider() {
        IStorageHandler storageHandler = (IStorageHandler) context.getBean("IStorageHandler");
        return new ProcessMoveDataAccessService(storageHandler);
    }

    @Bean(name = "ProcessMoveServiceProvider")
    public ProcessMoveServiceProvider getProcessMoveServiceProvider() {
        ProcessMoveDataAccessServiceProvider service = (ProcessMoveDataAccessServiceProvider) context.getBean("ProcessMoveDataAccessServiceProvider");
        return new ProcessMoveService(service);
    }

}
