package server.main.utilities;
import MessagesBase.EMove;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.main.gameStatus.GameStatusService;

import java.util.*;
import java.util.stream.Collectors;


public class GameMap implements Cloneable {
    public static final int grassSteps = 1;
    public static final int waterSteps = 1;
    public static final int mountainSteps = 2;
    private HashMap<String, GameField> fields = new HashMap<>();
    private int columns;
    private int rows;
    private static Logger LOGGER = LoggerFactory.getLogger(GameMap.class);

    public GameMap(int columns, int rows) {
        for(int row = 0; row < rows; row++)
        {
            for(int column = 0; column < columns; column++)
            {
                fields.put(column + "-" + row,
                        new GameField(column, row,
                                Terrain.Unassigned,
                                PlayerPositionState.NoPlayerPresent,
                                GameTreasureState.NoOrUnknownTreasureState,
                                GameFortState.NoOrUnknownFortState));
            }
        }
        this.columns = columns;
        this.rows = rows;
    }

    public enum Terrain {

        Water(0),
        Grass(1),
        Mountain(2),
        Unassigned(-1);
        private int index;

        private Terrain(int index) {
            this.index = index;
        }
    }

    public enum PlayerPositionState {
        NoPlayerPresent,
        EnemyPlayerPosition,
        MyPlayerPosition,
        BothPlayerPosition;

        private PlayerPositionState() {
        }
    }

    public enum GameTreasureState {
        NoOrUnknownTreasureState,
        MyTreasureIsPresent,
        EnemyTreasureIsPresent;

        private GameTreasureState() {
        }
    }

    public enum GameFortState {
        NoOrUnknownFortState,
        MyFortPresent,
        EnemyFortPresent;

        private GameFortState() {
        }
    }

    public enum  SequenceOrientation {
        Vertical,
        Horizontal,
        Both,
        None;

        private SequenceOrientation() {
        }
    }

