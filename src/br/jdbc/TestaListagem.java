package br.jdbc;

import java.util.List;

import br.jdbc.dao.ProdutoDao;
import br.jdbc.modelo.Produto;

public class TestaListagem {

	public static void main(String[] args) throws Exception { 		
		List<Produto> produtos = new ProdutoDao().lista();
		
		for (Produto prod : produtos) {
			System.out.println(prod);
		}

	}

}
