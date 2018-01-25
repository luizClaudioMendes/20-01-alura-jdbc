alter table Produto add column categoria_id integer;

update Produto set categoria_id=1 where id in (1,2);
update Produto set categoria_id=2 where id in (3);
update Produto set categoria_id=3 where id > 3;

select * from produto;