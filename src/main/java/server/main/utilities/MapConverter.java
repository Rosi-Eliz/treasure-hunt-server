package server.main.utilities;
import MessagesBase.ETerrain;
import MessagesBase.HalfMap;
import MessagesBase.HalfMapNode;
import MessagesGameState.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Random;

public class MapConverter {
    private static HalfMapNode generateHalfMapNode(GameMap.GameField field) {
        ETerrain terrain = null;
        switch(field.terrain)
        {
            case Grass: terrain =  ETerrain.Grass; break;
            case Water: terrain = ETerrain.Water; break;
            case Mountain: terrain = ETerrain.Mountain; break;
            default: throw new IllegalArgumentException("Unknown terrain type");

        }
        boolean castlePresent = field.gameFortState == GameMap.GameFortState.MyFortPresent;
        return new HalfMapNode(field.x, field.y, castlePresent, terrain);
    }

    private static FullMapNode generateFullMapNode(GameMap.GameField field) {
        ETerrain terrain = null;
        switch(field.terrain)
        {
            case Grass: terrain =  ETerrain.Grass; break;
            case Water: terrain = ETerrain.Water; break;
            case Mountain: terrain = ETerrain.Mountain; break;
            case Unassigned: throw new IllegalArgumentException("Unassigned terrain type");
            default: throw new IllegalArgumentException("Unknown terrain type");
        }

        EPlayerPositionState playerPos = null;
        switch(field.playerPositionState)
        {
            case NoPlayerPresent: playerPos = EPlayerPositionState.NoPlayerPresent; break;
            case EnemyPlayerPosition: playerPos = EPlayerPositionState.EnemyPlayerPosition; break;
            case MyPlayerPosition: playerPos = EPlayerPositionState.MyPlayerPosition; break;
            case BothPlayerPosition: playerPos = EPlayerPositionState.BothPlayerPosition; break;
            default: throw new IllegalArgumentException("Unknown player position state");
        }

        ETreasureState treasureState = null;
        switch(field.gameTreasureState)
        {
            case NoOrUnknownTreasureState: treasureState = ETreasureState.NoOrUnknownTreasureState; break;
            case MyTreasureIsPresent: treasureState = ETreasureState.MyTreasureIsPresent; break;
            default: throw new IllegalArgumentException("Unknown game treasure state");
        }

        EFortState fortState = null;
        switch(field.gameFortState)
        {
            case NoOrUnknownFortState: fortState = EFortState.NoOrUnknownFortState; break;
            case EnemyFortPresent: fortState = EFortState.EnemyFortPresent; break;
            case MyFortPresent: fortState = EFortState.MyFortPresent; break;
            default: throw new IllegalArgumentException("Unknown fort state");
        }

        return new FullMapNode(terrain, playerPos, treasureState, fortState, field.x, field.y );
    }

    private static GameMap.GameField generateFieldFromNode(FullMapNode fullMapNode) {
        GameMap.Terrain terrain = null;
        switch(fullMapNode.getTerrain())
        {
            case Grass: terrain =  GameMap.Terrain.Grass; break;
            case Water: terrain = GameMap.Terrain.Water; break;
            case Mountain: terrain = GameMap.Terrain.Mountain; break;
            default: throw new IllegalArgumentException("Unknown terrain type");
        }

        GameMap.PlayerPositionState playerPos = null;
        switch(fullMapNode.getPlayerPositionState())
        {
            case NoPlayerPresent: playerPos = GameMap.PlayerPositionState.NoPlayerPresent; break;
            case EnemyPlayerPosition: playerPos = GameMap.PlayerPositionState.EnemyPlayerPosition; break;
            case MyPlayerPosition: playerPos = GameMap.PlayerPositionState.MyPlayerPosition; break;
            case BothPlayerPosition: playerPos = GameMap.PlayerPositionState.BothPlayerPosition; break;
            default: throw new IllegalArgumentException("Unknown player position state");
        }

        GameMap.GameTreasureState treasureState = null;
        switch(fullMapNode.getTreasureState())
        {
            case NoOrUnknownTreasureState: treasureState = GameMap.GameTreasureState.NoOrUnknownTreasureState; break;
            case MyTreasureIsPresent: treasureState = GameMap.GameTreasureState.MyTreasureIsPresent; break;
        }

        GameMap.GameFortState fortState = null;
        switch(fullMapNode.getFortState())
        {
            case NoOrUnknownFortState: fortState = GameMap.GameFortState.NoOrUnknownFortState; break;
            case MyFortPresent: fortState = GameMap.GameFortState.MyFortPresent; break;
            case EnemyFortPresent: fortState = GameMap.GameFortState.EnemyFortPresent; break;

        }
        return new GameMap.GameField(fullMapNode.getX(), fullMapNode.getY(), terrain, playerPos, treasureState, fortState);
    }

