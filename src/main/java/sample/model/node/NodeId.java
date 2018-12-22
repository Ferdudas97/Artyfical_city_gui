package sample.model.node;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class NodeId {
    private final String id;

    @java.beans.ConstructorProperties({"id"})
    private NodeId(String id) {
        this.id = id;
    }

    public static NodeId of(String id) {
        return new NodeId(id);
    }
}
