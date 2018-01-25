package br.jdbc;

import java.sql.SQLException;
import br.jdbc.dao.ProdutoDao;

public class TestaRemocao {

	public static void main(String[] args) throws SQLException {
		try {
			new ProdutoDao().removeTodos();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
