package br.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import br.jdbc.Connection.ConnectionPool;

public class TestaConexao {

	public static void main(String[] args) throws SQLException {
		
		DataSource connectionPool;
		try {
			connectionPool = (DataSource) new ConnectionPool();
			
			Connection connection = connectionPool.getConnection();
			
			if(connection != null ) {
				System.out.println("Conexao OK");
			}else {
				System.out.println("Erro na conexao");
			}
	 		
	 		connection.close();
	 		System.out.println("Conexao Fechada!");
	 		
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
