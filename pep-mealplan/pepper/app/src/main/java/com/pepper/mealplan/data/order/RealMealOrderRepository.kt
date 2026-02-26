package com.pepper.mealplan.data.order

import com.pepper.mealplan.data.orders.OrdersRepository
import com.pepper.mealplan.data.residents.ResidentsRepository
import com.pepper.mealplan.network.dto.ExportOrderDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RealMealOrderRepository(
    private val residentsRepository: ResidentsRepository = ResidentsRepository(),
    private val ordersRepository: OrdersRepository = OrdersRepository()
) : MealOrderRepository {

    // yyyy-MM-dd passt zu deinem Export Endpoint und deiner Beispiel-Response
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    // WICHTIG: NICHT suspend, damit es zum Interface passt (verhindert override errors)
    override fun getMissingMealsForNextDays(personName: String, days: Int): List<MissingMealInfo> = runBlocking {
        withContext(Dispatchers.IO) {

            val residents = residentsRepository.getResidents().getOrNull().orEmpty()

            val person = residents.firstOrNull { "${it.firstname} ${it.lastname}" == personName }
                ?: return@withContext emptyList()

            val personId = person.id
            val missing = mutableListOf<MissingMealInfo>()

            val cal = Calendar.getInstance()

            for (i in 0 until days) {
                val dateKey = dateFormat.format(cal.time)

                val exportOrders: List<ExportOrderDto> =
                    ordersRepository.getExportedOrders(dateKey).getOrNull().orEmpty()

                val orderForPerson = exportOrders.firstOrNull { it.person.id == personId }

                if (orderForPerson == null) {
                    // Keine Bestellung => Mittag + Abend fehlen (Suppe/Dessert sind nicht bestellbar)
                    missing += MissingMealInfo(personName, dateKey, MealSlot.MAIN1)
                    missing += MissingMealInfo(personName, dateKey, MealSlot.DINNER1)
                } else {
                    if (orderForPerson.selectedLunch == null) {
                        missing += MissingMealInfo(personName, dateKey, MealSlot.MAIN1)
                    }
                    if (orderForPerson.selectedDinner == null) {
                        missing += MissingMealInfo(personName, dateKey, MealSlot.DINNER1)
                    }
                }

                // nächster Tag
                cal.add(Calendar.DAY_OF_YEAR, 1)
            }

            missing
        }
    }
}