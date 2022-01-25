package server.main.processMove;

import MessagesBase.EMove;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.main.models.MapComposer;
import server.main.utilities.GameMap;
import server.main.utilities.Graph;

import java.util.*;
import java.util.stream.Collectors;

public class MoveProcessor {
    private GameMap map;
    private Graph graph;
    private HashMap<String, MapComposer.Location> playersCastles;
    private HashMap<String, MapComposer.Location> playersTreasures;
    private HashMap<String, MapComposer.Location> playersLocations = new HashMap<>();
    private HashMap<String, List<MapComposer.Location>> playersRevealedFields = new HashMap<>();
    private HashMap<String, PlayerProgress> playersProgress = new HashMap<>();
    private HashMap<String, Boolean> playersHasPickedTreasure = new HashMap<>();
    private HashMap<String, Boolean> playersHasViolatedRule = new HashMap<>();
    private int completedMoves = 0;
    private static Logger LOGGER = LoggerFactory.getLogger(MoveProcessor.class);

    private class PlayerProgress{
        private Queue<EMove> moves;

        public PlayerProgress(Queue<EMove> moves, Graph.Node destinationNode) {
            this.moves = moves;
        }
    }

    public MoveProcessor(GameMap map, HashMap<String,
                         MapComposer.Location> playersCastles,
                         HashMap<String, MapComposer.Location> playersTreasures)  {
        try{
            this.map = map.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            throw new RuntimeException("Cloning map failed");
        }
        var fields = this.map.getAllFields().stream().filter(field -> field.playerPositionState == GameMap.PlayerPositionState.MyPlayerPosition ||
                field.playerPositionState == GameMap.PlayerPositionState.EnemyPlayerPosition ||
                field.playerPositionState == GameMap.PlayerPositionState.BothPlayerPosition).collect(Collectors.toList());

        for(var field : fields) {
            field.playerPositionState = GameMap.PlayerPositionState.NoPlayerPresent;
            field.gameFortState = GameMap.GameFortState.NoOrUnknownFortState;
            field.gameTreasureState = GameMap.GameTreasureState.NoOrUnknownTreasureState;
        }

        this.playersCastles = playersCastles;
        for(String id : playersCastles.keySet()) {
            playersRevealedFields.put(id, new LinkedList<MapComposer.Location>(List.of(playersCastles.get(id))));
            playersLocations.put(id, playersCastles.get(id));
            playersHasPickedTreasure.put(id, false);
            playersHasViolatedRule.put(id, false);
        }
        this.playersTreasures = playersTreasures;
        this.graph = new Graph(this.map);

    }

    public boolean shouldMockEnemyLocation()
    {
        return completedMoves <= 4;
    }



    public GameMap generateGameMapForPlayerID(String playerID) {
        GameMap map = null;
        try {
            map = this.map.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            throw new RuntimeException("Error when cloning game map");
        }
        var playerCastle = playersCastles.get(playerID);
        var currentPlayerLocation = playersLocations.get(playerID);
        var currentPlayerField = map.getFieldForCoordinates(currentPlayerLocation.x, currentPlayerLocation.y);
        currentPlayerField.playerPositionState = GameMap.PlayerPositionState.MyPlayerPosition;
        var currentPlayerCastleField = map.getFieldForCoordinates(playerCastle.x, playerCastle.y);
        currentPlayerCastleField.gameFortState = GameMap.GameFortState.MyFortPresent;
        var treasureLocation = playersTreasures.get(playerID);
        var enemyPlayerID = playersLocations.keySet().stream().filter(id -> !id.equals(playerID)).findFirst().orElseThrow();
        var enemyCastleLocation = playersCastles.get(enemyPlayerID);
        if(shouldMockEnemyLocation()) {
            LOGGER.info("Mocking enemy location");
            var suitableFields = map.getAllFields().stream()
                    .filter(field -> (field.terrain == GameMap.Terrain.Grass
                    || field.terrain == GameMap.Terrain.Mountain)
                            && field.playerPositionState == GameMap.PlayerPositionState.NoPlayerPresent)
                    .collect(Collectors.toList());
            Random rand = new Random();
            var randomField = suitableFields.get(rand.nextInt(suitableFields.size()));
            randomField.playerPositionState = GameMap.PlayerPositionState.EnemyPlayerPosition;
        } else {
            var enemyPlayerLocation = playersLocations.get(enemyPlayerID);
            if(enemyPlayerLocation.equals(currentPlayerLocation)) {
                map.getFieldForCoordinates(currentPlayerLocation.x, currentPlayerLocation.y).playerPositionState = GameMap.PlayerPositionState.BothPlayerPosition;
            } else {
                map.getFieldForCoordinates(enemyPlayerLocation.x, enemyPlayerLocation.y).playerPositionState = GameMap.PlayerPositionState.EnemyPlayerPosition;
            }
        }

        for(var location : playersRevealedFields.get(playerID)) {
            if(location.equals(treasureLocation)) {
                if(!playersHasPickedTreasure.get(playerID)) {
                    map.getFieldForCoordinates(location.x, location.y).gameTreasureState = GameMap.GameTreasureState.MyTreasureIsPresent;
                }
            } else if (location.equals(enemyCastleLocation)) {
                map.getFieldForCoordinates(location.x, location.y).gameFortState = GameMap.GameFortState.EnemyFortPresent;
            }
        }
        return map;
    }

