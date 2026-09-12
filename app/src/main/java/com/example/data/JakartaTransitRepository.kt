package com.example.data

import com.example.data.local.AppDatabase
import com.example.data.local.FeedbackEntity
import com.example.data.local.RecentDestinationEntity
import com.example.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class JakartaTransitRepository(
    private val dataSource: TransitDataSource,
    private val database: AppDatabase
) {
    fun getOriginLocation(): TransitLocation = dataSource.getOriginLocation()

    fun getPopularDestinations(): List<DestinationItem> = dataSource.getPopularDestinations()

    fun searchDestinations(query: String): List<DestinationItem> = dataSource.searchDestinations(query)

    fun planRoutes(origin: TransitLocation, destination: DestinationItem): List<RouteOption> =
        dataSource.planRoutes(origin, destination)

    fun calculateReroute(currentLocation: TransitLocation, destination: DestinationItem): RouteOption =
        dataSource.calculateReroute(currentLocation, destination)

    fun getStationMap(stationId: String): StationMap? = dataSource.getStationMap(stationId)

    suspend fun saveRecentDestination(destination: DestinationItem) {
        database.transitDao().insertRecentDestination(
            RecentDestinationEntity(
                id = destination.id,
                name = destination.name,
                subtitle = destination.subtitle,
                type = destination.type.name,
                latitude = destination.latitude,
                longitude = destination.longitude,
                lastSearchedAt = System.currentTimeMillis()
            )
        )
    }

    fun getRecentDestinations(): Flow<List<RecentDestinationEntity>> =
        database.transitDao().getRecentDestinations()

    suspend fun submitFeedback(feedback: JourneyFeedback) {
        database.transitDao().insertFeedback(
            FeedbackEntity(
                id = feedback.id,
                rating = feedback.rating,
                ratingEmoji = feedback.ratingEmoji,
                comment = feedback.comment,
                routeTitle = feedback.routeTitle,
                durationMinutes = feedback.durationMinutes,
                fare = feedback.fare,
                submittedAtEpochMs = feedback.submittedAtEpochMs
            )
        )
    }

    fun getAllFeedback(): Flow<List<FeedbackEntity>> =
        database.transitDao().getAllFeedback()
}
