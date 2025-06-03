create table users
(
    user_id int auto_increment primary key,
    username varchar(50) not null,
    password varchar(50) not null
);

create table styles
(
    style_id int auto_increment primary key,
    name varchar(50) not null
);

create table beers
(
    beer_id int auto_increment primary key,
    name varchar(50) not null ,
    abv decimal(10, 1) not null,
    style_id int not null,
    created_by int not null,

    constraint beer_styles_style_id_fk
    foreign key (style_id) references styles (style_id),
    constraint beer_users_user_id_fk
        foreign key (created_by) references users (user_id)

);

create table users_beers(

    user_id int not null,
    beer_id int not null,
    drunk tinyint(1) not null,

    constraint users_beers_beers_beer_id_fk
    foreign key (beer_id) references beers (beer_id),
    constraint users_beers_users_user_id_fk
        foreign key (user_id) references users (user_id)
);

create table roles(

  role_id int not null auto_increment primary key,
  name varchar(50) not null
);

create table users_roles(

     user_id int not null,
     role_id int not null,

     constraint users_roles_roles_role_fk
    foreign key (role_id) references roles (role_id),
    constraint users_roles_users_user_fk
     foreign key (user_id) references users (user_id)
);