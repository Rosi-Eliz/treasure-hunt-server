package server.main.processHalfMap;

import MessagesBase.HalfMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.main.utilities.GameMap;
import server.main.utilities.MapConverter;
import java.util.List;
import java.util.stream.Collectors;

public class MapValidator {
    private static final int MIN_WATER_FIELDS = 4;
    private static final int MIN_MOUNTAIN_FIELDS = 3;
    private static final int MAX_HORIZONTAL_WATER_FIELDS = 3;
    private static final int MAX_VERTICAL_WATER_FIELDS = 1;
    private static Logger LOGGER = LoggerFactory.getLogger(MapValidator.class);


    private static boolean borderConditionsWaterMet(GameMap map, List<GameMap.GameField> fields)
    {
        int horizontalWaters = 0;
        int verticalWaters  = 0;
        int tempPreviousX = -1;
        int tempPreviousY = -1;
        for(GameMap.GameField field : fields) {
            if (field.x == 0 || field.x == map.getColumns() - 1) {
                if (tempPreviousX == -1 || tempPreviousX == field.x) {
                    verticalWaters++;
                    tempPreviousX = field.x;
                }
            }

            if (field.y == 0 || field.y == map.getRows() - 1) {
                if (tempPreviousY == -1 || tempPreviousY == field.y) {
                    horizontalWaters++;
                    tempPreviousY = field.y;
                }
            }
        }
        return horizontalWaters <= MAX_HORIZONTAL_WATER_FIELDS && verticalWaters <= MAX_VERTICAL_WATER_FIELDS;
    }

    private static boolean isBorderWaterConditionMet(GameMap map, GameMap.GameField field)
    {
        if(map.isFieldBorderLocated(field)) {
            GameMap.Terrain oldTerrain = field.terrain;
            field.terrain = GameMap.Terrain.Water;
            map.setField(field);

            List<GameMap.GameField> fields = map.getWaterFieldsAtBorderFor(field);
            boolean result = borderConditionsWaterMet(map, fields);

            field.terrain = oldTerrain;
            map.setField(field);
            LOGGER.info("Border water conditions met: {}", result);
            return result;
        }
        return true;
    }

    private static boolean doesWaterFieldCreateSeparation(GameMap map, GameMap.GameField field) {
            GameMap.Terrain oldTerrain = field.terrain;
            field.terrain = GameMap.Terrain.Water;
            map.setField(field);
            var allWaterFields = map.getFieldsByTerrain(GameMap.Terrain.Water)
                    .stream()
                    .sorted((field1, field2) -> field1.y - field2.y)
                    .collect(Collectors.toList());

            var yCoordinates = allWaterFields.stream().map(f -> f.y).collect(Collectors.toList());
            if(yCoordinates.stream().distinct().count() == 4) {
                var tempField = allWaterFields.get(0);
                for(var f : allWaterFields) {
                    if(Math.abs(tempField.x - f.x) > 1) {
                        field.terrain = oldTerrain;
                        map.setField(field);
                        return false;
                    }
                    tempField = f;
                }
                field.terrain = oldTerrain;
                map.setField(field);
                return true;
            }
            field.terrain = oldTerrain;
            map.setField(field);
            return false;
    }

    public static boolean isHalfMapValid(HalfMap map) {
        GameMap gameMap = MapConverter.generateHalfMap(map);
        if(gameMap.getColumns() != 8 || gameMap.getRows() != 4) {
            return false;
        }

        if(!gameMap.getFieldsByTerrain(GameMap.Terrain.Unassigned).isEmpty()) {
            return false;
        }

        var waterFields = gameMap.getAllFields().stream()
                .filter(field -> field.terrain == GameMap.Terrain.Water).collect(Collectors.toList());
        var mountainFields = gameMap.getAllFields().stream()
                .filter(field -> field.terrain == GameMap.Terrain.Mountain).collect(Collectors.toList());
        var castleField = gameMap.getAllFields().stream()
                .filter(field -> field.gameFortState == GameMap.GameFortState.MyFortPresent).findFirst().orElse(null);

        if(castleField == null || castleField.terrain != GameMap.Terrain.Grass) {
            return false;
        }

        if(waterFields.size() < MIN_WATER_FIELDS || mountainFields.size() < MIN_MOUNTAIN_FIELDS) {
            return false;
        }

        if(waterFields.stream().anyMatch(gameMap::areThereNeighbouringIslandsForWaterAt) ||
                waterFields.stream().anyMatch(field -> !isBorderWaterConditionMet(gameMap, field)) ||
                waterFields.stream().anyMatch(field -> doesWaterFieldCreateSeparation(gameMap, field))) {
            return false;
        }

        LOGGER.info("Halfmap is valid!");
        return true;
    }


}
