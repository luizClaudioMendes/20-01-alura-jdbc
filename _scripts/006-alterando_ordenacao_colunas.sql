update Produto 
set categoria_id = 1 
where id = 24;

select * from Produto where id in (
	select max(id) 
    from Produto
);





select c.id as c_id, c.nome as c_nome, p.id as p_id, p.nome as p_nome, p.descricao as p_descricao 
from Produto as p 
join Categoria as c on p.categoria_id = c.id
order by c_id