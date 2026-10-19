package view;

import controller.AcoesJogador;
import model.LeituraTabuleiro;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * VIEW da arquitetura MVC: cuida só de desenhar a tela e capturar
 * interações do usuário. Nunca decide o que um clique "significa" em
 * termos de regra de jogo — ela apenas repassa o clique para quem
 * implementa {@link AcoesJogador} (o Controller) e espera ser chamada de
 * volta para atualizar o que aparece na tela.
 */
public class CampoMinadoView extends JFrame {

    private static final Color COR_FUNDO = new Color(30, 30, 35);
    private static final Color COR_FUNDO_CLARO = new Color(45, 45, 52);
    private static final Color COR_DESTAQUE = new Color(70, 130, 180);

    // Célula OCULTA: escura e "elevada" — ainda não foi clicada.
    private static final Color COR_CELULA_OCULTA = new Color(72, 78, 96);
    private static final Color COR_CELULA_OCULTA_HOVER = new Color(90, 97, 118);
    private static final Color COR_BORDA_OCULTA = new Color(100, 107, 128);

    // Célula REVELADA: clara e "afundada" — contraste forte e
    // inconfundível com a célula oculta, como no Campo Minado clássico.
    private static final Color COR_CELULA_REVELADA = new Color(228, 228, 233);
    private static final Color COR_BORDA_REVELADA = new Color(195, 195, 202);
    private static final Color COR_TEXTO_SOBRE_REVELADA = new Color(40, 40, 45);

    private static final Color COR_MINA = new Color(220, 60, 60);
    private static final Color COR_MINA_FUNDO = new Color(60, 20, 20);
    private static final Color COR_VITORIA = new Color(50, 180, 80);
    private static final Color COR_TEXTO_PRINCIPAL = new Color(230, 230, 235);
    private static final Color COR_TEXTO_SECUNDARIO = new Color(150, 150, 160);
    private static final Color COR_BORDA = new Color(80, 80, 90);
    private static final Color COR_CARD = new Color(50, 50, 58);
    private static final Color COR_CARD_HOVER = new Color(65, 65, 78);
    private static final Color COR_BANDEIRA = new Color(230, 180, 50);
    // MELHORIA: cor própria para a marcação "?", diferente da bandeira,
    // e cor de aviso para quando o jogador coloca mais bandeiras que minas.
    private static final Color COR_INTERROGACAO = new Color(90, 190, 210);
    private static final Color COR_AVISO = new Color(220, 90, 90);

    private static final String[] TEMAS_FUNDO = {"Escuro", "Claro", "Campo", "Cyberpunk"};
    private static final String[] TEMAS_TABULEIRO = {"Clássico", "Noite", "Verde", "Retrô Terminal"};
    private static final String[] TEMPOS_JOGO = {"Sem limite", "1 minuto", "2 minutos", "3 minutos", "5 minutos"};
    // MELHORIA (som): liga/desliga os efeitos sonoros sintetizados.
    private static final String[] OPCOES_SOM = {"Som: Ligado", "Som: Desligado"};

    private static final Font FONTE_CELULA = new Font("Segoe UI Emoji", Font.BOLD, 20);
    private static final Font FONTE_TITULO = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font FONTE_SUBTITULO = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font FONTE_NORMAL = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONTE_NUMERO = new Font("Consolas", Font.BOLD, 18);
    private static final Font FONTE_PEQUENA = new Font("Segoe UI", Font.PLAIN, 12);

    private static final String EMOJI_BOMBA = "\uD83D\uDCA3";
    private static final String EMOJI_BANDEIRA = "\uD83D\uDEA9";
    private static final String EMOJI_TROFEU = "\uD83C\uDFC6";
    private static final String EMOJI_EXPLOSAO = "\uD83D\uDCA5";
    private static final String EMOJI_RELOGIO = "\u23F1";
    private static final String EMOJI_JOGADA = "\uD83D\uDC46";
    // Antes havia um "quadradinho" (\u25A0) usado como ícone de estatística.
    // Trocado pelo emoji de bomba, como pedido.
    private static final String EMOJI_ICONE_ESTATISTICA = EMOJI_BOMBA;

    // Esquema clássico do Campo Minado, pensado para boa leitura sobre o
    // fundo claro (COR_CELULA_REVELADA) da célula já revelada.
    private static final Color[] CORES_NUMEROS = {
            null,
            new Color(25, 118, 210),   // 1 - azul
            new Color(56, 142, 60),    // 2 - verde
            new Color(211, 47, 47),    // 3 - vermelho
            new Color(13, 71, 161),    // 4 - azul-marinho
            new Color(136, 14, 14),    // 5 - vinho
            new Color(0, 131, 143),    // 6 - teal
            new Color(33, 33, 33),     // 7 - preto
            new Color(97, 97, 97)      // 8 - cinza-escuro
    };

    private final JTextField campoJogador = new JTextField("Jogador", 20);
    private AcoesJogador ouvinte;
    private JButton[][] botoes;
    private JLabel labelStatus;

    private JLabel lblTempo;
    private JLabel lblMinasRestantes;
    private JLabel lblCelulasReveladas;
    private JLabel lblJogadas;
    private JProgressBar barraProgresso;

    // MELHORIA: label do recorde salvo e botão/label do sistema de dicas.
    private JLabel lblRecorde;
    private JButton btnDica;

    private JComboBox<String> comboTemaFundo;
    private JComboBox<String> comboTemaTabuleiro;
    private JComboBox<String> comboTempo;
    private JComboBox<String> comboSom;

