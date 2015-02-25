# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table chat (
  id                        bigint auto_increment not null,
  send_user_id              bigint,
  received_user_id          bigint,
  chat_type_id              bigint,
  state                     varchar(8),
  created                   datetime,
  constraint ck_chat_state check (state in ('SENDING','RECEIVED','ACCEPT','REJECT')),
  constraint pk_chat primary key (id))
;

create table chat_type (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  is_visible                tinyint(1) default 0,
  constraint pk_chat_type primary key (id))
;

create table user (
  id                        bigint auto_increment not null,
  login_id                  varchar(255),
  password                  varchar(255),
  password_salt             varchar(255),
  api_token                 varchar(255),
  device_token              varchar(255),
  locale                    varchar(255),
  os                        integer,
  phone_number              varchar(255),
  phone_number_token        varchar(255),
  updated_at                datetime,
  constraint uq_user_1 unique (login_id),
  constraint pk_user primary key (id))
;

alter table chat add constraint fk_chat_sendUser_1 foreign key (send_user_id) references user (id) on delete restrict on update restrict;
create index ix_chat_sendUser_1 on chat (send_user_id);
alter table chat add constraint fk_chat_receivedUser_2 foreign key (received_user_id) references user (id) on delete restrict on update restrict;
create index ix_chat_receivedUser_2 on chat (received_user_id);
alter table chat add constraint fk_chat_chatType_3 foreign key (chat_type_id) references chat_type (id) on delete restrict on update restrict;
create index ix_chat_chatType_3 on chat (chat_type_id);



# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table chat;

drop table chat_type;

drop table user;

SET FOREIGN_KEY_CHECKS=1;

