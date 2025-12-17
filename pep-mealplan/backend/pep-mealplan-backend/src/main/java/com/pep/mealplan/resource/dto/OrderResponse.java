package com.pep.mealplan.resource.dto;

import com.pep.mealplan.entity.Order;

public class OrderResponse {

    public Long id;
    public Long personId;
    public String personName;

    public String date;

    public String starter;
    public String mainLunch;
    public String mainEvening;
    public String dessert;

    public String createdAt;

    public static OrderResponse fromEntity(Order o) {
        OrderResponse r = new OrderResponse();

        r.id = o.id;
        r.personId = (o.person != null) ? o.person.id : null;
        r.personName = (o.person != null)
                ? o.person.firstname + " " + o.person.lastname
                : null;

        r.date = (o.mealPlan != null) ? o.mealPlan.date.toString() : null;

        r.starter = (o.selectedStarter != null) ? o.selectedStarter.name : null;
        r.mainLunch = (o.selectedMain != null) ? o.selectedMain.name : null;
        r.mainEvening = (o.selectedEvening != null) ? o.selectedEvening.name : null;
        r.dessert = (o.selectedDessert != null) ? o.selectedDessert.name : null;

        r.createdAt = (o.createdAt != null) ? o.createdAt.toString() : null;

        return r;
    }
}
