package com.example.goindiacab.viewmodel

import com.example.goindiacab.data.models.VehicleCategory
import com.example.goindiacab.data.models.VehicleSeedData
import com.example.goindiacab.data.models.formatInr
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class VehicleSelectionViewModelTest {

    @Test
    fun testCurrencyFormatting() {
        assertEquals("₹3,450", 3450.formatInr())
        assertEquals("₹3,800", 3800.formatInr())
        assertEquals("₹5,200", 5200.formatInr())
        assertEquals("₹5,290", 5290.formatInr())
        assertEquals("₹5,520", 5520.formatInr())
        assertEquals("₹5,980", 5980.formatInr())
        assertEquals("₹6,900", 6900.formatInr())
    }

    @Test
    fun testSeedDataIntegrity() {
        val vehicles = VehicleSeedData.VEHICLE_OPTIONS
        assertEquals(7, vehicles.size)

        val recommended = vehicles.firstOrNull { it.isRecommended }
        assertNotNull(recommended)
        assertEquals("Swift Dzire", recommended.name)
        assertEquals(VehicleCategory.SEDAN, recommended.category)
        assertEquals(3450, recommended.allInclusiveFare)

        val hondaCity = vehicles.firstOrNull { it.name == "Honda City" }
        assertNotNull(hondaCity)
        assertEquals(VehicleCategory.SEDAN, hondaCity.category)
        assertEquals(3800, hondaCity.allInclusiveFare)

        val innova = vehicles.firstOrNull { it.name.contains("Innova") }
        assertNotNull(innova)
        assertEquals(VehicleCategory.SUV, innova.category)
        assertEquals(5200, innova.allInclusiveFare)

        val groupTravel = vehicles.filter { it.isGroupTravel }
        assertEquals(4, groupTravel.size)
        assertTrue(groupTravel.all { it.category == VehicleCategory.GROUP_TRAVEL })
    }

    @Test
    fun testVehicleCategoryMatching() {
        assertTrue(VehicleCategory.ALL.matches(VehicleCategory.SEDAN))
        assertTrue(VehicleCategory.ALL.matches(VehicleCategory.SUV))
        assertTrue(VehicleCategory.ALL.matches(VehicleCategory.GROUP_TRAVEL))

        assertTrue(VehicleCategory.SEDAN.matches(VehicleCategory.SEDAN))
        assertFalse(VehicleCategory.SEDAN.matches(VehicleCategory.SUV))
        assertFalse(VehicleCategory.SEDAN.matches(VehicleCategory.GROUP_TRAVEL))

        assertTrue(VehicleCategory.SUV.matches(VehicleCategory.SUV))
        assertFalse(VehicleCategory.SUV.matches(VehicleCategory.SEDAN))

        assertTrue(VehicleCategory.GROUP_TRAVEL.matches(VehicleCategory.GROUP_TRAVEL))
        assertFalse(VehicleCategory.GROUP_TRAVEL.matches(VehicleCategory.SEDAN))
    }

    @Test
    fun testCategoryFilteringLogic() {
        val vehicles = VehicleSeedData.VEHICLE_OPTIONS

        val all = vehicles.filter { it.matchesCategory(VehicleCategory.ALL) }
        assertEquals(7, all.size)

        val sedans = vehicles.filter { it.matchesCategory(VehicleCategory.SEDAN) }
        assertEquals(2, sedans.size)
        assertEquals("Swift Dzire", sedans[0].name)
        assertEquals("Honda City", sedans[1].name)

        val suvs = vehicles.filter { it.matchesCategory(VehicleCategory.SUV) }
        assertEquals(1, suvs.size)
        assertEquals("Toyota Innova Crysta", suvs.first().name)

        val premium = vehicles.filter { it.matchesCategory(VehicleCategory.PREMIUM) }
        assertEquals(2, premium.size)
        assertTrue(premium.any { it.name == "Honda City" })
        assertTrue(premium.any { it.name == "Toyota Innova Crysta" })

        val group = vehicles.filter { it.matchesCategory(VehicleCategory.GROUP_TRAVEL) }
        assertEquals(4, group.size)
        assertEquals("9 Seater Tempo Traveller", group[0].name)
        assertEquals("12 Seater Tempo Traveller", group[1].name)
        assertEquals("16 Seater Tempo Traveller", group[2].name)
        assertEquals("26 Seater Tempo Traveller", group[3].name)
    }
}
