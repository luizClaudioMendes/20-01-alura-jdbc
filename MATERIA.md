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


## AULA 2
Desejamos inserir um novo produto, um notebook, e para isso executamos o mesmo processo de antes.

criamos uma classe chamada **TestaInserçao** e colocamos um método *main*.
no método main:

```java
public static void main(String[] args) throws SQLException {
		Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/loja-virtual", "root", "root");
 		System.out.println("Conexao Aberta!");
 		
 		Statement stmt = connection.createStatement();
 		boolean resultado = stmt.execute("insert into Produto (nome, descricao) values ('Notebook', 'Notebook i5')");
 		System.out.println("O resultado foi: " + resultado);

 		stmt.close();
 		connection.close();
 		System.out.println("Conexao Fechada!");
	}
```

Executando o código acima obtemos o resultado esperado: **false**. E se fizermos um select no banco de dados temos que o notebook foi inserido com sucesso:

-  Apesar de ter funcionado, é interessante extrairmos o código que abre a conexão para um método separado. Criamos uma classe chamada **Database** com o método *getConnection*:
  ```java
 public static Connection getConnection() throws SQLException {
		Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/loja-virtual", "root", "root");
 		System.out.println("Conexao Aberta!");
		return connection;
	}
```

Apesar do código funcionar, **repare que não ficamos sabendo o ID que foi gerado dentro do código Java**. 

**Somente através de um select posterior seríamos capazes - de maneira complicada - de descobrir o id gerado. **

###### Na verdade a **API do Statement** nos entrega todas as chaves que foram geradas automaticamente caso utilizemos a configuração **Statement.RETURN_GENERATED_KEYS**.

Ao executar o statement passamos a opção mencionada como segundo argumento:

```java
boolean resultado = statement.execute("insert into Produto (nome, descricao) values ('Notebook', 'Notebook i5')", Statement.RETURN_GENERATED_KEYS);
```

Agora o método getGeneratedKeys nos retorna um ResultSet com todas as chaves geradas:

       

    ResultSet resultSet = statement.getGeneratedKeys();

Já sabemos iterar por um resultSet, portanto podemos imprimir todas as chaves que foram geradas. No nosso caso só será gerada a chave para o campo id, portanto fazemos um laço e imprimos o resultado do campo gerado ID:
     

      while (resultSet.next()) {
                String id = resultSet.getString("id");
                System.out.println(id + " gerado");
            }

Rodamos a aplicação mais algumas vezes até obter o resultado para o notebook:
5 gerado

Isso ocorreu pois temos agora três notebooks no banco. Executando o select no banco conseguimos notar esta repetição.

Queremos executar um delete para remover dois deles, para isto criamos uma nova classe **TestaRemocao**:
```java
public static void main(String[] args) throws SQLException {
		Connection connection = Database.getConnection();
 		
 		Statement stmt = connection.createStatement();
 		int resultado = stmt.executeUpdate("delete from Produto where id>3");
 		System.out.println("Foram atualizadas: " + resultado+ " linhas.");
 		stmt.close();
 		connection.close();
 		System.out.println("Conexao Fechada!");
	}
```

Mas antes de executar o delete, como descobrir quantas linhas foram removidas? Quero ter certeza que os dois notebooks extras serão removidos. 

Lembre-se que uma remoção também é uma atualização do banco. 

O método *getUpdateCount* diz o número de linhas atualizadas:
      

     int count = stmt.getUpdateCount();
     System.out.println(count + " linhas atualizadas");
	 
Pronto! Executamos o programa e temos sucesso: somente 2 linhas foram removidas. 

###### Como vimos, o JDBC é uma especificação que generaliza o acesso aos banco de dados relacionais, permitindo focarmos no SQL e trabalhar de maneira uniforme com a interface de Conexão, execução de Statement e resultados (ResultSet).


## AULA 3
Somos capazes de inserir novos produtos através de um Statement. Mas o que acontece se o nome do nosso produto é **Notebook'i5 2013**? Isso mesmo, com uma aspas simples no meio do nome?

