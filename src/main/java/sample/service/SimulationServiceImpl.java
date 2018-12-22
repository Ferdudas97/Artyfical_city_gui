package sample.service;


import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import javafx.scene.paint.Color;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sample.dto.NodeDto;
import sample.infastructure.NodeMapper;
import sample.model.car.Car;
import sample.model.car.CarHolder;
import sample.model.common.Pair;
import sample.model.node.*;
import sample.model.node.lights.TrafficLightsHolder;
import sample.model.node.lights.TrafficLightsNode;
import sample.model.node.spawn.SpawnNodeHolder;
import sample.model.node.spawn.SpawnStreamId;
import sample.dto.SimulationInfo;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
public class SimulationServiceImpl implements SimulationService {
    private boolean isSimulating;
    private SimulationInfo simulationInfo;
    private List<Disposable> spawnDisposables;
    private List<Disposable> traffigLightsDisposables = new ArrayList<>();
    private List<Disposable> carMover = new ArrayList<>();
    private Set<NodeDto> dtos;
    private Boolean stopped;

    @Autowired
    public SimulationServiceImpl(Set<NodeDto> boardDao) {
        this.dtos = boardDao;
    }

    @Override
    public Observable<Long> startSimulation() {
        isSimulating = !isSimulating;
        setSpawnStreams();

        Observable<Long> dataStream =
                Observable.interval(0, 1 * 1000 / simulationInfo.getSimulationSpeed(), TimeUnit.MILLISECONDS).subscribeOn(Schedulers.computation());
        carMover.add(dataStream.subscribe(v -> {
            CarHolder.getAllCars().forEach(Car::move);
        }));

        return dataStream;
    }

    @Override
    public void changeSimulationInfo(final SimulationInfo simulationInfo) {
        this.simulationInfo = simulationInfo;
        // setSpawnStreams();
    }

    private void setSpawnStreams() {
        Function<Map.Entry<SpawnStreamId, Integer>, Observable<Long>> mapToObesrvable;
        mapToObesrvable = entry -> Observable.interval(0, 1000 * 60 / entry.getValue() / simulationInfo.getSimulationSpeed(), TimeUnit.MILLISECONDS).subscribeOn(Schedulers.computation());
        // mały potworek XD aby  tworzyc samochód, i dodawać go do holdera
        if (spawnDisposables != null) spawnDisposables.forEach(Disposable::dispose);
        this.spawnDisposables = simulationInfo.getStreamProduction().entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, mapToObesrvable))
                .entrySet()
                .stream()
                .map(entry -> entry.getValue().subscribe(aLong -> spawnCars(entry.getKey())))
                .collect(Collectors.toList());

//            spawnCars(simulationInfo.getStreamProduction().keySet().stream().findFirst().get());


    }


    @Override
    public void stopSimulation() {

        isSimulating = !isSimulating;
        spawnDisposables.forEach(Disposable::dispose);
        spawnDisposables.clear();
        carMover.forEach(Disposable::dispose);
        carMover.clear();
    }

    @Override
    public void init() {
        //clearCache();
        getNodes();
    }


    private synchronized void getNodes() {
        val nodes = NodeMapper.toDomain(dtos);
        nodes.parallelStream()
                .filter(node -> NodeType.SPAWN.equals(node.getType()))
                .forEach(node -> {
                    val spawnNode = (SpawnCarNode) node;
                    SpawnNodeHolder.addToSpawnStrem(spawnNode);
                });
        nodes.parallelStream()
                .filter(node -> NodeType.LIGHTS.equals(node.getType()))
                .forEach(node -> {
                    val lightsNode = (TrafficLightsNode) node;
                    TrafficLightsHolder.addTrafficLight(lightsNode);
                });
    }


    private synchronized void spawnCars(final SpawnStreamId id) {
        val car = SpawnNodeHolder.spawnCar(id);
        CarHolder.addCar(car);
    }

    @Override
    public Map<String, Pair<NodePosition, Integer>> headAndSizes() {

        return CarHolder.getAllCars().filter(car -> car.getHead() != null).collect(Collectors.toMap(e -> e.getId().getId(), e -> Pair.of(e
                .getHead().getPosition(), e.getSize())));
    }

    @Override
    public Color changeLight(NodeId nodeId) {
        return TrafficLightsHolder.changeLight(nodeId);
    }

    @Override
    public boolean isSimulating() {
        return isSimulating;
    }


    private void clearCache() {
        CarHolder.clear();
        SpawnNodeHolder.clear();
        TrafficLightsHolder.clear();
    }



}
