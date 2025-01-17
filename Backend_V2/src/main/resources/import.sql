-- This file allow to write SQL commands that will be emitted in test and dev.
-- The commands are commented as their support depends of the database
-- insert into myentity (id, field) values(1, 'field-1');
-- insert into myentity (id, field) values(2, 'field-2');
-- insert into myentity (id, field) values(3, 'field-3');
-- alter sequence myentity_seq restart with 4;

insert into pe_game_type (gt_id, gt_name)
values('MEMORY', 'Memory');

insert into pe_game_type (gt_id, gt_name)
values('TIC_TAC_TOE', 'TicTacToe');

insert into pe_game_type (gt_id, gt_name)
values('TAG_ALONG_STORY', 'Mitmachgeschichten');

insert into pe_game_type (gt_id, gt_name)
values('CATCH_THE_THIEF', 'Fang den Dieb');

insert into pe_game (g_is_enabled, g_gt_id, g_name, g_story_icon)
VALUES (true, 'MEMORY', 'Memory fürs Anna', null);

insert into pe_game (g_is_enabled, g_gt_id, g_name, g_story_icon)
VALUES (true, 'TAG_ALONG_STORY', 'Geschichten aus der Voralberg', null);

insert into pe_game (g_is_enabled, g_gt_id, g_name, g_story_icon)
VALUES (false, 'TAG_ALONG_STORY', 'Geschichten aus der Hallstätter See', null);