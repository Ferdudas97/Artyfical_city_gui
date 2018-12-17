package sample.model.node;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import sample.model.car.Car;
import sample.model.car.CarFactory;
import sample.model.car.CarId;
import sample.model.car.CarType;
import sample.model.node.spawn.SpawnStreamId;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class SpawnCarNode extends Node {

    private SpawnStreamId spawnStreamId;
    public Car spawnCar() {
        return CarFactory.creatorMap.get(CarType.MEDIUM).apply(this);
    }

    @Builder
    public SpawnCarNode(@NotNull NodeType type,
                        @NotNull NodeId id,
                        @NotNull Boolean isTaken,
                        Neighbors neighbors,
                        Double maxSpeedAllowed,
                        @NotNull NodePosition position,
                        CarId carId,
                        SpawnStreamId spawnStreamId,
                        @NotNull NodeDirection direction) {
        super(type, id, isTaken, neighbors, maxSpeedAllowed, position, carId, direction);
        this.spawnStreamId = spawnStreamId;
    }
}
