package br.jdbc;

import br.jdbc.dao.ProdutoDao;
import br.jdbc.modelo.Produto;

public class InsereProduto {
	public static void main(String[] args) throws Exception {		
		Produto produto = new Produto("Pneu de carro", "Pneu de carro aro 18");
		
		new ProdutoDao().salva(produto);
	}
}
