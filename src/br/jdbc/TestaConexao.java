package br.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import br.jdbc.Connection.Database;

public class TestaConexao {

	public static void main(String[] args) throws SQLException {
		Connection connection = Database.getConnection();
 		
 		connection.close();
 		System.out.println("Conexao Fechada!");
	}

}
