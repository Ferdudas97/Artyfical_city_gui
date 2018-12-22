package sample.dto;

import sample.model.node.spawn.SpawnStreamId;

import java.util.Map;


public class SimulationInfo {
    // ile dany spawnStream ma produkować na minute
    private Map<SpawnStreamId, Integer> streamProduction;
    private Integer simulationSpeed;

    @java.beans.ConstructorProperties({"streamProduction", "simulationSpeed"})
    private SimulationInfo(Map<SpawnStreamId, Integer> streamProduction, Integer simulationSpeed) {
        this.streamProduction = streamProduction;
        this.simulationSpeed = simulationSpeed;
    }

    public static SimulationInfo of(Map<SpawnStreamId, Integer> streamProduction, Integer simulationSpeed) {
        return new SimulationInfo(streamProduction, simulationSpeed);
    }

    public Map<SpawnStreamId, Integer> getStreamProduction() {
        return this.streamProduction;
    }

    public Integer getSimulationSpeed() {
        return this.simulationSpeed;
    }
}
