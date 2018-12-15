package sample.service

import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.Image
import javafx.scene.paint.Color
import sample.dto.NodeDirection
import sample.dto.NodeType

class CanvasService(val gc: GraphicsContext, val nodeSize: Double) {

    fun drawNode(type: NodeType, direction: NodeDirection, x: Double, y: Double) {
        val image = getImage(direction, type)
        gc.drawImage(image, x, y)
    }

    private fun getImage(nodeDirection: NodeDirection, nodeType: NodeType) = when (nodeType) {
        NodeType.ROAD -> getRoadImage(nodeDirection)
        NodeType.SPAWN -> Image("sample/img/road_spawn.png", nodeSize, nodeSize, true, true)
        NodeType.CONNECTOR -> Image("sample/img/road_connect.png", nodeSize, nodeSize, true, true)

    }

    private fun getRoadImage(nodeDirection: NodeDirection) = when (nodeDirection) {
        NodeDirection.DOWN -> Image("sample/img/road_down.png", nodeSize, nodeSize, true, true)
        NodeDirection.UP -> Image("sample/img/road_up.png", nodeSize, nodeSize, true, true)
        NodeDirection.LEFT -> Image("sample/img/road_left.png", nodeSize, nodeSize, true, true)
        NodeDirection.RIGHT -> Image("sample/img/road_right.png", nodeSize, nodeSize, true, true)
    }

    fun drawGrid(width: Double, height: Double) = gc.drawGrid(width, height)

    fun drawGrass(width: Double, height: Double) = gc.drawGrass(width, height)

    private fun GraphicsContext.drawGrid(width: Double, height: Double) {
        stroke = Color.BLUE
        run {
            var i = 0
            while (i < width) {
                strokeLine(i.toDouble(), 0.0, i.toDouble(), height - height % nodeSize)
                i += nodeSize.toInt()
            }
        }

        // horizontal lines
        stroke = Color.RED
        var i = 0
        while (i < height) {
            strokeLine(0.0, i.toDouble(), width, i.toDouble())
            i += nodeSize.toInt()
        }
    }

    private fun GraphicsContext.drawGrass(width: Double, height: Double) {
        stroke = Color.BLACK
        val grass = Image("sample/img/grass.png", nodeSize.toDouble(), nodeSize.toDouble(), true, true)
        var i = 0
        while (i < height / nodeSize) {
            var j = 0
            while (j < width / nodeSize) {
                drawImage(grass, (j * nodeSize).toDouble(), (i * nodeSize).toDouble())
                j++
            }
            i++
        }
    }

}