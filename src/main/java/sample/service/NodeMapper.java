package sample.service;

import lombok.val;
import sample.dto.NodeDto;
import sample.model.node.*;
import sample.model.node.spawn.SpawnStreamId;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class NodeMapper {
    private static Map<NodeType, Function<NodeDto, Node>> specifiedMappers;

    @FunctionalInterface
    private interface NeighboursSetter {
        void setNeighbours(final NodeDto entity, final Map<String, Node> map);
    }

    private static Map<NodeDirection, NeighboursSetter> neighboursSetterMap;

    static {
        specifiedMappers = new HashMap<>();
        specifiedMappers.putIfAbsent(NodeType.ROAD, NodeMapper::toRoadNode);
        specifiedMappers.putIfAbsent(NodeType.SPAWN, NodeMapper::toSpawnNode);

        neighboursSetterMap = new HashMap<>();
        neighboursSetterMap.put(NodeDirection.LEFT, NodeMapper::setNeighboursWithLeftDirection);
        neighboursSetterMap.put(NodeDirection.RIGHT, NodeMapper::setNeighboursWithRightDirection);
        neighboursSetterMap.put(NodeDirection.UP, NodeMapper::setNeighboursWithTopDirection);
        neighboursSetterMap.put(NodeDirection.DOWN, NodeMapper::setNeighboursWithDownDirection);
    }


    public static Set<Node> toDomain(final Set<NodeDto> nodeDtos) {
        val mapWithNodes =
                nodeDtos.stream().map(entity -> specifiedMappers.get(entity.getNodeType()).apply(entity))
                        .collect(Collectors.toMap(e -> e.getId().getId(), Function.identity()));

        nodeDtos.forEach(entity -> neighboursSetterMap.get(entity.getDirection()).setNeighbours(entity, mapWithNodes));
        return new HashSet<>(mapWithNodes.values());
    }





    private static Node toRoadNode(final NodeDto dto) {
        return RoadNode.builder()
                .id(NodeId.of(dto.getNodeId()))
                .type(dto.getNodeType())
                .maxSpeedAllowed(dto.getMaxSpeedAllowed())
                .direction(dto.getDirection())
                .position(NodePosition.of(dto.getHorizontalPosition(), dto.getVerticalPosition()))
                .build();
    }

    private static Node toSpawnNode(final NodeDto dto) {
        return SpawnCarNode.builder()
                .id(NodeId.of(dto.getNodeId()))
                .spawnStreamId(SpawnStreamId.of(dto.getSpawnStreamId()))
                .type(dto.getNodeType())
                .direction(dto.getDirection())
                .maxSpeedAllowed(dto.getMaxSpeedAllowed())
                .position(NodePosition.of(dto.getHorizontalPosition(), dto.getVerticalPosition()))
                .build();
    }


    private static void setNeighboursWithLeftDirection(final NodeDto entity, final Map<String, Node> map) {
        map.get(entity.getNodeId())
                .setNeighbors(Neighbors.of(
                        getNodeWithTheSameDirection(map, entity.getDirection(), entity.getBottomId()),
                        getNodeWithTheSameDirection(map, entity.getDirection(), entity.getRightId()),
                        getNodeWithTheSameDirection(map, entity.getDirection(), entity.getLeftId()),
                        getNodeWithTheSameDirection(map, entity.getDirection(), entity.getTopId())));
    }

    private static void setNeighboursWithRightDirection(final NodeDto entity, final Map<String, Node> map) {
        map.get(entity.getNodeId())
                .setNeighbors(Neighbors.of(
                        getNodeWithTheSameDirection(map, entity.getDirection(), entity.getTopId()),
                        getNodeWithTheSameDirection(map, entity.getDirection(), entity.getLeftId()),
                        getNodeWithTheSameDirection(map, entity.getDirection(), entity.getRightId()),
                        getNodeWithTheSameDirection(map, entity.getDirection(), entity.getBottomId())));
    }

    private static void setNeighboursWithTopDirection(final NodeDto entity, final Map<String, Node> map) {
        map.get(entity.getNodeId())
                .setNeighbors(Neighbors.of(
                        getNodeWithTheSameDirection(map, entity.getDirection(), entity.getLeftId()),
                        getNodeWithTheSameDirection(map, entity.getDirection(), entity.getBottomId()),
                        getNodeWithTheSameDirection(map, entity.getDirection(), entity.getTopId()),
                        getNodeWithTheSameDirection(map, entity.getDirection(), entity.getRightId())));
    }


    private static void setNeighboursWithDownDirection(final NodeDto entity, final Map<String, Node> map) {
        map.get(entity.getNodeId())
                .setNeighbors(Neighbors.of(
                        getNodeWithTheSameDirection(map, entity.getDirection(), entity.getRightId()),
                        getNodeWithTheSameDirection(map, entity.getDirection(), entity.getTopId()),
                        getNodeWithTheSameDirection(map, entity.getDirection(), entity.getBottomId()),
                        getNodeWithTheSameDirection(map, entity.getDirection(), entity.getLeftId())));
    }

    private static Node getNodeWithTheSameDirection(final Map<String, Node> map,
                                                    final NodeDirection direction,
                                                    final String nodeId) {
        val node = map.get(nodeId);
        if (node == null) return null;
        return direction.equals(node.getDirection()) ? node : null;

    }
}
