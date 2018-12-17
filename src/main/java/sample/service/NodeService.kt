package sample.service

import sample.dto.NodeDto
import sample.model.node.NodeDirection
import sample.model.node.NodeType

class NodeService(var nodeSize: Double) {
    val nodeMap = hashMapOf<Pair<Double, Double>, NodeDto>()
    fun deleteNodes() = nodeMap.clear()
    fun addNode(node: NodeDto) {
        nodeMap[Pair(node.horizontalPosition, node.verticalPosition)] = node
    }

    fun getNode(x: Double, y: Double) = nodeMap[Pair(x, y)]

    fun getAllNodes() = nodeMap.values.toSet()
    fun putNeighboors() {
        nodeMap.values.forEach { node ->
            node.topId = nodeMap[Pair(node.horizontalPosition, node.verticalPosition + nodeSize)]?.nodeId
            node.bottomId = nodeMap[Pair(node.horizontalPosition, node.verticalPosition - nodeSize)]?.nodeId
            node.leftId = nodeMap[Pair(node.horizontalPosition - nodeSize, node.verticalPosition)]?.nodeId
            node.rightId = nodeMap[Pair(node.horizontalPosition + nodeSize, node.verticalPosition)]?.nodeId
        }
    }

    fun setSpawnStreams() {
        var id = 0;
        nodeMap.values.filter { node -> (node.nodeType == NodeType.SPAWN) }
                .forEach { node -> setSpawnStreamId(node, id++) }
    }

    private fun setSpawnStreamId(node: NodeDto, streamId: Int) {
        val delta = getDeltaToNextNode(node.direction)
        var nextNode = getNextNode(node, delta)
        var previousNode = getPreviousNode(node, delta)
        node.spawnStreamId = streamId.toString()
        while (nextNode != null && checkSpawnDirection(node.direction, nextNode)) {
            nextNode.spawnStreamId = streamId.toString()
            nextNode = getNextNode(nextNode, delta)
        }
        while (previousNode != null && checkSpawnDirection(node.direction, previousNode)) {
            previousNode.spawnStreamId = streamId.toString()
            previousNode = getPreviousNode(previousNode, delta)
        }
    }

    private fun getNextNode(node: NodeDto, delta: Pair<Double, Double>): NodeDto? {
        val nextHorizontal = node.horizontalPosition + delta.first
        val nextVertical = node.verticalPosition + delta.second
        return getNode(nextHorizontal, nextVertical)

    }

    private fun getPreviousNode(node: NodeDto, delta: Pair<Double, Double>): NodeDto? {
        val previousHorizontal = node.horizontalPosition - delta.first
        val previousVertical = node.verticalPosition - delta.second
        return getNode(previousHorizontal, previousVertical)
    }


    private fun getDeltaToNextNode(direction: NodeDirection): Pair<Double, Double> = when (direction) {
        NodeDirection.RIGHT -> Pair(0.0, nodeSize)
        NodeDirection.LEFT -> Pair(0.0, nodeSize)
        NodeDirection.UP -> Pair(nodeSize, 0.0)
        NodeDirection.DOWN -> Pair(nodeSize, 0.0)


    }
    private fun checkSpawnDirection(direction: NodeDirection, comparedNode: NodeDto) =
            comparedNode.nodeType == NodeType.SPAWN && comparedNode.direction == direction

}