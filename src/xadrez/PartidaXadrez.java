package xadrez;

import tabuleiro.Peca;
import tabuleiro.Posicao;
import tabuleiro.Tabuleiro;
import xadrez.pecas.Rei;
import xadrez.pecas.Torre;

public class PartidaXadrez {

	private Tabuleiro tabuleiro;

	public PartidaXadrez() {
		tabuleiro = new Tabuleiro(8, 8);
		setupInicial();
	}

	public PecaXadrez[][] getPecas() {
		PecaXadrez[][] mat = new PecaXadrez[tabuleiro.getLinhas()][tabuleiro.getColunas()];
		for (int i = 0; i < tabuleiro.getLinhas(); i++) {
			for (int j = 0; j < tabuleiro.getColunas(); j++) {
				mat[i][j] = (PecaXadrez) tabuleiro.peca(i, j);
			}
		}
		return mat;
	}

	public boolean[][] movimentosPossiveis(PosicaoXadrez posicaoOrigem) {
		Posicao posicao = posicaoOrigem.toPosition();
		validaFontePosicao(posicao);
		return tabuleiro.peca(posicao).movimentosPossiveis();
	}
	
	public PecaXadrez movimentandoPeca(PosicaoXadrez fontePosicao, PosicaoXadrez destinoPosicao) {
		Posicao fonte = fontePosicao.toPosition();
		Posicao destino = destinoPosicao.toPosition();
		validaFontePosicao(fonte);
		validaDestinoPosicao(fonte, destino);
		Peca pecaCapturada = facaMovimento(fonte, destino);
		return (PecaXadrez) pecaCapturada;
	}
	
	private Peca facaMovimento(Posicao fonte, Posicao destino) {
		Peca p = tabuleiro.removePeca(fonte);
		Peca pecaCapturada = tabuleiro.removePeca(destino);
		tabuleiro.colocaPeca(p, destino);
		return pecaCapturada;
	}
	
	private void validaFontePosicao(Posicao posicao) {
		if (!tabuleiro.ExistePosicao(posicao)) {
			throw new ExceptionXadrez("Tem uma peça na posição.");
		}
		if (!tabuleiro.peca(posicao).temAlgumMovimentoPossivel()) {
			throw new ExceptionXadrez("Não tem movimento pra essa peça.");
		}
	}
	
	private void validaDestinoPosicao(Posicao origem, Posicao destino) {
		if (!tabuleiro.peca(origem).movimentoPossivel(destino)) {
			throw new ExceptionXadrez("Peça escolhida não pode se mover para esse destino.");
		}
	}
	
	private void colocaNovaPeca(char coluna, int linha, PecaXadrez peca) {
		tabuleiro.colocaPeca(peca, new PosicaoXadrez(coluna, linha).toPosition());
	}

	private void setupInicial() {
		colocaNovaPeca('c', 1, new Torre(tabuleiro, Cor.BRANCO));
		colocaNovaPeca('c', 2, new Torre(tabuleiro, Cor.BRANCO));
		colocaNovaPeca('d', 2, new Torre(tabuleiro, Cor.BRANCO));
		colocaNovaPeca('e', 2, new Torre(tabuleiro, Cor.BRANCO));
		colocaNovaPeca('e', 1, new Torre(tabuleiro, Cor.BRANCO));
		colocaNovaPeca('d', 1, new Rei(tabuleiro, Cor.BRANCO));

		colocaNovaPeca('c', 7, new Torre(tabuleiro, Cor.PRETO));
		colocaNovaPeca('c', 8, new Torre(tabuleiro, Cor.PRETO));
		colocaNovaPeca('d', 7, new Torre(tabuleiro, Cor.PRETO));
		colocaNovaPeca('e', 7, new Torre(tabuleiro, Cor.PRETO));
		colocaNovaPeca('e', 8, new Torre(tabuleiro, Cor.PRETO));
		colocaNovaPeca('d', 8, new Rei(tabuleiro, Cor.PRETO));
	}
}