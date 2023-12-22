package xadrez;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import tabuleiro.Peca;
import tabuleiro.Posicao;
import tabuleiro.Tabuleiro;
import xadrez.pecas.Bispo;
import xadrez.pecas.Cavalo;
import xadrez.pecas.Peao;
import xadrez.pecas.Rainha;
import xadrez.pecas.Rei;
import xadrez.pecas.Torre;

public class PartidaXadrez {

	private int turno;
	private Cor jogadorAtual;
	private Tabuleiro tabuleiro;
	private boolean check;
	private boolean checkMate;
	private PecaXadrez enPassantVulneravel;
	private PecaXadrez promoted;

	private List<Peca> pecasNoTabuleiro = new ArrayList<>();
	private List<Peca> pecasCapturadas = new ArrayList<>();

	public PartidaXadrez() {
		tabuleiro = new Tabuleiro(8, 8);
		turno = 1;
		jogadorAtual = Cor.BRANCO;
		check = false;
		setupInicial();
	}

	public int getTurno() {
		return turno;
	}

	public Cor getJogadorAtual() {
		return jogadorAtual;
	}

	public boolean getCheck() {
		return check;
	}

	public boolean getCheckMate() {
		return checkMate;
	}

	public PecaXadrez getEnPassantVulneravel() {
		return enPassantVulneravel;
	}
	
	public PecaXadrez getPromoted() {
		return promoted;
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
		validaPosicaoOrigem(posicao);
		return tabuleiro.peca(posicao).movimentosPossiveis();
	}

	public PecaXadrez movimentandoPeca(PosicaoXadrez fontePosicao, PosicaoXadrez destinoPosicao) {
		Posicao origem = fontePosicao.toPosition();
		Posicao destino = destinoPosicao.toPosition();
		validaPosicaoOrigem(origem);
		validaPosicaoDestino(origem, destino);
		Peca pecaCapturada = facaMovimento(origem, destino);

		if (testCheck(jogadorAtual)) {
			desfazMovimento(origem, destino, pecaCapturada);
			throw new ExceptionXadrez("Você não pode se colocar em check.");
		}

		PecaXadrez pecaMovida = (PecaXadrez) tabuleiro.peca(destino);

		// movimento especial Promovido
		promoted = null;
		if (pecaMovida instanceof Peao) {
			if ((pecaMovida.getCor() == Cor.BRANCO) && destino.getLinha() == 0 || (pecaMovida.getCor() == Cor.PRETO) && destino.getLinha() == 7) {
				promoted = (PecaXadrez)tabuleiro.peca(destino);
				promoted = substituaPecaPromovida("D");
			}
		}
		
		check = (testCheck(oponente(jogadorAtual))) ? true : false;

		if (testCheck(oponente(jogadorAtual))) {
			checkMate = true;
		} else {
			proximoTurno();
		}

		// movimento especial en Passant
		if (pecaMovida instanceof Peao && (destino.getLinha() == origem.getLinha() - 2)
				|| destino.getLinha() == origem.getLinha() + 2) {
			enPassantVulneravel = pecaMovida;
		} else {
			enPassantVulneravel = null;
		}

		return (PecaXadrez) pecaCapturada;
	}

	public PecaXadrez substituaPecaPromovida(String type) {
		if (promoted == null) {
			throw new IllegalStateException("Não tem peça a ser promovida.");
		}
		if (!type.equals("B") && !type.equals("C") && !type.equals("T") && !type.equals("D")) {
			throw new InvalidParameterException("Tipo inválido de promoção.");
		}
		
		Posicao pos = promoted.getPosicaoXadrez().toPosition();
		Peca p = tabuleiro.removePeca(pos);
		pecasNoTabuleiro.remove(p);
		
		PecaXadrez novaPeca = novaPeca(type, promoted.getCor());
		tabuleiro.colocaPeca(novaPeca, pos);
		pecasNoTabuleiro.add(novaPeca);
		
		return novaPeca;
	}
	
	private PecaXadrez novaPeca(String type, Cor cor) {
		if (type.equals("B")) return new Bispo(tabuleiro, cor);
		if (type.equals("C")) return new Cavalo(tabuleiro, cor);
		if (type.equals("D")) return new Rainha(tabuleiro, cor);
		return new Torre(tabuleiro, cor);
	}
	
