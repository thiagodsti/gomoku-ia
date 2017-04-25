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
			this.ultimaPosicaoJogada = jogadas.get(totalJogadas - 2);
			Posicao melhorPosicao = minimax(this.ultimaPosicaoJogada, PROFUNDIDADE, true, MENOS_INFINITO,
					MAIS_INFINITO);
			this.ultimaPosicaoJogada = jogadas.get(totalJogadas - 1);
			Posicao melhorPosicao2 = minimax(this.ultimaPosicaoJogada, PROFUNDIDADE, true, MENOS_INFINITO,
					MAIS_INFINITO);
			if (melhorPosicao2.getHeuristica() > melhorPosicao.getHeuristica()) {
				melhorPosicao = melhorPosicao2;
			}
			this.frame.encontrarBotao(melhorPosicao.getLinha(), melhorPosicao.getColuna()).doClick();
		} else {
			PosicaoBtn btn = this.frame.encontrarBotao(7, 7);
			btn.doClick();
		}

	}

	private Posicao minimax(Posicao posicao, int profundidade, boolean maximizar, long alpha, long beta) {

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
					if (melhor.getHeuristica() > alpha) {
						alpha = melhor.getHeuristica();
						posicao = melhor;
					}
				} else {
					filho.setJogador(JogadorTipo.HUMANO.getValor());
					moverPosicaoTabuleiroPrincipal(filho, JogadorTipo.HUMANO);
					melhor = minimax(filho, profundidade - 1, true, alpha, beta);
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

		int jogador = posicao.getJogador();
		int adversario;
		if (jogador == JogadorTipo.COMPUTADOR.getValor()) {
			adversario = JogadorTipo.HUMANO.getValor();
		} else {
			adversario = JogadorTipo.COMPUTADOR.getValor();
		}

		List<Integer> achadosJogador = new ArrayList<>();
		List<Integer> achadosAdversario = new ArrayList<>();

		Pontuacao pontosHorizontal = avaliarHorizontal(linha, coluna, jogador);
		Pontuacao pontosHorizontalAdv = avaliarHorizontal(linha, coluna, adversario);

		Pontuacao pontosVertical = avaliarVertical(linha, coluna, jogador);
		Pontuacao pontosVerticalAdv = avaliarVertical(linha, coluna, adversario);

		Pontuacao pontosDiagonal = avaliarDiagonals(linha, coluna, jogador);
		Pontuacao pontosDiagonalAdv = avaliarDiagonals(linha, coluna, adversario);
		//achadosJogador.add(buscarPosicoesParaAvaliacao(tabuleiro, linha, coluna, linhaMover, colunaMover, jogador));
		//achadosAdversario
		//		.add(buscarPosicoesParaAvaliacao(tabuleiro, linha, coluna, linhaMover, colunaMover, adversario));

		posicao.setHeuristica(pontosHorizontal.total + pontosVertical.total + pontosDiagonal.total);

		if (posicao.getHeuristica() > 1) {
			System.out.println(posicao.getHeuristica());
		}
		return posicao;
	}

	private long avaliarPontuacao(Pontuacao pontuacao, boolean jogadorAtual) {
		if (pontuacao.getAbertura() == 0 && pontuacao.getTotalJogador() < 5) {
			return 0;
		}
		long total = 0;
		switch (pontuacao.getTotalJogador()) {
		case 1:
			switch (pontuacao.abertura) {
			case 1:
				return 0;
			case 2:
				return 1;
			}
			//total++;
			break;
		case 2:
			switch (pontuacao.abertura) {
			case 1:
				return 126 / 2;
			case 2:
				return 126;
			}
			break;
		case 3:
			switch (pontuacao.abertura) {
			case 1:
				if (jogadorAtual) {
					return 6175 / 3;
				}
				return 6175 / 4;
			case 2:
				if (jogadorAtual) {
					return 6175;
				}
				return 6175 / 2;
			}
			//total = total + 6175 / (pontuacao.getBloqueado() + 1);
			break;
		case 4:
			switch (pontuacao.abertura) {
			case 1:
				if (jogadorAtual) {
					return 154376 / 3;
				}
				return 154376 / 4;
			case 2:
				if (jogadorAtual) {
					return 154376;
				}
				return 154376 / 2;
			}
			//total = total + 154376 / (pontuacao.getBloqueado() + 1);
			break;
		case 5:
			total = Long.MAX_VALUE;
		}
		return total;
	}

	private Pontuacao avaliarDiagonals(int linha, int coluna, int jogador) {
		Pontuacao pontuacao = new Pontuacao();
		for (int i = linha; i < 5; i++) {
			int proximaLinha = linha + i;
			int proximaColuna = coluna + i;
			if (proximaLinha >= TOTAL_LINHAS || proximaColuna >= TOTAL_COLUNAS) {
				pontuacao.abertura--;
				pontuacao.totalJogador--;
			} else {

				int proximaPosicao = tabuleiro[proximaLinha][proximaColuna];
				avaliadorComum(jogador, pontuacao, proximaPosicao);
			}
		}

		for (int i = linha; i > 5; i--) {
			int proximaLinha = linha - i;
			int proximaColuna = coluna - i;
			if (proximaLinha < 0 || proximaColuna < 0) {
				pontuacao.abertura--;
				pontuacao.totalJogador--;
			} else {
				int proximaPosicao = tabuleiro[proximaLinha][proximaColuna];
				avaliadorComum(jogador, pontuacao, proximaPosicao);
			}
		}

		for (int i = linha; i > 5; i--) {
			int proximaLinha = linha - i;
			int proximaColuna = coluna + i;
			if (proximaLinha < 0 || proximaColuna >= TOTAL_COLUNAS) {
				pontuacao.abertura--;
				pontuacao.totalJogador--;
			} else {
				int proximaPosicao = tabuleiro[proximaLinha][proximaColuna];
				avaliadorComum(jogador, pontuacao, proximaPosicao);
			}
		}

		for (int i = linha; i < 5; i++) {
			int proximaLinha = linha + i;
			int proximaColuna = coluna - i;
			if (proximaLinha >= TOTAL_LINHAS || proximaColuna < 0) {
				pontuacao.abertura--;
				pontuacao.totalJogador--;
			} else {
				int proximaPosicao = tabuleiro[proximaLinha][proximaColuna];
				avaliadorComum(jogador, pontuacao, proximaPosicao);
			}
		}

		return pontuacao;
	}

	private Pontuacao avaliarVertical(int linha, int coluna, int jogador) {
		Pontuacao pontuacao = new Pontuacao();
		for (int i = linha; i < 5; i++) {
			int proximaLinha = linha + i;
			if (proximaLinha >= TOTAL_LINHAS) {
				pontuacao.abertura--;
				pontuacao.totalJogador--;
			}
			int proximaPosicao = tabuleiro[proximaLinha][coluna];
			avaliadorComum(jogador, pontuacao, proximaPosicao);
		}

		for (int i = linha; i > 5; i--) {
			int proximaLinha = linha - i;
			if (proximaLinha < 0) {
				pontuacao.abertura--;
				pontuacao.totalJogador--;
			}
			int proximaPosicao = tabuleiro[proximaLinha][coluna];
			avaliadorComum(jogador, pontuacao, proximaPosicao);
		}
		return pontuacao;
	}

	private Pontuacao avaliarHorizontal(int linha, int coluna, int jogador) {
		Pontuacao pontuacao = new Pontuacao();
		for (int i = coluna; i < 5; i++) {
			int proximaColuna = coluna + i;
			if (proximaColuna >= TOTAL_COLUNAS) {
				pontuacao.abertura--;
				pontuacao.totalJogador--;
			}
			int proximaPosicao = tabuleiro[linha][proximaColuna];
			avaliadorComum(jogador, pontuacao, proximaPosicao);
		}

		for (int i = coluna; i > 5; i--) {
			int proximaColuna = coluna - i;
			if (proximaColuna < 0) {
				pontuacao.abertura--;
				pontuacao.totalJogador--;
			}
			int proximaPosicao = tabuleiro[linha][proximaColuna];
			avaliadorComum(jogador, pontuacao, proximaPosicao);
		}
		return pontuacao;
	}

	private void avaliadorComum(int jogador, Pontuacao pontuacao, int proximaPosicao) {
		if (proximaPosicao == jogador) {
			pontuacao.totalJogador++;
		} else if (proximaPosicao == JogadorTipo.LIVRE.getValor() && pontuacao.totalJogador > 0) {
			pontuacao.abertura++;
			pontuacao.total += this.avaliarPontuacao(pontuacao, jogador == jogadorDaVez.getTipo().getValor());
			pontuacao.totalJogador = 0;
			pontuacao.abertura = 1;
		} else if (proximaPosicao == JogadorTipo.LIVRE.getValor()) {
			pontuacao.abertura = 1;
		} else if (pontuacao.totalJogador > 0) {
			pontuacao.total += this.avaliarPontuacao(pontuacao, jogador == jogadorDaVez.getTipo().getValor());
		} else {
			pontuacao.totalJogador = 0;
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

	// Helper pra buscar posicoes.
	private int buscarPosicoesParaAvaliacao(int tabuleiro[][], int linhaInicial, int colunaInicial, int linhaMover,
			int colunaMover, int valorBuscar) {
		int totalAchado = 0;
		for (int cont = 0; cont < TOTAL_PARA_GANHAR; cont++) {
			int linha = linhaInicial + cont * linhaMover;
			int coluna = colunaInicial + cont * colunaMover;
			if (linha < 0 || coluna < 0 || linha >= TOTAL_LINHAS || coluna >= TOTAL_COLUNAS) {
				return -1;
			} else if (tabuleiro[linha][coluna] == JogadorTipo.LIVRE.getValor()) {
				continue;
			} else if (tabuleiro[linha][coluna] == valorBuscar) {
				totalAchado++;
			} else {
				return -1;
			}
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
