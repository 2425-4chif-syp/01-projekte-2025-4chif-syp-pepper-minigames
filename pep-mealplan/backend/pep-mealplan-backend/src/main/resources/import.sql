-- Allergene
INSERT INTO Allergen(shortname, description) VALUES
                                                 ('A', 'Glutenhaltiges Getreide'),
                                                 ('B', 'Krebstiere'),
                                                 ('C', 'Eier'),
                                                 ('D', 'Fisch');

-- Food (Hauptspeisen, Vorspeisen, Desserts)
INSERT INTO Food(id, name, type, description, price) VALUES
                                                         (1, 'Tomatensuppe', 'starter', 'Leckere Suppe', 200),
                                                         (2, 'Schnitzel', 'main', 'Knusprig', 850),
                                                         (3, 'Salat', 'starter', 'Grüner Salat', 250),
                                                         (4, 'Spaghetti', 'main', 'Mit Tomatensauce', 750),
                                                         (5, 'Pudding', 'dessert', 'Schoko', 300);

-- FoodAllergen
INSERT INTO FoodAllergen(FoodId, AllergenShortname) VALUES
                                                        (2, 'A'),
                                                        (4, 'A'),
                                                        (5, 'C');

-- MealPlan für 2025-01-01
INSERT INTO MealPlan(id, date) VALUES
    (1, '2025-01-01');

-- MealPlan Zuordnungen
INSERT INTO mealplan_starters(mealplan_id, food_id) VALUES (1, 1);
INSERT INTO mealplan_mains(mealplan_id, food_id) VALUES (1, 2), (1,4);
INSERT INTO mealplan_desserts(mealplan_id, food_id) VALUES (1, 5);
