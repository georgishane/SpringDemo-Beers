INSERT INTO beers.styles (style_id, name)
VALUES (1, 'Pilsner');
INSERT INTO beers.styles (style_id, name)
VALUES (2, 'Pale Ale');
INSERT INTO beers.styles (style_id, name)
VALUES (3, 'Red Ale');
INSERT INTO beers.styles (style_id, name)
VALUES (4, 'Porter');
INSERT INTO beers.styles (style_id, name)
VALUES (5, 'Stout');
INSERT INTO beers.styles (style_id, name)
VALUES (6, 'Indian Pale Ale');
INSERT INTO beers.styles (style_id, name)
VALUES (7, 'Weissbier');
INSERT INTO beers.styles (style_id, name)
VALUES (8, 'Special Ale');

INSERT INTO beers.beers (beer_id, name, abv,style_id)
VALUES (1, 'Glarus English Ale', 4.6, 8);

INSERT INTO beers.beers (beer_id, name, abv,style_id)
VALUES (2, 'Rombus Porter', 5.0, 4);

INSERT INTO beers.beers (beer_id, name, abv,style_id)
VALUES (3, 'Opasen Char', 6.6, 6);

INSERT INTO beers.beers (beer_id, name, abv,style_id)
VALUES (4, 'Ailyak', 6.6, 6);

INSERT INTO beers.beers (beer_id, name, abv,style_id)
VALUES (5, 'Basi Kefa', 6.7, 6);

INSERT INTO beers.beers (beer_id, name, abv,style_id)
VALUES (6, 'Vitoshko lale', 5.5, 2);

INSERT INTO beers.beers (beer_id, name, abv,style_id)
VALUES (7, 'Divo Pivo', 4.5, 2);

INSERT INTO beers.beers (beer_id, name, abv,style_id)
VALUES (8, 'Bloody Muddy', 5.0, 2);

INSERT INTO beers.beers (beer_id, name, abv,style_id)
VALUES (9, 'Black Head', 5.0, 5);

INSERT INTO beers.beers (beer_id, name, abv,style_id)
VALUES (10, 'Pilsner Urquell', 4.4, 1);

INSERT INTO beers.users ( user_id,username, password, first_name, last_name, email)
VALUES (1, 'todor','pass1', 'Todor', 'Andonov','todor@telerikacademy.com');
INSERT INTO beers.users ( user_id,username, password, first_name, last_name, email)
VALUES (2, 'vladi','pass2', 'Vladislav', 'Venkov','vladi@telerikacademy.com');
INSERT INTO beers.users ( user_id,username, password, first_name, last_name, email)
VALUES (3, 'pesho','pass3', 'Petar', 'Raykov','pesho@telerikacademy.com');
