INSERT INTO pe_game_type (gt_id, gt_name) VALUES
                                              ('MEMORY', 'Memory'),
                                              ('TIC_TAC_TOE', 'TicTacToe'),
                                              ('TAG_ALONG_STORY', 'Mitmachgeschichten'),
                                              ('CATCH_THE_THIEF', 'Fang den Dieb');

INSERT INTO pe_game (g_is_enabled, g_gt_id, g_name, g_story_icon) VALUES
                                                                      (true, 'MEMORY', 'Memory fürs Anna', NULL),
                                                                      (true, 'TAG_ALONG_STORY', 'Geschichten aus der Voralberg', NULL),
                                                                      (false, 'TAG_ALONG_STORY', 'Geschichten aus der Hallstätter See', NULL);

INSERT INTO pe_move (m_name, m_description) VALUES
                                                ('emote_hurra', 'Hurra'),
                                                ('essen', 'Essen'),
                                                ('gehen', 'Gehen'),
                                                ('hand_heben', 'Hand heben'),
                                                ('highfive_links', 'Highfive links'),
                                                ('highfive_rechts', 'Highfive rechts'),
                                                ('klatschen', 'Klatschen'),
                                                ('strecken', 'Strecken'),
                                                ('umher_sehen', 'Umher sehen'),
                                                ('winken', 'Winken');


INSERT INTO pe_step (st_index, st_g_id, st_i_id, st_m_id, st_text) VALUES
                                                                       (1, 2, NULL, 1, 'GAME ID 2'),
                                                                       (1, 3, NULL, 2, 'GAME ID 3'),
                                                                       (2, 2, NULL, 3, 'GAME ID 2'),
                                                                       (2, 3, NULL, 4, 'GAME ID 3');



