package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.JakartaTransitRepository
import com.example.data.MockJakartaTransitDataSource
import com.example.data.local.AppDatabase
import com.example.data.local.RecentDestinationEntity
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AppScreen {
    HOME,
    SEARCH,
    ROUTE_OPTIONS,
    ACTIVE_NAV,
    LOCAL_STATION_MAP,
    ARRIVAL
}

data class UiState(
    val userName: String = "Ridwan",
    val isFirstTimeMode: Boolean = true,
    val currentScreen: AppScreen = AppScreen.HOME,
    val backStack: List<AppScreen> = listOf(AppScreen.HOME),
    val origin: TransitLocation,
    val searchQuery: String = "",
    val searchResults: List<DestinationItem> = emptyList(),
    val popularDestinations: List<DestinationItem> = emptyList(),
    val selectedDestination: DestinationItem? = null,
    val routeOptions: List<RouteOption> = emptyList(),
    val selectedRoute: RouteOption? = null,
    val activeJourney: ActiveJourneySession? = null,
    val activeStationMap: StationMap? = null,
    val isLocalStationMapVisible: Boolean = false,
    val showExitConfirmDialog: Boolean = false,
    val showHelpDialog: Boolean = false,
    val showOverviewSheet: Boolean = false,
    val alertMessage: String? = null,
    val recentDestinations: List<RecentDestinationEntity> = emptyList(),
    val isRerouted: Boolean = false,
    val rerouteBannerMessage: String? = null,
    val feedbackSubmitted: Boolean = false,
    val simulatedGpsStep: Int = 0
)

class TJBuddyViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: JakartaTransitRepository
    private val _uiState: MutableStateFlow<UiState>
    val uiState: StateFlow<UiState>

    init {
        val db = AppDatabase.getDatabase(application)
        val dataSource = MockJakartaTransitDataSource()
        repository = JakartaTransitRepository(dataSource, db)

        val defaultOrigin = repository.getOriginLocation()
        val popular = repository.getPopularDestinations()

        _uiState = MutableStateFlow(
            UiState(
                origin = defaultOrigin,
                popularDestinations = popular,
                searchResults = popular
            )
        )
        uiState = _uiState.asStateFlow()

        viewModelScope.launch {
            repository.getRecentDestinations().collect { recents ->
                _uiState.update { it.copy(recentDestinations = recents) }
            }
        }
    }

    fun toggleFirstTimeMode() {
        _uiState.update { it.copy(isFirstTimeMode = !it.isFirstTimeMode) }
    }

    fun navigateTo(screen: AppScreen) {
        _uiState.update { state ->
            val updatedStack = state.backStack + screen
            state.copy(currentScreen = screen, backStack = updatedStack)
        }
    }

    fun handleBack(): Boolean {
        val state = _uiState.value
        // If exit confirm dialog is showing, dismiss it
        if (state.showExitConfirmDialog) {
            _uiState.update { it.copy(showExitConfirmDialog = false) }
            return true
        }
        // If help dialog or local station map sheet is showing, close it
        if (state.showHelpDialog) {
            _uiState.update { it.copy(showHelpDialog = false) }
            return true
        }
        if (state.isLocalStationMapVisible) {
            _uiState.update { it.copy(isLocalStationMapVisible = false) }
            return true
        }
        if (state.showOverviewSheet) {
            _uiState.update { it.copy(showOverviewSheet = false) }
            return true
        }

        // If currently inside active navigation, back must prompt exit confirm
        if (state.currentScreen == AppScreen.ACTIVE_NAV && state.activeJourney != null) {
            _uiState.update { it.copy(showExitConfirmDialog = true) }
            return true
        }

        if (state.backStack.size > 1) {
            val newStack = state.backStack.dropLast(1)
            val prevScreen = newStack.last()
            _uiState.update { it.copy(currentScreen = prevScreen, backStack = newStack) }
            return true
        }
        return false
    }

    fun confirmExitJourney() {
        _uiState.update {
            it.copy(
                activeJourney = null,
                showExitConfirmDialog = false,
                currentScreen = AppScreen.HOME,
                backStack = listOf(AppScreen.HOME),
                isLocalStationMapVisible = false,
                isRerouted = false,
                rerouteBannerMessage = null
            )
        }
    }

    fun dismissExitDialog() {
        _uiState.update { it.copy(showExitConfirmDialog = false) }
    }

    fun onSearchQueryChanged(query: String) {
        val results = repository.searchDestinations(query)
        _uiState.update { it.copy(searchQuery = query, searchResults = results) }
    }

    fun selectDestination(destination: DestinationItem) {
        viewModelScope.launch {
            repository.saveRecentDestination(destination)
        }
        val routes = repository.planRoutes(_uiState.value.origin, destination)
        val defaultSelected = routes.firstOrNull { it.isRecommended } ?: routes.firstOrNull()

        _uiState.update { state ->
            val updatedStack = state.backStack + AppScreen.ROUTE_OPTIONS
            state.copy(
                selectedDestination = destination,
                routeOptions = routes,
                selectedRoute = defaultSelected,
                currentScreen = AppScreen.ROUTE_OPTIONS,
                backStack = updatedStack
            )
        }
    }

    fun selectRoute(route: RouteOption) {
        _uiState.update { it.copy(selectedRoute = route) }
    }

    fun startJourney() {
        val state = _uiState.value
        val route = state.selectedRoute ?: return
        val dest = state.selectedDestination ?: return

        val newSession = ActiveJourneySession(
            journeyId = "j_${System.currentTimeMillis()}",
            origin = state.origin,
            destination = dest,
            selectedRoute = route,
            currentStepIndex = 0,
            currentStopIndex = 0,
            journeyState = JourneyState.WALKING_TO_STOP,
            estimatedArrival = "1 jam 12 mnt lagi"
        )

        _uiState.update { s ->
            val updatedStack = s.backStack + AppScreen.ACTIVE_NAV
            s.copy(
                activeJourney = newSession,
                currentScreen = AppScreen.ACTIVE_NAV,
                backStack = updatedStack,
                isRerouted = false,
                rerouteBannerMessage = null,
                feedbackSubmitted = false
            )
        }
    }

    fun advanceJourneyStep() {
        val state = _uiState.value
        val journey = state.activeJourney ?: return
        val route = journey.selectedRoute
        val currentStep = journey.currentStepIndex
        val currentSeg = route.segments.getOrNull(currentStep)

        when (journey.journeyState) {
            JourneyState.WALKING_TO_STOP -> {
                // Arrived at stop -> waiting for vehicle
                _uiState.update {
                    it.copy(
                        activeJourney = journey.copy(journeyState = JourneyState.WAITING),
                        simulatedGpsStep = it.simulatedGpsStep + 1
                    )
                }
            }
            JourneyState.WAITING -> {
                // Boarded vehicle -> on vehicle
                _uiState.update {
                    it.copy(
                        activeJourney = journey.copy(
                            journeyState = JourneyState.ON_VEHICLE,
                            currentStopIndex = 0
                        ),
                        simulatedGpsStep = it.simulatedGpsStep + 1
                    )
                }
            }
            JourneyState.ON_VEHICLE -> {
                if (currentSeg != null && currentSeg.intermediateStops.isNotEmpty()) {
                    if (journey.currentStopIndex < currentSeg.intermediateStops.size - 1) {
                        // Advance to next intermediate stop
                        _uiState.update {
                            it.copy(
                                activeJourney = journey.copy(currentStopIndex = journey.currentStopIndex + 1),
                                simulatedGpsStep = it.simulatedGpsStep + 1
                            )
                        }
                    } else {
                        // Approaching destination of this segment!
                        triggerVibration()
                        _uiState.update {
                            it.copy(
                                activeJourney = journey.copy(journeyState = JourneyState.APPROACHING_STOP),
                                simulatedGpsStep = it.simulatedGpsStep + 1
                            )
                        }
                    }
                } else {
                    // No intermediate stops, directly approaching
                    triggerVibration()
                    _uiState.update {
                        it.copy(
                            activeJourney = journey.copy(journeyState = JourneyState.APPROACHING_STOP),
                            simulatedGpsStep = it.simulatedGpsStep + 1
                        )
                    }
                }
            }
            JourneyState.APPROACHING_STOP -> {
                // Alighted vehicle at destination of this segment
                val nextStepIndex = currentStep + 1
                if (nextStepIndex < route.segments.size) {
                    val nextSeg = route.segments[nextStepIndex]
                    // If transfer point, open Local Station/Halte Map!
                    val stationMap = repository.getStationMap(nextSeg.originStop.id)
                        ?: repository.getStationMap("s_b2")

                    _uiState.update {
                        it.copy(
                            activeJourney = journey.copy(
                                currentStepIndex = nextStepIndex,
                                currentStopIndex = 0,
                                journeyState = JourneyState.TRANSFER
                            ),
                            activeStationMap = stationMap,
                            isLocalStationMapVisible = true,
                            simulatedGpsStep = it.simulatedGpsStep + 1
                        )
                    }
                } else {
                    // Journey completed! Arrived!
                    val destMap = repository.getStationMap("dest_metland")
                    _uiState.update {
                        val newStack = it.backStack + AppScreen.ARRIVAL
                        it.copy(
                            activeJourney = journey.copy(journeyState = JourneyState.ARRIVED),
                            currentScreen = AppScreen.ARRIVAL,
                            backStack = newStack,
                            activeStationMap = destMap,
                            simulatedGpsStep = it.simulatedGpsStep + 1
                        )
                    }
                }
            }
            JourneyState.TRANSFER -> {
                // Finished transfer wayfinding, continuing to wait for next transport mode
                _uiState.update {
                    it.copy(
                        activeJourney = journey.copy(journeyState = JourneyState.WAITING),
                        isLocalStationMapVisible = false,
                        simulatedGpsStep = it.simulatedGpsStep + 1
                    )
                }
            }
            JourneyState.OFF_ROUTE -> {
                // Auto reroute
                performReroute()
            }
            JourneyState.REROUTING -> {
                _uiState.update {
                    it.copy(
                        activeJourney = journey.copy(journeyState = JourneyState.WALKING_TO_STOP),
                        simulatedGpsStep = it.simulatedGpsStep + 1
                    )
                }
            }
            JourneyState.ARRIVED -> {
                // Already arrived
            }
            else -> {}
        }
    }

    fun simulateOffRoute() {
        val state = _uiState.value
        val journey = state.activeJourney ?: return
        _uiState.update {
            it.copy(
                activeJourney = journey.copy(
                    journeyState = JourneyState.OFF_ROUTE,
                    isOffRoute = true
                )
            )
        }
    }

    fun performReroute() {
        val state = _uiState.value
        val journey = state.activeJourney ?: return
        val currentLoc = TransitLocation(
            id = "loc_deviated",
            name = "Dekat Stasiun Tebet (Keluar Rute)",
            address = "Tebet, Jakarta Selatan",
            latitude = -6.2263,
            longitude = 106.8580
        )
        val newRoute = repository.calculateReroute(currentLoc, journey.destination)

        val updatedJourney = journey.copy(
            selectedRoute = newRoute,
            currentStepIndex = 0,
            currentStopIndex = 0,
            journeyState = JourneyState.REROUTING,
            isOffRoute = false,
            estimatedArrival = "41 menit lagi"
        )

        _uiState.update {
            it.copy(
                activeJourney = updatedJourney,
                isRerouted = true,
                rerouteBannerMessage = "Rute diperbarui. Kamu tidak perlu kembali ke titik sebelumnya."
            )
        }
    }

    fun showStationMapForCurrentStep() {
        val state = _uiState.value
        val journey = state.activeJourney
        val stationId = journey?.selectedRoute?.segments?.getOrNull(journey.currentStepIndex)?.destinationStop?.id
            ?: "s_b2"
        val map = repository.getStationMap(stationId) ?: repository.getStationMap("s_b2")
        _uiState.update {
            it.copy(activeStationMap = map, isLocalStationMapVisible = true)
        }
    }

    fun dismissStationMap() {
        _uiState.update { it.copy(isLocalStationMapVisible = false) }
    }

    fun toggleOverviewSheet() {
        _uiState.update { it.copy(showOverviewSheet = !it.showOverviewSheet) }
    }

    fun toggleHelpDialog() {
        _uiState.update { it.copy(showHelpDialog = !it.showHelpDialog) }
    }

    fun submitFeedback(rating: String, emoji: String, comment: String) {
        val state = _uiState.value
        val journey = state.activeJourney
        val feedback = JourneyFeedback(
            rating = rating,
            ratingEmoji = emoji,
            comment = comment,
            routeTitle = journey?.selectedRoute?.title ?: "Perjalanan ke Metland",
            durationMinutes = journey?.selectedRoute?.totalDurationMinutes ?: 72,
            fare = journey?.selectedRoute?.totalFare ?: 8000
        )
        viewModelScope.launch {
            repository.submitFeedback(feedback)
            _uiState.update { it.copy(feedbackSubmitted = true) }
        }
    }

    fun resetToHome() {
        _uiState.update {
            it.copy(
                currentScreen = AppScreen.HOME,
                backStack = listOf(AppScreen.HOME),
                activeJourney = null,
                selectedDestination = null,
                selectedRoute = null,
                isLocalStationMapVisible = false,
                showOverviewSheet = false,
                feedbackSubmitted = false,
                isRerouted = false,
                rerouteBannerMessage = null
            )
        }
    }

    private fun triggerVibration() {
        try {
            val context = getApplication<Application>()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createWaveform(longArrayOf(0, 350, 150, 350), -1)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                vibrator?.vibrate(
                    VibrationEffect.createWaveform(longArrayOf(0, 350, 150, 350), -1)
                )
            }
        } catch (_: Exception) {
            // Ignore if vibrator not supported or test runner
        }
    }
}
