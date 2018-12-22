package sample.model.node.lights;

import javafx.scene.paint.Color;
import lombok.Builder;
import lombok.var;
import sample.model.car.CarId;
import sample.model.node.*;

import javax.validation.constraints.NotNull;
import java.util.function.Function;

public class TrafficLightsNode extends Node {

    private Boolean isGreen = true;

    public Color changeLight() {
        if (isGreen) {
            return redLight();
        } else return greenLight();
    }

    private Color greenLight() {
        changeFields(true,getMaxSpeedAllowed());
        isGreen=!isGreen;
        return Color.GREEN;
    }

    private Color redLight() {
        changeFields(false,0.0);
        isGreen=!isGreen;
        return Color.RED;
    }
    private void changeFields(final Boolean isTaken, final Double maxSpeed) {

        iterate(isTaken,maxSpeed, Neighbors::getTop);
        iterate(isTaken,maxSpeed, Neighbors::getRight);
        iterate(isTaken,maxSpeed, Neighbors::getBottom);
        iterate(isTaken,maxSpeed, Neighbors::getLeft);

    }


    private void iterate( final Boolean isTaken,final Double maxSpeed, final Function<Neighbors,Node> getter) {
        var node = getter.apply(getNeighbors());
        while (node != null) {
            node.setIsTaken(isTaken);
            node.setMaxSpeedAllowed(maxSpeed);
            node = getter.apply(node.getNeighbors());
        }

    }
    @Builder
    public TrafficLightsNode(@NotNull NodeType type, @NotNull NodeId id, @NotNull Boolean isTaken, Neighbors neighbors, Double maxSpeedAllowed, @NotNull NodePosition position, CarId carId, @NotNull NodeDirection direction) {
        super(type, id, isTaken, neighbors, maxSpeedAllowed, position, carId, direction);
    }
}
