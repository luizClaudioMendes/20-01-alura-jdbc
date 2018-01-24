package br.jdbc.Connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
	public static Connection getConnection() throws SQLException {
//		AULA 3
//		Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/loja-virtual", "root", "root");
// 		System.out.println("Conexao Aberta!");
//		return connection;
		
//		AULA 4
		Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/loja-virtual", "root", "root");
 		System.out.println("Conexao Aberta!");
 		connection.setAutoCommit(false);
		return connection;
	}

}
