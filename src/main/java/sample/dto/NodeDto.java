package sample.dto;

public class NodeDto {
    private NodeType nodeType;
    private NodeDirection direction;
    private String leftId;
    private String rightId;
    private String topId;
    private String bottomId;
    private String spawnStreamId; // czy to dostane?
    private Double maxSpeedAllowed;
    private Double verticalPosition;
    private Double horizontalPosition;
    private String nodeId;

    @java.beans.ConstructorProperties({"nodeType", "direction", "leftId", "rightId", "topId", "bottomId", "spawnStreamId", "maxSpeedAllowed", "verticalPosition", "horizontalPosition", "nodeId"})
    public NodeDto(NodeType nodeType, NodeDirection direction, String leftId, String rightId, String topId, String bottomId, String spawnStreamId, Double maxSpeedAllowed, Double verticalPosition, Double horizontalPosition, String nodeId) {
        this.nodeType = nodeType;
        this.direction = direction;
        this.leftId = leftId;
        this.rightId = rightId;
        this.topId = topId;
        this.bottomId = bottomId;
        this.spawnStreamId = spawnStreamId;
        this.maxSpeedAllowed = maxSpeedAllowed;
        this.verticalPosition = verticalPosition;
        this.horizontalPosition = horizontalPosition;
        this.nodeId = nodeId;
    }

    public NodeDto() {
    }

    public static NodeDtoBuilder builder() {
        return new NodeDtoBuilder();
    }

    public NodeType getNodeType() {
        return this.nodeType;
    }

    public NodeDirection getDirection() {
        return this.direction;
    }

    public String getLeftId() {
        return this.leftId;
    }

    public String getRightId() {
        return this.rightId;
    }

    public String getTopId() {
        return this.topId;
    }

    public String getBottomId() {
        return this.bottomId;
    }

    public String getSpawnStreamId() {
        return this.spawnStreamId;
    }

    public Double getMaxSpeedAllowed() {
        return this.maxSpeedAllowed;
    }

    public Double getVerticalPosition() {
        return this.verticalPosition;
    }

    public Double getHorizontalPosition() {
        return this.horizontalPosition;
    }

    public String getNodeId() {
        return this.nodeId;
    }

    public void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    public void setDirection(NodeDirection direction) {
        this.direction = direction;
    }

    public void setLeftId(String leftId) {
        this.leftId = leftId;
    }

    public void setRightId(String rightId) {
        this.rightId = rightId;
    }

    public void setTopId(String topId) {
        this.topId = topId;
    }

    public void setBottomId(String bottomId) {
        this.bottomId = bottomId;
    }

    public void setSpawnStreamId(String spawnStreamId) {
        this.spawnStreamId = spawnStreamId;
    }

    public void setMaxSpeedAllowed(Double maxSpeedAllowed) {
        this.maxSpeedAllowed = maxSpeedAllowed;
    }

    public void setVerticalPosition(Double verticalPosition) {
        this.verticalPosition = verticalPosition;
    }

    public void setHorizontalPosition(Double horizontalPosition) {
        this.horizontalPosition = horizontalPosition;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public static class NodeDtoBuilder {
        private NodeType nodeType;
        private NodeDirection direction;
        private String leftId;
        private String rightId;
        private String topId;
        private String bottomId;
        private String spawnStreamId;
        private Double maxSpeedAllowed;
        private Double verticalPosition;
        private Double horizontalPosition;
        private String nodeId;

        NodeDtoBuilder() {
        }

        public NodeDto.NodeDtoBuilder nodeType(NodeType nodeType) {
            this.nodeType = nodeType;
            return this;
        }

        public NodeDto.NodeDtoBuilder direction(NodeDirection direction) {
            this.direction = direction;
            return this;
        }

        public NodeDto.NodeDtoBuilder leftId(String leftId) {
            this.leftId = leftId;
            return this;
        }

        public NodeDto.NodeDtoBuilder rightId(String rightId) {
            this.rightId = rightId;
            return this;
        }

        public NodeDto.NodeDtoBuilder topId(String topId) {
            this.topId = topId;
            return this;
        }

        public NodeDto.NodeDtoBuilder bottomId(String bottomId) {
            this.bottomId = bottomId;
            return this;
        }

        public NodeDto.NodeDtoBuilder spawnStreamId(String spawnStreamId) {
            this.spawnStreamId = spawnStreamId;
            return this;
        }

        public NodeDto.NodeDtoBuilder maxSpeedAllowed(Double maxSpeedAllowed) {
            this.maxSpeedAllowed = maxSpeedAllowed;
            return this;
        }

        public NodeDto.NodeDtoBuilder verticalPosition(Double verticalPosition) {
            this.verticalPosition = verticalPosition;
            return this;
        }

        public NodeDto.NodeDtoBuilder horizontalPosition(Double horizontalPosition) {
            this.horizontalPosition = horizontalPosition;
            return this;
        }

        public NodeDto.NodeDtoBuilder nodeId(String nodeId) {
            this.nodeId = nodeId;
            return this;
        }

        public NodeDto build() {
            return new NodeDto(nodeType, direction, leftId, rightId, topId, bottomId, spawnStreamId, maxSpeedAllowed, verticalPosition, horizontalPosition, nodeId);
        }

        public String toString() {
            return "NodeDto.NodeDtoBuilder(nodeType=" + this.nodeType + ", direction=" + this.direction + ", leftId=" + this.leftId + ", rightId=" + this.rightId + ", topId=" + this.topId + ", bottomId=" + this.bottomId + ", spawnStreamId=" + this.spawnStreamId + ", maxSpeedAllowed=" + this.maxSpeedAllowed + ", verticalPosition=" + this.verticalPosition + ", horizontalPosition=" + this.horizontalPosition + ", nodeId=" + this.nodeId + ")";
        }
    }
}
