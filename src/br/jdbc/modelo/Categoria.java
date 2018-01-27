package br.jdbc.modelo;

import java.util.List;

public class Categoria {
	
	private Integer id;
	private String nome;
	private List<Produto> produtos;
	
	public Categoria() {
		super();
	}
	
	public Categoria(Integer id, String descricao) {
		super();
		this.id = id;
		this.nome = descricao;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public List<Produto> getProdutos() {
		return produtos;
	}

	public void setProdutos(List<Produto> produtos) {
		this.produtos = produtos;
	}
	
	@Override
	public String toString() {
		return "["+id+"]"+" " +nome;
	}

}
