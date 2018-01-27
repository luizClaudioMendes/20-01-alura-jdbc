select c.id as id_cat, c.nome as nome_cat, p.id as id_p, p.nome as nome_prod, p.descricao as desc_prod, p.categoria_id as cat_prod from Categoria as c
join Produto as p on p.categoria_id = c.id