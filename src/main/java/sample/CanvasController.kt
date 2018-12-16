package sample

import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.control.ChoiceBox
import javafx.scene.control.RadioButton
import javafx.scene.control.TextField
import javafx.scene.control.ToggleGroup
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import sample.dto.*
import sample.service.ApiService
import sample.service.CanvasService
import sample.service.NodeService
import java.lang.RuntimeException
import java.util.*
import javafx.stage.Modality
import javafx.stage.Stage
import sample.model.node.NodeDirection
import sample.model.node.NodeType


class CanvasController {
    lateinit var simulationMap: Canvas
    lateinit var anchor: AnchorPane
    lateinit var directionGroup: ToggleGroup
    lateinit var typeGroup: ToggleGroup
    lateinit var boardNames: ChoiceBox<String>
    lateinit var nameInput: TextField
    lateinit var sppedInput: TextField

    var isDrawing = false
    val retrofit = ApiService.create()
    val nodeSize = 15.0
    lateinit var canvasService: CanvasService
    var nodeId = 1;
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
        sppedInput.setText("50")
    }

    fun drawNode(mouseEvent: MouseEvent) {
        if (isDrawing) {


            val selectedRadioType = typeGroup.selectedToggle as RadioButton
            val selectedRadioDirection = directionGroup.selectedToggle as RadioButton
            val nodeDirection = getNodeDirection(selectedRadioDirection.text)
            val nodeType = getNodeType(selectedRadioType.text)
            val horizontalPosition = computeCoord(mouseEvent.x)
            val verticalPosition = computeCoord(mouseEvent.y)
            val sppedAllowed = java.lang.Double.parseDouble(sppedInput.text)
            canvasService.drawNode(nodeType, nodeDirection, horizontalPosition, verticalPosition)
            val node = NodeDto.builder()
                    .horizontalPosition(horizontalPosition)
                    .verticalPosition(verticalPosition)
                    .direction(nodeDirection)
                    .nodeType(nodeType)
                    .nodeId(nodeId++.toString())
                    .maxSpeedAllowed(sppedAllowed)
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
    fun openSimulation(museEvent: MouseEvent){
        val fxmlLoader = FXMLLoader(javaClass.getResource("simulationBoard.fxml"))
        val root1 = fxmlLoader.load<Any>() as Parent
        val stage = Stage()
        //set what you want on your stage
        stage.initModality(Modality.WINDOW_MODAL)
        stage.scene = Scene(root1)
        stage.show()
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