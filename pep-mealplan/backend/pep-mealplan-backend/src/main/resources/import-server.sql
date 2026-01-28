-- Server import script (matches vm107 schema)
-- Clears existing data first, then re-inserts

-- Delete in correct order (foreign key dependencies)
DELETE FROM pe_order;
DELETE FROM pe_mealplan;
DELETE FROM pe_foodallergen;
DELETE FROM pe_food;
DELETE FROM pe_picture;
-- Clear pe_image references to persons before deleting persons
UPDATE pe_image SET i_p_id = NULL WHERE i_p_id IS NOT NULL;
DELETE FROM pe_person;
DELETE FROM pe_allergen;

-- Allergens
INSERT INTO pe_allergen (shortname, description) VALUES
  ('A', 'Glutenhaltiges Getreide'),
  ('C', 'Eier'),
  ('D', 'Fisch'),
  ('G', 'Milch und Laktose'),
  ('L', 'Sellerie'),
  ('M', 'Senf'),
  ('O', 'Schwefeldioxid und Sulfite');

-- Pictures (server schema: id, name, mediatype, base64)
INSERT INTO pe_picture (id, name, mediatype, base64) OVERRIDING SYSTEM VALUE VALUES
  (1, 'frittatensuppe', 'image/jpeg', NULL),
  (2, 'wiener_schnitzel', 'image/jpeg', NULL),
  (3, 'tafelspitz', 'image/jpeg', NULL),
  (4, 'apfelstrudel', 'image/jpeg', NULL);

-- Foods (server uses "pictureid")
INSERT INTO pe_food (id, name, type, pictureid) OVERRIDING SYSTEM VALUE VALUES
  (1, 'Frittatensuppe', 'soup', 1),
  (2, 'Griessnockerlsuppe', 'soup', NULL),
  (3, 'Rindsuppe', 'soup', NULL),
  (4, 'Gulaschsuppe', 'soup', NULL),
  (5, 'Wiener Schnitzel', 'main', 2),
  (6, 'Tafelspitz', 'main', 3),
  (7, 'Zwiebelrostbraten', 'main', NULL),
  (8, 'Kaesespaetzle', 'main', NULL),
  (9, 'Schweinsbraten', 'main', NULL),
  (10, 'Tiroler Groestl', 'main', NULL),
  (11, 'Backhendl', 'main', NULL),
  (12, 'Forelle Muellnerin', 'main', NULL),
  (13, 'Apfelstrudel', 'dessert', 4),
  (14, 'Kaiserschmarrn', 'dessert', NULL),
  (15, 'Topfenknoedel', 'dessert', NULL),
  (16, 'Palatschinken', 'dessert', NULL),
  (17, 'Salzburger Nockerl', 'dessert', NULL),
  (18, 'Marillenknedel', 'dessert', NULL),
  (19, 'Germknoedel', 'dessert', NULL);

-- Food allergens
INSERT INTO pe_foodallergen (foodid, allergenshortname) VALUES
  (1, 'A'), (1, 'C'), (1, 'L'),
  (2, 'A'), (2, 'C'), (2, 'G'),
  (3, 'L'),
  (4, 'A'), (4, 'L'),
  (5, 'A'), (5, 'C'),
  (6, 'L'),
  (7, 'A'),
  (8, 'A'), (8, 'C'), (8, 'G'),
  (9, 'M'),
  (10, 'C'),
  (11, 'A'), (11, 'C'),
  (12, 'D'),
  (13, 'A'), (13, 'C'),
  (14, 'A'), (14, 'C'), (14, 'G'),
  (15, 'A'), (15, 'C'), (15, 'G'),
  (16, 'A'), (16, 'C'), (16, 'G'),
  (17, 'A'), (17, 'C'), (17, 'G'),
  (18, 'A'), (18, 'C'), (18, 'G'),
  (19, 'A'), (19, 'C'), (19, 'G');