    private void movePlayerFromNodeToNode(String playerID, Integer toX, Integer toY) {
        playersLocations.put(playerID, new MapComposer.Location(toX, toY));

        if(playersTreasures.get(playerID).x == toX && playersTreasures.get(playerID).y == toY) {
            playersHasPickedTreasure.put(playerID, true);
        }

        if(map.getFieldForCoordinates(toX, toY).terrain == GameMap.Terrain.Grass) {
            playersRevealedFields.get(playerID).add(new MapComposer.Location(toX, toY));
        } else if(map.getFieldForCoordinates(toX, toY).terrain == GameMap.Terrain.Mountain) {
            var neighbouringFields = map.getSurroundingFields(toX, toY);
            for(var field : neighbouringFields) {
                playersRevealedFields.get(playerID).add(new MapComposer.Location(field.x, field.y));
            }
        }
    }

    public void processMoveForPlayerID(String playerID, EMove move) {
        PlayerProgress progress = playersProgress.get(playerID);
        Graph.Node currentNode = graph.getNodes().stream().filter(node -> node.field.x.equals(playersLocations.get(playerID).x) &&
                node.field.y.equals(playersLocations.get(playerID).y)).findFirst().orElseThrow();
        var edgeForMove = currentNode.edges.get(move);
        if(edgeForMove == null)
        {
            LOGGER.info("Invalid move for player " + playerID + ": " + move);
            playersHasViolatedRule.put(playerID, true);
            return;
        }

        Queue<EMove> moves = new LinkedList<>();
        for(int i = 0; i < edgeForMove.weight; i++) {
            moves.add(move);
        }

        completedMoves++;
        if(progress == null || progress.moves.peek() == null || !progress.moves.peek().equals(move)) {
            progress = new PlayerProgress(moves, edgeForMove.destinationNode);
            progress.moves.remove();
            playersProgress.put(playerID, progress);
        } else {
            progress.moves.remove();
            if(progress.moves.isEmpty()){
                movePlayerFromNodeToNode(playerID, edgeForMove.destinationNode.field.x, edgeForMove.destinationNode.field.y);
            }
        }
    }

    public boolean hasPlayerPickedUpTreasure(String playerID) {
        if(!playersHasPickedTreasure.keySet().contains(playerID)) {
            throw new IllegalArgumentException("Player " + playerID + " does not exist!");
        }
        return playersHasPickedTreasure.get(playerID);
    }

    public boolean hasPlayerWon(String playerID) {
        if(!playersHasPickedTreasure.get(playerID)) {
            return false;
        }
        var playerLocation = playersLocations.get(playerID);
        var enemyPlayerID = playersCastles.keySet().stream().filter(id -> !id.equals(playerID)).findFirst().orElseThrow();
        return playerLocation.equals(playersCastles.get(enemyPlayerID));
    }

    public boolean hasPlayerLost(String playerID) {
        if(playersHasViolatedRule.get(playerID) || completedMoves >= 200) {
            return true;
        }
        var enemyPlayerID = playersCastles.keySet().stream().filter(id -> !id.equals(playerID)).findFirst().orElseThrow();
        return hasPlayerWon(enemyPlayerID);
    }
}
