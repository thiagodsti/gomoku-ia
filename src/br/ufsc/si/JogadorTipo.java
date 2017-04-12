package br.ufsc.si;

public enum JogadorTipo {

	COMPUTADOR(1), HUMANO(2);

	private int valor;

	private JogadorTipo(int valor) {
		this.valor = valor;
	}

	public int getValor() {
		return valor;
	}
}