-- Persons
INSERT INTO pe_person (p_id, p_first_name, p_last_name, p_dob, p_face_id) OVERRIDING SYSTEM VALUE VALUES
  (1, 'Johann', 'Gruber', '1988-04-12', NULL),
  (2, 'Anna', 'Huber', '1992-09-03', NULL),
  (3, 'Franz', 'Bauer', '1979-01-27', NULL),
  (4, 'Theresia', 'Steiner', '1985-11-15', NULL),
  (5, 'Klara', 'Hofer', '1995-06-21', NULL);

-- Meal plans
INSERT INTO pe_mealplan (id, weeknumber, weekday, soup_id, lunch1_id, lunch2_id, lunchdessert_id, dinner1_id, dinner2_id) OVERRIDING SYSTEM VALUE VALUES
  (1, 1, 0, 1, 5, 6, 13, 7, 8),
  (2, 1, 1, 2, 9, 10, 14, 11, 12),
  (3, 1, 2, 3, 6, 7, 15, 5, 9),
  (4, 1, 3, 4, 8, 11, 16, 10, 6),
  (5, 1, 4, 1, 12, 5, 17, 7, 9),
  (6, 1, 5, 2, 10, 8, 18, 11, 6),
  (7, 1, 6, 3, 5, 9, 19, 6, 7),
  (8, 2, 0, 4, 7, 8, 13, 9, 10),
  (9, 2, 1, 1, 11, 12, 14, 5, 6),
  (10, 2, 2, 2, 6, 7, 15, 8, 11),
  (11, 2, 3, 3, 9, 10, 16, 12, 5),
  (12, 2, 4, 4, 8, 11, 17, 6, 7),
  (13, 2, 5, 1, 10, 5, 18, 9, 12),
  (14, 2, 6, 2, 6, 8, 19, 11, 7),
  (15, 3, 0, 3, 5, 6, 13, 8, 9),
  (16, 3, 1, 4, 7, 10, 14, 11, 12),
  (17, 3, 2, 1, 8, 9, 15, 6, 5),
  (18, 3, 3, 2, 10, 11, 16, 7, 8),
  (19, 3, 4, 3, 12, 6, 17, 9, 10),
  (20, 3, 5, 4, 5, 7, 18, 11, 12),
  (21, 3, 6, 1, 9, 8, 19, 6, 7),
  (22, 4, 0, 2, 6, 7, 13, 10, 11),
  (23, 4, 1, 3, 8, 9, 14, 12, 5),
  (24, 4, 2, 4, 10, 11, 15, 6, 7),
  (25, 4, 3, 1, 5, 12, 16, 8, 9),
  (26, 4, 4, 2, 7, 6, 17, 10, 11),
  (27, 4, 5, 3, 9, 8, 18, 12, 5),
  (28, 4, 6, 4, 11, 10, 19, 6, 7);

-- Orders
INSERT INTO pe_order (person_id, order_date, selected_lunch_id, selected_dinner_id) VALUES
  (1, '2026-01-13', 5, 7),
  (2, '2026-01-14', 9, 11),
  (3, '2026-01-15', 6, 9),
  (4, '2026-01-16', 8, 10),
  (5, '2026-01-17', 12, 5);

-- Reset sequences
SELECT setval(pg_get_serial_sequence('pe_picture', 'id'), (SELECT COALESCE(MAX(id), 1) FROM pe_picture));
SELECT setval(pg_get_serial_sequence('pe_food', 'id'), (SELECT COALESCE(MAX(id), 1) FROM pe_food));
SELECT setval(pg_get_serial_sequence('pe_person', 'p_id'), (SELECT COALESCE(MAX(p_id), 1) FROM pe_person));
SELECT setval(pg_get_serial_sequence('pe_mealplan', 'id'), (SELECT COALESCE(MAX(id), 1) FROM pe_mealplan));
SELECT setval(pg_get_serial_sequence('pe_order', 'id'), (SELECT COALESCE(MAX(id), 1) FROM pe_order));
