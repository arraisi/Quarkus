create table hibernate_sequence
(
    next_val bigint null
);

create table fruit
(
    id   bigint      not null primary key,
    name varchar(40) null,
    constraint fruit_name_uindex
        unique (name)
);

create table person
(
    id         bigint       not null primary key,
    created    datetime(6)  null,
    created_by varchar(255) null,
    map        varchar(255) null,
    updated    datetime(6)  null,
    updated_by varchar(255) null,
    active     bit          null,
    birth      date         null,
    email      varchar(255) null,
    gender     int          null,
    name       varchar(255) null,
    password   varchar(255) null
);

create table pocket
(
    id        bigint         not null primary key,
    balance   decimal(19, 2) null,
    name      varchar(255)   null,
    person_id bigint         not null,
    constraint pocket_person_id_fk
        foreign key (person_id) references person (id)
);

create table product
(
    id         bigint         not null primary key,
    created    datetime(6)    null,
    created_by varchar(255)   null,
    map        varchar(255)   null,
    updated    datetime(6)    null,
    updated_by varchar(255)   null,
    name       varchar(255)   null,
    price      decimal(19, 2) null,
    quantity   int            null
);

create table role
(
    id   bigint       not null primary key,
    name varchar(255) null
);

create table person_role
(
    person_id bigint not null,
    role_id   bigint not null,
    constraint pr_person_id_fk
        foreign key (person_id) references person (id),
    constraint pr_role_id_fk
        foreign key (role_id) references role (id)
);

create table shop
(
    id         bigint       not null primary key,
    created    datetime(6)  null,
    created_by varchar(255) null,
    map        varchar(255) null,
    updated    datetime(6)  null,
    updated_by varchar(255) null,
    active     bit          null,
    quantity   int          null,
    person_id  bigint       null,
    product_id bigint       not null,
    constraint shop_product_id_fk
        foreign key (product_id) references product (id),
    constraint shop_person_id_fk
        foreign key (person_id) references person (id)
);


