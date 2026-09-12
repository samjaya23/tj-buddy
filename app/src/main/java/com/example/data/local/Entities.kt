package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "feedback_history")
data class FeedbackEntity(
    @PrimaryKey val id: String,
    val rating: String,
    val ratingEmoji: String,
    val comment: String,
    val routeTitle: String,
    val durationMinutes: Int,
    val fare: Int,
    val submittedAtEpochMs: Long
)

@Entity(tableName = "recent_destinations")
data class RecentDestinationEntity(
    @PrimaryKey val id: String,
    val name: String,
    val subtitle: String,
    val type: String,
    val latitude: Double,
    val longitude: Double,
    val lastSearchedAt: Long
)
