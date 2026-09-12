package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ActiveJourneySession
import com.example.model.JourneyState
import com.example.model.StationMap
import com.example.model.TransportMode
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveJourneyScreen(
    uiState: UiState,
    onBack: () -> Unit,
    onAdvanceStep: () -> Unit,
    onSimulateOffRoute: () -> Unit,
    onReroute: () -> Unit,
    onOpenHalteMap: () -> Unit,
    onCloseHalteMap: () -> Unit,
    onToggleOverview: () -> Unit,
    onToggleHelp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val journey = uiState.activeJourney ?: return
    val currentSegment = journey.selectedRoute.segments.getOrNull(journey.currentStepIndex)

    // Check if Local Station Map is requested or active
    if (uiState.isLocalStationMapVisible && uiState.activeStationMap != null) {
        Scaffold(
            modifier = modifier.testTag("local_station_map_screen_container"),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = uiState.activeStationMap.stationName,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onCloseHalteMap,
                            modifier = Modifier.testTag("halte_map_back_button")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali ke Navigasi")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
                )
            }
        ) { padding ->
            LocalStationHalteMap(
                stationMap = uiState.activeStationMap,
                isFirstTimeMode = uiState.isFirstTimeMode,
                onRecenter = { /* Recenter to local station marker */ },
                onReturnToGuidance = onCloseHalteMap,
                modifier = Modifier.padding(padding)
            )
        }
        return
    }

    Scaffold(
        modifier = modifier.testTag("active_journey_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Menuju: ${journey.destination.name}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            maxLines = 1
                        )
                        Text(
                            text = journey.selectedRoute.summaryRoute,
                            style = MaterialTheme.typography.labelSmall,
                            color = NeutralTextSecondary,
                            maxLines = 1
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("journey_nav_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali / Keluar")
                    }
                },
                actions = {
                    // Help button
                    IconButton(
                        onClick = onToggleHelp,
                        modifier = Modifier.testTag("btn_journey_help")
                    ) {
                        Icon(Icons.Default.HelpOutline, contentDescription = "Bantuan")
                    }
                    // Overview list sheet button
                    IconButton(
                        onClick = onToggleOverview,
                        modifier = Modifier.testTag("btn_journey_overview")
                    ) {
                        Icon(
                            if (uiState.showOverviewSheet) Icons.Default.Map else Icons.Default.FormatListNumbered,
                            contentDescription = "Rangkuman Rute"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = {
            // Persistent Journey Safety & Assistance Footer
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .windowInsetsPadding(WindowInsets.navigationBars),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // "Saya Bingung / Butuh Bantuan"
                    OutlinedButton(
                        onClick = onToggleHelp,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("btn_saya_bingung")
                    ) {
                        Icon(Icons.Default.HelpOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Saya Bingung", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }

                    // "Denah Halte / Stasiun"
                    FilledTonalButton(
                        onClick = onOpenHalteMap,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("btn_denah_halte_bottom")
                    ) {
                        Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Denah Halte", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    // Advance / Action button
                    Button(
                        onClick = onAdvanceStep,
                        colors = ButtonDefaults.buttonColors(containerColor = TjBlue),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("btn_next_action")
                    ) {
                        Text(
                            text = when (journey.journeyState) {
                                JourneyState.WALKING_TO_STOP -> "Sudah di Halte"
                                JourneyState.WAITING -> "Sudah Naik"
                                JourneyState.ON_VEHICLE -> "Lanjut Halte"
                                JourneyState.APPROACHING_STOP -> "Sudah Turun"
                                JourneyState.TRANSFER -> "Selesai Transit"
                                else -> "Lanjut"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        },
        containerColor = BackgroundLight
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(2.dp))
            }

            // GET-OFF ALERT (Shown prominently when approaching user's stop)
            if (journey.journeyState == JourneyState.APPROACHING_STOP) {
                item {
                    GetOffAlertBanner(
                        isVisible = true,
                        nextStopName = currentSegment?.destinationStop?.name ?: "Pemberhentian Berikutnya",
                        onAcknowledge = onAdvanceStep
                    )
                }
            }

            // WRONG ROUTE / OFF-ROUTE DETECTION BANNER
            if (journey.isOffRoute || journey.journeyState == JourneyState.OFF_ROUTE) {
                item {
                    OffRouteBanner(
                        isVisible = true,
                        onReroute = onReroute
                    )
                }
            }

            // REROUTED SUCCESS BANNER
            if (uiState.isRerouted && uiState.rerouteBannerMessage != null) {
                item {
                    ReroutedSuccessBanner(message = uiState.rerouteBannerMessage)
                }
            }

            // PRIMARY "WHAT TO DO NOW" CARD (Emphasized above all else)
            item {
                StepGuidanceCard(
                    journey = journey,
                    isFirstTimeMode = uiState.isFirstTimeMode,
                    onAdvanceStep = onAdvanceStep
                )
            }

            // INTERACTIVE TRANSIT MAP
            item {
                InteractiveTransitMap(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    activeJourney = journey,
                    simulatedGpsStep = uiState.simulatedGpsStep,
                    isFirstTimeMode = uiState.isFirstTimeMode,
                    onAdvanceStep = onAdvanceStep,
                    onSimulateOffRoute = onSimulateOffRoute,
                    onOpenHalteMap = onOpenHalteMap
                )
            }

            // JOURNEY OVERVIEW LIST (Can be toggled or visible below)
            if (uiState.showOverviewSheet) {
                item {
                    Text(
                        text = "Rangkaian Seluruh Perjalanan:",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = NeutralTextPrimary
                    )
                }

                itemsIndexed(journey.selectedRoute.segments) { index, segment ->
                    val isCurrent = index == journey.currentStepIndex
                    val isPast = index < journey.currentStepIndex

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = if (isCurrent) TjBlueLight else MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(12.dp),
                        border = if (isCurrent) androidx.compose.foundation.BorderStroke(1.5.dp, TjBlue) else androidx.compose.foundation.BorderStroke(1.dp, NeutralBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isPast -> TransitSuccess
                                            isCurrent -> TjBlue
                                            else -> Color(0xFFCFD8DC)
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isPast) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                } else {
                                    Text(
                                        text = "${index + 1}",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = segment.instructionForBeginner,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (isCurrent) TjBlueDark else NeutralTextPrimary
                                )
                                Text(
                                    text = "${segment.originStop.name} ➔ ${segment.destinationStop.name}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = NeutralTextSecondary
                                )
                            }

                            if (isCurrent) {
                                Surface(
                                    color = TjBlue,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "SEKARANG",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
