create database diary;
use diary;

-- Hibernate:

create table book (
    id bigint not null auto_increment,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    title varchar(63) not null,
    user_id bigint not null,
    primary key (id)
) engine=InnoDB;

create table record (
    id bigint not null auto_increment,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    text longtext not null,
    book_id bigint not null,
    primary key (id)
) engine=InnoDB;

create table record_tags (
    record_id bigint not null,
    tag_id bigint not null,
    primary key (record_id, tag_id)
) engine=InnoDB;

create table tag (
    id bigint not null auto_increment,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    name varchar(63) not null,
    user_id bigint not null,
    primary key (id)
) engine=InnoDB;

create table user (
    id bigint not null auto_increment,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    email varchar(255) not null,
    password varchar(63) not null,
    user_role varchar(15) not null,
    username varchar(63) not null,
    primary key (id)
) engine=InnoDB;

alter table user
    drop index if exists UK_ob8kqyqqgmefl0aco34akdtpe;

alter table user
    add constraint UK_ob8kqyqqgmefl0aco34akdtpe unique (email);

alter table user
    drop index if exists UK_sb8bbouer5wak8vyiiy4pf2bx;

alter table user
    add constraint UK_sb8bbouer5wak8vyiiy4pf2bx unique (username);

alter table book
    add constraint FK1wxwagv6cm3vjrxqhmv884hir
    foreign key (user_id)
    references user (id);

alter table record
    add constraint FKbcjk1br3shw5ke3ry9lfiqwgp
    foreign key (book_id)
    references book (id);

alter table record_tags
    add constraint FKh8df0e6628eoqddmg5fectmxm
    foreign key (tag_id)
    references tag (id);

alter table record_tags
    add constraint FKa90d7bh0789xrwwuhli1ag5ya
    foreign key (record_id)
    references record (id);

alter table tag
    add constraint FKld85w5kr7ky5w4wda3nrdo0p8
    foreign key (user_id)
    references user (id);