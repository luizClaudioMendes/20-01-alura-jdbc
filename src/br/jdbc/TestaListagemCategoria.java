package br.jdbc;

import java.util.List;

import br.jdbc.dao.CategoriaDao;
import br.jdbc.modelo.Categoria;

public class TestaListagemCategoria {

	public static void main(String[] args) throws Exception { 		
		List<Categoria> categorias = new CategoriaDao().lista();
		
		for (Categoria cat : categorias) {
			System.out.println(cat);
		}

	}

}
