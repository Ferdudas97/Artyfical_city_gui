package sample

import sample.dto.NodeDto

class CanvasService{
    val nodeSize = 25.0
    val nodeMap = hashMapOf<Pair<Double, Double>, NodeDto>()

    fun deleteNodes() = nodeMap.clear()
    fun addNode(node: NodeDto) {
        nodeMap[Pair(node.horizontalPosition, node.verticalPosition)] = node
    }
    fun getNode(x:Double, y:Double) = nodeMap[Pair(x,y)]

}