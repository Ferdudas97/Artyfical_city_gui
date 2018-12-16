package sample

import com.launchdarkly.eventsource.EventSource
import javafx.application.Platform
import javafx.event.Event
import javafx.geometry.Orientation
import javafx.scene.canvas.Canvas
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Label
import javafx.scene.control.Slider
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.VBox
import okhttp3.Request
import sample.dto.ChangeSimulationDetailsRequest
import sample.dto.NodeDto
import sample.dto.NodeType
import sample.service.ApiService
import sample.service.CanvasService
import sample.service.NodeService
import org.springframework.web.socket.sockjs.transport.handler.EventSourceTransportHandler
import sample.service.TestHandler
import java.net.URI
import java.util.concurrent.TimeUnit




class SimulationController{
    val retrofit = ApiService.create()
    lateinit var boardNames: ChoiceBox<String>
    lateinit var simulationMap: Canvas
    lateinit var anchor: AnchorPane
    lateinit var slidersVBox: VBox
    val nodeSize = 25.0
    val nodeService = NodeService(nodeSize)
    val sliders = hashMapOf<String, Slider>()
    lateinit var canvasService: CanvasService


    fun initialize() {
        canvasService = CanvasService(simulationMap.graphicsContext2D, nodeSize)
    }

    fun updateBoardNames(event: Event) {
        retrofit.getAllNames().forEach { list ->
            boardNames.itemsProperty().get().setAll(list)

        }
    }

    fun getSpawnStreams() {
        var i = 0
        nodeService.getAllNodes()
                .parallelStream()
                .filter { node -> node.nodeType == NodeType.SPAWN }
                .map(NodeDto::getSpawnStreamId)
                .distinct()
                .forEach { makeCarSpawnSlider(i++, it) }
        makeSimulationSpeedSlide()

    }

    private fun makeSimulationSpeedSlide() {
        val vBox = VBox()
        val value = Label()
        val title = Label("Simulation speed: ")
        val slider = Slider(0.0, 20.0, 1.0)
        vBox.spacing = 5.0
        slider.orientation = Orientation.HORIZONTAL
        value.labelFor = slider
        title.labelFor = value
        slider.valueProperty().addListener { observable, oldValue, newValue ->
            value.text = "${newValue.toInt()} x"
        }
        vBox.children.addAll(title, value, slider)
        slidersVBox.children.add(vBox)
        sliders["SPEED"] = slider
    }

    private fun makeCarSpawnSlider(it: Int, sliderId: String) {
        val vBox = VBox()
        val value = Label()
        val title = Label("Stream: $it")
        val slider = Slider(0.0, 500.0, 250.0)
        vBox.spacing = 5.0
        slider.orientation = Orientation.HORIZONTAL
        value.labelFor = slider
        title.labelFor = value
        slider.valueProperty().addListener { observable, oldValue, newValue ->
            value.text = "${newValue.toInt()} car/min"
        }
        vBox.children.addAll(title, value, slider)
        slidersVBox.children.add(vBox)
        sliders[sliderId] = slider
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
        getSpawnStreams()
        getSimulationData()
    }

    fun startSimulaton(mouseEvent: MouseEvent) {
        retrofit.saveDetails(prepareSimulationDetails()).subscribe()

    }

    fun getSimulationData() {
        Platform.runLater{
            val eventHandler = TestHandler()
            val url = String.format("http://localhost:8080/api/simulation")
            val builder = EventSource.Builder(eventHandler, URI.create(url)).method("GET")

            builder.build().use { eventSource ->
                eventSource.start()

            }

        }

    }

    private fun prepareSimulationDetails(): ChangeSimulationDetailsRequest {
        val streams = sliders.filter { it.key != "SPEED" }.entries.associate { it.key to it.value.value.toInt() }
        val speed = sliders["SPEED"]?.value?.toInt()
        return ChangeSimulationDetailsRequest(streamProduction = streams, simulationSpeed = speed?:1)
    }

    private fun prepareBoard() {
        simulationMap.width = anchor.width;
        simulationMap.height = anchor.height;
        canvasService.drawGrass(simulationMap.width, simulationMap.height)
        canvasService.drawGrid(simulationMap.width, simulationMap.height)
    }


}