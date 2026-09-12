package com.example.data

import com.example.model.DestinationItem
import com.example.model.RouteOption
import com.example.model.StationMap
import com.example.model.TransitLocation

interface TransitDataSource {
    fun getOriginLocation(): TransitLocation
    fun getPopularDestinations(): List<DestinationItem>
    fun searchDestinations(query: String): List<DestinationItem>
    fun planRoutes(origin: TransitLocation, destination: DestinationItem): List<RouteOption>
    fun calculateReroute(currentLocation: TransitLocation, destination: DestinationItem): RouteOption
    fun getStationMap(stationId: String): StationMap?
}
