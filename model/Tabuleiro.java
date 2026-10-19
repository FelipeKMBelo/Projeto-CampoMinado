package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Representa o tabuleiro do Campo Minado: uma matriz bidimensional de
 * {@link Celula}. É a única classe que conhece a grade inteira, que sabe
 * posicionar minas e calcular vizinhança.
 * <p>
 * Parte do MODEL na arquitetura MVC. Implementa {@link LeituraTabuleiro}
 * para que a View possa consultar o estado do jogo sem depender da API
 * completa (mutável) desta classe.
 */
public class Tabuleiro implements LeituraTabuleiro {

    private final int linhas;
    private final int colunas;
    private final int numMinas;
    private final Celula[][] grade;

    private boolean jogoEncerrado;
    private boolean derrota;

    /**
     * Cria um tabuleiro novo com minas posicionadas aleatoriamente.
     *
     * @param linhas   número de linhas do tabuleiro
     * @param colunas  número de colunas do tabuleiro
     * @param numMinas quantidade de minas a posicionar
     */
    public Tabuleiro(int linhas, int colunas, int numMinas) {
        if (linhas <= 0 || colunas <= 0) {
            throw new IllegalArgumentException("Linhas e colunas devem ser maiores que zero.");
        }
        if (numMinas < 0 || numMinas >= linhas * colunas) {
            throw new IllegalArgumentException("Número de minas inválido para esse tabuleiro.");
        }

        this.linhas = linhas;
        this.colunas = colunas;
        this.numMinas = numMinas;
        this.grade = new Celula[linhas][colunas];
        inicializarGrade();
        posicionarMinasAleatoriamente();
        calcularMinasVizinhasDeTodasAsCelulas();
    }

    /**
     * Construtor auxiliar que recebe as posições das minas explicitamente,
     * em vez de sortear. Pensado para ser usado em testes unitários, onde
     * é preciso saber exatamente onde as minas estão para verificar o
     * comportamento da cascata e da contagem de vizinhas.
     *
     * @param linhas         número de linhas do tabuleiro
     * @param colunas        número de colunas do tabuleiro
     * @param posicoesMinas  array de pares {linha, coluna} com as minas
     */
    public Tabuleiro(int linhas, int colunas, int[][] posicoesMinas) {
        this.linhas = linhas;
        this.colunas = colunas;
        this.numMinas = posicoesMinas.length;
        this.grade = new Celula[linhas][colunas];
        inicializarGrade();
        for (int[] posicao : posicoesMinas) {
            grade[posicao[0]][posicao[1]].setMinada(true);
        }
        calcularMinasVizinhasDeTodasAsCelulas();
    }

