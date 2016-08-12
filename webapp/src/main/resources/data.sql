INSERT IGNORE INTO `findlunch`.`country` (`country_code`, `name`) VALUES ('DE', 'Deutschland');

INSERT IGNORE INTO `findlunch`.`day_of_week` (`id`, `name`, `day_number`) VALUES (1, 'Montag', 2);
INSERT IGNORE INTO `findlunch`.`day_of_week` (`id`, `name`, `day_number`) VALUES (2, 'Dienstag', 3);
INSERT IGNORE INTO `findlunch`.`day_of_week` (`id`, `name`, `day_number`) VALUES (3, 'Mittwoch', 4);
INSERT IGNORE INTO `findlunch`.`day_of_week` (`id`, `name`, `day_number`) VALUES (4, 'Donnerstag', 5);
INSERT IGNORE INTO `findlunch`.`day_of_week` (`id`, `name`, `day_number`) VALUES (5, 'Freitag', 6);
INSERT IGNORE INTO `findlunch`.`day_of_week` (`id`, `name`, `day_number`) VALUES (6, 'Samstag', 7);
INSERT IGNORE INTO `findlunch`.`day_of_week` (`id`, `name`, `day_number`) VALUES (7, 'Sonntag', 1);

INSERT IGNORE INTO `findlunch`.`kitchen_type` (`id`, `name`) VALUES (1, 'Italienisch');
INSERT IGNORE INTO `findlunch`.`kitchen_type` (`id`, `name`) VALUES (2, 'Indisch');
INSERT IGNORE INTO `findlunch`.`kitchen_type` (`id`, `name`) VALUES (3, 'Griechisch');
INSERT IGNORE INTO `findlunch`.`kitchen_type` (`id`, `name`) VALUES (4, 'Asiatisch');
INSERT IGNORE INTO `findlunch`.`kitchen_type` (`id`, `name`) VALUES (5, 'Bayerisch');


INSERT IGNORE INTO `findlunch`.`restaurant_type` (`id`, `name`) VALUES (1, 'Imbiss');
INSERT IGNORE INTO `findlunch`.`restaurant_type` (`id`, `name`) VALUES (2, 'Restaurant');
INSERT IGNORE INTO `findlunch`.`restaurant_type` (`id`, `name`) VALUES (3, 'Bäckerei');
INSERT IGNORE INTO `findlunch`.`restaurant_type` (`id`, `name`) VALUES (4, 'Sonstiges');

INSERT IGNORE INTO `findlunch`.`restaurant` (`id`, `name`, `street`, `street_number`, `zip`, `city`, `country_code`, `location_latitude`, `location_longitude`, `email`, `phone`) VALUES (1, 'Test', 'Weg', '5', '85375', 'Neufahrn', 'DE', 5, 8, 'test@test.de', '081654445');
INSERT IGNORE INTO `findlunch`.`restaurant` (`id`, `name`, `street`, `street_number`, `zip`, `city`, `country_code`, `location_latitude`, `location_longitude`, `email`, `phone`) VALUES (10, 'Soon Café', 'Lothstraße', '15', '80335', 'München', 'DE', 48.154117, 11.5532038, 'test@test.de', '08920062770');
INSERT IGNORE INTO `findlunch`.`restaurant` (`id`, `name`, `street`, `street_number`, `zip`, `city`, `country_code`, `location_latitude`, `location_longitude`, `email`, `phone`) VALUES (11, 'Hometown Burger', 'Dachauer Str.', '153', '80335', 'München', 'DE', 48.153895, 11.553992, 'test@test.de', '08974037274');
INSERT IGNORE INTO `findlunch`.`restaurant` (`id`, `name`, `street`, `street_number`, `zip`, `city`, `country_code`, `location_latitude`, `location_longitude`, `email`, `phone`) VALUES (12, 'Chili-Asia Chili-Asia', 'Dachauer Str.', '151', '80335', 'München', 'DE', 48.153702, 11.554281, 'test@test.de', '08918985800');
INSERT IGNORE INTO `findlunch`.`restaurant` (`id`, `name`, `street`, `street_number`, `zip`, `city`, `country_code`, `location_latitude`, `location_longitude`, `email`, `phone`) VALUES (13, 'Wirtshaus Raffus', 'Dachauer Str.', '147', '80335', 'München', 'DE', 48.153237, 11.554839, 'test@test.de', '08918955924');
INSERT IGNORE INTO `findlunch`.`restaurant` (`id`, `name`, `street`, `street_number`, `zip`, `city`, `country_code`, `location_latitude`, `location_longitude`, `email`, `phone`) VALUES (14, 'Moon Night', 'Erzgießereistraße', '51', '80335', 'München', 'DE', 48.152736, 11.555421, 'test@test.de', '08912715919');

INSERT IGNORE INTO `findlunch`.`user_type` (`id`,`name`) VALUES (1, "Anbieter");
INSERT IGNORE INTO `findlunch`.`user_type` (`id`,`name`) VALUES (2, "Kunde");

INSERT IGNORE INTO `findlunch`.`user` (`id`, `username`, `password`, `restaurant_id`,`user_type_id`) VALUES (1, 'admin@admin.com', '$2a$10$Wo0WJitsLCrhuY4LJoOFRO.xfAWvTVuB8ktPZCqx1lFi67XnIimd6', 1, 1);
INSERT IGNORE INTO `findlunch`.`user` (`id`, `username`, `password`, `restaurant_id`,`user_type_id`) VALUES (2, 'user@user.de', '$2a$10$mkHTGZbKAMRsC54cH6kOYenXTnk4vFOTBQBFYgrQsJ7hkL3oClFSC', null, 2);