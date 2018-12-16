package sample.service;


import io.reactivex.Observable;
import sample.dto.SimulationResponse;
import sample.model.car.CarId;
import sample.model.common.Pair;
import sample.model.node.NodePosition;

import java.util.Map;

public interface SimulationService {

    Observable<Long> startSimulation();
    void changeSimulationInfo(SimulationInfo simulationInfo);
    void stopSimulation();
    void init();
    Map<String, NodePosition> computeCarPositions();
    boolean isSimulating();
    Map<String, Pair<NodePosition,Integer>> headAndSizes();

}
