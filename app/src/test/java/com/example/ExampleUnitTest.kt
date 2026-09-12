package com.example

import com.example.data.MockJakartaTransitDataSource
import com.example.model.TransitLocation
import com.example.model.TransportMode
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ExampleUnitTest {

    private lateinit var dataSource: MockJakartaTransitDataSource

    @Before
    fun setUp() {
        dataSource = MockJakartaTransitDataSource()
    }

    @Test
    fun `search destination returns relevant results for Metland and Tosari`() {
        val metlandResults = dataSource.searchDestinations("metland")
        assertTrue(metlandResults.isNotEmpty())
        assertTrue(metlandResults.first().name.contains("Metland"))

        val tosariResults = dataSource.searchDestinations("tosari")
        assertTrue(tosariResults.isNotEmpty())
        assertEquals("Halte Tosari ICBC", tosariResults.first().name)
    }

    @Test
    fun `ridwan scenario plans recommended route from Tosari to Metland`() {
        val origin = dataSource.getOriginLocation()
        val destination = dataSource.getPopularDestinations().first { it.id == "dest_metland" }

        val routes = dataSource.planRoutes(origin, destination)
        assertTrue(routes.isNotEmpty())

        val recommended = routes.firstOrNull { it.isRecommended }
        assertNotNull(recommended)
        assertEquals("B1 → B2 → Cikarang Line → Metland", recommended!!.summaryRoute)
        assertTrue(recommended.modesUsed.contains(TransportMode.TRANSJAKARTA))
        assertTrue(recommended.modesUsed.contains(TransportMode.KRL))
        assertEquals(2, recommended.transferCount)
        assertEquals(8000, recommended.totalFare)
    }

    @Test
    fun `off route rerouting preserves destination and provides updated route`() {
        val destination = dataSource.getPopularDestinations().first { it.id == "dest_metland" }
        val deviatedLoc = TransitLocation(
            id = "loc_deviated",
            name = "Dekat Stasiun Tebet",
            address = "Tebet, Jakarta Selatan",
            latitude = -6.2263,
            longitude = 106.8580
        )

        val rerouted = dataSource.calculateReroute(deviatedLoc, destination)
        assertNotNull(rerouted)
        assertEquals("route_rerouted", rerouted.id)
        assertTrue(rerouted.summaryRoute.contains("Metland"))
    }

    @Test
    fun `station map contains wayfinding steps starting with you are here`() {
        val stationMap = dataSource.getStationMap("s_b2")
        assertNotNull(stationMap)
        assertTrue(stationMap!!.wayfindingSteps.isNotEmpty())

        val firstStep = stationMap.wayfindingSteps.first()
        assertTrue(firstStep.instruction.contains("YOU ARE HERE"))

        val lastStep = stationMap.wayfindingSteps.last()
        assertTrue(lastStep.instruction.contains("Platform 2") || lastStep.detail.contains("Peron 2"))
    }
}