    public static class GameField {
        public Integer x;
        public Integer y;
        public Terrain terrain;
        public PlayerPositionState playerPositionState;
        public GameTreasureState gameTreasureState;
        public GameFortState gameFortState;
        public GameField(Integer x,
                         Integer y,
                         Terrain terrain,
                         PlayerPositionState playerPositionState,
                         GameTreasureState gameTreasureState,
                         GameFortState gameFortState) {
            this.x = x;
            this.y = y;
            this.terrain = terrain;
            this.playerPositionState = playerPositionState;
            this.gameTreasureState = gameTreasureState;
            this.gameFortState = gameFortState;
        }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GameField gameField = (GameField) o;
            return Objects.equals(x, gameField.x) && Objects.equals(y, gameField.y) && terrain == gameField.terrain;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, terrain, playerPositionState);
        }
    }

    public List<GameField> getFieldsByTerrain(Terrain terrain) {
        return fields.values()
                .stream()
                .filter(field -> field.terrain == terrain)
                .collect(Collectors.toList());
    }

    public GameField getFieldForCoordinates(int x, int y)
    {
        String key = x + "-" + y;
        return fields.get(key);
    }

    public void setField(GameField field)
    {
        String key = field.x + "-" + field.y;
        fields.put(key, field);
    }

    public List<GameField> getSurroundingFields(int x, int y)
    {
        List<GameField> surroundingFields = new ArrayList<>();
        for (int column = x - 1; column <= x + 1; column++)
        {
            for (int row = y - 1; row <= y + 1; row++)
            {
                if (column == x && row == y)
                {
                    continue;
                }
                GameField field = getFieldForCoordinates(column, row);
                if (field != null)
                {
                    surroundingFields.add(field);
                }
            }
        }
        return surroundingFields;
    }


    public List<GameField> getNeighboursForField(int x, int y)
    {
        List<GameMap.GameField> neighbouringFields = new ArrayList<>();
        neighbouringFields.add(getFieldForCoordinates(x+1, y));
        neighbouringFields.add(getFieldForCoordinates(x,y+1));
        neighbouringFields.add(getFieldForCoordinates(x-1, y));
        neighbouringFields.add(getFieldForCoordinates(x,y-1));

        neighbouringFields = neighbouringFields.stream().filter(field -> field != null)
                .collect(Collectors.toList());
        return neighbouringFields;
    }

    public Set<GameField> getAllFields()
    {
        return new HashSet<>(fields.values());
    }

    public boolean isFieldSurroundedByWater(GameMap.GameField field)
    {
        return getNeighboursForField(field.x, field.y).stream()
                .allMatch(neighbour -> neighbour.terrain == GameMap.Terrain.Water);
    }

    public boolean areThereNeighbouringIslandsForWaterAt(GameMap.GameField field) {
      GameMap.Terrain oldTerrain = field.terrain;
        field.terrain = GameMap.Terrain.Water;
        setField(field);

        boolean result = getNeighboursForField(field.x, field.y)
                .stream().anyMatch(neighbour -> isFieldSurroundedByWater(neighbour));
        field.terrain = oldTerrain;
        setField(field);
        return result;
    }

    public boolean isFieldBorderLocated(GameField field)
    {
        return field.x == 0 || field.y == 0 || field.x == columns - 1 || field.y == rows - 1;
    }

    public boolean isFieldBorderEdgeLocated(GameField field)
    {
        return field.x == 0 && field.y == 0 ||
                field.x == 0 && field.y == rows - 1 ||
                field.x == 0 && field.y == columns - 1 ||
                field.x == rows -1  && field.y == columns - 1;
    }

    public SequenceOrientation getSequenceOrientation(GameField field)
    {
        if(isFieldBorderEdgeLocated(field)) {
            return SequenceOrientation.Both;
        } else if(field.x == 0 || field.x == columns - 1) {
            return SequenceOrientation.Vertical;
        } else if(field.y == 0 || field.y == rows - 1) {
            return SequenceOrientation.Horizontal;
        } else {
            return SequenceOrientation.None;
        }
    }

    public List<GameField> getWaterFieldsAtBorderFor(GameField field)
    {
        switch(getSequenceOrientation(field))
        {
            case Vertical:
                return getAllFields().stream().filter(f -> f.x == field.x && f.terrain == Terrain.Water).collect(Collectors.toList());
            case Horizontal:
                return getAllFields().stream().filter(f -> f.y == field.y && f.terrain == Terrain.Water).collect(Collectors.toList());
            case Both:
                return getAllFields().stream().filter(f -> (f.x == field.x || f.y == field.y) && f.terrain == Terrain.Water).collect(Collectors.toList());
            case None:
                return new ArrayList<>();
        }
        return new ArrayList<>();
    }

    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }

    @Override
    public GameMap clone() throws CloneNotSupportedException
    {
        GameMap map = new GameMap(columns, rows);
        for(int row = 0; row < rows; row++)
        {
            for(int column = 0; column < columns; column++)
            {
                var field = getFieldForCoordinates(column, row);
                GameMap.Terrain terrain = null;
                switch(field.terrain)
                {
                    case Water:
                        terrain = Terrain.Water;
                        break;
                    case Grass:
                        terrain = Terrain.Grass;
                        break;
                    case Mountain:
                        terrain = Terrain.Mountain;
                        break;
                    case Unassigned:
                        terrain = Terrain.Unassigned;
                        break;
                }
                GameMap.PlayerPositionState playerPositionState = null;
                switch(field.playerPositionState)
                {
                    case MyPlayerPosition:
                        playerPositionState = PlayerPositionState.MyPlayerPosition;
                        break;
                    case EnemyPlayerPosition:
                        playerPositionState = PlayerPositionState.EnemyPlayerPosition;
                        break;
                    case BothPlayerPosition:
                        playerPositionState = PlayerPositionState.BothPlayerPosition;
                        break;
                    case NoPlayerPresent:
                        playerPositionState = PlayerPositionState.NoPlayerPresent;
                        break;
                }

                GameMap.GameTreasureState gameTreasureState = null;
                switch(field.gameTreasureState)
                {
                    case MyTreasureIsPresent:
                        gameTreasureState = GameTreasureState.MyTreasureIsPresent;
                        break;
                    case EnemyTreasureIsPresent:
                        gameTreasureState = GameTreasureState.EnemyTreasureIsPresent;
                        break;
                    case NoOrUnknownTreasureState:
                        gameTreasureState = GameTreasureState.NoOrUnknownTreasureState;
                        break;
                }

                GameMap.GameFortState gameFortState = null;
                switch(field.gameFortState)
                {
                    case MyFortPresent:
                        gameFortState = GameFortState.MyFortPresent;
                        break;
                    case EnemyFortPresent:
                        gameFortState = GameFortState.EnemyFortPresent;
                        break;
                    case NoOrUnknownFortState:
                        gameFortState = GameFortState.NoOrUnknownFortState;
                        break;
                }

                map.fields.put(column + "-" + row,
                        new GameField(column, row,
                                terrain,
                                playerPositionState,
                                gameTreasureState,
                                gameFortState));
            }
        }
      return map;
    }

    public GameMap.GameField getNeighbouringFieldForDirection(GameField field, EMove direction)
    {
        switch(direction)
        {
            case Up:
                return getFieldForCoordinates(field.x, field.y - 1);
            case Down:
                return getFieldForCoordinates(field.x, field.y + 1);
            case Left:
                return getFieldForCoordinates(field.x - 1, field.y);
            case Right:
                return getFieldForCoordinates(field.x + 1, field.y);
        }
        return null;
    }

    public void print() {
        for (int i = 0; i < rows; i ++) {
            for (int j = 0; j < columns;  j++) {
                GameField field = getFieldForCoordinates(j, i);
                if(field.playerPositionState == PlayerPositionState.MyPlayerPosition) {
                    System.out.print("[*]");
                    continue;
                }
                switch (field.terrain) {
                    case Water:
                        System.out.print("[W]");
                        break;
                    case Grass:
                        if (field.gameFortState == GameFortState.MyFortPresent) {
                            System.out.print("[x]");
                        } else if(field.gameTreasureState == GameTreasureState.MyTreasureIsPresent) {
                            System.out.print("[T]");
                        } else if(field.gameFortState == GameFortState.EnemyFortPresent) {
                            System.out.print("[E]");
                        } else {
                            System.out.print("[G]");
                        }
                        break;
                    case Mountain:
                        System.out.print("[М]");
                        break;
                    case Unassigned:
                        System.out.print("[U]");
                        break;
                }
            }
            System.out.println();
        }
    }
}
