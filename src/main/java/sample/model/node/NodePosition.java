package sample.model.node;

import lombok.AllArgsConstructor;

@AllArgsConstructor(staticName = "of")
public class NodePosition {
    private final Double horiziontalPosition;
    private final Double verticalPosition;

    public Double getHoriziontalPosition() {
        return this.horiziontalPosition;
    }

    public Double getVerticalPosition() {
        return this.verticalPosition;
    }
}
