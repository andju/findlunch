INSERT IGNORE INTO `findLunch_testinstance`.`country` (`country_code`, `name`) VALUES ('DE', 'Deutschland');

INSERT IGNORE INTO `findLunch_testinstance`.`day_of_week` (`id`, `name`, `day_number`) VALUES (1, 'Montag', 2);
INSERT IGNORE INTO `findLunch_testinstance`.`day_of_week` (`id`, `name`, `day_number`) VALUES (2, 'Dienstag', 3);
INSERT IGNORE INTO `findLunch_testinstance`.`day_of_week` (`id`, `name`, `day_number`) VALUES (3, 'Mittwoch', 4);
INSERT IGNORE INTO `findLunch_testinstance`.`day_of_week` (`id`, `name`, `day_number`) VALUES (4, 'Donnerstag', 5);
INSERT IGNORE INTO `findLunch_testinstance`.`day_of_week` (`id`, `name`, `day_number`) VALUES (5, 'Freitag', 6);
INSERT IGNORE INTO `findLunch_testinstance`.`day_of_week` (`id`, `name`, `day_number`) VALUES (6, 'Samstag', 7);
INSERT IGNORE INTO `findLunch_testinstance`.`day_of_week` (`id`, `name`, `day_number`) VALUES (7, 'Sonntag', 1);

INSERT IGNORE INTO `findLunch_testinstance`.`kitchen_type` (`id`, `name`) VALUES (1, 'Italienisch');
INSERT IGNORE INTO `findLunch_testinstance`.`kitchen_type` (`id`, `name`) VALUES (2, 'Indisch');
INSERT IGNORE INTO `findLunch_testinstance`.`kitchen_type` (`id`, `name`) VALUES (3, 'Griechisch');
INSERT IGNORE INTO `findLunch_testinstance`.`kitchen_type` (`id`, `name`) VALUES (4, 'Asiatisch');
INSERT IGNORE INTO `findLunch_testinstance`.`kitchen_type` (`id`, `name`) VALUES (5, 'Bayerisch');


INSERT IGNORE INTO `findLunch_testinstance`.`restaurant_type` (`id`, `name`) VALUES (1, 'Imbiss');
INSERT IGNORE INTO `findLunch_testinstance`.`restaurant_type` (`id`, `name`) VALUES (2, 'Restaurant');
INSERT IGNORE INTO `findLunch_testinstance`.`restaurant_type` (`id`, `name`) VALUES (3, 'Bäckerei');
INSERT IGNORE INTO `findLunch_testinstance`.`restaurant_type` (`id`, `name`) VALUES (4, 'Sonstiges');

INSERT IGNORE INTO `findLunch_testinstance`.`user_type` (`id`,`name`) VALUES (1, "Anbieter");
INSERT IGNORE INTO `findLunch_testinstance`.`user_type` (`id`,`name`) VALUES (2, "Kunde");
INSERT IGNORE INTO `findLunch_testinstance`.`user_type` (`id`,`name`) VALUES (3, "Betreiber");

INSERT IGNORE INTO `findLunch_testinstance`.`euro_per_point` (`id`,`euro`) VALUES (1, 1.0);

INSERT IGNORE INTO `findLunch_testinstance`.`minimum_profit` (`id`,`profit`) VALUES (1, 10);

-- account type
INSERT IGNORE INTO `findLunch_testinstance`.`account_type` (`id`,`name`) VALUES (1, 'Forderungskonto');
INSERT IGNORE INTO `findLunch_testinstance`.`account_type` (`id`,`name`) VALUES (2, 'Kundenkonto');

-- booking reason
INSERT IGNORE INTO `findLunch_testinstance`.`booking_reason` (`id`,`reason`) VALUES (1, 'Forderung');
INSERT IGNORE INTO `findLunch_testinstance`.`booking_reason` (`id`,`reason`) VALUES (2, 'Einzahlung');

