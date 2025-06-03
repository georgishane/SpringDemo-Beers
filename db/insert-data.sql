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

INSERT INTO beers.beers (beer_id, name, abv,style_id, created_by)
VALUES (1, 'Glarus English Ale', 4.9, 8, 3);

INSERT INTO beers.beers (beer_id, name, abv,style_id, created_by)
VALUES (2, 'Rombus Porter', 5.0, 4, 2);

INSERT INTO beers.beers (beer_id, name, abv,style_id,created_by)
VALUES (3, 'Opasen Char', 6.6, 6,1);

INSERT INTO beers.beers (beer_id, name, abv,style_id, created_by)
VALUES (4, 'Ailyak', 6.6, 6,2);

INSERT INTO beers.beers (beer_id, name, abv,style_id,created_by)
VALUES (5, 'Basi Kefa', 6.7, 6,3);

INSERT INTO beers.beers (beer_id, name, abv,style_id, created_by)
VALUES (6, 'Vitoshko lale', 5.5, 2, 1);

INSERT INTO beers.beers (beer_id, name, abv,style_id, created_by)
VALUES (7, 'Divo Pivo', 4.5, 1,2);

INSERT INTO beers.beers (beer_id, name, abv,style_id, created_by)
VALUES (8, 'Bloody Muddy', 5.0, 2, 3);

INSERT INTO beers.beers (beer_id, name, abv,style_id, created_by)
VALUES (9, 'Black Head', 5.0, 5,2);

INSERT INTO beers.beers (beer_id, name, abv,style_id,created_by)
VALUES (10, 'Pilsner Urquell', 4.4, 1, 1);


INSERT INTO beers.personal_info(first_name, last_name, email)
VALUES ('Todor', 'Todorov', 'toshko@bav.bg');

INSERT INTO beers.personal_info(first_name, last_name, email)
VALUES ('Vladi', 'Peev', 'vladi@bav.bg');

INSERT INTO beers.personal_info(first_name, last_name, email)
VALUES ('Petar', 'Minc', 'pesho@bav.bg');

INSERT INTO beers.users (username, password, personal_info)
VALUES ('todor','pass1',1);
INSERT INTO beers.users (username, password, personal_info)
VALUES ('vladi','pass2',2);
INSERT INTO beers.users (username, password, personal_info)
VALUES ('pesho','pass3', 3);

INSERT INTO beers.users_beers (user_id, beer_id, drunk) VALUES(1, 1, 0);
INSERT INTO beers.users_beers (user_id, beer_id, drunk) VALUES(2, 2, 0);
INSERT INTO beers.users_beers (user_id, beer_id, drunk) VALUES(3, 3, 1);
INSERT INTO beers.users_beers (user_id, beer_id, drunk) VALUES(1, 5, 1);
INSERT INTO beers.users_beers (user_id, beer_id, drunk) VALUES(2, 3, 0);
INSERT INTO beers.users_beers (user_id, beer_id, drunk) VALUES(3, 4, 0);
INSERT INTO beers.users_beers (user_id, beer_id, drunk) VALUES(1, 3, 1);
INSERT INTO beers.users_beers (user_id, beer_id, drunk) VALUES(2, 5, 1);
INSERT INTO beers.users_beers (user_id, beer_id, drunk) VALUES(3, 7, 1);
INSERT INTO beers.users_beers (user_id, beer_id, drunk) VALUES(1, 8, 1);
INSERT INTO beers.users_beers (user_id, beer_id, drunk) VALUES(2, 10, 1);

INSERT INTO beers.roles (role_id, name) VALUES(1, 'User');
INSERT INTO beers.roles (role_id, name) VALUES(2, 'Admin');

INSERT INTO beers.users_roles(user_id, role_id) VALUE (1,1);
INSERT INTO beers.users_roles(user_id, role_id) VALUE (1,2);
INSERT INTO beers.users_roles(user_id, role_id) VALUE (2,1);
INSERT INTO beers.users_roles(user_id, role_id) VALUE (3,1);