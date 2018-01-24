package br.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import br.jdbc.Connection.Database;

public class TestaInsercao {

	public static void main(String[] args) throws SQLException {
//		AULA 1 e 2
//		Connection connection = Database.getConnection();
// 		
// 		Statement stmt = connection.createStatement();
// 		boolean resultado = stmt.execute("insert into Produto (nome, descricao) values ('Notebook', 'Notebook i5')", Statement.RETURN_GENERATED_KEYS);
// 		System.out.println("O resultado foi: " + resultado);
// 		
// 		 ResultSet resultSet = stmt.getGeneratedKeys();
// 		 
// 		while (resultSet.next()) {
//            int id = resultSet.getInt(1);
//            System.out.println(id + " gerado");
//        }
//
// 		resultSet.close();
// 		stmt.close();
// 		connection.close();
// 		System.out.println("Conexao Fechada!");
		
//		AULA 3
//		Connection connection = Database.getConnection();
// 		String sql = "insert into Produto (nome, descricao) values (?, ?)";
//		
//		String nome = "Violao de corda";
//		String descricao = "violao com 6 cordas";
// 		
// 		PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
//		
//		stmt.setString(1, nome);
//		stmt.setString(2, descricao);
//
//		boolean resultado = stmt.execute();
//		
// 		System.out.println("O resultado foi: " + resultado);
// 		
// 		 ResultSet resultSet = stmt.getGeneratedKeys();
// 		 
// 		while (resultSet.next()) {
//            int id = resultSet.getInt(1);
//            System.out.println(id + " gerado");
//        }
//
// 		resultSet.close();
// 		stmt.close();
// 		connection.close();
// 		System.out.println("Conexao Fechada!");
		
//		AULA 4
		try(Connection connection = Database.getConnection()) {
	 		String sql = "insert into Produto (nome, descricao) values (?, ?)";
	 		adiciona(connection, sql, "TV LCD", "TV de 32 polegadas");	
	 		adiciona(connection, sql, "Blueray", "Blueray azul");
	 		connection.commit();
		}catch (Exception e) {
			e.printStackTrace();
            System.out.println("Rollback efetuado");

		}
 		System.out.println("Conexao Fechada!");
	}

	public static void adiciona(Connection connection, String sql, String nome, String descricao) throws SQLException {
		
		//erro proposital
		if (nome.equals("Blueray")) {
            throw new IllegalArgumentException("Problema ocorrido");
        }
		
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
