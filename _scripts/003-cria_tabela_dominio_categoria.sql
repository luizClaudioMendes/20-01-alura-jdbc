create table Categoria (id  INTEGER PRIMARY KEY auto_increment, nome varchar(255));

insert into Categoria (id,nome) values (0, 'Eletrodoméstico');
insert into Categoria (id,nome) values (1, 'Eletrônico');
insert into Categoria (id,nome) values (2, 'Móvel');

alter table Produto add column categoria_id integer;

update Produto set categoria_id=0 where id in (1,2);
update Produto set categoria_id=1 where id in (3);
update Produto set categoria_id=2 where categoria_id is null;