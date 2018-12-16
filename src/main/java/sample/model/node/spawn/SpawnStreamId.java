package sample.model.node.spawn;


public class SpawnStreamId {
    private String id;

    @java.beans.ConstructorProperties({"id"})
    private SpawnStreamId(String id) {
        this.id = id;
    }

    public static SpawnStreamId of(String id) {
        return new SpawnStreamId(id);
    }

    public String getId() {
        return this.id;
    }
}
