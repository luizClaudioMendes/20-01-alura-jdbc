package br.jdbc;

import java.util.List;

import br.jdbc.dao.CategoriaDao;
import br.jdbc.dao.ProdutoDao;
import br.jdbc.modelo.Categoria;
import br.jdbc.modelo.Produto;

public class TestaListagemCategoria {

//	public static void main(String[] args) throws Exception { 		
//		List<Categoria> categorias = new CategoriaDao().lista();
//		
//		for (Categoria cat : categorias) {
//			for (Produto prod : new ProdutoDao().lista(cat)) {
//				System.out.println("Categoria: "+ cat + " - Produto: "+ prod);
//				
//			}
//		}
//
//	}
	
	public static void main(String[] args) throws Exception { 		
		List<Categoria> categorias = new CategoriaDao().listaComProdutos();
		
		for (Categoria cat : categorias) {
			for (Produto prod : cat.getProdutos()) {
				System.out.println("Categoria: "+ cat + " - Produto: "+ prod);
				
			}
		}

	}

}
