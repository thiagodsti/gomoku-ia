package br.ufsc.si;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class Gomoku {

	private static final int PROFUNDIDADE = 3;
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
			this.ultimaPosicaoJogada = jogadas.get(totalJogadas - 2);
			Posicao melhorPosicao = minimax(this.ultimaPosicaoJogada, PROFUNDIDADE, true, MENOS_INFINITO,
					MAIS_INFINITO);
			this.ultimaPosicaoJogada = jogadas.get(totalJogadas - 1);
			Posicao melhorPosicao2 = minimax(this.ultimaPosicaoJogada, PROFUNDIDADE, true, MENOS_INFINITO,
					MAIS_INFINITO);
			if (melhorPosicao2.heuristica > melhorPosicao.heuristica) {
				melhorPosicao = melhorPosicao2;
			}
			this.frame.encontrarBotao(melhorPosicao.getLinha(), melhorPosicao.getColuna()).doClick();
		} else {
			PosicaoBtn btn = this.frame.encontrarBotao(7, 7);
			btn.doClick();
		}

	}

	private Posicao minimax(Posicao posicao, int profundidade, boolean maximizar, double alpha, double beta) {

		List<Posicao> posicoesLivres = pegarFilhosProximos(posicao);

		Posicao melhor;
		if (profundidade == 0) {
			return avaliar(posicao);
		} else {
			for (Posicao filho : posicoesLivres) {
				//Posicao filho = pegarProximoFilho(posicao, moverLinha, moverColuna, total);
				if (maximizar) {
					filho.setJogador(JogadorTipo.COMPUTADOR.getValor());
					moverPosicaoTabuleiroPrincipal(filho, JogadorTipo.COMPUTADOR);
					melhor = minimax(filho, profundidade - 1, false, alpha, beta);
					if (melhor.heuristica > alpha) {
						alpha = melhor.heuristica;
						posicao = melhor;
					}
				} else {
					filho.setJogador(JogadorTipo.HUMANO.getValor());
					moverPosicaoTabuleiroPrincipal(filho, JogadorTipo.HUMANO);
					melhor = minimax(filho, profundidade - 1, true, alpha, beta);
					if (melhor.heuristica < beta) {
						beta = melhor.heuristica;
						posicao = melhor;
					}
				}

				// desfazer movimento
				moverPosicaoTabuleiroPrincipal(filho, JogadorTipo.LIVRE);

				if (alpha >= beta) {
					break;
				}
			}
			return posicao;
		}
	}

	private List<Posicao> pegarFilhosProximos(Posicao posicao) {
		List<Posicao> posicoes = new ArrayList<>();

		int moverLinha = -1;
		int moverColuna = -1;

		for (int movimentos = 1; movimentos < 6; movimentos++) {
			for (int i = moverLinha; i < movimentos; i++) {
				int linha = this.ultimaPosicaoJogada.getLinha() + i;
				if (linha < 0) {
					linha = 0;
				}
				if (linha >= TOTAL_LINHAS) {
					linha = TOTAL_LINHAS - 1;
				}
				for (int j = moverColuna; j < movimentos; j++) {
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
		}

		return posicoes;
	}

	private void moverPosicaoTabuleiroPrincipal(Posicao posicao, JogadorTipo jogador) {
		tabuleiro[posicao.getLinha()][posicao.getColuna()] = jogador.getValor();
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
		int jogador = posicao.getJogador();

		Pontuacao pontosHorizontal = avaliarHorizontal(posicao, jogador);
		Pontuacao pontosVertical = avaliarVertical(posicao, jogador);
		Pontuacao pontosDiagonal = avaliarDiagonalMesmoSentido(posicao, jogador);
		Pontuacao pontosDiagonalInversa = avaliarDiagonalInversa(posicao, jogador);
		posicao.heuristica = pontosHorizontal.total + pontosVertical.total + pontosDiagonal.total
				+ pontosDiagonalInversa.total;

		if (posicao.heuristica > 1) {
			System.out.println(posicao.heuristica);
		}
		return posicao;
	}

	private double avaliarPontuacao(Pontuacao pontuacao) {
		double total = 0;
		double totalAdv = 0;

		int dupla = 1;
		int tripla = 1;
		int duplaAdv = 1;
		int triplaAdv = 1;
		switch (pontuacao.totalJogador) {
			case 1:
				total = 1;
				break;
			case 2:
				dupla++;
				total = 200 / pontuacao.distancia;
				break;
			case 3:
				tripla++;
				total = 7000 / pontuacao.distancia;
				break;
			case 4:
				total = 200000 / pontuacao.distancia;
				break;
			default:
				total = 200000000;
		}

		switch (pontuacao.totalAdversario) {
			case 1:
				totalAdv = 1;
				break;
			case 2:
				duplaAdv++;
				totalAdv = 200;
				break;
			case 3:
				triplaAdv++;
				totalAdv = 7000;
				break;
			case 4:
				totalAdv = 200000;
				break;
			default:
				totalAdv = 200000000;
		}

		if (dupla > 1) {
			total += Math.pow(200, dupla);
		}
		if (tripla > 1) {
			total += Math.pow(7000, tripla);
		}
		if (duplaAdv > 1) {
			totalAdv += Math.pow(200, duplaAdv);
		}
		if (triplaAdv > 1) {
			totalAdv += Math.pow(7000, triplaAdv);
		}
		return total - totalAdv;
	}

	private Pontuacao avaliarDiagonalMesmoSentido(Posicao posicao, int jogador) {
		int linha = posicao.linha;
		int coluna = posicao.coluna;
		Pontuacao pontuacao = new Pontuacao();
		for (int i = linha, mover = 1; i < linha + 5; i++, mover++) {
			int proximaLinha = linha + mover;
			int proximaColuna = coluna + mover;
			if (proximaLinha >= TOTAL_LINHAS || proximaColuna >= TOTAL_COLUNAS) {
				break;
			} else {
				int proximaPosicao = tabuleiro[proximaLinha][proximaColuna];
				avaliadorComum(jogador, pontuacao, proximaPosicao);
			}
		}

		for (int i = linha, mover = 1; i > linha - 5; i--, mover++) {
			int proximaLinha = linha - mover;
			int proximaColuna = coluna - mover;
			if (proximaLinha < 0 || proximaColuna < 0) {
				break;
			} else {
				int proximaPosicao = tabuleiro[proximaLinha][proximaColuna];
				avaliadorComum(jogador, pontuacao, proximaPosicao);
			}
		}

		pontuacao.total = avaliarPontuacao(pontuacao);
		return pontuacao;
	}

	private Pontuacao avaliarDiagonalInversa(Posicao posicao, int jogador) {
		int linha = posicao.linha;
		int coluna = posicao.coluna;
		Pontuacao pontuacao = new Pontuacao();
		for (int i = linha, mover = 1; i > linha - 5; i--, mover++) {
			int proximaLinha = linha - mover;
			int proximaColuna = coluna + mover;
			if (proximaLinha < 0 || proximaColuna >= TOTAL_COLUNAS) {
				break;
			} else {
				int proximaPosicao = tabuleiro[proximaLinha][proximaColuna];
				avaliadorComum(jogador, pontuacao, proximaPosicao);
			}
		}

		for (int i = linha, mover = 1; i < linha + 5; i++, mover++) {
			int proximaLinha = linha + mover;
			int proximaColuna = coluna - mover;
			if (proximaLinha >= TOTAL_LINHAS || proximaColuna < 0) {
				break;
			} else {
				int proximaPosicao = tabuleiro[proximaLinha][proximaColuna];
				avaliadorComum(jogador, pontuacao, proximaPosicao);
			}
		}

		pontuacao.total = avaliarPontuacao(pontuacao);
		return pontuacao;
	}

	private Pontuacao avaliarVertical(Posicao posicao, int jogador) {
		int linha = posicao.linha;
		int coluna = posicao.coluna;
		Pontuacao pontuacao = new Pontuacao();
		for (int i = linha, mover = 1; i < linha + 5; i++, mover++) {
			int proximaLinha = linha + mover;
			if (proximaLinha >= TOTAL_LINHAS) {
				break;
			}
			int proximaPosicao = tabuleiro[proximaLinha][coluna];
			avaliadorComum(jogador, pontuacao, proximaPosicao);
		}

		for (int i = linha, mover = 1; i > linha - 5; i--, mover++) {
			int proximaLinha = linha - mover;
			if (proximaLinha < 0) {
				break;
			}
			int proximaPosicao = tabuleiro[proximaLinha][coluna];
			avaliadorComum(jogador, pontuacao, proximaPosicao);
		}

		pontuacao.total = avaliarPontuacao(pontuacao);
		return pontuacao;
	}

	private Pontuacao avaliarHorizontal(Posicao posicao, int jogador) {
		int coluna = posicao.getColuna();
		int linha = posicao.getLinha();
		Pontuacao pontuacao = new Pontuacao();
		for (int i = coluna, mover = 1; i < coluna + 5; i++, mover++) {
			int proximaColuna = coluna + mover;
			if (proximaColuna >= TOTAL_COLUNAS) {
				break;
			}
			int proximaPosicao = tabuleiro[linha][proximaColuna];
			avaliadorComum(jogador, pontuacao, proximaPosicao);
		}

		for (int i = coluna, mover = 1; i > coluna - 5; i--, mover++) {
			int proximaColuna = coluna - mover;
			if (proximaColuna < 0) {
				break;
			}
			int proximaPosicao = tabuleiro[linha][proximaColuna];
			avaliadorComum(jogador, pontuacao, proximaPosicao);
		}

		pontuacao.total = avaliarPontuacao(pontuacao);
		return pontuacao;
	}

	private void avaliadorComum(int jogador, Pontuacao pontuacao, int proximaPosicao) {
		if (proximaPosicao == jogador) {
			pontuacao.totalJogador++;
		} else if (proximaPosicao == JogadorTipo.LIVRE.getValor() && pontuacao.totalJogador > 1) {
			pontuacao.livre++;
			pontuacao.distancia++;
		} else if (proximaPosicao == JogadorTipo.LIVRE.getValor() && pontuacao.totalJogador <= 1) {
			pontuacao.distancia = 1;
		} else {
			pontuacao.totalAdversario++;
		}
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
