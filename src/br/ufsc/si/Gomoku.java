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
	private TabuleiroFrame frame;
	private Posicao ultimaPosicaoJogada;
	private int melhorLinha = -1;
	private int melhorColuna = -1;

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
			minimax(this.ultimaPosicaoJogada, PROFUNDIDADE, true, MENOS_INFINITO, MAIS_INFINITO, 0);
			this.frame.encontrarBotao(melhorLinha, melhorColuna).doClick();
		} else {
			PosicaoBtn btn = this.frame.encontrarBotao(7, 7);
			btn.doClick();
		}

	}

	private double minimax(Posicao posicao, int profundidade, boolean maximizar, double alpha, double beta,
			double heuristica) {

		List<Posicao> posicoesLivres = pegarFilhosProximos(posicao);

		if (temGanhador()) {
			posicao.heuristica += MAIS_INFINITO;
			return MAIS_INFINITO;
		} else if (profundidade == 0) {
			double valor = avaliar(posicao);
			posicao.heuristica += valor;
			return valor;
		} else {
			for (Posicao filho : posicoesLivres) {
				if (maximizar) {
					filho.setJogador(JogadorTipo.COMPUTADOR.getValor());
					moverPosicaoTabuleiroPrincipal(filho, JogadorTipo.COMPUTADOR);
					heuristica = heuristica + minimax(filho, profundidade - 1, false, alpha, beta, heuristica);
					filho.heuristica += heuristica;
					if (heuristica > alpha) {
						alpha = heuristica;
						melhorLinha = filho.getLinha();
						melhorColuna = filho.getColuna();
					}
				} else {
					filho.setJogador(JogadorTipo.HUMANO.getValor());
					moverPosicaoTabuleiroPrincipal(filho, JogadorTipo.HUMANO);
					heuristica = heuristica - minimax(filho, profundidade - 1, true, alpha, beta, heuristica);
					filho.heuristica -= heuristica;
					if (heuristica < beta) {
						beta = heuristica;
						melhorLinha = filho.getLinha();
						melhorColuna = filho.getColuna();
					}
				}

				// desfazer movimento
				desfazerMovimento(filho);

				if (alpha >= beta) {
					break;
				}
			}
		}
		return heuristica;
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

			// Buscar filhos horizontal
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

			// Buscar filhos vertical
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

			// Buscar filhos diagonal mesmo sentido
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

			// Buscar filhos diagonal sentido inverso
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

	private double avaliar(Posicao posicao) {
		double heuristica = posicao.heuristica;
		int jogador = posicao.getJogador();
		Pontuacao pontosHorizontal = avaliarHorizontal(posicao, jogador);
		Pontuacao pontosVertical = avaliarVertical(posicao, jogador);
		Pontuacao pontosDiagonal = avaliarDiagonalMesmoSentido(posicao, jogador);
		Pontuacao pontosDiagonalInversa = avaliarDiagonalInversa(posicao, jogador);
		if (jogador == JogadorTipo.COMPUTADOR.getValor()) {
			heuristica += pontosHorizontal.total + pontosVertical.total + pontosDiagonal.total
				+ pontosDiagonalInversa.total;
		} else {
			heuristica -= pontosHorizontal.total - pontosVertical.total - pontosDiagonal.total - pontosDiagonalInversa.total;
		}
		return heuristica;
	}

	private double avaliarPontuacao(Pontuacao pontuacao) {
		double total = 0;
		total += pontuacao.umFechado * 0.5;
		total += pontuacao.um * 1;
		total += pontuacao.doisFechado * 100;
		total += pontuacao.dois * 200;
		total += pontuacao.tresFechado * 3500;
		total += pontuacao.tres * 7000;
		total += pontuacao.quatroFechado * 500000;
		total += pontuacao.quatro * 1000000;
		total += pontuacao.cinco * 100000000;
		return total;
	}
	
	/**
	 * Avalia a posição do jogador
	 * @param jogador
	 * @param pontuacao
	 * @param proximaPosicao
	 * @return false se o jogador foi bloqueado.
	 */
	private boolean avaliarPosicao(int jogador, Pontuacao pontuacao, int proximaPosicao) {
		if (proximaPosicao == jogador) {
			pontuacao.totalJogador++;
		} else if (proximaPosicao == JogadorTipo.LIVRE.getValor()) {
			pontuacao.livre++;
		} else {
			pontuacao.bloqueado++;
			return false;
		}
		return true;
	}

	private Pontuacao avaliarDiagonalMesmoSentido(Posicao posicao, int jogador) {
		int linha = posicao.linha;
		int coluna = posicao.coluna;
		Pontuacao pontuacao = new Pontuacao();
		for (int i=linha, j=coluna, mover = 0;mover < 5;mover++) {
			int proximaLinha = i + mover;
			int proximaColuna = j + mover;
			if (proximaLinha >= TOTAL_LINHAS || proximaColuna >= TOTAL_COLUNAS) {
				break;
			}
			int proximaPosicao = tabuleiro[proximaLinha][proximaColuna];
			boolean continuar = avaliarPosicao(jogador, pontuacao, proximaPosicao);
			if (!continuar) {
				break;
			}
		}
		
		for (int i=linha, j=coluna, mover = 1;mover < 5;mover++) {
			int proximaLinha = i - mover;
			int proximaColuna = j - mover;
			if (proximaLinha < 0 || proximaColuna < 0) {
				break;
			}
			int proximaPosicao = tabuleiro[proximaLinha][proximaColuna];
			boolean continuar = avaliarPosicao(jogador, pontuacao, proximaPosicao);
			if (!continuar) {
				break;
			}
		}
		avaliadorComum(pontuacao);
		pontuacao.total = avaliarPontuacao(pontuacao);
		return pontuacao;
	}

	private Pontuacao avaliarDiagonalInversa(Posicao posicao, int jogador) {
		int linha = posicao.linha;
		int coluna = posicao.coluna;
		Pontuacao pontuacao = new Pontuacao();
		for (int i=linha, j=coluna, mover = 0;mover < 5;mover++) {
			int proximaLinha = i + mover;
			int proximaColuna = j - mover;
			if (proximaLinha >= TOTAL_LINHAS || proximaColuna < 0) {
				break;
			}
			int proximaPosicao = tabuleiro[proximaLinha][proximaColuna];
			boolean continuar = avaliarPosicao(jogador, pontuacao, proximaPosicao);
			if (!continuar) {
				break;
			}
		}
		
		for (int i=linha, j=coluna, mover = 1;mover < 5;mover++) {
			int proximaLinha = i - mover;
			int proximaColuna = j + mover;
			if (proximaColuna >= TOTAL_COLUNAS || proximaLinha < 0) {
				break;
			}
			int proximaPosicao = tabuleiro[proximaLinha][proximaColuna];
			boolean continuar = avaliarPosicao(jogador, pontuacao, proximaPosicao);
			if (!continuar) {
				break;
			}
		}
		avaliadorComum(pontuacao);
		pontuacao.total = avaliarPontuacao(pontuacao);
		return pontuacao;
	}

	private Pontuacao avaliarVertical(Posicao posicao, int jogador) {
		int linha = posicao.linha;
		int coluna = posicao.coluna;
		Pontuacao pontuacao = new Pontuacao();
		for (int i=linha, mover = 0;mover < 5;mover++) {
			int proximaLinha = i + mover;
			if (proximaLinha >= TOTAL_LINHAS) {
				break;
			}
			int proximaPosicao = tabuleiro[proximaLinha][coluna];
			boolean continuar = avaliarPosicao(jogador, pontuacao, proximaPosicao);
			if (!continuar) {
				break;
			}
		}
		
		for (int i=linha, mover = 1;mover < 5;mover++) {
			int proximaLinha = i - mover;
			if (proximaLinha < 0) {
				break;
			}
			int proximaPosicao = tabuleiro[proximaLinha][coluna];
			boolean continuar = avaliarPosicao(jogador, pontuacao, proximaPosicao);
			if (!continuar) {
				break;
			}
		}
		avaliadorComum(pontuacao);
		pontuacao.total = avaliarPontuacao(pontuacao);
		return pontuacao;
	}

	private Pontuacao avaliarHorizontal(Posicao posicao, int jogador) {
		int coluna = posicao.getColuna();
		int linha = posicao.getLinha();
		Pontuacao pontuacao = new Pontuacao();
		for (int i=coluna, mover = 0;mover < 5;mover++) {
			int proximaColuna = i + mover;
			if (proximaColuna >= TOTAL_COLUNAS) {
				break;
			}
			int proximaPosicao = tabuleiro[linha][proximaColuna];
			boolean continuar = avaliarPosicao(jogador, pontuacao, proximaPosicao);
			if (!continuar) {
				break;
			}
		}
		
		for (int i=coluna, mover = 1;mover < 5;mover++) {
			int proximaColuna = i - mover;
			if (proximaColuna < 0) {
				break;
			}
			int proximaPosicao = tabuleiro[linha][proximaColuna];
			boolean continuar = avaliarPosicao(jogador, pontuacao, proximaPosicao);
			if (!continuar) {
				break;
			}
		}
		
		avaliadorComum(pontuacao);
		pontuacao.total = avaliarPontuacao(pontuacao);
		return pontuacao;
	}

	private void avaliadorComum(Pontuacao pontuacao) {
		if (pontuacao.bloqueado == 2) {
			return;
		}
		switch (pontuacao.totalJogador) {
			case 1:
				if (pontuacao.bloqueado == 0) {
					pontuacao.um++;
				} else {
					pontuacao.umFechado++;
				}
				break;
			case 2:
				if (pontuacao.bloqueado == 0) {
					pontuacao.dois++;
				} else {
					pontuacao.doisFechado++;
				}
				break;
			case 3:
				if (pontuacao.bloqueado == 0) {
					pontuacao.tres++;
				} else {
					pontuacao.tresFechado++;
				}
				break;
			case 4:
				if (pontuacao.bloqueado == 0) {
					pontuacao.quatro++;
				} else {
					pontuacao.quatroFechado++;
				}
				break;
			case 5:
				pontuacao.cinco++;
				break;
		}
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