    private void inicializarGrade() {
        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                grade[i][j] = new Celula();
            }
        }
    }

    private void posicionarMinasAleatoriamente() {
        Random sorteio = new Random();
        int minasColocadas = 0;
        while (minasColocadas < numMinas) {
            int linha = sorteio.nextInt(linhas);
            int coluna = sorteio.nextInt(colunas);
            if (!grade[linha][coluna].isMinada()) {
                grade[linha][coluna].setMinada(true);
                minasColocadas++;
            }
        }
    }

    private void calcularMinasVizinhasDeTodasAsCelulas() {
        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                grade[i][j].setMinasVizinhas(contarMinasVizinhas(i, j));
            }
        }
    }

    private int contarMinasVizinhas(int linha, int coluna) {
        int total = 0;
        for (int deltaLinha = -1; deltaLinha <= 1; deltaLinha++) {
            for (int deltaColuna = -1; deltaColuna <= 1; deltaColuna++) {
                if (deltaLinha == 0 && deltaColuna == 0) {
                    continue;
                }
                int vizinhoLinha = linha + deltaLinha;
                int vizinhoColuna = coluna + deltaColuna;
                if (dentroDosLimites(vizinhoLinha, vizinhoColuna)
                        && grade[vizinhoLinha][vizinhoColuna].isMinada()) {
                    total++;
                }
            }
        }
        return total;
    }

    private boolean dentroDosLimites(int linha, int coluna) {
        return linha >= 0 && linha < linhas && coluna >= 0 && coluna < colunas;
    }

    /**
     * Revela a célula indicada. Se a célula não tiver minas vizinhas, o
     * efeito cascata revela automaticamente as células ao redor (e assim
     * sucessivamente), sem nunca revelar uma célula minada por engano.
     * <p>
     * A cascata é implementada de forma iterativa usando uma
     * {@link ArrayList} como fila de células pendentes de revelação —
     * evita o uso de recursão profunda em tabuleiros grandes.
     *
     * @param linha  linha da célula a revelar
     * @param coluna coluna da célula a revelar
     * @return a lista das células que foram reveladas nesta jogada, na
     *         ordem em que foram reveladas — útil para quem quiser animar
     *         a cascata célula a célula (ex.: a View). Se a jogada não
     *         revelar nada (célula já revelada, marcada, jogo encerrado,
     *         etc.), retorna uma lista vazia.
     */
    public List<int[]> revelar(int linha, int coluna) {
        List<int[]> ordemRevelacao = new ArrayList<>();

        if (jogoEncerrado || !dentroDosLimites(linha, coluna)) {
            return ordemRevelacao;
        }

        Celula celulaInicial = grade[linha][coluna];
        if (celulaInicial.isRevelada() || celulaInicial.isMarcada()) {
            return ordemRevelacao;
        }

        if (celulaInicial.isMinada()) {
            celulaInicial.revelar();
            jogoEncerrado = true;
            derrota = true;
            ordemRevelacao.add(new int[] { linha, coluna });
            return ordemRevelacao;
        }

        List<int[]> pendentes = new ArrayList<>();
        pendentes.add(new int[] { linha, coluna });

        while (!pendentes.isEmpty()) {
            int[] posicaoAtual = pendentes.remove(pendentes.size() - 1);
            int linhaAtual = posicaoAtual[0];
            int colunaAtual = posicaoAtual[1];
            Celula atual = grade[linhaAtual][colunaAtual];

            if (atual.isRevelada() || atual.isMarcada() || atual.isMinada()) {
                continue;
            }

            atual.revelar();
            ordemRevelacao.add(new int[] { linhaAtual, colunaAtual });

            if (atual.getMinasVizinhas() == 0) {
                for (int deltaLinha = -1; deltaLinha <= 1; deltaLinha++) {
                    for (int deltaColuna = -1; deltaColuna <= 1; deltaColuna++) {
                        if (deltaLinha == 0 && deltaColuna == 0) {
                            continue;
                        }
                        int vizinhoLinha = linhaAtual + deltaLinha;
                        int vizinhoColuna = colunaAtual + deltaColuna;
                        if (dentroDosLimites(vizinhoLinha, vizinhoColuna)) {
                            Celula vizinha = grade[vizinhoLinha][vizinhoColuna];
                            if (!vizinha.isRevelada() && !vizinha.isMarcada() && !vizinha.isMinada()) {
                                pendentes.add(new int[] { vizinhoLinha, vizinhoColuna });
                            }
                        }
                    }
                }
            }
        }

        if (verificarVitoria()) {
            jogoEncerrado = true;
        }

        return ordemRevelacao;
    }

    /**
     * MELHORIA (chording): revela de uma vez todos os vizinhos não
     * marcados de uma célula já revelada, desde que o número de
     * bandeiras ao redor dela já bata com o número mostrado na célula.
     * É o recurso clássico de "duplo clique" / "clique do meio" do
     * Campo Minado, usado por jogadores experientes para jogar mais
     * rápido quando têm certeza de onde estão as minas vizinhas.
     * <p>
     * Reaproveita {@link #revelar(int, int)} para cada vizinho, então
     * a cascata e a derrota (se alguma bandeira estiver no lugar
     * errado) continuam funcionando exatamente como em uma revelação
     * normal.
     *
     * @param linha  linha da célula central (já revelada)
     * @param coluna coluna da célula central (já revelada)
     * @return lista de células reveladas nesta jogada, na ordem em que
     *         foram reveladas; vazia se a condição de chording não foi
     *         satisfeita.
     */
    public List<int[]> revelarVizinhos(int linha, int coluna) {
        List<int[]> reveladas = new ArrayList<>();

        if (jogoEncerrado || !dentroDosLimites(linha, coluna)) {
            return reveladas;
        }

        Celula central = grade[linha][coluna];
        if (!central.isRevelada() || central.isMinada()) {
            return reveladas;
        }

        int bandeirasVizinhas = 0;
        for (int deltaLinha = -1; deltaLinha <= 1; deltaLinha++) {
            for (int deltaColuna = -1; deltaColuna <= 1; deltaColuna++) {
                if (deltaLinha == 0 && deltaColuna == 0) {
                    continue;
                }
                int vl = linha + deltaLinha;
                int vc = coluna + deltaColuna;
                if (dentroDosLimites(vl, vc) && grade[vl][vc].isMarcada()) {
                    bandeirasVizinhas++;
                }
            }
        }

        if (bandeirasVizinhas != central.getMinasVizinhas()) {
            // Número de bandeiras ainda não bate: não faz nada, igual ao
            // comportamento clássico (evita revelar minas por engano).
            return reveladas;
        }

        for (int deltaLinha = -1; deltaLinha <= 1; deltaLinha++) {
            for (int deltaColuna = -1; deltaColuna <= 1; deltaColuna++) {
                if (deltaLinha == 0 && deltaColuna == 0) {
                    continue;
                }
                int vl = linha + deltaLinha;
                int vc = coluna + deltaColuna;
                if (dentroDosLimites(vl, vc)) {
                    Celula vizinha = grade[vl][vc];
                    if (!vizinha.isRevelada() && !vizinha.isMarcada()) {
                        reveladas.addAll(revelar(vl, vc));
                    }
                }
            }
        }

        return reveladas;
    }

    /**
     * MELHORIA (sistema de dicas): procura, entre as células ainda não
     * reveladas e não marcadas, uma que seja segura (sem mina) e a
     * sorteia. Usada pelo Controller para o botão de dica.
     *
     * @return posição {linha, coluna} de uma célula seguravel para
     *         revelar, ou {@code null} se não houver mais nenhuma
     *         célula segura escondida (ex.: só faltam minas).
     */
    public int[] obterCelulaSegura() {
        List<int[]> candidatas = new ArrayList<>();
        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                Celula celula = grade[i][j];
                if (!celula.isMinada() && !celula.isRevelada() && !celula.isMarcada()) {
                    candidatas.add(new int[] { i, j });
                }
            }
        }
        if (candidatas.isEmpty()) {
            return null;
        }
        return candidatas.get(new Random().nextInt(candidatas.size()));
    }

    /**
     * Marca ou desmarca uma célula com bandeira, sem revelá-la.
     */
    public void alternarMarcacao(int linha, int coluna) {
        if (jogoEncerrado || !dentroDosLimites(linha, coluna)) {
            return;
        }
        grade[linha][coluna].alternarMarcacao();
    }

    /**
     * O jogo é vencido quando todas as células que não são minas já
     * foram reveladas.
     */
    public boolean verificarVitoria() {
        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                Celula celula = grade[i][j];
                if (!celula.isMinada() && !celula.isRevelada()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean isDerrota() {
        return derrota;
    }

    @Override
    public boolean isJogoEncerrado() {
        return jogoEncerrado;
    }

    @Override
    public int getLinhas() {
        return linhas;
    }

    @Override
    public int getColunas() {
        return colunas;
    }

    public int getNumMinas() {
        return numMinas;
    }

    // ----- implementação de LeituraTabuleiro (usada pela View) -----

    @Override
    public boolean isRevelada(int linha, int coluna) {
        return grade[linha][coluna].isRevelada();
    }

    @Override
    public boolean isMarcada(int linha, int coluna) {
        return grade[linha][coluna].isMarcada();
    }

    @Override
    public boolean isInterrogacao(int linha, int coluna) {
        return grade[linha][coluna].isInterrogacao();
    }

    @Override
    public boolean isMinada(int linha, int coluna) {
        return grade[linha][coluna].isMinada();
    }

    @Override
    public int getMinasVizinhas(int linha, int coluna) {
        return grade[linha][coluna].getMinasVizinhas();
    }

    /**
     * Retorna a célula em uma posição específica. Mantido para uso interno
     * do próprio Model e para os testes unitários — a View nunca deve
     * chamar este método diretamente; ela usa {@link LeituraTabuleiro}.
     */
    public Celula getCelula(int linha, int coluna) {
        return grade[linha][coluna];
    }

    /**
     * Imprime o tabuleiro no console. Quando revelarTudo é true (por
     * exemplo, ao final de uma derrota), mostra também as minas.
     */
    public void imprimir(boolean revelarTudo) {
        StringBuilder cabecalho = new StringBuilder("   ");
        for (int j = 0; j < colunas; j++) {
            cabecalho.append(String.format("%2d", j));
        }
        System.out.println(cabecalho);

        for (int i = 0; i < linhas; i++) {
            StringBuilder linhaTexto = new StringBuilder(String.format("%2d ", i));
            for (int j = 0; j < colunas; j++) {
                Celula celula = grade[i][j];
                if (revelarTudo && celula.isMinada()) {
                    linhaTexto.append(" *");
                } else {
                    linhaTexto.append(" ").append(celula);
                }
            }
            System.out.println(linhaTexto);
        }
    }
}
