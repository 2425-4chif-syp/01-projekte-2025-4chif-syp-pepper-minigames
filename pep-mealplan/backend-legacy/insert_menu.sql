USE menu_assistent;

DELETE FROM Menu;

-- Woche 1
INSERT INTO Menu (WeekNumber, Weekday, SoupID, M1ID, M2ID, LunchDessertID, A1ID, A2ID) VALUES
(1, 'MO', 27, 1, 15, 40, 49, 63),
(1, 'DI', 28, 2, 16, 41, 63, 64),
(1, 'MI', 29, 3, 17, 42, 51, 65),
(1, 'DO', 30, 4, 18, 43, 52, 66),
(1, 'FR', 31, 5, 19, 44, 53, 67),
(1, 'SA', 32, 6, 20, 45, 54, 68),
(1, 'SO', 33, 7, 21, 41, 55, 69);

-- Woche 2
INSERT INTO Menu (WeekNumber, Weekday, SoupID, M1ID, M2ID, LunchDessertID, A1ID, A2ID) VALUES
(2, 'MO', 34, 8, 21, 46, 56, 70),
(2, 'DI', 35, 9, 22, 41, 57, 71),
(2, 'MI', 36, 10, 23, 44, 58, 72),
(2, 'DO', 31, 11, 24, 43, 59, 73),
(2, 'FR', 37, 12, 25, 47, 65, 74),
(2, 'SA', 38, 13, 26, 48, 61, 75),
(2, 'SO', 39, 14, 27, 41, 62, 76);

-- Woche 3
INSERT INTO Menu (WeekNumber, Weekday, SoupID, M1ID, M2ID, LunchDessertID, A1ID, A2ID) VALUES
(3, 'MO', 29, 90, 77, 111, 115, 123),
(3, 'DI', 102, 91, 78, 41, 52, 124),
(3, 'MI', 103, 92, 79, 43, 116, 125),
(3, 'DO', 104, 93, 80, 44, 117, 126),
(3, 'FR', 105, 94, 81, 113, 49, 127),
(3, 'SA', 34, 95, 82, 114, 118, 128),
(3, 'SO', 39, 83, 84, 41, 69, 51);

-- Woche 4
INSERT INTO Menu (WeekNumber, Weekday, SoupID, M1ID, M2ID, LunchDessertID, A1ID, A2ID) VALUES
(4, 'MO', 106, 73, 78, 46, 119, 129),
(4, 'DI', 28, 97, 85, 41, 54, 130),
(4, 'MI', 107, 98, 86, 47, 120, 128),
(4, 'DO', 108, 99, 87, 43, 121, 76),
(4, 'FR', 109, 100, 88, 112, 122, 69),
(4, 'SA', 110, 101, 89, 47, 97, 132),
(4, 'SO', 36, 89, 90, 41, 120, 56);

-- Woche 5 (zufällig generiert, aber realistisch)
INSERT INTO Menu (WeekNumber, Weekday, SoupID, M1ID, M2ID, LunchDessertID, A1ID, A2ID) VALUES
(5, 'MO', 27, 2, 14, 40, 49, 63),
(5, 'DI', 28, 9, 16, 41, 51, 65),
(5, 'MI', 29, 5, 17, 42, 52, 66),
(5, 'DO', 30, 4, 19, 43, 53, 67),
(5, 'FR', 31, 8, 20, 44, 54, 68),
(5, 'SA', 32, 7, 21, 45, 55, 69),
(5, 'SO', 33, 6, 22, 41, 56, 70);

-- Woche 6 (zufällig generiert, aber realistisch)
INSERT INTO Menu (WeekNumber, Weekday, SoupID, M1ID, M2ID, LunchDessertID, A1ID, A2ID) VALUES
(6, 'MO', 34, 13, 24, 47, 71, 66),
(6, 'DI', 35, 14, 25, 41, 72, 67),
(6, 'MI', 36, 15, 26, 42, 73, 68),
(6, 'DO', 31, 16, 27, 43, 74, 69),
(6, 'FR', 37, 17, 28, 47, 75, 70),
(6, 'SA', 38, 18, 29, 48, 76, 71),
(6, 'SO', 39, 19, 30, 41, 77, 72);
