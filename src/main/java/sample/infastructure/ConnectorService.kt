@file:Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")

package sample.infastructure

import sample.dto.NodeDto
import sample.model.node.NodeDirection


fun setNext(node: NodeDto, nextNodeDto: NodeDto?) = when(node.direction) {
    NodeDirection.UP ->if (node.topId == null) node.bottomId = nextNodeDto?.nodeId else{}
    NodeDirection.DOWN ->if (node.bottomId == null) node.topId = nextNodeDto?.nodeId else{}
    NodeDirection.LEFT -> if (node.leftId == null) node.leftId = nextNodeDto?.nodeId else{}
    NodeDirection.RIGHT -> if (node.rightId == null) node.rightId = nextNodeDto?.nodeId else{}

}

 fun setNeighboursToConnectorNode(node: NodeDto, connectorHelper: ConnectorHelper) = when (node.direction) {
    NodeDirection.UP -> setNeighboursToNodeWithUpDirection(node, connectorHelper)
    NodeDirection.DOWN -> setNeighboursToNodeWithDownDirection(node, connectorHelper)
    NodeDirection.LEFT -> setNeighboursToNodeWithLeftDirection(node, connectorHelper)
    NodeDirection.RIGHT -> setNeighboursToNodeWithRightDirection(node, connectorHelper)

}

private fun setNeighboursToNodeWithLeftDirection(node: NodeDto, connectorHelper: ConnectorHelper) {
    node.leftId = connectorHelper.firstId
    node.topId = connectorHelper.secondId
    node.bottomId = connectorHelper.thirdId
}

private fun setNeighboursToNodeWithRightDirection(node: NodeDto, connectorHelper: ConnectorHelper) {
    node.rightId = connectorHelper.firstId
    node.topId = connectorHelper.secondId
    node.bottomId = connectorHelper.thirdId
}

private fun setNeighboursToNodeWithUpDirection(node: NodeDto, connectorHelper: ConnectorHelper) {
    node.rightId = connectorHelper.secondId
    node.topId = connectorHelper.firstId
    node.leftId = connectorHelper.thirdId
}


private fun setNeighboursToNodeWithDownDirection(node: NodeDto, connectorHelper: ConnectorHelper) {
    node.rightId = connectorHelper.secondId
    node.leftId = connectorHelper.thirdId
    node.bottomId = connectorHelper.firstId
}




