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
    val retrofit = ApiService.create()
    val nodeSize = 25.0
    lateinit var canvasService: CanvasService
    val nodeService: NodeService = NodeService(nodeSize)


    fun draw(mouseEvent: MouseEvent) {
        if (!isDrawing) {
            isDrawing = !isDrawing
            prepareBoard()
        } else isDrawing = !isDrawing;
    }

    fun initialize() {
        updateNames()
        canvasService = CanvasService(simulationMap.graphicsContext2D, nodeSize)
    }

    fun drawNode(mouseEvent: MouseEvent) {
        if (isDrawing) {


            val selectedRadioType = typeGroup.selectedToggle as RadioButton
            val selectedRadioDirection = directionGroup.selectedToggle as RadioButton
            val nodeDirection = getNodeDirection(selectedRadioDirection.text)
            val nodeType = getNodeType(selectedRadioType.text)
            val horizontalPosition = computeCoord(mouseEvent.x)
            val verticalPosition = computeCoord(mouseEvent.y)
            canvasService.drawNode(nodeType, nodeDirection, horizontalPosition, verticalPosition)
            val node = NodeDto.builder()
                    .horizontalPosition(horizontalPosition)
                    .verticalPosition(verticalPosition)
                    .direction(nodeDirection)
                    .nodeType(nodeType)
                    .nodeId(UUID.randomUUID().toString())
                    .build()
            nodeService.addNode(node)
        }

    }

    fun save(mouseEvent: MouseEvent) {
        nodeService.putNeighboors()
        nodeService.setSpawnStreams()
        val request = SaveBoardRequest(BoardDto.of(nameInput.text, nodeService.getAllNodes()))
        retrofit.saveBoard(request).subscribe { updateNames() }

    }

    private fun updateNames() {
        Platform.runLater {
            retrofit.getAllNames().forEach { list ->
                boardNames.itemsProperty().get().setAll(list)
            }
        }

    }

    fun upload(mouseEvent: MouseEvent) {
        prepareBoard()
        nodeService.deleteNodes()
        retrofit.uploadBoard(boardNames.value)
                .map { response -> response.boardDto.nodeDtos }
                .forEach { nodes ->
                    nodes.forEach { node ->
                        nodeService.addNode(node)
                        canvasService.drawNode(node.nodeType, node.direction, node.horizontalPosition, node.verticalPosition)
                    }
                }
    }

    private fun prepareBoard() {
        simulationMap.width = anchor.width;
        simulationMap.height = anchor.height;
        canvasService.drawGrass(simulationMap.width, simulationMap.height)
        canvasService.drawGrid(simulationMap.width, simulationMap.height)
    }


    private fun computeCoord(x: Double) = Math.floor(x / nodeSize) * nodeSize

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



}