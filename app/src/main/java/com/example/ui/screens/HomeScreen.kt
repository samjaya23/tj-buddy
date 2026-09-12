package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DestinationItem
import com.example.model.DestinationType
import com.example.model.TransportMode
import com.example.ui.theme.*
import com.example.viewmodel.UiState

@Composable
fun HomeScreen(
    uiState: UiState,
    onNavigateToSearch: () -> Unit,
    onSelectDestination: (DestinationItem) -> Unit,
    onToggleFirstTimeMode: () -> Unit,
    onSelectQuickRidwanScenario: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.testTag("home_screen"),
        containerColor = BackgroundLight
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(12.dp))

                // Brand Top Header & First-Time Mode Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(TjBlue, TjBlueDark)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.DirectionsTransit,
                                contentDescription = "TJ Buddy Logo",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "TJ Buddy",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp
                                ),
                                color = TjBlueDark
                            )
                            Text(
                                text = "Sahabat Perjalanan Jakarta",
                                style = MaterialTheme.typography.labelSmall,
                                color = NeutralTextSecondary
                            )
                        }
                    }

                    // Guest Badge for Ridwan
                    Surface(
                        color = TjBlueLight,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.AccountCircle, contentDescription = null, tint = TjBlue, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Tamu: ${uiState.userName}",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = TjBlueDark
                            )
                        }
                    }
                }
            }

            // Mode Pertama Kali Card (High Prominence Switch)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("first_time_mode_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (uiState.isFirstTimeMode) Color(0xFFE8F0FE) else MaterialTheme.colorScheme.surface
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.5.dp,
                        if (uiState.isFirstTimeMode) TjBlue else NeutralBorder
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(if (uiState.isFirstTimeMode) TjBlue else Color(0xFFE2E8F0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Assistant,
                                contentDescription = null,
                                tint = if (uiState.isFirstTimeMode) Color.White else NeutralTextSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Mode Pertama Kali",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (uiState.isFirstTimeMode) TjBlueDark else NeutralTextPrimary
                            )
                            Text(
                                text = if (uiState.isFirstTimeMode)
                                    "Aktif: Huruf besar, petunjuk lebih visual & bebas rasa khawatir"
                                else
                                    "Matikan untuk tampilan rute ringkas standar",
                                style = MaterialTheme.typography.bodySmall,
                                color = NeutralTextSecondary
                            )
                        }

                        Switch(
                            checked = uiState.isFirstTimeMode,
                            onCheckedChange = { onToggleFirstTimeMode() },
                            modifier = Modifier.testTag("switch_first_time_mode"),
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = TjBlue
                            )
                        )
                    }
                }
            }

            // Headline: “Mau ke mana hari ini?”
            item {
                Column {
                    Text(
                        text = "Mau ke mana hari ini?",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = if (uiState.isFirstTimeMode) 28.sp else 24.sp
                        ),
                        color = NeutralTextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Jelajahi TransJakarta, KRL, MRT, dan LRT dengan panduan langkah demi langkah.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NeutralTextSecondary
                    )
                }
            }

            // Current Location Card
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeutralBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(TransitSuccessBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.MyLocation,
                                contentDescription = null,
                                tint = TransitSuccess,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Lokasi Kamu Saat Ini (GPS)",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = TransitSuccess
                            )
                            Text(
                                text = uiState.origin.name,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = NeutralTextPrimary
                            )
                        }
                    }
                }
            }

            // Destination Search Bar Trigger (Clickable)
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onNavigateToSearch() }
                        .testTag("home_search_bar_trigger"),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 3.dp,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, TjBlue.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Cari Tujuan",
                            tint = TjBlue,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Ketik tujuan: stasiun, halte, atau tempat...",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = if (uiState.isFirstTimeMode) 17.sp else 15.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = NeutralTextSecondary
                            )
                        }
                        Button(
                            onClick = onNavigateToSearch,
                            modifier = Modifier
                                .height(38.dp)
                                .testTag("btn_cari_rute_primary"),
                            colors = ButtonDefaults.buttonColors(containerColor = TjBlue),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp)
                        ) {
                            Text("Cari Rute", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }

            // Primary Demonstration User Scenario (Ridwan: Jakarta to Metland / Bekasi)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { onSelectQuickRidwanScenario() }
                        .testTag("ridwan_demo_scenario_card"),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF00377A)
                    ),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = Color(0xFF00E5FF).copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "SKENARIO DEMO: RIDWAN",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                                    color = Color(0xFF00E5FF),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Jakarta Pusat ➔ Metland / Bekasi",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = if (uiState.isFirstTimeMode) 20.sp else 17.sp
                            ),
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Perjalanan komuter pertama kali: TransJakarta B1 ➔ B2 ➔ Transit KRL Cikarang Line ➔ Metland.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.9f)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TransportModeTag(mode = TransportMode.TRANSJAKARTA)
                            TransportModeTag(mode = TransportMode.KRL)
                            Text(
                                text = "• 1 jam 12 min",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFFFFD54F)
                            )
                        }
                    }
                }
            }

            // Integrated Transport Modes Section
            item {
                Text(
                    text = "Moda Transportasi Terintegrasi",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = NeutralTextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        TransportModeInfoChip(
                            name = "TransJakarta",
                            code = "TJ Bus",
                            color = ColorTransJakarta,
                            icon = Icons.Default.DirectionsBus
                        )
                    }
                    item {
                        TransportModeInfoChip(
                            name = "KRL Commuter",
                            code = "Kereta",
                            color = ColorKrl,
                            icon = Icons.Default.Train
                        )
                    }
                    item {
                        TransportModeInfoChip(
                            name = "MRT Jakarta",
                            code = "Ratangga",
                            color = ColorMrt,
                            icon = Icons.Default.Subway
                        )
                    }
                    item {
                        TransportModeInfoChip(
                            name = "LRT Jabodebek",
                            code = "LRT",
                            color = ColorLrt,
                            icon = Icons.Default.Tram
                        )
                    }
                }
            }

            // Recent Destinations Section
            item {
                Text(
                    text = "Tujuan Populer & Riwayat",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = NeutralTextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(uiState.popularDestinations.take(4)) { dest ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onSelectDestination(dest) }
                        .testTag("dest_item_${dest.id}"),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeutralBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(TjBlueLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                when (dest.type) {
                                    DestinationType.STATION -> Icons.Default.Train
                                    DestinationType.HALTE -> Icons.Default.DirectionsBus
                                    else -> Icons.Default.Place
                                },
                                contentDescription = null,
                                tint = TjBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = dest.name,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = if (uiState.isFirstTimeMode) 16.sp else 14.sp
                                ),
                                color = NeutralTextPrimary
                            )
                            Text(
                                text = dest.subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = NeutralTextSecondary
                            )
                        }

                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = NeutralTextSecondary
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun TransportModeTag(mode: TransportMode) {
    Surface(
        color = Color.White.copy(alpha = 0.2f),
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = mode.displayName,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun TransportModeInfoChip(
    name: String,
    code: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = name, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = color)
                Text(text = code, style = MaterialTheme.typography.labelSmall, color = NeutralTextSecondary)
            }
        }
    }
}
