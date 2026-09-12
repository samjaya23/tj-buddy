package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.model.DestinationItem
import com.example.model.DestinationType
import com.example.model.TransportMode
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchDestinationScreen(
    searchQuery: String,
    searchResults: List<DestinationItem>,
    isFirstTimeMode: Boolean,
    onQueryChanged: (String) -> Unit,
    onSelectDestination: (DestinationItem) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.testTag("search_destination_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Pilih Tujuan",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("search_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = BackgroundLight
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Search Text Field with Auto-complete
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onQueryChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("destination_search_input"),
                placeholder = {
                    Text(
                        text = "Ketik halte, stasiun, atau landmark...",
                        fontSize = if (isFirstTimeMode) 16.sp else 14.sp
                    )
                },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = TjBlue)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onQueryChanged("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Hapus")
                        }
                    }
                },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TjBlue,
                    unfocusedBorderColor = NeutralBorder,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Category Filters
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = searchQuery.isEmpty(),
                    onClick = { onQueryChanged("") },
                    label = { Text("Semua", fontSize = 12.sp) }
                )
                FilterChip(
                    selected = searchQuery.contains("Stasiun", ignoreCase = true),
                    onClick = { onQueryChanged("Stasiun") },
                    label = { Text("Stasiun KRL/MRT", fontSize = 12.sp) }
                )
                FilterChip(
                    selected = searchQuery.contains("Halte", ignoreCase = true),
                    onClick = { onQueryChanged("Halte") },
                    label = { Text("Halte Busway", fontSize = 12.sp) }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = if (searchQuery.isBlank()) "Rekomendasi Tujuan Populer" else "Hasil Pencarian (${searchResults.size})",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = NeutralTextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (searchResults.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = NeutralTextSecondary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tujuan tidak ditemukan",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                            color = NeutralTextPrimary
                        )
                        Text(
                            text = "Coba cari nama halte seperti 'Tosari', 'Dukuh Atas', atau 'Metland'",
                            style = MaterialTheme.typography.bodySmall,
                            color = NeutralTextSecondary
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(searchResults) { dest ->
                        SearchResultItem(
                            destination = dest,
                            isFirstTimeMode = isFirstTimeMode,
                            onClick = { onSelectDestination(dest) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchResultItem(
    destination: DestinationItem,
    isFirstTimeMode: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag("search_result_${destination.id}"),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, NeutralBorder),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val (icon, bgCol, iconCol) = when (destination.type) {
                DestinationType.STATION -> Triple(Icons.Default.Train, Color(0xFFFFF3E0), ColorKrl)
                DestinationType.HALTE -> Triple(Icons.Default.DirectionsBus, Color(0xFFE8F1FC), ColorTransJakarta)
                DestinationType.LANDMARK -> Triple(Icons.Default.LocationCity, Color(0xFFE0F2F1), ColorMrt)
                else -> Triple(Icons.Default.Place, Color(0xFFF1F5F9), NeutralTextPrimary)
            }

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(bgCol),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconCol, modifier = Modifier.size(22.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = destination.name,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = if (isFirstTimeMode) 17.sp else 15.sp
                    ),
                    color = NeutralTextPrimary
                )
                Text(
                    text = destination.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = NeutralTextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    destination.connectedModes.forEach { mode ->
                        Surface(
                            color = when (mode) {
                                TransportMode.TRANSJAKARTA -> ColorTransJakarta.copy(alpha = 0.15f)
                                TransportMode.KRL -> ColorKrl.copy(alpha = 0.15f)
                                TransportMode.MRT -> ColorMrt.copy(alpha = 0.15f)
                                TransportMode.LRT -> ColorLrt.copy(alpha = 0.15f)
                                else -> Color(0xFFEEEEEE)
                            },
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = mode.shortCode,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (mode) {
                                    TransportMode.TRANSJAKARTA -> ColorTransJakarta
                                    TransportMode.KRL -> ColorKrl
                                    TransportMode.MRT -> ColorMrt
                                    TransportMode.LRT -> ColorLrt
                                    else -> NeutralTextPrimary
                                },
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = NeutralTextSecondary
            )
        }
    }
}
