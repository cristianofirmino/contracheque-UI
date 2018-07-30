package com.contrachequeUI.model;

public class ItemBean {
	private String iDPeople;
	private String lideranca;
	private int pagina;
	private String cargo;
	private String periodo;
	private String nome;
	
	public ItemBean(String matricula, int pagina) {
		super();
		this.iDPeople = matricula;
		this.pagina = pagina;
	}
	
	public ItemBean(String iDPeople, int pagina, String lideranca) {
		super();
		this.iDPeople = iDPeople;
		this.lideranca = lideranca;
		this.pagina = pagina;
	}
	
	public ItemBean(String nome, String iDPeople, String cargo, String periodo, String lideranca) {
		super();
		this.nome = nome;
		this.iDPeople = iDPeople;
		this.cargo = cargo;
		this.periodo = periodo;
		this.lideranca = lideranca;
	}
	
	public ItemBean(String nome, String iDPeople, String cargo, String lideranca) {
		super();
		this.nome = nome;
		this.iDPeople = iDPeople;
		this.cargo = cargo;
		this.lideranca = lideranca;
	}

	
	public ItemBean(String nome, String iDPeople, String cargo, String periodo, String lideranca, int pagina) {
		super();
		this.nome = nome;
		this.iDPeople = iDPeople;
		this.cargo = cargo;
		this.periodo = periodo;
		this.lideranca = lideranca;
		this.pagina = pagina;
	}

	public String getIDPeople() {
		return iDPeople;
	}
	public void setIDPeople(String matricula) {
		this.iDPeople = matricula;
	}
	public int getPagina() {
		return pagina;
	}
	public void setPagina(int pagina) {
		this.pagina = pagina;
	}
	public String getLideranca() {
		return lideranca;
	}
	public void setLideranca(String lideranca) {
		this.lideranca = lideranca;
	}

	public String getCargo() {
		return cargo;
	}

	public void setCargo(String cargo) {
		this.cargo = cargo;
	}

	public String getPeriodo() {
		return periodo;
	}

	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Override
	public String toString() {
		return "ItemBean [iDPeople=" + iDPeople + ", lideranca=" + lideranca + ", pagina=" + pagina + ", cargo=" + cargo
				+ ", periodo=" + periodo + ", nome=" + nome + "]";
	}

	@Override
	public boolean equals(Object obj) {
		
		Integrante integ = (Integrante) obj;
		return this.getIDPeople().equals(integ.getIdPeople());
	}

}
