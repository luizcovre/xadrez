package application;

import java.util.InputMismatchException;
import java.util.Scanner;

import xadrez.ExceptionXadrez;
import xadrez.PartidaXadrez;
import xadrez.PecaXadrez;
import xadrez.PosicaoXadrez;

public class Program {

	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);
		PartidaXadrez partida = new PartidaXadrez();
		
		while (true) {
			try {
				UI.limpaTela();
				UI.printBoard(partida.getPecas());
				System.out.println();
				System.out.print("Origem: ");
				PosicaoXadrez origem = UI.readPosicaoXadrez(sc);
				
				boolean[][] movimentosPossiveis = partida.movimentosPossiveis(origem);
				UI.limpaTela();
				UI.printBoard(partida.getPecas(), movimentosPossiveis);				
				System.out.println();
				System.out.print("Destino: ");
				PosicaoXadrez destino = UI.readPosicaoXadrez(sc);
				
				PecaXadrez pecaCapturada = partida.movimentandoPeca(origem, destino);
			}
			catch (ExceptionXadrez e) {
				System.out.println(e.getMessage());
				sc.nextLine();
			}
			catch (InputMismatchException e) {
				System.out.println(e.getMessage());
				sc.nextLine();
			}
		}	
	}
}