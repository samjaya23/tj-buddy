package com.example.data

import com.example.model.*

class MockJakartaTransitDataSource : TransitDataSource {

    override fun getOriginLocation(): TransitLocation {
        return TransitLocation(
            id = "loc_tosari",
            name = "Halte Tosari ICBC (Jl. MH Thamrin)",
            address = "Menteng, Jakarta Pusat",
            latitude = -6.1963,
            longitude = 106.8228,
            isCurrentLocation = true
        )
    }

    private val allDestinations = listOf(
        DestinationItem(
            id = "dest_metland",
            name = "Metland / Bekasi",
            subtitle = "Stasiun Metland Telaga Murni, Cikarang Barat",
            type = DestinationType.STATION,
            connectedModes = listOf(TransportMode.KRL, TransportMode.TRANSJAKARTA),
            latitude = -6.2731,
            longitude = 107.1082
        ),
        DestinationItem(
            id = "dest_tosari",
            name = "Halte Tosari ICBC",
            subtitle = "Jl. MH Thamrin, Dukuh Atas, Jakarta Pusat",
            type = DestinationType.HALTE,
            connectedModes = listOf(TransportMode.TRANSJAKARTA, TransportMode.MRT),
            latitude = -6.1963,
            longitude = 106.8228
        ),
        DestinationItem(
            id = "dest_bekasi_timur",
            name = "Stasiun Bekasi Timur",
            subtitle = "Jl. Ir. H. Juanda, Bekasi Timur",
            type = DestinationType.STATION,
            connectedModes = listOf(TransportMode.KRL, TransportMode.TRANSJAKARTA),
            latitude = -6.2483,
            longitude = 107.0163
        ),
        DestinationItem(
            id = "dest_dukuh_atas",
            name = "Transit Hub Dukuh Atas",
            subtitle = "Koneksi TJ, MRT, LRT, Commuter Line",
            type = DestinationType.HALTE,
            connectedModes = listOf(TransportMode.TRANSJAKARTA, TransportMode.MRT, TransportMode.LRT, TransportMode.KRL),
            latitude = -6.2008,
            longitude = 106.8227
        ),
        DestinationItem(
            id = "dest_bundaran_hi",
            name = "Bundaran HI Astra",
            subtitle = "Pusat Kota Jakarta, Halte & MRT",
            type = DestinationType.LANDMARK,
            connectedModes = listOf(TransportMode.TRANSJAKARTA, TransportMode.MRT),
            latitude = -6.1926,
            longitude = 106.8236
        ),
        DestinationItem(
            id = "dest_monas",
            name = "Monas (Monumen Nasional)",
            subtitle = "Halte Monas, Gambir, Jakarta Pusat",
            type = DestinationType.LANDMARK,
            connectedModes = listOf(TransportMode.TRANSJAKARTA),
            latitude = -6.1754,
            longitude = 106.8272
        ),
        DestinationItem(
            id = "dest_manggarai",
            name = "Stasiun Manggarai Central",
            subtitle = "Transit Utama KRL Bogor, Cikarang, Bandara",
            type = DestinationType.STATION,
            connectedModes = listOf(TransportMode.KRL, TransportMode.TRANSJAKARTA),
            latitude = -6.2100,
            longitude = 106.8502
        ),
        DestinationItem(
            id = "dest_blok_m",
            name = "Blok M Hub",
            subtitle = "Terminal Bus TransJakarta & MRT Blok M",
            type = DestinationType.HALTE,
            connectedModes = listOf(TransportMode.TRANSJAKARTA, TransportMode.MRT),
            latitude = -6.2435,
            longitude = 106.7978
        )
    )

    override fun getPopularDestinations(): List<DestinationItem> {
        return allDestinations
    }

    override fun searchDestinations(query: String): List<DestinationItem> {
        if (query.isBlank()) return getPopularDestinations()
        val cleaned = query.trim().lowercase()
        return allDestinations.filter {
            it.name.lowercase().contains(cleaned) ||
            it.subtitle.lowercase().contains(cleaned)
        }
    }

