package sample.model.node;

import lombok.Getter;
import lombok.Setter;
import sample.model.car.CarId;

import javax.validation.constraints.NotNull;


@Getter
@Setter
public abstract class Node {
    @NotNull
    private NodeType type;

    @NotNull
    private NodeId id;

    @NotNull
    private NodeDirection direction;

    @NotNull
    private Boolean isTaken;

    private Neighbors neighbors;

    private Double maxSpeedAllowed;

    @NotNull
    private NodePosition position;

    private CarId carId;

    public Node(@NotNull NodeType type,
                @NotNull NodeId id,
                @NotNull Boolean isTaken,
                Neighbors neighbors,
                Double maxSpeedAllowed,
                @NotNull NodePosition position,
                CarId carId,
                @NotNull NodeDirection direction) {
        this.type = type;
        this.id = id;
        this.isTaken = false;
        this.neighbors = neighbors;
        this.maxSpeedAllowed = maxSpeedAllowed;
        this.position = position;
        this.carId = carId;
        this.direction = direction;
    }
}
