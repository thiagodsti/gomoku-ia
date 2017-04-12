package br.ufsc.si;

import java.awt.EventQueue;

public class Principal {

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				Gomoku gomoku = new Gomoku();
				TabuleiroFrame frame = new TabuleiroFrame(gomoku);
				frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

}