    public static HalfMap generateHalfMap(String uniquePlayerID, GameMap gameMap){
            List<HalfMapNode> transformedFields = gameMap.getAllFields().stream().map(MapConverter::generateHalfMapNode).collect(Collectors.toList());
            return new HalfMap(uniquePlayerID, transformedFields);
    }

    public static FullMap generateFullMap(GameMap gameMap, String playerID, boolean shouldMockEnemyPosition){
        if(shouldMockEnemyPosition)
        {
            List<GameMap.GameField> transformedFields = gameMap.getAllFields().stream().filter(field -> field.terrain != GameMap.Terrain.Water
            && field.playerPositionState == GameMap.PlayerPositionState.NoPlayerPresent).collect(Collectors.toList());

            var enemyRealPosition = gameMap.getAllFields().stream().filter(field -> field.playerPositionState == GameMap.PlayerPositionState.MyPlayerPosition
            && field.gameFortState != GameMap.GameFortState.MyFortPresent).findFirst();
            enemyRealPosition.ifPresent(gameField -> gameField.playerPositionState = GameMap.PlayerPositionState.NoPlayerPresent);

            var randomField = transformedFields.get(new Random().nextInt(transformedFields.size()));
            randomField.playerPositionState = GameMap.PlayerPositionState.EnemyPlayerPosition;
        }
        List<FullMapNode> transformedFields = gameMap.getAllFields().stream().map(MapConverter::generateFullMapNode).collect(Collectors.toList());
        return new FullMap(transformedFields);
    }

    public static GameMap generateGameMap(FullMap fullMap)
    {
        int rows = fullMap.getMapNodes().stream().max(Comparator.comparingInt(FullMapNode::getX)).orElseThrow().getX() + 1;
        int columns = fullMap.getMapNodes().stream().max(Comparator.comparingInt(FullMapNode::getY)).orElseThrow().getY() + 1;
        GameMap gameMap = new GameMap(rows, columns);
        for(FullMapNode node : fullMap.getMapNodes())
        {
            gameMap.setField(generateFieldFromNode(node));
        }
        return gameMap;
    }

    private static GameMap.GameField generateFieldFromNode(HalfMapNode halfMapNode) {
        GameMap.Terrain terrain = null;
        switch(halfMapNode.getTerrain())
        {
            case Grass: terrain =  GameMap.Terrain.Grass; break;
            case Water: terrain = GameMap.Terrain.Water; break;
            case Mountain: terrain = GameMap.Terrain.Mountain; break;
            default: throw new IllegalArgumentException("Unknown terrain type");
        }
        GameMap.PlayerPositionState playerPos = GameMap.PlayerPositionState.NoPlayerPresent;
        GameMap.GameTreasureState treasureState = GameMap.GameTreasureState.NoOrUnknownTreasureState;
        GameMap.GameFortState fortState = null;
        if(halfMapNode.isFortPresent())
        {
           fortState = GameMap.GameFortState.MyFortPresent;
           playerPos = GameMap.PlayerPositionState.MyPlayerPosition; //????
        } else {
            fortState = GameMap.GameFortState.NoOrUnknownFortState;
        }
        return new GameMap.GameField(halfMapNode.getX(), halfMapNode.getY(), terrain, playerPos, treasureState, fortState);
    }