    private Color corFundo = COR_FUNDO;
    private Color corFundoClaro = COR_FUNDO_CLARO;
    private Color corDestaque = COR_DESTAQUE;
    private Color corTextoPrincipal = COR_TEXTO_PRINCIPAL;
    private Color corTextoSecundario = COR_TEXTO_SECUNDARIO;
    private Color corCard = COR_CARD;
    private Color corCardHover = COR_CARD_HOVER;
    private Color corBorda = COR_BORDA;
    private Color corCelulaOculta = COR_CELULA_OCULTA;
    private Color corCelulaOcultaHover = COR_CELULA_OCULTA_HOVER;
    private Color corBordaOculta = COR_BORDA_OCULTA;
    private Color corCelulaRevelada = COR_CELULA_REVELADA;
    private Color corBordaRevelada = COR_BORDA_REVELADA;
    private Color corTextoSobreRevelada = COR_TEXTO_SOBRE_REVELADA;
    private Color corMinaFundo = COR_MINA_FUNDO;

    public CampoMinadoView() {
        super("Campo Minado");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(corFundo);
        setLocationRelativeTo(null);
        setResizable(true);
    }

    /** Define quem recebe os eventos de clique/escolha (o Controller). */
    public void setOuvinte(AcoesJogador ouvinte) {
        this.ouvinte = ouvinte;
    }

    // ================================================================
    // TELA INICIAL
    // ================================================================