    override fun planRoutes(origin: TransitLocation, destination: DestinationItem): List<RouteOption> {
        // Build the 3 specific scenario alternatives requested in the prompt
        val stopTosari = Stop("s_tosari", "Halte Tosari", TransportMode.TRANSJAKARTA, "B1", 1, -6.1963, 106.8228)
        val stopDukuhAtas1 = Stop("s_da1", "Halte Dukuh Atas 1", TransportMode.TRANSJAKARTA, "B1", 2, -6.2008, 106.8227)
        val stopKaret = Stop("s_krt", "Halte Karet Sudirman", TransportMode.TRANSJAKARTA, "B1", 3, -6.2085, 106.8197)
        val stopB2 = Stop("s_b2", "Halte Transit B2 (Dukuh Atas 2)", TransportMode.TRANSJAKARTA, "B2", 4, -6.2015, 106.8235, true, "Pindah ke Stasiun KRL Sudirman melalui jembatan penyeberangan terhubung")

        val stnSudirman = Stop("s_sudirman", "Stasiun Sudirman", TransportMode.KRL, "Cikarang Line", 1, -6.2023, 106.8230, true)
        val stnManggarai = Stop("s_mgr", "Stasiun Manggarai", TransportMode.KRL, "Cikarang Line", 2, -6.2100, 106.8502)
        val stnJatinegara = Stop("s_jtn", "Stasiun Jatinegara", TransportMode.KRL, "Cikarang Line", 3, -6.2155, 106.8680)
        val stnBekasi = Stop("s_bks", "Stasiun Bekasi", TransportMode.KRL, "Cikarang Line", 4, -6.2361, 106.9995)
        val stnMetland = Stop("s_metland", "Stasiun Metland Telaga Murni", TransportMode.KRL, "Cikarang Line", 5, -6.2731, 107.1082)

        // Route 1: RECOMMENDED (B1 → B2 → Cikarang Line → Metland)
        val segWalkToB1 = RouteSegment(
            id = "seg_rec_1",
            mode = TransportMode.WALKING,
            lineName = "Jalan Kaki",
            vehicleNumberOrTrack = "Jalur Pedestrian",
            originStop = Stop("w1_o", "Posisi Kamu (Jl. Thamrin)", TransportMode.WALKING, "Walk", 0, origin.latitude, origin.longitude),
            destinationStop = stopTosari,
            intermediateStops = emptyList(),
            durationMinutes = 4,
            fare = 0,
            instructionForBeginner = "Jalan kaki santai 150 meter menuju Halte TransJakarta Tosari",
            tip = "Gunakan jembatan penyeberangan (JPO) beratap, aman dan nyaman."
        )

        val segB1 = RouteSegment(
            id = "seg_rec_2",
            mode = TransportMode.TRANSJAKARTA,
            lineName = "TransJakarta Koridor B1",
            vehicleNumberOrTrack = "Pintu 2 - Arah Dukuh Atas / Bekasi",
            originStop = stopTosari,
            destinationStop = stopB2,
            intermediateStops = listOf(stopDukuhAtas1, stopKaret),
            durationMinutes = 14,
            fare = 3500,
            instructionForBeginner = "Naik TransJakarta Koridor B1 di Halte Tosari",
            tip = "Tap kartu uang elektronik (Flazz/e-Money/JakLingko) di pintu masuk halte. Saldo minimal Rp 5.000."
        )

        val segTransferWalk = RouteSegment(
            id = "seg_rec_3",
            mode = TransportMode.WALKING,
            lineName = "Transit Antarmoda",
            vehicleNumberOrTrack = "JPO Terintegrasi Dukuh Atas - Sudirman",
            originStop = stopB2,
            destinationStop = stnSudirman,
            intermediateStops = emptyList(),
            durationMinutes = 5,
            fare = 0,
            instructionForBeginner = "Ikuti petunjuk arah menuju Stasiun KRL Sudirman (JPO terhubung)",
            tip = "Tidak perlu keluar ke jalan raya. Ikuti rambu berlogo Kereta KRL warna oranye."
        )

        val segKrlCikarang = RouteSegment(
            id = "seg_rec_4",
            mode = TransportMode.KRL,
            lineName = "KRL Commuter Line Cikarang",
            vehicleNumberOrTrack = "Peron 2 (Arah Bekasi / Cikarang)",
            originStop = stnSudirman,
            destinationStop = stnMetland,
            intermediateStops = listOf(stnManggarai, stnJatinegara, stnBekasi),
            durationMinutes = 49,
            fare = 4500,
            instructionForBeginner = "Naik KRL di Peron 2 menuju Stasiun Metland Telaga Murni",
            tip = "Dengarkan pengumuman di dalam gerbong. Kamu melewati 4 stasiun sebelum sampai di Metland."
        )

        val recommendedRoute = RouteOption(
            id = "route_recommended",
            title = "Rekomendasi Pemula (Paling Mudah)",
            isRecommended = true,
            recommendedBadgeText = "Easiest route for first-time users",
            summaryRoute = "B1 → B2 → Cikarang Line → Metland",
            totalDurationMinutes = 72, // 1 hr 12 min
            totalFare = 8000,
            transferCount = 2,
            walkingTimeMinutes = 9,
            walkingDistanceMeters = 350,
            complexity = RouteComplexity.VERY_EASY,
            modesUsed = listOf(TransportMode.WALKING, TransportMode.TRANSJAKARTA, TransportMode.KRL),
            segments = listOf(segWalkToB1, segB1, segTransferWalk, segKrlCikarang),
            beginnerAdvice = "Rute terbaik untuk pertama kali: halte dan stasiun terhubung jembatan khusus, papan informasi sangat jelas, dan tidak perlu menyeberang jalan raya."
        )

        // Route 2: Alternative 2 (B1 → B2 → Walking → Destination)
        val segAlt2Walk = RouteSegment(
            id = "seg_alt2_3",
            mode = TransportMode.WALKING,
            lineName = "Jalan Kaki Lanjutan",
            vehicleNumberOrTrack = "Jalur Trotoar Sudirman Timur",
            originStop = stopB2,
            destinationStop = Stop("s_dest_walk", "Kawasan Tujuan", TransportMode.WALKING, "Walk", 5, destination.latitude, destination.longitude),
            intermediateStops = emptyList(),
            durationMinutes = 77,
            fare = 0,
            instructionForBeginner = "Melanjutkan perjalanan dengan berjalan kaki",
            tip = "Jarak jalan kaki cukup jauh (lebih dari 3 km)."
        )

        val altRoute2 = RouteOption(
            id = "route_alt_2",
            title = "Alternatif 2 (Jalan Santai)",
            isRecommended = false,
            recommendedBadgeText = "Ongkos Termurah",
            summaryRoute = "B1 → B2 → Walking → Destination",
            totalDurationMinutes = 95, // 1 hr 35 min
            totalFare = 3500,
            transferCount = 1,
            walkingTimeMinutes = 81,
            walkingDistanceMeters = 3800,
            complexity = RouteComplexity.MODERATE,
            modesUsed = listOf(TransportMode.WALKING, TransportMode.TRANSJAKARTA),
            segments = listOf(segWalkToB1, segB1, segAlt2Walk),
            beginnerAdvice = "Hanya menggunakan 1 moda busway, tetapi memerlukan jalan kaki cukup jauh. Cocok jika kamu santai dan ingin berhemat."
        )

        // Route 3: Alternative 3 (B1 → LRT → Cikarang Line)
        val stnLrtDukuhAtas = Stop("s_lrt_da", "Stasiun LRT Dukuh Atas", TransportMode.LRT, "LRT Line Bekasi", 1, -6.2030, 106.8240, true)
        val stnLrtBekasiBarat = Stop("s_lrt_bb", "Stasiun LRT Bekasi Barat", TransportMode.LRT, "LRT Line Bekasi", 2, -6.2480, 106.9920, true)

        val segLrt = RouteSegment(
            id = "seg_alt3_lrt",
            mode = TransportMode.LRT,
            lineName = "LRT Jabodebek Lintas Bekasi",
            vehicleNumberOrTrack = "Peron 1 (Arah Jatimulya / Bekasi)",
            originStop = stnLrtDukuhAtas,
            destinationStop = stnLrtBekasiBarat,
            intermediateStops = listOf(Stop("s_lrt_cbg", "Cikoko Cawang", TransportMode.LRT, "LRT", 1, -6.2420, 106.8580)),
            durationMinutes = 35,
            fare = 12000,
            instructionForBeginner = "Naik LRT Jabodebek dari Dukuh Atas menuju Stasiun Bekasi Barat",
            tip = "LRT berjalan otomatis tanpa masinis. Tempat duduk luas dan dingin."
        )

        val segKrlConnect = RouteSegment(
            id = "seg_alt3_krl",
            mode = TransportMode.KRL,
            lineName = "KRL Cikarang Line Feeder",
            vehicleNumberOrTrack = "Peron 1 Arah Cikarang",
            originStop = Stop("s_krl_bks", "Stasiun Bekasi", TransportMode.KRL, "Cikarang", 1, -6.2361, 106.9995),
            destinationStop = stnMetland,
            intermediateStops = emptyList(),
            durationMinutes = 12,
            fare = 4000,
            instructionForBeginner = "Lanjut KRL dari Bekasi menuju Stasiun Metland Telaga Murni",
            tip = "Hanya melewati 2 stasiun kecil."
        )

        val altRoute3 = RouteOption(
            id = "route_alt_3",
            title = "Alternatif 3 (Kombinasi Modern LRT)",
            isRecommended = false,
            recommendedBadgeText = "Paling Cepat",
            summaryRoute = "B1 → LRT → Cikarang Line",
            totalDurationMinutes = 65, // 1 hr 05 min
            totalFare = 16000,
            transferCount = 2,
            walkingTimeMinutes = 14,
            walkingDistanceMeters = 520,
            complexity = RouteComplexity.ADVANCED,
            modesUsed = listOf(TransportMode.WALKING, TransportMode.TRANSJAKARTA, TransportMode.LRT, TransportMode.KRL),
            segments = listOf(segWalkToB1, segB1, segLrt, segKrlConnect),
            beginnerAdvice = "Menggunakan kereta LRT modern layang yang cepat bebas macet, tarif sedikit lebih tinggi dibandingkan KRL reguler."
        )

        return listOf(recommendedRoute, altRoute2, altRoute3)
    }

