 package sample.service;





import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sample.dto.NodeDto;
import sample.dto.SimulationResponse;
import sample.model.car.Car;
import sample.model.car.CarHolder;
import sample.model.node.Node;
import sample.model.node.NodePosition;
import sample.model.node.NodeType;
import sample.model.node.SpawnCarNode;
import sample.model.node.spawn.SpawnNodeHolder;
import sample.model.node.spawn.SpawnStreamId;
import sample.service.NodeMapper;
import sample.service.SimulationInfo;
import sample.service.SimulationService;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
public class SimulationServiceImpl implements SimulationService {
    private boolean isSimulating;
    private SimulationInfo simulationInfo;
    private List<Disposable> spawnDisposables;
    private List<Disposable> traffigLightsDisposables;
    private Set<NodeDto> dtos;

    @Autowired
    public SimulationServiceImpl(Set<NodeDto> boardDao) {
        this.dtos = boardDao;
    }

    @Override
    public void startSimulation() {
        isSimulating = true;
        setSpawnStreams();
        Thread thread = new Thread(()->
        {while (isSimulating) {
            CarHolder.getAllCars().forEach(Car::move);
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        }
        );
        thread.start();


    }

    @Override
    public void changeSimulationInfo(final SimulationInfo simulationInfo) {
        this.simulationInfo = simulationInfo;
        setSpawnStreams();
    }

    private void setSpawnStreams() {
        Function<Map.Entry<SpawnStreamId, Integer>, Observable<Long>> mapToObesrvable;
        mapToObesrvable = entry -> Observable.interval(4,
                TimeUnit.SECONDS);
        // mały potworek XD aby  tworzyc samochód, i dodawać go do holdera
        if (spawnDisposables != null) spawnDisposables.forEach(Disposable::dispose);
        this.spawnDisposables = simulationInfo.getStreamProduction().entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, mapToObesrvable))
                .entrySet()
                .stream()
                .map(entry -> entry.getValue().subscribe(aLong -> spawnCars(entry.getKey())))
                .collect(Collectors.toList());


    }


    @Override
    public void stopSimulation() {

        isSimulating = false;
        spawnDisposables.forEach(Disposable::dispose);
    }

    @Override
    public void init(){
        //clearCache();
        getNodes();
    }


    private void getNodes() {
        val nodes = NodeMapper.toDomain(dtos);
        nodes.stream()
                .filter(node -> NodeType.SPAWN.equals(node.getType()))
                .forEach(node -> {
                    val spawnNode = (SpawnCarNode) node;
                    SpawnNodeHolder.addToSpawnStrem(spawnNode);
                });
    }


    private void spawnCars(final SpawnStreamId id) {
        val car = SpawnNodeHolder.spawnCar(id);
        CarHolder.addCar(car);
    }

    @Override
    public Map<String, NodePosition> getNewCarPosition() {
        val mapOfPostions = CarHolder.getAllCars()
                .collect(Collectors.toMap(e -> e.getId().getId(), this::averagePosition));
        return mapOfPostions;

    }

    @Override
    public boolean isSimulating() {
        return isSimulating;
    }


    //Todo to powinno byc w klasie Car
    private NodePosition averagePosition(final Car car) {
        Set<NodePosition> nodePositionSet = new HashSet<>();
        int size = car.getSize();
        Node node = car.getHead();
        for (int i = 0; i < size; i++) {
            nodePositionSet.add(node.getPosition());
            node = node.getNeighbors().getLeft();
        }
        val horizontal = nodePositionSet.stream()
                .mapToDouble(NodePosition::getHoriziontalPosition)
                .average()
                .getAsDouble();
        val vertical = nodePositionSet.stream()
                .mapToDouble(NodePosition::getVerticalPosition)
                .average()
                .getAsDouble();
        return NodePosition.of(horizontal, vertical);

    }

    private void clearCache() {
        CarHolder.clear();
        SpawnNodeHolder.clear();
    }
}
