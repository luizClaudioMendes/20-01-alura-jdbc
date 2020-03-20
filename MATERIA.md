# CURSO DE JDBC
###### concluído em 23/01/2018

## AULA 1
É muito comum que uma aplicação queira acessar um banco de dados para armazenar suas informações. Poderíamos escrever diretamente todo o código de conexão com um banco, por exemplo, MySQL. Mas se desejássemos mudar dele para um SQLServer, teríamos que reimplementar todo o protocolo de comunicação do servidor. Cada banco possui seu próprio protocolo, através do qual enviamos e recebemos requisições SQL, mas não queremos implementar algo tão baixo nível quanto o protocolo do banco.
Portanto precisamos de alguém que faça uma ponte entre nós e o banco. Algum tipo de interface comum a todos os bancos relacionais. Imaginemos uma interface de conexão com o banco:

```java
    public interface Connection {
      Result executeQuery(String sql);
    }
```
Cada um dos bancos nos forneceria uma API que devolve uma instância de Connection diferente. 
Se queremos um Mysql, fazemos:
```java
Connection mysql = new MysqlConnection("localhost", "usuario", "senha");
```
Ou se desejamos um SQLServer:
```java
Connection mysql = new SQLServerConnection("localhost", "usuario", "senha");
```
Tudo isso poderia funcionar, mas ainda estaríamos colocando a escolha do banco em nosso código, escolhendo isto na mão através do código compilado. Seria possível fazer essa escolha do banco através de uma String? Assim poderíamos posteriormente ler essa String de um arquivo de configuração e a escolha do banco ser feita automaticamente:
```java
Connection conexao = GerenciadorDeConexoes.getConnection("mysql", "localhost", "nome_do_banco", "usuario", "senha");
```
O código do gerenciador de conexões seria o de alguém que, de acordo com a String passada escolhe o componente adequado a ser instanciado e utilizado:
```java
public class GerenciadorDeConexoes {

  public static Connection getConnection(String tipo, String ip, String nome, String usuario, String senha) {

    if(tipo.equals("mysql")) {
      return new MySQLConnection(ip, nome, usuario, senha);
    } else if(tipo.equals("sqlserver")) {
      return new SqlServerConnection(ip, nome, usuario, senha);
    }

    throw new IllegalArgumentException("Tipo de banco não suportado");
  }
}
```
**Note que o papel do gerenciador de conexões é o de escolher a estratégia adequada de comunicação com o banco.** Ele escolhe alguém que sabe falar a língua do banco. **Ele escolhe o Driver que fala a língua do banco, e nos devolve uma conexão**. A estratégia escolhida depende somente da String que passamos. Seria interessante criarmos esse GerenciadorDeConexoes, ou GerenciadorDeDrivers, mas na verdade **ele já existe**:
```java
Connection connection = DriverManager.getConnection("jdbc:mysql://ip-do-banco/nome-do-banco", "usuario", "senha");
```
A interface **java.sql.Connection** também já existe e toda conexão que é aberta deve ser fechada:
```java
connection.close();
```
Neste curso utilizaremos o MySQL. 

- Criamos um novo projeto java no Eclipse chamado **loja-virtual**. 
- subimos o servidor do MySQL
- clicamos em new schema 
- damo-lhes o nome de loja-virtual

```sql
CREATE SCHEMA `loja-virtual` DEFAULT CHARACTER SET latin1 ;
```

- Agora conectamos ao banco via Java. Devemos** importar o driver** para o projeto. Clicamos no arquivo ‘***mysql-connector-java-5.1.45-bin.jar***’ e arrastamos ele para o projeto. Escolhemos "*copia*" e depois, no eclipse, clicamos com o botao direito em cima do arquivo e em *build path* e *add to build path*. 

- Criamos uma classe chamada **TestaConexao** e colocamos o código *main* que usa a string de conexão que conecta em **localhost** no banco chamado *loja-virtual*. Note que o driver do mysql especifica em sua documentação que a String de conexão deve começar:

```java
"jdbc:mysql://localhost:3306/loja-virtual", "root", "root" 
```

-  o método *main* deve estar desta forma:
```java
public static void main(String[] args) throws SQLException {
		Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/loja-virtual", "root", "root");
		System.out.println("Conexao Aberta!");
 		c.close();
		System.out.println("Conexao Fechada!");
 }
```

-  Vamos agora criar uma tabela em nosso banco. 

```sql
CREATE TABLE Produto (id INTEGER PRIMARY KEY, nome VARCHAR(255), descricao VARCHAR(255));
```
Vamos inserir 2 novos produtos:

```sql
insert into Produto values (1, 'Geladeira', 'Geladeira duas portas');
```
```sql
insert into Produto values (2, 'Ferro de passar', 'Ferro de passar roupa com vaporizador');
```
E vamos conferir que os dois estão presentes:
```sql
select * from Produto;
```
Voltemos agora ao nosso código Java. Desejamos selecionar todos os produtos. Sabemos o código SQL, mas como executar este statement a partir de uma conexão com o banco (Connection)?
```java
Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/loja-virtual", "root", "root");
connection.statement("insert ..."); //???
connection.close();
```
Pesquisando no Javadoc do JDBC encontramos o método createStatement, que possui o método execute:
```java
Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/loja-virtual", "root", "root");
Statement stmt = connection.createStatement();
boolean resultado = stmt.execute("select * from Produto");
System.out.println("O resultado foi: " + resultado);
connection.close();
```
O resultado é **true** se obtemos uma lista de resultados como quando fazemos um select. Ao rodarmos nosso programa, o resultado é exatamente este, true. Mas como acessar o conjunto de dados que é o resultado da query? Queremos acessar o conjunto de resultados, o **ResultSet**. Para isso o **Statement** possui o método **getResultSet**:
```java
ResultSet resultSet = statement.getResultSet();
```
Após executar a query, o **cursor de busca é posicionado antes do primeiro resultado**. **Precisamos andar para o primeiro elemento**, então **resultSet.next()**. 

Agora que estamos no primeiro resultado, podemos usar o método **getString** (e similares) para **extrair o valor dos campos que queremos**:
```java
int id = resultSet.getInt("id");
String nome = resultSet.getString("nome");
String descricao = resultSet.getString("descricao");
```
Após terminarmos de analisar este primeiro elemento queremos entrar em um loop. Analisar o segundo, o terceiro etc. Então colocamos todo este código em loop:
```java
while(resultSet.next()) {
    int id = resultSet.getInt("id");
    String nome = resultSet.getString("nome");
    String descricao = resultSet.getString("descricao");
}
```
O que acontece é que quando executamos a query **o cursor fica posicionado antes do primeiro resultado**. Caso **não tenha nenhum resultado, o método next retorna false** e o loop não é executado. **Caso tenha um próximo resultado, entramos no loop** e processamos esta linha. A cada nova linha o loop é executado, até que não existem mais dados e o loop acaba. 

Em outras palavras, **o loop é executado para cada linha da tabela que é retornada**.

**Lembre-se que tudo que abrimos tem que ser fechado.** Portanto tanto o **ResultSet** quanto o **Statement** **devem ser fechados**.

```java
rs.close();
stmt.close();
```

Ficamos com um código final que executa um select e imprime os ids, nomes e descrições de tudo o que for retornado:

```java
	public static void main(String[] args) throws SQLException {
		Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/loja-virtual", "root", "root");
 		System.out.println("Conexao Aberta!");
 		
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
```


