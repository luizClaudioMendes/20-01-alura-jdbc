package br.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import br.jdbc.Connection.Database;

public class TestaListagem {

	public static void main(String[] args) throws SQLException {
		Connection connection = Database.getConnection();
 		
 		Statement stmt = connection.createStatement();
 		boolean resultado = stmt.execute("select * from Produto");
 		System.out.println("O resultado foi: " + resultado);

 		ResultSet resultSet = stmt.getResultSet();
 		
 		while(resultSet.next()) {
 		    int id = resultSet.getInt("id");
 		    String nome = resultSet.getString("nome");
 		    String descricao = resultSet.getString("descricao");
 		    
 		    System.out.println(id+"-"+nome+"-"+descricao);
 		}

 		resultSet.close();
 		stmt.close();
 		connection.close();
 		System.out.println("Conexao Fechada!");
	}

}
