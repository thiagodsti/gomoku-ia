package br.ufsc.si;

import java.awt.EventQueue;

import javax.swing.JOptionPane;

public class Principal {

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				Gomoku gomoku = new Gomoku();
				TabuleiroFrame frame = new TabuleiroFrame(gomoku);
				String resposta = "";
				do {
					resposta = JOptionPane.showInputDialog(frame, "Quem deve iniciar jogando H para Humano e C para Computador?");
					if (!resposta.toUpperCase().equals("H") && !resposta.toUpperCase().equals("C")){
						JOptionPane.showMessageDialog(frame, "Resposta inválida");
					}
				} while (!resposta.toUpperCase().equals("H") && !resposta.toUpperCase().equals("C"));
				gomoku.iniciarJogo(frame, resposta);
				frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

}
