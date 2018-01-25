package br.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import br.jdbc.Connection.ConnectionPool;

public class TestaListagem {

	public static void main(String[] args) throws SQLException { 		
 		//AULA 5
		ConnectionPool connectionPool;
		
		try{
			
			//a connection aqui abre somente uma connection pool
			connectionPool = (ConnectionPool) new ConnectionPool();
			
			//for(int i = 0; i < 100; i++) {
				//a connection aqui abre varias conexoes o que é oneroso
				//connection = (ConnectionPool) new ConnectionPool();
				
				Connection c = connectionPool.getConnection();
				
				Statement statement = c.createStatement();
				System.out.println("Statement Aberta!");
				
				@SuppressWarnings("unused")
				boolean resultado = statement.execute("select * from Produto");
				
				ResultSet resultSet = statement.getResultSet();
				System.out.println("ResultSet Aberto!");
				
				while(resultSet.next()) {
					int id = resultSet.getInt("id");
					String nome = resultSet.getString("nome");
					String descricao = resultSet.getString("descricao");
					System.out.println(id + ":" + nome +":"+ descricao);
					
				}
				resultSet.close();
				System.out.println("ResultSet Fechado!");
				statement.close();
				System.out.println("Statement Fechada!");
				c.close();
				System.out.println("Conexao Fechada!");
			//}
			
			
			
		}catch (Exception e ) {
			e.printStackTrace();
		}
	}

}
