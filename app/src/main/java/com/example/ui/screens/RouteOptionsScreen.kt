package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import com.example.model.DestinationItem
import com.example.model.RouteOption
import com.example.model.TransitLocation
import com.example.model.TransportMode
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteOptionsScreen(
    origin: TransitLocation,
    destination: DestinationItem,
    routes: List<RouteOption>,
    selectedRoute: RouteOption?,
    isFirstTimeMode: Boolean,
    onSelectRoute: (RouteOption) -> Unit,
    onStartJourney: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAlternatives by remember { mutableStateOf(!isFirstTimeMode) }
    val recommendedRoute = routes.firstOrNull { it.isRecommended } ?: routes.firstOrNull()
    val alternativeRoutes = routes.filter { it.id != recommendedRoute?.id }

    Scaffold(
        modifier = modifier.testTag("route_options_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Pilihan Rute",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${origin.name} ➔ ${destination.name}",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeutralTextSecondary,
                            maxLines = 1
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("route_options_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .windowInsetsPadding(WindowInsets.navigationBars),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Rute Terpilih:",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeutralTextSecondary
                        )
                        Text(
                            text = selectedRoute?.summaryRoute ?: "Pilih rute",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = NeutralTextPrimary,
                            maxLines = 1
                        )
                        Text(
                            text = "${selectedRoute?.totalDurationMinutes ?: 0} mnt • Rp ${selectedRoute?.totalFare ?: 0}",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = TjBlue
                        )
                    }

                    Button(
                        onClick = onStartJourney,
                        enabled = selectedRoute != null,
                        modifier = Modifier
                            .height(50.dp)
                            .testTag("btn_mulai_perjalanan"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TjBlue)
                    ) {
                        Text(
                            text = "Mulai Perjalanan",
                            fontWeight = FontWeight.Bold,
                            fontSize = if (isFirstTimeMode) 16.sp else 14.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                if (isFirstTimeMode) {
                    // First-Time Mode prominent guidance note
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFE8F1FC),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = TjBlue, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Kami memilihkan rute paling ramah untuk kamu yang baru pertama kali naik transportasi umum di rute ini.",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                                color = TjBlueDark
                            )
                        }
                    }
                }
            }

            // RECOMMENDED ROUTE (Dominant Visual Priority)
            if (recommendedRoute != null) {
                item {
                    val isSelected = selectedRoute?.id == recommendedRoute.id
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectRoute(recommendedRoute) }
                            .testTag("recommended_route_card"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            2.dp,
                            if (isSelected) TjBlue else Color(0xFF90CAF9)
                        ),
                        elevation = CardDefaults.cardElevation(if (isSelected) 6.dp else 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Badge
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = TransitSuccess,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Rekomendasi Pemula",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White
                                        )
                                    }
                                }

                                if (isSelected) {
                                    Surface(
                                        color = TjBlueLight,
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = "✓ Dipilih",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = TjBlueDark,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Route Summary
                            Text(
                                text = recommendedRoute.summaryRoute,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = if (isFirstTimeMode) 20.sp else 18.sp
                                ),
                                color = NeutralTextPrimary
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // Badge info: "Easiest route for first-time users"
                            Surface(
                                color = Color(0xFFFFF8E1),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "★ ${recommendedRoute.recommendedBadgeText ?: "Paling Mudah"}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFFF57F17),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Route Metrics: Duration, Fare, Transfers, Walking
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                RouteMetricItem(icon = Icons.Default.Schedule, label = "Durasi", value = "${recommendedRoute.totalDurationMinutes} mnt")
                                RouteMetricItem(icon = Icons.Default.Payments, label = "Ongkos", value = "Rp ${recommendedRoute.totalFare}")
                                RouteMetricItem(icon = Icons.Default.TransferWithinAStation, label = "Transit", value = "${recommendedRoute.transferCount} kali")
                                RouteMetricItem(icon = Icons.Default.DirectionsWalk, label = "Jalan Kaki", value = "${recommendedRoute.walkingTimeMinutes} mnt")
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Beginner Friendly Advice
                            Surface(
                                color = BackgroundLight,
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = "💡 ${recommendedRoute.beginnerAdvice}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                    color = NeutralTextSecondary,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    onSelectRoute(recommendedRoute)
                                    onStartJourney()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("btn_pilih_rute_rekomendasi"),
                                colors = ButtonDefaults.buttonColors(containerColor = TjBlue),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Pilih Rute Ini", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Toggle Alternatives Button (First-Time Mode Rule)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(
                        onClick = { showAlternatives = !showAlternatives },
                        modifier = Modifier.testTag("btn_toggle_alternatives")
                    ) {
                        Text(
                            text = if (showAlternatives) "Sembunyikan Rute Alternatif ▲" else "Lihat Alternatif Rute Lain (${alternativeRoutes.size}) ▼",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = TjBlue
                        )
                    }
                }
            }

            // Alternative Routes (Subordinate Visual Priority)
            if (showAlternatives) {
                items(alternativeRoutes) { alt ->
                    val isSelected = selectedRoute?.id == alt.id
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectRoute(alt) }
                            .testTag("alt_route_${alt.id}"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) TjBlue else NeutralBorder
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = alt.title,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = NeutralTextPrimary
                                )
                                if (alt.recommendedBadgeText != null) {
                                    Surface(
                                        color = Color(0xFFF1F5F9),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = alt.recommendedBadgeText,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = NeutralTextSecondary,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = alt.summaryRoute,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = TjBlueDark
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${alt.totalDurationMinutes} mnt • Rp ${alt.totalFare} • ${alt.transferCount} transit",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = NeutralTextSecondary
                                )
                                Text(
                                    text = "Jalan: ${alt.walkingDistanceMeters} m",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NeutralTextSecondary
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = alt.beginnerAdvice,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = NeutralTextSecondary
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedButton(
                                onClick = { onSelectRoute(alt) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(38.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = if (isSelected) ButtonDefaults.outlinedButtonColors(
                                    containerColor = TjBlueLight,
                                    contentColor = TjBlueDark
                                ) else ButtonDefaults.outlinedButtonColors()
                            ) {
                                Text(if (isSelected) "✓ Rute Dipilih" else "Pilih Rute Ini", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun RouteMetricItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = TjBlue, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = NeutralTextSecondary)
        Text(text = value, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = NeutralTextPrimary)
    }
}
