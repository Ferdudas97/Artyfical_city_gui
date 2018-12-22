package sample.model.node.lights;

import javafx.scene.paint.Color;
import sample.model.node.NodeId;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TrafficLightsHolder {

    private  static Map<NodeId, TrafficLightsNode> lights  = new ConcurrentHashMap<>();

    public synchronized static Color changeLight(final NodeId id) {
        return lights.get(id).changeLight();
    }

    public static void addTrafficLight(TrafficLightsNode node) {
        lights.putIfAbsent(node.getId(),node);
        System.out.println(node.getId().getId());
    }
    public static void clear() {
        lights.clear();
    }
}
