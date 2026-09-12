package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.model.TransportMode
import com.example.ui.theme.*

@Composable
fun StepGuidanceCard(
    journey: ActiveJourneySession,
    isFirstTimeMode: Boolean,
    onAdvanceStep: () -> Unit,
    modifier: Modifier = Modifier
) {
    val route = journey.selectedRoute
    val currentStepIndex = journey.currentStepIndex
    val currentSegment = route.segments.getOrNull(currentStepIndex)
    val totalSteps = route.segments.size
    val progress = ((currentStepIndex + 0.5f) / totalSteps).coerceIn(0.1f, 1.0f)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("step_guidance_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Priority Tag: "APA YANG HARUS SAYA LAKUKAN SEKARANG?"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = TjBlueLight,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Langkah ${currentStepIndex + 1} dari $totalSteps • SEKARANG",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = TjBlueDark,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = "Perkiraan: ${journey.estimatedArrival}",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeutralTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Primary Action Instruction (High Contrast & Clear Typography)
            val actionTitle = when (journey.journeyState) {
                JourneyState.WALKING_TO_STOP -> currentSegment?.instructionForBeginner ?: "Jalan menuju halte"
                JourneyState.WAITING -> "Tunggu armada di ${currentSegment?.originStop?.name}"
                JourneyState.ON_VEHICLE -> "Tetap di kendaraan (${currentSegment?.lineName})"
                JourneyState.APPROACHING_STOP -> "Bersiap turun di ${currentSegment?.destinationStop?.name}!"
                JourneyState.TRANSFER -> "Transit antarmoda di ${currentSegment?.originStop?.name}"
                JourneyState.OFF_ROUTE -> "Kamu berada di luar jalur yang direncanakan"
                JourneyState.REROUTING -> "Menyesuaikan rute baru..."
                JourneyState.ARRIVED -> "Kamu telah sampai di tujuan!"
                else -> currentSegment?.instructionForBeginner ?: "Lanjutkan perjalanan"
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(
                            when (currentSegment?.mode) {
                                TransportMode.TRANSJAKARTA -> ColorTransJakarta
                                TransportMode.KRL -> ColorKrl
                                TransportMode.MRT -> ColorMrt
                                TransportMode.LRT -> ColorLrt
                                else -> ColorWalking
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    val icon = when (currentSegment?.mode) {
                        TransportMode.TRANSJAKARTA -> Icons.Default.DirectionsBus
                        TransportMode.KRL -> Icons.Default.Train
                        TransportMode.MRT -> Icons.Default.Subway
                        TransportMode.LRT -> Icons.Default.Tram
                        else -> Icons.Default.DirectionsWalk
                    }
                    Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = actionTitle,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = if (isFirstTimeMode) 19.sp else 16.sp
                        ),
                        color = NeutralTextPrimary
                    )
                    if (currentSegment != null) {
                        Text(
                            text = "${currentSegment.lineName} • ${currentSegment.vehicleNumberOrTrack}",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = NeutralTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Live Trip Progress within current leg (Stops remaining)
            if (journey.journeyState == JourneyState.ON_VEHICLE && currentSegment != null && currentSegment.intermediateStops.isNotEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        val currentStopName = if (journey.currentStopIndex == 0) {
                            currentSegment.originStop.name
                        } else {
                            currentSegment.intermediateStops.getOrNull(journey.currentStopIndex - 1)?.name ?: "Dalam perjalanan"
                        }
                        val nextStopName = currentSegment.intermediateStops.getOrNull(journey.currentStopIndex)?.name
                            ?: currentSegment.destinationStop.name
                        val stopsLeft = currentSegment.intermediateStops.size - journey.currentStopIndex + 1

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Sekarang: $currentStopName",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = TjBlueDark
                            )
                            Text(
                                text = "$stopsLeft halte lagi",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = TjBlue
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Berikutnya: $nextStopName",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = if (isFirstTimeMode) 15.sp else 13.sp
                            ),
                            color = NeutralTextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "“Tenang, kami akan memberi tahu saat waktunya turun.”",
                            style = MaterialTheme.typography.labelSmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                            color = TransitSuccess
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Mode Pertama Kali Helpful Context Note
            if (isFirstTimeMode && currentSegment?.tip != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFFFF9C4),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.TipsAndUpdates, contentDescription = null, tint = Color(0xFFF57F17), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = currentSegment.tip,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp
                            ),
                            color = Color(0xFF5D4037)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Overall Trip Progress Bar
            Column(modifier = Modifier.fillMaxWidth()) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = TjBlue,
                    trackColor = Color(0xFFE2E8F0)
                )
            }
        }
    }
}
