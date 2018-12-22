package sample.service;


import io.reactivex.Observable;
import javafx.scene.paint.Color;
import sample.dto.SimulationInfo;
import sample.model.common.Pair;
import sample.model.node.NodeId;
import sample.model.node.NodePosition;

import java.util.Map;

public interface SimulationService {

    Observable<Long> startSimulation();
    void changeSimulationInfo(SimulationInfo simulationInfo);
    void stopSimulation();
    void init();
    boolean isSimulating();
    Map<String, Pair<NodePosition,Integer>> headAndSizes();
    Color changeLight(NodeId nodeId);

}