-- Account Data
INSERT IGNORE INTO `account` (`id`, `account_number`, `account_type_id`) VALUES
(1, 1, 1),
(2, 728666923, 2),
(3, 656553911, 2),
(4, 646594879, 2);

-- Restaurant Data
INSERT IGNORE INTO `findLunch_testinstance`.`restaurant` (`id`, `customer_id`, `name`, `street`, `street_number`, `zip`, `city`, `country_code`, `location_latitude`, `location_longitude`, `email`, `phone`, `url`, `restaurant_type_id`, `restaurant_uuid`, `qr_uuid`) VALUES
(11, 510207137, 'FH München Mensa', 'Lothstraße', '13', '80335', 'München', 'DE', 48.1534, 11.5518, 'fh@fh.com', '123123', 'www.aa.de', 4, '5b36b3e3-054d-41b9-be3c-19fcece09fb5', 0x89504e470d0a1a0a0000000d49484452000000fa000000fa0100000000a09767900000022a4944415478daed993d8e83400c858d282839023721178b04522e06379923a49c02e17dcf437643126db10d4f5a28103f5f819d67fbcdc4fcf763b213388113f8d780e1e87c764fb96f6f9ead5df86890021af7a5c36934ab709b2ff76be77e170326bb76f876c453ddaf16b7b8520498651c23de212855c06a66d9cca75611083dccdb151e7d16ccb140545633e3f1f7e943e91d0cf8160aa4601085f5eda72676380015400a7542a35a3a96bff9da6a01c86ded06003d1449cf5550ae05e086a554025a2c33d5a606f88acac7f5e07887ca9a100b221ba400f4a3e84c106d6fe8f633e4eb6a4009204626d08eb7692b2f1d00bf3de63abb3da711e3f1c49314c0ca4a7837c286300a161a5d931480b46e52c825c11c49d3f3381000f0ed756aa6b664d9d9f2f77ad00098655616926e54069abf8901e8f175caf49a90ef12ed89416901ac2cae2f6038d99922a8bd60040078cd81a1d0b0870d418f6a9e1d8802401b5c66504ccb668568e500a31e2200dea5c877aec400fa22b4273824543e16be5528570cb0d043cc75df5abe35cf865301083d2080cd10c784cabd1a806ecfa2a754e9da61936ca70705000120c16bd8ccd29eec7529773cf058a9b15b71645ed84df7aba4e38158f3c21a4305bc8ccda2bde114001ebb0723ea09dd2a360eccd480d8c2a2a18b1da328fff78d1a05c07ff606e1455e16c532c05c4652d8906dab500aa01ef0ed430a2f021b774b72405456cc4d14fd5256bf7bb327009c7f709cc0099cc05f802f0d4d513fcfb82e620000000049454e44ae426082),
(12, 954164649, 'Cafe Bar Mellow (Lounge Cafe Restaurant)', 'Dachauer Straße', '159', '80636', 'München', 'DE', 48.1545, 11.5528, 'mellow@mellow.com', '08989050923', 'www.aa.de', 2, '42e559a9-9cab-456a-a809-69f239bbb463', 0x89504e470d0a1a0a0000000d49484452000000fa000000fa0100000000a0976790000002284944415478daed99418eab3010441bb1f09223709370b14841cac5e0263e024b2f22f75495ff9f09c9cc5ffc0d2d0d2c50306fa4d8a92e577bccff7d2d7602277002bf1a305c63aab8d9d446af1cba850292fb6374dfccd2eaf7ecd5ae7c0c062c1c59acf7d2f93c26dfaea3751101eb3095b40cde6611132836dc9db75c2e2101ea01c3b7dc54b07c2b98838156592b54f079fbaef48e05be2e88827a187e34b1038175a33d6d7d96723100654c4f4b1d02a8aca71513b08b2a0bf24d1e0c309612943bcb4d47a0f8b40603ca84b58554a954eb333c807f34782c801e2f01940ea2c0e3c3767a880060a96fae7d1ddf5da27d182c3f16207bc76e74cff0fd5b4e12c5120c8054fb268aa2f9746fe51f00c002cfdc28e1f1a032de5d2d1ce04c70c5b4ca3e632822800918462055446350b0a7bc4b201100664dea15a9b83226f15daa4f1e150190d1cb48932635e1d3aefc2300dad75b7f413d140265f75b040028059812561996da53c30fb3680067f167f3acc30c590cdedc2a14c004d7762379fca240b2c5020a67d18c0a9b9391ca1e0dd0c141a1dbb79e1c8f160e6078cb12adf2318b0af379ce720100b593496e5a5159ca9ded442610f0b74943cc6c0904ab6c97d776f260403dafa231def5ed3021d5608096da747a5075d88177c95f7f8ba3017d6df66792aa02494c4046250f60bbb6d7431840276e8a4902d2db01e6d1804eb4a676cec19e8d8de59b471d0cb4ca52565777fe9ee50200e73f384ee0044ee07f800fda2b6ae4755a414e0000000049454e44ae426082),
(13, 276767662, 'Soon Coffe', 'Lothstraße', '15', '80335', 'München', 'DE', 48.1542, 11.5532, 'soon@soon.com', '33425235', 'www.aa.de', 3, '2188dd55-69e2-4fa4-ab58-f7358398e6e7', 0x89504e470d0a1a0a0000000d49484452000000fa000000fa0100000000a0976790000002224944415478daed993d8e84300c858d28527284dc042e86c4485c6ce6263902650a84f73d9bd50c2bb4c536585a52a0217c456cf9f92723fafb7aca0ddcc00dfc6b40b0b2be96b6e0e521f835726b0a0524d535e3f10005c05e559760c053c62c0d775eaaa54ab7eeafe1808156e8d26a1d96312890cccb1be2005444c0e26113866aa75a1b002701732d60ca4ad0d3fb7122bd8b015bb5c78e0cc8018887e924895d0db8e81791da77b30a1ec59c1e0ad8a45569742e088589d9548bf49fae0e00e0056bb05377ab9815351ed0f9b10ba8355b36e55e28c0d213b6e7c2c8c809a6e43d72e30075e0b11b5d052915caeabb1f753306b0a003d9ac0d712faff2c38a0040b367a662f18a440a796d1ff1100190c60ba530518d826f7075348052477384ba09f9cf9a081ce41f0180f2ed1b3b101a4065217c6301e8d52756f8315b9b94f6d6586301ece52ae3c14b12ec39ca3f020029b51e14e6ea64c0a7ab23005e2d8525891aabe22b16501b6b4320aa6ad59246a56312bb1e40a21f394ece5ee1dd8acf6c1f04d895c56f0c5f0f8f50000ad183f3cf23f3ecc5c6497d060358d22d1e8a5f21e05b39e6870000bd2c267a344c56dc85d36f2c40f7e95cac6e16ab46e1807de67510d98a89ea58580300efdb83ea374678a4f3eb850b01ab9b3dba760b0aaff03181efd6985d53ab310136ec6ce225c386560f83790880f100e5af9cce4dfe7629aca10053965dc1c0d5ad17f7c3a41601b8ffe0b8811bb881bf005f90ae3281ae9564d70000000049454e44ae426082);