    override fun calculateReroute(currentLocation: TransitLocation, destination: DestinationItem): RouteOption {
        // Calculated new journey from current deviated location (e.g. Tebet / Manggarai East)
        val stopTebet = Stop("s_tebet", "Stasiun Tebet", TransportMode.KRL, "Cikarang Loop", 1, -6.2263, 106.8580)
        val stnMetland = Stop("s_metland", "Stasiun Metland Telaga Murni", TransportMode.KRL, "Cikarang Line", 3, -6.2731, 107.1082)

        val segRerouteWalk = RouteSegment(
            id = "seg_reroute_1",
            mode = TransportMode.WALKING,
            lineName = "Jalan Menuju Stasiun Terdekat",
            vehicleNumberOrTrack = "Pintu Timur Stasiun Tebet",
            originStop = Stop("s_curr", "Posisi Sekarang (${currentLocation.name})", TransportMode.WALKING, "Walk", 0, currentLocation.latitude, currentLocation.longitude),
            destinationStop = stopTebet,
            intermediateStops = emptyList(),
            durationMinutes = 3,
            fare = 0,
            instructionForBeginner = "Jalan santai 120 meter menuju pintu masuk Stasiun Tebet",
            tip = "Tidak perlu balik ke Dukuh Atas! Kamu bisa langsung naik kereta dari stasiun ini."
        )

        val segRerouteKrl = RouteSegment(
            id = "seg_reroute_2",
            mode = TransportMode.KRL,
            lineName = "KRL Cikarang Line Langsung",
            vehicleNumberOrTrack = "Peron 1 (Arah Bekasi & Cikarang)",
            originStop = stopTebet,
            destinationStop = stnMetland,
            intermediateStops = listOf(
                Stop("s_klender", "Stasiun Klender", TransportMode.KRL, "Cikarang Line", 1, -6.2133, 106.8994),
                Stop("s_kranji", "Stasiun Kranji", TransportMode.KRL, "Cikarang Line", 2, -6.2241, 106.9790)
            ),
            durationMinutes = 38,
            fare = 4000,
            instructionForBeginner = "Naik KRL di Peron 1 langsung menuju Metland",
            tip = "Kereta langsung tanpa transit lagi. Cukup duduk manis sampai Stasiun Metland Telaga Murni."
        )

        return RouteOption(
            id = "route_rerouted",
            title = "Rute Baru Otomatis (Dari Posisi Kamu)",
            isRecommended = true,
            recommendedBadgeText = "Rute Diperbarui",
            summaryRoute = "Tebet → KRL Langsung → Metland",
            totalDurationMinutes = 41,
            totalFare = 4000,
            transferCount = 0,
            walkingTimeMinutes = 3,
            walkingDistanceMeters = 120,
            complexity = RouteComplexity.VERY_EASY,
            modesUsed = listOf(TransportMode.WALKING, TransportMode.KRL),
            segments = listOf(segRerouteWalk, segRerouteKrl),
            beginnerAdvice = "Rute baru disesuaikan otomatis dari lokasi kamu sekarang. Tidak perlu putar balik ke titik sebelumnya!"
        )
    }

