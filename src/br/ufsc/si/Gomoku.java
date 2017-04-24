package br.ufsc.si;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class Gomoku {

	private static final int PROFUNDIDADE = 5;
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
	private Posicao ultimaPosicaoJogada;

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

	private void jogarComputador() {
		int totalJogadas = jogadas.size();
		if (totalJogadas > 0) {
			if (jogadas.get(0).getJogador() == JogadorTipo.COMPUTADOR.getValor()) {
				this.ultimaPosicaoJogada = jogadas.get(totalJogadas - 2);
			} else {
				this.ultimaPosicaoJogada = jogadas.get(totalJogadas - 1);
			}
			Posicao melhorPosicao = minimax(this.ultimaPosicaoJogada, PROFUNDIDADE, true, MENOS_INFINITO,
					MAIS_INFINITO);
			this.frame.encontrarBotao(melhorPosicao.getLinha(), melhorPosicao.getColuna()).doClick();
		} else {
			PosicaoBtn btn = this.frame.encontrarBotao(7, 7);
			btn.doClick();
		}

	}

	private Posicao minimax(Posicao posicao, int profundidade, boolean maximizar, long alpha, long beta) {

		int moverLinha = -1;
		int moverColuna = -1;
		int total = 1;

		List<Posicao> posicoesLivres = pegarFilhosProximos(posicao);

		if (profundidade == 0 || total == 5) {
			return avaliar(posicao);
		} else {
			for (Posicao filho : posicoesLivres) {
				//Posicao filho = pegarProximoFilho(posicao, moverLinha, moverColuna, total);
				if (maximizar) {
					moverPosicaoTabuleiroPrincipal(filho, JogadorTipo.COMPUTADOR);
					Posicao melhor = minimax(filho, profundidade - 1, false, alpha, beta);

					if (melhor.getHeuristica() > alpha) {
						alpha = melhor.getHeuristica();
						posicao = melhor;
					}
				} else {
					moverPosicaoTabuleiroPrincipal(filho, JogadorTipo.HUMANO);
					Posicao melhor = minimax(filho, profundidade - 1, true, alpha, beta);
					if (melhor.getHeuristica() < beta) {
						beta = melhor.getHeuristica();
						posicao = melhor;
					}
				}

				// desfazer movimento
				moverPosicaoTabuleiroPrincipal(filho, JogadorTipo.LIVRE);

				if (alpha >= beta) {
					break;
				}
			}
			;//while (alpha < beta);
		}

		return posicao;

	}

	private List<Posicao> pegarFilhosProximos(Posicao posicao) {
		List<Posicao> posicoes = new ArrayList<>();

		for (int i = -3; i < 3; i++) {
			int linha = this.ultimaPosicaoJogada.getLinha() + i;
			if (linha < 0) {
				linha = 0;
			}
			if (linha >= TOTAL_LINHAS) {
				linha = TOTAL_LINHAS - 1;
			}
			for (int j = -3; j < 3; j++) {
				int coluna = this.ultimaPosicaoJogada.getColuna() + j;
				if (coluna < 0) {
					coluna = 0;
				}
				if (coluna >= TOTAL_COLUNAS) {
					coluna = TOTAL_COLUNAS - 1;
				}

				Posicao posicaoEncontrada = new Posicao(linha, coluna);
				if (jogadas.contains(posicaoEncontrada) || posicoes.contains(posicaoEncontrada)) {
					continue;
				}

				posicoes.add(posicaoEncontrada);
			}
		}

		return posicoes;
	}

	/*private List<Posicao> pegarFilhosProximos(Posicao posicao, int moverLinha, int moverColuna,
			int casasParaVerificar) {
		List<Posicao> posicoes = new ArrayList<>();
		
		for (int i=0;i<30;i++) {
			int linha = this.ultimaPosicaoJogada.getLinha() + moverLinha * casasParaVerificar;
			int coluna = this.ultimaPosicaoJogada.getColuna() + moverColuna * casasParaVerificar;
			if (linha < 0) {
				linha = 0;
			}
			if (coluna < 0) {
				coluna = 0;
			}
			if (linha >= TOTAL_LINHAS) {
				linha = TOTAL_LINHAS - 1;
			}
			if (coluna >= TOTAL_COLUNAS) {
				coluna = TOTAL_COLUNAS - 1;
			}
	
			int jogador = tabuleiro[linha][coluna];
			if (moverLinha < casasParaVerificar) {
				moverLinha++;
			} else if (moverColuna < casasParaVerificar) {
				moverColuna++;
			} else if (moverColuna == moverLinha) {
				casasParaVerificar++;
				moverColuna = casasParaVerificar * -1;
				moverLinha = casasParaVerificar * -1;
				pegarProximoFilho(posicao, moverLinha, moverColuna, casasParaVerificar);
			}
			if (jogador == JogadorTipo.LIVRE.getValor()) {
				return new Posicao(linha, coluna);
			}
		}
	} */

	private List<Posicao> obterPosicoesLivres() {
		List<Posicao> posicoes = new ArrayList<>();
		for (int i = 0; i < 15; i++) {
			for (int j = 0; j < 15; j++) {
				if (tabuleiro[i][j] == JogadorTipo.LIVRE.getValor()) {
					posicoes.add(new Posicao(i, j));
				}
			}
		}
		return posicoes;
	}

	private void moverPosicaoTabuleiroPrincipal(Posicao posicao, JogadorTipo jogador) {
		tabuleiro[posicao.getLinha()][posicao.getColuna()] = jogador.getValor();
	}

	private Posicao pegarProximoFilho(Posicao posicao, int moverLinha, int moverColuna, int casasParaVerificar) {
		int linha = this.ultimaPosicaoJogada.getLinha() + moverLinha * casasParaVerificar;
		int coluna = this.ultimaPosicaoJogada.getColuna() + moverColuna * casasParaVerificar;
		if (linha < 0) {
			linha = 0;
		}
		if (coluna < 0) {
			coluna = 0;
		}
		if (linha >= TOTAL_LINHAS) {
			linha = TOTAL_LINHAS - 1;
		}
		if (coluna >= TOTAL_COLUNAS) {
			coluna = TOTAL_COLUNAS - 1;
		}

		int jogador = tabuleiro[linha][coluna];
		if (moverLinha < casasParaVerificar) {
			moverLinha++;
		} else if (moverColuna < casasParaVerificar) {
			moverColuna++;
		} else if (moverColuna == moverLinha) {
			casasParaVerificar++;
			moverColuna = casasParaVerificar * -1;
			moverLinha = casasParaVerificar * -1;
			return pegarProximoFilho(posicao, moverLinha, moverColuna, casasParaVerificar);
		}
		if (jogador == JogadorTipo.LIVRE.getValor()) {
			return new Posicao(linha, coluna);
		}

		return pegarProximoFilho(posicao, moverLinha, moverColuna, casasParaVerificar);

	}

	// Valores para o IA
	// peça -> 1
	// dupla-fechada -> 63
	// dupla -> 126
	// tipla-fechada -> 3.086
	// tripla -> 6.175
	// quadrupla-fechada -> 77.188
	// quadrupla-> 154.376
	private Posicao avaliar(Posicao posicao) {
		int linha = posicao.getLinha();
		int coluna = posicao.getColuna();
		int posicaoValor = jogadorDaVez.getTipo().getValor();
		int heuristica = 0;

		List<Integer> achados = new ArrayList<>();

		int linhaMover = 0;
		int colunaMover = 1;
		achados.add(buscarPosicoesParaAvaliacao(tabuleiro, linha, coluna, linhaMover, colunaMover, posicaoValor));

		linhaMover = 1;
		colunaMover = 0;
		achados.add(buscarPosicoesParaAvaliacao(tabuleiro, linha, coluna, linhaMover, colunaMover, posicaoValor));

		// Busca diagonais de baixo pra cima e pra esquerda.
		linhaMover = 1;
		colunaMover = 1;
		achados.add(buscarPosicoesParaAvaliacao(tabuleiro, linha, coluna, linhaMover, colunaMover, posicaoValor));

		// Busca diagonais de cima pra baixo e pra direita.
		linhaMover = 1;
		colunaMover = -1;
		achados.add(buscarPosicoesParaAvaliacao(tabuleiro, linha, coluna, linhaMover, colunaMover, posicaoValor));

		long total = 0;

		for (Integer valor : achados) {
			switch (valor) {
			case 1:
				total++;
				break;
			case 2:
				total = total + 126;
				break;
			case 3:
				total = total + 6175;
				break;
			case 4:
				total = total + 154376;
				break;
			case 5:
				total = Long.MAX_VALUE;
			}
		}

		posicao.setHeuristica(total);

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
		boolean vertical = buscarPosicoes(tabuleiro, linha, coluna, linhaMover, colunaMover, TOTAL_PARA_GANHAR,
				posicaoValor);
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
			frame.dispose();
			System.exit(0);
		} else {
			passarVez();
			if (jogadorDaVez.getTipo().equals(JogadorTipo.COMPUTADOR)) {
				this.jogarComputador();
			}
		}
	}

	private boolean temGanhador() {
		for (Posicao jogada : jogadas) {
			int linha = jogada.getLinha();
			int coluna = jogada.getColuna();
			int jogador = jogada.getJogador();

			int linhaMover = 0;
			int colunaMover = 1;
			boolean horizontal = buscarPosicoes(this.tabuleiro, linha, coluna, linhaMover, colunaMover,
					TOTAL_PARA_GANHAR, jogador);
			if (horizontal) {
				return true;
			}

			linhaMover = 1;
			colunaMover = 0;
			boolean vertical = buscarPosicoes(this.tabuleiro, linha, coluna, linhaMover, colunaMover, TOTAL_PARA_GANHAR,
					jogador);
			if (vertical) {
				return true;
			}

			// Busca diagonais de baixo pra cima e pra esquerda.
			linhaMover = 1;
			colunaMover = 1;
			boolean diagonalEsquerda = buscarPosicoes(this.tabuleiro, linha, coluna, linhaMover, colunaMover,
					TOTAL_PARA_GANHAR, jogador);
			if (diagonalEsquerda) {
				return true;
			}

			System.out.println(
					buscarPosicoesParaAvaliacao(this.tabuleiro, linha, coluna, linhaMover, colunaMover, jogador));

			// Busca diagonais de cima pra baixo e pra direita.
			linhaMover = 1;
			colunaMover = -1;
			boolean diagonalDireita = buscarPosicoes(this.tabuleiro, linha, coluna, linhaMover, colunaMover,
					TOTAL_PARA_GANHAR, jogador);
			if (diagonalDireita) {
				return true;
			}
		}

		return false;
	}

	// Helper pra buscar posicoes.
	private boolean buscarPosicoes(int[][] tabuleiro, int linhaInicial, int colunaInicial, int linhaMover,
			int colunaMover, int totalBuscar, int valorBuscar) {
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
	private int buscarPosicoesParaAvaliacao(int tabuleiro[][], int linhaInicial, int colunaInicial, int linhaMover,
			int colunaMover, int valorBuscar) {
		int totalAchado = 0;
		for (int cont = 0; cont < TOTAL_PARA_GANHAR; cont++) {
			int linha = linhaInicial + cont * linhaMover;
			int coluna = colunaInicial + cont * colunaMover;
			if (linha < 0 || coluna < 0 || linha >= tabuleiro.length || coluna >= tabuleiro[linha].length
					|| tabuleiro[linha][coluna] != valorBuscar) {
				totalAchado--;
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
