package sample

import javafx.application.Platform
import javafx.event.Event
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.control.ChoiceBox
import javafx.scene.control.RadioButton
import javafx.scene.control.TextField
import javafx.scene.control.ToggleGroup
import javafx.scene.image.Image
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.paint.Color
import sample.dto.*
import sample.service.ApiService
import java.lang.RuntimeException
import java.util.*

class CanvasController {
    lateinit var simulationMap: Canvas
    lateinit var anchor: AnchorPane
    lateinit var directionGroup: ToggleGroup
    lateinit var typeGroup: ToggleGroup
    lateinit var boardNames: ChoiceBox<String>
    lateinit var nameInput: TextField
    var isDrawing = false
    val nodeMap = hashMapOf<Pair<Double, Double>, NodeDto>()
    val retrofit = ApiService.create()
    val nodeSize = 25.0
    val canvasService = CanvasService()

    fun draw(mouseEvent: MouseEvent) {
        if (!isDrawing) {
            isDrawing = !isDrawing
            prepareBoard()
        } else isDrawing = !isDrawing;
    }

    fun initialize() {
        updateNames()
    }

    fun drawNode(mouseEvent: MouseEvent) {
        if (isDrawing) {


            val selectedRadioType = typeGroup.selectedToggle as RadioButton
            val selectedRadioDirection = directionGroup.selectedToggle as RadioButton
            val nodeDirection = getNodeDirection(selectedRadioDirection.text)
            val nodeType = getNodeType(selectedRadioType.text)
            val horizontalPosition = computeCoord(mouseEvent.x)
            val verticalPosition = computeCoord(mouseEvent.y)
            drawNode(nodeType, nodeDirection, horizontalPosition, verticalPosition)
            val node = NodeDto.builder()
                    .horizontalPosition(horizontalPosition)
                    .verticalPosition(verticalPosition)
                    .direction(nodeDirection)
                    .nodeType(nodeType)
                    .nodeId(UUID.randomUUID().toString())
                    .build()
            canvasService.addNode(node)
        }

    }

    fun save(mouseEvent: MouseEvent) {
        putNeighboors()
        setSpawnId()
        val request = SaveBoardRequest(BoardDto.of(nameInput.text, nodeMap.values.toSet()))
        retrofit.saveBoard(request).subscribe{ updateNames()}

    }

    private fun updateNames(){
        Platform.runLater {
            retrofit.getAllNames().forEach { list ->
                boardNames.itemsProperty().get().setAll(list)
            }
        }

    }

    fun upload(mouseEvent: MouseEvent) {
        prepareBoard()
        canvasService.deleteNodes()
        retrofit.uploadBoard(boardNames.value)
                .map { response -> response.boardDto.nodeDtos }
                .forEach { nodes ->
                    nodes.forEach { node ->
                        canvasService.addNode(node)
                        drawNode(node.nodeType, node.direction, node.horizontalPosition, node.verticalPosition)
                    }
                }
    }


    private fun drawNode(type: NodeType, direction: NodeDirection, x: Double, y: Double) {
        val image = getImage(direction, type)
        val gc = simulationMap.graphicsContext2D
        gc.drawImage(image, x, y)
    }

    private fun prepareBoard() {
        simulationMap.width = anchor.width;
        simulationMap.height = anchor.height;
        val gc = simulationMap.graphicsContext2D
        gc.drawGrass(simulationMap.width, simulationMap.height)
        gc.drawGrid(simulationMap.width, simulationMap.height)
    }

    private fun putNeighboors() {
        nodeMap.values.forEach { node ->
            node.topId = nodeMap[Pair(node.horizontalPosition, node.verticalPosition + nodeSize)]?.nodeId
            node.bottomId = nodeMap[Pair(node.horizontalPosition, node.verticalPosition - nodeSize)]?.nodeId
            node.leftId = nodeMap[Pair(node.horizontalPosition - nodeSize, node.verticalPosition)]?.nodeId
            node.rightId = nodeMap[Pair(node.horizontalPosition + nodeSize, node.verticalPosition)]?.nodeId
        }
    }

    private fun computeCoord(x: Double) = Math.floor(x / nodeSize) * nodeSize
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

    private fun getNodeDirection(txt: String) = when (txt) {
        "Left" -> NodeDirection.LEFT
        "Right" -> NodeDirection.RIGHT
        "Top" -> NodeDirection.UP
        "Down" -> NodeDirection.DOWN
        else -> throw RuntimeException()
    }

    private fun getNodeType(txt: String) = when (txt) {
        "Spawn" -> NodeType.SPAWN
        "Road" -> NodeType.ROAD
        "Connector" -> NodeType.CONNECTOR
        else -> throw RuntimeException()
    }

    private fun setSpawnId() {
        var id = 0;
        nodeMap.values.filter { node -> (node.nodeType == NodeType.SPAWN) and (node.spawnStreamId == null) }
                .forEach { node -> setSpawnStream(node, id++) }
    }

    private fun setSpawnStream(node: NodeDto, streamId: Int) {
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
            previousNode = getNextNode(previousNode, delta)
        }
    }

    private fun getNextNode(node: NodeDto, delta: Pair<Double, Double>): NodeDto? {
        val nextHorizontal = node.horizontalPosition + delta.first
        val nextVertical = node.verticalPosition + delta.second
        return canvasService.getNode(nextHorizontal, nextVertical)

    }

    private fun getPreviousNode(node: NodeDto, delta: Pair<Double, Double>): NodeDto? {
        val previousHorizontal = node.horizontalPosition - delta.first
        val previousVertical = node.verticalPosition - delta.second
        return canvasService.getNode(previousHorizontal, previousVertical)
    }


    private fun getDeltaToNextNode(direction: NodeDirection): Pair<Double, Double> = when (direction) {
        NodeDirection.RIGHT -> Pair(0.0, nodeSize)
        NodeDirection.LEFT -> Pair(0.0, nodeSize)
        NodeDirection.UP -> Pair(nodeSize, 0.0)
        NodeDirection.DOWN -> Pair(nodeSize, 0.0)


    }

    private fun checkSpawnDirection(direction: NodeDirection, comparedNode: NodeDto) = comparedNode.nodeType == NodeType.SPAWN && comparedNode.direction == direction


}