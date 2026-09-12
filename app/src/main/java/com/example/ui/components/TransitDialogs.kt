package com.example.ui.components

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
import com.example.ui.theme.*

@Composable
fun ExitConfirmDialog(
    onDismiss: () -> Unit,
    onConfirmExit: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("exit_confirm_dialog"),
        shape = RoundedCornerShape(20.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.WarningAmber,
                    contentDescription = null,
                    tint = TransitCaution,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Keluar dari navigasi?",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        },
        text = {
            Text(
                text = "Perjalanan kamu masih berlangsung. Jika kamu keluar sekarang, sesi panduan aktif akan dihentikan.",
                style = MaterialTheme.typography.bodyMedium,
                color = NeutralTextSecondary
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = TjBlue),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("btn_keep_navigating")
            ) {
                Text("Tetap Navigasi", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onConfirmExit,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TransitError),
                modifier = Modifier.testTag("btn_confirm_exit")
            ) {
                Text("Keluar", fontWeight = FontWeight.SemiBold)
            }
        }
    )
}

@Composable
fun HelpDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("help_dialog"),
        shape = RoundedCornerShape(20.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(TjBlueLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.SupportAgent,
                        contentDescription = null,
                        tint = TjBlue,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Bantuan Penumpang Pemula",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                HelpItem(
                    title = "1. Saldo & Kartu E-Money",
                    description = "Gunakan kartu yang sama saat tap masuk dan tap keluar. Saldo aman minimal Rp 10.000."
                )
                HelpItem(
                    title = "2. Petugas di Lapangan",
                    description = "Petugas TransJakarta dan KRL berseragam siap membantu. Jangan sungkan bertanya peron atau pintu keluar."
                )
                HelpItem(
                    title = "3. Salah Naik atau Kebablasan?",
                    description = "Tenang, jangan panik! Turun di halte/stasiun berikutnya. Cukup menyeberang peron ke arah sebaliknya tanpa bayar lagi."
                )
                HelpItem(
                    title = "4. Layanan Pelanggan 24 Jam",
                    description = "TransJakarta: 1500-102 • KRL Commuter: 121"
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = TjBlue),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("btn_close_help")
            ) {
                Text("Tutup Panduan", fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
private fun HelpItem(title: String, description: String) {
    Surface(
        color = BackgroundLight,
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = TjBlueDark
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = NeutralTextPrimary
            )
        }
    }
}
