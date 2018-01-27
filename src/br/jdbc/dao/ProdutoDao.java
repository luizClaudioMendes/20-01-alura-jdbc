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

public class ProdutoDao {
	private ConnectionPool connectionPool;
	private Connection connection;
	
	public ProdutoDao() throws SQLException, Exception  {
		super();
		getConexao();
	}
	
	private void getConexao() throws Exception, SQLException {
		connectionPool = (ConnectionPool) new ConnectionPool();
		connection = connectionPool.getConnection();
		connection.setAutoCommit(false);
	}
	
	public Produto salva(Produto produto) {
		try{	
			String sql = "insert into Produto (nome, descricao) values (?, ?)";
			try(PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
				stmt.setString(1, produto.getNome());
				stmt.setString(2, produto.getDescricao());
				boolean resultado = stmt.execute();
						
		 		System.out.println("O resultado foi: " + resultado);
		 		ResultSet resultSet = stmt.getGeneratedKeys();
		 		while (resultSet.next()) {
		 			produto.setId(resultSet.getInt(1));
		            System.out.println(produto + " gerado");
		        }
		 		resultSet.close();
		 		
			}	
			connection.commit();
	 		
		}catch (Exception e) {
			e.printStackTrace();
            System.out.println("Rollback efetuado");
		}
		return produto;
	}

	public List<Produto> lista() {
		List<Produto> resultado = new ArrayList<>();
		
		try {
			Statement statement = connection.createStatement();
			statement.execute("select * from Produto");
			ResultSet resultSet;
				resultSet = statement.getResultSet();
			while(resultSet.next()) {
				Produto produto = transformaResultadosEmProdutos(resultSet);
				resultado.add(produto);
			}
			resultSet.close();
			statement.close();
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return resultado;
	}
	
	public List<Produto> lista(Categoria categoria) {
		List<Produto> resultado = new ArrayList<>();
		
		try {
			String sql = "select * from Produto where categoria_id = ?";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setInt(1, categoria.getId());
			statement.execute();
			ResultSet resultSet = statement.getResultSet();
			while(resultSet.next()) {
				Produto produto = transformaResultadosEmProdutos(resultSet);
				resultado.add(produto);
			}
			resultSet.close();
			statement.close();
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return resultado;
	}

	public Produto transformaResultadosEmProdutos(ResultSet resultSet) throws SQLException {
		Produto produto = new Produto();
		produto.setId(resultSet.getInt("id"));
		produto.setNome(resultSet.getString("nome"));
		produto.setDescricao(resultSet.getString("descricao"));
		produto.setCategoria(resultSet.getInt("categoria_id"));
		return produto;
	}

	public void removeTodos() {
		try {
			Statement stmt = connection.createStatement();
	 		int resultado = stmt.executeUpdate("delete from Produto where id>3");
	 		System.out.println("Foram atualizadas: " + resultado+ " linhas.");
	 		stmt.close();
	 		connection.close();
		}catch (SQLException e ) {
			e.printStackTrace();
		}
	}


	

}
