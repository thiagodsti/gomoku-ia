package br.ufsc.si;

import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class TabuleiroFrame extends JFrame {

	private JPanel contentPane;

	/**
	 * Create the frame.
	 */
	public TabuleiroFrame(Gomoku gomoku) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 683, 563);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(15, 15, 0, 0));

		for (int i = 0; i < 15; i++) {
			for (int j = 0; j < 15; j++) {
				PosicaoBtn btn = new PosicaoBtn(i, j);
				contentPane.add(btn);
				btn.addActionListener((e) -> {
					gomoku.jogar((PosicaoBtn) e.getSource());
				});
			}
		}
	}

}
