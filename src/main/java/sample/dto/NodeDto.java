package sample.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@Setter
public class NodeDto {
    public NodeType nodeType;
    public NodeDirection direction;
    public String leftId;
    public String rightId;
    public String topId;
    public String bottomId;
    public String spawnStreamId; // czy to dostane?
    public Double maxSpeedAllowed;
    public Double verticalPosition;
    public Double horizontalPosition;
    public String nodeId;

    public static NodeDtoBuilder builder() {
        return new NodeDtoBuilder();
    }

    public static class NodeDtoBuilder {
        private NodeType type;
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

        public NodeType getType() {
            return type;
        }

        public NodeDirection getDirection() {
            return direction;
        }

        public String getLeftId() {
            return leftId;
        }

        public String getRightId() {
            return rightId;
        }

        public String getTopId() {
            return topId;
        }

        public String getBottomId() {
            return bottomId;
        }

        public String getSpawnStreamId() {
            return spawnStreamId;
        }

        public Double getMaxSpeedAllowed() {
            return maxSpeedAllowed;
        }

        public Double getVerticalPosition() {
            return verticalPosition;
        }

        public Double getHorizontalPosition() {
            return horizontalPosition;
        }

        public String getNodeId() {
            return nodeId;
        }

        public NodeDtoBuilder type(NodeType type) {
            this.type = type;
            return this;
        }

        public NodeDtoBuilder direction(NodeDirection direction) {
            this.direction = direction;
            return this;
        }

        public NodeDtoBuilder leftId(String leftId) {
            this.leftId = leftId;
            return this;
        }

        public NodeDtoBuilder rightId(String rightId) {
            this.rightId = rightId;
            return this;
        }

        public NodeDtoBuilder topId(String topId) {
            this.topId = topId;
            return this;
        }

        public NodeDtoBuilder bottomId(String bottomId) {
            this.bottomId = bottomId;
            return this;
        }

        public NodeDtoBuilder spawnStreamId(String spawnStreamId) {
            this.spawnStreamId = spawnStreamId;
            return this;
        }

        public NodeDtoBuilder maxSpeedAllowed(Double maxSpeedAllowed) {
            this.maxSpeedAllowed = maxSpeedAllowed;
            return this;
        }

        public NodeDtoBuilder verticalPosition(Double verticalPosition) {
            this.verticalPosition = verticalPosition;
            return this;
        }

        public NodeDtoBuilder horizontalPosition(Double horizontalPosition) {
            this.horizontalPosition = horizontalPosition;
            return this;
        }

        public NodeDtoBuilder nodeId(String nodeId) {
            this.nodeId = nodeId;
            return this;
        }

        public NodeDto build() {
            return new NodeDto(type, direction, leftId, rightId, topId, bottomId, spawnStreamId, maxSpeedAllowed, verticalPosition, horizontalPosition, nodeId);
        }

        public String toString() {
            return "NodeDto.NodeDtoBuilder(nodeType=" + this.type + ", direction=" + this.direction + ", leftId=" + this.leftId + ", rightId=" + this.rightId + ", topId=" + this.topId + ", bottomId=" + this.bottomId + ", spawnStreamId=" + this.spawnStreamId + ", maxSpeedAllowed=" + this.maxSpeedAllowed + ", verticalPosition=" + this.verticalPosition + ", horizontalPosition=" + this.horizontalPosition + ", nodeId=" + this.nodeId + ")";
        }
    }
}
