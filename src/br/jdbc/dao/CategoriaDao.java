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
				cat.setDescricao(resultSet.getString("descricao"));
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

}
