package sample.model.node.spawn;


import sample.model.car.Car;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SpawnNodeHolder {
    private  static Map<SpawnStreamId, SpawnStream> spawnStreams  = new ConcurrentHashMap<>();

    public synchronized static Car spawnCar(final SpawnStreamId id) {
        return spawnStreams.get(id).spawnCar();
    }

    public static void addToSpawnStrem(SpawnCarNode spawnCarNode) {
        spawnStreams.putIfAbsent(spawnCarNode.getSpawnStreamId(),
                SpawnStream.of(spawnCarNode.getSpawnStreamId(), new ArrayList<>()));
        spawnStreams.get(spawnCarNode.getSpawnStreamId())
                .getNodes()
                .add(spawnCarNode);
    }
    public static void clear() {
        spawnStreams.clear();
    }
}
