package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.StationMap
import com.example.model.WayfindingStep
import com.example.ui.theme.*

@Composable
fun LocalStationHalteMap(
    stationMap: StationMap,
    isFirstTimeMode: Boolean,
    onRecenter: () -> Unit,
    onReturnToGuidance: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_local")
    val pulseSize by infiniteTransition.animateFloat(
        initialValue = 10f,
        targetValue = 22f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_local_val"
    )

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("local_station_map_screen"),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header: "Kamu sudah sampai di [halte/station]"
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = TransitSuccessBg,
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(TransitSuccess),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Orientasi Halte / Stasiun",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = TransitSuccess
                        )
                        Text(
                            text = stationMap.youAreHereNote,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = if (isFirstTimeMode) 18.sp else 16.sp
                            ),
                            color = NeutralTextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Station Schematic Floorplan Canvas
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height

                        // Draw Station schematic building outline
                        drawRoundRect(
                            color = Color(0xFFE2E8F0),
                            topLeft = Offset(w * 0.05f, h * 0.1f),
                            size = Size(w * 0.9f, h * 0.8f),
                            cornerRadius = CornerRadius(16f, 16f)
                        )

                        // 1. Busway Arrival Platform (Lantai 2)
                        drawRoundRect(
                            color = ColorTransJakarta.copy(alpha = 0.2f),
                            topLeft = Offset(w * 0.1f, h * 0.18f),
                            size = Size(w * 0.25f, h * 0.45f),
                            cornerRadius = CornerRadius(10f, 10f)
                        )
                        drawRoundRect(
                            color = ColorTransJakarta,
                            topLeft = Offset(w * 0.1f, h * 0.18f),
                            size = Size(w * 0.25f, h * 0.45f),
                            cornerRadius = CornerRadius(10f, 10f),
                            style = Stroke(width = 2.5f)
                        )

                        // 2. JPO Connector Bridge (Selasar Beratap)
                        val bridgePath = Path().apply {
                            moveTo(w * 0.35f, h * 0.4f)
                            lineTo(w * 0.6f, h * 0.4f)
                            lineTo(w * 0.6f, h * 0.6f)
                            lineTo(w * 0.72f, h * 0.6f)
                        }
                        drawPath(
                            path = bridgePath,
                            color = Color(0xFF00E5FF),
                            style = Stroke(
                                width = 8f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 8f), 0f)
                            )
                        )

                        // 3. Escalator & Stairs Zone
                        drawRoundRect(
                            color = Color(0xFFFFD54F).copy(alpha = 0.4f),
                            topLeft = Offset(w * 0.52f, h * 0.32f),
                            size = Size(w * 0.14f, h * 0.22f),
                            cornerRadius = CornerRadius(6f, 6f)
                        )

                        // 4. Gate Tap-In KRL
                        drawRoundRect(
                            color = Color(0xFF78909C),
                            topLeft = Offset(w * 0.68f, h * 0.52f),
                            size = Size(w * 0.08f, h * 0.2f),
                            cornerRadius = CornerRadius(4f, 4f)
                        )

                        // 5. KRL Platform 2 (Peron 2 Arah Bekasi)
                        drawRoundRect(
                            color = ColorKrl.copy(alpha = 0.2f),
                            topLeft = Offset(w * 0.74f, h * 0.35f),
                            size = Size(w * 0.18f, h * 0.5f),
                            cornerRadius = CornerRadius(10f, 10f)
                        )
                        drawRoundRect(
                            color = ColorKrl,
                            topLeft = Offset(w * 0.74f, h * 0.35f),
                            size = Size(w * 0.18f, h * 0.5f),
                            cornerRadius = CornerRadius(10f, 10f),
                            style = Stroke(width = 2.5f)
                        )

                        // YOU ARE HERE Pulse Marker at Busway Platform
                        val youAreHerePos = Offset(w * 0.22f, h * 0.4f)
                        drawCircle(
                            color = Color(0xFFFF3D00).copy(alpha = 0.35f),
                            radius = pulseSize,
                            center = youAreHerePos
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 9f,
                            center = youAreHerePos
                        )
                        drawCircle(
                            color = Color(0xFFFF3D00),
                            radius = 6f,
                            center = youAreHerePos
                        )

                        // Platform 2 Goal Marker
                        val goalPos = Offset(w * 0.83f, h * 0.6f)
                        drawCircle(
                            color = TransitSuccess,
                            radius = 12f,
                            center = goalPos,
                            style = Stroke(width = 3.5f)
                        )
                    }

                    // Floating station schematic labels
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(10.dp)
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Lantai 2: Halte B2 ➔ JPO Kaca ➔ Gate KRL Sudirman (Peron 2)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = NeutralTextPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Step-by-Step Wayfinding Guidance (YOU ARE HERE -> Platform 2)
            Text(
                text = "Panduan Langkah Fisik di Halte / Stasiun:",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = NeutralTextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(stationMap.wayfindingSteps) { step ->
                    WayfindingStepItem(step = step, isFirstTimeMode = isFirstTimeMode)
                }

                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = TjBlueLight),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = TjBlueDark,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Jangan ragu bertanya ke petugas bertopi merah atau staf halte jika bingung. Mereka sangat ramah!",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = if (isFirstTimeMode) 13.sp else 12.sp
                                ),
                                color = TjBlueDark
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bottom CTA Buttons: "Lokasi Saya" and "Kembali ke Panduan"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onRecenter,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("btn_local_my_location"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.MyLocation, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("◎ Lokasi Saya", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onReturnToGuidance,
                    modifier = Modifier
                        .weight(1.3f)
                        .height(48.dp)
                        .testTag("btn_return_to_guidance"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TjBlue)
                ) {
                    Text("Kembali ke Panduan", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun WayfindingStepItem(
    step: WayfindingStep,
    isFirstTimeMode: Boolean
) {
    val isYouAreHere = step.instruction.contains("YOU ARE HERE", ignoreCase = true)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (isYouAreHere) Color(0xFFFFEBEE) else MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        border = if (isYouAreHere) androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFF3D00)) else androidx.compose.foundation.BorderStroke(1.dp, NeutralBorder),
        shadowElevation = if (isYouAreHere) 2.dp else 0.dp
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isYouAreHere -> Color(0xFFFF3D00)
                            step.iconType == "platform" -> ColorKrl
                            step.iconType == "gate" -> TjBlue
                            else -> Color(0xFFE2E8F0)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isYouAreHere) {
                    Icon(Icons.Default.Place, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                } else {
                    val icon = when (step.iconType) {
                        "straight" -> Icons.Default.Straight
                        "right" -> Icons.Default.TurnRight
                        "escalator" -> Icons.Default.Elevator
                        "gate" -> Icons.Default.CreditCard
                        "platform" -> Icons.Default.Train
                        else -> Icons.Default.Navigation
                    }
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = if (step.iconType == "platform" || step.iconType == "gate") Color.White else NeutralTextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = step.instruction,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = if (isFirstTimeMode) 16.sp else 14.sp
                    ),
                    color = if (isYouAreHere) Color(0xFFD32F2F) else NeutralTextPrimary
                )
                if (step.detail.isNotBlank()) {
                    Text(
                        text = step.detail,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = if (isFirstTimeMode) 13.sp else 12.sp
                        ),
                        color = NeutralTextSecondary
                    )
                }
            }

            if (step.order < 5) {
                Icon(
                    Icons.Default.ArrowDownward,
                    contentDescription = null,
                    tint = NeutralTextSecondary.copy(alpha = 0.5f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
