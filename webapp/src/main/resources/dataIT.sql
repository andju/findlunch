INSERT IGNORE INTO `findlunchIT`.`country` (`country_code`, `name`) VALUES ('DE', 'Deutschland');

INSERT IGNORE INTO `findlunchIT`.`day_of_week` (`id`, `name`, `day_number`) VALUES (1, 'Montag', 2);
INSERT IGNORE INTO `findlunchIT`.`day_of_week` (`id`, `name`, `day_number`) VALUES (2, 'Dienstag', 3);
INSERT IGNORE INTO `findlunchIT`.`day_of_week` (`id`, `name`, `day_number`) VALUES (3, 'Mittwoch', 4);
INSERT IGNORE INTO `findlunchIT`.`day_of_week` (`id`, `name`, `day_number`) VALUES (4, 'Donnerstag', 5);
INSERT IGNORE INTO `findlunchIT`.`day_of_week` (`id`, `name`, `day_number`) VALUES (5, 'Freitag', 6);
INSERT IGNORE INTO `findlunchIT`.`day_of_week` (`id`, `name`, `day_number`) VALUES (6, 'Samstag', 7);
INSERT IGNORE INTO `findlunchIT`.`day_of_week` (`id`, `name`, `day_number`) VALUES (7, 'Sonntag', 1);

INSERT IGNORE INTO `findlunchIT`.`kitchen_type` (`id`, `name`) VALUES (1, 'Italienisch');
INSERT IGNORE INTO `findlunchIT`.`kitchen_type` (`id`, `name`) VALUES (2, 'Indisch');
INSERT IGNORE INTO `findlunchIT`.`kitchen_type` (`id`, `name`) VALUES (3, 'Griechisch');
INSERT IGNORE INTO `findlunchIT`.`kitchen_type` (`id`, `name`) VALUES (4, 'Asiatisch');
INSERT IGNORE INTO `findlunchIT`.`kitchen_type` (`id`, `name`) VALUES (5, 'Bayerisch');


INSERT IGNORE INTO `findlunchIT`.`restaurant_type` (`id`, `name`) VALUES (1, 'Imbiss');
INSERT IGNORE INTO `findlunchIT`.`restaurant_type` (`id`, `name`) VALUES (2, 'Restaurant');
INSERT IGNORE INTO `findlunchIT`.`restaurant_type` (`id`, `name`) VALUES (3, 'Bäckerei');
INSERT IGNORE INTO `findlunchIT`.`restaurant_type` (`id`, `name`) VALUES (4, 'Sonstiges');

INSERT IGNORE INTO `findlunchIT`.`user_type` (`id`,`name`) VALUES (1, "Anbieter");
INSERT IGNORE INTO `findlunchIT`.`user_type` (`id`,`name`) VALUES (2, "Kunde");
INSERT IGNORE INTO `findlunch`.`user_type` (`id`,`name`) VALUES (3, "Betreiber");

INSERT IGNORE INTO `findlunch`.`euro_per_point` (`id`,`euro`) VALUES (1, 1.0);

INSERT IGNORE INTO `findlunch`.`minimum_profit` (`id`,`profit`) VALUES (1, 10);

-- account type
INSERT IGNORE INTO `findlunch`.`account_type` (`id`,`name`) VALUES (1, 'Forderungskonto');
INSERT IGNORE INTO `findlunch`.`account_type` (`id`,`name`) VALUES (2, 'Kundenkonto');

-- booking reason
INSERT IGNORE INTO `findlunch`.`booking_reason` (`id`,`reason`) VALUES (1, 'Forderung');
INSERT IGNORE INTO `findlunch`.`booking_reason` (`id`,`reason`) VALUES (2, 'Einzahlung');