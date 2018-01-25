package br.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import br.jdbc.Connection.ConnectionPool;

@Deprecated
public class TestaInsercao {

	public static void main(String[] args) throws SQLException {		
//		AULA 5
		ConnectionPool connectionPool;
		
		try{
			
			connectionPool = (ConnectionPool) new ConnectionPool();
		
			Connection connection = connectionPool.getConnection();
			connection.setAutoCommit(false);
	 	
			String sql = "insert into Produto (nome, descricao) values (?, ?)";
	 		adiciona(connection, sql, "TV LCD", "TV de 32 polegadas");	
	 		adiciona(connection, sql, "Blueray", "Blueray azul");
	 		connection.commit();
		}catch (Exception e) {
			e.printStackTrace();
            System.out.println("Rollback efetuado");

		}
	}

	public static void adiciona(Connection connection, String sql, String nome, String descricao) throws SQLException {
		try(PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			
			stmt.setString(1, nome);
			stmt.setString(2, descricao);
			boolean resultado = stmt.execute();
					
	 		System.out.println("O resultado foi: " + resultado);
	 		ResultSet resultSet = stmt.getGeneratedKeys();
	 		while (resultSet.next()) {
	            int id = resultSet.getInt(1);
	            System.out.println(id + " gerado");
	        }
	 		resultSet.close();
		}	
		
	}

}
