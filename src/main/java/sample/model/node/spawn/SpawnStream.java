package sample.model.node.spawn;

import lombok.AllArgsConstructor;
import lombok.Getter;
import sample.model.car.Car;
import sample.model.car.CarType;
import sample.model.node.SpawnCarNode;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "of")
@Getter
public class SpawnStream {
    private final SpawnStreamId id;
    private final List<SpawnCarNode> nodes;
    public Car spawnCar() {
        return nodes.stream().filter(node -> !node.getIsTaken())
                .collect(Collectors.collectingAndThen(Collectors.toList(), collected -> {
                    Collections.shuffle(collected);
                    return collected.stream();
                }))
                .limit(1)
                .peek(node -> node.setIsTaken(true))
                .map(SpawnCarNode::spawnCar)
                .findFirst()
                .orElse(null);
    }


}
