package sample.dto


data class ChangeSimulationDetailsRequest(val streamProduction: Map<String, Int>, val simulationSpeed: Int)
