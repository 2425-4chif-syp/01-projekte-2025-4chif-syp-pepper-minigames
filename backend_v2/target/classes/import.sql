INSERT INTO pe_game_type (gt_id, gt_name) VALUES
                                              ('MEMORY', 'Memory'),
                                              ('TIC_TAC_TOE', 'TicTacToe'),
                                              ('TAG_ALONG_STORY', 'Mitmachgeschichten'),
                                              ('CATCH_THE_THIEF', 'Fang den Dieb');

INSERT INTO pe_game (g_is_enabled, g_gt_id, g_name, g_story_icon) VALUES
                                                                      (true, 'MEMORY', 'Memory für Anna', NULL),
                                                                      (true, 'TAG_ALONG_STORY', 'Geschichten aus Vorarlberg', NULL),
                                                                      (false, 'TAG_ALONG_STORY', 'Geschichten vom Hallstätter See', NULL);

INSERT INTO pe_move (m_name, m_description) VALUES
                                                ('emote_hurra', 'Hurra'),
                                                ('essen', 'Essen'),
                                                ('gehen', 'Gehen'),
                                                ('hand_heben', 'Hand heben'),
                                                ('highfive_links', 'Highfive links'),
                                                ('highfive_rechts', 'Highfive rechts'),
                                                ('klatschen', 'Klatschen'),
                                                ('strecken', 'Strecken'),
                                                ('umher_sehen', 'Umhersehen'),
                                                ('winken', 'Winken');


INSERT INTO pe_step (st_index, st_g_id, st_i_id, st_m_id, st_text, st_duration_in_sec) VALUES
                                                                       (1, 3, NULL, 1, 'GAME ID 2', 10),
                                                                       (1, 3, NULL, 2, 'GAME ID 3', 15),
                                                                       (2, 3, NULL, 3, 'GAME ID 2', 5),
                                                                       (2, 3, NULL, 4, 'GAME ID 3', 10);



INSERT INTO pe_person (p_first_name, p_last_name, p_dob, p_room_no, p_isWorker, p_password, p_gender)
VALUES
    ('Anna', 'Müller', '1985-03-15', '101', TRUE, '$2a$10$examplehashedpassword1', false),
    ('Franz', 'Huber', '1990-07-22', '102', FALSE, NULL, true),
    ('Maria', 'Bauer', '1978-11-30', '103', TRUE, '$2a$10$examplehashedpassword3', true),
    ('Michael', 'Wagner', '1982-05-10', '104', FALSE, NULL, false),
    ('Sophie', 'Pichler', '1995-09-18', '105', TRUE, '$2a$10$examplehashedpassword5', false),
    ('Thomas', 'Steiner', '1988-12-25', '106', FALSE, NULL, false),
    ('Laura', 'Fischer', '1992-04-05', '107', TRUE, '$2a$10$examplehashedpassword7', false),
    ('David', 'Weber', '1980-08-14', '108', FALSE, NULL, true),
    ('Julia', 'Schmid', '1998-02-20', '109', TRUE, '$2a$10$examplehashedpassword9', true),
    ('Markus', 'Wolf', '1975-06-12', '110', FALSE, NULL, false);


-- Insert fake data into pe_game_score with updated constraints
INSERT INTO pe_game_score (gs_elapsed_time, gs_score, gs_date_time, gs_g_id, gs_p_id, gs_comment)
VALUES
    (120, 1500, '2023-10-01 14:30:00', 1, 1, '2x3'),
    (90, 2300, '2023-10-02 09:15:00', 1, 2, '2x4'),
    (300, 500, '2023-10-03 18:45:00', 1, 3, '3x4'),
    (60, 4000, '2023-10-04 12:00:00', 1, 4, '4x4'),
    (180, 1200, '2023-10-05 20:20:00', 1, 5, '2x3'),
    (240, 800, '2023-10-06 16:10:00', 1, 6, '2x4'),
    (150, 3000, '2023-10-07 11:05:00', 1, 7, '3x4'),
    (200, 2500, '2023-10-08 19:30:00', 1, 8, '4x4'),
    (100, 4500, '2023-10-09 13:25:00', 1, 9, '2x3'),
    (270, 600, '2023-10-10 17:50:00', 1, 10, '2x4');

<<<<<<< HEAD:backend_v2/target/classes/import.sql
<<<<<<< HEAD:backend_v2/target/classes/import.sql
=======

INSERT INTO pe_image (i_p_id, i_description, i_url, i_image)
VALUES
    (1, 'Profile picture of Anna Müller', NULL, NULL),
    (2, 'Profile picture of Franz Huber', NULL, NULL),
    (3, 'Profile picture of Maria Bauer', NULL, NULL),
    (4, 'Profile picture of Michael Wagner', NULL, NULL),
    (5, 'Profile picture of Sophie Pichler', NULL, NULL),
    (6, 'Profile picture of Thomas Steiner', NULL, NULL),
    (7, 'Profile picture of Laura Fischer', NULL, NULL),
    (8, 'Profile picture of David Weber', NULL, NULL),
    (9, 'Profile picture of Julia Schmid', NULL, NULL),
    (10, 'Profile picture of Markus Wolf', NULL, NULL);
>>>>>>> bfc662559ef0baa91b58eff2524a9c99fa6378f2:backend_v2/src/main/resources/import.sql
=======
>>>>>>> main:backend_v2/src/main/resources/import.sql
