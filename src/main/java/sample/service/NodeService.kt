package sample.service

import sample.dto.NodeDto
import sample.model.node.NodeDirection
import sample.model.node.NodeType
import sample.infastructure.setNext

class NodeService(var nodeSize: Double) {
    private val nodeMap = hashMapOf<Pair<Double, Double>, NodeDto>()
    fun deleteNodes() = nodeMap.clear()
    fun addNode(node: NodeDto) {
        nodeMap[Pair(node.horizontalPosition, node.verticalPosition)] = node
    }

    fun getNode(x: Double, y: Double) = nodeMap[Pair(x, y)]
    fun getNode(id: String) = nodeMap.values.find { it.nodeId == id }
    fun getAllNodes() = nodeMap.values.toSet()
    fun clearNodes() = nodeMap.clear()

    fun putNeighboors() {
        nodeMap.values.filter { it.nodeType != NodeType.CONNECTOR }.forEach { node ->
            node.topId = nodeMap[Pair(node.horizontalPosition, node.verticalPosition + nodeSize)]?.nodeId
            node.bottomId = nodeMap[Pair(node.horizontalPosition, node.verticalPosition - nodeSize)]?.nodeId
            node.leftId = nodeMap[Pair(node.horizontalPosition - nodeSize, node.verticalPosition)]?.nodeId
            node.rightId = nodeMap[Pair(node.horizontalPosition + nodeSize, node.verticalPosition)]?.nodeId
        }
        nodeMap.values.filter { it.nodeType == NodeType.CONNECTOR }
                .forEach {
                    setNext(it, getNextNode(node = it, delta = getDeltaToNextHorizontalNode(it.direction)))
                }
    }

    fun setSpawnStreams() {
        var id = 0;
        nodeMap.values.filter { node -> (node.nodeType == NodeType.SPAWN) }
                .forEach { node -> setSpawnStreamId(node, id++) }
    }

    private fun setSpawnStreamId(node: NodeDto, streamId: Int) {
        val delta = getDeltaToNextVerticalNode(node.direction)
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


    private fun getDeltaToNextVerticalNode(direction: NodeDirection): Pair<Double, Double> = when (direction) {
        NodeDirection.RIGHT -> Pair(0.0, nodeSize)
        NodeDirection.LEFT -> Pair(0.0, nodeSize)
        NodeDirection.UP -> Pair(nodeSize, 0.0)
        NodeDirection.DOWN -> Pair(nodeSize, 0.0)


    }

    private fun getDeltaToNextHorizontalNode(direction: NodeDirection): Pair<Double, Double> = when (direction) {
        NodeDirection.RIGHT ->  Pair(nodeSize, 0.0)
        NodeDirection.LEFT -> Pair(-nodeSize, 0.0)
        NodeDirection.DOWN  ->  Pair(0.0, nodeSize)
        NodeDirection.UP ->   Pair(0.0, -nodeSize)


    }

    private fun checkSpawnDirection(direction: NodeDirection, comparedNode: NodeDto) =
            comparedNode.nodeType == NodeType.SPAWN && comparedNode.direction == direction

}