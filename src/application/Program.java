package application;

import tabuleiro.Tabuleiro;
import xadrez.PartidaXadrez;

public class Program {

	public static void main(String[] args) {

		PartidaXadrez partida = new PartidaXadrez();
		UI.printBoard(partida.getPecas());
	}

}