-- User Data
INSERT IGNORE INTO `findLunch_testinstance`.`user` (`id`, `username`, `password`, `restaurant_id`, `user_type_id`, `account_id`) VALUES
(1, 'admin@admin.com', '$2a$10$Wo0WJitsLCrhuY4LJoOFRO.xfAWvTVuB8ktPZCqx1lFi67XnIimd6', NULL, 1, NULL),
(2, 'user@user.de', '$2a$10$mkHTGZbKAMRsC54cH6kOYenXTnk4vFOTBQBFYgrQsJ7hkL3oClFSC', NULL, 2, NULL),
(3, 'owner@owner.com', '$2a$10$TlzzmKRGrRe/KzPUp111Ue0i.jMm.6uk5b62NCoL6N5DNZEmLkqFa', NULL, 3, 1),
(4, 'fh@fh.com', '$2a$10$TlzzmKRGrRe/KzPUp111Ue0i.jMm.6uk5b62NCoL6N5DNZEmLkqFa', 11, 1, 2),
(5, 'mellow@mellow.com', '$2a$10$1ALnrR/Wd/2dRLE7Kq8Gy.o.EyfPriBI6LB/ICae2DrHpHzgCNkVe', 12, 1, 3),
(6, 'soon@soon.com', '$2a$10$0T975lF3u9yG/rdBdsZL/uA5HBcx49W4OF8jaVMUKNbQdtnw2i7Zu', 13, 1, 4),
(8, 'a@a.com', '$2a$10$4AjQi77uMdNczVoma.zdWubOlOgot.LHvraZOV0ILo/srOH639DTq', NULL, 2, NULL),
(9, 'deniz@deniz.com', '$2a$10$JqPp8yWlIq4YYAHQ38NnKunWlGd1G7oQG1oe3njjyeVTdKvD4ULUe', NULL, 2, NULL),
(10, 'tom@tom.com', '$2a$10$wz4cYgRAo91l/rP7pT6i2uOjOrVG1YQ1YVZwLU7mQF8.EBBHJ9Kfi', NULL, 2, NULL);

