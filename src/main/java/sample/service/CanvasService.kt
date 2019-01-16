package sample.service

import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.Image
import javafx.scene.paint.Color
import sample.dto.NodeDto
import sample.model.node.NodeDirection
import sample.model.node.NodeType
import kotlin.random.Random


class CanvasService(val gc: GraphicsContext, val nodeSize: Double) {

    val carMap = HashMap<String, Color>()

    @Synchronized
    fun drawNode(type: NodeType, direction: NodeDirection, x: Double, y: Double) {
        val image = getImage(direction, type)
        gc.drawImage(image, x, y)
    }


    fun changeLightColor(x: Double, y: Double, color: Color) = gc.drawImage(getLightImage(color), x, y)

    fun drawConnectorsNode(x: Double, y: Double, id: String) {
        gc.stroke = Color.BLACK
        gc.strokeText(id,x, y, nodeSize)
    }

    private fun getImage(nodeDirection: NodeDirection, nodeType: NodeType) = when (nodeType) {
        NodeType.ROAD -> getRoadImage(nodeDirection)
        NodeType.SPAWN -> Image("sample/img/road_spawn.png", nodeSize, nodeSize, true, true)
        NodeType.CONNECTOR -> Image("sample/img/road_connect.png", nodeSize, nodeSize, true, true)
        NodeType.LIGHTS -> getLightImage(Color.GREEN)

    }

    private fun getLightImage(color: Color) = when (color) {
        Color.RED -> Image("sample/img/traffic_light_red.png", nodeSize, nodeSize, true, true)
        else -> Image("sample/img/traffic_light_green.png", nodeSize, nodeSize, true, true)

    }


    private fun getRoadImage(nodeDirection: NodeDirection) = when (nodeDirection) {
        NodeDirection.DOWN -> Image("sample/img/road_down.png", nodeSize, nodeSize, true, true)
        NodeDirection.UP -> Image("sample/img/road_up.png", nodeSize, nodeSize, true, true)
        NodeDirection.LEFT -> Image("sample/img/road_left.png", nodeSize, nodeSize, true, true)
        NodeDirection.RIGHT -> Image("sample/img/road_right.png", nodeSize, nodeSize, true, true)
    }


    @Synchronized
    fun drawCar(id: String, size: Int, head: NodeDto) {
        carMap.putIfAbsent(id, pickRandomColor())
        gc.fill = carMap[id]
        val delta = carDeltaInSpecifiedDirection(head.direction, size)
        drawCarInSpecifiedDirection(head.horizontalPosition, head.verticalPosition, head.direction, delta, size)
//        gc.fillRect(head.horizontalPosition+nodeSize/4, head.verticalPosition+nodeSize/4, delta.first, delta.second)
    }

    fun drawCarInSpecifiedDirection(x: Double, y: Double, direction: NodeDirection, delta: Pair<Double, Double>, size: Int) = when (direction) {
        NodeDirection.LEFT -> gc.fillRect(x, y, delta.first, delta.second)
        NodeDirection.RIGHT -> gc.fillRect(x - (nodeSize * (size - 1)), y, delta.first, delta.second)
        NodeDirection.UP -> gc.fillRect(x, y, delta.first, delta.second)
        NodeDirection.DOWN -> gc.fillRect(x, y - (nodeSize * (size - 1)), delta.first, delta.second)
    }

    private fun carDeltaInSpecifiedDirection(nodeDirection: NodeDirection, size: Int) = when (nodeDirection) {
        NodeDirection.DOWN, NodeDirection.UP -> (nodeSize) to (nodeSize) * (size)
        NodeDirection.LEFT, NodeDirection.RIGHT -> (nodeSize) * (size) to (nodeSize)
    }


    private fun pickRandomColor() = Color(Random.nextDouble() % 256, Random.nextDouble() % 256, Random.nextDouble() % 256, 1.0)

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