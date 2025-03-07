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
                                                                       (1, 2, NULL, 1, 'GAME ID 2', 10),
                                                                       (1, 3, NULL, 2, 'GAME ID 3', 15),
                                                                       (2, 2, NULL, 3, 'GAME ID 2', 5),
                                                                       (2, 3, NULL, 4, 'GAME ID 3', 10);



