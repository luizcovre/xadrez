package xadrez;

import tabuleiro.Peca;
import tabuleiro.Posicao;
import tabuleiro.Tabuleiro;

public abstract class PecaXadrez extends Peca  {

	private Cor cor;
	private int contaMovimentos;

	public PecaXadrez(Tabuleiro tabuleiro, Cor cor) {
		super(tabuleiro);
		this.cor = cor;
	}

	public Cor getCor() {
		return cor;
	}

	public int getContaMovimentos() {
		return contaMovimentos;
	}
	
	public void incrementaContaMovimentos() {
		contaMovimentos++;
	}
	
	public void decrementaContaMovimentos() {
		contaMovimentos--;
	}
	
	public PosicaoXadrez getPosicaoXadrez() {
		return PosicaoXadrez.fromPosition(posicao);
	}
	
	protected boolean temUmaPecaOponente(Posicao posicao) {
		PecaXadrez p = (PecaXadrez)getTabuleiro().peca(posicao);
		return p != null && p.getCor() != cor;
	}
}
