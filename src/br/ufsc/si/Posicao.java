package br.ufsc.si;

public class Posicao {

	private int valor;
	private int linha;
	private int coluna;

	public Posicao(int linha, int coluna) {
		this.setLinha(linha);
		this.setColuna(coluna);
	}

	public Posicao(int linha, int coluna, int valor) {
		this.setLinha(linha);
		this.setColuna(coluna);
		this.setValor(valor);
	}

	public int getValor() {
		return valor;
	}

	public void setValor(int valor) {
		this.valor = valor;
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

}
