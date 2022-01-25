package server.main.models;

import MessagesBase.EMove;
import MessagesBase.HalfMap;
import MessagesBase.PlayerRegistration;
import MessagesGameState.EFortState;
import MessagesGameState.EPlayerGameState;
import MessagesGameState.FullMap;
import server.exceptions.ProcessMoveException;
import server.main.processMove.MoveProcessor;
import server.main.utilities.GameMap;
import server.main.utilities.MapConverter;

import java.util.*;

import static MessagesGameState.EPlayerPositionState.EnemyPlayerPosition;
import static MessagesGameState.EPlayerPositionState.MyPlayerPosition;

public class GameRecord {
    private enum GameState {
        Won,
        Lost,
        InProgress;

        private GameState() {
        }
    }
    private final static int MAX_ACTION_DURATION = 3 * 1000;

    private String id;
    private Date creationDate;
    private String player1ID;
    private String player2ID;
    private Boolean enableDebugMode;
    private Boolean enableDummyCompetition;
    private HalfMap player1Map;
    private HalfMap player2Map;
    private FullMap map;
    private Boolean firstPlayerTurn;
    private Boolean player1HasPickedUpTreasure = false;
    private Boolean player2HasPickedUpTreasure = false;
    private PlayerRegistration player1Registration;
    private PlayerRegistration player2Registration;
    private MapComposer mapComposer;
    private GameMap player1FullMap;
    private GameMap player2FullMap;
    private Date player1LastActionDate;
    private Date player2LastActionDate;
    private Boolean player1HasViolatedRule = false;
    private Boolean player2HasViolatedRule = false;
    private String player1GameStateID;
    private String player2GameStateID;
    private MoveProcessor moveProcessor;

    public GameRecord(String id, Date creationDate, Boolean enableDebugMode, Boolean enableDummyCompetition) {
        this.id = id;
        this.creationDate = creationDate;
        player1ID = null;
        player2ID = null;
        firstPlayerTurn = new Random().nextBoolean();
        this.enableDebugMode = enableDebugMode;
        this.enableDummyCompetition = enableDummyCompetition;
        player1GameStateID = UUID.randomUUID().toString();
        player2GameStateID = UUID.randomUUID().toString();
    }

    public void setPlayerID(String playerID, PlayerRegistration playerRegistration) {
        if(player1ID == null) {
            player1ID = playerID;
            player1Registration = playerRegistration;
        } else if(player2ID == null) {
            player2ID = playerID;
            player2Registration = playerRegistration;
        } else {
            throw new IllegalArgumentException("GameRecord already has 2 players");
        }
    }

