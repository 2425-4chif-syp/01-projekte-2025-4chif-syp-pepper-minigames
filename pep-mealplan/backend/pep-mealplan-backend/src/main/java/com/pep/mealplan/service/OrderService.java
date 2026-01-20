package com.pep.mealplan.service;

import com.pep.mealplan.entity.Food;
import com.pep.mealplan.entity.MealPlan;
import com.pep.mealplan.entity.Order;
import com.pep.mealplan.entity.Person;
import com.pep.mealplan.repository.MealPlanRepository;
import com.pep.mealplan.repository.OrderRepository;

import com.pep.mealplan.resource.dto.OrderCreateDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import com.pep.mealplan.resource.dto.KitchenSummary;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class OrderService {

    @Inject
    OrderRepository orderRepo;

    @Inject
    MealPlanRepository mealPlanRepo;

    // -------------------------------------------------
    // READ
    // -------------------------------------------------

    public List<Order> getAll() {
        return orderRepo.listAll();
    }

    public Order getById(Long id) {
        return orderRepo.findById(id);
    }

    public List<Order> getByDate(LocalDate date) {
        return orderRepo.list("date", date);
    }

    // -------------------------------------------------
    // WRITE (UPSERT + VALIDATION)
    // -------------------------------------------------

    @Transactional
    public Order upsert(Order order) {

        if (order.person == null || order.date == null) {
            throw new BadRequestException("Person und Datum sind Pflichtfelder");
        }

        if (order.selectedLunch == null || order.selectedDinner == null) {
            throw new BadRequestException("Mittag- und Abendessen müssen gewählt werden");
        }

        // Datum → Kalenderwoche
        int calendarWeek =
                order.date.get(WeekFields.of(Locale.GERMANY).weekOfWeekBasedYear());

        // 4-Wochen-Zyklus (1–4)
        int weekNumber = ((calendarWeek - 1) % 4) + 1;

        // Wochentag (Montag = 0)
        int weekDay = order.date.getDayOfWeek().getValue() - 1;


        MealPlan plan = mealPlanRepo.find(
                "weekNumber = ?1 and weekDay = ?2",
                weekNumber,
                weekDay
        ).firstResult();

        if (plan == null) {
            throw new BadRequestException("Kein Menüplan für dieses Datum vorhanden");
        }


        // VALIDIERUNG: Lunch
        if (!isValidLunch(order.selectedLunch, plan)) {
            throw new BadRequestException("Ungültiges Mittagessen für diesen Tag");
        }

        // VALIDIERUNG: Dinner
        if (!isValidDinner(order.selectedDinner, plan)) {
            throw new BadRequestException("Ungültiges Abendessen für diesen Tag");
        }

        // UPSERT (person + date)
        Order existing = orderRepo.find(
                "person = ?1 and date = ?2",
                order.person,
                order.date
        ).firstResult();

        if (existing != null) {
            existing.selectedLunch = order.selectedLunch;
            existing.selectedDinner = order.selectedDinner;
            return existing;
        }

        orderRepo.persist(order);
        return order;
    }

    // -------------------------------------------------
    // VALIDATION HELPERS
    // -------------------------------------------------

    private boolean isValidLunch(Food food, MealPlan plan) {
        return food.equals(plan.lunch1) || food.equals(plan.lunch2);
    }

    private boolean isValidDinner(Food food, MealPlan plan) {
        return food.equals(plan.dinner1) || food.equals(plan.dinner2);
    }

    // -------------------------------------------------
    // EXPORT (KÜCHE)
    // -------------------------------------------------

    public List<Order> exportForWeek(LocalDate anyDateInWeek) {

        LocalDate monday =
                anyDateInWeek.minusDays(anyDateInWeek.getDayOfWeek().getValue() - 1);
        LocalDate sunday = monday.plusDays(6);

        return orderRepo.list(
                "date >= ?1 and date <= ?2",
                monday,
                sunday
        );
    }

    public KitchenSummary kitchenSummaryForWeek(LocalDate anyDateInWeek) {

        List<Order> orders = exportForWeek(anyDateInWeek);

        Map<String, Long> lunchCount = new HashMap<>();
        Map<String, Long> dinnerCount = new HashMap<>();

        for (Order o : orders) {

            if (o.selectedLunch != null) {
                lunchCount.merge(
                        o.selectedLunch.name,
                        1L,
                        Long::sum
                );
            }

            if (o.selectedDinner != null) {
                dinnerCount.merge(
                        o.selectedDinner.name,
                        1L,
                        Long::sum
                );
            }
        }

        return new KitchenSummary(lunchCount, dinnerCount);
    }

        // -------------------------------------------------
    // SIMPLE CREATE
    // -------------------------------------------------
        @Transactional
        public Order create(OrderCreateDTO dto) {

            if (dto.personId == null || dto.date == null) {
                throw new BadRequestException("personId und date sind Pflicht");
            }

            if (dto.selectedLunchId == null || dto.selectedDinnerId == null) {
                throw new BadRequestException("Lunch und Dinner sind Pflicht");
            }

            Person person = Person.findById(dto.personId);
            if (person == null) {
                throw new BadRequestException("Person existiert nicht");
            }

            Food lunch = Food.findById(dto.selectedLunchId);
            Food dinner = Food.findById(dto.selectedDinnerId);

            if (lunch == null || dinner == null) {
                throw new BadRequestException("Food existiert nicht");
            }

            Order order = new Order();
            order.person = person;
            order.date = dto.date;
            order.selectedLunch = lunch;
            order.selectedDinner = dinner;

            return upsert(order);
        }


    // -------------------------------------------------
    // DELETE
    // -------------------------------------------------
    @Transactional
    public boolean delete(Long id) {
        return orderRepo.deleteById(id);
    }


}