O SQL resultante seria:


    insert into Produto (nome, descricao) values ('Notebook 'i5 2013', 'Notebook i5');

Nesse caso teremos uma **exception** pois o **SQL é inválido**. 

###### Acontece que toda vez que vamos concatenar dados de fora, como os dados que um usuário fornece, com uma query precisamos lembrar de fazer o "escape" das aspas e caracteres especiais. Precisamos tomar esse cuidado para evitar que o usuário final digite um valor que quebre nossa aplicação, ou ainda um código malicioso que altere os dados do banco de maneira indesejada.

Imagine uma query de login que faz a busca do usuário por login e senha:


    String sql = "select * from Usuario where login='" + login + "' and senha='" + senha + "'";
	
Caso o usuário forneça o login "xpto" e a senha "' or id=1" ele consegue o sql a seguir:


    select * from Usuario where login='xpto' and senha='' or id=1
	
**E acaba se logando como o usuário 1**. 

Esse tipo de código malicioso é chamado de **SQL Injection** e temos que evitá-lo tratando os caracteres especiais. Felizmente o JDBC já faz isso para nós através de um **PreparedStatement**. 

- Começamos passando **?** (pontos de interrogação) **no lugar dos valores** que queremos preencher:
       

    String sql = "insert into Produto (nome, descricao) values(?, ?)";
	
- Criamos ele como um Statement normal, mas já passando o SQL:
      

     String sql = "insert into Produto (nome, descricao) values(?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql,
                    Statement.RETURN_GENERATED_KEYS);
                    statement.close();

Agora falta colocar os valores nas posições adequadas. 

Ao invés de serem as posições 0 e 1,** usamos os números 1 e 2** para marcar o primeiro e o segundo ponto de interrogação:
      

     statement.setString(1, nome);
            statement.setString(2, descricao);
			
Pronto, agora podemos executar nosso statement:

       

    boolean resultado = statement.execute();
	
A partir daqui podemos trabalhar com os métodos do statement como já estávamos acostumados. Note que com a simples adoção de um **PreparedStatement** com os **?** passamos a ter acesso a duas vantagens:
1. o JDBC passa a fazer o escaping de caracteres especiais e não nos preocupamos com SQL Injection.
1. o JDBC dá a chance ao banco de dados de escolher a melhor maneira de executar uma query (definir o plano de ação), e podemos executar a mesma query com parâmetros diferentes bastando chamar o método *setter* e o *execute* novamente.

###### Na prática, sempre que utilizamos JDBC puro acessamos um PreparedStatement ao invés de Statements normais.

Recapitulando o Prepared Statement:
- Altere sua classe **TestaInsercao**. 
- Crie as variáveis **nome** e **descrição**, colocando um nome de produto que contenha aspas simples. 
- Prepare o sql a seguir:


    String sql = "insert into Produto (nome, descricao) values(?, ?)";
	
- Use o prepared statement:


    PreparedStatement statement = connection.prepareStatement(sql,
                    Statement.RETURN_GENERATED_KEYS);
					
- Configure o nome e a descrição do seu produto:



    statement.setString(1, nome);        
    statement.setString(2, descricao);
	
- Execute o statement com o método *execute* sem nenhum argumento.



    boolean resultado = statement.execute();
	
- Execute o método *main* e veja como o produto é inserido com sucesso, mesmo que as aspas simples estejam dentro do nome do produto.

Pergunta: ***Quais os riscos de utilizar um Statement ao invés de um PreparedStatement?***

###### Os riscos de se utilizar um statement ao invés de um prepared statement são de erros com caracteres que não são escapados automaticamente, quebrando a query e de código malicioso que pode ser inserido no input do usuário (como um delete) que será injetado na query a ser executada e rodará sem erros.


## AULA 4
Vamos retomar o código de nossa inserção. Leve em consideração que ele inclui somente um produto:
       

    PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
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
     		stmt.close();

Mas e se desejamos incluir dois produtos? Podemos extrair um método no momento em que chamamos o método *statement.setString*. Extraímos o método *adiciona*:


     public static void adiciona(Connection connection, String sql, String nome, String descricao) throws SQLException {
    		PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
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
     		stmt.close();
    	}

E nosso método *main* passa a chamar o método *adiciona*:
              

     adiciona(connection, sql, "TV LCD", "TV de 32 polegadas");
	 
Da mesma maneira, podemos logo em seguida adicionar um segundo produto:
       

     adiciona(connection, sql, "TV LCD", "TV de 32 polegadas");
     adiciona(connection, sql, "Blueray", "Blueray azul");

###### Note que o statement será executado, terá seus parâmetros alterados e será executado novamente. 

Sem problemas, rodamos o programa e dois produtos são adicionados no banco! Fazemos o select no manager e temos os produtos lá.

Mas e se algo acontece de errado entre o primeiro e o segundo insert? 

O programa já adicionou o primeiro produto. Vamos testar? Removemos os produtos novamente:


    delete from Produto where id>3
	
E adicionamos um if dentro de nosso método adiciona para jogar uma Exception no caso do Blueray:
 

     public static void adiciona(Connection connection, String sql, String nome, String descricao) throws SQLException {
    		
    		//erro proposital
    		if (nome.equals("Blueray")) {
                throw new IllegalArgumentException("Problema ocorrido");
            }		
    		PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
    
            // resto do método adiciona 
    
        }
