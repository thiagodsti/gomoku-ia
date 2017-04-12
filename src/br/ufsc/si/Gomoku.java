package br.ufsc.si;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class Gomoku {

	private static final int TOTAL_PARA_GANHAR = 5;
	private Jogador jogadorDaVez;
	private Jogador jogador1;
	private Jogador jogador2;
	private int[][] tabuleiro = new int[15][15];
	private List<Posicao> jogadas;
	private TabuleiroFrame frame;

	public void iniciarJogo(TabuleiroFrame frame) {
		this.frame = frame;
		jogador1 = new Jogador();
		jogador1.setPeca("C");
		jogador1.setTipo(JogadorTipo.COMPUTADOR);

		jogador2 = new Jogador();
		jogador2.setPeca("H");
		jogador2.setTipo(JogadorTipo.HUMANO);

		jogadorDaVez = jogador1;

		jogadas = new ArrayList<>();

		// Jogador computador sempre começa no meio.
		if (jogadorDaVez.getTipo().equals(JogadorTipo.COMPUTADOR)) {
			PosicaoBtn btn = this.frame.encontrarBotao(7, 7);
			btn.doClick();
		}

	}

	// Valores para o IA
	// peça -> 1 x 125
	// dupla -> 126 * 49
	// tripla -> 6175 * 25
	// quadrupla-> 154.376

	private void jogarComputador() {

	}

	public void jogar(PosicaoBtn btn) {
		btn.setText(jogadorDaVez.getPeca());
		btn.setEnabled(false);
		Posicao posicao = new Posicao(btn.getLinha(), btn.getColuna(), jogadorDaVez.getTipo().getValor());
		jogadas.add(posicao);
		tabuleiro[btn.getLinha()][btn.getColuna()] = posicao.getValor();
		if (temGanhador()) {
			JOptionPane.showMessageDialog(btn.getParent(),
					String.format("O jogador %s ganhou", jogadorDaVez.getTipo()));
		}
		passarVez();

		if (jogadorDaVez.getTipo().equals(JogadorTipo.COMPUTADOR)) {
			this.jogarComputador();
		}
	}

	private boolean temGanhador() {
		for (Posicao jogada : jogadas) {
			int linha = jogada.getLinha();
			int coluna = jogada.getColuna();
			int posicaoValor = jogada.getValor();

			int linhaMover = 0;
			int colunaMover = 1;
			boolean horizontal = buscarPosicoes(linha, coluna, linhaMover, colunaMover, TOTAL_PARA_GANHAR,
					posicaoValor);
			if (horizontal) {
				return true;
			}

			linhaMover = 1;
			colunaMover = 0;
			boolean vertical = buscarPosicoes(linha, coluna, linhaMover, colunaMover, TOTAL_PARA_GANHAR, posicaoValor);
			if (vertical) {
				return true;
			}

			// Busca diagonais de baixo pra cima e pra esquerda.
			linhaMover = 1;
			colunaMover = 1;
			boolean diagonalEsquerda = buscarPosicoes(linha, coluna, linhaMover, colunaMover, TOTAL_PARA_GANHAR,
					posicaoValor);
			if (diagonalEsquerda) {
				return true;
			}

			System.out.println(buscarPosicoes(linha, coluna, linhaMover, colunaMover, posicaoValor));

			// Busca diagonais de cima pra baixo e pra direita.
			linhaMover = 1;
			colunaMover = -1;
			boolean diagonalDireita = buscarPosicoes(linha, coluna, linhaMover, colunaMover, TOTAL_PARA_GANHAR,
					posicaoValor);
			if (diagonalDireita) {
				return true;
			}

		}

		return false;
	}

	// Helper pra buscar posicoes.
	private boolean buscarPosicoes(int linhaInicial, int colunaInicial, int linhaMover, int colunaMover,
			int totalBuscar, int valorBuscar) {
		for (int cont = 0; cont < totalBuscar; cont++) {
			int linha = linhaInicial + cont * linhaMover;
			int coluna = colunaInicial + cont * colunaMover;

			if (linha < 0 || coluna < 0 || linha >= tabuleiro.length || coluna >= tabuleiro[linha].length
					|| tabuleiro[linha][coluna] != valorBuscar) {
				return false;
			}
		}
		return true;
	}

	// Helper pra buscar posicoes.
	private int buscarPosicoes(int linhaInicial, int colunaInicial, int linhaMover, int colunaMover, int valorBuscar) {
		int totalAchado = 0;
		for (int cont = 0; cont < TOTAL_PARA_GANHAR; cont++) {
			int linha = linhaInicial + cont * linhaMover;
			int coluna = colunaInicial + cont * colunaMover;
			if (linha < 0 || coluna < 0 || linha >= tabuleiro.length || coluna >= tabuleiro[linha].length
					|| tabuleiro[linha][coluna] != valorBuscar) {
				continue;
			}

			totalAchado++;
		}
		return totalAchado;
	}

	private void passarVez() {
		if (jogadorDaVez.equals(jogador1)) {
			jogadorDaVez = jogador2;
			return;
		}
		jogadorDaVez = jogador1;
	}

	public int[][] getTabuleiro() {
		return tabuleiro;
	}

	public void setTabuleiro(int[][] tabuleiro) {
		this.tabuleiro = tabuleiro;
	}

}
