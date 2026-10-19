package controller;

import model.Tabuleiro;
import util.GerenciadorRecordes;
import util.SomUtil;
import util.HistoricoPartidas;
import java.io.IOException;
import view.CampoMinadoView;

import javax.swing.Timer;
import java.util.List;

/**
 * CONTROLLER da arquitetura MVC: é o único ponto que conhece tanto o
 * {@link Tabuleiro} (Model) quanto a {@link CampoMinadoView} (View).
 * Recebe notificações de clique da View através de {@link AcoesJogador},
 * aplica a jogada no Model e manda a View se redesenhar. A View nunca
 * toca no Model diretamente, e o Model nunca conhece a View.
 */
public class CampoMinadoController implements AcoesJogador {

    /** MELHORIA (dicas): quantidade de dicas disponíveis a cada nova partida. */
    private static final int DICAS_POR_PARTIDA = 3;

    private final CampoMinadoView view;
    // MELHORIA (persistência): grava/lê o melhor tempo por dificuldade em disco.
    private final GerenciadorRecordes gerenciadorRecordes = new GerenciadorRecordes();

    private HistoricoPartidas historico;
    private String jogador;
    private boolean partidaEncerrada = true;
    private Tabuleiro tabuleiro;
    private int totalMinas;
    private int totalCelulas;
    private int celulasReveladas;
    private int jogadas;
    private boolean jogoIniciado;
    private long tempoInicio;
    private int limiteSegundos;
    private Timer timerJogo;

    // MELHORIA (dicas / recordes): estado extra da partida atual.
    private int dicasRestantes;
    private String chaveDificuldadeAtual;

    public CampoMinadoController(CampoMinadoView view) {
        this.view = view;
        this.view.setOuvinte(this);
    }

    public void iniciar() {
        try { historico = new HistoricoPartidas(); }
        catch (IOException e) { view.mostrarAviso("Não foi possível carregar o histórico. Os dados existentes serão preservados."); }
        view.mostrarTelaInicial();
        view.setVisible(true);
    }

    // ================================================================
    // AcoesJogador — chamado pela View
    // ================================================================

    @Override
    public void aoEscolherDificuldade(int linhas, int colunas, int minas) {
        this.partidaEncerrada = false;
        this.jogador = view.getNomeJogador();
        this.tabuleiro = new Tabuleiro(linhas, colunas, minas);
        this.totalMinas = minas;
        this.totalCelulas = linhas * colunas - minas;
        this.celulasReveladas = 0;
        this.jogadas = 0;
        this.jogoIniciado = false;
        this.dicasRestantes = DICAS_POR_PARTIDA;
        this.chaveDificuldadeAtual = nomeDificuldade(linhas, colunas, minas);

        pararTimer();
        view.aplicarTemaSelecionado();
        // MELHORIA (som): liga/desliga os efeitos sonoros conforme a opção
        // escolhida pelo jogador na tela inicial.
        SomUtil.setLigado(view.isSomLigado());

        this.limiteSegundos = view.getTempoLimiteSegundosSelecionado();
        view.iniciarTelaDeJogo(linhas, colunas, totalMinas, totalCelulas, limiteSegundos);
        view.atualizarEstatisticas(totalMinas, 0, totalCelulas, 0);
        view.atualizarDicas(dicasRestantes);
        // MELHORIA (recorde): mostra, antes mesmo do primeiro clique, qual é
        // o recorde salvo para essa dificuldade (ou "--:--" se não houver).
        view.definirRecordeExibido(formatarTempo(gerenciadorRecordes.obterRecordeSegundos(chaveDificuldadeAtual)));
    }

    /**
     * Identifica a dificuldade atual para fins de recorde. As três
     * dificuldades padrão têm nome fixo; qualquer outra combinação (vinda
     * do modo "Personalizado") ganha uma chave própria baseada no tamanho
     * do tabuleiro, para não misturar recordes de tamanhos diferentes.
     */
    private String nomeDificuldade(int linhas, int colunas, int minas) {
        if (linhas == 9 && colunas == 9 && minas == 10) {
            return "Iniciante";
        }
        if (linhas == 16 && colunas == 16 && minas == 40) {
            return "Intermediário";
        }
        if (linhas == 16 && colunas == 30 && minas == 99) {
            return "Avançado";
        }
        return "Personalizado-" + linhas + "x" + colunas + "x" + minas;
    }

    private String formatarTempo(int segundos) {
        if (segundos < 0) {
            return "--:--";
        }
        return String.format("%02d:%02d", segundos / 60, segundos % 60);
    }

