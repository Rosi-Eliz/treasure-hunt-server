package server.main.models;

import server.main.utilities.GameMap;
import server.main.utilities.MapConverter;

import java.util.HashMap;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

public class MapComposer {
    public static class Location{
        public int x;
        public int y;

        @Override
        public String toString() {
            return "Location{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }

        public Location(int x, int y) {
            this.x = x;
            this.y = y;

        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Location location = (Location) o;
            return x == location.x && y == location.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    private class BoundsLocator {
        private Location player1MapTopLeftLocation;
        private Location player1MapBottomRightLocation;
        private Location player2MapTopLeftLocation;
        private Location player2MapBottomRightLocation;

        public BoundsLocator(Location player1MapTopLeftLocation,
                             Location player1MapBottomRightLocation,
                             Location player2MapTopLeftLocation,
                             Location player2MapBottomRightLocation) {
            this.player1MapTopLeftLocation = player1MapTopLeftLocation;
            this.player1MapBottomRightLocation = player1MapBottomRightLocation;
            this.player2MapTopLeftLocation = player2MapTopLeftLocation;
            this.player2MapBottomRightLocation = player2MapBottomRightLocation;
        }
    }

    private String player1ID;
    private String player2ID;
    private final GameMap player1Half;
    private final GameMap player2Half;
    private Location player1CastleLocation;
    private Location player2CastleLocation;
    public  Location player1TreasureLocation;
    public  Location player2TreasureLocation;
    private BoundsLocator boundsLocator;
    private GameMap initialFullMap;

    private void combineMaps()
    {
        boolean player1MapIsFirst = new Random().nextBoolean();
        boolean secondMapIsCombinedHorizontally = new Random().nextBoolean();
        if(player1MapIsFirst && secondMapIsCombinedHorizontally)
        {
            Location player1MapTopLeftLocation =  new Location(0,0);
            Location player1MapBottomRightLocation = new Location(player1Half.getColumns() - 1, player1Half.getRows() - 1);
            Location player2MapTopLeftLocation = new Location(player1Half.getColumns(), 0);
            Location player2MapBottomRightLocation  = new Location(player1Half.getColumns() + player2Half.getColumns() - 1, player2Half.getRows() - 1);
            this.boundsLocator = new BoundsLocator(player1MapTopLeftLocation, player1MapBottomRightLocation, player2MapTopLeftLocation, player2MapBottomRightLocation);

        } else if(player1MapIsFirst && !secondMapIsCombinedHorizontally) {
            Location player1MapTopLeftLocation =  new Location(0,0);
            Location player1MapBottomRightLocation = new Location(player1Half.getColumns() - 1, player1Half.getRows() - 1);
            Location player2MapTopLeftLocation = new Location(0, player1Half.getRows());
            Location player2MapBottomRightLocation  = new Location(player2Half.getColumns() - 1, player1Half.getRows() + player2Half.getRows() - 1);
            this.boundsLocator = new BoundsLocator(player1MapTopLeftLocation, player1MapBottomRightLocation, player2MapTopLeftLocation, player2MapBottomRightLocation);

        } else if(!player1MapIsFirst && secondMapIsCombinedHorizontally) {
            Location player2MapTopLeftLocation =  new Location(0,0);
            Location player2MapBottomRightLocation = new Location(player1Half.getColumns() - 1, player1Half.getRows() - 1);
            Location player1MapTopLeftLocation = new Location(player1Half.getColumns(), 0);
            Location player1MapBottomRightLocation  = new Location(player1Half.getColumns() + player2Half.getColumns() - 1, player2Half.getRows() - 1);
            this.boundsLocator = new BoundsLocator(player1MapTopLeftLocation, player1MapBottomRightLocation, player2MapTopLeftLocation, player2MapBottomRightLocation);

        } else {
            Location player2MapTopLeftLocation =  new Location(0,0);
            Location player2MapBottomRightLocation = new Location(player1Half.getColumns() - 1, player1Half.getRows() - 1);
            Location player1MapTopLeftLocation = new Location(0, player1Half.getRows());
            Location player1MapBottomRightLocation  = new Location(player2Half.getColumns() - 1, player1Half.getRows() + player2Half.getRows() - 1);
            this.boundsLocator = new BoundsLocator(player1MapTopLeftLocation, player1MapBottomRightLocation, player2MapTopLeftLocation, player2MapBottomRightLocation);
        }

        setPlayersCastles(player1Half, player2Half, player1MapIsFirst, secondMapIsCombinedHorizontally);
        if(player1MapIsFirst) {
            initialFullMap = MapConverter.combineHalfMaps(player1Half, player2Half, secondMapIsCombinedHorizontally);
        } else {
            initialFullMap = MapConverter.combineHalfMaps(player2Half, player1Half, secondMapIsCombinedHorizontally);
        }
        setPlayersTreasures();
    }

    private void setPlayersCastles(GameMap player1Half, GameMap player2Half, boolean player1MapIsFirst, boolean secondMapIsCombinedHorizontally){
        var player1CastleField = player1Half.getAllFields().stream().filter(field -> field.gameFortState == GameMap.GameFortState.MyFortPresent).findFirst().orElseThrow();
        var player2CastleField = player2Half.getAllFields().stream().filter(field -> field.gameFortState == GameMap.GameFortState.MyFortPresent).findFirst().orElseThrow();
        Location player1CastleLocation = new Location(player1CastleField.x, player1CastleField.y);
        Location player2CastleLocation = new Location(player2CastleField.x, player2CastleField.y);
        if(player1MapIsFirst && secondMapIsCombinedHorizontally)
        {
            player2CastleLocation.x += player1Half.getColumns();
        } else if(player1MapIsFirst && !secondMapIsCombinedHorizontally) {
            player2CastleLocation.y += player1Half.getRows();
        } else if(!player1MapIsFirst && secondMapIsCombinedHorizontally) {
            player1CastleLocation.x += player2Half.getColumns();
        } else {
            player1CastleLocation.y += player2Half.getRows();
        }
        this.player1CastleLocation = player1CastleLocation;
        this.player2CastleLocation = player2CastleLocation;
    }

    private void setPlayersTreasures()
    {
        Location player1MapTopLeftLocation = boundsLocator.player1MapTopLeftLocation;
        Location player1MapBottomRightLocation = boundsLocator.player1MapBottomRightLocation;
        Location player2MapTopLeftLocation = boundsLocator.player2MapTopLeftLocation;
        Location player2MapBottomRightLocation = boundsLocator.player2MapBottomRightLocation;
        var grassFields = initialFullMap.getAllFields().stream().filter(field -> field.x >= player1MapTopLeftLocation.x &&
                field.y >= player1MapTopLeftLocation.y &&
                field.x <= player1MapBottomRightLocation.x &&
                field.y <= player1MapBottomRightLocation.y &&
                field.x != player1CastleLocation.x &&
                field.y != player1CastleLocation.y &&
                field.terrain == GameMap.Terrain.Grass).collect(Collectors.toList());

        var randomTreasureFieldPlayer1 = grassFields.get(new Random().nextInt(grassFields.size()));
        player1TreasureLocation = new Location(randomTreasureFieldPlayer1.x, randomTreasureFieldPlayer1.y);

        grassFields = initialFullMap.getAllFields().stream().filter(field -> field.x >= player2MapTopLeftLocation.x &&
                field.y >= player2MapTopLeftLocation.y &&
                field.x <= player2MapBottomRightLocation.x &&
                field.y <= player2MapBottomRightLocation.y &&
                field.x != player2CastleLocation.x &&
                field.y != player2CastleLocation.y &&
                field.terrain == GameMap.Terrain.Grass).collect(Collectors.toList());

        var randomTreasureFieldPlayer2 = grassFields.get(new Random().nextInt(grassFields.size()));
        player2TreasureLocation = new Location(randomTreasureFieldPlayer2.x, randomTreasureFieldPlayer2.y);

    }

    public MapComposer(GameMap player1Half, GameMap player2Half, String player1ID, String player2ID) {
        this.player1ID = player1ID;
        this.player2ID = player2ID;
        this.player1Half = player1Half;
        this.player2Half = player2Half;
        combineMaps();

        var player1GrassFields = player1Half.getFieldsByTerrain(GameMap.Terrain.Grass)
                .stream()
                .filter(f -> f.x >= boundsLocator.player1MapTopLeftLocation.x &&
                                f.x <= boundsLocator.player1MapBottomRightLocation.x &&
                                f.y >= boundsLocator.player1MapTopLeftLocation.y &&
                                f.y <= boundsLocator.player1MapBottomRightLocation.y &&
                                f.x != player1CastleLocation.x &&
                                f.y != player1CastleLocation.y
                        )
                .collect(Collectors.toList());
        var player2GrassFields = player2Half.getFieldsByTerrain(GameMap.Terrain.Grass)
                .stream()
                .filter(f -> f.x >= boundsLocator.player2MapTopLeftLocation.x &&
                        f.x <= boundsLocator.player2MapBottomRightLocation.x &&
                        f.y >= boundsLocator.player2MapTopLeftLocation.y &&
                        f.y <= boundsLocator.player2MapBottomRightLocation.y &&
                        f.x != player2CastleLocation.x &&
                        f.y != player2CastleLocation.y)
                .collect(Collectors.toList());

        Random rand = new Random();
        var randomGrassFieldPlayer1 = player1GrassFields.get(rand.nextInt(player1GrassFields.size() - 1));
        this.player1TreasureLocation = new Location(randomGrassFieldPlayer1.x, randomGrassFieldPlayer1.y);
        var randomGrassFieldPlayer2 = player2GrassFields.get(rand.nextInt(player2GrassFields.size() - 1));
        this.player2TreasureLocation = new Location(randomGrassFieldPlayer2.x, randomGrassFieldPlayer2.y);
    }

    public HashMap<String, Location> playersCastles() {
        HashMap<String, Location> playersCastles = new HashMap<>();
        playersCastles.put(player1ID, player1CastleLocation);
        playersCastles.put(player2ID, player2CastleLocation);
        return playersCastles;
    }

    public HashMap<String, Location> playersTreasures() {
        HashMap<String, Location> playersTreasures = new HashMap<>();
        playersTreasures.put(player1ID, player1TreasureLocation);
        playersTreasures.put(player2ID, player2TreasureLocation);
        return playersTreasures;
    }

    public GameMap getInitialFullMap() {
        return initialFullMap;
    }

    public GameMap generateFullMapForPlayer1()
    {
        GameMap map = null;
        try {
            map = initialFullMap.clone();
            var enemyCastleField = map.getFieldForCoordinates(player2CastleLocation.x, player2CastleLocation.y);
            enemyCastleField.gameFortState = GameMap.GameFortState.NoOrUnknownFortState;
        }   catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        return map;
    }

    public GameMap generateFullMapForPlayer2()
    {
        GameMap map = null;
        try {
            map = initialFullMap.clone();
            var enemyCastleField = map.getFieldForCoordinates(player1CastleLocation.x, player1CastleLocation.y);
            enemyCastleField.gameFortState = GameMap.GameFortState.NoOrUnknownFortState;
        }   catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        return map;
    }
}
