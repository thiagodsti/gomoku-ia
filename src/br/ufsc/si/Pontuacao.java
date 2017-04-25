package br.ufsc.si;

public class Pontuacao {

	int abertura;
	int totalJogador;
	int totalAdversario;
	int livre;
	int bloqueado;
	long total;

	public int getAbertura() {
		return abertura;
	}

	public void setAbertura(int abertura) {
		this.abertura = abertura;
	}

	public int getTotalJogador() {
		return totalJogador;
	}

	public void setTotalJogador(int totalJogador) {
		this.totalJogador = totalJogador;
	}

	public int getTotalAdversario() {
		return totalAdversario;
	}

	public void setTotalAdversario(int totalAdversario) {
		this.totalAdversario = totalAdversario;
	}

	public int getBloqueado() {
		return bloqueado;
	}

	public void setBloqueado(int bloqueado) {
		this.bloqueado = bloqueado;
	}

	public int getLivre() {
		return livre;
	}

	public void setLivre(int livre) {
		this.livre = livre;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

}
