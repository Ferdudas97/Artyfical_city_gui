package sample.controller

import javafx.geometry.Orientation
import javafx.scene.canvas.Canvas
import javafx.scene.control.Button
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Label
import javafx.scene.control.Slider
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.VBox
import sample.dto.NodeDto
import sample.dto.SimulationInfo
import sample.model.node.NodeId
import sample.model.node.NodeType
import sample.model.node.spawn.SpawnStreamId
import sample.service.*


class SimulationController {
    lateinit var boardNames: ChoiceBox<String>
    lateinit var simulationMap: Canvas
    lateinit var anchor: AnchorPane
    lateinit var slidersVBox: VBox
    lateinit var canvasService: CanvasService
    lateinit var simulationService: SimulationService
    lateinit var simulationButton: Button

    val nodeSize = 15.0
    private val nodeService = NodeService(nodeSize)
    private val retrofit = ApiService.create()
    private val sliders = hashMapOf<String, Slider>()

    fun initialize() {
        canvasService = CanvasService(simulationMap.graphicsContext2D, nodeSize)
        retrofit.getAllNames().forEach { list ->
            boardNames.itemsProperty().get().setAll(list)

        }
    }

    private fun getSpawnStreams() {
        var i = 0
        nodeService.getAllNodes()
                .stream()
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
        val slider = Slider(0.0, 5.0, 1.0)
        vBox.spacing = 5.0
        slider.orientation = Orientation.HORIZONTAL
        value.labelFor = slider
        title.labelFor = value
        value.text = "${slider.valueProperty().get()} x"
        slider.valueProperty().addListener { observable, oldValue, newValue ->
            value.text = "${newValue.toInt()} x"
            updateSimulation()


        }
        vBox.children.addAll(title, value, slider)
        slidersVBox.children.add(vBox)
        sliders["SPEED"] = slider
    }

    private fun makeCarSpawnSlider(it: Int, sliderId: String) {
        val vBox = VBox()
        val value = Label()
        val title = Label("Stream: $it")
        val slider = Slider(0.0, 200.0, 10.0)
        vBox.spacing = 5.0
        slider.orientation = Orientation.HORIZONTAL
        value.labelFor = slider
        value.text = "${slider.valueProperty().get()} car/min"
        title.labelFor = value
        slider.valueProperty().addListener { _, _, newValue ->
            value.text = "${newValue.toInt()} car/min"
            updateSimulation()

        }
        vBox.children.addAll(title, value, slider)
        slidersVBox.children.add(vBox)
        sliders[sliderId] = slider
    }

    private fun updateSimulation() {
        if (simulationButton.text == "Stop simulation") {
            simulationService.stopSimulation()
            simulationService.startSimulation()
        }
    }


    fun upload(mouseEvent: MouseEvent) {
        slidersVBox.children.clear()
        if (simulationButton.text == "Stop simulation") {
            simulationButton.text = "Start simulation"
            simulationService.stopSimulation()
        }
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
        simulationService = SimulationServiceImpl(nodeService.getAllNodes())
        simulationService.init()
    }

    fun startSimulaton(mouseEvent: MouseEvent) {
        if (!simulationService.isSimulating) {

            simulationService.changeSimulationInfo(prepareSimulationDetails())
            simulationService.startSimulation().subscribe {
                nodeService.getAllNodes().parallelStream().filter { it.nodeType !== NodeType.LIGHTS }.forEach { node ->
                    canvasService.drawNode(node.nodeType, node.direction, node.horizontalPosition, node.verticalPosition)

                }

                simulationService.headAndSizes().entries.parallelStream().forEach { e ->
                    canvasService.drawCar(e.key,
                            e.value.second, nodeService.getNode(e.value.first.horiziontalPosition, e.value.first.verticalPosition)!!)

                }
            }
            simulationButton.text = "Stop simulation"
        } else {
            simulationService.stopSimulation()
            simulationButton.text = "Start simulation"

        }
    }


    private fun prepareSimulationDetails(): SimulationInfo {
        val streams = sliders.filter { it.key != "SPEED" }.entries.associate { SpawnStreamId.of(it.key) to it.value.value.toInt() }
        val speed = sliders["SPEED"]?.value?.toInt()
        return SimulationInfo.of(streams, speed ?: 1)
    }

    private fun prepareBoard() {
        simulationMap.width = anchor.width;
        simulationMap.height = anchor.height;
        canvasService.drawGrass(simulationMap.width, simulationMap.height)
    }

    private fun computeCoord(x: Double) = Math.floor(x / nodeSize) * nodeSize
    fun changeTrafficLight(mouseEvent: MouseEvent) {

        val x = computeCoord(mouseEvent.x)
        val y = computeCoord(mouseEvent.y)

        val nodeId = nodeService.getNode(x, y)?.nodeId

        nodeId?.let {
            val color = simulationService.changeLight(NodeId.of(it))
            canvasService.changeLightColor(x, y, color)
        }

    }


}