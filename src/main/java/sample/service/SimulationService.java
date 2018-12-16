package sample.service;


import sample.dto.SimulationResponse;
import sample.model.car.CarId;
import sample.model.node.NodePosition;

import java.util.Map;

public interface SimulationService {

    void startSimulation();
    void changeSimulationInfo(SimulationInfo simulationInfo);
    void stopSimulation();
    void init();
    Map<String, NodePosition> getNewCarPosition();
    boolean isSimulating();

}
