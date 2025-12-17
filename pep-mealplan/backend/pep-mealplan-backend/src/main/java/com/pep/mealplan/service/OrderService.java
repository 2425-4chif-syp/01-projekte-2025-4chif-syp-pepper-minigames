package com.pep.mealplan.service;

import com.pep.mealplan.entity.Food;
import com.pep.mealplan.entity.MealPlan;
import com.pep.mealplan.entity.Order;
import com.pep.mealplan.entity.Person;
import com.pep.mealplan.repository.MealPlanRepository;
import com.pep.mealplan.repository.OrderRepository;
import com.pep.mealplan.repository.PersonRepository;
import com.pep.mealplan.resource.dto.OrderBulkRequest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.Map;

@ApplicationScoped
public class OrderService {

    @Inject
    OrderRepository orderRepo;

    @Inject
    PersonRepository personRepo;

    @Inject
    MealPlanRepository mealPlanRepo;

    // ---------------------------------------------------------
    // BULK ORDER CREATION
    // ---------------------------------------------------------
    @Transactional
    public void bulkCreateOrders(OrderBulkRequest req) {

        LocalDate weekStart = req.weekStartDate;

        for (Map.Entry<Long, Map<Integer, OrderBulkRequest.DaySelection>> personEntry : req.orders.entrySet()) {

            Long personId = personEntry.getKey();
            Person person = personRepo.findById(personId);

            if (person == null)
                continue; // Ungültige Person → überspringen

            Map<Integer, OrderBulkRequest.DaySelection> days = personEntry.getValue();

            for (int dayIndex = 0; dayIndex < 7; dayIndex++) {

                OrderBulkRequest.DaySelection selection = days.get(dayIndex);

                if (selection == null)
                    continue; // Keine Auswahl → überspringen

                LocalDate date = weekStart.plusDays(dayIndex);

                MealPlan mealPlan = mealPlanRepo.find("date", date).firstResult();
                if (mealPlan == null)
                    continue;

                // Food auswählen (Neu: Integer 1/2 statt String "one"/"two")
                Food lunch = getFoodForOption(mealPlan, selection.selectedMenu);
                Food evening = getFoodForOption(mealPlan, selection.selectedEvening);

                // Order anlegen
                Order o = new Order();
                o.person = person;
                o.mealPlan = mealPlan;
                o.selectedMain = lunch;       // Mittagessen
                o.selectedEvening = evening;  // Abendessen
                o.selectedStarter = mealPlan.starters.isEmpty() ? null : mealPlan.starters.get(0);
                o.selectedDessert = mealPlan.desserts.isEmpty() ? null : mealPlan.desserts.get(0);

                orderRepo.persist(o);
            }
        }
    }

    // ---------------------------------------------------------
    // Hilfsfunktion: Food auswählen (NEU: Integer statt String)
    // ---------------------------------------------------------
    private Food getFoodForOption(MealPlan mp, Integer option) {

        if (option == null)
            return null;

        if (option == 1)
            return mp.mains.size() > 0 ? mp.mains.get(0) : null;

        if (option == 2)
            return mp.mains.size() > 1 ? mp.mains.get(1) : null;

        return null;
    }
}