	private Peca facaMovimento(Posicao origem, Posicao destino) {
		PecaXadrez p = (PecaXadrez) tabuleiro.removePeca(origem);
		p.incrementaContaMovimentos();
		Peca pecaCapturada = tabuleiro.removePeca(destino);
		tabuleiro.colocaPeca(p, destino);

		if (pecaCapturada != null) {
			pecasNoTabuleiro.remove(pecaCapturada);
			pecasCapturadas.add(pecaCapturada);
		}

		// movimento especial Rei para torre
		if (p instanceof Rei && destino.getColuna() == destino.getColuna() + 2) {
			Posicao origemT = new Posicao(origem.getLinha(), origem.getColuna() + 3);
			Posicao destinoT = new Posicao(origem.getLinha(), origem.getColuna() + 3);
			PecaXadrez torre = (PecaXadrez) tabuleiro.removePeca(origemT);
			tabuleiro.colocaPeca(torre, destinoT);
			torre.incrementaContaMovimentos();
		}

		// movimento especial Rainha para torre
		if (p instanceof Rei && destino.getColuna() == destino.getColuna() - 2) {
			Posicao origemT = new Posicao(origem.getLinha(), origem.getColuna() - 4);
			Posicao destinoT = new Posicao(origem.getLinha(), origem.getColuna() - 1);
			PecaXadrez torre = (PecaXadrez) tabuleiro.removePeca(origemT);
			tabuleiro.colocaPeca(torre, destinoT);
			torre.incrementaContaMovimentos();
		}

		// movimento especial en Passant
		if (p instanceof Peao) {
			if (origem.getColuna() != destino.getColuna() && pecaCapturada == null) {
				Posicao posicaoPeao;
				if (p.getCor() == Cor.BRANCO) {
					posicaoPeao = new Posicao(destino.getLinha() + 1, destino.getColuna());
				} else {
					posicaoPeao = new Posicao(destino.getLinha() - 1, destino.getColuna());
				}
				pecaCapturada = tabuleiro.removePeca(posicaoPeao);
				pecasCapturadas.add(pecaCapturada);
				pecasNoTabuleiro.remove(pecaCapturada);
			}
		}

		return pecaCapturada;
	}

	private void desfazMovimento(Posicao origem, Posicao destino, Peca pecaCapturada) {
		PecaXadrez p = (PecaXadrez) tabuleiro.peca(destino);
		p.decrementaContaMovimentos();
		tabuleiro.colocaPeca(p, origem);

		if (pecaCapturada != null) {
			tabuleiro.colocaPeca(pecaCapturada, destino);
			pecasCapturadas.remove(pecaCapturada);
			pecasNoTabuleiro.add(pecaCapturada);
		}

		// movimento especial Rei para torre
		if (p instanceof Rei && destino.getColuna() == destino.getColuna() + 2) {
			Posicao origemT = new Posicao(origem.getLinha(), origem.getColuna() + 3);
			Posicao destinoT = new Posicao(origem.getLinha(), origem.getColuna() + 3);
			PecaXadrez torre = (PecaXadrez) tabuleiro.removePeca(destinoT);
			tabuleiro.colocaPeca(torre, origemT);
			torre.decrementaContaMovimentos();
		}

		// movimento especial Rainha para torre
		if (p instanceof Rei && destino.getColuna() == destino.getColuna() - 2) {
			Posicao origemT = new Posicao(origem.getLinha(), origem.getColuna() - 4);
			Posicao destinoT = new Posicao(origem.getLinha(), origem.getColuna() - 1);
			PecaXadrez torre = (PecaXadrez) tabuleiro.removePeca(destinoT);
			tabuleiro.colocaPeca(torre, origemT);
			torre.decrementaContaMovimentos();
		}

		// movimento especial en Passant
		if (p instanceof Peao) {
			if (origem.getColuna() != destino.getColuna() && pecaCapturada == enPassantVulneravel) {
				PecaXadrez peao = (PecaXadrez)tabuleiro.removePeca(destino);
				Posicao posicaoPeao;
				if (p.getCor() == Cor.BRANCO) {
					posicaoPeao = new Posicao(3, destino.getColuna());
				} else {
					posicaoPeao = new Posicao(4, destino.getColuna());
				}
				tabuleiro.colocaPeca(peao, posicaoPeao);
			}
		}
	}

	private void validaPosicaoOrigem(Posicao posicao) {
		if (!tabuleiro.ExistePosicao(posicao)) {
			throw new ExceptionXadrez("Não tem uma peça na origem.");
		}
		if (jogadorAtual != ((PecaXadrez) tabuleiro.peca(posicao)).getCor()) {
			throw new ExceptionXadrez("A peça escolhida não é sua.");
		}
		if (!tabuleiro.peca(posicao).temAlgumMovimentoPossivel()) {
			throw new ExceptionXadrez("Não tem movimento pra essa peça.");
		}
	}

	private void validaPosicaoDestino(Posicao origem, Posicao destino) {
		if (!tabuleiro.peca(origem).movimentoPossivel(destino)) {
			throw new ExceptionXadrez("Peça escolhida não pode se mover para esse destino.");
		}
	}

	private void proximoTurno() {
		turno++;
		jogadorAtual = (jogadorAtual == Cor.BRANCO) ? Cor.PRETO : Cor.BRANCO;
	}

	private Cor oponente(Cor cor) {
		return (cor == Cor.BRANCO) ? Cor.PRETO : Cor.BRANCO;
	}

	private PecaXadrez rei(Cor cor) {
		List<Peca> list = pecasNoTabuleiro.stream().filter(x -> ((PecaXadrez) x).getCor() == cor)
				.collect(Collectors.toList());
		for (Peca p : list) {
			if (p instanceof Rei) {
				return (PecaXadrez) p;
			}
		}
		throw new IllegalStateException("Não tem peça rei " + cor + " no tabuleiro.");
	}

