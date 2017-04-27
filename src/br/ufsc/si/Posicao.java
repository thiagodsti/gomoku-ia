package br.ufsc.si;

public class Posicao {

	int jogador;
	int linha;
	int coluna;
	double heuristica;

	public Posicao(int linha, int coluna) {
		this.setLinha(linha);
		this.setColuna(coluna);
	}

	public Posicao(int linha, int coluna, int jogador) {
		this.setLinha(linha);
		this.setColuna(coluna);
		this.setJogador(jogador);
	}

	public int getJogador() {
		return jogador;
	}

	public void setJogador(int jogador) {
		this.jogador = jogador;
	}

	public int getLinha() {
		return linha;
	}

	public void setLinha(int linha) {
		this.linha = linha;
	}

	public int getColuna() {
		return coluna;
	}

	public void setColuna(int coluna) {
		this.coluna = coluna;
	}

	@Override
	public String toString() {
		return "Linha: " + linha + " Coluna: " + coluna + " Heuristica: " + heuristica;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + coluna;
		result = prime * result + linha;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Posicao other = (Posicao) obj;
		if (coluna != other.coluna)
			return false;
		if (linha != other.linha)
			return false;
		return true;
	}

}