    public static GameMap generateHalfMap(HalfMap halfMap)
    {
        int columns = halfMap.getMapNodes().stream().max(Comparator.comparingInt(HalfMapNode::getX)).orElseThrow().getX() + 1;
        int rows = halfMap.getMapNodes().stream().max(Comparator.comparingInt(HalfMapNode::getY)).orElseThrow().getY() + 1;
        GameMap gameMap = new GameMap(columns, rows);
        for(HalfMapNode node : halfMap.getMapNodes())
        {
            gameMap.setField(generateFieldFromNode(node));
        }
        return gameMap;
    }

    public static GameMap combineHalfMaps(GameMap halfMap1, GameMap halfMap2, boolean areMapsCombinedHorizontally)
    {
        int rows = 0;
        int columns = 0;
        if(!areMapsCombinedHorizontally) {
             columns = halfMap1.getColumns();
             rows = halfMap1.getRows() + halfMap2.getRows();
        } else {
            columns =  halfMap1.getColumns() + halfMap2.getColumns();
            rows = halfMap1.getRows();
        }
        GameMap gameMap = new GameMap(columns, rows);

        for (GameMap.GameField field : halfMap1.getAllFields()) {
            gameMap.setField(field);
        }
        for (GameMap.GameField field : halfMap2.getAllFields())
        {
            if(areMapsCombinedHorizontally) {
                field.x += halfMap1.getColumns();
            } else {
                field.y += halfMap1.getRows();
            }
            gameMap.setField(field);
        }
        return gameMap;
    }

    private static GameMap.GameField generateFieldFromGraphNode(Graph.Node graphNode) {
        GameMap.Terrain terrain = null;
        switch(graphNode.field.terrain)
        {
            case Grass: terrain =  GameMap.Terrain.Grass; break;
            case Water: terrain = GameMap.Terrain.Water; break;
            case Mountain: terrain = GameMap.Terrain.Mountain; break;
            default: throw new IllegalArgumentException("Unknown terrain type");
        }

        GameMap.PlayerPositionState playerPos = null;
        switch(graphNode.field.playerPositionState)
        {
            case NoPlayerPresent: playerPos = GameMap.PlayerPositionState.NoPlayerPresent; break;
            case EnemyPlayerPosition: playerPos = GameMap.PlayerPositionState.EnemyPlayerPosition; break;
            case MyPlayerPosition: playerPos = GameMap.PlayerPositionState.MyPlayerPosition; break;
            case BothPlayerPosition: playerPos = GameMap.PlayerPositionState.BothPlayerPosition; break;
            default: throw new IllegalArgumentException("Unknown player position state");
        }

        GameMap.GameTreasureState treasureState = null;
        switch(graphNode.field.gameTreasureState)
        {
            case NoOrUnknownTreasureState: treasureState = GameMap.GameTreasureState.NoOrUnknownTreasureState; break;
            case MyTreasureIsPresent: treasureState = GameMap.GameTreasureState.MyTreasureIsPresent; break;
        }

        GameMap.GameFortState fortState = null;
        switch(graphNode.field.gameFortState)
        {
            case NoOrUnknownFortState: fortState = GameMap.GameFortState.NoOrUnknownFortState; break;
            case MyFortPresent: fortState = GameMap.GameFortState.MyFortPresent; break;
            case EnemyFortPresent: fortState = GameMap.GameFortState.EnemyFortPresent; break;

        }
        return new GameMap.GameField(graphNode.field.x, graphNode.field.y, terrain, playerPos, treasureState, fortState);
    }


    public static GameMap generateGameMap(Graph graph)
    {
        int rows = graph.getNodes().stream().max(Comparator.comparingInt(node -> node.field.x)).orElseThrow().field.x + 1;
        int columns = graph.getNodes().stream().max(Comparator.comparingInt(node->node.field.y)).orElseThrow().field.y + 1;
        GameMap gameMap = new GameMap(rows, columns);
        for(Graph.Node node : graph.getNodes())
        {
            gameMap.setField(generateFieldFromGraphNode(node));
        }
        return gameMap;
    }
}
