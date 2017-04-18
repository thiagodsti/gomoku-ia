package br.ufsc.si;

public class Posicao {

	private int jogador;
	private int linha;
	private int coluna;
	private int heuristica;

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

	public int getHeuristica() {
		return heuristica;
	}
	
	public void setHeuristica(int heuristica) {
		this.heuristica = heuristica;
	}

}
