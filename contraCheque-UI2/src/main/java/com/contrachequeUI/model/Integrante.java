package com.contrachequeUI.model;

public class Integrante {

	private String cpf;
	private String idPeople;
	private String nome;
	private String email;

	public Integrante() {
	}

	public Integrante(String idPeople, String nome, String email) {
		this.idPeople = idPeople;
		this.nome = nome;
		this.email = email;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getIdPeople() {
		return idPeople;
	}

	public void setIdPeople(String idPeople) {
		this.idPeople = idPeople;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof ItemBean) {
			ItemBean ib = (ItemBean) obj;
			
			if (ib.getIDPeople() != null && this.getIdPeople() != null) {
				return this.getIdPeople().equals(ib.getIDPeople());
			}
		}

		if (obj instanceof Integrante) {
			Integrante i = (Integrante) obj;
			
			if (i.getIdPeople() != null && this.getIdPeople() != null) {
				return this.getIdPeople().equals(i.getIdPeople());
			}
		}
		

		return false;
	}

	@Override
	public String toString() {
		return "Integrante [cpf=" + cpf + ", idPeople=" + idPeople + ", nome=" + nome + ", email=" + email + "]";
	}

}