    override fun getStationMap(stationId: String): StationMap? {
        return when (stationId) {
            "s_b2", "dest_dukuh_atas", "s_da1" -> StationMap(
                stationId = "s_b2",
                stationName = "Halte Dukuh Atas 2 / B2 & Hub Sudirman",
                stationType = "Transit Hub Terintegrasi",
                youAreHereNote = "Kamu sudah sampai di Halte TransJakarta Dukuh Atas 2 (B2).",
                wayfindingSteps = listOf(
                    WayfindingStep(1, "YOU ARE HERE", "Peron Kedatangan Busway B2 lantai 2", "pin"),
                    WayfindingStep(2, "Jalan lurus 30 meter", "Gunakan selasar JPO kaca ber-AC menuju arah Stasiun Sudirman", "straight"),
                    WayfindingStep(3, "Belok kanan setelah eskalator", "Turun 1 lantai menggunakan eskalator atau lift prioritas", "escalator"),
                    WayfindingStep(4, "Ikuti papan KRL warna oranye", "Siapkan kartu e-money dan tap di gate masuk Stasiun Sudirman", "gate"),
                    WayfindingStep(5, "Platform 2 (Peron 2)", "Menuju peron kereta arah Bekasi dan Cikarang", "platform")
                ),
                platforms = listOf(
                    StationPlatform("Peron 1", "Arah Tanah Abang / Kampung Bandan / Angke", listOf("KRL Cikarang Line")),
                    StationPlatform("Peron 2", "Arah Manggarai / Bekasi / Cikarang", listOf("KRL Cikarang Line"), "Ramai Lancar")
                ),
                facilities = listOf(
                    StationFacility("Lift & Eskalator", "Tersedia di setiap titik transit", "elevator"),
                    StationFacility("Toilet Bersih & Musholla", "Lantai 1 dekat gate KRL", "wc"),
                    StationFacility("Mesin Top-Up Kartu", "Tersedia dekat loket petugas", "card"),
                    StationFacility("Pos Petugas Bantuan", "Petugas berseragam siap memandu 24 jam", "help")
                ),
                exits = listOf(
                    "Pintu A: Jl. Jenderal Sudirman & Terowongan Kendal",
                    "Pintu B: Stasiun MRT Dukuh Atas BNI",
                    "Pintu C: Stasiun LRT Jabodebek Dukuh Atas",
                    "Pintu D: Stasiun KRL Sudirman (Jalur transit kamu)"
                )
            )
            "s_metland", "dest_metland" -> StationMap(
                stationId = "s_metland",
                stationName = "Stasiun Metland Telaga Murni",
                stationType = "Stasiun KRL Commuter Line",
                youAreHereNote = "Kamu sudah sampai di Stasiun Metland Telaga Murni.",
                wayfindingSteps = listOf(
                    WayfindingStep(1, "YOU ARE HERE", "Peron kedatangan sisi utara", "pin"),
                    WayfindingStep(2, "Jalan menuju gate keluar (Tap Out)", "Tempelkan kartu uang elektronik yang sama", "gate"),
                    WayfindingStep(3, "Pintu Keluar Utama", "Langsung terhubung dengan perumahan Metland Cibitung & angkutan lokal", "straight")
                ),
                platforms = listOf(
                    StationPlatform("Peron 1", "Arah Jakarta Kota / Angke / Manggarai", listOf("Cikarang Line")),
                    StationPlatform("Peron 2", "Arah Cikarang", listOf("Cikarang Line"))
                ),
                facilities = listOf(
                    StationFacility("Loket Petugas & Bantuan", "Lantai 1 dekat pintu keluar", "help"),
                    StationFacility("Musholla & Toilet", "Lantai 1 sisi barat", "wc"),
                    StationFacility("Pangkalan Ojek Online / Angkot", "Tepat di depan lobby stasiun", "local_transport")
                ),
                exits = listOf(
                    "Pintu Keluar Utara: Boulevard Metland Cibitung",
                    "Pintu Keluar Selatan: Jl. Raya Fatahillah"
                )
            )
            else -> StationMap(
                stationId = stationId,
                stationName = "Halte / Stasiun Transit",
                stationType = "Fasilitas Transit",
                youAreHereNote = "Kamu berada di area transit.",
                wayfindingSteps = listOf(
                    WayfindingStep(1, "YOU ARE HERE", "Area peron transit", "pin"),
                    WayfindingStep(2, "Ikuti papan petunjuk warna biru (TransJakarta) atau oranye (KRL)", "Papan informasi di langit-langit", "straight"),
                    WayfindingStep(3, "Menuju peron keberangkatan berikutnya", "Tanyakan petugas jika butuh bantuan", "platform")
                ),
                platforms = listOf(
                    StationPlatform("Peron 1", "Arah Tujuan", listOf("Transit"))
                ),
                facilities = listOf(
                    StationFacility("Petugas Bantuan", "Siap membantu penumpang pemula", "help")
                ),
                exits = listOf("Pintu Keluar Utama")
            )
        }
    }
}
