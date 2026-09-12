package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.ExitConfirmDialog
import com.example.ui.components.HelpDialog
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AppScreen
import com.example.viewmodel.TJBuddyViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                TJBuddyApp()
            }
        }
    }
}

@Composable
fun TJBuddyApp(
    viewModel: TJBuddyViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Global back handling according to requirements
    BackHandler {
        val handled = viewModel.handleBack()
        if (!handled) {
            // Let activity handle finish if at root of backstack
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        when (uiState.currentScreen) {
            AppScreen.HOME -> {
                HomeScreen(
                    uiState = uiState,
                    onNavigateToSearch = { viewModel.navigateTo(AppScreen.SEARCH) },
                    onSelectDestination = { dest -> viewModel.selectDestination(dest) },
                    onToggleFirstTimeMode = { viewModel.toggleFirstTimeMode() },
                    onSelectQuickRidwanScenario = {
                        val metlandDest = uiState.popularDestinations.firstOrNull { it.id == "dest_metland" }
                            ?: uiState.popularDestinations.first()
                        viewModel.selectDestination(metlandDest)
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            AppScreen.SEARCH -> {
                SearchDestinationScreen(
                    searchQuery = uiState.searchQuery,
                    searchResults = uiState.searchResults,
                    isFirstTimeMode = uiState.isFirstTimeMode,
                    onQueryChanged = { viewModel.onSearchQueryChanged(it) },
                    onSelectDestination = { dest -> viewModel.selectDestination(dest) },
                    onBack = { viewModel.handleBack() },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            AppScreen.ROUTE_OPTIONS -> {
                val dest = uiState.selectedDestination
                if (dest != null) {
                    RouteOptionsScreen(
                        origin = uiState.origin,
                        destination = dest,
                        routes = uiState.routeOptions,
                        selectedRoute = uiState.selectedRoute,
                        isFirstTimeMode = uiState.isFirstTimeMode,
                        onSelectRoute = { viewModel.selectRoute(it) },
                        onStartJourney = { viewModel.startJourney() },
                        onBack = { viewModel.handleBack() },
                        modifier = Modifier.padding(innerPadding)
                    )
                } else {
                    viewModel.navigateTo(AppScreen.HOME)
                }
            }
            AppScreen.ACTIVE_NAV, AppScreen.LOCAL_STATION_MAP -> {
                ActiveJourneyScreen(
                    uiState = uiState,
                    onBack = { viewModel.handleBack() },
                    onAdvanceStep = { viewModel.advanceJourneyStep() },
                    onSimulateOffRoute = { viewModel.simulateOffRoute() },
                    onReroute = { viewModel.performReroute() },
                    onOpenHalteMap = { viewModel.showStationMapForCurrentStep() },
                    onCloseHalteMap = { viewModel.dismissStationMap() },
                    onToggleOverview = { viewModel.toggleOverviewSheet() },
                    onToggleHelp = { viewModel.toggleHelpDialog() },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            AppScreen.ARRIVAL -> {
                ArrivalScreen(
                    journey = uiState.activeJourney,
                    isFirstTimeMode = uiState.isFirstTimeMode,
                    feedbackSubmitted = uiState.feedbackSubmitted,
                    onSubmitFeedback = { rating, emoji, comment ->
                        viewModel.submitFeedback(rating, emoji, comment)
                    },
                    onFinishAndHome = { viewModel.resetToHome() },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }

        // Global Modals
        if (uiState.showExitConfirmDialog) {
            ExitConfirmDialog(
                onDismiss = { viewModel.dismissExitDialog() },
                onConfirmExit = { viewModel.confirmExitJourney() }
            )
        }

        if (uiState.showHelpDialog) {
            HelpDialog(
                onDismiss = { viewModel.toggleHelpDialog() }
            )
        }
    }
}