Executamos nosso programa e note que a TV é adicionada, mesmo que o Blueray gere um erro. Conferimos no banco de dados.

Isto é, a cada novo statement do tipo insert (update, delete e similares), **o comando é comitado automaticamente para o servidor**, não dá para voltar atrás. 

###### Este é o comportamento padrão de uma Connection segundo a especificação do JDBC. 

Mas e se eu quiser executar **os dois inserts ou nada**? 

Isto é, tudo ou nada. Ou executa os dois com sucesso ou nenhum. **Nesse caso precisamos desativar o auto commit**. Para isso fazemos logo após receber a conexão:
          

     connection.setAutoCommit(false);
	 
Voltamos no nosso banco de dados, removemos os produtos extras para ficar com 3 produtos novamente:


    delete from Produto where id>3
Se rodarmos novamente o programa e efetuarmos o select notamos que nada foi inserido no banco, nem a TV, nem o Blueray!

Quando o auto commit está configurado como false precisamos indicar o momento de executar o commit, e não fizemos isso ainda. Vamos testar colocar o commit logo após adicionar o primeiro e o segundo produto?
        

    adiciona(connection, sql, "TV LCD", "TV de 32 polegadas");
    connection.commit();
    adiciona(connection, sql, "Blueray", "Blueray azul");
    connection.commit();

Rodamos novamente e agora sim a TV é gravada mas o blueray não.

Vamos apagar novamente os produtos e ficar só com os 3 primeiros:


    delete from Produto where id>3
Queremos agora deixar explícito a maneira de commit e de voltar atrás em nossa decisão, de fazer um **rollback**. Isto é, deixamos explícito o commit no caso de sucesso dos dois itens adicionados:
          

    		try {
    			PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
    			
    			stmt.setString(1, nome);
    			stmt.setString(2, descricao);
    			boolean resultado = stmt.execute();
    			connection.commit();		
    	 		System.out.println("O resultado foi: " + resultado);
    	 		ResultSet resultSet = stmt.getGeneratedKeys();
    	 		while (resultSet.next()) {
    	            int id = resultSet.getInt(1);
    	            System.out.println(id + " gerado");
    	        }
    	 		resultSet.close();
    	 		stmt.close();
    		}catch (Exception e) {
    			e.printStackTrace();
    		}
E dentro do nosso catch desejamos voltar atrás, executar um rollback em nossa operação, connection.rollback:
        

     		}catch (Exception e) {
    			e.printStackTrace();
    			connection.rollback();
                System.out.println("Rollback efetuado");
    		}
Mas temos também que fechar o statement que abrimos, onde fechamos ele? Dentro do try? Mas e se acontecer uma exception, não fecharemos: precisamos do close dentro de um bloco **finally**. E precisamos conferir que ela foi aberto com sucesso. São muitos detalhes pequenos que precisamos cuidar para fechar tudo o que abrimos em blocos do tipo **try/catch/finally**! 

###### Para evitar ter que se preocupar com o caso de fechar tais recursos, o Java 7 introduziu a construção try(recurso). Se abrirmos uma conexão, ou qualquer coisa "fechável", ela será automaticamente fechada quando o bloco try terminar, seja através de um sucesso ou de uma exception/error. Para evitar ter que se preocupar com o caso de fechar tais recursos, o Java 7 introduziu a construção try(recurso). Se abrirmos uma conexão, ou qualquer coisa "fechável", ela será automaticamente fechada quando o bloco try terminar, seja através de um sucesso ou de uma exception/error. 

Portanto podemos abrir a conexão com:
      

    		try(Connection connection = Database.getConnection()) {
    	 		String sql = "insert into Produto (nome, descricao) values (?, ?)";
    	 		adiciona(connection, sql, "TV LCD", "TV de 32 polegadas");	
    	 		adiciona(connection, sql, "Blueray", "Blueray azul");
    		}
E podemos fazer a mesma coisa para o prepared statement:
        

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
###### Note que agora não nos preocupamos mais em fechar os recursos que abrimos dentro do try. O compilador garante que eles serão fechados!

Se executarmos nosso sistema agora, podemos acessar o banco e verificamos que nada foi inserido. Inclusive o console mostra que o rollback foi efetuado. Isto é, enviamos os dois insert para o banco, mas como deu uma exception pedimos para ele voltar atrás em tudo, efetuar o rollback, portanto nada foi inserido.

Entao a classe **TestaInsercao** fica assim:


    public static void main(String[] args) throws SQLException {
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


