create table coordinates
(
    id bigserial primary key,
    x  bigint           not null
        constraint coordinates_x_check check (x <= 178),
    y  double precision not null
        constraint coordinates_y_check check (y <= 842.0)
);

create table person
(
    id          bigserial primary key,
    name        varchar(255)     not null,
    birthday    date             not null,
    height      double precision not null
        constraint person_height_check check (height >= 0),
    weight      bigint           not null
        constraint person_weight_check check (height >= 0),
    passport_id varchar(20) unique
);

create type moviegenre as enum ('DRAMA', 'FANTASY', 'THRILLER', 'TRAGEDY', 'WESTERN');

create table movie
(
    id             serial primary key,
    name           varchar(255) not null,
    coordinates_id bigint       not null references coordinates (id),
    creation_date  date         not null default now(),

    oscars_count   bigint       not null
        constraint movie_oscars_count_check
            check (oscars_count >= 0),
    usa_box_office bigint       not null
        constraint movie_usa_box_office_check
            check (usa_box_office >= 0),
    tagline        varchar(255) not null,
    genre          moviegenre   not null,
    person_id      bigint references person (id)
);