    @Override
    public void aoPedirNovoJogo() {
        if (jogoIniciado && !partidaEncerrada) registrarResultado("Abandonada");
        partidaEncerrada = true;
        pararTimer();
        view.mostrarTelaInicial();
    }

    @Override
    public void aoMarcarCelula(int linha, int coluna) {
        if (partidaEncerrada || tabuleiro == null || tabuleiro.isJogoEncerrado()) {
            return;
        }
        if (tempoEsgotou()) return;
        // MELHORIA: agora cicla entre bandeira e "?" (ver Celula.EstadoMarcacao).
        tabuleiro.alternarMarcacao(linha, coluna);
        SomUtil.tocarBandeira();
        view.atualizarCelula(linha, coluna, tabuleiro);
        atualizarEstatisticasNaView();
    }

    @Override
    public void aoRevelarCelula(int linha, int coluna) {
        if (tabuleiro == null || tabuleiro.isRevelada(linha, coluna) || tabuleiro.isMarcada(linha, coluna)) return;
        if (partidaEncerrada || tabuleiro == null || tabuleiro.isJogoEncerrado()) {
            return;
        }

        if (tempoEsgotou()) return;
        if (!jogoIniciado) {
            jogoIniciado = true;
            tempoInicio = System.currentTimeMillis();
            iniciarTimer();
        }

        if (limiteSegundos > 0 && obterSegundosPassados() >= limiteSegundos) {
            encerrarPorTempo();
            return;
        }

        jogadas++;
        List<int[]> reveladas = tabuleiro.revelar(linha, coluna);
        if (!reveladas.isEmpty()) {
            SomUtil.tocarClique();
        }
        celulasReveladas = contarCelulasReveladas();

        int atraso = reveladas.size() > 80 ? 3 : (reveladas.size() > 25 ? 8 : 18);
        animarRevelacao(reveladas, 0, atraso);
    }

    /**
     * MELHORIA (chording): disparado no duplo clique / clique do meio de
     * uma célula já revelada, para abrir de uma vez os vizinhos quando o
     * número de bandeiras ao redor já bate com o número da célula.
     */
    @Override
    public void aoRevelarVizinhos(int linha, int coluna) {
        if (partidaEncerrada || tabuleiro == null || tabuleiro.isJogoEncerrado() || !jogoIniciado) {
            return;
        }

        if (limiteSegundos > 0 && obterSegundosPassados() >= limiteSegundos) {
            encerrarPorTempo();
            return;
        }

        List<int[]> reveladas = tabuleiro.revelarVizinhos(linha, coluna);
        if (reveladas.isEmpty()) {
            return;
        }

        jogadas++;
        SomUtil.tocarClique();
        celulasReveladas = contarCelulasReveladas();

        int atraso = reveladas.size() > 80 ? 3 : (reveladas.size() > 25 ? 8 : 18);
        animarRevelacao(reveladas, 0, atraso);
    }

    /**
     * MELHORIA (sistema de dicas): revela uma célula segura escolhida ao
     * acaso pelo Model, gastando uma das dicas disponíveis na partida.
     */
    @Override
    public void aoPedirDica() {
        if (partidaEncerrada || tabuleiro == null || tabuleiro.isJogoEncerrado() || dicasRestantes <= 0) {
            return;
        }

        if (tempoEsgotou()) return;
        if (!jogoIniciado) {
            jogoIniciado = true;
            tempoInicio = System.currentTimeMillis();
            iniciarTimer();
        }

        int[] posicaoSegura = tabuleiro.obterCelulaSegura();
        if (posicaoSegura == null) {
            // Não há mais nenhuma célula segura escondida para sugerir
            // (só sobraram minas) — não gasta a dica à toa.
            return;
        }

        dicasRestantes--;
        view.atualizarDicas(dicasRestantes);

        jogadas++;
        List<int[]> reveladas = tabuleiro.revelar(posicaoSegura[0], posicaoSegura[1]);
        SomUtil.tocarClique();
        celulasReveladas = contarCelulasReveladas();

        int atraso = reveladas.size() > 80 ? 3 : (reveladas.size() > 25 ? 8 : 18);
        animarRevelacao(reveladas, 0, atraso);
    }

    // ================================================================
    // Contagens e sincronização com a View
    // ================================================================

    private int contarCelulasReveladas() {
        int count = 0;
        for (int i = 0; i < tabuleiro.getLinhas(); i++) {
            for (int j = 0; j < tabuleiro.getColunas(); j++) {
                if (tabuleiro.isRevelada(i, j) && !tabuleiro.isMinada(i, j)) {
                    count++;
                }
            }
        }
        return count;
    }

