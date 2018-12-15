package sample

import javafx.event.Event
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.control.ChoiceBox
import javafx.scene.control.RadioButton
import javafx.scene.image.Image
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.paint.Color
import sample.dto.NodeDirection
import sample.dto.NodeDto
import sample.dto.NodeType
import sample.service.ApiService
import sample.service.CanvasService
import sample.service.NodeService
import java.util.*

class SimulationController {
    val retrofit = ApiService.create()
    lateinit var boardNames: ChoiceBox<String>
    lateinit var simulationMap: Canvas
    lateinit var anchor: AnchorPane
    val nodeSize = 25.0
    val nodeService = NodeService(nodeSize)
    lateinit var canvasService: CanvasService


    fun initialize() {
        canvasService = CanvasService(simulationMap.graphicsContext2D, nodeSize)
    }

    fun updateBoardNames(event: Event) {
        retrofit.getAllNames().forEach { list ->
            boardNames.itemsProperty().get().setAll(list)

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

    fun startSimulaton(mouseEvent: MouseEvent) {

    }

    private fun prepareBoard() {
        simulationMap.width = anchor.width;
        simulationMap.height = anchor.height;
        canvasService.drawGrass(simulationMap.width, simulationMap.height)
        canvasService.drawGrid(simulationMap.width, simulationMap.height)
    }


}