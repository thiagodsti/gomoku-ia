package br.ufsc.si;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class Gomoku {

	private static final int TOTAL_COLUNAS = 15;
	private static final int TOTAL_LINHAS = 15;
	private static final int TOTAL_PARA_GANHAR = 5;
	private static final int MENOS_INFINITO = Integer.MIN_VALUE;
	private static final int MAIS_INFINITO = Integer.MAX_VALUE;
	private Jogador jogadorDaVez;
	private Jogador jogador1;
	private Jogador jogador2;
	private int[][] tabuleiro = new int[TOTAL_LINHAS][TOTAL_COLUNAS];
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
			jogarComputador();
		}

	}

	// Valores para o IA
	// peça -> 1 x 125
	// dupla -> 126 * 49
	// tripla -> 6175 * 25
	// quadrupla-> 154.376

	private void jogarComputador() {
		int totalJogadas = jogadas.size();
		Posicao ultimaPosicaoJogada = null;
		if (totalJogadas > 0) {
			ultimaPosicaoJogada = jogadas.get(totalJogadas-1);
			int linhaInicial = ultimaPosicaoJogada.getLinha();
			int colunaInicial = ultimaPosicaoJogada.getColuna();
			int contLinha = 0;
			int contColuna = 0;
			//Percorro 2 linhas e colunas antes e 2 linhas e colunas depois da ulima jogada.
			for (int linha=linhaInicial-2;contLinha<4;linha++, contLinha++){
				if (linha<0) {
					continue;
				} else if (linha>=TOTAL_LINHAS) {
					continue;
				}
				for (int coluna=colunaInicial-2;contColuna<4;coluna++, contColuna++) {
					if (coluna<0) {
						continue;
					} else if (coluna>=TOTAL_COLUNAS) {
						continue;
					}
					
					//int[][] tabuleiroTmp = mover(tabuleiro, jogadorDaVez.getTipo().getValor(), linha, coluna);
					//Acho que aqui chama o minimax pra cada posição.
					Posicao melhorPosicao = new Posicao(linha, coluna);
					melhorPosicao = minimax(melhorPosicao, 3, true, MENOS_INFINITO, MAIS_INFINITO);
					//minimax();
				}
			}
		} else {
			PosicaoBtn btn = this.frame.encontrarBotao(7, 7);
			btn.doClick();
		}
		
		
	}

	private int[][] mover(int[][] tabuleiro, int jogador, int linha, int coluna) {
		int[][] tabuleiroTmp = new int[15][15];
		for (int i=0;i<tabuleiro.length;i++) {
			for (int j=0;j<tabuleiro[i].length;j++) {
				tabuleiroTmp[i][j] = tabuleiro[i][j];
			}
		}
		if (tabuleiroTmp[linha][coluna] == 0) {
			tabuleiroTmp[linha][coluna] = jogador;
		}
		return tabuleiroTmp;
	}

	private Posicao minimax(Posicao posicao, int profundidade, boolean maximizar, double alpha, double beta) {
		int heuristica = 0;
		if (profundidade == 0 ) {
			return posicao;
		}
		
		if (maximizar) {
			int melhorValor = MENOS_INFINITO;
			for (int i=0;i<5;i++) {
				//tabuleiroTmp = mover(tabuleiroTmp, JogadorTipo.COMPUTADOR.getValor(), melhorPosicao.getLinha(), melhorPosicao.getColuna()+1);
			//	para cada filho (até 5 de cada lado que puder andar com espaço livre)
			//	tabuleiroFilho = movimentacao;
				//double melhorValor = max(melhorValor, melhorPosicao.getScore());
				//if (melhorValor > alpha) {
				//	melhorPosicao = melhor;
				//}
				//Posicao melhor = minimax(tabuleiroTmp, melhorPosicao, profundidade - 1, false, alpha, beta);
			}
			//
			// if (melhorValor > alpha )
			// alpha = melhorValor;
			// melhorPosicao = melhor;
			//
		}
		
		return posicao;
		
	}
	
	private boolean jogadorGanhou(int[][] tabuleiro, int valor, int linha, int coluna) {
		int posicaoValor = valor;

		int linhaMover = 0;
		int colunaMover = 1;
		boolean horizontal = buscarPosicoes(tabuleiro, linha, coluna, linhaMover, colunaMover, TOTAL_PARA_GANHAR,
				posicaoValor);
		if (horizontal) {
			return true;
		}

		linhaMover = 1;
		colunaMover = 0;
		boolean vertical = buscarPosicoes(tabuleiro, linha, coluna, linhaMover, colunaMover, TOTAL_PARA_GANHAR, posicaoValor);
		if (vertical) {
			return true;
		}

		// Busca diagonais de baixo pra cima e pra esquerda.
		linhaMover = 1;
		colunaMover = 1;
		boolean diagonalEsquerda = buscarPosicoes(tabuleiro, linha, coluna, linhaMover, colunaMover, TOTAL_PARA_GANHAR,
				posicaoValor);
		if (diagonalEsquerda) {
			return true;
		}

		// Busca diagonais de cima pra baixo e pra direita.
		linhaMover = 1;
		colunaMover = -1;
		boolean diagonalDireita = buscarPosicoes(tabuleiro, linha, coluna, linhaMover, colunaMover, TOTAL_PARA_GANHAR,
				posicaoValor);
		if (diagonalDireita) {
			return true;
		}
		return false;
	}

	public void jogar(PosicaoBtn btn) {
		btn.setText(jogadorDaVez.getPeca());
		btn.setEnabled(false);
		Posicao posicao = new Posicao(btn.getLinha(), btn.getColuna(), jogadorDaVez.getTipo().getValor());
		jogadas.add(posicao);
		tabuleiro[btn.getLinha()][btn.getColuna()] = posicao.getJogador();
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
			int jogador = jogada.getJogador();

			int linhaMover = 0;
			int colunaMover = 1;
			boolean horizontal = buscarPosicoes(this.tabuleiro, linha, coluna, linhaMover, colunaMover, TOTAL_PARA_GANHAR,
					jogador);
			if (horizontal) {
				return true;
			}

			linhaMover = 1;
			colunaMover = 0;
			boolean vertical = buscarPosicoes(this.tabuleiro, linha, coluna, linhaMover, colunaMover, TOTAL_PARA_GANHAR, jogador);
			if (vertical) {
				return true;
			}

			// Busca diagonais de baixo pra cima e pra esquerda.
			linhaMover = 1;
			colunaMover = 1;
			boolean diagonalEsquerda = buscarPosicoes(this.tabuleiro, linha, coluna, linhaMover, colunaMover, TOTAL_PARA_GANHAR,
					jogador);
			if (diagonalEsquerda) {
				return true;
			}

			System.out.println(buscarPosicoes(this.tabuleiro, linha, coluna, linhaMover, colunaMover, jogador));

			// Busca diagonais de cima pra baixo e pra direita.
			linhaMover = 1;
			colunaMover = -1;
			boolean diagonalDireita = buscarPosicoes(this.tabuleiro, linha, coluna, linhaMover, colunaMover, TOTAL_PARA_GANHAR,
					jogador);
			if (diagonalDireita) {
				return true;
			}
		}

		return false;
	}

	// Helper pra buscar posicoes.
	private boolean buscarPosicoes(int[][] tabuleiro, int linhaInicial, int colunaInicial, int linhaMover, int colunaMover,
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
	private int buscarPosicoes(int tabuleiro[][], int linhaInicial, int colunaInicial, int linhaMover, int colunaMover, int valorBuscar) {
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