    private int contarMarcadas() {
        int count = 0;
        for (int i = 0; i < tabuleiro.getLinhas(); i++) {
            for (int j = 0; j < tabuleiro.getColunas(); j++) {
                if (tabuleiro.isMarcada(i, j)) {
                    count++;
                }
            }
        }
        return count;
    }

    private void atualizarEstatisticasNaView() {
        int restantes = totalMinas - contarMarcadas();
        view.atualizarEstatisticas(restantes, celulasReveladas, totalCelulas, jogadas);
    }

    // ================================================================
    // Timer do cronômetro
    // ================================================================

    private void iniciarTimer() {
        timerJogo = new Timer(1000, e -> atualizarTempo());
        timerJogo.start();
    }

    private void pararTimer() {
        if (timerJogo != null) {
            timerJogo.stop();
        }
    }

    private long obterSegundosPassados() {
        return (System.currentTimeMillis() - tempoInicio) / 1000;
    }

    private void atualizarTempo() {
        long segundosPassados = obterSegundosPassados();
        if (limiteSegundos > 0) {
            long restantes = Math.max(0, limiteSegundos - segundosPassados);
            view.atualizarTempo(String.format("-%02d:%02d", restantes / 60, restantes % 60));
            if (restantes <= 0) {
                encerrarPorTempo();
                return;
            }
        } else {
            view.atualizarTempo(String.format("%02d:%02d", segundosPassados / 60, segundosPassados % 60));
        }
    }

    private boolean tempoEsgotou() {
        if (jogoIniciado && limiteSegundos > 0 && obterSegundosPassados() >= limiteSegundos) {
            encerrarPorTempo();
            return true;
        }
        return false;
    }

    private void encerrarPorTempo() {
        if (partidaEncerrada) return;
        pararTimer();
        registrarResultado("Tempo esgotado");
        revelarMinas();
    }

    // Atualização imediata evita animações antigas alterando a próxima partida.
    private void animarRevelacao(List<int[]> celulas, int indice, int atraso) {
        for (int[] pos : celulas) view.atualizarCelula(pos[0], pos[1], tabuleiro);
        finalizarJogada();
    }

    private void registrarResultado(String resultado) {
        if (partidaEncerrada) return;
        partidaEncerrada = true;
        int segundos = jogoIniciado ? (int) obterSegundosPassados() : 0;
        int dicas = DICAS_POR_PARTIDA - dicasRestantes;
        int pontos = HistoricoPartidas.calcularPontos(celulasReveladas, "Vitória".equals(resultado), segundos, dicas);
        view.mostrarResultado(resultado, pontos);
        if (historico == null) {
            view.mostrarAviso("O histórico está indisponível. Esta partida não foi salva.");
            return;
        }
        try { historico.registrar(jogador, chaveDificuldadeAtual, resultado, segundos, pontos, dicas, limiteSegundos); }
        catch (IOException e) { view.mostrarAviso("Não foi possível salvar esta partida. Verifique a pasta dados do jogo."); }
    }

    @Override
    public void aoAbrirHistorico() {
        if (historico == null) view.mostrarAviso("Histórico indisponível. Verifique o arquivo dados/partidas.properties.");
        else view.mostrarHistorico(historico.partidas(), historico.ranking());
    }

    private void finalizarJogada() {
        if (partidaEncerrada) return;
        atualizarEstatisticasNaView();

        if (!tabuleiro.isJogoEncerrado()) {
            return;
        }

        pararTimer();

        if (tabuleiro.isDerrota()) {
            SomUtil.tocarExplosao();
            view.mostrarDerrota();
            registrarResultado("Derrota");
            revelarMinas();
        } else {
            SomUtil.tocarVitoria();
            view.mostrarVitoria();
            // MELHORIA (recorde): só vale recorde no modo livre (sem tempo
            // limite), já que no modo "tempo rápido" o cronômetro conta
            // regressivamente e não representa o tempo total da partida.
            if (limiteSegundos == 0) {
                int tempoFinalSegundos = (int) obterSegundosPassados();
                boolean novoRecorde = gerenciadorRecordes
                        .registrarTempoSePossivelRecorde(chaveDificuldadeAtual, tempoFinalSegundos);
                if (novoRecorde) {
                    view.indicarNovoRecorde(formatarTempo(tempoFinalSegundos));
                }
            }
            registrarResultado("Vitória");
        }
    }

    private void revelarMinas() {
        for (int i = 0; i < tabuleiro.getLinhas(); i++)
            for (int j = 0; j < tabuleiro.getColunas(); j++)
                if (tabuleiro.isMinada(i, j)) view.marcarMinaExplodida(i, j);
    }
}
