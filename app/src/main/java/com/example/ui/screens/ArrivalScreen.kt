package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.model.TransportMode
import com.example.ui.theme.*

@Composable
fun ArrivalScreen(
    journey: ActiveJourneySession?,
    isFirstTimeMode: Boolean,
    feedbackSubmitted: Boolean,
    onSubmitFeedback: (rating: String, emoji: String, comment: String) -> Unit,
    onFinishAndHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedRating by remember { mutableStateOf<String?>(null) }
    var selectedEmoji by remember { mutableStateOf("") }
    var commentText by remember { mutableStateOf("") }

    val route = journey?.selectedRoute
    val durationMinutes = route?.totalDurationMinutes ?: 72
    val fare = route?.totalFare ?: 8000
    val transfers = route?.transferCount ?: 2
    val modes = route?.modesUsed ?: listOf(TransportMode.TRANSJAKARTA, TransportMode.KRL)

    Scaffold(
        modifier = modifier.testTag("arrival_screen"),
        containerColor = BackgroundLight
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                // Celebration Graphic
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(TransitSuccessBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🎉", fontSize = 48.sp)
                }
            }

            item {
                Text(
                    text = "Sampai! 🎉",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = if (isFirstTimeMode) 32.sp else 28.sp
                    ),
                    color = NeutralTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Kamu berhasil sampai di ${journey?.destination?.name ?: "tujuan"}.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = if (isFirstTimeMode) 18.sp else 16.sp
                    ),
                    color = TransitSuccess
                )
                Text(
                    text = "Selamat, perjalanan perdana kamu selesai dengan lancar!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NeutralTextSecondary
                )
            }

            // Summary Metrics Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(3.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Ringkasan Perjalanan",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = NeutralTextPrimary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            SummaryStat(label = "Total Waktu", value = "$durationMinutes Menit")
                            SummaryStat(label = "Total Ongkos", value = "Rp $fare")
                            SummaryStat(label = "Transit", value = "$transfers Kali")
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = NeutralBorder)
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Moda Digunakan:",
                                style = MaterialTheme.typography.labelMedium,
                                color = NeutralTextSecondary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                modes.forEach { mode ->
                                    Surface(
                                        color = TjBlueLight,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = mode.displayName,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TjBlueDark,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Interactive Feedback Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("feedback_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (feedbackSubmitted) TransitSuccessBg else MaterialTheme.colorScheme.surface
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (feedbackSubmitted) TransitSuccess else NeutralBorder
                    )
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        if (feedbackSubmitted) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TransitSuccess, modifier = Modifier.size(28.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Terima kasih atas ulasanmu!",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = TransitSuccess
                                    )
                                    Text(
                                        text = "Ulasanmu membantu kami membuat panduan yang semakin mudah bagi pemula.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = NeutralTextPrimary
                                    )
                                }
                            }
                        } else {
                            Text(
                                text = "Perjalanan tadi mudah diikuti?",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = if (isFirstTimeMode) 18.sp else 16.sp
                                ),
                                color = NeutralTextPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Bantu kami memahami pengalaman perjalanan komuter pertamamu.",
                                style = MaterialTheme.typography.bodySmall,
                                color = NeutralTextSecondary
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Options: 😊 Mudah, 😐 Lumayan, 😕 Membingungkan
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                FeedbackOptionButton(
                                    emoji = "😊",
                                    label = "Mudah",
                                    ratingKey = "EASY",
                                    isSelected = selectedRating == "EASY",
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        selectedRating = "EASY"
                                        selectedEmoji = "😊"
                                    }
                                )
                                FeedbackOptionButton(
                                    emoji = "😐",
                                    label = "Lumayan",
                                    ratingKey = "MODERATE",
                                    isSelected = selectedRating == "MODERATE",
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        selectedRating = "MODERATE"
                                        selectedEmoji = "😐"
                                    }
                                )
                                FeedbackOptionButton(
                                    emoji = "😕",
                                    label = "Membingungkan",
                                    ratingKey = "CONFUSING",
                                    isSelected = selectedRating == "CONFUSING",
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        selectedRating = "CONFUSING"
                                        selectedEmoji = "😕"
                                    }
                                )
                            }

                            if (selectedRating != null) {
                                Spacer(modifier = Modifier.height(12.dp))
                                OutlinedTextField(
                                    value = commentText,
                                    onValueChange = { commentText = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = { Text("Ceritakan bagian mana yang paling membantu atau membingungkan...", fontSize = 13.sp) },
                                    shape = RoundedCornerShape(12.dp),
                                    maxLines = 3
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = {
                                        onSubmitFeedback(selectedRating!!, selectedEmoji, commentText)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(44.dp)
                                        .testTag("btn_submit_feedback"),
                                    colors = ButtonDefaults.buttonColors(containerColor = TjBlue),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Simpan Ulasan", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Primary Button: Kembali ke Beranda
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Button(
                    onClick = onFinishAndHome,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_finish_and_home"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TjBlue)
                ) {
                    Icon(Icons.Default.Home, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Selesai & Kembali ke Beranda",
                        fontWeight = FontWeight.Bold,
                        fontSize = if (isFirstTimeMode) 16.sp else 15.sp
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun FeedbackOptionButton(
    emoji: String,
    label: String,
    ratingKey: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .testTag("feedback_option_$ratingKey"),
        color = if (isSelected) TjBlueLight else Color(0xFFF8FAFC),
        border = androidx.compose.foundation.BorderStroke(
            if (isSelected) 2.dp else 1.dp,
            if (isSelected) TjBlue else NeutralBorder
        )
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = emoji, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = if (isSelected) TjBlueDark else NeutralTextPrimary,
                maxLines = 1
            )
        }
    }
}

@Composable
fun SummaryStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = NeutralTextSecondary)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = NeutralTextPrimary)
    }
}