-- offer
INSERT IGNORE INTO `findLunch_testinstance`.`offer` (`id`, `restaurant_id`, `title`, `description`, `price`, `preparation_time`, `start_date`, `end_date`, `needed_points`) VALUES
(1, 11, 'Champignonreispfanne (Tg1) ', 'vegan', '1.00', 2, '2017-01-08', '2017-03-31', 30),
(2, 11, 'Hackbällchen mit Paprikasauce (Tg3)', 'Rindfleisch und Schweinefleisch', '1.90', 4, '2017-01-08', '2017-04-30', 35),
(3, 11, 'Pfannkuchen mit Schokosauce (Tg2)', 'fleischlos', '1.59', 3, '2017-01-08', '2017-05-01', 25),
(4, 11, 'Putengulasch (Tg4) ', 'Putenfleisch', '2.40', 5, '2017-01-08', '2017-04-30', 37),
(5, 12, 'Country Potatoes', 'Knusprige Countrypotatoes mit Sourcreamdip', '4.90', 5, '2017-01-08', '2017-04-30', 40),
(6, 12, 'Feurige Bohnen', 'Chili con Carne, der leckere texanische Eintopf)', '4.50', 6, '2017-01-08', '2017-05-01', 38),
(7, 12, 'Fleischpflanzerlsemmel', 'Frisch', '2.50', 3, '2017-01-08', '2017-05-01', 23),
(8, 12, 'Wiener mit Semmel oder Kartoffelsalat', 'klein', '2.90', 2, '2017-01-08', '2017-05-01', 15),
(9, 12, 'Wiener mit Semmel oder Kartoffelsalat ', 'groß', '4.00', 5, '2017-01-08', '2017-05-01', 30),
(10, 13, 'Butterbreze', 'mit Salz', '1.10', 1, '2017-01-08', '2017-05-01', 10),
(11, 13, 'Früchtetee', 'mit Himbeeren und Erdbeeren', '1.30', 2, '2017-01-08', '2017-05-01', 15),
(12, 13, 'Nussschnecke', 'jeden Tag frisch', '1.80', 1, '2017-01-08', '2017-05-01', 20),
(13, 13, 'Schwarzer Kaffe', 'aus Brasilien', '1.00', 1, '2017-01-08', '2017-05-01', 8);

