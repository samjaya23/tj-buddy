package com.example.model

enum class TransportMode(val displayName: String, val shortCode: String) {
    TRANSJAKARTA("TransJakarta", "TJ"),
    KRL("KRL Commuter Line", "KRL"),
    MRT("MRT Jakarta", "MRT"),
    LRT("LRT Jabodebek", "LRT"),
    WALKING("Jalan Kaki", "Walk")
}

enum class RouteComplexity(val label: String) {
    VERY_EASY("Sangat Mudah (Cocok untuk Pemula)"),
    MODERATE("Cukup Mudah"),
    ADVANCED("Banyak Transit")
}

data class TransitLocation(
    val id: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val isCurrentLocation: Boolean = false
)

enum class DestinationType {
    STATION,
    HALTE,
    LANDMARK,
    RESIDENTIAL_AREA
}

data class DestinationItem(
    val id: String,
    val name: String,
    val subtitle: String,
    val type: DestinationType,
    val connectedModes: List<TransportMode>,
    val latitude: Double,
    val longitude: Double
)

data class Stop(
    val id: String,
    val name: String,
    val mode: TransportMode,
    val lineCode: String,
    val sequenceNumber: Int,
    val latitude: Double,
    val longitude: Double,
    val isTransferPoint: Boolean = false,
    val transferNotes: String? = null
)

data class WayfindingStep(
    val order: Int,
    val instruction: String,
    val detail: String,
    val iconType: String // "straight", "right", "left", "stairs", "escalator", "platform", "gate"
)

data class StationFacility(
    val name: String,
    val locationDescription: String,
    val icon: String
)

data class StationPlatform(
    val platformNumber: String,
    val destinationDirection: String,
    val lines: List<String>,
    val crowdStatus: String = "Normal"
)

data class StationMap(
    val stationId: String,
    val stationName: String,
    val stationType: String, // "Halte TransJakarta", "Stasiun KRL", "Stasiun MRT"
    val youAreHereNote: String,
    val wayfindingSteps: List<WayfindingStep>,
    val platforms: List<StationPlatform>,
    val facilities: List<StationFacility>,
    val exits: List<String>
)

data class RouteSegment(
    val id: String,
    val mode: TransportMode,
    val lineName: String, // e.g. "B1 (Summarecon Bekasi - Tosari)", "Cikarang Line"
    val vehicleNumberOrTrack: String, // e.g. "Platform 2 arah Bekasi/Cikarang"
    val originStop: Stop,
    val destinationStop: Stop,
    val intermediateStops: List<Stop>,
    val durationMinutes: Int,
    val fare: Int,
    val instructionForBeginner: String,
    val tip: String? = null
)

data class RouteOption(
    val id: String,
    val title: String,
    val isRecommended: Boolean,
    val recommendedBadgeText: String? = null,
    val summaryRoute: String, // e.g. "B1 → B2 → Cikarang Line → Metland"
    val totalDurationMinutes: Int,
    val totalFare: Int,
    val transferCount: Int,
    val walkingTimeMinutes: Int,
    val walkingDistanceMeters: Int,
    val complexity: RouteComplexity,
    val modesUsed: List<TransportMode>,
    val segments: List<RouteSegment>,
    val beginnerAdvice: String
)

enum class JourneyState {
    IDLE,
    SEARCHING,
    ROUTE_SELECTED,
    WALKING_TO_STOP,
    WAITING,
    ON_VEHICLE,
    TRANSFER,
    APPROACHING_STOP,
    OFF_ROUTE,
    REROUTING,
    ARRIVED
}

data class ActiveJourneySession(
    val journeyId: String,
    val origin: TransitLocation,
    val destination: DestinationItem,
    val selectedRoute: RouteOption,
    val currentStepIndex: Int = 0,
    val currentStopIndex: Int = 0, // index within current segment's stops
    val journeyState: JourneyState = JourneyState.WALKING_TO_STOP,
    val isOffRoute: Boolean = false,
    val estimatedArrival: String = "",
    val startedAtEpochMs: Long = System.currentTimeMillis()
)

data class JourneyFeedback(
    val id: String = java.util.UUID.randomUUID().toString(),
    val rating: String, // "EASY", "MODERATE", "CONFUSING"
    val ratingEmoji: String,
    val comment: String = "",
    val routeTitle: String,
    val durationMinutes: Int,
    val fare: Int,
    val submittedAtEpochMs: Long = System.currentTimeMillis()
)