    public void mostrarTelaInicial() {
        int fundo = comboTemaFundo == null ? 0 : comboTemaFundo.getSelectedIndex();
        int tab = comboTemaTabuleiro == null ? 0 : comboTemaTabuleiro.getSelectedIndex();
        int tempo = comboTempo == null ? 0 : comboTempo.getSelectedIndex();
        int som = comboSom == null ? 0 : comboSom.getSelectedIndex();
        aplicarTemaSelecionado();
        getContentPane().removeAll();
        setLayout(new BorderLayout());

        JPanel painelCentral = new JPanel(new GridBagLayout());
        painelCentral.setBackground(corFundo);
        painelCentral.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        JPanel painelConteudo = new JPanel();
        painelConteudo.setLayout(new BoxLayout(painelConteudo, BoxLayout.Y_AXIS));
        painelConteudo.setBackground(corFundo);
        painelConteudo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titulo = new JLabel("Campo Minado", new IconeJogo(false), SwingConstants.CENTER);
        titulo.setFont(FONTE_TITULO);
        titulo.setForeground(corTextoPrincipal);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelConteudo.add(titulo);

        JLabel subtitulo = new JLabel("Escolha sua dificuldade");
        subtitulo.setFont(FONTE_NORMAL);
        subtitulo.setForeground(corTextoSecundario);
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 30, 0));
        painelConteudo.add(subtitulo);

        // MELHORIA: grade 2x2 para caber o novo card de dificuldade
        // "Personalizado", além dos três originais.
        JPanel painelCards = new JPanel(new GridLayout(2, 2, 15, 15));
        painelCards.setBackground(corFundo);
        painelCards.setAlignmentX(Component.CENTER_ALIGNMENT);

        painelCards.add(criarCardDificuldade("Iniciante", "9 × 9", "10 minas", 9, 9, 10));
        painelCards.add(criarCardDificuldade("Intermediário", "16 × 16", "40 minas", 16, 16, 40));
        painelCards.add(criarCardDificuldade("Avançado", "16 × 30", "99 minas", 16, 30, 99));
        painelCards.add(criarCardPersonalizado());

        painelConteudo.add(painelCards);

        JLabel dica = new JLabel("<html><center>\uD83D\uDDB1\uFE0F Esquerdo: revelar • Direito: bandeira/? • Duplo clique: abrir vizinhos</center></html>");
        dica.setFont(FONTE_PEQUENA);
        dica.setForeground(corTextoSecundario);
        dica.setAlignmentX(Component.CENTER_ALIGNMENT);
        dica.setBorder(BorderFactory.createEmptyBorder(25, 0, 0, 0));
        painelConteudo.add(dica);
        painelConteudo.add(Box.createVerticalStrut(20));
        painelConteudo.add(criarPainelOpcoes());

        comboTemaFundo.setSelectedIndex(fundo);
        comboTemaTabuleiro.setSelectedIndex(tab);
        comboTempo.setSelectedIndex(tempo);
        comboSom.setSelectedIndex(som);
        comboTemaFundo.addActionListener(e -> mostrarTelaInicial());
        painelCentral.add(painelConteudo);
        add(new JScrollPane(painelCentral), BorderLayout.CENTER);

        ajustarJanela();
        setLocationRelativeTo(null);
        revalidate();
        repaint();
    }

    private JPanel criarPainelOpcoes() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(corFundo);
        painel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel linha1 = criarLinhaSelecao("Tema de fundo:", TEMAS_FUNDO);
        comboTemaFundo = (JComboBox<String>) linha1.getClientProperty("combo");
        painel.add(linha1);
        painel.add(Box.createVerticalStrut(10));

        JPanel linha2 = criarLinhaSelecao("Cor do tabuleiro:", TEMAS_TABULEIRO);
        comboTemaTabuleiro = (JComboBox<String>) linha2.getClientProperty("combo");
        painel.add(linha2);
        painel.add(Box.createVerticalStrut(10));

        JPanel linha3 = criarLinhaSelecao("Tempo rápido:", TEMPOS_JOGO);
        comboTempo = (JComboBox<String>) linha3.getClientProperty("combo");
        painel.add(linha3);
        painel.add(Box.createVerticalStrut(10));

        // MELHORIA (som): opção para ligar/desligar os efeitos sonoros.
        JPanel linha4 = criarLinhaSelecao("Efeitos sonoros:", OPCOES_SOM);
        comboSom = (JComboBox<String>) linha4.getClientProperty("combo");
        painel.add(linha4);
        painel.add(Box.createVerticalStrut(10));

        JButton btnTutorial = new JButton("Ver tutorial");
        btnTutorial.setFont(FONTE_NORMAL);
        btnTutorial.setForeground(corTextoPrincipal);
        btnTutorial.setBackground(corFundoClaro);
        btnTutorial.setFocusPainted(false);
        btnTutorial.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(corBorda),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        btnTutorial.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTutorial.addActionListener(e -> mostrarTutorial());
        btnTutorial.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnTutorial.setBackground(corCardHover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnTutorial.setBackground(corFundoClaro);
            }
        });
        painel.add(btnTutorial);
        JButton historico = new JButton("Histórico e ranking");
        historico.addActionListener(e -> { if (ouvinte != null) ouvinte.aoAbrirHistorico(); });
        painel.add(historico);
        painel.add(new JLabel("Jogador (até 30 caracteres):"));
        painel.add(campoJogador);

        return painel;
    }

    private JPanel criarLinhaSelecao(String texto, String[] opcoes) {
        JPanel painel = new JPanel(new BorderLayout(10, 0));
        painel.setBackground(corFundo);
        painel.setMaximumSize(new Dimension(320, 40));

        JLabel lbl = new JLabel(texto);
        lbl.setFont(FONTE_PEQUENA);
        lbl.setForeground(corTextoSecundario);
        painel.add(lbl, BorderLayout.WEST);

        JComboBox<String> combo = new JComboBox<>(opcoes);
        combo.setFont(FONTE_PEQUENA);
        combo.setBackground(corFundoClaro);
        combo.setForeground(corTextoPrincipal);
        combo.setBorder(BorderFactory.createLineBorder(corBorda));
        painel.add(combo, BorderLayout.EAST);
        painel.putClientProperty("combo", combo);

        return painel;
    }

    private void mostrarTutorial() {
        getContentPane().removeAll();
        setLayout(new BorderLayout());

        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(corFundo);
        painel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel titulo = new JLabel("Como jogar Campo Minado");
        titulo.setFont(FONTE_TITULO);
        titulo.setForeground(corTextoPrincipal);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        painel.add(titulo);
        painel.add(Box.createVerticalStrut(20));

        String texto = "Objetivo: abra todas as casas sem minas. Cada número indica quantas minas existem nas oito casas ao redor, incluindo diagonais. Uma casa vazia não tem minas vizinhas.\n\n1. Escolha uma dificuldade (ou monte a sua em \"Personalizado\") e um tempo rápido.\n"
                + "2. Clique com o botão esquerdo para revelar uma célula.\n"
                + "3. Clique com o botão direito para marcar: bandeira → \"?\" → nada, em ciclo.\n"
                + "4. Já revelou uma célula com número? Duplo clique (ou clique do meio) nela abre\n"
                + "   todos os vizinhos de uma vez, se o número de bandeiras ao redor já bater\n"
                + "   com o número mostrado (o \"chording\" clássico do Campo Minado).\n"
                + "5. Sem ideia de onde clicar? Use o botão \"Dica\" (limitado por partida).\n"
                + "6. Revele todas as células sem minas para vencer.\n"
                + "7. Se explodir uma mina, o jogo termina em derrota.\n"
                + "8. O tempo selecionado limita a partida; se chegar a zero, você perde.\n"
                + "9. Bandeiras erradas podem causar derrota ao abrir os vizinhos. O primeiro clique não é garantidamente seguro; use uma dica para começar com segurança.\n"
                + "10. Histórico e ranking: informe seu nome no menu. Partidas encerradas e abandonadas pelo botão Novo Jogo são salvas localmente. Fechar a janela durante a partida não salva o jogo em andamento.\n"
                + "11. Pontos: 10 por casa segura revelada; vitória dá 500 extras e até 300 por rapidez (300 menos os segundos, mínimo zero). Cada dica desconta 50 pontos; o total nunca fica negativo.\n"
                + "12. No modo sem tempo limite, seu melhor tempo fica salvo como recorde\n"
                + "   da dificuldade, mesmo depois de fechar o jogo.\n";

        JTextArea area = new JTextArea(texto);
        area.setFont(FONTE_NORMAL);
        area.setForeground(corTextoPrincipal);
        area.setBackground(corFundoClaro);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane rolagem = new JScrollPane(area);
        rolagem.setPreferredSize(new Dimension(620, 340));
        painel.add(rolagem);
        area.setCaretPosition(0);
        painel.add(Box.createVerticalStrut(15));

        JLabel dicas = new JLabel("Dicas: use bandeiras para marcar minas e tente abrir áreas sem números.");
        dicas.setFont(FONTE_PEQUENA);
        dicas.setForeground(corTextoSecundario);
        dicas.setAlignmentX(Component.CENTER_ALIGNMENT);
        painel.add(dicas);
        painel.add(Box.createVerticalStrut(25));

        JButton voltar = new JButton("Voltar");
        voltar.setFont(FONTE_NORMAL);
        voltar.setForeground(corTextoPrincipal);
        voltar.setBackground(corFundoClaro);
        voltar.setFocusPainted(false);
        voltar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(corBorda),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        voltar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        voltar.addActionListener(e -> mostrarTelaInicial());
        painel.add(voltar);

        add(painel, BorderLayout.CENTER);
        ajustarJanela();
        setLocationRelativeTo(null);
        revalidate();
        repaint();
    }

    public int getTempoLimiteSegundosSelecionado() {
        if (comboTempo == null) {
            return 0;
        }
        String selecionado = (String) comboTempo.getSelectedItem();
        if (selecionado == null || selecionado.startsWith("Sem")) {
            return 0;
        }
        if (selecionado.contains("1 minuto")) {
            return 60;
        }
        if (selecionado.contains("2 minutos")) {
            return 120;
        }
        if (selecionado.contains("3 minutos")) {
            return 180;
        }
        if (selecionado.contains("5 minutos")) {
            return 300;
        }
        return 0;
    }

    /** MELHORIA (som): true quando o jogador deixou os efeitos sonoros ligados. */
    public boolean isSomLigado() {
        if (comboSom == null) {
            return true;
        }
        String selecionado = (String) comboSom.getSelectedItem();
        return selecionado == null || selecionado.contains("Ligado");
    }

    public void aplicarTemaSelecionado() {
        if (comboTemaFundo != null) {
            String tema = (String) comboTemaFundo.getSelectedItem();
            if ("Claro".equals(tema)) {
                corFundo = new Color(245, 245, 250);
                corFundoClaro = new Color(230, 230, 235);
                corTextoPrincipal = new Color(25, 25, 30);
                corTextoSecundario = new Color(95, 95, 110);
                corCard = new Color(245, 245, 250);
                corCardHover = new Color(225, 225, 235);
                corDestaque = new Color(35, 100, 190);
                corBorda = new Color(180, 180, 190);
            } else if ("Campo".equals(tema)) {
                corFundo = new Color(25, 35, 25);
                corFundoClaro = new Color(45, 65, 45);
                corTextoPrincipal = new Color(220, 230, 200);
                corTextoSecundario = new Color(170, 190, 150);
                corCard = new Color(35, 55, 35);
                corCardHover = new Color(55, 75, 55);
                corDestaque = new Color(140, 200, 120);
                corBorda = new Color(60, 80, 60);
            } else if ("Cyberpunk".equals(tema)) {
                // MELHORIA: novo tema de fundo "cyberpunk neon" (sugestão nº3
                // da lista de melhorias) — roxo escuro com destaques em
                // rosa e ciano vibrantes.
                corFundo = new Color(18, 10, 30);
                corFundoClaro = new Color(35, 18, 55);
                corTextoPrincipal = new Color(240, 230, 255);
                corTextoSecundario = new Color(190, 130, 230);
                corCard = new Color(35, 18, 55);
                corCardHover = new Color(55, 25, 85);
                corDestaque = new Color(255, 60, 180);
                corBorda = new Color(90, 40, 140);
            } else {
                corFundo = COR_FUNDO;
                corFundoClaro = COR_FUNDO_CLARO;
                corTextoPrincipal = COR_TEXTO_PRINCIPAL;
                corTextoSecundario = COR_TEXTO_SECUNDARIO;
                corCard = COR_CARD;
                corCardHover = COR_CARD_HOVER;
                corDestaque = COR_DESTAQUE;
                corBorda = COR_BORDA;
            }
        }

        if (comboTemaTabuleiro != null) {
            String tema = (String) comboTemaTabuleiro.getSelectedItem();
            if ("Noite".equals(tema)) {
                corCelulaOculta = new Color(20, 30, 45);
                corCelulaOcultaHover = new Color(35, 50, 75);
                corBordaOculta = new Color(70, 90, 120);
                corCelulaRevelada = new Color(55, 65, 80);
                corBordaRevelada = new Color(80, 95, 115);
                corTextoSobreRevelada = new Color(230, 230, 240);
                corMinaFundo = new Color(180, 40, 40);
            } else if ("Verde".equals(tema)) {
                corCelulaOculta = new Color(40, 70, 45);
                corCelulaOcultaHover = new Color(60, 95, 65);
                corBordaOculta = new Color(70, 105, 80);
                corCelulaRevelada = new Color(220, 235, 210);
                corBordaRevelada = new Color(155, 175, 145);
                corTextoSobreRevelada = new Color(25, 45, 25);
                corMinaFundo = new Color(170, 40, 40);
            } else if ("Retrô Terminal".equals(tema)) {
                // MELHORIA: novo tema de tabuleiro "terminal retrô"
                // (sugestão nº2) — verde fósforo sobre fundo bem escuro,
                // lembrando monitores de terminal antigos.
                corCelulaOculta = new Color(10, 25, 12);
                corCelulaOcultaHover = new Color(20, 45, 22);
                corBordaOculta = new Color(40, 120, 45);
                corCelulaRevelada = new Color(10, 35, 15);
                corBordaRevelada = new Color(50, 150, 60);
                corTextoSobreRevelada = new Color(80, 250, 100);
                corMinaFundo = new Color(90, 20, 20);
            } else {
                corCelulaOculta = COR_CELULA_OCULTA;
                corCelulaOcultaHover = COR_CELULA_OCULTA_HOVER;
                corBordaOculta = COR_BORDA_OCULTA;
                corCelulaRevelada = COR_CELULA_REVELADA;
                corBordaRevelada = COR_BORDA_REVELADA;
                corTextoSobreRevelada = COR_TEXTO_SOBRE_REVELADA;
                corMinaFundo = COR_MINA_FUNDO;
            }
        }

        getContentPane().setBackground(corFundo);
    }

    private JPanel criarCardDificuldade(String titulo, String dimensao, String minasTexto,
                                         int linhas, int colunas, int minas) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(corCard);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(corBorda, 1),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(FONTE_SUBTITULO);
        lblTitulo.setForeground(corDestaque);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblTitulo);

        JLabel lblDim = new JLabel(dimensao);
        lblDim.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblDim.setForeground(corTextoPrincipal);
        lblDim.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblDim.setBorder(BorderFactory.createEmptyBorder(8, 0, 4, 0));
        card.add(lblDim);

        JLabel lblMinas = new JLabel(EMOJI_BOMBA + " " + minasTexto);
        lblMinas.setFont(FONTE_NORMAL);
        lblMinas.setForeground(corTextoSecundario);
        lblMinas.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblMinas);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(corCardHover);
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(corDestaque, 2),
                        BorderFactory.createEmptyBorder(19, 24, 19, 24)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(corCard);
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(corBorda, 1),
                        BorderFactory.createEmptyBorder(20, 25, 20, 25)
                ));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (ouvinte != null) {
                    ouvinte.aoEscolherDificuldade(linhas, colunas, minas);
                }
            }
        });

        return card;
    }

    /**
     * MELHORIA (dificuldade customizada): card extra na tela inicial que,
     * em vez de já saber linhas/colunas/minas como os outros três, abre um
     * pequeno formulário para o jogador digitar os próprios valores.
     */
    private JPanel criarCardPersonalizado() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(corCard);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(corBorda, 1),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitulo = new JLabel("Personalizado");
        lblTitulo.setFont(FONTE_SUBTITULO);
        lblTitulo.setForeground(corDestaque);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblTitulo);

        JLabel lblDim = new JLabel("Você escolhe");
        lblDim.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblDim.setForeground(corTextoPrincipal);
        lblDim.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblDim.setBorder(BorderFactory.createEmptyBorder(8, 0, 4, 0));
        card.add(lblDim);

        JLabel lblMinas = new JLabel("linhas, colunas e minas");
        lblMinas.setFont(FONTE_NORMAL);
        lblMinas.setForeground(corTextoSecundario);
        lblMinas.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblMinas);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(corCardHover);
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(corDestaque, 2),
                        BorderFactory.createEmptyBorder(19, 24, 19, 24)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(corCard);
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(corBorda, 1),
                        BorderFactory.createEmptyBorder(20, 25, 20, 25)
                ));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                abrirDialogoPersonalizado();
            }
        });

        return card;
    }

    /**
     * Formulário simples (com {@link JSpinner}) para o jogador digitar sua
     * própria combinação de linhas, colunas e minas. Ajusta o número de
     * minas automaticamente se o jogador pedir mais minas do que células
     * disponíveis no tabuleiro escolhido.
     */
    private void abrirDialogoPersonalizado() {
        JSpinner spinnerLinhas = new JSpinner(new SpinnerNumberModel(9, 5, 40, 1));
        JSpinner spinnerColunas = new JSpinner(new SpinnerNumberModel(9, 5, 40, 1));
        JSpinner spinnerMinas = new JSpinner(new SpinnerNumberModel(10, 1, 400, 1));

        JPanel painel = new JPanel(new GridLayout(3, 2, 10, 10));
        painel.add(new JLabel("Linhas (5–40):"));
        painel.add(spinnerLinhas);
        painel.add(new JLabel("Colunas (5–40):"));
        painel.add(spinnerColunas);
        painel.add(new JLabel("Minas:"));
        painel.add(spinnerMinas);

        int opcao = JOptionPane.showConfirmDialog(this, painel, "Dificuldade personalizada",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opcao != JOptionPane.OK_OPTION) {
            return;
        }

        int linhas = (Integer) spinnerLinhas.getValue();
        int colunas = (Integer) spinnerColunas.getValue();
        int minas = (Integer) spinnerMinas.getValue();

        int maximoDeMinas = linhas * colunas - 1;
        if (minas > maximoDeMinas) {
            minas = maximoDeMinas;
            JOptionPane.showMessageDialog(this,
                    "O número de minas foi ajustado para o máximo possível nesse tamanho: " + maximoDeMinas,
                    "Aviso", JOptionPane.WARNING_MESSAGE);
        }

        if (ouvinte != null) {
            ouvinte.aoEscolherDificuldade(linhas, colunas, minas);
        }
    }

    // ================================================================
    // TELA DE JOGO
    // ================================================================

    /**
     * Monta a tela de jogo do zero para um tabuleiro de {@code linhas} x
     * {@code colunas}. Não recebe o {@link Tabuleiro}, apenas as
     * dimensões — quem decide o que cada célula mostra depois é sempre
     * o Controller, chamando {@link #atualizarCelula}.
     */
    public void iniciarTelaDeJogo(int linhas, int colunas, int totalMinas, int totalCelulas, int tempoLimiteSegundos) {
        getContentPane().removeAll();
        setLayout(new BorderLayout(0, 0));

        add(criarPainelSuperior(), BorderLayout.NORTH);

        JPanel painelPrincipal = new JPanel(new BorderLayout(15, 0));
        painelPrincipal.setBackground(corFundo);
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));

        painelPrincipal.add(new JScrollPane(criarPainelTabuleiro(linhas, colunas)), BorderLayout.CENTER);
        painelPrincipal.add(criarPainelEstatisticas(totalMinas, totalCelulas), BorderLayout.EAST);

        add(painelPrincipal, BorderLayout.CENTER);

        ajustarJanela();
        setLocationRelativeTo(null);
        revalidate();
        repaint();
    }

    private JPanel criarPainelSuperior() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(corFundo);
        painel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));

        JButton btnNovo = new JButton("← Novo Jogo");
        btnNovo.setFont(FONTE_NORMAL);
        btnNovo.setForeground(corTextoPrincipal);
        btnNovo.setBackground(corFundoClaro);
        btnNovo.setFocusPainted(false);
        btnNovo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(corBorda),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        btnNovo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNovo.addActionListener(e -> {
            if (ouvinte != null) {
                ouvinte.aoPedirNovoJogo();
            }
        });
        btnNovo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnNovo.setBackground(corCardHover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnNovo.setBackground(corFundoClaro);
            }
        });

        labelStatus = new JLabel("Boa sorte!", SwingConstants.CENTER);
        labelStatus.setFont(FONTE_SUBTITULO);
        labelStatus.setForeground(corTextoSecundario);

        painel.add(btnNovo, BorderLayout.WEST);
        painel.add(labelStatus, BorderLayout.CENTER);

        return painel;
    }

    private JPanel criarPainelEstatisticas(int totalMinas, int totalCelulas) {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(corFundoClaro);
        painel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(corBorda, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        int largura = 180 + Math.min(100, totalMinas * 2);
        painel.setPreferredSize(new Dimension(largura, 0));

        JLabel lblTitulo = new JLabel("Estatísticas");
        lblTitulo.setFont(FONTE_SUBTITULO);
        lblTitulo.setForeground(corDestaque);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        painel.add(lblTitulo);
        painel.add(Box.createVerticalStrut(20));

        JPanel pnlTempo = criarItemEstatistica(EMOJI_RELOGIO + " Tempo", "00:00");
        lblTempo = (JLabel) pnlTempo.getClientProperty("valor");
        painel.add(pnlTempo);
        painel.add(Box.createVerticalStrut(15));

        JPanel pnlMinas = criarItemEstatistica("Minas", String.valueOf(totalMinas));
        lblMinasRestantes = (JLabel) pnlMinas.getClientProperty("valor");
        painel.add(pnlMinas);
        painel.add(Box.createVerticalStrut(15));

        JPanel pnlReveladas = criarItemEstatistica("Reveladas", "0 / " + totalCelulas);
        lblCelulasReveladas = (JLabel) pnlReveladas.getClientProperty("valor");
        painel.add(pnlReveladas);
        painel.add(Box.createVerticalStrut(15));

        JPanel pnlJogadas = criarItemEstatistica(EMOJI_JOGADA + " Jogadas", "0");
        lblJogadas = (JLabel) pnlJogadas.getClientProperty("valor");
        painel.add(pnlJogadas);
        painel.add(Box.createVerticalStrut(15));

        // MELHORIA (recorde persistido): mostra o melhor tempo já salvo
        // para esta dificuldade, atualizado pelo Controller.
        JPanel pnlRecorde = criarItemEstatistica("\uD83C\uDFC6 Recorde", "--:--");
        lblRecorde = (JLabel) pnlRecorde.getClientProperty("valor");
        painel.add(pnlRecorde);
        painel.add(Box.createVerticalStrut(20));

        JLabel lblProgTitulo = new JLabel("Progresso");
        lblProgTitulo.setFont(FONTE_NORMAL);
        lblProgTitulo.setForeground(corTextoSecundario);
        lblProgTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        painel.add(lblProgTitulo);

        barraProgresso = new JProgressBar(0, Math.max(totalCelulas, 1));
        barraProgresso.setValue(0);
        barraProgresso.setStringPainted(true);
        barraProgresso.setString("0%");
        barraProgresso.setForeground(corDestaque);
        barraProgresso.setBackground(corFundo);
        barraProgresso.setBorder(BorderFactory.createLineBorder(corBorda));
        barraProgresso.setPreferredSize(new Dimension(150, 20));
        barraProgresso.setMaximumSize(new Dimension(150, 20));
        barraProgresso.setAlignmentX(Component.CENTER_ALIGNMENT);
        painel.add(barraProgresso);
        painel.add(Box.createVerticalStrut(15));

        // MELHORIA (sistema de dicas): botão que pede ao Controller para
        // revelar uma célula segura sorteada, com limite de usos por partida.
        btnDica = new JButton("\uD83D\uDCA1 Dica (3)");
        btnDica.setFont(FONTE_PEQUENA);
        btnDica.setForeground(corTextoPrincipal);
        btnDica.setBackground(corFundoClaro);
        btnDica.setFocusPainted(false);
        btnDica.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnDica.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(corBorda),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        btnDica.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDica.addActionListener(e -> {
            if (ouvinte != null) {
                ouvinte.aoPedirDica();
            }
        });
        painel.add(btnDica);
        painel.add(Box.createVerticalStrut(15));

        painel.add(Box.createVerticalGlue());

        JLabel lblDica = new JLabel("<html><center>\uD83D\uDDB1\uFE0F Esquerdo: revelar<br>\uD83D\uDDB1\uFE0F Direito: bandeira</center></html>");
        lblDica.setFont(FONTE_PEQUENA);
        lblDica.setForeground(corTextoSecundario);
        lblDica.setAlignmentX(Component.CENTER_ALIGNMENT);
        painel.add(lblDica);

        return painel;
    }

    /**
     * Cria um item de estatística (título + valor) como um único painel,
     * guardando a referência ao label de valor via putClientProperty para
     * que possa ser atualizado depois. (Antes o valor era retornado
     * "solto", sem o painel-pai ser adicionado à tela — corrigido aqui.)
     */
    private JPanel criarItemEstatistica(String titulo, String valorInicial) {
        JPanel painelItem = new JPanel();
        painelItem.setLayout(new BoxLayout(painelItem, BoxLayout.Y_AXIS));
        painelItem.setBackground(corFundoClaro);
        painelItem.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(FONTE_PEQUENA);
        lblTitulo.setForeground(corTextoSecundario);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblValor = new JLabel(valorInicial);
        lblValor.setFont(FONTE_NUMERO);
        lblValor.setForeground(corTextoPrincipal);
        lblValor.setAlignmentX(Component.CENTER_ALIGNMENT);

        painelItem.add(lblTitulo);
        painelItem.add(lblValor);
        painelItem.putClientProperty("valor", lblValor);

        return painelItem;
    }

    private JPanel criarPainelTabuleiro(int linhas, int colunas) {
        JPanel grade = new JPanel(new GridLayout(linhas, colunas, 2, 2));
        grade.setBackground(corFundo);

        botoes = new JButton[linhas][colunas];
        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                JButton botao = criarBotaoCelula(i, j);
                botoes[i][j] = botao;
                grade.add(botao);
            }
        }
        return grade;
    }

    /**
     * Cria o botão de uma célula. AQUI ESTAVA O BUG DA BANDEIRA: o código
     * original detectava o clique direito em mousePressed. Em trackpads
     * (Mac, e alguns drivers de notebook Windows/Linux) o clique direito
     * simulado por toque com dois dedos nem sempre reporta corretamente
     * qual botão foi pressionado no evento de "pressed" — só fica
     * confiável no evento de "released". Por isso o primeiro clique
     * direito costumava funcionar e os seguintes eram ignorados ou
     * tratados como clique esquerdo. A correção é ouvir mouseReleased.
     */
    private JButton criarBotaoCelula(int linha, int coluna) {
        JButton botao = new JButton();
        botao.setPreferredSize(new Dimension(36, 36));
        botao.setFont(FONTE_CELULA);
        botao.setFocusPainted(false);
        botao.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        botao.setBackground(corCelulaOculta);
        botao.setForeground(corTextoPrincipal);
        botao.setMargin(new Insets(0, 0, 0, 0));
        botao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(corBorda, 1),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)
        ));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));

        botao.putClientProperty("revelada", Boolean.FALSE);

        botao.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Só aplica o realce de "hover" em células ainda ocultas;
                // caso contrário isso sobrescreveria a cor clara da
                // célula já revelada sempre que o mouse passasse por cima.
                if (Boolean.FALSE.equals(botao.getClientProperty("revelada"))) {
                    botao.setBackground(corCelulaOcultaHover);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (Boolean.FALSE.equals(botao.getClientProperty("revelada"))) {
                    botao.setBackground(corCelulaOculta);
                }
            }

            @Override
            public void mouseReleased(MouseEvent evento) {
                if (ouvinte == null) {
                    return;
                }
                // e.getButton() é checado explicitamente além de
                // SwingUtilities.isRightMouseButton para cobrir cliques
                // direitos simulados por trackpad de forma confiável.
                boolean botaoDireito = SwingUtilities.isRightMouseButton(evento)
                        || evento.getButton() == MouseEvent.BUTTON3;
                // MELHORIA (chording): clique do meio abre os vizinhos de
                // uma célula já revelada, igual ao duplo clique esquerdo.
                boolean botaoMeio = evento.getButton() == MouseEvent.BUTTON2;

                if (botaoDireito) {
                    ouvinte.aoMarcarCelula(linha, coluna);
                } else if (botaoMeio) {
                    ouvinte.aoRevelarVizinhos(linha, coluna);
                } else if (SwingUtilities.isLeftMouseButton(evento)) {
                    ouvinte.aoRevelarCelula(linha, coluna);
                }
            }

            @Override
            public void mouseClicked(MouseEvent evento) {
                // MELHORIA (chording): duplo clique esquerdo em uma célula
                // já revelada tenta abrir os vizinhos de uma vez (o clique
                // simples correspondente já foi tratado em mouseReleased,
                // e revelar de novo uma célula revelada não faz nada).
                if (ouvinte != null && evento.getClickCount() == 2
                        && SwingUtilities.isLeftMouseButton(evento)) {
                    ouvinte.aoRevelarVizinhos(linha, coluna);
                }
            }
        });
        return botao;
    }

    // ================================================================
    // ATUALIZAÇÕES CHAMADAS PELO CONTROLLER
    // ================================================================

    /** Redesenha uma célula com base no estado atual do tabuleiro. */
    public void atualizarCelula(int linha, int coluna, LeituraTabuleiro leitura) {
        JButton botao = botoes[linha][coluna];
        botao.setIcon(null);
        botao.putClientProperty("revelada", leitura.isRevelada(linha, coluna));

        if (leitura.isMarcada(linha, coluna)) {
            botao.setText("");
            botao.setIcon(new IconeJogo(true));
            botao.setForeground(COR_BANDEIRA);
            botao.setBackground(corCelulaOculta);
            botao.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COR_BANDEIRA, 1),
                    BorderFactory.createEmptyBorder(2, 2, 2, 2)
            ));
            return;
        }

        // MELHORIA: terceiro estado de marcação, o "?" — o jogador usa
        // para anotar uma célula suspeita sem ter certeza de que é mina.
        if (leitura.isInterrogacao(linha, coluna)) {
            botao.setText("?");
            botao.setForeground(COR_INTERROGACAO);
            botao.setBackground(corCelulaOculta);
            botao.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COR_INTERROGACAO, 1),
                    BorderFactory.createEmptyBorder(2, 2, 2, 2)
            ));
            return;
        }

        if (!leitura.isRevelada(linha, coluna)) {
            botao.setText("");
            botao.setBackground(corCelulaOculta);
            botao.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(corBordaOculta, 1),
                    BorderFactory.createEmptyBorder(2, 2, 2, 2)
            ));
            return;
        }

        if (leitura.isMinada(linha, coluna)) {
            botao.setText("");
        botao.setIcon(new IconeJogo(false));
        botao.putClientProperty("revelada", true);
            botao.setBackground(corMinaFundo);
            botao.setForeground(COR_MINA);
            botao.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COR_MINA, 1),
                    BorderFactory.createEmptyBorder(2, 2, 2, 2)
            ));
        } else {
            // Célula revelada e segura: fundo claro e "afundado",
            // nitidamente diferente do fundo escuro da célula oculta.
            botao.setBackground(corCelulaRevelada);
            botao.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(corBordaRevelada, 1),
                    BorderFactory.createEmptyBorder(2, 2, 2, 2)
            ));
            int vizinhas = leitura.getMinasVizinhas(linha, coluna);
            if (vizinhas == 0) {
                botao.setText("");
                botao.setForeground(corTextoSobreRevelada);
            } else {
                botao.setText(String.valueOf(vizinhas));
                botao.setForeground(corCelulaRevelada.getRed() < 100 ? corTextoSobreRevelada : CORES_NUMEROS[vizinhas]);
            }
        }
    }

    public void atualizarTempo(String texto) {
        if (lblTempo != null) {
            lblTempo.setText(texto);
        }
    }

    public void atualizarEstatisticas(int minasRestantes, int celulasReveladas, int totalCelulas, int jogadas) {
        if (lblMinasRestantes != null) {
            lblMinasRestantes.setText(String.valueOf(minasRestantes));
            // MELHORIA: aviso visual (número em vermelho) quando o jogador
            // coloca mais bandeiras do que minas realmente existem.
            lblMinasRestantes.setForeground(minasRestantes < 0 ? COR_AVISO : corTextoPrincipal);
        }
        if (lblCelulasReveladas != null) {
            lblCelulasReveladas.setText(celulasReveladas + " / " + totalCelulas);
        }
        if (lblJogadas != null) {
            lblJogadas.setText(String.valueOf(jogadas));
        }

        int progresso = totalCelulas > 0 ? (int) ((celulasReveladas * 100.0) / totalCelulas) : 0;
        if (barraProgresso != null) {
            barraProgresso.setValue(celulasReveladas);
            barraProgresso.setString(progresso + "%");
            if (progresso < 30) {
                barraProgresso.setForeground(new Color(220, 80, 80));
            } else if (progresso < 70) {
                barraProgresso.setForeground(new Color(220, 180, 60));
            } else {
                barraProgresso.setForeground(COR_VITORIA);
            }
        }
    }

    public void mostrarDerrota() {
        labelStatus.setText("Você perdeu!");
        labelStatus.setForeground(COR_MINA);
    }

    public void mostrarVitoria() {
        labelStatus.setText("Você venceu!");
        labelStatus.setForeground(COR_VITORIA);
    }

    /** MELHORIA (dicas): atualiza o texto/estado do botão de dica. */
    public void atualizarDicas(int dicasRestantes) {
        if (btnDica == null) {
            return;
        }
        btnDica.setText("\uD83D\uDCA1 Dica (" + dicasRestantes + ")");
        btnDica.setEnabled(dicasRestantes > 0);
    }

    /** MELHORIA (recorde persistido): mostra o recorde salvo (ou "--:--"). */
    public void definirRecordeExibido(String tempoFormatado) {
        if (lblRecorde != null) {
            lblRecorde.setText(tempoFormatado);
        }
    }

    /** MELHORIA (recorde persistido): destaca que a partida atual bateu o recorde. */
    public void indicarNovoRecorde(String tempoFormatado) {
        if (lblRecorde != null) {
            lblRecorde.setText(tempoFormatado);
            lblRecorde.setForeground(COR_VITORIA);
        }
        labelStatus.setText("Novo recorde! " + tempoFormatado);
    }

    public void piscarFundoDeExplosao(boolean explodindo) {
        getContentPane().setBackground(explodindo ? corMinaFundo : corFundo);
    }

    public void marcarMinaExplodida(int linha, int coluna) {
        JButton botao = botoes[linha][coluna];
        botao.setText("");
        botao.setIcon(new IconeJogo(false));
        botao.putClientProperty("revelada", true);
        botao.setForeground(COR_MINA);
        botao.setBackground(corMinaFundo);
        botao.setBorder(BorderFactory.createLineBorder(COR_MINA, 1));
    }

    public void destacarCelulaVencedora(int linha, int coluna) {
        botoes[linha][coluna].setBackground(new Color(40, 100, 60));
    }
    private void ajustarJanela() {
        pack();
        Rectangle tela = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        setSize(Math.min(getWidth(), tela.width - 40), Math.min(Math.max(getHeight(), 600), tela.height - 40));
    }

    public String getNomeJogador() {
        String nome = campoJogador.getText().strip();
        return nome.isEmpty() ? "Jogador" : nome.substring(0, Math.min(30, nome.length()));
    }

    public void mostrarHistorico(Object[][] partidas, Object[][] ranking) {
        JTabbedPane abas = new JTabbedPane();
        abas.addTab("Partidas", tabela(partidas, new String[]{"Data", "Jogador", "Dificuldade", "Resultado", "Tempo (s)", "Pontos", "Dicas", "Limite (s)"}));
        abas.addTab("Quem jogou mais", tabela(ranking, new String[]{"Jogador", "Partidas", "Vitórias", "Pontos", "Tempo total (s)"}));
        JDialog janela = new JDialog(this, "Histórico e ranking local", true);
        janela.add(abas);
        janela.setSize(850, 440);
        janela.setLocationRelativeTo(this);
        janela.setVisible(true);
    }

    private JScrollPane tabela(Object[][] dados, String[] colunas) {
        JTable tabela = new JTable(new javax.swing.table.DefaultTableModel(dados, colunas) {
            public boolean isCellEditable(int linha, int coluna) { return false; }
            public Class<?> getColumnClass(int coluna) {
                return getRowCount() == 0 ? Object.class : getValueAt(0, coluna).getClass();
            }
        });
        tabela.setAutoCreateRowSorter(true);
        tabela.setFillsViewportHeight(true);
        return new JScrollPane(tabela);
    }

    public void mostrarAviso(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem);
    }

    public void mostrarResultado(String resultado, int pontos) {
        labelStatus.setText(resultado + " | " + pontos + " pontos");
        atualizarDicas(0);
    }
}