-- offer has day
INSERT IGNORE INTO `findLunch_testinstance`.`offer_has_day_of_week` (`offer_id`, `day_of_week_id`) VALUES
(1, 1),
(1, 2),
(1, 3),
(1, 4),
(1, 5),
(1, 6),
(1, 7),
(2, 1),
(2, 2),
(2, 3),
(2, 4),
(2, 5),
(2, 6),
(2, 7),
(3, 1),
(3, 2),
(3, 3),
(3, 4),
(3, 5),
(3, 6),
(3, 7),
(4, 1),
(4, 2),
(4, 3),
(4, 4),
(4, 5),
(4, 6),
(4, 7),
(5, 1),
(5, 2),
(5, 3),
(5, 4),
(5, 5),
(5, 6),
(5, 7),
(6, 1),
(6, 2),
(6, 3),
(6, 4),
(6, 5),
(6, 6),
(6, 7),
(7, 1),
(7, 2),
(7, 3),
(7, 4),
(7, 5),
(7, 6),
(7, 7),
(8, 1),
(8, 2),
(8, 3),
(8, 4),
(8, 5),
(8, 6),
(8, 7),
(9, 1),
(9, 2),
(9, 3),
(9, 4),
(9, 5),
(9, 6),
(9, 7),
(10, 1),
(10, 2),
(10, 3),
(10, 4),
(10, 5),
(10, 6),
(10, 7),
(11, 1),
(11, 2),
(11, 3),
(11, 4),
(11, 5),
(11, 6),
(11, 7),
(12, 1),
(12, 2),
(12, 3),
(12, 4),
(12, 5),
(12, 6),
(12, 7),
(13, 1),
(13, 2),
(13, 3),
(13, 4),
(13, 5),
(13, 6),
(13, 7);


-- time schedule
INSERT IGNORE INTO `findLunch_testinstance`.`time_schedule` (`id`, `restaurant_id`, `offer_start_time`, `offer_end_time`, `day_of_week_id`) VALUES
(1, 11, '1970-01-01 00:00:00', '1970-01-01 23:59:00', 1),
(2, 11, '1970-01-01 00:00:00', '1970-01-01 23:59:00', 2),
(3, 11, '1970-01-01 00:00:00', '1970-01-01 23:59:00', 3),
(4, 11, '1970-01-01 00:00:00', '1970-01-01 23:59:00', 4),
(5, 11, '1970-01-01 00:00:00', '1970-01-01 23:59:00', 5),
(6, 11, '1970-01-01 00:00:00', '1970-01-01 23:59:00', 6),
(7, 11, '1970-01-01 00:00:00', '1970-01-01 23:59:00', 7),
(8, 12, '1970-01-01 00:00:00', '1970-01-01 23:59:00', 1),
(9, 12, '1970-01-01 00:00:00', '1970-01-01 23:59:00', 2),
(10, 12, '1970-01-01 00:00:00', '1970-01-01 23:59:00', 3),
(11, 12, '1970-01-01 00:00:00', '1970-01-01 23:59:00', 4),
(12, 12, '1970-01-01 00:00:00', '1970-01-01 23:59:00', 5),
(13, 12, '1970-01-01 00:00:00', '1970-01-01 23:59:00', 6),
(14, 12, '1970-01-01 00:00:00', '1970-01-01 23:59:00', 7),
(15, 13, '1970-01-01 00:00:00', '1970-01-01 23:59:00', 1),
(16, 13, '1970-01-01 00:00:00', '1970-01-01 23:59:00', 2),
(17, 13, '1970-01-01 00:00:00', '1970-01-01 23:59:00', 3),
(18, 13, '1970-01-01 00:00:00', '1970-01-01 23:59:00', 4),
(19, 13, '1970-01-01 00:00:00', '1970-01-01 23:59:00', 5),
(20, 13, '1970-01-01 00:00:00', '1970-01-01 23:59:00', 6),
(21, 13, '1970-01-01 00:00:00', '1970-01-01 23:59:00', 7);