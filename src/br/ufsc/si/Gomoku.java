package br.ufsc.si;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class Gomoku {

	private static final int PROFUNDIDADE = 3;
	private static final int TOTAL_COLUNAS = 15;
	private static final int TOTAL_LINHAS = 15;
	private static final int TOTAL_PARA_GANHAR = 5;
	private static final double MENOS_INFINITO = -2000000000;
	private static final double MAIS_INFINITO = 2000000000;
	private Jogador jogadorDaVez;
	private Jogador jogador1;
	private Jogador jogador2;
	private int[][] tabuleiro = new int[TOTAL_LINHAS][TOTAL_COLUNAS];
	private List<Posicao> jogadas;
	private List<Posicao> jogadasAtual;
	private TabuleiroFrame frame;
	private Posicao ultimaPosicaoJogada;
	private List<Posicao> posicoesLivres = new ArrayList<>();

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
			this.frame.encontrarBotao(melhorPosicao.getLinha(), melhorPosicao.getColuna()).doClick();
		} else {
			PosicaoBtn btn = this.frame.encontrarBotao(7, 7);
			btn.doClick();
		}

	}

	private Posicao minimax(Posicao posicao, int profundidade, boolean maximizar, double alpha, double beta) {

		List<Posicao> posicoesLivres = pegarFilhosProximos(posicao);

		Posicao melhor;
		int melhorLinha = -1;
		int melhorColuna = -1;
		double melhorValor = 0;
		if (temGanhador()) {
			if (maximizar) {
				return new Posicao(posicao.getLinha(), posicao.getColuna(), MENOS_INFINITO);
			} else {
				return new Posicao(posicao.getLinha(), posicao.getColuna(), MAIS_INFINITO);
			}
		} else if (profundidade == 0) {
			double valor = avaliar(null);
			return new Posicao(posicao.getLinha(), posicao.getColuna(), valor);
		} else {
			for (Posicao filho : posicoesLivres) {
				if (maximizar) {
					filho.setJogador(JogadorTipo.COMPUTADOR.getValor());
					moverPosicaoTabuleiroPrincipal(filho, JogadorTipo.COMPUTADOR);
					melhor = minimax(filho, profundidade - 1, false, alpha, beta);
					if (melhorLinha == -1 || melhorColuna == -1 || melhor.heuristica > alpha) {
						alpha = melhor.heuristica;
						melhorValor = melhor.heuristica;
						melhorLinha = melhor.getLinha();
						melhorColuna = melhor.getColuna();
					}
				} else {
					filho.setJogador(JogadorTipo.HUMANO.getValor());
					moverPosicaoTabuleiroPrincipal(filho, JogadorTipo.HUMANO);
					melhor = minimax(filho, profundidade - 1, true, alpha, beta);
					if (melhor.heuristica < beta) {
						beta = melhor.heuristica;
						melhorValor = melhor.heuristica;
						melhorLinha = melhor.getLinha();
						melhorColuna = melhor.getColuna();
					}
				}

				// desfazer movimento
				desfazerMovimento(filho);

				if (alpha >= beta) {
					break;
				}
			}
		}
		return new Posicao(melhorLinha, melhorColuna, melhorValor);
	}

	private void desfazerMovimento(Posicao posicao) {
		tabuleiro[posicao.getLinha()][posicao.getColuna()] = JogadorTipo.LIVRE.getValor();
		jogadas.remove(posicao);
	}

	private List<Posicao> pegarFilhosProximos(Posicao posicao) {
		List<Posicao> posicoes = new ArrayList<>();

		int totalMovimentosBuscar = 3;

		for (Posicao p : jogadas) {
			int coluna = p.getColuna();
			int linha = p.getLinha();

			//Buscar filhos horizontal
			for (int i = coluna, mover = 1; i < coluna + totalMovimentosBuscar; i++, mover++) {
				int proximaColuna = coluna + mover;
				if (proximaColuna >= TOTAL_COLUNAS) {
					break;
				}

				if (tabuleiro[linha][proximaColuna] == JogadorTipo.LIVRE.getValor()) {
					Posicao nova = new Posicao(linha, proximaColuna);
					if (!posicoes.contains(nova)) {
						posicoes.add(nova);
					}
				}
			}

			for (int i = coluna, mover = 1; i > coluna - totalMovimentosBuscar; i--, mover++) {
				int proximaColuna = coluna - mover;
				if (proximaColuna < 0) {
					break;
				}
				if (tabuleiro[linha][proximaColuna] == JogadorTipo.LIVRE.getValor()) {
					Posicao nova = new Posicao(linha, proximaColuna);
					if (!posicoes.contains(nova)) {
						posicoes.add(nova);
					}
				}
			}

			//Buscar filhos vertical
			for (int i = linha, mover = 1; i < linha + totalMovimentosBuscar; i++, mover++) {
				int proximaLinha = linha + mover;
				if (proximaLinha >= TOTAL_LINHAS) {
					break;
				}
				if (tabuleiro[proximaLinha][coluna] == JogadorTipo.LIVRE.getValor()) {
					Posicao nova = new Posicao(proximaLinha, coluna);
					if (!posicoes.contains(nova)) {
						posicoes.add(nova);
					}
				}
			}

			for (int i = linha, mover = 1; i > linha - totalMovimentosBuscar; i--, mover++) {
				int proximaLinha = linha - mover;
				if (proximaLinha < 0) {
					break;
				}
				if (tabuleiro[proximaLinha][coluna] == JogadorTipo.LIVRE.getValor()) {
					Posicao nova = new Posicao(proximaLinha, coluna);
					if (!posicoes.contains(nova)) {
						posicoes.add(nova);
					}
				}
			}

			//Buscar filhos diagonal mesmo sentido
			for (int i = linha, mover = 1; i < linha + totalMovimentosBuscar; i++, mover++) {
				int proximaLinha = linha + mover;
				int proximaColuna = coluna + mover;
				if (proximaLinha >= TOTAL_LINHAS || proximaColuna >= TOTAL_COLUNAS) {
					break;
				}
				if (tabuleiro[proximaLinha][proximaColuna] == JogadorTipo.LIVRE.getValor()) {
					Posicao nova = new Posicao(proximaLinha, proximaColuna);
					if (!posicoes.contains(nova)) {
						posicoes.add(nova);
					}
				}
			}

			for (int i = linha, mover = 1; i > linha - totalMovimentosBuscar; i--, mover++) {
				int proximaLinha = linha - mover;
				int proximaColuna = coluna - mover;
				if (proximaLinha < 0 || proximaColuna < 0) {
					break;
				}
				if (tabuleiro[proximaLinha][proximaColuna] == JogadorTipo.LIVRE.getValor()) {
					Posicao nova = new Posicao(proximaLinha, proximaColuna);
					if (!posicoes.contains(nova)) {
						posicoes.add(nova);
					}
				}
			}

			//Buscar filhos diagonal sentido inverso
			for (int i = linha, mover = 1; i > linha - totalMovimentosBuscar; i--, mover++) {
				int proximaLinha = linha - mover;
				int proximaColuna = coluna + mover;
				if (proximaLinha < 0 || proximaColuna >= TOTAL_COLUNAS) {
					break;
				}
				if (tabuleiro[proximaLinha][proximaColuna] == JogadorTipo.LIVRE.getValor()) {
					Posicao nova = new Posicao(proximaLinha, proximaColuna);
					if (!posicoes.contains(nova)) {
						posicoes.add(nova);
					}
				}
			}

			for (int i = linha, mover = 1; i < linha + totalMovimentosBuscar; i++, mover++) {
				int proximaLinha = linha + mover;
				int proximaColuna = coluna - mover;
				if (proximaLinha >= TOTAL_LINHAS || proximaColuna < 0) {
					break;
				}
				if (tabuleiro[proximaLinha][proximaColuna] == JogadorTipo.LIVRE.getValor()) {
					Posicao nova = new Posicao(proximaLinha, proximaColuna);
					if (!posicoes.contains(nova)) {
						posicoes.add(nova);
					}
				}
			}

		}

		return posicoes;
	}

	private void moverPosicaoTabuleiroPrincipal(Posicao posicao, JogadorTipo jogador) {
		tabuleiro[posicao.getLinha()][posicao.getColuna()] = jogador.getValor();
		jogadas.add(posicao);
	}

	// Valores para o IA
	// peça -> 1
	// dupla-fechada -> 63
	// dupla -> 126
	// tipla-fechada -> 3.086
	// tripla -> 6.175
	// quadrupla-fechada -> 77.188
	// quadrupla-> 154.376
	private double avaliar(Posicao posicao) {
		//int jogador = posicao.getJogador();
		//int jogador = jogadorDaVez.getTipo().getValor();
		double heuristica = 0;
		for (Posicao p : jogadas) {
			int jogador = p.getJogador();
			Pontuacao pontosHorizontal = avaliarHorizontal(p, jogador);
			Pontuacao pontosVertical = avaliarVertical(p, jogador);
			Pontuacao pontosDiagonal = avaliarDiagonalMesmoSentido(p, jogador);
			Pontuacao pontosDiagonalInversa = avaliarDiagonalInversa(p, jogador);
			if (jogador == JogadorTipo.COMPUTADOR.getValor()) {
				heuristica += pontosHorizontal.total + pontosVertical.total + pontosDiagonal.total
						+ pontosDiagonalInversa.total;
			} else {
				heuristica -= pontosHorizontal.total - pontosVertical.total - pontosDiagonal.total
						- pontosDiagonalInversa.total;
			}
		}
		return heuristica;
	}

	private double avaliarPontuacao(Pontuacao pontuacao, int jogador) {
		double total = 0;

		int dupla = 0;
		int tripla = 0;

		int distancia = pontuacao.distancia;
		switch (pontuacao.totalJogador) {
			case 1:
				total += 1 * pontuacao.livre / pontuacao.bloqueado;
				break;
			case 2:
				//dupla++;
				if (jogador == jogadorDaVez.getTipo().getValor()) {
					total += 200 / distancia / pontuacao.bloqueado;
				} else {
					total += 100 / distancia / pontuacao.bloqueado;
					//dupla--;
				}
				break;
			case 3:
				//	tripla++;
				if (jogador == jogadorDaVez.getTipo().getValor()) {
					total += 7000000 / distancia / pontuacao.bloqueado;
				} else {
					total += 700000 / distancia / pontuacao.bloqueado;
					//	tripla--;
				}
				break;
			case 4:
				if (jogador == jogadorDaVez.getTipo().getValor()) {
					total += 2000000 / distancia / pontuacao.bloqueado;
				} else {
					total += 200000 / distancia / pontuacao.bloqueado;
				}
				break;
			case 5:
				if (jogador == jogadorDaVez.getTipo().getValor()) {
					total += MAIS_INFINITO / distancia / pontuacao.bloqueado;
				} else {
					total += 20000 / distancia / pontuacao.bloqueado;
				}
		}

		/*		if (dupla > 1) {
					total += Math.pow(200, dupla);
				}
				if (tripla > 1) {
					total += Math.pow(7000, tripla);
				} */
		return total;
	}

	private Pontuacao avaliarDiagonalMesmoSentido(Posicao posicao, int jogador) {
		int linha = posicao.linha;
		int coluna = posicao.coluna;
		Pontuacao pontuacao = new Pontuacao();
		for (int i = linha, mover = 1; i < linha + 5; i++, mover++) {
			int proximaLinha = linha + mover;
			int proximaColuna = coluna + mover;
			if (proximaLinha >= TOTAL_LINHAS || proximaColuna >= TOTAL_COLUNAS) {
				pontuacao.totalAdversario++;
				break;
			} else {
				int proximaPosicao = tabuleiro[proximaLinha][proximaColuna];
				avaliadorComum(jogador, pontuacao, proximaPosicao);
			}
		}
		if (pontuacao.totalAdversario > 0) {
			pontuacao.bloqueado--;
		}

		pontuacao.total = avaliarPontuacao(pontuacao, jogador);
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
				pontuacao.totalAdversario++;
				break;
			} else {
				int proximaPosicao = tabuleiro[proximaLinha][proximaColuna];
				avaliadorComum(jogador, pontuacao, proximaPosicao);
			}
		}
		if (pontuacao.totalAdversario > 0) {
			pontuacao.bloqueado--;
		}
		pontuacao.total = avaliarPontuacao(pontuacao, jogador);
		return pontuacao;
	}

	private Pontuacao avaliarVertical(Posicao posicao, int jogador) {
		int linha = posicao.linha;
		int coluna = posicao.coluna;
		Pontuacao pontuacao = new Pontuacao();
		for (int i = linha, mover = 1; i < linha + 5; i++, mover++) {
			int proximaLinha = linha + mover;
			if (proximaLinha >= TOTAL_LINHAS) {
				pontuacao.totalAdversario++;
				break;
			}
			int proximaPosicao = tabuleiro[proximaLinha][coluna];
			avaliadorComum(jogador, pontuacao, proximaPosicao);
		}
		if (pontuacao.totalAdversario > 0) {
			pontuacao.bloqueado--;
		}

		pontuacao.total = avaliarPontuacao(pontuacao, jogador);
		return pontuacao;
	}

	private Pontuacao avaliarHorizontal(Posicao posicao, int jogador) {
		int coluna = posicao.getColuna();
		int linha = posicao.getLinha();
		Pontuacao pontuacao = new Pontuacao();
		for (int i = coluna, mover = 1; i < coluna + 5; i++, mover++) {
			int proximaColuna = coluna + mover;
			if (proximaColuna >= TOTAL_COLUNAS) {
				pontuacao.totalAdversario++;
				break;
			}
			int proximaPosicao = tabuleiro[linha][proximaColuna];
			avaliadorComum(jogador, pontuacao, proximaPosicao);
		}
		if (pontuacao.totalAdversario > 0) {
			pontuacao.bloqueado--;
		}

		pontuacao.total = avaliarPontuacao(pontuacao, jogador);
		return pontuacao;
	}

	private void avaliadorComum(int jogador, Pontuacao pontuacao, int proximaPosicao) {
		if (proximaPosicao == jogador) {
			pontuacao.totalJogador++;
		} else if (proximaPosicao == JogadorTipo.LIVRE.getValor() && pontuacao.totalJogador > 1) {
			pontuacao.distancia++;
			pontuacao.livre++;
		} else if (proximaPosicao == JogadorTipo.LIVRE.getValor() && pontuacao.totalJogador <= 1) {
			pontuacao.livre++;
		} else if (proximaPosicao == JogadorTipo.COMPUTADOR.getValor()) {
			pontuacao.totalAdversario++;
		}
	}

	public void jogar(PosicaoBtn btn) {
		posicoesLivres = new ArrayList<>();
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
