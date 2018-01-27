package br.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.jdbc.Connection.ConnectionPool;
import br.jdbc.modelo.Categoria;
import br.jdbc.modelo.Produto;

public class CategoriaDao {
	private ConnectionPool connectionPool;
	private Connection connection;
	
	public CategoriaDao() throws SQLException, Exception  {
		super();
		getConexao();
	}
	
	private void getConexao() throws Exception, SQLException {
		connectionPool = (ConnectionPool) new ConnectionPool();
		connection = connectionPool.getConnection();
		connection.setAutoCommit(false);
	}

	public List<Categoria> lista() {
		List<Categoria> resultado = new ArrayList<>();
		
		try {
			Statement statement = connection.createStatement();
			statement.execute("select * from Categoria");
			ResultSet resultSet  = statement.getResultSet();
			while(resultSet.next()) {
				Categoria cat = new Categoria();
				cat.setId(resultSet.getInt("id"));
				cat.setNome(resultSet.getString("nome"));
				resultado.add(cat);
			}
			resultSet.close();
			statement.close();
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return resultado;
	}
	
	public List<Categoria> listaComProdutos() {
		List<Categoria> resultado = new ArrayList<>();
		Categoria ultima = null;
		
		try {
			Statement statement = connection.createStatement();
			statement.execute("select c.id as id_cat, c.nome as nome_cat, p.id as id_p, p.nome as nome_prod, p.descricao as desc_prod, "
					+ "p.categoria_id as cat_prod from Categoria as c\r\n" + 
					"join Produto as p on p.categoria_id = c.id "
					+ "order by id_cat");
			ResultSet resultSet  = statement.getResultSet();
			while(resultSet.next()) {
				if(ultima==null || !ultima.getNome().equals(resultSet.getString("nome_cat"))) {
                    Categoria categoria = new Categoria(resultSet.getInt("id_cat"), resultSet.getString("nome_cat"));
                    resultado.add(categoria);
                    ultima = categoria;
                }
				
				Produto prod = new Produto();
				prod.setId(resultSet.getInt("id_p"));
				prod.setNome(resultSet.getString("nome_prod"));
				prod.setDescricao(resultSet.getString("desc_prod"));
				prod.setCategoria(ultima.getId());
				
				if(ultima.getProdutos() == null) {
					ultima.setProdutos(new ArrayList<>());
				}
				
				ultima.getProdutos().add(prod);
			}
			resultSet.close();
			statement.close();
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return resultado;
	}

}
