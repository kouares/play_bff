create table memo.label (
  id int auto_increment primary key, 
  name varchar(100)
);

create table memo.memo (
  id int auto_increment primary key,
  title varchar(200) not null, 
  main_text varchar(10000),
  upadted_at datetime,
  created_at datetime not null
);

create table memo.label_mapping (
  memo_id int,
  label_id int,
  foreign key (memo_id) references memo(id),
  foreign key (label_id) references label(id)
);