	private boolean testCheck(Cor cor) {
		Posicao posicaoRei = rei(cor).getPosicaoXadrez().toPosition();
		List<Peca> pecasOponente = pecasNoTabuleiro.stream().filter(x -> ((PecaXadrez) x).getCor() == oponente(cor))
				.collect(Collectors.toList());
		for (Peca p : pecasOponente) {
			boolean[][] mat = p.movimentosPossiveis();
			if (mat[posicaoRei.getLinha()][posicaoRei.getColuna()]) {
				return true;
			}
		}
		return false;
	}

	private boolean testCheckMate(Cor cor) {
		if (!testCheck(cor)) {
			return false;
		}
		List<Peca> list = pecasNoTabuleiro.stream().filter(x -> ((PecaXadrez) x).getCor() == cor)
				.collect(Collectors.toList());
		for (Peca p : list) {
			boolean[][] mat = p.movimentosPossiveis();
			for (int i = 0; i < tabuleiro.getLinhas(); i++) {
				for (int j = 0; j < tabuleiro.getColunas(); j++) {
					if (mat[i][j]) {
						Posicao origem = ((PecaXadrez) p).getPosicaoXadrez().toPosition();
						Posicao destino = new Posicao(i, j);
						Peca pecaCapturada = facaMovimento(origem, destino);
						boolean testCheck = testCheck(cor);
						desfazMovimento(origem, destino, pecaCapturada);
						if (!testCheck) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	private void colocaNovaPeca(char coluna, int linha, PecaXadrez peca) {
		tabuleiro.colocaPeca(peca, new PosicaoXadrez(coluna, linha).toPosition());
		pecasNoTabuleiro.add(peca);
	}

	private void setupInicial() {
		colocaNovaPeca('a', 1, new Torre(tabuleiro, Cor.BRANCO));
		colocaNovaPeca('b', 1, new Cavalo(tabuleiro, Cor.BRANCO));
		colocaNovaPeca('c', 1, new Bispo(tabuleiro, Cor.BRANCO));
		colocaNovaPeca('d', 1, new Rainha(tabuleiro, Cor.BRANCO));
		colocaNovaPeca('e', 1, new Rei(tabuleiro, Cor.BRANCO, this));
		colocaNovaPeca('f', 1, new Bispo(tabuleiro, Cor.BRANCO));
		colocaNovaPeca('g', 1, new Cavalo(tabuleiro, Cor.BRANCO));
		colocaNovaPeca('h', 1, new Torre(tabuleiro, Cor.BRANCO));
		colocaNovaPeca('a', 2, new Peao(tabuleiro, Cor.BRANCO, this));
		colocaNovaPeca('b', 2, new Peao(tabuleiro, Cor.BRANCO, this));
		colocaNovaPeca('c', 2, new Peao(tabuleiro, Cor.BRANCO, this));
		colocaNovaPeca('d', 2, new Peao(tabuleiro, Cor.BRANCO, this));
		colocaNovaPeca('e', 2, new Peao(tabuleiro, Cor.BRANCO, this));
		colocaNovaPeca('f', 2, new Peao(tabuleiro, Cor.BRANCO, this));
		colocaNovaPeca('g', 2, new Peao(tabuleiro, Cor.BRANCO, this));
		colocaNovaPeca('h', 2, new Peao(tabuleiro, Cor.BRANCO, this));

		colocaNovaPeca('a', 8, new Torre(tabuleiro, Cor.PRETO));
		colocaNovaPeca('b', 8, new Cavalo(tabuleiro, Cor.PRETO));
		colocaNovaPeca('c', 8, new Bispo(tabuleiro, Cor.PRETO));
		colocaNovaPeca('d', 8, new Rainha(tabuleiro, Cor.PRETO));
		colocaNovaPeca('e', 8, new Rei(tabuleiro, Cor.PRETO, this));
		colocaNovaPeca('f', 8, new Bispo(tabuleiro, Cor.PRETO));
		colocaNovaPeca('g', 8, new Cavalo(tabuleiro, Cor.PRETO));
		colocaNovaPeca('h', 8, new Torre(tabuleiro, Cor.PRETO));
		colocaNovaPeca('a', 7, new Peao(tabuleiro, Cor.PRETO, this));
		colocaNovaPeca('b', 7, new Peao(tabuleiro, Cor.PRETO, this));
		colocaNovaPeca('c', 7, new Peao(tabuleiro, Cor.PRETO, this));
		colocaNovaPeca('d', 7, new Peao(tabuleiro, Cor.PRETO, this));
		colocaNovaPeca('e', 7, new Peao(tabuleiro, Cor.PRETO, this));
		colocaNovaPeca('f', 7, new Peao(tabuleiro, Cor.PRETO, this));
		colocaNovaPeca('g', 7, new Peao(tabuleiro, Cor.PRETO, this));
		colocaNovaPeca('h', 7, new Peao(tabuleiro, Cor.PRETO, this));

	}
}