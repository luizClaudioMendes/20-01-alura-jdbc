package br.jdbc.Connection;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class ConnectionPool {		
		//AULA 5
	
		//C3PO pool of connections
		DataSource dataSource;
		
		public ConnectionPool() throws Exception {
			ComboPooledDataSource pool = new ComboPooledDataSource();
			pool.setDriverClass("com.mysql.jdbc.Driver");
			pool.setJdbcUrl("jdbc:mysql://localhost:3306/loja-virtual");
			pool.setUser("root");
			pool.setPassword("root");
			pool.setMinPoolSize(1);
			pool.setAcquireIncrement(5);
			pool.setMaxPoolSize(20);
			dataSource = pool;
		}
		
		public Connection getConnection() throws SQLException {
	 		System.out.println("Conexao Aberta!");
			return dataSource.getConnection();
		}
	
}
