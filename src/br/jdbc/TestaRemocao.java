package br.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import br.jdbc.Connection.Database;

public class TestaRemocao {

	public static void main(String[] args) throws SQLException {
		Connection connection = Database.getConnection();
 		
 		Statement stmt = connection.createStatement();
 		int resultado = stmt.executeUpdate("delete from Produto where id>3");
 		System.out.println("Foram atualizadas: " + resultado+ " linhas.");
 		
 		
 		 
 		
 		stmt.close();
 		connection.close();
 		System.out.println("Conexao Fechada!");
	}

}
