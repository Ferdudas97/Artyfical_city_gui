package sample.controller

import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.control.*
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.VBox
import sample.dto.*
import sample.service.ApiService
import sample.service.CanvasService
import sample.service.NodeService
import java.lang.RuntimeException
import javafx.stage.Modality
import javafx.stage.Stage
import sample.infastructure.ConnectorHelper
import sample.infastructure.setNeighboursToConnectorNode
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
    lateinit var connectorVBox: VBox
    lateinit var connectorId: TextField
    lateinit var thirdConnectedId: TextField
    lateinit var secondConnectedId: TextField
    lateinit var firstConnectedId: TextField
    lateinit var drawingVBox: VBox
    lateinit var switchButton: Button
    private val nodeSize = 15.0
    private var nodeId = 1
    private var isConnecting = false

    private var isDrawing = true
    private val retrofit = ApiService.create()
    private val nodeService: NodeService = NodeService(nodeSize)
    private lateinit var canvasService: CanvasService


    private lateinit var connectorHelper: ConnectorHelper

    fun switch(mouseEvent: MouseEvent) {
        isConnecting = !isConnecting
        isDrawing = !isDrawing
        prepareBoard()
        changeVisibility(vBox = connectorVBox)
        changeVisibility(vBox = drawingVBox)

        if (connectorVBox.isManaged) {
            nodeService.getAllNodes().stream()
                    .filter { it.nodeType == NodeType.CONNECTOR }
                    .forEach { canvasService.drawConnectorsNode(it.horizontalPosition, it.verticalPosition, it.nodeId) }
            switchButton.text = "Draw"
        } else {
            prepareBoard()
            nodeService.getAllNodes().forEach { node ->
                canvasService.drawNode(node.nodeType, node.direction, node.horizontalPosition, node.verticalPosition)
            }
            switchButton.text = "Connect"

        }
    }

    fun updateConnector(mouseEvent: MouseEvent) {
        val helper = ConnectorHelper(secondId = secondConnectedId.text,
                firstId = firstConnectedId.text,
                thirdId = thirdConnectedId.text)
        nodeService.getNode(connectorId.text)?.let {
            setNeighboursToConnectorNode(node = it, connectorHelper = helper)
        }
        firstConnectedId.clear()
        secondConnectedId.clear()
        thirdConnectedId.clear()
        connectorId.clear()
    }


    fun clearBoard(mouseEvent: MouseEvent) {
        prepareBoard()
        nodeService.clearNodes()

    }

    fun initialize() {
        updateNames()
        canvasService = CanvasService(simulationMap.graphicsContext2D, nodeSize)
        sppedInput.text = "5"

    }

    fun drawNode(mouseEvent: MouseEvent) {
        if (isDrawing) {


            val selectedRadioType = typeGroup.selectedToggle as RadioButton
            val selectedRadioDirection = directionGroup.selectedToggle as RadioButton
            val nodeDirection = getNodeDirection(selectedRadioDirection.text)
            val nodeType = getNodeType(selectedRadioType.text)
            val horizontalPosition = computeCoord(mouseEvent.x)
            val verticalPosition = computeCoord(mouseEvent.y)
            val speedAllowed = sppedInput.text.toDouble()
            canvasService.drawNode(nodeType, nodeDirection, horizontalPosition, verticalPosition)
            val node = NodeDto.builder()
                    .horizontalPosition(horizontalPosition)
                    .verticalPosition(verticalPosition)
                    .direction(nodeDirection)
                    .nodeType(nodeType)
                    .nodeId(nodeId++.toString())
                    .maxSpeedAllowed(speedAllowed)
                    .build()
            nodeService.addNode(node)


        }
        if (isConnecting) {
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
                    nodes.stream().peek { node ->
                        nodeService.addNode(node)
                        canvasService.drawNode(node.nodeType, node.direction, node.horizontalPosition, node.verticalPosition)
                    }
                            .map(NodeDto::getNodeId)
                            .map(String::toInt)
                            .max { o1, o2 -> o1.compareTo(o2) }
                            .ifPresent { nodeId = it + 1 }
                }
    }

    fun openSimulation(museEvent: MouseEvent) {
        val fxmlLoader = FXMLLoader(javaClass.getResource("../simulationBoard.fxml"))
        val root1 = fxmlLoader.load<Any>() as Parent
        val stage = Stage()
        //set what you want on your stage
        stage.initModality(Modality.WINDOW_MODAL)
        stage.scene = Scene(root1)
        stage.show()
        stage.isMaximized = true
        stage.title = "Artyficial City"


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
        "Bottom" -> NodeDirection.DOWN
        else -> throw RuntimeException()
    }

    private fun getNodeType(txt: String) = when (txt) {
        "Spawn" -> NodeType.SPAWN
        "Road" -> NodeType.ROAD
        "Connector" -> NodeType.CONNECTOR
        "Traffic light" -> NodeType.LIGHTS
        else -> throw RuntimeException()
    }

    private fun changeVisibility(vBox: VBox) = vBox.run {
        isVisible = !isVisible
        isManaged = !isManaged
    }


}