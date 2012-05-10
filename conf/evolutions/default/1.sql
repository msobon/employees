# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table employee (
  id                        bigint not null,
  first_name                varchar(255),
  last_name                 varchar(255),
  home_address              varchar(255),
  mail_address              varchar(255),
  employment_date           timestamp,
  salary                    integer,
  comments                  varchar(255),
  constraint pk_employee primary key (id))
;

create table account (
  id                        bigint not null,
  email                     varchar(255),
  name                      varchar(255),
  password                  varchar(255),
  used_credits              bigint,
  is_admin                  boolean,
  constraint pk_account primary key (id))
;

create sequence employee_seq;

create sequence account_seq;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists employee;

drop table if exists account;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists employee_seq;

drop sequence if exists account_seq;

