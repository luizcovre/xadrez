package tabuleiro;

public class Tabuleiro {

	private int linhas;
	private int colunas;
	private Peca[][] pecas;
	
	public Tabuleiro(int linhas, int colunas) {
		if (linhas < 1 || colunas < 1) {
			throw new TabuleiroException("Precisa ter pelo menos uma linha e uma coluna...");
		}
		this.linhas = linhas;
		this.colunas = colunas;
		pecas = new Peca[linhas][colunas];
	}

	public int getLinhas() {
		return linhas;
	}

	public int getColunas() {
		return colunas;
	}
	
	public Peca peca(int linha, int coluna) {
		if (!ExistePosicao(linha, coluna)) {
			throw new TabuleiroException("Posição fora do tabuleiro");
		}
		return pecas[linha][coluna];
	}
	
	public Peca peca(Posicao posicao) {
		if (!ExistePosicao(posicao)) {
			throw new TabuleiroException("Posição fora do tabuleiro");
		}
		return pecas [posicao.getLinha()][posicao.getColuna()];
	}
	
	public void colocaPeca(Peca peca, Posicao posicao) {
		if (temPeca(posicao)) {
			throw new TabuleiroException("Já tem peça na posição "+posicao);
		}
		pecas[posicao.getLinha()][posicao.getColuna()] = peca;
		peca.posicao = posicao;	
	}
	
	private boolean ExistePosicao(int linha, int coluna) {
		return linha >= 0 && linha < linhas && coluna >= 0 && coluna < colunas;
	}
	
	public boolean ExistePosicao(Posicao posicao) {
		return ExistePosicao(posicao.getLinha(), posicao.getColuna());
	}
	
	public boolean temPeca(Posicao posicao) {
		if (!ExistePosicao(posicao)) {
			throw new TabuleiroException("Posição fora do tabuleiro");
		}
		return peca(posicao) != null;
	}
}