    public String getId() {
        return id;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public String getPlayer1ID() {
        return player1ID;
    }

    public String getPlayer2ID() {
        return player2ID;
    }

    public void switchTurn(){
        firstPlayerTurn = !firstPlayerTurn;
    }

    public Boolean player1DidSetMap(){
        return player1Map != null;
    }

    public Boolean player2DidSetMap(){
        return player2Map != null;
    }

    private GameState getGameProgressState(String playerID) {
        Boolean player1HasWon = player1HasPickedUpTreasure &&
                map.getMapNodes().stream()
                        .filter(node -> node.getPlayerPositionState() == MyPlayerPosition)
                        .anyMatch(node -> node.getFortState() == EFortState.EnemyFortPresent);

        Boolean player2HasWon = player2HasPickedUpTreasure &&
                map.getMapNodes().stream()
                        .filter(node -> node.getPlayerPositionState() == EnemyPlayerPosition)
                        .anyMatch(node -> node.getFortState() == EFortState.MyFortPresent);

        if(player1HasViolatedRule) {
            player2HasWon = true;
        } else if (player2HasViolatedRule) {
            player1HasWon = true;
        }

        if(this.player1ID != null && this.player1ID.equals(playerID) && player1HasWon) {
            return GameState.Won;
        } else if(this.player1ID != null && this.player1ID.equals(playerID) && player2HasWon) {
            return GameState.Lost;
        } else if(this.player2ID != null && this.player2ID.equals(playerID) && player2HasWon) {
            return GameState.Won;
        } else if(this.player2ID != null && this.player2ID.equals(playerID) && player1HasWon) {
            return GameState.Lost;
        } else {
            return GameState.InProgress;
        }
    }

    public EPlayerGameState getPlayerState(String playerID) {
        if(player1ID == null || player2ID == null) {
            return EPlayerGameState.MustWait;
        }
         if(moveProcessor != null && moveProcessor.hasPlayerWon(playerID)) {
            return EPlayerGameState.Won;
         } else if(moveProcessor != null && moveProcessor.hasPlayerLost(playerID)) {
            return EPlayerGameState.Lost;
         }

        var gameState = getGameProgressState(playerID);
        switch(gameState) {
            case Won:
                return EPlayerGameState.Won;
            case Lost:
                return EPlayerGameState.Lost;
            case InProgress:
                break;
        }
        if(player1ID.equals(playerID)) {
            return firstPlayerTurn ? EPlayerGameState.MustAct : EPlayerGameState.MustWait;
        } else if (player2ID.equals(playerID)) {
            return !firstPlayerTurn ? EPlayerGameState.MustAct : EPlayerGameState.MustWait;
        } else {
            throw new IllegalArgumentException("Wrong player ID!");
        }
    }

    public Boolean hasPlayerCollectedTreasure(String playerID) {
        if(moveProcessor == null) {
            return false;
        }
        return moveProcessor.hasPlayerPickedUpTreasure(playerID);
    }

    public PlayerRegistration playerRegistrationFor(String playerID) {
        if(player1ID.equals(playerID)) {
            return player1Registration;
        } else if(player2ID.equals(playerID)) {
            return player2Registration;
        } else {
            throw new IllegalArgumentException("Wrong player ID!");
        }
    }

    public List<String> getPlayerIDs()
    {
        ArrayList<String> ids = new ArrayList<>();
        if(player1ID != null)
            ids.add(player1ID);
        if(player2ID != null)
            ids.add(player2ID);
        return ids;
    }

    public FullMap getMap() {
        return map;
    }

    public void setMapForPlayer(String playerID, HalfMap map) {
        if (player1ID.equals(playerID)) {
            player1Map = map;
            player1LastActionDate = new Date();
        } else if(player2ID.equals(playerID)) {
            player2Map = map;
            player2LastActionDate = new Date();
        } else {
            throw new IllegalArgumentException("Wrong player ID!");
        }
        if(player1Map != null && player2Map != null)
        {
            mapComposer = new MapComposer(MapConverter.generateHalfMap(player1Map), MapConverter.generateHalfMap(player2Map), player1ID, player2ID);
            moveProcessor = new MoveProcessor(mapComposer.getInitialFullMap(), mapComposer.playersCastles(), mapComposer.playersTreasures());
            player1FullMap = mapComposer.generateFullMapForPlayer1();
            player2FullMap = mapComposer.generateFullMapForPlayer2();
        }
    }

    public FullMap getFullMapForPlayer(String playerID)
    {
        if(player1ID.equals(playerID))
            if(player1FullMap != null) {
                return MapConverter.generateFullMap(moveProcessor.generateGameMapForPlayerID(playerID), playerID, false);
            } else {
                return null;
            }
        else if(player2ID.equals(playerID))
            if(player2FullMap != null) {
                return MapConverter.generateFullMap(moveProcessor.generateGameMapForPlayerID(playerID), playerID, false);
            } else {
                return null;
            }
        else
            throw new IllegalArgumentException("Wrong player ID!");
    }

    public void setPlayerHasViolatedRule(String playerID, Boolean playerHasViolatedRule) {
        if(player1ID.equals(playerID)) {
            player1HasViolatedRule = playerHasViolatedRule;
        } else if(player2ID.equals(playerID)) {
            player2HasViolatedRule = playerHasViolatedRule;
        } else {
            throw new IllegalArgumentException("Wrong player ID!");
        }
    }

    public void processMoveForPlayer(String playerID, EMove move) {
        if(player1LastActionDate == null || player2LastActionDate == null) {
            throw new ProcessMoveException("Player has not performed an action yet!");
        }
        if(player1ID.equals(playerID)) {
            if(new Date().getTime() - player1LastActionDate.getTime() > MAX_ACTION_DURATION) {
                player1HasViolatedRule = true;
            } else {
                player1LastActionDate = new Date();
            }
        } else if (player2ID.equals(playerID)) {
            if(new Date().getTime() - player2LastActionDate.getTime() > MAX_ACTION_DURATION) {
                player2HasViolatedRule = true;
            } else {
                player2LastActionDate = new Date();
            }
        } else {
            throw new IllegalArgumentException("Wrong player ID!");
        }
        moveProcessor.processMoveForPlayerID(playerID, move);
        switchTurn();
    }

    public String getGameStateIDForPlayer(String playerID)
    {
        if(firstPlayerTurn)
            return player1GameStateID;
        else
            return player2GameStateID;
    }

}
