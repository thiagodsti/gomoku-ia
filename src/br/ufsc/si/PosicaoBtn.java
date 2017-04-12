package br.ufsc.si;

import javax.swing.JButton;

public class PosicaoBtn extends JButton {

	private int linha;
	private int coluna;

	public PosicaoBtn(int linha, int coluna) {
		super(" ");
		this.setLinha(linha);
		this.setColuna(coluna);
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
