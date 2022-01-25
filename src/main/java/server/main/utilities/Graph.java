package server.main.utilities;

import MessagesBase.EMove;

import java.util.HashMap;
import java.util.List;
import java.util.Arrays;

public class Graph {
    private static List<EMove> allMoves = Arrays.asList(EMove.values());
    private HashMap<String, Node> nodes = new HashMap<>();

    public class Edge{
        public int weight;
        public Node destinationNode;

        public Edge(int weight, Node destinationNode) {
            this.weight = weight;
            this.destinationNode = destinationNode;
        }
    }
    public class Node {
        public GameMap.GameField field;
        public HashMap<EMove, Edge> edges;
        public Node(GameMap.GameField field){
            this.field = field;
            edges = new HashMap<>();
        }

    }

    private GameMap map;

    public Graph(GameMap map) {
        this.map = map;
        for(GameMap.GameField field : map.getAllFields()) {
            Node node = new Node(field);
            nodes.put(field.x + "-" + field.y, node);
            for(EMove move : allMoves) {
                GameMap.GameField neighbouringField = map.getNeighbouringFieldForDirection(field, move);
                if(neighbouringField != null) {
                    int weight = calculateWeightBetween(field, neighbouringField);
                    Edge edge = new Edge(weight, new Node(neighbouringField));
                    node.edges.put(move, edge);
                }
            }
        }
    }

    private int calculateWeightBetween(GameMap.GameField field1, GameMap.GameField field2) {
        int result = 0;
        List<GameMap.GameField> fields = List.of(field1, field2);
        for(GameMap.GameField field : fields) {
            switch (field.terrain) {
                case Grass:
                    result += GameMap.grassSteps;
                    break;
                case Mountain:
                    result += GameMap.mountainSteps;
                    break;
                case Water:
                    result += GameMap.waterSteps;
            }
        }
        return result;
    }

    public List<Node> getNodes()
    {
        return List.copyOf(nodes.values());
    }
}
